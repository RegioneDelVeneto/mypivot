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
import it.regioneveneto.mygov.payment.mypivot4.model.FlussoTesoreria;
import it.regioneveneto.mygov.payment.mypivot4.model.ManageFlusso;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FlussoTesoreriaDao extends BaseDao {

  String WHERE_CLAUSE =
      " where "+Ente.ALIAS+".cod_ipa_ente = :codIpaEnte " +
          "   and ("+FlussoTesoreria.ALIAS+".cod_id_univoco_versamento = :iuv or :iuv is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".de_anno_bolletta = :annoBolletta or :annoBolletta is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".cod_bolletta = :codBolletta or :codBolletta is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".cod_id_univoco_flusso ilike '%' || :idr || '%' or :idr is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".num_ip_bolletta = :importo or :importo is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".de_anno_documento = :annoDocumento or :annoDocumento is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".cod_documento = :codDocumento or :codDocumento is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".de_ae_provvisorio = :annoProvvisorio or :annoProvvisorio is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".cod_provvisorio = :codProvvisorio or :codProvvisorio is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".de_cognome ilike '%' || :ordinante || '%' or :ordinante is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".dt_bolletta >= :dtContabileFrom or :dtContabileFrom::date  is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".dt_bolletta <= :dtContabileTo or :dtContabileTo::date  is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".dt_data_valuta_regione >= :dtValutaFrom or :dtValutaFrom::date  is null)" +
          "   and ("+FlussoTesoreria.ALIAS+".dt_data_valuta_regione <= :dtValutaTo or :dtValutaTo::date  is null)";

  @SqlQuery(
      "select "+ FlussoTesoreria.ALIAS+ALL_FIELDS+", "+ Ente.FIELDS+", "+ ManageFlusso.FIELDS +
      "  from mygov_flusso_tesoreria " + FlussoTesoreria.ALIAS +
      "  join mygov_ente "+Ente.ALIAS+" on "+Ente.ALIAS+".mygov_ente_id = "+FlussoTesoreria.ALIAS+".mygov_ente_id"  +
      "  left join mygov_manage_flusso "+ManageFlusso.ALIAS+" on "+ManageFlusso.ALIAS+".mygov_manage_flusso_id = "+FlussoTesoreria.ALIAS+".mygov_manage_flusso_id" +
      " where "+Ente.ALIAS+".cod_ipa_ente = :codIpaEnte " +
      "   and "+FlussoTesoreria.ALIAS+".de_anno_bolletta = :deAnnoBolletta " +
      "   and "+FlussoTesoreria.ALIAS+".cod_bolletta = :codBolletta "
  )
  @RegisterFieldMapper(FlussoTesoreria.class)
  FlussoTesoreria getByCodIpaDeAnnoBollettaCodBolletta(String codIpaEnte, String deAnnoBolletta, String codBolletta);

  @SqlQuery(
      "select "+ FlussoTesoreria.ALIAS+ALL_FIELDS+", "+ Ente.FIELDS+", "+ ManageFlusso.FIELDS +
          "  from mygov_flusso_tesoreria " + FlussoTesoreria.ALIAS +
          "  join mygov_ente "+Ente.ALIAS+" on "+Ente.ALIAS+".mygov_ente_id = "+FlussoTesoreria.ALIAS+".mygov_ente_id"  +
          "  left join mygov_manage_flusso "+ManageFlusso.ALIAS+" on "+ManageFlusso.ALIAS+".mygov_manage_flusso_id = "+FlussoTesoreria.ALIAS+".mygov_manage_flusso_id" +
          " where "+FlussoTesoreria.ALIAS+".mygov_flusso_tesoreria_id = :flussoTesoreriaId"
  )
  @RegisterFieldMapper(FlussoTesoreria.class)
  FlussoTesoreria getById(Long flussoTesoreriaId);

  @SqlQuery(
      "select "+ FlussoTesoreria.ALIAS+ALL_FIELDS+", "+ Ente.FIELDS+", "+ ManageFlusso.FIELDS +
          "  from mygov_flusso_tesoreria " + FlussoTesoreria.ALIAS +
          "  join mygov_ente "+Ente.ALIAS+" on "+Ente.ALIAS+".mygov_ente_id = "+FlussoTesoreria.ALIAS+".mygov_ente_id"  +
          "  left join mygov_manage_flusso "+ManageFlusso.ALIAS+" on "+ManageFlusso.ALIAS+".mygov_manage_flusso_id = "+FlussoTesoreria.ALIAS+".mygov_manage_flusso_id" +
          WHERE_CLAUSE +
          " limit <queryLimit>"
  )
  @RegisterFieldMapper(FlussoTesoreria.class)
  List<FlussoTesoreria> search(String codIpaEnte, String iuv, String annoBolletta, String codBolletta, String idr, BigDecimal importo,
                               String annoDocumento, String codDocumento, String annoProvvisorio, String codProvvisorio, String ordinante,
                               LocalDate dtContabileFrom, LocalDate dtContabileTo, LocalDate dtValutaFrom, LocalDate dtValutaTo, @Define int queryLimit);

  @SqlQuery(
      "select count("+ FlussoTesoreria.ALIAS+ALL_FIELDS+")" +
          "  from mygov_flusso_tesoreria " + FlussoTesoreria.ALIAS +
          "  join mygov_ente "+Ente.ALIAS+" on "+Ente.ALIAS+".mygov_ente_id = "+FlussoTesoreria.ALIAS+".mygov_ente_id"  +
          WHERE_CLAUSE
  )
  Integer searchCount(String codIpaEnte, String iuv, String annoBolletta, String codBolletta, String idr, BigDecimal importo,
                      String annoDocumento, String codDocumento, String annoProvvisorio, String codProvvisorio, String ordinante,
                      LocalDate dtContabileFrom, LocalDate dtContabileTo, LocalDate dtValutaFrom, LocalDate dtValutaTo);
}
