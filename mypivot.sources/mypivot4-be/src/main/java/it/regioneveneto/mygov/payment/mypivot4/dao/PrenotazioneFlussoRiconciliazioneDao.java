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
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoExportTo;
import it.regioneveneto.mygov.payment.mypivot4.model.AnagraficaStato;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.PrenotazioneFlussoRiconciliazione;
import it.regioneveneto.mygov.payment.mypivot4.model.Utente;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDate;
import java.util.List;

public interface PrenotazioneFlussoRiconciliazioneDao extends BaseDao {

  @SqlUpdate(
      "insert into mygov_prenotazione_flusso_riconciliazione (" +
          "  mygov_prenotazione_flusso_riconciliazione_id" +
          ", version" +
          ", mygov_ente_id" +
          ", mygov_anagrafica_stato_id" +
          ", mygov_utente_id" +
          ", cod_request_token" +
          ", de_nome_file_generato" +
          ", num_dimensione_file_generato" +
          ", cod_codice_classificazione" +
          ", de_tipo_dovuto" +
          ", cod_id_univoco_versamento" +
          ", cod_id_univoco_rendicontazione" +
          ", dt_data_ultimo_aggiornamento_da" +
          ", dt_data_ultimo_aggiornamento_a" +
          ", dt_data_esecuzione_da" +
          ", dt_data_esecuzione_a" +
          ", dt_data_esito_da" +
          ", dt_data_esito_a" +
          ", dt_data_regolamento_da" +
          ", dt_data_regolamento_a" +
          ", dt_data_contabile_da" +
          ", dt_data_contabile_a" +
          ", dt_data_valuta_da" +
          ", dt_data_valuta_a" +
          ", cod_id_univoco_dovuto" +
          ", cod_id_univoco_riscossione" +
          ", cod_id_univoco_pagatore" +
          ", de_anagrafica_pagatore" +
          ", cod_id_univoco_versante" +
          ", de_anagrafica_versante" +
          ", de_denominazione_attestante" +
          ", de_ordinante" +
          ", cod_id_regolamento" +
          ", cod_conto_tesoreria" +
          ", de_importo_tesoreria" +
          ", de_causale" +
          ", dt_creazione" +
          ", dt_ultima_modifica" +
          ", versione_tracciato" +
          ", cod_bolletta" +
          ", cod_documento" +
          ", cod_provvisorio" +
          ", de_anno_bolletta" +
          ", de_anno_documento" +
          ", de_anno_provvisorio" +
          ") values (" +
          "  nextval('mygov_pren_flus_ric_mygov_pren_flus_ric_id_seq')" +
          ", :d.version" +
          ", :d.mygovEnteId.mygovEnteId" +
          ", :d.mygovAnagraficaStatoId.mygovAnagraficaStatoId" +
          ", :d.mygovUtenteId.mygovUtenteId" +
          ", :d.codRequestToken" +
          ", :d.deNomeFileGenerato" +
          ", :d.numDimensioneFileGenerato" +
          ", :d.codCodiceClassificazione" +
          ", :d.deTipoDovuto" +
          ", :d.codIdUnivocoVersamento" +
          ", :d.codIdUnivocoRendicontazione" +
          ", :d.dtDataUltimoAggiornamentoDa" +
          ", :d.dtDataUltimoAggiornamentoA" +
          ", :d.dtDataEsecuzioneDa" +
          ", :d.dtDataEsecuzioneA" +
          ", :d.dtDataEsitoDa" +
          ", :d.dtDataEsitoA" +
          ", :d.dtDataRegolamentoDa" +
          ", :d.dtDataRegolamentoA" +
          ", :d.dtDataContabileDa" +
          ", :d.dtDataContabileA" +
          ", :d.dtDataValutaDa" +
          ", :d.dtDataValutaA" +
          ", :d.codIdUnivocoDovuto" +
          ", :d.codIdUnivocoRiscossione" +
          ", :d.codIdUnivocoPagatore" +
          ", :d.deAnagraficaPagatore" +
          ", :d.codIdUnivocoVersante" +
          ", :d.deAnagraficaVersante" +
          ", :d.deDenominazioneAttestante" +
          ", :d.deOrdinante" +
          ", :d.codIdRegolamento" +
          ", :d.codContoTesoreria" +
          ", :d.deImportoTesoreria" +
          ", :d.deCausale" +
          ", coalesce(:d.dtCreazione, now())" +
          ", coalesce(:d.dtUltimaModifica, now())" +
          ", :d.versioneTracciato" +
          ", :d.codBolletta" +
          ", :d.codDocumento" +
          ", :d.codProvvisorio" +
          ", :d.deAnnoBolletta" +
          ", :d.deAnnoDocumento" +
          ", :d.deAnnoProvvisorio" +
          ")"
  )
  @GetGeneratedKeys("mygov_prenotazione_flusso_riconciliazione_id")
  long insert(@BindBean("d") PrenotazioneFlussoRiconciliazione d);

