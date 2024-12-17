-- SET SCRIPT NAME
-- use pattern ####-pivot-[dml|ddl]-script_name.sql' (max 100 chars)
-- where #### is a sequence of integer numbers (without holes and duplicates) determining script execution order
set myvars.script_name to '0009-pivot-dml-init_table_mygov_script.sql';
-- SET SCRIPT DESCRIPTION
-- free text, max 500 chars
set myvars.script_description to 'aggiornamento dati tabella mygov_script per precedenti script già eseguiti';

-- start transaction
BEGIN;

-- check that script has not already been executed
--  and store script execution info into table mygov_script
insert into mygov_script values ( current_setting('myvars.script_name'),current_setting('myvars.script_description') );

------------------- business logic -------------------

insert into mygov_script values (
'0001-pivot-ddl-alter_table_mygov_manage_flusso_add_columns.sql'
,'aggiunta colonne su tabella mygov_manage_flusso'
);
insert into mygov_script values (
'0002-pivot-ddl-alter_table_mygov_prenotazione_flusso_riconciliazione_add_columns.sql'
,'aggiunta colonne su tabella mygov_prenotazione_flusso_riconciliazione'
);
insert into mygov_script values (
'0003-pivot-ddl-alter_table_mygov_utente_add_columns_validazione_email.sql'
,'aggiunta colonne su tabella mygov_utente'
);
insert into mygov_script values (
'0004-pivot-dml-update_table_mygov_tipo_flusso.sql'
,'inizializzazione dati su tabella mygov_tipo_flusso'
);
insert into mygov_script values (
'0005-pivot-ddl-alter_table_mygov_flusso_export_add_columns_ente_primario.sql'
,'aggiunta colonne su tabella mygov_flusso_export'
);
insert into mygov_script values (
'0006-ddl-alter-table-mygov_import_export_rendicontazione_tesoreria_completa.sql'
,'aggiunta colonne mygov_import_export_rendicontazione_tesoreria_completa'
);
insert into mygov_script values (
'0007-v_mygov_import_export_rendicontazione_tesoreria_completa_sanp25.sql'
,'aggiunta colonne vista v_mygov_import_export_rendicontazione_tesoreria_completa'
);
insert into mygov_script values (
'0008-pivot-ddl-create_table_mygov_script.sql'
,'creazione tabella mygov_script'
);

------------------------------------------------------

-- final commit
COMMIT;
