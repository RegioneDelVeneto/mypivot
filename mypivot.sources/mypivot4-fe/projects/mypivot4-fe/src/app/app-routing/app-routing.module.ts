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
import {
    LoggedComponent
} from 'projects/mypay4-fe-common/src/lib/components/logged/logged.component';
import { AccessGuard } from 'projects/mypay4-fe-common/src/public-api';

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {
    AccertamentiCapitoliAnagraficaComponent
} from '../components/accertamenti-capitoli-anagrafica/accertamenti-capitoli-anagrafica.component';
import {
    AccertamentiCapitoliComponent
} from '../components/accertamenti-capitoli/accertamenti-capitoli.component';
import {
    AccertamentiCreazioneComponent
} from '../components/accertamenti-creazione/accertamenti-creazione.component';
import {
    AccertamentiDettaglioAssociaComponent
} from '../components/accertamenti-dettaglio-associa/accertamenti-dettaglio-associa.component';
import {
    AccertamentiDettaglioCruscottoComponent
} from '../components/accertamenti-dettaglio-cruscotto/accertamenti-dettaglio-cruscotto.component';
import {
    AccertamentiDettaglioComponent
} from '../components/accertamenti-dettaglio/accertamenti-dettaglio.component';
import { AccertamentiComponent } from '../components/accertamenti/accertamenti.component';
import { EnteComponent } from '../components/admin/ente/ente.component';
import { EntiComponent } from '../components/admin/enti/enti.component';
import { TipoDovutoComponent } from '../components/admin/tipo-dovuto/tipo-dovuto.component';
import {
    AccertamentiComponent as AccertamentiCardComponent
} from '../components/cards/accertamenti/accertamenti.component';
import { AdminComponent } from '../components/cards/admin/admin.component';
import { FlussiComponent } from '../components/cards/flussi/flussi.component';
import { HomeComponent as HomeCardsComponent } from '../components/cards/home/home.component';
import {
    StatisticheComponent as StatisticheCardComponent
} from '../components/cards/statistiche/statistiche.component';
import { VisualizzaComponent } from '../components/cards/visualizza/visualizza.component';
import { FlussiExportComponent } from '../components/flussi-export/flussi-export.component';
import { FlussiImportComponent } from '../components/flussi-import/flussi-import.component';
import { HomeComponent } from '../components/home/home.component';
import { ImportMassivoComponent } from '../components/import-massivo/import-massivo.component';
import { NotAuthorizedComponent } from '../components/not-authorized/not-authorized.component';
import {
    RendicontazioneDetailComponent
} from '../components/rendicontazione-detail/rendicontazione-detail.component';
import { RendicontazioneComponent } from '../components/rendicontazione/rendicontazione.component';
import {
    RicevuteTelematicheVisualizzazioneComponent
} from '../components/ricevute-telematiche-visualizzazione/ricevute-telematiche-visualizzazione.component';
import {
    RiconciliazioneDetailComponent
} from '../components/riconciliazione-detail/riconciliazione-detail.component';
import { RiconciliazioneComponent } from '../components/riconciliazione/riconciliazione.component';
import {
    SegnalazioneStoricoComponent
} from '../components/segnalazione-storico/segnalazione-storico.component';
import {
    StatisticheAnnomesegiornoComponent
} from '../components/statistiche-annomesegiorno/statistiche-annomesegiorno.component';
import {
    StatisticheCapitoliComponent
} from '../components/statistiche-capitoli/statistiche-capitoli.component';
import {
    StatisticheDettaglioCruscottoComponent
} from '../components/statistiche-dettaglio-cruscotto/statistiche-dettaglio-cruscotto.component';
import { StatisticheComponent } from '../components/statistiche/statistiche.component';
import {
    TesorerieDetailComponent
} from '../components/tesorerie-detail/tesorerie-detail.component';
import { TesorerieComponent } from '../components/tesorerie/tesorerie.component';
import { ForcedMailValidationGuard } from './forced-mail-validation-guard';

