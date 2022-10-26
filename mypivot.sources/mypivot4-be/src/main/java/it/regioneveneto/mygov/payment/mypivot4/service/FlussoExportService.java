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
import it.regioneveneto.mygov.payment.mypivot4.dao.FlussoExportDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoExportKeysTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoRicevutaTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.RicevutaSearchTo;
import it.regioneveneto.mygov.payment.mypivot4.model.EnteTipoDovuto;
import it.regioneveneto.mygov.payment.mypivot4.model.FlussoExport;
import it.regioneveneto.mygov.payment.mypivot4.model.FlussoRendicontazione;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class FlussoExportService {

  @Autowired
  MessageSource messageSource;
  @Autowired
  FlussoExportDao flussoExportDao;
  @Autowired
  MaxResultsHelper maxResultsHelper;
  @Autowired
  EnteService enteService;
  @Autowired
  EnteTipoDovutoService enteTipoDovutoService;
  @Autowired
  OperatoreEnteTipoDovutoService operatoreEnteTipoDovutoService;

  public List<FlussoRicevutaTo> searchRicevuteTelematiche(Long mygovEnteId, String username, RicevutaSearchTo searchTo) {
    return maxResultsHelper.manageMaxResults(
        maxResults -> flussoExportDao
            .searchRicevuteTelematiche(mygovEnteId, username, searchTo, maxResults)
            .stream()
            .map(this::mapToPayload)
            .collect(toList()),
        () -> flussoExportDao.searchCountRt(mygovEnteId, username, searchTo));
  }

  public List<FlussoExport> findPagatiByKeySet(Set<FlussoExportKeysTo> keySet, RicevutaSearchTo searchTo) {
    return flussoExportDao.findPagatiByKeySet(keySet, searchTo);
  }

  public FlussoExportKeysTo mapToKeySet(FlussoRendicontazione rendicontazione) {
    return Optional.ofNullable(rendicontazione)
        .map(r -> FlussoExportKeysTo.builder()
            .mygov_ente_id(r.getMygovEnteId().getMygovEnteId())
            .cod_rp_silinviarp_id_univoco_versamento(r.getCodDatiSingPagamIdentificativoUnivocoVersamento())
            .cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss(r.getCodDatiSingPagamIdentificativoUnivocoRiscossione())
            .indice_dati_singolo_pagamento(r.getIndiceDatiSingoloPagamento())
            .build()
        ).orElseGet(FlussoExportKeysTo::new);
  }

  public FlussoRicevutaTo mapToPayload(FlussoExport entity) {
    return Optional.ofNullable(entity)
        .map(f -> FlussoRicevutaTo.builder()
            .codiceIpaEnte(entity.getMygovEnteId().getCodIpaEnte())
            .codIud(entity.getCodIud())
            .codRpSilinviarpIdUnivocoVersamento(entity.getCodRpSilinviarpIdUnivocoVersamento())
            .codEDatiPagDatiSingPagIdUnivocoRiscoss(entity.getCodEDatiPagDatiSingPagIdUnivocoRiscoss())
            .numEDatiPagDatiSingPagSingoloImportoPagato(entity.getNumEDatiPagDatiSingPagSingoloImportoPagato())
            .dtEDatiPagDatiSingPagDataEsitoSingoloPagamento(Utilities.toLocalDate(entity.getDtEDatiPagDatiSingPagDataEsitoSingoloPagamento()))
            .deEIstitAttDenominazioneAttestante(entity.getDeEIstitAttDenominazioneAttestante())
            .codESoggPagAnagraficaPagatore(entity.getCodESoggPagAnagraficaPagatore())
            .codESoggPagIdUnivPagCodiceIdUnivoco(entity.getCodESoggPagIdUnivPagCodiceIdUnivoco())
            .codESoggPagIdUnivPagTipoIdUnivoco(Optional.ofNullable(entity.getCodESoggPagIdUnivPagTipoIdUnivoco())
                .map(Object::toString).orElse(null))
            .deEDatiPagDatiSingPagCausaleVersamento(entity.getDeEDatiPagDatiSingPagCausaleVersamento())
            .codESoggVersAnagraficaVersante(entity.getCodESoggVersAnagraficaVersante())
            .codESoggVersIdUnivVersCodiceIdUnivoco(entity.getCodESoggVersIdUnivVersCodiceIdUnivoco())
            .codESoggVersIdUnivVersTipoIdUnivoco(Optional.ofNullable(entity.getCodESoggVersIdUnivVersTipoIdUnivoco())
                .map(Object::toString).orElse(null))
            .deTipoDovuto(enteTipoDovutoService.getByCodTipo(entity.getCodTipoDovuto(), entity.getMygovEnteId().getCodIpaEnte())
                .map(EnteTipoDovuto::getDeTipo).orElse(null))
            .indiceDatiSingoloPagamento(entity.getIndiceDatiSingoloPagamento())
            .build()
        ).orElseGet(FlussoRicevutaTo::new);
  }

  public FlussoRicevutaTo mapToPayload(FlussoRendicontazione rend) {
    return Optional.ofNullable(rend).map(rendicontazione ->
      FlussoRicevutaTo.builder()
          .codiceIpaEnte(rendicontazione.getMygovEnteId().getCodIpaEnte())
          .codRpSilinviarpIdUnivocoVersamento(rendicontazione.getCodDatiSingPagamIdentificativoUnivocoVersamento())
          .codEDatiPagDatiSingPagIdUnivocoRiscoss(rendicontazione.getCodDatiSingPagamIdentificativoUnivocoRiscossione())
          .numEDatiPagDatiSingPagSingoloImportoPagato(rendicontazione.getNumDatiSingPagamSingoloImportoPagato())
          .dtEDatiPagDatiSingPagDataEsitoSingoloPagamento(Utilities.toLocalDate(rendicontazione.getDtDatiSingPagamDataEsitoSingoloPagamento()))
          .indiceDatiSingoloPagamento(rendicontazione.getIndiceDatiSingoloPagamento())
          .build()
    ).orElseGet(FlussoRicevutaTo::new);
  }

  public RicevutaSearchTo stripToNull(Optional<RicevutaSearchTo> optional) {
    return optional.map(obj -> RicevutaSearchTo.builder()
        .dateEsitoFrom(obj.getDateEsitoFrom())
        .dateEsitoTo(obj.getDateEsitoTo())
        .iud(StringUtils.stripToNull(obj.getIud()))
        .iuv(StringUtils.stripToNull(obj.getIuv()))
        .iur(StringUtils.stripToNull(obj.getIur()))
        .codFiscalePagatore(StringUtils.stripToNull(obj.getCodFiscalePagatore()))
        .anagPagatore(StringUtils.stripToNull(obj.getAnagPagatore()))
        .codFiscaleVersante(StringUtils.stripToNull(obj.getCodFiscaleVersante()))
        .anagVersante(StringUtils.stripToNull(obj.getAnagVersante()))
        .attestante(StringUtils.stripToNull(obj.getAttestante()))
        .tipoDovuto(StringUtils.stripToNull(obj.getTipoDovuto()))
        .build()
    ).orElseGet(RicevutaSearchTo::new);
  }

  public static Predicate<FlussoExport> filterByTipoDovutoOperatore(List<String> codTipoDovutoList) {
    return f -> Predicate.not(Objects::nonNull)
        .or(codTipoDovutoList::contains)
        .test(f.getCodTipoDovuto());
  }

  public static Predicate<FlussoRicevutaTo> filterByDeTipoDovutoOperatore(List<String> deTipoDovutoList) {
    return f -> Predicate.not(Objects::nonNull)
        .or(deTipoDovutoList::contains)
        .test(f.getDeTipoDovuto());
  }

  public Predicate<FlussoRicevutaTo> testEqualsKeys(FlussoRicevutaTo other) {
    return item -> item.getCodiceIpaEnte().equals(other.getCodiceIpaEnte()) &&
        item.getCodRpSilinviarpIdUnivocoVersamento().equals(other.getCodRpSilinviarpIdUnivocoVersamento()) &&
        item.getCodEDatiPagDatiSingPagIdUnivocoRiscoss().equals(other.getCodEDatiPagDatiSingPagIdUnivocoRiscoss()) &&
        item.getIndiceDatiSingoloPagamento() == other.getIndiceDatiSingoloPagamento();
  }
}
