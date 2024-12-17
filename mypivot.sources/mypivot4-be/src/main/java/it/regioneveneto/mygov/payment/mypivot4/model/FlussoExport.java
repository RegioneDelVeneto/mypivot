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
public class FlussoExport extends BaseEntity {

  public final static String ALIAS = "FlussoExport";
  public final static String FIELDS = ""+ALIAS+".version as FlussoExport_version,"+ALIAS+".dt_creazione as FlussoExport_dtCreazione"+
      ","+ALIAS+".dt_ultima_modifica as FlussoExport_dtUltimaModifica,"+ALIAS+".mygov_ente_id as FlussoExport_mygovEnteId"+
      ","+ALIAS+".mygov_manage_flusso_id as FlussoExport_mygovManageFlussoId"+
      ","+ALIAS+".de_nome_flusso as FlussoExport_deNomeFlusso,"+ALIAS+".num_riga_flusso as FlussoExport_numRigaFlusso"+
      ","+ALIAS+".cod_iud as FlussoExport_codIud"+
      ","+ALIAS+".cod_rp_silinviarp_id_univoco_versamento as FlussoExport_codRpSilinviarpIdUnivocoVersamento"+
      ","+ALIAS+".de_e_versione_oggetto as FlussoExport_deEVersioneOggetto"+
      ","+ALIAS+".cod_e_dom_id_dominio as FlussoExport_codEDomIdDominio"+
      ","+ALIAS+".cod_e_dom_id_stazione_richiedente as FlussoExport_codEDomIdStazioneRichiedente"+
      ","+ALIAS+".cod_e_id_messaggio_ricevuta as FlussoExport_codEIdMessaggioRicevuta"+
      ","+ALIAS+".dt_e_data_ora_messaggio_ricevuta as FlussoExport_dtEDataOraMessaggioRicevuta"+
      ","+ALIAS+".cod_e_riferimento_messaggio_richiesta as FlussoExport_codERiferimentoMessaggioRichiesta"+
      ","+ALIAS+".dt_e_riferimento_data_richiesta as FlussoExport_dtERiferimentoDataRichiesta"+
      ","+ALIAS+".cod_e_istit_att_id_univ_att_tipo_id_univoco as FlussoExport_codEIstitAttIdUnivAttTipoIdUnivoco"+
      ","+ALIAS+".cod_e_istit_att_id_univ_att_codice_id_univoco as FlussoExport_codEIstitAttIdUnivAttCodiceIdUnivoco"+
      ","+ALIAS+".de_e_istit_att_denominazione_attestante as FlussoExport_deEIstitAttDenominazioneAttestante"+
      ","+ALIAS+".cod_e_istit_att_codice_unit_oper_attestante as FlussoExport_codEIstitAttCodiceUnitOperAttestante"+
      ","+ALIAS+".de_e_istit_att_denom_unit_oper_attestante as FlussoExport_deEIstitAttDenomUnitOperAttestante"+
      ","+ALIAS+".de_e_istit_att_indirizzo_attestante as FlussoExport_deEIstitAttIndirizzoAttestante"+
      ","+ALIAS+".de_e_istit_att_civico_attestante as FlussoExport_deEIstitAttCivicoAttestante"+
      ","+ALIAS+".cod_e_istit_att_cap_attestante as FlussoExport_codEIstitAttCapAttestante"+
      ","+ALIAS+".de_e_istit_att_localita_attestante as FlussoExport_deEIstitAttLocalitaAttestante"+
      ","+ALIAS+".de_e_istit_att_provincia_attestante as FlussoExport_deEIstitAttProvinciaAttestante"+
      ","+ALIAS+".cod_e_istit_att_nazione_attestante as FlussoExport_codEIstitAttNazioneAttestante"+
      ","+ALIAS+".cod_e_ente_benef_id_univ_benef_tipo_id_univoco as FlussoExport_codEEnteBenefIdUnivBenefTipoIdUnivoco"+
      ","+ALIAS+".cod_e_ente_benef_id_univ_benef_codice_id_univoco as FlussoExport_codEEnteBenefIdUnivBenefCodiceIdUnivoco"+
      ","+ALIAS+".de_e_ente_benef_denominazione_beneficiario as FlussoExport_deEEnteBenefDenominazioneBeneficiario"+
      ","+ALIAS+".cod_e_ente_benef_codice_unit_oper_beneficiario as FlussoExport_codEEnteBenefCodiceUnitOperBeneficiario"+
      ","+ALIAS+".de_e_ente_benef_denom_unit_oper_beneficiario as FlussoExport_deEEnteBenefDenomUnitOperBeneficiario"+
      ","+ALIAS+".de_e_ente_benef_indirizzo_beneficiario as FlussoExport_deEEnteBenefIndirizzoBeneficiario"+
      ","+ALIAS+".de_e_ente_benef_civico_beneficiario as FlussoExport_deEEnteBenefCivicoBeneficiario"+
      ","+ALIAS+".cod_e_ente_benef_cap_beneficiario as FlussoExport_codEEnteBenefCapBeneficiario"+
      ","+ALIAS+".de_e_ente_benef_localita_beneficiario as FlussoExport_deEEnteBenefLocalitaBeneficiario"+
      ","+ALIAS+".de_e_ente_benef_provincia_beneficiario as FlussoExport_deEEnteBenefProvinciaBeneficiario"+
      ","+ALIAS+".cod_e_ente_benef_nazione_beneficiario as FlussoExport_codEEnteBenefNazioneBeneficiario"+
      ","+ALIAS+".cod_e_sogg_vers_id_univ_vers_tipo_id_univoco as FlussoExport_codESoggVersIdUnivVersTipoIdUnivoco"+
      ","+ALIAS+".cod_e_sogg_vers_id_univ_vers_codice_id_univoco as FlussoExport_codESoggVersIdUnivVersCodiceIdUnivoco"+
      ","+ALIAS+".cod_e_sogg_vers_anagrafica_versante as FlussoExport_codESoggVersAnagraficaVersante"+
      ","+ALIAS+".de_e_sogg_vers_indirizzo_versante as FlussoExport_deESoggVersIndirizzoVersante"+
      ","+ALIAS+".de_e_sogg_vers_civico_versante as FlussoExport_deESoggVersCivicoVersante"+
      ","+ALIAS+".cod_e_sogg_vers_cap_versante as FlussoExport_codESoggVersCapVersante"+
      ","+ALIAS+".de_e_sogg_vers_localita_versante as FlussoExport_deESoggVersLocalitaVersante"+
      ","+ALIAS+".de_e_sogg_vers_provincia_versante as FlussoExport_deESoggVersProvinciaVersante"+
      ","+ALIAS+".cod_e_sogg_vers_nazione_versante as FlussoExport_codESoggVersNazioneVersante"+
      ","+ALIAS+".de_e_sogg_vers_email_versante as FlussoExport_deESoggVersEmailVersante"+
      ","+ALIAS+".cod_e_sogg_pag_id_univ_pag_tipo_id_univoco as FlussoExport_codESoggPagIdUnivPagTipoIdUnivoco"+
      ","+ALIAS+".cod_e_sogg_pag_id_univ_pag_codice_id_univoco as FlussoExport_codESoggPagIdUnivPagCodiceIdUnivoco"+
      ","+ALIAS+".cod_e_sogg_pag_anagrafica_pagatore as FlussoExport_codESoggPagAnagraficaPagatore"+
      ","+ALIAS+".de_e_sogg_pag_indirizzo_pagatore as FlussoExport_deESoggPagIndirizzoPagatore"+
      ","+ALIAS+".de_e_sogg_pag_civico_pagatore as FlussoExport_deESoggPagCivicoPagatore"+
      ","+ALIAS+".cod_e_sogg_pag_cap_pagatore as FlussoExport_codESoggPagCapPagatore"+
      ","+ALIAS+".de_e_sogg_pag_localita_pagatore as FlussoExport_deESoggPagLocalitaPagatore"+
      ","+ALIAS+".de_e_sogg_pag_provincia_pagatore as FlussoExport_deESoggPagProvinciaPagatore"+
      ","+ALIAS+".cod_e_sogg_pag_nazione_pagatore as FlussoExport_codESoggPagNazionePagatore"+
      ","+ALIAS+".de_e_sogg_pag_email_pagatore as FlussoExport_deESoggPagEmailPagatore"+
      ","+ALIAS+".cod_e_dati_pag_codice_esito_pagamento as FlussoExport_codEDatiPagCodiceEsitoPagamento"+
      ","+ALIAS+".num_e_dati_pag_importo_totale_pagato as FlussoExport_numEDatiPagImportoTotalePagato"+
      ","+ALIAS+".cod_e_dati_pag_id_univoco_versamento as FlussoExport_codEDatiPagIdUnivocoVersamento"+
      ","+ALIAS+".cod_e_dati_pag_codice_contesto_pagamento as FlussoExport_codEDatiPagCodiceContestoPagamento"+
      ","+ALIAS+".num_e_dati_pag_dati_sing_pag_singolo_importo_pagato as FlussoExport_numEDatiPagDatiSingPagSingoloImportoPagato"+
      ","+ALIAS+".de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento as FlussoExport_deEDatiPagDatiSingPagEsitoSingoloPagamento"+
      ","+ALIAS+".dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento as FlussoExport_dtEDatiPagDatiSingPagDataEsitoSingoloPagamento"+
      ","+ALIAS+".cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss as FlussoExport_codEDatiPagDatiSingPagIdUnivocoRiscoss"+
      ","+ALIAS+".de_e_dati_pag_dati_sing_pag_causale_versamento as FlussoExport_deEDatiPagDatiSingPagCausaleVersamento"+
      ","+ALIAS+".de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione as FlussoExport_deEDatiPagDatiSingPagDatiSpecificiRiscossione"+
      ","+ALIAS+".cod_tipo_dovuto as FlussoExport_codTipoDovuto,"+ALIAS+".dt_acquisizione as FlussoExport_dtAcquisizione"+
      ","+ALIAS+".indice_dati_singolo_pagamento as FlussoExport_indiceDatiSingoloPagamento"+
      ","+ALIAS+".de_importa_dovuto_esito as FlussoExport_deImportaDovutoEsito"+
      ","+ALIAS+".de_importa_dovuto_fault_code as FlussoExport_deImportaDovutoFaultCode"+
      ","+ALIAS+".de_importa_dovuto_fault_string as FlussoExport_deImportaDovutoFaultString"+
      ","+ALIAS+".de_importa_dovuto_fault_id as FlussoExport_deImportaDovutoFaultId"+
      ","+ALIAS+".de_importa_dovuto_fault_description as FlussoExport_deImportaDovutoFaultDescription"+
      ","+ALIAS+".num_importa_dovuto_fault_serial as FlussoExport_numImportaDovutoFaultSerial"+
      ","+ALIAS+".bilancio as FlussoExport_bilancio,"+ALIAS+".id_intermediario_pa as FlussoExport_idIntermediarioPa"+
      ","+ALIAS+".id_stazione_intermediario_pa as FlussoExport_idStazioneIntermediarioPa"+
      ","+ALIAS+".cod_tipo_dovuto_pa1 as FlussoExport_codTipoDovutoPa1"+
      ","+ALIAS+".de_tipo_dovuto_pa1 as FlussoExport_deTipoDovutoPa1"+
      ","+ALIAS+".cod_tassonomico_dovuto_pa1 as FlussoExport_codTassonomicoDovutoPa1"+
      ","+ALIAS+".cod_fiscale_pa1 as FlussoExport_codFiscalePa1"+
      ","+ALIAS+".de_nome_pa1 as FlussoExport_deNomePa1";

