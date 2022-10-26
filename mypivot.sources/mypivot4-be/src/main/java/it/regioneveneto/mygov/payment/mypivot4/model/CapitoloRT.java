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
public class CapitoloRT extends BaseEntity {

  public final static String ALIAS = "CapitoloRT";
  public final static String FIELDS = ""+ALIAS+".cod_ufficio as CapitoloRT_codUfficio,"+ALIAS+".de_ufficio as CapitoloRT_deUfficio"+
      ","+ALIAS+".cod_capitolo as CapitoloRT_codCapitolo,"+ALIAS+".de_capitolo as CapitoloRT_deCapitolo"+
      ","+ALIAS+".de_anno_esercizio as CapitoloRT_deAnnoEsercizio,"+ALIAS+".cod_accertamento as CapitoloRT_codAccertamento"+
      ","+ALIAS+".de_accertamento as CapitoloRT_deAccertamento,"+ALIAS+".num_importo as CapitoloRT_numImporto";

    private String codUfficio;
    private String deUfficio;
    private String codCapitolo;
    private String deCapitolo;
    private String deAnnoEsercizio;
    private String codAccertamento;
    private String deAccertamento;
    private BigDecimal numImporto;
}
