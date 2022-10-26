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

import it.regioneveneto.mygov.payment.mypivot4.dao.OperatoreEnteTipoDovutoDao;
import it.regioneveneto.mygov.payment.mypivot4.model.Operatore;
import it.regioneveneto.mygov.payment.mypivot4.model.OperatoreEnteTipoDovuto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OperatoreEnteTipoDovutoService {

  @Autowired
  private OperatoreEnteTipoDovutoDao operatoreEnteTipoDovutoDao;

  public List<OperatoreEnteTipoDovuto> getByCodIpaCodTipoCodFed(String codIpaEnte, String codTipo, String codFedUserId) {
    return operatoreEnteTipoDovutoDao.getByCodIpaCodTipoCodFed(codIpaEnte, codTipo, codFedUserId);
  }

  @Cacheable(value="operatoreEnteTipoDovutoCache", key = "{'codIpaEnte+codFedUserId',#codIpaEnte,#codFedUserId}", unless="#result==null")
  public List<String> getTipoByCodIpaCodFedUser(String codIpaEnte, String codFedUserId) {
    return operatoreEnteTipoDovutoDao.getTipoByCodIpaCodFedUser(codIpaEnte, codFedUserId);
  }

  public List<Operatore> getOperatoriByTipoDovutoId(Long tipoDovutoId) {
    return operatoreEnteTipoDovutoDao.getOperatoriByTipoDovutoId(tipoDovutoId);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void upsertOperatoreEnteTipoDovuto(Long tipoDovutoId, Long operatoreId, boolean newState){
    Optional<OperatoreEnteTipoDovuto> operTipo = operatoreEnteTipoDovutoDao.getOperatoreEnteTipoDovuto(tipoDovutoId, operatoreId);
    if(operTipo.isPresent()){
      operatoreEnteTipoDovutoDao.updateOperatoreTipoDovuto(operTipo.get().getMygovOperatoreEnteTipoDovutoId(), newState);
    } else {
      operatoreEnteTipoDovutoDao.insertOperatoreTipoDovuto(tipoDovutoId, operatoreId, newState);
    }
  }

}
