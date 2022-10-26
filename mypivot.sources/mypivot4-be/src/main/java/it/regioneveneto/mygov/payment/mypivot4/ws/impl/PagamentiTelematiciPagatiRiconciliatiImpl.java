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
package it.regioneveneto.mygov.payment.mypivot4.ws.impl;

import it.regioneveneto.mygov.payment.mypay4.security.JwtTokenUtil;
import it.regioneveneto.mygov.payment.mypay4.service.MyBoxService;
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypay4.util.VerificationUtils;
import it.regioneveneto.mygov.payment.mypivot4.controller.FlussoController;
import it.regioneveneto.mygov.payment.mypivot4.controller.MyBoxController;
import it.regioneveneto.mygov.payment.mypivot4.dto.VerificaClassificazioneDto;
import it.regioneveneto.mygov.payment.mypivot4.dto.VerificaRiconciliazioneDto;
import it.regioneveneto.mygov.payment.mypivot4.model.*;
import it.regioneveneto.mygov.payment.mypivot4.service.*;
import it.regioneveneto.mygov.payment.mypivot4.ws.PagamentiTelematiciPagatiRiconciliati;
import it.regioneveneto.mygov.payment.mypivot4.ws.helper.PagamentiTelematiciPagatiRiconciliatiHelper;
import it.veneto.regione.pagamenti.pivot.ente.*;
import it.veneto.regione.pagamenti.pivot.ente.ppthead.IntestazionePPT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service("PagamentiTelematiciPagatiRiconciliatiImpl")
@Slf4j
public class PagamentiTelematiciPagatiRiconciliatiImpl implements PagamentiTelematiciPagatiRiconciliati {

  @Autowired
  private EnteService enteService;

  @Autowired
  private UtenteService utenteService;

  @Autowired
  private FlussoService flussoService;

  @Autowired
  private EnteTipoDovutoService enteTipoDovutoService;

  @Autowired
  private ManageFlussoService manageFlussoService;

  @Autowired
  private PrenotazioneFlussoRiconciliazioneService prenotazioneFlussoRiconciliazioneService;

  @Autowired
  private AccertamentoDettaglioService accertamentoDettaglioService;

  @Autowired
  private PagatiRiconciliatiService pagatiRiconciliatiService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private MyBoxService myBoxService;

  @Value("${app.be.absolute-path}")
  private String apiAbsolutePath;

  @Override
  public PivotSILAutorizzaImportFlussoRisposta pivotSILAutorizzaImportFlusso(PivotSILAutorizzaImportFlusso bodyrichiesta, IntestazionePPT header) {
    return this.autorizzaImportFlusso(Constants.TIPO_FLUSSO.DOVUTI, header.getCodIpaEnte(), bodyrichiesta.getPassword());
  }

  @Override
  public PivotSILAutorizzaImportFlussoRendicontazioneRisposta pivotSILAutorizzaImportFlussoRendicontazione(PivotSILAutorizzaImportFlussoRendicontazione bodyrichiesta, IntestazionePPT header) {
    PivotSILAutorizzaImportFlussoRisposta _risposta = this.autorizzaImportFlusso(Constants.TIPO_FLUSSO.RENDICONTAZIONE_STANDARD, header.getCodIpaEnte(), bodyrichiesta.getPassword());
    PivotSILAutorizzaImportFlussoRendicontazioneRisposta risposta = new PivotSILAutorizzaImportFlussoRendicontazioneRisposta();
    try {
      BeanUtils.copyProperties(risposta, _risposta);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("error copying properties", e);
      risposta.setFault(VerificationUtils.getFaultBean(header.getCodIpaEnte(), Constants.CODE_PIVOT_SYSTEM_ERROR, "Errore interno", null));
    }
    return risposta;
  }

  @Override
  public PivotSILAutorizzaImportFlussoRTRisposta pivotSILAutorizzaImportFlussoRT(PivotSILAutorizzaImportFlussoRT bodyrichiesta, IntestazionePPT header) {
    PivotSILAutorizzaImportFlussoRisposta _risposta = this.autorizzaImportFlusso(Constants.TIPO_FLUSSO.EXPORT_PAGATI, header.getCodIpaEnte(), bodyrichiesta.getPassword());
    PivotSILAutorizzaImportFlussoRTRisposta risposta = new PivotSILAutorizzaImportFlussoRTRisposta();
    try {
      BeanUtils.copyProperties(risposta, _risposta);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("error copying properties", e);
      risposta.setFault(VerificationUtils.getFaultBean(header.getCodIpaEnte(), Constants.CODE_PIVOT_SYSTEM_ERROR, "Errore interno", null));
    }
    return risposta;
  }

