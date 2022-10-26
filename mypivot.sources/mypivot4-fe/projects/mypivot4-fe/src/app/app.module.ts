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
import { FileSaverModule } from 'ngx-filesaver';
import { MAT_LUXON_DATE_ADAPTER_OPTIONS, MatLuxonDateModule } from 'ngx-material-luxon';
import { ToastContainerModule, ToastrModule } from 'ngx-toastr';
import {
    ConfirmDialogComponent
} from 'projects/mypay4-fe-common/src/lib/components/confirm-dialog/confirm-dialog.component';
import {
    HelpFieldComponent
} from 'projects/mypay4-fe-common/src/lib/components/help-field/help-field.component';
import { HelpComponent } from 'projects/mypay4-fe-common/src/lib/components/help/help.component';
import {
    MyPayTableDetailComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table-detail/my-pay-table-detail.component';
import {
    AccessGuard, AppConfirmDirective, ConfigurationService, DatepickerFormatMmYyyyDirective,
    DatepickerFormatYyyyDirective, DecodeHtmlPipe, DetailFilterPipe, DynamicOverlay,
    DynamicOverlayContainer, DynamicPipe, FileSizePipe, getItalianPaginatorIntl, GlobalPipe,
    JoinPipe, MapPipe, MyPayBreadcrumbsComponent, OverlaySpinnerContainerComponent,
    OverlaySpinnerService, TabbingClickDirective, TokenInterceptor
} from 'projects/mypay4-fe-common/src/public-api';

import { DragDropModule } from '@angular/cdk/drag-drop';
import { OverlayModule } from '@angular/cdk/overlay';
import { CurrencyPipe, DatePipe, registerLocaleData, TitleCasePipe } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import localeItExtra from '@angular/common/locales/extra/it';
import localeIt from '@angular/common/locales/it';
import { APP_INITIALIZER, DEFAULT_CURRENCY_CODE, LOCALE_ID, NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorIntl, MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSliderModule } from '@angular/material/slider';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import {
    LoggedComponent
} from '../../../mypay4-fe-common/src/lib/components/logged/logged.component';
import { LoginComponent } from '../../../mypay4-fe-common/src/lib/components/login/login.component';
import {
    MypSearchChipsComponent
} from '../../../mypay4-fe-common/src/lib/components/myp-search-chips/myp-search-chips.component';
import { ToDoComponent } from '../../../mypay4-fe-common/src/lib/components/to-do/to-do.component';
import { AppRoutingModule } from './app-routing/app-routing.module';
import { ForcedMailValidationGuard } from './app-routing/forced-mail-validation-guard';
import { AppComponent } from './app.component';
import {
    AccertamentiCapitoliAnagraficaComponent
} from './components/accertamenti-capitoli-anagrafica/accertamenti-capitoli-anagrafica.component';
import {
    AccertamentiCapitoliComponent
} from './components/accertamenti-capitoli/accertamenti-capitoli.component';
import {
    AccertamentiCreazioneComponent
} from './components/accertamenti-creazione/accertamenti-creazione.component';
import {
    AccertamentiDettaglioAssociaDialogComponent
} from './components/accertamenti-dettaglio-associa-dialog/accertamenti-dettaglio-associa-dialog.component';
import {
    AccertamentiDettaglioAssociaComponent
} from './components/accertamenti-dettaglio-associa/accertamenti-dettaglio-associa.component';
import {
    AccertamentiDettaglioCruscottoComponent
} from './components/accertamenti-dettaglio-cruscotto/accertamenti-dettaglio-cruscotto.component';
import {
    AccertamentiDettaglioComponent
} from './components/accertamenti-dettaglio/accertamenti-dettaglio.component';
import { AccertamentiComponent } from './components/accertamenti/accertamenti.component';
import { EnteComponent } from './components/admin/ente/ente.component';
import { EntiComponent } from './components/admin/enti/enti.component';
import { TipoDovutoComponent } from './components/admin/tipo-dovuto/tipo-dovuto.component';
import { AppCardsComponent } from './components/app-cards/app-cards.component';
import {
    AccertamentiComponent as AccertamentiCardsComponent
} from './components/cards/accertamenti/accertamenti.component';
import { AdminComponent } from './components/cards/admin/admin.component';
import { FlussiComponent } from './components/cards/flussi/flussi.component';
import { HomeComponent as HomeCardsComponent } from './components/cards/home/home.component';
import {
    StatisticheComponent as StatisticheCardsComponent
} from './components/cards/statistiche/statistiche.component';
import { VisualizzaComponent } from './components/cards/visualizza/visualizza.component';
import { EnteOverlayComponent } from './components/ente-overlay/ente-overlay.component';
import { FlussiExportComponent } from './components/flussi-export/flussi-export.component';
import { FlussiImportComponent } from './components/flussi-import/flussi-import.component';
import { FooterComponent } from './components/footer/footer.component';
import { HeaderComponent } from './components/header/header.component';
import { HomeComponent } from './components/home/home.component';
import { ImportMassivoComponent } from './components/import-massivo/import-massivo.component';
import {
    MyPayTablePivotComponent
} from './components/my-pay-table-pivot/my-pay-table-pivot.component';
import { NotAuthorizedComponent } from './components/not-authorized/not-authorized.component';
import {
    RendicontazioneDetailComponent
} from './components/rendicontazione-detail/rendicontazione-detail.component';
import { RendicontazioneComponent } from './components/rendicontazione/rendicontazione.component';
import {
    RicevuteTelematicheVisualizzazioneComponent
} from './components/ricevute-telematiche-visualizzazione/ricevute-telematiche-visualizzazione.component';
import {
    RiconciliazioneDetailComponent
} from './components/riconciliazione-detail/riconciliazione-detail.component';
import { RiconciliazioneComponent } from './components/riconciliazione/riconciliazione.component';
import { SegnalazioneAddComponent } from './components/segnalazione-add/segnalazione-add.component';
import {
    SegnalazioneStoricoComponent
} from './components/segnalazione-storico/segnalazione-storico.component';
import {
    StatisticheAnnomesegiornoComponent
} from './components/statistiche-annomesegiorno/statistiche-annomesegiorno.component';
import {
    StatisticheCapitoliComponent
} from './components/statistiche-capitoli/statistiche-capitoli.component';
import {
    StatisticheDettaglioCruscottoComponent
} from './components/statistiche-dettaglio-cruscotto/statistiche-dettaglio-cruscotto.component';
import { StatisticheComponent } from './components/statistiche/statistiche.component';
import { TesorerieDetailComponent } from './components/tesorerie-detail/tesorerie-detail.component';
import { TesorerieComponent } from './components/tesorerie/tesorerie.component';
import { SidenavService } from './services/sidenav.service';

registerLocaleData(localeIt, localeItExtra);

export function bootstrapMyPayConfig(configurationService: ConfigurationService) {
  return () => configurationService.bootstrapConfig();
}

@NgModule({
  declarations: [
    AccertamentiCapitoliAnagraficaComponent,
    AccertamentiCapitoliComponent,
    AccertamentiCardsComponent,
    AccertamentiComponent,
    AccertamentiCreazioneComponent,
    AccertamentiDettaglioAssociaComponent,
    AccertamentiDettaglioAssociaDialogComponent,
    AccertamentiDettaglioComponent,
    AccertamentiDettaglioCruscottoComponent,
    AdminComponent,
    AppComponent,
    AppCardsComponent,
    AppConfirmDirective,
    ConfirmDialogComponent,
    DatepickerFormatMmYyyyDirective,
    DatepickerFormatYyyyDirective,
    DecodeHtmlPipe,
    DetailFilterPipe,
    DynamicPipe,
    EnteComponent,
    EnteOverlayComponent,
    EntiComponent,
    FileSizePipe,
    FlussiExportComponent,
    FlussiImportComponent,
    FooterComponent,
    GlobalPipe,
    HeaderComponent,
    HelpComponent,
    HelpFieldComponent,
    HomeCardsComponent,
    HomeComponent,
    ImportMassivoComponent,
    JoinPipe,
    LoggedComponent,
    LoginComponent,
    MapPipe,
    MyPayBreadcrumbsComponent,
    MyPayTableDetailComponent,
    MyPayTablePivotComponent,
    MypSearchChipsComponent,
    NotAuthorizedComponent,
    OverlaySpinnerContainerComponent,
    RendicontazioneComponent,
    RendicontazioneDetailComponent,
    RicevuteTelematicheVisualizzazioneComponent,
    RiconciliazioneComponent,
    RiconciliazioneDetailComponent,
    SegnalazioneAddComponent,
    SegnalazioneStoricoComponent,
    StatisticheAnnomesegiornoComponent,
    StatisticheCapitoliComponent,
    StatisticheCardsComponent,
    StatisticheComponent,
    StatisticheDettaglioCruscottoComponent,
    TabbingClickDirective,
    TesorerieComponent,
    TesorerieDetailComponent,
    TipoDovutoComponent,
    ToDoComponent,
    VisualizzaComponent,
    FlussiComponent,
    AdminComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    DragDropModule,
    FileSaverModule,
    FlexLayoutModule,
    FontAwesomeModule,
    FormsModule,
    HttpClientModule,
    MatAutocompleteModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatGridListModule,
    MatInputModule,
    MatListModule,
    MatLuxonDateModule,
    MatMenuModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatSelectModule,
    MatSidenavModule,
    MatSlideToggleModule,
    MatSliderModule,
    MatSortModule,
    MatTableModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    OverlayModule,
    ReactiveFormsModule,
    ToastContainerModule,
    ToastrModule.forRoot({
        timeOut: 5000,
        positionClass: 'toast-top-center',
        closeButton: true,
        progressBar: true,
      }),
  ],
  providers: [
    AccessGuard,
    CurrencyPipe, 
    DatePipe,
    DecodeHtmlPipe, 
    DetailFilterPipe, 
    DynamicOverlay, 
    DynamicOverlayContainer,
    DynamicPipe, 
    FileSizePipe, 
    ForcedMailValidationGuard,
    GlobalPipe,
    JoinPipe, 
    MapPipe,
    OverlaySpinnerService, 
    SidenavService,
    TitleCasePipe, 
    { provide: APP_INITIALIZER, useFactory: bootstrapMyPayConfig, multi: true, deps: [ConfigurationService] },
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
    { provide: LOCALE_ID, useValue: 'it-IT' },  // TODO: verify need for internationalization. Now italian is forced
    { provide: DEFAULT_CURRENCY_CODE, useValue: '€' },
    { provide: MatPaginatorIntl, useValue: getItalianPaginatorIntl() },
    { provide: MAT_LUXON_DATE_ADAPTER_OPTIONS, useValue: {
      firstDayOfWeek: (locale: string) => {return 1;} // 0 = Sunday, 1 = Monday, etc
    } },
    { provide: MAT_DATE_FORMATS, useValue: {
        parse: {
          dateInput: 'dd/MM/yyyy',
        },
        display: {
          dateInput: 'dd/MM/yyyy',
          monthYearLabel: 'MMM/yyyy',
          dateA11yLabel: 'dd/MM/yyyy',
          monthYearA11yLabel: 'MMM/yyyy',
        },
      } },
  ],
  entryComponents: [
    LoginComponent,
    OverlaySpinnerContainerComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
