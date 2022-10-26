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
import * as _ from 'lodash';
import { ToastrService } from 'ngx-toastr';
import {
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    manageError, MyPayBreadcrumbsService, OverlaySpinnerService, PageStateService, validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

import { Location } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { faBriefcase } from '@fortawesome/free-solid-svg-icons';

import { Accertamento } from '../../model/accertamento';
import { Ente } from '../../model/ente';
import { TipoDovuto } from '../../model/tipo-dovuto';
import { AccertamentoService } from '../../services/accertamento.service';
import { EnteService } from '../../services/ente.service';

@Component({
  selector: 'app-accertamenti-creazione',
  templateUrl: './accertamenti-creazione.component.html',
  styleUrls: ['./accertamenti-creazione.component.scss'],
})
export class AccertamentiCreazioneComponent implements OnInit, WithTitle {

  @ViewChild('sForm') insertFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<Accertamento>;

  get titleLabel(){ return this.pageTitle || "Crea nuovo / modifica" }
  get titleIcon(){ return faBriefcase }
  
  tipoDovutoOptionsMap: Map<String, TipoDovuto[]>;
  tipoDovutoOptions: TipoDovuto[];
  tipoDovutoFilteredOptions: Observable<TipoDovuto[]>;

  blockingError: boolean = false;
  modeAnag: string = 'insert'; // 'insert' or 'edit'
  private savedAccertamento: Accertamento; // Valued in'edit' mode
  private rtPresent: boolean = false;
  private tipoDovuoUpdated:boolean=false;

  private valueChangedSub: Subscription;

  ente: Ente;
  private pageTitle: string;
  private previousPageNavId: number;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private enteService: EnteService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private accertamentoService: AccertamentoService,
    private location: Location,
    private breadcrumbsService: MyPayBreadcrumbsService,
    private pageStateService: PageStateService,
  ) {
    this.insertForm = this.formBuilder.group({
      deNomeAccertamento: ['', [Validators.required]],
      tipoDovuto: [null, [this.tipoDovutoValidator, Validators.required]],
    });

    const paramMap = this.route.snapshot.paramMap;
    if (paramMap?.get('savedAccertamento')) {
      this.modeAnag = 'edit';
      this.savedAccertamento = JSON.parse(paramMap.get('savedAccertamento'));
      this.pageTitle = 'Modifica';
      if (paramMap?.get('rtPresent') && paramMap?.get('rtPresent') == "true") {
        this.rtPresent = true;
      }
    } else {
      this.modeAnag = 'insert';
      this.pageTitle = 'Crea nuovo';
    }
    this.breadcrumbsService.updateCurrentBreadcrumb(this.pageTitle);

    this.previousPageNavId = this.router.getCurrentNavigation()?.extras?.state?.backNavId;
  }

  ngOnDestroy(): void {
    this.valueChangedSub?.unsubscribe();
  }

  ngOnInit(): void {

    this.tipoDovutoOptionsMap = new Map();

    this.valueChangedSub = this.insertForm.valueChanges.subscribe(validateFormFun(this.insertForm, this.insertFormErrors));

    this.enteService.getCurrentEnteObs().subscribe(ente => {
      this.ente = ente;
      this.onReset(ente);
    });
  }

  insertForm: FormGroup;
  insertFormErrors = {};

  tipoDovutoDisplayFn(tipoDovuto: TipoDovuto): string {
    return tipoDovuto ? tipoDovuto.deTipo : '';
  }

  private _tipoDovutoFilter(name: string): TipoDovuto[] {
    const filterValue = name.toLowerCase();
    return this.tipoDovutoOptions.filter(option => option.deTipo.toLowerCase().indexOf(filterValue) !== -1);
  }

  private tipoDovutoValidator = (control: AbstractControl):{[key: string]: boolean} | null => {
    return ( !control.value || control.value.mygovEnteTipoDovutoId != null ) ? null : {'invalid': true};
  };

  // Ente in param is valued when changed newly.
  onReset(ente?: Ente) {
    this.insertForm.reset();
    if(ente && ente.mygovEnteId){
      //retrieve list of tipoDovuto and prepare autocomplete
      this.insertForm.controls['tipoDovuto'].setValue(null);
      if(!this.tipoDovutoOptionsMap.has(ente.codIpaEnte)){
        this.enteService.getListTipoDovutoByEnteAsOperatore(ente).subscribe(tipiDovuto => {
          this.tipoDovutoOptionsMap.set(ente.codIpaEnte, tipiDovuto);
          this.tipoDovutoOptions = this.tipoDovutoOptionsMap.get(ente.codIpaEnte);
          this.tipoDovutoFilteredOptions = this.insertForm.get('tipoDovuto').valueChanges
          .pipe(
            startWith(''),
            map(value => typeof value === 'string' || !value ? value : value.deTipo),
            map(deTipoDovuto => deTipoDovuto ? this._tipoDovutoFilter(deTipoDovuto) : this.tipoDovutoOptions.slice())
          );
          if (this.savedAccertamento) {
            this.insertForm.get('deNomeAccertamento').setValue(this.savedAccertamento.deNomeAccertamento);
            this.insertForm.get('tipoDovuto').setValue(
                this.tipoDovutoOptions.filter(t => t.codTipo === this.savedAccertamento.codTipoDovuto)[0]
            );
          }
        }, manageError('Errore caricando l\'elenco dei tipi dovuto', this.toastrService, ()=>{this.blockingError=true}) );
      } else {
        this.tipoDovutoOptions = this.tipoDovutoOptionsMap.get(ente.codIpaEnte);
        this.tipoDovutoFilteredOptions = this.insertForm.get('tipoDovuto').valueChanges
        .pipe(
          startWith(''),
          map(value => typeof value === 'string' || !value ? value : value.deTipo),
          map(deTipoDovuto => deTipoDovuto ? this._tipoDovutoFilter(deTipoDovuto) : this.tipoDovutoOptions.slice())
        );
        if (this.savedAccertamento) {
          this.insertForm.get('deNomeAccertamento').setValue(this.savedAccertamento.deNomeAccertamento);
          this.insertForm.get('tipoDovuto').setValue(
              this.tipoDovutoOptions.filter(t => t.codTipo === this.savedAccertamento.codTipoDovuto)[0]
          );
        }
      }
    } else {
      this.tipoDovutoOptions = [];
    }
  }

  goback() {
    this.location.back();
  }

  saveConfirmMsg(thisRef: AccertamentiCreazioneComponent): string | { message: string, invalid: boolean } {
    this.tipoDovuoUpdated = false;
    if (thisRef.modeAnag === 'insert') {
      return "Confermi l'inserimento del nuovo accertamento?";
    } else {
      let msg = ["Confermi la modifica dei seguenti campi?"];
      ['deNomeAccertamento', 'tipoDovuto'].filter(field => thisRef.isFieldModified(field))
        .forEach(field => {
          if (field == 'tipoDovuto')
            this.tipoDovuoUpdated = true;
          msg.push(this.createMsgByField(thisRef, field))
          console.log(msg)
        });
      if (this.tipoDovuoUpdated && this.rtPresent) {
        msg.push('<br>ATTENZIONE, modifica non possibile perché ci sono delle RT associate e per modificare il Tipo Dovuto dell’Accertamento devi prima rimuovere le RT');
        let message = msg.join("<br>");
        return { message: message, invalid: true }
      };
      return msg.join("<br>").replace('deNomeAccertamento', 'NomeAccertamento');
    }
  }

  private isFieldModified(field: string){
    if ('tipoDovuto' === field)
      return this.savedAccertamento?.codTipoDovuto !== this.insertForm.get('tipoDovuto').value?.codTipo;
    else {
      return this.savedAccertamento[field] !== this.insertForm.get(field).value;
    }
  }

  private createMsgByField(thisRef: AccertamentiCreazioneComponent, field: string): string {
    thisRef.savedAccertamento[field]
    if ('tipoDovuto' === field)
      return `${field}: ${thisRef.savedAccertamento?.deTipoDovuto} -> ${thisRef.insertForm.get('tipoDovuto').value?.deTipo}`;
    else
      return `${field}: ${thisRef.savedAccertamento[field]} -> ${thisRef.insertForm.get(field).value}`;
  }

  onSubmit(){
    const i = this.insertForm.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let tipoDovuto = this.insertForm.get('tipoDovuto').value as TipoDovuto;
    let upsertFn;
    if (this.modeAnag === 'insert')
      upsertFn = this.accertamentoService.insert.bind(this.accertamentoService, this.ente, tipoDovuto, i.deNomeAccertamento);
    else
      upsertFn = this.accertamentoService.update.bind(this.accertamentoService, this.ente, this.savedAccertamento.id, tipoDovuto, i.deNomeAccertamento);
    upsertFn().subscribe(accertamento => {
      this.toastrService.success('Nuovo accertamento salvato correttamente');
      this.overlaySpinnerService.detach(spinner);
      //go to detail
      if (this.modeAnag === 'insert'){
        this.router.navigate(['accertamenti', 'dettaglio', accertamento.id]);
      }
      if(!_.isNil(this.previousPageNavId)){
        this.pageStateService.addToSavedState(this.previousPageNavId, "modifiedAccertamento", accertamento);
      }
      this.modeAnag = 'view';
    }, manageError('Errore salvando l\'accertamento', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

}
