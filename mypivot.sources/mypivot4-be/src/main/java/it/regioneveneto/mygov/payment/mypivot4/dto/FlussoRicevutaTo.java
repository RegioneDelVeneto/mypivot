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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlussoRicevutaTo extends BaseTo {

  private String codiceIpaEnte;
  private String codIud; //IUD
  private String codRpSilinviarpIdUnivocoVersamento; //IUV
  private String codEDatiPagDatiSingPagIdUnivocoRiscoss; //IUR
  //private BigDecimal numEDatiPagImportoTotalePagato; //Importo totale
  private BigDecimal numEDatiPagDatiSingPagSingoloImportoPagato; //Importo
  private LocalDate dtEDatiPagDatiSingPagDataEsitoSingoloPagamento; //Data esito
  private String deEIstitAttDenominazioneAttestante; //Attestante
  private String codESoggPagAnagraficaPagatore; //Anagrafica pagatore
  private String codESoggPagIdUnivPagCodiceIdUnivoco; //CF pagatore
  private String codESoggPagIdUnivPagTipoIdUnivoco; //F('Persona Fisica') or G('Persona Giuridica')
  private String deEDatiPagDatiSingPagCausaleVersamento; //Causale
  private String codESoggVersAnagraficaVersante; //Anagrafica versante
  private String codESoggVersIdUnivVersCodiceIdUnivoco; //CF versante
  private String codESoggVersIdUnivVersTipoIdUnivoco; //F('Persona Fisica') or G('Persona Giuridica')
  private String deTipoDovuto;

  private String codFiscalePa1;
  private String deStato; //info from mypay
  private int indiceDatiSingoloPagamento;
}
