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
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import {
    MypSearchChipsComponent
} from 'projects/mypay4-fe-common/src/lib/components/myp-search-chips/myp-search-chips.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    DateValidators, manageError, OverlaySpinnerService, PageStateService, PaginatorData, PATTERNS,
    SearchFilterDef, TableAction, TableColumn, validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Observable, Subscription } from 'rxjs';

import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDatepicker } from '@angular/material/datepicker';
import { ActivatedRoute, Router } from '@angular/router';
import { faFileInvoiceDollar, faSearch } from '@fortawesome/free-solid-svg-icons';

import { Ente } from '../../model/ente';
import { Tesoreria } from '../../model/tesoreria';
import { EnteService } from '../../services/ente.service';
import { RendicontazioneService } from '../../services/rendicontazione.service';
import { TesoreriaService } from '../../services/tesoreria.service';

@Component({
  selector: 'app-tesorerie',
  templateUrl: './tesorerie.component.html',
  styleUrls: ['./tesorerie.component.scss']
})
export class TesorerieComponent implements OnInit, OnDestroy, WithTitle {

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<Tesoreria>;
  @ViewChild('mypSearchChips') mypSearchChips: MypSearchChipsComponent;

  get titleLabel(){ return "Giornale di cassa" }
  get titleIcon(){ return faFileInvoiceDollar }

  hasSearched: boolean = false;
  blockingError: boolean = false;

  tableDatailColumnsName = ['key','value'];
  expandedElement: Tesoreria | null;

