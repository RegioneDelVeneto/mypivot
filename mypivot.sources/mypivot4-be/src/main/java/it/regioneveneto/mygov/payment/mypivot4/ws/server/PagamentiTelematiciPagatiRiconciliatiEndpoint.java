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
package it.regioneveneto.mygov.payment.mypivot4.ws.server;


import it.regioneveneto.mygov.payment.mypay4.logging.LogExecution;
import it.regioneveneto.mygov.payment.mypay4.service.common.SystemBlockService;
import it.regioneveneto.mygov.payment.mypay4.ws.server.BaseEndpoint;
import it.regioneveneto.mygov.payment.mypivot4.ws.impl.PagamentiTelematiciPagatiRiconciliatiImpl;
import it.veneto.regione.pagamenti.pivot.ente.*;
import it.veneto.regione.pagamenti.pivot.ente.ppthead.IntestazionePPT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.server.endpoint.annotation.SoapHeader;

@Endpoint
@ConditionalOnWebApplication
public class PagamentiTelematiciPagatiRiconciliatiEndpoint extends BaseEndpoint {
  public static final String NAMESPACE_URI = "http://www.regione.veneto.it/pagamenti/pivot/ente/";
  public static final String NAME = "PagamentiTelematiciPagatiRiconciliati";

  @Autowired
  @Qualifier("PagamentiTelematiciPagatiRiconciliatiImpl")
  private PagamentiTelematiciPagatiRiconciliatiImpl pagamentiTelematiciPagatiRiconciliati;

  @Autowired
  private SystemBlockService systemBlockService;

  @LogExecution
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pivotSILAutorizzaImportFlusso")
  @ResponsePayload
  public PivotSILAutorizzaImportFlussoRisposta pivotSILAutorizzaImportFlusso(
          @RequestPayload PivotSILAutorizzaImportFlusso request,
          @SoapHeader("{http://www.regione.veneto.it/pagamenti/pivot/ente/ppthead}intestazionePPT") SoapHeaderElement header) throws Exception {
    systemBlockService.blockByOperationName("pivotSILAutorizzaImportFlusso");
    return pagamentiTelematiciPagatiRiconciliati.pivotSILAutorizzaImportFlusso(request, unmarshallHeader(header, IntestazionePPT.class));
  }

  @LogExecution
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pivotSILAutorizzaImportFlussoRendicontazione")
  @ResponsePayload
  public PivotSILAutorizzaImportFlussoRendicontazioneRisposta pivotSILAutorizzaImportFlussoRendicontazione(
          @RequestPayload PivotSILAutorizzaImportFlussoRendicontazione request,
          @SoapHeader("{http://www.regione.veneto.it/pagamenti/pivot/ente/ppthead}intestazionePPT") SoapHeaderElement header) throws Exception {
    systemBlockService.blockByOperationName("pivotSILAutorizzaImportFlussoRendicontazione");
    return pagamentiTelematiciPagatiRiconciliati.pivotSILAutorizzaImportFlussoRendicontazione(request, unmarshallHeader(header, IntestazionePPT.class));
  }

  @LogExecution
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pivotSILAutorizzaImportFlussoRT")
  @ResponsePayload
  public PivotSILAutorizzaImportFlussoRTRisposta pivotSILAutorizzaImportFlussoRT(
          @RequestPayload PivotSILAutorizzaImportFlussoRT request,
          @SoapHeader("{http://www.regione.veneto.it/pagamenti/pivot/ente/ppthead}intestazionePPT") SoapHeaderElement header) throws Exception {
    systemBlockService.blockByOperationName("pivotSILAutorizzaImportFlussoRT");
    return pagamentiTelematiciPagatiRiconciliati.pivotSILAutorizzaImportFlussoRT(request, unmarshallHeader(header, IntestazionePPT.class));
  }

  @LogExecution
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pivotSILAutorizzaImportFlussoTesoreria")
  @ResponsePayload
  public PivotSILAutorizzaImportFlussoTesoreriaRisposta pivotSILAutorizzaImportFlussoTesoreria(
          @RequestPayload PivotSILAutorizzaImportFlussoTesoreria request,
          @SoapHeader("{http://www.regione.veneto.it/pagamenti/pivot/ente/ppthead}intestazionePPT") SoapHeaderElement header) throws Exception {
    systemBlockService.blockByOperationName("pivotSILAutorizzaImportFlussoTesoreria");
    return pagamentiTelematiciPagatiRiconciliati.pivotSILAutorizzaImportFlussoTesoreria(request, unmarshallHeader(header, IntestazionePPT.class));
  }

