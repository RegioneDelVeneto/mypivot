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
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    manageError, MyPayBreadcrumbsService, OverlaySpinnerService, validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';

import { Location } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDatepicker } from '@angular/material/datepicker';
import { ActivatedRoute, Router } from '@angular/router';
import { faListOl } from '@fortawesome/free-solid-svg-icons';

import { AccertamentoCapitolo } from '../../model/accertamento-capitolo';
import { AnagraficaUffCapAcc } from '../../model/anagrafica-uff-cap-acc';
import { Ente } from '../../model/ente';
import { TipoDovuto } from '../../model/tipo-dovuto';
import { AccertamentoService } from '../../services/accertamento.service';
import { EnteService } from '../../services/ente.service';

@Component({
  selector: 'app-accertamenti-capitoli-anagrafica',
  templateUrl: './accertamenti-capitoli-anagrafica.component.html',
  styleUrls: ['./accertamenti-capitoli-anagrafica.component.scss']
})
export class AccertamentiCapitoliAnagraficaComponent implements OnInit, WithTitle {

  @ViewChild('sForm') insertFormDirective;

  get titleLabel(){ return this.pageTitle || "Dettaglio" }
  get titleIcon(){ return faListOl }
  
  blockingError: boolean = false;

  private valueChangedSub: Subscription;

  private pageTitle: string;
  ente: Ente;
  modeAnag: string; // 'view', 'edit' or 'insert'
  anagraficaId: number;
  accertamentoCapitolo: AccertamentoCapitolo;

  tipiDovuto: TipoDovuto[];
  uffici: AnagraficaUffCapAcc[];
  capitoli: AnagraficaUffCapAcc[];
  accertamenti: AnagraficaUffCapAcc[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private formBuilder: FormBuilder,
    private enteService: EnteService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private accertamentoService: AccertamentoService,
    private breadcrumbsService: MyPayBreadcrumbsService,
  ) {
    const formObj = {
      tipoDovuto: [null],
      ufficio: [null],
      codUfficio: [''],
      deUfficio: [''],
      flgUfficioAttivo: [false],
      capitolo: [null],
      codCapitolo: [''],
      deCapitolo: [''],
      annoCapitolo: [DateTime.now().startOf('day').toFormat('yyyy')],
      dpAnnoCapitolo: [DateTime.now().startOf('day')],
      accertamento: [null],
      codAccertamento: [''],
      deAccertamento: [''],
    };

    this.insertForm = this.formBuilder.group(formObj);
  }

  ngOnDestroy(): void {
    this.valueChangedSub?.unsubscribe();
  }

  ngOnInit(): void {
    const params = this.route.snapshot.params;
    if (params['anagraficaId']) {
      this.setMode('view');
      this.anagraficaId = params['anagraficaId'];
    } else{
      this.setMode('insert');
    }

    this.valueChangedSub = this.insertForm.valueChanges.subscribe(validateFormFun(this.insertForm, this.insertFormErrors));

    this.enteService.getCurrentEnteObs().subscribe(ente => {
      this.ente = ente;
      this.loadTipoDovuto(ente);
    });
  }

  insertForm: FormGroup;
  insertFormErrors = {};

  minDate = DateTime.fromFormat('01/01/1970','dd/LL/yyyy');
  maxDate = DateTime.now().endOf('day');

  chosenYearly(normalizedDt: DateTime, datepicker: MatDatepicker<DateTime>) {
    datepicker.close();
    this.insertForm.get('annoCapitolo').setValue(normalizedDt.toFormat('yyyy'));
    let ufficio = this.insertForm.get('ufficio').value as AnagraficaUffCapAcc;
    if (ufficio && ufficio.codUfficio)
      this.ufficioOnChange(ufficio);
  }

