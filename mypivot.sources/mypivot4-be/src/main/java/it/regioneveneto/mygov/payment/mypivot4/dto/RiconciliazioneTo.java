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
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class RiconciliazioneTo extends BaseTo {

  private String iuvKey;
  private String iudKey;
  private String iufKey;

  private String classificazione;
  private String classificazioneLabel;
  private LocalDateTime dataUltimoAgg;
  private Boolean hasSegnalazione;

  //notifiche
  private String deTipoDovuto;
  private String iuv;
  private String iud;
  private String iur;
  private String importo;
  private LocalDate dataEsecuzione;
  private String pagatoreAnagrafica;
  private String pagatoreCodFisc;
  private String causale;
  private String datiSpecificiRiscossione;

  //ricevuta telematica
  private String attestanteAnagrafica;
  private String attestanteCodFisc;
  private String versanteAnagrafica;
  private String versanteCodFisc;
  private LocalDate dataEsito;

  //rendicontazione
  private String idRendicontazione;
  private String importoTotale;
  private LocalDateTime dataFlusso;
  private String idRegolamento;
  private LocalDate dataRegolamento;

  //sospeso
  private String conto;
  private LocalDate dataValuta;
  private LocalDate dataContabile;
  private String ordinante;
  private Integer annoBolletta;
  private String codiceBolletta;
  private Integer annoDocumento;
  private String codDocumento;
  private Integer annoProvvisorio;
  private String codProvvisorio;
  private String importoTesoreria;
  private String causaleRiversamento;

  //segnalazione attiva
  private String notaSegnalazione;
  private String utenteSegnalazione;
  private String cfUtenteSegnalazione;
  private LocalDateTime dataInserimentoSegnalazione;
}
