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
import it.regioneveneto.mygov.payment.mypivot4.dto.PagamentiRiconciliatiTo;
import it.regioneveneto.mygov.payment.mypivot4.model.ExportRendicontazioneCompleta;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.Date;
import java.util.List;

public interface ExportRendicontazioneCompletaDao extends BaseDao {

  @SqlQuery(
      "select "+ExportRendicontazioneCompleta.ALIAS+ALL_FIELDS +
          " from mygov_export_rendicontazione_completa "+ ExportRendicontazioneCompleta.ALIAS +
          " where "+ExportRendicontazioneCompleta.ALIAS+".mygov_ente_id_e = :mygovEnteId " +
          "   and greatest(coalesce(dt_acquisizione_e,'2099-12-31'), coalesce(dt_acquisizione_r,'2014-01-01')) >= :dateFrom " +
          "   and greatest(coalesce(dt_acquisizione_e,'2099-12-31'), coalesce(dt_acquisizione_r,'2014-01-01')) < :dateTo " +
          "   and (concat(<listCodTipo>)='' or cod_tipo_dovuto_e in (<listCodTipo>)) " +
          " order by cod_tipo_dovuto_e, greatest(dt_acquisizione_e, dt_acquisizione_r)" +
          " limit 1000"
      )
  List<PagamentiRiconciliatiTo> getByDate(Long mygovEnteId, Date dateFrom, Date dateTo,
                                          @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> listCodTipo);

  @SqlQuery(
      "select "+ExportRendicontazioneCompleta.ALIAS+ALL_FIELDS +
          " from mygov_export_rendicontazione_completa "+ ExportRendicontazioneCompleta.ALIAS +
          " where "+ExportRendicontazioneCompleta.ALIAS+".mygov_ente_id_e = :mygovEnteId " +
          "   and (concat(<listIuf>)='' or upper(cod_identificativo_flusso_r) in (<listIuf>)) " +
          "   and (concat(<listIuv>)='' or cod_e_dati_pag_id_univoco_versamento_e in (<listIuv>)) " +
          "   and (concat(<listCodTipo>)='' or cod_tipo_dovuto_e in (<listCodTipo>)) " +
          " order by cod_tipo_dovuto_e, greatest(dt_acquisizione_e, dt_acquisizione_r)"
  )
  List<PagamentiRiconciliatiTo> getByIUVIUF(Long mygovEnteId, @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> listIuf,
                                                       @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> listIuv,
                                                       @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> listCodTipo);

}
