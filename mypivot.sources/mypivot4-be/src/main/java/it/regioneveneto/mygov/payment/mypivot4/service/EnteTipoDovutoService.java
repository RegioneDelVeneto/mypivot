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

import it.regioneveneto.mygov.payment.mypay4.exception.ManagedException;
import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import it.regioneveneto.mygov.payment.mypay4.exception.NotFoundException;
import it.regioneveneto.mygov.payment.mypay4.logging.LogExecution;
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypay4.util.VerificationUtils;
import it.regioneveneto.mygov.payment.mypivot4.dao.EnteDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.EnteTipoDovutoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.OperatoreEnteTipoDovutoDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.EnteTipoDovutoTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.EnteTipoDovuto;
import it.regioneveneto.mygov.payment.mypivot4.model.OperatoreEnteTipoDovuto;
import it.veneto.regione.pagamenti.pivot.ente.FaultBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EnteTipoDovutoService {

  @Autowired
  private EnteTipoDovutoDao enteTipoDovutoDao;

  @Autowired
  private EnteDao enteDao;

  @Autowired
  private OperatoreEnteTipoDovutoDao operatoreEnteTipoDovutoDao;

  @Resource
  private EnteTipoDovutoService self;

  @Cacheable(value="enteTipoDovutoCache", key = "{'mygovEnteId+operatoreUsername',#mygovEnteId,#operatoreUsername}", unless="#result==null")
  public List<EnteTipoDovuto> getByMygovEnteIdAndOperatoreUsername(Long mygovEnteId, String operatoreUsername) {
    return enteTipoDovutoDao.getByMygovEnteIdAndOperatoreUsername(mygovEnteId, operatoreUsername);
  }

  @Cacheable(value="enteTipoDovutoCache", key = "{'mygovEnteId+flags',#mygovEnteId, #esterno}", unless="#result==null")
  public List<EnteTipoDovuto> getByMygovEnteIdAndFlags(Long mygovEnteId, Boolean esterno) {
    return  enteTipoDovutoDao.getByMygovEnteIdAndFlags(mygovEnteId, esterno);
  }

  public List<String> getTipoByCodIpaEnte(String codIpaEnte) {
    return enteTipoDovutoDao.getTipoByCodIpaEnte(codIpaEnte);
  }

  @Cacheable(value="enteTipoDovutoCache", key="{'id',#id}", unless="#result==null")
  @LogExecution(params = LogExecution.ParamMode.ON, returns = LogExecution.ParamMode.ON)  //TODO to remove: added just as a configuration example
  public Optional<EnteTipoDovuto> getById(Long id) {
    return enteTipoDovutoDao.getById(id);
  }

  @Cacheable(value="enteTipoDovutoCache", key="{'codTipo',#codTipo,#codIpaEnte}", unless="#result==null")
  public Optional<EnteTipoDovuto> getByCodTipo(String codTipo, String codIpaEnte) {
    return enteTipoDovutoDao.getByCodTipo(codTipo, codIpaEnte);
  }

  @Cacheable(value="enteTipoDovutoCache", key="{'codTipoEnteId',#codTipo,#idEnte}", unless="#result==null")
  public Optional<EnteTipoDovuto> getByCodTipo(String codTipo, Long idEnte) {
    return enteTipoDovutoDao.getByCodTipoEnteId(codTipo, idEnte);
  }

  public List<EnteTipoDovuto> getByEnteCodFedUserId(Long enteId, String codFedUserId) {
    Ente ente = enteDao.getEnteById(enteId);
    List<OperatoreEnteTipoDovuto> oetd = operatoreEnteTipoDovutoDao.getByCodIpaCodTipoCodFed(ente.getCodIpaEnte(), null, codFedUserId);
    return oetd.stream().map(o -> o.getMygovEnteTipoDovutoId()).collect(Collectors.toList());
  }

  public FaultBean verificaTipiDovuto(String codIpaEnte, List<String> tipiDovuto) {
    List<String> tipiDovutoForEnte = getTipoByCodIpaEnte(codIpaEnte);
    List<String> invalidTipi = tipiDovuto.stream().filter(tipi -> !tipiDovutoForEnte.contains(tipi)).collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(invalidTipi)) {
      String faultString = "Tipo dovuto [ " + String.join(",", invalidTipi) + " ] non valido per ente: " + codIpaEnte;
      log.error(faultString);
      return VerificationUtils.getFaultBean(codIpaEnte, Constants.CODE_PIVOT_ENTE_NON_VALIDO, faultString,null);
    }
    return null;
  }

  @CacheEvict(value="enteTipoDovutoCache", allEntries = true)
  @Transactional(propagation = Propagation.REQUIRED)
  public EnteTipoDovutoTo insertTipoDovuto(EnteTipoDovutoTo enteTipoDovutoTo) {
    EnteTipoDovutoTo retValue =  enteTipoDovutoDao.getByCodTipoEnteId(enteTipoDovutoTo.getCodTipo(), enteTipoDovutoTo.getMygovEnteId())
        .or( () -> Optional.of(EnteTipoDovuto.builder()
            .mygovEnteId( Ente.builder().mygovEnteId(enteTipoDovutoTo.getMygovEnteId()).build() )
            .codTipo(enteTipoDovutoTo.getCodTipo())
            .deTipo(enteTipoDovutoTo.getDeTipo())
            .esterno(true)
            .build() ))
        .filter( enteTipoDovuto -> enteTipoDovuto.getMygovEnteTipoDovutoId()==null )  // check if enteTipoDovuto already exists
        .map( enteTipoDovutoDao::insert )
        .flatMap( enteTipoDovutoDao::getById )
        .map( this::mapEnteTipoDovutoToDto )
        .orElseThrow(() -> new MyPayException("Codice tipo dovuto già presente"));

    return retValue;
  }

  @CacheEvict(value="enteTipoDovutoCache", allEntries = true)
  @Transactional(propagation = Propagation.REQUIRED)
  public int updateTipoDovuto(Long mygovEnteTipoDovutoId, String codTipo, String deTipo){
    int retValue = enteTipoDovutoDao.getById(mygovEnteTipoDovutoId)
        .filter(enteTipoDovuto -> enteTipoDovuto.isEsterno()) //only update external tipo dovuto
        .map( enteTipoDovuto -> enteTipoDovutoDao.update(mygovEnteTipoDovutoId, codTipo, deTipo) )
        .orElseThrow(NotFoundException::new);

    return retValue;
  }

  @CacheEvict(value="enteTipoDovutoCache", allEntries = true)
  @Transactional(propagation = Propagation.REQUIRED)
  public int deleteTipoDovuto(Long mygovEnteTipoDovutoId){
    int retValue = enteTipoDovutoDao.getById(mygovEnteTipoDovutoId)
        .map( enteTipoDovuto -> { try{
          return enteTipoDovutoDao.delete(mygovEnteTipoDovutoId);
        } catch(UnableToExecuteStatementException utese){
          if(utese.getCause() instanceof PSQLException &&
              "23503".equals(((PSQLException) utese.getCause()).getSQLState()) ){ //foreign key violation on delete
            throw new ManagedException("Non è possibile eliminare il tipo dovuto perchè esistono accertamenti con tale tipo");
          } else
            throw utese;
        } })
        .orElseThrow(NotFoundException::new);

    return retValue;
  }

  public EnteTipoDovutoTo mapEnteTipoDovutoToDto(EnteTipoDovuto enteTipoDovuto) {
    return enteTipoDovuto == null ? null : EnteTipoDovutoTo.builder()
        .mygovEnteTipoDovutoId(enteTipoDovuto.getMygovEnteTipoDovutoId())
        .codTipo(enteTipoDovuto.getCodTipo())
        .deTipo(enteTipoDovuto.getDeTipo())
        .build();
  }

}
