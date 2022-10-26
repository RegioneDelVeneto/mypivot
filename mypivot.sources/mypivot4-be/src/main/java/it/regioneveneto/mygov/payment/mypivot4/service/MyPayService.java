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
package it.regioneveneto.mygov.payment.mypivot4.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoRicevutaTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class MyPayService {

  private String baseUrl;

  private PrivateKey mypayPrivateKey;

  private final RestTemplate restTemplate;

  public MyPayService(RestTemplateBuilder restTemplateBuilder, ConfigurableEnvironment env) {
    this.baseUrl = env.getProperty("a2a.mypay.baseUrl");
    //generate private key
    try{
      String mypayJwtPrivateKey = env.getProperty("a2a.mypay.private");
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Decoders.BASE64.decode(mypayJwtPrivateKey));
      KeyFactory kf = KeyFactory.getInstance("RSA");
      this.mypayPrivateKey = kf.generatePrivate(keySpec);
    } catch(Exception e){
      log.error("error while generating JWT Token key for MyPay", e);
      throw new MyPayException("error while generating JWT Token key for MyPay", e);
    }
    this.restTemplate = restTemplateBuilder.build();
  }

  public FlussoRicevutaTo getRtInfo(String codIpaEnte, String iuv){
    String url = baseUrl + String.format("a2a/pagati/info/%s/%s", codIpaEnte, iuv);
    HttpHeaders headers = new HttpHeaders();
    Map<String, Object> claims = new HashMap<>();
    claims.put("type", "a2a");
    claims.put("jti", UUID.randomUUID().toString());
    String jwtToken = Jwts.builder()
        .setClaims(claims)
        .setSubject("mypivot")
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis()+60*1000)) // 1 minute validity
        .signWith(mypayPrivateKey).compact();
    headers.setBearerAuth(jwtToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    log.info("invoking mypay api to get rt info, ente:"+codIpaEnte+" - iuv:"+iuv);
    ResponseEntity<Map> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
    Map<String, String> info = response.getBody();
    log.info("invoked mypay api to get rt info, ente:"+codIpaEnte+" - iuv:"+iuv+" - returned values: "+(info.size()==0 ? "<not found>" : "deStato="+info.get("deStato")));
    FlussoRicevutaTo dto = FlussoRicevutaTo.builder()
        .codIud(info.get("iud"))
        .codRpSilinviarpIdUnivocoVersamento(info.get("iuv"))
        .deStato(info.get("deStato"))
        .codESoggPagAnagraficaPagatore(info.get("anagPagatore"))
        .codESoggPagIdUnivPagCodiceIdUnivoco(info.get("cfPagatore"))
        .build();
    return dto;
  }

}
