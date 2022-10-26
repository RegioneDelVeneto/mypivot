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

import it.regioneveneto.mygov.payment.mypay4.exception.NotFoundException;
import it.regioneveneto.mygov.payment.mypivot4.dao.AnagraficaStatoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.EnteDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.ManageFlussoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.UtenteDao;
import it.regioneveneto.mygov.payment.mypivot4.model.AnagraficaStato;
import it.regioneveneto.mygov.payment.mypivot4.model.ManageFlusso;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Slf4j
@Service
public class ManageFlussoService {
  @Autowired
  private EnteDao enteDao;

  @Autowired
  private UtenteDao utenteDao;

  @Autowired
  private TipoFlussoService tipoFlussoService;

  @Autowired
  private ManageFlussoDao manageFlussoDao;

  @Autowired
  private AnagraficaStatoDao anagraficaStatoDao;


  @Transactional(propagation = Propagation.REQUIRED)
  public long insert(String codTipoFlusso, String codIpaEnte, String codFedUserId, String codRequestToken, String codProvenienza,
                     String deTipoStato, String codStato, String relativePath, String filename, Timestamp tsCreazione) {
    AnagraficaStato anagraficaStato = anagraficaStatoDao.getByCodStatoAndTipoStato(codStato, deTipoStato);

    ManageFlusso manageFlusso = new ManageFlusso();
    manageFlusso.setMygovTipoFlussoId(tipoFlussoService.getByCodTipo(codTipoFlusso)
        .orElseThrow(()->new NotFoundException(("tipo flusso not found"))));
    manageFlusso.setMygovEnteId(enteDao.getEnteByCodIpa(codIpaEnte));
    manageFlusso.setMygovAnagraficaStatoId(anagraficaStato);
    manageFlusso.setCodRequestToken(codRequestToken);
    manageFlusso.setMygovUtenteId(utenteDao.getByCodFedUserId(codFedUserId).orElseThrow(()->new NotFoundException(("utente not found"))));
    manageFlusso.setCodProvenienzaFile(codProvenienza);
    manageFlusso.setDePercorsoFile(relativePath);
    manageFlusso.setDeNomeFile(filename);

    manageFlusso.setDtCreazione(tsCreazione);
    manageFlusso.setDtUltimaModifica(tsCreazione);
    return manageFlussoDao.insert(manageFlusso);
  }

  public boolean isDuplicateFileName(String deNomeFile) {
    return manageFlussoDao.countDuplicateFileName(deNomeFile) > 0;
  }

  public ManageFlusso getByCodRequestToken(String codRequestToken) {
    return manageFlussoDao.getByCodRequestToken(codRequestToken);
  }

  public boolean isDuplicateRequestToken(String codRequestToken) {
    return manageFlussoDao.existingRequestToken(codRequestToken);
  }

}
