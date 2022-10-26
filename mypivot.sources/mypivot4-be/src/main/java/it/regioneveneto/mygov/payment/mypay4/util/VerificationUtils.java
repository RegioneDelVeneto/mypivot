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
package it.regioneveneto.mygov.payment.mypay4.util;

import it.regioneveneto.mygov.payment.mypay4.exception.ValidatorException;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.FlussoExport;
import it.regioneveneto.mygov.payment.mypivot4.model.FlussoTesoreria;
import it.regioneveneto.mygov.payment.mypivot4.model.PrenotazioneFlussoRiconciliazione;
import it.veneto.regione.pagamenti.pivot.ente.CtRiversamentiCumulativi;
import it.veneto.regione.pagamenti.pivot.ente.CtRiversamentiPuntuali;
import it.veneto.regione.pagamenti.pivot.ente.FaultBean;
import it.veneto.regione.pagamenti.pivot.ente.RichiestaPerBolletta;
import it.veneto.regione.pagamenti.pivot.ente.RichiestaPerIUF;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_IUD_NO_RT;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_IUD_RT_IUF;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_IUD_RT_IUF_TES;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_IUF_NO_TES;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_IUF_TES_DIV_IMP;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_IUV_NO_RT;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_RT_IUF;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_RT_IUF_TES;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_RT_NO_IUD;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_RT_NO_IUF;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_RT_TES;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_TES_NO_IUF_OR_IUV;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.COD_ERRORE_TES_NO_MATCH;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_BOLLETTA_NON_PAGOPA;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_BOLLETTA_NON_TROVATA;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_CLASSIFICAZIONE_NON_ABILITATA;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_CLASSIFICAZIONE_NON_VALIDA;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_DATE_FROM_NON_VALIDO;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_DATE_TO_NON_VALIDO;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_INTERVALLO_DATE_NON_VALIDO;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_NESSUNA_RENDICONTAZIONE_TROVATA;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_PARAMETRO_ANNO_BOLLETTA_NULLO;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_REQUEST_TOKEN_NON_VALIDO;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_RICHIESTA_CON_PARAMETRI_MULTIPLI;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.PIVOT_VERSIONE_TRACCIATO_EXPORT_NON_VALIDA;
import static it.regioneveneto.mygov.payment.mypay4.util.Constants.VERSIONE_TRACCIATO_EXPORT;

@Slf4j
public class VerificationUtils {


  public static FaultBean checkVersioneTracciatoExport(String codIpa, String versioneTracciato) {
    try {
      VERSIONE_TRACCIATO_EXPORT VER = VERSIONE_TRACCIATO_EXPORT.fromString(versioneTracciato);
      return null;
    } catch (ValidatorException ex) {
      log.error("Versione tracciato [ " + versioneTracciato + " ] non valida");
      return getFaultBean(codIpa, PIVOT_VERSIONE_TRACCIATO_EXPORT_NON_VALIDA, "Versione tracciato [ " + versioneTracciato + " ] non valida", null);
    }
  }

  public static FaultBean checkValiditaClassificazione(String classificazione, Ente ente) {
    boolean validForEnte;
    switch (classificazione) {
      case COD_ERRORE_IUD_RT_IUF_TES:
        validForEnte = ente.isFlgPagati() && ente.isFlgTesoreria(); break;
      case COD_ERRORE_RT_IUF_TES:
      case COD_ERRORE_IUF_NO_TES:
      case COD_ERRORE_TES_NO_IUF_OR_IUV:
      case COD_ERRORE_TES_NO_MATCH:
      case COD_ERRORE_RT_TES:
      case COD_ERRORE_IUF_TES_DIV_IMP:
        validForEnte = ente.isFlgTesoreria(); break;
      case COD_ERRORE_IUD_NO_RT:
      case COD_ERRORE_RT_NO_IUD:
      case COD_ERRORE_IUD_RT_IUF:
        validForEnte = ente.isFlgPagati(); break;
      case COD_ERRORE_RT_IUF:
      case COD_ERRORE_RT_NO_IUF:
      case COD_ERRORE_IUV_NO_RT:
        validForEnte =  true; break;
      default:
        log.error("Classificazione [ " + classificazione + " ] non valida");
        return getFaultBean(ente.getCodIpaEnte(), PIVOT_CLASSIFICAZIONE_NON_VALIDA,
            "Classificazione [ " + classificazione + " ] non valida", null);
    }
    if (!validForEnte) {
      log.error("Classificazione [ " + classificazione + " ] non abilitata per ente: " + ente.getCodIpaEnte());
      return getFaultBean(ente.getCodIpaEnte(), PIVOT_CLASSIFICAZIONE_NON_ABILITATA,
          "Classificazione [ " + classificazione + " ] non abilitata per ente [" + ente.getCodIpaEnte() + "]", null);
    }
    return null;
  }