  @Override
  public PivotSILAutorizzaImportFlussoTesoreriaRisposta pivotSILAutorizzaImportFlussoTesoreria(PivotSILAutorizzaImportFlussoTesoreria bodyrichiesta, IntestazionePPT header) {

    Constants.TIPO_FLUSSO tipoFlusso;
    if(StringUtils.isNotBlank(bodyrichiesta.getTipoFlusso())){
      if( Constants.TIPO_FLUSSO.TESORERIA.getCod().equals(bodyrichiesta.getTipoFlusso()) ||
          Constants.TIPO_FLUSSO.GIORNALE_DI_CASSA.getCod().equals(bodyrichiesta.getTipoFlusso()) ||
          Constants.TIPO_FLUSSO.GIORNALE_DI_CASSA_OPI.getCod().equals(bodyrichiesta.getTipoFlusso()) ||
          Constants.TIPO_FLUSSO.ESTRATTO_CONTO_POSTE.getCod().equals(bodyrichiesta.getTipoFlusso()) ){
        tipoFlusso = Constants.TIPO_FLUSSO.of(bodyrichiesta.getTipoFlusso());
      } else {
        log.error("invalid tipo flusso: "+bodyrichiesta.getTipoFlusso());
        PivotSILAutorizzaImportFlussoTesoreriaRisposta risposta = new PivotSILAutorizzaImportFlussoTesoreriaRisposta();
        risposta.setFault(VerificationUtils.getFaultBean(header.getCodIpaEnte(), Constants.CODE_PIVOT_TIPO_FLUSSO_NON_VALIDO,
            "Tipo Flusso Tesoreria [" + bodyrichiesta.getTipoFlusso() + "] non valido", null));
        return risposta;
      }
    } else {
      tipoFlusso = Constants.TIPO_FLUSSO.TESORERIA;
    }

    PivotSILAutorizzaImportFlussoRisposta _risposta = this.autorizzaImportFlusso(tipoFlusso, header.getCodIpaEnte(), bodyrichiesta.getPassword());
    PivotSILAutorizzaImportFlussoTesoreriaRisposta risposta = new PivotSILAutorizzaImportFlussoTesoreriaRisposta();
    try {
      BeanUtils.copyProperties(risposta, _risposta);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("error copying properties", e);
      risposta.setFault(VerificationUtils.getFaultBean(header.getCodIpaEnte(), Constants.CODE_PIVOT_SYSTEM_ERROR, "Errore interno", null));
    }
    return risposta;
  }

