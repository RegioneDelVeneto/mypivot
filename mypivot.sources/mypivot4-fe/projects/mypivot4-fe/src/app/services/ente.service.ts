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
import {
    ApiInvokerService, ConfigurationService, Ente as CommonEnte, environment, UserService
} from 'projects/mypay4-fe-common/src/public-api';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

import { Injectable, OnDestroy } from '@angular/core';
import { Router, RoutesRecognized } from '@angular/router';

import { Ente } from '../model/ente';
import { TipoDovuto } from '../model/tipo-dovuto';

@Injectable({
  providedIn: 'root'
})
export class EnteService implements OnDestroy{

  private baseApiUrl: string;
  private currentEnte: Ente;
  private currentCommonEnte: CommonEnte;
  private currentEnteSubject: Subject<Ente> = new BehaviorSubject(null);
  private userChangeSub: Subscription;
  private needEnte: boolean;
  private needEnteSubject: Subject<boolean> = new BehaviorSubject(false);
  private routeChangeSub: Subscription;

  constructor(
    private apiInvokerService: ApiInvokerService,
    private userService: UserService,
    conf: ConfigurationService,
    private router: Router,
  ) {
    this.baseApiUrl = conf.getProperty('baseApiUrl', environment);
    //reset current ente if user changes
    this.userChangeSub = this.userService.getLoggedUserObs().subscribe(user => {
      this.setCurrentEnte(null);
    });
    this.routeChangeSub = this.router.events
    .pipe(filter(event => event instanceof RoutesRecognized))
    .subscribe( (event:RoutesRecognized) => {
      const data = event.state.root.firstChild.data;
      let needEnte = true;
      if(data.menuItem)
        needEnte = data.menuItem.needEnte;
      this.setNeedEnte(needEnte);
    });
  }

  ngOnDestroy():void {
    this.userChangeSub?.unsubscribe();
  }

  getCurrentEnte():Ente {
    return this.currentEnte;
  }

  getCurrentCommonEnte():CommonEnte {
    return this.currentCommonEnte;
  }

  setCurrentEnte(ente:Ente) {
    const changed = this.currentEnte != ente;
    this.currentEnte = ente;
    if(changed){
      if(this.currentEnte==null)
        this.currentCommonEnte = null;
      else{
        this.currentCommonEnte = new CommonEnte();
        this.currentCommonEnte.codIpaEnte = ente.codIpaEnte;
        this.currentCommonEnte.codiceFiscaleEnte = ente.codiceFiscaleEnte;
        this.currentCommonEnte.deLogoEnte = null;
        this.currentCommonEnte.deNomeEnte = ente.deNomeEnte;
        this.currentCommonEnte.mygovEnteId = ente.mygovEnteId;
        this.currentCommonEnte.enabledActions = ente.enabledActions;
        this.currentCommonEnte.showFloatingButtons = ente.showFloatingButtons;
        this.currentCommonEnte.thumbLogoEnte = ente.thumbLogoEnte;
      }
    }
    if(changed || this.currentEnte === null){
      console.log("ente changed to",this.currentEnte);
      this.currentEnteSubject.next(this.currentEnte);
    }
  }

  getCurrentEnteObs():Observable<Ente> {
    return this.currentEnteSubject;
  }

  isNeedEnte(): boolean {
    return this.needEnte;
  }

  setNeedEnte(needEnte: boolean) {
    const changed = this.needEnte !== needEnte;
    this.needEnte = needEnte;
    if(changed){
      this.needEnteSubject.next(this.needEnte);
    }
  }

  getNeedEnteObs():Observable<boolean> {
    return this.needEnteSubject;
  }

  getEntiByUtente(): Observable<Ente[]> {
    return this.apiInvokerService.get<Ente[]>(this.baseApiUrl + 'enti/byUtente', {skipHandleError: true});
  }

  getListTipoDovutoByEnte(ente: Ente): Observable<TipoDovuto[]> {
    return this.apiInvokerService.get<TipoDovuto[]>(this.baseApiUrl + 'enti/' + ente.mygovEnteId + '/tipiDovuto');
  }

  getListTipoDovutoByEnteAsOperatore(ente: Ente): Observable<TipoDovuto[]> {
    return this.apiInvokerService.get<TipoDovuto[]>(this.baseApiUrl + 'enti/' + ente.mygovEnteId + '/tipiDovutoOperatore');
  }

}
