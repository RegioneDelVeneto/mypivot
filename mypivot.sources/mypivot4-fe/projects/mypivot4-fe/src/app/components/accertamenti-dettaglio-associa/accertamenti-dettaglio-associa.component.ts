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
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import {
    MypSearchChipsComponent
} from 'projects/mypay4-fe-common/src/lib/components/myp-search-chips/myp-search-chips.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    DateValidators, manageError, MyPayBreadcrumbsService, OverlaySpinnerService, PaginatorData,
    PATTERNS, SearchFilterDef, TableColumn, validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';

import { CurrencyPipe, DatePipe, Location } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { faBriefcase } from '@fortawesome/free-solid-svg-icons';

import { Accertamento } from '../../model/accertamento';
import { AccertamentoFlussoExport } from '../../model/accertamento-flusso-export';
import { Ente } from '../../model/ente';
import { AccertamentoService } from '../../services/accertamento.service';
import { EnteService } from '../../services/ente.service';
import {
    AccertamentiDettaglioAssociaDialogComponent
} from '../accertamenti-dettaglio-associa-dialog/accertamenti-dettaglio-associa-dialog.component';

@Component({
  selector: 'app-accertamenti-dettaglio-associa',
  templateUrl: './accertamenti-dettaglio-associa.component.html',
  styleUrls: ['./accertamenti-dettaglio-associa.component.scss']
})
export class AccertamentiDettaglioAssociaComponent implements OnInit, WithTitle {

  @ViewChild('sForm') formDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<AccertamentoFlussoExport>;
  @ViewChild('mypSearchChips') mypSearchChips: MypSearchChipsComponent;

  get titleLabel(){ return (this.associaMode==='associa' ? "Aggiungi pagamenti" : "Rimuovi pagamenti") || "Gestisci pagamenti" }
  get titleIcon(){ return faBriefcase }

  associaMode: string; // 'associa' or 'deassocia'
  hasSearched: boolean = false;
  blockingError: boolean = false;

  private enteChangesSub: Subscription;
  private ente: Ente;
  private accertamentoId: number;
  accertamento: Accertamento;
  private selectedPagamenti: AccertamentoFlussoExport[] = new Array();

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private enteService: EnteService,
    private accertamentoService: AccertamentoService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private dialog: MatDialog,
    private location: Location,
    private breadcrumbsService: MyPayBreadcrumbsService,
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

    const params = this.route.snapshot.params;
    this.accertamentoId = params['accertamentoId'];
    this.associaMode = params['associaMode'];

    this.enteChangesSub = this.enteService.getCurrentEnteObs().subscribe(ente => {
      this.tableData = new Array();
      this.ente = ente;
      if (this.ente) {
        this.accertamentoService.getAccertamento(this.ente, this.accertamentoId).subscribe(accertamento => {
          this.accertamento = accertamento;
          this.onSubmit(true);
        });
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
  private formChangesSub:Subscription;

  tableColumns: TableColumn[] = [
    new TableColumn('codiceIuv', 'IUV', { sortable: true }),
    new TableColumn('codiceIud', 'IUD', { sortable: true }),
    new TableColumn('identificativoUnivocoRiscossione', 'IUR', { sortable: true }),
    new TableColumn('codiceIdentificativoUnivocoPagatore', 'CF/PIVA Pagatore', { sortable: true }),
    new TableColumn('dtUltimoAggiornamento', 'Data Ultimo Agg.', { sortable: true, pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
    new TableColumn('singoloImportoPagato','Importo', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'] } ),
    new TableColumn('dtEsitoSingoloPagamento', 'Data esito', { sortable: true, pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
    new TableColumn('rowActions', 'Selezione', { checkbox: true, checkboxClick: this.checkboxClickFn} ),
  ];
  tableData: AccertamentoFlussoExport[];
  paginatorData: PaginatorData;


  private checkboxClickFn(elementRef: AccertamentoFlussoExport, thisRef: AccertamentiDettaglioAssociaComponent, eventRef: any) {
    if (eventRef.checked)
      thisRef.selectedPagamenti.push(elementRef);
    else
      thisRef.selectedPagamenti = thisRef.selectedPagamenti.filter(e => e.codiceIud != elementRef.codiceIud || e.codiceIuv != elementRef.codiceIuv);
  }

  addSelected() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "70%";
    dialogConfig.data = {
      selectedPagamenti: this.selectedPagamenti,
      ente: this.ente,
      accertamentoId: this.accertamentoId,
      codTipo: this.accertamento.codTipoDovuto
    }
    let dialog = this.dialog.open(AccertamentiDettaglioAssociaDialogComponent, dialogConfig);
    dialog.afterClosed().subscribe(result => {
      if(result === 'updated') {
        this.selectedPagamenti = [];
        this.onSubmit();
        this.toastrService.success('I pagamenti aggiunti correttamente');
      }
    })
  }

  removeSelected() {
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.accertamentoService.deleteAccertamentoDettaglio(this.ente, this.accertamentoId, this.selectedPagamenti)
    .subscribe(num => {
      this.selectedPagamenti = [];
      this.toastrService.success('I pagamenti cancellati correttamente');
      this.onSubmit();
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore remuovendo i pagamenti', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  onSubmit(firstPageLoading: boolean = false){
    const i = this.form.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let dtEsitoFrom = i.dtEsitoFrom || null;
    let dtEsitoTo = i.dtEsitoTo || null;
    let dtUltimoAggFrom = i.dtUltimoAggFrom || null;
    let dtUltimoAggTo = i.dtUltimoAggTo || null;

    let searchFun;
    if (this.associaMode === 'associa')
      searchFun = this.accertamentoService.getAccertamentiPagamentiInseribili.bind(this.accertamentoService, this.ente, this.accertamentoId, dtEsitoFrom, dtEsitoTo, dtUltimoAggFrom, dtUltimoAggTo, i.iud, i.iuv, i.cfPagatore);
    else if (this.associaMode === 'deassocia')
      searchFun = this.accertamentoService.getAccertamentoDettaglio.bind(this.accertamentoService, this.ente, this.accertamentoId, dtEsitoFrom, dtEsitoTo, dtUltimoAggFrom, dtUltimoAggTo, i.iud, i.iuv, i.cfPagatore);

    this.breadcrumbsService.updateCurrentBreadcrumb(this.titleLabel);

    searchFun().subscribe(data => {
      this.hasSearched = true;
      this.tableData = data;
      //close search panel if data found
      if(data?.length > 0 && !firstPageLoading)
        this.mypSearchChips.setSearchPanelState(false);
      this.selectedPagamenti = [];
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  onReset(){
    this.form.reset();
    this.hasSearched = false;
    this.tableData = null;
    this.selectedPagamenti = [];
  }

  goBack() {
    this.location.back();
  }

  onRemoveFilter(thisRef: AccertamentiDettaglioAssociaComponent) {
    //redo the search
    if(thisRef.hasSearched)
      thisRef.onSubmit();
  }
}
