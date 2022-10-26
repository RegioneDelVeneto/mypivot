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
import { DateTime } from 'luxon';
import { ToastrService } from 'ngx-toastr';
import {
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    getProp, manageError, MyPayBreadcrumbsService, OverlaySpinnerService, PageStateService,
    PaginatorData, TableColumn, validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';

import { COMMA, ENTER, SPACE, TAB } from '@angular/cdk/keycodes';
import { CurrencyPipe } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatChipList } from '@angular/material/chips';
import { MatDatepicker } from '@angular/material/datepicker';
import { ActivatedRoute, Router } from '@angular/router';
import { faTable, faTrash } from '@fortawesome/free-solid-svg-icons';

import { AnagraficaUffCapAcc } from '../../model/anagrafica-uff-cap-acc';
import { Ente } from '../../model/ente';
import { TipoDovuto } from '../../model/tipo-dovuto';
import { VmStatistica } from '../../model/vm-statistica';
import { EnteService } from '../../services/ente.service';
import { StatisticaService } from '../../services/statistica.service';

@Component({
  selector: 'app-statistiche',
  templateUrl: './statistiche.component.html',
  styleUrls: ['./statistiche.component.scss']
})
export class StatisticheComponent implements OnInit, WithTitle {

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<VmStatistica>;
  @ViewChild('chipListDates') chipListDates: MatChipList;

  get titleLabel(){ return this.title || "Statistiche" }
  get titleIcon(){ return faTable }

  iconTrash = faTrash;
  separatorKeysCodes: number[] = [ENTER, COMMA, SPACE, TAB];

  hasSearched: boolean = false;
  blockingError: boolean = false;
  statisticheMode: string; //'uffici', 'tipiDovuto', 'accertamenti' o 'annoMeseGirno';

  private valueChangedSub: Subscription;

  tipiDovuto: TipoDovuto[];
  uffici: AnagraficaUffCapAcc[];
  capitoli: AnagraficaUffCapAcc[];

  searchForm: FormGroup;
  searchFormErrors = {};
  searchKeysForAccertamenti = {
    codTipo: null,
    codUfficio: null,
    codCapitolo: null,
    date: null,
  };

  minDate = DateTime.fromFormat('01/01/1970','dd/LL/yyyy');
  maxDate = DateTime.now().endOf('day');

  private cellClickFn(tableId: string, parentRef: StatisticheComponent, row:VmStatistica, tableColumn: TableColumn, onlyCheckEnabled: boolean){
    if(onlyCheckEnabled)
      return getProp(row,tableColumn.id)>0;

    const navId = parentRef.pageStateService.saveState({
      formData: parentRef.searchForm.value,
      tableData: parentRef.tableData,
      paginatorData: {
        pageSize: parentRef.mypayTableComponent.paginator.pageSize,
        pageIndex: parentRef.mypayTableComponent.paginator.pageIndex
      }
    });

    parentRef.router.navigate(['/statistichedettaglio'], { queryParams: parentRef.searchKeysForAccertamenti });
  }

  private cellClickableConditionPagato(parentRef: StatisticheComponent, row:VmStatistica) {
    return parentRef.statisticheMode === 'accertamenti' && 0 < row.importoPagato && row.importoPagato <= 5000;
  }

  private cellClickableConditionRendicontato(parentRef: StatisticheComponent, row:VmStatistica) {
    return parentRef.statisticheMode === 'accertamenti' && 0 < row.importoRendicontato && row.importoRendicontato <= 5000;
  }

  private cellClickableConditionIncassato(parentRef: StatisticheComponent, row:VmStatistica) {
    return parentRef.statisticheMode === 'accertamenti' && 0 < row.importoIncassato && row.importoIncassato <= 5000;
  }

