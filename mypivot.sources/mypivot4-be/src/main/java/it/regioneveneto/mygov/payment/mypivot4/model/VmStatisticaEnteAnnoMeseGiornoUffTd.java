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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmStatisticaEnteAnnoMeseGiornoUffTd extends BaseEntity {

  public final static String ALIAS = "VmStatisticaEnteAnnoMeseGiornoUffTd";
  public final static String FIELDS = ""+ALIAS+".mygov_ente_id as VmStatisticaEnteAnnoMeseGiornoUffTd_mygovEnteId"+
      ","+ALIAS+".anno as VmStatisticaEnteAnnoMeseGiornoUffTd_anno,"+ALIAS+".mese as VmStatisticaEnteAnnoMeseGiornoUffTd_mese"+
      ","+ALIAS+".giorno as VmStatisticaEnteAnnoMeseGiornoUffTd_giorno"+
      ","+ALIAS+".cod_uff as VmStatisticaEnteAnnoMeseGiornoUffTd_codUff"+
      ","+ALIAS+".de_uff as VmStatisticaEnteAnnoMeseGiornoUffTd_deUff"+
      ","+ALIAS+".cod_td as VmStatisticaEnteAnnoMeseGiornoUffTd_codTd"+
      ","+ALIAS+".de_td as VmStatisticaEnteAnnoMeseGiornoUffTd_deTd"+
      ","+ALIAS+".imp_pag as VmStatisticaEnteAnnoMeseGiornoUffTd_impPag"+
      ","+ALIAS+".imp_rend as VmStatisticaEnteAnnoMeseGiornoUffTd_impRend"+
      ","+ALIAS+".imp_inc as VmStatisticaEnteAnnoMeseGiornoUffTd_impInc";

  private Long mygovEnteId;
  private Integer anno;
  private Integer mese;
  private Integer giorno;
  private String codUff;
  private String deUff;
  private String codTd;
  private String deTd;
  private BigDecimal impPag;
  private BigDecimal impRend;
  private BigDecimal impInc;
}
