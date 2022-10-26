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
    getProp, manageError, OverlaySpinnerService, PageStateService, PaginatorData, TableColumn,
    validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { combineLatest, Subscription } from 'rxjs';

import { CurrencyPipe } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDatepicker } from '@angular/material/datepicker';
import { Router } from '@angular/router';
import { faTable } from '@fortawesome/free-solid-svg-icons';

import { AnagraficaUffCapAcc } from '../../model/anagrafica-uff-cap-acc';
import { Ente } from '../../model/ente';
import { TipoDovuto } from '../../model/tipo-dovuto';
import { VmStatistica } from '../../model/vm-statistica';
import { VmStatisticaCapitolo } from '../../model/vm-statistica-capitolo';
import { EnteService } from '../../services/ente.service';
import { StatisticaService } from '../../services/statistica.service';

@Component({
  selector: 'app-statistiche-capitoli',
  templateUrl: './statistiche-capitoli.component.html',
  styleUrls: ['./statistiche-capitoli.component.scss']
})
export class StatisticheCapitoliComponent implements OnInit, WithTitle {

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<VmStatistica>;

  get titleLabel(){ return "Totali ripartiti per capitoli" }
  get titleIcon(){ return faTable }

  hasSearched: boolean = false;
  blockingError: boolean = false;

  private valueChangedSub: Subscription;

  tipiDovuto: TipoDovuto[];
  uffici: AnagraficaUffCapAcc[];
  searchType: SearchType = null;

