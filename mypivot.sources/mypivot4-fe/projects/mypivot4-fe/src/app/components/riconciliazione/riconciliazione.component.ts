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
    MyPayTableDetailComponent, UpdateDetailFun
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table-detail/my-pay-table-detail.component';
import {
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import {
    MypSearchChipsComponent
} from 'projects/mypay4-fe-common/src/lib/components/myp-search-chips/myp-search-chips.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    ApiInvokerService, ConfigurationService, DateValidators, manageError, OverlaySpinnerService,
    PageStateService, PaginatorData, PATTERNS, SearchFilterDef, TableAction, TableColumn,
    validateFormFun, WithActions
} from 'projects/mypay4-fe-common/src/public-api';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDatepicker } from '@angular/material/datepicker';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { faStickyNote } from '@fortawesome/free-regular-svg-icons';
import { faInfoCircle, faLink, faSearch } from '@fortawesome/free-solid-svg-icons';

import { Classificazione } from '../../model/classificazione';
import { Ente } from '../../model/ente';
import { Riconciliazione } from '../../model/riconciliazione';
import { Segnalazione } from '../../model/segnalazione';
import { TipoDovuto } from '../../model/tipo-dovuto';
import { EnteService } from '../../services/ente.service';
import {
    RiconciliazioneService, VisualizzazioneType
} from '../../services/riconciliazione.service';
import { SegnalazioneAddComponent } from '../segnalazione-add/segnalazione-add.component';

@Component({
  selector: 'app-riconciliazione',
  templateUrl: './riconciliazione.component.html',
  styleUrls: ['./riconciliazione.component.scss']
})
export class RiconciliazioneComponent implements OnInit, OnDestroy, WithTitle {

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<Riconciliazione>;
  @ViewChild('mypSearchChips') mypSearchChips: MypSearchChipsComponent;

  get titleLabel(){ return this.title }
  get titleIcon(){ return faLink }

  iconInfoCircle = faInfoCircle;

  viewType: VisualizzazioneType;
  private title: string;
  helpTemplate: string;
  hasSearched: boolean = false;
  blockingError: boolean = false;
  lastSearchFormData: any;
  hasDetail: boolean = true;

  allSearchTypes: Classificazione[];
  lastSearchSearchType: Classificazione;
  versioniTracciato: string[];

  formDef: {[key:string]:SearchFilterDef};
  form: FormGroup;
  formErrors = {};
  private formChangesSub:Subscription;

  tipoDovutoOptionsMap: Map<String, TipoDovuto[]>;
  tipoDovutoOptions: TipoDovuto[];
  tipoDovutoFilteredOptions: Observable<TipoDovuto[]>;

  private enteChangesSub: Subscription;
  private searchTypeChangesSub: Subscription;

  private exportMaxRecords: number;
  exportEnabled = false;

