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
import { MapperDef, MapperType } from 'projects/mypay4-fe-common/src/public-api';

export class RicevutaSearch {
  public static readonly MAPPER_C2S_DEF = [
    new MapperDef(MapperType.DateTime,'dateEsitoFrom','local-date'),
    new MapperDef(MapperType.DateTime,'dateEsitoTo','local-date')
  ];
  
  dateEsitoFrom: DateTime; 
  dateEsitoTo: DateTime; 
  iud: string; 
  iuv: string; 
  iur: string; 
  codFiscalePagatore: string; 
  anagPagatore: string; 
  codFiscaleVersante: string; 
  anagVersante: string; 
  attestante: string; 
  tipoDovuto: string; 
}