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
import * as lodash from 'lodash';
import { DateTime } from 'luxon';
import { ToastrService } from 'ngx-toastr';
import {
    MyPayTableDetailComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table-detail/my-pay-table-detail.component';
import {
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import {
    MypSearchChipsComponent
} from 'projects/mypay4-fe-common/src/lib/components/myp-search-chips/myp-search-chips.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    manageError, OverlaySpinnerService, PaginatorData, SearchFilterDef, TableAction, TableColumn,
    validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

import { animate, state, style, transition, trigger } from '@angular/animations';
import { DatePipe } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup } from '@angular/forms';
import { MatDatepicker } from '@angular/material/datepicker';
import { Router } from '@angular/router';
import { faListOl, faSearch } from '@fortawesome/free-solid-svg-icons';

import {
    PageStateService
} from '../../../../../mypay4-fe-common/src/lib/services/page-state.service';
import { AccertamentoCapitolo } from '../../model/accertamento-capitolo';
import { Ente } from '../../model/ente';
import { TipoDovuto } from '../../model/tipo-dovuto';
import { AccertamentoService } from '../../services/accertamento.service';
import { EnteService } from '../../services/ente.service';

@Component({
  selector: 'app-accertamenti-capitoli',
  templateUrl: './accertamenti-capitoli.component.html',
  styleUrls: ['./accertamenti-capitoli.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ]
})
export class AccertamentiCapitoliComponent implements OnInit, OnDestroy, WithTitle {

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<AccertamentoCapitolo>;
  @ViewChild('mypSearchChips') mypSearchChips: MypSearchChipsComponent;

  get titleLabel(){ return "Anagrafiche capitoli" }
  get titleIcon(){ return faListOl }
  
  tipoDovutoOptionsMap: Map<String, TipoDovuto[]>;
  tipoDovutoOptions: TipoDovuto[];
  tipoDovutoFilteredOptions: Observable<TipoDovuto[]>;

  hasSearched: boolean = false;
  blockingError: boolean = false;

  private enteSub: Subscription;

  ente: Ente;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private pageStateService: PageStateService,
    private enteService: EnteService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private accertamentoService: AccertamentoService,
  ) {

    this.formDef = [
      new SearchFilterDef('tipoDovuto', 'Tipo dovuto', '', [this.tipoDovutoValidator], v => v?.deTipo),
      new SearchFilterDef('codUfficio', 'Codice Ufficio', '', []),
      new SearchFilterDef('deUfficio', 'Denominazione Ufficio', '', []),
      new SearchFilterDef('flgUfficioAttivo', 'Ufficio Attivo', false, []),
      new SearchFilterDef('codCapitolo', 'Codice Capitolo', '', []),
      new SearchFilterDef('deCapitolo', 'Denominazione Capitolo', '', []),
      new SearchFilterDef('annoCapitolo', 'Anno Esercizio Capitolo', null, [], v => v?.toFormat('yyyy')),
      new SearchFilterDef('codAccertamento', 'Codice Accertamento', '', []),
      new SearchFilterDef('deAccertamento', 'Denominazione Accertamento', '', []),
    ].reduce((formObj, elem) => {formObj[elem.field] = elem; return formObj}, {} );

    const formObj = lodash.mapValues(this.formDef, x => [
      lodash.isFunction(x.value) ? x.value() : x.value, 
      x.validators]);

    this.form = this.formBuilder.group(formObj);

    this.formChangesSub = this.form.valueChanges.subscribe(validateFormFun(this.form, this.formErrors));
  }

  ngOnDestroy(): void {
    this.enteSub?.unsubscribe();
    this.formChangesSub?.unsubscribe();
  }

  ngOnInit(): void {
    this.tipoDovutoOptionsMap = new Map();

    this.enteSub = this.enteService.getCurrentEnteObs().subscribe(ente => {
      this.tableData = new Array();
      this.ente = ente;
      this.onReset(ente);
    });

    //retrieve page state data if navigating back for 'accertamenti'
    if(this.pageStateService.isNavigatingBack()){
      const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
      const pageState = this.pageStateService.restoreState();
      if(pageState){
        this.tipoDovutoOptions = pageState.tipoDovutoOptions;
        this.tipoDovutoOptionsMap = pageState.tipoDovutoOptionsMap;
        this.tipoDovutoFilteredOptions = pageState.tipoDovutoFilteredOptions;
        this.form.setValue(pageState.formData);
        setTimeout(()=>{
          if(pageState.reloadData){
            this.onSubmit();
          } else {
            this.tableData = pageState.tableData;
            this.paginatorData = pageState.paginatorData;
            if(pageState.tableData?.length > 0)
              this.mypSearchChips.setSearchPanelState(false);
          }
        });
      }
      this.overlaySpinnerService.detach(spinner);
    }
  }

  formDef: { [key: string]: SearchFilterDef };
  form: FormGroup;
  formErrors = {};
  private formChangesSub: Subscription;

  tableColumns = [
    new TableColumn('deTipoDovuto', 'Tipo Dovuto', { sortable: true }),
    new TableColumn('codUfficio', 'Codice Ufficio', { sortable: true }),
    new TableColumn('codCapitolo', 'Codice Capitolo', { sortable: true }),
    new TableColumn('codAccertamento', 'Codice Accertamento', { sortable: true }),
    new TableColumn('dtUltimaModifica', 'Data ultimo agg.', { sortable: true,  pipe: DatePipe, pipeArgs: ['dd/MM/yyyy']}),
    new TableColumn('rowActions', 'Azioni', { sortable: false, tooltip: 'Azioni', actions: [
      new TableAction(faSearch, this.gotoDetail, null, 'Dettaglio'),
      ] } )
  ];
  tableData: AccertamentoCapitolo[];
  paginatorData: PaginatorData;

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

  private gotoDetail(elementRef: AccertamentoCapitolo, thisRef: AccertamentiCapitoliComponent, eventRef: any) {
    if(eventRef)
      eventRef.stopPropagation();
    thisRef.pageStateService.saveState({
      formData: thisRef.form.value,
      tableData: thisRef.tableData,
      tipoDovutoOptionsMap: thisRef.tipoDovutoOptionsMap,
      tipoDovutoOptions: thisRef.tipoDovutoOptions,
      tipoDovutoFilteredOptions: thisRef.tipoDovutoFilteredOptions,
      paginatorData: {
        pageSize: thisRef.mypayTableComponent.paginator.pageSize,
        pageIndex: thisRef.mypayTableComponent.paginator.pageIndex
      }
    });
    thisRef.router.navigate(['accertamenti', 'capitoli', 'anagrafica', elementRef.id]);
  }

  gotoImportMassivo() {
    this.pageStateService.saveState({
      formData: this.form.value,
      tableData: this.tableData,
      tipoDovutoOptionsMap: this.tipoDovutoOptionsMap,
      tipoDovutoOptions: this.tipoDovutoOptions,
      tipoDovutoFilteredOptions: this.tipoDovutoFilteredOptions,
      paginatorData: {
        pageSize: this.mypayTableComponent.paginator.pageSize,
        pageIndex: this.mypayTableComponent.paginator.pageIndex
      }
    });
    this.router.navigate(['accertamenti', 'capitoli', 'importMassivo']);
  }

  gotoAnagrafica() {
    this.pageStateService.saveState({
      formData: this.form.value,
      tableData: this.tableData,
      tipoDovutoOptionsMap: this.tipoDovutoOptionsMap,
      tipoDovutoOptions: this.tipoDovutoOptions,
      tipoDovutoFilteredOptions: this.tipoDovutoFilteredOptions,
      paginatorData: {
        pageSize: this.mypayTableComponent.paginator.pageSize,
        pageIndex: this.mypayTableComponent.paginator.pageIndex
      }
    });
    this.router.navigate(['accertamenti', 'capitoli', 'anagrafica']);
  }

  onYearChosen(field: string, normalizedDt: DateTime, datePicker: MatDatepicker<DateTime>){
    datePicker.close();
    this.form.get(field).setValue(normalizedDt);
  }

  // Ente in param is valued when changed newly.
  onReset(ente?: Ente) {
    this.form.reset();
    if(ente && ente.mygovEnteId){
      //retrieve list of tipoDovuto and prepare autocomplete
      this.form.controls['tipoDovuto'].setValue(null);
      if(!this.tipoDovutoOptionsMap.has(ente.codIpaEnte)){
        this.enteService.getListTipoDovutoByEnteAsOperatore(ente).subscribe(tipiDovuto => {
          if (this.ente.codIpaEnte === 'R_VENETO') {
            let na = new TipoDovuto();
            na.mygovEnteTipoDovutoId = 0;
            na.codIpaEnte = this.ente.codIpaEnte;
            na.codTipo = 'n/a';
            na.deTipo = 'N/A';
            tipiDovuto = [na].concat(tipiDovuto);
          }
          this.tipoDovutoOptionsMap.set(ente.codIpaEnte, tipiDovuto);
          this.tipoDovutoOptions = this.tipoDovutoOptionsMap.get(ente.codIpaEnte);
          this.tipoDovutoFilteredOptions = this.form.get('tipoDovuto').valueChanges
          .pipe(
            startWith(''),
            map(value => typeof value === 'string' || !value ? value : value.deTipo),
            map(deTipoDovuto => deTipoDovuto ? this._tipoDovutoFilter(deTipoDovuto) : this.tipoDovutoOptions.slice())
          );
        }, manageError('Errore caricando l\'elenco dei tipi dovuto', this.toastrService, ()=>{this.blockingError=true}) );
      } else {
        this.tipoDovutoOptions = this.tipoDovutoOptionsMap.get(ente.codIpaEnte);
        this.tipoDovutoFilteredOptions = this.form.get('tipoDovuto').valueChanges
        .pipe(
          startWith(''),
          map(value => typeof value === 'string' || !value ? value : value.deTipo),
          map(deTipoDovuto => deTipoDovuto ? this._tipoDovutoFilter(deTipoDovuto) : this.tipoDovutoOptions.slice())
        );
      }
    } else {
      this.tipoDovutoOptions = [];
    }
  }

  onSubmit(){
    const i = this.form.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let tipoDovuto = this.form.get('tipoDovuto').value as TipoDovuto;
    let annoCapitolo = i.annoCapitolo?.toFormat('yyyy') || null;
    this.accertamentoService.getAccertamentiCapitoli(this.ente, tipoDovuto, i.codUfficio, i.deUfficio, i.flgUfficioAttivo, i.codCapitolo,
        i.deCapitolo, annoCapitolo, i.codAccertamento, i.deAccertamento).subscribe(data => {
        this.hasSearched = true;
        this.tableData = data;
        if (data?.length > 0)
          this.mypSearchChips.setSearchPanelState(false);
        this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }
  
  onRemoveFilter(thisRef: AccertamentiCapitoliComponent) {
    //redo the search
    if(thisRef.hasSearched)
      thisRef.onSubmit();
  }

  onClickRow(element:AccertamentoCapitolo, thisRef: AccertamentiCapitoliComponent) {
    if(!element.details)
      element.details = [
        {key:MyPayTableDetailComponent.SECTION_ID, value:'UFFICIO'},
        {key:'Codice', value: element.codUfficio},
        {key:'Denominazione', value: element.deUfficio},
        {key:'Ufficio Attivo', value: `${element.flgAttivo ? "SI" : "NO"}`},
        {key:MyPayTableDetailComponent.SECTION_ID, value:'CAPITOLO'},
        {key:'Codice', value: element.codCapitolo},
        {key:'Denominazione', value: element.deCapitolo},
        {key:'Anno Esercizio', value: element.deAnnoEsercizio},
        {key:MyPayTableDetailComponent.SECTION_ID, value:'ACCERTAMENTO'},
        {key:'Codice', value: element.codAccertamento},
        {key:'Denominazione', value: element.deAccertamento},
      ];
 }

}
