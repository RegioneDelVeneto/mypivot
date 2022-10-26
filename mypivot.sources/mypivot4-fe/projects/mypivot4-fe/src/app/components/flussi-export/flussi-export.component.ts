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

import { DatePipe } from '@angular/common';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { faCloudDownloadAlt, faEdit, faReceipt, faSearch } from '@fortawesome/free-solid-svg-icons';

import { FlussoExport } from '../../model/flusso-export';
import { EnteService } from '../../services/ente.service';
import { FlussoService } from '../../services/flusso.service';
import { RiconciliazioneService } from '../../services/riconciliazione.service';
import { Classificazione } from '../../model/classificazione';

@Component({
  selector: 'app-flussi-export',
  templateUrl: './flussi-export.component.html',
  styleUrls: ['./flussi-export.component.scss']
})
export class FlussiExportComponent implements OnInit, OnDestroy, WithTitle {

  get titleLabel(){ return "Esporta flussi" }
  get titleIcon(){ return faCloudDownloadAlt }

  iconEdit = faEdit;
  iconReceipt = faReceipt;
  inconSearch = faSearch;
  classificazioni: Classificazione[];
  classificazioniKeys: string[];


  @ViewChild('sForm') searchFormDirective;
  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<FlussoExport>;

  hasSearched: boolean = false;
  blockingError: boolean = false;

  private valueChangedSub: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private enteService: EnteService,
    private flussoService: FlussoService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private riconciliazioneService: RiconciliazioneService,
    private fileSaverService: FileSaverService,
  ) {

    this.searchForm = this.formBuilder.group({
      nomeFlusso: [''],
      dateFrom: [DateTime.now().startOf('day'), [Validators.required]],
      dateTo: [DateTime.now().startOf('day').plus({month:1}), [Validators.required]],
    }, { validators: DateValidators.dateRangeForRangePicker('dateFrom','dateTo') });

  }

  ngOnDestroy(): void {
    this.valueChangedSub?.unsubscribe();
  }

  ngOnInit(): void {
    this.valueChangedSub = this.searchForm.valueChanges.subscribe(validateFormFun(this.searchForm, this.searchFormErrors));

    if(this.enteService.getCurrentEnte())
      this.onSearch();

    if(!this.classificazioni)
      this.getClassificazioni();
  }

  searchForm: FormGroup;
  searchFormErrors = {};

  tableColumns = [
    new TableColumn('dataPrenotazione', 'Data Prenotazione', { sortable: (item: FlussoExport) => item.dataPrenotazione?.valueOf(), pipe: DatePipe, pipeArgs: ['dd/MM/yyyy'] } ),
    new TableColumn('versioneTracciato', 'Versione Tracciato', { sortable: true }),
    new TableColumn('operatore', 'Operatore', { sortable: true }),
    new TableColumn('classificazione', 'Classificazione', { sortable: true }),
    new TableColumn('nome', 'Nome File', { sortable: true }),
    new TableColumn('dimensione', 'Dimensione File', { sortable: (item: FlussoExport) => item.dimensione?.valueOf(), pipe: FileSizePipe} ),
    new TableColumn('rowActions', 'Azioni', { sortable: false, tooltip: 'Azioni', actions: [
      new TableAction(this.iconReceipt, this.downloadFromMybox, (item: FlussoExport) => item.showDownload, 'Scarica')
      ] } ) ];
  tableData: FlussoExport[];
  paginatorData: PaginatorData;

  onReset(){
    this.searchForm.reset();
    this.searchForm.get('dateFrom').setValue(DateTime.now().startOf('day'));
    this.searchForm.get('dateTo').setValue(DateTime.now().startOf('day').plus({month:1}));
    this.hasSearched = false;
    this.tableData = null;
  }

  onSearch(){
    if(!this.classificazioni)
      this.getClassificazioni();
    const i = this.searchForm.value;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.flussoService.searchFlussiExport(this.enteService.getCurrentEnte(), i.nomeFlusso, i.dateFrom, i.dateTo)
      .subscribe(data => {

        data.forEach(elem => {

          this.classificazioni.find(classificazioneElem => {
            if (classificazioneElem.code === elem.classificazione)
              elem.classificazione = classificazioneElem.label
          })

        })

        if(data)
        console.log(true);

        this.hasSearched = true;
        this.tableData = data;
        this.overlaySpinnerService.detach(spinner);
      }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  downloadFromMybox(elementRef: FlussoExport, thisRef: FlussiExportComponent, eventRef: any) {
    if(eventRef)
      eventRef.stopPropagation();
    thisRef.flussoService.downloadFlusso(thisRef.enteService.getCurrentEnte(), 'FLUSSI_EXPORT', elementRef.path, elementRef.securityToken)
      .subscribe(response => {
        const contentDisposition = response.headers.get('content-disposition');
        const fileName = ApiInvokerService.extractFilenameFromContentDisposition(contentDisposition)  ?? elementRef.path.replace(/^.*[\\\/]/, '');
        const contentType = response.headers.get('content-type') ?? 'application/octet-stream';
        const blob:any = new Blob([response.body], { type: contentType });
        thisRef.fileSaverService.save(blob, fileName);
        //close action bar
      }, manageError('Errore scaricando il file', thisRef.toastrService) );
  }

  getClassificazioni() {
    this.riconciliazioneService.getSearchTypes(this.enteService.getCurrentEnte()).subscribe(searchTypes => {
      this.classificazioni = searchTypes;
      this.classificazioniKeys = Object.keys(this.classificazioni);
    }, manageError('Errore caricando l\'elenco delle classificazioni', this.toastrService, () => { this.blockingError = true }));
  }

}
