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
import it.regioneveneto.mygov.payment.mypay4.util.MaxResultsHelper;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypay4.util.VerificationUtils;
import it.regioneveneto.mygov.payment.mypivot4.dao.PrenotazioneFlussoRiconciliazioneDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoExportTo;
import it.regioneveneto.mygov.payment.mypivot4.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class PrenotazioneFlussoRiconciliazioneService {

  @Autowired
  private AnagraficaStatoService anagraficaStatoService;

  @Autowired
  private OperatoreEnteTipoDovutoService operatoreEnteTipoDovutoService;

  @Autowired
  private PrenotazioneFlussoRiconciliazioneDao prenotazioneFlussoRiconciliazioneDao;

  @Autowired
  private MaxResultsHelper maxResultsHelper;

  @Autowired
  private MessageSource messageSource;

  @Transactional(propagation = Propagation.REQUIRED)
  public PrenotazioneFlussoRiconciliazione insert(Ente ente, Utente utente, String codTipoStato, String deTipoStato, String codCodiceClassificazione,
                                                  String deTipoDovuto, String codIdUnivocoVersamento, String codIdUnivocoRendicontazione,
                                                  Date dtDataUltimoAggiornamentoDa, Date dtDataUltimoAggiornamentoA, Date dtDataEsecuzioneDa,
                                                  Date dtDataEsecuzioneA, Date dtDataEsitoDa, Date dtDataEsitoA, Date dtDataRegolamentoDa,
                                                  Date dtDataRegolamentoA, Date dtDataContabileDa, Date dtDataContabileA, Date dtDataValutaDa,
                                                  Date dtDataValutaA, String codIdUnivocoDovuto, String codIdUnivocoRiscossione, String codIdUnivocoPagatore,
                                                  String deAnagraficaPagatore, String codIdUnivocoVersante, String deAnagraficaVersante,
                                                  String deDenominazioneAttestante, String deOrdinante, String codIdRegolamento, String codContoTesoreria,
                                                  String deImportoTesoreria, String deCausale, String versioneTracciato,
                                                  String codBolletta, String codDocumento, String codProvvisorio,
                                                  String deAnnoBolletta, String deAnnoDocumento, String deAnnoProvvisorio) {

    PrenotazioneFlussoRiconciliazione prenotazioneFlussoRiconciliazione = new PrenotazioneFlussoRiconciliazione();

    String codFedUserId = utente.getCodFedUserId();
    AnagraficaStato anagraficaStato = anagraficaStatoService.getByCodStatoAndTipoStato(codTipoStato, deTipoStato);

    prenotazioneFlussoRiconciliazione.setMygovEnteId(ente);
    prenotazioneFlussoRiconciliazione.setMygovUtenteId(utente);
    prenotazioneFlussoRiconciliazione.setMygovAnagraficaStatoId(anagraficaStato);
    prenotazioneFlussoRiconciliazione.setCodCodiceClassificazione(codCodiceClassificazione);

    String codRequestToken = UUID.randomUUID().toString();
    prenotazioneFlussoRiconciliazione.setCodRequestToken(codRequestToken);

    if (Utilities.isWSUSer(codFedUserId)) {
      if (StringUtils.isNotBlank(deTipoDovuto)) {
        prenotazioneFlussoRiconciliazione.setDeTipoDovuto(deTipoDovuto);
      }
    } else {
      if (VerificationUtils.isTipoDovutoAbilitatoPerClassificazione(codCodiceClassificazione)) {
        if (StringUtils.isNotBlank(deTipoDovuto)) {
          List<OperatoreEnteTipoDovuto> operatoreEnteTipoDovutos = operatoreEnteTipoDovutoService.getByCodIpaCodTipoCodFed(ente.getCodIpaEnte(), deTipoDovuto, codFedUserId);
          if (CollectionUtils.isNotEmpty(operatoreEnteTipoDovutos)) {
            prenotazioneFlussoRiconciliazione.setDeTipoDovuto(deTipoDovuto);
          } else {
            String message = "Tipo dovuto non valido per l'operatore [ " + utente.getCodFedUserId()
                + " ] per l'ente [ " + ente.getCodIpaEnte() + " ]";
            log.error(message);
            throw new ValidatorException(message);
          }
        } else {
          List<String> listaCodTipoDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
          if (CollectionUtils.isNotEmpty(listaCodTipoDovuto)) {
            String finalCodTd = "";
            for (String codTipo : listaCodTipoDovuto) {
              finalCodTd = finalCodTd + codTipo + "|";
            }
            if (finalCodTd.endsWith("|"))
              finalCodTd = finalCodTd.substring(0, finalCodTd.length() - 1);
            prenotazioneFlussoRiconciliazione.setDeTipoDovuto(finalCodTd);
          } else {
            String message = "Nessun tipo dovuto attivo associato all'operatore [ "
                + utente.getCodFedUserId() + " ] per l'ente [ " + ente.getCodIpaEnte() + " ]";
            log.error(message);
            throw new ValidatorException(message);
          }
        }
      }
    }

    prenotazioneFlussoRiconciliazione.setCodIdUnivocoVersamento(codIdUnivocoVersamento);
    prenotazioneFlussoRiconciliazione.setCodIdUnivocoRendicontazione(codIdUnivocoRendicontazione);
    prenotazioneFlussoRiconciliazione.setDtDataUltimoAggiornamentoDa(dtDataUltimoAggiornamentoDa);
    prenotazioneFlussoRiconciliazione.setDtDataUltimoAggiornamentoA(dtDataUltimoAggiornamentoA);
    prenotazioneFlussoRiconciliazione.setDtDataEsecuzioneDa(dtDataEsecuzioneDa);
    prenotazioneFlussoRiconciliazione.setDtDataEsecuzioneA(dtDataEsecuzioneA);
    prenotazioneFlussoRiconciliazione.setDtDataEsitoDa(dtDataEsitoDa);
    prenotazioneFlussoRiconciliazione.setDtDataEsitoA(dtDataEsitoA);
    prenotazioneFlussoRiconciliazione.setDtDataRegolamentoDa(dtDataRegolamentoDa);
    prenotazioneFlussoRiconciliazione.setDtDataRegolamentoA(dtDataRegolamentoA);
    prenotazioneFlussoRiconciliazione.setDtDataContabileDa(dtDataContabileDa);
    prenotazioneFlussoRiconciliazione.setDtDataContabileA(dtDataContabileA);
    prenotazioneFlussoRiconciliazione.setDtDataValutaDa(dtDataValutaDa);
    prenotazioneFlussoRiconciliazione.setDtDataValutaA(dtDataValutaA);
    prenotazioneFlussoRiconciliazione.setCodIdUnivocoDovuto(codIdUnivocoDovuto);
    prenotazioneFlussoRiconciliazione.setCodIdUnivocoRiscossione(codIdUnivocoRiscossione);
    prenotazioneFlussoRiconciliazione.setCodIdUnivocoPagatore(codIdUnivocoPagatore);
    prenotazioneFlussoRiconciliazione.setDeAnagraficaPagatore(deAnagraficaPagatore);
    prenotazioneFlussoRiconciliazione.setCodIdUnivocoVersante(codIdUnivocoVersante);
    prenotazioneFlussoRiconciliazione.setDeAnagraficaVersante(deAnagraficaVersante);
    prenotazioneFlussoRiconciliazione.setDeDenominazioneAttestante(deDenominazioneAttestante);
    prenotazioneFlussoRiconciliazione.setDeOrdinante(deOrdinante);
    prenotazioneFlussoRiconciliazione.setCodIdRegolamento(codIdRegolamento);
    prenotazioneFlussoRiconciliazione.setCodContoTesoreria(codContoTesoreria);
    prenotazioneFlussoRiconciliazione.setDeImportoTesoreria(deImportoTesoreria);
    prenotazioneFlussoRiconciliazione.setDeCausale(deCausale);
    prenotazioneFlussoRiconciliazione.setVersioneTracciato(versioneTracciato);
    prenotazioneFlussoRiconciliazione.setCodBolletta(codBolletta);
    prenotazioneFlussoRiconciliazione.setCodDocumento(codDocumento);
    prenotazioneFlussoRiconciliazione.setCodProvvisorio(codProvvisorio);
    prenotazioneFlussoRiconciliazione.setDeAnnoBolletta(deAnnoBolletta);
    prenotazioneFlussoRiconciliazione.setDeAnnoDocumento(deAnnoDocumento);
    prenotazioneFlussoRiconciliazione.setDeAnnoProvvisorio(deAnnoProvvisorio);

    Date data = new Date();
    prenotazioneFlussoRiconciliazione.setDtCreazione(data);
    prenotazioneFlussoRiconciliazione.setDtUltimaModifica(data);

    if (dtDataUltimoAggiornamentoDa != null && dtDataUltimoAggiornamentoA == null) {
      Date dataSenzaTime = Utilities.toDateAtMidnight(data);
      prenotazioneFlussoRiconciliazione.setDtDataUltimoAggiornamentoA(dataSenzaTime);
    }

    long mygovPrenotazioneFlussoRiconciliazioneId = prenotazioneFlussoRiconciliazioneDao.insert(prenotazioneFlussoRiconciliazione);
    prenotazioneFlussoRiconciliazione.setMygovPrenotazioneFlussoRiconciliazioneId(mygovPrenotazioneFlussoRiconciliazioneId);
    return prenotazioneFlussoRiconciliazione;
  }

  public List<FlussoExportTo> flussiExport(Long mygovEnteId, String codFedUserId, String nomeFile, LocalDate dateFrom,
                                           LocalDate dateTo) throws ValidatorException {
    if (dateTo.isBefore(dateFrom)) {
      throw new ValidatorException(messageSource.getMessage("pa.messages.invalidDataIntervallo", null, Locale.ITALY));
    }
    LocalDate dateToFinal = dateTo.plusDays(1);
    final List<String> listCodStatoRiconciliazione = Arrays.asList(
        Constants.COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_PRENOTATO,
        Constants.COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_ERRORE_EXPORT_FLUSSO_RICONCILIAZIONE,
        Constants.COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_NUMERO_MASSIMO_EXPORT_RIGHE_CONSENTITO_SUPERATO,
        Constants.COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_EXPORT_ESEGUITO,
        Constants.COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_EXPORT_ESEGUITO_NESSUN_RECORD_TROVATO,
        Constants.COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_VERSIONE_TRACCIATO_ERRATA);

    return maxResultsHelper.manageMaxResults( maxResults -> prenotazioneFlussoRiconciliazioneDao.getByNomeFileDtCreazione(
      mygovEnteId, codFedUserId, nomeFile, listCodStatoRiconciliazione, dateFrom, dateToFinal, maxResults),
      () -> prenotazioneFlussoRiconciliazioneDao.getByNomeFileDtCreazioneCount(
        mygovEnteId, codFedUserId, nomeFile, listCodStatoRiconciliazione, dateFrom, dateTo)
    );
  }

  public List<PrenotazioneFlussoRiconciliazione> getByRequestToken(String codRequestToken) {
    return prenotazioneFlussoRiconciliazioneDao.getByRequestToken(codRequestToken);
  }
}
