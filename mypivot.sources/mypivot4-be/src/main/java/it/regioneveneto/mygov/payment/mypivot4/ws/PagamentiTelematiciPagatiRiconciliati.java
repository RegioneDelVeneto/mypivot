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
package it.regioneveneto.mygov.payment.mypivot4.ws;

import it.veneto.regione.pagamenti.pivot.ente.PivotSILAutorizzaImportFlusso;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILAutorizzaImportFlussoRT;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILAutorizzaImportFlussoRTRisposta;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILAutorizzaImportFlussoRendicontazione;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILAutorizzaImportFlussoRendicontazioneRisposta;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILAutorizzaImportFlussoRisposta;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILAutorizzaImportFlussoTesoreria;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILAutorizzaImportFlussoTesoreriaRisposta;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILChiediAccertamento;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILChiediAccertamentoRisposta;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILChiediPagatiRiconciliati;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILChiediPagatiRiconciliatiRisposta;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILChiediStatoExportFlussoRiconciliazione;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILChiediStatoExportFlussoRiconciliazioneRisposta;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILChiediStatoImportFlusso;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILChiediStatoImportFlussoRisposta;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILChiediStatoImportFlussoTesoreria;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILChiediStatoImportFlussoTesoreriaRisposta;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILPrenotaExportFlussoRiconciliazione;
import it.veneto.regione.pagamenti.pivot.ente.PivotSILPrenotaExportFlussoRiconciliazioneRisposta;
import it.veneto.regione.pagamenti.pivot.ente.ppthead.IntestazionePPT;

public interface PagamentiTelematiciPagatiRiconciliati {
  PivotSILAutorizzaImportFlussoRisposta pivotSILAutorizzaImportFlusso(PivotSILAutorizzaImportFlusso bodyrichiesta, IntestazionePPT header) throws Exception;
  PivotSILAutorizzaImportFlussoRendicontazioneRisposta pivotSILAutorizzaImportFlussoRendicontazione(PivotSILAutorizzaImportFlussoRendicontazione bodyrichiesta, IntestazionePPT header) throws Exception;
  PivotSILAutorizzaImportFlussoRTRisposta pivotSILAutorizzaImportFlussoRT(PivotSILAutorizzaImportFlussoRT bodyrichiesta, IntestazionePPT header) throws Exception;
  PivotSILAutorizzaImportFlussoTesoreriaRisposta pivotSILAutorizzaImportFlussoTesoreria(PivotSILAutorizzaImportFlussoTesoreria bodyrichiesta, IntestazionePPT header) throws Exception;
  PivotSILChiediAccertamentoRisposta pivotSILChiediAccertamento(PivotSILChiediAccertamento bodyrichiesta, IntestazionePPT header) throws Exception;
  PivotSILChiediPagatiRiconciliatiRisposta pivotSILChiediPagatiRiconciliati(final PivotSILChiediPagatiRiconciliati bodyrichiesta) throws Exception;
  PivotSILChiediStatoExportFlussoRiconciliazioneRisposta pivotSILChiediStatoExportFlussoRiconciliazione(PivotSILChiediStatoExportFlussoRiconciliazione bodyrichiesta, IntestazionePPT header) throws Exception;
  PivotSILChiediStatoImportFlussoRisposta pivotSILChiediStatoImportFlusso(PivotSILChiediStatoImportFlusso bodyrichiesta, IntestazionePPT header) throws Exception;
  PivotSILChiediStatoImportFlussoTesoreriaRisposta pivotSILChiediStatoImportFlussoTesoreria(PivotSILChiediStatoImportFlussoTesoreria bodyrichiesta, IntestazionePPT header) throws Exception;
  PivotSILPrenotaExportFlussoRiconciliazioneRisposta pivotSILPrenotaExportFlussoRiconciliazione(PivotSILPrenotaExportFlussoRiconciliazione bodyrichiesta, IntestazionePPT header) throws Exception;
}
