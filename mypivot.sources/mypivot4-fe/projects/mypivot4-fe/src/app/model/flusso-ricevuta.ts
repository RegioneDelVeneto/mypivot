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
import { DateTime } from 'luxon';
import { MapperDef, MapperType, WithActions } from 'projects/mypay4-fe-common/src/public-api';

export class FlussoRicevuta extends WithActions {

  public static readonly MAPPER_DEF = [
    new MapperDef(MapperType.DateTime,'dateEsitoFrom','local-date'),
    new MapperDef(MapperType.DateTime,'dateEsitoTo','local-date'),
    new MapperDef(MapperType.DateTime,'dtEDatiPagDatiSingPagDataEsitoSingoloPagamento','local-date'),
    new MapperDef(MapperType.Function,'tipoIdUnivocoPagatore',(flusso: FlussoRicevuta) => {
      switch(flusso.codESoggPagIdUnivPagTipoIdUnivoco) {
        case 'F':
          return '(Persona fisica)';
        case 'G':
          return '(Persona giuridica)';
        default:
          return '';
      }
    }),
    new MapperDef(MapperType.Function,'tipoIdUnivocoVersante',(flusso: FlussoRicevuta) => {
      switch(flusso.codESoggVersIdUnivVersTipoIdUnivoco) {
        case 'F':
          return '(Persona fisica)';
        case 'G':
          return '(Persona giuridica)';
        default:
          return '';
      }
    }),
  ]

  codiceIpaEnte: string;
  codIud: string; //IUD
  codRpSilinviarpIdUnivocoVersamento: string; //IUV
  codEDatiPagDatiSingPagIdUnivocoRiscoss: string; //IUR
  //private BigDecimal numEDatiPagImportoTotalePagato; //Importo totale
  numEDatiPagDatiSingPagSingoloImportoPagato: number; //Importo
  dtEDatiPagDatiSingPagDataEsitoSingoloPagamento: DateTime; //Data esito
  deEIstitAttDenominazioneAttestante: string; //Attestante
  codESoggPagAnagraficaPagatore: string; //Anagrafica pagatore
  codESoggPagIdUnivPagCodiceIdUnivoco: string; //CF pagatore
  codESoggPagIdUnivPagTipoIdUnivoco: string; //F('Persona Fisica') or G('Persona Giuridica')
  deEDatiPagDatiSingPagCausaleVersamento: string; //Causale
  codESoggVersAnagraficaVersante: string; //Anagrafica versante
  codESoggVersIdUnivVersCodiceIdUnivoco: string; //CF versante
  codESoggVersIdUnivVersTipoIdUnivoco: string; //F('Persona Fisica') or G('Persona Giuridica')
  deTipoDovuto: string;

  details: object[];

  tipoIdUnivocoPagatore: string;
  tipoIdUnivocoVersante: string;
  deStato: string;
  codFiscalePa1: string;
}