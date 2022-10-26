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
import it.regioneveneto.mygov.payment.mypivot4.model.VmStatisticaEnteAnnoMeseGiornoUffTd;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

public interface VmStatisticaEnteAnnoMeseGiornoUffTdDao extends BaseDao {

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "cod_uff, " +
          "de_uff, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
      "FROM vm_statistica_ente_anno_mese_uff_td " +
      "WHERE mygov_ente_id = :enteId AND anno = :year AND cod_td in (<codTipiDovuto>) " +
      "GROUP BY mygov_ente_id, anno, cod_uff, de_uff " +
      "ORDER BY cod_uff "
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTd.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTd> getTotaliRipartitiPerUfficiByAnno(Long enteId, Integer year,
                                                                              @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> codTipiDovuto);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "mese, " +
          "cod_uff, " +
          "de_uff, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_uff_td " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND mese = :month AND cod_td in (<codTipiDovuto>) " +
          "GROUP BY mygov_ente_id, anno, mese, cod_uff, de_uff " +
          "ORDER BY cod_uff "
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTd.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTd> getTotaliRipartitiPerUfficiByAnnoMese(Long enteId, Integer year, Integer month,
                                                                                  @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> codTipiDovuto);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "mese, " +
          "giorno, " +
          "cod_uff, " +
          "de_uff, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_giorno_uff_td " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND mese = :month AND giorno = :day AND cod_td in (<codTipiDovuto>) " +
          "GROUP BY mygov_ente_id, anno, mese, giorno, cod_uff, de_uff " +
          "ORDER BY cod_uff"
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTd.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTd> getTotaliRipartitiPerUfficiByAnnoMeseGiorno(Long enteId, Integer year, Integer month, Integer day,
                                                                                        @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> codTipiDovuto);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "cod_td, " +
          "de_td, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_uff_td " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND cod_td in (<codTipiDovuto>) " +
          "GROUP BY mygov_ente_id, anno, cod_td, de_td " +
          "ORDER BY de_td "
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTd.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTd> getTotaliRipartitiPerTipiDovutoByAnno(Long enteId, Integer year,
                                                                                  @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> codTipiDovuto);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "mese, " +
          "cod_td, " +
          "de_td, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_uff_td " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND mese = :month AND cod_td in (<codTipiDovuto>) " +
          "GROUP BY mygov_ente_id, anno, mese, cod_td, de_td " +
          "ORDER BY de_td "
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTd.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTd> getTotaliRipartitiPerTipiDovutoByAnnoMese(Long enteId, Integer year, Integer month,
                                                                                      @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> codTipiDovuto);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "mese, " +
          "giorno, " +
          "cod_td, " +
          "de_td, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_giorno_uff_td " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND mese = :month AND giorno = :day AND cod_td in (<codTipiDovuto>) " +
          "GROUP BY mygov_ente_id, anno, mese, giorno, cod_td, de_td " +
          "ORDER BY de_td"
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTd.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTd> getTotaliRipartitiPerTipiDovutoByAnnoMeseGiorno(Long enteId,  Integer year, Integer month, Integer day,
                                                                                            @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> codTipiDovuto);
  }
