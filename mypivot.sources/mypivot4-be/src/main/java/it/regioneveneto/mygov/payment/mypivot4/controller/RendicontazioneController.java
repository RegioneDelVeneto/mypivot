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
import it.regioneveneto.mygov.payment.mypay4.exception.NotFoundException;
import it.regioneveneto.mygov.payment.mypay4.exception.ValidatorException;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoExportKeysTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoRicevutaTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.RendicontazioneTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.RicevutaSearchTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.EnteTipoDovuto;
import it.regioneveneto.mygov.payment.mypivot4.model.FlussoRendicontazione;
import it.regioneveneto.mygov.payment.mypivot4.model.OperatoreEnteTipoDovuto;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteService;
import it.regioneveneto.mygov.payment.mypivot4.service.FlussoExportService;
import it.regioneveneto.mygov.payment.mypivot4.service.OperatoreEnteTipoDovutoService;
import it.regioneveneto.mygov.payment.mypivot4.service.RendicontazioneService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Tag(name = "Rendicontazione", description = "Rendicontazione PagoPA")
@RestController
@RequestMapping("rendicontazione")
@Slf4j
@ConditionalOnWebApplication
public class RendicontazioneController {

  @Autowired
  MessageSource messageSource;
  @Autowired
  EnteService enteService;
  @Autowired
  FlussoExportService flussoExportService;
  @Autowired
  RendicontazioneService rendicontazioneService;
  @Autowired
  OperatoreEnteTipoDovutoService operatoreEnteTipoDovutoService;

  @GetMapping("/get/{mygovEnteId}/{iuf}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public RendicontazioneTo getRendicontazione(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                              @PathVariable Long mygovEnteId, @PathVariable String iuf) {
    this.getOperatoreEnteTipoDovutos(user, mygovEnteId);
    return rendicontazioneService.getDistinctFlussoRendicontazioneByEnteIuf(mygovEnteId, iuf);
  }

  @PostMapping("/search/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public List<RendicontazioneTo> searchRendicontazione(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                                       @PathVariable Long mygovEnteId, @RequestBody RendicontazioneTo searchParams) {
    this.getOperatoreEnteTipoDovutos(user, mygovEnteId);
    return rendicontazioneService.searchRendicontazione(mygovEnteId, searchParams);
  }

  @GetMapping("/detail/{mygovEnteId}/{iuf}/{iur}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public RendicontazioneTo getDetails(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                     @PathVariable String iuf, @PathVariable String iur) {
    return getRendicontazioneTo(user, mygovEnteId, iuf, iur, Optional.empty());
  }

  @PostMapping("/detail/{mygovEnteId}/{iuf}/{iur}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public RendicontazioneTo filterDetails(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                        @PathVariable String iuf, @PathVariable String iur, @RequestBody RicevutaSearchTo searchParams) {
    return getRendicontazioneTo(user, mygovEnteId, iuf, iur, Optional.ofNullable(searchParams));
  }

