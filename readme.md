
# MyPivot


## INTRODUZIONE


Il decreto di semplificazione “Switch off PagoPA e strumenti di pagamento elettronico – Artt. 24 e 24-bis, D.L. 76/2020 (art. 65, D.Lgs. 217/2017)”  cita l’obbligo per i prestatori di servizi di pagamento abilitati all’utilizzo della piattaforma PagoPA per i pagamenti verso le PA di dotarsi di una piattaforma evoluta in grado di soddisfare i requisiti d'integrazione con Nodo Centrale Nazionale dei Pagamenti  e dettagliati nelle SANP  periodicamente aggiornate e pubblicate da PagoPA.

Regione del Veneto ha aderito al Sistema dei pagamenti elettronici pagoPA, in qualità di intermediario tecnologico per tutti gli enti veneti. Per lo svolgimento di questa funzione di Intermediario Tecnologico da parte di un soggetto Ente Pubblico come potrebbe essere una Regione, è stato realizzato un progetto per implementare un’ apposita piattaforma territoriale dei pagamenti elettronici, MyPay, che garantisce omogeneità di utilizzo del sistema da parte dei cittadini e delle imprese sul territorio e consente a tutti gli enti che hanno aderito di operare con un unico sistema di intermediazione con il nodo nazionale “PagoPa”
Tale piattaforma consente la gestione dei pagamenti e regolarizzazione e riconciliazione delle posizioni debitorie tale da soddisfare i requisiti imposti dalle linee guida indicate da PagoPa, e rendere disponibili funzionalità  agli operatori degli enti per la verifica e gestione della regolarizzazione e riconciliazione delle informazioni relative alle posizioni debitorie pagate  rendicontate e riversate sui conti correnti degli enti

_Obiettivi del progetto_

Obiettivo del progetto è rendere disponibile una piattaforma dedicata al mondo dei pagamenti in grado di soddisfare le esigenze di gestione dei pagamenti degli enti che necessitano di


- gestire le posizioni debitorie pagate tramite il Nodo Nazionale dei Pagamenti PagoPA e conseguentemente da regolarizzare e riconciliare con i sistemi interno dell’ente stesso.

Col progetto si vogliono offrire servizi compatibili con le linee guida Agid in termini di adempienza agli SLA, all’accessibilità (UNI-en 301549-2018), all’enhancement della piattaforma in termini di usabilità e user experience per i cittadini, gli operatori e gli Enti.

Il progetto si basa sulle Specifiche Attuative del Nodo dei Pagamenti – SPC Versione 2.4.3 

Il sistema è fortemente orientata al mondo cloud e incentrata su concetti di scalabilità orizzontale e verticale al fine di potersi adattare in modo dinamico ed automatico ai crescenti carichi previsti vista la forte spinta all’utilizzo di strumenti di pagamenti telematici integrati con PagoPa.

Quindi se opportunamente deployato in ambiente clusterizzato e dockerizzato ha la possibilità di scalare in funzione del carico previsto e fortemente variabile in concomitanza di periodi temporali particolari per scadenze di pagamento previste dalla normativa nazionale.


La soluzione è composta dalle seguenti componenti:

 - L'**applicazione MyPivot App** orientata agli operatori degli Enti Locali EELL intermediati . L'applicazione prevede che in funzione del proprio profilo applicativo ogni operatore possa usufruire di  diverse funzionalità per la gestione delle posizioni debitorie pagate ed incassate e loro regolarizzazione e riconciliazione all'interno dell'Ente.

**Funzionalità Applicative**

Le macrocategorie di funzionalità applicative implementate nel progetto e incentrate sui pagamenti di posizioni debitorie varie e loro gestione sia in termini di pagamento effettuato tramite interazione PagoPA sia in termini di regolarizzazione e riconciliazione delle posizioni debitorie pagate e rendicontate da PAgoPA sono di seguito elencate e successivamente sinteticamente descritte:

**Funzionalità di backoffice per l'operatore dell' ente intermediato in ambito riconciliazione**