  // Ente in param is valued when changed newly.
  private loadTipoDovuto(ente: Ente) {
    if(ente && ente.mygovEnteId){
      //retrieve list of tipoDovuto and prepare autocomplete
      this.insertForm.controls['tipoDovuto'].setValue(null);
      this.enteService.getListTipoDovutoByEnteAsOperatore(ente).subscribe(tipiDovuto => {
        this.tipiDovuto = tipiDovuto;
        if (this.anagraficaId)
          this.loadAnagrafica(this.anagraficaId);
      }, manageError('Errore caricando l\'elenco dei tipi dovuto', this.toastrService, ()=>{this.blockingError=true}) );
    }
  }

  compareTipoDovuto(o1: TipoDovuto, o2: TipoDovuto) {
    return o1.codTipo === o2?.codTipo;
  }

  compareUfficio(o1: AnagraficaUffCapAcc, o2: AnagraficaUffCapAcc) {
    return o1.codUfficio === o2?.codUfficio;
  }

  compareCapitolo(o1: AnagraficaUffCapAcc, o2: AnagraficaUffCapAcc) {
    return o1.codCapitolo === o2?.codCapitolo;
  }

  compareAccertamento(o1: AnagraficaUffCapAcc, o2: AnagraficaUffCapAcc) {
    return o1.codAccertamento === o2?.codAccertamento;
  }

