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
package it.regioneveneto.mygov.payment.mypivot4.dto;

import it.regioneveneto.mygov.payment.mypay4.dto.BaseTo;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.model.RiconciliazioneSearch;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

@Data
public class RiconciliazioneSearchTo extends BaseTo {
  private LocalDate dateEsitoFrom;
  private LocalDate dateEsitoTo;
  private LocalDate dateUltModFrom;
  private LocalDate dateUltModTo;
  private LocalDate dateRegolFrom;
  private LocalDate dateRegolTo;
  private LocalDate dateContabFrom;
  private LocalDate dateContabTo;
  private LocalDate dateValutaFrom;
  private LocalDate dateValutaTo;
  private String iud;
  private String iuv;
  private String iur;
  private String codFiscalePagatore;
  private String anagPagatore;
  private String codFiscaleVersante;
  private String anagVersante;
  private String attestante;
  private String ordinante;
  private String idRendicont;
  private String idRegolamento;
  private String tipoDovuto;
  private String conto;
  private String importoTesoreria;
  private String causale;
  private Integer annoBolletta;
  private String codBolletta;
  private Integer annoDocumento;
  private String codDocumento;
  private Integer annoProvvisorio;
  private String codProvvisorio;

  public RiconciliazioneSearch toEntity(String codFedUserId, String codIpaEnte, String searchType){
    return RiconciliazioneSearch.builder()
        .cod_fed_user_id(codFedUserId)
        .codice_ipa_ente(codIpaEnte)
        .cod_iud(StringUtils.stripToNull(iud))
        .cod_iuv(StringUtils.stripToNull(iuv))
        .denominazione_attestante(StringUtils.stripToNull(attestante))
        .identificativo_univoco_riscossione(StringUtils.stripToNull(iur))
        .codice_identificativo_univoco_versante(StringUtils.stripToNull(codFiscaleVersante))
        .anagrafica_versante(StringUtils.stripToNull(anagVersante))
        .codice_identificativo_univoco_pagatore(StringUtils.stripToNull(codFiscalePagatore))
        .anagrafica_pagatore(StringUtils.stripToNull(anagPagatore))
        .causale_versamento(StringUtils.stripToNull(causale))
        .data_esecuzione_singolo_pagamento_da(null)
        .data_esecuzione_singolo_pagamento_a(null)
        .data_esito_singolo_pagamento_da(Utilities.toSqlDate(dateEsitoFrom))
        .data_esito_singolo_pagamento_a(Utilities.toSqlDate(dateEsitoTo))
        .identificativo_flusso_rendicontazione(StringUtils.stripToNull(idRendicont))
        .identificativo_univoco_regolamento(StringUtils.stripToNull(idRegolamento))
        .data_regolamento_da(Utilities.toSqlDate(dateRegolFrom))
        .data_regolamento_a(Utilities.toSqlDate(dateRegolTo))
        .dt_data_contabile_da(Utilities.toSqlDate(dateContabFrom))
        .dt_data_contabile_a(Utilities.toSqlDate(dateContabTo))
        .dt_data_valuta_da(Utilities.toSqlDate(dateValutaFrom))
        .dt_data_valuta_a(Utilities.toSqlDate(dateValutaTo))
        .dt_data_ultimo_aggiornamento_da(Utilities.toSqlDate(dateUltModFrom))
        .dt_data_ultimo_aggiornamento_a(Utilities.toSqlDate(dateUltModTo))
        .cod_tipo_dovuto(tipoDovuto)
        .codtipodovutopresent(true)
        .importo(StringUtils.stripToNull(importoTesoreria))
        .conto(StringUtils.stripToNull(conto))
        .cod_or1(StringUtils.stripToNull(ordinante))
        .cod_bolletta(StringUtils.stripToNull(codBolletta))
        .de_anno_bolletta(annoBolletta!=null?annoBolletta.toString():null)
        .cod_documento(StringUtils.stripToNull(codDocumento))
        .de_anno_documento(annoDocumento!=null?annoDocumento.toString():null)
        .cod_provvisorio(StringUtils.stripToNull(codProvvisorio))
        .de_anno_provvisorio(annoProvvisorio!=null?annoProvvisorio.toString():null)
        .classificazione_completezza(searchType)
        .build();
  }
}
