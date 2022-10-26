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
    ConfirmDialogComponent
} from 'projects/mypay4-fe-common/src/lib/components/confirm-dialog/confirm-dialog.component';
import {
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import {
    MypSearchChipsComponent
} from 'projects/mypay4-fe-common/src/lib/components/myp-search-chips/myp-search-chips.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    DateValidators, manageError, OverlaySpinnerService, PageStateService, PaginatorData,
    SearchFilterDef, TableAction, TableColumn, validateFormFun, WithActions
} from 'projects/mypay4-fe-common/src/public-api';
import { Observable, Subscription } from 'rxjs';
import { first, map, startWith } from 'rxjs/operators';

import { DatePipe } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { faBriefcase, faSearch } from '@fortawesome/free-solid-svg-icons';

import { Accertamento } from '../../model/accertamento';
import { AnagraficaStato } from '../../model/anagrafica-stato';
import { Ente } from '../../model/ente';
import { TipoDovuto } from '../../model/tipo-dovuto';
import { AccertamentoService } from '../../services/accertamento.service';
import { EnteService } from '../../services/ente.service';

@Component({
  selector: 'app-accertamenti',
  templateUrl: './accertamenti.component.html',
  styleUrls: ['./accertamenti.component.scss']
})
export class AccertamentiComponent implements OnInit, WithTitle, OnDestroy {

  @ViewChild('sForm') formDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<Accertamento>;
  @ViewChild('mypSearchChips') mypSearchChips: MypSearchChipsComponent;

  get titleLabel(){ return "Gestione accertamenti" }
  get titleIcon(){ return faBriefcase }
  
  tipoDovutoOptionsMap: Map<String, TipoDovuto[]>;
  tipoDovutoOptions: TipoDovuto[];
  tipoDovutoFilteredOptions: Observable<TipoDovuto[]>;
  anagraficaStati: AnagraficaStato[];

  hasSearched: boolean = false;
  blockingError: boolean = false;

  private valueChangedSub: Subscription;
  private enteChangedSub: Subscription;

