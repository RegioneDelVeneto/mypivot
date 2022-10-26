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
import it.regioneveneto.mygov.payment.mypivot4.model.ImportExportRendicontazioneTesoreria;
import it.regioneveneto.mygov.payment.mypivot4.model.RiconciliazioneSearch;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;
import java.util.Optional;

public interface RiconciliazioneDao extends BaseDao {

  String SEARCH_QUERY_BASE =
          "  from mygov_import_export_rendicontazione_tesoreria as tes " +
          "  left outer join ( " +
          "  select " +
          "    ment.cod_ipa_ente , " +
          "    mseg.cod_iuf , " +
          "    mseg.cod_iuv , " +
          "    mseg.cod_iud , " +
          "    mseg.flg_nascosto " +
          "    from mygov_segnalazione as mseg " +
          "   inner join mygov_ente as ment on mseg.mygov_ente_id = ment.mygov_ente_id " +
          "   where mseg.flg_attivo = true " +
          "     and mseg.classificazione_completezza = :classificazione_completezza) as ms " +
          "   on ms.cod_ipa_ente = tes.codice_ipa_ente " +
          "   and (ms.cod_iuf is null and tes.cod_iuf_key is null or ms.cod_iuf = tes.cod_iuf_key) " +
          "   and (ms.cod_iuv is null and tes.cod_iuv_key is null or ms.cod_iuv = tes.cod_iuv_key) " +
          "   and (ms.cod_iud is null and tes.cod_iud_key is null or ms.cod_iud = tes.cod_iud_key) " +
          " where case when :cod_tipo_dovuto is not null and :codtipodovutopresent then tes.tipo_dovuto = :cod_tipo_dovuto " +
          "            when :cod_tipo_dovuto is     null and :codtipodovutopresent then " +
          "                  tes.tipo_dovuto in (select distinct(metd.cod_tipo) " +
          "                                        from mygov_operatore_ente_tipo_dovuto as moetd, mygov_ente_tipo_dovuto as metd, mygov_operatore oper " +
          "                                       where moetd.mygov_ente_tipo_dovuto_id = metd.mygov_ente_tipo_dovuto_id " +
          "                                         and oper.mygov_operatore_id = moetd.mygov_operatore_id " +
          "                                         and oper.cod_ipa_ente = :codice_ipa_ente " +
          "                                         and oper.cod_fed_user_id = :cod_fed_user_id " +
          "                                         and moetd.flg_attivo = true) " +
          "            else true end " +
          "   and case when :codice_ipa_ente is not null then tes.codice_ipa_ente = :codice_ipa_ente else true end " +
          "   and case when :cod_iud is not null then tes.codice_iud = :cod_iud else true end " +
          "   and case when :cod_iuv is not null then tes.codice_iuv = :cod_iuv else true end " +
          "   and case when :denominazione_attestante is not null then " +
          "                    (upper(tes.denominazione_attestante) like '%' || upper(:denominazione_attestante) || '%' or " +
          "                     upper(tes.codice_identificativo_univoco_attestante) like '%' || upper(:denominazione_attestante) || '%') " +
          "            else true end " +
          "   and case when :identificativo_univoco_riscossione is not null then tes.identificativo_univoco_riscossione = :identificativo_univoco_riscossione else true end " +
          "   and case when :codice_identificativo_univoco_versante is not null then (tes.codice_identificativo_univoco_versante = upper(:codice_identificativo_univoco_versante) or " +
          "                  tes.codice_identificativo_univoco_versante = lower(:codice_identificativo_univoco_versante)) else true end " +
          "   and case when :anagrafica_versante is not null then upper(tes.anagrafica_versante) like '%' || upper(:anagrafica_versante) || '%' else true end " +
          "   and case when :codice_identificativo_univoco_pagatore is not null then (tes.codice_identificativo_univoco_pagatore = upper(:codice_identificativo_univoco_pagatore) or " +
          "                  tes.codice_identificativo_univoco_pagatore = lower(:codice_identificativo_univoco_pagatore)) else true end " +
          "   and case when :anagrafica_pagatore is not null then upper(tes.anagrafica_pagatore) like '%' || upper(:anagrafica_pagatore) || '%' else true end " +
          "   and case when :causale_versamento is not null then upper(tes.causale_versamento) like '%' || upper(:causale_versamento) || '%' else true end " +
          "   and case when :data_esecuzione_singolo_pagamento_da::date is not null then tes.dt_data_esecuzione_pagamento >= :data_esecuzione_singolo_pagamento_da::date else true end " +
          "   and case when :data_esecuzione_singolo_pagamento_a::date is not null then tes.dt_data_esecuzione_pagamento <= :data_esecuzione_singolo_pagamento_a::date else true end " +
          "   and case when :data_esito_singolo_pagamento_da::date is not null then tes.dt_data_esito_singolo_pagamento >= :data_esito_singolo_pagamento_da::date else true end " +
          "   and case when :data_esito_singolo_pagamento_a::date is not null then tes.dt_data_esito_singolo_pagamento <= :data_esito_singolo_pagamento_a::date else true end " +
          "   and case when :identificativo_flusso_rendicontazione is not null then  " +
          "                    upper(tes.identificativo_flusso_rendicontazione) like upper('%' || :identificativo_flusso_rendicontazione || '%') " +
          "            else true end " +
          "   and case when :identificativo_univoco_regolamento is not null then tes.identificativo_univoco_regolamento = :identificativo_univoco_regolamento else true end " +
          "   and case when :data_regolamento_da::date is not null then tes.dt_data_regolamento >= :data_regolamento_da::date else true end " +
          "   and case when :data_regolamento_a::date is not null then tes.dt_data_regolamento <= :data_regolamento_a::date else true end " +
          "   and case when :dt_data_contabile_da::date is not null then tes.dt_data_contabile >= :dt_data_contabile_da::date else true end " +
          "   and case when :dt_data_contabile_a::date is not null then tes.dt_data_contabile <= :dt_data_contabile_a::date else true end " +
          "   and case when :dt_data_valuta_da::date is not null then tes.dt_data_valuta >= :dt_data_valuta_da::date else true end " +
          "   and case when :dt_data_valuta_a::date is not null then tes.dt_data_valuta <= :dt_data_valuta_a::date else true end " +
          "   and case when :dt_data_ultimo_aggiornamento_da::date is not null then tes.dt_data_ultimo_aggiornamento >= :dt_data_ultimo_aggiornamento_da::date else true end " +
          "   and case when :dt_data_ultimo_aggiornamento_a::date is not null then tes.dt_data_ultimo_aggiornamento <= :dt_data_ultimo_aggiornamento_a::date else true end " +
          "   and case when :importo is not null then tes.de_importo = :importo else true end " +
          "   and case when :conto is not null then tes.cod_conto = :conto else true end " +
          "   and case when :cod_or1 is not null then upper(tes.cod_or1)  like '%' || upper(:cod_or1) || '%' else true end " +
          "   and case when :cod_bolletta is not null then upper(tes.cod_bolletta)  like '%' || upper(:cod_bolletta) || '%' else true end " +
          "   and case when :de_anno_bolletta is not null then tes.de_anno_bolletta = :de_anno_bolletta else true end " +
          "   and case when :cod_documento is not null then upper(tes.cod_documento)  like '%' || upper(:cod_documento) || '%' else true end " +
          "   and case when :de_anno_documento is not null then tes.de_anno_documento = :de_anno_documento else true end " +
          "   and case when :cod_provvisorio is not null then upper(tes.cod_provvisorio)  like '%' || upper(:cod_provvisorio) || '%' else true end " +
          "   and case when :de_anno_provvisorio is not null then upper(tes.de_anno_provvisorio) = :de_anno_provvisorio else true end " +
          "   and case when :flagnascosto is not null then ms.flg_nascosto = :flagnascosto else (ms.flg_nascosto is null or ms.flg_nascosto = false) end " +
          "   and case when :classificazione_completezza is not null then tes.classificazione_completezza = :classificazione_completezza else true end ";

