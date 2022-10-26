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
import it.regioneveneto.mygov.payment.mypivot4.dto.BilancioTo;
import it.regioneveneto.mygov.payment.mypivot4.model.*;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDate;
import java.util.List;

public interface AccertamentoDettaglioDao extends BaseDao {

  @SqlUpdate(
      "INSERT INTO mygov_accertamento_dettaglio (" +
          "  mygov_accertamento_dettaglio_id" +
          " ,mygov_accertamento_id" +
          " ,cod_ipa_ente" +
          " ,cod_tipo_dovuto" +
          " ,cod_iud" +
          " ,cod_iuv" +
          " ,cod_ufficio" +
          " ,cod_capitolo" +
          " ,cod_accertamento" +
          " ,num_importo" +
          " ,flg_importo_inserito" +
          " ,dt_ultima_modifica" +
          " ,dt_data_inserimento" +
          " ,mygov_utente_id" +
          ") VALUES (" +
          " nextval('mygov_accertamento_dettaglio_mygov_accertamento_dett_id_seq')" +
          " ,:d.mygovAccertamentoId.mygovAccertamentoId" +
          " ,:d.codIpaEnte" +
          " ,:d.codTipoDovuto" +
          " ,:d.codIud" +
          " ,:d.codIuv" +
          " ,:d.codUfficio" +
          " ,:d.codCapitolo" +
          " ,:d.codAccertamento" +
          " ,:d.numImporto" +
          " ,:d.flgImportoInserito" +
          " ,COALESCE(:d.dtUltimaModifica, NOW())" +
          " ,COALESCE(:d.dtDataInserimento, NOW())" +
          " ,:d.mygovUtenteId.mygovUtenteId)"
  )
  @GetGeneratedKeys("mygov_accertamento_dettaglio_id")
  long insert(@BindBean("d") AccertamentoDettaglio d);

  @SqlQuery(
      "SELECT "+AccertamentoDettaglio.ALIAS+ALL_FIELDS+","+Accertamento.FIELDS+","+Utente.FIELDS +
          " FROM mygov_accertamento_dettaglio " + AccertamentoDettaglio.ALIAS +
          " JOIN mygov_accertamento " + Accertamento.ALIAS +
          " ON "+Accertamento.ALIAS+".mygov_accertamento_id = "+AccertamentoDettaglio.ALIAS+".mygov_accertamento_id" +
          " JOIN mygov_anagrafica_stato " + AnagraficaStato.ALIAS +
          " ON "+AnagraficaStato.ALIAS+".mygov_anagrafica_stato_id = "+Accertamento.ALIAS+".mygov_anagrafica_stato_id" +
          " JOIN mygov_utente " + Utente.ALIAS +
          " ON "+Utente.ALIAS+".mygov_utente_id = "+AccertamentoDettaglio.ALIAS+".mygov_utente_id" +
          " WHERE "+AccertamentoDettaglio.ALIAS+".mygov_accertamento_id = :accertamentoId"
  )
  @RegisterFieldMapper(AccertamentoDettaglio.class)
  List<AccertamentoDettaglio> getByAccertamentoId(Long accertamentoId);

  @SqlQuery(
      "SELECT "+AccertamentoDettaglio.ALIAS+ALL_FIELDS+","+Accertamento.FIELDS+","+Utente.FIELDS +
          " FROM mygov_accertamento_dettaglio " + AccertamentoDettaglio.ALIAS +
          " JOIN mygov_accertamento " + Accertamento.ALIAS +
          " ON "+Accertamento.ALIAS+".mygov_accertamento_id = "+AccertamentoDettaglio.ALIAS+".mygov_accertamento_id" +
          " JOIN mygov_anagrafica_stato " + AnagraficaStato.ALIAS +
          " ON "+AnagraficaStato.ALIAS+".mygov_anagrafica_stato_id = "+Accertamento.ALIAS+".mygov_anagrafica_stato_id" +
          " JOIN mygov_utente " + Utente.ALIAS +
          " ON "+Utente.ALIAS+".mygov_utente_id = "+AccertamentoDettaglio.ALIAS+".mygov_utente_id" +
          " WHERE "+AccertamentoDettaglio.ALIAS+".cod_ipa_ente = :codIpaEnte" +
          "   AND "+AccertamentoDettaglio.ALIAS+".cod_iud = :codIud" +
          "   AND "+AccertamentoDettaglio.ALIAS+".cod_iuv = :codIuv" +
          "   AND "+AnagraficaStato.ALIAS+".de_tipo_stato = :deTipoStato " +
          "   AND "+AnagraficaStato.ALIAS+".cod_stato = :codStato"
  )
  @RegisterFieldMapper(AccertamentoDettaglio.class)
  List<AccertamentoDettaglio> getByEnteIudIuvStato(String codIpaEnte, String codIud, String codIuv, String deTipoStato, String codStato);


