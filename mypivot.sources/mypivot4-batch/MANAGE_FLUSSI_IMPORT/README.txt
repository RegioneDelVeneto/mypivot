Per configurare correttamente l'ambiente:

- Scaricare "Talend Open Studio for Data Integration" dal link (http://www.talend.com/download_form.php?cont=gen&src=HomePage) 
attualmente l'ultima versione è la 5.5.1

CONFIGURARE I COMPONENTI POSTGRESQL:
aprire i file (si presume di iniziare dal path: "[ROOT_TALEND_IDE]\plugins\org.talend.designer.components.localprovider_[TALEND_VERSION]\components"):

- tPostgresqlConnection/tPostgresqlConnection_java.xml
- tPostgresqlInput/tPostgresqlInput_java.xml
- tPostgresqlOutput/tPostgresqlOutput_java.xml
- tPostgresqlRow/tPostgresqlRow_java.xml

sostituire in tutti la stringa "postgresql-8.3-603.jdbc3" con la stringa "postgresql-8.4-703.jdbc4"
sostituire in tutti la stringa "postgresql-8.3-603" con la stringa "postgresql-8.4-703"


eventualmente riavviare TOS se già aperto.

- Scegliere un workspace nel proprio file system (dedicare una cartella all'ide)
- Avviare l'ide talend
- Quando si è avviato l'ide, scegliere il workspace appena creato nella parte inferiore della form proposta
- Riavviare eclipse se necessario
- Scegliere "import"
- Scegliere "Import progetti diversi"
- Nella nuova maschera cliccare "sfoglia" e selezionare la root MANAGE_FLUSSI_IMPORT (scaricato da svn)
- Deselezionare la check box "copia i progetti nel workspace"
- Quando si ritorna nella maschera iniziale selezionare il progetto appena importato "pa-talend" e cliccare "Apri"
- Clicca Window --> Preferences --> Talend --> Component. Su "user component folder" cliccare "Browse" e scegliere la cartella: "Software\mygov-payment\pa\pa\TalendCustomComponent" Premere "OK"
- Clicca Window --> Preferences --> General --> Workspace. Spuntare il flag "Refresh Automatically"