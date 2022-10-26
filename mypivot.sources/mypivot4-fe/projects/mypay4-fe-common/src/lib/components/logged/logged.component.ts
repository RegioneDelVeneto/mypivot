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
import { OverlaySpinnerService } from 'projects/mypay4-fe-common/src/public-api';
import { first, take } from 'rxjs/operators';

import { Component, ElementRef, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { UserService } from '../../services/user.service';
import { manageError } from '../../utils/manage-errors';

@Component({
  selector: 'app-logged',
  templateUrl: './logged.component.html',
  styleUrls: ['./logged.component.scss']
})
export class LoggedComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private toastrService: ToastrService,
    private overlaySpinnerService: OverlaySpinnerService,
    private elementRef: ElementRef
  ) { }

  ngOnInit(): void {

    this.route.queryParams.pipe(first()).subscribe(params => {
      const loginToken = params['login_token'];
      const spinner = this.overlaySpinnerService.showProgress(this.elementRef);
      this.userService.loginToken(loginToken, true).subscribe( user => {
        this.overlaySpinnerService.detach(spinner);
        const welcomeMessage = "Autenticato come "+user.nome+" "+user.cognome+" ("+user.codiceFiscale+")";
        this.toastrService.success(welcomeMessage);
        this.route.data.pipe(take(1)).subscribe(data => {
          const redirectTo = data?.redirectTo || 'cards';
          console.log('logged, redirect to: '+redirectTo);
          this.router.navigate([redirectTo]);
        })

      }, manageError("Errore finalizzando l\'autenticazione", this.toastrService, () => {this.overlaySpinnerService.detach(spinner)}) );

    });
  }

}
