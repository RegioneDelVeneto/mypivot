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
    DateValidators, getProp, manageError, OverlaySpinnerService, PaginatorData, TableColumn,
    validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';

import { COMMA, ENTER, SPACE, TAB } from '@angular/cdk/keycodes';
import { CurrencyPipe } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import {
    AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators
} from '@angular/forms';
import { MatChipInputEvent, MatChipList } from '@angular/material/chips';
import { MatCalendarCellClassFunction, MatDatepicker } from '@angular/material/datepicker';
import { Router } from '@angular/router';
import { faTable, faTrash } from '@fortawesome/free-solid-svg-icons';

import { AnagraficaUffCapAcc } from '../../model/anagrafica-uff-cap-acc';
import { Ente } from '../../model/ente';
import { TipoDovuto } from '../../model/tipo-dovuto';
import { VmStatistica } from '../../model/vm-statistica';
import { EnteService } from '../../services/ente.service';
import { StatisticaService } from '../../services/statistica.service';

@Component({
  selector: 'app-statistiche-annomesegiorno',
  templateUrl: './statistiche-annomesegiorno.component.html',
  styleUrls: ['./statistiche-annomesegiorno.component.scss']
})
export class StatisticheAnnomesegiornoComponent implements OnInit, WithTitle {

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<VmStatistica>;
  @ViewChild('chipListDates') chipListDates: MatChipList;

  get titleLabel(){ return "Totali ripartiti per anno/mese/giorno" }
  get titleIcon(){ return faTable }

  iconTrash = faTrash;
  separatorKeysCodes: number[] = [ENTER, COMMA, SPACE, TAB];

  hasSearched: boolean = false;
  blockingError: boolean = false;

  private valueChangedSub: Subscription;

  tipiDovuto: TipoDovuto[];
  uffici: AnagraficaUffCapAcc[];
  capitoli: AnagraficaUffCapAcc[];

  years = new Set<string>();
  selectedDates  = {
    'year': new Set([DateTime.now().toFormat('yyyy')]),
    'month': new Set([DateTime.now().toFormat('yyyy/LL')])
  };
  minDate = DateTime.fromFormat('01/01/1970','dd/LL/yyyy');
  maxDate = DateTime.now().endOf('day');

  dateFormats = {
    "1": {type:'year', view:'multi-year', parse: 'yyyy', display: 'yyyy', label: 'anni', invalid: 'Inserire un anno valido nel formato YYYY'},
    "2": {type:'month', view:'year', parse: 'LL/yyyy', display: 'yyyy/LL', label: 'anni/mese', invalid: 'Inserire un anno/mese valido nel formato MM/YYYY'},
  };

  searchForm: FormGroup;
  searchFormErrors = {};
  searchKeysForAccertamenti = {
    codTipo: null,
    codUfficio: null,
    codCapitolo: null,
  };

  private cellClickFn(tableId: string, parentRef: StatisticheAnnomesegiornoComponent, row:VmStatistica, tableColumn: TableColumn, onlyCheckEnabled: boolean){
    if(onlyCheckEnabled)
      return getProp(row,tableColumn.id)>0;

    parentRef.router.navigate(['/statistichedettaglio'], { queryParams: parentRef.searchKeysForAccertamenti });
  }

