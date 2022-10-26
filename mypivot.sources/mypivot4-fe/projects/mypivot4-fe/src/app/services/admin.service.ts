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
import {
    ApiInvokerService, ConfigurationService, environment
} from 'projects/mypay4-fe-common/src/public-api';
import { Observable } from 'rxjs';

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Ente } from '../model/ente';
import { Operatore } from '../model/operatore';
import { TipoDovuto } from '../model/tipo-dovuto';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private baseApiUrl: string;

  constructor(
    private apiInvokerService: ApiInvokerService,
    conf: ConfigurationService,
  ) { 
    this.baseApiUrl = conf.getProperty('baseApiUrl', environment);
  }

  searchEnti(codIpaEnte: string, deNome: string, codFiscale: string): Observable<Ente[]> {
    let params = new HttpParams();
    if (codIpaEnte)
      params = params.append('codIpaEnte', codIpaEnte);
    if (deNome)
      params = params.append('deNome', deNome);
    if (codFiscale)
      params = params.append('codFiscale', codFiscale);
    return this.apiInvokerService.get<Ente[]>(this.baseApiUrl + 'admin/enti', {params: params});
  }

  getEnteById(enteId: number): Observable<Ente> {
    return this.apiInvokerService.get<Ente>(this.baseApiUrl + 'admin/enti/' + enteId);
  }

  getListTipoDovutoEsterniByEnte(ente: Ente): Observable<TipoDovuto[]> {
    return this.apiInvokerService.get<TipoDovuto[]>(this.baseApiUrl + 'admin/enti/' + ente.mygovEnteId + '/tipiDovutoEsterni');
  }

  getTipoDovutoById(enteTipoDovutoId: number): Observable<TipoDovuto> {
    return this.apiInvokerService.get<TipoDovuto>(this.baseApiUrl + 'admin/tipiDovuto/' + enteTipoDovutoId);
  }

  getListOperatoriByTipoDovuto(tipoDovuto: TipoDovuto): Observable<Operatore[]> {
    return this.apiInvokerService.get<Operatore[]>(this.baseApiUrl + 'admin/tipiDovuto/' + tipoDovuto.mygovEnteTipoDovutoId + '/operatori');
  }

  swtichStateOperatoreTipoDovuto(tipoDovuto: TipoDovuto, operatore: Operatore, newState: boolean): Observable<void> {
    return this.apiInvokerService.post<void>(this.baseApiUrl + 'admin/tipiDovuto/' + tipoDovuto.mygovEnteTipoDovutoId + '/operatori/' + operatore.operatoreId + '/enabled/' + newState, null);
  }

  insertTipoDovuto(ente: Ente, tipoDovuto: TipoDovuto): Observable<void> {
    return this.apiInvokerService.post<void>(this.baseApiUrl + 'admin/enti/' + ente.mygovEnteId + '/tipiDovutoEsterni', tipoDovuto);
  }

  updateTipoDovuto(tipoDovuto: TipoDovuto): Observable<void> {
    return this.apiInvokerService.post<void>(this.baseApiUrl + 'admin/tipiDovuto/' + tipoDovuto.mygovEnteTipoDovutoId, tipoDovuto);
  }

  deleteTipoDovuto(tipoDovuto: TipoDovuto): Observable<void> {
    return this.apiInvokerService.delete<void>(this.baseApiUrl + 'admin/tipiDovuto/' + tipoDovuto.mygovEnteTipoDovutoId, null);
  }
}
