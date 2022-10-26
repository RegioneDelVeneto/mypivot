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

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "mygovAnagraficaStatoId")
public class AnagraficaStato extends BaseEntity {

  public final static String ALIAS = "AnagraficaStato";
  public final static String FIELDS = ""+ALIAS+".mygov_anagrafica_stato_id as AnagraficaStato_mygovAnagraficaStatoId"+
      ","+ALIAS+".cod_stato as AnagraficaStato_codStato,"+ALIAS+".de_stato as AnagraficaStato_deStato"+
      ","+ALIAS+".de_tipo_stato as AnagraficaStato_deTipoStato,"+ALIAS+".dt_creazione as AnagraficaStato_dtCreazione"+
      ","+ALIAS+".dt_ultima_modifica as AnagraficaStato_dtUltimaModifica";

  private Long mygovAnagraficaStatoId;
  private String codStato;
  private String deStato;
  private String deTipoStato;
  private Timestamp dtCreazione;
  private Timestamp dtUltimaModifica;
}