  @LogExecution
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pivotSILChiediAccertamento")
  @ResponsePayload
  public PivotSILChiediAccertamentoRisposta pivotSILChiediAccertamento(
          @RequestPayload PivotSILChiediAccertamento request,
          @SoapHeader("{http://www.regione.veneto.it/pagamenti/pivot/ente/ppthead}intestazionePPT") SoapHeaderElement header) throws Exception {
    systemBlockService.blockByOperationName("pivotSILChiediAccertamento");
    return pagamentiTelematiciPagatiRiconciliati.pivotSILChiediAccertamento(request, unmarshallHeader(header, IntestazionePPT.class));
  }

  @LogExecution
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pivotSILChiediPagatiRiconciliati")
  @ResponsePayload
  public PivotSILChiediPagatiRiconciliatiRisposta pivotSILChiediPagatiRiconciliati(@RequestPayload PivotSILChiediPagatiRiconciliati request) throws Exception{
    systemBlockService.blockByOperationName("pivotSILChiediPagatiRiconciliati");
    return pagamentiTelematiciPagatiRiconciliati.pivotSILChiediPagatiRiconciliati(request);
  }

  @LogExecution
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pivotSILChiediStatoExportFlussoRiconciliazione")
  @ResponsePayload
  public PivotSILChiediStatoExportFlussoRiconciliazioneRisposta pivotSILChiediStatoExportFlussoRiconciliazione(
          @RequestPayload PivotSILChiediStatoExportFlussoRiconciliazione request,
          @SoapHeader("{http://www.regione.veneto.it/pagamenti/pivot/ente/ppthead}intestazionePPT") SoapHeaderElement header) throws Exception {
    systemBlockService.blockByOperationName("pivotSILChiediStatoExportFlussoRiconciliazione");
    return pagamentiTelematiciPagatiRiconciliati.pivotSILChiediStatoExportFlussoRiconciliazione(request, unmarshallHeader(header, IntestazionePPT.class));
  }

  @LogExecution
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pivotSILChiediStatoImportFlusso")
  @ResponsePayload
  public PivotSILChiediStatoImportFlussoRisposta pivotSILChiediStatoImportFlusso(
          @RequestPayload PivotSILChiediStatoImportFlusso request,
          @SoapHeader("{http://www.regione.veneto.it/pagamenti/pivot/ente/ppthead}intestazionePPT") SoapHeaderElement header) throws Exception {
    systemBlockService.blockByOperationName("pivotSILChiediStatoImportFlusso");
    return pagamentiTelematiciPagatiRiconciliati.pivotSILChiediStatoImportFlusso(request, unmarshallHeader(header, IntestazionePPT.class));
  }

  @LogExecution
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pivotSILChiediStatoImportFlussoTesoreria")
  @ResponsePayload
  public PivotSILChiediStatoImportFlussoTesoreriaRisposta pivotSILChiediStatoImportFlussoTesoreria(
          @RequestPayload PivotSILChiediStatoImportFlussoTesoreria request,
          @SoapHeader("{http://www.regione.veneto.it/pagamenti/pivot/ente/ppthead}intestazionePPT") SoapHeaderElement header) throws Exception {
    systemBlockService.blockByOperationName("pivotSILChiediStatoImportFlussoTesoreria");
    return pagamentiTelematiciPagatiRiconciliati.pivotSILChiediStatoImportFlussoTesoreria(request, unmarshallHeader(header, IntestazionePPT.class));
  }

  @LogExecution
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pivotSILPrenotaExportFlussoRiconciliazione")
  @ResponsePayload
  public PivotSILPrenotaExportFlussoRiconciliazioneRisposta pivotSILPrenotaExportFlussoRiconciliazione(
          @RequestPayload PivotSILPrenotaExportFlussoRiconciliazione request,
          @SoapHeader("{http://www.regione.veneto.it/pagamenti/pivot/ente/ppthead}intestazionePPT") SoapHeaderElement header) throws Exception {
    systemBlockService.blockByOperationName("pivotSILPrenotaExportFlussoRiconciliazione");
    return pagamentiTelematiciPagatiRiconciliati.pivotSILPrenotaExportFlussoRiconciliazione(request, unmarshallHeader(header, IntestazionePPT.class));
  }

}