  private enteChangesSub: Subscription;
  private ente: Ente;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private enteService: EnteService,
    private tesoreriaService: TesoreriaService,
    private toastrService: ToastrService,
    private rendicontazioneService: RendicontazioneService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private pageStateService: PageStateService
  ) {

    this.formDef = [
      new SearchFilterDef('dtContabileFrom', 'Data Contabile da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dtContabileTo', 'Data Contabile a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dtValutaFrom', 'Data Valuta da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dtValutaTo', 'Data Valuta a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('iuv', 'IUV', '', []),
      new SearchFilterDef('annoBolletta', 'Anno Bolletta', null, [], v => v?.toFormat('yyyy')),
      new SearchFilterDef('codBolletta', 'Codice Bolletta', '', []),
      new SearchFilterDef('idr', 'ID Rendicontazione', '', []),
      new SearchFilterDef('importo', 'Importo', '', [Validators.pattern(PATTERNS.importo)]),
      new SearchFilterDef('annoDocumento', 'Anno Codice Documento', null, [], v => v?.toFormat('yyyy')),
      new SearchFilterDef('codDocumento', 'Codice Documento', '', []),
      new SearchFilterDef('annoProvvisorio', 'Anno Codice Provvisorio', null, [], v => v?.toFormat('yyyy')),
      new SearchFilterDef('codProvvisorio', 'Codice Provvisorio', '', []),
      new SearchFilterDef('ordinante', 'Ordinante', '', []),
    ].reduce((formObj, elem) => { formObj[elem.field] = elem; return formObj }, {});

    const formObj = lodash.mapValues(this.formDef, x => [
      lodash.isFunction(x.value) ? x.value() : x.value,
      x.validators]);

    this.form = this.formBuilder.group(formObj, {
      validators: [
        DateValidators.dateRangeForRangePicker('dtContabileFrom', 'dtContabileTo'),
        DateValidators.dateRangeForRangePicker('dtValutaFrom', 'dtValutaTo')
      ]
    });

    this.formChangesSub = this.form.valueChanges.subscribe(validateFormFun(this.form, this.formErrors));
  }

  ngOnInit(): void {

    this.enteChangesSub = this.enteService.getCurrentEnteObs().subscribe(ente => {
      this.tableData = new Array();
      this.ente = ente;
      //retrieve page state data if navigating back for 'accertamenti'
      if(this.pageStateService.isNavigatingBack()){
        const pageState = this.pageStateService.restoreState();
        if(pageState){
          const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
          this.form.setValue(pageState.formData);
          setTimeout(()=>{
            if(pageState.reloadData){
              this.onSubmit();
            } else {
              this.tableData = pageState.tableData;
              this.paginatorData = pageState.paginatorData;
            }
            this.overlaySpinnerService.detach(spinner);
          });
        }
      }
    });
  }

  ngOnDestroy():void {
    this.formChangesSub?.unsubscribe();
    this.enteChangesSub?.unsubscribe();
  }

  formDef: { [key: string]: SearchFilterDef };
  form: FormGroup;
  formErrors = {};
  private formChangesSub: Subscription;

  private gotoDetail(elementRef: Tesoreria, thisRef: TesorerieComponent, eventRef: any) {
    const navId = thisRef.pageStateService.saveState({
      formData: thisRef.form.value,
      tableData: thisRef.tableData,
      paginatorData: {
        pageSize: thisRef.mypayTableComponent.paginator.pageSize,
        pageIndex: thisRef.mypayTableComponent.paginator.pageIndex
      }
    });

    const queryParams = {
      tesoreriaId: elementRef.id,
      annoBolletta: elementRef.annoBolletta,
      codBolletta: elementRef.codBolletta,
    };

    thisRef.router.navigate(['/visualizzazione/tesoreria/dettaglio'], { queryParams: queryParams });
  }

  private gotoRendicontazione(ref: Tesoreria, thisRef: TesorerieComponent, eventRef: any) {
    if(eventRef)
      eventRef.stopPropagation();
    thisRef.pageStateService.saveState({
      formData: thisRef.form.value,
      tableData: thisRef.tableData,
      paginatorData: {
        pageSize: thisRef.mypayTableComponent.paginator.pageSize,
        pageIndex: thisRef.mypayTableComponent.paginator.pageIndex
      }
    });
    const spinner = thisRef.overlaySpinnerService.showProgress(thisRef.elementRef);
    thisRef.rendicontazioneService.get(thisRef.enteService.getCurrentEnte(), ref.idRendicontazione).subscribe(data => {
      if(data)
        thisRef.router.navigate(['visualizzazione','rendicontazione', data.idRendicontazione, data.idRegolamento]);
      else
        thisRef.toastrService.warning('Dati rendicontazione non trovati');
      thisRef.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', thisRef.toastrService, () => {thisRef.overlaySpinnerService.detach(spinner)}) );
  }

  private gotoRendicontazioneEnable(elementRef: Tesoreria, thisRef: TesorerieComponent) {
    return elementRef?.idRendicontazione && elementRef?.idRendicontazione.length > 0;
  }

  tableColumns: TableColumn[] = [
    new TableColumn('annoBolletta', 'Anno Bolletta', { sortable: true }),
    new TableColumn('codBolletta', 'Codice Bolletta', { sortable: true }),
    new TableColumn('dtValuta', 'Data Valuta', { sortable: true, pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] }),
    new TableColumn('dtContabile', 'Data Contabile', { sortable: true, pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] }),
    new TableColumn('idRendicontazione', 'ID Rendicontazione', { sortable: true }),
    new TableColumn('importoTesoreria', 'Importo Tesoreria', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'] }),
    new TableColumn('rowActions', 'Azioni', { sortable: false, tooltip: 'Azioni', actions: [
      new TableAction(faSearch, this.gotoDetail, null, 'Dettaglio'),
      new TableAction(faSearch, this.gotoRendicontazione, this.gotoRendicontazioneEnable, 'Rendicontazione'),
      ] } )
  ];
  tableData: Tesoreria[];
  paginatorData: PaginatorData;


  onYearChosen(field: string, normalizedDt: DateTime, datePicker: MatDatepicker<DateTime>){
    datePicker.close();
    this.form.get(field).setValue(normalizedDt);
  }

  onSubmit(){
    const i = lodash.clone(this.form.value);
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let dtContabileFrom = i.dtContabileFrom || null;
    let dtContabileTo = i.dtContabileTo || null;
    let dtValutaFrom = i.dtValutaFrom || null;
    let dtValutaTo = i.dtValutaTo || null;
    let annoBolletta = i.annoBolletta?.toFormat('yyyy') || null;
    let annoDocumento = i.annoDocumento?.toFormat('yyyy') || null;
    let annoProvvisorio = i.annoProvvisorio?.toFormat('yyyy') || null;

    this.tesoreriaService.search(this.ente, i.iuv, annoBolletta, i.codBolletta, i.idr, i.importo, annoDocumento, i.codDocumento,
      annoProvvisorio, i.codProvvisorio, i.ordinante, dtContabileFrom, dtContabileTo, dtValutaFrom, dtValutaTo).subscribe(data => {
      this.hasSearched = true;
      this.tableData = data;
      if (data?.length > 0)
        this.mypSearchChips.setSearchPanelState(false);
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  onReset(){
    this.form.reset();
    this.hasSearched = false;
    this.tableData = null;
  }
  
  onRemoveFilter(thisRef: TesorerieComponent) {
    //redo the search
    if(thisRef.hasSearched)
      thisRef.onSubmit();
  }

  onClickRow(element: Tesoreria, thisRef: TesorerieComponent): Observable<void> {
    if(element.details)
      return;
    element.details = [
          { key:'Anno Codice Documento', value:element.annoCodDocumento },
          { key:'Codice Documento', value:element.codDocumento },
          { key:'Anno Codice Provvisorio', value:element.annoCodProvvisorio },
          { key:'Codice Provvisorio', value:element.codProvvisorio },
     ];
  }
}
