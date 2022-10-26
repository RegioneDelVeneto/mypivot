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
package it.regioneveneto.mygov.payment.mypivot4.ws.helper;

import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypay4.util.VerificationUtils;
import it.regioneveneto.mygov.payment.mypivot4.dto.VerificaClassificazioneDto;
import it.regioneveneto.mygov.payment.mypivot4.dto.VerificaRiconciliazioneDto;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.veneto.regione.pagamenti.pivot.ente.CodiceClassificazioneType;
import it.veneto.regione.pagamenti.pivot.ente.FaultBean;
import it.veneto.regione.pagamenti.pivot.ente.IdUnivocoRendicontazioneType;
import it.veneto.regione.pagamenti.pivot.ente.IdUnivocoVersamentoType;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILPrenotaExportFlussoRiconciliazione;
import it.veneto.regione.pagamenti.pivot.ente.ppthead.IntestazionePPT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Date;

@Slf4j
public class PagamentiTelematiciPagatiRiconciliatiHelper {

  public static VerificaClassificazioneDto verificaClassificazione(Ente ente, PivotSILPrenotaExportFlussoRiconciliazione bodyrichiesta, IntestazionePPT header) {
    VerificaClassificazioneDto verificaClassificazioneDto = new VerificaClassificazioneDto();
    // Versione tracciato
    FaultBean faultBean = VerificationUtils.checkVersioneTracciatoExport(header.getCodIpaEnte(), bodyrichiesta.getVersioneTracciato());
    if (faultBean != null) {
      verificaClassificazioneDto.setFaultBean(faultBean);
      return verificaClassificazioneDto;
    }
    // Classificazioni
    CodiceClassificazioneType ccType = bodyrichiesta.getCodiceClassificazione();
    if (ccType == null || CollectionUtils.isEmpty(ccType.getClassificaziones())) {
      log.error("Nessuna classificazione inserita");
      verificaClassificazioneDto.setFaultBean(VerificationUtils.getFaultBean(header.getCodIpaEnte(), Constants.PIVOT_CLASSIFICAZIONE_NON_VALIDA, "Nessuna classificazione inserita", null));
      return verificaClassificazioneDto;
    }
    for (String classificazione : ccType.getClassificaziones()) {
      faultBean = VerificationUtils.checkValiditaClassificazione(classificazione, ente);
      if (faultBean != null) {
        verificaClassificazioneDto.setFaultBean(faultBean);
        return verificaClassificazioneDto;
      }
    }
    String finalCC = String.join("|", ccType.getClassificaziones());

    verificaClassificazioneDto.setFinalCC(finalCC);
    verificaClassificazioneDto.setFaultBean(null);

    return verificaClassificazioneDto;
  }

