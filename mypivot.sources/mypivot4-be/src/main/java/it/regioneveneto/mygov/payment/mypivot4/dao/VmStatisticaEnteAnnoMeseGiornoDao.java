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
import it.regioneveneto.mygov.payment.mypivot4.model.VmStatisticaEnteAnnoMeseGiorno;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.time.LocalDate;
import java.util.List;

public interface VmStatisticaEnteAnnoMeseGiornoDao extends BaseDao {

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "SUM(num_pag)  AS num_pag,  " +
          "SUM(imp_pag)  AS imp_pag,  " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc 	" +
          "FROM vm_statistica_ente_anno_mese " +
          "WHERE mygov_ente_id = :enteId AND anno in (<years>) " +
          "GROUP BY mygov_ente_id, anno " +
          "ORDER BY anno "
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiorno.class)
  List<VmStatisticaEnteAnnoMeseGiorno> getTotaliPerAnno(Long enteId, @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<Integer> years);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, anno, mese, num_pag, imp_pag, imp_rend, imp_inc " +
          "FROM vm_statistica_ente_anno_mese " +
          "WHERE mygov_ente_id = :enteId AND anno = :year " +
          "AND (mese in (<months>) OR null in (<months>)) " +
          "ORDER BY mese"
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiorno.class)
  List<VmStatisticaEnteAnnoMeseGiorno> getTotaliPerAnnoMese(Long enteId, Integer year, @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<Integer> months);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, anno, mese, giorno, num_pag, imp_pag, imp_rend, imp_inc " +
          "FROM vm_statistica_ente_anno_mese_giorno " +
          "WHERE mygov_ente_id = :enteId " +
          "AND to_date(anno || '-' || mese || '-' || giorno, 'YYYY-MM-DD') BETWEEN :fromDay AND :toDay " +
          "ORDER BY to_date(anno || '-' || mese || '-' || giorno, 'YYYY-MM-DD') "
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiorno.class)
  List<VmStatisticaEnteAnnoMeseGiorno> getTotaliPerAnnoMeseGiorno(Long enteId, LocalDate fromDay, LocalDate toDay);


}
