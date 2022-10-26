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

import it.regioneveneto.mygov.payment.mypay4.exception.ValidatorException;
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypivot4.dao.*;
import it.regioneveneto.mygov.payment.mypivot4.dto.AccertamentoFlussoExportTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.BilancioTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoRicevutaTo;
import it.regioneveneto.mygov.payment.mypivot4.mapper.BilancioToCtBilancioMapper;
import it.regioneveneto.mygov.payment.mypivot4.model.*;
import it.veneto.regione.pagamenti.pivot.ente.CtBilancio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@Transactional
public class AccertamentoDettaglioService {

  @Autowired
  private EnteService enteService;

  @Autowired
  private OperatoreEnteTipoDovutoService operatoreEnteTipoDovutoService;

  @Autowired
  private UtenteService utenteService;

  @Autowired
  private AccertamentoDettaglioDao accertamentoDettaglioDao;

  @Autowired
  private AccertamentoService accertamentoService;

  @Autowired
  private AnagraficaUffCapAccDao anagraficaUffCapAccDao;

  @Autowired
  private FlussoExportDao flussoExportDao;

  @Autowired
  private FlussoRendicontazioneDao flussoRendicontazioneDao;

  @Autowired
  private FlussoTesoreriaDao flussoTesoreriaDao;

  @Autowired
  private StatisticaService statisticaService;

  @Autowired
  private MessageSource messageSource;

  public List<AccertamentoDettaglio> getByAccertamentoId(Long accertamentoId) {
    return accertamentoDettaglioDao.getByAccertamentoId(accertamentoId);
  }

  public List<FlussoRendicontazione> getFlussoRendicontazioneByCodIpaIUF(String codIpaEnte, String iuf) {
    return flussoRendicontazioneDao.getByCodIpaIUF(codIpaEnte, iuf);
  }

  public List<FlussoExport> getFlussiExportByCodIpaIUV(String codIpaEnte, String iuv) {
    Assert.hasText(codIpaEnte, "Parametro [ codIpaEnte ] vuoto");
    Assert.hasText(iuv, "Parametro [ iuv ] vuoto");
    return flussoExportDao.getByCodIpaIUV(codIpaEnte, iuv);
  }

  public List<FlussoExport> getFlussiExportByCodIpaIUF(String codIpaEnte, String iuf) {
    Assert.hasText(codIpaEnte, "Parametro [ codIpaEnte ] vuoto");
    Assert.hasText(iuf, "Parametro [ iuf ] vuoto");

    List<FlussoRendicontazione> flussiRendicontazione = flussoRendicontazioneDao.getByCodIpaIUF(codIpaEnte, iuf);
    if (!CollectionUtils.isEmpty(flussiRendicontazione)) {
      return flussiRendicontazione.stream().flatMap(rend -> {
        String iuv = rend.getCodDatiSingPagamIdentificativoUnivocoVersamento();
        int indiceDatiSingoloPagamento = rend.getIndiceDatiSingoloPagamento();
        FlussoExport flusso = getFlussiExportByCodIpaIUVIdDtSinPag(codIpaEnte, iuv, indiceDatiSingoloPagamento);
        return flusso != null ? Stream.of(flusso) : Stream.empty();
      }).collect(Collectors.toList());
    }
    return null;
  }

  public FlussoTesoreria getByCodIpaDeAnnoBollettaCodBolletta(String codIpaEnte, String deAnnoBolletta, String codBolletta) {
    return flussoTesoreriaDao.getByCodIpaDeAnnoBollettaCodBolletta(codIpaEnte, deAnnoBolletta, codBolletta);
  }

  private FlussoExport getFlussiExportByCodIpaIUVIdDtSinPag(String codIpaEnte, String iuv, int indiceDatiSingoloPagamento) {
    List<FlussoExport> flussi = flussoExportDao.getByCodIpaIUVIdDtSinPag(codIpaEnte, iuv, indiceDatiSingoloPagamento);
    if (!CollectionUtils.isEmpty(flussi)) {
      if (flussi.size() > 1) {
        flussi = flussi.stream().filter(f -> !"9".equals(f.getCodEDatiPagCodiceEsitoPagamento().toString())).collect(Collectors.toList());
      }
      return flussi.get(0);
    }
    return null;
  }

  public List<BilancioTo> getBilancios(String codIpaEnte, List<FlussoExport> flussi) {
    String codStato = Constants.COD_TIPO_STATO_ACCERTAMENTO_CHIUSO;
    String deStato = Constants.DE_TIPO_STATO_ACCERTAMENTO;
    List<String> codIudList = flussi.stream().map(FlussoExport::getCodIud).collect(Collectors.toList());
    return accertamentoDettaglioDao.getBilanciosByCodIpaTipoStatoCodIud(codIpaEnte, codStato, deStato, codIudList);
  }

