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

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "mygovOperatoreId")
public class Operatore extends BaseEntity {

  public final static String ALIAS = "Operatore";
  public final static String FIELDS = ""+ALIAS+".mygov_operatore_id as Operatore_mygovOperatoreId,"+ALIAS+".ruolo as Operatore_ruolo"+
      ","+ALIAS+".cod_fed_user_id as Operatore_codFedUserId,"+ALIAS+".cod_ipa_ente as Operatore_codIpaEnte";

  private Long mygovOperatoreId;
  private String ruolo;
  private String codFedUserId;
  private String codIpaEnte;

  //this calculated field is used in query OperatoreEnteTipoDovutoDao.getOperatoriByTipoDovutoId()
  //  true: operatore is associated and enabled on that tipo dovuto
  // false: operatore is associated but disabled on that tipo dovuto
  //  null: operatore is not associated
  private Optional<Boolean> flgAssociazione;
}
