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
    ConfirmDialogComponent
} from 'projects/mypay4-fe-common/src/lib/components/confirm-dialog/confirm-dialog.component';
import {
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    manageError, OverlaySpinnerService, PageStateService, PaginatorData, TableAction, TableColumn
} from 'projects/mypay4-fe-common/src/public-api';
import { first } from 'rxjs/operators';

import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionPanel } from '@angular/material/expansion';
import { ActivatedRoute, Router } from '@angular/router';
import { faClone, faSearch } from '@fortawesome/free-solid-svg-icons';

import { Ente } from '../../../model/ente';
import { TipoDovuto } from '../../../model/tipo-dovuto';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-ente',
  templateUrl: './ente.component.html',
  styleUrls: ['./ente.component.scss']
})
export class EnteComponent implements OnInit, WithTitle {

  get titleLabel(){ return "Dettaglio ente" }
  get titleIcon(){ return faClone }

  form: FormGroup;
  formErrors = {};
  blockingError: boolean = false;

  ente: Ente;

  @ViewChild('insertExpansionPanel') expansionPanel: MatExpansionPanel;

  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<TipoDovuto>;
  tableColumns: TableColumn[] = [
    new TableColumn('codTipo', 'Codice tipo dovuto'),
    new TableColumn('deTipo', 'Descrizione tipo dovuto'),
    new TableColumn('rowActions', 'Azioni', { sortable: false, tooltip: 'Azioni', actions: [
      new TableAction(faSearch, this.gotoDetails, null , 'Visualizza dettaglio'),
      new TableAction(null, this.delete, () => true , 'Elimina'),
      ] } ) ];
  tableData: TipoDovuto[];
  paginatorData: PaginatorData;

  constructor(
    private formBuilder: FormBuilder,
    private adminService: AdminService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private route: ActivatedRoute,
    private router: Router,
    private pageStateService: PageStateService,
    private dialog: MatDialog,
  ) {
    this.form = this.formBuilder.group({
      codTipoDovuto: ['',[Validators.required]],
      descr: ['',[Validators.required]],
    });
   }

  ngOnInit(): void {
    const params = this.route.snapshot.params;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.adminService.getEnteById(params['enteId']).subscribe(ente => {
      this.ente = ente;
      this.overlaySpinnerService.detach(spinner);
      this.searchTipiDovuto();
    }, manageError('Errore recuperando l\'ente', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  private searchTipiDovuto() {
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.adminService.getListTipoDovutoEsterniByEnte(this.ente).subscribe(tipiDovuto => {
      this.tableData = tipiDovuto;
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore recuperando i tipi dovuti', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  onInsert() {
    const tipoDovuto = new TipoDovuto();
    tipoDovuto.codTipo = this.form.get('codTipoDovuto').value;
    tipoDovuto.deTipo = this.form.get('descr').value;
    tipoDovuto.esterno = true;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.adminService.insertTipoDovuto(this.ente, tipoDovuto).subscribe(() => {
      this.overlaySpinnerService.detach(spinner);
      this.toastrService.success('Tipo dovuto inserito correttamente');
      this.form.reset();
      this.expansionPanel.close();
      this.searchTipiDovuto();
    }, manageError('Errore inserendo tipo dovuto', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  gotoDetails(element: TipoDovuto, thisRef: EnteComponent, eventRef: any) {
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
    thisRef.router.navigate(['admin/tipo-dovuto/', element.mygovEnteTipoDovutoId]);
  }

  delete(element: TipoDovuto, thisRef: EnteComponent, eventRef: any) {
    const msg = "Confermi di voler eliminare il tipo dovuto '"+element.codTipo+"' ?";
    thisRef.dialog.open(ConfirmDialogComponent,{autoFocus:false, data: {message: msg}})
      .afterClosed().pipe(first()).subscribe(result => {
        if(result==="true"){
          const spinner = thisRef.overlaySpinnerService.showProgress(thisRef.elementRef);
          thisRef.adminService.deleteTipoDovuto(element).subscribe(() => {
            thisRef.tableData = thisRef.tableData.filter(e => e.mygovEnteTipoDovutoId !== element.mygovEnteTipoDovutoId);
            thisRef.overlaySpinnerService.detach(spinner);
            thisRef.toastrService.success('Tipo dovuto eliminato correttamente');
          }, manageError('Errore eliminando tipo dovuto', thisRef.toastrService, () => {thisRef.overlaySpinnerService.detach(spinner)}) );
        }
      });
  }

}
