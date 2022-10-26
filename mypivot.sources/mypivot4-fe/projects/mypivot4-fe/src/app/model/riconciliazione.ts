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
    KeyValue
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table-detail/my-pay-table-detail.component';
import { MapperDef, MapperType, WithActions } from 'projects/mypay4-fe-common/src/public-api';
import { BehaviorSubject } from 'rxjs';

export class Riconciliazione extends WithActions {

  public static readonly MAPPER_S2C_DEF = [
    new MapperDef(MapperType.DateTime,'dataEsito','local-date'),
    new MapperDef(MapperType.DateTime,'dataFlusso','local-date-time'),
    new MapperDef(MapperType.DateTime,'dataUltimoAgg','local-date-time'),
    new MapperDef(MapperType.Currency,'importo'),
    new MapperDef(MapperType.Currency,'importoTotale'),
    new MapperDef(MapperType.Currency,'importoTesoreria'),
    new MapperDef(MapperType.DateTime,'dataEsecuzione','local-date'),
    new MapperDef(MapperType.DateTime,'dataRegolamento','local-date'),
    new MapperDef(MapperType.DateTime,'dataValuta','local-date'),
    new MapperDef(MapperType.DateTime,'dataContabile','local-date'),
    new MapperDef(MapperType.DateTime,'dataInserimentoSegnalazione','local-date-time'),
    new MapperDef(MapperType.Boolean,'hasSegnalazione'),
    new MapperDef(MapperType.Number,'annoBolletta'),
    new MapperDef(MapperType.Number,'annoDocumento'),
    new MapperDef(MapperType.Number,'annoProvvisorio'),
  ];

  classificazione: string;
  classificazioneLabel: string;
  deTipoDovuto: string;
  dataUltimoAgg: DateTime;
  iuvKey: string;
  iudKey: string;
  iufKey: string;
  hasSegnalazione: boolean;

  //notifica
  iuv: string;
  iud: string;
  iur: string;
  importo: number;
  dataEsecuzione: DateTime;
  pagatoreAnagrafica: string;
  pagatoreCodFisc: string;
  causale: string;
  datiSpecificiRiscossione: string;

  //ricevuta telematica
  attestanteAnagrafica: string;
  attestanteCodFisc: string;
  versanteAnagrafica: string;
  versanteCodFisc: string;
  dataEsito: DateTime;

  //rendicontazione
  idRendicontazione: string;
  idRegolamento: string;
  dataRegolamento: DateTime;
  importoTotale: number;
  dataFlusso: DateTime;

  //sospeso
  importoTesoreria: number;
  conto: string;
  dataValuta: DateTime;
  dataContabile: DateTime;
  ordinante: string;
  annoBolletta: number;
  codiceBolletta: string;
  annoDocumento: number;
  codDocumento: string;
  annoProvvisorio: number;
  codProvvisorio: string;
  causaleRiversamento: string;

  //segnalazione
  notaSegnalazione: string;
  utenteSegnalazione: string;
  cfUtenteSegnalazione: string;
  dataInserimentoSegnalazione: DateTime;

  details: BehaviorSubject<KeyValue[]>;
}