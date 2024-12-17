INSERT INTO public.mygov_tipo_flusso(
	mygov_tipo_flusso_id, "version", cod_tipo, de_tipo, dt_creazione, dt_ultima_modifica)
	VALUES(nextval('mygov_tipo_flusso_mygov_tipo_flusso_id_seq'), 0, 'S', 'Flusso dovuti pagati enti secondari', now(), now());
	