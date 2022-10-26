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
import it.regioneveneto.mygov.payment.mypivot4.model.VmStatisticaEnteAnnoMeseGiornoUffTdCap;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface VmStatisticaEnteAnnoMeseGiornoUffTdCapDao extends BaseDao {

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "cod_uff, " +
          "de_uff, " +
          "cod_td, " +
          "de_td, " +
          "cod_cap, " +
          "de_cap, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_uff_td_cap " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND cod_uff = :codUfficio AND cod_td = :codTipoDovuto " +
          "GROUP BY mygov_ente_id, anno, cod_uff, de_uff, cod_td, de_td, cod_cap, de_cap " +
          "ORDER BY de_cap "
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTdCap.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTdCap> getTotaliRipartitiPerCapitoliByAnnoUfficioDovuto(Long enteId, Integer year, String codUfficio, String codTipoDovuto);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "mese, " +
          "cod_uff, " +
          "de_uff, " +
          "cod_td, " +
          "de_td, " +
          "cod_cap, " +
          "de_cap, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_uff_td_cap " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND mese = :month AND cod_uff = :codUfficio AND cod_td = :codTipoDovuto " +
          "GROUP BY mygov_ente_id, anno, mese, cod_uff, de_uff, cod_td, de_td, cod_cap, de_cap " +
          "ORDER BY de_cap "
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTdCap.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTdCap> getTotaliRipartitiPerCapitoliByAnnoMeseUfficioDovuto(Long enteId, Integer year, Integer month, String codUfficio, String codTipoDovuto);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "mese, " +
          "cod_uff, " +
          "de_uff, " +
          "cod_td, " +
          "de_td, " +
          "cod_cap, " +
          "de_cap, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_giorno_uff_td_cap " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND mese = :month AND giorno = :day AND cod_uff = :codUfficio AND cod_td = :codTipoDovuto " +
          "GROUP BY mygov_ente_id, anno, mese, giorno, cod_uff, de_uff, cod_td, de_td, cod_cap, de_cap " +
          "ORDER BY de_cap"
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTdCap.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTdCap> getTotaliRipartitiPerCapitoliByAnnoMeseGiornoUfficioDovuto(Long enteId, Integer year, Integer month, Integer day, String codUfficio, String codTipoDovuto);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "cod_uff, " +
          "de_uff, " +
          "cod_td, " +
          "de_td, " +
          "cod_cap, " +
          "de_cap, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_uff_td_cap " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND cod_td = :codTipoDovuto " +
          "GROUP BY mygov_ente_id, anno, cod_uff, de_uff, cod_td, de_td, cod_cap, de_cap " +
          "ORDER BY cod_uff, cod_cap"
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTdCap.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTdCap> getTotaliRipartitiPerCapitoliByAnnoDovuto(Long enteId, Integer year, String codTipoDovuto);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno,		" +
          "mese, 		" +
          "cod_uff, 	" +
          "de_uff, 	" +
          "cod_td, 	" +
          "de_td, 	" +
          "cod_cap, 	" +
          "de_cap, 	" +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_uff_td_cap " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND mese = :month AND cod_td = :codTipoDovuto " +
          "GROUP BY mygov_ente_id, anno, mese, cod_uff, de_uff, cod_td, de_td, cod_cap, de_cap " +
          "ORDER BY cod_uff, cod_cap"
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTdCap.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTdCap> getTotaliRipartitiPerCapitoliByAnnoMeseDovuto(Long enteId, Integer year, Integer month, String codTipoDovuto);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "mese, " +
          "giorno, " +
          "cod_uff, " +
          "de_uff, " +
          "cod_td, " +
          "de_td, " +
          "cod_cap, " +
          "de_cap, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_giorno_uff_td_cap " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND mese = :month AND giorno = :day AND cod_td = :codTipoDovuto " +
          "GROUP BY mygov_ente_id, anno, mese, giorno, cod_uff, de_uff, cod_td, de_td, cod_cap, de_cap " +
          "ORDER BY cod_uff, cod_cap"
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTdCap.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTdCap> getTotaliRipartitiPerCapitoliByAnnoMeseGiornoDovuto(Long enteId, Integer year, Integer month, Integer day, String codTipoDovuto);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, " +
          "cod_uff, " +
          "de_uff, " +
          "cod_td, " +
          "de_td, " +
          "cod_cap, " +
          "de_cap, " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_uff_td_cap " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND cod_uff = :codUfficio " +
          "GROUP BY mygov_ente_id, anno, cod_uff, de_uff, cod_td, de_td, cod_cap, de_cap " +
          "ORDER BY cod_td, cod_cap"
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTdCap.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTdCap> getTotaliRipartitiPerCapitoliByAnnoUfficio(Long enteId, Integer year, String codUfficio);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, 	   " +
          "mese, 	   " +
          "cod_uff,  " +
          "de_uff,   " +
          "cod_td,   " +
          "de_td,    " +
          "cod_cap,  " +
          "de_cap,   " +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_uff_td_cap " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND mese = :month AND cod_uff = :codUfficio " +
          "GROUP BY mygov_ente_id, anno, mese, cod_uff, de_uff, cod_td, de_td, cod_cap, de_cap " +
          "ORDER BY cod_td, cod_cap"
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTdCap.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTdCap> getTotaliRipartitiPerCapitoliByAnnoMeseUfficio(Long enteId, Integer year, Integer month, String codUfficio);

  @SqlQuery(
      "SELECT " +
          "mygov_ente_id, " +
          "anno, 		" +
          "mese, 		" +
          "giorno, 	" +
          "cod_uff, 	" +
          "de_uff, 	" +
          "cod_td, 	" +
          "de_td, 	" +
          "cod_cap, 	" +
          "de_cap, 	" +
          "SUM(imp_pag)  AS imp_pag, " +
          "SUM(imp_rend) AS imp_rend, " +
          "SUM(imp_inc)  AS imp_inc " +
          "FROM vm_statistica_ente_anno_mese_giorno_uff_td_cap " +
          "WHERE mygov_ente_id = :enteId AND anno = :year AND mese = :month AND giorno = :day AND cod_uff = :codUfficio " +
          "GROUP BY mygov_ente_id, anno, mese, giorno, cod_uff, de_uff, cod_td, de_td, cod_cap, de_cap " +
          "ORDER BY cod_td, cod_cap"
  )
  @RegisterFieldMapper(VmStatisticaEnteAnnoMeseGiornoUffTdCap.class)
  List<VmStatisticaEnteAnnoMeseGiornoUffTdCap> getTotaliRipartitiPerCapitoliByAnnoMeseGiornoUfficio(Long enteId, Integer year, Integer month, Integer day, String codUfficio);
}
