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
import { ToastrService } from 'ngx-toastr';
import {
    KeyValue, MyPayTableDetailComponent, UpdateDetailFun
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table-detail/my-pay-table-detail.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    manageError, OverlaySpinnerService, PageStateService, WithActions
} from 'projects/mypay4-fe-common/src/public-api';
import { BehaviorSubject, Subscription } from 'rxjs';

import { Location } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { faLink } from '@fortawesome/free-solid-svg-icons';

import { Riconciliazione } from '../../model/riconciliazione';
import { Segnalazione } from '../../model/segnalazione';
import { EnteService } from '../../services/ente.service';
import { RiconciliazioneService } from '../../services/riconciliazione.service';
import { SegnalazioneAddComponent } from '../segnalazione-add/segnalazione-add.component';

@Component({
  selector: 'app-riconciliazione-detail',
  templateUrl: './riconciliazione-detail.component.html',
  styleUrls: ['./riconciliazione-detail.component.scss']
})
export class RiconciliazioneDetailComponent implements OnInit, OnDestroy, WithTitle {

  get titleLabel(){ return this.classificazioneLabel ? (this.classificazioneLabel+ " - dettaglio") : "Dettaglio" }
  get titleIcon(){ return faLink }

  private searchType: string;
  private iufKey: string;
  private iudKey: string;
  private iuvKey: string;
  private elem: Riconciliazione;
  hasSegnalazione: boolean = false;
  hasDetailRt: boolean = false;
  classificazioneLabel: string;
  detailFilterExclude = ['classificazioneLabel'];
  private elemKeyValueSubject: BehaviorSubject<KeyValue[]>;
  private elemKeyValueSubjectSubscription: Subscription;
  public detailsGroups: any;
  public detailsGroupsLabel: any;
  private previousPageNavId: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private matDialog: MatDialog,
    private enteService: EnteService,
    private riconciliazioneService: RiconciliazioneService,
    private pageStateService: PageStateService,
  ) { 
    this.previousPageNavId = this.router.getCurrentNavigation()?.extras?.state?.backNavId;
  }

  ngOnInit(): void {
    if(this.enteService.getCurrentEnte()) {
      const params = this.route.snapshot.params;
      this.searchType = params['searchType'];
      this.iufKey = params['iuf'] || null;
      this.iudKey = params['iud'] || null;
      this.iuvKey = params['iuv'] || null;

      const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
      this.riconciliazioneService.detail(this.enteService.getCurrentEnte(), this.searchType, 
        this.iufKey, this.iudKey, this.iuvKey).subscribe( elem => {
          this.elem = elem;
          this.classificazioneLabel = elem.classificazioneLabel;
          this.hasSegnalazione = elem.hasSegnalazione;

          this.elemKeyValueSubject = new BehaviorSubject(this.riconciliazioneService.mapDetailToKeyValueSections(elem));
          this.hasDetailRt = this.riconciliazioneService.hasDetailRt(this.elem);

          this.elemKeyValueSubjectSubscription = this.elemKeyValueSubject.subscribe(elemKeyValue => {
            const detailsGroups = [];
            const detailsGroupsLabel = [];
            let details = [];
            elemKeyValue.forEach(element => {
              if(element.key === MyPayTableDetailComponent.SECTION_ID){
                detailsGroupsLabel.push(element.value);
                details = [];
                detailsGroups.push(details);
              } else {
                if(!details){
                  detailsGroupsLabel.push(null);
                  detailsGroups.push(details);
                }
                if(!details.find(elem => elem.key === element.key))
                  details.push(element);
              }
            });
            this.detailsGroups = detailsGroups;
            this.detailsGroupsLabel = detailsGroupsLabel;
          });

          this.overlaySpinnerService.detach(spinner);
        }, manageError('Errore recuperando il dettaglio', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
    } else {
      //redirect to search view
      const path = this.riconciliazioneService.isRiconciliazioneSearchType(this.searchType) ? 'riconciliazioni' : 'anomalie';
      this.router.navigate(['visualizzazione',path]);
    }
    
  }

  ngOnDestroy():void {
    this.elemKeyValueSubjectSubscription?.unsubscribe();
  }

  back(){
    this.location.back();
  }

  addSegnalazione(){
    //open detail panel
    this.matDialog.open(SegnalazioneAddComponent, {
      panelClass: 'add-segnalazione-panel', autoFocus:false, 
    id: SegnalazioneAddComponent.DIALOG_ID,
    data: {
      classificazione: this.searchType,
      iufKey: this.iufKey || null,
      iudKey: this.iudKey || null,
      iuvKey: this.iuvKey || null,
      updateDetailsFun: (updateFun:UpdateDetailFun)=>updateFun(this.elemKeyValueSubject) ,
      callbackFun: (segnalazione:Segnalazione)=>{
        this.elem.hasSegnalazione=true;
        this.hasSegnalazione = true;
        WithActions.reset(this.elem);
        if(!_.isNil(this.previousPageNavId))
          this.pageStateService.addToSavedState(this.previousPageNavId, "riconciliazioneToReload", this.elem);
      }
    } } );
  }

  gotoStorico(){
    this.router.navigate(['visualizzazione','segnalazione','storico'],{ state: {
      searchType: this.searchType,
      iuf: this.iufKey,
      iud: this.iudKey,
      iuv: this.iuvKey
    } });
  }

}
