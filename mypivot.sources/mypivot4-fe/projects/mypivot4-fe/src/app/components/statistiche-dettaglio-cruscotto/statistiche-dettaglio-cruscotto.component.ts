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
    DateValidators, manageError, OverlaySpinnerService, PaginatorData, PATTERNS, TableColumn,
    validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { combineLatest, Subscription } from 'rxjs';

import { animate, state, style, transition, trigger } from '@angular/animations';
import { CurrencyPipe, DatePipe, Location } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { faTable } from '@fortawesome/free-solid-svg-icons';

import { Ente } from '../../model/ente';
import { FlussoRicevuta } from '../../model/flusso-ricevuta';
import { TipoDovuto } from '../../model/tipo-dovuto';
import { EnteService } from '../../services/ente.service';
import { StatisticaService } from '../../services/statistica.service';

@Component({
  selector: 'app-statistiche-dettaglio-cruscotto',
  templateUrl: './statistiche-dettaglio-cruscotto.component.html',
  styleUrls: ['./statistiche-dettaglio-cruscotto.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ]
})
export class StatisticheDettaglioCruscottoComponent implements OnInit, OnDestroy, WithTitle {

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<FlussoRicevuta>;

  get titleLabel(){ return "Dettaglio cruscotto" }
  get titleIcon(){ return faTable }

  hasSearched: boolean = false;
  blockingError: boolean = false;

  tableDatailColumnsName = ['key','value'];
  expandedElement: FlussoRicevuta | null;

  private enteChangesSub: Subscription;
  private ente: Ente;
  private codTipo: string;
  codUfficio: string;
  deUfficio: string;
  codCapitolo: string;
  deCapitolo: string;

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private location: Location,
    private enteService: EnteService,
    private statisticaService: StatisticaService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
  ) {

    this.searchForm = this.formBuilder.group({
      dateFrom: [null],
      dateTo: [null],
      iuv: [''],
      iur: [''],
      attestante: [''],
      cfPagatore: ['',[Validators.pattern(PATTERNS.codiceFiscaleOPartitaIva)]],
      anagPagatore: [''],
      cfVersante: ['',[Validators.pattern(PATTERNS.codiceFiscaleOPartitaIva)]],
      anagVersante: [''],
    }, { validators: DateValidators.dateRange('dateFrom','dateTo') });

    this.searchFormChangesSub = this.searchForm.valueChanges.subscribe(validateFormFun(this.searchForm, this.searchFormErrors));
  }

  ngOnInit(): void {

    this.route.queryParams.subscribe(params => {
      this.codTipo = params['codTipo'];
      this.codUfficio = params['codUfficio'];
      this.codCapitolo = params['codCapitolo'];
      let dateFrom = null, dateTo = null;
      if (params['date']?.length === 4) {
        dateFrom = DateTime.fromFormat(params['date'], 'yyyy');
        dateTo = DateTime.fromFormat(params['date'], 'yyyy').plus({years: 1}).plus({days: -1});
      } else if (params['date']?.length === 6) {
        dateFrom = DateTime.fromFormat(params['date'], 'yyyyMM');
        dateTo = DateTime.fromFormat(params['date'], 'yyyyMM').plus({months: 1}).plus({days: -1});
      } else if (params['date']?.length === 8) {
        dateFrom = DateTime.fromFormat(params['date'], 'yyyyMMdd');
        dateTo = DateTime.fromFormat(params['date'], 'yyyyMMdd');
      }
      this.searchForm.get('dateFrom').setValue(dateFrom);
      this.searchForm.get('dateTo').setValue(dateTo);
    });

    this.enteChangesSub = this.enteService.getCurrentEnteObs().subscribe(ente => {
      const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
      let tipoDovuto = new TipoDovuto();
      tipoDovuto.codTipo = this.codTipo;
      combineLatest([
       this.statisticaService.getUffici(ente, tipoDovuto),
       this.statisticaService.getCapitoli(ente, tipoDovuto, this.codUfficio)
      ]).subscribe( ([uffici, capitoli]) => {
          let ufficio = uffici.filter(u => u.codUfficio === this.codUfficio)[0] || null;
          this.deUfficio = ufficio?.deUfficio;
          let capitolo = capitoli.filter(c => c.codCapitolo === this.codCapitolo)[0] || null;
          this.deCapitolo = capitolo?.deCapitolo;
          this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore recuperando il dettaglio Ufficio/Capitolo', this.toastrService, () => {this.blockingError=true; this.overlaySpinnerService.detach(spinner)}) );
      this.tableData = new Array();
      this.ente = ente;
      if (this.ente) {
        this.onSubmit();
      }
    });
  }

  ngOnDestroy():void {
    this.searchFormChangesSub?.unsubscribe();
    this.enteChangesSub?.unsubscribe();
  }

  searchForm: FormGroup;
  searchFormErrors = {};
  private searchFormChangesSub:Subscription;

  tableColumns: TableColumn[] = [
    new TableColumn('deTipoDovuto', 'Tipo dovuto'),
    new TableColumn('codESoggPagAnagraficaPagatore', 'Pagatore'),
    new TableColumn('codRpSilinviarpIdUnivocoVersamento', 'IUV'),
    new TableColumn('numEDatiPagDatiSingPagSingoloImportoPagato','Importo', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'] } ),
    new TableColumn('dtEDatiPagDatiSingPagDataEsitoSingoloPagamento', 'Data esito', { sortable: true, pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
  ];
  tableData: FlussoRicevuta[];
  paginatorData: PaginatorData;

  goBack() {
    this.location.back();
  }

  onSubmit(){
    const i = this.searchForm.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let dateFrom = i.dateFrom || null;
    let dateTo = i.dateTo || null;

    this.statisticaService.getRicevuteTelematiche(this.ente, this.codTipo, this.codUfficio, this.codCapitolo,
        dateFrom, dateTo, i.iuv, i.iur, i.attestante, i.cfPagatore, i.anagPagatore, i.cfVersante, i.anagVersante).subscribe(data => {
      this.hasSearched = true;
      this.tableData = data;
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  onReset(){
    this.searchForm.reset();
    this.searchForm.get('dateFrom').setValue(DateTime.now().startOf('day').minus({month: 1}));
    this.searchForm.get('dateTo').setValue(DateTime.now().startOf('day'));
    this.searchForm.get('dateFrom').disable();
    this.searchForm.get('dateTo').disable();
    this.hasSearched = false;
    this.tableData = null;
  }

  onClickRow(element:FlussoRicevuta, thisRef: StatisticheDettaglioCruscottoComponent) {
    if(element.details)
      return;
    element.details = [
      {key:'IUD', value:element.codIud},
      {key:'IUR', value:element.codEDatiPagDatiSingPagIdUnivocoRiscoss},
      {key:'Attestante', value:element.deEIstitAttDenominazioneAttestante},
      {key:'Pagatore', value:`${element.codESoggPagAnagraficaPagatore}  [CF/PIVA: ${element.codESoggPagIdUnivPagCodiceIdUnivoco} ${element.tipoIdUnivocoPagatore}]`},
      {key:'Causale', value:element.deEDatiPagDatiSingPagCausaleVersamento},
    ]
    if(element.codESoggVersAnagraficaVersante)
      element.details.splice(4, 0, {key:'Versante', value:`${element.codESoggVersAnagraficaVersante}  [CF/PIVA: ${element.codESoggVersIdUnivVersCodiceIdUnivoco} ${element.tipoIdUnivocoVersante}]`});
  }

}
