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
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
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

public interface ManageFlussoDao extends BaseDao {

  @SqlUpdate(
      "insert into mygov_manage_flusso (" +
          "  mygov_manage_flusso_id"+
          ", version"+
          ", mygov_ente_id"+
          ", identificativo_psp"+
          ", cod_identificativo_flusso"+
          ", dt_data_ora_flusso"+
          ", mygov_tipo_flusso_id"+
          ", mygov_utente_id"+
          ", mygov_anagrafica_stato_id"+
          ", de_percorso_file"+
          ", de_nome_file"+
          ", num_dimensione_file_scaricato"+
          ", cod_request_token"+
          ", dt_creazione"+
          ", dt_ultima_modifica"+
          ", cod_provenienza_file"+
          ", id_chiave_multitabella"+
          ", de_nome_file_scarti"+
          ", cod_errore"+
          ", num_righe_totali"+
          ", num_righe_importate_correttamente"+
          ") values ("+
          "  nextval('mygov_manage_flusso_mygov_manage_flusso_id_seq')"+
          ", :d.version"+
          ", :d.mygovEnteId.mygovEnteId"+
          ", :d.identificativoPsp"+
          ", :d.codIdentificativoFlusso"+
          ", :d.dtDataOraFlusso"+
          ", :d.mygovTipoFlussoId.mygovTipoFlussoId"+
          ", :d.mygovUtenteId.mygovUtenteId"+
          ", :d.mygovAnagraficaStatoId.mygovAnagraficaStatoId"+
          ", :d.dePercorsoFile"+
          ", :d.deNomeFile"+
          ", :d.numDimensioneFileScaricato"+
          ", :d.codRequestToken"+
          ", :d.dtCreazione"+
          ", :d.dtUltimaModifica"+
          ", :d.codProvenienzaFile"+
          ", :d.idChiaveMultitabella"+
          ", :d.deNomeFileScarti"+
          ", :d.codErrore"+
          ", :d.numRigheTotali"+
          ", :d.numRigheImportateCorrettamente)")
  @GetGeneratedKeys("mygov_manage_flusso_id")
  long insert(@BindBean("d") ManageFlusso d);

  @SqlUpdate(
      "    update mygov_manage_flusso set " +
          "   version = :d.version" +
          " , mygov_ente_id = :d.mygovEnteId.mygovEnteId" +
          " , identificativo_psp = :d.identificativoPsp" +
          " , cod_identificativo_flusso = :d.codIdentificativoFlusso" +
          " , dt_data_ora_flusso = :d.dtDataOraFlusso" +
          " , mygov_tipo_flusso_id = :d.mygovTipoFlussoId.mygovTipoFlussoId" +
          " , mygov_utente_id = :d.mygovUtenteId.mygovUtenteId" +
          " , mygov_anagrafica_stato_id = :d.mygovAnagraficaStatoId.mygovAnagraficaStatoId" +
          " , de_percorso_file = :d.dePercorsoFile" +
          " , de_nome_file = :d.deNomeFile" +
          " , num_dimensione_file_scaricato = :d.numDimensioneFileScaricato" +
          " , cod_request_token = :d.codRequestToken" +
          " , dt_creazione = :d.dtCreazione" +
          " , dt_ultima_modifica = :d.dtUltimaModifica" +
          " , cod_request_token = :d.codRequestToken" +
          " , cod_provenienza_file = :d.codProvenienzaFile" +
          " , id_chiave_multitabella = :d.idChiaveMultitabella" +
          " , de_nome_file_scarti = :d.deNomeFileScarti" +
          " , cod_errore = :d.codErrore" +
          " , num_righe_totali = :d.numRigheTotali" +
          " , num_righe_importate_correttamente = :d.numRigheImportateCorrettamente" +
          " where mygov_manage_flusso_id = :d.mygovManageFlussoId"
  )
  int update(@BindBean("d") ManageFlusso d);

  @SqlQuery(
      "    select "+ManageFlusso.ALIAS+ALL_FIELDS +", "+AnagraficaStato.FIELDS+", "+Ente.FIELDS +
          "  from mygov_manage_flusso " + ManageFlusso.ALIAS +
          "  join mygov_ente " + Ente.ALIAS + "    on "+ManageFlusso.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          "  join mygov_anagrafica_stato "+ AnagraficaStato.ALIAS+
          "    on "+AnagraficaStato.ALIAS+".mygov_anagrafica_stato_id = "+ManageFlusso.ALIAS+".mygov_anagrafica_stato_id " +
          " where "+ManageFlusso.ALIAS+".mygov_manage_flusso_id = :mygovManageFlussoId ")
  @RegisterFieldMapper(ManageFlusso.class)
  ManageFlusso getById(Long mygovManageFlussoId);

  @SqlQuery(
      "    select " + ManageFlusso.ALIAS + ALL_FIELDS +
          "  from mygov_manage_flusso " + ManageFlusso.ALIAS +
          "  join mygov_ente " + Ente.ALIAS +
          "    on "+ManageFlusso.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          " where "+ManageFlusso.ALIAS+".mygov_ente_id = :mygovEnteId " +
          " order by " + ManageFlusso.ALIAS + ".codIdentificativoFlusso")
  @RegisterFieldMapper(ManageFlusso.class)
  List<ManageFlusso> getByEnte(Long mygovEnteId);

