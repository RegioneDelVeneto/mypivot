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
package it.regioneveneto.mygov.payment.mypivot4.mapper;

import it.gov.digitpa.schemas._2011.pagamenti.CtDatiSingoliPagamenti;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoMittente;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoRicevente;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.dto.PagamentiRiconciliatiTo;
import it.veneto.regione.pagamenti.pivot.ente.CtDatiVersamentoExport;
import it.veneto.regione.pagamenti.pivot.ente.CtExport;
import it.veneto.regione.pagamenti.pivot.ente.CtFlussoRiversamento;
import it.veneto.regione.pagamenti.pivot.ente.CtPagamentiRiconciliati;
import it.veneto.regione.schemas._2012.pagamenti.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class PagamentiRiconciliatiToMapper implements RowMapper<PagamentiRiconciliatiTo> {

  @Override
  public PagamentiRiconciliatiTo map(ResultSet rs, StatementContext ctx) throws SQLException {
    String tipoDovuto = rs.getString("cod_tipo_dovuto_e");
    // String codiceIUV = rs.getString("cod_e_dati_pag_id_univoco_versamento_e");
    if (StringUtils.isBlank(tipoDovuto)) {
      tipoDovuto = "INCASSO_SENZA_RT";
    }
    CtPagamentiRiconciliati ctPagamentiRiconciliati = new CtPagamentiRiconciliati();
    ctPagamentiRiconciliati.setPagamento(extractCtExport(rs));
    ctPagamentiRiconciliati.setRiversamento(extractCtFlussoRiversamento(rs));
    // calcolo la data di aggiornamento del pagato riconciliato estratto
    Date dt_acquisizione_e = rs.getDate("dt_acquisizione_e");
    Date dt_acquisizione_r = rs.getDate("dt_acquisizione_r");
    Date data = dt_acquisizione_r != null && dt_acquisizione_r.after(dt_acquisizione_e) ? dt_acquisizione_r : dt_acquisizione_e;
    ctPagamentiRiconciliati.setData(Utilities.toXMLGregorianCalendar(data));
    return PagamentiRiconciliatiTo.builder()
        .ctPagamentiRiconciliati(ctPagamentiRiconciliati)
        .tipoDovuto(tipoDovuto)
        .build();
  }

  private CtExport extractCtExport(ResultSet rs) throws SQLException {
    // CtExport
    CtExport ctExport = new CtExport();
    // "versioneOggetto",
    ctExport.setVersioneOggetto(rs.getString("de_e_versione_oggetto_e"));
    // "dominio",
    ctExport.setDominio(extractCtDominio(rs));
    // "identificativoMessaggioRicevuta",
    ctExport.setIdentificativoMessaggioRicevuta(rs.getString("cod_e_id_messaggio_ricevuta_e"));
    // "dataOraMessaggioRicevuta",
    ctExport.setDataOraMessaggioRicevuta(Utilities.toXMLGregorianCalendar(rs.getDate("dt_e_data_ora_messaggio_ricevuta_e")));
    // "riferimentoMessaggioRichiesta",
    ctExport.setRiferimentoMessaggioRichiesta(rs.getString("cod_e_riferimento_messaggio_richiesta_e"));
    // "riferimentoDataRichiesta",
    ctExport.setRiferimentoDataRichiesta(Utilities.toXMLGregorianCalendar(rs.getDate("dt_e_riferimento_data_richiesta_e")));
    // "istitutoAttestante",
    if (StringUtils.isNotBlank(rs.getString("de_e_istit_att_denominazione_attestante_e"))) {
      ctExport.setIstitutoAttestante(extractCtIstitutoAttestante(rs));
    }
    // "enteBeneficiario",
    if (StringUtils.isNotBlank(rs.getString("de_e_ente_benef_denominazione_beneficiario_e"))) {
      ctExport.setEnteBeneficiario(extractCtEnteBeneficiario(rs));
    }
    // "soggettoVersante",
    if (StringUtils.isNotBlank(rs.getString("cod_e_sogg_vers_anagrafica_versante_e"))) {
      ctExport.setSoggettoVersante(extractCtSoggettoVersante(rs));
    }
    // "soggettoPagatore",
    if (StringUtils.isNotBlank(rs.getString("cod_e_sogg_pag_anagrafica_pagatore_e"))) {
      ctExport.setSoggettoPagatore(extractCtSoggettoPagatore(rs));
    }
    // "datiPagamento",
    ctExport.setDatiPagamento(extractCtDatiVersamentoExport(rs));
    // "codice_iud"
    ctExport.setIdentificativoUnivocoDovuto(rs.getString("cod_iud_e"));
    return ctExport;
  }

  private CtDominio extractCtDominio(ResultSet rs) throws SQLException {
    CtDominio ctDominio = new CtDominio();
    // "identificativoDominio",
    ctDominio.setIdentificativoDominio(rs.getString("cod_e_dom_id_dominio_e"));
    // "identificativoStazioneRichiedente"
    ctDominio.setIdentificativoStazioneRichiedente(rs.getString("cod_e_dom_id_stazione_richiedente_e"));
    return ctDominio;
  }

  private CtIstitutoAttestante extractCtIstitutoAttestante(ResultSet rs) throws SQLException {
    CtIstitutoAttestante istitutoAttestante = new CtIstitutoAttestante();
    // "identificativoUnivocoAttestante",
    CtIdentificativoUnivoco identificativoUnivocoAttestante = new CtIdentificativoUnivoco();
    // "tipoIdentificativoUnivoco",
    identificativoUnivocoAttestante.setTipoIdentificativoUnivoco(StTipoIdentificativoUnivoco.fromValue(rs.getString("cod_e_istit_att_id_univ_att_tipo_id_univoco_e")));
    // "codiceIdentificativoUnivoco"
    identificativoUnivocoAttestante.setCodiceIdentificativoUnivoco(rs.getString("cod_e_istit_att_id_univ_att_codice_id_univoco_e"));
    istitutoAttestante.setIdentificativoUnivocoAttestante(identificativoUnivocoAttestante);
    // "denominazioneAttestante",
    istitutoAttestante.setDenominazioneAttestante(rs.getString("de_e_istit_att_denominazione_attestante_e"));
    // "codiceUnitOperAttestante",
    istitutoAttestante.setCodiceUnitOperAttestante(rs.getString("cod_e_istit_att_codice_unit_oper_attestante_e"));
    // "denomUnitOperAttestante",
    istitutoAttestante.setDenomUnitOperAttestante(rs.getString("de_e_istit_att_denom_unit_oper_attestante_e"));
    // "indirizzoAttestante",
    istitutoAttestante.setIndirizzoAttestante(rs.getString("de_e_istit_att_indirizzo_attestante_e"));
    // "civicoAttestante",
    istitutoAttestante.setCivicoAttestante(rs.getString("de_e_istit_att_civico_attestante_e"));
    // "capAttestante",
    istitutoAttestante.setCapAttestante(rs.getString("cod_e_istit_att_cap_attestante_e"));
    // "localitaAttestante",
    istitutoAttestante.setLocalitaAttestante(rs.getString("de_e_istit_att_localita_attestante_e"));
    // "provinciaAttestante",
    istitutoAttestante.setProvinciaAttestante(rs.getString("de_e_istit_att_provincia_attestante_e"));
    // "nazioneAttestante"
    istitutoAttestante.setNazioneAttestante(rs.getString("cod_e_istit_att_nazione_attestante_e"));
    return istitutoAttestante;
  }

  private CtEnteBeneficiario extractCtEnteBeneficiario(ResultSet rs) throws SQLException {
    CtEnteBeneficiario enteBeneficiario = new CtEnteBeneficiario();
    // "identificativoUnivocoBeneficiario",
    CtIdentificativoUnivocoPersonaG identificativoUnivocoBeneficiario = new CtIdentificativoUnivocoPersonaG();
    // "tipoIdentificativoUnivoco",
    identificativoUnivocoBeneficiario.setTipoIdentificativoUnivoco(StTipoIdentificativoUnivocoPersG.fromValue(rs.getString("cod_e_ente_benef_id_univ_benef_tipo_id_univoco_e")));
    // "codiceIdentificativoUnivoco"
    identificativoUnivocoBeneficiario.setCodiceIdentificativoUnivoco(rs.getString("cod_e_ente_benef_id_univ_benef_codice_id_univoco_e"));
    enteBeneficiario.setIdentificativoUnivocoBeneficiario(identificativoUnivocoBeneficiario);
    // "denominazioneBeneficiario",
    enteBeneficiario.setDenominazioneBeneficiario(rs.getString("de_e_ente_benef_denominazione_beneficiario_e"));
    // "codiceUnitOperBeneficiario",
    enteBeneficiario.setCodiceUnitOperBeneficiario(rs.getString("cod_e_ente_benef_codice_unit_oper_beneficiario_e"));
    // "denomUnitOperBeneficiario",
    enteBeneficiario.setDenomUnitOperBeneficiario(rs.getString("de_e_ente_benef_denom_unit_oper_beneficiario_e"));
    // "indirizzoBeneficiario",
    enteBeneficiario.setIndirizzoBeneficiario(rs.getString("de_e_ente_benef_indirizzo_beneficiario_e"));
    // "civicoBeneficiario",
    enteBeneficiario.setCivicoBeneficiario(rs.getString("de_e_ente_benef_civico_beneficiario_e"));
    // "capBeneficiario",
    enteBeneficiario.setCapBeneficiario(rs.getString("cod_e_ente_benef_cap_beneficiario_e"));
    // "localitaBeneficiario",
    enteBeneficiario.setLocalitaBeneficiario(rs.getString("de_e_ente_benef_localita_beneficiario_e"));
    // "provinciaBeneficiario",
    enteBeneficiario.setProvinciaBeneficiario(rs.getString("de_e_ente_benef_provincia_beneficiario_e"));
    // "nazioneBeneficiario"
    enteBeneficiario.setNazioneBeneficiario(rs.getString("cod_e_ente_benef_nazione_beneficiario_e"));
    return enteBeneficiario;
  }

  private CtSoggettoVersante extractCtSoggettoVersante(ResultSet rs) throws SQLException {
    CtSoggettoVersante soggettoVersante = new CtSoggettoVersante();
    // "identificativoUnivocoVersante",
    CtIdentificativoUnivocoPersonaFG identificativoUnivocoVersante = new CtIdentificativoUnivocoPersonaFG();
    // "tipoIdentificativoUnivoco",
    identificativoUnivocoVersante.setTipoIdentificativoUnivoco(StTipoIdentificativoUnivocoPersFG.valueOf(rs.getString("cod_e_sogg_vers_id_univ_vers_tipo_id_univoco_e")));
    // "codiceIdentificativoUnivoco"
    identificativoUnivocoVersante.setCodiceIdentificativoUnivoco(rs.getString("cod_e_sogg_vers_id_univ_vers_codice_id_univoco_e"));
    soggettoVersante.setIdentificativoUnivocoVersante(identificativoUnivocoVersante);
    // "anagraficaVersante",
    soggettoVersante.setAnagraficaVersante(rs.getString("cod_e_sogg_vers_anagrafica_versante_e"));
    // "indirizzoVersante",
    soggettoVersante.setIndirizzoVersante(rs.getString("de_e_sogg_vers_indirizzo_versante_e"));
    // "civicoVersante",
    soggettoVersante.setCivicoVersante(rs.getString("de_e_sogg_vers_civico_versante_e"));
    // "capVersante",
    soggettoVersante.setCapVersante(rs.getString("cod_e_sogg_vers_cap_versante_e"));
    // "localitaVersante",
    soggettoVersante.setLocalitaVersante(rs.getString("de_e_sogg_vers_localita_versante_e"));
    // "provinciaVersante",
    soggettoVersante.setProvinciaVersante(rs.getString("de_e_sogg_vers_provincia_versante_e"));
    // "nazioneVersante",
    soggettoVersante.setNazioneVersante(rs.getString("cod_e_sogg_vers_nazione_versante_e"));
    // "eMailVersante"
    soggettoVersante.setEMailVersante(rs.getString("de_e_sogg_vers_email_versante_e"));
    return soggettoVersante;
  }

  private CtSoggettoPagatore extractCtSoggettoPagatore(ResultSet rs) throws SQLException {
    CtSoggettoPagatore soggettoPagatore = new CtSoggettoPagatore();
    // "identificativoUnivocoPagatore",
    CtIdentificativoUnivocoPersonaFG identificativoUnivocoPagatore = new CtIdentificativoUnivocoPersonaFG();
    // "tipoIdentificativoUnivoco",
    identificativoUnivocoPagatore.setTipoIdentificativoUnivoco(StTipoIdentificativoUnivocoPersFG.valueOf(rs.getString("cod_e_sogg_pag_id_univ_pag_tipo_id_univoco_e")));
    // "codiceIdentificativoUnivoco"
    identificativoUnivocoPagatore.setCodiceIdentificativoUnivoco(rs.getString("cod_e_sogg_pag_id_univ_pag_codice_id_univoco_e"));
    soggettoPagatore.setIdentificativoUnivocoPagatore(identificativoUnivocoPagatore);
    // "anagraficaPagatore",
    soggettoPagatore.setAnagraficaPagatore(rs.getString("cod_e_sogg_pag_anagrafica_pagatore_e"));
    // "indirizzoPagatore",
    soggettoPagatore.setIndirizzoPagatore(rs.getString("de_e_sogg_pag_indirizzo_pagatore_e"));
    // "civicoPagatore",
    soggettoPagatore.setCivicoPagatore(rs.getString("de_e_sogg_pag_civico_pagatore_e"));
    // "capPagatore",
    soggettoPagatore.setCapPagatore(rs.getString("cod_e_sogg_pag_cap_pagatore_e"));
    // "localitaPagatore",
    soggettoPagatore.setLocalitaPagatore(rs.getString("de_e_sogg_pag_localita_pagatore_e"));
    // "provinciaPagatore",
    soggettoPagatore.setProvinciaPagatore(rs.getString("de_e_sogg_pag_provincia_pagatore_e"));
    // "nazionePagatore",
    soggettoPagatore.setNazionePagatore(rs.getString("cod_e_sogg_pag_nazione_pagatore_e"));
    // "eMailPagatore"
    soggettoPagatore.setEMailPagatore(rs.getString("de_e_sogg_pag_email_pagatore_e"));
    return soggettoPagatore;
  }

  private CtDatiVersamentoExport extractCtDatiVersamentoExport(ResultSet rs) throws SQLException {
    CtDatiVersamentoExport ctDatiVersamentoExport = new CtDatiVersamentoExport();
    // "codiceEsitoPagamento",
    ctDatiVersamentoExport.setCodiceEsitoPagamento(rs.getString("cod_e_dati_pag_codice_esito_pagamento_e"));
    // "importoTotalePagato",
    ctDatiVersamentoExport.setImportoTotalePagato(rs.getBigDecimal("num_e_dati_pag_importo_totale_pagato_e"));
    // "identificativoUnivocoVersamento",
    ctDatiVersamentoExport.setIdentificativoUnivocoVersamento(rs.getString("cod_e_dati_pag_id_univoco_versamento_e"));
    // "codiceContestoPagamento",
    ctDatiVersamentoExport.setCodiceContestoPagamento(rs.getString("cod_e_dati_pag_codice_contesto_pagamento_e"));
    // "datiSingoloPagamento"
    ctDatiVersamentoExport.setDatiSingoloPagamento(extractCtDatiSingoloPagamentoEsito(rs));
    return ctDatiVersamentoExport;
  }

  private CtDatiSingoloPagamentoEsito extractCtDatiSingoloPagamentoEsito(ResultSet rs) throws SQLException {
    CtDatiSingoloPagamentoEsito ctDatiSingoloPagamentoEsito = new CtDatiSingoloPagamentoEsito();
    // "singoloImportoPagato",
    ctDatiSingoloPagamentoEsito.setSingoloImportoPagato(rs.getBigDecimal("num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e"));
    // "esitoSingoloPagamento",
    ctDatiSingoloPagamentoEsito.setEsitoSingoloPagamento(rs.getString("de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento_e"));
    // "dataEsitoSingoloPagamento",
    ctDatiSingoloPagamentoEsito.setDataEsitoSingoloPagamento(Utilities.toXMLGregorianCalendar(rs.getDate("dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento_e")));
    // "identificativoUnivocoRiscossione",
    ctDatiSingoloPagamentoEsito.setIdentificativoUnivocoRiscossione(rs.getString("cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss_e"));
    // "causaleVersamento",
    ctDatiSingoloPagamentoEsito.setCausaleVersamento(rs.getString("de_e_dati_pag_dati_sing_pag_causale_versamento_e"));
    // "datiSpecificiRiscossione"
    ctDatiSingoloPagamentoEsito.setDatiSpecificiRiscossione(rs.getString("de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione_e"));
    return ctDatiSingoloPagamentoEsito;
  }

  private CtFlussoRiversamento extractCtFlussoRiversamento(ResultSet rs) throws SQLException {
    // CtFlussoRiversamento
    CtFlussoRiversamento ctFlussoRiversamento = new CtFlussoRiversamento();
    // "versioneOggetto",
    ctFlussoRiversamento.setVersioneOggetto(rs.getString("versione_oggetto_r"));
    // "identificativoFlusso",
    ctFlussoRiversamento.setIdentificativoFlusso(rs.getString("cod_identificativo_flusso_r"));
    // "dataOraFlusso",
    try {
      ctFlussoRiversamento.setDataOraFlusso(Utilities.toXMLGregorianCalendar(rs.getDate("dt_data_ora_flusso_r")));
    } catch (Exception ignored) {
    }
    // "identificativoUnivocoRegolamento",
    ctFlussoRiversamento.setIdentificativoUnivocoRegolamento(rs.getString("cod_identificativo_univoco_regolamento_r"));
    // "dataRegolamento",
    try {
      ctFlussoRiversamento.setDataRegolamento(Utilities.toXMLGregorianCalendar(rs.getDate("dt_data_regolamento_r")));
    } catch (Exception ignored) {
    }
    // "istitutoMittente",
    try {
      ctFlussoRiversamento.setIstitutoMittente(extractCtIstitutoMittente(rs));
    } catch (Exception ignored) {
    }
    // "istitutoRicevente",
    try {
      ctFlussoRiversamento.setIstitutoRicevente(extractCtIstitutoRicevente(rs));
    } catch (Exception ignored) {
    }
    // "numeroTotalePagamenti",
    ctFlussoRiversamento.setNumeroTotalePagamenti(rs.getBigDecimal("num_numero_totale_pagamenti_r"));
    // "importoTotalePagamenti",
    ctFlussoRiversamento.setImportoTotalePagamenti(rs.getBigDecimal("num_importo_totale_pagamenti_r"));
    // "datiSingoliPagamenti"
    try {
      ctFlussoRiversamento.setDatiSingoliPagamenti(extractCtDatiSingoliPagamenti(rs));
    } catch (Exception ignored) {
    }
    return ctFlussoRiversamento;
  }

  private CtIstitutoMittente extractCtIstitutoMittente(ResultSet rs) throws SQLException {
    CtIstitutoMittente ctIstitutoMittente = new CtIstitutoMittente();
    it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivoco ctIdentificativoUnivoco = new it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivoco();
    ctIdentificativoUnivoco.setTipoIdentificativoUnivoco(it.gov.digitpa.schemas._2011.pagamenti.StTipoIdentificativoUnivoco
        .fromValue(rs.getString("cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco_r")));
    ctIdentificativoUnivoco.setCodiceIdentificativoUnivoco(rs.getString("cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco_r"));
    ctIstitutoMittente.setIdentificativoUnivocoMittente(ctIdentificativoUnivoco);
    ctIstitutoMittente.setDenominazioneMittente(rs.getString("de_ist_mitt_denominazione_mittente_r"));
    return ctIstitutoMittente;
  }

  private CtIstitutoRicevente extractCtIstitutoRicevente(ResultSet rs) throws SQLException {
    CtIstitutoRicevente ctIstitutoRicevente = new CtIstitutoRicevente();
    it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivocoPersonaG ctIdentificativoUnivocoPersonaG = new it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivocoPersonaG();
    ctIdentificativoUnivocoPersonaG.setTipoIdentificativoUnivoco(it.gov.digitpa.schemas._2011.pagamenti.StTipoIdentificativoUnivocoPersG
        .fromValue(rs.getString("cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco_r")));
    ctIdentificativoUnivocoPersonaG.setCodiceIdentificativoUnivoco(rs.getString("cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco_r"));
    ctIstitutoRicevente.setIdentificativoUnivocoRicevente(ctIdentificativoUnivocoPersonaG);
    ctIstitutoRicevente.setDenominazioneRicevente(rs.getString("de_ist_ricev_denominazione_ricevente_r"));
    return ctIstitutoRicevente;
  }

  private CtDatiSingoliPagamenti extractCtDatiSingoliPagamenti(ResultSet rs) throws SQLException {
    CtDatiSingoliPagamenti ctDatiSingoliPagamenti = new CtDatiSingoliPagamenti();
    // "identificativoUnivocoVersamento",
    ctDatiSingoliPagamenti.setIdentificativoUnivocoVersamento(rs.getString("cod_dati_sing_pagam_identificativo_univoco_versamento_r"));
    // "identificativoUnivocoRiscossione",
    ctDatiSingoliPagamenti.setIdentificativoUnivocoRiscossione(rs.getString("cod_dati_sing_pagam_identificativo_univoco_riscossione_r"));
    // "singoloImportoPagato",
    ctDatiSingoliPagamenti.setSingoloImportoPagato(rs.getBigDecimal("num_dati_sing_pagam_singolo_importo_pagato_r"));
    // "codiceEsitoSingoloPagamento",
    ctDatiSingoliPagamenti.setCodiceEsitoSingoloPagamento(rs.getString("cod_dati_sing_pagam_codice_esito_singolo_pagamento_r"));
    // "dataEsitoSingoloPagamento"
    ctDatiSingoliPagamenti.setDataEsitoSingoloPagamento(Utilities.toXMLGregorianCalendar(rs.getDate("dt_dati_sing_pagam_data_esito_singolo_pagamento_r")));
    // TODO prossimamente da valorizzare con relativo campo
    // ctDatiSingoliPagamenti.setIndiceDatiSingoloPagamento(value);
    return ctDatiSingoliPagamenti;
  }
}
