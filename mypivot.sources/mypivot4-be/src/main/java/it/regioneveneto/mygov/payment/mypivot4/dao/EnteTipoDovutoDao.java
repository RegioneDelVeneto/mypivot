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
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;

import java.util.List;
import java.util.Optional;

public interface EnteTipoDovutoDao extends BaseDao {

  @SqlQuery(
      "    select " + EnteTipoDovuto.ALIAS + ALL_FIELDS +", " + Ente.FIELDS +
          "  from mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
          "  join mygov_ente " + Ente.ALIAS +
          "    on "+EnteTipoDovuto.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          " where "+Ente.ALIAS+".mygov_ente_id = :mygovEnteId " +
          "   and ("+ EnteTipoDovuto.ALIAS+".esterno = :esterno "+" or :esterno is null) " +
          " order by " + EnteTipoDovuto.ALIAS + ".de_tipo")
  @RegisterFieldMapper(EnteTipoDovuto.class)
  List<EnteTipoDovuto> getByMygovEnteIdAndFlags(Long mygovEnteId, Boolean esterno);

  @SqlQuery(
      "    select cod_tipo " +
          "  from mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
          "  join mygov_ente " + Ente.ALIAS +
          "    on "+EnteTipoDovuto.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          " where ("+ Ente.ALIAS+".cod_ipa_ente = :codIpaEnte or :codIpaEnte is null) " +
          " group by cod_tipo")
  List<String> getTipoByCodIpaEnte(String codIpaEnte);

  @SqlQuery(
      "    select " + EnteTipoDovuto.ALIAS + ALL_FIELDS +", " + Ente.FIELDS +
          "  from mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
          "  join mygov_ente " + Ente.ALIAS +
          "    on "+EnteTipoDovuto.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          " where "+EnteTipoDovuto.ALIAS+".mygov_ente_tipo_dovuto_id = :id ")
  @RegisterFieldMapper(EnteTipoDovuto.class)
  @Caching(
      put = {
          @CachePut(value = "enteTipoDovutoCache", key = "{'id',#result.mygovEnteTipoDovutoId}", condition="#result!=null"),
          @CachePut(value = "enteTipoDovutoCache", key = "{'codTipo',#result.codTipo, #result.mygovEnteId.codIpaEnte}", condition="#result!=null"),
          @CachePut(value = "enteTipoDovutoCache", key = "{'codTipoEnteId',#result.codTipo, #result.mygovEnteId.mygovEnteId}", condition="#result!=null")
      }
  )
  Optional<EnteTipoDovuto> getById(Long id);



  @SqlQuery(
      "    select " + EnteTipoDovuto.ALIAS + ALL_FIELDS +", " + Ente.FIELDS +
          "  from mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
          "  join mygov_ente " + Ente.ALIAS +
          "    on "+EnteTipoDovuto.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          " where "+ EnteTipoDovuto.ALIAS+".cod_tipo = :codTipo " +
          " and "+ Ente.ALIAS+".cod_ipa_ente = :codIpaEnte " +
          " order by " + Ente.ALIAS + ".de_nome_ente")
  @RegisterFieldMapper(EnteTipoDovuto.class)
  @Caching(
      put = {
          @CachePut(value = "enteTipoDovutoCache", key = "{'id',#result.mygovEnteTipoDovutoId}", condition="#result!=null"),
          @CachePut(value = "enteTipoDovutoCache", key = "{'codTipo',#result.codTipo, #result.mygovEnteId.codIpaEnte}", condition="#result!=null"),
          @CachePut(value = "enteTipoDovutoCache", key = "{'codTipoEnteId',#result.codTipo, #result.mygovEnteId.mygovEnteId}", condition="#result!=null")
      }
  )
  Optional<EnteTipoDovuto> getByCodTipo(String codTipo, String codIpaEnte);

  @SqlQuery(
      "    select " + EnteTipoDovuto.ALIAS + ALL_FIELDS +", " + Ente.FIELDS +
          "  from mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
          "  join mygov_ente " + Ente.ALIAS +
          "    on "+EnteTipoDovuto.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          " where "+ EnteTipoDovuto.ALIAS+".cod_tipo = :codTipo " +
          " and "+ Ente.ALIAS+".mygov_ente_id = :idEnte " +
          " order by " + Ente.ALIAS + ".de_nome_ente")
  @RegisterFieldMapper(EnteTipoDovuto.class)
  @Caching(
      put = {
          @CachePut(value = "enteTipoDovutoCache", key = "{'id',#result.mygovEnteTipoDovutoId}", condition="#result!=null"),
          @CachePut(value = "enteTipoDovutoCache", key = "{'codTipo',#result.codTipo, #result.mygovEnteId.codIpaEnte}", condition="#result!=null"),
          @CachePut(value = "enteTipoDovutoCache", key = "{'codTipoEnteId',#result.codTipo, #result.mygovEnteId.mygovEnteId}", condition="#result!=null")
      }
  )
  Optional<EnteTipoDovuto> getByCodTipoEnteId(String codTipo, Long idEnte);

  @SqlQuery(
      "    select " + EnteTipoDovuto.ALIAS + ALL_FIELDS +", " + Ente.FIELDS +
          "  from mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
          "  join mygov_ente " + Ente.ALIAS +
          "    on "+EnteTipoDovuto.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          "  join mygov_operatore Operatore " +
          "    on Operatore.cod_ipa_ente = "+Ente.ALIAS+".cod_ipa_ente" +
          "  join mygov_utente Utente " +
          "    on Utente.cod_fed_user_id  = Operatore.cod_fed_user_id " +
          " where Utente.cod_fed_user_id = :operatoreUsername " +
          "   and "+Ente.ALIAS+".mygov_ente_id = :mygovEnteId " +
          " order by " + EnteTipoDovuto.ALIAS + ".de_tipo")
  @RegisterFieldMapper(EnteTipoDovuto.class)
  List<EnteTipoDovuto> getByMygovEnteIdAndOperatoreUsername(Long mygovEnteId, String operatoreUsername);

  @SqlUpdate("INSERT INTO mygov_ente_tipo_dovuto (mygov_ente_tipo_dovuto_id, mygov_ente_id, cod_tipo, de_tipo, esterno) " +
             "VALUES(nextval('mygov_ente_tipo_dovuto_mygov_ente_tipo_dovuto_id_seq'), :mygovEnteId.mygovEnteId, :codTipo, :deTipo, :esterno) " +
             " returning mygov_ente_tipo_dovuto_id")
  @GetGeneratedKeys
  Long insert(@BindBean EnteTipoDovuto enteTipoDovuto);

  @SqlUpdate("update mygov_ente_tipo_dovuto " +
      " set cod_tipo = :codTipo, de_tipo = :deTipo " +
      " where mygov_ente_tipo_dovuto_id = :mygovEnteTipoDovutoId")
  int update(Long mygovEnteTipoDovutoId, String codTipo, String deTipo);

  @SqlUpdate("delete from mygov_ente_tipo_dovuto " +
      " where mygov_ente_tipo_dovuto_id = :mygovEnteTipoDovutoId ")
  int delete(Long mygovEnteTipoDovutoId);
}
