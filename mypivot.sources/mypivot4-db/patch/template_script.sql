-- SET SCRIPT NAME
-- use pattern ####-pivot-[dml|ddl]-script_name.sql' (max 100 chars)
-- where #### is a sequence of integer numbers (without holes and duplicates) determining script execution order
set myvars.script_name to '0000-pivot-ddl-test_script.sql';
-- SET SCRIPT DESCRIPTION
-- free text, max 500 chars
set myvars.script_description to 'here goes script description';

-- start transaction
BEGIN;

-- check that script has not already been executed
--  and store script execution info into table mygov_script
insert into mygov_script values ( current_setting('myvars.script_name'),current_setting('myvars.script_description') );

------------------- business logic -------------------

-- PUT HERE SCRIPT BUSINESS LOGIC...

------------------------------------------------------

-- final commit
COMMIT;