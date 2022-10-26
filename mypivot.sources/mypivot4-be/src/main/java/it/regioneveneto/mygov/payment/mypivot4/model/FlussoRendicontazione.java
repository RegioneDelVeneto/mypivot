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
public class FlussoRendicontazione extends BaseEntity {

  public final static String ALIAS = "FlussoRendicontazione";
  public final static String FIELDS = ""+ALIAS+".version as FlussoRendicontazione_version,"+ALIAS+".dt_creazione as FlussoRendicontazione_dtCreazione"+
      ","+ALIAS+".dt_ultima_modifica as FlussoRendicontazione_dtUltimaModifica"+
      ","+ALIAS+".mygov_ente_id as FlussoRendicontazione_mygovEnteId"+
      ","+ALIAS+".mygov_manage_flusso_id as FlussoRendicontazione_mygovManageFlussoId"+
      ","+ALIAS+".identificativo_psp as FlussoRendicontazione_identificativoPsp"+
      ","+ALIAS+".versione_oggetto as FlussoRendicontazione_versioneOggetto"+
      ","+ALIAS+".cod_identificativo_flusso as FlussoRendicontazione_codIdentificativoFlusso"+
      ","+ALIAS+".dt_data_ora_flusso as FlussoRendicontazione_dtDataOraFlusso"+
      ","+ALIAS+".cod_identificativo_univoco_regolamento as FlussoRendicontazione_codIdentificativoUnivocoRegolamento"+
      ","+ALIAS+".dt_data_regolamento as FlussoRendicontazione_dtDataRegolamento"+
      ","+ALIAS+".cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco as FlussoRendicontazione_codIstMittIdUnivMittTipoIdentificativoUnivoco"+
      ","+ALIAS+".cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco as FlussoRendicontazione_codIstMittIdUnivMittCodiceIdentificativoUnivoco"+
      ","+ALIAS+".de_ist_mitt_denominazione_mittente as FlussoRendicontazione_deIstMittDenominazioneMittente"+
      ","+ALIAS+".cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco as FlussoRendicontazione_codIstRicevIdUnivRicevTipoIdentificativoUnivoco"+
      ","+ALIAS+".cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco as FlussoRendicontazione_codIstRicevIdUnivRicevCodiceIdentificativoUnivoco"+
      ","+ALIAS+".de_ist_ricev_denominazione_ricevente as FlussoRendicontazione_deIstRicevDenominazioneRicevente"+
      ","+ALIAS+".num_numero_totale_pagamenti as FlussoRendicontazione_numNumeroTotalePagamenti"+
      ","+ALIAS+".num_importo_totale_pagamenti as FlussoRendicontazione_numImportoTotalePagamenti"+
      ","+ALIAS+".cod_dati_sing_pagam_identificativo_univoco_versamento as FlussoRendicontazione_codDatiSingPagamIdentificativoUnivocoVersamento"+
      ","+ALIAS+".cod_dati_sing_pagam_identificativo_univoco_riscossione as FlussoRendicontazione_codDatiSingPagamIdentificativoUnivocoRiscossione"+
      ","+ALIAS+".num_dati_sing_pagam_singolo_importo_pagato as FlussoRendicontazione_numDatiSingPagamSingoloImportoPagato"+
      ","+ALIAS+".cod_dati_sing_pagam_codice_esito_singolo_pagamento as FlussoRendicontazione_codDatiSingPagamCodiceEsitoSingoloPagamento"+
      ","+ALIAS+".dt_dati_sing_pagam_data_esito_singolo_pagamento as FlussoRendicontazione_dtDatiSingPagamDataEsitoSingoloPagamento"+
      ","+ALIAS+".dt_acquisizione as FlussoRendicontazione_dtAcquisizione"+
      ","+ALIAS+".indice_dati_singolo_pagamento as FlussoRendicontazione_indiceDatiSingoloPagamento"+
      ","+ALIAS+".codice_bic_banca_di_riversamento as FlussoRendicontazione_codiceBicBancaDiRiversamento";

  private int version;
  private Date dtCreazione;
  private Date dtUltimaModifica;
  @Nested(Ente.ALIAS)
  private Ente mygovEnteId;
  @Nested(ManageFlusso.ALIAS)
  private ManageFlusso mygovManageFlussoId;
  private String identificativoPsp;
  private String versioneOggetto;
  private String codIdentificativoFlusso;
  private Date dtDataOraFlusso;
  private String codIdentificativoUnivocoRegolamento;
  private Date dtDataRegolamento;
  private Character codIstMittIdUnivMittTipoIdentificativoUnivoco;
  private String codIstMittIdUnivMittCodiceIdentificativoUnivoco;
  private String deIstMittDenominazioneMittente;
  private Character codIstRicevIdUnivRicevTipoIdentificativoUnivoco;
  private String codIstRicevIdUnivRicevCodiceIdentificativoUnivoco;
  private String deIstRicevDenominazioneRicevente;
  private long numNumeroTotalePagamenti;
  private BigDecimal numImportoTotalePagamenti;
  private String codDatiSingPagamIdentificativoUnivocoVersamento;
  private String codDatiSingPagamIdentificativoUnivocoRiscossione;
  private BigDecimal numDatiSingPagamSingoloImportoPagato;
  private String codDatiSingPagamCodiceEsitoSingoloPagamento;
  private Date dtDatiSingPagamDataEsitoSingoloPagamento;
  private Date dtAcquisizione;
  private int indiceDatiSingoloPagamento;
  private String codiceBicBancaDiRiversamento;

  // used on query of "pagamenti doppi"
  @Nested(FlussoExport.ALIAS)
  private FlussoExport nestedFlussoExport;

  private Boolean hasSegnalazione;
}
