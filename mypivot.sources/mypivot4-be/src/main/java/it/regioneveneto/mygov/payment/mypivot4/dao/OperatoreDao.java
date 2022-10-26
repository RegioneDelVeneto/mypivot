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
package it.regioneveneto.mygov.payment.mypivot4.dao;

import it.regioneveneto.mygov.payment.mypay4.dao.BaseDao;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.EnteTipoDovuto;
import it.regioneveneto.mygov.payment.mypivot4.model.Operatore;
import it.regioneveneto.mygov.payment.mypivot4.model.OperatoreEnteTipoDovuto;
import it.regioneveneto.mygov.payment.mypivot4.model.Utente;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface OperatoreDao extends BaseDao {

  @SqlQuery(
      "    select "+Operatore.ALIAS+ALL_FIELDS +
          "  from mygov_operatore "+Operatore.ALIAS +
          " where "+Operatore.ALIAS+".mygov_operatore_id = :operatoreId "
  )
  @RegisterFieldMapper(Operatore.class)
  Optional<Operatore> getById(Long operatoreId);

  @SqlQuery(
      "    select 1 " +
          "  from mygov_ente " + Ente.ALIAS +
          "  join mygov_operatore " + Operatore.ALIAS +
          "    on "+Operatore.ALIAS+".cod_ipa_ente = "+Ente.ALIAS+".cod_ipa_ente" +
          "  join mygov_utente " + Utente.ALIAS +
          "    on "+Utente.ALIAS+".cod_fed_user_id  = "+Operatore.ALIAS+".cod_fed_user_id " +
          " where "+Utente.ALIAS+".cod_fed_user_id = :operatoreUsername " +
          "   and (:mygovEnteId is null or "+Ente.ALIAS+".mygov_ente_id = :mygovEnteId)" +
          " limit 1")
  Optional<Boolean> isOperatoreForEnte(String operatoreUsername, Long mygovEnteId);

}
