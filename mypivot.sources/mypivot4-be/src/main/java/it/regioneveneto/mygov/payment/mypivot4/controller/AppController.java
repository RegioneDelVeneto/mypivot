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
package it.regioneveneto.mygov.payment.mypivot4.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.mygov.payment.mypay4.config.MyPay4AbstractSecurityConfig;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore;
import it.regioneveneto.mygov.payment.mypay4.service.myprofile.MyProfileServiceI;
import it.regioneveneto.mygov.payment.mypivot4.ApplicationStartupService;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@OpenAPIDefinition(
    info = @Info(
        title = "MyPivot4",
        version = "0.0",
        description = "Sistema di gestione delle riconciliazioni contabili dei pagamenti"
    ),
    security = { @SecurityRequirement(name="myPay4Security") }
)
@SecurityScheme(name = "myPay4Security",
    type = SecuritySchemeType.APIKEY,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.COOKIE,
    paramName = "jwtToken")
@Tag(name = "Applicazione", description = "Gestione generale dell'applicazione")
@SecurityRequirements
@RestController
@Slf4j
@ConditionalOnWebApplication
public class AppController {

  @Value("${auth.fake.enabled:false}")
  private String fakeAuthEnabled;

  @Value("${cors.enabled:false}")
  private String corsEnabled;

  @Value("${mypivot.codIpaEntePredefinito}")
  private String adminEnte;

  @Value("${export.max-records}")
  private String exportMaxRecords;

  @Autowired
  private BuildProperties buildProperties;

  @Autowired
  private ApplicationStartupService applicationStartupService;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private MyProfileServiceI myProfileService;

  @Autowired
  private EnteService enteService;

  @Operation(summary = "Configurazione applicativa",
      description = "Ritorna i parametri della configurazione applicativa",
      responses = { @ApiResponse(description = "I parametri della configurazione applicativa come mappa chiave/valore")})
  @PostMapping(MyPay4AbstractSecurityConfig.PATH_PUBLIC+"/info/config")
  public ResponseEntity<?> configInfo() {
    Map<String, Object> configInfo = new HashMap<>();
    configInfo.put("fakeAuth", Boolean.valueOf(fakeAuthEnabled));
    configInfo.put("useAuthCookie", !Boolean.valueOf(corsEnabled));
    configInfo.put("adminEnte", adminEnte);
    configInfo.put("exportMaxRecords", Integer.parseInt(exportMaxRecords));
    return ResponseEntity.status(HttpStatus.OK).body(configInfo);
  }

  @Operation(summary = "Informazioni applicazione",
      description = "Ritorna le informazioni dell'applicazione (es. versione, build time, etc..)",
      responses = { @ApiResponse(description = "Le informazioni dell'applicazione come mappa chiave/valore")})
  @PostMapping(MyPay4AbstractSecurityConfig.PATH_PUBLIC+"/info/app")
  public ResponseEntity<?> appInfo() {
    Map<String, String> appInfo = new HashMap<>();
    appInfo.put("gitHash", buildProperties.get("gitHash"));
    appInfo.put("branchName", buildProperties.get("branchName"));
    appInfo.put("lastTag", buildProperties.get("lastTag"));
    appInfo.put("version", buildProperties.get("version"));
    appInfo.put("buildTime", buildProperties.get("time"));
    appInfo.put("startTime", Long.toString(applicationStartupService.getApplicationReadyTimestamp()));
    return ResponseEntity.status(HttpStatus.OK).body(appInfo);
  }

  @Operation(summary = "Ping",
    description = "Metodo per verificare che il server stia rispondendo correttamente",
    responses = { @ApiResponse(description = "La data corrente in millisecondi dal 01/Gen/1970")})
  @GetMapping(MyPay4AbstractSecurityConfig.PATH_PUBLIC+"/info/ping")
  public ResponseEntity<?> ping() {
    return ResponseEntity.status(HttpStatus.OK).body(""+System.currentTimeMillis());
  }
  @Operation(summary = "CacheFlush",
    description = "Metodo per svuotare la cache applicativa",
    responses = { @ApiResponse(description = "L'elenco dei nomi delle cache svuotate")})
  @GetMapping(MyPay4AbstractSecurityConfig.PATH_APP_ADMIN+"/cache/flush")
  @Operatore(appAdmin = true)
  public ResponseEntity<?> cacheFlush() {
    try {
      String cacheNames = cacheManager.getCacheNames()
        .stream()
        .peek(c -> cacheManager.getCache(c).clear())
        .collect(Collectors.joining(";"));
      log.warn("cache flushed by user request: "+cacheNames);
      return ResponseEntity.ok("cache flushed: "+cacheNames);
    } catch(Exception e){
      log.warn("error flushing cache", e);
      return ResponseEntity.ok("cache not flushed: "+e);
    }
  }

  @GetMapping(MyPay4AbstractSecurityConfig.PATH_APP_ADMIN+"/grant/{cfUser}")
  @Operatore(appAdmin = true)
  public ResponseEntity<?> checkGrantOperatore(@PathVariable String cfUser){
    Map<String, Object> grant = new HashMap<>();
    Object value;
    try {
      value = myProfileService.getUserTenantsAndRoles(cfUser);
    } catch(Exception e){
      log.error("checkGrantOperatore - myprofile", e);
      value = e.toString();
    }
    grant.put("myProfile", value);
    try {
      value = enteService.getEntiByOperatoreUsername(cfUser).stream().map(ente -> ente.getCodIpaEnte());
    } catch(Exception e){
      log.error("checkGrantOperatore - enti", e);
      value = e.toString();
    }
    grant.put("enti", value);
    return ResponseEntity.status(HttpStatus.OK).body(grant);
  }
}