  @SqlQuery(
      "    select tes.* " +
          " , ms.cod_ipa_ente is not null as has_segnalazione " +
          SEARCH_QUERY_BASE +
          " order by case when :classificazione_completezza = 'IUD_RT_IUF_TES' or " +
          "                    :classificazione_completezza = 'RT_IUF_TES' or " +
          "                    :classificazione_completezza = 'RT_IUF' or " +
          "                    :classificazione_completezza = 'IUD_RT_IUF' or " +
          "                    :classificazione_completezza = 'RT_NO_IUF' or " +
          "                    :classificazione_completezza = 'RT_NO_IUD' then (dt_data_esito_singolo_pagamento, codice_iuv, codice_iud) " +
          "               when :classificazione_completezza = 'IUD_NO_RT' then (dt_data_esecuzione_pagamento, codice_iud) " +
          "               else (dt_data_esito_singolo_pagamento, codice_iuv, codice_iud) " +
          "          end " +
          " limit <queryLimit>"
  )
  @RegisterFieldMapper(ImportExportRendicontazioneTesoreria.class)
  List<ImportExportRendicontazioneTesoreria> search(@BindBean RiconciliazioneSearch in, @Define int queryLimit);

  @SqlQuery(
      "    select count(1) " +
          SEARCH_QUERY_BASE
  )
  Integer searchCount(@BindBean RiconciliazioneSearch in);