  public static boolean isTipoDovutoAbilitatoPerClassificazione(String classificazioneCompletezza) {
    switch (classificazioneCompletezza) {
      case Constants.COD_ERRORE_RT_NO_IUF:
      case Constants.COD_ERRORE_IUF_NO_TES:
      case Constants.COD_ERRORE_IUD_NO_RT:
      case Constants.COD_ERRORE_RT_NO_IUD:
      case Constants.COD_ERRORE_IUD_RT_IUF_TES:
      case Constants.COD_ERRORE_RT_IUF:
      case Constants.COD_ERRORE_RT_IUF_TES:
      case Constants.COD_ERRORE_IUD_RT_IUF:
      case Constants.COD_ERRORE_RT_TES:
        return true;
      case Constants.COD_ERRORE_TES_NO_IUF_OR_IUV:
      case Constants.COD_ERRORE_IUV_NO_RT:
      case Constants.COD_ERRORE_TES_NO_MATCH:
      case Constants.COD_ERRORE_IUF_TES_DIV_IMP:
      default:
        return false;
    }
  }

  public static FaultBean checkDateFromTo(String codIpa, String namePrefix, Date from, Date to) {
    if (from == null && to == null)
      return null;
    if ((from != null && to == null) || (from == null && to != null)) {
      if (from == null)
        return getFaultBean(codIpa, PIVOT_DATE_FROM_NON_VALIDO, "Data "+namePrefix+" DA non valida", "");
      else
        return getFaultBean(codIpa, PIVOT_DATE_TO_NON_VALIDO, "Data "+namePrefix+" A non valida", "");
    }
    if (from.after(to))
      return getFaultBean(codIpa, PIVOT_INTERVALLO_DATE_NON_VALIDO, "Intervallo date "+namePrefix+" non valido", "");
    return null;
  }

  public static FaultBean checkDateUltimoAggiornamento(String codIpa, Date from, Date to) {
    if (from == null && to == null)
      return null;
    if (from == null && to != null) {
      return getFaultBean(codIpa, PIVOT_DATE_FROM_NON_VALIDO, "Data Ultimo Aggiornamento DA non valorizzata", null);
    } else if (from != null && to == null) {
      return null;
    }
    if (from.after(to) || from.equals(to)) {
      return getFaultBean(codIpa, PIVOT_INTERVALLO_DATE_NON_VALIDO,"Intervallo date ultimo aggiornamento non valido", null);
    }
    return null;
  }

  public static FaultBean checkDateExtractionRendicontazioneCompleta(String codIpa, Date from, Date to) {
    if (from == null) {
      return getFaultBean(codIpa, Constants.CODE_PIVOT_DATE_FROM_NON_VALIDO, Constants.DESC_PIVOT_DATE_FROM_NON_VALIDO,null);
    }
    if (to == null) {
      return getFaultBean(codIpa, Constants.CODE_PIVOT_DATE_TO_NON_VALIDO, Constants.DESC_PIVOT_DATE_TO_NON_VALIDO,null);
    }
    from = new Date(from.getTime() / 1000 * 1000);
    to = new Date(to.getTime() / 1000 * 1000);
    if (to.before(from)) {
      return getFaultBean(codIpa, Constants.CODE_PIVOT_INTERVALLO_DATE_NON_VALIDO, Constants.DESC_PIVOT_INTERVALLO_DATE_NON_VALIDO,null);
    }
    return null;
  }

  public static FaultBean checkRequestToken(String codIpa, String requestToken) {
    if (requestToken != null && StringUtils.isNotBlank(requestToken.trim()))
      return null;
    return getFaultBean(codIpa, PIVOT_REQUEST_TOKEN_NON_VALIDO, "Request Token vuoto", null);
  }

  public static FaultBean checkPrenotazioni(String codIpa, List<PrenotazioneFlussoRiconciliazione> prenotazioni) {
    if (prenotazioni == null || prenotazioni.size() != 1) // Check if the record has been found correctly.
      return getFaultBean(codIpa, Constants.PIVOT_REQUEST_TOKEN_NON_VALIDO, "Request Token non valido", null);
    if (!prenotazioni.get(0).getMygovEnteId().getCodIpaEnte().equals(codIpa)) // Check if the record has the same codIpa.
      return getFaultBean(codIpa, Constants.PIVOT_REQUEST_TOKEN_NON_VALIDO, "Request Token non valido", null);
    return null;
  }

  public static FaultBean checkRichiestaBollettaAndIUF(String codIpa, RichiestaPerBolletta reqBolletta, RichiestaPerIUF reqIuf) {
    if (reqBolletta == null || reqIuf == null)
      return null;
    log.error("pivotSILChiediAccertamento: Scegliere solo un metodo di richiesta per ente [" + codIpa + " ]");
    return getFaultBean(codIpa, PIVOT_RICHIESTA_CON_PARAMETRI_MULTIPLI, "Scegliere solo un metodo di richiesta per [ " + codIpa + " ]", null);
  }

