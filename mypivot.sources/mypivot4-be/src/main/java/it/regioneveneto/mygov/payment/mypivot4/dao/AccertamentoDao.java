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
import it.regioneveneto.mygov.payment.mypivot4.model.*;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDate;
import java.util.List;

public interface AccertamentoDao extends BaseDao {

  @SqlUpdate(
      "INSERT INTO mygov_accertamento (" +
          "  mygov_accertamento_id"+
          " ,mygov_ente_tipo_dovuto_id"+
          " ,mygov_anagrafica_stato_id"+
          " ,mygov_utente_id"+
          " ,de_nome_accertamento"+
          " ,dt_creazione"+
          " ,dt_ultima_modifica"+
          " ,printed"+
          ") VALUES (" +
          " nextval('mygov_accertamento_mygov_accertamento_id_seq')"+
          " ,:d.mygovEnteTipoDovutoId.mygovEnteTipoDovutoId"+
          " ,:d.mygovAnagraficaStatoId.mygovAnagraficaStatoId"+
          " ,:d.mygovUtenteId.mygovUtenteId"+
          " ,:d.deNomeAccertamento"+
          " ,COALESCE(:d.dtCreazione, NOW())"+
          " ,COALESCE(:d.dtUltimaModifica, NOW())"+
          " ,:d.printed"+
          ")"
  )
  @GetGeneratedKeys("mygov_accertamento_id")
  long insert(@BindBean("d") Accertamento d);

  @SqlUpdate(
      "UPDATE mygov_accertamento SET " +
          "  mygov_ente_tipo_dovuto_id = :d.mygovEnteTipoDovutoId.mygovEnteTipoDovutoId"+
          " ,mygov_anagrafica_stato_id = :d.mygovAnagraficaStatoId.mygovAnagraficaStatoId"+
          " ,mygov_utente_id = :d.mygovUtenteId.mygovUtenteId"+
          " ,de_nome_accertamento = :d.deNomeAccertamento"+
          " ,dt_ultima_modifica = NOW() "+
          " ,printed = :d.printed"+
          " WHERE mygov_accertamento_id = :d.mygovAccertamentoId"
  )
  int update(@BindBean("d") Accertamento d);

  @SqlQuery(
      "SELECT "+Accertamento.ALIAS+ALL_FIELDS+", "+EnteTipoDovuto.FIELDS+", "+AnagraficaStato.FIELDS+","+Utente.FIELDS +
      " FROM mygov_accertamento " + Accertamento.ALIAS +
      " INNER JOIN mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
      " ON "+Accertamento.ALIAS+".mygov_ente_tipo_dovuto_id = "+EnteTipoDovuto.ALIAS+".mygov_ente_tipo_dovuto_id " +
      " INNER JOIN mygov_anagrafica_stato " + AnagraficaStato.ALIAS +
      " ON "+Accertamento.ALIAS+".mygov_anagrafica_stato_id = "+AnagraficaStato.ALIAS+".mygov_anagrafica_stato_id " +
      " INNER JOIN mygov_utente " + Utente.ALIAS +
      " ON "+Accertamento.ALIAS+".mygov_utente_id = "+Utente.ALIAS+".mygov_utente_id " +
      " WHERE "+Accertamento.ALIAS+".mygov_accertamento_id = :mygovAccertamentoId"
  )
  @RegisterFieldMapper(Accertamento.class)
  Accertamento getById(Long mygovAccertamentoId);

  String WHERE =
    " FROM mygov_accertamento " + Accertamento.ALIAS +
      " INNER JOIN mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
      " ON "+Accertamento.ALIAS+".mygov_ente_tipo_dovuto_id = "+EnteTipoDovuto.ALIAS+".mygov_ente_tipo_dovuto_id " +
      " INNER JOIN mygov_anagrafica_stato " + AnagraficaStato.ALIAS +
      " ON "+Accertamento.ALIAS+".mygov_anagrafica_stato_id = "+AnagraficaStato.ALIAS+".mygov_anagrafica_stato_id " +
      " INNER JOIN mygov_utente " + Utente.ALIAS +
      " ON "+Accertamento.ALIAS+".mygov_utente_id = "+Utente.ALIAS+".mygov_utente_id " +
      " LEFT JOIN mygov_accertamento_dettaglio " + AccertamentoDettaglio.ALIAS +
      " ON "+Accertamento.ALIAS+".mygov_accertamento_id = "+AccertamentoDettaglio.ALIAS+".mygov_accertamento_id " +
      //"INNER JOIN mygov_ente ente ON enteTipoDovuto.mygov_ente_id = ente.mygov_ente_id "
      " WHERE "+EnteTipoDovuto.ALIAS+".mygov_ente_id = :enteId " +
      "   AND "+EnteTipoDovuto.ALIAS+".cod_tipo in (<codTipiDovuto>) " +
      "   AND ("+Accertamento.ALIAS+".mygov_utente_id = :utenteId or :utenteId is null) " +
      "   AND ("+AnagraficaStato.ALIAS+".cod_stato = :codStato OR :codStato is null) " +
      //"   AND ("+Accertamento.ALIAS+".de_nome_accertamento ilike '%' || :nomeAccertamento || '%' OR :nomeAccertamento is null) " +
      "   AND ("+Accertamento.ALIAS+".dt_ultima_modifica >= :from OR (:from::DATE) is null) " +
      "   AND ("+Accertamento.ALIAS+".dt_ultima_modifica < :to OR (:to::DATE) is null) " +
      "   AND ("+AccertamentoDettaglio.ALIAS+".cod_iuv = :codIuv OR :codIuv is null) "+
      "   AND ("+Accertamento.ALIAS+".de_nome_accertamento ilike '%' || :deNomeAccertamento || '%' or coalesce(:deNomeAccertamento, '') = '') ";

  @SqlQuery(
      "SELECT "+Accertamento.ALIAS+ALL_FIELDS+", "+EnteTipoDovuto.FIELDS+", "+AnagraficaStato.FIELDS+","+Utente.FIELDS +
          WHERE +
          " ORDER BY "+Accertamento.ALIAS+".dt_ultima_modifica DESC" +
          " LIMIT <queryLimit>"
  )
  @RegisterFieldMapper(Accertamento.class)
  List<Accertamento> search(Long enteId, Long utenteId, @BindList(onEmpty = BindList.EmptyHandling.NULL_STRING) List<String> codTipiDovuto,
                            String codStato, LocalDate from, LocalDate to, String codIuv, String deNomeAccertamento, @Define int queryLimit);

  @SqlQuery(
    "SELECT count(1) " +
      WHERE
  )
  @RegisterFieldMapper(Accertamento.class)
  Integer searchCount(Long enteId, Long utenteId, @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> codTipiDovuto,
                            String codStato, LocalDate from, LocalDate to, String codIuv, String deNomeAccertamento);
}