  @Override
  public PivotSILChiediAccertamentoRisposta pivotSILChiediAccertamento(PivotSILChiediAccertamento bodyrichiesta, IntestazionePPT header) {
    PivotSILChiediAccertamentoRisposta risposta = new PivotSILChiediAccertamentoRisposta();

    // Ente, Password
    risposta.setFault(enteService.verificaEnte(header.getCodIpaEnte(), bodyrichiesta.getPassword()));
    if (risposta.getFault() != null)
      return risposta;

    //bolletta, iuf
    RichiestaPerBolletta richiestaPerBolletta = bodyrichiesta.getRichiestaPerBolletta();
    RichiestaPerIUF richiestaPerIUF = bodyrichiesta.getRichiestaPerIUF();
    risposta.setFault(VerificationUtils.checkRichiestaBollettaAndIUF(header.getCodIpaEnte(), richiestaPerBolletta, richiestaPerIUF));
    if (risposta.getFault() != null)
      return risposta;

    List<FlussoExport> flussiExport = null;
    String codIpaEnte = header.getCodIpaEnte(), IUF = "", IUV = "";

    try {
      if (richiestaPerBolletta != null) {
        risposta.setFault(VerificationUtils.checkRichiestaPerBolletta(codIpaEnte, richiestaPerBolletta));
        if (risposta.getFault() != null)
          return risposta;

        String annoBolletta = richiestaPerBolletta.getAnnoBolletta(), codiceBolletta = richiestaPerBolletta.getNumeroBolletta();
        log.debug("Chiamata al servizio pivotSILChiediAccertamento con richiesta per bolletta con parameri codIpaEnte [ "
            + codIpaEnte + " ], annoBolletta [ " + annoBolletta + " ] e numeroBolletta [ " + codiceBolletta + " ]");

        FlussoTesoreria flussoTesoreria =accertamentoDettaglioService.getByCodIpaDeAnnoBollettaCodBolletta(codIpaEnte, annoBolletta, codiceBolletta);
        risposta.setFault(VerificationUtils.checkFlussoTesoreria(codIpaEnte, annoBolletta, codiceBolletta, flussoTesoreria));
        if (risposta.getFault() != null)
          return risposta;

        IUF = flussoTesoreria.getCodIdUnivocoFlusso();
        IUV = flussoTesoreria.getCodIdUnivocoVersamento();
        if (StringUtils.isNotBlank(IUF)) {
          flussiExport = accertamentoDettaglioService.getFlussiExportByCodIpaIUF(codIpaEnte, IUF);
          risposta.setFault(VerificationUtils.checkFlussiExportByIUF(codIpaEnte, flussiExport));
          if (risposta.getFault() != null)
            return risposta;
        } else { // Same condition as StringUtils.isNotBlank(IUV)
          flussiExport = accertamentoDettaglioService.getFlussiExportByCodIpaIUV(codIpaEnte, IUV);
        }

      } else if (richiestaPerIUF != null) {
        risposta.setFault(VerificationUtils.checkRichiestaPerIUF(codIpaEnte, richiestaPerIUF));
        if (risposta.getFault() != null)
          return risposta;

        IUF = richiestaPerIUF.getIdentificativoUnivocoFlusso();
        flussiExport = accertamentoDettaglioService.getFlussiExportByCodIpaIUF(codIpaEnte, IUF);
        risposta.setFault(VerificationUtils.checkFlussiExportByIUF(codIpaEnte, flussiExport));
        if (risposta.getFault() != null)
          return risposta;
      }

      List<CtBilancio> bilancios = accertamentoDettaglioService.getCtBilancios(codIpaEnte, flussiExport);
      risposta.getBilancios().addAll(bilancios);

    } catch (Exception ex) {
      String errorMsg = String.format("Si è verificato un errore durante l'esecuzione della richiesta per " +
              "codIpaEnte [%s], identificativoUnivocoFlusso [%s], identificativoUnivocoVersamento [%s]", codIpaEnte, IUF, IUV);
      log.error("pivotSILChiediAccertamento: " + errorMsg);
      risposta.setFault(VerificationUtils.getFaultBean(codIpaEnte, Constants.PIVOT_SYSTEM_ERROR, errorMsg, null));
    }
    return risposta;
  }

  @Override
  public PivotSILChiediPagatiRiconciliatiRisposta pivotSILChiediPagatiRiconciliati(PivotSILChiediPagatiRiconciliati bodyrichiesta) {
    PivotSILChiediPagatiRiconciliatiRisposta risposta = new PivotSILChiediPagatiRiconciliatiRisposta();

    FaultBean faultBean = enteService.verificaEnte(bodyrichiesta.getCodIpaEnte(), bodyrichiesta.getPassword());
    if (faultBean != null) {
      return risposta;
    }
    risposta = pagatiRiconciliatiService.verificaPagatiRiconciliati(bodyrichiesta);
    return risposta;
  }

  @Override
  public PivotSILChiediStatoExportFlussoRiconciliazioneRisposta pivotSILChiediStatoExportFlussoRiconciliazione(PivotSILChiediStatoExportFlussoRiconciliazione bodyrichiesta, IntestazionePPT header) {
    PivotSILChiediStatoExportFlussoRiconciliazioneRisposta risposta = new PivotSILChiediStatoExportFlussoRiconciliazioneRisposta();

    // Ente, Password
    risposta.setFault(enteService.verificaEnte(header.getCodIpaEnte(), bodyrichiesta.getPassword()));
    if (risposta.getFault() != null)
      return risposta;

    // Token
    risposta.setFault(VerificationUtils.checkRequestToken(header.getCodIpaEnte(), bodyrichiesta.getRequestToken()));
    if (risposta.getFault() != null)
      return risposta;

    List<PrenotazioneFlussoRiconciliazione> prenotazioni = prenotazioneFlussoRiconciliazioneService.getByRequestToken(bodyrichiesta.getRequestToken());
    risposta.setFault(VerificationUtils.checkPrenotazioni(header.getCodIpaEnte(), prenotazioni));
    if (risposta.getFault() != null)
      return risposta;

    PrenotazioneFlussoRiconciliazione prenotazione = prenotazioni.get(0);
    String downloadUrl=null;
    if (prenotazione.getMygovAnagraficaStatoId().getDeTipoStato().equals(Constants.DE_TIPO_STATO_PRENOTA_FLUSSO_RICONCILIAZIONE)
        && prenotazione.getMygovAnagraficaStatoId().getCodStato().equals(Constants.COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_EXPORT_ESEGUITO)) {
      try {
        Ente ente = enteService.getEnteByCodIpa(header.getCodIpaEnte());
        String securityToken = myBoxService.generateSecurityToken(
            FlussoController.FILE_TYPE_FLUSSI_EXPORT,
            prenotazione.getDeNomeFileGenerato(),
            null,
            ente.getMygovEnteId());
        downloadUrl = String.format("%s/public/mybox/download/%d?type=%s&filename=%s&securityToken=%s", apiAbsolutePath, ente.getMygovEnteId(),
            FlussoController.FILE_TYPE_FLUSSI_EXPORT, prenotazione.getDeNomeFileGenerato(), securityToken);
        downloadUrl = new URL(downloadUrl).toURI().toASCIIString();
      } catch (Exception ex) {
        log.error("pivotSILChiediStatoExportFlussoRiconciliazione: Error in building download URL :" + downloadUrl);
        risposta.setFault(VerificationUtils.getFaultBean(header.getCodIpaEnte(), Constants.PIVOT_SYSTEM_ERROR,
            "Errore durante la creazione dell'url di download", null));
        return risposta;
      }
    }
    risposta.setDownloadUrl(downloadUrl);
    risposta.setStato(prenotazione.getMygovAnagraficaStatoId().getCodStato());
    return risposta;
  }

