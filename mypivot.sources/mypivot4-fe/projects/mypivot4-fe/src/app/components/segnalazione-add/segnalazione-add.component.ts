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
import { ToastContainerDirective, ToastrService } from 'ngx-toastr';
import {
    UpdateDetailFun
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table-detail/my-pay-table-detail.component';
import { manageError, OverlaySpinnerService } from 'projects/mypay4-fe-common/src/public-api';

import { Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { faTimes } from '@fortawesome/free-solid-svg-icons';

import { Segnalazione } from '../../model/segnalazione';
import { EnteService } from '../../services/ente.service';
import { RiconciliazioneService } from '../../services/riconciliazione.service';
import { SegnalazioneService } from '../../services/segnalazione.service';

@Component({
  selector: 'app-segnalazione-add',
  templateUrl: './segnalazione-add.component.html',
  styleUrls: ['./segnalazione-add.component.scss']
})
export class SegnalazioneAddComponent implements OnInit {

  @ViewChild(ToastContainerDirective, { static: true })
  toastContainer: ToastContainerDirective;

  static DIALOG_ID = "segnalazione-add-dialog";

  iconTimes = faTimes;

  blockingError: boolean = false;

  form: FormGroup;
  formErrors = {};

  private segnalazione: Segnalazione;
  private updateDetailsFun: (x:UpdateDetailFun)=>void;
  private callbackFun: (x:Segnalazione)=>void;


  constructor(
    private formBuilder: FormBuilder,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    public dialogRef: MatDialogRef<SegnalazioneAddComponent>,
    @Inject(MAT_DIALOG_DATA) data: any,
    private segnalazioneService: SegnalazioneService,
    private riconciliazioneService: RiconciliazioneService,
    private enteService: EnteService,
  ) {
    this.form = this.formBuilder.group({
      nota: ['', [Validators.required]],
    });
    this.segnalazione = new Segnalazione();
    this.segnalazione.iufKey = data.iufKey;
    this.segnalazione.iudKey = data.iudKey;
    this.segnalazione.iuvKey = data.iuvKey;
    this.segnalazione.classificazione = data.classificazione;
    this.updateDetailsFun = data.updateDetailsFun;
    this.callbackFun = data.callbackFun;
  }

  ngOnInit(): void {
    this.toastrService.overlayContainer = this.toastContainer;
  }

  onSubmit(): void {
    this.segnalazione.nota = this.form.get('nota').value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.segnalazioneService.insert(this.enteService.getCurrentEnte(), this.segnalazione).subscribe(segnalazione => {
      this.updateDetailsFun?.( detailsObs => {
        if(detailsObs){
          const details = detailsObs.value;
          this.riconciliazioneService.updateDetailSegnalazione(details, segnalazione);
          detailsObs.next(details);
        }
      });

      this.callbackFun?.(segnalazione);
      
      this.overlaySpinnerService.detach(spinner);
      this.dialogRef.close();
      this.toastrService.overlayContainer = null;
      this.toastrService.success('Segnalazione inserita correttamente');
    }, manageError('Errore inserendo la segnalazione', this.toastrService, () => this.overlaySpinnerService.detach(spinner)) );
  }

  static close(matDialog: MatDialog){
    matDialog.getDialogById(SegnalazioneAddComponent.DIALOG_ID)?.close();
  }

}
