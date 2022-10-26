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
import it.regioneveneto.mygov.payment.mypivot4.model.InfoMappingTesoreria;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface InfoMappingTesoreriaDao extends BaseDao {

  @SqlUpdate(
      "insert into mygov_info_mapping_tesoreria (" +
          "   mygov_info_mapping_tesoreria_id" +
          " , mygov_manage_flusso_id" +
          " , pos_de_anno_bolletta" +
          " , pos_cod_bolletta" +
          " , pos_dt_contabile" +
          " , pos_de_denominazione" +
          " , pos_de_causale" +
          " , pos_num_importo" +
          " , pos_dt_valuta" +
          " , dt_creazione" +
          " , dt_ultima_modifica" +
          ") values (" +
          "   nextval('mygov_info_mapping_tesoreria_mygov_info_mapping_tes_id_seq')" +
          " , :d.mygovManageFlussoId.mygovManageFlussoId" +
          " , :d.posDeAnnoBolletta" +
          " , :d.posCodBolletta" +
          " , :d.posDtContabile" +
          " , :d.posDeDenominazione" +
          " , :d.posDeCausale" +
          " , :d.posNumImporto" +
          " , :d.posDtValuta" +
          " , now()" +
          " , now())")
  @GetGeneratedKeys("mygov_info_mapping_tesoreria_id")
  long insert(@BindBean("d") InfoMappingTesoreria d);
}