Le funzionalità di backoffice consentono agli operatori abilitati di:

 - importare le ricevute telematiche da apposita funzione di frontend e o tramite web service indipendentemente dall’intermediario tecnologico che ha gestito il pagamento

 - importare le rendicontazioni fornite dal Nodo PagoPA da apposita funzione di frontend o tramite web service indipendentemente dall’intermediario tecnologico che ha gestito il pagamento

 - importare il giornale di cassa nel formato OPI da apposita funzione di frontend o tramite web service

 - importare il giornale di cassa nel formato CSV da apposita funzione di frontend o tramite web service

 - importare l’estratto conto postale da apposita funzione di frontend o tramite web service

 - importare il giornale di cassa in formato XLS da apposita funzione di frontend o tramite webservice

 - filtrare e/o visualizzare l'elenco dei flussi importati, in corso di importazione o da importare nell’ente di amminsitrazione, con possibilità di scaricare i dati scartati o il file caricato

 - filtrare e/o visualizzare l’elenco dei movimenti che compongono il giornale di cassa

 - visualizzare la rendicontazione e le RT associate

 - visualizzare l’elenco delle RT per un ente filtrabile

 - importare in mypivot il flusso dei pagamenti notificati tramite web service

 - visualizzare i pagamenti riconciliati e notificati

 - visualizzare i pagamenti riversati cumulativamente

 - visualizzare i pagamenti riversati puntualmente

 - visualizzare i pagamenti riconciliati e rendicontati

 - visualizzare i pagamenti riconciliati e con notifica

 - inserire una segnalazione sul pagamento riconciliato e che sia successivamente visualizzabile e modificabile dagli operatori dell’ente

 - visualizzare l'elenco degli accertamenti contabili e il rispettivo dettaglio dell’Ente di Amministrazione

 - annullare il singolo accertamento, in modo da poter rettificare eventuali errori

 - chiudere un singolo accertamento contabile in modo da poterne inserire uno di nuovo per un cambio di annualità

 - inserire un nuovo accertamento in modo da poterne censire uno per ogni casistica come (Nuova annualità, Nuovo tipo dovuto)

 - Abbinare pagamenti all'accertamento

 - Modificare l'anagrafica di un accertamento contabile

 - visualizzare le informazioni dei pagamenti totali per anno/mese/giorno

 - visualizzare le informazioni dei pagamenti totali per tipo dovuto

 - visualizzare le informazioni dei pagamenti totali per capitolo

 - visualizzare le informazioni dei pagamenti totali per ufficio

 - visualizzare le informazioni dei pagamenti totali per accertamento

 - visualizzare i pagamenti notificati dal SIL per cui non esiste la RT

 - visualizzare i pagamenti con RT, Rendicontazione e giornale di cassa, ma senza notifica da parte del SIL

 - visualizzare i pagamenti con RT, ma senza rendicontazione

 - visualizzare i pagamenti con rendicontazione, ma senza RT

 - visualizzare i pagamenti con rendicontazione, ma senza sospeso di cassa

 - visualizzare i pagamenti con rendicontazione, ma con sospeso di cassa con importo non coerente

 - visualizzare i sospesi di cassa per cui non esiste rendicontazione o RT

 - visualizzare i sospesi di cassa le cui causali non sono nel formato previsto da PagoPA

 - visualizzare Anomalie-Pagamenti Doppi

 - visualizzare dettaglio iuv incassato e non rendicontato

 - Ricerca Giornale di Cassa per Anno Documento, Codice Documento, Anno Provvisorio e Codice Provvisorio


## STRUTTURA DEL REPOSITORY
Il repository git di MyPivot ha le seguente struttura:

**/mypivot.sources**: E’ la cartella che contiene i sorgenti e gli script gradle per la compilazione la creazione dell’immagine e la pubblicazione sul Repository Nexus.

**/mypivot.deploy:** E’ la cartella che contiene i descrittori di base per il dispiegamento su kubernets e gli overlay specifici per ogni ambiente target di deploy.


## I SORGENTI
La cartella **`mypivot.sources`** contiene i sorgenti dell'applicazione ed è così strutturata:

`gradle`: contiene file utilizzati per la build tramite Gradle 
`mypivot-batch`: contiene i sorgenti del processi batch Talend
`mypivot-db`: contiene i sorgenti degli script database
`mypivot4-be`: contiene i sorgenti del back-end Java Spring Boot myPivot Operatore
`mypivot4-fe`: contiene i sorgenti del front-end Angular myPivot Operatore 

## Esecuzione in modalità standalone
Per l'esecuzione in modalità standalone si rimanda al manuale di istallazione presente sotto documentazione e al file INSTALL.md nella cartella `mypivot.sources`.

### Prerequisiti Infrastrutturali
La soluzione MyPivot necessità di alcuni requisiti Infrastrutturali:

- **Redis** : Per la gestione della cache applicativa

In caso di installazione su K8s ( si veda il documento MI ) sono necessari anche:

- **Cluster K8s** :  Kubernetes (1.7.2 +) con support di container Docker (1.12.x +)
- **Repository Nexus** : Per la pubblicazione di artefatti e immagini docker una volta compilati i sorgenti


### Prerequisiti servizi MyPlace e verticali MyP3
La soluzione MyPivot ha dipendenza verso alcuni servizi della piattaforma MyPlace:

- **MyProfile**: Per la gestione delle autorizzazioni e dei profili

MyPivot espone servizi e funzionalità ai SIL degli Enti intermediati attraverso l'esposizione di WS

### Configurazione MyPivot
Per poter eseguire lo start dell’applicazione, è importante inserire i puntamenti dei propri ambienti per i componenti da cui dipende MyPivot.

### Creazione del file jar

Per la build si rimanda al manuale di istallazione presente sotto documentazione e al file INSTALL.md nella cartella `mypivot.sources`.

### Esecuzione dell'applicazione

Per l'esecuzione dell'applicazione in modalità standalone si rimanda al manuale di installazione presente sotto documentazione e al file INSTALL.md nella cartella `mypivot.sources`.
 
## Esecuzione su cluster kubernetes

Per l'esecuzione su cluster kubernetes si rimanda al manuale di istallazione presente sotto documentazione.