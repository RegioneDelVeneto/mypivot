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
import { HttpBackend, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { environment } from '../environments/environment';
import { User } from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {

  private externalizedConfiguration: {} = {};
  private backendConfig: {};
  private userFromCookie: User;

  constructor(
    private httpBackend: HttpBackend
  ) {}

  bootstrapConfig(): Promise<any> {
    let promise:Promise<void> = new HttpClient(this.httpBackend)
      .get("assets/conf/environment.json")
      .toPromise()
      .then(data => {
        if(data && typeof data === 'object'){
          this.externalizedConfiguration = data;
          console.log('externalized configuration: ',this.externalizedConfiguration );
          return null;
        }
        return;
      }, error => {
        if(error?.status==404)
          console.log('missing externalized configuration');
        else
          console.log('error externalized configuration', error);
      })
      .then(() => {
        return new HttpClient(this.httpBackend)
        .post<{}>(this.getProperty('baseApiUrl')+'public/info/config', null)
        .toPromise();
      })
      .then(data => {
        console.log('backend config', data);
        if(data && typeof data === 'object')
          this.backendConfig = data;

          const useAuthCookie = this.getBackendProperty<boolean>('useAuthCookie', false);
          if(useAuthCookie){
            return new HttpClient(this.httpBackend)
            .post<User>(this.getProperty('baseApiUrl')+'checkLoginCookie', null)
            .toPromise().then(user => {
              if(user && typeof user === 'object')
                this.userFromCookie = user;
              return;
            }, error => {
              console.log('error checkLoginCookie, ignoring it!', error);
            });
          }

        return;
      }, error => {
        console.log('error backend config', error);
      });

    return promise;
  }

  getBackendProperty<T = string>(key: string, defaultValue?: T):T {
    if(this.backendConfig?.hasOwnProperty(key))
      return this.backendConfig[key];
    return defaultValue;
  }

  getProperty<T = string>(key: string, appEnvironment?:object, defaultValue?: T):T {
    if(this.externalizedConfiguration.hasOwnProperty(key))
      return this.externalizedConfiguration[key];

      if(appEnvironment?.hasOwnProperty(key))
      return appEnvironment[key];

    if(environment.hasOwnProperty(key))
      return environment[key];

    return defaultValue;
  }

  getUserFromCookie():User {
    return this.userFromCookie;
  }

}