  String SEARCH_QUERY_RENDICONTAZIONE_SUBSET_FIELDS =
      " select distinct " +
          " (upper(tes.identificativo_flusso_rendicontazione)) as identificativo_flusso_rendicontazione, " +
          " tes.codice_ipa_ente, " +
          " tes.singolo_importo_commissione_carico_pa, " +
          " tes.bilancio, " +
          " tes.data_ora_flusso_rendicontazione, " +
          " tes.identificativo_univoco_regolamento, " +
          " tes.dt_data_regolamento, " +
          " tes.de_data_regolamento, " +
          " tes.importo_totale_pagamenti, " +
          " tes.de_anno_bolletta, " +
          " tes.cod_bolletta, " +
          " tes.cod_id_dominio, " +
          " tes.dt_ricezione, " +
          " tes.de_data_ricezione, " +
          " tes.de_anno_documento, " +
          " tes.cod_documento, " +
          " tes.de_anno_provvisorio, " +
          " tes.cod_provvisorio, " +
          " tes.classificazione_completezza, " +
          " MAX(tes.dt_data_ultimo_aggiornamento)as dt_data_ultimo_aggiornamento, " +
          " to_char(MAX(tes.dt_data_ultimo_aggiornamento), 'DD-MM-YYYY') as de_data_ultimo_aggiornamento, " +
          " tes.indice_dati_singolo_pagamento, " +
          " tes.cod_iuf_key ";

  String SEARCH_QUERY_RENDICONTAZIONE_SUBSET_BASE =
      "  from mygov_import_export_rendicontazione_tesoreria as tes " +
          "  left outer join ( " +
          "  select " +
          "    ment.cod_ipa_ente , " +
          "    mseg.cod_iuf , " +
          "    mseg.cod_iuv , " +
          "    mseg.cod_iud , " +
          "    mseg.flg_nascosto " +
          "    from mygov_segnalazione as mseg " +
          "   inner join mygov_ente as ment on mseg.mygov_ente_id = ment.mygov_ente_id " +
          "   where mseg.flg_attivo = true " +
          "     and mseg.classificazione_completezza = :classificazione_completezza) as ms " +
          "   on ms.cod_ipa_ente = tes.codice_ipa_ente " +
          "   and (ms.cod_iuf is null and tes.cod_iuf_key is null or ms.cod_iuf = tes.cod_iuf_key) " +
          " where case when :cod_tipo_dovuto is not null and tes.classificazione_completezza <> 'IUV_NO_RT' then tes.tipo_dovuto = :cod_tipo_dovuto " +
          "            when :cod_tipo_dovuto is     null and tes.classificazione_completezza <> 'IUV_NO_RT' then " +
          "                  tes.tipo_dovuto in (select distinct(metd.cod_tipo) " +
          "                                        from mygov_operatore_ente_tipo_dovuto as moetd, mygov_ente_tipo_dovuto as metd, mygov_operatore oper " +
          "                                       where moetd.mygov_ente_tipo_dovuto_id = metd.mygov_ente_tipo_dovuto_id " +
          "                                         and oper.mygov_operatore_id = moetd.mygov_operatore_id " +
          "                                         and oper.cod_ipa_ente = :codice_ipa_ente " +
          "                                         and oper.cod_fed_user_id = :cod_fed_user_id " +
          "                                         and moetd.flg_attivo = true) " +
          "            else true end " +
          "   and case when :codice_ipa_ente is not null then tes.codice_ipa_ente = :codice_ipa_ente else true end " +
          "   and case when :identificativo_flusso_rendicontazione is not null then  " +
          "                    upper(tes.identificativo_flusso_rendicontazione) like upper('%' || :identificativo_flusso_rendicontazione || '%') " +
          "            else true end " +
          "   and case when :data_regolamento_da::date is not null then tes.dt_data_regolamento >= :data_regolamento_da::date else true end " +
          "   and case when :data_regolamento_a::date is not null then tes.dt_data_regolamento <= :data_regolamento_a::date else true end " +
          "   and case when :dt_data_ultimo_aggiornamento_da::date is not null then tes.dt_data_ultimo_aggiornamento >= :dt_data_ultimo_aggiornamento_da::date else true end " +
          "   and case when :dt_data_ultimo_aggiornamento_a::date is not null then tes.dt_data_ultimo_aggiornamento <= :dt_data_ultimo_aggiornamento_a::date else true end " +
          "   and case when :classificazione_completezza is not null then tes.classificazione_completezza = :classificazione_completezza else true end " +
          "   and case when :flagnascosto is not null then ms.flg_nascosto = :flagnascosto else (ms.flg_nascosto is null or ms.flg_nascosto = false) end ";