  private int version;
  private Date dtCreazione;
  private Date dtUltimaModifica;
  @Nested(Ente.ALIAS)
  private Ente mygovEnteId;
  @Nested(ManageFlusso.ALIAS)
  private ManageFlusso mygovManageFlussoId;
  private String deNomeFlusso;
  private Integer numRigaFlusso;
  private String codIud;
  private String codRpSilinviarpIdUnivocoVersamento;
  private String deEVersioneOggetto;
  private String codEDomIdDominio;
  private String codEDomIdStazioneRichiedente;
  private String codEIdMessaggioRicevuta;
  private Date dtEDataOraMessaggioRicevuta;
  private String codERiferimentoMessaggioRichiesta;
  private Date dtERiferimentoDataRichiesta;
  private Character codEIstitAttIdUnivAttTipoIdUnivoco;
  private String codEIstitAttIdUnivAttCodiceIdUnivoco;
  private String deEIstitAttDenominazioneAttestante;
  private String codEIstitAttCodiceUnitOperAttestante;
  private String deEIstitAttDenomUnitOperAttestante;
  private String deEIstitAttIndirizzoAttestante;
  private String deEIstitAttCivicoAttestante;
  private String codEIstitAttCapAttestante;
  private String deEIstitAttLocalitaAttestante;
  private String deEIstitAttProvinciaAttestante;
  private String codEIstitAttNazioneAttestante;
  private Character codEEnteBenefIdUnivBenefTipoIdUnivoco;
  private String codEEnteBenefIdUnivBenefCodiceIdUnivoco;
  private String deEEnteBenefDenominazioneBeneficiario;
  private String codEEnteBenefCodiceUnitOperBeneficiario;
  private String deEEnteBenefDenomUnitOperBeneficiario;
  private String deEEnteBenefIndirizzoBeneficiario;
  private String deEEnteBenefCivicoBeneficiario;
  private String codEEnteBenefCapBeneficiario;
  private String deEEnteBenefLocalitaBeneficiario;
  private String deEEnteBenefProvinciaBeneficiario;
  private String codEEnteBenefNazioneBeneficiario;
  private Character codESoggVersIdUnivVersTipoIdUnivoco;
  private String codESoggVersIdUnivVersCodiceIdUnivoco;
  private String codESoggVersAnagraficaVersante;
  private String deESoggVersIndirizzoVersante;
  private String deESoggVersCivicoVersante;
  private String codESoggVersCapVersante;
  private String deESoggVersLocalitaVersante;
  private String deESoggVersProvinciaVersante;
  private String codESoggVersNazioneVersante;
  private String deESoggVersEmailVersante;
  private Character codESoggPagIdUnivPagTipoIdUnivoco;
  private String codESoggPagIdUnivPagCodiceIdUnivoco;
  private String codESoggPagAnagraficaPagatore;
  private String deESoggPagIndirizzoPagatore;
  private String deESoggPagCivicoPagatore;
  private String codESoggPagCapPagatore;
  private String deESoggPagLocalitaPagatore;
  private String deESoggPagProvinciaPagatore;
  private String codESoggPagNazionePagatore;
  private String deESoggPagEmailPagatore;
  private Character codEDatiPagCodiceEsitoPagamento;
  private BigDecimal numEDatiPagImportoTotalePagato;
  private String codEDatiPagIdUnivocoVersamento;
  private String codEDatiPagCodiceContestoPagamento;
  private BigDecimal numEDatiPagDatiSingPagSingoloImportoPagato;
  private String deEDatiPagDatiSingPagEsitoSingoloPagamento;
  private Date dtEDatiPagDatiSingPagDataEsitoSingoloPagamento;
  private String codEDatiPagDatiSingPagIdUnivocoRiscoss;
  private String deEDatiPagDatiSingPagCausaleVersamento;
  private String deEDatiPagDatiSingPagDatiSpecificiRiscossione;
  private String codTipoDovuto;
  private Date dtAcquisizione;
  private int indiceDatiSingoloPagamento;
  private String deImportaDovutoEsito;
  private String deImportaDovutoFaultCode;
  private String deImportaDovutoFaultString;
  private String deImportaDovutoFaultId;
  private String deImportaDovutoFaultDescription;
  private Integer numImportaDovutoFaultSerial;
  private String bilancio;
  private String idIntermediarioPa;
  private String idStazioneIntermediarioPa;
  private String codTipoDovutoPa1;
  private String deTipoDovutoPa1;
  private String codTassonomicoDovutoPa1;
  private String codFiscalePa1;
  private String deNomePa1;
}
