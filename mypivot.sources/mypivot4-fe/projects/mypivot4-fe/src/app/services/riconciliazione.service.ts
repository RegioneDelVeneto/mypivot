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
    KeyValue, MyPayTableDetailComponent
} from 'projects/mypay4-fe-common/src/lib/components/my-pay-table-detail/my-pay-table-detail.component';
import {
    ApiInvokerService, ConfigurationService, environment, MapperDef, Mappers, MapperType
} from 'projects/mypay4-fe-common/src/public-api';
import { map } from 'rxjs/operators';

import { CurrencyPipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Classificazione } from '../model/classificazione';
import { Ente } from '../model/ente';
import { Riconciliazione } from '../model/riconciliazione';
import { RiconciliazioneSearch } from '../model/riconciliazione-search';
import { Segnalazione } from '../model/segnalazione';

@Injectable({
  providedIn: 'root'
})
export class RiconciliazioneService {

  private baseApiUrl: string;

  constructor(
    private apiInvokerService: ApiInvokerService,
    private currencyPipe: CurrencyPipe, 
    conf: ConfigurationService
  ) {
    this.baseApiUrl = conf.getProperty('baseApiUrl', environment);
  }

  getSearchTypes(ente: Ente, viewType?: VisualizzazioneType) {
    let url = `${this.baseApiUrl}riconciliazione/classificazioni/${ente.mygovEnteId}`;
    if(viewType)
      url += `/${viewType}`;
    return this.apiInvokerService.get<Classificazione[]>(url, null, new Mappers({mapperS2C:MapperKeyDescr2CodeLabel}));
  }

  search(ente: Ente, searchType: string, searchParams: RiconciliazioneSearch) {
    return this.apiInvokerService.post<Riconciliazione[]>(`${this.baseApiUrl}riconciliazione/search/${ente.mygovEnteId}/${searchType}`,
      searchParams, null, new Mappers({mapperS2C: Riconciliazione, mapperC2S: RiconciliazioneSearch}));
  }

  detail(ente: Ente, searchType: string, iufKey: string, iudKey: string, iuvKey: string) {
    const params = new HttpParams()
      .append("iuf", iufKey || null)
      .append("iud", iudKey || null)
      .append("iuv", iuvKey || null);
    return this.apiInvokerService.get<Riconciliazione>(`${this.baseApiUrl}riconciliazione/detail/${ente.mygovEnteId}/${searchType}`,
      {params:params}, new Mappers({mapperS2C: Riconciliazione}));
  }

  export(ente: Ente, searchType: string, versioneTracciato: string, searchParams: RiconciliazioneSearch) {
    return this.apiInvokerService.post<any>(`${this.baseApiUrl}riconciliazione/export/${ente.mygovEnteId}/${searchType}/${versioneTracciato}`,
      searchParams, null, new Mappers({mapperC2S: RiconciliazioneSearch}))
      .pipe(map(x => x.requestToken));
  }

