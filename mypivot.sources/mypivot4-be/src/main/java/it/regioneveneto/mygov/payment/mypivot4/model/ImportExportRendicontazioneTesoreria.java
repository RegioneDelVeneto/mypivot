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

import it.regioneveneto.mygov.payment.mypay4.model.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class ImportExportRendicontazioneTesoreria extends BaseEntity {

  public final static String ALIAS = "ImportExportRendicontazioneTesoreria";
  public final static String FIELDS = ""+ALIAS+".codice_ipa_ente as ImportExportRendicontazioneTesoreria_codiceIpaEnte"+
      ","+ALIAS+".dt_data_esecuzione_pagamento as ImportExportRendicontazioneTesoreria_dtDataEsecuzionePagamento"+
      ","+ALIAS+".de_data_esecuzione_pagamento as ImportExportRendicontazioneTesoreria_deDataEsecuzionePagamento"+
      ","+ALIAS+".singolo_importo_commissione_carico_pa as ImportExportRendicontazioneTesoreria_singoloImportoCommissioneCaricoPa"+
      ","+ALIAS+".bilancio as ImportExportRendicontazioneTesoreria_bilancio"+
      ","+ALIAS+".nome_flusso_import_ente as ImportExportRendicontazioneTesoreria_nomeFlussoImportEnte"+
      ","+ALIAS+".riga_flusso_import_ente as ImportExportRendicontazioneTesoreria_rigaFlussoImportEnte"+
      ","+ALIAS+".codice_iud as ImportExportRendicontazioneTesoreria_codiceIud"+
      ","+ALIAS+".codice_iuv as ImportExportRendicontazioneTesoreria_codiceIuv"+
      ","+ALIAS+".versione_oggetto as ImportExportRendicontazioneTesoreria_versioneOggetto"+
      ","+ALIAS+".identificativo_dominio as ImportExportRendicontazioneTesoreria_identificativoDominio"+
      ","+ALIAS+".identificativo_stazione_richiedente as ImportExportRendicontazioneTesoreria_identificativoStazioneRichiedente"+
      ","+ALIAS+".identificativo_messaggio_ricevuta as ImportExportRendicontazioneTesoreria_identificativoMessaggioRicevuta"+
      ","+ALIAS+".data_ora_messaggio_ricevuta as ImportExportRendicontazioneTesoreria_dataOraMessaggioRicevuta"+
      ","+ALIAS+".riferimento_messaggio_richiesta as ImportExportRendicontazioneTesoreria_riferimentoMessaggioRichiesta"+
      ","+ALIAS+".riferimento_data_richiesta as ImportExportRendicontazioneTesoreria_riferimentoDataRichiesta"+
      ","+ALIAS+".tipo_identificativo_univoco_attestante as ImportExportRendicontazioneTesoreria_tipoIdentificativoUnivocoAttestante"+
      ","+ALIAS+".codice_identificativo_univoco_attestante as ImportExportRendicontazioneTesoreria_codiceIdentificativoUnivocoAttestante"+
      ","+ALIAS+".denominazione_attestante as ImportExportRendicontazioneTesoreria_denominazioneAttestante"+
      ","+ALIAS+".codice_unit_oper_attestante as ImportExportRendicontazioneTesoreria_codiceUnitOperAttestante"+
      ","+ALIAS+".denom_unit_oper_attestante as ImportExportRendicontazioneTesoreria_denomUnitOperAttestante"+
      ","+ALIAS+".indirizzo_attestante as ImportExportRendicontazioneTesoreria_indirizzoAttestante"+
      ","+ALIAS+".civico_attestante as ImportExportRendicontazioneTesoreria_civicoAttestante"+
      ","+ALIAS+".cap_attestante as ImportExportRendicontazioneTesoreria_capAttestante"+
      ","+ALIAS+".localita_attestante as ImportExportRendicontazioneTesoreria_localitaAttestante"+
      ","+ALIAS+".provincia_attestante as ImportExportRendicontazioneTesoreria_provinciaAttestante"+
      ","+ALIAS+".nazione_attestante as ImportExportRendicontazioneTesoreria_nazioneAttestante"+
      ","+ALIAS+".tipo_identificativo_univoco_beneficiario as ImportExportRendicontazioneTesoreria_tipoIdentificativoUnivocoBeneficiario"+
      ","+ALIAS+".codice_identificativo_univoco_beneficiario as ImportExportRendicontazioneTesoreria_codiceIdentificativoUnivocoBeneficiario"+
      ","+ALIAS+".denominazione_beneficiario as ImportExportRendicontazioneTesoreria_denominazioneBeneficiario"+
      ","+ALIAS+".codice_unit_oper_beneficiario as ImportExportRendicontazioneTesoreria_codiceUnitOperBeneficiario"+
      ","+ALIAS+".denom_unit_oper_beneficiario as ImportExportRendicontazioneTesoreria_denomUnitOperBeneficiario"+
      ","+ALIAS+".indirizzo_beneficiario as ImportExportRendicontazioneTesoreria_indirizzoBeneficiario"+
      ","+ALIAS+".civico_beneficiario as ImportExportRendicontazioneTesoreria_civicoBeneficiario"+
      ","+ALIAS+".cap_beneficiario as ImportExportRendicontazioneTesoreria_capBeneficiario"+
      ","+ALIAS+".localita_beneficiario as ImportExportRendicontazioneTesoreria_localitaBeneficiario"+
      ","+ALIAS+".provincia_beneficiario as ImportExportRendicontazioneTesoreria_provinciaBeneficiario"+
      ","+ALIAS+".nazione_beneficiario as ImportExportRendicontazioneTesoreria_nazioneBeneficiario"+
      ","+ALIAS+".tipo_identificativo_univoco_versante as ImportExportRendicontazioneTesoreria_tipoIdentificativoUnivocoVersante"+
      ","+ALIAS+".codice_identificativo_univoco_versante as ImportExportRendicontazioneTesoreria_codiceIdentificativoUnivocoVersante"+
      ","+ALIAS+".anagrafica_versante as ImportExportRendicontazioneTesoreria_anagraficaVersante"+
      ","+ALIAS+".indirizzo_versante as ImportExportRendicontazioneTesoreria_indirizzoVersante"+
      ","+ALIAS+".civico_versante as ImportExportRendicontazioneTesoreria_civicoVersante"+
      ","+ALIAS+".cap_versante as ImportExportRendicontazioneTesoreria_capVersante"+
      ","+ALIAS+".localita_versante as ImportExportRendicontazioneTesoreria_localitaVersante"+
      ","+ALIAS+".provincia_versante as ImportExportRendicontazioneTesoreria_provinciaVersante"+
      ","+ALIAS+".nazione_versante as ImportExportRendicontazioneTesoreria_nazioneVersante"+
      ","+ALIAS+".email_versante as ImportExportRendicontazioneTesoreria_emailVersante"+
      ","+ALIAS+".tipo_identificativo_univoco_pagatore as ImportExportRendicontazioneTesoreria_tipoIdentificativoUnivocoPagatore"+
      ","+ALIAS+".codice_identificativo_univoco_pagatore as ImportExportRendicontazioneTesoreria_codiceIdentificativoUnivocoPagatore"+
      ","+ALIAS+".anagrafica_pagatore as ImportExportRendicontazioneTesoreria_anagraficaPagatore"+
      ","+ALIAS+".indirizzo_pagatore as ImportExportRendicontazioneTesoreria_indirizzoPagatore"+
      ","+ALIAS+".civico_pagatore as ImportExportRendicontazioneTesoreria_civicoPagatore"+
      ","+ALIAS+".cap_pagatore as ImportExportRendicontazioneTesoreria_capPagatore"+
      ","+ALIAS+".localita_pagatore as ImportExportRendicontazioneTesoreria_localitaPagatore"+
      ","+ALIAS+".provincia_pagatore as ImportExportRendicontazioneTesoreria_provinciaPagatore"+
      ","+ALIAS+".nazione_pagatore as ImportExportRendicontazioneTesoreria_nazionePagatore"+
      ","+ALIAS+".email_pagatore as ImportExportRendicontazioneTesoreria_emailPagatore"+
      ","+ALIAS+".codice_esito_pagamento as ImportExportRendicontazioneTesoreria_codiceEsitoPagamento"+
      ","+ALIAS+".importo_totale_pagato as ImportExportRendicontazioneTesoreria_importoTotalePagato"+
      ","+ALIAS+".identificativo_univoco_versamento as ImportExportRendicontazioneTesoreria_identificativoUnivocoVersamento"+
      ","+ALIAS+".codice_contesto_pagamento as ImportExportRendicontazioneTesoreria_codiceContestoPagamento"+
      ","+ALIAS+".singolo_importo_pagato as ImportExportRendicontazioneTesoreria_singoloImportoPagato"+
      ","+ALIAS+".esito_singolo_pagamento as ImportExportRendicontazioneTesoreria_esitoSingoloPagamento"+
      ","+ALIAS+".dt_data_esito_singolo_pagamento as ImportExportRendicontazioneTesoreria_dtDataEsitoSingoloPagamento"+
      ","+ALIAS+".de_data_esito_singolo_pagamento as ImportExportRendicontazioneTesoreria_deDataEsitoSingoloPagamento"+
      ","+ALIAS+".identificativo_univoco_riscossione as ImportExportRendicontazioneTesoreria_identificativoUnivocoRiscossione"+
      ","+ALIAS+".causale_versamento as ImportExportRendicontazioneTesoreria_causaleVersamento"+
      ","+ALIAS+".dati_specifici_riscossione as ImportExportRendicontazioneTesoreria_datiSpecificiRiscossione"+
      ","+ALIAS+".tipo_dovuto as ImportExportRendicontazioneTesoreria_tipoDovuto"+
      ","+ALIAS+".identificativo_flusso_rendicontazione as ImportExportRendicontazioneTesoreria_identificativoFlussoRendicontazione"+
      ","+ALIAS+".data_ora_flusso_rendicontazione as ImportExportRendicontazioneTesoreria_dataOraFlussoRendicontazione"+
      ","+ALIAS+".identificativo_univoco_regolamento as ImportExportRendicontazioneTesoreria_identificativoUnivocoRegolamento"+
      ","+ALIAS+".dt_data_regolamento as ImportExportRendicontazioneTesoreria_dtDataRegolamento"+
      ","+ALIAS+".de_data_regolamento as ImportExportRendicontazioneTesoreria_deDataRegolamento"+
      ","+ALIAS+".numero_totale_pagamenti as ImportExportRendicontazioneTesoreria_numeroTotalePagamenti"+
      ","+ALIAS+".importo_totale_pagamenti as ImportExportRendicontazioneTesoreria_importoTotalePagamenti"+
      ","+ALIAS+".data_acquisizione as ImportExportRendicontazioneTesoreria_dataAcquisizione"+
      ","+ALIAS+".cod_conto as ImportExportRendicontazioneTesoreria_codConto"+
      ","+ALIAS+".dt_data_contabile as ImportExportRendicontazioneTesoreria_dtDataContabile"+
      ","+ALIAS+".de_data_contabile as ImportExportRendicontazioneTesoreria_deDataContabile"+
      ","+ALIAS+".dt_data_valuta as ImportExportRendicontazioneTesoreria_dtDataValuta"+
      ","+ALIAS+".de_data_valuta as ImportExportRendicontazioneTesoreria_deDataValuta"+
      ","+ALIAS+".num_importo as ImportExportRendicontazioneTesoreria_numImporto"+
      ","+ALIAS+".de_importo as ImportExportRendicontazioneTesoreria_deImporto"+
      ","+ALIAS+".cod_or1 as ImportExportRendicontazioneTesoreria_codOr1"+
      ","+ALIAS+".de_anno_bolletta as ImportExportRendicontazioneTesoreria_deAnnoBolletta"+
      ","+ALIAS+".cod_bolletta as ImportExportRendicontazioneTesoreria_codBolletta"+
      ","+ALIAS+".cod_id_dominio as ImportExportRendicontazioneTesoreria_codIdDominio"+
      ","+ALIAS+".dt_ricezione as ImportExportRendicontazioneTesoreria_dtRicezione"+
      ","+ALIAS+".de_data_ricezione as ImportExportRendicontazioneTesoreria_deDataRicezione"+
      ","+ALIAS+".de_anno_documento as ImportExportRendicontazioneTesoreria_deAnnoDocumento"+
      ","+ALIAS+".cod_documento as ImportExportRendicontazioneTesoreria_codDocumento"+
      ","+ALIAS+".de_anno_provvisorio as ImportExportRendicontazioneTesoreria_deAnnoProvvisorio"+
      ","+ALIAS+".cod_provvisorio as ImportExportRendicontazioneTesoreria_codProvvisorio"+
      ","+ALIAS+".de_causale_t as ImportExportRendicontazioneTesoreria_deCausaleT"+
      ","+ALIAS+".verifica_totale as ImportExportRendicontazioneTesoreria_verificaTotale"+
      ","+ALIAS+".classificazione_completezza as ImportExportRendicontazioneTesoreria_classificazioneCompletezza"+
      ","+ALIAS+".dt_data_ultimo_aggiornamento as ImportExportRendicontazioneTesoreria_dtDataUltimoAggiornamento"+
      ","+ALIAS+".de_data_ultimo_aggiornamento as ImportExportRendicontazioneTesoreria_deDataUltimoAggiornamento"+
      ","+ALIAS+".indice_dati_singolo_pagamento as ImportExportRendicontazioneTesoreria_indiceDatiSingoloPagamento"+
      ","+ALIAS+".cod_iuf_key as ImportExportRendicontazioneTesoreria_codIufKey"+
      ","+ALIAS+".cod_iud_key as ImportExportRendicontazioneTesoreria_codIudKey"+
      ","+ALIAS+".cod_iuv_key as ImportExportRendicontazioneTesoreria_codIuvKey"+
      ","+ALIAS+".bilancio_e as ImportExportRendicontazioneTesoreria_bilancioE"+
      ","+ALIAS+".dt_effettiva_sospeso as ImportExportRendicontazioneTesoreria_dtEffettivaSospeso"+
      ","+ALIAS+".codice_gestionale_provvisorio as ImportExportRendicontazioneTesoreria_codiceGestionaleProvvisorio";

  private String codiceIpaEnte;
  private Timestamp dtDataEsecuzionePagamento;
  private String deDataEsecuzionePagamento;
  private String singoloImportoCommissioneCaricoPa;
  private String bilancio;
  private String nomeFlussoImportEnte;
  private String rigaFlussoImportEnte;
  private String codiceIud;
  private String codiceIuv;
  private String versioneOggetto;
  private String identificativoDominio;
  private String identificativoStazioneRichiedente;
  private String identificativoMessaggioRicevuta;
  private String dataOraMessaggioRicevuta;
  private String riferimentoMessaggioRichiesta;
  private String riferimentoDataRichiesta;
  private String tipoIdentificativoUnivocoAttestante;
  private String codiceIdentificativoUnivocoAttestante;
  private String denominazioneAttestante;
  private String codiceUnitOperAttestante;
  private String denomUnitOperAttestante;
  private String indirizzoAttestante;
  private String civicoAttestante;
  private String capAttestante;
  private String localitaAttestante;
  private String provinciaAttestante;
  private String nazioneAttestante;
  private String tipoIdentificativoUnivocoBeneficiario;
  private String codiceIdentificativoUnivocoBeneficiario;
  private String denominazioneBeneficiario;
  private String codiceUnitOperBeneficiario;
  private String denomUnitOperBeneficiario;
  private String indirizzoBeneficiario;
  private String civicoBeneficiario;
  private String capBeneficiario;
  private String localitaBeneficiario;
  private String provinciaBeneficiario;
  private String nazioneBeneficiario;
  private String tipoIdentificativoUnivocoVersante;
  private String codiceIdentificativoUnivocoVersante;
  private String anagraficaVersante;
  private String indirizzoVersante;
  private String civicoVersante;
  private String capVersante;
  private String localitaVersante;
  private String provinciaVersante;
  private String nazioneVersante;
  private String emailVersante;
  private String tipoIdentificativoUnivocoPagatore;
  private String codiceIdentificativoUnivocoPagatore;
  private String anagraficaPagatore;
  private String indirizzoPagatore;
  private String civicoPagatore;
  private String capPagatore;
  private String localitaPagatore;
  private String provinciaPagatore;
  private String nazionePagatore;
  private String emailPagatore;
  private String codiceEsitoPagamento;
  private String importoTotalePagato;
  private String identificativoUnivocoVersamento;
  private String codiceContestoPagamento;
  private String singoloImportoPagato;
  private String esitoSingoloPagamento;
  private Timestamp dtDataEsitoSingoloPagamento;
  private String deDataEsitoSingoloPagamento;
  private String identificativoUnivocoRiscossione;
  private String causaleVersamento;
  private String datiSpecificiRiscossione;
  private String tipoDovuto;
  private String identificativoFlussoRendicontazione;
  private String dataOraFlussoRendicontazione;
  private String identificativoUnivocoRegolamento;
  private Timestamp dtDataRegolamento;
  private String deDataRegolamento;
  private String numeroTotalePagamenti;
  private String importoTotalePagamenti;
  private String dataAcquisizione;
  private String codConto;
  private Timestamp dtDataContabile;
  private String deDataContabile;
  private Timestamp dtDataValuta;
  private String deDataValuta;
  private BigDecimal numImporto;
  private String deImporto;
  private String codOr1;
  private String deAnnoBolletta;
  private String codBolletta;
  private String codIdDominio;
  private Timestamp dtRicezione;
  private String deDataRicezione;
  private String deAnnoDocumento;
  private String codDocumento;
  private String deAnnoProvvisorio;
  private String codProvvisorio;
  private String deCausaleT;
  private String verificaTotale;
  private String classificazioneCompletezza;
  private Timestamp dtDataUltimoAggiornamento;
  private String deDataUltimoAggiornamento;
  private Integer indiceDatiSingoloPagamento;
  private String codIufKey;
  private String codIudKey;
  private String codIuvKey;
  private String bilancioE;
  private Timestamp dtEffettivaSospeso;
  private String codiceGestionaleProvvisorio;

  private Boolean hasSegnalazione;
}