  tableColumns = [
    new TableColumn('desc', 'Data', { sortable: true, totalLabel: true }),
    new TableColumn('numPagamenti', 'N°Pagamenti', { sortable: true }),
    new TableColumn('importoPagato', 'Pagati', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'],
        dispCondition: () => this.searchForm.get('flgPagati').value }),
    new TableColumn('importoRendicontato', 'Rendicontati', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'],
        dispCondition: () => this.searchForm.get('flgRendicontati').value }),
    new TableColumn('importoIncassato', 'Incassati', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'],
        dispCondition: () => this.ente?.flgTesoreria && this.searchForm.get('flgIncassati').value }),
  ];
  tableData: VmStatistica[];
  paginatorData: PaginatorData;

  ente: Ente;

  private selectedDateValidator(thisRef: StatisticheAnnomesegiornoComponent): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if(!thisRef.searchForm)
        return;
      const df = thisRef.dateFormats[thisRef.selectedDateSearchType];
      const error = thisRef.selectedDateSearchType!=='3' && !(thisRef.selectedDates?.[df.type]?.size>0);
      //control.setErrors({required: error ? true : null});
      if(thisRef.chipListDates)
        thisRef.chipListDates.errorState = error;
      return error ? {required: true} : null;
    };
  }

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private enteService: EnteService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private statisticaService: StatisticaService,
  ) {
    const formObj = {
      dtType: ['1'],
      dateFake: [''],
      selectedDateFake: [null, [this.selectedDateValidator(this)]],
      dateFrom: [{value:DateTime.now().minus({'month':1}).startOf('day'), disabled:true}, [Validators.required]],
      dateTo: [{value:DateTime.now().startOf('day'), disabled:true}, [Validators.required]],
      flgPagati: [true],
      flgRendicontati: [true],
      flgIncassati: [true],
    };

    this.searchForm = this.formBuilder.group(formObj, { validators: DateValidators.dateRangeForRangePicker('dateFrom','dateTo') });

    this.router.routeReuseStrategy.shouldReuseRoute = () => {
      return false;
    };
  }

  ngOnDestroy(): void {
    this.valueChangedSub?.unsubscribe();
  }

  ngOnInit(): void {
    this.valueChangedSub = this.searchForm.valueChanges.subscribe(validateFormFun(this.searchForm, this.searchFormErrors));
    this.searchForm.statusChanges.subscribe(x => this.updateSearchFormInvalidState(x));
 
    this.enteService.getCurrentEnteObs().subscribe(ente => {
      this.onReset();
      this.tableData = new Array();
      this.ente = ente;
      if (this.ente)
        this.onSearch();
    });
  }

  isSearchFormInvalid: boolean = true;

  private updateSearchFormInvalidState(status: string){
    setTimeout(()=>{this.isSearchFormInvalid = status === 'INVALID' || this.blockingError},0);
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

  get selectedDateSearchType(){
    return this.searchForm?.get('dtType').value || '1';
  }
  
  private changeControlEnabledState(controlName: string, enable: boolean){
    const control = this.searchForm.get(controlName);
    if(control?.invalid && !enable)
      control.setValue(null);
    control?.[enable?'enable':'disable']();
  }

  changeSearchType(type: string) {
    this.changeControlEnabledState('selectedDateFake', type==='1' || type==='2');
    this.changeControlEnabledState('dateFrom', type==='3');
    this.changeControlEnabledState('dateTo', type==='3');

    if(!this.chipListDates)
      return;
    const df = this.dateFormats[this.selectedDateSearchType];
    this.chipListDates.errorState = this.selectedDateSearchType!=='3' && !(this.selectedDates?.[df.type]?.size>0);
    this.chipListDates?.updateErrorState();
  }

  selectedDateClass: MatCalendarCellClassFunction<DateTime> = (cellDate, view) => {
    const df = this.dateFormats[this.selectedDateSearchType];
    if(df.view === view)
      return this.selectedDates[df.type]?.has(cellDate.toFormat(df.display)) ? 'selected' : '';
    else if(df.view === 'year' && view === 'multi-year')
      return [...this.selectedDates[df.type]]?.find(elem => DateTime.fromFormat(elem, df.display).year === cellDate.year) ? 'partially-selected' : '';
  }
  
  setPeriodOptions(datepicker: MatDatepicker<DateTime>):void {
    setTimeout(()=>{
      const calendar = datepicker['_popupComponentRef'].instance._calendar;
      const originalFn = datepicker['_popupComponentRef'].instance._calendar._calendarHeaderPortal._attachedHost.attachedRef.instance.currentPeriodClicked;
      datepicker['_popupComponentRef'].instance._calendar._calendarHeaderPortal._attachedHost.attachedRef.instance.currentPeriodClicked = () => {
        switch(this.dateFormats[this.selectedDateSearchType].view){
          case 'multi-year': calendar.currentView = 'multi-year'; break;
          case 'year': calendar.currentView = calendar.currentView === 'multi-year' ? 'year' : 'multi-year'; break;
          default: originalFn();
        }
      };
    },0);
  }

  chosen(value: DateTime | MatChipInputEvent | string, datepicker?: MatDatepicker<DateTime>) {
    const df = this.dateFormats[this.selectedDateSearchType];
    const calendar = datepicker?.['_popupComponentRef'].instance._calendar;
    if(!calendar || calendar?.currentView === df.view){
      let date: DateTime;
      let onlyAdd = false;
      if(value instanceof DateTime) {
        date = value;
      } else if((value as MatChipInputEvent).input instanceof HTMLInputElement) {
        onlyAdd = true;
        date = DateTime.fromFormat((value as MatChipInputEvent).value, df.parse);
        //try parsing with display format
        if(!date.isValid)
          date = DateTime.fromFormat((value as MatChipInputEvent).value, df.display);
        if(!date.isValid){
          this.toastrService.error(df.invalid);
          return;
        }
      } else if(typeof value === 'string') {
        date = DateTime.fromFormat(value, df.display);
      } else{
        throw new Error('invalid type for date '+value+" - "+(typeof value));
      }

      date = date.startOf(df.type);
      //valid date
      if(date < this.minDate || date > this.maxDate){
        this.toastrService.error('Scegliere una data compresa tra '+this.minDate.year+' e '+this.maxDate.year);
        return;
      }
      this.searchForm.get('dateFake').setValue(null);
      const dateString = date.toFormat(df.display);
      if(this.selectedDates[df.type]?.has(dateString)) {
        !onlyAdd && this.selectedDates[df.type].delete(dateString);
      } else {
        this.selectedDates[df.type] = this.selectedDates[df.type] || new Set<string>();
        if(this.selectedDates[df.type].size>=10)
          this.toastrService.error('È possibile scegliere massimo 10 '+df.parse);
        else {
          if (this.selectedDateSearchType === '2') {
            //If yearMonth in another year selected, the old yearMonths must be cleared.
            let anno = dateString.split('/')[0];
            this.selectedDates[df.type].forEach(yearMonth => {
              if (!yearMonth.startsWith(anno))
                this.selectedDates[df.type] = new Set();
            });
          }
          this.selectedDates[df.type] = new Set([...this.selectedDates[df.type].add(dateString)].sort());
        }
      }

      this.searchForm.get('selectedDateFake').updateValueAndValidity();

      if(datepicker){
        const closeFn = datepicker.close;
        const goToDateInViewFn = datepicker['_popupComponentRef'].instance._calendar._goToDateInView;
        datepicker.close = () => { };
        datepicker['_popupComponentRef'].instance._calendar._goToDateInView = () => {};
        setTimeout(() => {
          datepicker.close = closeFn;
          datepicker['_popupComponentRef'].instance._calendar._goToDateInView = goToDateInViewFn;
          datepicker['_popupComponentRef'].instance._calendar.updateTodaysDate();
        });
      }
    }
  }

  onReset() {
    this.searchForm.reset();
    this.selectedDates  = {
      'year': new Set([DateTime.now().toFormat('yyyy')]),
      'month': new Set([DateTime.now().toFormat('yyyy/LL')])
    };
    this.searchForm.get('dtType').setValue('1');
    this.searchForm.get('dateFake').setValue('');
    this.searchForm.get('selectedDateFake').setValue(null);
    this.searchForm.get('dateFrom').setValue(DateTime.now().minus({'month':1}).startOf('day'));
    this.searchForm.get('dateTo').setValue(DateTime.now().startOf('day'));
    this.searchForm.get('flgPagati').setValue(true);
    this.searchForm.get('flgRendicontati').setValue(true);
    this.searchForm.get('flgIncassati').setValue(true);
  }

  onSearch(){
    const i = this.searchForm.value;
    const df = this.dateFormats[this.selectedDateSearchType];

    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let year: string[], yearMonth: string[], dateFrom: DateTime, dateTo: DateTime;
    if (i.dtType === '1') {
      year = [...(this.selectedDates[df.type] || [])];
    } else if (i.dtType === '2') {
      yearMonth = [...(this.selectedDates?.[df.type] || [])];
    } else if (i.dtType === '3') {
      dateFrom = i.dateFrom;
      dateTo = i.dateTo;
    }

    let searchFun;
    if (i.dtType === '1') {
      searchFun = this.statisticaService.getStatisticheAnno.bind(this.statisticaService, this.ente, year);
    } else if (i.dtType === '2') {
      let anno = yearMonth[0].split('/')[0];
      let mesi = yearMonth.map(mese => mese.split('/')[1]);
      searchFun = this.statisticaService.getStatisticheMese.bind(this.statisticaService, this.ente, anno, mesi);
    } else if (i.dtType === '3') {
      searchFun = this.statisticaService.getStatisticheGiorno.bind(this.statisticaService, this.ente, dateFrom, dateTo);
    }

    searchFun().subscribe(data => {
        this.hasSearched = true;
        this.tableData = data;
        this.mypayTableComponent.changeColumnsToShow();
        this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

}
