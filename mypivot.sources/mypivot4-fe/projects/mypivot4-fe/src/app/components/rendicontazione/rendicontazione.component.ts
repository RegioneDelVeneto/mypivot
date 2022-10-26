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
    CodeLabel, DateValidators, manageError, OverlaySpinnerService, PageStateService, PaginatorData,
    SearchFilterDef, TableAction, TableColumn, validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';

import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { faInfoCircle, faSearch, faThList } from '@fortawesome/free-solid-svg-icons';

import { Ente } from '../../model/ente';
import { Rendicontazione } from '../../model/rendicontazione';
import { EnteService } from '../../services/ente.service';
import { RendicontazioneService } from '../../services/rendicontazione.service';

@Component({
  selector: 'app-rendicontazione',
  templateUrl: './rendicontazione.component.html',
  styleUrls: ['./rendicontazione.component.scss']
})
export class RendicontazioneComponent implements OnInit, OnDestroy, WithTitle {

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<Rendicontazione>;
  @ViewChild('mypSearchChips') mypSearchChips: MypSearchChipsComponent;

  get titleLabel(){ return "Rendicontazione PagoPA" }
  get titleIcon(){ return faThList }

  title: string;
  iconInfoCircle = faInfoCircle;
  infoTextBox: string;
  hasSearched: boolean = false;
  blockingError: boolean = false;
  lastSearchFormData: any;
  allSearchTypes: CodeLabel[];
  currentSearchType: CodeLabel;

  formDef: { [key: string]: SearchFilterDef };
  form: FormGroup;
  formErrors = {};
  private formChangesSub: Subscription;
  private enteChangesSub: Subscription;

  tableColumns: TableColumn[] = [
    new TableColumn('idRendicontazione', 'ID rendicontazione'),
    new TableColumn('idRegolamento', 'ID regolamento'),
    new TableColumn('dateFlusso', 'Data ora flusso', { sortable: (item: Rendicontazione) => item.dateFlusso?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy HH:mm:ss'] }),
    new TableColumn('dataRegolamento', 'Data regolamento', { sortable: (item: Rendicontazione) => item.dataRegolamento?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] }),
    new TableColumn('countTotalePagamenti', 'Totale pagamenti'),
    new TableColumn('importoTotale', 'Importo totale', { pipe: CurrencyPipe, pipeArgs: ['EUR', 'symbol'] }),
    new TableColumn('rowActions', 'Azioni', {
      sortable: false, tooltip: 'Azioni', actions: [
        new TableAction(faSearch, this.gotoDetails, () => true, 'Visualizza dettaglio'),
      ]
    })];
  tableData: Rendicontazione[];
  paginatorData: PaginatorData;
  hasDetail: boolean;

  constructor(
    private formBuilder: FormBuilder,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private router: Router,
    private pageStateService: PageStateService,
    private enteService: EnteService,
    private rendicontazioneService: RendicontazioneService,
    private matDialog: MatDialog,
    private route: ActivatedRoute,
  ) {
    this.formDef = [
      new SearchFilterDef('idRendicontazione', 'ID rendicontazione', '', []),
      new SearchFilterDef('idRegolamento', 'ID regolamento', '', []),
      new SearchFilterDef('dateRegolFrom', 'Data regolamento da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateRegolTo', 'Data regolamento a', null, [], v => v?.toFormat('dd/MM/yyyy'))
    ].reduce((formObj, elem) => { formObj[elem.field] = elem; return formObj }, {});

    const formObj = lodash.mapValues(this.formDef, x => [
      lodash.isFunction(x.value) ? x.value() : x.value,
      x.validators]);

    this.form = this.formBuilder.group(formObj, {
      validators: [
        DateValidators.dateRangeForRangePicker('dateRegolFrom', 'dateRegolTo')
      ]
    });

    this.formChangesSub = this.form.valueChanges.subscribe(validateFormFun(this.form, this.formErrors));
  }

  ngOnInit(): void {
    this.hasDetail = false;
    this.infoTextBox = 'Stai visualizzando la rendicontazione';
    this.enteChangesSub = this.enteService.getCurrentEnteObs().subscribe(value => this.onChangeEnte(this, value));

    //retrieve page state data if navigating back
    if(this.pageStateService.isNavigatingBack()){
      const pageState = this.pageStateService.restoreState();
      if(pageState){
        this.form.setValue(lodash.assign(this.form.value, pageState.formData));
        setTimeout(()=>{
          this.tableData = pageState.tableData;
          this.paginatorData = pageState.paginatorData;
          if(this.tableData){
            this.hasSearched = true;
            this.mypSearchChips.setSearchPanelState(false);
          }
        });
      }
    }
  }

  ngOnDestroy(): void {
    this.formChangesSub?.unsubscribe();
    this.enteChangesSub?.unsubscribe();
  }

  onSubmit(){
    const searchParams = lodash.clone(this.form.value);
    this.lastSearchFormData = this.form.value;
    this.tableData = [];
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.rendicontazioneService.search(this.enteService.getCurrentEnte(), searchParams).subscribe(data => {
      this.hasSearched = true;
      this.tableData = data;
      //close search panel if data found
      if(data?.length > 0) 
        this.mypSearchChips.setSearchPanelState(false);
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  onRemoveFilter(thisRef: RendicontazioneComponent) {
    //redo the search
    if(thisRef.hasSearched)
      thisRef.onSubmit();
  }

  onReset(){
    this.form.reset();
    lodash.forOwn(this.formDef, (value, key) => this.form.get(key).setValue(value.value));
    this.hasSearched = false;
    this.tableData = null;  
  }

  private onChangeEnte(thisRef: RendicontazioneComponent, ente:Ente){
    this.onReset();
    thisRef.mypSearchChips?.setSearchPanelState(true);
  }

  gotoDetails(elementRef: Rendicontazione, thisRef: RendicontazioneComponent, eventRef: any){
    if(eventRef)
      eventRef.stopPropagation();
    MyPayTableDetailComponent.close(thisRef.matDialog);

    thisRef.pageStateService.saveState({
      formData: thisRef.lastSearchFormData,
      tableData: thisRef.tableData,
      paginatorData: {
        pageSize: thisRef.mypayTableComponent.paginator.pageSize,
        pageIndex: thisRef.mypayTableComponent.paginator.pageIndex
      }
    });
    thisRef.router.navigate(['visualizzazione','rendicontazione', elementRef.idRendicontazione, elementRef.idRegolamento]);
  }

}