  String SEARCH_QUERY_RENDICONTAZIONE_SUBSET_GROUP_BY =
          " group by upper(tes.identificativo_flusso_rendicontazione),tes.codice_ipa_ente,tes.singolo_importo_commissione_carico_pa, " +
          "   tes.bilancio,tes.data_ora_flusso_rendicontazione,tes.identificativo_univoco_regolamento,tes.dt_data_regolamento,tes.de_data_regolamento, " +
          "   tes.importo_totale_pagamenti,tes.de_anno_bolletta,tes.cod_bolletta,tes.cod_id_dominio,tes.dt_ricezione,tes.de_data_ricezione, " +
          "   tes.de_anno_documento,tes.cod_documento,tes.de_anno_provvisorio,tes.cod_provvisorio,tes.classificazione_completezza, " +
          "   tes.indice_dati_singolo_pagamento,tes.cod_iuf_key ";

  @SqlQuery(
          SEARCH_QUERY_RENDICONTAZIONE_SUBSET_FIELDS +
          ", ms.cod_ipa_ente is not null as has_segnalazione " +
          SEARCH_QUERY_RENDICONTAZIONE_SUBSET_BASE +
          SEARCH_QUERY_RENDICONTAZIONE_SUBSET_GROUP_BY +
          ", has_segnalazione " +
          " order by dt_data_ultimo_aggiornamento DESC " +
          " limit <queryLimit>"
  )
  @RegisterFieldMapper(ImportExportRendicontazioneTesoreria.class)
  List<ImportExportRendicontazioneTesoreria> searchRendicontazioneSubset(@BindBean RiconciliazioneSearch in, @Define int queryLimit);

  @SqlQuery(
      "    select count(distinct(upper(tes.identificativo_flusso_rendicontazione))) " +
          SEARCH_QUERY_RENDICONTAZIONE_SUBSET_BASE
  )
  Integer searchRendicontazioneSubsetCount(@BindBean RiconciliazioneSearch in);

