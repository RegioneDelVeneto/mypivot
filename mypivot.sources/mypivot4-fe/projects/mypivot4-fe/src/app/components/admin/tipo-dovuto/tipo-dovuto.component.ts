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
    ConfirmDialogComponent
} from 'projects/mypay4-fe-common/src/lib/components/confirm-dialog/confirm-dialog.component';
import {
    MyPayBaseTableComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table/my-pay-table.component';
import { WithTitle } from 'projects/mypay4-fe-common/src/lib/components/with-title';
import {
    manageError, MapPipe, OverlaySpinnerService, PageStateService, PaginatorData, TableAction,
    TableColumn, validateFormFun, WithActions
} from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';
import { first } from 'rxjs/operators';

import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { faClone } from '@fortawesome/free-solid-svg-icons';

import { Operatore } from '../../../model/operatore';
import { TipoDovuto } from '../../../model/tipo-dovuto';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-tipo-dovuto',
  templateUrl: './tipo-dovuto.component.html',
  styleUrls: ['./tipo-dovuto.component.scss']
})
export class TipoDovutoComponent implements OnInit, OnDestroy, WithTitle {

  get titleLabel(){ return "Dettaglio tipo dovuto" }
  get titleIcon(){ return faClone }

  tipoDovuto: TipoDovuto;
  form: FormGroup;
  formErrors = {};
  private valueChangedSub: Subscription;
  modified = false;

  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<Operatore>;
  tableColumns: TableColumn[] = [
    new TableColumn('username', 'ID utente'),
    new TableColumn('cognome', 'Cognome'),
    new TableColumn('nome', 'Nome'),
    new TableColumn('flgAssociazione', 'Stato', { pipe: MapPipe, pipeArgs: [{true: 'Abilitato', false: 'Disabilitato'}, 'Disabilitato']}) ,
    new TableColumn('rowActions', 'Azioni', { sortable: false, tooltip: 'Azioni', actions: [
      new TableAction(null, this.switchState(true), this.switchStateEnabled(true) , 'Abilita'),
      new TableAction(null, this.switchState(false), this.switchStateEnabled(false) , 'Disabilita'),
      ] } ) ];
  tableData: Operatore[];
  paginatorData: PaginatorData;

  constructor(
    private formBuilder: FormBuilder,
    private adminService: AdminService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private dialog: MatDialog,
    private route: ActivatedRoute,
    private router: Router,
    private pageStateService: PageStateService,
  ) {
    this.form = this.formBuilder.group({
      codTipo: ['',[Validators.required]],
      deTipo: ['',[Validators.required]],
    });
   }

  ngOnInit(): void {
    const params = this.route.snapshot.params;
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.adminService.getTipoDovutoById(params['tipoDovutoId']).subscribe(tipoDovuto => {
      this.tipoDovuto = tipoDovuto;
      this.onReset();
      this.overlaySpinnerService.detach(spinner);
      this.valueChangedSub = this.form.valueChanges.subscribe(validateFormFun(this.form, this.formErrors, null, data => {
        this.modified = data['codTipo'] !== this.tipoDovuto.codTipo || data['deTipo'] !== this.tipoDovuto.deTipo;
      }));
      this.searchOperatori();
    }, manageError('Errore recuperando il tipo dovuto', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  ngOnDestroy(): void {
    this.valueChangedSub?.unsubscribe();
  }

  private searchOperatori() {
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    this.adminService.getListOperatoriByTipoDovuto(this.tipoDovuto).subscribe(operatori => {
      this.tableData = operatori;
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore recuperando gli operatori', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  switchState(newState: boolean){
    return (element: Operatore, thisRef: TipoDovutoComponent, eventRef: any):void => {
      const spinner = thisRef.overlaySpinnerService.showProgress(thisRef.elementRef);
      thisRef.adminService.swtichStateOperatoreTipoDovuto(thisRef.tipoDovuto, element, newState).subscribe(() => {
        element.flgAssociazione = newState;
        this.overlaySpinnerService.detach(spinner);
        WithActions.reset(element);
        this.toastrService.success('Cambio stato effettuato correttamente');
      }, manageError('Errore cambiando stato associazione per l\'operatore', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
    };
  }

  switchStateEnabled(newState: boolean){
    return (element: Operatore, thisRef: TipoDovutoComponent):boolean  => {
      return (element.flgAssociazione || false) !== newState;
    };
  }

  onModify(){
    const msg = "Confermi di voler modificare il tipo dovuto '"+this.tipoDovuto.codTipo+"' ?";
    this.dialog.open(ConfirmDialogComponent,{autoFocus:false, data: {message: msg}})
      .afterClosed().pipe(first()).subscribe(result => {
        if(result==="true"){
          const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
          const newTipoDovuto = _.clone(this.tipoDovuto);
          newTipoDovuto.codTipo = this.form.get('codTipo').value;
          newTipoDovuto.deTipo = this.form.get('deTipo').value;
          
          this.adminService.updateTipoDovuto(newTipoDovuto).subscribe(() => {
            this.tipoDovuto = newTipoDovuto;
            this.onReset();
            this.overlaySpinnerService.detach(spinner);
            this.toastrService.success('Tipo dovuto modificato correttamente');
          }, manageError('Errore modificando tipo dovuto', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
        }
      });
  }

  onReset(){
    this.form.get('codTipo').setValue(this.tipoDovuto.codTipo);
    this.form.get('deTipo').setValue(this.tipoDovuto.deTipo);
  }

}
