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
    DateValidators, manageError, OverlaySpinnerService, PageStateService, PaginatorData, PATTERNS,
    SearchFilterDef, TableColumn, validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { faInfoCircle, faReceipt } from '@fortawesome/free-solid-svg-icons';

import { Ente } from '../../model/ente';
import { FlussoRicevuta } from '../../model/flusso-ricevuta';
import { Rendicontazione } from '../../model/rendicontazione';
import { TipoDovuto } from '../../model/tipo-dovuto';
import { EnteService } from '../../services/ente.service';
import { RicevuteTelematicheService } from '../../services/ricevute-telematiche.service';

@Component({
  selector: 'app-ricevute-telematiche-visualizzazione',
  templateUrl: './ricevute-telematiche-visualizzazione.component.html',
  styleUrls: ['./ricevute-telematiche-visualizzazione.component.scss']
})
export class RicevuteTelematicheVisualizzazioneComponent implements OnInit, OnDestroy, WithTitle {

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<Rendicontazione>;
  @ViewChild('mypSearchChips') mypSearchChips: MypSearchChipsComponent;

  get titleLabel(){ return "Ricevute Telematiche" }
  get titleIcon(){ return faReceipt }

  iconInfoCircle = faInfoCircle;
  infoTextBox: string;
  hasSearched: boolean = false;
  blockingError: boolean = false;
  formDef: { [key: string]: SearchFilterDef };
  form: FormGroup;
  formErrors = {};
  private formChangesSub: Subscription;
  private enteChangesSub: Subscription;

  tipoDovutoOptionsMap: Map<String, TipoDovuto[]>;
  tipoDovutoOptions: TipoDovuto[];
  tipoDovutoFilteredOptions: Observable<TipoDovuto[]>;

