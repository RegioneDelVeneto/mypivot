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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "mygovEnteTipoDovutoId")
public class EnteTipoDovuto extends BaseEntity {

  public final static String ALIAS = "EnteTipoDovuto";
  public final static String FIELDS = ""+ALIAS+".mygov_ente_tipo_dovuto_id as EnteTipoDovuto_mygovEnteTipoDovutoId"+
      ","+ALIAS+".mygov_ente_id as EnteTipoDovuto_mygovEnteId,"+ALIAS+".cod_tipo as EnteTipoDovuto_codTipo"+
      ","+ALIAS+".de_tipo as EnteTipoDovuto_deTipo,"+ALIAS+".esterno as EnteTipoDovuto_esterno";

  private Long mygovEnteTipoDovutoId;
  @Nested(Ente.ALIAS)
  private Ente mygovEnteId;
  private String codTipo;
  private String deTipo;
  private boolean esterno;
}
