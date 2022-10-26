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
import { MapperType, MapperDef } from 'projects/mypay4-fe-common/src/public-api';

export class AnagraficaUffCapAcc extends WithActions {

  public static readonly MAPPER_S2C_DEF = [
    new MapperDef(MapperType.Function,'deComboUfficio',(anag: AnagraficaUffCapAcc) => {
      let deUfficio = (!anag.deUfficio || anag.deUfficio.length <= 35) ?
          anag.deUfficio : anag.deUfficio.substr(0, 35) + '...';
      return `${anag.codUfficio} - ${deUfficio}`;
    }),
    new MapperDef(MapperType.Function,'deComboCapitolo',(anag: AnagraficaUffCapAcc) => {
      let deCapitolo = (!anag.deCapitolo || anag.deCapitolo.length <= 35) ?
          anag.deCapitolo : anag.deCapitolo.substr(0, 35) + '...';
      return `${anag.codCapitolo} - ${deCapitolo}`;
    }),
  ]

  mygovAnagraficaUffCapAccId: number;
  mygovEnteId: number;
  codTipoDovuto: string;
  codUfficio: string;
  deUfficio: string;
  flgAttivo: boolean;
  codCapitolo: string;
  deCapitolo: string;
  deAnnoEsercizio: string;
  codAccertamento: string;
  deAccertamento: string;
  dtCreazione: DateTime;
  dtUltimaModifica: DateTime;

  deComboUfficio: string;
  deComboCapitolo: string;
}