  tableColumns: TableColumn[] = [
    new TableColumn('deTipoDovuto', 'Tipo dovuto', {dispCondition: this.showField}),
    new TableColumn('dataUltimoAgg', 'Data ultimo aggiorn.', { dispCondition: this.showField, sortable: (item: Riconciliazione) => item.dataUltimoAgg?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
    //notifica
    new TableColumn('iuv', 'IUV', {dispCondition: this.showField}),
    new TableColumn('iud', 'IUD', {dispCondition: this.showField}),
    new TableColumn('iur', 'IUR', {dispCondition: this.showField}),
    new TableColumn('importo', 'Importo', { dispCondition: this.showField, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'] } ),
    new TableColumn('dataEsecuzione', 'Data esecuzione', { dispCondition: this.showField, sortable: (item: Riconciliazione) => item.dataEsecuzione?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
    new TableColumn('pagatoreCodFisc', 'Pagatore', {dispCondition: this.showField}),
    new TableColumn('datiSpecificiRiscossione', 'Dati specifici riscossione', {dispCondition: this.showField}),
    //ricevuta telematica
    new TableColumn('attestanteAnagrafica', 'Attestante', {dispCondition: this.showField}),
    new TableColumn('dataEsito', 'Data esito', { dispCondition: this.showField, sortable: (item: Riconciliazione) => item.dataEsito?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
    //rendicontazione
    new TableColumn('idRendicontazione', 'ID Rendicontazione', {dispCondition: this.showField}),
    new TableColumn('idRegolamento', 'ID regolamento', {dispCondition: this.showField}),
    new TableColumn('dataRegolamento', 'Data regolamento', { dispCondition: this.showField, sortable: (item: Riconciliazione) => item.dataRegolamento?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy HH:mm:ss'] } ),
    new TableColumn('dataFlusso', 'Data flusso', { dispCondition: this.showField, sortable: (item: Riconciliazione) => item.dataFlusso?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy HH:mm:ss'] } ),
    new TableColumn('importoTotale', 'Importo totale', { dispCondition: this.showField, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'] }),
    //sospeso - giornale di cassa
    new TableColumn('importoTesoreria', 'Importo tesoreria', { dispCondition: this.showField, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'] }),
    new TableColumn('conto', 'Conto', {dispCondition: this.showField}),
    new TableColumn('dataValuta', 'Data valuta', { dispCondition: this.showField, sortable: (item: Riconciliazione) => item.dataValuta?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
    new TableColumn('dataContabile', 'Data contabile', { dispCondition: this.showField, sortable: (item: Riconciliazione) => item.dataContabile?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
    new TableColumn('ordinante', 'Ordinante', {dispCondition: this.showField}),
    new TableColumn('iufKey', 'Id riversamento', {dispCondition: this.showField}),
    new TableColumn('annoBolletta', 'Anno bolletta', {dispCondition: this.showField}),
    new TableColumn('codiceBolletta', 'Codice bolletta', {dispCondition: this.showField}),
    new TableColumn('causaleRiversamento', 'Causale riversamento', {dispCondition: this.showField}),
    new TableColumn('rowActions', 'Azioni', { dispCondition:this.showActions, sortable: false, tooltip: 'Azioni', actions: [
      new TableAction(faSearch, this.gotoSegnalazioni, this.gotoSegnalazioniEnabled, 'Storico segnalazioni'),
      new TableAction(faSearch, this.gotoDetails, this.gotoDetailsEnabled, 'Visualizza dettaglio'),
      new TableAction(faStickyNote, this.addSegnalazione, this.gotoDetailsEnabled, 'Aggiungi segnalazione'),
    ] } ) ];
  tableData: Riconciliazione[];
  paginatorData: PaginatorData;
  detailFilterExclude = ['classificazioneLabel'];

  constructor(
    private formBuilder: FormBuilder,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private router: Router,
    private pageStateService: PageStateService,
    private enteService: EnteService,
    private riconciliazioneService: RiconciliazioneService,
    private matDialog: MatDialog,
    private apiInvokationService: ApiInvokerService,
    conf: ConfigurationService,
    route: ActivatedRoute,
  ) {
    this.exportMaxRecords = conf.getBackendProperty('exportMaxRecords');

    this.viewType = route.snapshot.data['type'];
    switch(this.viewType){
      case 'A': this.title = 'Anomalie'; break;
      case 'R': this.title = 'Riconciliazioni'; break;
      default: throw new Error('invalid type: '+this.viewType);
    }

    this.formDef = [
      new SearchFilterDef('searchType', 'Classificazione', this.firstValidSearchType, [Validators.required], v => v?.label),

      new SearchFilterDef('dateEsecuzioneFrom', 'Data esecuzione da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateEsecuzioneTo', 'Data esecuzione a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateEsitoFrom', 'Data esito da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateEsitoTo', 'Data esito a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateUltModFrom', 'Data ultimo aggiornamento da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateUltModTo', 'Data ultimo aggiornamento a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateRegolFrom', 'Data regolamento da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateRegolTo', 'Data regolamento a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateContabFrom', 'Data contabile da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateContabTo', 'Data contabile a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateValutaFrom', 'Data valuta da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateValutaTo', 'Data valuta a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('iud', 'IUD', '', []),
      new SearchFilterDef('iuv', 'IUV', '', []),
      new SearchFilterDef('iur', 'IUR', '', []),
      new SearchFilterDef('codFiscalePagatore', 'CF/PIVA pagatore', '', [Validators.pattern(PATTERNS.codiceFiscaleOPartitaIva)]),
      new SearchFilterDef('anagPagatore', 'Anagrafica pagatore', '', []),
      new SearchFilterDef('codFiscaleVersante', 'CF/PIVA versante', '', [Validators.pattern(PATTERNS.codiceFiscaleOPartitaIva)]),
      new SearchFilterDef('anagVersante', 'Anagrafica versante', '', []),
      new SearchFilterDef('attestante', 'Attestante', '', []),
      new SearchFilterDef('ordinante', 'Ordinante', '', []),
      new SearchFilterDef('idRendicont', 'ID rendicontazione', '', []),
      new SearchFilterDef('idRegolamento', 'ID regolamento', '', []),
      new SearchFilterDef('tipoDovuto', 'Tipo dovuto', '', [this.tipoDovutoValidator], v => v?.deTipo),
      new SearchFilterDef('conto', 'Conto', '', []),
      new SearchFilterDef('importoTesoreria', 'Importo tesoreria', null, [Validators.pattern(PATTERNS.importo)]),
      new SearchFilterDef('causale', 'Causale', '', []),
      new SearchFilterDef('annoBolletta', 'Anno bolletta', null, [], v => v?.toFormat('yyyy')),
      new SearchFilterDef('codBolletta', 'Codice bolletta', '', []),
      new SearchFilterDef('annoDocumento', 'Anno codice documento', null, [], v => v?.toFormat('yyyy')),
      new SearchFilterDef('codDocumento', 'Codice documento', '', []),
      new SearchFilterDef('annoProvvisorio', 'Anno codice provvisorio', null, [], v => v?.toFormat('yyyy')),
      new SearchFilterDef('codProvvisorio', 'Codice provvisorio', '', []),
    ].reduce((formObj, elem) => {formObj[elem.field] = elem; return formObj}, {} );

    const formObj = _.mapValues(this.formDef, x => [
      _.isFunction(x.value) ? x.value() : x.value, 
      x.validators]);

    //init versioneTracciato
    //this.versioniTracciato = this.riconciliazioneService.getVersioniTracciato(formObj.searchType[0].code);
    formObj['versioneTracciato'] = [null, []];

    this.form = this.formBuilder.group(formObj, { validators: [
      DateValidators.dateRangeForRangePicker('dateEsecuzioneFrom','dateEsecuzioneTo'),
      DateValidators.dateRangeForRangePicker('dateEsitoFrom','dateEsitoTo'),
      DateValidators.dateRangeForRangePicker('dateUltModFrom','dateUltModTo'),
      DateValidators.dateRangeForRangePicker('dateRegolFrom','dateRegolTo'),
      DateValidators.dateRangeForRangePicker('dateContabFrom','dateContabTo'),
      DateValidators.dateRangeForRangePicker('dateValutaFrom','dateValutaTo'),
    ] });

    this.formChangesSub = this.form.valueChanges.subscribe(validateFormFun(this.form, this.formErrors));
  }

  ngOnInit(): void {
    this.tipoDovutoOptionsMap = new Map();
    this.enteChangesSub = this.enteService.getCurrentEnteObs().subscribe(value => this.onChangeEnte(this, value) );
    
    //change enabled form fields based on searchType
    this.searchTypeChangesSub = this.form.get('searchType').valueChanges.subscribe(searchType => {
      Object.keys(this.form.controls).forEach(field => {
        if(this.form.get(field).disabled)
          this.form.get(field).enable();
      });
      this.riconciliazioneService.getDisabledFiltersBySearchType(searchType?.code).forEach(field => {
        if(this.form.get(field).enabled)
          this.form.get(field).disable();
      });
      this.versioniTracciato = searchType?.exportVersions || [];
      this.form.get('versioneTracciato').setValue(this.versioniTracciato[0]);
    });

    //retrieve page state data if navigating back
    if(this.pageStateService.isNavigatingBack()){
      const pageState = this.pageStateService.restoreState();
      if(pageState){
        this.lastSearchSearchType = pageState.currentSearchType;
        this.form.setValue(_.assign(this.form.value, pageState.formData));
        this.exportEnabled = pageState.exportEnabled;
        setTimeout(()=>{
          this.tableData = pageState.tableData;
          this.paginatorData = pageState.paginatorData;
          if(this.tableData){
            this.hasSearched = true;
            this.mypSearchChips.setSearchPanelState(false);
            const elem = pageState.riconciliazioneToReload;
            if(elem){
              const idx = this.tableData.findIndex(e => e.iufKey===elem.iufKey && e.iudKey===elem.iudKey && e.iuvKey===elem.iuvKey);
              if(idx>-1)
                this.tableData[idx] = elem;
            }
          }
        });
      }
    }
  }

  ngOnDestroy(): void {
    this.formChangesSub?.unsubscribe();
    this.enteChangesSub?.unsubscribe();
    this.searchTypeChangesSub?.unsubscribe();
  }

  onSubmit(){
    const searchParams = _.clone(this.form.value);
    const searchType = searchParams.searchType;
    searchParams.tipoDovuto = searchParams.tipoDovuto?.codTipo;

    this.lastSearchFormData = this.form.value;
    this.lastSearchSearchType = searchType;
    this.tableData = [];

    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);

    this.riconciliazioneService.search(this.enteService.getCurrentEnte(), this.lastSearchSearchType.code, searchParams).subscribe(data => {
      //set table structure based on classificazione
      this.hasDetail = this.lastSearchSearchType.code !== 'TES_NO_MATCH';
      this.mypayTableComponent.changeColumnsToShow();
      
      //data.filter(row => row.classificazione === 'TES_NO_MATCH').forEach(row => MyPayTableDetailComponent.markNoDetail(row));
      this.hasSearched = true;
      this.tableData = data;
      //close search panel if data found
      let totalFoundElements;
      if(data?.length > 0){
        this.mypSearchChips.setSearchPanelState(false);
        totalFoundElements = this.apiInvokationService.getTotalCount(data) || data.length;
        console.log('total found: '+totalFoundElements);
      } else {
        totalFoundElements = 0;
      }
      this.exportEnabled = totalFoundElements > 0 && totalFoundElements < this.exportMaxRecords;
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.hasSearched = true; this.overlaySpinnerService.detach(spinner)}) );
  }

  onExport(){
    const searchParams = _.clone(this.lastSearchFormData);
    const versioneTracciato = this.form.get('versioneTracciato').value;
    searchParams.tipoDovuto = searchParams.tipoDovuto?.codTipo;

    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);

    this.riconciliazioneService.export(this.enteService.getCurrentEnte(), this.lastSearchSearchType.code, versioneTracciato, searchParams)
    .subscribe(requestToken => {
      console.log('export token ok, token: '+requestToken);
      this.overlaySpinnerService.detach(spinner);
      this.toastrService.success('Export prenotato correttamente (token: '+requestToken+')');
    }, manageError('Errore prenotando l\'export', this.toastrService, () => {this.hasSearched = true; this.overlaySpinnerService.detach(spinner)}) );
 
  }

  gotoDetailsEnabled(elementRef: Riconciliazione, thisRef: RiconciliazioneComponent){
    return elementRef.classificazione && elementRef.classificazione !== 'TES_NO_MATCH';
  }

  gotoDetails(elementRef: Riconciliazione, thisRef: RiconciliazioneComponent, eventRef: any){
    if(eventRef)
      eventRef.stopPropagation();
    MyPayTableDetailComponent.close(thisRef.matDialog);
    //remove detail from cache (it may change on detail page)
    elementRef.details = null;

    const navId = thisRef.pageStateService.saveState({
      formData: thisRef.lastSearchFormData,
      tableData: thisRef.tableData,
      currentSearchType: thisRef.lastSearchSearchType,
      exportEnabled: thisRef.exportEnabled,
      paginatorData: {
        pageSize: thisRef.mypayTableComponent.paginator.pageSize,
        pageIndex: thisRef.mypayTableComponent.paginator.pageIndex
      }
    });
    thisRef.router.navigate(['visualizzazione','riconciliazioni',elementRef.classificazione,elementRef.iufKey || 'null', elementRef.iudKey || 'null', elementRef.iuvKey || 'null'], {state:{backNavId:navId}});
  }

  gotoSegnalazioniEnabled(elementRef: Riconciliazione, thisRef: RiconciliazioneComponent){
    return elementRef.hasSegnalazione;
  }

  gotoSegnalazioni(elementRef: Riconciliazione, thisRef: RiconciliazioneComponent, eventRef: any){
    if(eventRef)
      eventRef.stopPropagation();
    MyPayTableDetailComponent.close(thisRef.matDialog);

    thisRef.pageStateService.saveState({
      formData: thisRef.lastSearchFormData,
      tableData: thisRef.tableData,
      currentSearchType: thisRef.lastSearchSearchType,
      exportEnabled: thisRef.exportEnabled,
      paginatorData: {
        pageSize: thisRef.mypayTableComponent.paginator.pageSize,
        pageIndex: thisRef.mypayTableComponent.paginator.pageIndex
      }
    });
    thisRef.router.navigate(['visualizzazione','segnalazione','storico'],{ state: {
      searchType: elementRef.classificazione,
      iuf: elementRef.iufKey,
      iud: elementRef.iudKey,
      iuv: elementRef.iuvKey
    } });
  }

  addSegnalazione(elementRef: Riconciliazione, thisRef: RiconciliazioneComponent, eventRef: any){
    if(eventRef)
      eventRef.stopPropagation();
    //open detail panel
    thisRef.matDialog.open(SegnalazioneAddComponent, {
      panelClass: 'add-segnalazione-panel', autoFocus:false, 
    id: SegnalazioneAddComponent.DIALOG_ID,
    data: {
      classificazione: elementRef.classificazione,
      iufKey: elementRef.iufKey || null,
      iudKey: elementRef.iudKey || null,
      iuvKey: elementRef.iuvKey || null,
      updateDetailsFun: (updateFun:UpdateDetailFun)=>updateFun(elementRef.details) ,
      callbackFun: (segnalazione:Segnalazione)=>{
        elementRef.hasSegnalazione=true;
        WithActions.reset(elementRef);
      }
    } } );
  }
  
  onRemoveFilter(thisRef: RiconciliazioneComponent) {
    //redo the search
    if(thisRef.hasSearched)
      thisRef.onSubmit();
  }

  onReset(){
    this.form.reset();
    _.forOwn(this.formDef, (value, key) => this.form.get(key).setValue(value.value));
    this.hasSearched = false;
    this.tableData = null;
    this.exportEnabled = false;
  }

  private onChangeEnte(thisRef: RiconciliazioneComponent, ente:Ente){
    if(ente && ente.mygovEnteId){
      //retrieve list of searchTypes (classificazioni)
      thisRef.riconciliazioneService.getSearchTypes(ente, this.viewType).subscribe(searchTypes => {
        thisRef.allSearchTypes = searchTypes;
        const searchTypeField = thisRef.form.get('searchType');
        const currentlySelectedSearchType = thisRef.allSearchTypes.find(x => x.code == searchTypeField.value?.code);
        searchTypeField.setValue(currentlySelectedSearchType ||thisRef.firstValidSearchType());
       }, manageError('Errore caricando l\'elenco delle classificazioni', thisRef.toastrService, ()=>{thisRef.blockingError=true}) );
      //retrieve list of tipoDovuto and prepare autocomplete
      thisRef.form.controls['tipoDovuto'].setValue(null);
      if(!thisRef.tipoDovutoOptionsMap.has(ente.codIpaEnte)){
        thisRef.enteService.getListTipoDovutoByEnteAsOperatore(ente).subscribe(tipiDovuto => {
          thisRef.tipoDovutoOptionsMap.set(ente.codIpaEnte, tipiDovuto);
          thisRef.tipoDovutoOptions = thisRef.tipoDovutoOptionsMap.get(ente.codIpaEnte);
          thisRef.tipoDovutoFilteredOptions = thisRef.form.get('tipoDovuto').valueChanges
          .pipe(
            startWith(''),
            map(value => typeof value === 'string' || !value ? value : value.deTipo),
            map(deTipoDovuto => deTipoDovuto ? thisRef._tipoDovutoFilter(deTipoDovuto) : thisRef.tipoDovutoOptions.slice())
          );
        }, manageError('Errore caricando l\'elenco dei tipi dovuto', thisRef.toastrService, ()=>{thisRef.blockingError=true}) );
      } else {
        thisRef.tipoDovutoOptions = thisRef.tipoDovutoOptionsMap.get(ente.codIpaEnte);
        thisRef.tipoDovutoFilteredOptions = thisRef.form.get('tipoDovuto').valueChanges
        .pipe(
          startWith(''),
          map(value => typeof value === 'string' || !value ? value : value.deTipo),
          map(deTipoDovuto => deTipoDovuto ? thisRef._tipoDovutoFilter(deTipoDovuto) : thisRef.tipoDovutoOptions.slice())
        );
      }
    }

    // //reopen search panel if closed
    thisRef.mypSearchChips?.setSearchPanelState(true);
    //reset search state
    thisRef.hasSearched = false;
    thisRef.tableData = null;
    thisRef.exportEnabled = false;
  }

  onYearChosen(field: string, normalizedDt: DateTime, datePicker: MatDatepicker<DateTime>){
    datePicker.close();
    this.form.get(field).setValue(normalizedDt);
  }

  tipoDovutoDisplayFn(tipoDovuto: TipoDovuto): string {
    return tipoDovuto ? tipoDovuto.deTipo : '';
  }

  private _tipoDovutoFilter(name: string): TipoDovuto[] {
    const filterValue = name.toLowerCase();
    return this.tipoDovutoOptions.filter(option => option.deTipo.toLowerCase().indexOf(filterValue) !== -1);
  }

  private tipoDovutoValidator = (control: AbstractControl):{[key: string]: boolean} | null => {
    return ( !control.value || control.value.mygovEnteTipoDovutoId != null ) ? null : {'invalid': true};
  };

  private firstValidSearchType(){
    return this.allSearchTypes?.length > 0 ? this.allSearchTypes[0] : null;
  }

  onClickRow(elementRef:Riconciliazione, thisRef: RiconciliazioneComponent): Observable<void> {
    if(elementRef.details)
      return;

    return thisRef.riconciliazioneService.detail(thisRef.enteService.getCurrentEnte(), elementRef.classificazione, 
      elementRef.iufKey,elementRef.iudKey,elementRef.iuvKey)
      .pipe(map(detail => {
        elementRef.details = new BehaviorSubject(thisRef.riconciliazioneService.mapDetailToKeyValueSections(detail));
        //to manage cases when segnalazione was added after initial load of elementRef
        elementRef.hasSegnalazione = detail.hasSegnalazione;
      }));
  }

  private showField(thisRef: RiconciliazioneComponent, column: TableColumn): boolean {
    return thisRef.lastSearchSearchType?.fields.includes(column.id);
  }


  private showActions(thisRef: RiconciliazioneComponent, column: TableColumn): boolean {
    return thisRef.lastSearchSearchType?.code !== 'TES_NO_MATCH';
  }

}