  String SEARCH_QUERY_TESORERIA_SUBSET_FIELDS =
      " select distinct " +
      "  tes.codice_iuv, " +
      "  upper(tes.identificativo_flusso_rendicontazione) as identificativo_flusso_rendicontazione, " +
      "  tes.dt_data_esecuzione_pagamento, " +
      "  tes.de_data_esecuzione_pagamento, " +
      "  tes.singolo_importo_commissione_carico_pa, " +
      "  tes.bilancio, " +
      "  tes.cod_conto, " +
      "  tes.dt_data_contabile, " +
      "  tes.de_data_contabile, " +
      "  tes.dt_data_valuta, " +
      "  tes.de_data_valuta, " +
      "  tes.num_importo, " +
      "  tes.de_importo, " +
      "  tes.cod_or1, " +
      "  tes.de_anno_bolletta, " +
      "  tes.cod_bolletta, " +
      "  tes.cod_id_dominio, " +
      "  tes.dt_ricezione, " +
      "  tes.de_data_ricezione, " +
      "  tes.de_anno_documento, " +
      "  tes.cod_documento, " +
      "  tes.de_anno_provvisorio, " +
      "  tes.cod_provvisorio, " +
      "  tes.de_causale_t, " +
      "  tes.classificazione_completezza, " +
      "  tes.dt_data_ultimo_aggiornamento, " +
      "  tes.de_data_ultimo_aggiornamento, " +
      "  tes.cod_iuf_key, " +
      "  tes.cod_iuv_key, " +
      "  ms.cod_ipa_ente is not null as has_segnalazione ";

  String SEARCH_QUERY_TESORERIA_SUBSET_BASE =
      "  from mygov_import_export_rendicontazione_tesoreria as tes " +
          "  left outer join ( " +
          "  select " +
          "    ment.cod_ipa_ente , " +
          "    mseg.cod_iuf , " +
          "    mseg.cod_iuv , " +
          "    mseg.cod_iud , " +
          "    mseg.flg_nascosto " +
          "    from mygov_segnalazione as mseg " +
          "   inner join mygov_ente as ment on mseg.mygov_ente_id = ment.mygov_ente_id " +
          "   where mseg.flg_attivo = true " +
          "     and mseg.classificazione_completezza = :classificazione_completezza) as ms " +
          "   on ms.cod_ipa_ente = tes.codice_ipa_ente " +
          "   and (ms.cod_iuf is null and tes.cod_iuf_key is null or ms.cod_iuf = tes.cod_iuf_key) " +
          "   and (ms.cod_iuv is null and tes.cod_iuv_key is null or ms.cod_iuv = tes.cod_iuv_key) " +
          " where case when :codice_ipa_ente is not null then tes.codice_ipa_ente = :codice_ipa_ente else true end " +
          "   and case when :dt_data_contabile_da::date is not null then tes.dt_data_contabile >= :dt_data_contabile_da::date else true end " +
          "   and case when :dt_data_contabile_a::date is not null then tes.dt_data_contabile <= :dt_data_contabile_a::date else true end " +
          "   and case when :dt_data_valuta_da::date is not null then tes.dt_data_valuta >= :dt_data_valuta_da::date else true end " +
          "   and case when :dt_data_valuta_a::date is not null then tes.dt_data_valuta <= :dt_data_valuta_a::date else true end " +
          "   and case when :dt_data_ultimo_aggiornamento_da::date is not null then tes.dt_data_ultimo_aggiornamento >= :dt_data_ultimo_aggiornamento_da::date else true end " +
          "   and case when :dt_data_ultimo_aggiornamento_a::date is not null then tes.dt_data_ultimo_aggiornamento <= :dt_data_ultimo_aggiornamento_a::date else true end " +
          "   and case when :causale_versamento is not null then  " +
          "                    upper(tes.de_causale_t) like upper('%' || :causale_versamento || '%') " +
          "            else true end " +
          "   and case when :importo is not null then tes.de_importo = :importo else true end " +
          "   and case when :conto is not null then tes.cod_conto = :conto else true end " +
          "   and case when :cod_or1 is not null then upper(tes.cod_or1)  like '%' || upper(:cod_or1) || '%' else true end " +
          "   and case when :cod_bolletta is not null then upper(tes.cod_bolletta)  like '%' || upper(:cod_bolletta) || '%' else true end " +
          "   and case when :de_anno_bolletta is not null then tes.de_anno_bolletta = :de_anno_bolletta else true end " +
          "   and case when :cod_documento is not null then upper(tes.cod_documento)  like '%' || upper(:cod_documento) || '%' else true end " +
          "   and case when :de_anno_documento is not null then tes.de_anno_documento = :de_anno_documento else true end " +
          "   and case when :cod_provvisorio is not null then upper(tes.cod_provvisorio)  like '%' || upper(:cod_provvisorio) || '%' else true end " +
          "   and case when :de_anno_provvisorio is not null then upper(tes.de_anno_provvisorio) = :de_anno_provvisorio else true end " +
          "   and case when :classificazione_completezza is not null then tes.classificazione_completezza = :classificazione_completezza else true end " +
          "   and case when :flagnascosto is not null then ms.flg_nascosto = :flagnascosto else (ms.flg_nascosto is null or ms.flg_nascosto = false) end " +
          "   and case when :cod_iuv is not null then tes.codice_iuv = :cod_iuv else true end " +
          "   and case when :identificativo_flusso_rendicontazione is not null then  " +
          "                    upper(tes.identificativo_flusso_rendicontazione) like upper('%' || :identificativo_flusso_rendicontazione || '%') " +
          "            else true end ";

