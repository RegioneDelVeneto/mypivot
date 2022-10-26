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
package it.regioneveneto.mygov.payment.mypivot4.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.mygov.payment.mypay4.exception.BadRequestException;
import it.regioneveneto.mygov.payment.mypay4.exception.NotFoundException;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.dto.SegnalazioneSearchTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.SegnalazioneTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.Segnalazione;
import it.regioneveneto.mygov.payment.mypivot4.model.Utente;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteService;
import it.regioneveneto.mygov.payment.mypivot4.service.RiconciliazioneService;
import it.regioneveneto.mygov.payment.mypivot4.service.SegnalazioneService;
import it.regioneveneto.mygov.payment.mypivot4.service.UtenteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Segnalazione", description = "Ricerca, visualizzazione e inserimento segnalazioni")
@RestController
@RequestMapping("segnalazione")
@Slf4j
@ConditionalOnWebApplication
public class SegnalazioneController {

  @Autowired
  private SegnalazioneService segnalazioneService;

  @Autowired
  private RiconciliazioneService riconciliazioneService;

  @Autowired
  private RiconciliazioneController riconciliazioneController;

  @Autowired
  private EnteService enteService;

  @Autowired
  private UtenteService utenteService;

  @PostMapping("/search/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public List<SegnalazioneTo> searchByType(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                              @PathVariable Long mygovEnteId,
                                              @RequestBody SegnalazioneSearchTo searchParams) {
    //check ente
    Ente ente = Optional.of(enteService.getEnteById(mygovEnteId)).orElseThrow(NotFoundException::new);
    //check classificazione
    if(StringUtils.isBlank(searchParams.getClassificazione()) || "null".equalsIgnoreCase(searchParams.getClassificazione()))
      searchParams.setClassificazione(null);
    else
      riconciliazioneService.getClassificazioniEnte(ente, null).stream()
        .filter(x -> x.getKey().equals(searchParams.getClassificazione()))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("Invalid search type: "+searchParams.getClassificazione()));
    //normalize null params
    searchParams.setIuf(Utilities.nullNormalize(searchParams.getIuf()));
    searchParams.setIud(Utilities.nullNormalize(searchParams.getIud()));
    searchParams.setIuv(Utilities.nullNormalize(searchParams.getIuv()));
    searchParams.setUtente(Utilities.nullNormalize(searchParams.getUtente()));
    //nascosto il always null (not supported in mypivot4)
    searchParams.setNascosto(null);

    return segnalazioneService.searchSegnalazioni(ente, searchParams);
  }

  @PostMapping("/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public SegnalazioneTo insert(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                  @PathVariable Long mygovEnteId, @RequestBody SegnalazioneTo segnalazioneTo) {
    //check nota is present
    if(StringUtils.isBlank(segnalazioneTo.getNota()))
      throw new BadRequestException("Missing nota");

    //normalize null params
    if(StringUtils.isBlank(segnalazioneTo.getIufKey()) || "null".equalsIgnoreCase(segnalazioneTo.getIufKey()))
      segnalazioneTo.setIufKey(null);
    if(StringUtils.isBlank(segnalazioneTo.getIudKey()) || "null".equalsIgnoreCase(segnalazioneTo.getIudKey()))
      segnalazioneTo.setIudKey(null);
    if(StringUtils.isBlank(segnalazioneTo.getIuvKey()) || "null".equalsIgnoreCase(segnalazioneTo.getIuvKey()))
      segnalazioneTo.setIuvKey(null);

    //check that riconciliazione exists
    riconciliazioneController.getDetail(user, mygovEnteId, segnalazioneTo.getClassificazione(), segnalazioneTo.getIufKey(), segnalazioneTo.getIudKey(), segnalazioneTo.getIuvKey());

    //user
    Utente utente = utenteService.getByCodFedUserId(user.getUsername()).orElseThrow(NotFoundException::new);

    //insert new segnalazione
    Segnalazione segnalazione = Segnalazione.builder()
        .classificazioneCompletezza(segnalazioneTo.getClassificazione())
        .codIud(segnalazioneTo.getIudKey())
        .codIuf(segnalazioneTo.getIufKey())
        .codIuv(segnalazioneTo.getIuvKey())
        .deNota(segnalazioneTo.getNota())
        .mygovEnteId(Ente.builder().mygovEnteId(mygovEnteId).build())
        .mygovUtenteId(Utente.builder().mygovUtenteId(utente.getMygovUtenteId()).build())
        .build();
    segnalazioneService.addSegnalazione(segnalazione);

    SegnalazioneTo segnalazioneReturn = segnalazioneService.getSegnalazioneAttivaByElement(segnalazione).map(segnalazioneService::mapToDto).orElseThrow();

    return segnalazioneReturn;
  }

  @GetMapping("/storico/utenti/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public List<Utente> getStoricoUtenti(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId) {
    Ente ente = Optional.of(enteService.getEnteById(mygovEnteId)).orElseThrow(NotFoundException::new);
    return utenteService.getByCodIpaEnte(ente.getCodIpaEnte());
  }
}
