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
package it.regioneveneto.mygov.payment.mypivot4.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import it.regioneveneto.mygov.payment.mypay4.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jdbi.v3.core.mapper.Nested;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "mygovPrenotazioneFlussoRiconciliazioneId")
public class PrenotazioneFlussoRiconciliazione extends BaseEntity {

  public final static String ALIAS = "PrenotazioneFlussoRiconciliazione";
  public final static String FIELDS = ""+ALIAS+".mygov_prenotazione_flusso_riconciliazione_id as PrenotazioneFlussoRiconciliazione_mygovPrenotazioneFlussoRiconciliazioneId"+
      ","+ALIAS+".version as PrenotazioneFlussoRiconciliazione_version"+
      ","+ALIAS+".mygov_ente_id as PrenotazioneFlussoRiconciliazione_mygovEnteId"+
      ","+ALIAS+".mygov_anagrafica_stato_id as PrenotazioneFlussoRiconciliazione_mygovAnagraficaStatoId"+
      ","+ALIAS+".mygov_utente_id as PrenotazioneFlussoRiconciliazione_mygovUtenteId"+
      ","+ALIAS+".cod_request_token as PrenotazioneFlussoRiconciliazione_codRequestToken"+
      ","+ALIAS+".de_nome_file_generato as PrenotazioneFlussoRiconciliazione_deNomeFileGenerato"+
      ","+ALIAS+".num_dimensione_file_generato as PrenotazioneFlussoRiconciliazione_numDimensioneFileGenerato"+
      ","+ALIAS+".cod_codice_classificazione as PrenotazioneFlussoRiconciliazione_codCodiceClassificazione"+
      ","+ALIAS+".de_tipo_dovuto as PrenotazioneFlussoRiconciliazione_deTipoDovuto"+
      ","+ALIAS+".cod_id_univoco_versamento as PrenotazioneFlussoRiconciliazione_codIdUnivocoVersamento"+
      ","+ALIAS+".cod_id_univoco_rendicontazione as PrenotazioneFlussoRiconciliazione_codIdUnivocoRendicontazione"+
      ","+ALIAS+".dt_data_ultimo_aggiornamento_da as PrenotazioneFlussoRiconciliazione_dtDataUltimoAggiornamentoDa"+
      ","+ALIAS+".dt_data_ultimo_aggiornamento_a as PrenotazioneFlussoRiconciliazione_dtDataUltimoAggiornamentoA"+
      ","+ALIAS+".dt_data_esecuzione_da as PrenotazioneFlussoRiconciliazione_dtDataEsecuzioneDa"+
      ","+ALIAS+".dt_data_esecuzione_a as PrenotazioneFlussoRiconciliazione_dtDataEsecuzioneA"+
      ","+ALIAS+".dt_data_esito_da as PrenotazioneFlussoRiconciliazione_dtDataEsitoDa"+
      ","+ALIAS+".dt_data_esito_a as PrenotazioneFlussoRiconciliazione_dtDataEsitoA"+
      ","+ALIAS+".dt_data_regolamento_da as PrenotazioneFlussoRiconciliazione_dtDataRegolamentoDa"+
      ","+ALIAS+".dt_data_regolamento_a as PrenotazioneFlussoRiconciliazione_dtDataRegolamentoA"+
      ","+ALIAS+".dt_data_contabile_da as PrenotazioneFlussoRiconciliazione_dtDataContabileDa"+
      ","+ALIAS+".dt_data_contabile_a as PrenotazioneFlussoRiconciliazione_dtDataContabileA"+
      ","+ALIAS+".dt_data_valuta_da as PrenotazioneFlussoRiconciliazione_dtDataValutaDa"+
      ","+ALIAS+".dt_data_valuta_a as PrenotazioneFlussoRiconciliazione_dtDataValutaA"+
      ","+ALIAS+".cod_id_univoco_dovuto as PrenotazioneFlussoRiconciliazione_codIdUnivocoDovuto"+
      ","+ALIAS+".cod_id_univoco_riscossione as PrenotazioneFlussoRiconciliazione_codIdUnivocoRiscossione"+
      ","+ALIAS+".cod_id_univoco_pagatore as PrenotazioneFlussoRiconciliazione_codIdUnivocoPagatore"+
      ","+ALIAS+".de_anagrafica_pagatore as PrenotazioneFlussoRiconciliazione_deAnagraficaPagatore"+
      ","+ALIAS+".cod_id_univoco_versante as PrenotazioneFlussoRiconciliazione_codIdUnivocoVersante"+
      ","+ALIAS+".de_anagrafica_versante as PrenotazioneFlussoRiconciliazione_deAnagraficaVersante"+
      ","+ALIAS+".de_denominazione_attestante as PrenotazioneFlussoRiconciliazione_deDenominazioneAttestante"+
      ","+ALIAS+".de_ordinante as PrenotazioneFlussoRiconciliazione_deOrdinante"+
      ","+ALIAS+".cod_id_regolamento as PrenotazioneFlussoRiconciliazione_codIdRegolamento"+
      ","+ALIAS+".cod_conto_tesoreria as PrenotazioneFlussoRiconciliazione_codContoTesoreria"+
      ","+ALIAS+".de_importo_tesoreria as PrenotazioneFlussoRiconciliazione_deImportoTesoreria"+
      ","+ALIAS+".de_causale as PrenotazioneFlussoRiconciliazione_deCausale"+
      ","+ALIAS+".dt_creazione as PrenotazioneFlussoRiconciliazione_dtCreazione"+
      ","+ALIAS+".dt_ultima_modifica as PrenotazioneFlussoRiconciliazione_dtUltimaModifica"+
      ","+ALIAS+".versione_tracciato as PrenotazioneFlussoRiconciliazione_versioneTracciato"+
      ","+ALIAS+".cod_bolletta as PrenotazioneFlussoRiconciliazione_codBolletta"+
      ","+ALIAS+".cod_documento as PrenotazioneFlussoRiconciliazione_codDocumento"+
      ","+ALIAS+".cod_provvisorio as PrenotazioneFlussoRiconciliazione_codProvvisorio"+
      ","+ALIAS+".de_anno_bolletta as PrenotazioneFlussoRiconciliazione_deAnnoBolletta"+
      ","+ALIAS+".de_anno_documento as PrenotazioneFlussoRiconciliazione_deAnnoDocumento"+
      ","+ALIAS+".de_anno_provvisorio as PrenotazioneFlussoRiconciliazione_deAnnoProvvisorio";