  @SqlQuery(
      SEARCH_QUERY_TESORERIA_SUBSET_FIELDS +
          SEARCH_QUERY_TESORERIA_SUBSET_BASE +
          " order by tes.dt_data_valuta, upper(tes.identificativo_flusso_rendicontazione), tes.codice_iuv " +
          " limit <queryLimit>"
  )
  @RegisterFieldMapper(ImportExportRendicontazioneTesoreria.class)
  List<ImportExportRendicontazioneTesoreria> searchTesoreriaSubset(@BindBean RiconciliazioneSearch in, @Define int queryLimit);

  @SqlQuery(
      "    select count(1) " +
          SEARCH_QUERY_TESORERIA_SUBSET_BASE
  )
  Integer searchTesoreriaSubsetCount(@BindBean RiconciliazioneSearch in);

  @SqlQuery(
      "    select count(distinct(tes.codice_ipa_ente, tes.de_anno_bolletta, tes.cod_bolletta)) " +
          SEARCH_QUERY_TESORERIA_SUBSET_BASE
  )
  Integer searchTesoreriaNoMatchSubsetCount(@BindBean RiconciliazioneSearch in);


  String SEARCH_QUERY_RENDICONTAZIONE_TESORERIA_SUBSET_FIELDS =
      " select distinct " +
          " upper(tes.identificativo_flusso_rendicontazione) as identificativo_flusso_rendicontazione, " +
          " tes.codice_ipa_ente, " +
          " tes.dt_data_esecuzione_pagamento, " +
          " tes.de_data_esecuzione_pagamento, " +
          " tes.singolo_importo_commissione_carico_pa, " +
          " tes.cod_conto, " +
          " tes.dt_data_contabile, " +
          " tes.de_data_contabile, " +
          " tes.dt_data_valuta, " +
          " tes.de_data_valuta, " +
          " tes.num_importo, " +
          " tes.de_importo, " +
          " tes.cod_or1, " +
          " tes.de_anno_bolletta, " +
          " tes.cod_bolletta, " +
          " tes.cod_id_dominio, " +
          " tes.dt_ricezione, " +
          " tes.de_data_ricezione, " +
          " tes.de_anno_documento, " +
          " tes.cod_documento, " +
          " tes.de_anno_provvisorio, " +
          " tes.cod_provvisorio, " +
          " tes.de_causale_t, " +
          " tes.classificazione_completezza, " +
          " tes.dt_data_ultimo_aggiornamento, " +
          " tes.de_data_ultimo_aggiornamento, " +
          " tes.data_ora_flusso_rendicontazione, " +
          " tes.identificativo_univoco_regolamento, " +
          " tes.dt_data_regolamento, " +
          " tes.de_data_regolamento, " +
          " tes.importo_totale_pagamenti, " +
          " tes.cod_iuf_key, " +
          " ms.cod_ipa_ente is not null as has_segnalazione ";