  String SQL_SEARCH =
    "  from mygov_manage_flusso " + ManageFlusso.ALIAS +
    "  join mygov_ente "+Ente.ALIAS+" on "+ManageFlusso.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
    "  join mygov_anagrafica_stato "+ AnagraficaStato.ALIAS+
    "    on "+AnagraficaStato.ALIAS+".mygov_anagrafica_stato_id = "+ManageFlusso.ALIAS+".mygov_anagrafica_stato_id " +
    "  join mygov_tipo_flusso "+ TipoFlusso.ALIAS+
    "    on "+TipoFlusso.ALIAS+".mygov_tipo_flusso_id = "+ManageFlusso.ALIAS+".mygov_tipo_flusso_id " +
    "  left join mygov_utente "+ Utente.ALIAS+
    "    on "+Utente.ALIAS+".mygov_utente_id = "+ManageFlusso.ALIAS+".mygov_utente_id " +
    " where "+ManageFlusso.ALIAS+".mygov_ente_id = :mygovEnteId " +
    "   and "+TipoFlusso.ALIAS+".cod_tipo = :codTipo " +
    "   and ("+ManageFlusso.ALIAS+".dt_creazione >= :dateFrom::DATE and "+ManageFlusso.ALIAS+".dt_creazione < :dateTo::DATE) " +
    "   and (:codIdentificativoFlusso is null or "+ManageFlusso.ALIAS+".de_nome_file ilike '%' || :codIdentificativoFlusso || '%')" +
    "   and "+AnagraficaStato.ALIAS+".de_tipo_stato = '"+Constants.DE_TIPO_STATO_MANAGE+"'" +
    "   and "+AnagraficaStato.ALIAS+".cod_stato in (<listCodStatoManage>) " +
    "   and (:codFedUserId is null or "+Utente.ALIAS+".cod_fed_user_id = :codFedUserId) ";
  @SqlQuery(
      "    select "+ManageFlusso.ALIAS+ALL_FIELDS +", "+AnagraficaStato.FIELDS+", "+Ente.FIELDS+", "+ Utente.FIELDS+", "+ TipoFlusso.FIELDS +
        SQL_SEARCH +
        " order by " + ManageFlusso.ALIAS + ".dt_creazione desc " +
        " limit <queryLimit>")
  @RegisterFieldMapper(ManageFlusso.class)
  List<ManageFlusso> getByEnteCodIdentificativoFlussoCreateDt(Long mygovEnteId, String codFedUserId, String codTipo, String codIdentificativoFlusso, LocalDate dateFrom, LocalDate dateTo,
                                                              @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> listCodStatoManage, @Define int queryLimit);

  @SqlQuery(
    "    select count(1) " +
      SQL_SEARCH)
  int getByEnteCodIdentificativoFlussoCreateDtCount(Long mygovEnteId, String codFedUserId, String codTipo, String codIdentificativoFlusso, LocalDate dateFrom, LocalDate dateTo,
                                                              @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> listCodStatoManage);

  @SqlQuery(
      "select count(*) from mygov_manage_flusso where de_nome_file = :deNomeFile"
  )
  int countDuplicateFileName(String deNomeFile);

  @SqlQuery(
      "    select "+ManageFlusso.ALIAS+ALL_FIELDS +", "+AnagraficaStato.FIELDS+", "+Ente.FIELDS+", "+ Utente.FIELDS+", "+ TipoFlusso.FIELDS +
          "  from mygov_manage_flusso " + ManageFlusso.ALIAS +
          "  join mygov_ente "+Ente.ALIAS+" on "+ManageFlusso.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          "  join mygov_anagrafica_stato "+ AnagraficaStato.ALIAS+
          "    on "+AnagraficaStato.ALIAS+".mygov_anagrafica_stato_id = "+ManageFlusso.ALIAS+".mygov_anagrafica_stato_id " +
          "  join mygov_tipo_flusso "+ TipoFlusso.ALIAS+
          "    on "+TipoFlusso.ALIAS+".mygov_tipo_flusso_id = "+ManageFlusso.ALIAS+".mygov_tipo_flusso_id " +
          "  left join mygov_utente "+ Utente.ALIAS+
          "    on "+Utente.ALIAS+".mygov_utente_id = "+ManageFlusso.ALIAS+".mygov_utente_id " +
          " where "+ManageFlusso.ALIAS+".cod_request_token = :codRequestToken"
  )
  @RegisterFieldMapper(ManageFlusso.class)
  ManageFlusso getByCodRequestToken(String codRequestToken);

  @SqlQuery(
      "select count(1)>0 from mygov_manage_flusso where cod_request_token = :codRequestToken"
  )
  boolean existingRequestToken(String codRequestToken);

  @SqlQuery(
          "    select "+ManageFlusso.ALIAS+".mygov_manage_flusso_id" +
                  "  from mygov_manage_flusso " + ManageFlusso.ALIAS +
                  "  join mygov_ente "+Ente.ALIAS+" on "+ManageFlusso.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
                  "  join mygov_anagrafica_stato "+ AnagraficaStato.ALIAS+
                  "    on "+AnagraficaStato.ALIAS+".mygov_anagrafica_stato_id = "+ManageFlusso.ALIAS+".mygov_anagrafica_stato_id " +
                  "  join mygov_tipo_flusso "+ TipoFlusso.ALIAS+
                  "    on "+TipoFlusso.ALIAS+".mygov_tipo_flusso_id = "+ManageFlusso.ALIAS+".mygov_tipo_flusso_id " +
                  "  left join mygov_utente "+ Utente.ALIAS+
                  "    on "+Utente.ALIAS+".mygov_utente_id = "+ManageFlusso.ALIAS+".mygov_utente_id " +
                  " where "+TipoFlusso.ALIAS+".cod_tipo = :codTipo " +
                  "   and "+Ente.ALIAS+".codice_fiscale_ente = :codiceFiscaleEnte "
  )
  Long getByTypeSecondaryEnte(String codTipo, String codiceFiscaleEnte);
}
