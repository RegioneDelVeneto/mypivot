/*
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
import { DateTime } from 'luxon';
import {
    ApiInvokerService, ConfigurationService, environment, Mappers
} from 'projects/mypay4-fe-common/src/public-api';

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Accertamento } from '../model/accertamento';
import { AccertamentoCapitolo } from '../model/accertamento-capitolo';
import { AccertamentoFlussoExport } from '../model/accertamento-flusso-export';
import { AnagraficaStato } from '../model/anagrafica-stato';
import { AnagraficaUffCapAcc } from '../model/anagrafica-uff-cap-acc';
import { CapitoloRT } from '../model/capitolo-rt';
import { Ente } from '../model/ente';
import { FlussoRicevuta } from '../model/flusso-ricevuta';
import { TipoDovuto } from '../model/tipo-dovuto';

@Injectable({
  providedIn: 'root'
})
export class AccertamentoService {

  private baseApiUrl: string;

  constructor(
    private apiInvokerService: ApiInvokerService,
    conf: ConfigurationService
  ) {
    this.baseApiUrl = conf.getProperty('baseApiUrl', environment);
  }

  getStati() {
    return this.apiInvokerService.get<AnagraficaStato[]>(this.baseApiUrl + 'accertamenti/stati');
  }

  getComboUffici(ente: Ente, codTipo: string) {
    let params = new HttpParams().append('codTipo', codTipo);
    return this.apiInvokerService.get<AnagraficaUffCapAcc[]>(this.baseApiUrl + 'accertamenti/uffici/'+ente.mygovEnteId, {params: params});
  }

  getComboCapitoli(ente: Ente, codTipo: string, codUfficio: string, annoEsercizio: string) {
    let params = new HttpParams().append('codTipo', codTipo)
        .append('annoEsercizio', annoEsercizio)
        .append('codUfficio', codUfficio)
    return this.apiInvokerService.get<AnagraficaUffCapAcc[]>(this.baseApiUrl + 'accertamenti/capitoli/'+ente.mygovEnteId, {params: params});
  }

  getComboAccertamenti(ente: Ente, codTipo: string, codUfficio: string, annoEsercizio: string, codCapitolo: string) {
    let params = new HttpParams().append('codTipo', codTipo)
        .append('annoEsercizio', annoEsercizio)
        .append('codUfficio', codUfficio)
        .append('codCapitolo', codCapitolo)
    return this.apiInvokerService.get<AnagraficaUffCapAcc[]>(this.baseApiUrl + 'accertamenti/accertamenti/'+ente.mygovEnteId, {params: params});
  }

  getAccertamentiCapitoli(ente: Ente, tipoDovuto: TipoDovuto, codUfficio: string, deUfficio: string, flgUfficioAttivo: boolean,
      codCapitolo: string, deCapitolo: string, annoCapitolo: string, codAccertamento: string, deAccertamento) {
    let params = new HttpParams();
    if (tipoDovuto)
      params = params.append('codTipo', tipoDovuto.codTipo);
    if (codUfficio)
      params = params.append('codUfficio', codUfficio);
    if (flgUfficioAttivo)
      params = params.append('flgUfficioAttivo', String(flgUfficioAttivo))
    if (deUfficio)
      params = params.append('deUfficio', deUfficio);
    if (codCapitolo)
      params = params.append('codCapitolo', codCapitolo);
    if (deCapitolo)
      params = params.append('deCapitolo', deCapitolo);
    if (annoCapitolo)
      params = params.append('annoCapitolo', annoCapitolo);
    if (codAccertamento)
      params = params.append('codAccertamento', codAccertamento);
    if (deAccertamento)
      params = params.append('deAccertamento', deAccertamento);

    return this.apiInvokerService.get<AccertamentoCapitolo[]>(this.baseApiUrl + 'accertamenti/capitolo/'+ente.mygovEnteId, {params: params}, new Mappers({mapperS2C: AccertamentoCapitolo}));
  }

  getAccertamentoCapitolo(ente: Ente, anagraficaId: number) {
    return this.apiInvokerService.get<AccertamentoCapitolo>(this.baseApiUrl + 'accertamenti/capitolo/'+ente.mygovEnteId+'/'+anagraficaId);
  }

  putAccertamentoCapitolo(ente: Ente, accertamentoCapitolo: AccertamentoCapitolo) {
    return this.apiInvokerService.put<AccertamentoCapitolo>(this.baseApiUrl + 'accertamenti/capitolo/'+ente.mygovEnteId, accertamentoCapitolo);
  }

  getAccertamenti(ente: Ente, tipoDovuto: TipoDovuto, dateFrom: DateTime, dateTo: DateTime, iuv: string, codStato: string, deNomeAccertamento: string) {
    let params = new HttpParams();
    if (tipoDovuto)
      params = params.append('codTipo', tipoDovuto.codTipo);
    if (dateFrom)
      params = params.append('from', dateFrom.toFormat('yyyy/MM/dd'));
    if (dateTo)
      params = params.append('to', dateTo.toFormat('yyyy/MM/dd'));
    if (iuv)
      params = params.append('iuv', iuv)
    if (codStato)
      params = params.append('codStato', codStato);
    if (deNomeAccertamento)
      params = params.append('deNomeAccertamento', deNomeAccertamento)
    return this.apiInvokerService.get<Accertamento[]>(this.baseApiUrl + 'accertamenti/list/'+ente.mygovEnteId, {params: params});
  }

  getAccertamento(ente: Ente, accertamentoId: number) {
    return this.apiInvokerService.get<Accertamento>(this.baseApiUrl + 'accertamenti/list/'+ente.mygovEnteId+'/'+accertamentoId+'/anagrafica');
  }

  closeAccertamento(ente: Ente, accertamentoId: number) {
    return this.apiInvokerService.put<Accertamento>(this.baseApiUrl + 'accertamenti/list/'+ente.mygovEnteId+'/'+accertamentoId+'/close', null);
  }

  cancelAccertamento(ente: Ente, accertamentoId: number) {
    return this.apiInvokerService.put<Accertamento>(this.baseApiUrl + 'accertamenti/list/'+ente.mygovEnteId+'/'+accertamentoId+'/cancel', null);
  }

  getAccertamentiPagamentiInseribili(ente: Ente, accertamenoId: number, dtEsitoFrom: DateTime, dtEsitoTo: DateTime, dtUltimoAggFrom: DateTime,
      dtUltimoAggTo: DateTime, codIud: string, codIuv: string, cfPagatore: string) {
    let params = new HttpParams();
    if (dtEsitoFrom)
      params = params.append('dtEsitoFrom', dtEsitoFrom.toFormat('yyyy/MM/dd'));
    if (dtEsitoTo)
      params = params.append('dtEsitoTo', dtEsitoTo.toFormat('yyyy/MM/dd'));
    if (dtUltimoAggFrom)
      params = params.append('dtUltimoAggFrom', dtUltimoAggFrom.toFormat('yyyy/MM/dd'));
    if (dtUltimoAggTo)
      params = params.append('dtUltimoAggTo', dtUltimoAggTo.toFormat('yyyy/MM/dd'));
    if (codIud)
      params = params.append('codIud', codIud)
    if (codIuv)
      params = params.append('codIuv', codIuv);
    if (cfPagatore)
      params = params.append('cfPagatore', cfPagatore);
    return this.apiInvokerService.get<AccertamentoFlussoExport[]>(this.baseApiUrl + 'accertamenti/list/'+ente.mygovEnteId+'/'+accertamenoId+'/toAddRT', {params: params});
  }

  getAccertamentoDettaglio(ente: Ente, accertamenoId: number, dtEsitoFrom: DateTime, dtEsitoTo: DateTime, dtUltimoAggFrom: DateTime,
      dtUltimoAggTo: DateTime, codIud: string, codIuv: string, cfPagatore: string) {
    let params = new HttpParams();
    if (dtEsitoFrom)
      params = params.append('dtEsitoFrom', dtEsitoFrom.toFormat('yyyy/MM/dd'));
    if (dtEsitoTo)
      params = params.append('dtEsitoTo', dtEsitoTo.toFormat('yyyy/MM/dd'));
    if (dtUltimoAggFrom)
      params = params.append('dtUltimoAggFrom', dtUltimoAggFrom.toFormat('yyyy/MM/dd'));
    if (dtUltimoAggTo)
      params = params.append('dtUltimoAggTo', dtUltimoAggTo.toFormat('yyyy/MM/dd'));
    if (codIud)
      params = params.append('codIud', codIud)
    if (codIuv)
      params = params.append('codIuv', codIuv);
    if (cfPagatore)
      params = params.append('cfPagatore', cfPagatore);
    return this.apiInvokerService.get<AccertamentoFlussoExport[]>(this.baseApiUrl + 'accertamenti/list/'+ente.mygovEnteId+'/'+accertamenoId+'/dettaglioRT', {params: params});
  }

  deleteAccertamentoDettaglio(ente: Ente, accertamenoId: number, accertamenti: AccertamentoFlussoExport[]) {
    let params = new HttpParams()
        .append('json', JSON.stringify(accertamenti))
    return this.apiInvokerService.delete<number>(this.baseApiUrl + 'accertamenti/list/'+ente.mygovEnteId+'/'+accertamenoId+'/dettaglioRT', { params: params});
  }

  insertAccertamentoDettaglio(ente: Ente, accertamenoId: number, accertamenti: AccertamentoFlussoExport[],
      codUfficio: string, annoEsercizio: string, codCapitolo: string, codAccertamento: string) {
    let params = new HttpParams()
        .append('codUfficio', codUfficio)
        .append('annoEsercizio', annoEsercizio)
        .append('codCapitolo', codCapitolo)
        .append('codAccertamento', codAccertamento)
        .append('json', JSON.stringify(accertamenti))
    return this.apiInvokerService.put<number>(this.baseApiUrl + 'accertamenti/list/'+ente.mygovEnteId+'/'+accertamenoId+'/dettaglioRT', null, {params: params});
  }

  getCapitoliByRT(ente: Ente, accertamenoId: number, codTipo: string, codIud: string, codIuv: string) {
    let params = new HttpParams()
      .append('codTipo', codTipo)
      .append('codIud', codIud)
      .append('codIuv', codIuv);
    return this.apiInvokerService.get<CapitoloRT[]>(this.baseApiUrl + 'accertamenti/list/'+ente.mygovEnteId+'/'+accertamenoId+'/capitoli', {params: params});
  }

  getRicevuteTelematiche(ente: Ente, codTipo: string, codIud: string) {
    let params = new HttpParams()
        .append('codTipo', codTipo)
        .append('codIud', codIud);
    return this.apiInvokerService.get<FlussoRicevuta[]>(this.baseApiUrl + 'accertamenti/ricevuteTelematiche/'+ente.mygovEnteId, {params: params}, new Mappers({mapperS2C: FlussoRicevuta}) );
  }

  insert(ente: Ente, tipoDovuto: TipoDovuto, nomeAccertamento: string) {
    let accertamento = new Accertamento();
    accertamento.codTipoDovuto = tipoDovuto.codTipo;
    accertamento.deNomeAccertamento = nomeAccertamento;
    return this.apiInvokerService.post<Accertamento>(this.baseApiUrl + 'accertamenti/insert/'+ente.mygovEnteId, accertamento);
  }

  update(ente: Ente, accertamentoId: number, tipoDovuto: TipoDovuto, nomeAccertamento: string) {
    let accertamento = new Accertamento();
    accertamento.id = accertamentoId;
    accertamento.codTipoDovuto = tipoDovuto.codTipo;
    accertamento.deNomeAccertamento = nomeAccertamento;
    return this.apiInvokerService.put<Accertamento>(this.baseApiUrl + 'accertamenti/update/'+ente.mygovEnteId, accertamento);
  }

  uploadImportMassivo(ente: Ente, formData: FormData) {
    return this.apiInvokerService.post<any>(this.baseApiUrl + 'accertamenti/importMassivo/'+ente.mygovEnteId, formData);
  }

}
