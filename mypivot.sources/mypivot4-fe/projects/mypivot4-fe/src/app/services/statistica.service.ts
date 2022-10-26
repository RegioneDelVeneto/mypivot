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

import { AnagraficaUffCapAcc } from '../model/anagrafica-uff-cap-acc';
import { Ente } from '../model/ente';
import { FlussoRicevuta } from '../model/flusso-ricevuta';
import { TipoDovuto } from '../model/tipo-dovuto';
import { VmStatistica } from '../model/vm-statistica';
import { VmStatisticaCapitolo } from '../model/vm-statistica-capitolo';

@Injectable({
  providedIn: 'root'
})
export class StatisticaService {

  private baseApiUrl: string;

  constructor(
    private apiInvokerService: ApiInvokerService,
    conf: ConfigurationService
  ) {
    this.baseApiUrl = conf.getProperty('baseApiUrl', environment);
  }

  getUffici(ente: Ente, tipoDovuto: TipoDovuto) {
    let params = new HttpParams()
        .append('codTipo', tipoDovuto.codTipo)
    return this.apiInvokerService.get<AnagraficaUffCapAcc[]>(this.baseApiUrl + 'statistiche/uffici/'+ente.mygovEnteId, {params: params}, new Mappers({mapperS2C: AnagraficaUffCapAcc}));
  }

  getUfficiAll(ente: Ente) {
    return this.apiInvokerService.get<AnagraficaUffCapAcc[]>(this.baseApiUrl + 'statistiche/uffici/all/'+ente.mygovEnteId, null, new Mappers({mapperS2C: AnagraficaUffCapAcc}));
  }

  getCapitoli(ente: Ente, tipoDovuto: TipoDovuto, codUfficio: string) {
    let params = new HttpParams()
        .append('codTipo', tipoDovuto.codTipo)
        .append('codUfficio', codUfficio)
    return this.apiInvokerService.get<AnagraficaUffCapAcc[]>(this.baseApiUrl + 'statistiche/capitoli/'+ente.mygovEnteId, {params: params}, new Mappers({mapperS2C: AnagraficaUffCapAcc}));
  }

  getStatisticheUffici(ente: Ente, anno: string, mese: string, giorno: string) {
    let params = new HttpParams();
    if(anno)
      params = params.append('anno', anno);
    if(mese)
      params = params.append('mese', mese);
    if(giorno)
      params = params.append('giorno', giorno);
    return this.apiInvokerService.get<VmStatistica[]>(this.baseApiUrl + 'statistiche/ufficio/'+ente.mygovEnteId, {params: params}, new Mappers({mapperS2C: VmStatistica}));
  }

  getStatisticheTipiDovuto(ente: Ente, anno: string, mese: string, giorno: string) {
    let params = new HttpParams();
    if(anno)
      params = params.append('anno', anno);
    if(mese)
      params = params.append('mese', mese);
    if(giorno)
      params = params.append('giorno', giorno);
    return this.apiInvokerService.get<VmStatistica[]>(this.baseApiUrl + 'statistiche/tipoDovuto/'+ente.mygovEnteId, {params: params}, new Mappers({mapperS2C: VmStatistica}));
  }

  getStatisticheAccertamenti(ente: Ente, anno: string, mese: string, giorno: string, codTipo: string, codUfficio: string, codCapitolo: string) {
    let params = new HttpParams()
        .append('codTipo', codTipo)
        .append('codUfficio', codUfficio)
        .append('codCapitolo', codCapitolo);
    if(anno)
      params = params.append('anno', anno);
    if(mese)
      params = params.append('mese', mese);
    if(giorno)
      params = params.append('giorno', giorno);
    return this.apiInvokerService.get<VmStatistica[]>(this.baseApiUrl + 'statistiche/accertamento/'+ente.mygovEnteId, {params: params}, new Mappers({mapperS2C: VmStatistica}));
  }

  getStatisticheCapitoli(ente: Ente, anno: string, mese: string, giorno: string, codTipo: string, codUfficio: string) {
    let params = new HttpParams();
    if(codTipo)
      params = params.append('codTipo', codTipo)
    if(codUfficio)
      params = params.append('codUfficio', codUfficio)
    if(anno)
      params = params.append('anno', anno);
    if(mese)
      params = params.append('mese', mese);
    if(giorno)
      params = params.append('giorno', giorno);
    return this.apiInvokerService.get<VmStatisticaCapitolo[]>(this.baseApiUrl + 'statistiche/capitolo/'+ente.mygovEnteId, {params: params});
  }

  getStatisticheAnno(ente: Ente, anni: string[]) {
    let params = new HttpParams();
    anni.forEach(anno => { params = params.append('anni', anno); });
    return this.apiInvokerService.get<VmStatisticaCapitolo[]>(this.baseApiUrl + 'statistiche/anno/'+ente.mygovEnteId, {params: params});
  }

  getStatisticheMese(ente: Ente, anno: string, mesi: string[]) {
    let params = new HttpParams().append('anno', anno);
    mesi.forEach(mese => { params = params.append('mesi', mese); });
    return this.apiInvokerService.get<VmStatisticaCapitolo[]>(this.baseApiUrl + 'statistiche/mese/'+ente.mygovEnteId, {params: params});
  }

  getStatisticheGiorno(ente: Ente, dateFrom: DateTime, dateTo: DateTime) {
    let params = new HttpParams()
        .append('from', dateFrom.toFormat('yyyy/MM/dd'))
        .append('to', dateTo.toFormat('yyyy/MM/dd'));
    return this.apiInvokerService.get<VmStatisticaCapitolo[]>(this.baseApiUrl + 'statistiche/giorno/'+ente.mygovEnteId, {params: params});
  }

  getRicevuteTelematiche(ente: Ente, codTipo: string, codUfficio: string, codCapitolo: string, dateFrom: DateTime, dateTo: DateTime,
      iuv: string, iur: string, attestante: string, cfPagatore: string, anagPagatore: string, cfVersante: string, anagVersante) {
    let params = new HttpParams()
        .append('codTipo', codTipo)
        .append('codUfficio', codUfficio)
        .append('codCapitolo', codCapitolo);
    if (dateFrom)
      params = params.append('from', dateFrom.toFormat('yyyy/MM/dd'));
    if (dateTo)
      params = params.append('to', dateTo.toFormat('yyyy/MM/dd'));
    if (iuv)
      params = params.append('iuv', iuv);
    if (iur)
      params = params.append('iur', iur);
    if (attestante)
      params = params.append('attestante', attestante);
    if (cfPagatore)
      params = params.append('cfPagatore', cfPagatore);
    if (anagPagatore)
      params = params.append('anagPagatore', anagPagatore);
    if (cfVersante)
      params = params.append('cfVersante', cfVersante);
    if (anagVersante)
      params = params.append('anagVersante', anagVersante);
    return this.apiInvokerService.get<FlussoRicevuta[]>(this.baseApiUrl + 'statistiche/dettaglio/'+ente.mygovEnteId, {params: params}, new Mappers({mapperS2C: FlussoRicevuta}) );
  }
}