  ente: Ente;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private enteService: EnteService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private accertamentoService: AccertamentoService,
    private pageStateService: PageStateService,
    private dialog: MatDialog,
  ) {

    this.formDef = [
      new SearchFilterDef('deNomeAccertamento', 'Nome Accertamento', '', []),
      new SearchFilterDef('dateFrom', 'Data Ultimo Agg. da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateTo', 'Data Ultimo Agg. a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('iuv', 'IUV', '', []),
      new SearchFilterDef('tipoDovuto', 'Tipo dovuto', '', [this.tipoDovutoValidator], v => v?.deTipo),
      new SearchFilterDef('codStato', 'Stato', null, [], v => v?.codStato ),
    ].reduce((formObj, elem) => {formObj[elem.field] = elem; return formObj}, {} );

    const formObj = lodash.mapValues(this.formDef, x => [
      lodash.isFunction(x.value) ? x.value() : x.value, 
      x.validators]);

    this.form = this.formBuilder.group(formObj, { validators: [
      DateValidators.dateRangeForRangePicker('dateFrom','dateTo')
    ] });

    this.formChangesSub = this.form.valueChanges.subscribe(validateFormFun(this.form, this.formErrors));
  }

  ngOnDestroy(): void {
    this.valueChangedSub?.unsubscribe();
    this.formChangesSub?.unsubscribe();
    this.enteChangedSub?.unsubscribe();
  }

  ngOnInit(): void {

    this.tipoDovutoOptionsMap = new Map();

    this.enteChangedSub = this.enteService.getCurrentEnteObs().subscribe(ente => {
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
        this.anagraficaStati = pageState.anagraficaStati;
        this.form.setValue(pageState.formData);
        setTimeout(()=>{
          this.onSubmit();
        });
      }
      this.overlaySpinnerService.detach(spinner);
    } else {
      this.accertamentoService.getStati().subscribe(anagraficaStati => {
        let allStati = new AnagraficaStato();
        allStati.codStato = 'TUTTI';
        allStati.deStato = 'TUTTI';
        this.anagraficaStati = [allStati].concat(anagraficaStati);
        this.form.get('codStato').setValue(allStati);
      });
    }
  }

  formDef: { [key: string]: SearchFilterDef };
  form: FormGroup;
  formErrors = {};
  private formChangesSub: Subscription;

  minDate = DateTime.fromFormat('01/01/1970','dd/LL/yyyy');
  maxDate = DateTime.now().endOf('day');

  tableColumns = [
    new TableColumn('deNomeAccertamento', 'Nome Accertamento', { sortable: true }),
    new TableColumn('deTipoDovuto', 'Tipo Dovuto', { sortable: true }),
    new TableColumn('deStato', 'Stato', { sortable: true }),
    new TableColumn('creatore', 'Creato da', { sortable: true }),
    new TableColumn('dtUltimaModifica', 'Aggiornato il', { sortable: true,  pipe: DatePipe, pipeArgs: ['dd/MM/yyyy HH:mm:ss']}),
    new TableColumn('rowActions', 'Azioni', { sortable: false, tooltip: 'Azioni', actions: [
      new TableAction(faSearch, this.gotoDetail, null, 'Dettaglio'),
      new TableAction(faSearch, this.chiudi, this.isStatoInserito, 'Chiudi'),
      new TableAction(faSearch, this.annulla, this.isStatoInserito, 'Annulla'),
      ] } )
  ];
  tableData: Accertamento[];
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

  private gotoDetail(elementRef: Accertamento, thisRef: AccertamentiComponent, eventRef: any) {
    if(eventRef)
      eventRef.stopPropagation();
    thisRef.pageStateService.saveState({
      formData: thisRef.form.value,
      tableData: thisRef.tableData,
      anagraficaStati: thisRef.anagraficaStati,
      tipoDovutoOptionsMap: thisRef.tipoDovutoOptionsMap,
      tipoDovutoOptions: thisRef.tipoDovutoOptions,
      tipoDovutoFilteredOptions: thisRef.tipoDovutoFilteredOptions,
      paginatorData: {
        pageSize: thisRef.mypayTableComponent.paginator.pageSize,
        pageIndex: thisRef.mypayTableComponent.paginator.pageIndex
      }
    });
    thisRef.router.navigate(['accertamenti', 'dettaglio', elementRef.id]);
  }

  gotoAnagrafica() {
    this.pageStateService.saveState({
      formData: this.form.value,
      tableData: this.tableData,
      anagraficaStati: this.anagraficaStati,
      tipoDovutoOptionsMap: this.tipoDovutoOptionsMap,
      tipoDovutoOptions: this.tipoDovutoOptions,
      tipoDovutoFilteredOptions: this.tipoDovutoFilteredOptions,
      paginatorData: {
        pageSize: this.mypayTableComponent.paginator.pageSize,
        pageIndex: this.mypayTableComponent.paginator.pageIndex
      }
    });
    this.router.navigate(['accertamenti', 'anagrafica']);
  }

  private annulla(elementRef: Accertamento, thisRef: AccertamentiComponent, eventRef: any) {
    const msg = `Confermi di voler annullare l'accertamento "${elementRef.deNomeAccertamento}"?`;

    thisRef.dialog.open(ConfirmDialogComponent,{autoFocus:false, data: {message: msg}})
      .afterClosed().pipe(first()).subscribe(result => {
        if(result==="false") return;
      const spinner = thisRef.overlaySpinnerService.showProgress(thisRef.elementRef);
      thisRef.accertamentoService.cancelAccertamento(thisRef.ente, elementRef.id).subscribe(accertamento => {
        elementRef.deStato = accertamento.deStato;
        elementRef.dtUltimaModifica = accertamento.dtUltimaModifica;
        WithActions.reset(elementRef);
        thisRef.overlaySpinnerService.detach(spinner);
        thisRef.toastrService.success('Lo stato aggiornato.');
      }, manageError('Errore aggiornando lo stato', thisRef.toastrService, () => {thisRef.overlaySpinnerService.detach(spinner)}) );
    });
  }

  private chiudi(elementRef: Accertamento, thisRef: AccertamentiComponent, eventRef: any) {
    const msg = `Confermi di voler chiudere l'accertamento "${elementRef.deNomeAccertamento}"?`;

    thisRef.dialog.open(ConfirmDialogComponent,{autoFocus:false, data: {message: msg}})
      .afterClosed().pipe(first()).subscribe(result => {
        if(result==="false") return;
      const spinner = thisRef.overlaySpinnerService.showProgress(thisRef.elementRef);
      thisRef.accertamentoService.closeAccertamento(thisRef.ente, elementRef.id).subscribe(accertamento => {
        elementRef.deStato = accertamento.deStato;
        elementRef.dtUltimaModifica = accertamento.dtUltimaModifica;
        WithActions.reset(elementRef);
        thisRef.overlaySpinnerService.detach(spinner);
        thisRef.toastrService.success('Lo stato aggiornato.');
      }, manageError('Errore aggiornando lo stato', thisRef.toastrService, () => {thisRef.overlaySpinnerService.detach(spinner)}) );
    });
  }

  private isStatoInserito(elementRef: Accertamento, thisRef: AccertamentiComponent) {
    return elementRef.deStato.toUpperCase().trim() === 'INSERITO';
  }

  // Ente in param is valued when changed newly.
  onReset(ente?: Ente) {
    this.form.reset();
    this.form.get('codStato').setValue(this.anagraficaStati?.filter(a => a.codStato === 'TUTTI')[0]);
    if(ente && ente.mygovEnteId){
      //retrieve list of tipoDovuto and prepare autocomplete
      this.form.controls['tipoDovuto'].setValue(null);
      if(!this.tipoDovutoOptionsMap.has(ente.codIpaEnte)){
        this.enteService.getListTipoDovutoByEnteAsOperatore(ente).subscribe(tipiDovuto => {
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
    const anag = this.form.get('codStato').value as AnagraficaStato;
    let codStato = (anag?.codStato && anag.codStato !== 'TUTTI') ? anag.codStato : null;
    this.accertamentoService.getAccertamenti(this.ente, tipoDovuto, i.dateFrom, i.dateTo, i.iuv, codStato, i.deNomeAccertamento).subscribe(data => {
        this.hasSearched = true;
        this.tableData = data;
        //close search panel if data found
        if(data?.length > 0) 
          this.mypSearchChips.setSearchPanelState(false);
        this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  onRemoveFilter(thisRef: AccertamentiComponent) {
    //redo the search
    if(thisRef.hasSearched)
      thisRef.onSubmit();
  }
}
