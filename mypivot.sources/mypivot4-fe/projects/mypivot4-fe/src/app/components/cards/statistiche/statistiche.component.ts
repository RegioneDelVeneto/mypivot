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
import { CardInfo, MenuItem } from 'projects/mypay4-fe-common/src/public-api';

import { Component, OnInit } from '@angular/core';
import { faTable } from '@fortawesome/free-solid-svg-icons';

import { MenuService } from '../../../services/menu.service';
import {
    StatisticheAnnomesegiornoComponent
} from '../../statistiche-annomesegiorno/statistiche-annomesegiorno.component';
import {
    StatisticheCapitoliComponent
} from '../../statistiche-capitoli/statistiche-capitoli.component';

@Component({
  selector: 'app-statistiche',
  templateUrl: './statistiche.component.html',
  styleUrls: ['./statistiche.component.scss']
})
export class StatisticheComponent implements OnInit, WithTitle {

  get titleLabel(){ return "Statistiche" }
  get titleIcon(){ return faTable }
  
  cards: CardInfo[];

  constructor(menuService: MenuService) {
    const menuTotaleUffici = menuService.getMenuItem(31);
    const menuTipiDovuto = menuService.getMenuItem(32);
    const menuAccertamenti = menuService.getMenuItem(34);

    this.cards = [
      new CardInfo('/statistiche/uffici', menuTotaleUffici.getHeaderLabel(), MenuItem.isFAIcon(menuTotaleUffici.icon) ? menuTotaleUffici.icon : null, 
        'Questa statistica permette di visualizzare la ripartizione dei pagamenti in base ai diversi uffici.'),
      new CardInfo('/statistiche/tipiDovuto', menuTipiDovuto.getHeaderLabel(), MenuItem.isFAIcon(menuTipiDovuto.icon) ? menuTipiDovuto.icon : null, 
        'Questa statistica permette di visualizzare la ripartizione dei pagamenti in base ai diversi tipi dovuti dell’ente.'),
      new CardInfo('/statistichecapitoli', StatisticheCapitoliComponent.prototype.titleLabel, StatisticheCapitoliComponent.prototype.titleIcon, 
        'Questa statistica permette di visualizzare la ripartizione in capitoli dei pagamenti relativi a un tipo dovuto e/o '+
        'un ufficio specifico (la ricerca deve essere fatta per almeno uno di questi due parametri).'),
      new CardInfo('/statistiche/accertamenti', menuAccertamenti.getHeaderLabel(), MenuItem.isFAIcon(menuAccertamenti.icon) ? menuAccertamenti.icon : null, 
        'Questa statistica permette di visualizzare la ripartizione dei pagamenti in base ai diversi accertamenti relativi a '+
        'un tipo dovuto, un ufficio e un capitolo specifici (campi di ricerca obbligatori).'),
      new CardInfo('/statisticheannomesegiorno', StatisticheAnnomesegiornoComponent.prototype.titleLabel, StatisticheAnnomesegiornoComponent.prototype.titleIcon, 
        'Questa statistica permette di visualizzare i totali dei pagamenti compresi in un dato intervallo temporale. '+
        'L\' aggregazione temporale può essere specificata: per anno, per mese, per intervallo di giorni.'),
    ];
   }

   ngOnInit(): void {
    
  }

}
