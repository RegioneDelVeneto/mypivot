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

import { Injectable } from '@angular/core';

import { Ente } from '../model/ente';
import { Segnalazione } from '../model/segnalazione';
import { SegnalazioneSearch } from '../model/segnalazione-search';
import { Utente } from '../model/utente';

@Injectable({
  providedIn: 'root'
})
export class SegnalazioneService {

  private baseApiUrl: string;

  constructor(
    private apiInvokerService: ApiInvokerService,
    conf: ConfigurationService
  ) {
    this.baseApiUrl = conf.getProperty('baseApiUrl', environment);
  }

  insert(ente: Ente, segnalazione: Segnalazione) {
    return this.apiInvokerService.post<Segnalazione>(`${this.baseApiUrl}segnalazione/${ente.mygovEnteId}`,segnalazione, null, new Mappers({mapperS2C: Segnalazione}));
  }

  search(ente: Ente, searchType: string, iufKey: string, iudKey: string, iuvKey: string) {
    return this.apiInvokerService.get<Segnalazione[]>(`${this.baseApiUrl}riconciliazione/detail/${ente.mygovEnteId}/${searchType}/${iufKey}/${iudKey}/${iuvKey}`,
      null, new Mappers({mapperS2C: Segnalazione}));
  }

  searchSegnalazioni(ente: Ente, segnalazioneSearch: SegnalazioneSearch) {
    return this.apiInvokerService.post<Segnalazione[]>(`${this.baseApiUrl}segnalazione/search/${ente.mygovEnteId}`, segnalazioneSearch, null, new Mappers({mapperC2S: SegnalazioneSearch, mapperS2C: Segnalazione}));
  }

  getUtenti(ente: Ente) {
    return this.apiInvokerService.get<Utente[]>(`${this.baseApiUrl}segnalazione/storico/utenti/${ente.mygovEnteId}`);
  }
}
