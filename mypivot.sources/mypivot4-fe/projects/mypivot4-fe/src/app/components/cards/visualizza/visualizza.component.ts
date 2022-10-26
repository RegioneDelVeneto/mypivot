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
import { faSearch } from '@fortawesome/free-solid-svg-icons';

import { MenuService } from '../../../services/menu.service';
import { RendicontazioneComponent } from '../../rendicontazione/rendicontazione.component';
import {
    RicevuteTelematicheVisualizzazioneComponent
} from '../../ricevute-telematiche-visualizzazione/ricevute-telematiche-visualizzazione.component';
import {
    SegnalazioneStoricoComponent
} from '../../segnalazione-storico/segnalazione-storico.component';
import { TesorerieComponent } from '../../tesorerie/tesorerie.component';

@Component({
  selector: 'app-visualizza',
  templateUrl: './visualizza.component.html',
  styleUrls: ['./visualizza.component.scss']
})
export class VisualizzaComponent implements OnInit, WithTitle {

  get titleLabel(){ return "Visualizza" }
  get titleIcon(){ return faSearch }

  cards: CardInfo[];

  constructor(menuService: MenuService) {
    const menuRiconciliazioni = menuService.getMenuItem(61);
    const menuAnomalie = menuService.getMenuItem(64);

    this.cards = [
      new CardInfo('/visualizzazione/riconciliazioni', menuRiconciliazioni.getHeaderLabel(), MenuItem.isFAIcon(menuRiconciliazioni.icon) ? menuRiconciliazioni.icon : null, 
        'In questa sezione potrai cercare le riconciliazioni secondo le varie classificazioni dei pagamenti.'),
      new CardInfo('/visualizzazione/anomalie', menuAnomalie.getHeaderLabel(), MenuItem.isFAIcon(menuAnomalie.icon) ? menuAnomalie.icon : null, 
        'In questa sezione potrai visualizzare il dettaglio di eventuali situazioni anomale. Le anomalie si possono verificare quando le informazioni '+
        'relative ai dovuti caricati nel sistema dai diversi flussi sono incomplete, oppure presentano discrepanze oppure non sono transitate nel '+
        'sistema pagoPA.'),
      new CardInfo('/visualizzazione/rendicontazione', RendicontazioneComponent.prototype.titleLabel, RendicontazioneComponent.prototype.titleIcon, 
        'In questa sezione potrai cercare le rendicontazioni pagoPA e visualizzarne il dettaglio (solo per i  tipo dovuto abilitati per l\'operatore). '+
        'La data regolamento, presente tra i parametri di ricerca, corrisponde alla data di emissione della rendicontazione. Secondo le linee guida '+
        'PagoPA, i flussi di rendicontazione devono essere trasmessi dai PSP entro 48 ore dalla giornata dell\'avvenuto pagamento.'),
      new CardInfo('/visualizzazione/ricevute-telematiche', RicevuteTelematicheVisualizzazioneComponent.prototype.titleLabel, RicevuteTelematicheVisualizzazioneComponent.prototype.titleIcon, 
        'In questa sezione potrai cercare le Ricevute Telematiche e visualizzarne il dettaglio (solo per i  tipi dovuto abilitati per il tuo operatore). '+
        'Le Ricevute Telematiche sono importate in MyPivot in maniera automatica da MyPay.'),
      new CardInfo('/visualizzazione/tesoreria', TesorerieComponent.prototype.titleLabel, TesorerieComponent.prototype.titleIcon, 
        'In questa sezione potrai visualizzare i Giornali di Cassa (dati di tesoreria) acquisiti da MyPivot. I dati del giornale di cassa possono essere '+
        'importati in MyPivot in maniera automatica, tramite una integrazione del software di contabilità, oppure in maniera manuale, tramite l\'importazione '+
        'di un flusso di tipo “Giornale di Cassa”.'),
      new CardInfo('/visualizzazione/segnalazione/storico', SegnalazioneStoricoComponent.prototype.titleLabel, SegnalazioneStoricoComponent.prototype.titleIcon, 
        'Una segnalazione è una nota interna che può essere associata ad un pagamento o ad una anomalia. '+
        'In questa sezione potrai visualizzare la storia delle segnalazioni inserite dai vari operatori dell’ente.'),
    ];
   }

  ngOnInit(): void {
  }

}