  public static VerificaRiconciliazioneDto verificaDateRiconciliazione(PivotSILPrenotaExportFlussoRiconciliazione bodyrichiesta, IntestazionePPT header) {
    VerificaRiconciliazioneDto verificaRiconciliazioneDto = new VerificaRiconciliazioneDto();
    // IUV
    IdUnivocoVersamentoType iuvType = bodyrichiesta.getIdUnivocoVersamento();
    String finalIuv = iuvType == null || CollectionUtils.isEmpty(iuvType.getIuvs()) ? null : String.join("|", iuvType.getIuvs());

    // IUF
    IdUnivocoRendicontazioneType iufType = bodyrichiesta.getIdUnivocoRendicontazione();
    String finalIuf = iufType == null || CollectionUtils.isEmpty(iufType.getIurs()) ? null : String.join("|", iufType.getIurs());

    // Data ultimo aggiornamento
    Date dtUltimoAggiornamentoDa = Utilities.toDate(bodyrichiesta.getDataUltimoAggiornamentoDa());
    Date dtUltimoAggiornamentoA = Utilities.toDate(bodyrichiesta.getDataUltimoAggiornamentoA());
    FaultBean faultBean = VerificationUtils.checkDateUltimoAggiornamento(header.getCodIpaEnte(), dtUltimoAggiornamentoDa, dtUltimoAggiornamentoA);
    if (faultBean != null) {
      verificaRiconciliazioneDto.setFaultBean(faultBean);
      return verificaRiconciliazioneDto;
    }

    // Data esecuzione
    Date dtEsecuzioneDa = Utilities.toDate(bodyrichiesta.getDataEsecuzioneDa());
    Date dtEsecuzioneA = Utilities.toDate(bodyrichiesta.getDataEsecuzioneA());
    faultBean = VerificationUtils.checkDateFromTo(header.getCodIpaEnte(), "esecuzione", dtEsecuzioneDa, dtEsecuzioneA);
    if (faultBean != null) {
      verificaRiconciliazioneDto.setFaultBean(faultBean);
      return verificaRiconciliazioneDto;
    }

    // Data esito
    Date dtEsitoDa = Utilities.toDate(bodyrichiesta.getDataEsitoDa());
    Date dtEsitoA = Utilities.toDate(bodyrichiesta.getDataEsitoA());
    faultBean = VerificationUtils.checkDateFromTo(header.getCodIpaEnte(), "esito", dtEsitoDa, dtEsitoA);
    if (faultBean != null) {
      verificaRiconciliazioneDto.setFaultBean(faultBean);
      return verificaRiconciliazioneDto;
    }

    // Data regolamento
    Date dtRegolamentoDa = Utilities.toDate(bodyrichiesta.getDataRegolamentoDa());
    Date dtRegolamentoA = Utilities.toDate(bodyrichiesta.getDataRegolamentoA());
    faultBean = VerificationUtils.checkDateFromTo(header.getCodIpaEnte(), "regolamento", dtRegolamentoDa, dtRegolamentoA);
    if (faultBean != null) {
      verificaRiconciliazioneDto.setFaultBean(faultBean);
      return verificaRiconciliazioneDto;
    }

    // Data contabile
    Date dtContabileDa = Utilities.toDate(bodyrichiesta.getDataContabileDa());
    Date dtContabileA = Utilities.toDate(bodyrichiesta.getDataContabileA());
    faultBean = VerificationUtils.checkDateFromTo(header.getCodIpaEnte(), "contabile", dtContabileDa, dtContabileA);
    if (faultBean != null) {
      verificaRiconciliazioneDto.setFaultBean(faultBean);
      return verificaRiconciliazioneDto;
    }

    // Data valuta
    Date dtValutaDa = Utilities.toDate(bodyrichiesta.getDataValutaDa());
    Date dtValutaA = Utilities.toDate(bodyrichiesta.getDataValutaA());
    faultBean = VerificationUtils.checkDateFromTo(header.getCodIpaEnte(), "valuta", dtValutaDa, dtValutaA);
    if (faultBean != null) {
      verificaRiconciliazioneDto.setFaultBean(faultBean);
      return verificaRiconciliazioneDto;
    }

    verificaRiconciliazioneDto.setFinalIuv(finalIuv);
    verificaRiconciliazioneDto.setFinalIuf(finalIuf);
    verificaRiconciliazioneDto.setDtUltimoAggiornamentoDa(dtUltimoAggiornamentoDa);
    verificaRiconciliazioneDto.setDtUltimoAggiornamentoA(dtUltimoAggiornamentoA);
    verificaRiconciliazioneDto.setDtEsecuzioneDa(dtEsecuzioneDa);
    verificaRiconciliazioneDto.setDtEsecuzioneA(dtEsecuzioneA);
    verificaRiconciliazioneDto.setDtEsitoDa(dtEsitoDa);
    verificaRiconciliazioneDto.setDtEsitoA(dtEsitoA);
    verificaRiconciliazioneDto.setDtRegolamentoDa(dtRegolamentoDa);
    verificaRiconciliazioneDto.setDtRegolamentoA(dtRegolamentoA);
    verificaRiconciliazioneDto.setDtContabileDa(dtContabileDa);
    verificaRiconciliazioneDto.setDtContabileA(dtContabileA);
    verificaRiconciliazioneDto.setDtValutaDa(dtValutaDa);
    verificaRiconciliazioneDto.setDtValutaA(dtValutaA);
    verificaRiconciliazioneDto.setFaultBean(null);

    return verificaRiconciliazioneDto;
  }
}
