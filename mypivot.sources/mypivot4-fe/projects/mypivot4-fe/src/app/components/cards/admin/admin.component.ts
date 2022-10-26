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
import { faTools } from '@fortawesome/free-solid-svg-icons';

import { EntiComponent } from '../../admin/enti/enti.component';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit, WithTitle {

  get titleLabel(){ return "Back office" }
  get titleIcon(){ return faTools }

  cards: CardInfo[];

  constructor() { }

  ngOnInit(): void {
    this.cards = [
      new CardInfo('/admin/enti', EntiComponent.prototype.titleLabel, EntiComponent.prototype.titleIcon, 
        'In questa sezione potrai visualizzare, creare o eliminare dei codici tipo dovuto esterni, cioè tipi dovuto non '+
        'gestiti da MyPay. Potrai inoltre abilitare gli operatori MyPivot ai tipi dovuto esterni dei rispettivi enti.'),
    ];
  }

}