  @Override
  public PivotSILChiediStatoImportFlussoRisposta pivotSILChiediStatoImportFlusso(PivotSILChiediStatoImportFlusso bodyrichiesta, IntestazionePPT header) {
    return this.chiediStatoImportFlusso(header.getCodIpaEnte(), bodyrichiesta.getPassword(), bodyrichiesta.getRequestToken());
  }

  @Override
  public PivotSILChiediStatoImportFlussoTesoreriaRisposta pivotSILChiediStatoImportFlussoTesoreria(PivotSILChiediStatoImportFlussoTesoreria bodyrichiesta, IntestazionePPT header) {
    PivotSILChiediStatoImportFlussoRisposta _risposta = this.chiediStatoImportFlusso(header.getCodIpaEnte(), bodyrichiesta.getPassword(), bodyrichiesta.getRequestToken());
    PivotSILChiediStatoImportFlussoTesoreriaRisposta risposta = new PivotSILChiediStatoImportFlussoTesoreriaRisposta();
    try {
      BeanUtils.copyProperties(risposta, _risposta);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("error copying properties", e);
      risposta.setFault(VerificationUtils.getFaultBean(header.getCodIpaEnte(), Constants.CODE_PIVOT_SYSTEM_ERROR, "Errore interno", null));
    }
    return risposta;
  }

