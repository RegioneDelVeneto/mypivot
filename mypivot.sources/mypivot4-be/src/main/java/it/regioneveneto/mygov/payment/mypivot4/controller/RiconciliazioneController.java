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
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.dto.ClassificazioneTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.RiconciliazioneSearchTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.RiconciliazioneTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.PrenotazioneFlussoRiconciliazione;
import it.regioneveneto.mygov.payment.mypivot4.model.RiconciliazioneSearch;
import it.regioneveneto.mygov.payment.mypivot4.model.Utente;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteService;
import it.regioneveneto.mygov.payment.mypivot4.service.PrenotazioneFlussoRiconciliazioneService;
import it.regioneveneto.mygov.payment.mypivot4.service.RiconciliazioneService;
import it.regioneveneto.mygov.payment.mypivot4.service.UtenteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Riconciliazione", description = "Ricerca e visualizzazione dei record riconciliati")
@RestController
@RequestMapping("riconciliazione")
@Slf4j
@ConditionalOnWebApplication
public class RiconciliazioneController {

  @Autowired
  private RiconciliazioneService riconciliazioneService;

  @Autowired
  private PrenotazioneFlussoRiconciliazioneService prenotazioneFlussoRiconciliazioneService;

  @Autowired
  private EnteService enteService;

  @Autowired
  private UtenteService utenteService;

  @GetMapping(value = {"classificazioni/{mygovEnteId}","classificazioni/{mygovEnteId}/{viewType}"})
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public List<ClassificazioneTo> getClassificazioni(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                                    @PathVariable Long mygovEnteId, @PathVariable(required = false) String viewType){
    Constants.TIPO_VISUALIZZAZIONE viewTypeObj = null;
    try{
      if(viewType != null)
        viewTypeObj = Constants.TIPO_VISUALIZZAZIONE.byValue(viewType);
    } catch(IllegalArgumentException iae){
      throw new BadRequestException("Invalid view type: "+viewType);
    }
    Ente ente = Optional.of(enteService.getEnteById(mygovEnteId))
        .orElseThrow(() -> new BadRequestException("Invalid ente: "+mygovEnteId));

    return riconciliazioneService.getClassificazioniEnte(ente, viewTypeObj);
  }


  @PostMapping("/search/{mygovEnteId}/{searchType}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public List<RiconciliazioneTo> searchByType(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                              @PathVariable Long mygovEnteId, @PathVariable String searchType,
                                              @RequestBody RiconciliazioneSearchTo searchParams) {
    Ente ente = Optional.of(enteService.getEnteById(mygovEnteId)).orElseThrow(NotFoundException::new);
    //verify ente is coherent with searchType
    ClassificazioneTo classificazione = riconciliazioneService.getClassificazioniEnte(ente, null).stream()
        .filter(x -> x.getKey().equals(searchType))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("Invalid search type: "+searchType));
    log.info("search riconciliazioni, ente:"+ente.getCodIpaEnte()+"search type: "+classificazione);

