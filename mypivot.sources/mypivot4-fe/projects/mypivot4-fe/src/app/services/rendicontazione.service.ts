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
    KeyValue
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table-detail/my-pay-table-detail.component';
import {
    ApiInvokerService, ConfigurationService, environment, Mappers
} from 'projects/mypay4-fe-common/src/public-api';
import { Observable } from 'rxjs';

import { CurrencyPipe } from '@angular/common';
import { Injectable } from '@angular/core';

import { Ente } from '../model/ente';
import { Rendicontazione } from '../model/rendicontazione';
import { RendicontazioneDetail } from '../model/rendicontazione-detail';
import { RicevutaSearch } from '../model/ricevuta-search';

@Injectable({
  providedIn: 'root'
})
export class RendicontazioneService {

  private baseApiUrl: string;

  constructor(
    private apiInvokerService: ApiInvokerService,
    private currencyPipe: CurrencyPipe, 
    conf: ConfigurationService
  ) {
    this.baseApiUrl = conf.getProperty('baseApiUrl', environment);
  }

  get(ente: Ente, iuf: string): Observable<Rendicontazione> {
    const targetUrl = `${this.baseApiUrl}rendicontazione/get/${ente.mygovEnteId}/${iuf}`;
    return this.apiInvokerService.get<Rendicontazione>(
      targetUrl, null, new Mappers({mapperS2C: Rendicontazione, mapperC2S: Rendicontazione})
    );
  }

  search(ente: Ente, searchParams: Rendicontazione): Observable<Rendicontazione[]> {
    const targetUrl = `${this.baseApiUrl}rendicontazione/search/${ente.mygovEnteId}`;
    return this.apiInvokerService.post<Rendicontazione[]>(
      targetUrl, searchParams, null, new Mappers({mapperS2C: Rendicontazione, mapperC2S: Rendicontazione})
    );
  }

  detail(ente: Ente, iuf: string, iur: string): Observable<Rendicontazione> {
    const targetUrl = `${this.baseApiUrl}rendicontazione/detail/${ente.mygovEnteId}/${iuf}/${iur}`;
    return this.apiInvokerService.get<Rendicontazione>(
      targetUrl, null, new Mappers({mapperS2C: Rendicontazione, mapperC2S: Rendicontazione})
    );
  }

  filterDetail(ente: Ente, iuf: string, iur: string, searchParams: RicevutaSearch): Observable<Rendicontazione> {
    const targetUrl = `${this.baseApiUrl}rendicontazione/detail/${ente.mygovEnteId}/${iuf}/${iur}`;
    return this.apiInvokerService.post<Rendicontazione>(
      targetUrl, searchParams, null, new Mappers({mapperS2C: RendicontazioneDetail, mapperC2S: RendicontazioneDetail})
    );
  }

  mapDataToShow(data: Rendicontazione): KeyValue[] {
    return [
      {key:'ID rendicontazione', value: data.idRendicontazione},
      {key:'ID regolamento', value: data.idRegolamento},
      {key:'Data ora flusso', value: data.dateFlusso?.toFormat('dd/MM/yyyy HH:mm:ss')},
      {key:'Data regolamento', value: data.dataRegolamento?.toFormat('dd/MM/yyyy')},
      {key:'Totale pagamenti', value: data.countTotalePagamenti.toString()},
      {key:'Importo totale', value: this.currencyPipe.transform(data.importoTotale,'EUR','symbol')}
    ];
  }
}
