/**
 *     MyPivot - Accounting reconciliation system of Regione Veneto.
 *     Copyright (C) 2022  Regione Veneto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.regioneveneto.mygov.payment.mypay4.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.SignatureException;
import it.regioneveneto.mygov.payment.mypay4.config.SecurityConfigWhitelist;
import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import it.regioneveneto.mygov.payment.mypay4.exception.NotAuthorizedException;
import it.regioneveneto.mygov.payment.mypay4.service.myprofile.MyProfileServiceI;
import it.regioneveneto.mygov.payment.mypay4.storage.JwtTokenUsageStorage;
import it.regioneveneto.mygov.payment.mypivot4.model.Utente;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static it.regioneveneto.mygov.payment.mypay4.config.MyPay4AbstractSecurityConfig.PATH_A2A;

@Component
@Slf4j
@ConditionalOnWebApplication
public class JwtRequestFilter extends OncePerRequestFilter {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String REQUEST_UID_HEADER = "ReqUid";
  static final String CLAIMS_ATTRIBUTE = "__CLAIMS_"+ UUID.randomUUID();

  @Value("${static.serve.enabled:false}")
  private String staticContentEnabled;
  @Value("${static.serve.paths:/staticContent}")
  private String[] staticContentPaths;
  @Value("${jwt.usage-check.grace-period.milliseconds:3000}")
  private String gracePeriodString;
  private long gracePeriod;
  @Value("${jwt.usage-check.enabled:true}")
  private boolean usageCheckEnabled;
  @Value("${jwt.usage-check.ignorelongcall.milliseconds:0}")
  private long usageCheckIgnoreLongCallMilliseconds;

  @Value("${server.servlet.context-path:}")
  private String serverServletContextPath;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private MyProfileServiceI myProfileService;

  @Autowired
  private JwtTokenUsageStorage jwtTokenUsageService;

  @Autowired(required = false)
  protected SecurityConfigWhitelist securityConfigWhitelist;

  private AntPathRequestMatcher[] antPathRequestMatchers;

  @Override
  protected void initFilterBean() throws ServletException {
    super.initFilterBean();
    String[] authWhitelist = securityConfigWhitelist != null ? ArrayUtils.addAll(securityConfigWhitelist.getAuthWithelist(), securityConfigWhitelist.getSecurityWhitelist()) : new String[0];
    if ("true".equalsIgnoreCase(staticContentEnabled)) {
      String[] pathsToWhitelist = new String[0];
      for(String aPath: staticContentPaths)
        pathsToWhitelist = ArrayUtils.addAll(pathsToWhitelist, aPath, aPath+"/**");
      authWhitelist = ArrayUtils.addAll(authWhitelist, pathsToWhitelist);
    }
    antPathRequestMatchers = Arrays.stream(authWhitelist).map(AntPathRequestMatcher::new).toArray(AntPathRequestMatcher[]::new);

    gracePeriod = Long.parseLong(gracePeriodString);
    log.debug("setting JWT usage check grace time: " + gracePeriod + " ms");
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return Arrays.stream(antPathRequestMatchers).anyMatch(antPathRequestMatcher -> antPathRequestMatcher.matches(request));
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
      throws ServletException, IOException {

    String jwtToken = null;
    Claims claims;
    String jti = null;
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    boolean tokenCheckOk = false;
    boolean isA2ACall = isApplication2ApplicationCall(request);
    Optional<String> requestUid = Optional.ofNullable(request.getHeader(REQUEST_UID_HEADER));

    if(isA2ACall || !jwtTokenUtil.isTokenInCookie()) {
      // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
      final String requestTokenHeader = request.getHeader(AUTHORIZATION_HEADER);
      if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer "))
        jwtToken = requestTokenHeader.substring(7);
    } else {
      // JWT Token is in an http-only token
      jwtToken = jwtTokenUtil.extractTokenFromCookies(request.getCookies());
    }

    if (jwtToken != null) {
      try {
        Jws<Claims> jws;
        String a2aSystem;
        //parse JWT token
        if(isA2ACall){
          Pair<String, Jws<Claims>> jwsAndSystem = jwtTokenUtil.parseA2AAuthorizationToken(jwtToken);
          a2aSystem = jwsAndSystem.getLeft();
          jws = jwsAndSystem.getRight();
          jws.getBody().setSubject(a2aSystem);
        } else {
          jws = jwtTokenUtil.parseToken(jwtToken);
        }
        claims = jws.getBody();
        jti = claims.getId();
        //subject cannot be empty
        String subject = claims.getSubject();
        if( StringUtils.isBlank(subject) ){
          throw new InvalidJwtException(jws.getHeader(), claims, "Invalid subject");
        }
        //add logged user info into log
        MDC.put("user",subject);
        //check if token was not already used before
        if (usageCheckEnabled && wasTokenAlreadyUsed(jti, requestUid)) {
          throw new AlreadyUsedJwtException(jws.getHeader(), claims, "Token was already used");
        } else if (!isA2ACall && !jwtTokenUtil.isAuthToken(jws)){
          throw new InvalidJwtException(jws.getHeader(), claims, "Invalid token type");
        } else if(!isA2ACall && isEmailValidationNeeded(claims)){
          throw new InvalidJwtException(jws.getHeader(), claims, "Invalid token type (email validation needed)");
        }
        //retrieve user info from token
        UserWithAdditionalInfo user;
        if(isA2ACall)
          user = UserWithAdditionalInfo.builder()
            .username("A2A-"+subject)
            .build();
        else {
          Map<String, Set<String>> userTenantsAndRoles = myProfileService.getUserTenantsAndRoles(subject);
          user = UserWithAdditionalInfo.builder()
              .username(subject)
              .codiceFiscale(claims.get(JwtTokenUtil.JWT_CLAIM_CODICE_FISCALE, String.class))
              .familyName(claims.get(JwtTokenUtil.JWT_CLAIM_COGNOME, String.class))
              .firstName(claims.get(JwtTokenUtil.JWT_CLAIM_NOME, String.class))
              .email(claims.get(JwtTokenUtil.JWT_CLAIM_EMAIL, String.class))
              .emailSourceType(Optional.ofNullable(claims.get(JwtTokenUtil.JWT_CLAIM_EMAIL_SOURCE_TYPE, String.class))
                .map(x -> x.charAt(0)).filter(Utente.EMAIL_SOURCE_TYPES::isValid)
                .orElseThrow(() -> new MyPayException("invalid emailSourceType")))
            //retrieve enti/roles from profile service (may be cached for performance reasons)
            .entiRoles(userTenantsAndRoles)
            .sysAdmin(myProfileService.isSystemAdministrator(userTenantsAndRoles))
            .build();
        }
        //set the current user (with details) from JWT Token into Spring Security configuration
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities());
        usernamePasswordAuthenticationToken
            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        request.setAttribute(CLAIMS_ATTRIBUTE, claims);
        tokenCheckOk = true;
      } catch (IllegalArgumentException | SignatureException | InvalidJwtException e) {
        log.warn("Unable to get or invalid JWT Token", e);
        request.setAttribute(JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_ATTRIB, JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_INVALID);
      } catch (ExpiredJwtException e) {
        log.warn("JWT Token has expired");
        request.setAttribute(JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_ATTRIB, JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_EXPIRED);
      } catch (AlreadyUsedJwtException e) {
        log.warn("JWT Token was already used");
        request.setAttribute(JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_ATTRIB, JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_USED);
      }
    } else {
      request.setAttribute(JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_ATTRIB, JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_MISSING);
    }
    try {
      chain.doFilter(request, response);
      //mark token as used
      stopWatch.stop();
      if(usageCheckEnabled && tokenCheckOk) {
        //do not set the token as "used" when operation took more than X seconds,
        // to decrease the risk that user ignored the operation front-end side and tried to navigate
        if(usageCheckIgnoreLongCallMilliseconds > 0 && stopWatch.getTime() > usageCheckIgnoreLongCallMilliseconds){
          log.debug("token not set as used because response time longer than threshold [{}/{}]ms", stopWatch.getTime(), usageCheckIgnoreLongCallMilliseconds);
        } else {
          log.debug("operation completed, elapsed time ms[{}]", stopWatch.getTime());
          jwtTokenUsageService.markTokenUsed(jti);
          if(requestUid.isPresent())
            jwtTokenUsageService.markTokenUsedReqUid(jti, requestUid.get());
        }
      }
    } catch(Exception e){
      Throwable notAuthExc = e;
      while(notAuthExc.getCause()!=null && notAuthExc.getCause()!=notAuthExc){
        if(notAuthExc instanceof NotAuthorizedException)
          break;
        notAuthExc = notAuthExc.getCause();
      }
      if(notAuthExc instanceof NotAuthorizedException)
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, notAuthExc.getMessage());
      else
        throw e;
    } //finally {
      //clear logged user info from log
      //remark: MDC is cleared just at star t of next request in order to keep
      // user information in case of exception logging by Spring DispatcherServlet
      //MDC.clear();
    //}
  }

  private boolean wasTokenAlreadyUsed(String jti, Optional<String> requestUid) {
    Long lastUsed = jwtTokenUsageService.getTokenUsageTime(jti);
    if (lastUsed == null) {
      log.debug("wasTokenAlreadyUsed [{}]: null", jti);
      return false;
    } else {
      long now = System.currentTimeMillis();
      boolean used = now - lastUsed > gracePeriod;
      log.debug("wasTokenAlreadyUsed (lastUsed) [{}]: {}", jti, used ? (now+"-"+lastUsed) : "null" );

      if(used) {
        Long rolledAt = jwtTokenUsageService.wasTokenRolled(jti);
        log.debug("wasTokenAlreadyRolled [{}]: {}", jti, rolledAt);
        used = rolledAt==null || now - rolledAt > gracePeriod;
        if(!used)
          log.debug("wasTokenAlreadyUsed (alreadyRolled) [{}]: false", jti);
      }

      if(used && requestUid.isPresent()) {
        String originalRequestUid = jwtTokenUsageService.getTokenUsageReqUid(jti);
        used = !StringUtils.equals(originalRequestUid, requestUid.get());
        if(!used)
          log.debug("wasTokenAlreadyUsed (requestUid) [{}]: false", jti);
      }

      return used;
    }
  }

  private String a2aPath = null;
  private boolean isApplication2ApplicationCall(HttpServletRequest request) {
    if(this.a2aPath == null){
      this.a2aPath = "...";
      try {
        String contextPath = StringUtils.stripToEmpty(serverServletContextPath);
        if (contextPath.endsWith("/"))
          contextPath = contextPath.substring(0, contextPath.length() - 1);
        String a2aPath = contextPath + PATH_A2A + "/";
        log.info("ContextPath: {} - A2A path: {}", contextPath, a2aPath);
        this.a2aPath = a2aPath;
      } catch(Exception e){
        log.error("error initializing A2A path", e);
        this.a2aPath = null;
      }
    }
    boolean isA2a = request.getRequestURI().startsWith(this.a2aPath);
    log.trace("isA2a [{}]: {}",request.getRequestURI(),isA2a);
    return isA2a;
  }

  private boolean isEmailValidationNeeded(Claims claims){
    return JwtRequestFilter.isEmailValidationNeeded(claims.get(JwtTokenUtil.JWT_CLAIM_EMAIL, String.class));
  }

  public static boolean isEmailValidationNeeded(String email){
    return StringUtils.isBlank(email);
  }



}
