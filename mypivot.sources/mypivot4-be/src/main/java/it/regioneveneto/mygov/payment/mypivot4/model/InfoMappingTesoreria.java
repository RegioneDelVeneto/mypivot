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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "mygovInfoMappingTesoreriaId")
public class InfoMappingTesoreria extends BaseEntity {

  public final static String ALIAS = "InfoMappingTesoreria";
  public final static String FIELDS = ""+ALIAS+".mygov_info_mapping_tesoreria_id as InfoMappingTesoreria_mygovInfoMappingTesoreriaId"+
      ","+ALIAS+".mygov_manage_flusso_id as InfoMappingTesoreria_mygovManageFlussoId"+
      ","+ALIAS+".pos_de_anno_bolletta as InfoMappingTesoreria_posDeAnnoBolletta"+
      ","+ALIAS+".pos_cod_bolletta as InfoMappingTesoreria_posCodBolletta"+
      ","+ALIAS+".pos_dt_contabile as InfoMappingTesoreria_posDtContabile"+
      ","+ALIAS+".pos_de_denominazione as InfoMappingTesoreria_posDeDenominazione"+
      ","+ALIAS+".pos_de_causale as InfoMappingTesoreria_posDeCausale"+
      ","+ALIAS+".pos_num_importo as InfoMappingTesoreria_posNumImporto"+
      ","+ALIAS+".pos_dt_valuta as InfoMappingTesoreria_posDtValuta,"+ALIAS+".dt_creazione as InfoMappingTesoreria_dtCreazione"+
      ","+ALIAS+".dt_ultima_modifica as InfoMappingTesoreria_dtUltimaModifica";

  private Long mygovInfoMappingTesoreriaId;
  @Nested(ManageFlusso.ALIAS)
  private ManageFlusso mygovManageFlussoId;
  private int posDeAnnoBolletta;
  private int posCodBolletta;
  private int posDtContabile;
  private int posDeDenominazione;
  private int posDeCausale;
  private int posNumImporto;
  private int posDtValuta;
  private Date dtCreazione;
  private Date dtUltimaModifica;
}
