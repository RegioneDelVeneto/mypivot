ALTER TABLE IF EXISTS public.mygov_import_export_rendicontazione_tesoreria_completa
    ADD COLUMN IF NOT EXISTS cod_tipo_dovuto_pa1 character varying(64) COLLATE pg_catalog."default";
ALTER TABLE IF EXISTS public.mygov_import_export_rendicontazione_tesoreria_completa
    ADD COLUMN IF NOT EXISTS de_tipo_dovuto_pa1 character varying(256) COLLATE pg_catalog."default";
ALTER TABLE IF EXISTS public.mygov_import_export_rendicontazione_tesoreria_completa
    ADD COLUMN IF NOT EXISTS cod_tassonomico_dovuto_pa1 character varying(35) COLLATE pg_catalog."default";
ALTER TABLE IF EXISTS public.mygov_import_export_rendicontazione_tesoreria_completa
    ADD COLUMN IF NOT EXISTS cod_fiscale_pa1 character varying(11) COLLATE pg_catalog."default";
ALTER TABLE IF EXISTS public.mygov_import_export_rendicontazione_tesoreria_completa
    ADD COLUMN IF NOT EXISTS de_nome_pa1 character varying(100) COLLATE pg_catalog."default";
	