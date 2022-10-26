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
import it.regioneveneto.mygov.payment.mypay4.util.MaxResultsHelper;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.dao.SegnalazioneDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.SegnalazioneSearchTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.SegnalazioneTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.Segnalazione;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class SegnalazioneService {

  @Autowired
  private EnteService enteService;

  @Autowired
  private MaxResultsHelper maxResultsHelper;

  @Autowired
  private SegnalazioneDao segnalazioneDao;

  @Transactional(propagation = Propagation.REQUIRED)
  public void addSegnalazione(Segnalazione segnalazione){
    List<Segnalazione> segnalazioniAttiva = segnalazioneDao.getSegnalazioniByElement(segnalazione, Boolean.TRUE);
    segnalazioniAttiva.forEach( segnalazioneAttiva -> segnalazioneDao.updateSetNonAttivo(segnalazioneAttiva.getMygovSegnalazioneId()));
    segnalazioneDao.insert(segnalazione);
  }

  public Optional<Segnalazione> getSegnalazioneAttivaByElement(Segnalazione segnalazione) {
    List<Segnalazione> segnalazioneAttiva =  segnalazioneDao.getSegnalazioniByElement(segnalazione, Boolean.TRUE);
    if(segnalazioneAttiva.size()>1)
      throw new MyPayException("invalid state - multiple active 'segnalazione'");
    if(segnalazioneAttiva.size()==1)
      return Optional.of(segnalazioneAttiva.get(0));
    else
      return Optional.empty();
  }

  public List<SegnalazioneTo> searchSegnalazioni(Ente ente, SegnalazioneSearchTo searchParams){
    if(searchParams.getDtInseritoPrima()!=null)
      searchParams.setDtInseritoPrima(searchParams.getDtInseritoPrima().plusDays(1));
    return maxResultsHelper.manageMaxResults(
        maxResults -> segnalazioneDao.searchSegnalazioni(ente.getMygovEnteId(), searchParams, maxResults)
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList()),
        () -> segnalazioneDao.searchSegnalazioniCount(ente.getMygovEnteId(), searchParams) );
  }

  public SegnalazioneTo mapToDto(Segnalazione entity){
    return SegnalazioneTo.builder()
        .id(entity.getMygovSegnalazioneId())
        .classificazione(entity.getClassificazioneCompletezza())
        .iuvKey(Utilities.nullNormalize(entity.getCodIuv()))
        .iudKey(Utilities.nullNormalize(entity.getCodIud()))
        .iufKey(Utilities.nullNormalize(entity.getCodIuf()))
        .nota(entity.getDeNota())
        .attivo(entity.isFlgAttivo())
        .nascosto(entity.isFlgNascosto())
        .dtInserimento(Utilities.toLocalDateTime(entity.getDtCreazione()))
        .utente(StringUtils.joinWith(" ", entity.getMygovUtenteId().getDeFirstname(), entity.getMygovUtenteId().getDeLastname()))
        .cfUtente(entity.getMygovUtenteId().getCodCodiceFiscaleUtente())
        .build();
  }

}
