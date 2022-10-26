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
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import { CardInfo } from 'projects/mypay4-fe-common/src/public-api';

import { Component, OnInit } from '@angular/core';
import { faCloud } from '@fortawesome/free-solid-svg-icons';

import { FlussiExportComponent } from '../../flussi-export/flussi-export.component';
import { FlussiImportComponent } from '../../flussi-import/flussi-import.component';

@Component({
  selector: 'app-flussi',
  templateUrl: './flussi.component.html',
  styleUrls: ['./flussi.component.scss']
})
export class FlussiComponent implements OnInit, WithTitle {

  get titleLabel(){ return "Gestione flussi" }
  get titleIcon(){ return faCloud }

  cards: CardInfo[];

  constructor() { }

  ngOnInit(): void {
    this.cards = [
      new CardInfo('/flussi-import', FlussiImportComponent.prototype.titleLabel, FlussiImportComponent.prototype.titleIcon, 
        'In questa sezione potrai caricare su MyPivot i flussi di dati relativi a Ricevute Telematiche, Rendicontazioni pagoPA e '+
        'Giornale di Cassa, al fine di consentire la riconciliazione automatica dei pagamenti. Potrai inoltre visualizzare i '+
        'flussi di dati già presenti in MyPivot.'),
      new CardInfo('/flussi-export', FlussiExportComponent.prototype.titleLabel, FlussiExportComponent.prototype.titleIcon, 
        'In questa sezione potrai scaricare in locale una copia dei flussi precedentemente prenotati e generati a partire dai dati presenti in MyPivot.'),
    ];
  }

}
