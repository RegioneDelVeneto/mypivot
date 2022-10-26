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

export class RiconciliazioneSearch {
  public static readonly MAPPER_C2S_DEF = [
    new MapperDef(MapperType.DateTime,'dateEsitoFrom','local-date'),
    new MapperDef(MapperType.DateTime,'dateEsitoTo','local-date'),
    new MapperDef(MapperType.DateTime,'dateUltModFrom','local-date'),
    new MapperDef(MapperType.DateTime,'dateUltModTo','local-date'),
    new MapperDef(MapperType.DateTime,'dateRegolFrom','local-date'),
    new MapperDef(MapperType.DateTime,'dateRegolTo','local-date'),
    new MapperDef(MapperType.DateTime,'dateContabFrom','local-date'),
    new MapperDef(MapperType.DateTime,'dateContabTo','local-date'),
    new MapperDef(MapperType.DateTime,'dateValutaFrom','local-date'),
    new MapperDef(MapperType.DateTime,'dateValutaTo','local-date'),
    new MapperDef(MapperType.DateTime,'annoBolletta','yyyy'),
    new MapperDef(MapperType.DateTime,'annoDocumento','yyyy'),
    new MapperDef(MapperType.DateTime,'annoProvvisorio','yyyy'),
  ];
  dateEsitoFrom: DateTime; 
  dateEsitoTo: DateTime; 
  dateUltModFrom: DateTime; 
  dateUltModTo: DateTime; 
  dateRegolFrom: DateTime; 
  dateRegolTo: DateTime; 
  dateContabFrom: DateTime; 
  dateContabTo: DateTime; 
  dateValutaFrom: DateTime; 
  dateValutaTo: DateTime; 
  iud: string; 
  iuv: string; 
  iur: string; 
  codFiscalePagatore: string; 
  anagPagatore: string; 
  codFiscaleVersante: string; 
  anagVersante: string; 
  attestante: string; 
  ordinante: string; 
  idRendicont: string; 
  idRegolamento: string; 
  tipoDovuto: string; 
  conto: string; 
  importoTesoreria: string; 
  causale: string; 
  annoBolletta: number; 
  codBolletta: string; 
  annoDocumento: number; 
  codDocumento: string; 
  annoProvvisorio: number; 
  codProvvisorio: string; 
}