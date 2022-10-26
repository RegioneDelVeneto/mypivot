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

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccertamentoPagamentoInseribile extends BaseEntity {

  public final static String ALIAS = "AccertamentoPagamentoInseribile";
  public final static String FIELDS = ""+ALIAS+".cod_tipo_dovuto as AccertamentoPagamentoInseribile_codTipoDovuto"+
      ","+ALIAS+".de_tipo_dovuto as AccertamentoPagamentoInseribile_deTipoDovuto"+
      ","+ALIAS+".cod_iud as AccertamentoPagamentoInseribile_codIud"+
      ","+ALIAS+".cod_rp_silinviarp_id_univoco_versamento as AccertamentoPagamentoInseribile_codRpSilinviarpIdUnivocoVersamento"+
      ","+ALIAS+".cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss as AccertamentoPagamentoInseribile_codEDatiPagDatiSingPagIdUnivocoRiscoss"+
      ","+ALIAS+".de_e_istit_att_denominazione_attestante as AccertamentoPagamentoInseribile_deEIstitAttDenominazioneAttestante"+
      ","+ALIAS+".cod_e_istit_att_id_univ_att_codice_id_univoco as AccertamentoPagamentoInseribile_codEIstitAttIdUnivAttCodiceIdUnivoco"+
      ","+ALIAS+".cod_e_istit_att_id_univ_att_tipo_id_univoco as AccertamentoPagamentoInseribile_codEIstitAttIdUnivAttTipoIdUnivoco"+
      ","+ALIAS+".cod_e_sogg_vers_anagrafica_versante as AccertamentoPagamentoInseribile_codESoggVersAnagraficaVersante"+
      ","+ALIAS+".cod_e_sogg_vers_id_univ_vers_codice_id_univoco as AccertamentoPagamentoInseribile_codESoggVersIdUnivVersCodiceIdUnivoco"+
      ","+ALIAS+".cod_e_sogg_vers_id_univ_vers_tipo_id_univoco as AccertamentoPagamentoInseribile_codESoggVersIdUnivVersTipoIdUnivoco"+
      ","+ALIAS+".cod_e_sogg_pag_anagrafica_pagatore as AccertamentoPagamentoInseribile_codESoggPagAnagraficaPagatore"+
      ","+ALIAS+".cod_e_sogg_pag_id_univ_pag_codice_id_univoco as AccertamentoPagamentoInseribile_codESoggPagIdUnivPagCodiceIdUnivoco"+
      ","+ALIAS+".cod_e_sogg_pag_id_univ_pag_tipo_id_univoco as AccertamentoPagamentoInseribile_codESoggPagIdUnivPagTipoIdUnivoco"+
      ","+ALIAS+".dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento as AccertamentoPagamentoInseribile_dtEDatiPagDatiSingPagDataEsitoSingoloPagamento"+
      ","+ALIAS+".dt_ultima_modifica as AccertamentoPagamentoInseribile_dtUltimaModifica"+
      ","+ALIAS+".num_e_dati_pag_dati_sing_pag_singolo_importo_pagato as AccertamentoPagamentoInseribile_numEDatiPagDatiSingPagSingoloImportoPagato"+
      ","+ALIAS+".de_e_dati_pag_dati_sing_pag_causale_versamento as AccertamentoPagamentoInseribile_deEDatiPagDatiSingPagCausaleVersamento";

  private String codTipoDovuto;
  private String deTipoDovuto;
  private String codIud;
  private String codRpSilinviarpIdUnivocoVersamento;
  private String codEDatiPagDatiSingPagIdUnivocoRiscoss;
  private String deEIstitAttDenominazioneAttestante;
  private String codEIstitAttIdUnivAttCodiceIdUnivoco;
  private String codEIstitAttIdUnivAttTipoIdUnivoco;
  private String codESoggVersAnagraficaVersante;
  private String codESoggVersIdUnivVersCodiceIdUnivoco;
  private String codESoggVersIdUnivVersTipoIdUnivoco;
  private String codESoggPagAnagraficaPagatore;
  private String codESoggPagIdUnivPagCodiceIdUnivoco;
  private String codESoggPagIdUnivPagTipoIdUnivoco;
  private Date dtEDatiPagDatiSingPagDataEsitoSingoloPagamento;
  private Date dtUltimaModifica;
  private BigDecimal numEDatiPagDatiSingPagSingoloImportoPagato;
  private String deEDatiPagDatiSingPagCausaleVersamento;
}
