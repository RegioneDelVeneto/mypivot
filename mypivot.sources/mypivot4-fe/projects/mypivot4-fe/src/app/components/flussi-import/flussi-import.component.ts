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
import { FileSaverService } from 'ngx-filesaver';
import { ToastrService } from 'ngx-toastr';
import {
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    ApiInvokerService, DateValidators, FileSizePipe, manageError, OverlaySpinnerService,
    PaginatorData, TableAction, TableColumn, validateFormFun
} from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';

import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { DatePipe } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
    faCloudUploadAlt, faDownload, faEdit, faEllipsisH, faReceipt, faTrash
} from '@fortawesome/free-solid-svg-icons';

import { Ente } from '../../model/ente';
import { Flusso } from '../../model/flusso';
import { FlussoImport } from '../../model/flusso-import';
import { FlussoUploadRequest } from '../../model/flusso-upload-request';
import { TipoFlusso } from '../../model/tipo-flusso';
import { EnteService } from '../../services/ente.service';
import { FlussoService } from '../../services/flusso.service';

@Component({
  selector: 'app-flussi-import',
  templateUrl: './flussi-import.component.html',
  styleUrls: ['./flussi-import.component.scss'],
})
export class FlussiImportComponent implements OnInit, OnDestroy, WithTitle {

  get titleLabel(){ return "Importa flussi" }
  get titleIcon(){ return faCloudUploadAlt }

  iconEllipsisH = faEllipsisH;
  iconEdit = faEdit;
  iconReceipt = faReceipt;
  iconTrash = faTrash;
  iconDownload = faDownload;

  @ViewChild('sForm') searchFormDirective;
  @ViewChild('fileForm', { read: ElementRef }) fileFormElement: ElementRef;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<FlussoImport>;
  @ViewChild('fileInput') fileInput: ElementRef;

