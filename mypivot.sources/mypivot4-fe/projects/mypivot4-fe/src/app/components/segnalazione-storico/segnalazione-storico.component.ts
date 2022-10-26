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
import { DateTime } from 'luxon';
import { ToastrService } from 'ngx-toastr';
import {
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import {
    MypSearchChipsComponent
} from 'projects/mypay4-fe-common/src/lib/components/myp-search-chips/myp-search-chips.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    DateValidators, manageError, MapPipe, OverlaySpinnerService, PaginatorData, SearchFilterDef,
    TableColumn, validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';

import { DatePipe } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDatepicker } from '@angular/material/datepicker';
import { Router } from '@angular/router';
import { faCommentAlt, faInfoCircle } from '@fortawesome/free-solid-svg-icons';

import { Classificazione } from '../../model/classificazione';
import { Ente } from '../../model/ente';
import { Segnalazione } from '../../model/segnalazione';
import { SegnalazioneSearch } from '../../model/segnalazione-search';
import { Utente } from '../../model/utente';
import { EnteService } from '../../services/ente.service';
import { RiconciliazioneService } from '../../services/riconciliazione.service';
import { SegnalazioneService } from '../../services/segnalazione.service';

@Component({
  selector: 'app-segnalazione-storico',
  templateUrl: './segnalazione-storico.component.html',
  styleUrls: ['./segnalazione-storico.component.scss']
})
export class SegnalazioneStoricoComponent implements OnInit, WithTitle {

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<Segnalazione>;
  @ViewChild('mypSearchChips') mypSearchChips: MypSearchChipsComponent;

  get titleLabel(){ return "Storico segnalazioni" }
  get titleIcon(){ return faCommentAlt }

  iconInfoCircle = faInfoCircle;

  hasSearched: boolean = false;
  blockingError: boolean = false;

  allSearchTypes: Classificazione[];

  formDef: {[key:string]:SearchFilterDef};
  form: FormGroup;
  formErrors = {};
  private formChangesSub: Subscription;
  utenti: Utente[];
  private initialSearchType: string;

  private enteChangesSub: Subscription;

