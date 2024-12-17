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

import io.jsonwebtoken.Claims;
import it.regioneveneto.mygov.payment.mypay4.security.JwtTokenUtil;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypay4.service.common.CacheService;
import it.regioneveneto.mygov.payment.mypivot4.dao.UtenteDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.OperatoreTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.UtenteTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Utente;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class UtenteService {
  
  @Resource
  private UtenteService self;

  @Autowired
  private UtenteDao utenteDao;

  @Cacheable(value = CacheService.CACHE_NAME_UTENTE, key = "{'codFedUserId',#codFedUserId}", unless = "#result==null")
  public Optional<Utente> getByCodFedUserId(String codFedUserId) {
    return utenteDao.getByCodFedUserId(codFedUserId);
  }

  @CacheEvict(value=CacheService.CACHE_NAME_UTENTE,key="{'codFedUserId',#codFedUserId}")
  public void clearCacheByCodFedUserId(String codFedUserId){}

  @Cacheable(value = CacheService.CACHE_NAME_UTENTE, key = "{'codIpaEnte',#codIpaEnte}", unless = "#result==null")
  public List<Utente> getByCodIpaEnte(String codIpaEnte) {
    return utenteDao.getByCodIpaEnte(codIpaEnte);
  }

  @Cacheable(value = CacheService.CACHE_NAME_UTENTE, key = "{'WScodIpaEnte',#codIpaEnte}", unless = "#result==null")
  public Utente getUtenteWSByCodIpaEnte(final String codIpaEnte) {
    return utenteDao.getByCodFedUserIdIgnoreCase(codIpaEnte + "-WS_USER");
  }

  public UtenteTo mapUtenteToDto(Utente utente) {
    return UtenteTo.builder()
      .userId(utente.getMygovUtenteId())
      .username(utente.getCodFedUserId())
      .codiceFiscale(utente.getCodCodiceFiscaleUtente())
      .email(utente.getDeEmailAddress())
      .emailSourceType(utente.getEmailSourceType())
      .nome(utente.getDeFirstname())
      .cognome(utente.getDeLastname())
      .build();
  }

  public OperatoreTo mapUtenteToOperatoreDto(Utente utente) {
    return OperatoreTo.builder()
      .userId(utente.getMygovUtenteId())
      .username(utente.getCodFedUserId())
      .codiceFiscale(utente.getCodCodiceFiscaleUtente())
      .email(utente.getDeEmailAddress())
      .nome(utente.getDeFirstname())
      .cognome(utente.getDeLastname())
      .build();
  }

  public Utente mapLoginClaimsToUtente(Claims claims) {
    return Utente.builder()
      .codFedUserId(claims.getSubject())
      .deEmailAddress(claims.get(JwtTokenUtil.JWT_CLAIM_EMAIL, String.class))
      .emailSourceType(Utente.EMAIL_SOURCE_TYPES.AUTH_SYSTEM.asChar())
      .deFirstname(claims.get(JwtTokenUtil.JWT_CLAIM_NOME, String.class))
      .deLastname(claims.get(JwtTokenUtil.JWT_CLAIM_COGNOME, String.class))
      .codCodiceFiscaleUtente(claims.get(JwtTokenUtil.JWT_CLAIM_CODICE_FISCALE, String.class))
      .build();
  }

  public Map<String, Object> mapUserToLoginClaims(UserWithAdditionalInfo userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(JwtTokenUtil.JWT_CLAIM_COGNOME, userDetails.getFamilyName());
    claims.put(JwtTokenUtil.JWT_CLAIM_NOME, userDetails.getFirstName());
    claims.put(JwtTokenUtil.JWT_CLAIM_CODICE_FISCALE, userDetails.getCodiceFiscale());
    claims.put(JwtTokenUtil.JWT_CLAIM_EMAIL, userDetails.getEmail());
    claims.put(JwtTokenUtil.JWT_CLAIM_EMAIL_SOURCE_TYPE,String.valueOf(userDetails.getEmailSourceType()));
    return claims;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Utente upsertUtente(Utente utente) {
    final Utente[] utenteToReturn = new Utente[1];
    utenteDao.getByCodFedUserId(utente.getCodFedUserId()).ifPresentOrElse(existingUtente -> {
        boolean skipEmailUpdate = existingUtente.isChosenByUserEmail()
          && utente.getEmailSourceType()==Utente.EMAIL_SOURCE_TYPES.AUTH_SYSTEM.asChar()
          || StringUtils.isBlank(utente.getDeEmailAddress());
        boolean equals = Objects.equals(existingUtente.getCodCodiceFiscaleUtente(), utente.getCodCodiceFiscaleUtente())
          && (skipEmailUpdate || Objects.equals(existingUtente.getDeEmailAddress(), utente.getDeEmailAddress()))
          && Objects.equals(existingUtente.getDeFirstname(), utente.getDeFirstname())
          && Objects.equals(existingUtente.getDeLastname(), utente.getDeLastname());

        existingUtente.setCodCodiceFiscaleUtente(utente.getCodCodiceFiscaleUtente());
        if (!skipEmailUpdate) {
          existingUtente.setDeEmailAddress(utente.getDeEmailAddress());
          existingUtente.setEmailSourceType(utente.getEmailSourceType());
        }
        existingUtente.setDeFirstname(utente.getDeFirstname());
        existingUtente.setDeLastname(utente.getDeLastname());
        existingUtente.setDtUltimoLogin(utente.getDtUltimoLogin());

        //increase version by 1 if at least a field is not equal (except last login time)
        // version passed by input object is ignored
        existingUtente.setVersion(existingUtente.getVersion() + (equals ? 0 : 1));
        //update utente
        utenteDao.update(existingUtente);
        //clear invalid cache
        self.clearCacheByCodFedUserId(existingUtente.getCodFedUserId());
        utenteToReturn[0] = existingUtente;
      }, () -> {
        log.info("adding new utente to DB: " + utente.getCodFedUserId() + " (" + utente.getDeFirstname() + " - " + utente.getDeLastname() + ")");
        long newUtenteId = utenteDao.insert(utente);
        utenteToReturn[0] = utenteDao.getById(newUtenteId);
      }
    );

    return utenteToReturn[0];
  }
}
