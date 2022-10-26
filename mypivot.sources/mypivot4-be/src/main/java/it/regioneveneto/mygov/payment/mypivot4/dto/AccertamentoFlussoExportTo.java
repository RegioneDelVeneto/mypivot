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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.regioneveneto.mygov.payment.mypay4.dto.BaseTo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccertamentoFlussoExportTo extends BaseTo {

  private String codTipoDovuto;
  private String deTipoDovuto;
  private String codiceIud;
  private String codiceIuv;
  private String identificativoUnivocoRiscossione;
  private String denominazioneAttestante;
  private String codiceIdentificativoUnivocoAttestante;
  private String tipoIdentificativoUnivocoAttestante;
  private String anagraficaVersante;
  private String codiceIdentificativoUnivocoVersante;
  private String tipoIdentificativoUnivocoVersante;
  private String anagraficaPagatore;
  private String codiceIdentificativoUnivocoPagatore;
  private String tipoIdentificativoUnivocoPagatore;
  @JsonFormat(pattern="yyyy/MM/dd-HH:mm:ss")
  private Date dtUltimoAggiornamento;
  @JsonFormat(pattern="yyyy/MM/dd-HH:mm:ss")
  private Date dtEsitoSingoloPagamento;
  private String causaleVersamento;
  private BigDecimal singoloImportoPagato;
}