  tableColumns: TableColumn[] = [
    new TableColumn('attivo', 'Attivo', { sortable: true, pipe: MapPipe, pipeArgs: [{true: 'Si', false: 'No'}] }),
    //new TableColumn('nascosto', 'Nacosto', { sortable: true }),
    new TableColumn('utente', 'Nome', { sortable: true }),
    new TableColumn('cfUtente', 'Cod. Fiscale', { sortable: true }),
    new TableColumn('dtInserimento', 'Inserito', { sortable: (item: Segnalazione) => item.dtInserimento?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy HH:mm:ss'] } ),
    new TableColumn('classificazione', 'Classificazione', { sortable: true, pipe: MapPipe, pipeArgs: [{}] }), //pipeArgs is initialized when classificazioni are loaded
    new TableColumn('iuvKey', 'IUV', { sortable: true, pipe: MapPipe, pipeArgs: [{},'n/a'] }),
    new TableColumn('iudKey', 'IUD', { sortable: true, pipe: MapPipe, pipeArgs: [{},'n/a'] }),
    new TableColumn('iufKey', 'IUF', { sortable: true, pipe: MapPipe, pipeArgs: [{},'n/a'] }),
    new TableColumn('nota', 'Nota'),
  ];
  tableData: Segnalazione[];
  paginatorData: PaginatorData;
  detailFilterExclude = ['classificazioneLabel'];

  constructor(
    private formBuilder: FormBuilder,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private enteService: EnteService,
    private segnalazioneService: SegnalazioneService,
    private riconciliazioneService: RiconciliazioneService,
    private router: Router,
  ) {

    this.formDef = [
      new SearchFilterDef('searchType', 'Classificazione', null, [], v => v?.label),
      new SearchFilterDef('iuv', 'IUV', '', []),
      new SearchFilterDef('iud', 'IUD', '', []),
      new SearchFilterDef('iuf', 'IUF', '', []),
      new SearchFilterDef('dtInseritoFrom', 'Data inserito da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dtInseritoTo', 'Data inserito a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('attivo', 'Attivo', '', [], v => v === "false" ? 'Non attivo' : (v === "true" ? 'Attivo' : null)),
      //new SearchFilterDef('nascosto', 'Nascosto', '', []),
      new SearchFilterDef('utente', 'Utente', null, [], v => v ? `${v.deFirstname} ${v.deLastname}` : null),
    ].reduce((formObj, elem) => {formObj[elem.field] = elem; return formObj}, {} );

    const formObj = _.mapValues(this.formDef, x => [
      _.isFunction(x.value) ? x.value() : x.value, 
      x.validators]);

    this.form = this.formBuilder.group(formObj, { validators: [
      DateValidators.dateRangeForRangePicker('dtInseritoFrom','dtInseritoTo'),
    ] });

    const initialState = this.router.getCurrentNavigation().extras?.state;
    if(initialState) {
      this.initialSearchType = initialState.searchType;
      this.form.get('iuv').setValue(initialState.iuv);
      this.form.get('iud').setValue(initialState.iud);
      this.form.get('iuf').setValue(initialState.iuf);
    }

    this.formChangesSub = this.form.valueChanges.subscribe(validateFormFun(this.form, this.formErrors));
  }

  ngOnInit(): void {
    this.enteChangesSub = this.enteService.getCurrentEnteObs().subscribe(value => this.onChangeEnte(this, value) );
  }

  ngOnDestroy(): void {
    this.formChangesSub?.unsubscribe();
    this.enteChangesSub?.unsubscribe();
  }

  onSubmit(){
    const formValue = this.form.value;
    const searchParams = new SegnalazioneSearch();
    searchParams.classificazione = formValue.searchType?.code;
    searchParams.utente = formValue.utente?.codFedUserId ? `${formValue.utente.deFirstname} ${formValue.utente.deLastname}` : null;
    searchParams.dtInseritoPrima = formValue.dtInseritoTo;
    searchParams.dtInseritoDopo = formValue.dtInseritoFrom;
    searchParams.iud = formValue.iud;
    searchParams.iuv = formValue.iuv;
    searchParams.iuf = formValue.iuf;
    searchParams.attivo = formValue.attivo;

    this.tableData = [];

    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);

    this.segnalazioneService.searchSegnalazioni(this.enteService.getCurrentEnte(), searchParams).subscribe(data => {
      this.hasSearched = true;
      this.tableData = data;
      //close search panel if data found
      if(data?.length > 0){
        this.mypSearchChips.setSearchPanelState(false);
      } 
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.hasSearched = true; this.overlaySpinnerService.detach(spinner)}) );
  }

  onRemoveFilter(thisRef: SegnalazioneStoricoComponent) {
    //redo the search
    if(thisRef.hasSearched)
      thisRef.onSubmit();
  }

  onReset(){
    this.form.reset();
    _.forOwn(this.formDef, (value, key) => this.form.get(key).setValue(value.value));
    this.hasSearched = false;
    this.tableData = null;
  }

  private onChangeEnte(thisRef: SegnalazioneStoricoComponent, ente:Ente){
    if(ente && ente.mygovEnteId){
      //retrieve list of searchTypes (classificazioni)
      thisRef.riconciliazioneService.getSearchTypes(ente).subscribe(classificazioni => {
        thisRef.allSearchTypes = classificazioni;
        thisRef.tableColumns.find(x => x.id === 'classificazione').pipeArgs[0] = 
            classificazioni.reduce((acc, elem) => {acc[elem.code]=elem.label; return acc;}, {});
        //manage initial value (i.e. when navigating from riconciliazioni/anomalie)
        if(thisRef.initialSearchType){
          thisRef.form.get('searchType').setValue(thisRef.allSearchTypes.find(x => x.code === thisRef.initialSearchType));
          thisRef.initialSearchType = null;
          thisRef.onSubmit();
        } else {
          thisRef.form.get('searchType').setValue(null);
        }
      }, manageError('Errore caricando l\'elenco delle classificazioni', thisRef.toastrService, ()=>{thisRef.blockingError=true}) );
      //retrieve list of utenti
      thisRef.segnalazioneService.getUtenti(ente).subscribe(utenti => {
          thisRef.utenti = utenti;
          thisRef.form.get('utente').setValue(null);
        }, manageError('Errore caricando l\'elenco degli utenti', thisRef.toastrService, ()=>{thisRef.blockingError=true}) );
    }

    // //reopen search panel if closed
    thisRef.mypSearchChips?.setSearchPanelState(true);
    //reset search state
    thisRef.hasSearched = false;
    thisRef.tableData = null;
  }

  onYearChosen(field: string, normalizedDt: DateTime, datePicker: MatDatepicker<DateTime>){
    datePicker.close();
    this.form.get(field).setValue(normalizedDt);
  }
}