  @SqlQuery(
      "select " +
          "  "+AccertamentoDettaglio.ALIAS+".cod_ufficio " +
          ", "+AccertamentoDettaglio.ALIAS+".cod_tipo_dovuto " +
          ", "+AccertamentoDettaglio.ALIAS+".cod_capitolo " +
          ", "+AccertamentoDettaglio.ALIAS+".cod_accertamento " +
          ", sum("+AccertamentoDettaglio.ALIAS+".num_importo) as importo " +
          "  from mygov_accertamento_dettaglio "+AccertamentoDettaglio.ALIAS +
          "  join mygov_accertamento acc on acc.mygov_accertamento_id = "+AccertamentoDettaglio.ALIAS+".mygov_accertamento_id " +
          "  join mygov_anagrafica_stato "+ AnagraficaStato.ALIAS +
          "      on "+AnagraficaStato.ALIAS+".mygov_anagrafica_stato_id = acc.mygov_anagrafica_stato_id " +
          " where "+AccertamentoDettaglio.ALIAS+".cod_ipa_ente = :codIpaEnte " +
          "   and "+AnagraficaStato.ALIAS+".cod_stato = :codStato " +
          "   and "+AnagraficaStato.ALIAS+".de_tipo_stato = :deTipoStato " +
          "   and "+AccertamentoDettaglio.ALIAS+".cod_iud in (<ListCodIud>) " +
          " group by " +
          "  "+AccertamentoDettaglio.ALIAS+".cod_ufficio " +
          ", "+AccertamentoDettaglio.ALIAS+".cod_tipo_dovuto " +
          ", "+AccertamentoDettaglio.ALIAS+".cod_capitolo " +
          ", "+AccertamentoDettaglio.ALIAS+".cod_accertamento " +
          " order by " +
          "  "+AccertamentoDettaglio.ALIAS+".cod_ufficio " +
          ", "+AccertamentoDettaglio.ALIAS+".cod_tipo_dovuto " +
          ", "+AccertamentoDettaglio.ALIAS+".cod_capitolo " +
          ", "+AccertamentoDettaglio.ALIAS+".cod_accertamento "
  )
  @RegisterFieldMapper(BilancioTo.class)
  List<BilancioTo> getBilanciosByCodIpaTipoStatoCodIud(String codIpaEnte, String codStato, String deTipoStato,
                                                       @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> ListCodIud);

  @SqlQuery(
      "SELECT "+AccertamentoFlussoExport.ALIAS+ALL_FIELDS+" FROM get_pagamenti_inseriti_in_accertamento(:accertamentoId, :enteId, :codTipo, :codIuv, :codIud, :codIdUnivPag, " +
          ":dtEsitoFrom, :dtEsitoTo, :dtUltimoAggFrom, :dtUltimoAggTo, false, null, null) " + AccertamentoFlussoExport.ALIAS
  )
  @RegisterFieldMapper(AccertamentoFlussoExport.class)
  List<AccertamentoFlussoExport> get_pagamenti_inseriti_in_accertamento(
      Long accertamentoId, Long enteId, String codTipo, String codIuv, String codIud, String codIdUnivPag, LocalDate dtEsitoFrom,
      LocalDate dtEsitoTo, LocalDate dtUltimoAggFrom, LocalDate dtUltimoAggTo);

  @SqlQuery(
      "SELECT "+ AccertamentoPagamentoInseribile.ALIAS+ALL_FIELDS+" FROM get_pagamenti_inseribili_in_accertamento(:enteId, :codTipo, :codIuv, :codIud, :codIdUnivPag, " +
          ":dtEsitoFrom, :dtEsitoTo, :dtUltimoAggFrom, :dtUltimoAggTo, false, null, null) " + AccertamentoPagamentoInseribile.ALIAS
  )
  @RegisterFieldMapper(AccertamentoPagamentoInseribile.class)
  List<AccertamentoPagamentoInseribile> get_pagamenti_inseribili_in_accertamento(
      Long enteId, String codTipo, String codIuv, String codIud, String codIdUnivPag, LocalDate dtEsitoFrom,
      LocalDate dtEsitoTo, LocalDate dtUltimoAggFrom, LocalDate dtUltimoAggTo);

