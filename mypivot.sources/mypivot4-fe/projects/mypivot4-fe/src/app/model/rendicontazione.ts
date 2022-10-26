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
import { MapperDef, MapperType, WithActions } from 'projects/mypay4-fe-common/src/public-api';

import { FlussoRicevuta } from './flusso-ricevuta';

export class Rendicontazione extends WithActions {
  public static readonly MAPPER_S2C_DEF = [
    new MapperDef(MapperType.DateTime,'dateFlusso','local-date-time'),
    new MapperDef(MapperType.DateTime,'dataRegolamento','local-date'),
    new MapperDef(MapperType.Currency,'importoTotale'),
  ];
  public static readonly MAPPER_C2S_DEF = [
    new MapperDef(MapperType.DateTime,'dateRegolFrom','local-date'),
    new MapperDef(MapperType.DateTime,'dateRegolTo','local-date')
  ];
  
  idRendicontazione: string;
  idRegolamento: string;
  dateRegolFrom: DateTime;
  dateRegolTo: DateTime;

  dateFlusso: DateTime;
  dataRegolamento: DateTime;
  importoTotale: number;
  countTotalePagamenti: number;

  details: FlussoRicevuta[];
}