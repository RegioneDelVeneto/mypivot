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
import { Observable } from 'rxjs';

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Ente } from '../model/ente';
import { Flusso } from '../model/flusso';
import { FlussoExport } from '../model/flusso-export';
import { FlussoImport } from '../model/flusso-import';
import { TipoFlusso } from '../model/tipo-flusso';

@Injectable({
  providedIn: 'root'
})
export class FlussoService {

  private baseApiUrl: string;

  constructor(
    private apiInvokerService: ApiInvokerService,
    conf: ConfigurationService
  ) {
    this.baseApiUrl = conf.getProperty('baseApiUrl', environment);
  }

  getFlussiByEnte(ente: Ente): Observable<Flusso[]> {
    return this.apiInvokerService.get<Flusso[]>(this.baseApiUrl + 'flussi/byEnteId/'+ente.mygovEnteId);
  }

  searchFlussiImport(ente: Ente, tipoFlusso: TipoFlusso, nomeFlusso: string, dateFrom: DateTime, dateTo: DateTime): Observable<FlussoImport[]> {
    let params = new HttpParams();
    if(nomeFlusso)
      params = params.append('nomeFlusso', nomeFlusso);
    params = params
      .append('codTipo', tipoFlusso.codTipo)
      .append('from', dateFrom.toFormat('yyyy/MM/dd'))
      .append('to', dateTo.toFormat('yyyy/MM/dd'));
    return this.apiInvokerService.get<FlussoImport[]>
      (this.baseApiUrl + 'flussi/import/'+ente.mygovEnteId, {params:params}, new Mappers({mapperS2C: FlussoImport}) );
  }

  searchFlussiExport(ente: Ente, nomeFlusso: string, dateFrom: DateTime, dateTo: DateTime): Observable<FlussoExport[]> {
    let params = new HttpParams();
    if(nomeFlusso)
      params = params.append('nomeFlusso', nomeFlusso);
    params = params
      .append('from', dateFrom.toFormat('yyyy/MM/dd'))
      .append('to', dateTo.toFormat('yyyy/MM/dd'));
    return this.apiInvokerService.get<FlussoExport[]>
      (this.baseApiUrl + 'flussi/export/'+ente.mygovEnteId, {params: params}, new Mappers({mapperS2C: FlussoExport}));
  }

  uploadFlusso(ente: Ente, formData: FormData): Observable<any> {
    return this.apiInvokerService.post<any>(this.baseApiUrl + 'mybox/upload/'+ente.mygovEnteId, formData);
  }

  downloadFlusso(ente: Ente, type: string, filename: string, securityToken: string): any {
    let params = new HttpParams()
      .append('type', type)
      .append('filename', filename)
      .append('securityToken', securityToken);
    return this.apiInvokerService.get<any>(this.baseApiUrl + 'mybox/download/' + ente.mygovEnteId, {params: params,observe: 'response',responseType: 'blob'});
  }

  removeFlusso(ente: Ente, mygovFlussoId: number): Observable<any> {
    return this.apiInvokerService.get<any>(this.baseApiUrl + 'flussi/remove/' + ente.mygovEnteId + '/' + mygovFlussoId);
  }
}
