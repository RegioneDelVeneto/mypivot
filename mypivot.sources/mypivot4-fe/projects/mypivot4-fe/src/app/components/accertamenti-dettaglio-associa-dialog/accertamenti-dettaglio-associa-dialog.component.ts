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
import { OverlaySpinnerService } from './../../../../../mypay4-fe-common/src/lib/overlay-spinner/overlay-spinner.service';
import { Component, OnInit, Inject, ElementRef } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AccertamentoFlussoExport } from '../../model/accertamento-flusso-export';
import { Ente } from '../../model/ente';
import { DateTime } from 'luxon';
import { MatDatepicker } from '@angular/material/datepicker';
import { AnagraficaUffCapAcc } from '../../model/anagrafica-uff-cap-acc';
import { FormGroup, FormBuilder } from '@angular/forms';
import { AccertamentoService } from '../../services/accertamento.service';
import { manageError } from 'projects/mypay4-fe-common/src/public-api';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-accertamenti-dettaglio-associa-dialog',
  templateUrl: './accertamenti-dettaglio-associa-dialog.component.html',
  styleUrls: ['./accertamenti-dettaglio-associa-dialog.component.scss']
})
export class AccertamentiDettaglioAssociaDialogComponent implements OnInit {

  private ente: Ente;
  private accertamentoId: number;
  private selectedPagamenti: AccertamentoFlussoExport[];
  private codTipo: string;

  uffici: AnagraficaUffCapAcc[];
  capitoli: AnagraficaUffCapAcc[];
  accertamenti: AnagraficaUffCapAcc[];
  pagamentiSalvati: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private accertamentoService: AccertamentoService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    @Inject(MAT_DIALOG_DATA) data,
    private dialogRef: MatDialogRef<AccertamentiDettaglioAssociaDialogComponent>,
  ) {
    this.selectedPagamenti = data.selectedPagamenti;
    this.ente = data.ente;
    this.accertamentoId = data.accertamentoId;
    this.codTipo = data.codTipo;
  }

  ngOnInit(): void {

    const formObj = {
      ufficio: [null],
      annoEsercizio: [DateTime.now().startOf('day').toFormat('yyyy')],
      dpAnnoEsercizio: [DateTime.now().startOf('day')],
      capitolo: [null],
      accertamento: [null],
    };
    this.insertForm = this.formBuilder.group(formObj);

    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.accertamentoService.getComboUffici(this.ente, this.codTipo).subscribe(
      uffici => {
        this.overlaySpinnerService.detach(spinner);
        this.uffici = uffici;
      }, manageError('Errore recuperando lista ufficio', this.toastrService, () => { this.overlaySpinnerService.detach(spinner)}) );
    this.insertForm.get('ufficio').enable();
    this.insertForm.get('capitolo').disable();
    this.insertForm.get('accertamento').disable();
  }

  insertForm: FormGroup;
  insertFormErrors = {};

  minDate = DateTime.fromFormat('01/01/1970','dd/LL/yyyy');
  maxDate = DateTime.now().endOf('day');

  chosenYearly(normalizedDt: DateTime, datepicker: MatDatepicker<DateTime>) {
    datepicker.close();
    this.insertForm.get('annoEsercizio').setValue(normalizedDt.toFormat('yyyy'));
    let ufficio = this.insertForm.get('ufficio').value as AnagraficaUffCapAcc;
    if (ufficio && ufficio.codUfficio)
      this.ufficioOnChange(ufficio);
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

  ufficioOnChange(ufficio: AnagraficaUffCapAcc) {
    this.insertForm.get('capitolo').setValue(null);
    this.insertForm.get('accertamento').setValue(null);
    this.capitoli = [];
    this.accertamenti = [];
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let annoEsercizio = this.insertForm.get('annoEsercizio').value;
    this.accertamentoService.getComboCapitoli(this.ente, this.codTipo, ufficio.codUfficio, annoEsercizio).subscribe(
      capitoli => {
        this.capitoli = capitoli;
        this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore recuperando lista capitolo', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
    this.insertForm.get('capitolo').enable();
    this.insertForm.get('accertamento').disable();
  }

  capitoloOnChange(capitolo: AnagraficaUffCapAcc) {
    this.insertForm.get('accertamento').setValue(null);
    this.accertamenti = [];
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    let annoEsercizio = this.insertForm.get('annoEsercizio').value;
    let ufficio = this.insertForm.get('ufficio').value;
    this.accertamentoService.getComboAccertamenti(this.ente, this.codTipo, ufficio.codUfficio, annoEsercizio, capitolo.codCapitolo).subscribe(
      accertamenti => {
        this.accertamenti = accertamenti;
        this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore recuperando lista accertamento', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
    this.insertForm.get('accertamento').enable();
  }

  close() {
    this.dialogRef.close();
  }

  onSubmit() {
    const i = this.insertForm.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);

    let ufficio = i.ufficio as AnagraficaUffCapAcc;
    let capitolo = i.capitolo as AnagraficaUffCapAcc;
    let accertamento = i.accertamento as AnagraficaUffCapAcc;
    this.accertamentoService.insertAccertamentoDettaglio(this.ente, this.accertamentoId, this.selectedPagamenti,
      ufficio.codUfficio, i.annoEsercizio, capitolo.codCapitolo, accertamento.codAccertamento).subscribe(num => {
      this.overlaySpinnerService.detach(spinner);
      this.pagamentiSalvati = true;
      this.dialogRef.close("updated");
    }, manageError('Errore aggiungendo i pagamenti', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }
}
