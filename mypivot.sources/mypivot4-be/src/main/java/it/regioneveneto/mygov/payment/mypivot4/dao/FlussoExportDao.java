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
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoExportKeysTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.RicevutaSearchTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.EnteTipoDovuto;
import it.regioneveneto.mygov.payment.mypivot4.model.FlussoExport;
import it.regioneveneto.mygov.payment.mypivot4.model.ManageFlusso;
import it.regioneveneto.mygov.payment.mypivot4.model.Operatore;
import it.regioneveneto.mygov.payment.mypivot4.model.OperatoreEnteTipoDovuto;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindBeanList;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface FlussoExportDao extends BaseDao {

  String FROM_BASE =
      " FROM mygov_flusso_export " + FlussoExport.ALIAS +
      " JOIN mygov_ente "+Ente.ALIAS+" ON "+Ente.ALIAS+".mygov_ente_id = "+FlussoExport.ALIAS+".mygov_ente_id"  +
      " JOIN mygov_manage_flusso "+ManageFlusso.ALIAS+" ON "+ManageFlusso.ALIAS+".mygov_manage_flusso_id = "+FlussoExport.ALIAS+".mygov_manage_flusso_id";

  String FROM_RT =
      FROM_BASE +
      " join mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
      "   on "+Ente.ALIAS+".mygov_ente_id = "+EnteTipoDovuto.ALIAS+".mygov_ente_id " +
      " join mygov_operatore_ente_tipo_dovuto " + OperatoreEnteTipoDovuto.ALIAS +
      "   on "+EnteTipoDovuto.ALIAS+".mygov_ente_tipo_dovuto_id = "+OperatoreEnteTipoDovuto.ALIAS+".mygov_ente_tipo_dovuto_id " +
      "  join mygov_operatore " + Operatore.ALIAS +
      "    on "+Operatore.ALIAS+".mygov_operatore_id = "+OperatoreEnteTipoDovuto.ALIAS+".mygov_operatore_id ";

  String WHERE_CASE =
      "     case when :tipoDovuto is not null then "+FlussoExport.ALIAS+".cod_tipo_dovuto = :tipoDovuto else true end " +
          " and case when :iuv is not null then "+FlussoExport.ALIAS+".cod_rp_silinviarp_id_univoco_versamento = :iuv else true end " +
          " and case when :iur is not null then "+FlussoExport.ALIAS+".cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss = :iur else true end " +
          " and case when :iud is not null then "+FlussoExport.ALIAS+".cod_iud = :iud else true end " +
          " and case when :dateEsitoFrom::date is not null then "+FlussoExport.ALIAS+".dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento >= :dateEsitoFrom::date else true end " +
          " and case when :dateEsitoTo::date is not null then "+FlussoExport.ALIAS+".dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento <= :dateEsitoTo::date else true end " +
          " and case when :attestante is not null then "+FlussoExport.ALIAS+".de_e_istit_att_denominazione_attestante ilike '%' || :attestante || '%' else true end " +
          " and case when :codFiscalePagatore is not null then "+FlussoExport.ALIAS+".cod_e_sogg_pag_id_univ_pag_codice_id_univoco = :codFiscalePagatore else true end " +
          " and case when :anagPagatore is not null then "+FlussoExport.ALIAS+".cod_e_sogg_pag_anagrafica_pagatore ilike '%' || :anagPagatore || '%' else true end " +
          " and case when :codFiscaleVersante is not null then "+FlussoExport.ALIAS+".cod_e_sogg_vers_id_univ_vers_codice_id_univoco = :codFiscaleVersante else true end " +
          " and case when :anagVersante is not null then "+FlussoExport.ALIAS+".cod_e_sogg_vers_anagrafica_versante ilike '%' || :anagVersante || '%' else true end ";

  String WHERE_RT =
      Ente.ALIAS+".mygov_ente_id = :mygovEnteId " +
          " and ("+Operatore.ALIAS+".cod_fed_user_id = :codFedUserId or :codFedUserId is null) " +
          " and "+EnteTipoDovuto.ALIAS+".cod_tipo = "+FlussoExport.ALIAS+".cod_tipo_dovuto " +
          " and " + WHERE_CASE;



  @SqlQuery(
      "select "+ FlussoExport.ALIAS+ALL_FIELDS+", "+ Ente.FIELDS+", "+ ManageFlusso.FIELDS +
          "  from mygov_flusso_export " + FlussoExport.ALIAS +
          "  join mygov_ente "+Ente.ALIAS+" on "+Ente.ALIAS+".mygov_ente_id = "+FlussoExport.ALIAS+".mygov_ente_id"  +
          "  join mygov_manage_flusso "+ManageFlusso.ALIAS+" on "+ManageFlusso.ALIAS+".mygov_manage_flusso_id = "+FlussoExport.ALIAS+".mygov_manage_flusso_id" +
          " where "+Ente.ALIAS+".cod_ipa_ente = :codIpaEnte " +
          "   and "+FlussoExport.ALIAS+".cod_rp_silinviarp_id_univoco_versamento = :iuv"
  )
  @RegisterFieldMapper(FlussoExport.class)
  List<FlussoExport> getByCodIpaIUV(String codIpaEnte, String iuv);

  @SqlQuery(
      "select "+ FlussoExport.ALIAS+ALL_FIELDS+", "+ Ente.FIELDS+", "+ ManageFlusso.FIELDS +
          "  from mygov_flusso_export " + FlussoExport.ALIAS +
          "  join mygov_ente "+Ente.ALIAS+" on "+Ente.ALIAS+".mygov_ente_id = "+FlussoExport.ALIAS+".mygov_ente_id"  +
          "  join mygov_manage_flusso "+ManageFlusso.ALIAS+" on "+ManageFlusso.ALIAS+".mygov_manage_flusso_id = "+FlussoExport.ALIAS+".mygov_manage_flusso_id" +
          " where "+Ente.ALIAS+".cod_ipa_ente = :codIpaEnte " +
          "   and "+FlussoExport.ALIAS+".cod_rp_silinviarp_id_univoco_versamento = :iuv" +
          "   and "+FlussoExport.ALIAS+".indice_dati_singolo_pagamento = :indiceDatiSingoloPagamento"
  )
  @RegisterFieldMapper(FlussoExport.class)
  List<FlussoExport> getByCodIpaIUVIdDtSinPag(String codIpaEnte, String iuv, int indiceDatiSingoloPagamento);

  @SqlQuery(
      "select "+ FlussoExport.ALIAS+ALL_FIELDS+", "+ Ente.FIELDS+", "+ ManageFlusso.FIELDS +
          "  from mygov_flusso_export " + FlussoExport.ALIAS +
          "  join mygov_ente "+Ente.ALIAS+" on "+Ente.ALIAS+".mygov_ente_id = "+FlussoExport.ALIAS+".mygov_ente_id"  +
          "  join mygov_manage_flusso "+ManageFlusso.ALIAS+" on "+ManageFlusso.ALIAS+".mygov_manage_flusso_id = "+FlussoExport.ALIAS+".mygov_manage_flusso_id" +
          " where "+Ente.ALIAS+".mygov_ente_id = :mygovEnteId" +
          "   and "+FlussoExport.ALIAS+".cod_tipo_dovuto = :codTipo " +
          "   and "+FlussoExport.ALIAS+".cod_iud in (<codIud>)" +
          "   and ("+FlussoExport.ALIAS+".dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento >= :from::date or :from::date is null)" +
          "   and ("+FlussoExport.ALIAS+".dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento <= :to::date or :to::date is null)" +
          "   and ("+FlussoExport.ALIAS+".cod_rp_silinviarp_id_univoco_versamento = :iuv or :iuv is null)" +
          "   and ("+FlussoExport.ALIAS+".cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss = :iur or :iur is null)" +
          "   and ("+FlussoExport.ALIAS+".de_e_istit_att_denominazione_attestante ilike '%' || :attestante || '%' or :attestante is null)" +
          "   and ("+FlussoExport.ALIAS+".cod_e_sogg_pag_id_univ_pag_codice_id_univoco = :cfPagatore or :cfPagatore is null)" +
          "   and ("+FlussoExport.ALIAS+".cod_e_sogg_pag_anagrafica_pagatore ilike '%' || :anagPagatore || '%' or :anagPagatore is null)" +
          "   and ("+FlussoExport.ALIAS+".cod_e_sogg_vers_id_univ_vers_codice_id_univoco = :cfVersante or :cfVersante is null)" +
          "   and ("+FlussoExport.ALIAS+".cod_e_sogg_vers_anagrafica_versante ilike '%' || :anagVersante || '%' or :anagVersante is null)"
  )
  @RegisterFieldMapper(FlussoExport.class)
  List<FlussoExport> getDettalioCruscotto(Long mygovEnteId, String codTipo, @BindList(onEmpty= BindList.EmptyHandling.NULL_STRING) List<String> codIud, LocalDate from, LocalDate to,
                                          String iuv, String iur, String attestante, String cfPagatore, String anagPagatore, String cfVersante, String anagVersante);

  @SqlQuery(
      "select get_dettaglio_pagamenti_cruscotto(:anno, :mese, :giorno, :codUfficio, :codDovuto, :codCapitolo, :enteId, :codAccertamento)"
  )
  List<String> get_dettaglio_pagamenti_cruscotto(Integer anno, Integer mese, Integer giorno, String codUfficio, String codDovuto,
                                                 String codCapitolo, Long enteId, String codAccertamento);

  @SqlQuery("SELECT "+ FlussoExport.ALIAS+ALL_FIELDS+", "+ Ente.FIELDS+", "+ ManageFlusso.FIELDS + FROM_BASE +
      " WHERE ("+FlussoExport.ALIAS+".mygov_ente_id, " +
                 FlussoExport.ALIAS+".cod_rp_silinviarp_id_univoco_versamento, " +
                 FlussoExport.ALIAS+".cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss, " +
                 FlussoExport.ALIAS+".indice_dati_singolo_pagamento" +
      "     ) IN (<keySet>) " +
      " AND " + WHERE_CASE
  )
  @RegisterFieldMapper(FlussoExport.class)
  List<FlussoExport> findPagatiByKeySet(@BindBeanList(value = "keySet", propertyNames = {
      "mygov_ente_id",
      "cod_rp_silinviarp_id_univoco_versamento",
      "cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss",
      "indice_dati_singolo_pagamento"}) Set<FlussoExportKeysTo> keySet, @BindBean RicevutaSearchTo searchTo);


  @SqlQuery("SELECT "+ FlussoExport.ALIAS+ALL_FIELDS+", "+ Ente.FIELDS+", "+ ManageFlusso.FIELDS + FROM_RT + " WHERE " + WHERE_RT + " limit <queryLimit>")
  @RegisterFieldMapper(FlussoExport.class)
  List<FlussoExport> searchRicevuteTelematiche(Long mygovEnteId, String codFedUserId, @BindBean RicevutaSearchTo searchTo, @Define int queryLimit);

  @SqlQuery("SELECT COUNT(1) " + FROM_RT + " WHERE " + WHERE_RT)
  Integer searchCountRt(Long mygovEnteId, String codFedUserId, @BindBean RicevutaSearchTo searchTo);
}
