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
import it.regioneveneto.mygov.payment.mypivot4.model.Operatore;
import it.regioneveneto.mygov.payment.mypivot4.model.Utente;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface UtenteDao extends BaseDao {

  @SqlQuery(
      "select "+Utente.ALIAS + ALL_FIELDS +" from mygov_utente "+ Utente.ALIAS +
          " where mygov_utente_id = :id" )
  @RegisterFieldMapper(Utente.class)
  Utente getById(Long id);

  @SqlQuery(
      "select distinct("+Utente.ALIAS + ALL_FIELDS +") from mygov_utente "+ Utente.ALIAS +
          " join mygov_operatore " + Operatore.ALIAS +
          " on "+Operatore.ALIAS+".cod_fed_user_id = "+Utente.ALIAS+".cod_fed_user_id " +
          " where "+Operatore.ALIAS+".cod_ipa_ente = :codIpaEnte")
  @RegisterFieldMapper(Utente.class)
  List<Utente> getByCodIpaEnte(String codIpaEnte);

  @SqlQuery(
      "select "+Utente.ALIAS + ALL_FIELDS +" from mygov_utente "+ Utente.ALIAS +
          " where lower(cod_fed_user_id) = lower(:codFedUserId)" )
  @RegisterFieldMapper(Utente.class)
  Utente getByCodFedUserIdIgnoreCase(String codFedUserId);

  @SqlQuery(
      "select "+Utente.ALIAS + ALL_FIELDS +" from mygov_utente "+ Utente.ALIAS +
          " where cod_fed_user_id = :codFedUserId" )
  @RegisterFieldMapper(Utente.class)
  Optional<Utente> getByCodFedUserId(String codFedUserId);

  @SqlUpdate(" insert into mygov_utente ( "+
      " mygov_utente_id "+
      ",version "+
      ",cod_fed_user_id "+
      ",cod_codice_fiscale_utente "+
      ",de_email_address "+
      ",de_firstname "+
      ",de_lastname "+
      ",dt_ultimo_login "+
      ") values ( "+
      " nextval('mygov_utente_mygov_utente_id_seq') "+
      ", 0 "+
      ",:u.codFedUserId "+
      ",:u.codCodiceFiscaleUtente "+
      ",:u.deEmailAddress "+
      ",:u.deFirstname "+
      ",:u.deLastname "+
      ",:u.dtUltimoLogin "+
      ")" )
  @GetGeneratedKeys("mygov_utente_id")
  long insert(@BindBean("u") Utente utente);

  @SqlUpdate("update mygov_utente set "+
      " version = :u.version "+
      ",cod_fed_user_id = :u.codFedUserId "+
      ",cod_codice_fiscale_utente = :u.codCodiceFiscaleUtente "+
      ",de_email_address = :u.deEmailAddress "+
      ",de_firstname = :u.deFirstname "+
      ",de_lastname = :u.deLastname "+
      ",dt_ultimo_login = :u.dtUltimoLogin "+
      " where mygov_utente_id = :u.mygovUtenteId " )
  int update(@BindBean("u") Utente utente);
}
