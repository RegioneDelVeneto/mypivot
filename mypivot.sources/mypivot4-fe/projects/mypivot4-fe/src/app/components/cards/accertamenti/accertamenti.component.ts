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
import { faBriefcase } from '@fortawesome/free-solid-svg-icons';

import {
    AccertamentiCapitoliComponent
} from '../../accertamenti-capitoli/accertamenti-capitoli.component';
import {
    AccertamentiComponent as AccertamentiSearchComponent
} from '../../accertamenti/accertamenti.component';

@Component({
  selector: 'app-accertamenti',
  templateUrl: './accertamenti.component.html',
  styleUrls: ['./accertamenti.component.scss']
})
export class AccertamentiComponent implements OnInit, WithTitle {

  get titleLabel(){ return "Accertamenti" }
  get titleIcon(){ return faBriefcase }

  cards: CardInfo[];

  constructor() { }

  ngOnInit(): void {
    this.cards = [
      new CardInfo('/accertamenti/main', AccertamentiSearchComponent.prototype.titleLabel, AccertamentiSearchComponent.prototype.titleIcon, 
        'In questa sezione potrai creare degli accertamenti, aggiornarli associandovi dei pagamenti, chiuderli e consultarli.'),
      new CardInfo('/accertamenti/capitoli', AccertamentiCapitoliComponent.prototype.titleLabel, AccertamentiCapitoliComponent.prototype.titleIcon, 
        'In questa sezione potrai ricercare e visualizzare il dettaglio per ogni capitolo, e creare nuove anagrafiche.'),
    ];
  }

}
