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

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "mygovFlussoTesoreriaId")
public class FlussoTesoreria extends BaseEntity {

  public final static String ALIAS = "FlussoTesoreria";
  public final static String FIELDS = ""+ALIAS+".mygov_flusso_tesoreria_id as FlussoTesoreria_mygovFlussoTesoreriaId"+
      ","+ALIAS+".de_anno_bolletta as FlussoTesoreria_deAnnoBolletta,"+ALIAS+".cod_bolletta as FlussoTesoreria_codBolletta"+
      ","+ALIAS+".cod_conto as FlussoTesoreria_codConto,"+ALIAS+".cod_id_dominio as FlussoTesoreria_codIdDominio"+
      ","+ALIAS+".cod_tipo_movimento as FlussoTesoreria_codTipoMovimento,"+ALIAS+".cod_causale as FlussoTesoreria_codCausale"+
      ","+ALIAS+".de_causale as FlussoTesoreria_deCausale,"+ALIAS+".num_ip_bolletta as FlussoTesoreria_numIpBolletta"+
      ","+ALIAS+".dt_bolletta as FlussoTesoreria_dtBolletta,"+ALIAS+".dt_ricezione as FlussoTesoreria_dtRicezione"+
      ","+ALIAS+".de_anno_documento as FlussoTesoreria_deAnnoDocumento,"+ALIAS+".cod_documento as FlussoTesoreria_codDocumento"+
      ","+ALIAS+".cod_bollo as FlussoTesoreria_codBollo,"+ALIAS+".de_cognome as FlussoTesoreria_deCognome"+
      ","+ALIAS+".de_nome as FlussoTesoreria_deNome,"+ALIAS+".de_via as FlussoTesoreria_deVia"+
      ","+ALIAS+".de_cap as FlussoTesoreria_deCap,"+ALIAS+".de_citta as FlussoTesoreria_deCitta"+
      ","+ALIAS+".cod_codice_fiscale as FlussoTesoreria_codCodiceFiscale"+
      ","+ALIAS+".cod_partita_iva as FlussoTesoreria_codPartitaIva,"+ALIAS+".cod_abi as FlussoTesoreria_codAbi"+
      ","+ALIAS+".cod_cab as FlussoTesoreria_codCab,"+ALIAS+".cod_conto_anagrafica as FlussoTesoreria_codContoAnagrafica"+
      ","+ALIAS+".de_ae_provvisorio as FlussoTesoreria_deAeProvvisorio"+
      ","+ALIAS+".cod_provvisorio as FlussoTesoreria_codProvvisorio,"+ALIAS+".cod_iban as FlussoTesoreria_codIban"+
      ","+ALIAS+".cod_tipo_conto as FlussoTesoreria_codTipoConto,"+ALIAS+".cod_processo as FlussoTesoreria_codProcesso"+
      ","+ALIAS+".cod_pg_esecuzione as FlussoTesoreria_codPgEsecuzione"+
      ","+ALIAS+".cod_pg_trasferimento as FlussoTesoreria_codPgTrasferimento"+
      ","+ALIAS+".num_pg_processo as FlussoTesoreria_numPgProcesso"+
      ","+ALIAS+".dt_data_valuta_regione as FlussoTesoreria_dtDataValutaRegione"+
      ","+ALIAS+".mygov_ente_id as FlussoTesoreria_mygovEnteId"+
      ","+ALIAS+".cod_id_univoco_flusso as FlussoTesoreria_codIdUnivocoFlusso"+
      ","+ALIAS+".cod_id_univoco_versamento as FlussoTesoreria_codIdUnivocoVersamento"+
      ","+ALIAS+".dt_creazione as FlussoTesoreria_dtCreazione,"+ALIAS+".dt_ultima_modifica as FlussoTesoreria_dtUltimaModifica"+
      ","+ALIAS+".flg_regolarizzata as FlussoTesoreria_flgRegolarizzata"+
      ","+ALIAS+".mygov_manage_flusso_id as FlussoTesoreria_mygovManageFlussoId"+
      ","+ALIAS+".dt_effettiva_sospeso as FlussoTesoreria_dtEffettivaSospeso"+
      ","+ALIAS+".codice_gestionale_provvisorio as FlussoTesoreria_codiceGestionaleProvvisorio"+
      ","+ALIAS+".end_to_end_id as FlussoTesoreria_endToEndId";

  private Long mygovFlussoTesoreriaId;
  private String deAnnoBolletta;
  private String codBolletta;
  private String codConto;
  private String codIdDominio;
  private String codTipoMovimento;
  private String codCausale;
  private String deCausale;
  private BigDecimal numIpBolletta;
  private Date dtBolletta;
  private Date dtRicezione;
  private String deAnnoDocumento;
  private String codDocumento;
  private String codBollo;
  private String deCognome;
  private String deNome;
  private String deVia;
  private String deCap;
  private String deCitta;
  private String codCodiceFiscale;
  private String codPartitaIva;
  private String codAbi;
  private String codCab;
  private String codContoAnagrafica;
  private String deAeProvvisorio;
  private String codProvvisorio;
  private String codIban;
  private Character codTipoConto;
  private String codProcesso;
  private String codPgEsecuzione;
  private String codPgTrasferimento;
  private Long numPgProcesso;
  private Date dtDataValutaRegione;
  @Nested(Ente.ALIAS)
  private Ente mygovEnteId;
  private String codIdUnivocoFlusso;
  private String codIdUnivocoVersamento;
  private Date dtCreazione;
  private Date dtUltimaModifica;
  private boolean flgRegolarizzata;
  @Nested(ManageFlusso.ALIAS)
  private ManageFlusso mygovManageFlussoId;
  private Date dtEffettivaSospeso;
  private String codiceGestionaleProvvisorio;
  private String endToEndId;
}
