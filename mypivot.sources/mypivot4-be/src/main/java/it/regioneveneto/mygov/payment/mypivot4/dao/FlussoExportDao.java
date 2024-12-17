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
import it.regioneveneto.mygov.payment.mypivot4.model.*;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindBeanList;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

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

  String SQL_DETTAGLIO_CRUSCOTTO =
    "select "+ FlussoExport.ALIAS+ALL_FIELDS+", "+ Ente.FIELDS+", "+ ManageFlusso.FIELDS +
      "  from mygov_flusso_export " + FlussoExport.ALIAS +
      "  join mygov_ente "+Ente.ALIAS+" on "+Ente.ALIAS+".mygov_ente_id = "+FlussoExport.ALIAS+".mygov_ente_id"  +
      "  join mygov_manage_flusso "+ManageFlusso.ALIAS+" on "+ManageFlusso.ALIAS+".mygov_manage_flusso_id = "+FlussoExport.ALIAS+".mygov_manage_flusso_id" +
      " where "+Ente.ALIAS+".mygov_ente_id = :mygovEnteId" +
      "   and "+FlussoExport.ALIAS+".cod_tipo_dovuto = :codTipo " +
      "   and ("+FlussoExport.ALIAS+".dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento >= :from::date or :from::date is null)" +
      "   and ("+FlussoExport.ALIAS+".dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento <= :to::date or :to::date is null)" +
      "   and ("+FlussoExport.ALIAS+".cod_rp_silinviarp_id_univoco_versamento = :iuv or :iuv is null)" +
      "   and ("+FlussoExport.ALIAS+".cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss = :iur or :iur is null)" +
      "   and ("+FlussoExport.ALIAS+".de_e_istit_att_denominazione_attestante ilike '%' || :attestante || '%' or :attestante is null)" +
      "   and ("+FlussoExport.ALIAS+".cod_e_sogg_pag_id_univ_pag_codice_id_univoco = :cfPagatore or :cfPagatore is null)" +
      "   and ("+FlussoExport.ALIAS+".cod_e_sogg_pag_anagrafica_pagatore ilike '%' || :anagPagatore || '%' or :anagPagatore is null)" +
      "   and ("+FlussoExport.ALIAS+".cod_e_sogg_vers_id_univ_vers_codice_id_univoco = :cfVersante or :cfVersante is null)" +
      "   and ("+FlussoExport.ALIAS+".cod_e_sogg_vers_anagrafica_versante ilike '%' || :anagVersante || '%' or :anagVersante is null)";

  @SqlQuery(
    SQL_DETTAGLIO_CRUSCOTTO +
          "   and "+FlussoExport.ALIAS+".cod_iud in (<codIud>)"
  )
  @RegisterFieldMapper(FlussoExport.class)
  List<FlussoExport> getDettaglioCruscotto(Long mygovEnteId, String codTipo, @BindList(onEmpty= BindList.EmptyHandling.NULL_STRING) List<String> codIud, LocalDate from, LocalDate to,
                                           String iuv, String iur, String attestante, String cfPagatore, String anagPagatore, String cfVersante, String anagVersante);

  @SqlQuery(
    SQL_DETTAGLIO_CRUSCOTTO +
      "   and "+FlussoExport.ALIAS+".cod_iud in (select get_dettaglio_pagamenti_cruscotto(:anno, :mese, :giorno, :codUfficio, :codTipo, :codCapitolo, :mygovEnteId, :codAccertamento))"
  )
  @RegisterFieldMapper(FlussoExport.class)
  List<FlussoExport> getDettaglioCruscotto(Long mygovEnteId, String codTipo, LocalDate from, LocalDate to,
                                           String iuv, String iur, String attestante, String cfPagatore, String anagPagatore, String cfVersante, String anagVersante,
                                           Integer anno, Integer mese, Integer giorno, String codUfficio, String codCapitolo, String codAccertamento);


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

  String STMN_INSERT =
    " INSERT INTO mygov_flusso_export (" +
    "version, dt_creazione, dt_ultima_modifica, " +
    "mygov_ente_id, " +
    "mygov_manage_flusso_id, " +
    "de_nome_flusso, num_riga_flusso, cod_iud, " +
    "cod_rp_silinviarp_id_univoco_versamento, de_e_versione_oggetto, cod_e_dom_id_dominio, cod_e_dom_id_stazione_richiedente, cod_e_id_messaggio_ricevuta, dt_e_data_ora_messaggio_ricevuta, cod_e_riferimento_messaggio_richiesta, dt_e_riferimento_data_richiesta, cod_e_istit_att_id_univ_att_tipo_id_univoco, cod_e_istit_att_id_univ_att_codice_id_univoco, de_e_istit_att_denominazione_attestante, cod_e_istit_att_codice_unit_oper_attestante, de_e_istit_att_denom_unit_oper_attestante, de_e_istit_att_indirizzo_attestante, de_e_istit_att_civico_attestante, cod_e_istit_att_cap_attestante, de_e_istit_att_localita_attestante, de_e_istit_att_provincia_attestante, cod_e_istit_att_nazione_attestante, cod_e_ente_benef_id_univ_benef_tipo_id_univoco, cod_e_ente_benef_id_univ_benef_codice_id_univoco, de_e_ente_benef_denominazione_beneficiario, cod_e_ente_benef_codice_unit_oper_beneficiario, de_e_ente_benef_denom_unit_oper_beneficiario, de_e_ente_benef_indirizzo_beneficiario, de_e_ente_benef_civico_beneficiario, cod_e_ente_benef_cap_beneficiario, de_e_ente_benef_localita_beneficiario, de_e_ente_benef_provincia_beneficiario, cod_e_ente_benef_nazione_beneficiario, cod_e_sogg_vers_id_univ_vers_tipo_id_univoco, cod_e_sogg_vers_id_univ_vers_codice_id_univoco, cod_e_sogg_vers_anagrafica_versante, de_e_sogg_vers_indirizzo_versante, de_e_sogg_vers_civico_versante, cod_e_sogg_vers_cap_versante, de_e_sogg_vers_localita_versante, de_e_sogg_vers_provincia_versante, cod_e_sogg_vers_nazione_versante, de_e_sogg_vers_email_versante, cod_e_sogg_pag_id_univ_pag_tipo_id_univoco, cod_e_sogg_pag_id_univ_pag_codice_id_univoco, cod_e_sogg_pag_anagrafica_pagatore, de_e_sogg_pag_indirizzo_pagatore, de_e_sogg_pag_civico_pagatore, cod_e_sogg_pag_cap_pagatore, de_e_sogg_pag_localita_pagatore, de_e_sogg_pag_provincia_pagatore, cod_e_sogg_pag_nazione_pagatore, de_e_sogg_pag_email_pagatore, cod_e_dati_pag_codice_esito_pagamento, num_e_dati_pag_importo_totale_pagato, cod_e_dati_pag_id_univoco_versamento, cod_e_dati_pag_codice_contesto_pagamento, num_e_dati_pag_dati_sing_pag_singolo_importo_pagato, de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento, dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento, cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss, de_e_dati_pag_dati_sing_pag_causale_versamento, de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione, cod_tipo_dovuto, dt_acquisizione, indice_dati_singolo_pagamento, de_importa_dovuto_esito, de_importa_dovuto_fault_code, de_importa_dovuto_fault_string, de_importa_dovuto_fault_id, de_importa_dovuto_fault_description, num_importa_dovuto_fault_serial, bilancio, id_intermediario_pa, id_stazione_intermediario_pa, cod_tipo_dovuto_pa1, de_tipo_dovuto_pa1, cod_tassonomico_dovuto_pa1, cod_fiscale_pa1, de_nome_pa1" +
    ") VALUES (" +
    " 0 , NOW(), NOW()" +    //dt_ultima_modifica
    ", :fe.mygovEnteId.mygovEnteId" +   //mygov_ente_id
    ", :fe.mygovManageFlussoId.mygovManageFlussoId" +   //mygov_manage_flusso_id
    ", :fe.deNomeFlusso, :fe.numRigaFlusso, :fe.codIud" +    //cod_iud
    ", :fe.codRpSilinviarpIdUnivocoVersamento" +    //cod_rp_silinviarp_id_univoco_versamento
    ", :fe.deEVersioneOggetto" +    //de_e_versione_oggetto
    ", :fe.codEDomIdDominio" +    //cod_e_dom_id_dominio
    ", :fe.codEDomIdStazioneRichiedente" +    //cod_e_dom_id_stazione_richiedente
    ", :fe.codEIdMessaggioRicevuta" +    //cod_e_id_messaggio_ricevuta
    ", :fe.dtEDataOraMessaggioRicevuta" +    //dt_e_data_ora_messaggio_ricevuta
    ", :fe.codERiferimentoMessaggioRichiesta" +    //cod_e_riferimento_messaggio_richiesta
    ", :fe.dtERiferimentoDataRichiesta" +    //dt_e_riferimento_data_richiesta
    ", :fe.codEIstitAttIdUnivAttTipoIdUnivoco" +    //cod_e_istit_att_id_univ_att_tipo_id_univoco
    ", :fe.codEIstitAttIdUnivAttCodiceIdUnivoco" +    //cod_e_istit_att_id_univ_att_codice_id_univoco
    ", :fe.deEIstitAttDenominazioneAttestante" +    //de_e_istit_att_denominazione_attestante
    ", :fe.codEIstitAttCodiceUnitOperAttestante" +    //cod_e_istit_att_codice_unit_oper_attestante
    ", :fe.deEIstitAttDenomUnitOperAttestante" +    //de_e_istit_att_denom_unit_oper_attestante
    ", :fe.deEIstitAttIndirizzoAttestante" +    //de_e_istit_att_indirizzo_attestante
    ", :fe.deEIstitAttCivicoAttestante" +    //de_e_istit_att_civico_attestante
    ", :fe.codEIstitAttCapAttestante" +    //cod_e_istit_att_cap_attestante
    ", :fe.deEIstitAttLocalitaAttestante" +    //de_e_istit_att_localita_attestante
    ", :fe.deEIstitAttProvinciaAttestante" +    //de_e_istit_att_provincia_attestante
    ", :fe.codEIstitAttNazioneAttestante" +    //cod_e_istit_att_nazione_attestante
    ", :fe.codEEnteBenefIdUnivBenefTipoIdUnivoco" +    //cod_e_ente_benef_id_univ_benef_tipo_id_univoco
    ", :fe.codEEnteBenefIdUnivBenefCodiceIdUnivoco" +    //cod_e_ente_benef_id_univ_benef_codice_id_univoco
    ", :fe.deEEnteBenefDenominazioneBeneficiario" +    //de_e_ente_benef_denominazione_beneficiario
    ", :fe.codEEnteBenefCodiceUnitOperBeneficiario" +    //cod_e_ente_benef_codice_unit_oper_beneficiario
    ", :fe.deEEnteBenefDenomUnitOperBeneficiario" +    //de_e_ente_benef_denom_unit_oper_beneficiario
    ", :fe.deEEnteBenefIndirizzoBeneficiario" +    //de_e_ente_benef_indirizzo_beneficiario
    ", :fe.deEEnteBenefCivicoBeneficiario" +    //de_e_ente_benef_civico_beneficiario
    ", :fe.codEEnteBenefCapBeneficiario" +    //cod_e_ente_benef_cap_beneficiario
    ", :fe.deEEnteBenefLocalitaBeneficiario" +    //de_e_ente_benef_localita_beneficiario
    ", :fe.deEEnteBenefProvinciaBeneficiario" +    //de_e_ente_benef_provincia_beneficiario
    ", :fe.codEEnteBenefNazioneBeneficiario" +    //cod_e_ente_benef_nazione_beneficiario
    ", :fe.codESoggVersIdUnivVersTipoIdUnivoco" +    //cod_e_sogg_vers_id_univ_vers_tipo_id_univoco
    ", :fe.codESoggVersIdUnivVersCodiceIdUnivoco" +    //cod_e_sogg_vers_id_univ_vers_codice_id_univoco
    ", :fe.codESoggVersAnagraficaVersante" +    //cod_e_sogg_vers_anagrafica_versante
    ", :fe.deESoggVersIndirizzoVersante" +    //de_e_sogg_vers_indirizzo_versante
    ", :fe.deESoggVersCivicoVersante" +    //de_e_sogg_vers_civico_versante
    ", :fe.codESoggVersCapVersante" +    //cod_e_sogg_vers_cap_versante
    ", :fe.deESoggVersLocalitaVersante" +    //de_e_sogg_vers_localita_versante
    ", :fe.deESoggVersProvinciaVersante" +    //de_e_sogg_vers_provincia_versante
    ", :fe.codESoggVersNazioneVersante" +    //cod_e_sogg_vers_nazione_versante
    ", :fe.deESoggVersEmailVersante" +    //de_e_sogg_vers_email_versante
    ", :fe.codESoggPagIdUnivPagTipoIdUnivoco" +    //cod_e_sogg_pag_id_univ_pag_tipo_id_univoco
    ", :fe.codESoggPagIdUnivPagCodiceIdUnivoco" +    //cod_e_sogg_pag_id_univ_pag_codice_id_univoco
    ", :fe.codESoggPagAnagraficaPagatore" +    //cod_e_sogg_pag_anagrafica_pagatore
    ", :fe.deESoggPagIndirizzoPagatore" +    //de_e_sogg_pag_indirizzo_pagatore
    ", :fe.deESoggPagCivicoPagatore" +    //de_e_sogg_pag_civico_pagatore
    ", :fe.codESoggPagCapPagatore" +    //cod_e_sogg_pag_cap_pagatore
    ", :fe.deESoggPagLocalitaPagatore" +    //de_e_sogg_pag_localita_pagatore
    ", :fe.deESoggPagProvinciaPagatore" +    //de_e_sogg_pag_provincia_pagatore
    ", :fe.codESoggPagNazionePagatore" +    //cod_e_sogg_pag_nazione_pagatore
    ", :fe.deESoggPagEmailPagatore" +    //de_e_sogg_pag_email_pagatore
    ", :fe.codEDatiPagCodiceEsitoPagamento" +    //cod_e_dati_pag_codice_esito_pagamento
    ", :fe.numEDatiPagImportoTotalePagato" +    //num_e_dati_pag_importo_totale_pagato
    ", :fe.codEDatiPagIdUnivocoVersamento" +    //cod_e_dati_pag_id_univoco_versamento
    ", :fe.codEDatiPagCodiceContestoPagamento" +    //cod_e_dati_pag_codice_contesto_pagamento
    ", :fe.numEDatiPagDatiSingPagSingoloImportoPagato" +    //num_e_dati_pag_dati_sing_pag_singolo_importo_pagato
    ", :fe.deEDatiPagDatiSingPagEsitoSingoloPagamento" +    //de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento
    ", :fe.dtEDatiPagDatiSingPagDataEsitoSingoloPagamento" +    //dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento
    ", :fe.codEDatiPagDatiSingPagIdUnivocoRiscoss" +    //cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss
    ", :fe.deEDatiPagDatiSingPagCausaleVersamento" +    //de_e_dati_pag_dati_sing_pag_causale_versamento
    ", :fe.deEDatiPagDatiSingPagDatiSpecificiRiscossione" +    //de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione
    ", :fe.codTipoDovuto" +    //cod_tipo_dovuto
    ", :fe.dtAcquisizione" +    //dt_acquisizione
    ", :fe.indiceDatiSingoloPagamento" +    //indice_dati_singolo_pagamento
    ", :fe.deImportaDovutoEsito" +    //de_importa_dovuto_esito
    ", :fe.deImportaDovutoFaultCode" +    //de_importa_dovuto_fault_code
    ", :fe.deImportaDovutoFaultString" +    //de_importa_dovuto_fault_string
    ", :fe.deImportaDovutoFaultId" +    //de_importa_dovuto_fault_id
    ", :fe.deImportaDovutoFaultDescription" +    //de_importa_dovuto_fault_description
    ", :fe.numImportaDovutoFaultSerial" +    //num_importa_dovuto_fault_serial
    ", :fe.bilancio" +    //bilancio
    ", '80007580279'::character varying" +    //id_intermediario_pa
    ", '80007580279_01'::character varying" +    //id_stazione_intermediario_pa
    ", :fe.codTipoDovutoPa1" +
    ", :fe.deTipoDovutoPa1" +
    ", :fe.codTassonomicoDovutoPa1" +
    ", :fe.codFiscalePa1" +
    ", :fe.deNomePa1" +
    ")";
  @SqlUpdate(STMN_INSERT)
  int insert(@BindBean("fe") FlussoExport fe);

  String STMN_UPDATE_SET =
    "SET " +
      " version = :fe.version" +
      ", dt_creazione = :fe.dtCreazione " +
      ", dt_ultima_modifica = NOW() " +
      ", mygov_ente_id = :fe.mygovEnteId.mygovEnteId " +
      ", mygov_manage_flusso_id = :fe.mygovManageFlussoId.mygovManageFlussoId " +
      ", de_nome_flusso = :fe.deNomeFlusso" +
      ", num_riga_flusso = :fe.numRigaFlusso" +
      ", cod_iud = :fe.codIud" +
      ", cod_rp_silinviarp_id_univoco_versamento = :fe.codRpSilinviarpIdUnivocoVersamento" +
      ", de_e_versione_oggetto = :fe.deEVersioneOggetto" +
      ", cod_e_dom_id_dominio = :fe.codEDomIdDominio" +
      ", cod_e_dom_id_stazione_richiedente = :fe.codEDomIdStazioneRichiedente" +
      ", cod_e_id_messaggio_ricevuta = :fe.codEIdMessaggioRicevuta" +
      ", dt_e_data_ora_messaggio_ricevuta = :fe.dtEDataOraMessaggioRicevuta" +
      ", cod_e_riferimento_messaggio_richiesta = :fe.codERiferimentoMessaggioRichiesta" +
      ", dt_e_riferimento_data_richiesta = :fe.dtERiferimentoDataRichiesta" +
      ", cod_e_istit_att_id_univ_att_tipo_id_univoco = :fe.codEIstitAttIdUnivAttTipoIdUnivoco" +
      ", cod_e_istit_att_id_univ_att_codice_id_univoco = :fe.codEIstitAttIdUnivAttCodiceIdUnivoco" +
      ", de_e_istit_att_denominazione_attestante = :fe.deEIstitAttDenominazioneAttestante" +
      ", cod_e_istit_att_codice_unit_oper_attestante = :fe.codEIstitAttCodiceUnitOperAttestante" +
      ", de_e_istit_att_denom_unit_oper_attestante = :fe.deEIstitAttDenomUnitOperAttestante" +
      ", de_e_istit_att_indirizzo_attestante = :fe.deEIstitAttIndirizzoAttestante" +
      ", de_e_istit_att_civico_attestante = :fe.deEIstitAttCivicoAttestante" +
      ", cod_e_istit_att_cap_attestante = :fe.codEIstitAttCapAttestante" +
      ", de_e_istit_att_localita_attestante = :fe.deEIstitAttLocalitaAttestante" +
      ", de_e_istit_att_provincia_attestante = :fe.deEIstitAttProvinciaAttestante" +
      ", cod_e_istit_att_nazione_attestante = :fe.codEIstitAttNazioneAttestante" +
      ", cod_e_ente_benef_id_univ_benef_tipo_id_univoco = :fe.codEEnteBenefIdUnivBenefTipoIdUnivoco" +
      ", cod_e_ente_benef_id_univ_benef_codice_id_univoco = :fe.codEEnteBenefIdUnivBenefCodiceIdUnivoco" +
      ", de_e_ente_benef_denominazione_beneficiario = :fe.deEEnteBenefDenominazioneBeneficiario" +
      ", cod_e_ente_benef_codice_unit_oper_beneficiario = :fe.codEEnteBenefCodiceUnitOperBeneficiario" +
      ", de_e_ente_benef_denom_unit_oper_beneficiario = :fe.deEEnteBenefDenomUnitOperBeneficiario" +
      ", de_e_ente_benef_indirizzo_beneficiario = :fe.deEEnteBenefIndirizzoBeneficiario" +
      ", de_e_ente_benef_civico_beneficiario = :fe.deEEnteBenefCivicoBeneficiario" +
      ", cod_e_ente_benef_cap_beneficiario = :fe.codEEnteBenefCapBeneficiario" +
      ", de_e_ente_benef_localita_beneficiario = :fe.deEEnteBenefLocalitaBeneficiario" +
      ", de_e_ente_benef_provincia_beneficiario = :fe.deEEnteBenefProvinciaBeneficiario" +
      ", cod_e_ente_benef_nazione_beneficiario = :fe.codEEnteBenefNazioneBeneficiario" +
      ", cod_e_sogg_vers_id_univ_vers_tipo_id_univoco = :fe.codESoggVersIdUnivVersTipoIdUnivoco" +
      ", cod_e_sogg_vers_id_univ_vers_codice_id_univoco = :fe.codESoggVersIdUnivVersCodiceIdUnivoco" +
      ", cod_e_sogg_vers_anagrafica_versante = :fe.codESoggVersAnagraficaVersante" +
      ", de_e_sogg_vers_indirizzo_versante = :fe.deESoggVersIndirizzoVersante" +
      ", de_e_sogg_vers_civico_versante = :fe.deESoggVersCivicoVersante" +
      ", cod_e_sogg_vers_cap_versante = :fe.codESoggVersCapVersante" +
      ", de_e_sogg_vers_localita_versante = :fe.deESoggVersLocalitaVersante" +
      ", de_e_sogg_vers_provincia_versante = :fe.deESoggVersProvinciaVersante" +
      ", cod_e_sogg_vers_nazione_versante = :fe.codESoggVersNazioneVersante" +
      ", de_e_sogg_vers_email_versante = :fe.deESoggVersEmailVersante" +
      ", cod_e_sogg_pag_id_univ_pag_tipo_id_univoco = :fe.codESoggPagIdUnivPagTipoIdUnivoco" +
      ", cod_e_sogg_pag_id_univ_pag_codice_id_univoco = :fe.codESoggPagIdUnivPagCodiceIdUnivoco" +
      ", cod_e_sogg_pag_anagrafica_pagatore = :fe.codESoggPagAnagraficaPagatore" +
      ", de_e_sogg_pag_indirizzo_pagatore = :fe.deESoggPagIndirizzoPagatore" +
      ", de_e_sogg_pag_civico_pagatore = :fe.deESoggPagCivicoPagatore" +
      ", cod_e_sogg_pag_cap_pagatore = :fe.codESoggPagCapPagatore" +
      ", de_e_sogg_pag_localita_pagatore = :fe.deESoggPagLocalitaPagatore" +
      ", de_e_sogg_pag_provincia_pagatore = :fe.deESoggPagProvinciaPagatore" +
      ", cod_e_sogg_pag_nazione_pagatore = :fe.codESoggPagNazionePagatore" +
      ", de_e_sogg_pag_email_pagatore = :fe.deESoggPagEmailPagatore" +
      ", cod_e_dati_pag_codice_esito_pagamento = :fe.codEDatiPagCodiceEsitoPagamento" +
      ", num_e_dati_pag_importo_totale_pagato = :fe.numEDatiPagImportoTotalePagato" +
      ", cod_e_dati_pag_id_univoco_versamento = :fe.codEDatiPagIdUnivocoVersamento" +
      ", cod_e_dati_pag_codice_contesto_pagamento = :fe.codEDatiPagCodiceContestoPagamento" +
      ", num_e_dati_pag_dati_sing_pag_singolo_importo_pagato = :fe.numEDatiPagDatiSingPagSingoloImportoPagato" +
      ", de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento = :fe.deEDatiPagDatiSingPagEsitoSingoloPagamento" +
      ", dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento = :fe.dtEDatiPagDatiSingPagDataEsitoSingoloPagamento" +
      ", cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss = :fe.codEDatiPagDatiSingPagIdUnivocoRiscoss" +
      ", de_e_dati_pag_dati_sing_pag_causale_versamento = :fe.deEDatiPagDatiSingPagCausaleVersamento" +
      ", de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione = :fe.deEDatiPagDatiSingPagDatiSpecificiRiscossione" +
      ", cod_tipo_dovuto = :fe.codTipoDovuto" +
      ", dt_acquisizione = :fe.dtAcquisizione" +
      ", indice_dati_singolo_pagamento = :fe.indiceDatiSingoloPagamento" +
      ", de_importa_dovuto_esito = :fe.deImportaDovutoEsito" +
      ", de_importa_dovuto_fault_code = :fe.deImportaDovutoFaultCode" +
      ", de_importa_dovuto_fault_string = :fe.deImportaDovutoFaultString" +
      ", de_importa_dovuto_fault_id = :fe.deImportaDovutoFaultId" +
      ", de_importa_dovuto_fault_description = :fe.deImportaDovutoFaultDescription" +
      ", num_importa_dovuto_fault_serial = :fe.numImportaDovutoFaultSerial" +
      ", bilancio = :fe.bilancio" +
      ", id_intermediario_pa = '80007580279'::character varying" +
      ", id_stazione_intermediario_pa = '80007580279_01'::character varying" +
      ", cod_tipo_dovuto_pa1 = :fe.codTipoDovutoPa1" +
      ", de_tipo_dovuto_pa1 = :fe.deTipoDovutoPa1" +
      ", cod_tassonomico_dovuto_pa1 = :fe.codTassonomicoDovutoPa1" +
      ", cod_fiscale_pa1 = :fe.codFiscalePa1" +
      ", de_nome_pa1 = :fe.deNomePa1" +
      " WHERE mygov_flusso_export.mygov_ente_id = :fe.mygovEnteId.mygovEnteId AND" +
      " mygov_flusso_export.cod_rp_silinviarp_id_univoco_versamento = :fe.codRpSilinviarpIdUnivocoVersamento AND" +
      " mygov_flusso_export.cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss = :fe.codEDatiPagDatiSingPagIdUnivocoRiscoss AND" +
      " mygov_flusso_export.indice_dati_singolo_pagamento = :fe.indiceDatiSingoloPagamento";

  @SqlUpdate("UPDATE mygov_flusso_export "+STMN_UPDATE_SET)
  int update(@BindBean("fe") FlussoExport fe);

  @SqlUpdate(
    STMN_INSERT +
      " on conflict (mygov_ente_id, cod_rp_silinviarp_id_univoco_versamento, cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss, indice_dati_singolo_pagamento) do update " +
    STMN_UPDATE_SET
  )
  int upsert(@BindBean("fe") FlussoExport fe);
}