  getDisabledFiltersBySearchType(searchType: string) {
    const disabledFields = [];
    //riconciliazioni
    if(searchType=='IUD_RT_IUF_TES'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo');
    } else if(searchType=='RT_IUF_TES'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo');
    } else if(searchType=='RT_TES'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','dateRegolFrom','dateRegolTo');
    } else if(searchType=='RT_IUF'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','ordinante','conto','importoTesoreria','causale',
        'annoBolletta','codBolletta','annoDocumento','codDocumento','annoProvvisorio','codProvvisorio');
    } else if(searchType=='IUD_RT_IUF'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','ordinante','conto','importoTesoreria');
    //anomalie
    } else if(searchType=='IUD_NO_RT'){
      disabledFields.push('dateEsitoFrom','dateEsitoTo','dateRegolFrom','dateRegolTo','dateContabFrom','dateContabTo','dateValutaFrom','dateValutaTo','iud','iuv','iur','codFiscaleVersante','anagVersante','attestante','ordinante','idRendicont','idRegolamento','conto','importoTesoreria','annoBolletta','codBolletta','annoDocumento','codDocumento','annoProvvisorio','codProvvisorio');
    } else if(searchType=='RT_NO_IUD'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','annoBolletta','codBolletta','annoDocumento','codDocumento','annoProvvisorio','codProvvisorio');
    } else if(searchType=='RT_NO_IUF'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','dateRegolFrom','dateRegolTo','dateContabFrom','dateContabTo','dateValutaFrom','dateValutaTo','ordinante','idRendicont','idRegolamento','conto','importoTesoreria','causale','annoBolletta','codBolletta','annoDocumento','codDocumento','annoProvvisorio','codProvvisorio');
    } else if(searchType=='IUV_NO_RT'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','dateEsitoFrom','dateEsitoTo','dateContabFrom','dateContabTo','dateValutaFrom','dateValutaTo','iud','iuv','iur','codFiscalePagatore','anagPagatore','codFiscaleVersante','anagVersante','attestante','ordinante','tipoDovuto','conto','importoTesoreria','causale','annoBolletta','codBolletta','annoDocumento','codDocumento','annoProvvisorio','codProvvisorio');
    } else if(searchType=='IUF_NO_TES'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','dateEsitoFrom','dateEsitoTo','dateContabFrom','dateContabTo','dateValutaFrom','dateValutaTo','iud','iuv','iur','codFiscalePagatore','anagPagatore','codFiscaleVersante','anagVersante','attestante','ordinante','conto','importoTesoreria','causale','annoBolletta','codBolletta','annoDocumento','codDocumento','annoProvvisorio','codProvvisorio');
    } else if(searchType=='IUF_TES_DIV_IMP'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','dateEsitoFrom','dateEsitoTo','iud','iuv','iur','codFiscalePagatore','anagPagatore','codFiscaleVersante','anagVersante','attestante','tipoDovuto','annoBolletta','codBolletta','annoDocumento','codDocumento','annoProvvisorio','codProvvisorio');
    } else if(searchType=='TES_NO_IUF_OR_IUV'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','dateEsitoFrom','dateEsitoTo','dateRegolFrom','dateRegolTo','iud','iuv','iur','codFiscalePagatore','anagPagatore','codFiscaleVersante','anagVersante','attestante','idRegolamento','tipoDovuto','causale','annoBolletta','codBolletta','annoDocumento','codDocumento','annoProvvisorio','codProvvisorio');
    } else if(searchType=='TES_NO_MATCH'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','dateEsitoFrom','dateEsitoTo','dateRegolFrom','dateRegolTo','iud','iuv','iur','codFiscalePagatore','anagPagatore','codFiscaleVersante','anagVersante','attestante','idRendicont','idRegolamento','tipoDovuto','annoBolletta','codBolletta','annoDocumento','codDocumento','annoProvvisorio','codProvvisorio');
    } else if(searchType=='DOPPI'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','dateUltModFrom','dateUltModTo',/*'dateContabFrom','dateContabTo','dateValutaFrom','dateValutaTo',*/'ordinante','conto','importoTesoreria','annoBolletta','codBolletta','annoDocumento','codDocumento','annoProvvisorio','codProvvisorio');
    } else if(searchType=='__TEMPLATE__'){
      disabledFields.push('dateEsecuzioneFrom','dateEsecuzioneTo','dateEsitoFrom','dateEsitoTo','dateUltModFrom','dateUltModTo','dateRegolFrom','dateRegolTo','dateContabFrom','dateContabTo','dateValutaFrom','dateValutaTo','iud','iuv','iur','codFiscalePagatore','anagPagatore','codFiscaleVersante','anagVersante','attestante','ordinante','idRendicont','idRegolamento','tipoDovuto','conto','importoTesoreria','causale','annoBolletta','codBolletta','annoDocumento','codDocumento','annoProvvisorio','codProvvisorio');
    }
    return disabledFields;
  }

  hasDetailRt(detail: Riconciliazione): boolean {
    return ['IUV_NO_RT','IUF_NO_TES','IUF_TES_DIV_IMP'].includes(detail?.classificazione);
  }
  isRiconciliazioneSearchType(searchType: string): boolean {
    return ['IUD_RT_IUF_TES','RT_IUF_TES','RT_TES','RT_IUF','IUD_RT_IUF'].includes(searchType);
  }

  mapDetailToKeyValueSections(detail: Riconciliazione): KeyValue[] {
    const details = [];
    if(['IUD_RT_IUF_TES','IUD_RT_IUF','IUD_NO_RT'].includes(detail?.classificazione))
      details.push( ...[
        {key:MyPayTableDetailComponent.SECTION_ID, value:'Notificato'},
        {key:'Tipo dovuto', value:detail.deTipoDovuto},
        {key:'IUD', value:detail.iud},
        {key:'Importo', value:this.currencyPipe.transform(detail.importo,'EUR','symbol')},
        {key:'Data esecuzione', value: detail.dataEsecuzione?.toFormat('dd/MM/yyyy')},
        {key:'Anagrafica pagatore', value:detail.pagatoreAnagrafica},
        {key:'Codice fiscale pagatore', value:detail.pagatoreCodFisc},
        {key:'Causale', value:detail.causale},
        {key:'Dati specifici riscossione', value:detail.datiSpecificiRiscossione},
      ] );
    if(['IUD_RT_IUF_TES','RT_IUF_TES','RT_TES','RT_IUF','IUD_RT_IUF','RT_NO_IUD','RT_NO_IUF','DOPPI'].includes(detail?.classificazione))
      details.push( ...[
        {key:MyPayTableDetailComponent.SECTION_ID, value:'Ricevuta telematica'},
        {key:'Tipo dovuto', value:detail.deTipoDovuto},
        {key:'IUV', value:detail.iuv},
        {key:'IUD', value:detail.iud},
        {key:'IUR', value:detail.iur},
        {key:'Importo', value:this.currencyPipe.transform(detail.importo,'EUR','symbol')},
        {key:'Data esito', value:detail.dataEsito?.toFormat('dd/MM/yyyy')},
        {key:'Anagrafica attestante', value:detail.attestanteAnagrafica},
        {key:'Codice fiscale attestante', value:detail.attestanteCodFisc},
        {key:'Anagrafica pagatore', value:detail.pagatoreAnagrafica},
        {key:'Codice fiscale pagatore', value:detail.pagatoreCodFisc},
        {key:'Anagrafica versante', value:detail.versanteAnagrafica},
        {key:'Codice fiscale versante', value:detail.versanteCodFisc},
        {key:'Causale', value:detail.causale},
      ] );
    if(['IUD_RT_IUF_TES','RT_IUF_TES','RT_IUF','IUD_RT_IUF','RT_NO_IUD','IUV_NO_RT','IUF_NO_TES','IUF_TES_DIV_IMP','DOPPI'].includes(detail?.classificazione))
      details.push( ...[
        {key:MyPayTableDetailComponent.SECTION_ID, value:'Rendicontazione pagoPA'},
        {key:'Id rendicontazione', value:detail.idRendicontazione},
        {key:'Data ora flusso', value:detail.dataFlusso?.toFormat('dd/MM/yyyy HH:mm:ss')},
        {key:'Id regolamento', value:detail.idRegolamento},
        {key:'Data regolamento', value:detail.dataRegolamento?.toFormat('dd/MM/yyyy')},
        {key:'Importo totale', value:this.currencyPipe.transform(detail.importoTotale,'EUR','symbol')},
      ] );
    if(['IUD_RT_IUF_TES','RT_IUF_TES','RT_TES','RT_NO_IUD','IUV_NO_RT','IUF_TES_DIV_IMP','TES_NO_IUF_OR_IUV'].includes(detail?.classificazione))
      details.push( ...[
        {key:MyPayTableDetailComponent.SECTION_ID, value:'Giornale di cassa'},
        {key:'Conto', value:detail.conto},
        {key:'Data valuta', value:detail.dataValuta?.toFormat('dd/MM/yyyy')},
        {key:'Data contabile', value:detail.dataContabile?.toFormat('dd/MM/yyyy')},
        {key:'Importo tesoreria', value:this.currencyPipe.transform(detail.importoTesoreria,'EUR','symbol')},
        {key:'Ordinante', value:detail.ordinante},
        {key:'Anno bolletta', value:detail.annoBolletta},
        {key:'Codice bolletta', value:detail.codiceBolletta},
        {key:'Anno documento', value:detail.annoDocumento},
        {key:'Codice documento', value:detail.codDocumento},
        {key:'Anno provvisorio', value:detail.annoProvvisorio},
        {key:'Codice provvisorio', value:detail.codProvvisorio},
      ] );
    //segnalazione attiva
    if(detail.notaSegnalazione)
      details.push( ...[
        {key:MyPayTableDetailComponent.SECTION_ID, value:'Segnalazione'},
        {key:'Nota', value:detail.notaSegnalazione},
        {key:'Utente', value:detail.utenteSegnalazione},
        {key:'Cod. Fiscale', value:detail.cfUtenteSegnalazione},
        {key:'Data inserimento', value:detail.dataInserimentoSegnalazione?.toFormat('dd/MM/yyyy HH:mm:ss')},
      ] );

    return details;
  }

  updateDetailSegnalazione(details: KeyValue[], segnalazione: Segnalazione) {
    const idx = details.findIndex(x => x.key===MyPayTableDetailComponent.SECTION_ID && x.value==='Segnalazione');
    if(idx != -1)
      details.splice(idx, 4);
    details.push( ...[
      {key:MyPayTableDetailComponent.SECTION_ID, value:'Segnalazione'},
      {key:'Nota', value:segnalazione.nota},
      {key:'Utente', value:segnalazione.utente},
      {key:'Cod. Fiscale', value:segnalazione.cfUtente},
      {key:'Data inserimento', value:segnalazione.dtInserimento?.toFormat('dd/MM/yyyy HH:mm:ss')},
    ]);
  }
}

export class MapperKeyDescr2CodeLabel {
  public static readonly MAPPER_S2C_DEF = [
    new MapperDef(MapperType.Rename,'code','key'),
    new MapperDef(MapperType.Rename,'detailText','infoText'),
  ]
}

export type VisualizzazioneType = 'A' | 'R';
