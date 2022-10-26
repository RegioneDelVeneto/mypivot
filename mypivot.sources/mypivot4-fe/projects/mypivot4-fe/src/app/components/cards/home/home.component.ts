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
import { faHome } from '@fortawesome/free-solid-svg-icons';

import { AccertamentiComponent } from '../../cards/accertamenti/accertamenti.component';
import { AdminComponent } from '../admin/admin.component';
import { FlussiComponent } from '../flussi/flussi.component';
import { StatisticheComponent } from '../statistiche/statistiche.component';
import { VisualizzaComponent } from '../visualizza/visualizza.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, WithTitle {

  get titleLabel(){ return "Home" }
  get titleIcon(){ return faHome }

  cards: CardInfo[];

  constructor() {}

  ngOnInit(): void {
    this.cards = [
      new CardInfo('/visualizzazione', VisualizzaComponent.prototype.titleLabel, VisualizzaComponent.prototype.titleIcon,
        'In questa sezione potrai visualizzare i dati relativi a ciascun elemento coinvolto nella riconciliazione contabile: ' +
        'la rendicontazione, le ricevute telematiche e il giornale di cassa. Potrai inoltre visualizzare il dettaglio di ' +
        'eventuali situazioni anomale che si possono verificare quando la riconciliazione non si chiude correttamente.'),
      new CardInfo('/flussi', FlussiComponent.prototype.titleLabel, FlussiComponent.prototype.titleIcon,
        'In questa sezione potrai importare i flussi relativi a rendicontazioni pagoPA, ricevute telematiche e giornale ' +
        'di cassa. Alcuni di questi flussi vengono caricati automaticamente, altri possono essere importati manualmente. ' +
        'Potrai inoltre esportare i flussi presenti in MyPivot.'),
      new CardInfo('/accertamenti', AccertamentiComponent.prototype.titleLabel, AccertamentiComponent.prototype.titleIcon,
        'In questa sezione potrai gestire i capitoli e gli accertamenti contabili associati ai singoli pagamenti.'),
      new CardInfo('/statistiche', StatisticheComponent.prototype.titleLabel, StatisticheComponent.prototype.titleIcon,
        'In questa sezione potrai effettuare un monitoraggio statistico delle entrate. Sono disponibili varie tipologie di ' +
        'report che possono mostrare la suddivisione degli importi per le seguenti categorie: pagati, rendicontati e incassati. ' +
        'Un parametro comune a tutte le ricerche è l’intervallo temporale.  A partire dagli importi visualizzati è possibile ' +
        'accedere al relativo dettaglio, evidenziato tramite link, ove disponibile. '),
      new CardInfo('/admin', AdminComponent.prototype.titleLabel, AdminComponent.prototype.titleIcon,
        'In questa sezione potrai gestire Tipi Dovuto esterni per i vari enti e abilitare ad essi gli operatori degli enti.'),
    ];
  }

}