  String SEARCH_QUERY_RENDICONTAZIONE_TESORERIA_SUBSET_BASE =
      "  from mygov_import_export_rendicontazione_tesoreria as tes " +
          "  left outer join ( " +
          "  select " +
          "    ment.cod_ipa_ente , " +
          "    mseg.cod_iuf , " +
          "    mseg.cod_iuv , " +
          "    mseg.cod_iud , " +
          "    mseg.flg_nascosto " +
          "    from mygov_segnalazione as mseg " +
          "   inner join mygov_ente as ment on mseg.mygov_ente_id = ment.mygov_ente_id " +
          "   where mseg.flg_attivo = true " +
          "     and mseg.classificazione_completezza = :classificazione_completezza) as ms " +
          "   on ms.cod_ipa_ente = tes.codice_ipa_ente " +
          "   and (ms.cod_iuf is null and tes.cod_iuf_key is null or ms.cod_iuf = tes.cod_iuf_key) " +
          "   and (ms.cod_iuv is null and tes.cod_iuv_key is null or ms.cod_iuv = tes.cod_iuv_key) " +
          " where case when :cod_tipo_dovuto is not null and tes.classificazione_completezza <> 'IUV_NO_RT' then tes.tipo_dovuto = :cod_tipo_dovuto " +
          "            when :cod_tipo_dovuto is     null and tes.classificazione_completezza <> 'IUV_NO_RT' then " +
          "                  tes.tipo_dovuto in (select distinct(metd.cod_tipo) " +
          "                                        from mygov_operatore_ente_tipo_dovuto as moetd, mygov_ente_tipo_dovuto as metd, mygov_operatore oper, mygov_ente as me " +
          "                                       where moetd.mygov_ente_tipo_dovuto_id = metd.mygov_ente_tipo_dovuto_id " +
          "                                         and metd.mygov_ente_id = me.mygov_ente_id " +
          "                                         and me.cod_ipa_ente = oper.cod_ipa_ente " +
          "                                         and oper.mygov_operatore_id = moetd.mygov_operatore_id " +
          "                                         and oper.cod_ipa_ente = :codice_ipa_ente " +
          "                                         and oper.cod_fed_user_id = :cod_fed_user_id " +
          "                                         and moetd.flg_attivo = true) " +
          "            else true end " +
          "   and case when :codice_ipa_ente is not null then tes.codice_ipa_ente = :codice_ipa_ente else true end " +
          "   and case when :identificativo_flusso_rendicontazione is not null then  " +
          "                    upper(tes.identificativo_flusso_rendicontazione) like upper('%' || :identificativo_flusso_rendicontazione || '%') " +
          "            else true end " +
          "   and case when :identificativo_univoco_regolamento is not null then tes.identificativo_univoco_regolamento = :identificativo_univoco_regolamento else true end " +
          "   and case when :data_regolamento_da::date is not null then tes.dt_data_regolamento >= :data_regolamento_da::date else true end " +
          "   and case when :data_regolamento_a::date is not null then tes.dt_data_regolamento <= :data_regolamento_a::date else true end " +
          "   and case when :dt_data_contabile_da::date is not null then tes.dt_data_contabile >= :dt_data_contabile_da::date else true end " +
          "   and case when :dt_data_contabile_a::date is not null then tes.dt_data_contabile <= :dt_data_contabile_a::date else true end " +
          "   and case when :dt_data_valuta_da::date is not null then tes.dt_data_valuta >= :dt_data_valuta_da::date else true end " +
          "   and case when :dt_data_valuta_a::date is not null then tes.dt_data_valuta <= :dt_data_valuta_a::date else true end " +
          "   and case when :dt_data_ultimo_aggiornamento_da::date is not null then tes.dt_data_ultimo_aggiornamento >= :dt_data_ultimo_aggiornamento_da::date else true end " +
          "   and case when :dt_data_ultimo_aggiornamento_a::date is not null then tes.dt_data_ultimo_aggiornamento <= :dt_data_ultimo_aggiornamento_a::date else true end " +
          "   and case when :importo is not null then tes.de_importo = :importo else true end " +
          "   and case when :conto is not null then tes.cod_conto = :conto else true end " +
          "   and case when :cod_or1 is not null then upper(tes.cod_or1)  like '%' || upper(:cod_or1) || '%' else true end " +
          "   and case when :cod_bolletta is not null then upper(tes.cod_bolletta)  like '%' || upper(:cod_bolletta) || '%' else true end " +
          "   and case when :de_anno_bolletta is not null then tes.de_anno_bolletta = :de_anno_bolletta else true end " +
          "   and case when :cod_documento is not null then upper(tes.cod_documento)  like '%' || upper(:cod_documento) || '%' else true end " +
          "   and case when :de_anno_documento is not null then tes.de_anno_documento = :de_anno_documento else true end " +
          "   and case when :cod_provvisorio is not null then upper(tes.cod_provvisorio)  like '%' || upper(:cod_provvisorio) || '%' else true end " +
          "   and case when :de_anno_provvisorio is not null then upper(tes.de_anno_provvisorio) = :de_anno_provvisorio else true end " +
          "   and case when :flagnascosto is not null then ms.flg_nascosto = :flagnascosto else (ms.flg_nascosto is null or ms.flg_nascosto = false) end " +
          "   and case when :classificazione_completezza is not null then tes.classificazione_completezza = :classificazione_completezza else true end ";

