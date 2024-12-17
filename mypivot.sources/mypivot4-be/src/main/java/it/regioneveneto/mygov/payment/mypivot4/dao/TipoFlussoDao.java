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
import it.regioneveneto.mygov.payment.mypay4.service.common.CacheService;
import it.regioneveneto.mygov.payment.mypivot4.model.TipoFlusso;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;

import java.util.Optional;

public interface TipoFlussoDao extends BaseDao {
  @SqlQuery(
      "    select "+ TipoFlusso.ALIAS+ALL_FIELDS+
          "  from mygov_tipo_flusso "+TipoFlusso.ALIAS+
          " where "+TipoFlusso.ALIAS+".cod_tipo = :codTipo ")
  @RegisterFieldMapper(TipoFlusso.class)
  @Caching(
      put = {
          @CachePut(value = CacheService.CACHE_NAME_TIPO_FLUSSO, key = "{'id',#result.mygovTipoFlussoId}", condition="#result!=null"),
          @CachePut(value = CacheService.CACHE_NAME_TIPO_FLUSSO, key = "{'codTipo+deTipo',#result.codTipo,#result.deTipo}", condition="#result!=null")
      }
  )
  Optional<TipoFlusso> getByCodTipo(String codTipo);

  @SqlQuery(
      "    select "+TipoFlusso.ALIAS+ALL_FIELDS+
          "  from mygov_tipo_flusso "+TipoFlusso.ALIAS+
          " where "+TipoFlusso.ALIAS+".mygov_tipo_flusso_id = :mygovTipoFlussoId ")
  @RegisterFieldMapper(TipoFlusso.class)
  @Caching(
      put = {
          @CachePut(value = CacheService.CACHE_NAME_TIPO_FLUSSO, key = "{'id',#result.mygovTipoFlussoId}", condition="#result!=null"),
          @CachePut(value = CacheService.CACHE_NAME_TIPO_FLUSSO, key = "{'codTipo+deTipo',#result.codTipo,#result.deTipo}", condition="#result!=null")
      }
  )
  TipoFlusso getById(Long mygovTipoFlussoId);
}