  formData: FormData = null;
  hasSearched: boolean = false;
  blockingError: boolean = false;
  smallScreen: boolean = false;
  fileLabel: string;
  private enteSub: Subscription;
  private valueChangedSub: Subscription;
  private smallScreenSub: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private enteService: EnteService,
    private flussoService: FlussoService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private fileSaverService: FileSaverService,
    private fileSizePipe: FileSizePipe,
    private breakpointObserver: BreakpointObserver,
  ) {

    this.form = this.formBuilder.group({
      tipoFlusso: [null, [Validators.required]],
      nomeFlusso: [''],
      dateFrom: [DateTime.now().startOf('day').minus({month:1}), [Validators.required]],
      dateTo: [DateTime.now().startOf('day'), [Validators.required]],
    }, {validators: DateValidators.dateRange('dateFrom','dateTo')});

    this.uploadForm = this.formBuilder.group({
      tipoFlusso: [null, [Validators.required]],
    });

    this.valueChangedSub = this.form.valueChanges.subscribe(validateFormFun(this.form, this.formErrors));
  }

  ngOnInit(): void {
    //stepper responsiveness
    this.smallScreenSub = this.breakpointObserver.observe([
        Breakpoints.XSmall,
        Breakpoints.Small
      ]).subscribe(result => {
        this.smallScreen = result.matches;
        console.log('small screen: ',this.smallScreen);
    });

    this.enteSub = this.enteService.getCurrentEnteObs().subscribe(value => this.onChangeEnte(this, value) );
    this.onChangeEnte(this, this.enteService.getCurrentEnte());
    if(this.enteService.getCurrentEnte())
      this.onSubmit();
  }

  ngOnDestroy(): void {
    this.enteSub?.unsubscribe();
    this.valueChangedSub?.unsubscribe();
    this.smallScreenSub?.unsubscribe();
  }

  private onChangeEnte(thisRef: FlussiImportComponent, ente:Ente){
    this.uploadForm.controls['tipoFlusso'].setValue(this.tipiFlusso[0]);
    this.form.controls['tipoFlusso'].setValue(this.tipiFlusso[0])
    this.form.controls['nomeFlusso'].setValue(null);
  }

  flussoDisplayFn(flusso: Flusso): string {
    return flusso ? flusso.nome : '';
  }

  form: FormGroup;
  formErrors = {};

  uploadForm: FormGroup;
  uploadFormErrors = {};
  tipiFlusso= [
    new TipoFlusso(1, 'E', 'Ricevute Telematiche'),
    new TipoFlusso(2, 'R', 'Rendicontazione PagoPA'),
    new TipoFlusso(5, 'T', 'Giornale di Cassa XLS'),
    new TipoFlusso(7, 'C', 'Giornale di Cassa CSV'),
    new TipoFlusso(8, 'O', 'Giornale di Cassa OPI'),
    new TipoFlusso(9, 'Y', 'Estratto conto poste'),
  ];

  columnsOrderCassaCSV: Array<ColumnOrderElem> = [
    new ColumnOrderElem("posDeAnnoBolletta", "Colonna Anno Bolletta"),
    new ColumnOrderElem("posCodBolletta", "Colonna Codice Bolletta"),
    new ColumnOrderElem("posDtContabile", "Colonna Data Contabile"),
    new ColumnOrderElem("posDeDenominazione", "Colonna Denominazione Ordinante"),
    new ColumnOrderElem("posDeCausale", "Colonna Causale Versamento"),
    new ColumnOrderElem("posNumImporto", "Colonna Importo Bolletta"),
    new ColumnOrderElem("posDtValuta", "Colonna Data Valuta")
  ];

  tableColumns = [
    new TableColumn('id', 'ID Interno'),
    new TableColumn('nomeFlusso', 'Nome Flusso'),
    new TableColumn('dataCaricamento', 'Data Caricamento', { sortable: (item: FlussoImport) => item.dataCaricamento?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
    new TableColumn('operatore', 'Operatore'),
    new TableColumn('deStato', 'Stato'),
    new TableColumn('rowActions', 'Azioni', { sortable: false, tooltip: 'Azioni', actions: [
      new TableAction(this.iconDownload, this.downloadFlusso('originale'), this.downloadFlussoEnabled('originale'),
                      'Scarica originale',{text: 'O', class: 'badge-download', transform: 'right-7 up-7 shrink-4'}),
      new TableAction(this.iconDownload, this.downloadFlusso('scartati'), this.downloadFlussoEnabled('scartati'),
                      'Scarica scartati',{text: 'S', class: 'badge-download', transform: 'right-7 up-7 shrink-4'}),
      ] } ) ];
  tableData: FlussoImport[];
  paginatorData: PaginatorData;

  onSubmit(){
    const i = this.form.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.flussoService.searchFlussiImport(this.enteService.getCurrentEnte(), i.tipoFlusso, i.nomeFlusso, i.dateFrom, i.dateTo)
      .subscribe(data => {
        this.hasSearched = true;
        this.tableData = data;
        this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', this.toastrService, ()=>{this.overlaySpinnerService.detach(spinner)}) );
  }

  onReset(){
    this.form.reset();
    this.hasSearched = false;
    this.tableData = null;
  }

  selectFileOnChange(files: FileList) {
    if (files?.length > 0) {
      this.formData = new FormData();
      this.formData.append("file", files[0]);
      this.formData.append("type", "FLUSSI_IMPORT");
      this.fileLabel = files[0].name + " ["+this.fileSizePipe.transform(files[0].size)+"]";
    } else {
      this.formData = null;
      this.fileLabel = null;
      this.fileFormElement.nativeElement.reset();
    }
  }

  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.columnsOrderCassaCSV, event.previousIndex, event.currentIndex);
  }

  downloadFlusso(type: string){
    return function(element: FlussoImport, thisRef: FlussiImportComponent){
      switch(type){
        case 'originale': return thisRef.downloadFromMybox(element.filePathOriginale, element.securityToken);
        case 'scartati': return thisRef.downloadFromMybox(element.filePathScarti, element.securityToken);
        default: throw new Error("invalid download type: "+type);
      }
    }
  }

  downloadFlussoEnabled(type: string){
    return function(element: FlussoImport, thisRef: FlussiImportComponent){
      switch(type){
        case 'originale': return element.filePathOriginale && element.filePathOriginale.trim().length > 0;
        case 'scartati': return element.filePathScarti && element.filePathScarti.trim().length > 0;
        default: throw new Error("invalid download type: "+type);
      }
    }
  }

  uploadFlusso() {
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    const uploadRequest = new FlussoUploadRequest((this.uploadForm.get('tipoFlusso').value as TipoFlusso).codTipo);
    for(let idxKey of this.columnsOrderCassaCSV.entries())
      uploadRequest[idxKey[1].key] = idxKey[0] + 1;
    this.formData.set("postedJson", JSON.stringify(uploadRequest));
    this.flussoService.uploadFlusso(this.enteService.getCurrentEnte(), this.formData)
      .subscribe(() => {
        this.toastrService.success('File caricato correttamente');
        this.selectFileOnChange(null);
        this.overlaySpinnerService.detach(spinner);

        //set search fields
        const tipoFlussoUploadSelected=this.uploadForm.get('tipoFlusso').value;
        this.form.get('tipoFlusso').setValue(tipoFlussoUploadSelected);
        this.form.get('nomeFlusso').setValue('');
        this.form.get('dateFrom').setValue(DateTime.now().startOf('day').minus({month:1}));
        this.form.get('dateTo').setValue(DateTime.now().startOf('day'));
        //do search
        this.onSubmit();
        

    }, manageError('Errore effettuando il caricamento del file', this.toastrService, ()=>{this.overlaySpinnerService.detach(spinner)}) );
  }

  downloadFromMybox(filename: string, securityToken: string) {
    this.flussoService.downloadFlusso(this.enteService.getCurrentEnte(), 'FLUSSI_IMPORT', filename, securityToken).subscribe(response => {
      const contentDisposition = response.headers.get('content-disposition');
      const fileName = ApiInvokerService.extractFilenameFromContentDisposition(contentDisposition)  ?? filename.replace(/^.*[\\\/]/, '');
      const contentType = response.headers.get('content-type') ?? 'application/octet-stream';
      const blob:any = new Blob([response.body], { type: contentType });
      this.fileSaverService.save(blob, fileName);
      //close action bar
    }, manageError('Errore scaricando il file dei flussi', this.toastrService) );
  }

  gotoRemove(elementRef: FlussoImport, thisRef: FlussiImportComponent, eventRef: any) {
    if(eventRef)
      eventRef.stopPropagation();
    thisRef.flussoService.removeFlusso(thisRef.enteService.getCurrentEnte(), elementRef.id).subscribe(response => {
      thisRef.toastrService.info('Flusso annullato correttamente.');
    }, manageError('Errore annullando il flusso', thisRef.toastrService) );
  }

  gotoRemoveEnabled(elementRef: FlussoImport, thisRef: FlussiImportComponent) {
    let stato = elementRef.codStato?.toLowerCase().trim();
    return stato === 'load_flow' || stato === 'caricato';
  }

}

class ColumnOrderElem {
  constructor(key: string, desc: string) {
    this.key = key;
    this.desc = desc;
  }

  key: string;
  desc: string;
}
