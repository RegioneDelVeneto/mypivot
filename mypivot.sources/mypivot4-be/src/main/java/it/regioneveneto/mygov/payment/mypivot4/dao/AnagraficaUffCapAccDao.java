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
import it.regioneveneto.mygov.payment.mypivot4.model.AnagraficaUffCapAcc;
import it.regioneveneto.mygov.payment.mypivot4.model.EnteTipoDovuto;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface AnagraficaUffCapAccDao extends BaseDao {

  @SqlUpdate(
      "INSERT INTO mygov_anagrafica_uff_cap_acc (" +
          "  mygov_anagrafica_uff_cap_acc_id" +
          " ,mygov_ente_id" +
          " ,cod_tipo_dovuto" +
          " ,cod_ufficio" +
          " ,de_ufficio" +
          " ,flg_attivo" +
          " ,cod_capitolo" +
          " ,de_capitolo" +
          " ,de_anno_esercizio" +
          " ,cod_accertamento" +
          " ,de_accertamento" +
          " ,dt_creazione" +
          " ,dt_ultima_modifica" +
          ") VALUES (" +
          " nextval('mygov_anag_uff_cap_acc_mygov_anag_uff_cap_acc_id_seq')" +
          " ,:d.mygovEnteId" +
          " ,:d.codTipoDovuto" +
          " ,:d.codUfficio" +
          " ,:d.deUfficio" +
          " ,:d.flgAttivo" +
          " ,:d.codCapitolo" +
          " ,:d.deCapitolo" +
          " ,:d.deAnnoEsercizio" +
          " ,:d.codAccertamento" +
          " ,:d.deAccertamento" +
          " ,COALESCE(:d.dtCreazione, now())" +
          " ,COALESCE(:d.dtUltimaModifica, now())" +
          ")"
  )
  @GetGeneratedKeys("mygov_anagrafica_uff_cap_acc_id")
  long insert(@BindBean("d") AnagraficaUffCapAcc d);

  @SqlUpdate(
      "UPDATE mygov_anagrafica_uff_cap_acc SET " +
          "  mygov_ente_id = :d.mygovEnteId" +
          " ,cod_tipo_dovuto = :d.codTipoDovuto" +
          " ,cod_ufficio = :d.codUfficio" +
          " ,de_ufficio = :d.deUfficio" +
          " ,flg_attivo = :d.flgAttivo" +
          " ,cod_capitolo = :d.codCapitolo" +
          " ,de_capitolo = :d.deCapitolo" +
          " ,de_anno_esercizio = :d.deAnnoEsercizio" +
          " ,cod_accertamento = :d.codAccertamento" +
          " ,de_accertamento = :d.deAccertamento" +
          //" ,dt_creazione = :d.dtCreazione" +
          " ,dt_ultima_modifica = now()" +
          " WHERE mygov_anagrafica_uff_cap_acc_id = :d.mygovAnagraficaUffCapAccId"
  )
  int update(@BindBean("d") AnagraficaUffCapAcc d);

  @SqlQuery(
      "SELECT "+ AnagraficaUffCapAcc.ALIAS+ALL_FIELDS +
          " FROM mygov_anagrafica_uff_cap_acc " + AnagraficaUffCapAcc.ALIAS +
          " WHERE "+AnagraficaUffCapAcc.ALIAS+".mygov_anagrafica_uff_cap_acc_id = :mygovAnagraficaUffCapAccId"
  )
  @RegisterFieldMapper(AnagraficaUffCapAcc.class)
  AnagraficaUffCapAcc getById(Long mygovAnagraficaUffCapAccId);


  @SqlQuery(
      "SELECT anag.* FROM " +
      "(SELECT DISTINCT ON (cod_ufficio) cod_ufficio, de_ufficio" +
      " FROM mygov_anagrafica_uff_cap_acc " +
      " WHERE mygov_ente_id = :enteId AND cod_tipo_dovuto IN (<codTipiDovuto>) " +
      "   AND (flg_attivo = :flgAttivo OR :flgAttivo is null)" +
      " ) as anag" +
      " ORDER BY anag.de_ufficio ASC"
  )
  @RegisterFieldMapper(AnagraficaUffCapAcc.class)
  List<AnagraficaUffCapAcc> findDistinctUfficiByFilter(Long enteId, Boolean flgAttivo,
                                                       @BindList(onEmpty=BindList.EmptyHandling.NULL_STRING) List<String> codTipiDovuto);

  @SqlQuery(
      "SELECT anag.* FROM " +
          "(SELECT DISTINCT ON (cod_capitolo) cod_capitolo, de_capitolo" +
          " FROM mygov_anagrafica_uff_cap_acc" +
          " WHERE mygov_ente_id = :enteId AND cod_tipo_dovuto = :codTipoDovuto AND cod_ufficio = :codUfficio " +
          " AND (de_anno_esercizio = :annoEsercizio OR :annoEsercizio is null) " +
          " AND (flg_attivo = :flgAttivo OR :flgAttivo is null) " +
          " ) as anag " +
          " ORDER BY anag.de_capitolo ASC"
  )
  @RegisterFieldMapper(AnagraficaUffCapAcc.class)
  List<AnagraficaUffCapAcc> findDistinctCapitoliByEnteDovutoUfficio(Long enteId, String codTipoDovuto, String codUfficio, String annoEsercizio, Boolean flgAttivo);

  @SqlQuery(
      "SELECT anag.* FROM " +
          "(SELECT DISTINCT ON (cod_accertamento) cod_accertamento, de_accertamento" +
          " FROM mygov_anagrafica_uff_cap_acc" +
          " WHERE mygov_ente_id = :enteId AND cod_tipo_dovuto = :codTipoDovuto AND cod_ufficio = :codUfficio " +
          " AND de_anno_esercizio = :annoEsercizio AND cod_capitolo = :codCapitolo " +
          " AND cod_accertamento is not null AND cod_accertamento <> 'n/a' " +
          " AND (flg_attivo = :flgAttivo OR :flgAttivo is null) " +
          " ) as anag " +
          " ORDER BY anag.de_accertamento ASC"
  )
  @RegisterFieldMapper(AnagraficaUffCapAcc.class)
  List<AnagraficaUffCapAcc> findDistinctAccertamentiByEnteDovutoUfficioCapitolo(Long enteId, String codTipoDovuto, String codUfficio, String annoEsercizio, String codCapitolo, Boolean flgAttivo);

  @SqlQuery(
      "SELECT "+ AnagraficaUffCapAcc.ALIAS+ALL_FIELDS +
          " FROM mygov_anagrafica_uff_cap_acc " + AnagraficaUffCapAcc.ALIAS +
          " JOIN mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
          " ON "+EnteTipoDovuto.ALIAS+".cod_tipo = "+AnagraficaUffCapAcc.ALIAS+".cod_tipo_dovuto " +
          " AND "+EnteTipoDovuto.ALIAS+".mygov_ente_id = "+AnagraficaUffCapAcc.ALIAS+".mygov_ente_id " +
          " WHERE "+AnagraficaUffCapAcc.ALIAS+".mygov_ente_id = :idEnte " +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_tipo_dovuto = :codTipoDovuto OR (:codTipoDovuto is null))" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_ufficio = :codUfficio OR :codUfficio is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".de_ufficio ilike '%' || :deUfficio || '%' OR :deUfficio is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_capitolo = :codCapitolo OR :codCapitolo is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".de_capitolo ilike '%' || :deCapitolo || '%' OR :deCapitolo is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".de_anno_esercizio = :deAnnoEsercizio OR :deAnnoEsercizio is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_accertamento = :codAccertamento OR :codAccertamento is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".de_accertamento ilike '%' || :deAccertamento || '%' OR :deAccertamento is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".flg_attivo = true OR :flgAttivo is not true)"
  )
  @RegisterFieldMapper(AnagraficaUffCapAcc.class)
  List<AnagraficaUffCapAcc> getAccertamentiCapitoli(Long idEnte, String codTipoDovuto, String codUfficio, String deUfficio, Boolean flgAttivo, String codCapitolo,
                                                    String deCapitolo, String deAnnoEsercizio, String codAccertamento, String deAccertamento);

  @SqlQuery(
      "SELECT "+ AnagraficaUffCapAcc.ALIAS+ALL_FIELDS +
          " FROM mygov_anagrafica_uff_cap_acc " + AnagraficaUffCapAcc.ALIAS +
          " JOIN mygov_ente_tipo_dovuto " + EnteTipoDovuto.ALIAS +
          " ON "+EnteTipoDovuto.ALIAS+".cod_tipo = "+AnagraficaUffCapAcc.ALIAS+".cod_tipo_dovuto " +
          " AND "+EnteTipoDovuto.ALIAS+".mygov_ente_id = "+AnagraficaUffCapAcc.ALIAS+".mygov_ente_id " +
          " WHERE "+AnagraficaUffCapAcc.ALIAS+".mygov_ente_id = :idEnte " +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_tipo_dovuto = :codTipoDovuto OR (:codTipoDovuto is null))" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_ufficio = :codUfficio OR :codUfficio is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_capitolo = :codCapitolo OR :codCapitolo is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".de_anno_esercizio = :deAnnoEsercizio OR :deAnnoEsercizio is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_accertamento = :codAccertamento OR :codAccertamento is null)"
  )
  @RegisterFieldMapper(AnagraficaUffCapAcc.class)
  List<AnagraficaUffCapAcc> getAccertamentiCapitoliByCod(Long idEnte, String codTipoDovuto, String codUfficio, String codCapitolo, String deAnnoEsercizio, String codAccertamento);

  @SqlQuery(
      "SELECT "+ AnagraficaUffCapAcc.ALIAS+ALL_FIELDS +
          " FROM mygov_anagrafica_uff_cap_acc " + AnagraficaUffCapAcc.ALIAS +
          " WHERE "+AnagraficaUffCapAcc.ALIAS+".mygov_ente_id = :idEnte " +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_tipo_dovuto is null AND :codTipoDovuto = 'n/a')" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_ufficio = :codUfficio OR :codUfficio is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".de_ufficio ilike '%' || :deUfficio || '%' OR :deUfficio is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_capitolo = :codCapitolo OR :codCapitolo is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".de_capitolo ilike '%' || :deCapitolo || '%' OR :deCapitolo is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".de_anno_esercizio = :deAnnoEsercizio OR :deAnnoEsercizio is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".cod_accertamento = :codAccertamento OR :codAccertamento is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".de_accertamento ilike '%' || :deAccertamento || '%' OR :deAccertamento is null)" +
          "   AND ("+AnagraficaUffCapAcc.ALIAS+".flg_attivo = true OR :flgAttivo is not true)"
  )
  @RegisterFieldMapper(AnagraficaUffCapAcc.class)
  List<AnagraficaUffCapAcc> getAccertamentiCapitoliNA(Long idEnte, String codTipoDovuto, String codUfficio, String deUfficio, Boolean flgAttivo, String codCapitolo,
                                                    String deCapitolo, String deAnnoEsercizio, String codAccertamento, String deAccertamento);

  @SqlUpdate(
      "DELETE FROM mygov_anagrafica_uff_cap_acc WHERE mygov_anagrafica_uff_cap_acc_id = :anagraficaUffCapAccId"
  )
  int deleteById(Long anagraficaUffCapAccId);
}
