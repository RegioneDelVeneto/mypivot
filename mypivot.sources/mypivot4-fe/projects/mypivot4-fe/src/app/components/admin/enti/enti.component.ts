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
import {
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    manageError, OverlaySpinnerService, PageStateService, PaginatorData, TableAction, TableColumn
} from 'projects/mypay4-fe-common/src/public-api';

import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { faClone, faSearch } from '@fortawesome/free-solid-svg-icons';

import { Ente } from '../../../model/ente';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-enti',
  templateUrl: './enti.component.html',
  styleUrls: ['./enti.component.scss']
})
export class EntiComponent implements OnInit, WithTitle {

  get titleLabel(){ return "Gestione tipi dovuto" }
  get titleIcon(){ return faClone }

  form: FormGroup;
  formErrors = {};
  hasSearched: boolean = false;
  blockingError: boolean = false;

  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<Ente>;
  tableColumns: TableColumn[] = [
    new TableColumn('thumbLogoEnte', null, {type:'img64', ariaLabel:'Logo'}),
    new TableColumn('deNomeEnte', 'Nome'),
    new TableColumn('codIpaEnte', 'Codice IPA'),
    new TableColumn('codiceFiscaleEnte', 'Codice fiscale'),
    new TableColumn('rowActions', 'Azioni', { sortable: false, tooltip: 'Azioni', actions: [
      new TableAction(faSearch, this.gotoDetails, null , 'Visualizza dettaglio'),
      ] } ) ];
  tableData: Ente[];
  paginatorData: PaginatorData;

  constructor(
    private formBuilder: FormBuilder,
    private adminService: AdminService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private router: Router,
    private pageStateService: PageStateService,
  ) {
    this.form = this.formBuilder.group({
      codIpaEnte: [''],
      deNomeEnte: [''],
      codFiscale: [''],
    });
   }

  ngOnInit(): void {
    //retrieve page state data if navigating back
    if(this.pageStateService.isNavigatingBack()){
      const pageState = this.pageStateService.restoreState();
      if(pageState){
        this.form.setValue(pageState.formData);
        setTimeout(()=>{
          if(pageState.reloadData){
            this.onSearch();
          } else {
            this.tableData = pageState.tableData;
            this.paginatorData = pageState.paginatorData;
          }
        });
        // Adjust the table and activate "Cerca" button.
      }
    }
  }

  onReset() {
    this.form.reset({
      codIpaEnte:'',
      deNomeEnte:'',
      codFiscale:''
    });
  }

  onSearch() {
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    const codIpaEnte = this.form.get('codIpaEnte')?.value;
    const deNome = this.form.get('deNomeEnte')?.value;
    const codFiscale = this.form.get('codFiscale')?.value;

    this.adminService.searchEnti(codIpaEnte, deNome, codFiscale).subscribe(enti => {
      this.hasSearched = true;
      this.tableData = enti;
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore effettuando la ricerca', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  gotoDetails(element: Ente, thisRef: EntiComponent, eventRef: any) {
    if(eventRef)
      eventRef.stopPropagation();
    const navId = thisRef.pageStateService.saveState({
      formData: thisRef.form.value,
      tableData: thisRef.tableData,
      paginatorData: {
        pageSize: thisRef.mypayTableComponent.paginator.pageSize,
        pageIndex: thisRef.mypayTableComponent.paginator.pageIndex
      }
    });
    thisRef.router.navigate(['admin/enti/', element.mygovEnteId]);
  }

}