  @SqlQuery(
      SEARCH_QUERY_RENDICONTAZIONE_TESORERIA_SUBSET_FIELDS +
          SEARCH_QUERY_RENDICONTAZIONE_TESORERIA_SUBSET_BASE +
          " order by tes.dt_data_regolamento, tes.dt_data_contabile, tes.dt_data_valuta, tes.dt_data_ultimo_aggiornamento " +
          " limit <queryLimit>"
  )
  @RegisterFieldMapper(ImportExportRendicontazioneTesoreria.class)
  List<ImportExportRendicontazioneTesoreria> searchRendicontazioneTesoreriaSubset(@BindBean RiconciliazioneSearch in, @Define int queryLimit);

  @SqlQuery(
      "    select count(distinct(tes.codice_ipa_ente, upper(tes.identificativo_flusso_rendicontazione))) " +
          SEARCH_QUERY_RENDICONTAZIONE_TESORERIA_SUBSET_BASE
  )
  Integer searchRendicontazioneTesoreriaSubsetCount(@BindBean RiconciliazioneSearch in);


  @SqlQuery(
      "     select " + ImportExportRendicontazioneTesoreria.ALIAS + ALL_FIELDS +
          "   from mygov_import_export_rendicontazione_tesoreria " + ImportExportRendicontazioneTesoreria.ALIAS +
          "  where " + ImportExportRendicontazioneTesoreria.ALIAS + ".codice_ipa_ente = :codIpaEnte " +
          "    and " + ImportExportRendicontazioneTesoreria.ALIAS + ".classificazione_completezza = :classificazione " +
          "    and (:ignoreIuv or :iuv is null and " + ImportExportRendicontazioneTesoreria.ALIAS + ".cod_iuv_key is null or " + ImportExportRendicontazioneTesoreria.ALIAS + ".cod_iuv_key = :iuv) " +
          "    and (:ignoreIud or :iud is null and " + ImportExportRendicontazioneTesoreria.ALIAS + ".cod_iud_key is null or " + ImportExportRendicontazioneTesoreria.ALIAS + ".cod_iud_key = :iud) " +
          "    and (:ignoreIuf or :iuf is null and " + ImportExportRendicontazioneTesoreria.ALIAS + ".cod_iuf_key is null or " + ImportExportRendicontazioneTesoreria.ALIAS + ".cod_iuf_key = :iuf) " +
          "  limit case when :limitOne then 1 else 2 end "
  )
  @RegisterFieldMapper(ImportExportRendicontazioneTesoreria.class)
  Optional<ImportExportRendicontazioneTesoreria> getDetail(String codIpaEnte, String classificazione, String iuf, boolean ignoreIuf, String iud, boolean ignoreIud, String iuv, boolean ignoreIuv, boolean limitOne);

  @SqlQuery(
          SEARCH_QUERY_RENDICONTAZIONE_SUBSET_FIELDS +
          "  from mygov_import_export_rendicontazione_tesoreria as tes " +
          "  where tes.codice_ipa_ente = :codIpaEnte " +
          "    and tes.classificazione_completezza = :classificazione " +
          "    and tes.cod_iuf_key = :iuf " +
          SEARCH_QUERY_RENDICONTAZIONE_SUBSET_GROUP_BY +
          " limit 1" // since in some cases more than 1 row may be returned (beacause IUF is not PK of the table), this is necessary
                     // TODO check if this is correct; on MyPivot3 is done this way
  )
  @RegisterFieldMapper(ImportExportRendicontazioneTesoreria.class)
  Optional<ImportExportRendicontazioneTesoreria> getRendicontazioneSubsetDetail(String codIpaEnte, String classificazione, String iuf);



}
