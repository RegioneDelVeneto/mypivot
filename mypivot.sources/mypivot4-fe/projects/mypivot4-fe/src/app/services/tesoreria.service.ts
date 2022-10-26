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
import { Bilancio } from './../model/bilancio';
import { HttpParams } from '@angular/common/http';
import { Ente } from './../model/ente';
import { Injectable } from '@angular/core';
import {
    ApiInvokerService, ConfigurationService, environment, Mappers
} from 'projects/mypay4-fe-common/src/public-api';
import { Tesoreria } from '../model/tesoreria';
import { DateTime } from 'luxon';

@Injectable({
  providedIn: 'root'
})
export class TesoreriaService {

  private baseApiUrl: string;

  constructor(
    private apiInvokerService: ApiInvokerService,
    conf: ConfigurationService
  ) {
    this.baseApiUrl = conf.getProperty('baseApiUrl', environment);
  }

  search(ente: Ente, iuv: string, annoBolletta: string, codBolletta: string, idr: string, importo: number,
      annoDocumento: string, codDocumento: string, annoProvvisorio: string, codProvvisorio: string, ordinante: string,
      dtContabileFrom: DateTime, dtContabileTo: DateTime, dtValutaFrom: DateTime, dtValutaTo: DateTime) {
    let params = new HttpParams();
    if (iuv)
      params = params.append('iuv', iuv);
    if (annoBolletta)
      params = params.append('annoBolletta', annoBolletta);
    if (codBolletta)
      params = params.append('codBolletta', codBolletta);
    if (idr)
      params = params.append('idr', idr);
    if (importo)
      params = params.append('importo', importo.toString().replace(',','.'));
    if (annoDocumento)
      params = params.append('annoDocumento', annoDocumento);
    if (codDocumento)
      params = params.append('codDocumento', codDocumento);
    if (annoProvvisorio)
      params = params.append('annoProvvisorio', annoProvvisorio);
    if (codProvvisorio)
      params = params.append('codProvvisorio', codProvvisorio);
    if (dtContabileFrom)
      params = params.append('dtContabileFrom', dtContabileFrom.toFormat('yyyy/MM/dd'));
    if (dtContabileTo)
      params = params.append('dtContabileTo', dtContabileTo.toFormat('yyyy/MM/dd'));
    if (dtValutaFrom)
      params = params.append('dtValutaFrom', dtValutaFrom.toFormat('yyyy/MM/dd'));
    if (dtValutaTo)
      params = params.append('dtValutaTo', dtValutaTo.toFormat('yyyy/MM/dd'));
    return this.apiInvokerService.get<Tesoreria[]>(this.baseApiUrl + 'tesoreria/search/'+ente.mygovEnteId, {params: params});
  }

  getDettaglio(ente: Ente, tesoreriaId: number) {
    return this.apiInvokerService.get<Tesoreria>(this.baseApiUrl + 'tesoreria/dettaglio/'+ente.mygovEnteId+'/'+tesoreriaId);
  }

  getBilanci(ente: Ente, annoBolletta: string, codBolletta: string) {
    let params = new HttpParams();
    if (annoBolletta)
      params = params.append('annoBolletta', annoBolletta);
    if (codBolletta)
      params = params.append('codBolletta', codBolletta);
    return this.apiInvokerService.get<Bilancio[]>(this.baseApiUrl + 'tesoreria/dettaglio/bilanci/'+ente.mygovEnteId, {params: params});
  }
}
