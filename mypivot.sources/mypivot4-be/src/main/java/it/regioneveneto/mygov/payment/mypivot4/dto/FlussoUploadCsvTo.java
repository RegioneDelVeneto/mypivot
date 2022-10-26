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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.regioneveneto.mygov.payment.mypay4.dto.BaseTo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlussoUploadCsvTo extends BaseTo {

  private String iuf;
  private Integer numRigaFlusso;
  private String codIud;
  private String codIuv;
  private String versioneOggetto;
  private String identificativoDominio;
  private String identificativoStazioneRichiedente;
  private String identificativoMessaggioRicevuta;
  private String dataOraMessaggioRicevuta;
  private String riferimentoMessaggioRichiesta;
  private String riferimentoDataRichiesta;
  private String tipoIdentificativoUnivoco;
  private String codiceIdentificativoUnivoco;
  private String denominazioneAttestante;
  private String codiceUnitOperAttestante;
  private String denomUnitOperAttestante;
  private String indirizzoAttestante;
  private String civicoAttestante;
  private String capAttestante;
  private String localitaAttestante;
  private String provinciaAttestante;
  private String nazioneAttestante;
  private String enteBenefTipoIdentificativoUnivoco;
  private String enteBenefCodiceIdentificativoUnivoco;
  private String denominazioneBeneficiario;
  private String codiceUnitOperBeneficiario;
  private String denomUnitOperBeneficiario;
  private String indirizzoBeneficiario;
  private String civicoBeneficiario;
  private String capBeneficiario;
  private String localitaBeneficiario;
  private String provinciaBeneficiario;
  private String nazioneBeneficiario;
  private String soggVersTipoIdentificativoUnivoco;
  private String soggVersCodiceIdentificativoUnivoco;
  private String anagraficaVersante;
  private String indirizzoVersante;
  private String civicoVersante;
  private String capVersante;
  private String localitaVersante;
  private String provinciaVersante;
  private String nazioneVersante;
  private String emailVersante;
  private String soggPagTipoIdentificativoUnivoco;
  private String soggPagCodiceIdentificativoUnivoco;
  private String anagraficaPagatore;
  private String indirizzoPagatore;
  private String civicoPagatore;
  private String capPagatore;
  private String localitaPagatore;
  private String provinciaPagatore;
  private String nazionePagatore;
  private String emailPagatore;
  private String codiceEsitoPagamento;
  private String importoTotalePagato;
  private String identificativoUnivocoVersamento;
  private String codiceContestoPagamento;
  private String singoloImportoPagato;
  private String esitoSingoloPagamento;
  private String dataEsitoSingoloPagamento;
  private String identificativoUnivocoRiscoss;
  private String causaleVersamento;
  private String datiSpecificiRiscossione;
  private String tipoDovuto;
  private String tipoFirma;
  private String rt;
  private String indiceDatiSingoloPagamento;
  private Integer numRtDatiPagDatiSingPagCommissioniApplicatePsp;
  private String codRtDatiPagDatiSingPagAllegatoRicevutaTipo;
  private String blbRtDatiPagDatiSingPagAllegatoRicevutaTest;
  private String bilancio;
  private Long idIntermediarioPa;
  private String idStazioneIntermediarioPa;
}