  private Long mygovPrenotazioneFlussoRiconciliazioneId;
  private int version;
  @Nested(Ente.ALIAS)
  private Ente mygovEnteId;
  @Nested(AnagraficaStato.ALIAS)
  private AnagraficaStato mygovAnagraficaStatoId;
  @Nested(Utente.ALIAS)
  private Utente mygovUtenteId;
  private String codRequestToken;
  private String deNomeFileGenerato;
  private Long numDimensioneFileGenerato;
  private String codCodiceClassificazione;
  private String deTipoDovuto;
  private String codIdUnivocoVersamento;
  private String codIdUnivocoRendicontazione;
  private Date dtDataUltimoAggiornamentoDa;
  private Date dtDataUltimoAggiornamentoA;
  private Date dtDataEsecuzioneDa;
  private Date dtDataEsecuzioneA;
  private Date dtDataEsitoDa;
  private Date dtDataEsitoA;
  private Date dtDataRegolamentoDa;
  private Date dtDataRegolamentoA;
  private Date dtDataContabileDa;
  private Date dtDataContabileA;
  private Date dtDataValutaDa;
  private Date dtDataValutaA;
  private String codIdUnivocoDovuto;
  private String codIdUnivocoRiscossione;
  private String codIdUnivocoPagatore;
  private String deAnagraficaPagatore;
  private String codIdUnivocoVersante;
  private String deAnagraficaVersante;
  private String deDenominazioneAttestante;
  private String deOrdinante;
  private String codIdRegolamento;
  private String codContoTesoreria;
  private String deImportoTesoreria;
  private String deCausale;
  private Date dtCreazione;
  private Date dtUltimaModifica;
  private String versioneTracciato;
  private String codBolletta;
  private String codDocumento;
  private String codProvvisorio;
  private String deAnnoBolletta;
  private String deAnnoDocumento;
  private String deAnnoProvvisorio;

}
