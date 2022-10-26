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
package it.regioneveneto.mygov.payment.mypivot4.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlussoUploadRequestTo {

  private String codTipo;
  private String importPath;
  private String codProvenienza;
  private String requestToken;
  private Integer posDeAnnoBolletta;
  private Integer posCodBolletta;
  private Integer posDtContabile;
  private Integer posDeDenominazione;
  private Integer posDeCausale;
  private Integer posNumImporto;
  private Integer posDtValuta;

  public static FlussoUploadRequestTo DEFAULT_POSITION(){
    return FlussoUploadRequestTo.builder()
        .posDeAnnoBolletta(1)
        .posCodBolletta(2)
        .posDtContabile(3)
        .posDeDenominazione(4)
        .posDeCausale(5)
        .posNumImporto(6)
        .posDtValuta(7)
        .build();
  }
}
