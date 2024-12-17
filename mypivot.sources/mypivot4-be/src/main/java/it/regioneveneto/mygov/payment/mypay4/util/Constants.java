/**
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
package it.regioneveneto.mygov.payment.mypay4.util;

import it.regioneveneto.mygov.payment.mypay4.exception.ValidatorException;

import java.math.BigDecimal;
import java.util.Arrays;

public class Constants {

  public enum VERSIONE_TRACCIATO_EXPORT {
    VERSIONE_1_0("1.0")
    , VERSIONE_1_1("1.1")
    , VERSIONE_1_2("1.2")
    , VERSIONE_1_3("1.3")
    , VERSIONE_1_4("1.4")
    ;
    String value;

    public String getValue() {
      return this.value;
    }

    VERSIONE_TRACCIATO_EXPORT(String value) {
      this.value = value;
    }

    public static VERSIONE_TRACCIATO_EXPORT fromString(String ver) {
      return Arrays.stream(VERSIONE_TRACCIATO_EXPORT.values())
          .filter(e -> e.value.equals(ver)).findFirst()
          .orElseThrow(() -> new ValidatorException("Invalid Versione Tracciato: " + ver));
    }
  }

  public static final String DEFAULT_VERSIONE_TRACCIATO = "1.0";


  public enum TIPO_FLUSSO {
    EXPORT_PAGATI("E"),
    EXPORT_PAGATI_ENTI_SECONDARI("S"),
    RENDICONTAZIONE_STANDARD("R"),
    TESORERIA("T"),
    GIORNALE_DI_CASSA("C"),
    GIORNALE_DI_CASSA_OPI("O"),
    ESTRATTO_CONTO_POSTE("Y"),
    DOVUTI("D");

    String value;

    TIPO_FLUSSO(String value) {
      this.value = value;
    }

    public String getCod() {
      return this.value;
    }

    public static TIPO_FLUSSO of(String cod){
      return Arrays.stream(TIPO_FLUSSO.values()).filter(e -> e.getCod().equals(cod)).findFirst().orElseThrow();
    }
  }

  public static final String COD_PROVENIENZA_FILE_WEB = "WEB";
  public static final String COD_PROVENIENZA_FILE_BATCH = "batch";

  //STATI ENTE
  public final static String STATO_ENTE_INSERITO = "INSERITO";
  public final static String STATO_ENTE_PRE_ESERCIZIO = "PRE-ESERCIZIO";
  public final static String STATO_ENTE_ESERCIZIO = "ESERCIZIO";

  //Tipi Pagamenti
  public final static String ALL_PAGAMENTI = "ALL";
  public final static String PAY_BONIFICO_BANCARIO_TESORERIA = "BBT";
  public final static String PAY_BONIFICO_POSTALE = "BP";
  public final static String PAY_ADDEBITO_DIRETTO = "AD";
  public final static String PAY_CARTA_PAGAMENTO = "CP";
  public final static String PAY_PRESSO_PSP = "PO";
  public final static String PAY_MYBANK = "OBEP";


  public final static String  DATI_SPECIFICI_RISCOSSIONE_UNKNOW = "9/---";


  public final static String COD_MARCATURA_REND_9 = "-9-";


  public final static String CODICE_CONTESTO_PAGAMENTO_NA = "n/a";
  public final static String PAGATO_CON_RENDICONTAZIONE_9 = "PAGATO CON RENDICONTAZIONE 9";
  public final static String CODICE_AUTENTICAZIONE_SOGGETTO_NA = "N/A";


  public static final String SIL = "SIL";


  public final static String EMAIL_PATTERN = "^[A-Za-z0-9_]+([\\-\\+\\.'][A-Za-z0-9_]+)*@[A-Za-z0-9_]+([\\-\\.][A-Za-z0-9_]+)*\\.[A-Za-z0-9_]+([\\-\\.][A-Za-z0-9_]+)*$";
  public final static String DATI_SPECIFICI_RISCOSSIONE_PATTERN = "[0129]{1}\\/\\S{3,138}";
  public final static String ANAGRAFICA_INDIRIZZO_PATTERN = "[a-z A-Z0-9.,()/'&]{1,70}";
  public final static String ANAGRAFICA_CIVICO_PATTERN = "[a-z A-Z0-9.,()/'&]{1,16}";
  public final static String NOT_ANAGRAFICA_INDIRIZZO_PATTERN = "[^a-z A-Z0-9.,()/'&]{1,70}";
  public final static String NOT_ANAGRAFICA_CIVICO_PATTERN = "[^a-z A-Z0-9.,()/'&]{1,16}";
  public final static String NOT_ANAGRAFICA_LOC_PROV_PATTERN = "[^a-z A-Z0-9.,()/'&]{1,35}";
  public static final String CAUSALE_PATTERN = "[^a-z A-Z0-9/\\-?:().,'+]{1,1024}";
  public static final String CAUSALE_TRUNCATE_PATTERN = "[%1$.140s]";

  public final static BigDecimal MAX_AMOUNT = BigDecimal.valueOf(999999999.99);

  public final static String IDENTIFICATIVO_PSP_POSTE = "BPPIITRRXXX";
  public final static int MAX_LENGHT_ANAGRAFICA_UTENTE_POSTE = 50;
  public final static int MAX_LENGHT_INDIRIZZO_PLUS_CIVICO_POSTE = 50;
  public final static int MAX_LENGHT_CAUSALE = 140;


  /** Constants appended for Mypivot from here **/
  /*
   * DE TIPI STATO
   */
  public static final String DE_TIPO_STATO_MANAGE = "MANAGE";
  public static final String DE_TIPO_STATO_PRENOTA_FLUSSO_RICONCILIAZIONE = "EXPORT_FLUSSO_RICONCILIAZIONE";
  public static final String DE_TIPO_STATO_ALL = "ALL";
  public static final String DE_TIPO_STATO_ACCERTAMENTO = "ACCERTAMENTO";

  /*
   * COD STATO
   */
  public static String COD_TIPO_STATO_MANAGE_FLUSSO_OBSOLETO = "FLUSSO_OBSOLETO";
  public static String COD_TIPO_STATO_MANAGE_FILE_IN_DOWNLOAD = "FILE_IN_DOWNLOAD";
  public static String COD_TIPO_STATO_MANAGE_FILE_SCARICATO = "FILE_SCARICATO";
  public static String COD_TIPO_STATO_MANAGE_FILE_IN_CARICAMENTO = "FILE_IN_CARICAMENTO";
  public static String COD_TIPO_STATO_MANAGE_FILE_CARICATO = "FILE_CARICATO";
  public static String COD_TIPO_STATO_MANAGE_ERROR_DOWNLOAD = "ERROR_DOWNLOAD";
  public static String COD_TIPO_STATO_MANAGE_ERROR_LOAD = "ERROR_LOAD";

  public static final String COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_PRENOTATO = "PRENOTATO";
  public static final String COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_ERRORE_EXPORT_FLUSSO_RICONCILIAZIONE = "ERRORE_EXPORT_FLUSSO_RICONCILIAZIONE";
  public static final String COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_NUMERO_MASSIMO_EXPORT_RIGHE_CONSENTITO_SUPERATO = "NUMERO_MASSIMO_EXPORT_RIGHE_CONSENTITO_SUPERATO";
  public static final String COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_EXPORT_ESEGUITO = "EXPORT_ESEGUITO";
  public static final String COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_EXPORT_ESEGUITO_NESSUN_RECORD_TROVATO = "EXPORT_ESEGUITO_NESSUN_RECORD_TROVATO";
  public static final String COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_VERSIONE_TRACCIATO_ERRATA = "VERSIONE_TRACCIATO_ERRATA";
  public static final String COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_CLASSIFICAZIONE_COMPLETEZZA_NON_VALIDA = "CLASSIFICAZIONE_COMPLETEZZA_NON_VALIDA";

  public static final String COD_TIPO_STATO_IN_CARICO = "IN_CARICO";

  /*
   * Stati che descrivono l'evoluzione dell'Accertamento.
   * @author Marianna Memoli
   */
  public static final String COD_TIPO_STATO_ACCERTAMENTO_INSERITO = 	"INSERITO";
  public static final String COD_TIPO_STATO_ACCERTAMENTO_CHIUSO = 	"CHIUSO";
  public static final String COD_TIPO_STATO_ACCERTAMENTO_ANNULLATO = 	"ANNULLATO";

  public static final String CODE_PIVOT_RICHIESTA_PER_IUV_IUF_NON_COMPLETA = "PIVOT_RICHIESTA_PER_IUV_IUF_NON_COMPLETA";
  public static final String DESC_PIVOT_RICHIESTA_PER_IUV_IUF_NON_COMPLETA = "Valorizzare almeno un oggetto tra 'riversamentiPuntuali' e 'riversamentiCumulativi'";
  public static final String CODE_PIVOT_RICHIESTA_NON_VALORIZZATA = "PIVOT_RICHIESTA_NON_VALORIZZATA";
  public static final String DESC_PIVOT_RICHIESTA_NON_VALORIZZATA = "Valorizzare almeno un oggetto tra 'ctRichiestaPerIUVIUF' e 'ctRichiestaPerData'";
  public static final String CODE_PIVOT_DATE_FROM_NON_VALIDO = "CODE_PIVOT_DATE_FROM_NON_VALIDO";
  public static final String DESC_PIVOT_DATE_FROM_NON_VALIDO = "Data inizio non valida";
  public static final String CODE_PIVOT_DATE_TO_NON_VALIDO = "CODE_PIVOT_DATE_TO_NON_VALIDO";
  public static final String DESC_PIVOT_DATE_TO_NON_VALIDO = "Data fine non valida";
  public static final String CODE_PIVOT_INTERVALLO_DATE_NON_VALIDO = "CODE_PIVOT_INTERVALLO_DATE_NON_VALIDO";
  public static final String DESC_PIVOT_INTERVALLO_DATE_NON_VALIDO = "L’intervallo data inizio e data fine non è valido";
  public static final String CODE_PIVOT_SYSTEM_ERROR = "CODE_PIVOT_SYSTEM_ERROR";
  public static final String CODE_PIVOT_REQUEST_TOKEN_NON_VALIDO = "PIVOT_REQUEST_TOKEN_NON_VALIDO";
  public static final String CODE_PIVOT_TIPO_FLUSSO_NON_VALIDO = "CODE_PIVOT_TIPO_FLUSSO_NON_VALIDO";
  public static final String CODE_PIVOT_ENTE_NON_VALIDO = "CODE_PIVOT_ENTE_NON_VALIDO";

  /**  Fault Constants: Fault code in the response of SOAP.  **/
  public static final String PIVOT_SYSTEM_ERROR = "PIVOT_SYSTEM_ERROR";
  public static final String PIVOT_ENTE_NON_VALIDO = "PIVOT_ENTE_NON_VALIDO";
  public static final String PIVOT_DATE_FROM_NON_VALIDO = "PIVOT_DATE_FROM_NON_VALIDO";
  public static final String PIVOT_DATE_TO_NON_VALIDO = "PIVOT_DATE_TO_NON_VALIDO";
  public static final String PIVOT_INTERVALLO_DATE_NON_VALIDO = "PIVOT_INTERVALLO_DATE_NON_VALIDO";
  public static final String PIVOT_IDENTIFICATIVO_TIPO_DOVUTO_NON_VALIDO = "PIVOT_IDENTIFICATIVO_TIPO_DOVUTO_NON_VALIDO";
  public static final String PIVOT_CLASSIFICAZIONE_NON_VALIDA = "PIVOT_CLASSIFICAZIONE_NON_VALIDA";
  public static final String PIVOT_CLASSIFICAZIONE_NON_ABILITATA = "PIVOT_CLASSIFICAZIONE_NON_ABILITATA";
  public static final String PIVOT_REQUEST_TOKEN_NON_VALIDO = "PIVOT_REQUEST_TOKEN_NON_VALIDO";
  public static final String PIVOT_VERSIONE_TRACCIATO_EXPORT_NON_VALIDA = "PIVOT_VERSIONE_TRACCIATO_EXPORT_NON_VALIDA";
  public static final String PIVOT_CLASSIFICAZIONE_COMPLETEZZA_NON_VALIDA = "PIVOT_CLASSIFICAZIONE_COMPLETEZZA_NON_VALIDA";

  public static final String PIVOT_RICHIESTA_CON_PARAMETRI_MULTIPLI = "PIVOT_RICHIESTA_CON_PARAMETRI_MULTIPLI";
  public static final String PIVOT_PARAMETRO_ANNO_BOLLETTA_NULLO = "PIVOT_PARAMETRO_ANNO_BOLLETTA_NULLO";
  public static final String PIVOT_PARAMETRO_NUMERO_BOLLETTA_NULLO = "PIVOT_PARAMETRO_NUMERO_BOLLETTA_NULLO";
  public static final String PIVOT_PARAMETRO_IUF_NULLO = "PIVOT_PARAMETRO_IUF_NULLO";
  public static final String PIVOT_BOLLETTA_NON_TROVATA = "PIVOT_BOLLETTA_NON_TROVATA";
  public static final String PIVOT_BOLLETTA_NON_PAGOPA = "PIVOT_BOLLETTA_NON_PAGOPA";
  public static final String PIVOT_NESSUNA_RENDICONTAZIONE_TROVATA = "PIVOT_NESSUNA_RENDICONTAZIONE_TROVATA";
  public static final String PIVOT_NESSUNA_RICEVUTA_TROVATA = "PIVOT_NESSUNA_RICEVUTA_TROVATA";
  public static final String PIVOT_DETTAGLIO_NON_PRESENTE = "PIVOT_DETTAGLIO_NON_PRESENTE";
  /** ======================================================= **/

  /*
   * COD ERRORE
   */
  public static final String COD_ERRORE_RT_NO_IUF = "RT_NO_IUF";// Pagamenti non correttamente rendicontati
  public static final String COD_ERRORE_IUF_NO_TES = "IUF_NO_TES";// Rendicontazioni non correttamente riversate
  public static final String COD_ERRORE_TES_NO_IUF_OR_IUV = "TES_NO_IUF_OR_IUV";// Riversamenti non rendicontati o di pagamenti non eseguiti
  public static final String COD_ERRORE_IUV_NO_RT = "IUV_NO_RT";// Rendicontazioni di pagamenti non eseguiti
  public static final String COD_ERRORE_IUD_NO_RT = "IUD_NO_RT";// Notifiche di pagamenti non eseguiti
  public static final String COD_ERRORE_RT_NO_IUD = "RT_NO_IUD";// Pagamenti non correttamente notificati
  public static final String COD_ERRORE_IUD_RT_IUF_TES = "IUD_RT_IUF_TES";// Pagamenti Notificati
  public static final String COD_ERRORE_RT_IUF = "RT_IUF";// Pagamenti Rendicontati
  public static final String COD_ERRORE_RT_IUF_TES = "RT_IUF_TES";// Pagamenti riversati cumulativamente
  public static final String COD_ERRORE_IUD_RT_IUF = "IUD_RT_IUF";// Pagamenti Notificati e Riconciliati
  public static final String COD_ERRORE_TES_NO_MATCH = "TES_NO_MATCH";// Riversamenti di tesoreria sconosciuti
  public static final String COD_ERRORE_IUF_TES_DIV_IMP = "IUF_TES_DIV_IMP";// Rendicontazioni con riversamento ma con importo differente
  public static final String COD_ERRORE_RT_TES = "RT_TES";// Pagamenti con riversamento puntuale
  public static final String COD_ERRORE_DOPPI = "DOPPI";// Pagamenti doppi

  public static final String WS_USER = "WS_USER";

  public final static String STATO_IMPORT_LOAD = "LOAD_IMPORT";
  public final static String STATO_IMPORT_ESEGUITO = "IMPORT_ESEGUITO";
  public final static String STATO_TIPO_IMPORT = "TIPO_IMPORT";

  public enum TIPO_VISUALIZZAZIONE {
    RICONCILIAZIONE("R"), ANOMALIE("A");
    String value;

    private TIPO_VISUALIZZAZIONE(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static TIPO_VISUALIZZAZIONE byValue(String value){
      return Arrays.stream(TIPO_VISUALIZZAZIONE.values())
          .filter(x -> x.value.equals(value))
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
  }
}
