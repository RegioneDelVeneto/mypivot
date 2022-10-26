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
import { ToastrService } from 'ngx-toastr';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    FileSizePipe, manageError, OverlaySpinnerService
} from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';

import { Location } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { faListOl } from '@fortawesome/free-solid-svg-icons';

import { Ente } from '../../model/ente';
import { AccertamentoService } from '../../services/accertamento.service';
import { EnteService } from '../../services/ente.service';

@Component({
  selector: 'app-import-massivo',
  templateUrl: './import-massivo.component.html',
  styleUrls: ['./import-massivo.component.scss']
})
export class ImportMassivoComponent implements OnInit, OnDestroy, WithTitle {

  @ViewChild('fileForm', { read: ElementRef }) fileFormElement: ElementRef;
  @ViewChild('fileInput') fileInput: ElementRef;

  get titleLabel(){ return "Import massivo" }
  get titleIcon(){ return faListOl }

  private enteSub: Subscription;
  ente: Ente;
  formData: FormData = null;
  blockingError: boolean = false;
  fileLabel: string;

  constructor(
    private enteService: EnteService,
    private accertamentoService: AccertamentoService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private fileSizePipe: FileSizePipe,
    private location: Location,
  ) {
  }

  ngOnInit(): void {
    this.enteSub = this.enteService.getCurrentEnteObs().subscribe(ente => {
      this.ente = ente;
      this.selectFileOnChange(null);
    });
  }

  ngOnDestroy(): void {
    this.enteSub?.unsubscribe();
  }

  uploadImportMassivo(){
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.accertamentoService.uploadImportMassivo(this.ente, this.formData).subscribe(data => {
      this.toastrService.success('Il nuovo import massivo caricato correttamente');
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore caricando il file', this.toastrService, ()=>{this.overlaySpinnerService.detach(spinner)}) );
  }

  onReset(){
    this.selectFileOnChange(null);
  }

  selectFileOnChange(files: FileList) {
    if (files?.length > 0) {
      this.formData = new FormData();
      this.formData.append("file", files[0]);
      this.formData.append("type", "IMPORT_MASSIVO");
      this.fileLabel = files[0].name + " ["+this.fileSizePipe.transform(files[0].size)+"]";
    } else {
      this.formData = null;
      this.fileLabel = null;
      this.fileFormElement?.nativeElement.reset();
    }
  }

  goBack() {
    this.location.back();
  }
}
