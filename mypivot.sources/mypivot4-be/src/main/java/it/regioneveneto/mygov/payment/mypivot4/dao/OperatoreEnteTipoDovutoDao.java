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
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface OperatoreEnteTipoDovutoDao extends BaseDao {

  @SqlQuery(
      "    select "+OperatoreEnteTipoDovuto.ALIAS+ALL_FIELDS+", "+EnteTipoDovuto.FIELDS +
          "  from mygov_operatore_ente_tipo_dovuto " + OperatoreEnteTipoDovuto.ALIAS +
          "  join mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
          "    on "+EnteTipoDovuto.ALIAS+".mygov_ente_tipo_dovuto_id = "+OperatoreEnteTipoDovuto.ALIAS+".mygov_ente_tipo_dovuto_id " +
          "  join mygov_ente " + Ente.ALIAS +
          "    on "+Ente.ALIAS+".mygov_ente_id = "+EnteTipoDovuto.ALIAS+".mygov_ente_id " +
          "  join mygov_operatore " + Operatore.ALIAS +
          "    on "+Operatore.ALIAS+".mygov_operatore_id = "+OperatoreEnteTipoDovuto.ALIAS+".mygov_operatore_id " +
          " where ("+Ente.ALIAS+".cod_ipa_ente = :codIpaEnte or :codIpaEnte is null) " +
          "   and ("+EnteTipoDovuto.ALIAS+".cod_tipo = :codTipo or :codTipo is null)" +
          "   and ("+Operatore.ALIAS+".cod_fed_user_id = :codFedUserId or :codFedUserId is null) " +
          " order by mygov_operatore_ente_tipo_dovuto_id "
  )
  @RegisterFieldMapper(OperatoreEnteTipoDovuto.class)
  List<OperatoreEnteTipoDovuto> getByCodIpaCodTipoCodFed(String codIpaEnte, String codTipo, String codFedUserId);

  @SqlQuery(
      "    select "+EnteTipoDovuto.ALIAS+".cod_tipo " +
          "  from mygov_operatore_ente_tipo_dovuto " + OperatoreEnteTipoDovuto.ALIAS +
          "  join mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
          "    on "+EnteTipoDovuto.ALIAS+".mygov_ente_tipo_dovuto_id = "+OperatoreEnteTipoDovuto.ALIAS+".mygov_ente_tipo_dovuto_id " +
          "  join mygov_ente " + Ente.ALIAS +
          "    on "+Ente.ALIAS+".mygov_ente_id = "+EnteTipoDovuto.ALIAS+".mygov_ente_id " +
          "  join mygov_operatore " + Operatore.ALIAS +
          "    on "+Operatore.ALIAS+".mygov_operatore_id = "+OperatoreEnteTipoDovuto.ALIAS+".mygov_operatore_id " +
          " where ("+Ente.ALIAS+".cod_ipa_ente = :codIpaEnte or :codIpaEnte is null) " +
          "   and ("+Operatore.ALIAS+".cod_fed_user_id = :codFedUserId or :codFedUserId is null) "
  )
  List<String> getTipoByCodIpaCodFedUser(String codIpaEnte, String codFedUserId);

  @SqlQuery(
      "   select "+Operatore.ALIAS+ALL_FIELDS+", ( " +
          "   select "+OperatoreEnteTipoDovuto.ALIAS+".flg_attivo " +
          "     from mygov_operatore_ente_tipo_dovuto " + OperatoreEnteTipoDovuto.ALIAS +
          "    where "+OperatoreEnteTipoDovuto.ALIAS+".mygov_ente_tipo_dovuto_id = :tipoDovutoId " +
          "      and "+OperatoreEnteTipoDovuto.ALIAS+".mygov_operatore_id = "+Operatore.ALIAS+".mygov_operatore_id ) as flgAssociazione " +
          " from mygov_operatore "+Operatore.ALIAS +
          " join mygov_ente "+Ente.ALIAS +
          "   on "+Operatore.ALIAS+".cod_ipa_ente = "+Ente.ALIAS+".cod_ipa_ente " +
          "where "+Ente.ALIAS+".mygov_ente_id = (select mygov_ente_id from mygov_ente_tipo_dovuto where mygov_ente_tipo_dovuto_id = :tipoDovutoId) "+
          "order by "+Operatore.ALIAS+".cod_fed_user_id "
  )
  @RegisterFieldMapper(Operatore.class)
  List<Operatore> getOperatoriByTipoDovutoId(Long tipoDovutoId);

  @SqlQuery(
      "    select "+OperatoreEnteTipoDovuto.ALIAS+ALL_FIELDS +
          "  from mygov_operatore_ente_tipo_dovuto "+OperatoreEnteTipoDovuto.ALIAS +
          " where "+OperatoreEnteTipoDovuto.ALIAS+".mygov_operatore_id = :operatoreId "+
          "   and "+OperatoreEnteTipoDovuto.ALIAS+".mygov_ente_tipo_dovuto_id = :tipoDovutoId "
  )
  @RegisterFieldMapper(OperatoreEnteTipoDovuto.class)
  Optional<OperatoreEnteTipoDovuto> getOperatoreEnteTipoDovuto(Long tipoDovutoId, Long operatoreId);

  @SqlUpdate("INSERT INTO mygov_operatore_ente_tipo_dovuto " +
      "(mygov_operatore_ente_tipo_dovuto_id, mygov_ente_tipo_dovuto_id, flg_attivo, mygov_operatore_id) " +
      "VALUES(nextval('mygov_op_ente_tipo_dovuto_mygov_op_ente_tipo_dovuto_id_seq'), :tipoDovutoId, :flgAttivo, :operatoreId)")
  int insertOperatoreTipoDovuto(Long tipoDovutoId, Long operatoreId, boolean flgAttivo);

  @SqlUpdate("update mygov_operatore_ente_tipo_dovuto " +
      "set flg_attivo = :flgAttivo "+
      "where mygov_operatore_ente_tipo_dovuto_id = :operatoreTipoDovutoId")
  int updateOperatoreTipoDovuto(Long operatoreTipoDovutoId, boolean flgAttivo);

}