  public static FaultBean checkRichiestaPerBolletta(String codIpa, RichiestaPerBolletta reqBolletta) {
    if (StringUtils.isBlank(reqBolletta.getAnnoBolletta())) {
      log.error("pivotSILChiediAccertamento: Il campo \"Anno bolletta\" è obbligatorio");
      return getFaultBean(codIpa, PIVOT_PARAMETRO_ANNO_BOLLETTA_NULLO,"Il campo \"Anno bolletta\" è obbligatorio", null);
    }
    if (StringUtils.isBlank(reqBolletta.getNumeroBolletta())) {
      log.error("pivotSILChiediAccertamento: Il campo \"Numero bolletta\" è obbligatorio");
      return getFaultBean(codIpa, Constants.PIVOT_PARAMETRO_NUMERO_BOLLETTA_NULLO, "Il campo \"Numero bolletta\" è obbligatorio", null);
    }
    return null;
  }

  public static FaultBean checkFlussoTesoreria(String codIpa, String codiceBolletta, String annoBolletta, FlussoTesoreria flusso) {
    if (flusso == null) {
      log.error("pivotSILChiediAccertamento: La bolletta per codIpaEnte [ " + codIpa + " ], annoBolletta [ "
          + annoBolletta + " ] e numeroBolletta [ " + codiceBolletta + " ] non è stata trovata");
      return getFaultBean(codIpa, PIVOT_BOLLETTA_NON_TROVATA, "La bolletta per codIpaEnte [ " + codIpa + " ], annoBolletta [ " + annoBolletta
              + " ] e numeroBolletta [ " + codiceBolletta + " ] non è stata trovata", null);
    }
    if (StringUtils.isBlank(flusso.getCodIdUnivocoFlusso()) && StringUtils.isBlank(flusso.getCodIdUnivocoVersamento())) {
      log.error("pivotSILChiediAccertamento: La bolletta per codIpaEnte [ " + codIpa
          + " ], annoBolletta [ " + annoBolletta + " ] e numeroBolletta [ " + codiceBolletta + " ] non è in standard pagoPA");
      return getFaultBean(codIpa, PIVOT_BOLLETTA_NON_PAGOPA, "La bolletta per codIpaEnte [ " + codIpa + " ], annoBolletta [ " + annoBolletta
                  + " ] e numeroBolletta [ " + codiceBolletta + " ] non è in standard pagoPA", null);
    }
    return null;
  }

  public static FaultBean checkFlussiExportByIUF(String codIpa, List<FlussoExport> flussiExport) {
    if (CollectionUtils.isEmpty(flussiExport)) {
      log.error("pivotSILChiediAccertamento: Nessuna rendicontazione associata allo IUF della bolletta");
      return getFaultBean(codIpa, PIVOT_NESSUNA_RENDICONTAZIONE_TROVATA,
          "Nessuna rendicontazione associata allo IUF della bolletta", null);
    }
    return null;
  }

  public static FaultBean checkRichiestaPerIUF(String codIpa, RichiestaPerIUF reqIuf) {
    if (StringUtils.isBlank(reqIuf.getIdentificativoUnivocoFlusso())) {
      log.error("pivotSILChiediAccertamento: Il campo \"Identificativo univoco flusso\" è obbligatorio");
      return getFaultBean(codIpa, Constants.PIVOT_PARAMETRO_IUF_NULLO, "Il campo \"Identificativo univoco flusso\" è obbligatorio", null);
    }
    return null;
  }

  public static FaultBean checkRichiestaPerIUVIUF(String codIpa, CtRiversamentiCumulativi riversamentiCumulativi, CtRiversamentiPuntuali riversamentiPuntuali) {
    if ((riversamentiCumulativi == null || CollectionUtils.isEmpty(riversamentiCumulativi.getIdentificativoFlussos())) &&
            (riversamentiPuntuali == null || CollectionUtils.isEmpty(riversamentiPuntuali.getIdentificativoUnivocoVersamentos()))) {
      log.error("riversamentiCumulativi e riversamentiPuntuali non presenti");
      return getFaultBean(codIpa, Constants.CODE_PIVOT_RICHIESTA_PER_IUV_IUF_NON_COMPLETA, Constants.DESC_PIVOT_RICHIESTA_PER_IUV_IUF_NON_COMPLETA,null);
    }
    return null;
  }

  public static FaultBean getFaultBean(String faultID, String faultCode, String faultString, String description) {
    log.error(faultCode + " " + description);
    FaultBean faultBean = new FaultBean();
    faultBean.setId(faultID);
    faultBean.setFaultCode(faultCode);
    faultBean.setFaultString(faultString);
    faultBean.setDescription(description);
    return faultBean;
  }
}
