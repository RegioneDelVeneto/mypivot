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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "mygovAnagraficaUffCapAccId")
public class AnagraficaUffCapAcc extends BaseEntity {

  public final static String ALIAS = "AnagraficaUffCapAcc";
  public final static String FIELDS = ""+ALIAS+".mygov_anagrafica_uff_cap_acc_id as AnagraficaUffCapAcc_mygovAnagraficaUffCapAccId"+
      ","+ALIAS+".mygov_ente_id as AnagraficaUffCapAcc_mygovEnteId"+
      ","+ALIAS+".cod_tipo_dovuto as AnagraficaUffCapAcc_codTipoDovuto,"+ALIAS+".cod_ufficio as AnagraficaUffCapAcc_codUfficio"+
      ","+ALIAS+".de_ufficio as AnagraficaUffCapAcc_deUfficio,"+ALIAS+".flg_attivo as AnagraficaUffCapAcc_flgAttivo"+
      ","+ALIAS+".cod_capitolo as AnagraficaUffCapAcc_codCapitolo,"+ALIAS+".de_capitolo as AnagraficaUffCapAcc_deCapitolo"+
      ","+ALIAS+".de_anno_esercizio as AnagraficaUffCapAcc_deAnnoEsercizio"+
      ","+ALIAS+".cod_accertamento as AnagraficaUffCapAcc_codAccertamento"+
      ","+ALIAS+".de_accertamento as AnagraficaUffCapAcc_deAccertamento"+
      ","+ALIAS+".dt_creazione as AnagraficaUffCapAcc_dtCreazione"+
      ","+ALIAS+".dt_ultima_modifica as AnagraficaUffCapAcc_dtUltimaModifica";

  private Long mygovAnagraficaUffCapAccId;
  private Long mygovEnteId;
  private String codTipoDovuto;
  private String codUfficio;
  private String deUfficio;
  private boolean flgAttivo;
  private String codCapitolo;
  private String deCapitolo;
  private String deAnnoEsercizio;
  private String codAccertamento;
  private String deAccertamento;
  private Date dtCreazione;
  private Date dtUltimaModifica;
}