    return riconciliazioneService.search(user, ente, classificazione, searchParams);
  }

  @GetMapping("/detail/{mygovEnteId}/{searchType}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public RiconciliazioneTo getDetail(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                           @PathVariable Long mygovEnteId, @PathVariable String searchType,
                                           @RequestParam String iuf, @RequestParam("iud") String iudOrIndex, @RequestParam String iuv) {
    //iudOrInxed is IUD except for "pagamenti doppi" where it represents the index of single payment
    Ente ente = Optional.of(enteService.getEnteById(mygovEnteId)).orElseThrow(NotFoundException::new);
    //verify ente is coherent with searchType
    ClassificazioneTo classificazione = riconciliazioneService.getClassificazioniEnte(ente, null).stream()
        .filter(x -> x.getKey().equals(searchType))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("Invalid search type: "+searchType));
    log.info("search riconciliazioni, ente: "+ente.getCodIpaEnte()+" - search type: "+classificazione);
    //normalize null params
    iuf = Utilities.nullNormalize(iuf);
    iudOrIndex = Utilities.nullNormalize(iudOrIndex);
    iuv = Utilities.nullNormalize(iuv);

    return riconciliazioneService.getDetail(user, ente, classificazione, iuf, iudOrIndex, iuv);
  }

  @PostMapping("/export/{mygovEnteId}/{searchType}/{versioneTracciato}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public ResponseEntity<?> export(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                  @PathVariable Long mygovEnteId, @PathVariable String searchType,
                                  @PathVariable String versioneTracciato, @RequestBody RiconciliazioneSearchTo searchParams) {
    Ente ente = Optional.of(enteService.getEnteById(mygovEnteId)).orElseThrow(NotFoundException::new);
    //verify correct versione tracciato
    try {
      Constants.VERSIONE_TRACCIATO_EXPORT.fromString(versioneTracciato);
    } catch (IllegalArgumentException iae){
      throw new BadRequestException("Invalid versione tracciato: "+versioneTracciato);
    }

    //verify ente is coherent with searchType
    ClassificazioneTo classificazione = riconciliazioneService.getClassificazioniEnte(ente, null)
        .stream()
        .filter(x -> x.getKey().equals(searchType))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("Invalid search type: "+searchType));
    log.info("export riconciliazioni, ente:"+ente.getCodIpaEnte()+"search type: "+classificazione);

    //user
    Utente utente = utenteService.getByCodFedUserId(user.getUsername()).orElseThrow(NotFoundException::new);

    RiconciliazioneSearch p = searchParams.toEntity(user.getUsername(), ente.getCodIpaEnte(), classificazione.getKey());
    PrenotazioneFlussoRiconciliazione prenotazioneFlussoRiconciliazione = prenotazioneFlussoRiconciliazioneService.insert(
        ente,
        utente,
        Constants.COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_PRENOTATO,
        Constants.DE_TIPO_STATO_PRENOTA_FLUSSO_RICONCILIAZIONE,
        classificazione.getKey(),
        p.getCod_tipo_dovuto(),
        p.getCod_iuv(),
        p.getIdentificativo_flusso_rendicontazione(),
        p.getDt_data_ultimo_aggiornamento_da(),
        p.getDt_data_ultimo_aggiornamento_a(),
        p.getData_esecuzione_singolo_pagamento_da(),
        p.getData_esecuzione_singolo_pagamento_a(),
        p.getData_esito_singolo_pagamento_da(),
        p.getData_esito_singolo_pagamento_a(),
        p.getData_regolamento_da(),
        p.getData_regolamento_a(),
        p.getDt_data_contabile_da(),
        p.getDt_data_contabile_a(),
        p.getDt_data_valuta_da(),
        p.getDt_data_valuta_a(),
        p.getCod_iud(),
        p.getIdentificativo_univoco_riscossione(),
        p.getCodice_identificativo_univoco_pagatore(),
        p.getAnagrafica_pagatore(),
        p.getCodice_identificativo_univoco_versante(),
        p.getAnagrafica_versante(),
        p.getDenominazione_attestante(),
        p.getCod_or1(),
        p.getIdentificativo_univoco_regolamento(),
        p.getConto(),
        p.getImporto(),
        p.getCausale_versamento(),
        versioneTracciato,
        p.getCod_bolletta(),
        p.getCod_documento(),
        p.getCod_provvisorio(),
        p.getDe_anno_bolletta(),
        p.getDe_anno_documento(),
        p.getDe_anno_provvisorio()
        );
    log.info("export booked with requestToken: "+prenotazioneFlussoRiconciliazione.getCodRequestToken());

    Map<String, String> appInfo = new HashMap<>();
    appInfo.put("requestToken", prenotazioneFlussoRiconciliazione.getCodRequestToken());
    return ResponseEntity.status(HttpStatus.OK).body(appInfo);
  }

}
