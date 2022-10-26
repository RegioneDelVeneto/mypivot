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

import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import it.regioneveneto.mygov.payment.mypay4.logging.LogExecution;
import it.regioneveneto.mygov.payment.mypay4.service.common.ThumbnailService;
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypay4.util.VerificationUtils;
import it.regioneveneto.mygov.payment.mypivot4.dao.EnteDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.EnteTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.veneto.regione.pagamenti.pivot.ente.FaultBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EnteService {

  @Autowired
  private EnteDao enteDao;

  @Autowired
  ThumbnailService thumbnailService;

  @Cacheable(value="allObjectCache", key="{#root.method}")
  public List<Ente> getAllEnti() {
    return enteDao.getAllEnti();
  }

  @Cacheable(value="enteCache")
  public List<Ente> searchEnti(String codIpaEnte, String deNome, String codFiscale) {
    return enteDao.searchEnti(codIpaEnte, deNome, codFiscale);
  }

  @Cacheable(value="enteCache", key="{'id',#id}", unless="#result==null")
  @LogExecution(params = LogExecution.ParamMode.ON, returns = LogExecution.ParamMode.ON)  //TODO to remove: added just as a configuration example
  public Ente getEnteById(Long id) {
    return enteDao.getEnteById(id);
  }
  @Cacheable(value="enteCache", key="{'codIpa',#codIpa}", unless="#result==null")
  public Ente getEnteByCodIpa(String codIpa) {
    return enteDao.getEnteByCodIpa(codIpa);
  }
  @Cacheable(value="enteCache", key="{'codFiscale',#codFiscale}", unless="#result==null")
  public Ente getEnteByCodFiscale(String codFiscale) {
    return enteDao.getEnteByCodFiscale(codFiscale);
  }

  @Cacheable(value="enteCache")
  public List<Ente> getEntiByOperatoreUsername(String operatoreUsername) {
    return enteDao.getEntiByOperatoreUsername(operatoreUsername);
  }

  public Boolean verificaPassword(final String codIpaEnte, final String password) {
  List<Ente> entes = enteDao.findByCodIpaEnteAndNullPassword(codIpaEnte);

    if (entes.size() > 1) {
      throw new DataIntegrityViolationException("mypivot.ente.enteDuplicato");
    }

    //se la password in database e' NULL autorizzo
    if (entes.size() == 1)
      return true;

    if (password == null) {
      return false;
    }

    //altrimenti controllo che password in input corrisponda con password sul database
    entes = enteDao.findByCodIpaEnteAndPassword(codIpaEnte, password);

    if (entes.size() > 1) {
      throw new DataIntegrityViolationException("mypivot.ente.enteDuplicato");
    }
    return !entes.isEmpty();
  }

  public FaultBean verificaEnte(String codIpaEnte, String password) {
    Ente ente = enteDao.getEnteByCodIpa(codIpaEnte);
    if (ente == null) {
      String faultString = "Codice IPA Ente [" + codIpaEnte + "] non valido o password errata";
      log.error(faultString);
      return VerificationUtils.getFaultBean(codIpaEnte, Constants.CODE_PIVOT_ENTE_NON_VALIDO, faultString,null);
    }
    Boolean passwordValidaPerEnte = verificaPassword(codIpaEnte, password);
    if (!passwordValidaPerEnte) {
      String faultString = "Password non valida per ente [" + codIpaEnte + "]";
      log.error(faultString);
      return VerificationUtils.getFaultBean(codIpaEnte, Constants.CODE_PIVOT_ENTE_NON_VALIDO, faultString, null);
    }
    return null;
  }

  public EnteTo mapEnteToDtoWithoutLogo(Ente ente) {
    return ente == null ? null : EnteTo.builder()
        .mygovEnteId(ente.getMygovEnteId())
        .codIpaEnte(ente.getCodIpaEnte())
        .codiceFiscaleEnte(ente.getCodiceFiscaleEnte())
        .deNomeEnte(ente.getDeNomeEnte())
        .flgTesoreria(ente.isFlgTesoreria())
        .build();
  }

  public EnteTo mapEnteToDtoWithThumbnail(Ente ente) {
    if(ente==null)
      return null;
    EnteTo enteTo = mapEnteToDtoWithoutLogo(ente);
    try {
      thumbnailService.generateThumbnail(ente.getDeLogoEnte()).ifPresent( thumbLogoEnte -> {
        enteTo.setThumbLogoEnte(thumbLogoEnte.getContent());
        enteTo.setHashThumbLogoEnte(thumbLogoEnte.getHash());
      });
    } catch(Exception e){
      throw new MyPayException("invalid logo for ente: "+ente.getCodIpaEnte(), e);
    }
    return enteTo;
  }

  public EnteTo mapEnteToDtoWithThumbnailHash(Ente ente) {
    if(ente==null)
      return null;
    EnteTo enteTo = mapEnteToDtoWithoutLogo(ente);
    try {
      thumbnailService.getThumbnailHash(ente.getDeLogoEnte()).ifPresent(enteTo::setHashThumbLogoEnte);
    } catch(Exception e){
      throw new MyPayException("invalid logo for ente: "+ente.getCodIpaEnte(), e);
    }
    return enteTo;
  }

}