  @SqlQuery(
      "    select " +
          "  "+PrenotazioneFlussoRiconciliazione.ALIAS+".mygov_prenotazione_flusso_riconciliazione_id " +
          ", "+PrenotazioneFlussoRiconciliazione.ALIAS+".de_nome_file_generato " +
          ", "+PrenotazioneFlussoRiconciliazione.ALIAS+".num_dimensione_file_generato " +
          ", "+PrenotazioneFlussoRiconciliazione.ALIAS+".dt_creazione " +
          ", "+PrenotazioneFlussoRiconciliazione.ALIAS+".cod_codice_classificazione " +
          ", "+PrenotazioneFlussoRiconciliazione.ALIAS+".versione_tracciato " +
          ", coalesce("+Utente.ALIAS+".de_firstname, '') as de_firstname " +
          ", coalesce("+Utente.ALIAS+".de_lastname, '') as de_lastname " +
          ", "+AnagraficaStato.ALIAS+".cod_stato " +
          ", "+AnagraficaStato.ALIAS+".de_stato " +
          ", "+AnagraficaStato.ALIAS+".de_tipo_stato " +
          "  from mygov_prenotazione_flusso_riconciliazione " + PrenotazioneFlussoRiconciliazione.ALIAS +
          "  join mygov_ente " + Ente.ALIAS +
          "    on "+PrenotazioneFlussoRiconciliazione.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          "  join mygov_anagrafica_stato " + AnagraficaStato.ALIAS +
          "    on "+PrenotazioneFlussoRiconciliazione.ALIAS+".mygov_anagrafica_stato_id = "+AnagraficaStato.ALIAS+".mygov_anagrafica_stato_id " +
          "  join mygov_utente " + Utente.ALIAS +
          "    on "+PrenotazioneFlussoRiconciliazione.ALIAS+".mygov_utente_id = "+Utente.ALIAS+".mygov_utente_id " +
          " where "+PrenotazioneFlussoRiconciliazione.ALIAS+".mygov_ente_id = :mygovEnteId " +
          "   and "+Utente.ALIAS+".cod_fed_user_id = :codFedUserId " +
          "   and (:nomeFile is null or "+PrenotazioneFlussoRiconciliazione.ALIAS+".de_nome_file_generato ilike '%' || :nomeFile || '%')" +
          "   and (("+AnagraficaStato.ALIAS+".de_tipo_stato = '"+ Constants.DE_TIPO_STATO_PRENOTA_FLUSSO_RICONCILIAZIONE+"'" +
          "          and "+AnagraficaStato.ALIAS+".cod_stato in (<listCodStatoRiconciliazione>)) " +
          "       or ("+AnagraficaStato.ALIAS+".de_tipo_stato = '"+ Constants.DE_TIPO_STATO_ALL+"'" +
          "          and "+AnagraficaStato.ALIAS+".cod_stato = '"+Constants.COD_TIPO_STATO_IN_CARICO+"')) " +
          "   and ("+PrenotazioneFlussoRiconciliazione.ALIAS+".dt_creazione >= :dateFrom and "+PrenotazioneFlussoRiconciliazione.ALIAS+".dt_creazione < :dateTo)"
  )
  List<FlussoExportTo> getByNomeFileDtCreazione(Long mygovEnteId, String codFedUserId, String nomeFile,
                                                @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> listCodStatoRiconciliazione,
                                                LocalDate dateFrom, LocalDate dateTo);

  @SqlQuery(
      "    select "+PrenotazioneFlussoRiconciliazione.ALIAS+ALL_FIELDS+", "+Ente.FIELDS+", "+AnagraficaStato.FIELDS+", "+Utente.FIELDS +
          "  from mygov_prenotazione_flusso_riconciliazione " + PrenotazioneFlussoRiconciliazione.ALIAS +
          "  join mygov_ente " + Ente.ALIAS +
          "    on "+PrenotazioneFlussoRiconciliazione.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          "  join mygov_anagrafica_stato " + AnagraficaStato.ALIAS +
          "    on "+PrenotazioneFlussoRiconciliazione.ALIAS+".mygov_anagrafica_stato_id = "+AnagraficaStato.ALIAS+".mygov_anagrafica_stato_id " +
          "  join mygov_utente " + Utente.ALIAS +
          "    on "+PrenotazioneFlussoRiconciliazione.ALIAS+".mygov_utente_id = "+Utente.ALIAS+".mygov_utente_id " +
          " where "+PrenotazioneFlussoRiconciliazione.ALIAS+".cod_request_token = :codRequestToken "
  )
  @RegisterFieldMapper(PrenotazioneFlussoRiconciliazione.class)
  List<PrenotazioneFlussoRiconciliazione> getByRequestToken(String codRequestToken);
}
