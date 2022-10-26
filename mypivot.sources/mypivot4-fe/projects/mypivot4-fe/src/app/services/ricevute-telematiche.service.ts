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
    ApiInvokerService, ConfigurationService, environment, Mappers
} from 'projects/mypay4-fe-common/src/public-api';
import { Observable } from 'rxjs';

import { Injectable } from '@angular/core';

import { Ente } from '../model/ente';
import { FlussoRicevuta } from '../model/flusso-ricevuta';
import { RicevutaSearch } from '../model/ricevuta-search';

@Injectable({
  providedIn: 'root'
})
export class RicevuteTelematicheService {

  private baseApiUrl: string;

  constructor(
    private apiInvokerService: ApiInvokerService,
    conf: ConfigurationService
  ) {
    this.baseApiUrl = conf.getProperty('baseApiUrl', environment);
  }

  search(ente: Ente, searchParams: RicevutaSearch): Observable<FlussoRicevuta[]> {
    const targetUrl = `${this.baseApiUrl}ricevute-telematiche/search/${ente.mygovEnteId}`;
    return this.apiInvokerService.post<FlussoRicevuta[]>(
      targetUrl, searchParams, null, new Mappers({mapper: FlussoRicevuta})
    );
  }

  getMypayInfo(ente: Ente, iuv: string): Observable<FlussoRicevuta> {
    const targetUrl = `${this.baseApiUrl}ricevute-telematiche/mypayinfo/${ente.mygovEnteId}/${iuv}`;
    return this.apiInvokerService.get<FlussoRicevuta>(
      targetUrl, null, new Mappers({mapper: FlussoRicevuta})
    );
  }
}
