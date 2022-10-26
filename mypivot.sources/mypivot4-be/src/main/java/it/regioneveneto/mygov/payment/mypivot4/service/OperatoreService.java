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

import it.regioneveneto.mygov.payment.mypivot4.dao.OperatoreDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.OperatoreTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Operatore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class OperatoreService {

  @Autowired
  private OperatoreDao operatoreDao;
  @Autowired
  private UtenteService utenteService;

  public Optional<Operatore> getById(Long id){
    return operatoreDao.getById(id);
  }

  public OperatoreTo mapOperatoreToDto(Operatore operatore) {
    return utenteService.getByCodFedUserId(operatore.getCodFedUserId())
        .map(utenteService::mapUtenteToOperatoreDto)
        .orElse(
            OperatoreTo.builder()
                .username(operatore.getCodFedUserId())
                .nome("<utente non registrato>")
                .cognome("<utente non registrato>")
                .notRegisteredUser(true)
        .build())
        .toBuilder()
        .operatoreId(operatore.getMygovOperatoreId())
        .flgAssociazione(operatore.getFlgAssociazione().orElse(null))
        .build();
  }

}
