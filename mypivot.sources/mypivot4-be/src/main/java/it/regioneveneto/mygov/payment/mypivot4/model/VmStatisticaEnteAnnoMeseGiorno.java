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
public class VmStatisticaEnteAnnoMeseGiorno extends BaseEntity {

  public final static String ALIAS = "VmStatisticaEnteAnnoMeseGiorno";
  public final static String FIELDS = ""+ALIAS+".mygov_ente_id as VmStatisticaEnteAnnoMeseGiorno_mygovEnteId"+
      ","+ALIAS+".anno as VmStatisticaEnteAnnoMeseGiorno_anno,"+ALIAS+".mese as VmStatisticaEnteAnnoMeseGiorno_mese"+
      ","+ALIAS+".giorno as VmStatisticaEnteAnnoMeseGiorno_giorno,"+ALIAS+".num_pag as VmStatisticaEnteAnnoMeseGiorno_numPag"+
      ","+ALIAS+".imp_pag as VmStatisticaEnteAnnoMeseGiorno_impPag"+
      ","+ALIAS+".imp_rend as VmStatisticaEnteAnnoMeseGiorno_impRend"+
      ","+ALIAS+".imp_inc as VmStatisticaEnteAnnoMeseGiorno_impInc";

  private Long mygovEnteId;
  private Integer anno;
  private Integer mese;
  private Integer giorno;
  private Integer numPag;
  private BigDecimal impPag;
  private BigDecimal impRend;
  private BigDecimal impInc;
}
