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

import it.regioneveneto.mygov.payment.mypivot4.dao.AnagraficaStatoDao;
import it.regioneveneto.mygov.payment.mypivot4.model.AnagraficaStato;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AnagraficaStatoService {

  @Autowired
  private AnagraficaStatoDao anagraficaStatoDao;

  @Cacheable(value="anagraficaStatoCache", key="{'codStato+deTipoStato',#codStato,#deTipoStato}", unless="#result==null")
  public AnagraficaStato getByCodStatoAndTipoStato(String codStato, String deTipoStato) {
    return anagraficaStatoDao.getByCodStatoAndTipoStato(codStato, deTipoStato);
  }

  @Cacheable(value="anagraficaStatoCache", key="{'id',#mygovAnagraficaStatoId}", unless="#result==null")
  public AnagraficaStato getById(Long mygovAnagraficaStatoId) {
    return anagraficaStatoDao.getById(mygovAnagraficaStatoId);
  }

  public List<AnagraficaStato> getByTipoStato(String deTipoStato) {
    return anagraficaStatoDao.getByTipoStato(deTipoStato);
  }
}