  tableColumns: TableColumn[] = [
    new TableColumn('deTipoDovuto', 'Tipo dovuto'),
    new TableColumn('codESoggPagAnagraficaPagatore', 'Pagatore'),
    new TableColumn('codRpSilinviarpIdUnivocoVersamento', 'IUV'),
    new TableColumn('numEDatiPagDatiSingPagSingoloImportoPagato', 'Importo', { sortable: true, pipe: CurrencyPipe, pipeArgs: ['EUR', 'symbol'] }),
    new TableColumn('dtEDatiPagDatiSingPagDataEsitoSingoloPagamento', 'Data esito', { sortable: true, pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] }),
  ];
  tableData: FlussoRicevuta[];
  paginatorData: PaginatorData;


  constructor(
    private formBuilder: FormBuilder,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private pageStateService: PageStateService,
    private enteService: EnteService,
    private ricevuteTelematicheService: RicevuteTelematicheService,
  ) {

    this.formDef = [
      new SearchFilterDef('dateEsitoFrom', 'Data esito da', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('dateEsitoTo', 'Data esito a', null, [], v => v?.toFormat('dd/MM/yyyy')),
      new SearchFilterDef('iud', 'IUD', '', []),
      new SearchFilterDef('iuv', 'IUV', '', []),
      new SearchFilterDef('iur', 'IUR', '', []),
      new SearchFilterDef('attestante', 'Attestante', '', []),
      new SearchFilterDef('codFiscalePagatore', 'CF/PIVA pagatore', '', [Validators.pattern(PATTERNS.codiceFiscaleOPartitaIva)]),
      new SearchFilterDef('anagPagatore', 'Anagrafica pagatore', '', []),
      new SearchFilterDef('codFiscaleVersante', 'CF/PIVA versante', '', [Validators.pattern(PATTERNS.codiceFiscaleOPartitaIva)]),
      new SearchFilterDef('anagVersante', 'Anagrafica versante', '', []),
      new SearchFilterDef('tipoDovuto', 'Tipo dovuto', '', [this.tipoDovutoValidator], v => v?.deTipo),
    ].reduce((formObj, elem) => { formObj[elem.field] = elem; return formObj }, {});

    const formObj = lodash.mapValues(this.formDef, x => [
      lodash.isFunction(x.value) ? x.value() : x.value,
      x.validators]);

    this.form = this.formBuilder.group(formObj, {
      validators: [
        DateValidators.dateRangeForRangePicker('dateEsitoFrom', 'dateEsitoTo')
      ]
    });

    this.formChangesSub = this.form.valueChanges.subscribe(validateFormFun(this.form, this.formErrors));
  }

  ngOnInit(): void {
    this.infoTextBox = 'Stai visualizzando il dettaglio della Rendicontazione che contiene l\’elenco delle RT presenti all\’interno del flusso (solo per i Tipi Dovuto abilitati per la tua utenza)';
    this.tipoDovutoOptionsMap = new Map();
    this.enteChangesSub = this.enteService.getCurrentEnteObs().subscribe(value => this.onChangeEnte(this, value));

    //retrieve page state data if navigating back
    if (this.pageStateService.isNavigatingBack()) {
      const pageState = this.pageStateService.restoreState();
      if (pageState) {
        this.form.setValue(lodash.assign(this.form.value, pageState.formData));
        setTimeout(() => {
          this.tableData = pageState.tableData;
          this.paginatorData = pageState.paginatorData;
          if (this.tableData) {
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

  onSubmit() {
    const searchParams = lodash.clone(this.form.value);
    searchParams.tipoDovuto = searchParams.tipoDovuto?.codTipo;
    this.tableData = [];
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.ricevuteTelematicheService.search(this.enteService.getCurrentEnte(), searchParams).subscribe(data => {
      this.hasSearched = true;
      this.tableData = data;
      //close search panel if data found
      if (data?.length > 0)
        this.mypSearchChips.setSearchPanelState(false);
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', this.toastrService, () => { this.overlaySpinnerService.detach(spinner) }));
  }

  onRemoveFilter(thisRef: RicevuteTelematicheVisualizzazioneComponent) {
    //redo the search
    if (thisRef.hasSearched)
      thisRef.onSubmit();
  }

  onReset() {
    this.form.reset();
    lodash.forOwn(this.formDef, (value, key) => this.form.get(key).setValue(value.value));
    this.hasSearched = false;
    this.tableData = null;
  }

  private onChangeEnte(thisRef: RicevuteTelematicheVisualizzazioneComponent, ente: Ente) {
    if (ente && ente.mygovEnteId) {
      //retrieve list of tipoDovuto and prepare autocomplete
      thisRef.form.controls['tipoDovuto'].setValue(null);
      if (!thisRef.tipoDovutoOptionsMap.has(ente.codIpaEnte)) {
        thisRef.enteService.getListTipoDovutoByEnteAsOperatore(ente).subscribe(tipiDovuto => {
          thisRef.tipoDovutoOptionsMap.set(ente.codIpaEnte, tipiDovuto);
          thisRef.tipoDovutoOptions = thisRef.tipoDovutoOptionsMap.get(ente.codIpaEnte);
          thisRef.tipoDovutoFilteredOptions = thisRef.form.get('tipoDovuto').valueChanges
            .pipe(
              startWith(''),
              map(value => typeof value === 'string' || !value ? value : value.deTipo),
              map(deTipoDovuto => deTipoDovuto ? thisRef._tipoDovutoFilter(deTipoDovuto) : thisRef.tipoDovutoOptions.slice())
            );
        }, manageError('Errore caricando l\'elenco dei tipi dovuto', thisRef.toastrService, () => { thisRef.blockingError = true }));
      } else {
        thisRef.tipoDovutoOptions = thisRef.tipoDovutoOptionsMap.get(ente.codIpaEnte);
        thisRef.tipoDovutoFilteredOptions = thisRef.form.get('tipoDovuto').valueChanges
          .pipe(
            startWith(''),
            map(value => typeof value === 'string' || !value ? value : value.deTipo),
            map(deTipoDovuto => deTipoDovuto ? thisRef._tipoDovutoFilter(deTipoDovuto) : thisRef.tipoDovutoOptions.slice())
          );
      }
    }
    // //reopen search panel if closed
    thisRef.mypSearchChips?.setSearchPanelState(true);
    //reset search state
    thisRef.hasSearched = false;
    thisRef.tableData = null;
  }

  onClickRow(element: FlussoRicevuta, thisRef: RicevuteTelematicheVisualizzazioneComponent) {
    if(element.details)
      return;
    const tipoPersonaMap = { 'F': '(Persona fisica)', 'G': '(Persona giuridica)' };
    element.details = [
      { key: 'IUD', value: element.codIud },
      { key: 'IUR', value: element.codEDatiPagDatiSingPagIdUnivocoRiscoss },
      { key: 'Attestante', value: element.deEIstitAttDenominazioneAttestante },
      {
        key: 'Pagatore', 
        value: `${element.codESoggPagAnagraficaPagatore} [CF/PIVA: ${element.codESoggPagIdUnivPagCodiceIdUnivoco} ${tipoPersonaMap[element.codESoggPagIdUnivPagTipoIdUnivoco]}]`
      },
      { key: 'Causale', value: element.deEDatiPagDatiSingPagCausaleVersamento },
    ]
    if(element.codESoggVersAnagraficaVersante)
      element.details.splice(4, 0, {
          key: 'Versante',
          value: `${element.codESoggVersAnagraficaVersante} [CF/PIVA: ${element.codESoggVersIdUnivVersCodiceIdUnivoco} ${tipoPersonaMap[element.codESoggVersIdUnivVersTipoIdUnivoco]}]`
      });
  }

  tipoDovutoDisplayFn(tipoDovuto: TipoDovuto): string {
    return tipoDovuto ? tipoDovuto.deTipo : '';
  }

  private tipoDovutoValidator = (control: AbstractControl): { [key: string]: boolean } | null => {
    return (!control.value || control.value.mygovEnteTipoDovutoId != null) ? null : { 'invalid': true };
  };

  private _tipoDovutoFilter(name: string): TipoDovuto[] {
    const filterValue = name.toLowerCase();
    return this.tipoDovutoOptions.filter(option => option.deTipo.toLowerCase().indexOf(filterValue) !== -1);
  }
}
