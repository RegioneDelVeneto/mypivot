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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jdbi.v3.core.mapper.Nested;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportRendicontazioneCompleta extends BaseEntity {

  public final static String ALIAS = "ExportRendicontazioneCompleta";
  public final static String FIELDS = ""+ALIAS+".mygov_ente_id_e as ExportRendicontazioneCompleta_mygovEnteIdE"+
      ","+ALIAS+".mygov_manage_flusso_id_e as ExportRendicontazioneCompleta_mygovManageFlussoIdE"+
      ","+ALIAS+".de_nome_flusso_e as ExportRendicontazioneCompleta_deNomeFlussoE"+
      ","+ALIAS+".num_riga_flusso_e as ExportRendicontazioneCompleta_numRigaFlussoE"+
      ","+ALIAS+".cod_iud_e as ExportRendicontazioneCompleta_codIudE"+
      ","+ALIAS+".cod_rp_silinviarp_id_univoco_versamento_e as ExportRendicontazioneCompleta_codRpSilinviarpIdUnivocoVersamentoE"+
      ","+ALIAS+".de_e_versione_oggetto_e as ExportRendicontazioneCompleta_deEVersioneOggettoE"+
      ","+ALIAS+".cod_e_dom_id_dominio_e as ExportRendicontazioneCompleta_codEDomIdDominioE"+
      ","+ALIAS+".cod_e_dom_id_stazione_richiedente_e as ExportRendicontazioneCompleta_codEDomIdStazioneRichiedenteE"+
      ","+ALIAS+".cod_e_id_messaggio_ricevuta_e as ExportRendicontazioneCompleta_codEIdMessaggioRicevutaE"+
      ","+ALIAS+".dt_e_data_ora_messaggio_ricevuta_e as ExportRendicontazioneCompleta_dtEDataOraMessaggioRicevutaE"+
      ","+ALIAS+".cod_e_riferimento_messaggio_richiesta_e as ExportRendicontazioneCompleta_codERiferimentoMessaggioRichiestaE"+
      ","+ALIAS+".dt_e_riferimento_data_richiesta_e as ExportRendicontazioneCompleta_dtERiferimentoDataRichiestaE"+
      ","+ALIAS+".cod_e_istit_att_id_univ_att_tipo_id_univoco_e as ExportRendicontazioneCompleta_codEIstitAttIdUnivAttTipoIdUnivocoE"+
      ","+ALIAS+".cod_e_istit_att_id_univ_att_codice_id_univoco_e as ExportRendicontazioneCompleta_codEIstitAttIdUnivAttCodiceIdUnivocoE"+
      ","+ALIAS+".de_e_istit_att_denominazione_attestante_e as ExportRendicontazioneCompleta_deEIstitAttDenominazioneAttestanteE"+
      ","+ALIAS+".cod_e_istit_att_codice_unit_oper_attestante_e as ExportRendicontazioneCompleta_codEIstitAttCodiceUnitOperAttestanteE"+
      ","+ALIAS+".de_e_istit_att_denom_unit_oper_attestante_e as ExportRendicontazioneCompleta_deEIstitAttDenomUnitOperAttestanteE"+
      ","+ALIAS+".de_e_istit_att_indirizzo_attestante_e as ExportRendicontazioneCompleta_deEIstitAttIndirizzoAttestanteE"+
      ","+ALIAS+".de_e_istit_att_civico_attestante_e as ExportRendicontazioneCompleta_deEIstitAttCivicoAttestanteE"+
      ","+ALIAS+".cod_e_istit_att_cap_attestante_e as ExportRendicontazioneCompleta_codEIstitAttCapAttestanteE"+
      ","+ALIAS+".de_e_istit_att_localita_attestante_e as ExportRendicontazioneCompleta_deEIstitAttLocalitaAttestanteE"+
      ","+ALIAS+".de_e_istit_att_provincia_attestante_e as ExportRendicontazioneCompleta_deEIstitAttProvinciaAttestanteE"+
      ","+ALIAS+".cod_e_istit_att_nazione_attestante_e as ExportRendicontazioneCompleta_codEIstitAttNazioneAttestanteE"+
      ","+ALIAS+".cod_e_ente_benef_id_univ_benef_tipo_id_univoco_e as ExportRendicontazioneCompleta_codEEnteBenefIdUnivBenefTipoIdUnivocoE"+
      ","+ALIAS+".cod_e_ente_benef_id_univ_benef_codice_id_univoco_e as ExportRendicontazioneCompleta_codEEnteBenefIdUnivBenefCodiceIdUnivocoE"+
      ","+ALIAS+".de_e_ente_benef_denominazione_beneficiario_e as ExportRendicontazioneCompleta_deEEnteBenefDenominazioneBeneficiarioE"+
      ","+ALIAS+".cod_e_ente_benef_codice_unit_oper_beneficiario_e as ExportRendicontazioneCompleta_codEEnteBenefCodiceUnitOperBeneficiarioE"+
      ","+ALIAS+".de_e_ente_benef_denom_unit_oper_beneficiario_e as ExportRendicontazioneCompleta_deEEnteBenefDenomUnitOperBeneficiarioE"+
      ","+ALIAS+".de_e_ente_benef_indirizzo_beneficiario_e as ExportRendicontazioneCompleta_deEEnteBenefIndirizzoBeneficiarioE"+
      ","+ALIAS+".de_e_ente_benef_civico_beneficiario_e as ExportRendicontazioneCompleta_deEEnteBenefCivicoBeneficiarioE"+
      ","+ALIAS+".cod_e_ente_benef_cap_beneficiario_e as ExportRendicontazioneCompleta_codEEnteBenefCapBeneficiarioE"+
      ","+ALIAS+".de_e_ente_benef_localita_beneficiario_e as ExportRendicontazioneCompleta_deEEnteBenefLocalitaBeneficiarioE"+
      ","+ALIAS+".de_e_ente_benef_provincia_beneficiario_e as ExportRendicontazioneCompleta_deEEnteBenefProvinciaBeneficiarioE"+
      ","+ALIAS+".cod_e_ente_benef_nazione_beneficiario_e as ExportRendicontazioneCompleta_codEEnteBenefNazioneBeneficiarioE"+
      ","+ALIAS+".cod_e_sogg_vers_id_univ_vers_tipo_id_univoco_e as ExportRendicontazioneCompleta_codESoggVersIdUnivVersTipoIdUnivocoE"+
      ","+ALIAS+".cod_e_sogg_vers_id_univ_vers_codice_id_univoco_e as ExportRendicontazioneCompleta_codESoggVersIdUnivVersCodiceIdUnivocoE"+
      ","+ALIAS+".cod_e_sogg_vers_anagrafica_versante_e as ExportRendicontazioneCompleta_codESoggVersAnagraficaVersanteE"+
      ","+ALIAS+".de_e_sogg_vers_indirizzo_versante_e as ExportRendicontazioneCompleta_deESoggVersIndirizzoVersanteE"+
      ","+ALIAS+".de_e_sogg_vers_civico_versante_e as ExportRendicontazioneCompleta_deESoggVersCivicoVersanteE"+
      ","+ALIAS+".cod_e_sogg_vers_cap_versante_e as ExportRendicontazioneCompleta_codESoggVersCapVersanteE"+
      ","+ALIAS+".de_e_sogg_vers_localita_versante_e as ExportRendicontazioneCompleta_deESoggVersLocalitaVersanteE"+
      ","+ALIAS+".de_e_sogg_vers_provincia_versante_e as ExportRendicontazioneCompleta_deESoggVersProvinciaVersanteE"+
      ","+ALIAS+".cod_e_sogg_vers_nazione_versante_e as ExportRendicontazioneCompleta_codESoggVersNazioneVersanteE"+
      ","+ALIAS+".de_e_sogg_vers_email_versante_e as ExportRendicontazioneCompleta_deESoggVersEmailVersanteE"+
      ","+ALIAS+".cod_e_sogg_pag_id_univ_pag_tipo_id_univoco_e as ExportRendicontazioneCompleta_codESoggPagIdUnivPagTipoIdUnivocoE"+
      ","+ALIAS+".cod_e_sogg_pag_id_univ_pag_codice_id_univoco_e as ExportRendicontazioneCompleta_codESoggPagIdUnivPagCodiceIdUnivocoE"+
      ","+ALIAS+".cod_e_sogg_pag_anagrafica_pagatore_e as ExportRendicontazioneCompleta_codESoggPagAnagraficaPagatoreE"+
      ","+ALIAS+".de_e_sogg_pag_indirizzo_pagatore_e as ExportRendicontazioneCompleta_deESoggPagIndirizzoPagatoreE"+
      ","+ALIAS+".de_e_sogg_pag_civico_pagatore_e as ExportRendicontazioneCompleta_deESoggPagCivicoPagatoreE"+
      ","+ALIAS+".cod_e_sogg_pag_cap_pagatore_e as ExportRendicontazioneCompleta_codESoggPagCapPagatoreE"+
      ","+ALIAS+".de_e_sogg_pag_localita_pagatore_e as ExportRendicontazioneCompleta_deESoggPagLocalitaPagatoreE"+
      ","+ALIAS+".de_e_sogg_pag_provincia_pagatore_e as ExportRendicontazioneCompleta_deESoggPagProvinciaPagatoreE"+
      ","+ALIAS+".cod_e_sogg_pag_nazione_pagatore_e as ExportRendicontazioneCompleta_codESoggPagNazionePagatoreE"+
      ","+ALIAS+".de_e_sogg_pag_email_pagatore_e as ExportRendicontazioneCompleta_deESoggPagEmailPagatoreE"+
      ","+ALIAS+".cod_e_dati_pag_codice_esito_pagamento_e as ExportRendicontazioneCompleta_codEDatiPagCodiceEsitoPagamentoE"+
      ","+ALIAS+".num_e_dati_pag_importo_totale_pagato_e as ExportRendicontazioneCompleta_numEDatiPagImportoTotalePagatoE"+
      ","+ALIAS+".cod_e_dati_pag_id_univoco_versamento_e as ExportRendicontazioneCompleta_codEDatiPagIdUnivocoVersamentoE"+
      ","+ALIAS+".cod_e_dati_pag_codice_contesto_pagamento_e as ExportRendicontazioneCompleta_codEDatiPagCodiceContestoPagamentoE"+
      ","+ALIAS+".num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e as ExportRendicontazioneCompleta_numEDatiPagDatiSingPagSingoloImportoPagatoE"+
      ","+ALIAS+".de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento_e as ExportRendicontazioneCompleta_deEDatiPagDatiSingPagEsitoSingoloPagamentoE"+
      ","+ALIAS+".dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento_e as ExportRendicontazioneCompleta_dtEDatiPagDatiSingPagDataEsitoSingoloPagamentoE"+
      ","+ALIAS+".cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss_e as ExportRendicontazioneCompleta_codEDatiPagDatiSingPagIdUnivocoRiscossE"+
      ","+ALIAS+".de_e_dati_pag_dati_sing_pag_causale_versamento_e as ExportRendicontazioneCompleta_deEDatiPagDatiSingPagCausaleVersamentoE"+
      ","+ALIAS+".de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione_e as ExportRendicontazioneCompleta_deEDatiPagDatiSingPagDatiSpecificiRiscossioneE"+
      ","+ALIAS+".cod_tipo_dovuto_e as ExportRendicontazioneCompleta_codTipoDovutoE"+
      ","+ALIAS+".dt_acquisizione_e as ExportRendicontazioneCompleta_dtAcquisizioneE"+
      ","+ALIAS+".mygov_ente_id_r as ExportRendicontazioneCompleta_mygovEnteIdR"+
      ","+ALIAS+".mygov_manage_flusso_id_r as ExportRendicontazioneCompleta_mygovManageFlussoIdR"+
      ","+ALIAS+".versione_oggetto_r as ExportRendicontazioneCompleta_versioneOggettoR"+
      ","+ALIAS+".cod_identificativo_flusso_r as ExportRendicontazioneCompleta_codIdentificativoFlussoR"+
      ","+ALIAS+".dt_data_ora_flusso_r as ExportRendicontazioneCompleta_dtDataOraFlussoR"+
      ","+ALIAS+".cod_identificativo_univoco_regolamento_r as ExportRendicontazioneCompleta_codIdentificativoUnivocoRegolamentoR"+
      ","+ALIAS+".dt_data_regolamento_r as ExportRendicontazioneCompleta_dtDataRegolamentoR"+
      ","+ALIAS+".cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco_r as ExportRendicontazioneCompleta_codIstMittIdUnivMittTipoIdentificativoUnivocoR"+
      ","+ALIAS+".cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco_r as ExportRendicontazioneCompleta_codIstMittIdUnivMittCodiceIdentificativoUnivocoR"+
      ","+ALIAS+".de_ist_mitt_denominazione_mittente_r as ExportRendicontazioneCompleta_deIstMittDenominazioneMittenteR"+
      ","+ALIAS+".cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco_r as ExportRendicontazioneCompleta_codIstRicevIdUnivRicevTipoIdentificativoUnivocoR"+
      ","+ALIAS+".cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco_r as ExportRendicontazioneCompleta_codIstRicevIdUnivRicevCodiceIdentificativoUnivocoR"+
      ","+ALIAS+".de_ist_ricev_denominazione_ricevente_r as ExportRendicontazioneCompleta_deIstRicevDenominazioneRiceventeR"+
      ","+ALIAS+".num_numero_totale_pagamenti_r as ExportRendicontazioneCompleta_numNumeroTotalePagamentiR"+
      ","+ALIAS+".num_importo_totale_pagamenti_r as ExportRendicontazioneCompleta_numImportoTotalePagamentiR"+
      ","+ALIAS+".cod_dati_sing_pagam_identificativo_univoco_versamento_r as ExportRendicontazioneCompleta_codDatiSingPagamIdentificativoUnivocoVersamentoR"+
      ","+ALIAS+".cod_dati_sing_pagam_identificativo_univoco_riscossione_r as ExportRendicontazioneCompleta_codDatiSingPagamIdentificativoUnivocoRiscossioneR"+
      ","+ALIAS+".num_dati_sing_pagam_singolo_importo_pagato_r as ExportRendicontazioneCompleta_numDatiSingPagamSingoloImportoPagatoR"+
      ","+ALIAS+".cod_dati_sing_pagam_codice_esito_singolo_pagamento_r as ExportRendicontazioneCompleta_codDatiSingPagamCodiceEsitoSingoloPagamentoR"+
      ","+ALIAS+".dt_dati_sing_pagam_data_esito_singolo_pagamento_r as ExportRendicontazioneCompleta_dtDatiSingPagamDataEsitoSingoloPagamentoR"+
      ","+ALIAS+".dt_acquisizione_r as ExportRendicontazioneCompleta_dtAcquisizioneR"+
      ","+ALIAS+".classificazione_completezza as ExportRendicontazioneCompleta_classificazioneCompletezza";

  @Nested(Ente.ALIAS)
  private Ente mygovEnteIdE;
  @Nested(ManageFlusso.ALIAS)
  private ManageFlusso mygovManageFlussoIdE;
  private String deNomeFlussoE;
  private Integer numRigaFlussoE;
  private String codIudE;
  private String codRpSilinviarpIdUnivocoVersamentoE;
  private String deEVersioneOggettoE;
  private String codEDomIdDominioE;
  private String codEDomIdStazioneRichiedenteE;
  private String codEIdMessaggioRicevutaE;
  private Date dtEDataOraMessaggioRicevutaE;
  private String codERiferimentoMessaggioRichiestaE;
  private Date dtERiferimentoDataRichiestaE;
  private Character codEIstitAttIdUnivAttTipoIdUnivocoE;
  private String codEIstitAttIdUnivAttCodiceIdUnivocoE;
  private String deEIstitAttDenominazioneAttestanteE;
  private String codEIstitAttCodiceUnitOperAttestanteE;
  private String deEIstitAttDenomUnitOperAttestanteE;
  private String deEIstitAttIndirizzoAttestanteE;
  private String deEIstitAttCivicoAttestanteE;
  private String codEIstitAttCapAttestanteE;
  private String deEIstitAttLocalitaAttestanteE;
  private String deEIstitAttProvinciaAttestanteE;
  private String codEIstitAttNazioneAttestanteE;
  private Character codEEnteBenefIdUnivBenefTipoIdUnivocoE;
  private String codEEnteBenefIdUnivBenefCodiceIdUnivocoE;
  private String deEEnteBenefDenominazioneBeneficiarioE;
  private String codEEnteBenefCodiceUnitOperBeneficiarioE;
  private String deEEnteBenefDenomUnitOperBeneficiarioE;
  private String deEEnteBenefIndirizzoBeneficiarioE;
  private String deEEnteBenefCivicoBeneficiarioE;
  private String codEEnteBenefCapBeneficiarioE;
  private String deEEnteBenefLocalitaBeneficiarioE;
  private String deEEnteBenefProvinciaBeneficiarioE;
  private String codEEnteBenefNazioneBeneficiarioE;
  private Character codESoggVersIdUnivVersTipoIdUnivocoE;
  private String codESoggVersIdUnivVersCodiceIdUnivocoE;
  private String codESoggVersAnagraficaVersanteE;
  private String deESoggVersIndirizzoVersanteE;
  private String deESoggVersCivicoVersanteE;
  private String codESoggVersCapVersanteE;
  private String deESoggVersLocalitaVersanteE;
  private String deESoggVersProvinciaVersanteE;
  private String codESoggVersNazioneVersanteE;
  private String deESoggVersEmailVersanteE;
  private Character codESoggPagIdUnivPagTipoIdUnivocoE;
  private String codESoggPagIdUnivPagCodiceIdUnivocoE;
  private String codESoggPagAnagraficaPagatoreE;
  private String deESoggPagIndirizzoPagatoreE;
  private String deESoggPagCivicoPagatoreE;
  private String codESoggPagCapPagatoreE;
  private String deESoggPagLocalitaPagatoreE;
  private String deESoggPagProvinciaPagatoreE;
  private String codESoggPagNazionePagatoreE;
  private String deESoggPagEmailPagatoreE;
  private Character codEDatiPagCodiceEsitoPagamentoE;
  private BigDecimal numEDatiPagImportoTotalePagatoE;
  private String codEDatiPagIdUnivocoVersamentoE;
  private String codEDatiPagCodiceContestoPagamentoE;
  private BigDecimal numEDatiPagDatiSingPagSingoloImportoPagatoE;
  private String deEDatiPagDatiSingPagEsitoSingoloPagamentoE;
  private Date dtEDatiPagDatiSingPagDataEsitoSingoloPagamentoE;
  private String codEDatiPagDatiSingPagIdUnivocoRiscossE;
  private String deEDatiPagDatiSingPagCausaleVersamentoE;
  private String deEDatiPagDatiSingPagDatiSpecificiRiscossioneE;
  private String codTipoDovutoE;
  private Date dtAcquisizioneE;
  @Nested(Ente.ALIAS)
  private Ente mygovEnteIdR;
  @Nested(ManageFlusso.ALIAS)
  private ManageFlusso mygovManageFlussoIdR;
  private String versioneOggettoR;
  private String codIdentificativoFlussoR;
  private Date dtDataOraFlussoR;
  private String codIdentificativoUnivocoRegolamentoR;
  private Date dtDataRegolamentoR;
  private Character codIstMittIdUnivMittTipoIdentificativoUnivocoR;
  private String codIstMittIdUnivMittCodiceIdentificativoUnivocoR;
  private String deIstMittDenominazioneMittenteR;
  private Character codIstRicevIdUnivRicevTipoIdentificativoUnivocoR;
  private String codIstRicevIdUnivRicevCodiceIdentificativoUnivocoR;
  private String deIstRicevDenominazioneRiceventeR;
  private Long numNumeroTotalePagamentiR;
  private BigDecimal numImportoTotalePagamentiR;
  private String codDatiSingPagamIdentificativoUnivocoVersamentoR;
  private String codDatiSingPagamIdentificativoUnivocoRiscossioneR;
  private BigDecimal numDatiSingPagamSingoloImportoPagatoR;
  private String codDatiSingPagamCodiceEsitoSingoloPagamentoR;
  private Date dtDatiSingPagamDataEsitoSingoloPagamentoR;
  private Date dtAcquisizioneR;
  private String classificazioneCompletezza;
}
