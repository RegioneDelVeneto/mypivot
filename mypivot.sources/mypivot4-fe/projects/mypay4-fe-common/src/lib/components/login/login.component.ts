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

import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { faTimes } from '@fortawesome/free-solid-svg-icons';

import { environment } from '../../environments/environment';
import { ConfigurationService } from '../../services/configuration.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  iconTimes = faTimes;

  @ViewChild(ToastContainerDirective, { static: true })
  toastContainer: ToastContainerDirective;

  user = {username: '', password: '', remember: false};
  errorMessage = '';

  constructor(
    private toastrService: ToastrService,
    public dialogRef: MatDialogRef<LoginComponent>,
    private userService: UserService,
    private conf: ConfigurationService,
    private router: Router) { }

  ngOnInit(): void {
    this.toastrService.overlayContainer = this.toastContainer;
    this.user.username = this.conf.getProperty('fakeAuthUser', environment, '');
    this.user.password = this.conf.getProperty('fakeAuthPassword', environment, '');
  }

  onSubmit() {
    this.userService.loginPassword(this.user.username, this.user.password, this.user.remember)
      .subscribe( () => {
          let user = this.userService.getLoggedUser();
          //success
          console.log("logged user:", user);
          this.dialogRef.close();
          this.toastrService.overlayContainer = null;
          const welcomeMessage = "Autenticato come "+user.nome+" "+user.cognome+" ("+user.codiceFiscale+")";
          this.toastrService.success(welcomeMessage);
          this.router.navigate(['cards']);
      }, error => {
        this.toastrService.error(error, null, {disableTimeOut: true, positionClass:'toast-inline'});
      });

  }

}