  tipoDovutoOnChange(tipoDovuto: TipoDovuto) {
    this.insertForm.get('ufficio').setValue(null);
    this.insertForm.get('codUfficio').setValue(null);
    this.insertForm.get('deUfficio').setValue(null);
    this.insertForm.get('capitolo').setValue(null);
    this.insertForm.get('codCapitolo').setValue(null);
    this.insertForm.get('deCapitolo').setValue(null);
    this.insertForm.get('accertamento').setValue(null);
    this.insertForm.get('codAccertamento').setValue(null);
    this.insertForm.get('deAccertamento').setValue(null);
    this.capitoli = [];
    this.accertamenti = [];
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.accertamentoService.getComboUffici(this.ente, tipoDovuto?.codTipo).subscribe(
      uffici => {
        let nullUfficio = new AnagraficaUffCapAcc();
        if(uffici.length==0){
          nullUfficio.codUfficio='Non sono disponibili uffici per il tipo dovuto selezionato'
        }
        this.uffici = [nullUfficio].concat(uffici);
        this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore recuperando lista ufficio', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
    this.insertForm.get('ufficio').enable();
    this.insertForm.get('capitolo').disable();
    this.insertForm.get('accertamento').disable();
  }

  ufficioOnChange(ufficio: AnagraficaUffCapAcc) {
    this.insertForm.get('codUfficio').setValue(ufficio.codUfficio);
    this.insertForm.get('deUfficio').setValue(ufficio.deUfficio);
    this.insertForm.get('capitolo').setValue(null);
    this.insertForm.get('codCapitolo').setValue(null);
    this.insertForm.get('deCapitolo').setValue(null);
    this.insertForm.get('accertamento').setValue(null);
    this.insertForm.get('codAccertamento').setValue(null);
    this.insertForm.get('deAccertamento').setValue(null);
    this.capitoli = [];
    this.accertamenti = [];
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let tipoDovuto = this.insertForm.get('tipoDovuto').value as TipoDovuto;
    let annoCapitolo = this.insertForm.get('annoCapitolo').value;
    this.accertamentoService.getComboCapitoli(this.ente, tipoDovuto?.codTipo, ufficio.codUfficio, annoCapitolo).subscribe(
      capitoli => {
        let nullCapitolo = new AnagraficaUffCapAcc();
        this.capitoli = [nullCapitolo].concat(capitoli);
        this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore recuperando lista capitolo', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
    this.insertForm.get('capitolo').enable();
    this.insertForm.get('accertamento').disable();
  }

  capitoloOnChange(capitolo: AnagraficaUffCapAcc) {
    this.insertForm.get('codCapitolo').setValue(capitolo.codCapitolo);
    this.insertForm.get('deCapitolo').setValue(capitolo.deCapitolo);
    this.insertForm.get('accertamento').setValue(null);
    this.insertForm.get('codAccertamento').setValue(null);
    this.insertForm.get('deAccertamento').setValue(null);
    this.accertamenti = [];
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let tipoDovuto = this.insertForm.get('tipoDovuto').value as TipoDovuto;
    let annoCapitolo = this.insertForm.get('annoCapitolo').value;
    let ufficio = this.insertForm.get('ufficio').value;
    this.accertamentoService.getComboAccertamenti(this.ente, tipoDovuto?.codTipo, ufficio.codUfficio, annoCapitolo, capitolo.codCapitolo).subscribe(
      accertamenti => {
        let nullAccertamento = new AnagraficaUffCapAcc();
        this.accertamenti = [nullAccertamento].concat(accertamenti);
        this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore recuperando lista accertamento', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
    this.insertForm.get('accertamento').enable();
  }

  accertamentoOnChange(accertamento: AnagraficaUffCapAcc) {
    this.insertForm.get('codAccertamento').setValue(accertamento.codAccertamento);
    this.insertForm.get('deAccertamento').setValue(accertamento.deAccertamento);
  }

  goBack() {
    this.location.back();
  }

  enableEdit() {
    this.setMode('edit');
  }

  annulla() {
    this.setMode('view');
    this.setForm(this.accertamentoCapitolo);
  }

  onSubmit(){
    const i = this.insertForm.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let newAnagrafica = new AccertamentoCapitolo();
    if (this.modeAnag === 'edit')
      newAnagrafica.id = this.anagraficaId;
    let tipoDovuto = this.insertForm.get('tipoDovuto').value as TipoDovuto;
    newAnagrafica.codTipoDovuto = tipoDovuto.codTipo;
    newAnagrafica.deTipoDovuto = tipoDovuto.deTipo;
    newAnagrafica.codUfficio = i.codUfficio;
    newAnagrafica.deUfficio = i.deUfficio;
    newAnagrafica.flgAttivo = i.flgUfficioAttivo;
    newAnagrafica.codCapitolo = i.codCapitolo;
    newAnagrafica.deCapitolo = i.deCapitolo;
    newAnagrafica.deAnnoEsercizio = i.annoCapitolo;
    newAnagrafica.codAccertamento = i.codAccertamento;
    newAnagrafica.deAccertamento= i.deAccertamento;

    this.accertamentoService.putAccertamentoCapitolo(this.ente, newAnagrafica).subscribe(anagrafica => {
        this.setForm(anagrafica);
      this.accertamentoCapitolo = anagrafica;
        this.setMode('view');
        this.overlaySpinnerService.detach(spinner);
        this.toastrService.success('Anagrafica Salvata correttamente');
      }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  private loadAnagrafica(anagraficaId: number) {
    const i = this.insertForm.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.accertamentoService.getAccertamentoCapitolo(this.ente, anagraficaId).subscribe(anagrafica => {
      this.setForm(anagrafica);
      this.accertamentoCapitolo = anagrafica;
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  private setForm(anagrafica: AccertamentoCapitolo) {
    this.anagraficaId = anagrafica.id || null;
    let tipoDovuto = this.tipiDovuto.filter(td => td.codTipo === anagrafica.codTipoDovuto)[0];
    this.insertForm.get('tipoDovuto').setValue(tipoDovuto),
    this.insertForm.get('codUfficio').setValue(anagrafica.codUfficio);
    this.insertForm.get('deUfficio').setValue(anagrafica.deUfficio);
    this.insertForm.get('flgUfficioAttivo').setValue(anagrafica.flgAttivo);
    this.insertForm.get('codCapitolo').setValue(anagrafica.codCapitolo);
    this.insertForm.get('deCapitolo').setValue(anagrafica.deCapitolo);
    this.insertForm.get('annoCapitolo').setValue(anagrafica.deAnnoEsercizio);
    this.insertForm.get('dpAnnoCapitolo').setValue(DateTime.fromFormat(anagrafica.deAnnoEsercizio, 'yyyy').startOf('day'));
    this.insertForm.get('codAccertamento').setValue(anagrafica.codAccertamento);
    this.insertForm.get('deAccertamento').setValue(anagrafica.deAccertamento);
    const spinner1 = this.overlaySpinnerService.showProgress(this.elementRef);
    this.accertamentoService.getComboUffici(this.ente, anagrafica.codTipoDovuto).subscribe(
      uffici => {
        let nullUfficio = new AnagraficaUffCapAcc();
        this.uffici = [nullUfficio].concat(uffici);
        let ufficio = this.uffici.filter(c => c.codUfficio == anagrafica.codUfficio)[0] || nullUfficio;
        this.insertForm.get('ufficio').setValue(ufficio);
        this.overlaySpinnerService.detach(spinner1);
      }, manageError('Errore recuperando lista ufficio', this.toastrService, () => {this.overlaySpinnerService.detach(spinner1)}) );
    const spinner2 = this.overlaySpinnerService.showProgress(this.elementRef);
    this.accertamentoService.getComboCapitoli(this.ente, anagrafica.codTipoDovuto, anagrafica.codUfficio, anagrafica.deAnnoEsercizio).subscribe(
      capitoli => {
        let nullCapitolo = new AnagraficaUffCapAcc();
        this.capitoli = [nullCapitolo].concat(capitoli);
        let capitolo = this.capitoli.filter(c => c.codCapitolo == anagrafica.codCapitolo)[0] || nullCapitolo;
        this.insertForm.get('capitolo').setValue(capitolo);
        this.overlaySpinnerService.detach(spinner2);
      }, manageError('Errore recuperando lista capitolo', this.toastrService, () => {this.overlaySpinnerService.detach(spinner2)}) );
    const spinner3 = this.overlaySpinnerService.showProgress(this.elementRef);
    this.accertamentoService.getComboAccertamenti(this.ente, anagrafica.codTipoDovuto, anagrafica.codUfficio, anagrafica.deAnnoEsercizio, anagrafica.codCapitolo).subscribe(
      accertamenti => {
        let nullAccertamento = new AnagraficaUffCapAcc();
        this.accertamenti = [nullAccertamento].concat(accertamenti);
        let accertamento = this.accertamenti.filter(a => a.codAccertamento === anagrafica.codAccertamento)[0] || nullAccertamento;
        this.insertForm.get('accertamento').setValue(accertamento);
        this.overlaySpinnerService.detach(spinner3);
      }, manageError('Errore recuperando lista accertamento', this.toastrService, () => {this.overlaySpinnerService.detach(spinner3)}) );
  }

  private setMode(mode: string) {
    this.modeAnag = mode;
    if (mode === 'insert') {
      this.pageTitle = 'Inserimento';
      this.insertForm.get('tipoDovuto').enable();
      this.insertForm.get('ufficio').disable();
      this.insertForm.get('capitolo').disable();
      this.insertForm.get('accertamento').disable();
      this.breadcrumbsService.updateCurrentBreadcrumb(this.titleLabel);
    } else if (mode === 'edit') {
      //this.pageTitle = 'Modifica';
      this.insertForm.get('tipoDovuto').enable();
      this.insertForm.get('ufficio').enable();
      this.insertForm.get('capitolo').enable();
      this.insertForm.get('accertamento').enable();
    } else if (mode === 'view') {
      this.pageTitle = 'Dettaglio';
      this.insertForm.get('tipoDovuto').disable();
      this.insertForm.get('ufficio').disable();
      this.insertForm.get('capitolo').disable();
      this.insertForm.get('accertamento').disable();
      this.breadcrumbsService.updateCurrentBreadcrumb(this.titleLabel);
    }
  }
}