  @Override
  public PivotSILPrenotaExportFlussoRiconciliazioneRisposta pivotSILPrenotaExportFlussoRiconciliazione(PivotSILPrenotaExportFlussoRiconciliazione bodyrichiesta, IntestazionePPT header) {
    PivotSILPrenotaExportFlussoRiconciliazioneRisposta risposta = new PivotSILPrenotaExportFlussoRiconciliazioneRisposta();

    // Ente, Password
    risposta.setFault(enteService.verificaEnte(header.getCodIpaEnte(), bodyrichiesta.getPassword()));
    if (risposta.getFault() != null) {
      return risposta;
    }

    Ente ente = enteService.getEnteByCodIpa(header.getCodIpaEnte());

    // Versione tracciato, Classificazioni
    VerificaClassificazioneDto vc = PagamentiTelematiciPagatiRiconciliatiHelper.verificaClassificazione(ente, bodyrichiesta, header);
    if (vc.getFaultBean() != null) {
      risposta.setFault(vc.getFaultBean());
      return risposta;
    }

    // Tipi dovuto
    TipoDovutoType tdType = bodyrichiesta.getTipoDovuto();
    String finalTipoDovuto = null;
    if (tdType != null && CollectionUtils.isNotEmpty(tdType.getTipos())) {
      risposta.setFault(enteTipoDovutoService.verificaTipiDovuto(header.getCodIpaEnte(), tdType.getTipos()));
      if (risposta.getFault() != null) {
        return risposta;
      }
      finalTipoDovuto = String.join("|", tdType.getTipos());
    }

    VerificaRiconciliazioneDto vr = PagamentiTelematiciPagatiRiconciliatiHelper.verificaDateRiconciliazione(bodyrichiesta, header);
    if (vr.getFaultBean() != null) {
      risposta.setFault(vr.getFaultBean());
      return risposta;
    }

    Utente utente = utenteService.getUtenteWSByCodIpaEnte(ente.getCodIpaEnte());

    PrenotazioneFlussoRiconciliazione prenotazione = prenotazioneFlussoRiconciliazioneService.insert(ente, utente,
            Constants.COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_PRENOTATO,
            Constants.DE_TIPO_STATO_PRENOTA_FLUSSO_RICONCILIAZIONE, vc.getFinalCC(), finalTipoDovuto, vr.getFinalIuv(),
            vr.getFinalIuf(), vr.getDtUltimoAggiornamentoDa(), vr.getDtUltimoAggiornamentoA(), vr.getDtEsecuzioneDa(),
            vr.getDtEsecuzioneA(), vr.getDtEsitoDa(), vr.getDtEsitoA(), vr.getDtRegolamentoDa(), vr.getDtRegolamentoA(),
            vr.getDtContabileA(), vr.getDtContabileA(), vr.getDtValutaDa(), vr.getDtValutaA(),
            bodyrichiesta.getIdUnivocoDovuto(), bodyrichiesta.getIdUnivocoRiscossione(),
            bodyrichiesta.getIdUnivocoPagatore(), bodyrichiesta.getAnagraficaPagatore(),
            bodyrichiesta.getIdUnivocoVersante(), bodyrichiesta.getAnagraficaVersante(),
            bodyrichiesta.getDenominazioneAttestante(), bodyrichiesta.getOrdinante(),
            bodyrichiesta.getIdRegolamento(), bodyrichiesta.getContoTesoreria(),
            bodyrichiesta.getImportoTesoreria(), bodyrichiesta.getCausale(), bodyrichiesta.getVersioneTracciato(),
            null, null, null, null, null, null);

    risposta.setRequestToken(prenotazione.getCodRequestToken());
    if (prenotazione.getDtDataUltimoAggiornamentoDa() != null && prenotazione.getDtDataUltimoAggiornamentoA() != null)
      risposta.setDataA(Utilities.toXMLGregorianCalendarWithoutTimezone(prenotazione.getDtDataUltimoAggiornamentoA()));

    return risposta;
  }

  private  PivotSILAutorizzaImportFlussoRisposta autorizzaImportFlusso(Constants.TIPO_FLUSSO tipoFlusso, String codIpaEnte, String password) {
    PivotSILAutorizzaImportFlussoRisposta risposta = new PivotSILAutorizzaImportFlussoRisposta();

    FaultBean faultBean = enteService.verificaEnte(codIpaEnte, password);
    if (faultBean != null) {
      risposta.setFault(faultBean);
      return risposta;
    }
    Timestamp tsCreazione = new Timestamp(System.currentTimeMillis());
    String importPath = flussoService.getImportPath(tipoFlusso, tsCreazione);
    String requestToken = UUID.randomUUID().toString();
    String authorizationToken = jwtTokenUtil.generateWsAuthorizationToken(codIpaEnte, importPath, requestToken, tipoFlusso.getCod());
    risposta.setImportPath(importPath);
    risposta.setRequestToken(requestToken);
    risposta.setAuthorizationToken(authorizationToken);
    risposta.setUploadUrl(flussoService.getApiAbsolutePath()+ MyBoxController.UPLOAD_FLUSSO_PATH);

    return risposta;
  }

  private PivotSILChiediStatoImportFlussoRisposta chiediStatoImportFlusso(String codIpaEnte, String password, String requestToken) {
    PivotSILChiediStatoImportFlussoRisposta risposta = new PivotSILChiediStatoImportFlussoRisposta();
    FaultBean faultBean =  enteService.verificaEnte(codIpaEnte, password);
    if (faultBean != null) {
      risposta.setFault(faultBean);
      return risposta;
    }

    ManageFlusso manageFlusso = manageFlussoService.getByCodRequestToken(requestToken);
    if (manageFlusso == null || !codIpaEnte.equals(manageFlusso.getMygovEnteId().getCodIpaEnte())) {
      String errorMsg = "Ente [" + codIpaEnte + "] non autorizzato per requestToken ["+requestToken + "]";
      risposta.setFault(VerificationUtils.getFaultBean(codIpaEnte, Constants.CODE_PIVOT_REQUEST_TOKEN_NON_VALIDO, errorMsg, null));
      return risposta;
    }
    String codStato = manageFlusso.getMygovAnagraficaStatoId().getCodStato();
    risposta.setStato(codStato);
    return risposta;
  }

}
