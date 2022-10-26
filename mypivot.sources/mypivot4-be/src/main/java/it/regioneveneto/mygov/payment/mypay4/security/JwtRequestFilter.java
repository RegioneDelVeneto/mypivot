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
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@ConditionalOnWebApplication
public class JwtRequestFilter extends OncePerRequestFilter {

  public final static String AUTHORIZATION_HEADER = "Authorization";
  final static String CLAIMS_ATTRIBUTE = "__CLAIMS_"+ UUID.randomUUID();

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
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return Arrays.stream(antPathRequestMatchers).anyMatch(antPathRequestMatcher -> antPathRequestMatcher.matches(request));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String jwtToken = null;
    Claims claims;
    String jti = null;
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    boolean tokenCheckOk = false;

    if(!jwtTokenUtil.isTokenInCookie()) {
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
        //parse JWT token
        Jws<Claims> jws = jwtTokenUtil.parseToken(jwtToken);
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
        if (usageCheckEnabled && wasTokenAlreadyUsed(jti)) {
          throw new AlreadyUsedJwtException(jws.getHeader(), claims, "Token was already used");
        } else if (!jwtTokenUtil.isAuthToken(jws)){
          throw new InvalidJwtException(jws.getHeader(), claims, "Invalid token type");
        } else if(isEmailValidationNeeded(claims)){
          throw new InvalidJwtException(jws.getHeader(), claims, "Invalid token type (email validation needed)");
        }
        //retrieve user info from token
        UserWithAdditionalInfo user = UserWithAdditionalInfo.builder()
              .username(subject)
              .codiceFiscale(claims.get(JwtTokenUtil.JWT_CLAIM_CODICE_FISCALE, String.class))
              .familyName(claims.get(JwtTokenUtil.JWT_CLAIM_COGNOME, String.class))
              .firstName(claims.get(JwtTokenUtil.JWT_CLAIM_NOME, String.class))
              .email(claims.get(JwtTokenUtil.JWT_CLAIM_EMAIL, String.class))
              .emailSourceType(Optional.ofNullable(claims.get(JwtTokenUtil.JWT_CLAIM_EMAIL_SOURCE_TYPE, String.class))
                .map(x -> x.charAt(0)).filter(Utente.EMAIL_SOURCE_TYPES::isValid)
                .orElseThrow(() -> new MyPayException("invalid emailSourceType")))
            //retrieve enti/roles from profile service (may be cached for performance reasons)
            .entiRoles(myProfileService.getUserTenantsAndRoles(subject))
            .build();
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
          log.debug("token tot set as used because response time longer than threshold [{}/{}]ms", stopWatch.getTime(), usageCheckIgnoreLongCallMilliseconds);
        } else {
          log.debug("operation completed, elapsed time ms[{}]", stopWatch.getTime());
          jwtTokenUsageService.markTokenUsed(jti);
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

  private boolean wasTokenAlreadyUsed(String jti) {
    Long lastUsed = jwtTokenUsageService.getTokenUsageTime(jti);
    if (lastUsed == null) {
      log.debug("wasTokenAlreadyUsed: " + jti + " : null");
      return false;
    } else {
      boolean used = System.currentTimeMillis() - lastUsed > gracePeriod;
      log.debug("wasTokenAlreadyUsed: " + jti + " :" + used);
      return used;
    }
  }

  private boolean isEmailValidationNeeded(Claims claims){
    return JwtRequestFilter.isEmailValidationNeeded(claims.get(JwtTokenUtil.JWT_CLAIM_EMAIL, String.class));
  }

  public static boolean isEmailValidationNeeded(String email){
    return StringUtils.isBlank(email);
  }



}
