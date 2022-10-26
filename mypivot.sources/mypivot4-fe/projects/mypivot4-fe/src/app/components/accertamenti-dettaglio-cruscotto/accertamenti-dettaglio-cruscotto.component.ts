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
import { manageError, OverlaySpinnerService } from 'projects/mypay4-fe-common/src/public-api';
import { Subscription } from 'rxjs';

import { Location } from '@angular/common';
import { Component, ElementRef, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { faBriefcase } from '@fortawesome/free-solid-svg-icons';

import { Ente } from '../../model/ente';
import { FlussoRicevuta } from '../../model/flusso-ricevuta';
import { AccertamentoService } from '../../services/accertamento.service';
import { EnteService } from '../../services/ente.service';

@Component({
  selector: 'app-accertamenti-dettaglio-cruscotto',
  templateUrl: './accertamenti-dettaglio-cruscotto.component.html',
  styleUrls: ['./accertamenti-dettaglio-cruscotto.component.scss']
})
export class AccertamentiDettaglioCruscottoComponent implements OnInit, WithTitle {

  get titleLabel(){ return "Dettaglio RT" }
  get titleIcon(){ return faBriefcase }

  private enteChangesSub: Subscription;
  ente: Ente;
  flussoRicevuta = new FlussoRicevuta();
  codTipo: string;
  codIud: string;
  pagatore: string;
  versante: string;

  constructor(
    private enteService: EnteService,
    private accertamentoService: AccertamentoService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef,
    private route: ActivatedRoute,
    private toastrService: ToastrService,
    private location: Location,
  ) { }

  ngOnInit(): void {
    this.enteChangesSub = this.enteService.getCurrentEnteObs().subscribe(ente => {
      this.ente = ente;
      const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
      this.route.queryParams.subscribe(params => {
        this.codTipo = params['codTipo'];
        this.codIud = params['codIud'];
        this.accertamentoService.getRicevuteTelematiche(ente, this.codTipo, this.codIud).subscribe(ricevute => {
          this.flussoRicevuta = ricevute[0] || new FlussoRicevuta();
          this.overlaySpinnerService.detach(spinner);
        }, manageError('Errore caricando la RT', this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );
      });
    });
  }

  ngOnDestroy():void {
    this.enteChangesSub?.unsubscribe();
  }

  goBack() {
    this.location.back();
  }

}