  private RendicontazioneTo getRendicontazioneTo(UserWithAdditionalInfo user, Long mygovEnteId, String iuf, String iur, Optional<RicevutaSearchTo> optParams) {

    List<String> deTipoDovutoList = getOperatoreEnteTipoDovutos(user, mygovEnteId)
        .stream()
        .map(OperatoreEnteTipoDovuto::getMygovEnteTipoDovutoId)
        .map(EnteTipoDovuto::getDeTipo)
        .collect(toList());

    List<FlussoRendicontazione> rendicontazioneList = rendicontazioneService.getFlussoRendicontazioneByEnteIufIur(mygovEnteId, iuf, iur);

    if(rendicontazioneList.isEmpty())
      throw new NotFoundException("flussoRendicontazione not found for this IUF and IUR");

    Set<FlussoExportKeysTo> keySet = rendicontazioneList.stream()
        .map(flussoExportService::mapToKeySet)
        .collect(toSet());

    List<FlussoRicevutaTo> flussoExportList = flussoExportService.findPagatiByKeySet(keySet, flussoExportService.stripToNull(Optional.empty()))
      .stream().map(flussoExportService::mapToPayload).collect(toList());
    List<FlussoRicevutaTo> details = rendicontazioneList.stream()
        .map(flussoExportService::mapToPayload)
        .map(rend -> flussoExportList.stream()
            .filter(fe -> flussoExportService.testEqualsKeys(fe).test(rend))
            .findFirst()
            .orElse(rend))
        .filter(flussoExport -> {
          boolean filtered = true;
          if (!optParams.isEmpty()) {
            //IUV
            filtered = StringUtils.isBlank(optParams.get().getIuv()) || flussoExport.getCodRpSilinviarpIdUnivocoVersamento().toLowerCase().contains(optParams.get().getIuv().toLowerCase());
            //IUD
            filtered = filtered && (StringUtils.isBlank(optParams.get().getIud()) || flussoExport.getCodIud().toLowerCase().contains(optParams.get().getIud().toLowerCase()));
            //IUR
            filtered = filtered && (StringUtils.isBlank(optParams.get().getIur()) || flussoExport.getCodEDatiPagDatiSingPagIdUnivocoRiscoss().toLowerCase().contains(optParams.get().getIur().toLowerCase()));
            //cf pagatore
            filtered = filtered && (StringUtils.isBlank(optParams.get().getCodFiscalePagatore()) || flussoExport.getCodESoggPagIdUnivPagCodiceIdUnivoco().toLowerCase().contains(optParams.get().getCodFiscalePagatore().toLowerCase()));
            //anagrafica pagatore
            filtered = filtered && (StringUtils.isBlank(optParams.get().getAnagPagatore()) || flussoExport.getCodESoggPagAnagraficaPagatore().toLowerCase().contains(optParams.get().getAnagPagatore().toLowerCase()));
            //cf versante
            filtered = filtered && (StringUtils.isBlank(optParams.get().getCodFiscaleVersante()) || flussoExport.getCodESoggVersIdUnivVersCodiceIdUnivoco().toLowerCase().contains(optParams.get().getCodFiscaleVersante().toLowerCase()));
            //anagrafica versante
            filtered = filtered && (StringUtils.isBlank(optParams.get().getAnagVersante()) || flussoExport.getCodESoggVersAnagraficaVersante().toLowerCase().contains(optParams.get().getAnagVersante().toLowerCase()));
            //attestante
            filtered = filtered && (StringUtils.isBlank(optParams.get().getAttestante()) || flussoExport.getDeEIstitAttDenominazioneAttestante().toLowerCase().contains(optParams.get().getAttestante().toLowerCase()));
            //tipo dovuto
            filtered = filtered && (StringUtils.isBlank(optParams.get().getTipoDovuto()) || flussoExport.getDeTipoDovuto().toLowerCase().contains(optParams.get().getTipoDovuto().toLowerCase()));
            //data da
            filtered = filtered && (optParams.get().getDateEsitoFrom() == null || (optParams.get().getDateEsitoFrom().isBefore(flussoExport.getDtEDatiPagDatiSingPagDataEsitoSingoloPagamento()) || optParams.get().getDateEsitoFrom().isEqual(flussoExport.getDtEDatiPagDatiSingPagDataEsitoSingoloPagamento())));
            //data a
            filtered = filtered && (optParams.get().getDateEsitoTo() == null || (optParams.get().getDateEsitoTo().isAfter(flussoExport.getDtEDatiPagDatiSingPagDataEsitoSingoloPagamento()) || optParams.get().getDateEsitoFrom().isEqual(flussoExport.getDtEDatiPagDatiSingPagDataEsitoSingoloPagamento())));
          }
          return filtered;
        })
        .filter(flussoExportService.filterByDeTipoDovutoOperatore(deTipoDovutoList))
        .sorted(Comparator
            .comparing(FlussoRicevutaTo::getCodRpSilinviarpIdUnivocoVersamento, Comparator.reverseOrder())
            .thenComparing(FlussoRicevutaTo::getIndiceDatiSingoloPagamento)
        )
        .collect(toList());

    return rendicontazioneService.mapToPayload(rendicontazioneList.get(0))
        .toBuilder()
        .details(details)
        .build();
  }

  private List<OperatoreEnteTipoDovuto> getOperatoreEnteTipoDovutos(UserWithAdditionalInfo user, Long mygovEnteId) {
    Ente ente = Optional.of(enteService.getEnteById(mygovEnteId)).orElseThrow(NotFoundException::new);
    return Optional
        .of(operatoreEnteTipoDovutoService.getByCodIpaCodTipoCodFed(ente.getCodIpaEnte(), null, user.getUsername()))
        .orElseThrow(() -> new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY)));
  }
}
