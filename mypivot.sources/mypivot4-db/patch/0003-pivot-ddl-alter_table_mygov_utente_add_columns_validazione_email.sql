ALTER TABLE public.mygov_utente ADD email_source_type char NOT NULL DEFAULT 'A';
ALTER TABLE public.mygov_utente ALTER COLUMN de_email_address DROP NOT NULL;
ALTER TABLE public.mygov_utente ALTER COLUMN dt_ultimo_login DROP NOT NULL;