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
    manageError, OverlaySpinnerService, PaginatorData, TableColumn
} from 'projects/mypay4-fe-common/src/public-api';
import { combineLatest, Subscription } from 'rxjs';

import { CurrencyPipe, Location } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { faLink } from '@fortawesome/free-solid-svg-icons';

import { Bilancio } from '../../model/bilancio';
import { Ente } from '../../model/ente';
import { Tesoreria } from '../../model/tesoreria';
import { EnteService } from '../../services/ente.service';
import { TesoreriaService } from '../../services/tesoreria.service';

@Component({
  selector: 'app-tesorerie-detail',
  templateUrl: './tesorerie-detail.component.html',
  styleUrls: ['./tesorerie-detail.component.scss']
})
export class TesorerieDetailComponent implements OnInit, WithTitle {

  @ViewChild('myPayTable') mypayTableComponent: MyPayBaseTableComponent<Bilancio>;

  get titleLabel(){ return "Giornale di cassa - dettaglio" }
  get titleIcon(){ return faLink }

  hasSearched: boolean = false;
  blockingError: boolean = false;

  private enteChangesSub: Subscription;
  private ente: Ente;
  private tesoreriaId: number;
  private annoBolletta: string;
  private codBolletta: string;
  tesoreria: Tesoreria = new Tesoreria();

  constructor(
    private route: ActivatedRoute,
    private enteService: EnteService,
    private tesoreriaService: TesoreriaService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private location: Location,
  ) {

    this.route.queryParams.subscribe(params => {
      this.tesoreriaId = params['tesoreriaId'];
      this.annoBolletta = params['annoBolletta'];
      this.codBolletta = params['codBolletta'];
    });
  }

  ngOnInit(): void {

    this.enteChangesSub = this.enteService.getCurrentEnteObs().subscribe(ente => {
      this.tableData = new Array();
      this.ente = ente;
      if (this.ente) {
        this.onSubmit();
      }
    });
  }

  ngOnDestroy():void {
    this.enteChangesSub?.unsubscribe();
  }

  tableColumns: TableColumn[] = [
    new TableColumn('intermediario', 'Intermediario', { sortable: true }),
    new TableColumn('datiBolletta', 'Anno bolletta - Codice bolletta', { sortable: true }),
    new TableColumn('codUfficio', 'Codice ufficio', { sortable: true }),
    new TableColumn('codCapitolo', 'Codice capitolo', { sortable: true }),
    new TableColumn('deTipoDovuto', 'Tipo dovuto', { sortable: true }),
    new TableColumn('codAccertamento', 'Codice accertamento', { sortable: true }),
    new TableColumn('importo', 'Importo', { sortable: true, pipe: CurrencyPipe, pipeArgs:['EUR', 'symbol'] }),
  ];
  tableData: Bilancio[];
  paginatorData: PaginatorData;

  onSubmit(){
    const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
    combineLatest(
      [this.tesoreriaService.getDettaglio(this.ente, this.tesoreriaId),
      this.tesoreriaService.getBilanci(this.ente, this.annoBolletta, this.codBolletta)]
    ).subscribe(([tesoreria, bilanci]) => {
      this.tesoreria = tesoreria || new Tesoreria();
      if (bilanci!.length > 0) {
        bilanci.forEach(b => { 
          b.datiBolletta = `${this.annoBolletta} - ${this.codBolletta}`; 
        });
        this.tableData = bilanci;
      }
      this.hasSearched = true;
      this.overlaySpinnerService.detach(spinner);
    }, manageError('Errore caricando il dettaglio', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
  }

  goBack() {
    this.location.back();
  }
}