  public List<CtBilancio> getCtBilancios(String codIpaEnte, List<FlussoExport> flussi) {
    String codStato = Constants.COD_TIPO_STATO_ACCERTAMENTO_CHIUSO;
    String deStato = Constants.DE_TIPO_STATO_ACCERTAMENTO;
    List<String> codIudList = flussi.stream().map(FlussoExport::getCodIud).collect(Collectors.toList());
    List<BilancioTo> bilancios = accertamentoDettaglioDao.getBilanciosByCodIpaTipoStatoCodIud(codIpaEnte, codStato, deStato, codIudList);
    return BilancioToCtBilancioMapper.map(bilancios);
  }

  public List<AnagraficaUffCapAcc> getAnagraficaByEnteTipo(Long enteId, String codFedUserId, String codTipo) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("RICERCA :: STATISTICA :: RIPARTITI per UFFICI :: Utente[codFedUserId: " + codFedUserId + "] :: NON risulta OPERATORE per nessun tipo dovuto.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    return anagraficaUffCapAccDao.findDistinctUfficiByFilter(enteId, true, List.of(codTipo));
  }

  public List<AnagraficaUffCapAcc> getAnagraficaByEnteTipoUfficio(Long enteId, String codFedUserId, String codTipo, String codUfficio, String annoEsercizio) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("RICERCA :: STATISTICA :: RIPARTITI per UFFICI :: Utente[codFedUserId: " + codFedUserId + "] :: NON risulta OPERATORE per nessun tipo dovuto.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    return anagraficaUffCapAccDao.findDistinctCapitoliByEnteDovutoUfficio(enteId, codTipo, codUfficio, annoEsercizio, true);
  }

  public List<AnagraficaUffCapAcc> getAnagraficaByEnteTipoUfficioCapitolo(Long enteId, String codFedUserId, String codTipo, String codUfficio, String annoEsercizio, String capitolo) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("RICERCA :: STATISTICA :: RIPARTITI per UFFICI :: Utente[codFedUserId: " + codFedUserId + "] :: NON risulta OPERATORE per nessun tipo dovuto.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    return anagraficaUffCapAccDao.findDistinctAccertamentiByEnteDovutoUfficioCapitolo(enteId, codTipo, codUfficio, annoEsercizio, capitolo, true);
  }

