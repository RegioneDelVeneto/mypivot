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
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface FlussoRendicontazioneDao extends BaseDao {

  String WHERE_CLAUSE = "  WHERE CASE WHEN :id IS NOT NULL THEN " + FlussoRendicontazione.ALIAS+".mygov_ente_id = :id ELSE true END" +
      "  AND   CASE WHEN :dateRegolFrom::date IS NOT NULL THEN " + FlussoRendicontazione.ALIAS+".dt_data_regolamento >= :dateRegolFrom::date ELSE true END" +
      "  AND   CASE WHEN :dateRegolTo::date IS NOT NULL THEN " + FlussoRendicontazione.ALIAS+".dt_data_regolamento <= :dateRegolTo::date ELSE true END" +
      "  AND   CASE WHEN :idRendicontazione IS NOT NULL IS TRUE THEN upper(" + FlussoRendicontazione.ALIAS+".cod_identificativo_flusso) like '%' || upper(:idRendicontazione) || '%' ELSE true END" +
      "  AND   CASE WHEN :idRegolamento IS NOT NULL IS TRUE THEN " + FlussoRendicontazione.ALIAS+".cod_identificativo_univoco_regolamento = :idRegolamento ELSE true END";

  String QUERY_PAGAMENTI_DOPPI_BASE =
      "  from mygov_flusso_rendicontazione " + FlussoRendicontazione.ALIAS +
          "  join mygov_ente "+Ente.ALIAS+" on "+Ente.ALIAS+".mygov_ente_id = "+FlussoRendicontazione.ALIAS+".mygov_ente_id" +
          "  join mygov_flusso_export "+ FlussoExport.ALIAS +
          "    on "+FlussoExport.ALIAS+".mygov_ente_id = "+FlussoRendicontazione.ALIAS+".mygov_ente_id" +
          "   and "+FlussoExport.ALIAS+".cod_rp_silinviarp_id_univoco_versamento = "+FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_identificativo_univoco_versamento" +
          "   and "+FlussoExport.ALIAS+".cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss = "+FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_identificativo_univoco_riscossione" +
          " where "+FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_codice_esito_singolo_pagamento in ('0','9') " +
          "   and "+Ente.ALIAS+".cod_ipa_ente = :codice_ipa_ente " +
          "   and case when :dt_data_ultimo_aggiornamento_da::date is not null then "+FlussoRendicontazione.ALIAS+".dt_ultima_modifica >= :dt_data_ultimo_aggiornamento_da::date else true end " +
          "   and case when :dt_data_ultimo_aggiornamento_a::date is not null then "+FlussoRendicontazione.ALIAS+".dt_ultima_modifica <= :dt_data_ultimo_aggiornamento_a::date else true end " +
          "   and case when :data_esito_singolo_pagamento_da::date is not null then "+FlussoRendicontazione.ALIAS+".dt_dati_sing_pagam_data_esito_singolo_pagamento >= :data_esito_singolo_pagamento_da::date else true end " +
          "   and case when :data_esito_singolo_pagamento_a::date is not null then "+FlussoRendicontazione.ALIAS+".dt_dati_sing_pagam_data_esito_singolo_pagamento <= :data_esito_singolo_pagamento_a::date else true end " +
          "   and case when :data_regolamento_da::date is not null then "+FlussoRendicontazione.ALIAS+".dt_data_regolamento >= :data_regolamento_da::date else true end " +
          "   and case when :data_regolamento_a::date is not null then "+FlussoRendicontazione.ALIAS+".dt_data_regolamento <= :data_regolamento_a::date else true end " +
          "   and case when :cod_iud is not null then "+FlussoExport.ALIAS+".cod_iud = :cod_iud else true end " +
          "   and case when :cod_iuv is not null then "+FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_identificativo_univoco_versamento = :cod_iuv else true end " +
          "   and case when :identificativo_univoco_riscossione is not null then "+FlussoExport.ALIAS+".cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss = :identificativo_univoco_riscossione else true end " +
          "   and case when :anagrafica_pagatore is not null then upper("+FlussoExport.ALIAS+".cod_e_sogg_pag_anagrafica_pagatore) like '%' || upper(:anagrafica_pagatore) || '%' else true end " +
          "   and case when :codice_identificativo_univoco_pagatore is not null then upper("+FlussoExport.ALIAS+".cod_e_sogg_pag_id_univ_pag_codice_id_univoco) like '%' || upper(:codice_identificativo_univoco_pagatore) || '%' else true end " +
          "   and case when :anagrafica_versante is not null then upper("+FlussoExport.ALIAS+".cod_e_sogg_vers_anagrafica_versante) like '%' || upper(:anagrafica_versante) || '%' else true end " +
          "   and case when :codice_identificativo_univoco_versante is not null then upper("+FlussoExport.ALIAS+".cod_e_sogg_vers_id_univ_vers_codice_id_univoco) like '%' || upper(:codice_identificativo_univoco_versante) || '%' else true end " +
          "   and case when :denominazione_attestante is not null then upper("+FlussoExport.ALIAS+".de_e_istit_att_denominazione_attestante) like '%' || upper(:denominazione_attestante) || '%' else true end " +
          "   and case when :identificativo_flusso_rendicontazione is not null then "+FlussoRendicontazione.ALIAS+".cod_identificativo_flusso = :identificativo_flusso_rendicontazione else true end " +
          "   and case when :identificativo_univoco_regolamento is not null then "+FlussoRendicontazione.ALIAS+".cod_identificativo_univoco_regolamento = :identificativo_univoco_regolamento else true end " +
          "   and case when :cod_tipo_dovuto is not null then "+FlussoExport.ALIAS+".cod_tipo_dovuto = :cod_tipo_dovuto " +
          "            else "+FlussoExport.ALIAS+".cod_tipo_dovuto in " +
          "                          (select distinct(metd.cod_tipo) " +
          "                             from mygov_operatore_ente_tipo_dovuto as moetd, mygov_ente_tipo_dovuto as metd, mygov_operatore oper " +
          "                            where moetd.mygov_ente_tipo_dovuto_id = metd.mygov_ente_tipo_dovuto_id " +
          "                              and oper.mygov_operatore_id = moetd.mygov_operatore_id " +
          "                              and oper.cod_fed_user_id = :cod_fed_user_id " +
          "                              and moetd.flg_attivo = true) " +
          "            end " +
          "   and case when :causale_versamento is not null then upper("+FlussoExport.ALIAS+".de_e_dati_pag_dati_sing_pag_causale_versamento) like '%' || upper(:causale_versamento) || '%' else true end " +
          "   and exists ( " +
          "   select 1 " +
          "     from mygov_flusso_rendicontazione r1 " +
          "    where "+FlussoRendicontazione.ALIAS+".mygov_ente_id = r1.mygov_ente_id " +
          "      and "+FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_identificativo_univoco_versamento = r1.cod_dati_sing_pagam_identificativo_univoco_versamento " +
          "      and "+FlussoRendicontazione.ALIAS+".num_dati_sing_pagam_singolo_importo_pagato = r1.num_dati_sing_pagam_singolo_importo_pagato " +
          "      and "+FlussoRendicontazione.ALIAS+".cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco = r1.cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco " +
          "      and r1.cod_dati_sing_pagam_codice_esito_singolo_pagamento = (9-"+FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_codice_esito_singolo_pagamento::INTEGER)::VARCHAR ) ";

  @SqlQuery(
      "select "+ FlussoRendicontazione.ALIAS+ALL_FIELDS+", "+Ente.FIELDS +", "+ FlussoExport.FIELDS+
          ", exists( select 1 from mygov_segnalazione "+Segnalazione.ALIAS +
          "           where "+Segnalazione.ALIAS+".classificazione_completezza = '"+ Constants.COD_ERRORE_DOPPI+"' " +
          "             and "+Segnalazione.ALIAS+".mygov_ente_id = "+Ente.ALIAS+".mygov_ente_id " +
          "             and "+Segnalazione.ALIAS+".cod_iuf = "+FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_identificativo_univoco_riscossione " +
          "             and "+Segnalazione.ALIAS+".cod_iud = "+FlussoRendicontazione.ALIAS+".indice_dati_singolo_pagamento::varchar " +
          "             and "+Segnalazione.ALIAS+".cod_iuv = "+FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_identificativo_univoco_versamento ) as has_segnalazione " +
          QUERY_PAGAMENTI_DOPPI_BASE +
          " order by "+FlussoRendicontazione.ALIAS+".dt_ultima_modifica ASC,"+FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_codice_esito_singolo_pagamento ASC" +
          " limit <queryLimit>"
  )
  @RegisterFieldMapper(FlussoRendicontazione.class)
  List<FlussoRendicontazione> searchPagamentiDoppi(@BindBean RiconciliazioneSearch in, @Define int queryLimit);

  @SqlQuery(
      "    select count(1) " +
          QUERY_PAGAMENTI_DOPPI_BASE
  )
  Integer searchPagamentiDoppiCount(@BindBean RiconciliazioneSearch in);


  @SqlQuery(
      "select " +  FlussoRendicontazione.ALIAS+ALL_FIELDS+", " + Ente.FIELDS+", " +  FlussoExport.FIELDS +
          "  from mygov_flusso_rendicontazione " + FlussoRendicontazione.ALIAS +
          "  join mygov_ente "+Ente.ALIAS+" on "+Ente.ALIAS+".mygov_ente_id = "+FlussoRendicontazione.ALIAS+".mygov_ente_id" +
          "  join mygov_flusso_export "+ FlussoExport.ALIAS +
          "    on "+FlussoExport.ALIAS+".mygov_ente_id = "+FlussoRendicontazione.ALIAS+".mygov_ente_id" +
          "   and "+FlussoExport.ALIAS+".cod_rp_silinviarp_id_univoco_versamento = "+FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_identificativo_univoco_versamento" +
          "   and "+FlussoExport.ALIAS+".cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss = "+FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_identificativo_univoco_riscossione" +
          " where " + Ente.ALIAS+".cod_ipa_ente = :codIpaEnte " +
          "   and " + FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_identificativo_univoco_riscossione = :iur " +
          "   and " + FlussoRendicontazione.ALIAS+".cod_dati_sing_pagam_identificativo_univoco_versamento = :iuv" +
          "   and " + FlussoRendicontazione.ALIAS+".indice_dati_singolo_pagamento = :index::integer"
  )
  @RegisterFieldMapper(FlussoRendicontazione.class)
  Optional<FlussoRendicontazione> getByPrimaryKey(String codIpaEnte, String iur, String index, String iuv);

  @SqlQuery(
      "select " +  FlussoRendicontazione.ALIAS+ALL_FIELDS+", " + Ente.FIELDS+", " +  ManageFlusso.FIELDS +
          "  from mygov_flusso_rendicontazione " + FlussoRendicontazione.ALIAS +
          "  join mygov_ente " + Ente.ALIAS+" ON " + Ente.ALIAS+".mygov_ente_id = " + FlussoRendicontazione.ALIAS+".mygov_ente_id"  +
          "  join mygov_manage_flusso " + ManageFlusso.ALIAS+" ON " + ManageFlusso.ALIAS+".mygov_manage_flusso_id = " + FlussoRendicontazione.ALIAS+".mygov_manage_flusso_id" +
          " where " + Ente.ALIAS+".cod_ipa_ente = :codIpaEnte " +
          "   and upper(" + FlussoRendicontazione.ALIAS+".cod_identificativo_flusso) = upper(:iuf)"
  )
  @RegisterFieldMapper(FlussoRendicontazione.class)
  List<FlussoRendicontazione> getByCodIpaIUF(String codIpaEnte, String iuf);

  @SqlQuery("SELECT count(DISTINCT(upper(" + FlussoRendicontazione.ALIAS+".cod_identificativo_flusso))) " +
          "FROM mygov_flusso_rendicontazione " + FlussoRendicontazione.ALIAS +
          WHERE_CLAUSE
  )
  Integer searchCount(Long id, String idRendicontazione, String idRegolamento, Date dateRegolFrom, Date dateRegolTo);

  @SqlQuery("SELECT " +
      " DISTINCT(upper(" + FlussoRendicontazione.ALIAS+".cod_identificativo_flusso)) cod_identificativo_flusso," +
      FlussoRendicontazione.ALIAS+".mygov_ente_id, " +
      FlussoRendicontazione.ALIAS+".mygov_manage_flusso_id, " +
      FlussoRendicontazione.ALIAS+".identificativo_psp, " +
      FlussoRendicontazione.ALIAS+".dt_data_ora_flusso, " +
      FlussoRendicontazione.ALIAS+".cod_identificativo_univoco_regolamento, " +
      FlussoRendicontazione.ALIAS+".dt_data_regolamento, " +
      FlussoRendicontazione.ALIAS+".cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco, " +
      FlussoRendicontazione.ALIAS+".cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco, " +
      FlussoRendicontazione.ALIAS+".de_ist_mitt_denominazione_mittente, " +
      FlussoRendicontazione.ALIAS+".cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco, " +
      FlussoRendicontazione.ALIAS+".cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco, " +
      FlussoRendicontazione.ALIAS+".de_ist_ricev_denominazione_ricevente, " +
      FlussoRendicontazione.ALIAS+".num_numero_totale_pagamenti, " +
      FlussoRendicontazione.ALIAS+".num_importo_totale_pagamenti, " +
      FlussoRendicontazione.ALIAS+".dt_acquisizione, " +
      FlussoRendicontazione.ALIAS+".codice_bic_banca_di_riversamento " +
      " FROM mygov_flusso_rendicontazione " + FlussoRendicontazione.ALIAS +
        WHERE_CLAUSE +
      " ORDER BY " + FlussoRendicontazione.ALIAS+".dt_data_regolamento DESC, " + FlussoRendicontazione.ALIAS+".dt_data_ora_flusso DESC, upper(" + FlussoRendicontazione.ALIAS+".cod_identificativo_flusso) DESC" +
      " LIMIT <queryLimit>"
  )
  @RegisterFieldMapper(FlussoRendicontazione.class)
  List<FlussoRendicontazione> search(Long id, String idRendicontazione, String idRegolamento, Date dateRegolFrom, Date dateRegolTo, @Define int queryLimit);

  @SqlQuery("SELECT " +  FlussoRendicontazione.ALIAS+ALL_FIELDS+", " + Ente.FIELDS+", " +  ManageFlusso.FIELDS +
          " FROM mygov_flusso_rendicontazione " + FlussoRendicontazione.ALIAS +
          " JOIN mygov_ente " + Ente.ALIAS+" ON " + Ente.ALIAS+".mygov_ente_id = " + FlussoRendicontazione.ALIAS+".mygov_ente_id"  +
          " JOIN mygov_manage_flusso " + ManageFlusso.ALIAS+" ON " + ManageFlusso.ALIAS+".mygov_manage_flusso_id = " + FlussoRendicontazione.ALIAS+".mygov_manage_flusso_id" +
          " WHERE " + FlussoRendicontazione.ALIAS+".mygov_ente_id = :idEnte " +
          " AND upper(" + FlussoRendicontazione.ALIAS+".cod_identificativo_flusso) = upper(:idRendicontazione)" +
          " AND " + FlussoRendicontazione.ALIAS+".cod_identificativo_univoco_regolamento = :idRegolamento"
  )
  @RegisterFieldMapper(FlussoRendicontazione.class)
  List<FlussoRendicontazione> getByEnteIufIur(Long idEnte, String idRendicontazione, String idRegolamento);

  @SqlQuery("SELECT " +
      " DISTINCT ON (upper(" + FlussoRendicontazione.ALIAS+".cod_identificativo_flusso))" + FlussoRendicontazione.ALIAS+".cod_identificativo_flusso," +
      FlussoRendicontazione.ALIAS+".mygov_ente_id, " +
      FlussoRendicontazione.ALIAS+".mygov_manage_flusso_id, " +
      FlussoRendicontazione.ALIAS+".identificativo_psp, " +
      FlussoRendicontazione.ALIAS+".dt_data_ora_flusso, " +
      FlussoRendicontazione.ALIAS+".cod_identificativo_univoco_regolamento, " +
      FlussoRendicontazione.ALIAS+".dt_data_regolamento, " +
      FlussoRendicontazione.ALIAS+".cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco, " +
      FlussoRendicontazione.ALIAS+".cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco, " +
      FlussoRendicontazione.ALIAS+".de_ist_mitt_denominazione_mittente, " +
      FlussoRendicontazione.ALIAS+".cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco, " +
      FlussoRendicontazione.ALIAS+".cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco, " +
      FlussoRendicontazione.ALIAS+".de_ist_ricev_denominazione_ricevente, " +
      FlussoRendicontazione.ALIAS+".num_numero_totale_pagamenti, " +
      FlussoRendicontazione.ALIAS+".num_importo_totale_pagamenti, " +
      FlussoRendicontazione.ALIAS+".dt_acquisizione, " +
      FlussoRendicontazione.ALIAS+".codice_bic_banca_di_riversamento , " +
      Ente.FIELDS+", " +  ManageFlusso.FIELDS +
      " FROM mygov_flusso_rendicontazione " + FlussoRendicontazione.ALIAS +
      " JOIN mygov_ente " + Ente.ALIAS+" ON " + Ente.ALIAS+".mygov_ente_id = " + FlussoRendicontazione.ALIAS+".mygov_ente_id"  +
      " JOIN mygov_manage_flusso " + ManageFlusso.ALIAS+" ON " + ManageFlusso.ALIAS+".mygov_manage_flusso_id = " + FlussoRendicontazione.ALIAS+".mygov_manage_flusso_id" +
      " WHERE " + FlussoRendicontazione.ALIAS+".mygov_ente_id = :idEnte " +
      " AND upper(" + FlussoRendicontazione.ALIAS+".cod_identificativo_flusso) = upper(:idRendicontazione)"
  )
  @RegisterFieldMapper(FlussoRendicontazione.class)
  Optional<FlussoRendicontazione> getDistinctByEnteIuf(Long idEnte, String idRendicontazione);

  @SqlQuery("SELECT " + FlussoRendicontazione.ALIAS+ALL_FIELDS+", " + Ente.FIELDS+", " +  ManageFlusso.FIELDS +
      " FROM mygov_flusso_rendicontazione " + FlussoRendicontazione.ALIAS +
      " JOIN mygov_ente " + Ente.ALIAS+" ON " + Ente.ALIAS+".mygov_ente_id = " + FlussoRendicontazione.ALIAS+".mygov_ente_id"  +
      " JOIN mygov_manage_flusso " + ManageFlusso.ALIAS+" ON " + ManageFlusso.ALIAS+".mygov_manage_flusso_id = " + FlussoRendicontazione.ALIAS+".mygov_manage_flusso_id" +
      " WHERE " + FlussoRendicontazione.ALIAS+".mygov_ente_id = :idEnte " +
      " AND upper(" + FlussoRendicontazione.ALIAS+".cod_identificativo_flusso) = upper(:idRendicontazione)"
  )
  @RegisterFieldMapper(FlussoRendicontazione.class)
  List<FlussoRendicontazione> getByEnteIuf(Long idEnte, String idRendicontazione);
}
