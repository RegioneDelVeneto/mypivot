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
    KeyValue, MyPayTableDetailComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table-detail/my-pay-table-detail.component';
import {
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import {
    MypSearchChipsComponent
} from 'projects/mypay4-fe-common/src/lib/components/myp-search-chips/myp-search-chips.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    DateValidators, manageError, MapPipe, OverlaySpinnerService, PaginatorData, PATTERNS,
    SearchFilterDef, TableColumn, validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

import { CurrencyPipe, DatePipe, Location } from '@angular/common';
import { Component, ElementRef, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { faInfoCircle, faLink } from '@fortawesome/free-solid-svg-icons';

import { FlussoRicevuta } from '../../model/flusso-ricevuta';
import { RendicontazioneDetail } from '../../model/rendicontazione-detail';
import { TipoDovuto } from '../../model/tipo-dovuto';
import { EnteService } from '../../services/ente.service';
import { RendicontazioneService } from '../../services/rendicontazione.service';
import { RicevuteTelematicheService } from '../../services/ricevute-telematiche.service';

@Component({
  selector: 'app-rendicontazione-detail',
  templateUrl: './rendicontazione-detail.component.html',
  styleUrls: ['./rendicontazione-detail.component.scss']
})
export class RendicontazioneDetailComponent implements OnInit, OnDestroy, WithTitle {

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<RendicontazioneDetail>;
  @ViewChild('mypSearchChips') mypSearchChips: MypSearchChipsComponent;

  get titleLabel(){ return this.title || "Dettaglio" }
  get titleIcon(){ return faLink }

  title: string;
  iconInfoCircle = faInfoCircle;
  infoTextBox: string;
  @Input() iuf: string;
  @Input() iur: string;
  isStandaloneView: boolean = true;
  tableData: FlussoRicevuta[];
  paginatorData: PaginatorData;
  hasSearched: boolean = false;
  blockingError: boolean = false;
  dataParent: KeyValue[];
  typeSearch:string;

  tableColumns: TableColumn[] = [
    new TableColumn('deTipoDovuto', 'Tipo dovuto', { pipe: MapPipe, pipeArgs: [{},'n/d'] }),
    new TableColumn('codESoggPagAnagraficaPagatore', 'Pagatore', { pipe: MapPipe, pipeArgs: [{},'n/d'] }),
    new TableColumn('codRpSilinviarpIdUnivocoVersamento', 'IUV'),
    new TableColumn('codEDatiPagDatiSingPagIdUnivocoRiscoss', 'IUR'),
    new TableColumn('numEDatiPagDatiSingPagSingoloImportoPagato', 'Importo', { sortable: true, pipe: CurrencyPipe, pipeArgs: ['EUR', 'symbol'] }),
    new TableColumn('dtEDatiPagDatiSingPagDataEsitoSingoloPagamento', 'Data esito', { sortable: true, pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] }),
    new TableColumn('rowActions', 'Azioni', { sortable: false, tooltip: 'Azioni', actions: [
    ] } ) ];
  private enteChangesSub: Subscription;
  private formChangesSub: Subscription;
  formDef: { [key: string]: SearchFilterDef };
  form: FormGroup;
  formErrors = {};
  tipoDovutoOptionsMap: Map<String, TipoDovuto[]>;
  tipoDovutoOptions: TipoDovuto[];
  tipoDovutoFilteredOptions: Observable<TipoDovuto[]>;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private enteService: EnteService,
    private rendicontazioneService: RendicontazioneService,
    private ricevuteTelematicheSercice: RicevuteTelematicheService,
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
    let ente = this.enteService.getCurrentEnte();
    if (ente && ente.mygovEnteId) {
      this.tipoDovutoOptionsMap = new Map();
      //retrieve list of tipoDovuto and prepare autocomplete
      this.form.controls['tipoDovuto'].setValue(null);
      if (!this.tipoDovutoOptionsMap.has(ente.codIpaEnte)) {
        this.enteService.getListTipoDovutoByEnteAsOperatore(ente).subscribe(tipiDovuto => {
          this.tipoDovutoOptionsMap.set(ente.codIpaEnte, tipiDovuto);
          this.tipoDovutoOptions = this.tipoDovutoOptionsMap.get(ente.codIpaEnte);
          this.tipoDovutoFilteredOptions = this.form.get('tipoDovuto').valueChanges
            .pipe(
              startWith(''),
              map(value => typeof value === 'string' || !value ? value : value.deTipo),
              map(deTipoDovuto => deTipoDovuto ? this._tipoDovutoFilter(deTipoDovuto) : this.tipoDovutoOptions.slice())
            );
        }, manageError('Errore caricando l\'elenco dei tipi dovuto', this.toastrService, () => { this.blockingError = true }));
      } else {
        this.tipoDovutoOptions = this.tipoDovutoOptionsMap.get(ente.codIpaEnte);
        this.tipoDovutoFilteredOptions = this.form.get('tipoDovuto').valueChanges
          .pipe(
            startWith(''),
            map(value => typeof value === 'string' || !value ? value : value.deTipo),
            map(deTipoDovuto => deTipoDovuto ? this._tipoDovutoFilter(deTipoDovuto) : this.tipoDovutoOptions.slice())
          );
      }
      this.title = 'Rendicontazione - dettaglio';
      this.infoTextBox = 'Stai visualizzando il dettaglio della rendicontazione (solo tipo dovuto abilitati per l\'operatore)';
      const params = this.route.snapshot.params;
      this.typeSearch=params.searchType;
      if(this.iuf) {
        this.isStandaloneView = false;
        this.title = params.searchType=='IUV_NO_RT'?'Pagamenti privi di Ricevuta Telematica':'Ricevute telematiche';
      } else {
        this.isStandaloneView = true;
        this.iuf = params['iuf'] || null;
        this.iur = params['iur'] || null;
      }

      const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
      this.rendicontazioneService.detail(this.enteService.getCurrentEnte(), this.iuf, this.iur)
        .subscribe(data => {
          this.tableData = data.details;
          this.dataParent = this.rendicontazioneService.mapDataToShow(data);
          this.hasSearched = true;
          if (data.details?.length > 0)
            this.mypSearchChips.setSearchPanelState(false);
          this.overlaySpinnerService.detach(spinner);
        }, manageError('Errore recuperando il dettaglio', this.toastrService, () => { this.overlaySpinnerService.detach(spinner) }));
    } else {
      //redirect to search view
      this.router.navigate(['visualizzazione', 'rendicontazione']);
    }
  }



  onSubmit() {
    const searchParams = lodash.clone(this.form.value);
    searchParams.tipoDovuto = searchParams.tipoDovuto?.codTipo;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);

    let filterPresent = false;
    let searchProp = Object.values(searchParams).forEach(elem => {
      if (!(elem === null || elem === '' || elem === undefined))
        filterPresent = true
    })

    if (filterPresent) {
      this.rendicontazioneService.filterDetail(this.enteService.getCurrentEnte(), this.iuf, this.iur, searchParams)
        .subscribe(data => {
          this.hasSearched = true;
          this.tableData = data.details;
          //close search panel if data found
          if (data && data.details?.length > 0)
            this.mypSearchChips.setSearchPanelState(false);
          this.overlaySpinnerService.detach(spinner);
        }, manageError('Errore effettuando la ricerca', this.toastrService, () => { this.overlaySpinnerService.detach(spinner) }));
    } else {
      this.rendicontazioneService.detail(this.enteService.getCurrentEnte(), this.iuf, this.iur)
        .subscribe(data => {
          this.tableData = data.details;
          this.dataParent = this.rendicontazioneService.mapDataToShow(data);
          this.hasSearched = true;
          if (data.details?.length > 0)
            this.mypSearchChips.setSearchPanelState(false);
          this.overlaySpinnerService.detach(spinner);
        }, manageError('Errore recuperando il dettaglio', this.toastrService, () => { this.overlaySpinnerService.detach(spinner) }));
    }
  }

  onReset() {
    this.form.reset();
    lodash.forOwn(this.formDef, (value, key) => this.form.get(key).setValue(value.value));
  }

  onRemoveFilter(thisRef: RendicontazioneDetailComponent) {
    //redo the search
    if (thisRef.hasSearched)
      thisRef.onSubmit();
  }

  onClickRow(element: FlussoRicevuta, thisRef: RendicontazioneDetailComponent) {
    if(element.details)
     return;
    
    const tipoPersonaMap = { 'F': '(Persona fisica)', 'G': '(Persona giuridica)' };
    const details = []
    
    if (thisRef.typeSearch != 'IUV_NO_RT') {
      details.push(...[
        { key: 'IUD', value: element.codIud },
        { key: 'IUR', value: element.codEDatiPagDatiSingPagIdUnivocoRiscoss },
        { key: 'Attestante', value: element.deEIstitAttDenominazioneAttestante },
        {
          key: 'Pagatore',
          value: `${element.codESoggPagAnagraficaPagatore} [CF/PIVA: ${element.codESoggPagIdUnivPagCodiceIdUnivoco} ${tipoPersonaMap[element.codESoggPagIdUnivPagTipoIdUnivoco] || ''}]`
        },
        { key: 'Causale', value: element.deEDatiPagDatiSingPagCausaleVersamento },
      ]);}
      if (element.codESoggVersAnagraficaVersante && thisRef.typeSearch != 'IUV_NO_RT')
        details.splice(4, 0,
          {
            key: 'Versante',
            value: `${element.codESoggVersAnagraficaVersante} [CF/PIVA: ${element.codESoggVersIdUnivVersCodiceIdUnivoco} ${tipoPersonaMap[element.codESoggVersIdUnivVersTipoIdUnivoco] || ''}]`
          }
        );
    
    if(this.isStandaloneView){
      element.details = details;
      return;
    } else {
      return thisRef.ricevuteTelematicheSercice.getMypayInfo(
        thisRef.enteService.getCurrentEnte().mygovEnteId, element.codRpSilinviarpIdUnivocoVersamento, element.codFiscalePa1)
        .pipe(map(mypayInfo => {
          element.details = details;
          if(mypayInfo.codRpSilinviarpIdUnivocoVersamento)
            element.details.push( ...[
              {key:MyPayTableDetailComponent.SECTION_ID, value:'Info da MyPay'},
              {key:'IUD', value:mypayInfo.codIud},
              {key:'Anagrafica pagatore', value:mypayInfo.codESoggPagAnagraficaPagatore},
              {key:'Codice fiscale pagatore', value:mypayInfo.codESoggPagIdUnivPagCodiceIdUnivoco},
              {key:'Stato', value:mypayInfo.deStato},
            ] );
          else
            element.details.push( ...[
              {key:MyPayTableDetailComponent.SECTION_ID, value:'Info da MyPay'},
              {key:'Errore', value:'Nessun dato trovato'},
            ] );
        }));
    }
  }

  ngOnDestroy(): void {
    this.formChangesSub?.unsubscribe();
    this.enteChangesSub?.unsubscribe();
  }

  back() {
    this.location.back();
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
