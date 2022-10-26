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

import it.regioneveneto.mygov.payment.mypay4.util.MaxResultsHelper;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.dao.FlussoRendicontazioneDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.RendicontazioneTo;
import it.regioneveneto.mygov.payment.mypivot4.model.FlussoRendicontazione;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class RendicontazioneService {

  @Autowired
  MaxResultsHelper maxResultsHelper;
  @Autowired
  FlussoRendicontazioneDao rendicontazioneDao;

  public RendicontazioneTo getDistinctFlussoRendicontazioneByEnteIuf(Long idEnte, String idFlusso) {
    return rendicontazioneDao.getDistinctByEnteIuf(idEnte, idFlusso).map(this::mapToPayload).orElse(null);
  }

  public List<FlussoRendicontazione> getFlussoRendicontazioneByEnteIuf(Long idEnte, String idFlusso) {
    return rendicontazioneDao.getByEnteIuf(idEnte, idFlusso);
  }

  public List<FlussoRendicontazione> getFlussoRendicontazioneByEnteIufIur(Long idEnte, String idFlusso, String idRegolamento) {
    return rendicontazioneDao.getByEnteIufIur(idEnte, idFlusso, idRegolamento);
  }

  public List<RendicontazioneTo> searchRendicontazione(Long idEnte, RendicontazioneTo searchParams) {
    String idRendicontazione = StringUtils.stripToNull(searchParams.getIdRendicontazione());
    String idRegolamento = StringUtils.stripToNull(searchParams.getIdRegolamento());
    Date dateFrom = Utilities.toSqlDate(searchParams.getDateRegolFrom());
    Date dateTo = Utilities.toSqlDate(searchParams.getDateRegolTo());

    return maxResultsHelper.manageMaxResults(
        maxResults -> rendicontazioneDao.search(idEnte, idRendicontazione, idRegolamento, dateFrom, dateTo, maxResults)
          .stream()
          .map(this::mapToPayload)
          .collect(toList()),
        () -> rendicontazioneDao.searchCount(idEnte, idRendicontazione, idRegolamento, dateFrom, dateTo) );
  }

  public RendicontazioneTo mapToPayload(FlussoRendicontazione entity) {
    log.debug(entity.toString());
    return Optional.ofNullable(entity)
        .map(r -> RendicontazioneTo.builder()
            .idRendicontazione(r.getCodIdentificativoFlusso())
            .idRegolamento(r.getCodIdentificativoUnivocoRegolamento())
            .dateFlusso(Utilities.toLocalDateTime(r.getDtDataOraFlusso()))
            .dataRegolamento(Utilities.toLocalDate(r.getDtDataRegolamento()))
            .importoTotale(r.getNumImportoTotalePagamenti().toString())
            .countTotalePagamenti(r.getNumNumeroTotalePagamenti())
            .build()
        ).orElseGet(RendicontazioneTo::new);

  }
}
