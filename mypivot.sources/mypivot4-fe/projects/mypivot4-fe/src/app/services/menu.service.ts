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
import * as _ from 'lodash';
import { MyPayBreadcrumbsService, UserService } from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';

import { Injectable, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import {
    faClipboardCheck, faClone, faExclamationTriangle, faLink, faPlayCircle, faStar, faTable,
    faTachometerAlt
} from '@fortawesome/free-solid-svg-icons';

import { MenuItem } from '../../../../mypay4-fe-common/src/lib/model/menu-item';
import { routes } from '../app-routing/app-routing.module';
import {
    AccertamentiCapitoliComponent
} from '../components/accertamenti-capitoli/accertamenti-capitoli.component';
import { AccertamentiComponent } from '../components/accertamenti/accertamenti.component';
import { AccertamentiComponent as AccertamentiCardComponent } from '../components/cards/accertamenti/accertamenti.component';
import { EntiComponent } from '../components/admin/enti/enti.component';
import { AdminComponent } from '../components/cards/admin/admin.component';
import { FlussiComponent } from '../components/cards/flussi/flussi.component';
import { HomeComponent as HomeCardsComponent } from '../components/cards/home/home.component';
import {
    StatisticheComponent as StatisticheCardComponent
} from '../components/cards/statistiche/statistiche.component';
import { VisualizzaComponent } from '../components/cards/visualizza/visualizza.component';
import { FlussiExportComponent } from '../components/flussi-export/flussi-export.component';
import { FlussiImportComponent } from '../components/flussi-import/flussi-import.component';
import { RendicontazioneComponent } from '../components/rendicontazione/rendicontazione.component';
import {
    RicevuteTelematicheVisualizzazioneComponent
} from '../components/ricevute-telematiche-visualizzazione/ricevute-telematiche-visualizzazione.component';
import {
    SegnalazioneStoricoComponent
} from '../components/segnalazione-storico/segnalazione-storico.component';
import {
    StatisticheAnnomesegiornoComponent
} from '../components/statistiche-annomesegiorno/statistiche-annomesegiorno.component';
import {
    StatisticheCapitoliComponent
} from '../components/statistiche-capitoli/statistiche-capitoli.component';
import { TesorerieComponent } from '../components/tesorerie/tesorerie.component';
import { EnteService } from './ente.service';

@Injectable({
  providedIn: 'root'
})
export class MenuService implements OnDestroy{

  private menuItemsMap:{[key:number]:MenuItem} = {};
  private menuItemsMapByPath:{[key:string]:MenuItem} = {};
  private menuItemsMyIntranet: MenuItem[];
  private menuItemsApp: MenuItem[];

  private roles: string[];
  private enteSub: Subscription;

  constructor(
    private router: Router,
    private userService: UserService,
    private enteService: EnteService,
    private myPayBreadcrumbsService: MyPayBreadcrumbsService,
  ) {
    this.menuItemsMyIntranet = [
      new MenuItem(10, '#', 'Link esterno 1', {external:true, icon:faTachometerAlt}),
      new MenuItem(11, '#', 'Link esterno 2', {external:true, icon:faClone}),
      new MenuItem(12, '#', 'Link esterno 3', {external:true, icon:faClipboardCheck}),
      new MenuItem(13, '#', 'Link esterno 4', {external:true, icon:faStar}),
      new MenuItem(14, '#', 'Link esterno 5', {external:true, icon:faPlayCircle}),
    ];
    this.menuItemsApp = [
      new MenuItem(15, '/cards/home', HomeCardsComponent.prototype, {needEnte:true}),
      new MenuItem(60, '/visualizzazione', VisualizzaComponent.prototype, {roles:['ROLE_VISUAL'], submenu:[
        new MenuItem(61,'/visualizzazione/riconciliazioni','Riconciliazioni', {icon:faLink, roles:['ROLE_VISUAL']}),
        new MenuItem(64,'/visualizzazione/anomalie','Anomalie', {icon:faExclamationTriangle, roles:['ROLE_VISUAL']}),
        new MenuItem(62,'/visualizzazione/rendicontazione',RendicontazioneComponent.prototype, {roles:['ROLE_VISUAL']}),
        new MenuItem(63,'/visualizzazione/ricevute-telematiche',RicevuteTelematicheVisualizzazioneComponent.prototype, {roles:['ROLE_VISUAL']}),
        new MenuItem(66,'/visualizzazione/tesoreria',TesorerieComponent.prototype, {roles:['ROLE_VISUAL']}),
        new MenuItem(67,'/visualizzazione/segnalazione/storico',SegnalazioneStoricoComponent.prototype, {roles:['ROLE_VISUAL']}),
      ], needEnte:true}),
      new MenuItem(20, '/flussi', FlussiComponent.prototype, {roles:['ROLE_ADMIN','ROLE_VISUAL'], submenu:[
        new MenuItem(21, '/flussi-import', FlussiImportComponent.prototype, {roles:['ROLE_ADMIN']}),
        new MenuItem(22, '/flussi-export', FlussiExportComponent.prototype, {roles:['ROLE_VISUAL']}),
      ]}),
      new MenuItem(50,'/accertamenti', AccertamentiCardComponent.prototype, {roles:['ROLE_ADMIN','ROLE_ACC'], submenu:[
        new MenuItem(51,'/accertamenti/main', AccertamentiComponent.prototype, {roles:['ROLE_ADMIN','ROLE_ACC']}),
        new MenuItem(53,'/accertamenti/capitoli', AccertamentiCapitoliComponent.prototype, {roles:['ROLE_ADMIN','ROLE_ACC']})
      ], needEnte:true}),
      new MenuItem(30, '/statistiche', StatisticheCardComponent.prototype, {roles:['ROLE_ADMIN','ROLE_STATS'], submenu:[
        new MenuItem(31, '/statistiche/uffici', 'Totali ripartiti per uffici', {icon:faTable, roles:['ROLE_ADMIN','ROLE_STATS']}),
        new MenuItem(32, '/statistiche/tipiDovuto', 'Totali ripartiti per tipi dovuto', {icon:faTable, roles:['ROLE_ADMIN','ROLE_STATS']}),
        new MenuItem(33, '/statistichecapitoli', StatisticheCapitoliComponent.prototype, {roles:['ROLE_ADMIN','ROLE_STATS']}),
        new MenuItem(34, '/statistiche/accertamenti', 'Totali ripartiti per accertamenti', {icon:faTable, roles:['ROLE_ADMIN','ROLE_STATS']}),
        new MenuItem(35, '/statisticheannomesegiorno', StatisticheAnnomesegiornoComponent.prototype, {roles:['ROLE_ADMIN','ROLE_STATS']}),
      ], needEnte:true}),
      new MenuItem(40,'/admin', AdminComponent.prototype, {roles:[UserService.BACK_OFFICE_ADMIN_ROLE], submenu:[
        new MenuItem(41,'/admin/enti',EntiComponent.prototype, {roles:[UserService.BACK_OFFICE_ADMIN_ROLE], needEnte:true})
      ], needEnte:true}),
    ];

    this.enteSub = this.enteService.getCurrentEnteObs().subscribe(ente => {
      if(ente)
        this.roles = this.userService.getLoggedUser()?.entiRoles?.[ente.codIpaEnte] || [];
      else
        this.roles = [];
      
      this.setRoleAuth(this.menuItemsApp);
      this.myPayBreadcrumbsService.setMenuItemMap(this.menuItemsMap);

      //when defining the routes, just set the corresponding menu item id
      // here retrieve the full MenuItem object
      routes.filter(route => route.data?.menuItemId).forEach(route => route.data.menuItem = this.getMenuItem(route.data.menuItemId) );
    })
  }

  private setRoleAuth(menuItem: MenuItem[], parent?: MenuItem){
    menuItem.forEach(item => {
      item.parent = parent;
      this.menuItemsMap[item.id] = item;
      let url;
      if(typeof item.url ==='string')
        url = item.url;
      else
        throw 'unsupported';
      this.menuItemsMapByPath[url] = item;
      item.auth = !item.roles
        || item.roles.includes(UserService.BACK_OFFICE_ADMIN_ROLE) && this.userService.isRoleAuthorized(UserService.BACK_OFFICE_ADMIN_ROLE)    //special handling for app admin role
        || _.intersection(item.roles, this.roles).length > 0;
      console.log('setting auth for item: '+item.labelHeader+' - '+item.auth+' - '+_.intersection(item.roles, this.roles).join(','));
      if(item.submenu)
        this.setRoleAuth(item.submenu, item);
    });
  }

  ngOnDestroy(){
    this.enteSub?.unsubscribe();
  }

  getMainMenu(){
    return this.menuItemsMyIntranet;
  }

  getMenuItem(id: number): MenuItem {
    return this.menuItemsMap[id];
  }

  getApplicationMenu(){
    return this.menuItemsApp;
  }

  onClickMenu(item: MenuItem, thisRef: MenuService) {
    if(!item.url)
      return;

    const url = (typeof item.url === 'string') ? item.url : item.url?.(thisRef.enteService.getCurrentCommonEnte(), thisRef);
    if(item.external)
      window.location.href = url;
    else {
      thisRef.myPayBreadcrumbsService.resetBreadcrumbs();
      thisRef.router.navigateByUrl(url);
    }
  }

  // isMenuItemAuth(id: number):boolean {
  //   return this.menuItemsMap[id]?.auth ?? false;
  // }

  isMenuItemAuth(url: string):boolean {
    return this.menuItemsMapByPath[url]?.auth ?? false;
  }


}
