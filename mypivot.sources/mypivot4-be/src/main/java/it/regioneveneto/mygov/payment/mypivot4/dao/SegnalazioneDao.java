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
import it.regioneveneto.mygov.payment.mypivot4.dto.SegnalazioneSearchTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Segnalazione;
import it.regioneveneto.mygov.payment.mypivot4.model.Utente;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface SegnalazioneDao extends BaseDao {


  @SqlUpdate(
      "insert into mygov_segnalazione (" +
          "  mygov_segnalazione_id" +
          ", mygov_ente_id" +
          ", mygov_utente_id" +
          ", classificazione_completezza" +
          ", cod_iud" +
          ", cod_iuv" +
          ", cod_iuf" +
          ", de_nota" +
          ", flg_nascosto" +
          ", flg_attivo" +
          ", dt_creazione" +
          ", dt_ultima_modifica" +
          ", \"version\"" +
          ") values (" +
          "  nextval('mygov_segnalazione_mygov_segnalazione_id_seq')" +
          ", :d.mygovEnteId.mygovEnteId" +
          ", :d.mygovUtenteId.mygovUtenteId" +
          ", :d.classificazioneCompletezza" +
          ", :d.codIud" +
          ", :d.codIuv" +
          ", :d.codIuf" +
          ", :d.deNota" +
          ", false" +
          ", true" +
          ", CURRENT_TIMESTAMP" +
          ", CURRENT_TIMESTAMP" +
          ", 0" +
          ")"
  )
  @GetGeneratedKeys("mygov_segnalazione_id")
  long insert(@BindBean("d") Segnalazione d);

  @SqlUpdate(
      "  update mygov_segnalazione " +
          " set flg_attivo = false " +
          "   , dt_ultima_modifica = CURRENT_TIMESTAMP " +
          "   , version = version + 1 " +
          " where mygov_segnalazione_id = :mygovSegnalazioneId " +
          "   and flg_attivo = true "
  )
  int updateSetNonAttivo(Long mygovSegnalazioneId);

  @SqlQuery(
      "select "+Segnalazione.ALIAS+ALL_FIELDS+", "+Utente.FIELDS +
          "  from mygov_segnalazione " + Segnalazione.ALIAS +
          "  join mygov_utente " + Utente.ALIAS + " on "+Utente.ALIAS+".mygov_utente_id = "+Segnalazione.ALIAS+".mygov_utente_id" +
          " where "+Segnalazione.ALIAS+".mygov_ente_id = :mygovEnteId.mygovEnteId " +
          "   and "+Segnalazione.ALIAS+".classificazione_completezza = :classificazioneCompletezza " +
          "   and ("+Segnalazione.ALIAS+".cod_iuf is null and :codIuf is null or "+Segnalazione.ALIAS+".cod_iuf = :codIuf) " +
          "   and ("+Segnalazione.ALIAS+".cod_iud is null and :codIud is null or "+Segnalazione.ALIAS+".cod_iud = :codIud) " +
          "   and ("+Segnalazione.ALIAS+".cod_iuv is null and :codIuv is null or "+Segnalazione.ALIAS+".cod_iuv = :codIuv) " +
          "   and (:attivo is null or "+Segnalazione.ALIAS+".flg_attivo = :attivo) "
  )
  @RegisterFieldMapper(Segnalazione.class)
  List<Segnalazione> getSegnalazioniByElement(@BindBean Segnalazione segnalazione, Boolean attivo);

  String SEARCH_QUERY_BASE =
      "  from mygov_segnalazione " + Segnalazione.ALIAS +
      "  join mygov_utente " + Utente.ALIAS + " on "+Utente.ALIAS+".mygov_utente_id = "+Segnalazione.ALIAS+".mygov_utente_id" +
      " where "+Segnalazione.ALIAS+".mygov_ente_id = :mygovEnteId " +
      "   and (:classificazione is null or "+Segnalazione.ALIAS+".classificazione_completezza = :classificazione) " +
      "   and (:iuf is null or "+Segnalazione.ALIAS+".cod_iuf = :iuf) " +
      "   and (:iud is null or "+Segnalazione.ALIAS+".cod_iud = :iud) " +
      "   and (:iuv is null or "+Segnalazione.ALIAS+".cod_iuv = :iuv) " +
      "   and (:attivo is null or "+Segnalazione.ALIAS+".flg_attivo = :attivo) " +
      "   and (:nascosto is null or "+Segnalazione.ALIAS+".flg_nascosto = :nascosto) " +
      "   and (:dtInseritoPrima::date is null or "+Segnalazione.ALIAS+".dt_creazione < :dtInseritoPrima) " +
      "   and (:dtInseritoDopo::date is null or "+Segnalazione.ALIAS+".dt_creazione >= :dtInseritoDopo) " +
      "   and (:utente is null or "+Utente.ALIAS+".de_firstname || ' ' || "+Utente.ALIAS+".de_lastname ilike '%' || :utente || '%') ";

  @SqlQuery(
      "select "+Segnalazione.ALIAS+ALL_FIELDS+", "+Utente.FIELDS +
        SEARCH_QUERY_BASE +
        " order by "+Segnalazione.ALIAS+".dt_creazione DESC " +
        " limit <queryLimit>"
  )
  @RegisterFieldMapper(Segnalazione.class)
  List<Segnalazione> searchSegnalazioni(Long mygovEnteId, @BindBean SegnalazioneSearchTo segnalazioneSearch, @Define int queryLimit);

  @SqlQuery(
      "select count(1) " +
          SEARCH_QUERY_BASE
  )
  Integer searchSegnalazioniCount(Long mygovEnteId, @BindBean SegnalazioneSearchTo segnalazioneSearch);

}