  @SqlUpdate(
      "DELETE FROM mygov_accertamento_dettaglio a WHERE a.mygov_accertamento_id = :accertamentoId " +
          " AND a.cod_ipa_ente = :codIpaEnte AND a.cod_tipo_dovuto = :codTipoDovuto " +
          " AND a.cod_iud = :codIud AND a.cod_iuv = :codIuv"
  )
  int deleteByAccertamentoIdCodIpaTipoIudIuv(Long accertamentoId, String codIpaEnte, String codTipoDovuto, String codIud, String codIuv);

  @SqlQuery(
      "SELECT * FROM ( " +
          "     SELECT " +
          "     DISTINCT ON (adt.cod_ufficio, adt.cod_capitolo) adt.cod_ufficio, " +
          "     uff.de_ufficio, " +
          "     adt.cod_capitolo, " +
          "     uff.de_capitolo, " +
          "     uff.de_anno_esercizio, " +
          "     adt.cod_accertamento, " +
          "     'n/a' as de_accertamento, " +
          "     adt.num_importo " +
          "     FROM " +
          "     mygov_accertamento_dettaglio AS adt 	LEFT JOIN 	  mygov_anagrafica_uff_cap_acc AS uff   ON " +
          "     adt.cod_ufficio = uff.cod_ufficio AND " +
          "     adt.cod_capitolo = uff.cod_capitolo AND " +
          "     adt.cod_tipo_dovuto = uff.cod_tipo_dovuto " +
          "     JOIN mygov_ente ente ON ente.mygov_ente_id = uff.mygov_ente_id AND ente.cod_ipa_ente = :codIpaEnte " +
          "     WHERE " +
          "     adt.mygov_accertamento_id = :accertamentoId AND " +
          "     adt.cod_tipo_dovuto = :codTipoDovuto AND " +
          "     adt.cod_iud = :codIud AND " +
          "     adt.cod_iuv = :codIuv AND " +
          "     adt.cod_ipa_ente = :codIpaEnte AND " +
          "     adt.cod_accertamento = 'n/a' AND " +
          "     (uff.flg_attivo = :flgAttivo OR :flgAttivo is null)" +
          " UNION " +
          "     SELECT " +
          "     DISTINCT ON (adt.cod_ufficio, adt.cod_capitolo, adt.cod_accertamento) adt.cod_ufficio, " +
          "     uff.de_ufficio, " +
          "     adt.cod_capitolo, " +
          "     uff.de_capitolo, " +
          "     uff.de_anno_esercizio, " +
          "     adt.cod_accertamento, " +
          "     uff.de_accertamento, " +
          "     adt.num_importo " +
          "     FROM " +
          "     mygov_accertamento_dettaglio AS adt 	LEFT JOIN 	  mygov_anagrafica_uff_cap_acc AS uff   ON " +
          "     adt.cod_ufficio = uff.cod_ufficio AND " +
          "     adt.cod_capitolo = uff.cod_capitolo AND " +
          "     adt.cod_accertamento = uff.cod_accertamento AND " +
          "     adt.cod_tipo_dovuto = uff.cod_tipo_dovuto " +
          "     JOIN mygov_ente ente ON ente.mygov_ente_id = uff.mygov_ente_id AND ente.cod_ipa_ente = :codIpaEnte " +
          "     WHERE " +
          "     adt.mygov_accertamento_id = :accertamentoId AND " +
          "     adt.cod_tipo_dovuto = :codTipoDovuto AND " +
          "     adt.cod_iud = :codIud AND " +
          "     adt.cod_iuv = :codIuv AND " +
          "     adt.cod_ipa_ente = :codIpaEnte AND " +
          "     adt.cod_accertamento <> 'n/a' AND " +
          "     (uff.flg_attivo = :flgAttivo OR :flgAttivo is null)" +
          ") as subq " +
          " ORDER BY subq.de_ufficio, subq.num_importo ASC"
  )
  @RegisterFieldMapper(CapitoloRT.class)
  List<CapitoloRT> getCapitoliByRT(Long accertamentoId, String codIpaEnte, String codTipoDovuto, String codIud, String codIuv, Boolean flgAttivo);
}
