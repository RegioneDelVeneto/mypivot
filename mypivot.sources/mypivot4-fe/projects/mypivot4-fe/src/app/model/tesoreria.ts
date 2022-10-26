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
import { WithActions } from './../../../../mypay4-fe-common/src/lib/table/with-actions';
import { DateTime } from 'luxon';

export class Tesoreria extends WithActions {
  id: number;
  annoBolletta: string;
  codBolletta: string;
  dtValuta: DateTime;
  dtContabile: DateTime;
  idRendicontazione: string;
  importoTesoreria: number;
  annoCodDocumento: string;
  codDocumento: string;
  annoCodProvvisorio: string;
  codProvvisorio: string;
  ordinante: string
  causale: string;
  codConto: string;

  details: object[];
}