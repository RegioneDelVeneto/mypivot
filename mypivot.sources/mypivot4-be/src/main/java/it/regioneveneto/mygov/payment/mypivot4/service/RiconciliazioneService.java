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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import it.regioneveneto.mygov.payment.mypay4.exception.BadRequestException;
import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import it.regioneveneto.mygov.payment.mypay4.exception.NotFoundException;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypay4.util.MaxResultsHelper;
import it.regioneveneto.mygov.payment.mypay4.util.Possibly;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.dao.FlussoRendicontazioneDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.RiconciliazioneDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.ClassificazioneTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.RiconciliazioneSearchTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.RiconciliazioneTo;
import it.regioneveneto.mygov.payment.mypivot4.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class RiconciliazioneService {

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
  private final List<ClassificazioneTo> classificazioni;

  @Autowired
  EnteService enteService;

  @Autowired
  EnteTipoDovutoService enteTipoDovutoService;

  @Autowired
  private MaxResultsHelper maxResultsHelper;

  @Autowired
  private RiconciliazioneDao riconciliazioneDao;

  @Autowired
  FlussoRendicontazioneDao flussoRendicontazioneDao;

  @Autowired
  SegnalazioneService segnalazioneService;


  @Autowired
  public RiconciliazioneService(MessageSource messageSource) {
    List<String> allVersions = Arrays.stream(Constants.VERSIONE_TRACCIATO_EXPORT.values())
      .map(Constants.VERSIONE_TRACCIATO_EXPORT::getValue)
      .sorted(Comparator.reverseOrder())
      .collect(Collectors.toUnmodifiableList());
    List<ClassificazioneTo> classific = new ArrayList<>();
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_IUD_RT_IUF_TES, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.RICONCILIAZIONE.getValue(), true, true,
      ImmutableSet.of("deTipoDovuto", "iuv", "iud", "iur", "importo", "dataEsito", "idRendicontazione", "importoTesoreria"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_RT_IUF_TES, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.RICONCILIAZIONE.getValue(), null, true,
      ImmutableSet.of("deTipoDovuto", "iuv", "iud", "iur", "importo", "dataEsito", "idRendicontazione", "importoTesoreria"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_RT_TES, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.RICONCILIAZIONE.getValue(), null, true,
      ImmutableSet.of("deTipoDovuto", "iuv", "iud", "iur", "importo", "dataEsito", "importoTesoreria"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_RT_IUF, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.RICONCILIAZIONE.getValue(), null, null,
      ImmutableSet.of("deTipoDovuto", "iuv", "iud", "iur", "importo", "dataEsito", "idRendicontazione", "importoTotale"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_IUD_RT_IUF, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.RICONCILIAZIONE.getValue(), true, null,
      ImmutableSet.of("deTipoDovuto", "iuv", "iud", "iur", "importo", "dataEsito", "idRendicontazione", "importoTotale"),
      allVersions));

    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_IUD_NO_RT, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.ANOMALIE.getValue(), true, null,
      ImmutableSet.of("deTipoDovuto", "iud", "datiSpecificiRiscossione", "pagatoreCodFisc", "dataUltimoAgg", "importo", "dataEsecuzione"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_RT_NO_IUD, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.ANOMALIE.getValue(), true, null,
      ImmutableSet.of("deTipoDovuto", "iud", "iuv", "importo", "dataEsito", "idRendicontazione", "importoTotale", "importoTesoreria", "dataUltimoAgg"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_RT_NO_IUF, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.ANOMALIE.getValue(), null, null,
      ImmutableSet.of("deTipoDovuto", "iud", "iuv", "iur", "pagatoreCodFisc", "dataUltimoAgg", "importo", "dataEsito"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_IUV_NO_RT, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.ANOMALIE.getValue(), null, true,
      ImmutableSet.of("idRendicontazione", "dataFlusso", "idRegolamento", "dataRegolamento", "dataUltimoAgg", "importoTotale"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_IUF_NO_TES, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.ANOMALIE.getValue(), null, true,
      ImmutableSet.of("idRendicontazione", "dataFlusso", "idRegolamento", "dataRegolamento", "dataUltimoAgg", "importoTotale"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_IUF_TES_DIV_IMP, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.ANOMALIE.getValue(), null, true,
      ImmutableSet.of("dataUltimoAgg", "idRendicontazione", "dataFlusso", "importoTotale", "conto", "dataValuta", "dataContabile", "ordinante", "importoTesoreria"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_TES_NO_IUF_OR_IUV, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.ANOMALIE.getValue(), null, true,
      ImmutableSet.of("conto", "dataValuta", "dataContabile", "dataUltimoAgg", "importoTesoreria", "ordinante", "iufKey"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_TES_NO_MATCH, null, null, false,
      Constants.TIPO_VISUALIZZAZIONE.ANOMALIE.getValue(), null, true,
      ImmutableSet.of("conto", "dataValuta", "dataContabile", "dataUltimoAgg", "annoBolletta", "codiceBolletta", "importoTesoreria", "causaleRiversamento", "ordinante"),
      allVersions));
    classific.add(new ClassificazioneTo(
      Constants.COD_ERRORE_DOPPI, null, null, true,
      Constants.TIPO_VISUALIZZAZIONE.ANOMALIE.getValue(), null, null,
      ImmutableSet.of("deTipoDovuto", "iuv", "importo", "dataEsito", "attestanteAnagrafica", "pagatoreCodFisc", "idRendicontazione", "dataFlusso", "importoTotale"),
      ImmutableList.of(Constants.VERSIONE_TRACCIATO_EXPORT.VERSIONE_1_4.getValue(), Constants.VERSIONE_TRACCIATO_EXPORT.VERSIONE_1_0.getValue())));

    this.classificazioni = classific.stream()
      .peek(x -> {
        x.setLabel(messageSource.getMessage("mypivot.classificazioni." + x.getKey(), null, Locale.ITALY));
        x.setInfoText(messageSource.getMessage("mypivot.classificazioni." + x.getKey() + ".info", null, Locale.ITALY));
      })
      .collect(Collectors.toUnmodifiableList());

  }

  public List<ClassificazioneTo> getClassificazioniEnte(Ente ente, Constants.TIPO_VISUALIZZAZIONE viewType) {
    return this.classificazioni.stream()
      .filter(x -> (viewType == null || viewType.getValue().equals(x.getType())) &&
        (x.getFlgPagati() == null || x.getFlgPagati() == ente.isFlgPagati()) &&
        (x.getFlgTesoreria() == null || x.getFlgTesoreria() == ente.isFlgTesoreria()))
      .collect(Collectors.toList());
  }

  public ClassificazioneTo getClassificazioneByKey(String key) {
    return this.classificazioni.stream()
      .filter(x -> x.getKey().equals(key))
      .findFirst()
      .orElseThrow(NotFoundException::new);
  }

  public List<RiconciliazioneTo> search(UserWithAdditionalInfo user, Ente ente, ClassificazioneTo searchType, RiconciliazioneSearchTo searchParams) {
    RiconciliazioneSearch riconciliazioneSearch = searchParams.toEntity(user.getUsername(), ente.getCodIpaEnte(), searchType.getKey());
    Function<Integer, List<RiconciliazioneTo>> searchFun;
    Supplier<Integer> searchCountFun;

    if (searchType.getKey().equals(Constants.COD_ERRORE_DOPPI)) {
      //special flow for query pagamenti doppi
      searchFun = (maxResults) -> flussoRendicontazioneDao.searchPagamentiDoppi(riconciliazioneSearch, maxResults)
        .stream().map(this::mapToDto).collect(Collectors.toList());
      searchCountFun = () -> flussoRendicontazioneDao.searchPagamentiDoppiCount(riconciliazioneSearch);
    } else if (
      searchType.getKey().equals(Constants.COD_ERRORE_IUD_RT_IUF_TES) ||
        searchType.getKey().equals(Constants.COD_ERRORE_RT_IUF_TES) ||
        searchType.getKey().equals(Constants.COD_ERRORE_RT_IUF) ||
        searchType.getKey().equals(Constants.COD_ERRORE_RT_NO_IUF) ||
        searchType.getKey().equals(Constants.COD_ERRORE_RT_NO_IUD) ||
        searchType.getKey().equals(Constants.COD_ERRORE_IUD_NO_RT) ||
        searchType.getKey().equals(Constants.COD_ERRORE_IUD_RT_IUF) ||
        searchType.getKey().equals(Constants.COD_ERRORE_RT_TES)) {
      searchFun = (maxResults) -> riconciliazioneDao.search(riconciliazioneSearch, maxResults)
        .stream().map(this::mapToDto).collect(Collectors.toList());
      searchCountFun = () -> riconciliazioneDao.searchCount(riconciliazioneSearch);
    } else if (
      searchType.getKey().equals(Constants.COD_ERRORE_IUF_NO_TES) ||
        searchType.getKey().equals(Constants.COD_ERRORE_IUV_NO_RT)) {
      searchFun = (maxResults) -> riconciliazioneDao.searchRendicontazioneSubset(riconciliazioneSearch, maxResults)
        .stream().map(this::mapToDto).collect(Collectors.toList());
      searchCountFun = () -> riconciliazioneDao.searchRendicontazioneSubsetCount(riconciliazioneSearch);
    } else if (
      searchType.getKey().equals(Constants.COD_ERRORE_TES_NO_IUF_OR_IUV)) {
      searchFun = (maxResults) -> riconciliazioneDao.searchTesoreriaSubset(riconciliazioneSearch, maxResults)
        .stream().map(this::mapToDto).collect(Collectors.toList());
      searchCountFun = () -> riconciliazioneDao.searchTesoreriaSubsetCount(riconciliazioneSearch);
    } else if (
      searchType.getKey().equals(Constants.COD_ERRORE_TES_NO_MATCH)) {
      searchFun = (maxResults) -> riconciliazioneDao.searchTesoreriaSubset(riconciliazioneSearch, maxResults)
        .stream().map(this::mapToDto).collect(Collectors.toList());
      searchCountFun = () -> riconciliazioneDao.searchTesoreriaNoMatchSubsetCount(riconciliazioneSearch);
    } else if (
      searchType.getKey().equals(Constants.COD_ERRORE_IUF_TES_DIV_IMP)) {
      searchFun = (maxResults) -> riconciliazioneDao.searchRendicontazioneTesoreriaSubset(riconciliazioneSearch, maxResults)
        .stream().map(this::mapToDto).collect(Collectors.toList());
      searchCountFun = () -> riconciliazioneDao.searchRendicontazioneTesoreriaSubsetCount(riconciliazioneSearch);
    } else
      throw new NotFoundException("unknown search type: " + searchType.getKey());

    return maxResultsHelper.manageMaxResults(searchFun, searchCountFun);
  }

  public RiconciliazioneTo getDetail(UserWithAdditionalInfo user, Ente ente, ClassificazioneTo searchType, String iuf, String iudOrIndex, String iuv) {
    Optional<RiconciliazioneTo> detailOpt;

    if (searchType.getKey().equals(Constants.COD_ERRORE_DOPPI)) {
      //special flow for query pagamenti doppi
      detailOpt = flussoRendicontazioneDao.getByPrimaryKey(ente.getCodIpaEnte(), iuf, iudOrIndex, iuv)
        .map(this::mapToDetailDto);
    } else if (
      searchType.getKey().equals(Constants.COD_ERRORE_IUD_RT_IUF_TES) ||
        searchType.getKey().equals(Constants.COD_ERRORE_RT_IUF_TES) ||
        searchType.getKey().equals(Constants.COD_ERRORE_RT_IUF) ||
        searchType.getKey().equals(Constants.COD_ERRORE_RT_NO_IUF) ||
        searchType.getKey().equals(Constants.COD_ERRORE_RT_NO_IUD) ||
        searchType.getKey().equals(Constants.COD_ERRORE_IUD_NO_RT) ||
        searchType.getKey().equals(Constants.COD_ERRORE_IUD_RT_IUF) ||
        searchType.getKey().equals(Constants.COD_ERRORE_RT_TES)) {
      try {
        detailOpt = riconciliazioneDao.getDetail(ente.getCodIpaEnte(), searchType.getKey(), iuf, false, iudOrIndex, false, iuv, false, false)
          .map(this::mapToDetailDto);
      } catch (IllegalArgumentException iae) {
        //duplicate rows
        throw new MyPayException("dati non validi e/o duplicati", iae);
      }
    } else if (
      searchType.getKey().equals(Constants.COD_ERRORE_IUF_TES_DIV_IMP)) {
      detailOpt = riconciliazioneDao.getDetail(ente.getCodIpaEnte(), searchType.getKey(), iuf, false, iudOrIndex, true, iuv, true, true)
        .map(this::mapToDetailDto);
    } else if (
      searchType.getKey().equals(Constants.COD_ERRORE_TES_NO_IUF_OR_IUV)) {
      detailOpt = riconciliazioneDao.getDetail(ente.getCodIpaEnte(), searchType.getKey(), iuf, false, iudOrIndex, false, iuv, false, true)
        .map(this::mapToDetailDto);
    } else if (
      searchType.getKey().equals(Constants.COD_ERRORE_IUF_NO_TES) ||
        searchType.getKey().equals(Constants.COD_ERRORE_IUV_NO_RT)) {
      detailOpt = riconciliazioneDao.getRendicontazioneSubsetDetail(ente.getCodIpaEnte(), searchType.getKey(), iuf)
        .map(this::mapToDetailDto);
    } else
      throw new NotFoundException("unknown search type: " + searchType.getKey());

    RiconciliazioneTo detail = detailOpt
      .map(dto -> {
        dto.setClassificazioneLabel(searchType.getLabel());
        return dto;
      })
      .orElseThrow(NotFoundException::new);

    //retrieve segnalazione attiva
    segnalazioneService.getSegnalazioneAttivaByElement(Segnalazione.builder()
        .mygovEnteId(ente)
        .classificazioneCompletezza(searchType.getKey())
        .codIuf(iuf)
        .codIud(iudOrIndex)
        .codIuv(iuv)
        .build())
      .ifPresent(segnalazione -> {
        detail.setHasSegnalazione(true);
        detail.setNotaSegnalazione(segnalazione.getDeNota());
        detail.setUtenteSegnalazione(StringUtils.joinWith(" ", segnalazione.getMygovUtenteId().getDeFirstname(), segnalazione.getMygovUtenteId().getDeLastname()));
        detail.setCfUtenteSegnalazione(segnalazione.getMygovUtenteId().getCodCodiceFiscaleUtente());
        detail.setDataInserimentoSegnalazione(Utilities.toLocalDateTime(segnalazione.getDtCreazione()));
      });

    return detail;
  }


  private <T> T setIf(Set<String> fieldMapper, String fieldId, Supplier<T> fun) {
    return fieldMapper == null || fieldMapper.contains(fieldId) ? fun.get() : null;
  }

  private RiconciliazioneTo mapToDto(ImportExportRendicontazioneTesoreria entity) {
    return this.mapToDtoImpl(entity, false);
  }

  private RiconciliazioneTo mapToDetailDto(ImportExportRendicontazioneTesoreria entity) {
    return this.mapToDtoImpl(entity, true);
  }

  private RiconciliazioneTo mapToDtoImpl(ImportExportRendicontazioneTesoreria entity, boolean detail) {
    Set<String> set = detail ? null : getClassificazioneByKey(entity.getClassificazioneCompletezza()).getFields();

    return RiconciliazioneTo.builder()
      //common
      .classificazione(entity.getClassificazioneCompletezza())
      .deTipoDovuto(setIf(set, "deTipoDovuto", () -> Optional.ofNullable(entity.getTipoDovuto())
        .flatMap(x -> enteTipoDovutoService.getByCodTipo(entity.getTipoDovuto(), entity.getCodiceIpaEnte()))
        .map(EnteTipoDovuto::getDeTipo)
        .orElse(null)))
      .iuvKey(entity.getCodIuvKey())
      .iudKey(entity.getCodIudKey())
      .iufKey(entity.getCodIufKey())
      .dataUltimoAgg(setIf(set, "dataUltimoAgg", () -> Utilities.toLocalDateTime(entity.getDtDataUltimoAggiornamento())))
      .hasSegnalazione(entity.getHasSegnalazione())
      //notifiche
      .iud(setIf(set, "iud", entity::getCodiceIud))
      .iuv(setIf(set, "iuv", entity::getCodiceIuv))
      .iur(setIf(set, "iur", entity::getIdentificativoUnivocoRiscossione))
      .importo(setIf(set, "importo", entity::getSingoloImportoPagato))
      .dataEsecuzione(setIf(set, "dataEsecuzione", () -> Utilities.toLocalDate(entity.getDtDataEsecuzionePagamento())))
      .pagatoreAnagrafica(setIf(set, "pagatoreAnagrafica", entity::getAnagraficaPagatore))
      .pagatoreCodFisc(setIf(set, "pagatoreCodFisc", entity::getCodiceIdentificativoUnivocoPagatore))
      .causale(setIf(set, "causale", entity::getCausaleVersamento))
      .datiSpecificiRiscossione(setIf(set, "datiSpecificiRiscossione", entity::getDatiSpecificiRiscossione))
      //rendicontazione
      .importoTotale(setIf(set, "importoTotale", entity::getImportoTotalePagamenti))
      .idRendicontazione(setIf(set, "idRendicontazione", entity::getIdentificativoFlussoRendicontazione))
      .dataFlusso(setIf(set, "dataFlusso", () -> Utilities.toOptional(entity.getDataOraFlussoRendicontazione())
        .map(x -> LocalDateTime.parse(x, formatter)).orElse(null)))
      .idRegolamento(setIf(set, "idRegolamento", entity::getIdentificativoUnivocoRegolamento))
      .dataRegolamento(setIf(set, "dataRegolamento", () -> Utilities.toLocalDate(entity.getDtDataRegolamento())))
      //ricevuta telematica
      .dataEsito(setIf(set, "dataEsito", () -> Utilities.toLocalDate(entity.getDtDataEsitoSingoloPagamento())))
      .attestanteAnagrafica(setIf(set, "attestanteAnagrafica", entity::getDenominazioneAttestante))
      .attestanteCodFisc(setIf(set, "attestanteCodFisc", entity::getCodiceIdentificativoUnivocoAttestante))
      .versanteAnagrafica(setIf(set, "versanteAnagrafica", entity::getAnagraficaVersante))
      .versanteCodFisc(setIf(set, "versanteCodFisc", entity::getCodiceIdentificativoUnivocoVersante))
      //sospeso
      .importoTesoreria(setIf(set, "importoTesoreria", entity::getDeImporto))
      .conto(setIf(set, "conto", entity::getCodConto))
      .dataValuta(setIf(set, "dataValuta", () -> Utilities.toLocalDate(entity.getDtDataValuta())))
      .dataContabile(setIf(set, "dataContabile", () -> Utilities.toLocalDate(entity.getDtDataContabile())))
      .ordinante(setIf(set, "ordinante", entity::getCodOr1))
      .annoBolletta(setIf(set, "annoBolletta", () -> Possibly.of(() -> Utilities.toInteger(entity.getDeAnnoBolletta())).orIfException(null)))
      .codiceBolletta(setIf(set, "codiceBolletta", entity::getCodBolletta))
      .annoDocumento(setIf(set, "annoDocumento", () -> Possibly.of(() -> Utilities.toInteger(entity.getDeAnnoDocumento())).orIfException(null)))
      .codDocumento(setIf(set, "codDocumento", entity::getCodDocumento))
      .annoProvvisorio(setIf(set, "annoProvvisorio", () -> Possibly.of(() -> Utilities.toInteger(entity.getDeAnnoProvvisorio())).orIfException(null)))
      .codProvvisorio(setIf(set, "codProvvisorio", entity::getCodProvvisorio))
      .causaleRiversamento(setIf(set, "causaleRiversamento", entity::getDeCausaleT))
      .build();
  }

  private RiconciliazioneTo mapToDto(FlussoRendicontazione entity) {
    return this.mapToDtoImpl(entity, false);
  }

  private RiconciliazioneTo mapToDetailDto(FlussoRendicontazione entity) {
    return this.mapToDtoImpl(entity, true);
  }

  private RiconciliazioneTo mapToDtoImpl(FlussoRendicontazione entity, boolean detail) {
    Set<String> set = detail ? null : getClassificazioneByKey(Constants.COD_ERRORE_DOPPI).getFields();

    return RiconciliazioneTo.builder()
      //common
      .classificazione(Constants.COD_ERRORE_DOPPI)
      .deTipoDovuto(setIf(set, "deTipoDovuto", () -> enteTipoDovutoService.getByCodTipo(entity.getNestedFlussoExport().getCodTipoDovuto(), entity.getMygovEnteId().getCodIpaEnte())
        .orElseThrow(() -> new BadRequestException("Invalid tipo dovuto: " + entity.getNestedFlussoExport().getCodTipoDovuto()))
        .getDeTipo()))
      .iuvKey(entity.getCodDatiSingPagamIdentificativoUnivocoVersamento())
      .iudKey(Integer.toString(entity.getIndiceDatiSingoloPagamento())) //index
      .iufKey(entity.getCodDatiSingPagamIdentificativoUnivocoRiscossione()) //iur
      .hasSegnalazione(entity.getHasSegnalazione())
      //notifiche
      .iuv(setIf(set, "iuv", entity.getNestedFlussoExport()::getCodEDatiPagIdUnivocoVersamento))
      .importo(setIf(set, "importo", () -> Utilities.parseImportoString(entity.getNumDatiSingPagamSingoloImportoPagato())))
      .iud(setIf(set, "iud", entity.getNestedFlussoExport()::getCodIud))
      .dataEsecuzione(null)
      .pagatoreAnagrafica(setIf(set, "pagatoreAnagrafica", entity.getNestedFlussoExport()::getCodESoggPagAnagraficaPagatore))
      .pagatoreCodFisc(setIf(set, "pagatoreCodFisc", entity.getNestedFlussoExport()::getCodESoggPagIdUnivPagCodiceIdUnivoco))
      .causale(setIf(set, "causale", entity.getNestedFlussoExport()::getDeEDatiPagDatiSingPagCausaleVersamento))
      .datiSpecificiRiscossione(setIf(set, "datiSpecificiRiscossione", entity.getNestedFlussoExport()::getDeEDatiPagDatiSingPagDatiSpecificiRiscossione))
      //ricevuta telematica
      .dataEsito(setIf(set, "dataEsito", () -> Utilities.toLocalDate(entity.getDtDatiSingPagamDataEsitoSingoloPagamento())))
      .iur(setIf(set, "iur", entity.getNestedFlussoExport()::getCodEDatiPagDatiSingPagIdUnivocoRiscoss))
      .attestanteAnagrafica(setIf(set, "attestanteAnagrafica", entity.getNestedFlussoExport()::getDeEIstitAttDenominazioneAttestante))
      .attestanteCodFisc(setIf(set, "attestanteCodFisc", entity.getNestedFlussoExport()::getCodEIstitAttIdUnivAttCodiceIdUnivoco))
      .versanteAnagrafica(setIf(set, "versanteAnagrafica", entity.getNestedFlussoExport()::getCodESoggVersAnagraficaVersante))
      .versanteCodFisc(setIf(set, "versanteCodFisc", entity.getNestedFlussoExport()::getCodESoggVersIdUnivVersCodiceIdUnivoco))
      //rendicontazione
      .idRendicontazione(setIf(set, "idRendicontazione", entity::getCodIdentificativoFlusso))
      .idRegolamento(setIf(set, "idRegolamento", entity::getCodIdentificativoUnivocoRegolamento))
      .dataRegolamento(setIf(set, "dataRegolamento", () -> Utilities.toLocalDate(entity.getDtDataRegolamento())))
      .dataFlusso(setIf(set, "dataFlusso", () -> Utilities.toLocalDateTime(entity.getDtDataOraFlusso())))
      .importoTotale(setIf(set, "importoTotale", () -> Utilities.parseImportoString(entity.getNumImportoTotalePagamenti())))
      //sospeso
      .conto(null)
      .dataValuta(null)
      .dataContabile(null)
      .ordinante(null)
      .annoBolletta(null)
      .codiceBolletta(null)
      .annoDocumento(null)
      .codDocumento(null)
      .annoProvvisorio(null)
      .codProvvisorio(null)
      .build();
  }
}