  ente: Ente;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private enteService: EnteService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private statisticaService: StatisticaService,
    private pageStateService: PageStateService,
  ) {

    const formObj = {
      dtType: ['1'],
      dtYearly: [DateTime.now().startOf('day')],
      dtMonthly: [DateTime.now().startOf('day')],
      dtDaily: [DateTime.now().startOf('day')],
      flgPagati: [true],
      flgRendicontati: [true],
      flgIncassati: [true],
      tipoDovuto: [null],
      ufficio: [null],
    };

    this.searchForm = this.formBuilder.group(formObj);

    this.router.routeReuseStrategy.shouldReuseRoute = () => {
      return false;
    };
  }

  ngOnDestroy(): void {
    this.valueChangedSub?.unsubscribe();
  }

  ngOnInit(): void {
    this.valueChangedSub = this.searchForm.valueChanges.subscribe(validateFormFun(this.searchForm, this.searchFormErrors));
 
    this.enteService.getCurrentEnteObs().subscribe(ente => {
      this.tableData = new Array();
      this.ente = ente;
      this.onReset(ente);
    });
  }

  searchForm: FormGroup;
  searchFormErrors = {};

  minDate = DateTime.fromFormat('01/01/1970','dd/LL/yyyy');
  maxDate = DateTime.now().endOf('day');

  private cellClickFn(tableId: string, parentRef: StatisticheCapitoliComponent, row:VmStatisticaCapitolo, tableColumn: TableColumn, onlyCheckEnabled: boolean){
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

    let date = null;
    const i = parentRef.searchForm.value;
    switch (i.dtType) {
      case '1':
        date = (i.dtYearly as DateTime).toFormat('yyyy'); break;
      case '2':
        date = (i.dtMonthly as DateTime).toFormat('yyyyMM'); break;
      case '3':
        date = (i.dtDaily as DateTime).toFormat('yyyyMMdd'); break;
    }

    const queryParams = {
      codTipo: row.codTipoDovuto,
      codUfficio: row.codUfficio,
      codCapitolo: row.codCapitolo,
      date: date,
    };

    parentRef.router.navigate(['/statistichedettaglio'], { queryParams: queryParams });
  }

  private cellClickableConditionPagato(parentRef: StatisticheCapitoliComponent, row:VmStatisticaCapitolo) {
    const notTotalRow = row.codTipoDovuto?.length > 0 && row.codUfficio?.length > 0 && row.codCapitolo?.length > 0;
    return notTotalRow && 0 < row.importoPagato && row.importoPagato <= 5000;
  }

  private cellClickableConditionRendicontato(parentRef: StatisticheCapitoliComponent, row:VmStatisticaCapitolo) {
    const notTotalRow = row.codTipoDovuto?.length > 0 && row.codUfficio?.length > 0 && row.codCapitolo?.length > 0;
    return notTotalRow && 0 < row.importoRendicontato && row.importoRendicontato <= 5000;
  }

  private cellClickableConditionIncassato(parentRef: StatisticheCapitoliComponent, row:VmStatisticaCapitolo) {
    const notTotalRow = row.codTipoDovuto?.length > 0 && row.codUfficio?.length > 0 && row.codCapitolo?.length > 0;
    return notTotalRow && 0 < row.importoIncassato && row.importoIncassato <= 5000;
  }

  tableColumns = [
    new TableColumn('deTipoDovuto', 'Tipi Dovuto', { sortable: false, totalLabel: true, dispCondition: () => this.searchType !== SearchType.UFF }),
    new TableColumn('codUfficio', 'Uffici', { sortable: false, totalLabel: true }),
    new TableColumn('deTipoDovuto', 'Tipi Dovuto', { sortable: false, totalLabel: true, dispCondition: () => this.searchType === SearchType.UFF }),
    new TableColumn('codCapitolo', 'Capitolo', { sortable: false, totalLabel: true }),
    new TableColumn('importoPagato', 'Pagati', { sortable: false, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'],
        dispCondition: () => this.searchForm.get('flgPagati').value,
        cellClick: this.cellClickFn, cellClickableCondition: this.cellClickableConditionPagato }),
    new TableColumn('importoRendicontato', 'Rendicontati', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'],
        dispCondition: () => this.searchForm.get('flgRendicontati').value,
        cellClick: this.cellClickFn, cellClickableCondition: this.cellClickableConditionRendicontato }),
    new TableColumn('importoIncassato', 'Incassati', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'],
        dispCondition: () => this.ente?.flgTesoreria && this.searchForm.get('flgIncassati').value,
        cellClick: this.cellClickFn, cellClickableCondition: this.cellClickableConditionIncassato }),
  ];
  tableData: VmStatisticaCapitolo[];
  paginatorData: PaginatorData;

  flgImportiOnChange() {
    const i = this.searchForm.value;
    if (!i.flgPagati && !i.flgRendicontati && !i.flgIncassati) {
      this.searchForm.get('flgPagati').setValue(true);
      this.searchForm.get('flgRendicontati').setValue(true);
      this.searchForm.get('flgIncassati').setValue(true);
    }
    this.mypayTableComponent.changeColumnsToShow();
  }

  chosenYearly(normalizedDt: DateTime, datepicker: MatDatepicker<DateTime>) {
    datepicker.close();
    this.searchForm.get('dtYearly').setValue(normalizedDt);
  }

  chosenMonthly(normalizedDt: DateTime, datepicker: MatDatepicker<DateTime>) {
    datepicker.close();
    this.searchForm.get('dtMonthly').setValue(normalizedDt);
  }

  compareTipoDovuto(o1: TipoDovuto, o2: TipoDovuto) {
    return o1.codTipo === o2?.codTipo;
  }

  compareUfficio(o1: AnagraficaUffCapAcc, o2: AnagraficaUffCapAcc) {
    return o1.codUfficio === o2?.codUfficio;
  }

  tipoDovutoOnChange(tipoDovuto: TipoDovuto) {
    const ufficio = this.searchForm.get('ufficio').value as AnagraficaUffCapAcc;
    if (tipoDovuto?.codTipo && ufficio?.codUfficio)
      this.searchType = SearchType.TD_UFF;
    else if (tipoDovuto?.codTipo && !ufficio?.codUfficio)
      this.searchType = SearchType.TD;
    else if (!tipoDovuto?.codTipo && ufficio?.codUfficio)
      this.searchType = SearchType.UFF;
    else
      this.searchType = null;
  }

  ufficioOnChange(ufficio: AnagraficaUffCapAcc) {
    const tipoDovuto = this.searchForm.get('tipoDovuto').value as TipoDovuto;
    if (tipoDovuto?.codTipo && ufficio?.codUfficio)
      this.searchType = SearchType.TD_UFF;
    else if (tipoDovuto?.codTipo && !ufficio?.codUfficio)
      this.searchType = SearchType.TD;
    else if (!tipoDovuto?.codTipo && ufficio?.codUfficio)
      this.searchType = SearchType.UFF;
    else
      this.searchType = null;
  }

  // Ente in param is valued when changed newly.
  onReset(ente?: Ente) {
    this.searchType = null;
    this.searchForm.reset();
    this.searchForm.get('dtType').setValue('1');
    this.searchForm.get('dtYearly').setValue(DateTime.now().startOf('day'));
    this.searchForm.get('dtMonthly').setValue(DateTime.now().startOf('day'));
    this.searchForm.get('dtDaily').setValue(DateTime.now().startOf('day'));
    this.searchForm.get('flgPagati').setValue(true);
    this.searchForm.get('flgRendicontati').setValue(true);
    this.searchForm.get('flgIncassati').setValue(true);
    this.searchForm.get('tipoDovuto').setValue(null);
    this.searchForm.get('ufficio').setValue(null);
    if(ente){
      const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
      combineLatest([
        this.enteService.getListTipoDovutoByEnteAsOperatore(ente),
        this.statisticaService.getUfficiAll(ente),
      ]).subscribe(([tipiDovuto, uffici]) => {
        let tipoDovuto = new TipoDovuto();
        tipoDovuto.deTipo = 'Seleziona Tipo dovuto';
        this.tipiDovuto = [tipoDovuto].concat(tipiDovuto);
        let ufficio = new AnagraficaUffCapAcc();
        ufficio.deComboUfficio = 'Seleziona Ufficio';
        this.uffici = [ufficio].concat(uffici);
        this.overlaySpinnerService.detach(spinner);

        //retrieve page state data if navigating back
        if(this.pageStateService.isNavigatingBack()){
          const pageState = this.pageStateService.restoreState();
          if(pageState){
            this.searchForm.setValue(pageState.formData);
            setTimeout(()=>{
              if(pageState.reloadData){
                this.onSearch();
              } else {
                this.tableData = pageState.tableData;
                this.paginatorData = pageState.paginatorData;
              }
            });

            // Adjust the table and activate "Cerca" button.
            let tipoDovuto = this.searchForm.get('tipoDovuto').value as TipoDovuto;
            this.tipoDovutoOnChange(tipoDovuto);
          }
        }
      }, manageError('Errore recuperando lista tipo dovuto/ufficio', this.toastrService, () => {this.blockingError=true; this.overlaySpinnerService.detach(spinner)}) );
    }
  }

  onSearch(){
    const i = this.searchForm.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
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

    let codTipo = (this.searchForm.get('tipoDovuto').value as TipoDovuto)?.codTipo;
    let codUfficio = (this.searchForm.get('ufficio').value as AnagraficaUffCapAcc)?.codUfficio;
    this.statisticaService.getStatisticheCapitoli(this.ente, anno, mese, giorno, codTipo, codUfficio).subscribe(data => {
        this.hasSearched = true;
        this.tableData = this.addTotalLine(data);
        this.mypayTableComponent.changeColumnsToShow();
        this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  private addTotalLine(capitoli: VmStatisticaCapitolo[]): VmStatisticaCapitolo[] {
    if (!capitoli || capitoli.length == 0)
      return capitoli;

    let capWithTotal: VmStatisticaCapitolo[] = [];
    if (this.searchType === SearchType.TD) {
      let currentUfficio = '';
      let totalPagati = 0, totalRendicontati = 0, totalIncassati = 0;
      capitoli.forEach(cap => {
        if (currentUfficio === '') {
          currentUfficio = cap.codUfficio;
          totalPagati += cap.importoPagato;
          totalRendicontati += cap.importoRendicontato;
          totalIncassati += cap.importoIncassato;
        } else if (currentUfficio === cap.codUfficio) {
          totalPagati += cap.importoPagato;
          totalRendicontati += cap.importoRendicontato;
          totalIncassati += cap.importoIncassato;
        } else {
          let total = new VmStatisticaCapitolo();
          total.codUfficio = 'Totale';
          total.importoPagato = totalPagati;
          total.importoRendicontato = totalRendicontati;
          total.importoIncassato = totalIncassati;
          capWithTotal.push(total);
          currentUfficio = cap.codUfficio;
          totalPagati = cap.importoPagato;
          totalRendicontati = cap.importoRendicontato;
          totalIncassati = cap.importoIncassato;
        }
        capWithTotal.push(cap);
      });
      let total = new VmStatisticaCapitolo();
      total.codUfficio = 'Totale';
      total.importoPagato = totalPagati;
      total.importoRendicontato = totalRendicontati;
      total.importoIncassato = totalIncassati;
      capWithTotal.push(total);
      let totalAll = new VmStatisticaCapitolo();
      totalAll.deTipoDovuto = 'Totale';
      totalAll.importoPagato = capitoli.map(c => c.importoPagato).reduce((a, b) => a+b);
      totalAll.importoRendicontato = capitoli.map(c => c.importoRendicontato).reduce((a, b) => a+b);
      totalAll.importoIncassato = capitoli.map(c => c.importoIncassato).reduce((a, b) => a+b);
      capWithTotal.push(totalAll);
    } else if (this.searchType === SearchType.UFF) {
      let currentTipo = '';
      let totalPagati = 0, totalRendicontati = 0, totalIncassati = 0;
      capitoli.forEach(cap => {
        if (currentTipo === '') {
          currentTipo = cap.codTipoDovuto;
          totalPagati += cap.importoPagato;
          totalRendicontati += cap.importoRendicontato;
          totalIncassati += cap.importoIncassato;
        } else if (currentTipo === cap.codTipoDovuto) {
          totalPagati += cap.importoPagato;
          totalRendicontati += cap.importoRendicontato;
          totalIncassati += cap.importoIncassato;
        } else {
          let total = new VmStatisticaCapitolo();
          total.deTipoDovuto = 'Totale';
          total.importoPagato = totalPagati;
          total.importoRendicontato = totalRendicontati;
          total.importoIncassato = totalIncassati;
          capWithTotal.push(total);
          currentTipo = cap.codTipoDovuto;
          totalPagati = cap.importoPagato;
          totalRendicontati = cap.importoRendicontato;
          totalIncassati = cap.importoIncassato;
        }
        capWithTotal.push(cap);
      });
      let total = new VmStatisticaCapitolo();
      total.deTipoDovuto = 'Totale';
      total.importoPagato = totalPagati;
      total.importoRendicontato = totalRendicontati;
      total.importoIncassato = totalIncassati;
      capWithTotal.push(total);
      let totalAll = new VmStatisticaCapitolo();
      totalAll.codUfficio = 'Totale';
      totalAll.importoPagato = capitoli.map(c => c.importoPagato).reduce((a, b) => a+b);
      totalAll.importoRendicontato = capitoli.map(c => c.importoRendicontato).reduce((a, b) => a+b);
      totalAll.importoIncassato = capitoli.map(c => c.importoIncassato).reduce((a, b) => a+b);
      capWithTotal.push(totalAll);
    } else {
      let totalAll = new VmStatisticaCapitolo();
      totalAll.deTipoDovuto = 'Totale';
      totalAll.importoPagato = capitoli.map(c => c.importoPagato).reduce((a, b) => a+b);
      totalAll.importoRendicontato = capitoli.map(c => c.importoRendicontato).reduce((a, b) => a+b);
      totalAll.importoIncassato = capitoli.map(c => c.importoIncassato).reduce((a, b) => a+b);
      capWithTotal = capitoli.concat([totalAll]);
    }
    return capWithTotal;
  }
}

enum SearchType {
  TD,
  UFF,
  TD_UFF
}