  tableColumns = [
    new TableColumn('codDesc', 'Uffici', { sortable: true, totalLabel: true, dispCondition: () => this.statisticheMode ==='uffici' }),
    new TableColumn('desc', 'Tipi Dovuto', { sortable: true, totalLabel: true, dispCondition: () => this.statisticheMode ==='tipiDovuto' }),
    new TableColumn('codDesc', 'Accertamenti', { sortable: true, totalLabel: true, dispCondition: () => this.statisticheMode ==='accertamenti' }),
    new TableColumn('importoPagato', 'Pagati', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'],
        dispCondition: () => this.searchForm.get('flgPagati').value,
        cellClick: this.cellClickFn, cellClickableCondition: this.cellClickableConditionPagato }),
    new TableColumn('importoRendicontato', 'Rendicontati', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'],
        dispCondition: () => this.searchForm.get('flgRendicontati').value,
        cellClick: this.cellClickFn, cellClickableCondition: this.cellClickableConditionRendicontato }),
    new TableColumn('importoIncassato', 'Incassati', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'],
        dispCondition: () => this.ente?.flgTesoreria && this.searchForm.get('flgIncassati').value,
        cellClick: this.cellClickFn, cellClickableCondition: this.cellClickableConditionIncassato }),
  ];
  tableData: VmStatistica[];
  paginatorData: PaginatorData;

  private title: string;
  ente: Ente;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private enteService: EnteService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private statisticaService: StatisticaService,
    private mypayBreadcrumbsService: MyPayBreadcrumbsService,
    private pageStateService: PageStateService,
  ) {

    const params = this.route.snapshot.params;
    this.statisticheMode = params['statisticheMode'] || 'uffici';

    const formObj = {
      dtType: ['1'],
      dtYearly: [DateTime.now().startOf('day')],
      dtMonthly: [DateTime.now().startOf('day')],
      dtDaily: [DateTime.now().startOf('day')],
      flgPagati: [true],
      flgRendicontati: [true],
      flgIncassati: [true],
    };
    if(this.statisticheMode === 'accertamenti'){
      formObj['tipoDovuto'] = [null, [Validators.required]];
      formObj['ufficio'] = [{disabled:true}, [Validators.required]];
      formObj['capitolo'] = [{disabled:true}, [Validators.required]];
    }
    this.searchForm = this.formBuilder.group(formObj);

    this.router.routeReuseStrategy.shouldReuseRoute = () => {
      return false;
    };
  }

  ngOnDestroy(): void {
    this.valueChangedSub?.unsubscribe();
  }

  ngOnInit(): void {
    switch (this.statisticheMode) {
      case 'uffici': this.title = 'Statistiche - Totali ripartiti per uffici'; break;
      case 'tipiDovuto': this.title = 'Statistiche - Totali ripartiti per tipi dovuto'; break;
      case 'accertamenti': this.title = 'Statistiche - Totali ripartiti per accertamenti'; break;
      default:
        throw new Error('invalid statisticheMode '+this.statisticheMode);
    }

    this.mypayBreadcrumbsService.updateCurrentBreadcrumb(this.title);
    this.valueChangedSub = this.searchForm.valueChanges.subscribe(validateFormFun(this.searchForm, this.searchFormErrors));
    this.searchForm.statusChanges.subscribe(x => this.updateSearchFormInvalidState(x));

    this.enteService.getCurrentEnteObs().subscribe(ente => {
      this.tableData = new Array();
      this.ente = ente;
      this.onReset(ente);
      if(ente){
        if (this.statisticheMode !== 'accertamenti') {
          //this.onSearch();
          return;
        }

        //retrieve page state data if navigating back for 'accertamenti'
        if(this.pageStateService.isNavigatingBack()){
          const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
          const pageState = this.pageStateService.restoreState();
          if(pageState){
            this.searchForm.setValue(pageState.formData);
            let tipoDovuto = pageState.formData.tipoDovuto as TipoDovuto;
            let ufficio = pageState.formData.ufficio as AnagraficaUffCapAcc;
            let capitolo = pageState.formData.capitolo as AnagraficaUffCapAcc;
            let date = null;
            switch (pageState.formData.dtType) {
              case '1':
                date = (pageState.formData.dtYearly as DateTime).toFormat('yyyy'); break;
              case '2':
                date = (pageState.formData.dtMonthly as DateTime).toFormat('yyyyMM'); break;
              case '3':
                date = (pageState.formData.dtDaily as DateTime).toFormat('yyyyMMdd'); break;
            }
            this.statisticaService.getUffici(ente, tipoDovuto).subscribe(
              uffici => {
                this.uffici = uffici;
                this.statisticaService.getCapitoli(ente, tipoDovuto, ufficio.codUfficio).subscribe(
                  capitoli => {
                    this.capitoli = capitoli;
                  }, manageError('Errore recuperando lista capitolo', this.toastrService, () => {this.blockingError=true; this.overlaySpinnerService.detach(spinner)}) );
            }, manageError('Errore recuperando lista ufficio', this.toastrService, () => {this.blockingError=true; this.overlaySpinnerService.detach(spinner)}) );
            this.searchKeysForAccertamenti = {
              codTipo: tipoDovuto.codTipo, codUfficio: ufficio.codUfficio, codCapitolo: capitolo.codCapitolo, date: date
            };
            setTimeout(()=>{
              if(pageState.reloadData){
                this.onSearch();
              } else {
                this.tableData = pageState.tableData;
                this.paginatorData = pageState.paginatorData;
              }
              // Adjust the table.
              this.isSearchFormInvalid = this.searchForm.status === 'INVALID' || this.blockingError
            });
          }
          this.searchForm.get('ufficio').enable();
          this.searchForm.get('capitolo').enable();
          this.overlaySpinnerService.detach(spinner);
        }
      }
    });
  }

  isSearchFormInvalid: boolean = true;

  private updateSearchFormInvalidState(status: string){
    setTimeout(()=>{this.isSearchFormInvalid = status === 'INVALID' || this.blockingError},0);
  }

  chosenYearly(normalizedDt: DateTime, datepicker: MatDatepicker<DateTime>) {
    datepicker.close();
    this.searchForm.get('dtYearly').setValue(normalizedDt);
  }

  chosenMonthly(normalizedDt: DateTime, datepicker: MatDatepicker<DateTime>) {
    datepicker.close();
    this.searchForm.get('dtMonthly').setValue(normalizedDt);
  }

  flgImportiOnChange() {
    const i = this.searchForm.value;
    if (!i.flgPagati && !i.flgRendicontati && !i.flgIncassati) {
      this.searchForm.get('flgPagati').setValue(true);
      this.searchForm.get('flgRendicontati').setValue(true);
      this.searchForm.get('flgIncassati').setValue(true);
    }
    this.mypayTableComponent.changeColumnsToShow();
  }

  private loadTipiDovuto(ente: Ente) {
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.enteService.getListTipoDovutoByEnteAsOperatore(ente).subscribe(
      tipiDovuto => {
        this.overlaySpinnerService.detach(spinner);
        this.tipiDovuto = tipiDovuto;
      }, manageError('Errore recuperando lista tipo dovuto', this.toastrService, () => {this.blockingError=true; this.overlaySpinnerService.detach(spinner)}) );
  }

  private loadUffici(ente: Ente, tipoDovuto: TipoDovuto) {
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.statisticaService.getUffici(ente, tipoDovuto).subscribe(
      uffici => {
        this.overlaySpinnerService.detach(spinner);
        this.uffici = uffici;
      }, manageError('Errore recuperando lista ufficio', this.toastrService, () => {this.blockingError=true; this.overlaySpinnerService.detach(spinner)}) );
  }

  private loadCapitoli(ente: Ente, tipoDovuto: TipoDovuto, codUfficio: string) {
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.statisticaService.getCapitoli(ente, tipoDovuto, codUfficio).subscribe(
      capitoli => {
        this.overlaySpinnerService.detach(spinner);
        this.capitoli = capitoli;
      }, manageError('Errore recuperando lista capitolo', this.toastrService, () => {this.blockingError=true; this.overlaySpinnerService.detach(spinner)}) );
  }

  compareTipoDovuto(o1: TipoDovuto, o2: TipoDovuto) {
    return o1.codTipo === o2?.codTipo;
  }

  compareUfficio(o1: AnagraficaUffCapAcc, o2: AnagraficaUffCapAcc) {
    return o1.codUfficio === o2?.codUfficio;
  }

  compareCapitolo(o1: AnagraficaUffCapAcc, o2: AnagraficaUffCapAcc) {
    return o1.codCapitolo === o2?.codCapitolo;
  }

  tipoDovutoOnChange(tipoDovuto: TipoDovuto) {
    this.searchForm.get('ufficio').setValue(null);
    this.searchForm.get('capitolo').setValue(null);
    this.uffici = [];
    this.capitoli = [];
    this.loadUffici(this.ente, tipoDovuto);
    this.searchForm.get('ufficio').enable();
    this.searchForm.get('capitolo').disable();
  }

  ufficioOnChange(ufficio: AnagraficaUffCapAcc) {
    if (this.statisticheMode === 'accertamenti') {
      this.searchForm.get('capitolo').setValue(null);
      this.capitoli = [];
      let tipoDovuto: TipoDovuto = this.searchForm.get('tipoDovuto').value;
      this.loadCapitoli(this.ente, tipoDovuto, ufficio.codUfficio);
      this.searchForm.get('capitolo').enable();
    }
  }

  capitoloOnChange(capitolo: AnagraficaUffCapAcc) {
    // Do nothing for now.
  }

  // Ente in param is valued when changed newly.
  onReset(ente?: Ente) {
    this.searchForm.reset();
    this.searchForm.get('dtType').setValue('1');
    this.searchForm.get('dtYearly').setValue(DateTime.now().startOf('day'));
    this.searchForm.get('dtMonthly').setValue(DateTime.now().startOf('day'));
    this.searchForm.get('dtDaily').setValue(DateTime.now().startOf('day'));
    this.searchForm.get('flgPagati').setValue(true);
    this.searchForm.get('flgRendicontati').setValue(true);
    this.searchForm.get('flgIncassati').setValue(true);
    if (this.statisticheMode === 'accertamenti') {
      this.searchForm.get('tipoDovuto').setValue(null);
      this.searchForm.get('ufficio').setValue(null);
      this.searchForm.get('capitolo').setValue(null);
      this.searchForm.get('ufficio').disable();
      this.searchForm.get('capitolo').disable();
      this.uffici = [];
      this.capitoli = [];
      if (ente)
        this.loadTipiDovuto(ente);
    }
  }

  onSearch(){
    const i = this.searchForm.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);

    let searchFun;
    let anno, mese, giorno;
    if (i.dtType === '1') {
      anno = (i.dtYearly as DateTime).year;
    } else if (i.dtType === '2') {
      anno = (i.dtMonthly as DateTime).year;
      mese = (i.dtMonthly as DateTime).month;
    } else if (i.dtType === '3') {
      anno = (i.dtDaily as DateTime).year;
      mese = (i.dtDaily as DateTime).month;
      giorno = (i.dtDaily as DateTime).day;
    }

    switch (this.statisticheMode) {
      case 'uffici':
        searchFun = this.statisticaService.getStatisticheUffici.bind(this.statisticaService, this.ente, anno, mese, giorno);
        break;
      case 'tipiDovuto':
        searchFun = this.statisticaService.getStatisticheTipiDovuto.bind(this.statisticaService, this.ente, anno, mese, giorno);
        break;
      case 'accertamenti':
        let codTipo = (this.searchForm.get('tipoDovuto').value as TipoDovuto).codTipo;
        let codUfficio = (this.searchForm.get('ufficio').value as AnagraficaUffCapAcc).codUfficio;
        let codCapitolo = (this.searchForm.get('capitolo').value as AnagraficaUffCapAcc).codCapitolo;
        let date = null;
        const i = this.searchForm.value;
        switch (i.dtType) {
          case '1':
            date = (i.dtYearly as DateTime).toFormat('yyyy'); break;
          case '2':
            date = (i.dtMonthly as DateTime).toFormat('yyyyMM'); break;
          case '3':
            date = (i.dtDaily as DateTime).toFormat('yyyyMMdd'); break;
        }
        searchFun = this.statisticaService.getStatisticheAccertamenti.bind(
          this.statisticaService, this.ente, anno, mese, giorno, codTipo, codUfficio, codCapitolo);
        this.searchKeysForAccertamenti.codTipo = codTipo;
        this.searchKeysForAccertamenti.codUfficio = codUfficio;
        this.searchKeysForAccertamenti.codCapitolo = codCapitolo;
        this.searchKeysForAccertamenti.date = date;
        break;
      default:
        throw new Error('invalid statisticheMode '+this.statisticheMode);
    }
    searchFun().subscribe(data => {
        this.hasSearched = true;
        this.tableData = data;
        this.mypayTableComponent.changeColumnsToShow();
        this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

}