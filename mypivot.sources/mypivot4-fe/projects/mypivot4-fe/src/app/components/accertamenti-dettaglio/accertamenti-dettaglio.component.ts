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
    DateValidators, manageError, OverlaySpinnerService, PageStateService, PaginatorData, PATTERNS,
    SearchFilterDef, TableAction, TableColumn, validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Observable, Subscription } from 'rxjs';
import { first, map } from 'rxjs/operators';

import { animate, state, style, transition, trigger } from '@angular/animations';
import { CurrencyPipe, DatePipe, Location } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { faBriefcase, faSearch } from '@fortawesome/free-solid-svg-icons';

import { Accertamento } from '../../model/accertamento';
import { AccertamentoFlussoExport } from '../../model/accertamento-flusso-export';
import { Ente } from '../../model/ente';
import { FlussoRicevuta } from '../../model/flusso-ricevuta';
import { AccertamentoService } from '../../services/accertamento.service';
import { EnteService } from '../../services/ente.service';

@Component({
  selector: 'app-accertamenti-dettaglio',
  templateUrl: './accertamenti-dettaglio.component.html',
  styleUrls: ['./accertamenti-dettaglio.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ]
})
export class AccertamentiDettaglioComponent implements OnInit, WithTitle {

  @ViewChild('sForm') formDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<AccertamentoFlussoExport>;
  @ViewChild('mypSearchChips') mypSearchChips: MypSearchChipsComponent;

  get titleLabel(){ return "Dettaglio" }
  get titleIcon(){ return faBriefcase }

  hasSearched: boolean = false;
  blockingError: boolean = false;

  tableDatailColumnsName = ['key','value'];
  expandedElement: FlussoRicevuta | null;

  private enteChangesSub: Subscription;
  private ente: Ente;
  private accertamentoId: number;
  accertamento: Accertamento = new Accertamento();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private enteService: EnteService,
    private accertamentoService: AccertamentoService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private pageStateService: PageStateService,
    private dialog: MatDialog,
    private currencyPipe: CurrencyPipe,
    private location: Location,
  ) {

    this.formDef = [
      new SearchFilterDef('dtEsitoFrom', 'Data Esito da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dtEsitoTo', 'Data Esito. a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dtUltimoAggFrom', 'Data Ultimo Agg. da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dtUltimoAggTo', 'Data Ultimo Agg. a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('iuv', 'IUV', '', []),
      new SearchFilterDef('iud', 'IUD', '', []),
      new SearchFilterDef('cfPagatore', 'CF/PIVA Pagatore', '', [Validators.pattern(PATTERNS.codiceFiscaleOPartitaIva)]),
    ].reduce((formObj, elem) => {formObj[elem.field] = elem; return formObj}, {} );
    
    const formObj = lodash.mapValues(this.formDef, x => [
      lodash.isFunction(x.value) ? x.value() : x.value,
      x.validators]);
    
    this.form = this.formBuilder.group(formObj, { validators: [
      DateValidators.dateRangeForRangePicker('dtEsitoFrom','dtEsitoTo'),
      DateValidators.dateRange('dtUltimoAggFrom','dtUltimoAggTo')
    ] });
    
    this.formChangesSub = this.form.valueChanges.subscribe(validateFormFun(this.form, this.formErrors));
  }

  ngOnInit(): void {

    //retrieve page state data if navigating back
    if(this.pageStateService.isNavigatingBack()){
      const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
      const pageState = this.pageStateService.restoreState();
      if(pageState){
        let modifiedAccertamento:Accertamento = pageState.modifiedAccertamento;
        this.ente = pageState.ente;
        this.accertamentoId = pageState.accertamentoId;
        this.accertamento =  modifiedAccertamento?.id==pageState.accertamentoId ? modifiedAccertamento : pageState.accertamento;
        this.form.setValue(pageState.formData);
        setTimeout(()=>{
          this.onSubmit();
        });
      }
      this.overlaySpinnerService.detach(spinner);
    } else {
      const params = this.route.snapshot.params;
      this.accertamentoId = params['accertamentoId'];

      this.enteChangesSub = this.enteService.getCurrentEnteObs().subscribe(ente => {
        this.tableData = new Array();
        this.ente = ente;
        if (this.ente) {
          this.accertamentoService.getAccertamento(this.ente, this.accertamentoId).subscribe(accertamento => {
            this.accertamento = accertamento;
          });
          this.onSubmit(true);
        }
      });
    }
  }

  ngOnDestroy():void {
    this.formChangesSub?.unsubscribe();
    this.enteChangesSub?.unsubscribe();
  }

  formDef: { [key: string]: SearchFilterDef };
  form: FormGroup;
  formErrors = {};
  private formChangesSub:Subscription;

  private gotoRT(elementRef: AccertamentoFlussoExport, thisRef: AccertamentiDettaglioComponent){
    const navId = thisRef.pageStateService.saveState({
      formData: thisRef.form.value,
      tableData: thisRef.tableData,
      ente: thisRef.ente,
      accertamentoId: thisRef.accertamentoId,
      accertamento: thisRef.accertamento,
      paginatorData: {
        pageSize: thisRef.mypayTableComponent.paginator.pageSize,
        pageIndex: thisRef.mypayTableComponent.paginator.pageIndex
      }
    });

    const queryParams = {
      codTipo: elementRef.codTipoDovuto,
      codIud: elementRef.codiceIud,
    };

    thisRef.router.navigate(['/accertamenti/dettaglio-cruscotto'], { queryParams: queryParams });
  }

  tableColumns: TableColumn[] = [
    new TableColumn('codiceIuv', 'IUV', { sortable: true }),
    new TableColumn('codiceIud', 'IUD', { sortable: true }),
    new TableColumn('identificativoUnivocoRiscossione', 'IUR', { sortable: true }),
    new TableColumn('codiceIdentificativoUnivocoPagatore', 'CF/PIVA Pagatore', { sortable: true }),
    new TableColumn('dtUltimoAggiornamento', 'Data Ultimo Agg.', { sortable: true, pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
    new TableColumn('singoloImportoPagato','Importo', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'] } ),
    new TableColumn('dtEsitoSingoloPagamento', 'Data esito', { sortable: true, pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
    new TableColumn('rowActions', 'Azioni', { sortable: false, tooltip: 'Azioni', actions: [
      new TableAction(faSearch, this.gotoRT, null, 'Dettaglio RT'),
      ] } )
  ];
  tableData: AccertamentoFlussoExport[];
  paginatorData: PaginatorData;

  modifica() {
    const navId = this.pageStateService.saveState({
      formData: this.form.value,
      tableData: this.tableData,
      ente: this.ente,
      accertamentoId: this.accertamentoId,
      accertamento: this.accertamento,
      paginatorData: {
        pageSize: this.mypayTableComponent.paginator.pageSize,
        pageIndex: this.mypayTableComponent.paginator.pageIndex
      }
    });
    this.router.navigate(['/accertamenti/anagrafica',{savedAccertamento: JSON.stringify(this.accertamento),rtPresent:this.tableData?.length>0}], {state:{backNavId:navId}});
  }

  chiudi() {
    const msg = `Confermi di voler chiudere l'accertamento?`;

    this.dialog.open(ConfirmDialogComponent,{autoFocus:false, data: {message: msg}})
      .afterClosed().pipe(first()).subscribe(result => {
        if(result==="false") return;
      const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
      this.accertamentoService.closeAccertamento(this.ente, this.accertamentoId).subscribe(accertamento => {
        this.accertamento = accertamento;
        this.overlaySpinnerService.detach(spinner);
        this.toastrService.success('Lo stato aggiornato.');
      }, manageError('Errore aggiornando lo stato', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
    });
  }

  annulla() {
    const msg = `Confermi di voler annullare l'accertamento?`;

    this.dialog.open(ConfirmDialogComponent,{autoFocus:false, data: {message: msg}})
      .afterClosed().pipe(first()).subscribe(result => {
        if(result==="false") return;
      const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
      this.accertamentoService.cancelAccertamento(this.ente, this.accertamentoId).subscribe(accertamento => {
        this.accertamento = accertamento;
        this.overlaySpinnerService.detach(spinner);
        this.toastrService.success('Lo stato aggiornato.');
      }, manageError('Errore aggiornando lo stato', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
    });
  }

  onSubmit(firstPageLoading: boolean = false){
    const i = this.form.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let dtEsitoFrom = i.dtEsitoFrom || null;
    let dtEsitoTo = i.dtEsitoTo || null;
    let dtUltimoAggFrom = i.dtUltimoAggFrom || null;
    let dtUltimoAggTo = i.dtUltimoAggTo || null;

    this.accertamentoService.getAccertamentoDettaglio(this.ente, this.accertamentoId, dtEsitoFrom, dtEsitoTo,
      dtUltimoAggFrom, dtUltimoAggTo, i.iud, i.iuv, i.cfPagatore).subscribe(data => {
      this.hasSearched = true;
      this.tableData = data;
      //close search panel if data found
      if(data?.length > 0 && !firstPageLoading)
        this.mypSearchChips.setSearchPanelState(false);

      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  onReset(){
    this.form.reset();
    this.hasSearched = false;
    this.tableData = null;
  }

  gotoDettaglioAssocia(associaMode: string) {
    this.pageStateService.saveState({
      formData: this.form.value,
      tableData: this.tableData,
      ente: this.ente,
      accertamentoId: this.accertamentoId,
      accertamento: this.accertamento,
      paginatorData: {
        pageSize: this.mypayTableComponent.paginator.pageSize,
        pageIndex: this.mypayTableComponent.paginator.pageIndex
      }
    });
    this.router.navigate(['accertamenti', 'dettaglio', this.accertamentoId, associaMode]);
  }

  onClickRow(element: AccertamentoFlussoExport, thisRef: AccertamentiDettaglioComponent): Observable<void> {
    if (element.details && element.details.length > 0)
      return;
    const spinner = thisRef.overlaySpinnerService.showProgress(thisRef.elementRef);
    let acc = thisRef.accertamento;
    element.details = []
    return thisRef.accertamentoService.getCapitoliByRT(thisRef.ente, acc.id, acc.codTipoDovuto, element.codiceIud, element.codiceIuv)
    .pipe(map(capitoli =>{
      let values: object[] = capitoli.map((cap,idx) => {
          return { key: (idx+1), value:`<p><span>Ufficio</span> ${cap.deUfficio}<br>\
                                        <span>Capitolo</span> ${cap.deCapitolo}<br>\
                                        <span>Accertamento</span> ${cap.deAccertamento}<br>\
                                        <span>Importo</span> ${thisRef.currencyPipe.transform(cap.numImporto, 'EUR')}</p>`,
                  inHTML: true,
                 };
      });
      element.details = values;
      element.detailsInHTML = true;
      thisRef.overlaySpinnerService.detach(spinner);
    }, manageError('Errore caricando il capitolo', thisRef.toastrService, () => {thisRef.overlaySpinnerService.detach(spinner)}) ));
  }

  goBack() {
    this.location.back();
  }

  onRemoveFilter(thisRef: AccertamentiDettaglioComponent) {
    //redo the search
    if(thisRef.hasSearched)
      thisRef.onSubmit();
  }
}
