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
import { CardInfo } from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';

import { Component, Input, OnDestroy, OnInit, SecurityContext } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';

import { EnteService } from '../../services/ente.service';
import { MenuService } from '../../services/menu.service';

@Component({
  selector: 'app-cards',
  templateUrl: './app-cards.component.html',
  styleUrls: ['./app-cards.component.scss']
})
export class AppCardsComponent implements OnInit, OnDestroy {

  filteredCards: CardInfo[] = [];
  enteSelected: boolean = false;
  iconAngleRight = faAngleRight;
  enteChangeSub: Subscription;

  @Input("cards") cards: CardInfo[];

  constructor(
    private menuService: MenuService,
    private domSanitizer: DomSanitizer,
    private enteService: EnteService,
  ) { }

  ngOnInit(): void {
    this.cards.forEach(ci => ci.htmlSafeContent = this.domSanitizer.sanitize(SecurityContext.HTML, ci.content));

    this.enteChangeSub = this.enteService.getCurrentEnteObs().subscribe(ente => {
      setTimeout(() => {
        if(ente){
          this.enteSelected = true;
          this.filteredCards = this.cards?.filter(aCard => this.menuService.isMenuItemAuth(aCard.url)) ?? [];
        } else {
          this.enteSelected = false;
          this.filteredCards = [];
        }          
      }, 0);
    });
  }

  ngOnDestroy(): void {
    this.enteChangeSub?.unsubscribe();
  }
  
}