  public List<AccertamentoFlussoExportTo> getAccertamentiPagamentiInseriti(String codFedUserId, Long enteId, Long accertamentoId, String codIud, String codIuv, String cfPagatore,
                                                                           LocalDate dtEsitoFrom, LocalDate dtEsitoTo, LocalDate dtUltimoAggFrom, LocalDate dtUltimoAggTo) {
    Ente ente = enteService.getEnteById(enteId);
    Accertamento accertamento = accertamentoService.getById(accertamentoId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    String codTipo = accertamento.getMygovEnteTipoDovutoId().getCodTipo();
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("Utente[codFedUserId: " + codFedUserId + "] :: NON è autorizzato per TIPO DOVUTO["+ codTipo +"].");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    if (dtEsitoTo != null)
      dtEsitoTo = dtEsitoTo.plusDays(1L);
    if (dtUltimoAggTo != null)
      dtUltimoAggTo = dtUltimoAggTo.plusDays(1L);
    List<AccertamentoFlussoExport> accertamenti = accertamentoDettaglioDao.get_pagamenti_inseriti_in_accertamento(accertamentoId, enteId, codTipo, codIud, codIuv, cfPagatore, dtEsitoFrom, dtEsitoTo, dtUltimoAggFrom, dtUltimoAggTo);
    return accertamenti.stream().map(this::mapToDto).collect(Collectors.toList());
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public int deleteAccertamentiPagamentiInseriti(String codFedUserId, Long enteId, Long accertamentoId, List<AccertamentoFlussoExportTo> accertamenti) {
    Ente ente = enteService.getEnteById(enteId);
    Accertamento accertamento = accertamentoService.getById(accertamentoId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    String codTipo = accertamento.getMygovEnteTipoDovutoId().getCodTipo();
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("Utente[codFedUserId: " + codFedUserId + "] :: NON è autorizzato per TIPO DOVUTO["+ codTipo +"].");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    for (AccertamentoFlussoExportTo dettaglio: accertamenti) {
      accertamentoDettaglioDao.deleteByAccertamentoIdCodIpaTipoIudIuv(accertamentoId, ente.getCodIpaEnte(), dettaglio.getCodTipoDovuto(), dettaglio.getCodiceIud(), dettaglio.getCodiceIuv());
    }
    return accertamenti.size();
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public int insertAccertamentiPagamenti(String codFedUserId, Long enteId, Long accertamentoId, List<AccertamentoFlussoExportTo> accertamenti, String codUfficio, String annoEsercizio, String codCapitolo, String codAccertamento) {
    Ente ente = enteService.getEnteById(enteId);
    Accertamento accertamento = accertamentoService.getById(accertamentoId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    String codTipo = accertamento.getMygovEnteTipoDovutoId().getCodTipo();
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("Utente[codFedUserId: " + codFedUserId + "] :: NON è autorizzato per TIPO DOVUTO["+ codTipo +"].");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    Utente utente = utenteService.getByCodFedUserId(codFedUserId).orElseThrow(() -> new ValidatorException("Nessun utente trovato"));
    for (AccertamentoFlussoExportTo dettaglio: accertamenti) {
      List<AccertamentoDettaglio> dettagliAnnullati =
          accertamentoDettaglioDao.getByEnteIudIuvStato(ente.getCodIpaEnte(), dettaglio.getCodiceIud(), dettaglio.getCodiceIuv(), Constants.DE_TIPO_STATO_ACCERTAMENTO, Constants.COD_TIPO_STATO_ACCERTAMENTO_ANNULLATO);
      if (!CollectionUtils.isEmpty(dettagliAnnullati))
        throw new ValidatorException("Relation Fields[codIud:" + dettaglio.getCodiceIud() + ", codIuv:" + dettaglio.getCodiceIuv() + ", codIpaEnte:" + ente.getCodIpaEnte() + "] Esiste già.");
      accertamentoDettaglioDao.insert(
          AccertamentoDettaglio.builder()
              .mygovAccertamentoId(accertamento)
              .mygovUtenteId(utente)
              .codIud(dettaglio.getCodiceIud())
              .codIuv(dettaglio.getCodiceIuv())
              .codIpaEnte(ente.getCodIpaEnte())
              .codTipoDovuto(dettaglio.getCodTipoDovuto())
              .codUfficio(codUfficio)
              .codCapitolo(codCapitolo)
              .codAccertamento(codAccertamento)
              .numImporto(dettaglio.getSingoloImportoPagato())
              .flgImportoInserito(true).build()
      );
    }
    return accertamenti.size();
  }

  public List<AccertamentoFlussoExportTo> getAccertamentiPagamentiInseribili(String codFedUserId, Long enteId, Long accertamentoId, String codIud, String codIuv, String cfPagatore,
                                                                             LocalDate dtEsitoFrom, LocalDate dtEsitoTo, LocalDate dtUltimoAggFrom, LocalDate dtUltimoAggTo) {
    Ente ente = enteService.getEnteById(enteId);
    Accertamento accertamento = accertamentoService.getById(accertamentoId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    String codTipo = accertamento.getMygovEnteTipoDovutoId().getCodTipo();
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("Utente[codFedUserId: " + codFedUserId + "] :: NON è autorizzato per TIPO DOVUTO["+ codTipo +"].");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    if (dtEsitoTo != null)
      dtEsitoTo = dtEsitoTo.plusDays(1L);
    if (dtUltimoAggTo != null)
      dtUltimoAggTo = dtUltimoAggTo.plusDays(1L);
    List<AccertamentoPagamentoInseribile> accertamenti = accertamentoDettaglioDao.get_pagamenti_inseribili_in_accertamento(enteId, codTipo, codIud, codIuv, cfPagatore, dtEsitoFrom, dtEsitoTo, dtUltimoAggFrom, dtUltimoAggTo);
    return accertamenti.stream().map(this::mapToDto).collect(Collectors.toList());
  }

  public List<CapitoloRT> getCapitoliByRT(String codFedUserId, Long enteId, Long accertamentoId, String codTipo, String codIud, String codIuv) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("Utente[codFedUserId: " + codFedUserId + "] :: NON è autorizzato per TIPO DOVUTO["+ codTipo +"].");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    return accertamentoDettaglioDao.getCapitoliByRT(accertamentoId, ente.getCodIpaEnte(), codTipo, codIud, codIuv, null);
  }

  public List<FlussoRicevutaTo> getRicevuteTelematiche(Long enteId, String codFedUserId, String codTipo, String codIud, LocalDate from, LocalDate to,
                                                       String iuv, String iur, String attestante, String cfPagatore, String anagPagatore, String cfVersante, String anagVersante) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("Utente[codFedUserId: " + codFedUserId + "] :: NON è autorizzato per TIPO DOVUTO["+ codTipo +"].");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }

    to = to != null ? to.plusDays(1) : null;
    List<FlussoExport> flussiExport = flussoExportDao.getDettalioCruscotto(enteId, codTipo, List.of(codIud), from, to, iuv, iur, attestante, cfPagatore, anagPagatore, cfVersante, anagVersante);
    return flussiExport.stream().map(statisticaService::mapToDto).collect(Collectors.toList());
  }

  private AccertamentoFlussoExportTo mapToDto(AccertamentoFlussoExport accertamento) {
    return AccertamentoFlussoExportTo.builder()
      .codTipoDovuto(accertamento.getCodTipoDovuto())
      .deTipoDovuto(accertamento.getDeTipoDovuto())
      .codiceIud(accertamento.getCodIud())
      .codiceIuv(accertamento.getCodRpSilinviarpIdUnivocoVersamento())
      .identificativoUnivocoRiscossione(accertamento.getCodEDatiPagDatiSingPagIdUnivocoRiscoss())
      .denominazioneAttestante(accertamento.getDeEIstitAttDenominazioneAttestante())
      .codiceIdentificativoUnivocoAttestante(accertamento.getCodEIstitAttIdUnivAttCodiceIdUnivoco())
      .tipoIdentificativoUnivocoAttestante(accertamento.getCodEIstitAttIdUnivAttTipoIdUnivoco())
      .anagraficaVersante(accertamento.getCodESoggVersAnagraficaVersante())
      .codiceIdentificativoUnivocoVersante(accertamento.getCodESoggVersIdUnivVersCodiceIdUnivoco())
      .tipoIdentificativoUnivocoVersante(accertamento.getCodESoggVersIdUnivVersTipoIdUnivoco())
      .anagraficaPagatore(accertamento.getCodESoggPagAnagraficaPagatore())
      .codiceIdentificativoUnivocoPagatore(accertamento.getCodESoggPagIdUnivPagCodiceIdUnivoco())
      .tipoIdentificativoUnivocoPagatore(accertamento.getCodESoggPagIdUnivPagTipoIdUnivoco())
      .dtUltimoAggiornamento(accertamento.getDtUltimaModifica())
      .dtEsitoSingoloPagamento(accertamento.getDtEDatiPagDatiSingPagDataEsitoSingoloPagamento())
      .singoloImportoPagato(accertamento.getNumEDatiPagDatiSingPagSingoloImportoPagato())
      .causaleVersamento(accertamento.getDeEDatiPagDatiSingPagCausaleVersamento()).build();
  }

  private AccertamentoFlussoExportTo mapToDto(AccertamentoPagamentoInseribile accertamento) {
    return AccertamentoFlussoExportTo.builder()
        .codTipoDovuto(accertamento.getCodTipoDovuto())
        .deTipoDovuto(accertamento.getDeTipoDovuto())
        .codiceIud(accertamento.getCodIud())
        .codiceIuv(accertamento.getCodRpSilinviarpIdUnivocoVersamento())
        .identificativoUnivocoRiscossione(accertamento.getCodEDatiPagDatiSingPagIdUnivocoRiscoss())
        .denominazioneAttestante(accertamento.getDeEIstitAttDenominazioneAttestante())
        .codiceIdentificativoUnivocoAttestante(accertamento.getCodEIstitAttIdUnivAttCodiceIdUnivoco())
        .tipoIdentificativoUnivocoAttestante(accertamento.getCodEIstitAttIdUnivAttTipoIdUnivoco())
        .anagraficaVersante(accertamento.getCodESoggVersAnagraficaVersante())
        .codiceIdentificativoUnivocoVersante(accertamento.getCodESoggVersIdUnivVersCodiceIdUnivoco())
        .tipoIdentificativoUnivocoVersante(accertamento.getCodESoggVersIdUnivVersTipoIdUnivoco())
        .anagraficaPagatore(accertamento.getCodESoggPagAnagraficaPagatore())
        .codiceIdentificativoUnivocoPagatore(accertamento.getCodESoggPagIdUnivPagCodiceIdUnivoco())
        .tipoIdentificativoUnivocoPagatore(accertamento.getCodESoggPagIdUnivPagTipoIdUnivoco())
        .dtUltimoAggiornamento(accertamento.getDtUltimaModifica())
        .dtEsitoSingoloPagamento(accertamento.getDtEDatiPagDatiSingPagDataEsitoSingoloPagamento())
        .singoloImportoPagato(accertamento.getNumEDatiPagDatiSingPagSingoloImportoPagato())
        .causaleVersamento(accertamento.getDeEDatiPagDatiSingPagCausaleVersamento()).build();
  }
}