export const routes: Routes = [
  {path: 'home', component: HomeComponent, data:{breadcrumbs:{home:true, label:"Home"}}},
  {path: 'logged', component: LoggedComponent},
  {path: 'cards', component: HomeCardsComponent, data:{requiresLogin: true, menuItemId: 15, breadcrumb:{home:true}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'not-authorized', component: NotAuthorizedComponent, data:{requiresLogin: true, breadcrumb:{label:"Errore"}}, canActivate: [ AccessGuard ]},
  {path: 'flussi', component: FlussiComponent, data:{requiresLogin: true, menuItemId: 15, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'flussi-import', component: FlussiImportComponent, data:{requiresLogin: true, menuItemId: 21, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'flussi-export', component: FlussiExportComponent, data:{requiresLogin: true, menuItemId: 22, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'statistiche', component: StatisticheCardComponent, data:{requiresLogin: true, menuItemId: 30, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'statistiche/:statisticheMode', component: StatisticheComponent, data:{requiresLogin: true, menuItemId: 31, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'statistichecapitoli', component: StatisticheCapitoliComponent, data:{requiresLogin: true, menuItemId: 33, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'statisticheannomesegiorno', component: StatisticheAnnomesegiornoComponent, data:{requiresLogin: true, menuItemId: 35, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'statistichedettaglio', component: StatisticheDettaglioCruscottoComponent, data:{requiresLogin: true, menuItemId: 33, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'admin', component: AdminComponent, data:{requiresLogin: true, menuItemId: 40, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'admin/enti', component: EntiComponent, data:{requiresLogin: true, menuItemId: 41, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'admin/enti/:enteId', component: EnteComponent, data:{requiresLogin: true, menuItemId: 41, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'admin/tipo-dovuto/:tipoDovutoId', component: TipoDovutoComponent, data:{requiresLogin: true, menuItemId: 41, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'accertamenti', component: AccertamentiCardComponent, data:{requiresLogin: true, menuItemId: 60, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'accertamenti/main', component: AccertamentiComponent, data:{requiresLogin: true, menuItemId: 51, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'accertamenti/anagrafica', component: AccertamentiCreazioneComponent, data:{requiresLogin: true, menuItemId: 50, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'accertamenti/dettaglio/:accertamentoId', component: AccertamentiDettaglioComponent, data:{requiresLogin: true, menuItemId: 51, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'accertamenti/dettaglio/:accertamentoId/:associaMode', component: AccertamentiDettaglioAssociaComponent, data:{requiresLogin: true, menuItemId: 51, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'accertamenti/dettaglio-cruscotto', component: AccertamentiDettaglioCruscottoComponent, data:{requiresLogin: true, menuItemId: 51, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'accertamenti/capitoli', component: AccertamentiCapitoliComponent, data:{requiresLogin: true, menuItemId: 53, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'accertamenti/capitoli/anagrafica', component: AccertamentiCapitoliAnagraficaComponent, data:{requiresLogin: true, menuItemId: 53, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'accertamenti/capitoli/anagrafica/:anagraficaId', component: AccertamentiCapitoliAnagraficaComponent, data:{requiresLogin: true, menuItemId: 53, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'accertamenti/capitoli/importMassivo', component: ImportMassivoComponent, data:{requiresLogin: true, menuItemId: 53, breadcrumb:{label:"Accertamento/ Import Massivo"}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'visualizzazione', component: VisualizzaComponent, data:{requiresLogin: true, menuItemId: 60, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'visualizzazione/riconciliazioni', component: RiconciliazioneComponent, data:{type:'R', requiresLogin: true, menuItemId: 61, breadcrumb:{label:"Riconciliazioni"}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'visualizzazione/riconciliazioni/:searchType/:iuf/:iud/:iuv', component: RiconciliazioneDetailComponent, data:{requiresLogin: true, menuItemId: 61, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'visualizzazione/anomalie', component: RiconciliazioneComponent, data:{type:'A', requiresLogin: true, menuItemId: 64, breadcrumb:{label:"Anomalie"}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'visualizzazione/rendicontazione', component: RendicontazioneComponent, data:{requiresLogin: true, menuItemId: 62, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'visualizzazione/rendicontazione/:iuf/:iur', component: RendicontazioneDetailComponent, data:{requiresLogin: true, menuItemId: 62, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'visualizzazione/ricevute-telematiche', component: RicevuteTelematicheVisualizzazioneComponent, data:{requiresLogin: true, menuItemId: 62, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'visualizzazione/tesoreria', component: TesorerieComponent, data:{requiresLogin: true, menuItemId: 66, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'visualizzazione/tesoreria/dettaglio', component: TesorerieDetailComponent, data:{requiresLogin: true, menuItemId: 66, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: 'visualizzazione/segnalazione/storico', component: SegnalazioneStoricoComponent, data:{requiresLogin: true, menuItemId: 67, breadcrumb:{}}, canActivate: [ AccessGuard, ForcedMailValidationGuard ]},
  {path: '', redirectTo: 'cards', pathMatch: 'full'},
  {path: '**', redirectTo: 'cards', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
