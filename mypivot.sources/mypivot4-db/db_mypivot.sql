--
-- PostgreSQL database dump
--

-- Dumped from database version 10.14
-- Dumped by pg_dump version 14.4

-- Started on 2022-09-23 10:01:08

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2694 (class 1262 OID 32863)
-- Name: mypivot; Type: DATABASE; Schema: -; Owner: mypay4
--

CREATE DATABASE mypivot WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'en_US.UTF-8';


ALTER DATABASE mypivot OWNER TO mypay4;

\connect mypivot

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2 (class 3079 OID 32864)
-- Name: pgstattuple; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgstattuple WITH SCHEMA public;


--
-- TOC entry 2695 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION pgstattuple; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgstattuple IS 'show tuple-level statistics';


--
-- TOC entry 286 (class 1255 OID 32874)
-- Name: get_count_flusso_rendicontazione_function(bigint, date, date, character varying, character varying); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_count_flusso_rendicontazione_function(_mygov_ente_id bigint, _dt_data_regolamento_da date, _dt_data_regolamento_a date, _cod_iuf character varying, _identificativo_univoco_regolamento character varying) RETURNS bigint
    LANGUAGE sql STABLE
    AS $$
  SELECT count(DISTINCT(upper(rend.cod_identificativo_flusso)))
  FROM mygov_flusso_rendicontazione rend
     
  WHERE CASE WHEN _mygov_ente_id IS NOT NULL THEN rend.mygov_ente_id = _mygov_ente_id ELSE true END
  AND   CASE WHEN _dt_data_regolamento_da IS NOT NULL THEN rend.dt_data_regolamento >= _dt_data_regolamento_da ELSE true END
  AND   CASE WHEN _dt_data_regolamento_a IS NOT NULL THEN rend.dt_data_regolamento <= _dt_data_regolamento_a ELSE true END
  AND   CASE WHEN (_cod_iuf <> '') IS TRUE THEN upper(rend.cod_identificativo_flusso) like upper('%' || _cod_iuf || '%') ELSE true END
  AND   CASE WHEN (_identificativo_univoco_regolamento <> '') IS TRUE THEN rend.cod_identificativo_univoco_regolamento = _identificativo_univoco_regolamento ELSE true END;
$$;


ALTER FUNCTION public.get_count_flusso_rendicontazione_function(_mygov_ente_id bigint, _dt_data_regolamento_da date, _dt_data_regolamento_a date, _cod_iuf character varying, _identificativo_univoco_regolamento character varying) OWNER TO mypay4;

--
-- TOC entry 287 (class 1255 OID 32875)
-- Name: get_count_import_export_rend_tes_function(character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, date, date, date, date, character varying, character varying, date, date, date, date, date, date, date, date, character varying, boolean, character varying, character varying, character varying, boolean, character varying); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_count_import_export_rend_tes_function(_cod_fed_user_id character varying, _codice_ipa_ente character varying, _cod_iud character varying, _cod_iuv character varying, _denominazione_attestante character varying, _identificativo_univoco_riscossione character varying, _codice_identificativo_univoco_versante character varying, _anagrafica_versante character varying, _codice_identificativo_univoco_pagatore character varying, _anagrafica_pagatore character varying, _causale_versamento character varying, _data_esecuzione_singolo_pagamento_da date, _data_esecuzione_singolo_pagamento_a date, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _data_regolamento_da date, _data_regolamento_a date, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _cod_tipo_dovuto character varying, _is_cod_tipo_dovuto_present boolean, _importo character varying, _conto character varying, _codor1 character varying, _flagnascosto boolean, _classificazione_completezza character varying) RETURNS bigint
    LANGUAGE sql STABLE
    AS $$
   SELECT 
      count(1)
   FROM 
      mygov_import_export_rendicontazione_tesoreria as tes 
	LEFT OUTER JOIN (SELECT ment.cod_ipa_ente
                        , mseg.cod_iuf
                        , mseg.cod_iuv
                        , mseg.cod_iud
                        , mseg.flg_nascosto
                     FROM mygov_segnalazione as mseg 
                             INNER JOIN   mygov_ente as ment 
                             ON           mseg.mygov_ente_id = ment.mygov_ente_id 
                             WHERE        mseg.flg_attivo = true 
                             AND          mseg.classificazione_completezza = _classificazione_completezza) as ms                              
	   ON   ms.cod_ipa_ente = tes.codice_ipa_ente 
     AND (ms.cod_iuf IS NULL 
     AND  tes.cod_iuf_key IS NULL 
     OR   ms.cod_iuf = tes.cod_iuf_key)
     AND (ms.cod_iuv IS NULL 
     AND  tes.cod_iuv_key IS NULL 
     OR   ms.cod_iuv = tes.cod_iuv_key)
     AND (ms.cod_iud IS NULL 
     AND  tes.cod_iud_key IS NULL 
     OR   ms.cod_iud = tes.cod_iud_key)
     
   WHERE CASE WHEN (_cod_tipo_dovuto <> '') IS TRUE AND _is_cod_tipo_dovuto_present THEN tes.tipo_dovuto = _cod_tipo_dovuto 
      	      WHEN (_cod_tipo_dovuto <> '') IS NOT TRUE AND _is_cod_tipo_dovuto_present THEN 
            	    tes.tipo_dovuto in (SELECT DISTINCT(metd.cod_tipo)
                              				FROM   mygov_operatore_ente_tipo_dovuto as moetd, mygov_ente_tipo_dovuto as metd 
                              				WHERE  moetd.mygov_ente_tipo_dovuto_id = metd.mygov_ente_tipo_dovuto_id
                              				AND    moetd.cod_fed_user_id = _cod_fed_user_id 
                              				AND   moetd.flg_attivo = true)
    		      ELSE true		   
    	   END	
   AND   CASE WHEN (_codice_ipa_ente <> '') IS TRUE THEN tes.codice_ipa_ente = _codice_ipa_ente ELSE true END 
   AND   CASE WHEN (_cod_iud <> '') IS TRUE THEN tes.codice_iud = _cod_iud ELSE true END  
   AND   CASE WHEN (_cod_iuv <> '') IS TRUE THEN tes.codice_iuv = _cod_iuv ELSE true END        
   AND   CASE WHEN (_denominazione_attestante <> '') IS TRUE THEN 
                    (upper(tes.denominazione_attestante) like '%' || upper(_denominazione_attestante) || '%' 
                 OR upper(tes.codice_identificativo_univoco_attestante) like '%' || upper(_denominazione_attestante) || '%') 
         ELSE true END     
   AND   CASE WHEN (_identificativo_univoco_riscossione <> '') IS TRUE THEN tes.identificativo_univoco_riscossione = _identificativo_univoco_riscossione ELSE true END        
   AND   CASE WHEN (_codice_identificativo_univoco_versante <> '') IS TRUE THEN (tes.codice_identificativo_univoco_versante = upper(_codice_identificativo_univoco_versante) OR tes.codice_identificativo_univoco_versante = lower(_codice_identificativo_univoco_versante)) ELSE true END        
   AND   CASE WHEN (_anagrafica_versante <> '') IS TRUE THEN upper(tes.anagrafica_versante) like '%' || upper(_anagrafica_versante) || '%' ELSE true END              
   AND   CASE WHEN (_codice_identificativo_univoco_pagatore <> '') IS TRUE THEN (tes.codice_identificativo_univoco_pagatore = upper(_codice_identificativo_univoco_pagatore) OR tes.codice_identificativo_univoco_pagatore = lower(_codice_identificativo_univoco_pagatore)) ELSE true END           
   AND   CASE WHEN (_anagrafica_pagatore <> '') IS TRUE THEN upper(tes.anagrafica_pagatore) like '%' || upper(_anagrafica_pagatore) || '%' ELSE true END                  
   AND   CASE WHEN (_causale_versamento <> '') IS TRUE THEN upper(tes.causale_versamento) like '%' || upper(_causale_versamento) || '%' ELSE true END                    
   AND   CASE WHEN _data_esecuzione_singolo_pagamento_da IS NOT NULL THEN tes.dt_data_esecuzione_pagamento >= _data_esecuzione_singolo_pagamento_da ELSE true END                       
   AND   CASE WHEN _data_esecuzione_singolo_pagamento_a IS NOT NULL THEN tes.dt_data_esecuzione_pagamento <= _data_esecuzione_singolo_pagamento_a ELSE true END                     
   AND   CASE WHEN _data_esito_singolo_pagamento_da IS NOT NULL THEN tes.dt_data_esito_singolo_pagamento >= _data_esito_singolo_pagamento_da ELSE true END                       
   AND   CASE WHEN _data_esito_singolo_pagamento_a IS NOT NULL THEN tes.dt_data_esito_singolo_pagamento <= _data_esito_singolo_pagamento_a ELSE true END             
   AND   CASE WHEN (_identificativo_flusso_rendicontazione <> '') IS TRUE THEN upper(tes.identificativo_flusso_rendicontazione) like upper('%' || _identificativo_flusso_rendicontazione || '%') ELSE true END           
   AND   CASE WHEN (_identificativo_univoco_regolamento <> '') IS TRUE THEN tes.identificativo_univoco_regolamento = _identificativo_univoco_regolamento ELSE true END           
   AND   CASE WHEN _data_regolamento_da IS NOT NULL THEN tes.dt_data_regolamento >= _data_regolamento_da ELSE true END                       
   AND   CASE WHEN _data_regolamento_a IS NOT NULL THEN tes.dt_data_regolamento <= _data_regolamento_a ELSE true END             
   AND   CASE WHEN _dt_data_contabile_da IS NOT NULL THEN tes.dt_data_contabile >= _dt_data_contabile_da ELSE true END                       
   AND   CASE WHEN _dt_data_contabile_a IS NOT NULL THEN tes.dt_data_contabile <= _dt_data_contabile_a ELSE true END             
   AND   CASE WHEN _dt_data_valuta_da IS NOT NULL THEN tes.dt_data_valuta >= _dt_data_valuta_da ELSE true END                      
   AND   CASE WHEN _dt_data_valuta_a IS NOT NULL THEN tes.dt_data_valuta <= _dt_data_valuta_a ELSE true END              
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_da IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento >= _dt_data_ultimo_aggiornamento_da ELSE true END                       
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_a IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento <= _dt_data_ultimo_aggiornamento_a ELSE true END             
   AND   CASE WHEN (_importo <> '') IS TRUE THEN tes.de_importo = _importo ELSE true END                   
   AND   CASE WHEN (_conto <> '') IS TRUE THEN tes.cod_conto = _conto ELSE true END                 
   AND   CASE WHEN (_codOr1 <> '') IS TRUE THEN upper(tes.cod_or1)  like '%' || upper(_codOr1) || '%' ELSE true END                                                            
   AND   CASE WHEN _flagnascosto IS NOT NULL THEN ms.flg_nascosto = _flagnascosto ELSE (ms.flg_nascosto is null or ms.flg_nascosto = false) END                    
   AND   CASE WHEN (_classificazione_completezza <> '') IS TRUE THEN tes.classificazione_completezza = _classificazione_completezza ELSE true END;
$$;


ALTER FUNCTION public.get_count_import_export_rend_tes_function(_cod_fed_user_id character varying, _codice_ipa_ente character varying, _cod_iud character varying, _cod_iuv character varying, _denominazione_attestante character varying, _identificativo_univoco_riscossione character varying, _codice_identificativo_univoco_versante character varying, _anagrafica_versante character varying, _codice_identificativo_univoco_pagatore character varying, _anagrafica_pagatore character varying, _causale_versamento character varying, _data_esecuzione_singolo_pagamento_da date, _data_esecuzione_singolo_pagamento_a date, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _data_regolamento_da date, _data_regolamento_a date, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _cod_tipo_dovuto character varying, _is_cod_tipo_dovuto_present boolean, _importo character varying, _conto character varying, _codor1 character varying, _flagnascosto boolean, _classificazione_completezza character varying) OWNER TO mypay4;

--
-- TOC entry 288 (class 1255 OID 32877)
-- Name: get_count_pagamenti_inseribili_in_accertamento(bigint, character varying, character varying, character varying, character varying, date, date, date, date); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_count_pagamenti_inseribili_in_accertamento(_ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date) RETURNS bigint
    LANGUAGE sql
    AS $_$
 
  SELECT 
        count(*) 
  FROM 
      mygov_flusso_export AS p
  WHERE 
      /* Escludo le righe gia in accertamento */
      (p.cod_iud  || '-' || p.cod_rp_silinviarp_id_univoco_versamento) 
  NOT IN 
      (
        SELECT 
           ad.cod_iud || '-' || ad.cod_iuv 
        FROM 
           mygov_accertamento_dettaglio ad 
              INNER JOIN mygov_accertamento a ON ad.mygov_accertamento_id = a.mygov_accertamento_id
              INNER JOIN mygov_anagrafica_stato st ON a.mygov_anagrafica_stato_id = st.mygov_anagrafica_stato_id
        WHERE 
             st.de_tipo_stato = 'ACCERTAMENTO' AND st.cod_stato <> 'ANNULLATO' 
      ) AND
  
      /* Condizioni obbligatorie */
      p.mygov_ente_id = $1 AND p.cod_tipo_dovuto = $2 AND p.bilancio IS NULL AND
            
      /* IUD */
      CASE WHEN ($3 IS NOT NULL) THEN p.cod_iud = $3 ELSE true END AND
      /* IUV */
      CASE WHEN ($4 IS NOT NULL) THEN p.cod_rp_silinviarp_id_univoco_versamento = $4 ELSE true END AND
      /* Identificativo univoco pagatore */
      CASE WHEN ($5 IS NOT NULL) THEN p.cod_e_sogg_pag_id_univ_pag_codice_id_univoco = $5 ELSE true END AND
      /* Data esito pagamento da */
      CASE WHEN ($6 IS NOT NULL) THEN p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento >= $6 ELSE true END AND
      /* Data esito pagamento a */
      CASE WHEN ($7 IS NOT NULL) THEN p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento <= $7 ELSE true END AND
      /* Data ultimo aggiornamento da */
      CASE WHEN ($8 IS NOT NULL) THEN p.dt_ultima_modifica >= $8 ELSE true END AND
      /* Data ultimo aggiornamento a */
      CASE WHEN ($9 IS NOT NULL) THEN p.dt_ultima_modifica <= $9 ELSE true END;
   
$_$;


ALTER FUNCTION public.get_count_pagamenti_inseribili_in_accertamento(_ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date) OWNER TO mypay4;

--
-- TOC entry 2696 (class 0 OID 0)
-- Dependencies: 288
-- Name: FUNCTION get_count_pagamenti_inseribili_in_accertamento(_ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date); Type: COMMENT; Schema: public; Owner: mypay4
--

COMMENT ON FUNCTION public.get_count_pagamenti_inseribili_in_accertamento(_ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date) IS 'Count dei Pagamenti inseribili in accertamento';


--
-- TOC entry 290 (class 1255 OID 32878)
-- Name: get_count_pagamenti_inseriti_in_accertamento(bigint, bigint, character varying, character varying, character varying, character varying, date, date, date, date); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_count_pagamenti_inseriti_in_accertamento(_accertamento_id bigint, _ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date) RETURNS bigint
    LANGUAGE sql
    AS $_$
  SELECT 
       count(DISTINCT(a.cod_iud || '-' || a.cod_iuv))
  FROM 
      mygov_flusso_export AS p INNER JOIN mygov_accertamento_dettaglio AS a ON p.cod_iud = a.cod_iud AND p.cod_rp_silinviarp_id_univoco_versamento = a.cod_iuv
  WHERE
      /* Condizioni obbligatorie */
      a.mygov_accertamento_id = $1 AND p.mygov_ente_id = $2 AND p.cod_tipo_dovuto = $3 AND
            
      /* IUD */
      CASE WHEN ($4 IS NOT NULL) THEN p.cod_iud = $4 ELSE true END AND
      /* IUV */
      CASE WHEN ($5 IS NOT NULL) THEN p.cod_rp_silinviarp_id_univoco_versamento = $5 ELSE true END AND
      /* Identificativo univoco pagatore */
      CASE WHEN ($6 IS NOT NULL) THEN p.cod_e_sogg_pag_id_univ_pag_codice_id_univoco = $6 ELSE true END AND
      /* Data esito pagamento da */
      CASE WHEN ($7 IS NOT NULL) THEN p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento >= $7 ELSE true END AND
      /* Data esito pagamento a */
      CASE WHEN ($8 IS NOT NULL) THEN p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento <= $8 ELSE true END AND
      /* Data ultimo aggiornamento da */
      CASE WHEN ($9 IS NOT NULL) THEN p.dt_ultima_modifica >= $9 ELSE true END AND
      /* Data ultimo aggiornamento a */
      CASE WHEN ($10 IS NOT NULL) THEN p.dt_ultima_modifica <= $10 ELSE true END;
   
$_$;


ALTER FUNCTION public.get_count_pagamenti_inseriti_in_accertamento(_accertamento_id bigint, _ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date) OWNER TO mypay4;

--
-- TOC entry 2697 (class 0 OID 0)
-- Dependencies: 290
-- Name: FUNCTION get_count_pagamenti_inseriti_in_accertamento(_accertamento_id bigint, _ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date); Type: COMMENT; Schema: public; Owner: mypay4
--

COMMENT ON FUNCTION public.get_count_pagamenti_inseriti_in_accertamento(_accertamento_id bigint, _ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date) IS 'Count dei pagamenti inseriti in accertamento';


--
-- TOC entry 291 (class 1255 OID 32879)
-- Name: get_count_rendicontazione_subset_function(character varying, character varying, character varying, date, date, date, date, character varying, character varying, character varying, boolean); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_count_rendicontazione_subset_function(_codice_ipa_ente character varying, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _dt_data_regolamento_da date, _dt_data_regolamento_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _classificazione_completezza character varying, _cod_tipo_dovuto character varying, _cod_fed_user_id character varying, _flagnascosto boolean) RETURNS bigint
    LANGUAGE sql
    AS $_$
   SELECT 
         COUNT( DISTINCT(upper(tes.identificativo_flusso_rendicontazione)))
   FROM 
      mygov_import_export_rendicontazione_tesoreria as tes 
  LEFT OUTER JOIN (SELECT mseg.*, ment.* FROM mygov_segnalazione as mseg INNER JOIN mygov_ente as ment ON mseg.mygov_ente_id = ment.mygov_ente_id WHERE mseg.flg_attivo = true AND mseg.classificazione_completezza = $8) as ms 
     ON ms.cod_ipa_ente = tes.codice_ipa_ente AND (ms.cod_iuf = tes.cod_iuf_key OR (ms.cod_iuf IS NULL and tes.cod_iuf_key IS NULL))
   WHERE  
         CASE WHEN $1 IS NOT NULL AND $1!='' THEN tes.codice_ipa_ente = $1 ELSE true END
     AND CASE WHEN $2 IS NOT NULL AND $2!='' THEN upper(tes.identificativo_flusso_rendicontazione) like upper('%' || $2 || '%') ELSE true END
     AND (COALESCE($3, '') ='' OR tes.identificativo_univoco_regolamento = $3)
     AND CASE WHEN $4 IS NOT NULL THEN tes.dt_data_regolamento >= $4 ELSE true END
     AND CASE WHEN $5 IS NOT NULL THEN tes.dt_data_regolamento <= $5 ELSE true END
     AND CASE WHEN $6 IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento >= $6 ELSE true END
     AND CASE WHEN $7 IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento < $7 ELSE true END
     AND CASE WHEN $8 IS NOT NULL AND $8!='' THEN tes.classificazione_completezza = $8 ELSE true END
     AND CASE WHEN $9 IS NOT NULL AND $9!='' AND tes.classificazione_completezza <> 'IUV_NO_RT' THEN tes.tipo_dovuto = $9 
          WHEN ($9 IS NULL OR $9='') AND tes.classificazione_completezza <> 'IUV_NO_RT' THEN  
      tes.tipo_dovuto in (SELECT
           DISTINCT(metd.cod_tipo)
        FROM 
           mygov_operatore_ente_tipo_dovuto as moetd, mygov_ente_tipo_dovuto as metd 
        WHERE
           moetd.mygov_ente_tipo_dovuto_id = metd.mygov_ente_tipo_dovuto_id AND
           moetd.cod_fed_user_id = $10 AND 
           moetd.flg_attivo = true)
      ELSE true
   END
     AND CASE WHEN $11 IS NOT NULL 
         THEN
       ms.flg_nascosto = $11
         ELSE
             (ms.flg_nascosto is null or ms.flg_nascosto = false)
         END

$_$;


ALTER FUNCTION public.get_count_rendicontazione_subset_function(_codice_ipa_ente character varying, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _dt_data_regolamento_da date, _dt_data_regolamento_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _classificazione_completezza character varying, _cod_tipo_dovuto character varying, _cod_fed_user_id character varying, _flagnascosto boolean) OWNER TO mypay4;

--
-- TOC entry 292 (class 1255 OID 32880)
-- Name: get_count_rendicontazione_tesoreria_subset_function(character varying, character varying, character varying, date, date, date, date, date, date, date, date, text, character varying, character varying, character varying, character varying, boolean, character varying, boolean, character varying); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_count_rendicontazione_tesoreria_subset_function(_codice_ipa_ente character varying, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _dt_data_regolamento_da date, _dt_data_regolamento_a date, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _de_causale_t text, _importo character varying, _conto character varying, _codor1 character varying, _cod_tipo_dovuto character varying, _is_cod_tipo_dovuto_present boolean, _cod_fed_user_id character varying, _flagnascosto boolean, _classificazione_completezza character varying) RETURNS bigint
    LANGUAGE sql STABLE
    AS $$
   SELECT 
        count(DISTINCT(iert.codice_ipa_ente, upper(iert.identificativo_flusso_rendicontazione)))
   FROM 
      mygov_import_export_rendicontazione_tesoreria as iert 
  LEFT OUTER JOIN (SELECT ment.cod_ipa_ente
                        , mseg.cod_iuf
                        , mseg.cod_iuv
                        , mseg.flg_nascosto
                     FROM mygov_segnalazione as mseg 
                             INNER JOIN   mygov_ente as ment 
                             ON           mseg.mygov_ente_id = ment.mygov_ente_id 
                             WHERE        mseg.flg_attivo = true 
                             AND          mseg.classificazione_completezza = _classificazione_completezza) as ms
     ON   ms.cod_ipa_ente = iert.codice_ipa_ente 
    AND (ms.cod_iuf IS NULL 
    AND  iert.cod_iuf_key IS NULL 
    OR   ms.cod_iuf = iert.cod_iuf_key)
    AND (ms.cod_iuv IS NULL 
    AND  iert.cod_iuv_key IS NULL 
    OR   ms.cod_iuv = iert.cod_iuv_key)
     
   WHERE CASE WHEN _codice_ipa_ente IS NOT NULL THEN iert.codice_ipa_ente = _codice_ipa_ente ELSE true END
   AND   CASE WHEN _identificativo_flusso_rendicontazione IS NOT NULL THEN upper(iert.identificativo_flusso_rendicontazione) like upper('%' || _identificativo_flusso_rendicontazione || '%') ELSE true END
   AND   CASE WHEN _identificativo_univoco_regolamento IS NOT NULL THEN iert.identificativo_univoco_regolamento = _identificativo_univoco_regolamento ELSE true END
   AND   CASE WHEN _dt_data_regolamento_da IS NOT NULL THEN iert.dt_data_regolamento >= _dt_data_regolamento_da ELSE true END
   AND   CASE WHEN _dt_data_regolamento_a IS NOT NULL THEN iert.dt_data_regolamento <= _dt_data_regolamento_a ELSE true END
   AND   CASE WHEN _dt_data_contabile_da IS NOT NULL THEN iert.dt_data_contabile >= _dt_data_contabile_da ELSE true END
   AND   CASE WHEN _dt_data_contabile_a IS NOT NULL THEN iert.dt_data_contabile <= _dt_data_contabile_a ELSE true END
   AND   CASE WHEN _dt_data_valuta_da IS NOT NULL THEN iert.dt_data_valuta >= _dt_data_valuta_da ELSE true END
   AND   CASE WHEN _dt_data_valuta_a IS NOT NULL THEN iert.dt_data_valuta <= _dt_data_valuta_a ELSE true END
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_da IS NOT NULL THEN iert.dt_data_ultimo_aggiornamento >= _dt_data_ultimo_aggiornamento_da ELSE true END
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_a IS NOT NULL THEN iert.dt_data_ultimo_aggiornamento <= _dt_data_ultimo_aggiornamento_a ELSE true END
   AND   CASE WHEN _de_causale_t IS NOT NULL THEN upper(iert.de_causale_t) like '%' || upper(_de_causale_t) || '%' ELSE true END
   AND   CASE WHEN _importo IS NOT NULL THEN iert.de_importo = _importo ELSE true END
   AND   CASE WHEN _conto IS NOT NULL THEN iert.cod_conto = _conto ELSE true END
   AND   CASE WHEN _codOr1 IS NOT NULL THEN upper(iert.cod_or1) like '%' || upper(_codOr1) || '%' ELSE true END
   AND   CASE WHEN _flagnascosto IS NOT NULL THEN ms.flg_nascosto = _flagnascosto ELSE (ms.flg_nascosto is null or ms.flg_nascosto = false) END
   AND	 CASE WHEN _cod_tipo_dovuto IS NOT NULL AND _is_cod_tipo_dovuto_present THEN iert.tipo_dovuto = _cod_tipo_dovuto
              WHEN _cod_tipo_dovuto IS NULL AND _is_cod_tipo_dovuto_present THEN
                  iert.tipo_dovuto in (SELECT DISTINCT(metd.cod_tipo)
                                      FROM   mygov_operatore_ente_tipo_dovuto as moetd, mygov_ente_tipo_dovuto as metd
                                      WHERE  moetd.mygov_ente_tipo_dovuto_id = metd.mygov_ente_tipo_dovuto_id
                                      AND    moetd.cod_fed_user_id = _cod_fed_user_id 
                                      AND   moetd.flg_attivo = true)
              ELSE true
         END
   AND   CASE WHEN _classificazione_completezza IS NOT NULL THEN iert.classificazione_completezza = _classificazione_completezza ELSE true END;
$$;


ALTER FUNCTION public.get_count_rendicontazione_tesoreria_subset_function(_codice_ipa_ente character varying, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _dt_data_regolamento_da date, _dt_data_regolamento_a date, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _de_causale_t text, _importo character varying, _conto character varying, _codor1 character varying, _cod_tipo_dovuto character varying, _is_cod_tipo_dovuto_present boolean, _cod_fed_user_id character varying, _flagnascosto boolean, _classificazione_completezza character varying) OWNER TO mypay4;

--
-- TOC entry 293 (class 1255 OID 32881)
-- Name: get_count_tesoreria_no_match_subset_function(character varying, date, date, date, date, date, date, text, character varying, character varying, character varying, boolean, character varying); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_count_tesoreria_no_match_subset_function(_codice_ipa_ente character varying, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _de_causale_t text, _importo character varying, _conto character varying, _codor1 character varying, _flagnascosto boolean, _classificazione_completezza character varying) RETURNS bigint
    LANGUAGE sql STABLE
    AS $$
   SELECT COUNT( DISTINCT(tes.codice_ipa_ente, tes.de_anno_bolletta, tes.cod_bolletta))
   FROM 
      mygov_import_export_rendicontazione_tesoreria as tes 
  LEFT OUTER JOIN (SELECT ment.cod_ipa_ente
                        , mseg.cod_iuf
                        , mseg.cod_iuv
                        , mseg.flg_nascosto
                     FROM mygov_segnalazione as mseg 
                             INNER JOIN   mygov_ente as ment 
                             ON           mseg.mygov_ente_id = ment.mygov_ente_id 
                             WHERE        mseg.flg_attivo = true 
                             AND          mseg.classificazione_completezza = _classificazione_completezza) as ms                              
     ON   ms.cod_ipa_ente = tes.codice_ipa_ente 
    AND (ms.cod_iuf IS NULL 
    AND  tes.cod_iuf_key IS NULL 
    OR   ms.cod_iuf = tes.cod_iuf_key)
    AND (ms.cod_iuv IS NULL 
    AND  tes.cod_iuv_key IS NULL 
    OR   ms.cod_iuv = tes.cod_iuv_key)
     
   WHERE CASE WHEN (_codice_ipa_ente <> '') IS TRUE THEN tes.codice_ipa_ente = _codice_ipa_ente ELSE true END     
   AND   CASE WHEN _dt_data_contabile_da IS NOT NULL THEN tes.dt_data_contabile >= _dt_data_contabile_da ELSE true END                       
   AND   CASE WHEN _dt_data_contabile_a IS NOT NULL THEN tes.dt_data_contabile <= _dt_data_contabile_a ELSE true END         
   AND   CASE WHEN _dt_data_valuta_da IS NOT NULL THEN tes.dt_data_valuta >= _dt_data_valuta_da ELSE true END                      
   AND   CASE WHEN _dt_data_valuta_a IS NOT NULL THEN tes.dt_data_valuta <= _dt_data_valuta_a ELSE true END              
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_da IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento >= _dt_data_ultimo_aggiornamento_da ELSE true END                       
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_a IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento <= _dt_data_ultimo_aggiornamento_a ELSE true END
   AND   CASE WHEN (_de_causale_t <> '') IS TRUE THEN upper(tes.de_causale_t) like '%' || upper(_de_causale_t) || '%' ELSE true END
   AND   CASE WHEN (_importo <> '') IS TRUE THEN tes.de_importo = _importo ELSE true END                   
   AND   CASE WHEN (_conto <> '') IS TRUE THEN tes.cod_conto = _conto ELSE true END                 
   AND   CASE WHEN (_codOr1 <> '') IS TRUE THEN upper(tes.cod_or1) like '%' || upper(_codOr1) || '%' ELSE true END                                                            
   AND   CASE WHEN _flagnascosto IS NOT NULL THEN ms.flg_nascosto = _flagnascosto ELSE (ms.flg_nascosto is null or ms.flg_nascosto = false) END                    
   AND   CASE WHEN (_classificazione_completezza <> '') IS TRUE THEN tes.classificazione_completezza = _classificazione_completezza ELSE true END;
$$;


ALTER FUNCTION public.get_count_tesoreria_no_match_subset_function(_codice_ipa_ente character varying, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _de_causale_t text, _importo character varying, _conto character varying, _codor1 character varying, _flagnascosto boolean, _classificazione_completezza character varying) OWNER TO mypay4;

--
-- TOC entry 294 (class 1255 OID 32882)
-- Name: get_count_tesoreria_subset_function(character varying, date, date, date, date, date, date, text, character varying, character varying, character varying, boolean, character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_count_tesoreria_subset_function(_codice_ipa_ente character varying, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _de_causale_t text, _importo character varying, _conto character varying, _codor1 character varying, _flagnascosto boolean, _classificazione_completezza character varying, _cod_iuv character varying, _cod_iuf character varying) RETURNS bigint
    LANGUAGE sql STABLE
    AS $$
   SELECT COUNT(1)
   FROM 
      mygov_import_export_rendicontazione_tesoreria as tes 
  LEFT OUTER JOIN (SELECT ment.cod_ipa_ente
                        , mseg.cod_iuf
                        , mseg.cod_iuv
                        , mseg.flg_nascosto
                     FROM mygov_segnalazione as mseg 
                             INNER JOIN   mygov_ente as ment 
                             ON           mseg.mygov_ente_id = ment.mygov_ente_id 
                             WHERE        mseg.flg_attivo = true 
                             AND          mseg.classificazione_completezza = _classificazione_completezza) as ms                              
     ON   ms.cod_ipa_ente = tes.codice_ipa_ente 
    AND (ms.cod_iuf IS NULL 
    AND  tes.cod_iuf_key IS NULL 
    OR   ms.cod_iuf = tes.cod_iuf_key)
    AND (ms.cod_iuv IS NULL 
    AND  tes.cod_iuv_key IS NULL 
    OR   ms.cod_iuv = tes.cod_iuv_key)
     
   WHERE CASE WHEN (_codice_ipa_ente <> '') IS TRUE THEN tes.codice_ipa_ente = _codice_ipa_ente ELSE true END     
   AND   CASE WHEN _dt_data_contabile_da IS NOT NULL THEN tes.dt_data_contabile >= _dt_data_contabile_da ELSE true END                       
   AND   CASE WHEN _dt_data_contabile_a IS NOT NULL THEN tes.dt_data_contabile <= _dt_data_contabile_a ELSE true END         
   AND   CASE WHEN _dt_data_valuta_da IS NOT NULL THEN tes.dt_data_valuta >= _dt_data_valuta_da ELSE true END                      
   AND   CASE WHEN _dt_data_valuta_a IS NOT NULL THEN tes.dt_data_valuta <= _dt_data_valuta_a ELSE true END              
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_da IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento >= _dt_data_ultimo_aggiornamento_da ELSE true END                       
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_a IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento <= _dt_data_ultimo_aggiornamento_a ELSE true END
   AND   CASE WHEN (_de_causale_t <> '') IS TRUE THEN upper(tes.de_causale_t) like '%' || upper(_de_causale_t) || '%' ELSE true END             
   AND   CASE WHEN (_importo <> '') IS TRUE THEN tes.de_importo = _importo ELSE true END                   
   AND   CASE WHEN (_conto <> '') IS TRUE THEN tes.cod_conto = _conto ELSE true END                 
   AND   CASE WHEN (_codOr1 <> '') IS TRUE THEN upper(tes.cod_or1) like '%' || upper(_codOr1) || '%' ELSE true END                                                            
   AND   CASE WHEN _flagnascosto IS NOT NULL THEN ms.flg_nascosto = _flagnascosto ELSE (ms.flg_nascosto is null or ms.flg_nascosto = false) END                    
   AND   CASE WHEN (_classificazione_completezza <> '') IS TRUE THEN tes.classificazione_completezza = _classificazione_completezza ELSE true END
   AND   CASE WHEN (_cod_iuv <> '') IS TRUE THEN tes.codice_iuv = _cod_iuv ELSE true END
   AND   CASE WHEN (_cod_iuf <> '') IS TRUE THEN upper(tes.identificativo_flusso_rendicontazione) like upper('%' || _cod_iuf || '%') ELSE true END;
$$;


ALTER FUNCTION public.get_count_tesoreria_subset_function(_codice_ipa_ente character varying, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _de_causale_t text, _importo character varying, _conto character varying, _codor1 character varying, _flagnascosto boolean, _classificazione_completezza character varying, _cod_iuv character varying, _cod_iuf character varying) OWNER TO mypay4;

--
-- TOC entry 289 (class 1255 OID 32883)
-- Name: get_dettaglio_pagamenti_cruscotto(integer, integer, integer, character varying, character varying, character varying, bigint, character varying); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_dettaglio_pagamenti_cruscotto(_anno integer, _mese integer, _giorno integer, _cod_ufficio character varying, _cod_dovuto character varying, _cod_capitolo character varying, _ente_id bigint, _cod_accertamento character varying) RETURNS SETOF character varying
    LANGUAGE sql STABLE
    AS $_$

SELECT DISTINCT(iud) as iud   	 
FROM
     (
       -- PAGATI
	SELECT 
	     DISTINCT(p.cod_iud) as iud
	FROM 
	    mygov_flusso_export AS p 
	    
	    INNER JOIN mygov_accertamento_dettaglio AS ad ON p.cod_iud = ad.cod_iud AND p.cod_tipo_dovuto = ad.cod_tipo_dovuto

	    INNER JOIN mygov_accertamento AS a ON a.mygov_accertamento_id = ad.mygov_accertamento_id

	    INNER JOIN mygov_anagrafica_stato AS st ON st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id
	    
	    INNER JOIN mygov_ente e ON e.cod_ipa_ente = ad.cod_ipa_ente AND p.mygov_ente_id = e.mygov_ente_id
	WHERE 
	     /* ACCERTAMENTI CHIUSI */
	     st.de_tipo_stato = 'ACCERTAMENTO' AND st.cod_stato = 'CHIUSO' AND 

	     /* ANNO */
	     CASE WHEN ($1 IS NOT NULL) THEN EXTRACT(YEAR FROM p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento)::integer = $1 ELSE true END AND	

	     /* MESE */
	     CASE WHEN ($2 IS NOT NULL) THEN EXTRACT(MONTH FROM p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento)::integer = $2 ELSE true END AND	

	     /* GIORNO */
	     CASE WHEN ($3 IS NOT NULL) THEN EXTRACT(DAY FROM p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento)::integer = $3 ELSE true END AND	
	      
	     /* CODICE UFFICIO */
	     CASE WHEN ($4 IS NOT NULL) THEN ad.cod_ufficio = $4 ELSE true END AND
	     
	     /* CODICE DOVUTO */
	     CASE WHEN ($5 IS NOT NULL) THEN p.cod_tipo_dovuto = $5 AND ad.cod_tipo_dovuto = $5 ELSE true END AND

	     /* CODICE CAPITOLO */
	     CASE WHEN ($6 IS NOT NULL) THEN ad.cod_capitolo = $6 ELSE true END AND
	     
	     /* ENTE ID */
	     CASE WHEN ($7 IS NOT NULL) THEN p.mygov_ente_id = $7 AND e.mygov_ente_id = $7 ELSE true END AND

	     /* CODICE ACCERTAMENTO */
	     CASE WHEN ($8 IS NOT NULL) THEN ad.cod_accertamento = $8 ELSE true END 
        
      UNION
    
      -- RENDICONTATI
	SELECT 
	     DISTINCT(r.cod_iud_e) AS iud
	FROM
	     mygov_import_export_rendicontazione_tesoreria_completa AS r 

	     INNER JOIN mygov_accertamento_dettaglio AS ad ON r.cod_iud_e = ad.cod_iud AND r.cod_tipo_dovuto_e = ad.cod_tipo_dovuto

	     INNER JOIN mygov_accertamento AS a ON a.mygov_accertamento_id = ad.mygov_accertamento_id 

	     INNER JOIN mygov_anagrafica_stato AS st ON st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id 

	     INNER JOIN mygov_ente e ON e.cod_ipa_ente = ad.cod_ipa_ente AND r.mygov_ente_id = e.mygov_ente_id 
	WHERE 
	     /* ACCERTAMENTI CHIUSI */
	     st.de_tipo_stato = 'ACCERTAMENTO' AND st.cod_stato = 'CHIUSO' AND 
	     
	     /* CLASSIFICAZIONE */
	     r.classificazione_completezza = 'RT_IUF' AND
	     
	     /* ANNO */
	     CASE WHEN ($1 IS NOT NULL) THEN EXTRACT(YEAR FROM r.dt_data_regolamento_r)::integer = $1 ELSE true END AND	

	     /* MESE */
	     CASE WHEN ($2 IS NOT NULL) THEN EXTRACT(MONTH FROM r.dt_data_regolamento_r)::integer = $2 ELSE true END AND	

	     /* GIORNO */
	     CASE WHEN ($3 IS NOT NULL) THEN EXTRACT(DAY FROM r.dt_data_regolamento_r)::integer = $3 ELSE true END AND	
	      
	     /* CODICE UFFICIO */
	     CASE WHEN ($4 IS NOT NULL) THEN ad.cod_ufficio = $4 ELSE true END AND
	     
	     /* CODICE DOVUTO */
	     CASE WHEN ($5 IS NOT NULL) THEN r.cod_tipo_dovuto_e = $5 AND ad.cod_tipo_dovuto = $5 ELSE true END AND

	     /* CODICE CAPITOLO */
	     CASE WHEN ($6 IS NOT NULL) THEN ad.cod_capitolo = $6 ELSE true END AND
	     
	     /* ENTE ID */
	     CASE WHEN ($7 IS NOT NULL) THEN e.mygov_ente_id = $7 AND e.mygov_ente_id = $7 ELSE true END AND

	     /* CODICE ACCERTAMENTO */
	     CASE WHEN ($8 IS NOT NULL) THEN ad.cod_accertamento = $8 ELSE true END 
      UNION 
     
      -- INCASSATI 
	SELECT 
	      DISTINCT(r.cod_iud_e) AS iud
	FROM
	     mygov_import_export_rendicontazione_tesoreria_completa AS r 

	     INNER JOIN mygov_accertamento_dettaglio AS ad ON r.cod_iud_e = ad.cod_iud AND r.cod_tipo_dovuto_e = ad.cod_tipo_dovuto
		
	     INNER JOIN mygov_accertamento AS a ON a.mygov_accertamento_id = ad.mygov_accertamento_id

	     INNER JOIN mygov_anagrafica_stato AS st ON st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id
	    
	     INNER JOIN mygov_ente e ON e.cod_ipa_ente = ad.cod_ipa_ente AND r.mygov_ente_id = e.mygov_ente_id 
	WHERE 
	    /* ACCERTAMENTI CHIUSI */
	    st.de_tipo_stato = 'ACCERTAMENTO' AND st.cod_stato = 'CHIUSO' AND 

	    /* CLASSSIFICAZIONE */
	    (r.classificazione_completezza = 'RT_IUF_TES' OR r.classificazione_completezza = 'RT_TES') AND

	    /* ANNO */
	    CASE WHEN ($1 IS NOT NULL) THEN EXTRACT(YEAR FROM r.dt_data_valuta_t)::integer = $1 ELSE true END AND	

	    /* MESE */
	    CASE WHEN ($2 IS NOT NULL) THEN EXTRACT(MONTH FROM r.dt_data_valuta_t)::integer = $2 ELSE true END AND	

	    /* GIORNO */
	    CASE WHEN ($3 IS NOT NULL) THEN EXTRACT(DAY FROM r.dt_data_valuta_t)::integer = $3 ELSE true END AND	
	      
	    /* CODICE UFFICIO */
	    CASE WHEN ($4 IS NOT NULL) THEN ad.cod_ufficio = $4 ELSE true END AND
	     
	    /* CODICE DOVUTO */
	    CASE WHEN ($5 IS NOT NULL) THEN r.cod_tipo_dovuto_e = $5 AND ad.cod_tipo_dovuto = $5 ELSE true END AND

	    /* CODICE CAPITOLO */
	    CASE WHEN ($6 IS NOT NULL) THEN ad.cod_capitolo = $6 ELSE true END AND
	     
	    /* ENTE ID */
	    CASE WHEN ($7 IS NOT NULL) THEN r.mygov_ente_id = $7 AND e.mygov_ente_id = $7 ELSE true END AND

	    /* CODICE ACCERTAMENTO */
	    CASE WHEN ($8 IS NOT NULL) THEN ad.cod_accertamento = $8 ELSE true END 
	     
     ) as subq
 ORDER BY subq.iud	 
   
$_$;


ALTER FUNCTION public.get_dettaglio_pagamenti_cruscotto(_anno integer, _mese integer, _giorno integer, _cod_ufficio character varying, _cod_dovuto character varying, _cod_capitolo character varying, _ente_id bigint, _cod_accertamento character varying) OWNER TO mypay4;

--
-- TOC entry 283 (class 1255 OID 32884)
-- Name: get_flusso_rendicontazione_function(bigint, date, date, character varying, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_flusso_rendicontazione_function(_mygov_ente_id bigint, _dt_data_regolamento_da date, _dt_data_regolamento_a date, _cod_iuf character varying, _identificativo_univoco_regolamento character varying, _page integer, _page_size integer) RETURNS TABLE(cod_identificativo_flusso character varying, mygov_ente_id bigint, mygov_manage_flusso_id bigint, identificativo_psp character varying, dt_data_ora_flusso timestamp without time zone, cod_identificativo_univoco_regolamento character varying, dt_data_regolamento date, cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco character, cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco character varying, de_ist_mitt_denominazione_mittente character varying, cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco character, cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco character varying, de_ist_ricev_denominazione_ricevente character varying, num_numero_totale_pagamenti numeric, num_importo_totale_pagamenti numeric, dt_acquisizione date, codice_bic_banca_di_riversamento character varying)
    LANGUAGE sql STABLE
    AS $$
  SELECT 
    DISTINCT(upper(rend.cod_identificativo_flusso)),
    rend.mygov_ente_id,
    rend.mygov_manage_flusso_id,
    rend.identificativo_psp,
    rend.dt_data_ora_flusso,
    rend.cod_identificativo_univoco_regolamento,
    rend.dt_data_regolamento,
    rend.cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco,
    rend.cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco,
    rend.de_ist_mitt_denominazione_mittente,
    rend.cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco,
    rend.cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco,
    rend.de_ist_ricev_denominazione_ricevente,
    rend.num_numero_totale_pagamenti,
    rend.num_importo_totale_pagamenti,
    rend.dt_acquisizione,
    rend.codice_bic_banca_di_riversamento
  FROM mygov_flusso_rendicontazione rend
     
  WHERE CASE WHEN _mygov_ente_id IS NOT NULL THEN rend.mygov_ente_id = _mygov_ente_id ELSE true END
  AND   CASE WHEN _dt_data_regolamento_da IS NOT NULL THEN rend.dt_data_regolamento >= _dt_data_regolamento_da ELSE true END
  AND   CASE WHEN _dt_data_regolamento_a IS NOT NULL THEN rend.dt_data_regolamento <= _dt_data_regolamento_a ELSE true END
  AND   CASE WHEN (_cod_iuf <> '') IS TRUE THEN upper(rend.cod_identificativo_flusso) like upper('%' || _cod_iuf || '%') ELSE true END
  AND   CASE WHEN (_identificativo_univoco_regolamento <> '') IS TRUE THEN rend.cod_identificativo_univoco_regolamento = _identificativo_univoco_regolamento ELSE true END
      
  ORDER BY rend.dt_data_regolamento DESC, rend.dt_data_ora_flusso DESC, upper(rend.cod_identificativo_flusso) DESC
  OFFSET CASE WHEN (_page IS NOT NULL) THEN ((_page - 1) * _page_size) ELSE 0 END 
  LIMIT CASE WHEN (_page_size IS NOT NULL) THEN _page_size ELSE 5 END;
$$;


ALTER FUNCTION public.get_flusso_rendicontazione_function(_mygov_ente_id bigint, _dt_data_regolamento_da date, _dt_data_regolamento_a date, _cod_iuf character varying, _identificativo_univoco_regolamento character varying, _page integer, _page_size integer) OWNER TO mypay4;

SET default_tablespace = '';

--
-- TOC entry 197 (class 1259 OID 32885)
-- Name: mygov_import_export_rendicontazione_tesoreria; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_import_export_rendicontazione_tesoreria (
    codice_ipa_ente character varying(80),
    dt_data_esecuzione_pagamento date,
    de_data_esecuzione_pagamento character varying(10),
    singolo_importo_commissione_carico_pa character varying(15),
    bilancio character varying(4096),
    nome_flusso_import_ente character varying(50),
    riga_flusso_import_ente character varying(12),
    codice_iud character varying(35),
    codice_iuv character varying(35),
    versione_oggetto character varying(16),
    identificativo_dominio character varying(35),
    identificativo_stazione_richiedente character varying(35),
    identificativo_messaggio_ricevuta character varying(35),
    data_ora_messaggio_ricevuta character varying(19),
    riferimento_messaggio_richiesta character varying(35),
    riferimento_data_richiesta character varying(10),
    tipo_identificativo_univoco_attestante character varying(1),
    codice_identificativo_univoco_attestante character varying(35),
    denominazione_attestante character varying(70),
    codice_unit_oper_attestante character varying(35),
    denom_unit_oper_attestante character varying(70),
    indirizzo_attestante character varying(70),
    civico_attestante character varying(16),
    cap_attestante character varying(16),
    localita_attestante character varying(35),
    provincia_attestante character varying(35),
    nazione_attestante character varying(2),
    tipo_identificativo_univoco_beneficiario character varying(1),
    codice_identificativo_univoco_beneficiario character varying(35),
    denominazione_beneficiario character varying(70),
    codice_unit_oper_beneficiario character varying(35),
    denom_unit_oper_beneficiario character varying(70),
    indirizzo_beneficiario character varying(70),
    civico_beneficiario character varying(16),
    cap_beneficiario character varying(16),
    localita_beneficiario character varying(35),
    provincia_beneficiario character varying(35),
    nazione_beneficiario character varying(2),
    tipo_identificativo_univoco_versante character varying(1),
    codice_identificativo_univoco_versante character varying(35),
    anagrafica_versante character varying(70),
    indirizzo_versante character varying(70),
    civico_versante character varying(16),
    cap_versante character varying(16),
    localita_versante character varying(35),
    provincia_versante character varying(35),
    nazione_versante character varying(2),
    email_versante character varying(256),
    tipo_identificativo_univoco_pagatore character varying(1),
    codice_identificativo_univoco_pagatore character varying(35),
    anagrafica_pagatore character varying(70),
    indirizzo_pagatore character varying(70),
    civico_pagatore character varying(16),
    cap_pagatore character varying(16),
    localita_pagatore character varying(35),
    provincia_pagatore character varying(35),
    nazione_pagatore character varying(2),
    email_pagatore character varying(256),
    codice_esito_pagamento character varying(1),
    importo_totale_pagato character varying(15),
    identificativo_univoco_versamento character varying(35),
    codice_contesto_pagamento character varying(35),
    singolo_importo_pagato character varying(15),
    esito_singolo_pagamento character varying(35),
    dt_data_esito_singolo_pagamento date,
    de_data_esito_singolo_pagamento character varying(10),
    identificativo_univoco_riscossione character varying(35),
    causale_versamento character varying(1024),
    dati_specifici_riscossione character varying(140),
    tipo_dovuto character varying(64),
    identificativo_flusso_rendicontazione character varying(35),
    data_ora_flusso_rendicontazione character varying(19),
    identificativo_univoco_regolamento character varying(35),
    dt_data_regolamento date,
    de_data_regolamento character varying,
    numero_totale_pagamenti character varying(15),
    importo_totale_pagamenti character varying(21),
    data_acquisizione character varying(10),
    cod_conto character varying(12),
    dt_data_contabile date,
    de_data_contabile character varying(10),
    dt_data_valuta date,
    de_data_valuta character varying(10),
    num_importo numeric(12,2),
    de_importo character varying(15),
    cod_or1 text,
    de_anno_bolletta character varying(4),
    cod_bolletta character varying(7),
    cod_id_dominio character varying(7),
    dt_ricezione timestamp without time zone,
    de_data_ricezione character varying(10),
    de_anno_documento character varying(4),
    cod_documento character varying(7),
    de_anno_provvisorio character varying(4),
    cod_provvisorio character varying(6),
    de_causale_t text,
    verifica_totale character varying(3),
    classificazione_completezza character varying(20),
    dt_data_ultimo_aggiornamento date,
    de_data_ultimo_aggiornamento character varying(10),
    indice_dati_singolo_pagamento integer,
    cod_iuf_key character varying(35),
    cod_iud_key character varying(35),
    cod_iuv_key character varying(35),
    bilancio_e character varying(4096),
    dt_effettiva_sospeso date,
    codice_gestionale_provvisorio character varying(10)
);


ALTER TABLE public.mygov_import_export_rendicontazione_tesoreria OWNER TO mypay4;

--
-- TOC entry 295 (class 1255 OID 32891)
-- Name: get_import_export_rend_tes_function(character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, date, date, date, date, character varying, character varying, date, date, date, date, date, date, date, date, character varying, boolean, character varying, character varying, character varying, boolean, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_import_export_rend_tes_function(_cod_fed_user_id character varying, _codice_ipa_ente character varying, _cod_iud character varying, _cod_iuv character varying, _denominazione_attestante character varying, _identificativo_univoco_riscossione character varying, _codice_identificativo_univoco_versante character varying, _anagrafica_versante character varying, _codice_identificativo_univoco_pagatore character varying, _anagrafica_pagatore character varying, _causale_versamento character varying, _data_esecuzione_singolo_pagamento_da date, _data_esecuzione_singolo_pagamento_a date, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _data_regolamento_da date, _data_regolamento_a date, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _cod_tipo_dovuto character varying, _is_cod_tipo_dovuto_present boolean, _importo character varying, _conto character varying, _codor1 character varying, _flagnascosto boolean, _classificazione_completezza character varying, _page integer, _size integer) RETURNS SETOF public.mygov_import_export_rendicontazione_tesoreria
    LANGUAGE sql STABLE
    AS $$
   SELECT 
      tes.*
   FROM 
      mygov_import_export_rendicontazione_tesoreria as tes 
  LEFT OUTER JOIN (SELECT ment.cod_ipa_ente
                        , mseg.cod_iuf
                        , mseg.cod_iuv
                        , mseg.cod_iud
                        , mseg.flg_nascosto
                     FROM mygov_segnalazione as mseg 
                             INNER JOIN   mygov_ente as ment 
                             ON           mseg.mygov_ente_id = ment.mygov_ente_id 
                             WHERE        mseg.flg_attivo = true 
                             AND          mseg.classificazione_completezza = _classificazione_completezza) as ms                              
     ON   ms.cod_ipa_ente = tes.codice_ipa_ente 
     AND (ms.cod_iuf IS NULL 
     AND  tes.cod_iuf_key IS NULL 
     OR   ms.cod_iuf = tes.cod_iuf_key)
     AND (ms.cod_iuv IS NULL 
     AND  tes.cod_iuv_key IS NULL 
     OR   ms.cod_iuv = tes.cod_iuv_key)
     AND (ms.cod_iud IS NULL 
     AND  tes.cod_iud_key IS NULL 
     OR   ms.cod_iud = tes.cod_iud_key)
     
   WHERE CASE WHEN (_cod_tipo_dovuto <> '') IS TRUE AND _is_cod_tipo_dovuto_present THEN tes.tipo_dovuto = _cod_tipo_dovuto 
              WHEN (_cod_tipo_dovuto <> '') IS NOT TRUE AND _is_cod_tipo_dovuto_present THEN 
                  tes.tipo_dovuto in (SELECT DISTINCT(metd.cod_tipo)
                                      FROM   mygov_operatore_ente_tipo_dovuto as moetd, mygov_ente_tipo_dovuto as metd 
                                      WHERE  moetd.mygov_ente_tipo_dovuto_id = metd.mygov_ente_tipo_dovuto_id
                                      AND    moetd.cod_fed_user_id = _cod_fed_user_id 
                                      AND   moetd.flg_attivo = true)
              ELSE true      
         END  
   AND   CASE WHEN (_codice_ipa_ente <> '') IS TRUE THEN tes.codice_ipa_ente = _codice_ipa_ente ELSE true END 
   AND   CASE WHEN (_cod_iud <> '') IS TRUE THEN tes.codice_iud = _cod_iud ELSE true END  
   AND   CASE WHEN (_cod_iuv <> '') IS TRUE THEN tes.codice_iuv = _cod_iuv ELSE true END        
   AND   CASE WHEN (_denominazione_attestante <> '') IS TRUE THEN 
                    (upper(tes.denominazione_attestante) like '%' || upper(_denominazione_attestante) || '%' 
                 OR upper(tes.codice_identificativo_univoco_attestante) like '%' || upper(_denominazione_attestante) || '%') 
         ELSE true END     
   AND   CASE WHEN (_identificativo_univoco_riscossione <> '') IS TRUE THEN tes.identificativo_univoco_riscossione = _identificativo_univoco_riscossione ELSE true END        
   AND   CASE WHEN (_codice_identificativo_univoco_versante <> '') IS TRUE THEN (tes.codice_identificativo_univoco_versante = upper(_codice_identificativo_univoco_versante) OR tes.codice_identificativo_univoco_versante = lower(_codice_identificativo_univoco_versante)) ELSE true END        
   AND   CASE WHEN (_anagrafica_versante <> '') IS TRUE THEN upper(tes.anagrafica_versante) like '%' || upper(_anagrafica_versante) || '%' ELSE true END              
   AND   CASE WHEN (_codice_identificativo_univoco_pagatore <> '') IS TRUE THEN (tes.codice_identificativo_univoco_pagatore = upper(_codice_identificativo_univoco_pagatore) OR tes.codice_identificativo_univoco_pagatore = lower(_codice_identificativo_univoco_pagatore)) ELSE true END           
   AND   CASE WHEN (_anagrafica_pagatore <> '') IS TRUE THEN upper(tes.anagrafica_pagatore) like '%' || upper(_anagrafica_pagatore) || '%' ELSE true END                  
   AND   CASE WHEN (_causale_versamento <> '') IS TRUE THEN upper(tes.causale_versamento) like '%' || upper(_causale_versamento) || '%' ELSE true END                    
   AND   CASE WHEN _data_esecuzione_singolo_pagamento_da IS NOT NULL THEN tes.dt_data_esecuzione_pagamento >= _data_esecuzione_singolo_pagamento_da ELSE true END                       
   AND   CASE WHEN _data_esecuzione_singolo_pagamento_a IS NOT NULL THEN tes.dt_data_esecuzione_pagamento <= _data_esecuzione_singolo_pagamento_a ELSE true END                     
   AND   CASE WHEN _data_esito_singolo_pagamento_da IS NOT NULL THEN tes.dt_data_esito_singolo_pagamento >= _data_esito_singolo_pagamento_da ELSE true END                       
   AND   CASE WHEN _data_esito_singolo_pagamento_a IS NOT NULL THEN tes.dt_data_esito_singolo_pagamento <= _data_esito_singolo_pagamento_a ELSE true END             
   AND   CASE WHEN (_identificativo_flusso_rendicontazione <> '') IS TRUE THEN upper(tes.identificativo_flusso_rendicontazione) like upper('%' || _identificativo_flusso_rendicontazione || '%') ELSE true END           
   AND   CASE WHEN (_identificativo_univoco_regolamento <> '') IS TRUE THEN tes.identificativo_univoco_regolamento = _identificativo_univoco_regolamento ELSE true END           
   AND   CASE WHEN _data_regolamento_da IS NOT NULL THEN tes.dt_data_regolamento >= _data_regolamento_da ELSE true END                       
   AND   CASE WHEN _data_regolamento_a IS NOT NULL THEN tes.dt_data_regolamento <= _data_regolamento_a ELSE true END             
   AND   CASE WHEN _dt_data_contabile_da IS NOT NULL THEN tes.dt_data_contabile >= _dt_data_contabile_da ELSE true END                       
   AND   CASE WHEN _dt_data_contabile_a IS NOT NULL THEN tes.dt_data_contabile <= _dt_data_contabile_a ELSE true END             
   AND   CASE WHEN _dt_data_valuta_da IS NOT NULL THEN tes.dt_data_valuta >= _dt_data_valuta_da ELSE true END                      
   AND   CASE WHEN _dt_data_valuta_a IS NOT NULL THEN tes.dt_data_valuta <= _dt_data_valuta_a ELSE true END              
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_da IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento >= _dt_data_ultimo_aggiornamento_da ELSE true END                       
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_a IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento <= _dt_data_ultimo_aggiornamento_a ELSE true END             
   AND   CASE WHEN (_importo <> '') IS TRUE THEN tes.de_importo = _importo ELSE true END                   
   AND   CASE WHEN (_conto <> '') IS TRUE THEN tes.cod_conto = _conto ELSE true END                 
   AND   CASE WHEN (_codOr1 <> '') IS TRUE THEN upper(tes.cod_or1)  like '%' || upper(_codOr1) || '%' ELSE true END                                                            
   AND   CASE WHEN _flagnascosto IS NOT NULL THEN ms.flg_nascosto = _flagnascosto ELSE (ms.flg_nascosto is null or ms.flg_nascosto = false) END                    
   AND   CASE WHEN (_classificazione_completezza <> '') IS TRUE THEN tes.classificazione_completezza = _classificazione_completezza ELSE true END     
         

   
   ORDER BY CASE WHEN _classificazione_completezza = 'IUD_RT_IUF_TES' OR 
                      _classificazione_completezza = 'RT_IUF_TES' OR
                      _classificazione_completezza = 'RT_IUF' OR
                      _classificazione_completezza = 'IUD_RT_IUF' OR 
                      _classificazione_completezza = 'RT_NO_IUF' OR
                      _classificazione_completezza = 'RT_NO_IUD' THEN (dt_data_esito_singolo_pagamento, codice_iuv, codice_iud)                         
                 WHEN _classificazione_completezza = 'IUD_NO_RT' THEN (dt_data_esecuzione_pagamento, codice_iud)  
                ELSE (dt_data_esito_singolo_pagamento, codice_iuv, codice_iud)
            END 

   OFFSET CASE WHEN (_page IS NOT NULL) THEN ((_page - 1)*_size) ELSE 0 END 
   LIMIT CASE WHEN (_size IS NOT NULL) THEN _size ELSE 5 END;
$$;


ALTER FUNCTION public.get_import_export_rend_tes_function(_cod_fed_user_id character varying, _codice_ipa_ente character varying, _cod_iud character varying, _cod_iuv character varying, _denominazione_attestante character varying, _identificativo_univoco_riscossione character varying, _codice_identificativo_univoco_versante character varying, _anagrafica_versante character varying, _codice_identificativo_univoco_pagatore character varying, _anagrafica_pagatore character varying, _causale_versamento character varying, _data_esecuzione_singolo_pagamento_da date, _data_esecuzione_singolo_pagamento_a date, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _data_regolamento_da date, _data_regolamento_a date, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _cod_tipo_dovuto character varying, _is_cod_tipo_dovuto_present boolean, _importo character varying, _conto character varying, _codor1 character varying, _flagnascosto boolean, _classificazione_completezza character varying, _page integer, _size integer) OWNER TO mypay4;

--
-- TOC entry 296 (class 1255 OID 32893)
-- Name: get_pagamenti_inseribili_in_accertamento(bigint, character varying, character varying, character varying, character varying, date, date, date, date, boolean, integer, integer); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_pagamenti_inseribili_in_accertamento(_ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date, _has_pagination boolean, _page integer, _page_size integer) RETURNS TABLE(cod_tipo_dovuto character varying, de_tipo_dovuto character varying, cod_iud character varying, cod_rp_silinviarp_id_univoco_versamento character varying, cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss character varying, de_e_istit_att_denominazione_attestante character varying, cod_e_istit_att_id_univ_att_codice_id_univoco character varying, cod_e_istit_att_id_univ_att_tipo_id_univoco character, cod_e_sogg_vers_anagrafica_versante character varying, cod_e_sogg_vers_id_univ_vers_codice_id_univoco character varying, cod_e_sogg_vers_id_univ_vers_tipo_id_univoco character, cod_e_sogg_pag_anagrafica_pagatore character varying, cod_e_sogg_pag_id_univ_pag_codice_id_univoco character varying, cod_e_sogg_pag_id_univ_pag_tipo_id_univoco character, dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento date, dt_ultima_modifica timestamp without time zone, num_e_dati_pag_dati_sing_pag_singolo_importo_pagato numeric, de_e_dati_pag_dati_sing_pag_causale_versamento character varying)
    LANGUAGE sql
    AS $_$
  SELECT 
        cod_tipo_dovuto, td.de_tipo as de_tipo_dovuto, cod_iud, cod_rp_silinviarp_id_univoco_versamento, cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss, de_e_istit_att_denominazione_attestante,
        cod_e_istit_att_id_univ_att_codice_id_univoco, cod_e_istit_att_id_univ_att_tipo_id_univoco, cod_e_sogg_vers_anagrafica_versante, 
        cod_e_sogg_vers_id_univ_vers_codice_id_univoco, cod_e_sogg_vers_id_univ_vers_tipo_id_univoco, cod_e_sogg_pag_anagrafica_pagatore, 
        cod_e_sogg_pag_id_univ_pag_codice_id_univoco, cod_e_sogg_pag_id_univ_pag_tipo_id_univoco, dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento,
        dt_ultima_modifica, num_e_dati_pag_dati_sing_pag_singolo_importo_pagato, de_e_dati_pag_dati_sing_pag_causale_versamento
  FROM 
      mygov_flusso_export AS p INNER JOIN mygov_ente_tipo_dovuto AS td ON p.cod_tipo_dovuto = td.cod_tipo AND td.mygov_ente_id = p.mygov_ente_id
  WHERE
      /* Escludo le righe gia in accertamento */
      (p.cod_iud  || '-' || p.cod_rp_silinviarp_id_univoco_versamento) 
  NOT IN 
      (
        SELECT 
           ad.cod_iud || '-' || ad.cod_iuv 
        FROM 
           mygov_accertamento_dettaglio ad 
              INNER JOIN mygov_accertamento a ON ad.mygov_accertamento_id = a.mygov_accertamento_id
              INNER JOIN mygov_anagrafica_stato st ON a.mygov_anagrafica_stato_id = st.mygov_anagrafica_stato_id
        WHERE 
             st.de_tipo_stato = 'ACCERTAMENTO' AND st.cod_stato <> 'ANNULLATO' 
      ) AND
  
      /* Condizioni obbligatorie */
      p.mygov_ente_id = $1 AND p.cod_tipo_dovuto = $2 AND p.bilancio IS NULL AND
            
      /* IUD */
      CASE WHEN ($3 IS NOT NULL) THEN p.cod_iud = $3 ELSE true END AND
      /* IUV */
      CASE WHEN ($4 IS NOT NULL) THEN p.cod_rp_silinviarp_id_univoco_versamento = $4 ELSE true END AND
      /* Identificativo univoco pagatore */
      CASE WHEN ($5 IS NOT NULL) THEN p.cod_e_sogg_pag_id_univ_pag_codice_id_univoco = $5 ELSE true END AND
      /* Data esito pagamento da */
      CASE WHEN ($6 IS NOT NULL) THEN p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento >= $6 ELSE true END AND
      /* Data esito pagamento a */
      CASE WHEN ($7 IS NOT NULL) THEN p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento <= $7 ELSE true END AND
      /* Data ultimo aggiornamento da */
      CASE WHEN ($8 IS NOT NULL) THEN p.dt_ultima_modifica >= $8 ELSE true END AND
      /* Data ultimo aggiornamento a */
      CASE WHEN ($9 IS NOT NULL) THEN p.dt_ultima_modifica <= $9 ELSE true END

   ORDER BY dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento ASC, cod_rp_silinviarp_id_univoco_versamento ASC, cod_iud ASC

   OFFSET CASE WHEN ($10) THEN $11 ELSE 0 END 

   LIMIT CASE WHEN ($10) THEN $12 ELSE null END;
   
$_$;


ALTER FUNCTION public.get_pagamenti_inseribili_in_accertamento(_ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date, _has_pagination boolean, _page integer, _page_size integer) OWNER TO mypay4;

--
-- TOC entry 2698 (class 0 OID 0)
-- Dependencies: 296
-- Name: FUNCTION get_pagamenti_inseribili_in_accertamento(_ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date, _has_pagination boolean, _page integer, _page_size integer); Type: COMMENT; Schema: public; Owner: mypay4
--

COMMENT ON FUNCTION public.get_pagamenti_inseribili_in_accertamento(_ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date, _has_pagination boolean, _page integer, _page_size integer) IS 'Pagamenti inseribili in accertamento';


--
-- TOC entry 297 (class 1255 OID 32894)
-- Name: get_pagamenti_inseriti_in_accertamento(bigint, bigint, character varying, character varying, character varying, character varying, date, date, date, date, boolean, integer, integer); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_pagamenti_inseriti_in_accertamento(_accertamento_id bigint, _ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date, _has_pagination boolean, _page integer, _page_size integer) RETURNS TABLE(mygov_accertamento_dettaglio_id_acc bigint, mygov_accertamento_id_acc bigint, cod_ipa_ente_acc character varying, cod_tipo_dovuto_acc character varying, cod_iud_acc character varying, cod_iuv_acc character varying, dt_ultima_modifica_acc timestamp without time zone, dt_data_inserimento_acc timestamp without time zone, cod_tipo_dovuto character varying, de_tipo_dovuto character varying, cod_iud character varying, cod_rp_silinviarp_id_univoco_versamento character varying, cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss character varying, de_e_istit_att_denominazione_attestante character varying, cod_e_istit_att_id_univ_att_codice_id_univoco character varying, cod_e_istit_att_id_univ_att_tipo_id_univoco character, cod_e_sogg_vers_anagrafica_versante character varying, cod_e_sogg_vers_id_univ_vers_codice_id_univoco character varying, cod_e_sogg_vers_id_univ_vers_tipo_id_univoco character, cod_e_sogg_pag_anagrafica_pagatore character varying, cod_e_sogg_pag_id_univ_pag_codice_id_univoco character varying, cod_e_sogg_pag_id_univ_pag_tipo_id_univoco character, dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento date, dt_ultima_modifica timestamp without time zone, num_e_dati_pag_dati_sing_pag_singolo_importo_pagato numeric, de_e_dati_pag_dati_sing_pag_causale_versamento character varying)
    LANGUAGE sql
    AS $_$
    SELECT
	/* Accertamento dettaglio */
	mygov_accertamento_dettaglio_id_acc, 
	mygov_accertamento_id_acc, cod_ipa_ente_acc, 
	cod_tipo_dovuto_acc, 
	cod_iud_acc, 
	cod_iuv_acc,
	dt_ultima_modifica_acc, 
	dt_data_inserimento_acc,

	/* Flusso export */
	cod_tipo_dovuto, de_tipo_dovuto, cod_iud, cod_rp_silinviarp_id_univoco_versamento,        cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss, 
	de_e_istit_att_denominazione_attestante,  cod_e_istit_att_id_univ_att_codice_id_univoco,  cod_e_istit_att_id_univ_att_tipo_id_univoco, 
	cod_e_sogg_vers_anagrafica_versante,      cod_e_sogg_vers_id_univ_vers_codice_id_univoco, cod_e_sogg_vers_id_univ_vers_tipo_id_univoco, 
	cod_e_sogg_pag_anagrafica_pagatore,       cod_e_sogg_pag_id_univ_pag_codice_id_univoco,   cod_e_sogg_pag_id_univ_pag_tipo_id_univoco, 
	dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento, dt_ultima_modifica,             num_e_dati_pag_dati_sing_pag_singolo_importo_pagato, 
	de_e_dati_pag_dati_sing_pag_causale_versamento
    FROM
	(
	  SELECT
	      DISTINCT ON(a.cod_iud || '-' || a.cod_iuv) cod_iuv,
	      /* Accertamento dettaglio */
	      a.mygov_accertamento_dettaglio_id AS mygov_accertamento_dettaglio_id_acc, a.mygov_accertamento_id AS mygov_accertamento_id_acc, 
	      a.cod_ipa_ente AS cod_ipa_ente_acc, a.cod_tipo_dovuto AS cod_tipo_dovuto_acc, a.cod_iud AS cod_iud_acc, a.cod_iuv AS cod_iuv_acc,
	      a.dt_ultima_modifica AS dt_ultima_modifica_acc, a.dt_data_inserimento AS dt_data_inserimento_acc,

	      /* Flusso export */
	      p.cod_tipo_dovuto, td.de_tipo as de_tipo_dovuto, p.cod_iud, p.cod_rp_silinviarp_id_univoco_versamento, p.cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss, 
	      p.de_e_istit_att_denominazione_attestante, p.cod_e_istit_att_id_univ_att_codice_id_univoco, p.cod_e_istit_att_id_univ_att_tipo_id_univoco, 
	      p.cod_e_sogg_vers_anagrafica_versante, p.cod_e_sogg_vers_id_univ_vers_codice_id_univoco, p.cod_e_sogg_vers_id_univ_vers_tipo_id_univoco, 
	      p.cod_e_sogg_pag_anagrafica_pagatore, p.cod_e_sogg_pag_id_univ_pag_codice_id_univoco, p.cod_e_sogg_pag_id_univ_pag_tipo_id_univoco, 
	      p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento, p.dt_ultima_modifica, p.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato, 
	      p.de_e_dati_pag_dati_sing_pag_causale_versamento  
	  FROM 
	     mygov_flusso_export AS p 
		INNER JOIN mygov_accertamento_dettaglio AS a ON p.cod_iud = a.cod_iud AND p.cod_rp_silinviarp_id_univoco_versamento = a.cod_iuv
		INNER JOIN mygov_ente_tipo_dovuto AS td ON p.cod_tipo_dovuto = td.cod_tipo AND td.mygov_ente_id = p.mygov_ente_id
	  WHERE
	      /* Condizioni obbligatorie */
	      a.mygov_accertamento_id = $1 AND p.mygov_ente_id = $2 AND p.cod_tipo_dovuto = $3 AND
		    
	      /* IUD */
	      CASE WHEN ($4 IS NOT NULL) THEN p.cod_iud = $4 ELSE true END AND
	      /* IUV */
	      CASE WHEN ($5 IS NOT NULL) THEN p.cod_rp_silinviarp_id_univoco_versamento = $5 ELSE true END AND
	      /* Identificativo univoco pagatore */
	      CASE WHEN ($6 IS NOT NULL) THEN p.cod_e_sogg_pag_id_univ_pag_codice_id_univoco = $6 ELSE true END AND
	      /* Data esito pagamento da */
	      CASE WHEN ($7 IS NOT NULL) THEN p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento >= $7 ELSE true END AND
	      /* Data esito pagamento a */
	      CASE WHEN ($8 IS NOT NULL) THEN p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento <= $8 ELSE true END AND
	      /* Data ultimo aggiornamento da */
	      CASE WHEN ($9 IS NOT NULL) THEN p.dt_ultima_modifica >= $9 ELSE true END AND
	      /* Data ultimo aggiornamento a */
	      CASE WHEN ($10 IS NOT NULL) THEN p.dt_ultima_modifica <= $10 ELSE true END
	 
	   OFFSET CASE WHEN ($11) THEN $12 ELSE 0 END 

	   LIMIT CASE WHEN ($11) THEN $13 ELSE null END
	) as subq
    ORDER BY dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento ASC, cod_rp_silinviarp_id_univoco_versamento ASC, cod_iud ASC;

   
$_$;


ALTER FUNCTION public.get_pagamenti_inseriti_in_accertamento(_accertamento_id bigint, _ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date, _has_pagination boolean, _page integer, _page_size integer) OWNER TO mypay4;

--
-- TOC entry 2699 (class 0 OID 0)
-- Dependencies: 297
-- Name: FUNCTION get_pagamenti_inseriti_in_accertamento(_accertamento_id bigint, _ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date, _has_pagination boolean, _page integer, _page_size integer); Type: COMMENT; Schema: public; Owner: mypay4
--

COMMENT ON FUNCTION public.get_pagamenti_inseriti_in_accertamento(_accertamento_id bigint, _ente_id bigint, _cod_tipo_dovuto character varying, _codice_iud character varying, _codice_iuv character varying, _codice_identificativo_univoco_pagatore character varying, _data_esito_singolo_pagamento_da date, _data_esito_singolo_pagamento_a date, _data_ultimo_aggiornamento_da date, _data_ultimo_aggiornamento_a date, _has_pagination boolean, _page integer, _page_size integer) IS 'Pagamenti inseriti in accertamento';


--
-- TOC entry 298 (class 1255 OID 32896)
-- Name: get_rendicontazione_subset_function(character varying, character varying, character varying, date, date, date, date, character varying, character varying, character varying, boolean, integer, integer); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_rendicontazione_subset_function(_codice_ipa_ente character varying, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _dt_data_regolamento_da date, _dt_data_regolamento_a date, dt_data_ultimo_aggiornamento_da date, dt_data_ultimo_aggiornamento_a date, _classificazione_completezza character varying, _cod_tipo_dovuto character varying, _cod_fed_user_id character varying, _flagnascosto boolean, _page integer, _size integer) RETURNS TABLE(identificativo_flusso_rendicontazione character varying, codice_ipa_ente character varying, singolo_importo_commissione_carico_pa character varying, bilancio character varying, data_ora_flusso_rendicontazione character varying, identificativo_univoco_regolamento character varying, dt_data_regolamento date, de_data_regolamento character varying, importo_totale_pagamenti character varying, de_anno_bolletta character varying, cod_bolletta character varying, cod_id_dominio character varying, dt_ricezione timestamp without time zone, de_data_ricezione character varying, de_anno_documento character varying, cod_documento character varying, de_anno_provvisorio character varying, cod_provvisorio character varying, classificazione_completezza character varying, dt_data_ultimo_aggiornamento date, de_data_ultimo_aggiornamento character varying, indice_dati_singolo_pagamento integer, cod_iuf_key character varying)
    LANGUAGE sql
    AS $_$
   SELECT 
      DISTINCT (upper(tes.identificativo_flusso_rendicontazione)),tes.codice_ipa_ente,tes.singolo_importo_commissione_carico_pa,tes.bilancio,tes.data_ora_flusso_rendicontazione,tes.identificativo_univoco_regolamento,tes.dt_data_regolamento,tes.de_data_regolamento,tes.importo_totale_pagamenti,tes.de_anno_bolletta,tes.cod_bolletta,tes.cod_id_dominio,tes.dt_ricezione,tes.de_data_ricezione,tes.de_anno_documento,tes.cod_documento,tes.de_anno_provvisorio,tes.cod_provvisorio,tes.classificazione_completezza, MAX(tes.dt_data_ultimo_aggiornamento)as dt_data_ultimo_aggiornamento, to_char(MAX(tes.dt_data_ultimo_aggiornamento), 'DD-MM-YYYY') as de_data_ultimo_aggiornamento, tes.indice_dati_singolo_pagamento,tes.cod_iuf_key
   FROM 
      mygov_import_export_rendicontazione_tesoreria as tes 
  LEFT OUTER JOIN (SELECT mseg.*, ment.* FROM mygov_segnalazione as mseg INNER JOIN mygov_ente as ment ON mseg.mygov_ente_id = ment.mygov_ente_id WHERE mseg.flg_attivo = true AND mseg.classificazione_completezza = $8) as ms 
     ON ms.cod_ipa_ente = tes.codice_ipa_ente AND (ms.cod_iuf = tes.cod_iuf_key OR (ms.cod_iuf IS NULL and tes.cod_iuf_key IS NULL))
   WHERE  
         CASE WHEN $1 IS NOT NULL AND $1!='' THEN tes.codice_ipa_ente = $1 ELSE true END
     AND CASE WHEN $2 IS NOT NULL AND $2!='' THEN upper(tes.identificativo_flusso_rendicontazione) like upper('%' || $2 || '%') ELSE true END
     AND (COALESCE($3, '') ='' OR tes.identificativo_univoco_regolamento = $3)
     AND CASE WHEN $4 IS NOT NULL THEN tes.dt_data_regolamento >= $4 ELSE true END
     AND CASE WHEN $5 IS NOT NULL THEN tes.dt_data_regolamento <= $5 ELSE true END
     AND CASE WHEN $6 IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento >= $6 ELSE true END
     AND CASE WHEN $7 IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento < $7 ELSE true END
     AND CASE WHEN $8 IS NOT NULL AND $8!='' THEN tes.classificazione_completezza = $8 ELSE true END
     AND CASE WHEN $9 IS NOT NULL AND $9!='' AND tes.classificazione_completezza <> 'IUV_NO_RT' THEN tes.tipo_dovuto = $9 
          WHEN ($9 IS NULL OR $9='') AND tes.classificazione_completezza <> 'IUV_NO_RT' THEN 
      tes.tipo_dovuto in (SELECT
           DISTINCT(metd.cod_tipo)
        FROM 
           mygov_operatore_ente_tipo_dovuto as moetd, mygov_ente_tipo_dovuto as metd 
        WHERE
           moetd.mygov_ente_tipo_dovuto_id = metd.mygov_ente_tipo_dovuto_id AND
           moetd.cod_fed_user_id = $10 AND 
           moetd.flg_attivo = true)
    ELSE true      
   END
     AND CASE WHEN $11 IS NOT NULL 
         THEN
       ms.flg_nascosto = $11
         ELSE
             (ms.flg_nascosto is null or ms.flg_nascosto = false)
         END
   GROUP BY upper(tes.identificativo_flusso_rendicontazione),tes.codice_ipa_ente,tes.singolo_importo_commissione_carico_pa,tes.bilancio,tes.data_ora_flusso_rendicontazione,tes.identificativo_univoco_regolamento,tes.dt_data_regolamento,tes.de_data_regolamento,tes.importo_totale_pagamenti,tes.de_anno_bolletta,tes.cod_bolletta,tes.cod_id_dominio,tes.dt_ricezione,tes.de_data_ricezione,tes.de_anno_documento,tes.cod_documento,tes.de_anno_provvisorio,tes.cod_provvisorio,tes.classificazione_completezza, tes.indice_dati_singolo_pagamento,tes.cod_iuf_key
   ORDER BY dt_data_ultimo_aggiornamento DESC
   OFFSET CASE WHEN ($12 IS NOT NULL) THEN (($12 - 1)*$13) ELSE 0 END 
   LIMIT CASE WHEN ($13 IS NOT NULL) THEN $13 ELSE 5 END;
$_$;


ALTER FUNCTION public.get_rendicontazione_subset_function(_codice_ipa_ente character varying, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _dt_data_regolamento_da date, _dt_data_regolamento_a date, dt_data_ultimo_aggiornamento_da date, dt_data_ultimo_aggiornamento_a date, _classificazione_completezza character varying, _cod_tipo_dovuto character varying, _cod_fed_user_id character varying, _flagnascosto boolean, _page integer, _size integer) OWNER TO mypay4;

--
-- TOC entry 299 (class 1255 OID 32898)
-- Name: get_rendicontazione_tesoreria_subset_function(character varying, character varying, character varying, date, date, date, date, date, date, date, date, text, character varying, character varying, character varying, character varying, boolean, character varying, boolean, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_rendicontazione_tesoreria_subset_function(_codice_ipa_ente character varying, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _dt_data_regolamento_da date, _dt_data_regolamento_a date, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _de_causale_t text, _importo character varying, _conto character varying, _codor1 character varying, _cod_tipo_dovuto character varying, _is_cod_tipo_dovuto_present boolean, _cod_fed_user_id character varying, _flagnascosto boolean, _classificazione_completezza character varying, _page integer, _size integer) RETURNS TABLE(identificativo_flusso_rendicontazione character varying, codice_ipa_ente character varying, dt_data_esecuzione_pagamento date, de_data_esecuzione_pagamento character varying, singolo_importo_commissione_carico_pa character varying, cod_conto character varying, dt_data_contabile date, de_data_contabile character varying, dt_data_valuta date, de_data_valuta character varying, num_importo numeric, de_importo character varying, cod_or1 text, de_anno_bolletta character varying, cod_bolletta character varying, cod_id_dominio character varying, dt_ricezione timestamp without time zone, de_data_ricezione character varying, de_anno_documento character varying, cod_documento character varying, de_anno_provvisorio character varying, cod_provvisorio character varying, de_causale_t text, classificazione_completezza character varying, dt_data_ultimo_aggiornamento date, de_data_ultimo_aggiornamento character varying, data_ora_flusso_rendicontazione character varying, identificativo_univoco_regolamento character varying, dt_data_regolamento date, de_data_regolamento character varying, importo_totale_pagamenti character varying, cod_iuf_key character varying)
    LANGUAGE sql STABLE
    AS $$
   SELECT 
        DISTINCT (upper(iert.identificativo_flusso_rendicontazione)),
        iert.codice_ipa_ente,
		iert.dt_data_esecuzione_pagamento,
		iert.de_data_esecuzione_pagamento,
		iert.singolo_importo_commissione_carico_pa,
		iert.cod_conto,
		iert.dt_data_contabile,
		iert.de_data_contabile,
		iert.dt_data_valuta,
		iert.de_data_valuta,
		iert.num_importo,
		iert.de_importo,
		iert.cod_or1,
		iert.de_anno_bolletta,
		iert.cod_bolletta,
		iert.cod_id_dominio,
		iert.dt_ricezione,
		iert.de_data_ricezione,
		iert.de_anno_documento,
		iert.cod_documento,
		iert.de_anno_provvisorio,
		iert.cod_provvisorio,
		iert.de_causale_t,
		iert.classificazione_completezza,
		iert.dt_data_ultimo_aggiornamento,
		iert.de_data_ultimo_aggiornamento,
		iert.data_ora_flusso_rendicontazione,
		iert.identificativo_univoco_regolamento,
		iert.dt_data_regolamento,
		iert.de_data_regolamento,
		iert.importo_totale_pagamenti,
		iert.cod_iuf_key
   FROM 
      mygov_import_export_rendicontazione_tesoreria as iert 
  LEFT OUTER JOIN (SELECT ment.cod_ipa_ente
                        , mseg.cod_iuf
                        , mseg.cod_iuv
                        , mseg.flg_nascosto
                     FROM mygov_segnalazione as mseg 
                             INNER JOIN   mygov_ente as ment 
                             ON           mseg.mygov_ente_id = ment.mygov_ente_id 
                             WHERE        mseg.flg_attivo = true 
                             AND          mseg.classificazione_completezza = _classificazione_completezza) as ms
     ON   ms.cod_ipa_ente = iert.codice_ipa_ente 
    AND (ms.cod_iuf IS NULL 
    AND  iert.cod_iuf_key IS NULL 
    OR   ms.cod_iuf = iert.cod_iuf_key)
    AND (ms.cod_iuv IS NULL 
    AND  iert.cod_iuv_key IS NULL 
    OR   ms.cod_iuv = iert.cod_iuv_key)
     
   WHERE CASE WHEN _codice_ipa_ente IS NOT NULL THEN iert.codice_ipa_ente = _codice_ipa_ente ELSE true END
   AND   CASE WHEN _identificativo_flusso_rendicontazione IS NOT NULL THEN upper(iert.identificativo_flusso_rendicontazione) like upper('%' || _identificativo_flusso_rendicontazione || '%') ELSE true END
   AND   CASE WHEN _identificativo_univoco_regolamento IS NOT NULL THEN iert.identificativo_univoco_regolamento = _identificativo_univoco_regolamento ELSE true END
   AND   CASE WHEN _dt_data_regolamento_da IS NOT NULL THEN iert.dt_data_regolamento >= _dt_data_regolamento_da ELSE true END
   AND   CASE WHEN _dt_data_regolamento_a IS NOT NULL THEN iert.dt_data_regolamento <= _dt_data_regolamento_a ELSE true END
   AND   CASE WHEN _dt_data_contabile_da IS NOT NULL THEN iert.dt_data_contabile >= _dt_data_contabile_da ELSE true END
   AND   CASE WHEN _dt_data_contabile_a IS NOT NULL THEN iert.dt_data_contabile <= _dt_data_contabile_a ELSE true END
   AND   CASE WHEN _dt_data_valuta_da IS NOT NULL THEN iert.dt_data_valuta >= _dt_data_valuta_da ELSE true END
   AND   CASE WHEN _dt_data_valuta_a IS NOT NULL THEN iert.dt_data_valuta <= _dt_data_valuta_a ELSE true END
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_da IS NOT NULL THEN iert.dt_data_ultimo_aggiornamento >= _dt_data_ultimo_aggiornamento_da ELSE true END
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_a IS NOT NULL THEN iert.dt_data_ultimo_aggiornamento <= _dt_data_ultimo_aggiornamento_a ELSE true END
   AND   CASE WHEN _de_causale_t IS NOT NULL THEN upper(iert.de_causale_t) like '%' || upper(_de_causale_t) || '%' ELSE true END
   AND   CASE WHEN _importo IS NOT NULL THEN iert.de_importo = _importo ELSE true END
   AND   CASE WHEN _conto IS NOT NULL THEN iert.cod_conto = _conto ELSE true END
   AND   CASE WHEN _codOr1 IS NOT NULL THEN upper(iert.cod_or1) like '%' || upper(_codOr1) || '%' ELSE true END
   AND   CASE WHEN _flagnascosto IS NOT NULL THEN ms.flg_nascosto = _flagnascosto ELSE (ms.flg_nascosto is null or ms.flg_nascosto = false) END
   AND	 CASE WHEN _cod_tipo_dovuto IS NOT NULL AND _is_cod_tipo_dovuto_present THEN iert.tipo_dovuto = _cod_tipo_dovuto
              WHEN _cod_tipo_dovuto IS NULL AND _is_cod_tipo_dovuto_present THEN
                  iert.tipo_dovuto in (SELECT DISTINCT(metd.cod_tipo)
                                      FROM   mygov_operatore_ente_tipo_dovuto as moetd, mygov_ente_tipo_dovuto as metd
                                      WHERE  moetd.mygov_ente_tipo_dovuto_id = metd.mygov_ente_tipo_dovuto_id
                                      AND    moetd.cod_fed_user_id = _cod_fed_user_id 
                                      AND   moetd.flg_attivo = true)
              ELSE true
         END
   AND   CASE WHEN _classificazione_completezza IS NOT NULL THEN iert.classificazione_completezza = _classificazione_completezza ELSE true END
   
   ORDER BY iert.dt_data_regolamento, iert.dt_data_contabile, iert.dt_data_valuta, iert.dt_data_ultimo_aggiornamento
   OFFSET CASE WHEN (_page IS NOT NULL) THEN ((_page - 1)*_size) ELSE 0 END 
   LIMIT CASE WHEN (_size IS NOT NULL) THEN _size ELSE 5 END;
$$;


ALTER FUNCTION public.get_rendicontazione_tesoreria_subset_function(_codice_ipa_ente character varying, _identificativo_flusso_rendicontazione character varying, _identificativo_univoco_regolamento character varying, _dt_data_regolamento_da date, _dt_data_regolamento_a date, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _de_causale_t text, _importo character varying, _conto character varying, _codor1 character varying, _cod_tipo_dovuto character varying, _is_cod_tipo_dovuto_present boolean, _cod_fed_user_id character varying, _flagnascosto boolean, _classificazione_completezza character varying, _page integer, _size integer) OWNER TO mypay4;

--
-- TOC entry 300 (class 1255 OID 32900)
-- Name: get_tesoreria_subset_function(character varying, date, date, date, date, date, date, text, character varying, character varying, character varying, boolean, character varying, character varying, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.get_tesoreria_subset_function(_codice_ipa_ente character varying, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _de_causale_t text, _importo character varying, _conto character varying, _codor1 character varying, _flagnascosto boolean, _classificazione_completezza character varying, _cod_iuv character varying, _cod_iuf character varying, _page integer, _size integer) RETURNS TABLE(codice_ipa_ente character varying, codice_iuv character varying, identificativo_flusso_rendicontazione character varying, dt_data_esecuzione_pagamento date, de_data_esecuzione_pagamento character varying, singolo_importo_commissione_carico_pa character varying, bilancio character varying, cod_conto character varying, dt_data_contabile date, de_data_contabile character varying, dt_data_valuta date, de_data_valuta character varying, num_importo numeric, de_importo character varying, cod_or1 text, de_anno_bolletta character varying, cod_bolletta character varying, cod_id_dominio character varying, dt_ricezione timestamp without time zone, de_data_ricezione character varying, de_anno_documento character varying, cod_documento character varying, de_anno_provvisorio character varying, cod_provvisorio character varying, de_causale_t text, classificazione_completezza character varying, dt_data_ultimo_aggiornamento date, de_data_ultimo_aggiornamento character varying, cod_iuf_key character varying, cod_iuv_key character varying)
    LANGUAGE sql STABLE
    AS $$
   SELECT 
        DISTINCT (tes.codice_ipa_ente),
  tes.codice_iuv,
  tes.identificativo_flusso_rendicontazione,
  tes.dt_data_esecuzione_pagamento,
  tes.de_data_esecuzione_pagamento,
  tes.singolo_importo_commissione_carico_pa,
  tes.bilancio,
  tes.cod_conto,
  tes.dt_data_contabile,
  tes.de_data_contabile,
        tes.dt_data_valuta,
        tes.de_data_valuta,
  tes.num_importo,
  tes.de_importo,
  tes.cod_or1,
  tes.de_anno_bolletta,
  tes.cod_bolletta,
  tes.cod_id_dominio,
  tes.dt_ricezione,
  tes.de_data_ricezione,
  tes.de_anno_documento,
  tes.cod_documento,
  tes.de_anno_provvisorio,
  tes.cod_provvisorio,
  tes.de_causale_t,
  tes.classificazione_completezza,
  tes.dt_data_ultimo_aggiornamento,
  tes.de_data_ultimo_aggiornamento,
  tes.cod_iuf_key,
  tes.cod_iuv_key
   FROM 
      mygov_import_export_rendicontazione_tesoreria as tes 
  LEFT OUTER JOIN (SELECT ment.cod_ipa_ente
                        , mseg.cod_iuf
                        , mseg.cod_iuv
                        , mseg.flg_nascosto
                     FROM mygov_segnalazione as mseg 
                             INNER JOIN   mygov_ente as ment 
                             ON           mseg.mygov_ente_id = ment.mygov_ente_id 
                             WHERE        mseg.flg_attivo = true 
                             AND          mseg.classificazione_completezza = _classificazione_completezza) as ms                              
     ON   ms.cod_ipa_ente = tes.codice_ipa_ente 
    AND (ms.cod_iuf IS NULL 
    AND  tes.cod_iuf_key IS NULL 
    OR   ms.cod_iuf = tes.cod_iuf_key)
    AND (ms.cod_iuv IS NULL 
    AND  tes.cod_iuv_key IS NULL 
    OR   ms.cod_iuv = tes.cod_iuv_key)
     
   WHERE CASE WHEN (_codice_ipa_ente <> '') IS TRUE THEN tes.codice_ipa_ente = _codice_ipa_ente ELSE true END     
   AND   CASE WHEN _dt_data_contabile_da IS NOT NULL THEN tes.dt_data_contabile >= _dt_data_contabile_da ELSE true END                       
   AND   CASE WHEN _dt_data_contabile_a IS NOT NULL THEN tes.dt_data_contabile <= _dt_data_contabile_a ELSE true END         
   AND   CASE WHEN _dt_data_valuta_da IS NOT NULL THEN tes.dt_data_valuta >= _dt_data_valuta_da ELSE true END                      
   AND   CASE WHEN _dt_data_valuta_a IS NOT NULL THEN tes.dt_data_valuta <= _dt_data_valuta_a ELSE true END              
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_da IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento >= _dt_data_ultimo_aggiornamento_da ELSE true END                       
   AND   CASE WHEN _dt_data_ultimo_aggiornamento_a IS NOT NULL THEN tes.dt_data_ultimo_aggiornamento <= _dt_data_ultimo_aggiornamento_a ELSE true END
   AND   CASE WHEN (_de_causale_t <> '') IS TRUE THEN upper(tes.de_causale_t) like '%' || upper(_de_causale_t) || '%' ELSE true END
   AND   CASE WHEN (_importo <> '') IS TRUE THEN tes.de_importo = _importo ELSE true END                   
   AND   CASE WHEN (_conto <> '') IS TRUE THEN tes.cod_conto = _conto ELSE true END                 
   AND   CASE WHEN (_codOr1 <> '') IS TRUE THEN upper(tes.cod_or1) like '%' || upper(_codOr1) || '%' ELSE true END                                                           
   AND   CASE WHEN _flagnascosto IS NOT NULL THEN ms.flg_nascosto = _flagnascosto ELSE (ms.flg_nascosto is null or ms.flg_nascosto = false) END                    
   AND   CASE WHEN (_classificazione_completezza <> '') IS TRUE THEN tes.classificazione_completezza = _classificazione_completezza ELSE true END
   AND   CASE WHEN (_cod_iuv <> '') IS TRUE THEN tes.codice_iuv = _cod_iuv ELSE true END
   AND   CASE WHEN (_cod_iuf <> '') IS TRUE THEN upper(tes.identificativo_flusso_rendicontazione) like upper('%' || _cod_iuf || '%') ELSE true END
      
   ORDER BY tes.dt_data_valuta, tes.identificativo_flusso_rendicontazione, tes.codice_iuv
   OFFSET CASE WHEN (_page IS NOT NULL) THEN ((_page - 1)*_size) ELSE 0 END 
   LIMIT CASE WHEN (_size IS NOT NULL) THEN _size ELSE 5 END;
$$;


ALTER FUNCTION public.get_tesoreria_subset_function(_codice_ipa_ente character varying, _dt_data_contabile_da date, _dt_data_contabile_a date, _dt_data_valuta_da date, _dt_data_valuta_a date, _dt_data_ultimo_aggiornamento_da date, _dt_data_ultimo_aggiornamento_a date, _de_causale_t text, _importo character varying, _conto character varying, _codor1 character varying, _flagnascosto boolean, _classificazione_completezza character varying, _cod_iuv character varying, _cod_iuf character varying, _page integer, _size integer) OWNER TO mypay4;

--
-- TOC entry 301 (class 1255 OID 32902)
-- Name: inserisci_richiesta_ente(bigint); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.inserisci_richiesta_ente(mygov_ente_id_n bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$

    DECLARE
	id_lock 		bigint; 
	pren_recup_cursor	CURSOR FOR 
				 SELECT pren.*
				   FROM mygov_ente_prenotazione pren
				       ,mygov_anagrafica_stato stato        
				  WHERE pren.mygov_anagrafica_stato_id = stato.mygov_anagrafica_stato_id
				   AND (stato.cod_stato = 'ERROR_CHIEDI_STATO_EXPORT' 
				    OR stato.cod_stato = 'ERROR_PRENOTAZIONE'
				    OR stato.cod_stato = 'ERRORE_DOWNLOAD'
				    OR stato.cod_stato = 'ERROR_LOAD')
				   AND stato.de_tipo_stato = 'FLUSSO_EXPORT'
				   AND pren.mygov_ente_id = mygov_ente_id_n 
				   AND pren.mygov_ente_prenotazione_id_rif IS NULL;

	pren_recup_record	mygov_ente_prenotazione%ROWTYPE;	
	new_id			bigint;
	pren_in_corso		bigint;
	max_date_to		timestamp without time zone;
	
    BEGIN

	/* ************  LOCK SU mygov_ente_id TABELLA mygov_ente  ********** */
	SELECT mygov_ente_id 
	  FROM mygov_ente 
	 WHERE mygov_ente_id = mygov_ente_id_n INTO id_lock FOR UPDATE; 
  	/* ******************************************************************* */ 

  	OPEN pren_recup_cursor;
  	LOOP
		FETCH pren_recup_cursor INTO pren_recup_record;
		EXIT WHEN NOT FOUND;
		
		SELECT nextval('mygov_ente_prenotazione_mygov_ente_prenotazione_id_seq') INTO new_id;

		INSERT INTO mygov_ente_prenotazione(
					    mygov_ente_prenotazione_id, "version", mygov_ente_id, mygov_anagrafica_stato_id, 
					    dt_prenota_date_from, dt_prenota_date_to, cod_prenota_identificativo_tipo_dovuto,  
					    dt_creazione, dt_ultima_modifica)
				    VALUES (new_id, 0, mygov_ente_id_n, 
				              (SELECT mygov_anagrafica_stato_id 
				                 FROM mygov_anagrafica_stato
				                WHERE cod_stato = 'INSERITO'
				                  AND de_tipo_stato = 'FLUSSO_EXPORT')
				            , pren_recup_record.dt_prenota_date_from, pren_recup_record.dt_prenota_date_to, 'ALL', 
					    current_timestamp, current_timestamp);

		UPDATE mygov_ente_prenotazione p
		   SET mygov_ente_prenotazione_id_rif = new_id
		 WHERE p.mygov_ente_prenotazione_id = pren_recup_record.mygov_ente_prenotazione_id;
  	END LOOP; 

  	SELECT count(1)
  	  INTO pren_in_corso
	  FROM mygov_ente_prenotazione pren
	      ,mygov_anagrafica_stato stato        
	 WHERE pren.mygov_anagrafica_stato_id = stato.mygov_anagrafica_stato_id
	   AND ((stato.cod_stato = 'INSERITO'
	  AND stato.de_tipo_stato = 'FLUSSO_EXPORT') OR
		(stato.cod_stato = 'IN_CARICO'
	  AND stato.de_tipo_stato = 'ALL'))	  
	  AND dt_prenota_date_to IS NULL
	  AND pren.mygov_ente_id = mygov_ente_id_n;

	IF pren_in_corso = 0 THEN

		SELECT max(pren.dt_prenota_date_to)
		  INTO max_date_to
		  FROM mygov_ente_prenotazione pren
		      ,mygov_anagrafica_stato stato        
		 WHERE pren.mygov_anagrafica_stato_id = stato.mygov_anagrafica_stato_id
		  AND pren.mygov_ente_id = mygov_ente_id_n
		  AND (((stato.cod_stato = 'INSERITO'
		   OR stato.cod_stato = 'PRENOTATO' 
		   OR stato.cod_stato = 'CHIEDI_STATO_EXPORT'
		   OR stato.cod_stato = 'CHIEDI_STATO_EXPORT_NO_FILE'
		   OR stato.cod_stato = 'ATTESA_PRODUZIONE_FILE'
		   OR stato.cod_stato = 'DOWNLOAD'
		   OR stato.cod_stato = 'LOAD')
		  AND stato.de_tipo_stato = 'FLUSSO_EXPORT')	 
		  OR (stato.cod_stato = 'IN_CARICO'
	          AND stato.de_tipo_stato = 'ALL'));	  
		  
		IF max_date_to IS NULL THEN
			INSERT INTO mygov_ente_prenotazione(
					    mygov_ente_prenotazione_id, "version", mygov_ente_id, mygov_anagrafica_stato_id, 
					    dt_prenota_date_from, cod_prenota_identificativo_tipo_dovuto,  
					    dt_creazione, dt_ultima_modifica)
				    VALUES (nextval('mygov_ente_prenotazione_mygov_ente_prenotazione_id_seq'), 0, mygov_ente_id_n, 
					      (SELECT mygov_anagrafica_stato_id 
						 FROM mygov_anagrafica_stato
						WHERE cod_stato = 'INSERITO'
						  AND de_tipo_stato = 'FLUSSO_EXPORT')
					    , '2014-01-01', 'ALL', 
					    current_timestamp, current_timestamp);
		ELSEIF current_timestamp > (max_date_to + interval '1 hour') THEN
			INSERT INTO mygov_ente_prenotazione(
					    mygov_ente_prenotazione_id, "version", mygov_ente_id, mygov_anagrafica_stato_id, 
					    dt_prenota_date_from, cod_prenota_identificativo_tipo_dovuto,  
					    dt_creazione, dt_ultima_modifica)
				    VALUES (nextval('mygov_ente_prenotazione_mygov_ente_prenotazione_id_seq'), 0, mygov_ente_id_n, 
					      (SELECT mygov_anagrafica_stato_id 
						 FROM mygov_anagrafica_stato
						WHERE cod_stato = 'INSERITO'
						  AND de_tipo_stato = 'FLUSSO_EXPORT')
					    , max_date_to, 'ALL', 
					    current_timestamp, current_timestamp);
		END IF;
	END IF;     

    END;
$$;


ALTER FUNCTION public.inserisci_richiesta_ente(mygov_ente_id_n bigint) OWNER TO mypay4;

--
-- TOC entry 302 (class 1255 OID 32903)
-- Name: inserisci_richiesta_flusso_rendicontazione(bigint); Type: FUNCTION; Schema: public; Owner: mypay4
--

CREATE FUNCTION public.inserisci_richiesta_flusso_rendicontazione(mygov_ente_id_n bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$
    DECLARE
        pren_cursor		CURSOR FOR 
				SELECT pren.*
				FROM mygov_prenotazione_flusso_rendicontazione_ente pren
				    ,mygov_anagrafica_stato stato		    
				WHERE pren.mygov_ente_id = mygov_ente_id_n
				  AND pren.mygov_anagrafica_stato_id = stato.mygov_anagrafica_stato_id
				  AND ((stato.cod_stato <> 'ERROR_CHIEDI_ELENCO_FLUSSI'
				  AND stato.de_tipo_stato = 'PRENOTAZIONE_RENDICONTAZIONE')
				  OR (stato.cod_stato = 'IN_CARICO'
				  AND stato.de_tipo_stato = 'ALL'))
				ORDER BY pren.dt_date_from;
				  
        pren_record		mygov_prenotazione_flusso_rendicontazione_ente%ROWTYPE; 

        date_prec		date;
        differenza		integer; 
        iterator		float4 := 0; 
        lost_init		boolean := false;
        date_lost_to		date;
        id_lock 		bigint;   
    
    BEGIN

	/* ************  LOCK SU mygov_ente_id TABELLA mygov_ente  ********** */
	SELECT mygov_ente_id 
	  FROM mygov_ente 
	 WHERE mygov_ente_id = mygov_ente_id_n INTO id_lock FOR UPDATE;  
	/* ******************************************************************* */  

	OPEN pren_cursor;

	LOOP
		FETCH pren_cursor INTO pren_record;
		EXIT WHEN NOT FOUND;
		iterator := iterator + 1;
		IF iterator = 1 AND pren_record.dt_date_from IS NOT NULL AND pren_record.dt_date_from > '2014-01-01'::date THEN
			lost_init := true;
			date_lost_to := pren_record.dt_date_from - 1;
		END IF;
		
		IF date_prec IS NULL THEN
			date_prec := pren_record.dt_date_to;
			CONTINUE;
		END IF;
		differenza := pren_record.dt_date_from - (date_prec + 1);
		IF differenza > 0 THEN
			INSERT INTO mygov_prenotazione_flusso_rendicontazione_ente(
					    mygov_prenotazione_flusso_rendicontazione_ente_id, "version", mygov_ente_id, 
					    mygov_anagrafica_stato_id, mygov_tipo_flusso_id, dt_date_from, 
					    dt_date_to, dt_creazione, dt_ultima_modifica)
				    VALUES (nextval('mygov_pren_flusso_rend_ente_mygov_pren_flusso_rend_ente_seq'), 0, mygov_ente_id_n, 
				              (SELECT mygov_anagrafica_stato_id 
				                 FROM mygov_anagrafica_stato
				                WHERE cod_stato = 'INSERITO'
				                  AND de_tipo_stato = 'PRENOTAZIONE_RENDICONTAZIONE')
				            , ( SELECT mygov_tipo_flusso_id
						  FROM mygov_tipo_flusso
						 WHERE cod_tipo = 'R'), date_prec + 1, pren_record.dt_date_from - 1, 
					    current_timestamp, current_timestamp);
		END IF;
		date_prec := pren_record.dt_date_to;		
	END LOOP;

	CLOSE pren_cursor;
	IF date_prec IS NULL THEN
		INSERT INTO mygov_prenotazione_flusso_rendicontazione_ente(
				    mygov_prenotazione_flusso_rendicontazione_ente_id, "version", mygov_ente_id, 
					    mygov_anagrafica_stato_id, mygov_tipo_flusso_id, dt_date_from, 
					    dt_date_to, dt_creazione, dt_ultima_modifica)
			    VALUES (nextval('mygov_pren_flusso_rend_ente_mygov_pren_flusso_rend_ente_seq'), 0, mygov_ente_id_n, 
				      (SELECT mygov_anagrafica_stato_id 
					 FROM mygov_anagrafica_stato
					WHERE cod_stato = 'INSERITO'
					  AND de_tipo_stato = 'PRENOTAZIONE_RENDICONTAZIONE'), 
					  ( SELECT mygov_tipo_flusso_id
						  FROM mygov_tipo_flusso
						 WHERE cod_tipo = 'R')
				    , '2014-01-01', current_date - 1,  
				    current_timestamp, current_timestamp);
	END IF;

	IF lost_init THEN
		INSERT INTO mygov_prenotazione_flusso_rendicontazione_ente(
				    mygov_prenotazione_flusso_rendicontazione_ente_id, "version", mygov_ente_id, 
					    mygov_anagrafica_stato_id, mygov_tipo_flusso_id, dt_date_from, 
					    dt_date_to, dt_creazione, dt_ultima_modifica)
			    VALUES (nextval('mygov_pren_flusso_rend_ente_mygov_pren_flusso_rend_ente_seq'), 0, mygov_ente_id_n, 
				      (SELECT mygov_anagrafica_stato_id 
					 FROM mygov_anagrafica_stato
					WHERE cod_stato = 'INSERITO'
					  AND de_tipo_stato = 'PRENOTAZIONE_RENDICONTAZIONE'), 
					  ( SELECT mygov_tipo_flusso_id
						  FROM mygov_tipo_flusso
						 WHERE cod_tipo = 'R')
				    , '2014-01-01', date_lost_to,  
				    current_timestamp, current_timestamp);
	END IF;	

	IF date_prec < current_date - 1 THEN
		INSERT INTO mygov_prenotazione_flusso_rendicontazione_ente(
				    mygov_prenotazione_flusso_rendicontazione_ente_id, "version", mygov_ente_id, 
					    mygov_anagrafica_stato_id, mygov_tipo_flusso_id, dt_date_from, 
					    dt_date_to, dt_creazione, dt_ultima_modifica)
			    VALUES (nextval('mygov_pren_flusso_rend_ente_mygov_pren_flusso_rend_ente_seq'), 0, mygov_ente_id_n, 
				      (SELECT mygov_anagrafica_stato_id 
					 FROM mygov_anagrafica_stato
					WHERE cod_stato = 'INSERITO'
					  AND de_tipo_stato = 'PRENOTAZIONE_RENDICONTAZIONE'), 
					  ( SELECT mygov_tipo_flusso_id
						  FROM mygov_tipo_flusso
						 WHERE cod_tipo = 'R')
				    , date_prec + 1, current_date - 1, 
				    current_timestamp, current_timestamp);
	END IF;

    END;
$$;


ALTER FUNCTION public.inserisci_richiesta_flusso_rendicontazione(mygov_ente_id_n bigint) OWNER TO mypay4;

--
-- TOC entry 198 (class 1259 OID 32904)
-- Name: mygov_accertamento; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_accertamento (
    mygov_accertamento_id bigint NOT NULL,
    mygov_ente_tipo_dovuto_id bigint NOT NULL,
    mygov_anagrafica_stato_id bigint NOT NULL,
    mygov_utente_id bigint NOT NULL,
    de_nome_accertamento character varying(255) NOT NULL,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    printed boolean DEFAULT false NOT NULL
);


ALTER TABLE public.mygov_accertamento OWNER TO mypay4;

--
-- TOC entry 199 (class 1259 OID 32908)
-- Name: mygov_accertamento_dettaglio; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_accertamento_dettaglio (
    mygov_accertamento_dettaglio_id bigint NOT NULL,
    mygov_accertamento_id bigint NOT NULL,
    cod_ipa_ente character varying(35) NOT NULL,
    cod_tipo_dovuto character varying(64) NOT NULL,
    cod_iud character varying(35) NOT NULL,
    cod_iuv character varying(35) NOT NULL,
    cod_ufficio character varying(64) NOT NULL,
    cod_capitolo character varying(64) NOT NULL,
    cod_accertamento character varying(64) NOT NULL,
    num_importo numeric(17,2) NOT NULL,
    flg_importo_inserito boolean DEFAULT true NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    dt_data_inserimento timestamp without time zone NOT NULL,
    mygov_utente_id bigint NOT NULL
);


ALTER TABLE public.mygov_accertamento_dettaglio OWNER TO mypay4;

--
-- TOC entry 200 (class 1259 OID 32912)
-- Name: mygov_accertamento_dettaglio_mygov_accertamento_dett_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_accertamento_dettaglio_mygov_accertamento_dett_id_seq
    START WITH 35
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_accertamento_dettaglio_mygov_accertamento_dett_id_seq OWNER TO mypay4;

--
-- TOC entry 201 (class 1259 OID 32914)
-- Name: mygov_accertamento_mygov_accertamento_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_accertamento_mygov_accertamento_id_seq
    START WITH 6
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_accertamento_mygov_accertamento_id_seq OWNER TO mypay4;

--
-- TOC entry 202 (class 1259 OID 32916)
-- Name: mygov_anag_uff_cap_acc_mygov_anag_uff_cap_acc_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_anag_uff_cap_acc_mygov_anag_uff_cap_acc_id_seq
    START WITH 35
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_anag_uff_cap_acc_mygov_anag_uff_cap_acc_id_seq OWNER TO mypay4;

--
-- TOC entry 203 (class 1259 OID 32918)
-- Name: mygov_anagrafica_stato; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_anagrafica_stato (
    mygov_anagrafica_stato_id bigint NOT NULL,
    cod_stato character varying(80) NOT NULL,
    de_stato character varying(100) NOT NULL,
    de_tipo_stato character varying(80) NOT NULL,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL
);


ALTER TABLE public.mygov_anagrafica_stato OWNER TO mypay4;

--
-- TOC entry 204 (class 1259 OID 32921)
-- Name: mygov_anagrafica_stato_mygov_anagrafica_stato_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_anagrafica_stato_mygov_anagrafica_stato_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_anagrafica_stato_mygov_anagrafica_stato_id_seq OWNER TO mypay4;

--
-- TOC entry 2700 (class 0 OID 0)
-- Dependencies: 204
-- Name: mygov_anagrafica_stato_mygov_anagrafica_stato_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mypay4
--

ALTER SEQUENCE public.mygov_anagrafica_stato_mygov_anagrafica_stato_id_seq OWNED BY public.mygov_anagrafica_stato.mygov_anagrafica_stato_id;


--
-- TOC entry 205 (class 1259 OID 32923)
-- Name: mygov_anagrafica_uff_cap_acc; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_anagrafica_uff_cap_acc (
    mygov_anagrafica_uff_cap_acc_id bigint NOT NULL,
    mygov_ente_id bigint NOT NULL,
    cod_tipo_dovuto character varying(64),
    cod_ufficio character varying(64) NOT NULL,
    de_ufficio character varying(512) NOT NULL,
    flg_attivo boolean NOT NULL,
    cod_capitolo character varying(64) NOT NULL,
    de_capitolo character varying(512) NOT NULL,
    de_anno_esercizio character varying(4) NOT NULL,
    cod_accertamento character varying(64) NOT NULL,
    de_accertamento character varying(512) NOT NULL,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL
);


ALTER TABLE public.mygov_anagrafica_uff_cap_acc OWNER TO mypay4;

--
-- TOC entry 206 (class 1259 OID 32929)
-- Name: mygov_classificazione_completezza; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_classificazione_completezza (
    mygov_classificazione_codice character varying(20) NOT NULL,
    mygov_classificazione_descrizione character varying(256)
);


ALTER TABLE public.mygov_classificazione_completezza OWNER TO mypay4;

--
-- TOC entry 207 (class 1259 OID 32932)
-- Name: mygov_ente; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_ente (
    mygov_ente_id bigint NOT NULL,
    cod_ipa_ente character varying(80) NOT NULL,
    codice_fiscale_ente character varying(11) NOT NULL,
    de_nome_ente character varying(100) NOT NULL,
    email_amministratore character varying(50),
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    mybox_client_key character varying(256),
    mybox_client_secret character varying(256),
    num_giorni_pagamento_presunti integer DEFAULT 3 NOT NULL,
    de_password character varying(15),
    flg_pagati boolean DEFAULT false NOT NULL,
    flg_tesoreria boolean DEFAULT false NOT NULL,
    de_logo_ente text
);


ALTER TABLE public.mygov_ente OWNER TO mypay4;

--
-- TOC entry 208 (class 1259 OID 32941)
-- Name: mygov_ente_flusso_rendicon_mygov_ente_flusso_rendicon_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_ente_flusso_rendicon_mygov_ente_flusso_rendicon_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_ente_flusso_rendicon_mygov_ente_flusso_rendicon_seq OWNER TO mypay4;

--
-- TOC entry 209 (class 1259 OID 32943)
-- Name: mygov_ente_flusso_rendicontazione; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_ente_flusso_rendicontazione (
    mygov_ente_flusso_rendicontazione_id bigint NOT NULL,
    version integer NOT NULL,
    mygov_ente_id bigint NOT NULL,
    mygov_anagrafica_stato_id bigint NOT NULL,
    mygov_prenotazione_flusso_rendicontazione_ente_id bigint NOT NULL,
    cod_lista_ipa_ente character varying(80) NOT NULL,
    flg_lista_tipo_flusso character(1) NOT NULL,
    identificativo_lista_psp character varying(35) NOT NULL,
    dt_lista_date_from date NOT NULL,
    dt_lista_date_to date NOT NULL,
    cod_lista_identificativo_flusso character varying(256) NOT NULL,
    dt_lista_data_ora_flusso timestamp without time zone NOT NULL,
    de_lista_fault_fault_code character varying(256),
    de_lista_fault_fault_string character varying(256),
    de_lista_fault_id character varying(256),
    de_lista_fault_description character varying(1024),
    num_lista_fault_serial integer,
    cod_chiedi_ipa_ente character varying(80),
    flg_chiedi_tipo_flusso character(1),
    identificativo_chiedi_psp character varying(35),
    cod_chiedi_identificativo_flusso character varying(256),
    dt_chiedi_data_ora_flusso timestamp without time zone,
    de_chiedi_fault_fault_code character varying(256),
    de_chiedi_fault_fault_string character varying(256),
    de_chiedi_fault_id character varying(256),
    de_chiedi_fault_description character varying(1024),
    num_chiedi_fault_serial integer,
    de_chiedi_stato character varying(64),
    de_chiedi_download_url text,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL
);


ALTER TABLE public.mygov_ente_flusso_rendicontazione OWNER TO mypay4;

--
-- TOC entry 210 (class 1259 OID 32949)
-- Name: mygov_ente_mygov_ente_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_ente_mygov_ente_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_ente_mygov_ente_id_seq OWNER TO mypay4;

--
-- TOC entry 2701 (class 0 OID 0)
-- Dependencies: 210
-- Name: mygov_ente_mygov_ente_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mypay4
--

ALTER SEQUENCE public.mygov_ente_mygov_ente_id_seq OWNED BY public.mygov_ente.mygov_ente_id;


--
-- TOC entry 211 (class 1259 OID 32951)
-- Name: mygov_ente_prenotazione; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_ente_prenotazione (
    mygov_ente_prenotazione_id bigint NOT NULL,
    version integer NOT NULL,
    mygov_ente_id bigint NOT NULL,
    mygov_anagrafica_stato_id bigint NOT NULL,
    dt_prenota_date_from timestamp(0) without time zone NOT NULL,
    dt_prenota_date_to timestamp(0) without time zone,
    cod_prenota_identificativo_tipo_dovuto character varying(64) NOT NULL,
    de_prenota_fault_fault_code character varying(256),
    de_prenota_fault_fault_string character varying(256),
    de_prenota_fault_id character varying(256),
    de_prenota_fault_description character varying(1024),
    de_prenota_fault_serial integer,
    request_token character varying(64),
    de_chiedi_stato_fault_fault_code character varying(256),
    de_chiedi_stato_fault_fault_string character varying(256),
    de_chiedi_stato_fault_id character varying(256),
    de_chiedi_stato_fault_description character varying(1024),
    de_chiedi_stato_fault_serial integer,
    de_chiedi_stato_stato character varying(64),
    de_chiedi_stato_download_url text,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    mygov_ente_prenotazione_id_rif bigint
);


ALTER TABLE public.mygov_ente_prenotazione OWNER TO mypay4;

--
-- TOC entry 212 (class 1259 OID 32957)
-- Name: mygov_ente_prenotazione_mygov_ente_prenotazione_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_ente_prenotazione_mygov_ente_prenotazione_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_ente_prenotazione_mygov_ente_prenotazione_id_seq OWNER TO mypay4;

--
-- TOC entry 2702 (class 0 OID 0)
-- Dependencies: 212
-- Name: mygov_ente_prenotazione_mygov_ente_prenotazione_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mypay4
--

ALTER SEQUENCE public.mygov_ente_prenotazione_mygov_ente_prenotazione_id_seq OWNED BY public.mygov_ente_prenotazione.mygov_ente_prenotazione_id;


--
-- TOC entry 213 (class 1259 OID 32959)
-- Name: mygov_ente_tipo_dovuto; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_ente_tipo_dovuto (
    mygov_ente_tipo_dovuto_id bigint NOT NULL,
    mygov_ente_id bigint NOT NULL,
    cod_tipo character varying(64),
    de_tipo character varying(256),
    esterno boolean DEFAULT false NOT NULL
);


ALTER TABLE public.mygov_ente_tipo_dovuto OWNER TO mypay4;

--
-- TOC entry 214 (class 1259 OID 32963)
-- Name: mygov_ente_tipo_dovuto_mygov_ente_tipo_dovuto_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_ente_tipo_dovuto_mygov_ente_tipo_dovuto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_ente_tipo_dovuto_mygov_ente_tipo_dovuto_id_seq OWNER TO mypay4;

--
-- TOC entry 2703 (class 0 OID 0)
-- Dependencies: 214
-- Name: mygov_ente_tipo_dovuto_mygov_ente_tipo_dovuto_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mypay4
--

ALTER SEQUENCE public.mygov_ente_tipo_dovuto_mygov_ente_tipo_dovuto_id_seq OWNED BY public.mygov_ente_tipo_dovuto.mygov_ente_tipo_dovuto_id;


--
-- TOC entry 215 (class 1259 OID 32965)
-- Name: mygov_entepsp; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_entepsp (
    mygov_entepsp_id bigint NOT NULL,
    mygov_ente_id bigint NOT NULL,
    identificativo_psp character varying(35) NOT NULL
);


ALTER TABLE public.mygov_entepsp OWNER TO mypay4;

--
-- TOC entry 216 (class 1259 OID 32968)
-- Name: mygov_entepsp_flusso_rendicontazione; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_entepsp_flusso_rendicontazione (
    mygov_entepsp_flusso_rendicontazione_id bigint NOT NULL,
    version integer NOT NULL,
    mygov_entepsp_id bigint NOT NULL,
    mygov_anagrafica_stato_id bigint NOT NULL,
    mygov_prenotazione_flusso_rendicontazione_id bigint NOT NULL,
    cod_lista_ipa_ente character varying(80) NOT NULL,
    flg_lista_tipo_flusso character(1) NOT NULL,
    identificativo_lista_psp character varying(35) NOT NULL,
    dt_lista_date_from date NOT NULL,
    dt_lista_date_to date NOT NULL,
    cod_lista_identificativo_flusso character varying(256) NOT NULL,
    dt_lista_data_ora_flusso timestamp without time zone NOT NULL,
    de_lista_fault_fault_code character varying(256),
    de_lista_fault_fault_string character varying(256),
    de_lista_fault_id character varying(256),
    de_lista_fault_description character varying(1024),
    num_lista_fault_serial integer,
    cod_chiedi_ipa_ente character varying(80),
    flg_chiedi_tipo_flusso character(1),
    identificativo_chiedi_psp character varying(35),
    cod_chiedi_identificativo_flusso character varying(256),
    dt_chiedi_data_ora_flusso timestamp without time zone,
    de_chiedi_fault_fault_code character varying(256),
    de_chiedi_fault_fault_string character varying(256),
    de_chiedi_fault_id character varying(256),
    de_chiedi_fault_description character varying(1024),
    num_chiedi_fault_serial integer,
    de_chiedi_stato character varying(64),
    de_chiedi_download_url text,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL
);


ALTER TABLE public.mygov_entepsp_flusso_rendicontazione OWNER TO mypay4;

--
-- TOC entry 217 (class 1259 OID 32974)
-- Name: mygov_entepsp_flusso_rendicon_mygov_entepsp_flusso_rendicon_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_entepsp_flusso_rendicon_mygov_entepsp_flusso_rendicon_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_entepsp_flusso_rendicon_mygov_entepsp_flusso_rendicon_seq OWNER TO mypay4;

--
-- TOC entry 2704 (class 0 OID 0)
-- Dependencies: 217
-- Name: mygov_entepsp_flusso_rendicon_mygov_entepsp_flusso_rendicon_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mypay4
--

ALTER SEQUENCE public.mygov_entepsp_flusso_rendicon_mygov_entepsp_flusso_rendicon_seq OWNED BY public.mygov_entepsp_flusso_rendicontazione.mygov_entepsp_flusso_rendicontazione_id;


--
-- TOC entry 218 (class 1259 OID 32976)
-- Name: mygov_entepsp_mygov_entepsp_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_entepsp_mygov_entepsp_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_entepsp_mygov_entepsp_id_seq OWNER TO mypay4;

--
-- TOC entry 2705 (class 0 OID 0)
-- Dependencies: 218
-- Name: mygov_entepsp_mygov_entepsp_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mypay4
--

ALTER SEQUENCE public.mygov_entepsp_mygov_entepsp_id_seq OWNED BY public.mygov_entepsp.mygov_entepsp_id;


--
-- TOC entry 219 (class 1259 OID 32978)
-- Name: mygov_export_rendicontazione_completa; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_export_rendicontazione_completa (
    mygov_ente_id_e bigint,
    mygov_manage_flusso_id_e bigint,
    de_nome_flusso_e character varying(50),
    num_riga_flusso_e integer,
    cod_iud_e character varying(35),
    cod_rp_silinviarp_id_univoco_versamento_e character varying(35),
    de_e_versione_oggetto_e character varying(16),
    cod_e_dom_id_dominio_e character varying(35),
    cod_e_dom_id_stazione_richiedente_e character varying(35),
    cod_e_id_messaggio_ricevuta_e character varying(35),
    dt_e_data_ora_messaggio_ricevuta_e timestamp without time zone,
    cod_e_riferimento_messaggio_richiesta_e character varying(35),
    dt_e_riferimento_data_richiesta_e date,
    cod_e_istit_att_id_univ_att_tipo_id_univoco_e character(1),
    cod_e_istit_att_id_univ_att_codice_id_univoco_e character varying(35),
    de_e_istit_att_denominazione_attestante_e character varying(70),
    cod_e_istit_att_codice_unit_oper_attestante_e character varying(35),
    de_e_istit_att_denom_unit_oper_attestante_e character varying(70),
    de_e_istit_att_indirizzo_attestante_e character varying(70),
    de_e_istit_att_civico_attestante_e character varying(16),
    cod_e_istit_att_cap_attestante_e character varying(16),
    de_e_istit_att_localita_attestante_e character varying(35),
    de_e_istit_att_provincia_attestante_e character varying(35),
    cod_e_istit_att_nazione_attestante_e character varying(2),
    cod_e_ente_benef_id_univ_benef_tipo_id_univoco_e character(1),
    cod_e_ente_benef_id_univ_benef_codice_id_univoco_e character varying(35),
    de_e_ente_benef_denominazione_beneficiario_e character varying(70),
    cod_e_ente_benef_codice_unit_oper_beneficiario_e character varying(35),
    de_e_ente_benef_denom_unit_oper_beneficiario_e character varying(70),
    de_e_ente_benef_indirizzo_beneficiario_e character varying(70),
    de_e_ente_benef_civico_beneficiario_e character varying(16),
    cod_e_ente_benef_cap_beneficiario_e character varying(16),
    de_e_ente_benef_localita_beneficiario_e character varying(35),
    de_e_ente_benef_provincia_beneficiario_e character varying(35),
    cod_e_ente_benef_nazione_beneficiario_e character varying(2),
    cod_e_sogg_vers_id_univ_vers_tipo_id_univoco_e character(1),
    cod_e_sogg_vers_id_univ_vers_codice_id_univoco_e character varying(35),
    cod_e_sogg_vers_anagrafica_versante_e character varying(70),
    de_e_sogg_vers_indirizzo_versante_e character varying(70),
    de_e_sogg_vers_civico_versante_e character varying(16),
    cod_e_sogg_vers_cap_versante_e character varying(16),
    de_e_sogg_vers_localita_versante_e character varying(35),
    de_e_sogg_vers_provincia_versante_e character varying(35),
    cod_e_sogg_vers_nazione_versante_e character varying(2),
    de_e_sogg_vers_email_versante_e character varying(256),
    cod_e_sogg_pag_id_univ_pag_tipo_id_univoco_e character(1),
    cod_e_sogg_pag_id_univ_pag_codice_id_univoco_e character varying(35),
    cod_e_sogg_pag_anagrafica_pagatore_e character varying(70),
    de_e_sogg_pag_indirizzo_pagatore_e character varying(70),
    de_e_sogg_pag_civico_pagatore_e character varying(16),
    cod_e_sogg_pag_cap_pagatore_e character varying(16),
    de_e_sogg_pag_localita_pagatore_e character varying(35),
    de_e_sogg_pag_provincia_pagatore_e character varying(35),
    cod_e_sogg_pag_nazione_pagatore_e character varying(2),
    de_e_sogg_pag_email_pagatore_e character varying(256),
    cod_e_dati_pag_codice_esito_pagamento_e character(1),
    num_e_dati_pag_importo_totale_pagato_e numeric(12,2),
    cod_e_dati_pag_id_univoco_versamento_e character varying(35),
    cod_e_dati_pag_codice_contesto_pagamento_e character varying(35),
    num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e numeric(12,2),
    de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento_e character varying(35),
    dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento_e date,
    cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss_e character varying(35),
    de_e_dati_pag_dati_sing_pag_causale_versamento_e character varying(1024),
    de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione_e character varying(140),
    cod_tipo_dovuto_e character varying(64),
    dt_acquisizione_e date,
    mygov_ente_id_r bigint,
    mygov_manage_flusso_id_r bigint,
    versione_oggetto_r character varying(16),
    cod_identificativo_flusso_r character varying(35),
    dt_data_ora_flusso_r timestamp without time zone,
    cod_identificativo_univoco_regolamento_r character varying(35),
    dt_data_regolamento_r date,
    cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco_r character(1),
    cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco_r character varying(35),
    de_ist_mitt_denominazione_mittente_r character varying(70),
    cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco_r character(1),
    cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco_r character varying(35),
    de_ist_ricev_denominazione_ricevente_r character varying(70),
    num_numero_totale_pagamenti_r numeric(15,0),
    num_importo_totale_pagamenti_r numeric(18,2),
    cod_dati_sing_pagam_identificativo_univoco_versamento_r character varying(35),
    cod_dati_sing_pagam_identificativo_univoco_riscossione_r character varying(35),
    num_dati_sing_pagam_singolo_importo_pagato_r numeric(12,2),
    cod_dati_sing_pagam_codice_esito_singolo_pagamento_r character varying(1),
    dt_dati_sing_pagam_data_esito_singolo_pagamento_r date,
    dt_acquisizione_r date,
    classificazione_completezza character varying(20)
);


ALTER TABLE public.mygov_export_rendicontazione_completa OWNER TO mypay4;

--
-- TOC entry 220 (class 1259 OID 32984)
-- Name: mygov_flusso_export; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_flusso_export (
    version integer NOT NULL,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    mygov_ente_id bigint NOT NULL,
    mygov_manage_flusso_id bigint NOT NULL,
    de_nome_flusso character varying(50),
    num_riga_flusso integer,
    cod_iud character varying(35),
    cod_rp_silinviarp_id_univoco_versamento character varying(35) NOT NULL,
    de_e_versione_oggetto character varying(16),
    cod_e_dom_id_dominio character varying(35),
    cod_e_dom_id_stazione_richiedente character varying(35),
    cod_e_id_messaggio_ricevuta character varying(35),
    dt_e_data_ora_messaggio_ricevuta timestamp without time zone,
    cod_e_riferimento_messaggio_richiesta character varying(35),
    dt_e_riferimento_data_richiesta date,
    cod_e_istit_att_id_univ_att_tipo_id_univoco character(1),
    cod_e_istit_att_id_univ_att_codice_id_univoco character varying(35),
    de_e_istit_att_denominazione_attestante character varying(70),
    cod_e_istit_att_codice_unit_oper_attestante character varying(35),
    de_e_istit_att_denom_unit_oper_attestante character varying(70),
    de_e_istit_att_indirizzo_attestante character varying(70),
    de_e_istit_att_civico_attestante character varying(16),
    cod_e_istit_att_cap_attestante character varying(16),
    de_e_istit_att_localita_attestante character varying(35),
    de_e_istit_att_provincia_attestante character varying(35),
    cod_e_istit_att_nazione_attestante character varying(2),
    cod_e_ente_benef_id_univ_benef_tipo_id_univoco character(1),
    cod_e_ente_benef_id_univ_benef_codice_id_univoco character varying(35),
    de_e_ente_benef_denominazione_beneficiario character varying(70),
    cod_e_ente_benef_codice_unit_oper_beneficiario character varying(35),
    de_e_ente_benef_denom_unit_oper_beneficiario character varying(70),
    de_e_ente_benef_indirizzo_beneficiario character varying(70),
    de_e_ente_benef_civico_beneficiario character varying(16),
    cod_e_ente_benef_cap_beneficiario character varying(16),
    de_e_ente_benef_localita_beneficiario character varying(35),
    de_e_ente_benef_provincia_beneficiario character varying(35),
    cod_e_ente_benef_nazione_beneficiario character varying(2),
    cod_e_sogg_vers_id_univ_vers_tipo_id_univoco character(1),
    cod_e_sogg_vers_id_univ_vers_codice_id_univoco character varying(35),
    cod_e_sogg_vers_anagrafica_versante character varying(70),
    de_e_sogg_vers_indirizzo_versante character varying(70),
    de_e_sogg_vers_civico_versante character varying(16),
    cod_e_sogg_vers_cap_versante character varying(16),
    de_e_sogg_vers_localita_versante character varying(35),
    de_e_sogg_vers_provincia_versante character varying(35),
    cod_e_sogg_vers_nazione_versante character varying(2),
    de_e_sogg_vers_email_versante character varying(256),
    cod_e_sogg_pag_id_univ_pag_tipo_id_univoco character(1),
    cod_e_sogg_pag_id_univ_pag_codice_id_univoco character varying(35),
    cod_e_sogg_pag_anagrafica_pagatore character varying(70),
    de_e_sogg_pag_indirizzo_pagatore character varying(70),
    de_e_sogg_pag_civico_pagatore character varying(16),
    cod_e_sogg_pag_cap_pagatore character varying(16),
    de_e_sogg_pag_localita_pagatore character varying(35),
    de_e_sogg_pag_provincia_pagatore character varying(35),
    cod_e_sogg_pag_nazione_pagatore character varying(2),
    de_e_sogg_pag_email_pagatore character varying(256),
    cod_e_dati_pag_codice_esito_pagamento character(1),
    num_e_dati_pag_importo_totale_pagato numeric(12,2),
    cod_e_dati_pag_id_univoco_versamento character varying(35),
    cod_e_dati_pag_codice_contesto_pagamento character varying(35),
    num_e_dati_pag_dati_sing_pag_singolo_importo_pagato numeric(12,2),
    de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento character varying(35),
    dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento date,
    cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss character varying(35) NOT NULL,
    de_e_dati_pag_dati_sing_pag_causale_versamento character varying(1024),
    de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione character varying(140),
    cod_tipo_dovuto character varying(64),
    dt_acquisizione date NOT NULL,
    indice_dati_singolo_pagamento integer NOT NULL,
    de_importa_dovuto_esito text,
    de_importa_dovuto_fault_code text,
    de_importa_dovuto_fault_string text,
    de_importa_dovuto_fault_id text,
    de_importa_dovuto_fault_description text,
    num_importa_dovuto_fault_serial integer,
    bilancio character varying(4096),
    id_intermediario_pa character varying(20) DEFAULT '80007580279'::character varying NOT NULL,
    id_stazione_intermediario_pa character varying(20) DEFAULT '80007580279_01'::character varying NOT NULL
);


ALTER TABLE public.mygov_flusso_export OWNER TO mypay4;

--
-- TOC entry 221 (class 1259 OID 32992)
-- Name: mygov_flusso_import; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_flusso_import (
    version integer NOT NULL,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    mygov_ente_id bigint NOT NULL,
    mygov_manage_flusso_id bigint NOT NULL,
    cod_iud character varying(35) NOT NULL,
    cod_rp_silinviarp_id_univoco_versamento character varying(35) NOT NULL,
    cod_rp_sogg_pag_id_univ_pag_tipo_id_univoco character(1) NOT NULL,
    cod_rp_sogg_pag_id_univ_pag_codice_id_univoco character varying(35) NOT NULL,
    de_rp_sogg_pag_anagrafica_pagatore character varying(70) NOT NULL,
    de_rp_sogg_pag_indirizzo_pagatore character varying(70),
    de_rp_sogg_pag_civico_pagatore character varying(16),
    cod_rp_sogg_pag_cap_pagatore character varying(16),
    de_rp_sogg_pag_localita_pagatore character varying(35),
    de_rp_sogg_pag_provincia_pagatore character varying(2),
    cod_rp_sogg_pag_nazione_pagatore character varying(2),
    de_rp_sogg_pag_email_pagatore character varying(256),
    dt_rp_dati_vers_data_esecuzione_pagamento date NOT NULL,
    cod_rp_dati_vers_tipo_versamento character varying(32) NOT NULL,
    num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento numeric(12,2) NOT NULL,
    num_rp_dati_vers_dati_sing_vers_commissione_carico_pa numeric(12,2),
    de_rp_dati_vers_dati_sing_vers_causale_versamento character varying(140) NOT NULL,
    de_rp_dati_vers_dati_sing_vers_dati_specifici_riscossione character varying(140) NOT NULL,
    cod_tipo_dovuto character varying(64) NOT NULL,
    bilancio character varying(4096),
    dt_acquisizione date NOT NULL
);


ALTER TABLE public.mygov_flusso_import OWNER TO mypay4;

--
-- TOC entry 222 (class 1259 OID 32998)
-- Name: mygov_flusso_rendicontazione; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_flusso_rendicontazione (
    version integer NOT NULL,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    mygov_ente_id bigint NOT NULL,
    mygov_manage_flusso_id bigint NOT NULL,
    identificativo_psp character varying(35) NOT NULL,
    versione_oggetto character varying(16) NOT NULL,
    cod_identificativo_flusso character varying(35) NOT NULL,
    dt_data_ora_flusso timestamp without time zone NOT NULL,
    cod_identificativo_univoco_regolamento character varying(35) NOT NULL,
    dt_data_regolamento date NOT NULL,
    cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco character(1) NOT NULL,
    cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco character varying(35) NOT NULL,
    de_ist_mitt_denominazione_mittente character varying(70),
    cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco character(1),
    cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco character varying(35),
    de_ist_ricev_denominazione_ricevente character varying(70),
    num_numero_totale_pagamenti numeric(15,0) NOT NULL,
    num_importo_totale_pagamenti numeric(18,2) NOT NULL,
    cod_dati_sing_pagam_identificativo_univoco_versamento character varying(35) NOT NULL,
    cod_dati_sing_pagam_identificativo_univoco_riscossione character varying(35) NOT NULL,
    num_dati_sing_pagam_singolo_importo_pagato numeric(12,2) NOT NULL,
    cod_dati_sing_pagam_codice_esito_singolo_pagamento character varying(1) NOT NULL,
    dt_dati_sing_pagam_data_esito_singolo_pagamento date NOT NULL,
    dt_acquisizione date NOT NULL,
    indice_dati_singolo_pagamento integer NOT NULL,
    codice_bic_banca_di_riversamento character varying(35)
);


ALTER TABLE public.mygov_flusso_rendicontazione OWNER TO mypay4;

--
-- TOC entry 223 (class 1259 OID 33001)
-- Name: mygov_flusso_tesoreria; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_flusso_tesoreria (
    mygov_flusso_tesoreria_id bigint NOT NULL,
    de_anno_bolletta character varying(4) NOT NULL,
    cod_bolletta character varying(7) NOT NULL,
    cod_conto character(7),
    cod_id_dominio character(7),
    cod_tipo_movimento character(3),
    cod_causale character(3),
    de_causale character varying(2000) NOT NULL,
    num_ip_bolletta numeric(17,2) NOT NULL,
    dt_bolletta date NOT NULL,
    dt_ricezione timestamp without time zone,
    de_anno_documento character(4),
    cod_documento character(7),
    cod_bollo character(6),
    de_cognome character(30) NOT NULL,
    de_nome character(30),
    de_via character varying(50),
    de_cap character varying(5),
    de_citta character varying(40),
    cod_codice_fiscale character(16),
    cod_partita_iva character(12),
    cod_abi character(5),
    cod_cab character(5),
    cod_conto_anagrafica character(50),
    de_ae_provvisorio character(4),
    cod_provvisorio character(6),
    cod_iban character varying(34),
    cod_tipo_conto character(1),
    cod_processo character(10),
    cod_pg_esecuzione character(4),
    cod_pg_trasferimento character(4),
    num_pg_processo numeric(17,0),
    dt_data_valuta_regione date,
    mygov_ente_id bigint NOT NULL,
    cod_id_univoco_flusso character varying(35),
    cod_id_univoco_versamento character varying(35),
    dt_creazione timestamp without time zone,
    dt_ultima_modifica timestamp without time zone,
    flg_regolarizzata boolean DEFAULT false NOT NULL,
    mygov_manage_flusso_id bigint,
    dt_effettiva_sospeso date,
    codice_gestionale_provvisorio character varying(10),
    end_to_end_id character varying(35)
);


ALTER TABLE public.mygov_flusso_tesoreria OWNER TO mypay4;

--
-- TOC entry 224 (class 1259 OID 33008)
-- Name: mygov_flusso_tesoreria_cod_bolletta_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.mygov_flusso_tesoreria_cod_bolletta_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_flusso_tesoreria_cod_bolletta_seq OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 33010)
-- Name: mygov_flusso_tesoreria_iuf; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_flusso_tesoreria_iuf (
    version integer NOT NULL,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    mygov_ente_id bigint NOT NULL,
    mygov_manage_flusso_id bigint NOT NULL,
    cod_abi character(5),
    cod_cab character(5),
    cod_conto character(12),
    cod_divisa character varying(10),
    dt_data_contabile date,
    dt_data_valuta date,
    num_importo numeric(12,2),
    cod_segno character(1),
    de_causale text,
    cod_numero_assegno text,
    cod_riferimento_banca text,
    cod_riferimento_cliente text,
    dt_data_ordine date,
    de_descrizione_ordinante text,
    cod_bi2 text NOT NULL,
    cod_be1 text,
    cod_ib1 text,
    cod_ib2 text,
    cod_ib4 text,
    cod_tid text,
    cod_dte text,
    cod_dtn text,
    cod_eri text,
    cod_im2 text,
    cod_ma2 text,
    cod_ri3 text,
    cod_or1 text,
    cod_sc2 text,
    cod_tr1 text,
    cod_sec text,
    cod_ior text,
    cod_id_univoco_flusso character varying(35) NOT NULL,
    dt_acquisizione date NOT NULL
);


ALTER TABLE public.mygov_flusso_tesoreria_iuf OWNER TO mypay4;

--
-- TOC entry 226 (class 1259 OID 33016)
-- Name: mygov_flusso_tesoreria_iuv; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_flusso_tesoreria_iuv (
    version integer NOT NULL,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    mygov_ente_id bigint NOT NULL,
    mygov_manage_flusso_id bigint NOT NULL,
    cod_abi character(5),
    cod_cab character(5),
    cod_conto character(12),
    cod_divisa character varying(10),
    dt_data_contabile date,
    dt_data_valuta date,
    num_importo numeric(12,2),
    cod_segno character(1),
    de_causale text,
    cod_numero_assegno text,
    cod_riferimento_banca text,
    cod_riferimento_cliente text,
    dt_data_ordine date,
    de_descrizione_ordinante text,
    cod_bi2 text,
    cod_be1 text,
    cod_ib1 text,
    cod_ib2 text,
    cod_ib4 text,
    cod_tid text,
    cod_dte text,
    cod_dtn text,
    cod_eri text,
    cod_im2 text,
    cod_ma2 text,
    cod_ri3 text,
    cod_or1 text,
    cod_sc2 text,
    cod_tr1 text,
    cod_sec text,
    cod_ior text,
    cod_id_univoco_versamento character varying(35) NOT NULL,
    dt_acquisizione date NOT NULL
);


ALTER TABLE public.mygov_flusso_tesoreria_iuv OWNER TO mypay4;

--
-- TOC entry 227 (class 1259 OID 33022)
-- Name: mygov_flusso_tesoreria_mygov_flusso_tesoreria_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_flusso_tesoreria_mygov_flusso_tesoreria_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_flusso_tesoreria_mygov_flusso_tesoreria_id_seq OWNER TO mypay4;

--
-- TOC entry 228 (class 1259 OID 33024)
-- Name: mygov_import_export_rendicontazione_tesoreria_completa; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_import_export_rendicontazione_tesoreria_completa (
    mygov_ente_id bigint,
    codice_iuv character varying(35),
    identificativo_univoco_riscossione character varying(35),
    identificativo_flusso_rendicontazione character varying(35),
    mygov_ente_id_i bigint,
    mygov_manage_flusso_id_i bigint,
    cod_iud_i character varying(35),
    cod_rp_silinviarp_id_univoco_versamento_i character varying(35),
    cod_rp_sogg_pag_id_univ_pag_tipo_id_univoco_i character(1),
    cod_rp_sogg_pag_id_univ_pag_codice_id_univoco_i character varying(35),
    de_rp_sogg_pag_anagrafica_pagatore_i character varying(70),
    de_rp_sogg_pag_indirizzo_pagatore_i character varying(70),
    de_rp_sogg_pag_civico_pagatore_i character varying(16),
    cod_rp_sogg_pag_cap_pagatore_i character varying(16),
    de_rp_sogg_pag_localita_pagatore_i character varying(35),
    de_rp_sogg_pag_provincia_pagatore_i character varying(2),
    cod_rp_sogg_pag_nazione_pagatore_i character varying(2),
    de_rp_sogg_pag_email_pagatore_i character varying(256),
    dt_rp_dati_vers_data_esecuzione_pagamento_i date,
    cod_rp_dati_vers_tipo_versamento_i character varying(32),
    num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento_i numeric(12,2),
    num_rp_dati_vers_dati_sing_vers_commissione_carico_pa_i numeric(12,2),
    de_rp_dati_vers_dati_sing_vers_causale_versamento_i character varying(140),
    de_rp_dati_vers_dati_sing_vers_dati_specifici_riscossione_i character varying(140),
    cod_tipo_dovuto_i character varying(64),
    bilancio_i character varying(4096),
    dt_acquisizione_i date,
    mygov_ente_id_e bigint,
    mygov_manage_flusso_id_e bigint,
    de_nome_flusso_e character varying(50),
    num_riga_flusso_e integer,
    cod_iud_e character varying(35),
    cod_rp_silinviarp_id_univoco_versamento_e character varying(35),
    de_e_versione_oggetto_e character varying(16),
    cod_e_dom_id_dominio_e character varying(35),
    cod_e_dom_id_stazione_richiedente_e character varying(35),
    cod_e_id_messaggio_ricevuta_e character varying(35),
    dt_e_data_ora_messaggio_ricevuta_e timestamp without time zone,
    cod_e_riferimento_messaggio_richiesta_e character varying(35),
    dt_e_riferimento_data_richiesta_e date,
    cod_e_istit_att_id_univ_att_tipo_id_univoco_e character(1),
    cod_e_istit_att_id_univ_att_codice_id_univoco_e character varying(35),
    de_e_istit_att_denominazione_attestante_e character varying(70),
    cod_e_istit_att_codice_unit_oper_attestante_e character varying(35),
    de_e_istit_att_denom_unit_oper_attestante_e character varying(70),
    de_e_istit_att_indirizzo_attestante_e character varying(70),
    de_e_istit_att_civico_attestante_e character varying(16),
    cod_e_istit_att_cap_attestante_e character varying(16),
    de_e_istit_att_localita_attestante_e character varying(35),
    de_e_istit_att_provincia_attestante_e character varying(35),
    cod_e_istit_att_nazione_attestante_e character varying(2),
    cod_e_ente_benef_id_univ_benef_tipo_id_univoco_e character(1),
    cod_e_ente_benef_id_univ_benef_codice_id_univoco_e character varying(35),
    de_e_ente_benef_denominazione_beneficiario_e character varying(70),
    cod_e_ente_benef_codice_unit_oper_beneficiario_e character varying(35),
    de_e_ente_benef_denom_unit_oper_beneficiario_e character varying(70),
    de_e_ente_benef_indirizzo_beneficiario_e character varying(70),
    de_e_ente_benef_civico_beneficiario_e character varying(16),
    cod_e_ente_benef_cap_beneficiario_e character varying(16),
    de_e_ente_benef_localita_beneficiario_e character varying(35),
    de_e_ente_benef_provincia_beneficiario_e character varying(35),
    cod_e_ente_benef_nazione_beneficiario_e character varying(2),
    cod_e_sogg_vers_id_univ_vers_tipo_id_univoco_e character(1),
    cod_e_sogg_vers_id_univ_vers_codice_id_univoco_e character varying(35),
    cod_e_sogg_vers_anagrafica_versante_e character varying(70),
    de_e_sogg_vers_indirizzo_versante_e character varying(70),
    de_e_sogg_vers_civico_versante_e character varying(16),
    cod_e_sogg_vers_cap_versante_e character varying(16),
    de_e_sogg_vers_localita_versante_e character varying(35),
    de_e_sogg_vers_provincia_versante_e character varying(35),
    cod_e_sogg_vers_nazione_versante_e character varying(2),
    de_e_sogg_vers_email_versante_e character varying(256),
    cod_e_sogg_pag_id_univ_pag_tipo_id_univoco_e character(1),
    cod_e_sogg_pag_id_univ_pag_codice_id_univoco_e character varying(35),
    cod_e_sogg_pag_anagrafica_pagatore_e character varying(70),
    de_e_sogg_pag_indirizzo_pagatore_e character varying(70),
    de_e_sogg_pag_civico_pagatore_e character varying(16),
    cod_e_sogg_pag_cap_pagatore_e character varying(16),
    de_e_sogg_pag_localita_pagatore_e character varying(35),
    de_e_sogg_pag_provincia_pagatore_e character varying(35),
    cod_e_sogg_pag_nazione_pagatore_e character varying(2),
    de_e_sogg_pag_email_pagatore_e character varying(256),
    cod_e_dati_pag_codice_esito_pagamento_e character(1),
    num_e_dati_pag_importo_totale_pagato_e numeric(12,2),
    cod_e_dati_pag_id_univoco_versamento_e character varying(35),
    cod_e_dati_pag_codice_contesto_pagamento_e character varying(35),
    num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e numeric(12,2),
    de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento_e character varying(35),
    dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento_e date,
    cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss_e character varying(35),
    de_e_dati_pag_dati_sing_pag_causale_versamento_e character varying(1024),
    de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione_e character varying(140),
    cod_tipo_dovuto_e character varying(64),
    dt_acquisizione_e date,
    indice_dati_singolo_pagamento_e integer,
    bilancio_e character varying(4096),
    mygov_ente_id_r bigint,
    mygov_manage_flusso_id_r bigint,
    versione_oggetto_r character varying(16),
    cod_identificativo_flusso_r character varying(35),
    dt_data_ora_flusso_r timestamp without time zone,
    cod_identificativo_univoco_regolamento_r character varying(35),
    dt_data_regolamento_r date,
    cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco_r character(1),
    cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco_r character varying(35),
    de_ist_mitt_denominazione_mittente_r character varying(70),
    cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco_r character(1),
    cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco_r character varying(35),
    de_ist_ricev_denominazione_ricevente_r character varying(70),
    num_numero_totale_pagamenti_r numeric(15,0),
    num_importo_totale_pagamenti_r numeric(18,2),
    cod_dati_sing_pagam_identificativo_univoco_versamento_r character varying(35),
    cod_dati_sing_pagam_identificativo_univoco_riscossione_r character varying(35),
    num_dati_sing_pagam_singolo_importo_pagato_r numeric(12,2),
    cod_dati_sing_pagam_codice_esito_singolo_pagamento_r character varying(1),
    dt_dati_sing_pagam_data_esito_singolo_pagamento_r date,
    dt_acquisizione_r date,
    indice_dati_singolo_pagamento_r integer,
    mygov_ente_id_t bigint,
    mygov_manage_flusso_id_t bigint,
    cod_abi_t character(5),
    cod_cab_t character(5),
    cod_conto_t character(12),
    cod_divisa_t character varying(10),
    dt_data_contabile_t date,
    dt_data_valuta_t date,
    num_importo_t numeric(12,2),
    cod_segno_t character(1),
    de_causale_t text,
    cod_numero_assegno_t text,
    cod_riferimento_banca_t text,
    cod_riferimento_cliente_t text,
    dt_data_ordine_t date,
    de_descrizione_ordinante_t text,
    cod_bi2_t text,
    cod_be1_t text,
    cod_ib1_t text,
    cod_ib2_t text,
    cod_ib4_t text,
    cod_tid_t text,
    cod_dte_t text,
    cod_dtn_t text,
    cod_eri_t text,
    cod_im2_t text,
    cod_ma2_t text,
    cod_ri3_t text,
    cod_or1_t text,
    cod_sc2_t text,
    cod_tr1_t text,
    cod_sec_t text,
    cod_ior_t text,
    cod_id_univoco_flusso_t character varying(35),
    cod_id_univoco_versamento_t character varying(35),
    dt_acquisizione_t date,
    de_anno_bolletta_t character varying(4),
    cod_bolletta_t character varying(7),
    cod_id_dominio_t character varying(7),
    dt_ricezione_t timestamp without time zone,
    de_anno_documento_t character varying(4),
    cod_documento_t character varying(7),
    de_anno_provvisorio_t character varying(4),
    cod_provvisorio_t character varying(6),
    classificazione_completezza character varying(20),
    dt_data_ultimo_aggiornamento date,
    dt_effettiva_sospeso_t date,
    codice_gestionale_provvisorio_t character varying(10)
);


ALTER TABLE public.mygov_import_export_rendicontazione_tesoreria_completa OWNER TO mypay4;

--
-- TOC entry 229 (class 1259 OID 33030)
-- Name: mygov_info_flusso_poste_web; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_info_flusso_poste_web (
    mygov_info_flusso_poste_web_id bigint NOT NULL,
    version integer NOT NULL,
    mygov_manage_flusso_id bigint NOT NULL,
    cod_identificativo_univoco_regolamento character varying(35),
    dt_data_regolamento date,
    num_importo_totale_pagamenti numeric(18,2),
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL
);


ALTER TABLE public.mygov_info_flusso_poste_web OWNER TO mypay4;

--
-- TOC entry 230 (class 1259 OID 33033)
-- Name: mygov_info_flusso_poste_web_mygov_info_flusso_poste_web_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_info_flusso_poste_web_mygov_info_flusso_poste_web_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_info_flusso_poste_web_mygov_info_flusso_poste_web_id_seq OWNER TO mypay4;

--
-- TOC entry 2706 (class 0 OID 0)
-- Dependencies: 230
-- Name: mygov_info_flusso_poste_web_mygov_info_flusso_poste_web_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mypay4
--

ALTER SEQUENCE public.mygov_info_flusso_poste_web_mygov_info_flusso_poste_web_id_seq OWNED BY public.mygov_info_flusso_poste_web.mygov_info_flusso_poste_web_id;


--
-- TOC entry 231 (class 1259 OID 33035)
-- Name: mygov_info_mapping_tesoreria; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_info_mapping_tesoreria (
    mygov_info_mapping_tesoreria_id bigint NOT NULL,
    mygov_manage_flusso_id bigint NOT NULL,
    pos_de_anno_bolletta integer,
    pos_cod_bolletta integer,
    pos_dt_contabile integer,
    pos_de_denominazione integer,
    pos_de_causale integer,
    pos_num_importo integer,
    pos_dt_valuta integer,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL
);


ALTER TABLE public.mygov_info_mapping_tesoreria OWNER TO mypay4;

--
-- TOC entry 232 (class 1259 OID 33038)
-- Name: mygov_info_mapping_tesoreria_mygov_info_mapping_tes_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_info_mapping_tesoreria_mygov_info_mapping_tes_id_seq
    START WITH 35
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_info_mapping_tesoreria_mygov_info_mapping_tes_id_seq OWNER TO mypay4;

--
-- TOC entry 233 (class 1259 OID 33040)
-- Name: mygov_manage_flusso; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_manage_flusso (
    mygov_manage_flusso_id bigint NOT NULL,
    version integer NOT NULL,
    mygov_ente_id bigint NOT NULL,
    identificativo_psp character varying(35),
    cod_identificativo_flusso character varying(35),
    dt_data_ora_flusso timestamp without time zone,
    mygov_tipo_flusso_id bigint NOT NULL,
    mygov_utente_id bigint,
    mygov_anagrafica_stato_id bigint NOT NULL,
    de_percorso_file character varying(256),
    de_nome_file character varying(256),
    num_dimensione_file_scaricato bigint,
    cod_request_token text,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    cod_provenienza_file character varying(10) DEFAULT 'batch'::character varying,
    id_chiave_multitabella bigint,
    de_nome_file_scarti character varying(256),
    cod_errore character varying(256),
    num_righe_totali numeric(10,0),
    num_righe_importate_correttamente numeric(10,0)
);


ALTER TABLE public.mygov_manage_flusso OWNER TO mypay4;

--
-- TOC entry 234 (class 1259 OID 33047)
-- Name: mygov_manage_flusso_mygov_manage_flusso_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_manage_flusso_mygov_manage_flusso_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_manage_flusso_mygov_manage_flusso_id_seq OWNER TO mypay4;

--
-- TOC entry 2707 (class 0 OID 0)
-- Dependencies: 234
-- Name: mygov_manage_flusso_mygov_manage_flusso_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mypay4
--

ALTER SEQUENCE public.mygov_manage_flusso_mygov_manage_flusso_id_seq OWNED BY public.mygov_manage_flusso.mygov_manage_flusso_id;


--
-- TOC entry 235 (class 1259 OID 33049)
-- Name: mygov_op_ente_tipo_dovuto_mygov_op_ente_tipo_dovuto_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_op_ente_tipo_dovuto_mygov_op_ente_tipo_dovuto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_op_ente_tipo_dovuto_mygov_op_ente_tipo_dovuto_id_seq OWNER TO mypay4;

--
-- TOC entry 236 (class 1259 OID 33051)
-- Name: mygov_operatore; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mygov_operatore (
    mygov_operatore_id bigint NOT NULL,
    ruolo character varying(64),
    cod_fed_user_id character varying(128) NOT NULL,
    cod_ipa_ente character varying(80) NOT NULL
);


ALTER TABLE public.mygov_operatore OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 33054)
-- Name: mygov_operatore_ente_tipo_dovuto; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_operatore_ente_tipo_dovuto (
    mygov_operatore_ente_tipo_dovuto_id bigint NOT NULL,
    mygov_ente_tipo_dovuto_id bigint,
    flg_attivo boolean NOT NULL,
    mygov_operatore_id bigint NOT NULL
);


ALTER TABLE public.mygov_operatore_ente_tipo_dovuto OWNER TO mypay4;

--
-- TOC entry 238 (class 1259 OID 33057)
-- Name: mygov_operatore_ente_tipo_dovuto_backup; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mygov_operatore_ente_tipo_dovuto_backup (
    mygov_operatore_ente_tipo_dovuto_id bigint,
    cod_fed_user_id character varying(128),
    mygov_ente_tipo_dovuto_id bigint,
    flg_attivo boolean
);


ALTER TABLE public.mygov_operatore_ente_tipo_dovuto_backup OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 33060)
-- Name: mygov_operatore_mygov_operatore_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.mygov_operatore_mygov_operatore_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_operatore_mygov_operatore_id_seq OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 33062)
-- Name: mygov_pren_flus_ric_mygov_pren_flus_ric_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_pren_flus_ric_mygov_pren_flus_ric_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_pren_flus_ric_mygov_pren_flus_ric_id_seq OWNER TO mypay4;

--
-- TOC entry 241 (class 1259 OID 33064)
-- Name: mygov_pren_flusso_rend_ente_mygov_pren_flusso_rend_ente_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_pren_flusso_rend_ente_mygov_pren_flusso_rend_ente_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_pren_flusso_rend_ente_mygov_pren_flusso_rend_ente_seq OWNER TO mypay4;

--
-- TOC entry 242 (class 1259 OID 33066)
-- Name: mygov_prenotazione_flusso_rendicontazione; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_prenotazione_flusso_rendicontazione (
    mygov_prenotazione_flusso_rendicontazione_id bigint NOT NULL,
    version integer NOT NULL,
    mygov_entepsp_id bigint NOT NULL,
    mygov_anagrafica_stato_id bigint NOT NULL,
    mygov_tipo_flusso_id bigint NOT NULL,
    dt_date_from date NOT NULL,
    dt_date_to date NOT NULL,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL
);


ALTER TABLE public.mygov_prenotazione_flusso_rendicontazione OWNER TO mypay4;

--
-- TOC entry 243 (class 1259 OID 33069)
-- Name: mygov_prenotazione_flusso_ren_mygov_prenotazione_flusso_ren_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_prenotazione_flusso_ren_mygov_prenotazione_flusso_ren_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_prenotazione_flusso_ren_mygov_prenotazione_flusso_ren_seq OWNER TO mypay4;

--
-- TOC entry 2708 (class 0 OID 0)
-- Dependencies: 243
-- Name: mygov_prenotazione_flusso_ren_mygov_prenotazione_flusso_ren_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mypay4
--

ALTER SEQUENCE public.mygov_prenotazione_flusso_ren_mygov_prenotazione_flusso_ren_seq OWNED BY public.mygov_prenotazione_flusso_rendicontazione.mygov_prenotazione_flusso_rendicontazione_id;


--
-- TOC entry 244 (class 1259 OID 33071)
-- Name: mygov_prenotazione_flusso_rendicontazione_ente; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_prenotazione_flusso_rendicontazione_ente (
    mygov_prenotazione_flusso_rendicontazione_ente_id bigint NOT NULL,
    version integer NOT NULL,
    mygov_ente_id bigint NOT NULL,
    mygov_anagrafica_stato_id bigint NOT NULL,
    mygov_tipo_flusso_id bigint NOT NULL,
    dt_date_from date NOT NULL,
    dt_date_to date NOT NULL,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL
);


ALTER TABLE public.mygov_prenotazione_flusso_rendicontazione_ente OWNER TO mypay4;

--
-- TOC entry 245 (class 1259 OID 33074)
-- Name: mygov_prenotazione_flusso_riconciliazione; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_prenotazione_flusso_riconciliazione (
    mygov_prenotazione_flusso_riconciliazione_id bigint NOT NULL,
    version integer NOT NULL,
    mygov_ente_id bigint NOT NULL,
    mygov_anagrafica_stato_id bigint NOT NULL,
    mygov_utente_id bigint NOT NULL,
    cod_request_token text NOT NULL,
    de_nome_file_generato character varying(256),
    num_dimensione_file_generato bigint,
    cod_codice_classificazione text NOT NULL,
    de_tipo_dovuto text,
    cod_id_univoco_versamento text,
    cod_id_univoco_rendicontazione text,
    dt_data_ultimo_aggiornamento_da date,
    dt_data_ultimo_aggiornamento_a date,
    dt_data_esecuzione_da date,
    dt_data_esecuzione_a date,
    dt_data_esito_da date,
    dt_data_esito_a date,
    dt_data_regolamento_da date,
    dt_data_regolamento_a date,
    dt_data_contabile_da date,
    dt_data_contabile_a date,
    dt_data_valuta_da date,
    dt_data_valuta_a date,
    cod_id_univoco_dovuto character varying(35),
    cod_id_univoco_riscossione character varying(35),
    cod_id_univoco_pagatore character varying(35),
    de_anagrafica_pagatore character varying(70),
    cod_id_univoco_versante character varying(35),
    de_anagrafica_versante character varying(70),
    de_denominazione_attestante character varying(70),
    de_ordinante character varying(1024),
    cod_id_regolamento character varying(35),
    cod_conto_tesoreria character varying(12),
    de_importo_tesoreria character varying(35),
    de_causale character varying(1024),
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    versione_tracciato character varying(35) NOT NULL
);


ALTER TABLE public.mygov_prenotazione_flusso_riconciliazione OWNER TO mypay4;

--
-- TOC entry 246 (class 1259 OID 33080)
-- Name: mygov_segnalazione; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_segnalazione (
    mygov_segnalazione_id bigint NOT NULL,
    mygov_ente_id bigint NOT NULL,
    mygov_utente_id bigint NOT NULL,
    classificazione_completezza character varying(20) NOT NULL,
    cod_iud character varying(35),
    cod_iuv character varying(35),
    cod_iuf character varying(35),
    de_nota text NOT NULL,
    flg_nascosto boolean DEFAULT false NOT NULL,
    flg_attivo boolean,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL,
    version integer NOT NULL,
    CONSTRAINT mygov_segnalazione_not_null_iuv_or_iuf CHECK (((cod_iud IS NOT NULL) OR (cod_iuv IS NOT NULL) OR (cod_iuf IS NOT NULL)))
);


ALTER TABLE public.mygov_segnalazione OWNER TO mypay4;

--
-- TOC entry 247 (class 1259 OID 33088)
-- Name: mygov_segnalazione_mygov_segnalazione_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_segnalazione_mygov_segnalazione_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_segnalazione_mygov_segnalazione_id_seq OWNER TO mypay4;

--
-- TOC entry 248 (class 1259 OID 33090)
-- Name: mygov_tipo_flusso; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_tipo_flusso (
    mygov_tipo_flusso_id bigint NOT NULL,
    version integer NOT NULL,
    cod_tipo character(1) NOT NULL,
    de_tipo character varying(100) NOT NULL,
    dt_creazione timestamp without time zone NOT NULL,
    dt_ultima_modifica timestamp without time zone NOT NULL
);


ALTER TABLE public.mygov_tipo_flusso OWNER TO mypay4;

--
-- TOC entry 249 (class 1259 OID 33093)
-- Name: mygov_tipo_flusso_mygov_tipo_flusso_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_tipo_flusso_mygov_tipo_flusso_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_tipo_flusso_mygov_tipo_flusso_id_seq OWNER TO mypay4;

--
-- TOC entry 2709 (class 0 OID 0)
-- Dependencies: 249
-- Name: mygov_tipo_flusso_mygov_tipo_flusso_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mypay4
--

ALTER SEQUENCE public.mygov_tipo_flusso_mygov_tipo_flusso_id_seq OWNED BY public.mygov_tipo_flusso.mygov_tipo_flusso_id;


--
-- TOC entry 250 (class 1259 OID 33095)
-- Name: mygov_utente; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.mygov_utente (
    mygov_utente_id bigint NOT NULL,
    version integer NOT NULL,
    cod_fed_user_id character varying(128) NOT NULL,
    cod_codice_fiscale_utente character varying(16) NOT NULL,
    de_email_address character varying(256) NOT NULL,
    de_firstname character varying(64),
    de_lastname character varying(64),
    dt_ultimo_login timestamp without time zone NOT NULL
);


ALTER TABLE public.mygov_utente OWNER TO mypay4;

--
-- TOC entry 251 (class 1259 OID 33101)
-- Name: mygov_utente_mygov_utente_id_seq; Type: SEQUENCE; Schema: public; Owner: mypay4
--

CREATE SEQUENCE public.mygov_utente_mygov_utente_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mygov_utente_mygov_utente_id_seq OWNER TO mypay4;

--
-- TOC entry 2710 (class 0 OID 0)
-- Dependencies: 251
-- Name: mygov_utente_mygov_utente_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mypay4
--

ALTER SEQUENCE public.mygov_utente_mygov_utente_id_seq OWNED BY public.mygov_utente.mygov_utente_id;


--
-- TOC entry 252 (class 1259 OID 33103)
-- Name: t_mygov_export_rendicontazione_tesoreria; Type: TABLE; Schema: public; Owner: mypay4
--

CREATE TABLE public.t_mygov_export_rendicontazione_tesoreria (
    codice_ipa_ente character varying(80),
    nome_flusso_import_ente character varying(50),
    riga_flusso_import_ente character varying(12),
    codice_iud character varying(35),
    codice_iuv character varying(35),
    versione_oggetto character varying(16),
    identificativo_dominio character varying(35),
    identificativo_stazione_richiedente character varying(35),
    identificativo_messaggio_ricevuta character varying(35),
    data_ora_messaggio_ricevuta character varying(19),
    riferimento_messaggio_richiesta character varying(35),
    riferimento_data_richiesta character varying(10),
    tipo_identificativo_univoco_attestante character varying(1),
    codice_identificativo_univoco_attestante character varying(35),
    denominazione_attestante character varying(70),
    codice_unit_oper_attestante character varying(35),
    denom_unit_oper_attestante character varying(70),
    indirizzo_attestante character varying(70),
    civico_attestante character varying(16),
    cap_attestante character varying(16),
    localita_attestante character varying(35),
    provincia_attestante character varying(35),
    nazione_attestante character varying(2),
    tipo_identificativo_univoco_beneficiario character varying(1),
    codice_identificativo_univoco_beneficiario character varying(35),
    denominazione_beneficiario character varying(70),
    codice_unit_oper_beneficiario character varying(35),
    denom_unit_oper_beneficiario character varying(70),
    indirizzo_beneficiario character varying(70),
    civico_beneficiario character varying(16),
    cap_beneficiario character varying(16),
    localita_beneficiario character varying(35),
    provincia_beneficiario character varying(2),
    nazione_beneficiario character varying(2),
    tipo_identificativo_univoco_versante character varying(1),
    codice_identificativo_univoco_versante character varying(35),
    anagrafica_versante character varying(70),
    indirizzo_versante character varying(70),
    civico_versante character varying(16),
    cap_versante character varying(16),
    localita_versante character varying(35),
    provincia_versante character varying(35),
    nazione_versante character varying(2),
    email_versante character varying(256),
    tipo_identificativo_univoco_pagatore character varying(1),
    codice_identificativo_univoco_pagatore character varying(35),
    anagrafica_pagatore character varying(70),
    indirizzo_pagatore character varying(70),
    civico_pagatore character varying(16),
    cap_pagatore character varying(16),
    localita_pagatore character varying(35),
    provincia_pagatore character varying(35),
    nazione_pagatore character varying(2),
    email_pagatore character varying(256),
    codice_esito_pagamento character varying(1),
    importo_totale_pagato character varying(15),
    identificativo_univoco_versamento character varying(35),
    codice_contesto_pagamento character varying(35),
    singolo_importo_pagato character varying(15),
    esito_singolo_pagamento character varying(35),
    dt_data_esito_singolo_pagamento date,
    de_data_esito_singolo_pagamento character varying(10),
    identificativo_univoco_riscossione character varying(35),
    causale_versamento character varying(140),
    dati_specifici_riscossione character varying(140),
    tipo_dovuto character varying(64),
    identificativo_flusso_rendicontazione character varying(35),
    data_ora_flusso_rendicontazione character varying(19),
    identificativo_univoco_regolamento character varying(35),
    dt_data_regolamento date,
    de_data_regolamento character varying,
    numero_totale_pagamenti character varying(15),
    importo_totale_pagamenti character varying(21),
    data_acquisizione character varying(10),
    cod_conto character varying(12),
    dt_data_contabile date,
    de_data_contabile character varying(10),
    dt_data_valuta date,
    de_data_valuta character varying(10),
    num_importo numeric(12,2),
    de_importo character varying(15),
    cod_or1 text,
    verifica_totale character varying(3),
    classificazione_completezza character varying(17)
);


ALTER TABLE public.t_mygov_export_rendicontazione_tesoreria OWNER TO mypay4;

--
-- TOC entry 253 (class 1259 OID 33109)
-- Name: v_mygov_export_rendicontazione_completa; Type: VIEW; Schema: public; Owner: mypay4
--

CREATE VIEW public.v_mygov_export_rendicontazione_completa AS
 SELECT export.mygov_ente_id AS mygov_ente_id_e,
    export.mygov_manage_flusso_id AS mygov_manage_flusso_id_e,
    export.de_nome_flusso AS de_nome_flusso_e,
    export.num_riga_flusso AS num_riga_flusso_e,
    export.cod_iud AS cod_iud_e,
    export.cod_rp_silinviarp_id_univoco_versamento AS cod_rp_silinviarp_id_univoco_versamento_e,
    export.de_e_versione_oggetto AS de_e_versione_oggetto_e,
    export.cod_e_dom_id_dominio AS cod_e_dom_id_dominio_e,
    export.cod_e_dom_id_stazione_richiedente AS cod_e_dom_id_stazione_richiedente_e,
    export.cod_e_id_messaggio_ricevuta AS cod_e_id_messaggio_ricevuta_e,
    export.dt_e_data_ora_messaggio_ricevuta AS dt_e_data_ora_messaggio_ricevuta_e,
    export.cod_e_riferimento_messaggio_richiesta AS cod_e_riferimento_messaggio_richiesta_e,
    export.dt_e_riferimento_data_richiesta AS dt_e_riferimento_data_richiesta_e,
    export.cod_e_istit_att_id_univ_att_tipo_id_univoco AS cod_e_istit_att_id_univ_att_tipo_id_univoco_e,
    export.cod_e_istit_att_id_univ_att_codice_id_univoco AS cod_e_istit_att_id_univ_att_codice_id_univoco_e,
    export.de_e_istit_att_denominazione_attestante AS de_e_istit_att_denominazione_attestante_e,
    export.cod_e_istit_att_codice_unit_oper_attestante AS cod_e_istit_att_codice_unit_oper_attestante_e,
    export.de_e_istit_att_denom_unit_oper_attestante AS de_e_istit_att_denom_unit_oper_attestante_e,
    export.de_e_istit_att_indirizzo_attestante AS de_e_istit_att_indirizzo_attestante_e,
    export.de_e_istit_att_civico_attestante AS de_e_istit_att_civico_attestante_e,
    export.cod_e_istit_att_cap_attestante AS cod_e_istit_att_cap_attestante_e,
    export.de_e_istit_att_localita_attestante AS de_e_istit_att_localita_attestante_e,
    export.de_e_istit_att_provincia_attestante AS de_e_istit_att_provincia_attestante_e,
    export.cod_e_istit_att_nazione_attestante AS cod_e_istit_att_nazione_attestante_e,
    export.cod_e_ente_benef_id_univ_benef_tipo_id_univoco AS cod_e_ente_benef_id_univ_benef_tipo_id_univoco_e,
    export.cod_e_ente_benef_id_univ_benef_codice_id_univoco AS cod_e_ente_benef_id_univ_benef_codice_id_univoco_e,
    export.de_e_ente_benef_denominazione_beneficiario AS de_e_ente_benef_denominazione_beneficiario_e,
    export.cod_e_ente_benef_codice_unit_oper_beneficiario AS cod_e_ente_benef_codice_unit_oper_beneficiario_e,
    export.de_e_ente_benef_denom_unit_oper_beneficiario AS de_e_ente_benef_denom_unit_oper_beneficiario_e,
    export.de_e_ente_benef_indirizzo_beneficiario AS de_e_ente_benef_indirizzo_beneficiario_e,
    export.de_e_ente_benef_civico_beneficiario AS de_e_ente_benef_civico_beneficiario_e,
    export.cod_e_ente_benef_cap_beneficiario AS cod_e_ente_benef_cap_beneficiario_e,
    export.de_e_ente_benef_localita_beneficiario AS de_e_ente_benef_localita_beneficiario_e,
    export.de_e_ente_benef_provincia_beneficiario AS de_e_ente_benef_provincia_beneficiario_e,
    export.cod_e_ente_benef_nazione_beneficiario AS cod_e_ente_benef_nazione_beneficiario_e,
    export.cod_e_sogg_vers_id_univ_vers_tipo_id_univoco AS cod_e_sogg_vers_id_univ_vers_tipo_id_univoco_e,
    export.cod_e_sogg_vers_id_univ_vers_codice_id_univoco AS cod_e_sogg_vers_id_univ_vers_codice_id_univoco_e,
    export.cod_e_sogg_vers_anagrafica_versante AS cod_e_sogg_vers_anagrafica_versante_e,
    export.de_e_sogg_vers_indirizzo_versante AS de_e_sogg_vers_indirizzo_versante_e,
    export.de_e_sogg_vers_civico_versante AS de_e_sogg_vers_civico_versante_e,
    export.cod_e_sogg_vers_cap_versante AS cod_e_sogg_vers_cap_versante_e,
    export.de_e_sogg_vers_localita_versante AS de_e_sogg_vers_localita_versante_e,
    export.de_e_sogg_vers_provincia_versante AS de_e_sogg_vers_provincia_versante_e,
    export.cod_e_sogg_vers_nazione_versante AS cod_e_sogg_vers_nazione_versante_e,
    export.de_e_sogg_vers_email_versante AS de_e_sogg_vers_email_versante_e,
    export.cod_e_sogg_pag_id_univ_pag_tipo_id_univoco AS cod_e_sogg_pag_id_univ_pag_tipo_id_univoco_e,
    export.cod_e_sogg_pag_id_univ_pag_codice_id_univoco AS cod_e_sogg_pag_id_univ_pag_codice_id_univoco_e,
    export.cod_e_sogg_pag_anagrafica_pagatore AS cod_e_sogg_pag_anagrafica_pagatore_e,
    export.de_e_sogg_pag_indirizzo_pagatore AS de_e_sogg_pag_indirizzo_pagatore_e,
    export.de_e_sogg_pag_civico_pagatore AS de_e_sogg_pag_civico_pagatore_e,
    export.cod_e_sogg_pag_cap_pagatore AS cod_e_sogg_pag_cap_pagatore_e,
    export.de_e_sogg_pag_localita_pagatore AS de_e_sogg_pag_localita_pagatore_e,
    export.de_e_sogg_pag_provincia_pagatore AS de_e_sogg_pag_provincia_pagatore_e,
    export.cod_e_sogg_pag_nazione_pagatore AS cod_e_sogg_pag_nazione_pagatore_e,
    export.de_e_sogg_pag_email_pagatore AS de_e_sogg_pag_email_pagatore_e,
    export.cod_e_dati_pag_codice_esito_pagamento AS cod_e_dati_pag_codice_esito_pagamento_e,
    export.num_e_dati_pag_importo_totale_pagato AS num_e_dati_pag_importo_totale_pagato_e,
    export.cod_e_dati_pag_id_univoco_versamento AS cod_e_dati_pag_id_univoco_versamento_e,
    export.cod_e_dati_pag_codice_contesto_pagamento AS cod_e_dati_pag_codice_contesto_pagamento_e,
    export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato AS num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e,
    export.de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento AS de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento_e,
    export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento AS dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento_e,
    export.cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss AS cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss_e,
    export.de_e_dati_pag_dati_sing_pag_causale_versamento AS de_e_dati_pag_dati_sing_pag_causale_versamento_e,
    export.de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione AS de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione_e,
    export.cod_tipo_dovuto AS cod_tipo_dovuto_e,
    export.dt_acquisizione AS dt_acquisizione_e,
    rendicontazione.mygov_ente_id AS mygov_ente_id_r,
    rendicontazione.mygov_manage_flusso_id AS mygov_manage_flusso_id_r,
    rendicontazione.versione_oggetto AS versione_oggetto_r,
    rendicontazione.cod_identificativo_flusso AS cod_identificativo_flusso_r,
    rendicontazione.dt_data_ora_flusso AS dt_data_ora_flusso_r,
    rendicontazione.cod_identificativo_univoco_regolamento AS cod_identificativo_univoco_regolamento_r,
    rendicontazione.dt_data_regolamento AS dt_data_regolamento_r,
    rendicontazione.cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco AS cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco_r,
    rendicontazione.cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco AS cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco_r,
    rendicontazione.de_ist_mitt_denominazione_mittente AS de_ist_mitt_denominazione_mittente_r,
    rendicontazione.cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco AS cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco_r,
    rendicontazione.cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco AS cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco_r,
    rendicontazione.de_ist_ricev_denominazione_ricevente AS de_ist_ricev_denominazione_ricevente_r,
    rendicontazione.num_numero_totale_pagamenti AS num_numero_totale_pagamenti_r,
    rendicontazione.num_importo_totale_pagamenti AS num_importo_totale_pagamenti_r,
    rendicontazione.cod_dati_sing_pagam_identificativo_univoco_versamento AS cod_dati_sing_pagam_identificativo_univoco_versamento_r,
    rendicontazione.cod_dati_sing_pagam_identificativo_univoco_riscossione AS cod_dati_sing_pagam_identificativo_univoco_riscossione_r,
    rendicontazione.num_dati_sing_pagam_singolo_importo_pagato AS num_dati_sing_pagam_singolo_importo_pagato_r,
    rendicontazione.cod_dati_sing_pagam_codice_esito_singolo_pagamento AS cod_dati_sing_pagam_codice_esito_singolo_pagamento_r,
    rendicontazione.dt_dati_sing_pagam_data_esito_singolo_pagamento AS dt_dati_sing_pagam_data_esito_singolo_pagamento_r,
    rendicontazione.dt_acquisizione AS dt_acquisizione_r,
    classificazione.mygov_classificazione_codice AS classificazione_completezza
   FROM ((public.mygov_flusso_export export
     FULL JOIN ( SELECT mygov_flusso_rendicontazione.version,
            mygov_flusso_rendicontazione.dt_creazione,
            mygov_flusso_rendicontazione.dt_ultima_modifica,
            mygov_flusso_rendicontazione.mygov_ente_id,
            mygov_flusso_rendicontazione.mygov_manage_flusso_id,
            mygov_flusso_rendicontazione.identificativo_psp,
            mygov_flusso_rendicontazione.versione_oggetto,
            mygov_flusso_rendicontazione.cod_identificativo_flusso,
            mygov_flusso_rendicontazione.dt_data_ora_flusso,
            mygov_flusso_rendicontazione.cod_identificativo_univoco_regolamento,
            mygov_flusso_rendicontazione.dt_data_regolamento,
            mygov_flusso_rendicontazione.cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco,
            mygov_flusso_rendicontazione.cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco,
            mygov_flusso_rendicontazione.de_ist_mitt_denominazione_mittente,
            mygov_flusso_rendicontazione.cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco,
            mygov_flusso_rendicontazione.cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco,
            mygov_flusso_rendicontazione.de_ist_ricev_denominazione_ricevente,
            mygov_flusso_rendicontazione.num_numero_totale_pagamenti,
            mygov_flusso_rendicontazione.num_importo_totale_pagamenti,
            mygov_flusso_rendicontazione.cod_dati_sing_pagam_identificativo_univoco_versamento,
            mygov_flusso_rendicontazione.cod_dati_sing_pagam_identificativo_univoco_riscossione,
            mygov_flusso_rendicontazione.num_dati_sing_pagam_singolo_importo_pagato,
            mygov_flusso_rendicontazione.cod_dati_sing_pagam_codice_esito_singolo_pagamento,
            mygov_flusso_rendicontazione.dt_dati_sing_pagam_data_esito_singolo_pagamento,
            mygov_flusso_rendicontazione.dt_acquisizione,
            mygov_flusso_rendicontazione.indice_dati_singolo_pagamento,
            mygov_flusso_rendicontazione.codice_bic_banca_di_riversamento
           FROM public.mygov_flusso_rendicontazione
          WHERE ((mygov_flusso_rendicontazione.cod_dati_sing_pagam_codice_esito_singolo_pagamento)::text <> '3'::text)) rendicontazione ON (((export.mygov_ente_id = rendicontazione.mygov_ente_id) AND ((export.cod_rp_silinviarp_id_univoco_versamento)::text = (rendicontazione.cod_dati_sing_pagam_identificativo_univoco_versamento)::text) AND ((export.cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss)::text = (rendicontazione.cod_dati_sing_pagam_identificativo_univoco_riscossione)::text) AND (export.indice_dati_singolo_pagamento = rendicontazione.indice_dati_singolo_pagamento))))
     LEFT JOIN public.mygov_classificazione_completezza classificazione ON (((((classificazione.mygov_classificazione_codice)::text = 'RT_IUF'::text) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato IS NOT NULL) AND (rendicontazione.num_dati_sing_pagam_singolo_importo_pagato IS NOT NULL) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato = rendicontazione.num_dati_sing_pagam_singolo_importo_pagato)) OR (((classificazione.mygov_classificazione_codice)::text = 'RT_NO_IUF'::text) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato IS NOT NULL) AND ((rendicontazione.num_dati_sing_pagam_singolo_importo_pagato IS NULL) OR ((rendicontazione.num_dati_sing_pagam_singolo_importo_pagato IS NOT NULL) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato <> rendicontazione.num_dati_sing_pagam_singolo_importo_pagato)))))));


ALTER TABLE public.v_mygov_export_rendicontazione_completa OWNER TO mypay4;

--
-- TOC entry 254 (class 1259 OID 33115)
-- Name: v_mygov_import_export_rendicontazione_tesoreria; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.v_mygov_import_export_rendicontazione_tesoreria AS
 SELECT ente.cod_ipa_ente AS codice_ipa_ente,
    import_export_rendicontazione_tesoreria.dt_rp_dati_vers_data_esecuzione_pagamento_i AS dt_data_esecuzione_pagamento,
    (COALESCE(to_char((import_export_rendicontazione_tesoreria.dt_rp_dati_vers_data_esecuzione_pagamento_i)::timestamp with time zone, 'dd/MM/yyyy'::text), 'n/a'::text))::character varying(10) AS de_data_esecuzione_pagamento,
    (COALESCE((import_export_rendicontazione_tesoreria.num_rp_dati_vers_dati_sing_vers_commissione_carico_pa_i)::character varying(15), ''::character varying))::character varying(15) AS singolo_importo_commissione_carico_pa,
    (COALESCE(import_export_rendicontazione_tesoreria.bilancio_i, ''::character varying))::character varying(4096) AS bilancio,
    (COALESCE(import_export_rendicontazione_tesoreria.de_nome_flusso_e, 'n/a'::character varying))::character varying(50) AS nome_flusso_import_ente,
    (COALESCE((import_export_rendicontazione_tesoreria.num_riga_flusso_e)::character varying(12), 'n/a'::character varying))::character varying(12) AS riga_flusso_import_ente,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_iud_i, import_export_rendicontazione_tesoreria.cod_iud_e, 'n/a'::character varying))::character varying(35) AS codice_iud,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_rp_silinviarp_id_univoco_versamento_i, import_export_rendicontazione_tesoreria.cod_rp_silinviarp_id_univoco_versamento_e, import_export_rendicontazione_tesoreria.cod_dati_sing_pagam_identificativo_univoco_versamento_r, import_export_rendicontazione_tesoreria.cod_id_univoco_versamento_t, 'n/a'::character varying))::character varying(35) AS codice_iuv,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_versione_oggetto_e, ''::character varying))::character varying(16) AS versione_oggetto,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_dom_id_dominio_e, ''::character varying))::character varying(35) AS identificativo_dominio,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_dom_id_stazione_richiedente_e, ''::character varying))::character varying(35) AS identificativo_stazione_richiedente,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_id_messaggio_ricevuta_e, ''::character varying))::character varying(35) AS identificativo_messaggio_ricevuta,
    (COALESCE(to_char(import_export_rendicontazione_tesoreria.dt_e_data_ora_messaggio_ricevuta_e, 'dd/MM/yyyy HH:mm:ss'::text), ''::text))::character varying(19) AS data_ora_messaggio_ricevuta,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_riferimento_messaggio_richiesta_e, ''::character varying))::character varying(35) AS riferimento_messaggio_richiesta,
    (COALESCE(to_char((import_export_rendicontazione_tesoreria.dt_e_riferimento_data_richiesta_e)::timestamp with time zone, 'dd/MM/yyyy'::text), ''::text))::character varying(10) AS riferimento_data_richiesta,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_istit_att_id_univ_att_tipo_id_univoco_e, ''::bpchar))::character varying(1) AS tipo_identificativo_univoco_attestante,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_istit_att_id_univ_att_codice_id_univoco_e, ''::character varying))::character varying(35) AS codice_identificativo_univoco_attestante,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_istit_att_denominazione_attestante_e, ''::character varying))::character varying(70) AS denominazione_attestante,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_istit_att_codice_unit_oper_attestante_e, ''::character varying))::character varying(35) AS codice_unit_oper_attestante,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_istit_att_denom_unit_oper_attestante_e, ''::character varying))::character varying(70) AS denom_unit_oper_attestante,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_istit_att_indirizzo_attestante_e, ''::character varying))::character varying(70) AS indirizzo_attestante,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_istit_att_civico_attestante_e, ''::character varying))::character varying(16) AS civico_attestante,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_istit_att_cap_attestante_e, ''::character varying))::character varying(16) AS cap_attestante,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_istit_att_localita_attestante_e, ''::character varying))::character varying(35) AS localita_attestante,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_istit_att_provincia_attestante_e, ''::character varying))::character varying(35) AS provincia_attestante,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_istit_att_nazione_attestante_e, ''::character varying))::character varying(2) AS nazione_attestante,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_ente_benef_id_univ_benef_tipo_id_univoco_e, ''::bpchar))::character varying(1) AS tipo_identificativo_univoco_beneficiario,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_ente_benef_id_univ_benef_codice_id_univoco_e, ''::character varying))::character varying(35) AS codice_identificativo_univoco_beneficiario,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_ente_benef_denominazione_beneficiario_e, ''::character varying))::character varying(70) AS denominazione_beneficiario,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_ente_benef_codice_unit_oper_beneficiario_e, ''::character varying))::character varying(35) AS codice_unit_oper_beneficiario,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_ente_benef_denom_unit_oper_beneficiario_e, ''::character varying))::character varying(70) AS denom_unit_oper_beneficiario,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_ente_benef_indirizzo_beneficiario_e, ''::character varying))::character varying(70) AS indirizzo_beneficiario,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_ente_benef_civico_beneficiario_e, ''::character varying))::character varying(16) AS civico_beneficiario,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_ente_benef_cap_beneficiario_e, ''::character varying))::character varying(16) AS cap_beneficiario,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_ente_benef_localita_beneficiario_e, ''::character varying))::character varying(35) AS localita_beneficiario,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_ente_benef_provincia_beneficiario_e, ''::character varying))::character varying(35) AS provincia_beneficiario,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_ente_benef_nazione_beneficiario_e, ''::character varying))::character varying(2) AS nazione_beneficiario,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_sogg_vers_id_univ_vers_tipo_id_univoco_e, ''::bpchar))::character varying(1) AS tipo_identificativo_univoco_versante,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_sogg_vers_id_univ_vers_codice_id_univoco_e, ''::character varying))::character varying(35) AS codice_identificativo_univoco_versante,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_sogg_vers_anagrafica_versante_e, ''::character varying))::character varying(70) AS anagrafica_versante,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_sogg_vers_indirizzo_versante_e, ''::character varying))::character varying(70) AS indirizzo_versante,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_sogg_vers_civico_versante_e, ''::character varying))::character varying(16) AS civico_versante,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_sogg_vers_cap_versante_e, ''::character varying))::character varying(16) AS cap_versante,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_sogg_vers_localita_versante_e, ''::character varying))::character varying(35) AS localita_versante,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_sogg_vers_provincia_versante_e, ''::character varying))::character varying(35) AS provincia_versante,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_sogg_vers_nazione_versante_e, ''::character varying))::character varying(2) AS nazione_versante,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_sogg_vers_email_versante_e, ''::character varying))::character varying(256) AS email_versante,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_rp_sogg_pag_id_univ_pag_tipo_id_univoco_i, import_export_rendicontazione_tesoreria.cod_e_sogg_pag_id_univ_pag_tipo_id_univoco_e, ''::bpchar))::character varying(1) AS tipo_identificativo_univoco_pagatore,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_rp_sogg_pag_id_univ_pag_codice_id_univoco_i, import_export_rendicontazione_tesoreria.cod_e_sogg_pag_id_univ_pag_codice_id_univoco_e, ''::character varying))::character varying(35) AS codice_identificativo_univoco_pagatore,
    (COALESCE(import_export_rendicontazione_tesoreria.de_rp_sogg_pag_anagrafica_pagatore_i, import_export_rendicontazione_tesoreria.cod_e_sogg_pag_anagrafica_pagatore_e, ''::character varying))::character varying(70) AS anagrafica_pagatore,
    (COALESCE(import_export_rendicontazione_tesoreria.de_rp_sogg_pag_indirizzo_pagatore_i, import_export_rendicontazione_tesoreria.de_e_sogg_pag_indirizzo_pagatore_e, ''::character varying))::character varying(70) AS indirizzo_pagatore,
    (COALESCE(import_export_rendicontazione_tesoreria.de_rp_sogg_pag_civico_pagatore_i, import_export_rendicontazione_tesoreria.de_e_sogg_pag_civico_pagatore_e, ''::character varying))::character varying(16) AS civico_pagatore,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_rp_sogg_pag_cap_pagatore_i, import_export_rendicontazione_tesoreria.cod_e_sogg_pag_cap_pagatore_e, ''::character varying))::character varying(16) AS cap_pagatore,
    (COALESCE(import_export_rendicontazione_tesoreria.de_rp_sogg_pag_localita_pagatore_i, import_export_rendicontazione_tesoreria.de_e_sogg_pag_localita_pagatore_e, ''::character varying))::character varying(35) AS localita_pagatore,
    (COALESCE(import_export_rendicontazione_tesoreria.de_rp_sogg_pag_provincia_pagatore_i, import_export_rendicontazione_tesoreria.de_e_sogg_pag_provincia_pagatore_e, ''::character varying))::character varying(35) AS provincia_pagatore,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_rp_sogg_pag_nazione_pagatore_i, import_export_rendicontazione_tesoreria.cod_e_sogg_pag_nazione_pagatore_e, ''::character varying))::character varying(2) AS nazione_pagatore,
    (COALESCE(import_export_rendicontazione_tesoreria.de_rp_sogg_pag_email_pagatore_i, import_export_rendicontazione_tesoreria.de_e_sogg_pag_email_pagatore_e, ''::character varying))::character varying(256) AS email_pagatore,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_dati_pag_codice_esito_pagamento_e, ''::bpchar))::character varying(1) AS codice_esito_pagamento,
    (COALESCE((import_export_rendicontazione_tesoreria.num_e_dati_pag_importo_totale_pagato_e)::character varying(15), ''::character varying))::character varying(15) AS importo_totale_pagato,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_dati_pag_id_univoco_versamento_e, ''::character varying))::character varying(35) AS identificativo_univoco_versamento,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_dati_pag_codice_contesto_pagamento_e, ''::character varying))::character varying(35) AS codice_contesto_pagamento,
    (COALESCE((import_export_rendicontazione_tesoreria.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento_i)::character varying(15), (import_export_rendicontazione_tesoreria.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e)::character varying(15), (import_export_rendicontazione_tesoreria.num_dati_sing_pagam_singolo_importo_pagato_r)::character varying(15), ''::character varying))::character varying(15) AS singolo_importo_pagato,
    (COALESCE(import_export_rendicontazione_tesoreria.de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento_e, import_export_rendicontazione_tesoreria.cod_dati_sing_pagam_codice_esito_singolo_pagamento_r, ''::character varying))::character varying(35) AS esito_singolo_pagamento,
    COALESCE(import_export_rendicontazione_tesoreria.dt_dati_sing_pagam_data_esito_singolo_pagamento_r, import_export_rendicontazione_tesoreria.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento_e) AS dt_data_esito_singolo_pagamento,
    (COALESCE(to_char((COALESCE(import_export_rendicontazione_tesoreria.dt_dati_sing_pagam_data_esito_singolo_pagamento_r, import_export_rendicontazione_tesoreria.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento_e))::timestamp with time zone, 'dd/MM/yyyy'::text), 'n/a'::text))::character varying(10) AS de_data_esito_singolo_pagamento,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss_e, import_export_rendicontazione_tesoreria.cod_dati_sing_pagam_identificativo_univoco_riscossione_r, 'n/a'::character varying))::character varying(35) AS identificativo_univoco_riscossione,
    (COALESCE(import_export_rendicontazione_tesoreria.de_rp_dati_vers_dati_sing_vers_causale_versamento_i, import_export_rendicontazione_tesoreria.de_e_dati_pag_dati_sing_pag_causale_versamento_e, ''::character varying))::character varying(1024) AS causale_versamento,
    (COALESCE(import_export_rendicontazione_tesoreria.de_rp_dati_vers_dati_sing_vers_dati_specifici_riscossione_i, import_export_rendicontazione_tesoreria.de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione_e, ''::character varying))::character varying(140) AS dati_specifici_riscossione,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_tipo_dovuto_i, import_export_rendicontazione_tesoreria.cod_tipo_dovuto_e, ''::character varying))::character varying(64) AS tipo_dovuto,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_identificativo_flusso_r, import_export_rendicontazione_tesoreria.cod_id_univoco_flusso_t, 'n/a'::character varying))::character varying(35) AS identificativo_flusso_rendicontazione,
    (COALESCE(to_char(import_export_rendicontazione_tesoreria.dt_data_ora_flusso_r, 'dd/MM/yyyy HH:mm:ss'::text), 'n/a'::text))::character varying(19) AS data_ora_flusso_rendicontazione,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_identificativo_univoco_regolamento_r, 'n/a'::character varying))::character varying(35) AS identificativo_univoco_regolamento,
    COALESCE(import_export_rendicontazione_tesoreria.dt_data_regolamento_r, (import_export_rendicontazione_tesoreria.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento_e + ente.num_giorni_pagamento_presunti)) AS dt_data_regolamento,
    COALESCE((to_char((COALESCE(import_export_rendicontazione_tesoreria.dt_data_regolamento_r, (import_export_rendicontazione_tesoreria.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento_e + ente.num_giorni_pagamento_presunti)))::timestamp with time zone, 'dd/MM/yyyy'::text))::character varying(10), 'n/a'::character varying) AS de_data_regolamento,
    (COALESCE((import_export_rendicontazione_tesoreria.num_numero_totale_pagamenti_r)::character varying(15), 'n/a'::character varying))::character varying(15) AS numero_totale_pagamenti,
    (COALESCE((import_export_rendicontazione_tesoreria.num_importo_totale_pagamenti_r)::character varying(15), 'n/a'::character varying))::character varying(21) AS importo_totale_pagamenti,
    (to_char((GREATEST(import_export_rendicontazione_tesoreria.dt_acquisizione_r, import_export_rendicontazione_tesoreria.dt_acquisizione_e, import_export_rendicontazione_tesoreria.dt_acquisizione_t))::timestamp with time zone, 'dd/MM/yyyy'::text))::character varying(10) AS data_acquisizione,
    (COALESCE(import_export_rendicontazione_tesoreria.cod_conto_t, (''::character varying)::bpchar))::character varying(12) AS cod_conto,
    import_export_rendicontazione_tesoreria.dt_data_contabile_t AS dt_data_contabile,
    (COALESCE(to_char((import_export_rendicontazione_tesoreria.dt_data_contabile_t)::timestamp with time zone, 'dd/MM/yyyy'::text), (''::character varying)::text))::character varying(10) AS de_data_contabile,
    import_export_rendicontazione_tesoreria.dt_data_valuta_t AS dt_data_valuta,
    (COALESCE(to_char((import_export_rendicontazione_tesoreria.dt_data_valuta_t)::timestamp with time zone, 'dd/MM/yyyy'::text), (''::character varying)::text))::character varying(10) AS de_data_valuta,
    import_export_rendicontazione_tesoreria.num_importo_t AS num_importo,
    (COALESCE((import_export_rendicontazione_tesoreria.num_importo_t)::character(15), ('n/a'::character varying)::bpchar))::character varying(15) AS de_importo,
    COALESCE(import_export_rendicontazione_tesoreria.cod_or1_t, ''::text) AS cod_or1,
    COALESCE(import_export_rendicontazione_tesoreria.de_anno_bolletta_t, ('n/a'::text)::character varying) AS de_anno_bolletta,
    COALESCE(import_export_rendicontazione_tesoreria.cod_bolletta_t, ('n/a'::text)::character varying) AS cod_bolletta,
    COALESCE(import_export_rendicontazione_tesoreria.cod_id_dominio_t, ('n/a'::text)::character varying) AS cod_id_dominio,
    import_export_rendicontazione_tesoreria.dt_ricezione_t AS dt_ricezione,
    (COALESCE(to_char((import_export_rendicontazione_tesoreria.dt_ricezione_t)::timestamp with time zone, 'dd/MM/yyyy'::text), ('n/a'::character varying)::text))::character varying(10) AS de_data_ricezione,
    COALESCE(import_export_rendicontazione_tesoreria.de_anno_documento_t, ('n/a'::text)::character varying) AS de_anno_documento,
    COALESCE(import_export_rendicontazione_tesoreria.cod_documento_t, ('n/a'::text)::character varying) AS cod_documento,
    COALESCE(import_export_rendicontazione_tesoreria.de_anno_provvisorio_t, ('n/a'::text)::character varying) AS de_anno_provvisorio,
    COALESCE(import_export_rendicontazione_tesoreria.cod_provvisorio_t, ('n/a'::text)::character varying) AS cod_provvisorio,
    COALESCE(import_export_rendicontazione_tesoreria.de_causale_t, (('n/a'::text)::character varying)::text) AS de_causale_t,
        CASE
            WHEN ((import_export_rendicontazione_tesoreria.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento_i = import_export_rendicontazione_tesoreria.num_e_dati_pag_importo_totale_pagato_e) OR (((sum(import_export_rendicontazione_tesoreria.num_dati_sing_pagam_singolo_importo_pagato_r) OVER (PARTITION BY import_export_rendicontazione_tesoreria.cod_identificativo_flusso_r) - import_export_rendicontazione_tesoreria.num_importo_totale_pagamenti_r) = (0)::numeric) AND (import_export_rendicontazione_tesoreria.num_e_dati_pag_importo_totale_pagato_e = import_export_rendicontazione_tesoreria.num_dati_sing_pagam_singolo_importo_pagato_r) AND (import_export_rendicontazione_tesoreria.num_importo_totale_pagamenti_r = import_export_rendicontazione_tesoreria.num_importo_t))) THEN 'OK'::character varying(3)
            WHEN ((import_export_rendicontazione_tesoreria.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento_i <> import_export_rendicontazione_tesoreria.num_e_dati_pag_importo_totale_pagato_e) OR ((sum(import_export_rendicontazione_tesoreria.num_dati_sing_pagam_singolo_importo_pagato_r) OVER (PARTITION BY import_export_rendicontazione_tesoreria.cod_identificativo_flusso_r) - import_export_rendicontazione_tesoreria.num_importo_totale_pagamenti_r) <> (0)::numeric) OR (import_export_rendicontazione_tesoreria.num_e_dati_pag_importo_totale_pagato_e <> import_export_rendicontazione_tesoreria.num_dati_sing_pagam_singolo_importo_pagato_r) OR (import_export_rendicontazione_tesoreria.num_importo_totale_pagamenti_r <> import_export_rendicontazione_tesoreria.num_importo_t)) THEN 'KO'::character varying(3)
            ELSE 'n/a'::character varying(3)
        END AS verifica_totale,
    (COALESCE(import_export_rendicontazione_tesoreria.classificazione_completezza, 'OTHERS'::character varying))::character varying(20) AS classificazione_completezza,
    import_export_rendicontazione_tesoreria.dt_data_ultimo_aggiornamento,
    (to_char((import_export_rendicontazione_tesoreria.dt_data_ultimo_aggiornamento)::timestamp with time zone, 'dd/MM/yyyy'::text))::character varying(10) AS de_data_ultimo_aggiornamento,
    COALESCE(import_export_rendicontazione_tesoreria.indice_dati_singolo_pagamento_e, import_export_rendicontazione_tesoreria.indice_dati_singolo_pagamento_r) AS indice_dati_singolo_pagamento,
    COALESCE(import_export_rendicontazione_tesoreria.cod_identificativo_flusso_r, import_export_rendicontazione_tesoreria.cod_id_univoco_flusso_t) AS cod_iuf_key,
    COALESCE(import_export_rendicontazione_tesoreria.cod_iud_i, import_export_rendicontazione_tesoreria.cod_iud_e) AS cod_iud_key,
    COALESCE(import_export_rendicontazione_tesoreria.cod_rp_silinviarp_id_univoco_versamento_i, import_export_rendicontazione_tesoreria.cod_rp_silinviarp_id_univoco_versamento_e, import_export_rendicontazione_tesoreria.cod_dati_sing_pagam_identificativo_univoco_versamento_r, import_export_rendicontazione_tesoreria.cod_id_univoco_versamento_t) AS cod_iuv_key,
    COALESCE(import_export_rendicontazione_tesoreria.bilancio_e, ('n/a'::text)::character varying) AS bilancio_e,
    import_export_rendicontazione_tesoreria.dt_effettiva_sospeso_t AS dt_effettiva_sospeso,
    COALESCE(import_export_rendicontazione_tesoreria.codice_gestionale_provvisorio_t, ''::character varying) AS codice_gestionale_provvisorio
   FROM public.mygov_import_export_rendicontazione_tesoreria_completa import_export_rendicontazione_tesoreria,
    public.mygov_ente ente
  WHERE (COALESCE(import_export_rendicontazione_tesoreria.mygov_ente_id_i, import_export_rendicontazione_tesoreria.mygov_ente_id_e, import_export_rendicontazione_tesoreria.mygov_ente_id_r, import_export_rendicontazione_tesoreria.mygov_ente_id_t) = ente.mygov_ente_id);


ALTER TABLE public.v_mygov_import_export_rendicontazione_tesoreria OWNER TO postgres;

--
-- TOC entry 255 (class 1259 OID 33120)
-- Name: v_mygov_import_export_rendicontazione_tesoreria_completa; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.v_mygov_import_export_rendicontazione_tesoreria_completa AS
 SELECT COALESCE(import.mygov_ente_id, export.mygov_ente_id, rendicontazione.mygov_ente_id, tesoreria_iuf.mygov_ente_id, tesoreria_iuv.mygov_ente_id, tesoreria_f2k_iuf.mygov_ente_id, tesoreria_f2k_iuv.mygov_ente_id, tesoreria_f2k.mygov_ente_id) AS mygov_ente_id,
    (COALESCE(import.cod_rp_silinviarp_id_univoco_versamento, export.cod_rp_silinviarp_id_univoco_versamento, rendicontazione.cod_dati_sing_pagam_identificativo_univoco_versamento, tesoreria_iuv.cod_id_univoco_versamento, tesoreria_f2k_iuv.cod_id_univoco_versamento, 'n/a'::character varying))::character varying(35) AS codice_iuv,
    (COALESCE(export.cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss, rendicontazione.cod_dati_sing_pagam_identificativo_univoco_riscossione, 'n/a'::character varying))::character varying(35) AS identificativo_univoco_riscossione,
    (COALESCE(rendicontazione.cod_identificativo_flusso, tesoreria_iuf.cod_id_univoco_flusso, tesoreria_f2k_iuf.cod_id_univoco_flusso, 'n/a'::character varying))::character varying(35) AS identificativo_flusso_rendicontazione,
    import.mygov_ente_id AS mygov_ente_id_i,
    import.mygov_manage_flusso_id AS mygov_manage_flusso_id_i,
    import.cod_iud AS cod_iud_i,
    import.cod_rp_silinviarp_id_univoco_versamento AS cod_rp_silinviarp_id_univoco_versamento_i,
    import.cod_rp_sogg_pag_id_univ_pag_tipo_id_univoco AS cod_rp_sogg_pag_id_univ_pag_tipo_id_univoco_i,
    import.cod_rp_sogg_pag_id_univ_pag_codice_id_univoco AS cod_rp_sogg_pag_id_univ_pag_codice_id_univoco_i,
    import.de_rp_sogg_pag_anagrafica_pagatore AS de_rp_sogg_pag_anagrafica_pagatore_i,
    import.de_rp_sogg_pag_indirizzo_pagatore AS de_rp_sogg_pag_indirizzo_pagatore_i,
    import.de_rp_sogg_pag_civico_pagatore AS de_rp_sogg_pag_civico_pagatore_i,
    import.cod_rp_sogg_pag_cap_pagatore AS cod_rp_sogg_pag_cap_pagatore_i,
    import.de_rp_sogg_pag_localita_pagatore AS de_rp_sogg_pag_localita_pagatore_i,
    import.de_rp_sogg_pag_provincia_pagatore AS de_rp_sogg_pag_provincia_pagatore_i,
    import.cod_rp_sogg_pag_nazione_pagatore AS cod_rp_sogg_pag_nazione_pagatore_i,
    import.de_rp_sogg_pag_email_pagatore AS de_rp_sogg_pag_email_pagatore_i,
    import.dt_rp_dati_vers_data_esecuzione_pagamento AS dt_rp_dati_vers_data_esecuzione_pagamento_i,
    import.cod_rp_dati_vers_tipo_versamento AS cod_rp_dati_vers_tipo_versamento_i,
    import.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento AS num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento_i,
    import.num_rp_dati_vers_dati_sing_vers_commissione_carico_pa AS num_rp_dati_vers_dati_sing_vers_commissione_carico_pa_i,
    import.de_rp_dati_vers_dati_sing_vers_causale_versamento AS de_rp_dati_vers_dati_sing_vers_causale_versamento_i,
    import.de_rp_dati_vers_dati_sing_vers_dati_specifici_riscossione AS de_rp_dati_vers_dati_sing_vers_dati_specifici_riscossione_i,
    import.cod_tipo_dovuto AS cod_tipo_dovuto_i,
    import.bilancio AS bilancio_i,
    import.dt_acquisizione AS dt_acquisizione_i,
    export.mygov_ente_id AS mygov_ente_id_e,
    export.mygov_manage_flusso_id AS mygov_manage_flusso_id_e,
    export.de_nome_flusso AS de_nome_flusso_e,
    export.num_riga_flusso AS num_riga_flusso_e,
    export.cod_iud AS cod_iud_e,
    export.cod_rp_silinviarp_id_univoco_versamento AS cod_rp_silinviarp_id_univoco_versamento_e,
    export.de_e_versione_oggetto AS de_e_versione_oggetto_e,
    export.cod_e_dom_id_dominio AS cod_e_dom_id_dominio_e,
    export.cod_e_dom_id_stazione_richiedente AS cod_e_dom_id_stazione_richiedente_e,
    export.cod_e_id_messaggio_ricevuta AS cod_e_id_messaggio_ricevuta_e,
    export.dt_e_data_ora_messaggio_ricevuta AS dt_e_data_ora_messaggio_ricevuta_e,
    export.cod_e_riferimento_messaggio_richiesta AS cod_e_riferimento_messaggio_richiesta_e,
    export.dt_e_riferimento_data_richiesta AS dt_e_riferimento_data_richiesta_e,
    export.cod_e_istit_att_id_univ_att_tipo_id_univoco AS cod_e_istit_att_id_univ_att_tipo_id_univoco_e,
    export.cod_e_istit_att_id_univ_att_codice_id_univoco AS cod_e_istit_att_id_univ_att_codice_id_univoco_e,
    export.de_e_istit_att_denominazione_attestante AS de_e_istit_att_denominazione_attestante_e,
    export.cod_e_istit_att_codice_unit_oper_attestante AS cod_e_istit_att_codice_unit_oper_attestante_e,
    export.de_e_istit_att_denom_unit_oper_attestante AS de_e_istit_att_denom_unit_oper_attestante_e,
    export.de_e_istit_att_indirizzo_attestante AS de_e_istit_att_indirizzo_attestante_e,
    export.de_e_istit_att_civico_attestante AS de_e_istit_att_civico_attestante_e,
    export.cod_e_istit_att_cap_attestante AS cod_e_istit_att_cap_attestante_e,
    export.de_e_istit_att_localita_attestante AS de_e_istit_att_localita_attestante_e,
    export.de_e_istit_att_provincia_attestante AS de_e_istit_att_provincia_attestante_e,
    export.cod_e_istit_att_nazione_attestante AS cod_e_istit_att_nazione_attestante_e,
    export.cod_e_ente_benef_id_univ_benef_tipo_id_univoco AS cod_e_ente_benef_id_univ_benef_tipo_id_univoco_e,
    export.cod_e_ente_benef_id_univ_benef_codice_id_univoco AS cod_e_ente_benef_id_univ_benef_codice_id_univoco_e,
    export.de_e_ente_benef_denominazione_beneficiario AS de_e_ente_benef_denominazione_beneficiario_e,
    export.cod_e_ente_benef_codice_unit_oper_beneficiario AS cod_e_ente_benef_codice_unit_oper_beneficiario_e,
    export.de_e_ente_benef_denom_unit_oper_beneficiario AS de_e_ente_benef_denom_unit_oper_beneficiario_e,
    export.de_e_ente_benef_indirizzo_beneficiario AS de_e_ente_benef_indirizzo_beneficiario_e,
    export.de_e_ente_benef_civico_beneficiario AS de_e_ente_benef_civico_beneficiario_e,
    export.cod_e_ente_benef_cap_beneficiario AS cod_e_ente_benef_cap_beneficiario_e,
    export.de_e_ente_benef_localita_beneficiario AS de_e_ente_benef_localita_beneficiario_e,
    export.de_e_ente_benef_provincia_beneficiario AS de_e_ente_benef_provincia_beneficiario_e,
    export.cod_e_ente_benef_nazione_beneficiario AS cod_e_ente_benef_nazione_beneficiario_e,
    export.cod_e_sogg_vers_id_univ_vers_tipo_id_univoco AS cod_e_sogg_vers_id_univ_vers_tipo_id_univoco_e,
    export.cod_e_sogg_vers_id_univ_vers_codice_id_univoco AS cod_e_sogg_vers_id_univ_vers_codice_id_univoco_e,
    export.cod_e_sogg_vers_anagrafica_versante AS cod_e_sogg_vers_anagrafica_versante_e,
    export.de_e_sogg_vers_indirizzo_versante AS de_e_sogg_vers_indirizzo_versante_e,
    export.de_e_sogg_vers_civico_versante AS de_e_sogg_vers_civico_versante_e,
    export.cod_e_sogg_vers_cap_versante AS cod_e_sogg_vers_cap_versante_e,
    export.de_e_sogg_vers_localita_versante AS de_e_sogg_vers_localita_versante_e,
    export.de_e_sogg_vers_provincia_versante AS de_e_sogg_vers_provincia_versante_e,
    export.cod_e_sogg_vers_nazione_versante AS cod_e_sogg_vers_nazione_versante_e,
    export.de_e_sogg_vers_email_versante AS de_e_sogg_vers_email_versante_e,
    export.cod_e_sogg_pag_id_univ_pag_tipo_id_univoco AS cod_e_sogg_pag_id_univ_pag_tipo_id_univoco_e,
    export.cod_e_sogg_pag_id_univ_pag_codice_id_univoco AS cod_e_sogg_pag_id_univ_pag_codice_id_univoco_e,
    export.cod_e_sogg_pag_anagrafica_pagatore AS cod_e_sogg_pag_anagrafica_pagatore_e,
    export.de_e_sogg_pag_indirizzo_pagatore AS de_e_sogg_pag_indirizzo_pagatore_e,
    export.de_e_sogg_pag_civico_pagatore AS de_e_sogg_pag_civico_pagatore_e,
    export.cod_e_sogg_pag_cap_pagatore AS cod_e_sogg_pag_cap_pagatore_e,
    export.de_e_sogg_pag_localita_pagatore AS de_e_sogg_pag_localita_pagatore_e,
    export.de_e_sogg_pag_provincia_pagatore AS de_e_sogg_pag_provincia_pagatore_e,
    export.cod_e_sogg_pag_nazione_pagatore AS cod_e_sogg_pag_nazione_pagatore_e,
    export.de_e_sogg_pag_email_pagatore AS de_e_sogg_pag_email_pagatore_e,
    export.cod_e_dati_pag_codice_esito_pagamento AS cod_e_dati_pag_codice_esito_pagamento_e,
    export.num_e_dati_pag_importo_totale_pagato AS num_e_dati_pag_importo_totale_pagato_e,
    export.cod_e_dati_pag_id_univoco_versamento AS cod_e_dati_pag_id_univoco_versamento_e,
    export.cod_e_dati_pag_codice_contesto_pagamento AS cod_e_dati_pag_codice_contesto_pagamento_e,
    export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato AS num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e,
    export.de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento AS de_e_dati_pag_dati_sing_pag_esito_singolo_pagamento_e,
    export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento AS dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento_e,
    export.cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss AS cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss_e,
    export.de_e_dati_pag_dati_sing_pag_causale_versamento AS de_e_dati_pag_dati_sing_pag_causale_versamento_e,
    export.de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione AS de_e_dati_pag_dati_sing_pag_dati_specifici_riscossione_e,
    export.cod_tipo_dovuto AS cod_tipo_dovuto_e,
    export.dt_acquisizione AS dt_acquisizione_e,
    export.indice_dati_singolo_pagamento AS indice_dati_singolo_pagamento_e,
    export.bilancio AS bilancio_e,
    rendicontazione.mygov_ente_id AS mygov_ente_id_r,
    rendicontazione.mygov_manage_flusso_id AS mygov_manage_flusso_id_r,
    rendicontazione.versione_oggetto AS versione_oggetto_r,
    rendicontazione.cod_identificativo_flusso AS cod_identificativo_flusso_r,
    rendicontazione.dt_data_ora_flusso AS dt_data_ora_flusso_r,
    rendicontazione.cod_identificativo_univoco_regolamento AS cod_identificativo_univoco_regolamento_r,
    rendicontazione.dt_data_regolamento AS dt_data_regolamento_r,
    rendicontazione.cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco AS cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco_r,
    rendicontazione.cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco AS cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco_r,
    rendicontazione.de_ist_mitt_denominazione_mittente AS de_ist_mitt_denominazione_mittente_r,
    rendicontazione.cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco AS cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco_r,
    rendicontazione.cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco AS cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco_r,
    rendicontazione.de_ist_ricev_denominazione_ricevente AS de_ist_ricev_denominazione_ricevente_r,
    rendicontazione.num_numero_totale_pagamenti AS num_numero_totale_pagamenti_r,
    rendicontazione.num_importo_totale_pagamenti AS num_importo_totale_pagamenti_r,
    rendicontazione.cod_dati_sing_pagam_identificativo_univoco_versamento AS cod_dati_sing_pagam_identificativo_univoco_versamento_r,
    rendicontazione.cod_dati_sing_pagam_identificativo_univoco_riscossione AS cod_dati_sing_pagam_identificativo_univoco_riscossione_r,
    rendicontazione.num_dati_sing_pagam_singolo_importo_pagato AS num_dati_sing_pagam_singolo_importo_pagato_r,
    rendicontazione.cod_dati_sing_pagam_codice_esito_singolo_pagamento AS cod_dati_sing_pagam_codice_esito_singolo_pagamento_r,
    rendicontazione.dt_dati_sing_pagam_data_esito_singolo_pagamento AS dt_dati_sing_pagam_data_esito_singolo_pagamento_r,
    rendicontazione.dt_acquisizione AS dt_acquisizione_r,
    rendicontazione.indice_dati_singolo_pagamento AS indice_dati_singolo_pagamento_r,
    COALESCE(tesoreria_iuf.mygov_ente_id, tesoreria_iuv.mygov_ente_id, tesoreria_f2k_iuf.mygov_ente_id, tesoreria_f2k_iuv.mygov_ente_id, tesoreria_f2k.mygov_ente_id) AS mygov_ente_id_t,
    COALESCE(tesoreria_iuf.mygov_manage_flusso_id, tesoreria_iuv.mygov_manage_flusso_id) AS mygov_manage_flusso_id_t,
    COALESCE(tesoreria_iuf.cod_abi, tesoreria_iuv.cod_abi, tesoreria_f2k_iuf.cod_abi, tesoreria_f2k_iuv.cod_abi, tesoreria_f2k.cod_abi) AS cod_abi_t,
    COALESCE(tesoreria_iuf.cod_cab, tesoreria_iuv.cod_cab, tesoreria_f2k_iuf.cod_cab, tesoreria_f2k_iuv.cod_cab, tesoreria_f2k.cod_cab) AS cod_cab_t,
    COALESCE(tesoreria_iuf.cod_conto, tesoreria_iuv.cod_conto, tesoreria_f2k_iuf.cod_conto, tesoreria_f2k_iuv.cod_conto, tesoreria_f2k.cod_conto) AS cod_conto_t,
    COALESCE(tesoreria_iuf.cod_divisa, tesoreria_iuv.cod_divisa) AS cod_divisa_t,
    COALESCE(tesoreria_iuf.dt_data_contabile, tesoreria_iuv.dt_data_contabile, tesoreria_f2k_iuf.dt_bolletta, tesoreria_f2k_iuv.dt_bolletta, tesoreria_f2k.dt_bolletta) AS dt_data_contabile_t,
    COALESCE(tesoreria_iuf.dt_data_valuta, tesoreria_iuv.dt_data_valuta, tesoreria_f2k_iuf.dt_data_valuta_regione, tesoreria_f2k_iuv.dt_data_valuta_regione, tesoreria_f2k.dt_data_valuta_regione) AS dt_data_valuta_t,
    COALESCE(tesoreria_iuf.num_importo, tesoreria_iuv.num_importo, tesoreria_f2k_iuf.num_ip_bolletta, tesoreria_f2k_iuv.num_ip_bolletta, tesoreria_f2k.num_ip_bolletta) AS num_importo_t,
    COALESCE(tesoreria_iuf.cod_segno, tesoreria_iuv.cod_segno) AS cod_segno_t,
    COALESCE(tesoreria_iuf.de_causale, tesoreria_iuv.de_causale, (tesoreria_f2k_iuf.de_causale)::text, (tesoreria_f2k_iuv.de_causale)::text, (tesoreria_f2k.de_causale)::text) AS de_causale_t,
    COALESCE(tesoreria_iuf.cod_numero_assegno, tesoreria_iuv.cod_numero_assegno) AS cod_numero_assegno_t,
    COALESCE(tesoreria_iuf.cod_riferimento_banca, tesoreria_iuv.cod_riferimento_banca) AS cod_riferimento_banca_t,
    COALESCE(tesoreria_iuf.cod_riferimento_cliente, tesoreria_iuv.cod_riferimento_cliente) AS cod_riferimento_cliente_t,
    COALESCE(tesoreria_iuf.dt_data_ordine, tesoreria_iuv.dt_data_ordine) AS dt_data_ordine_t,
    COALESCE(tesoreria_iuf.de_descrizione_ordinante, tesoreria_iuv.de_descrizione_ordinante, (tesoreria_f2k_iuf.de_cognome)::text, (tesoreria_f2k_iuv.de_cognome)::text, (tesoreria_f2k.de_cognome)::text) AS de_descrizione_ordinante_t,
    COALESCE(tesoreria_iuf.cod_bi2, tesoreria_iuv.cod_bi2) AS cod_bi2_t,
    COALESCE(tesoreria_iuf.cod_be1, tesoreria_iuv.cod_be1) AS cod_be1_t,
    COALESCE(tesoreria_iuf.cod_ib1, tesoreria_iuv.cod_ib1) AS cod_ib1_t,
    COALESCE(tesoreria_iuf.cod_ib2, tesoreria_iuv.cod_ib2) AS cod_ib2_t,
    COALESCE(tesoreria_iuf.cod_ib4, tesoreria_iuv.cod_ib4) AS cod_ib4_t,
    COALESCE(tesoreria_iuf.cod_tid, tesoreria_iuv.cod_tid) AS cod_tid_t,
    COALESCE(tesoreria_iuf.cod_dte, tesoreria_iuv.cod_dte) AS cod_dte_t,
    COALESCE(tesoreria_iuf.cod_dtn, tesoreria_iuv.cod_dtn) AS cod_dtn_t,
    COALESCE(tesoreria_iuf.cod_eri, tesoreria_iuv.cod_eri) AS cod_eri_t,
    COALESCE(tesoreria_iuf.cod_im2, tesoreria_iuv.cod_im2) AS cod_im2_t,
    COALESCE(tesoreria_iuf.cod_ma2, tesoreria_iuv.cod_ma2) AS cod_ma2_t,
    COALESCE(tesoreria_iuf.cod_ri3, tesoreria_iuv.cod_ri3) AS cod_ri3_t,
    COALESCE(tesoreria_iuf.cod_or1, tesoreria_iuv.cod_or1, (tesoreria_f2k_iuf.de_cognome)::text, (tesoreria_f2k_iuv.de_cognome)::text, (tesoreria_f2k.de_cognome)::text) AS cod_or1_t,
    COALESCE(tesoreria_iuf.cod_sc2, tesoreria_iuv.cod_sc2) AS cod_sc2_t,
    COALESCE(tesoreria_iuf.cod_tr1, tesoreria_iuv.cod_tr1) AS cod_tr1_t,
    COALESCE(tesoreria_iuf.cod_sec, tesoreria_iuv.cod_sec) AS cod_sec_t,
    COALESCE(tesoreria_iuf.cod_ior, tesoreria_iuv.cod_ior) AS cod_ior_t,
    COALESCE(tesoreria_iuf.cod_id_univoco_flusso, tesoreria_f2k_iuf.cod_id_univoco_flusso, tesoreria_f2k_iuv.cod_id_univoco_flusso, tesoreria_f2k.cod_id_univoco_flusso) AS cod_id_univoco_flusso_t,
    COALESCE(tesoreria_iuv.cod_id_univoco_versamento, tesoreria_f2k_iuf.cod_id_univoco_versamento, tesoreria_f2k_iuv.cod_id_univoco_versamento, tesoreria_f2k.cod_id_univoco_versamento) AS cod_id_univoco_versamento_t,
    COALESCE((tesoreria_iuf.dt_acquisizione)::timestamp without time zone, (tesoreria_iuv.dt_acquisizione)::timestamp without time zone, tesoreria_f2k_iuf.dt_ultima_modifica, tesoreria_f2k_iuv.dt_ultima_modifica, tesoreria_f2k.dt_ultima_modifica) AS dt_acquisizione_t,
    COALESCE(tesoreria_f2k_iuf.de_anno_bolletta, tesoreria_f2k_iuv.de_anno_bolletta, tesoreria_f2k.de_anno_bolletta) AS de_anno_bolletta_t,
    COALESCE(tesoreria_f2k_iuf.cod_bolletta, tesoreria_f2k_iuv.cod_bolletta, tesoreria_f2k.cod_bolletta) AS cod_bolletta_t,
    COALESCE(tesoreria_f2k_iuf.cod_id_dominio, tesoreria_f2k_iuv.cod_id_dominio, tesoreria_f2k.cod_id_dominio) AS cod_id_dominio_t,
    COALESCE(tesoreria_f2k_iuf.dt_ricezione, tesoreria_f2k_iuv.dt_ricezione, tesoreria_f2k.dt_ricezione) AS dt_ricezione_t,
    COALESCE(tesoreria_f2k_iuf.de_anno_documento, tesoreria_f2k_iuv.de_anno_documento, tesoreria_f2k.de_anno_documento) AS de_anno_documento_t,
    COALESCE(tesoreria_f2k_iuf.cod_documento, tesoreria_f2k_iuv.cod_documento, tesoreria_f2k.cod_documento) AS cod_documento_t,
    COALESCE(tesoreria_f2k_iuf.de_ae_provvisorio, tesoreria_f2k_iuv.de_ae_provvisorio, tesoreria_f2k.de_ae_provvisorio) AS de_anno_provvisorio_t,
    COALESCE(tesoreria_f2k_iuf.cod_provvisorio, tesoreria_f2k_iuv.cod_provvisorio, tesoreria_f2k.cod_provvisorio) AS cod_provvisorio_t,
    classificazione.mygov_classificazione_codice AS classificazione_completezza,
    GREATEST((import.dt_acquisizione)::timestamp without time zone, (export.dt_acquisizione)::timestamp without time zone, (rendicontazione.dt_acquisizione)::timestamp without time zone, COALESCE((tesoreria_iuf.dt_acquisizione)::timestamp without time zone, (tesoreria_iuv.dt_acquisizione)::timestamp without time zone, tesoreria_f2k_iuf.dt_ultima_modifica, tesoreria_f2k_iuv.dt_ultima_modifica, tesoreria_f2k.dt_ultima_modifica)) AS dt_data_ultimo_aggiornamento,
    COALESCE(tesoreria_f2k_iuf.dt_effettiva_sospeso, tesoreria_f2k_iuv.dt_effettiva_sospeso, tesoreria_f2k.dt_effettiva_sospeso) AS dt_effettiva_sospeso_t,
    COALESCE(tesoreria_f2k_iuf.codice_gestionale_provvisorio, tesoreria_f2k_iuv.codice_gestionale_provvisorio, tesoreria_f2k.codice_gestionale_provvisorio) AS codice_gestionale_provvisorio_t
   FROM ((((((((public.mygov_flusso_import import
     FULL JOIN public.mygov_flusso_export export ON (((import.mygov_ente_id = export.mygov_ente_id) AND ((import.cod_iud)::text = (export.cod_iud)::text))))
     FULL JOIN ( SELECT mygov_flusso_rendicontazione.version,
            mygov_flusso_rendicontazione.dt_creazione,
            mygov_flusso_rendicontazione.dt_ultima_modifica,
            mygov_flusso_rendicontazione.mygov_ente_id,
            mygov_flusso_rendicontazione.mygov_manage_flusso_id,
            mygov_flusso_rendicontazione.identificativo_psp,
            mygov_flusso_rendicontazione.versione_oggetto,
            mygov_flusso_rendicontazione.cod_identificativo_flusso,
            mygov_flusso_rendicontazione.dt_data_ora_flusso,
            mygov_flusso_rendicontazione.cod_identificativo_univoco_regolamento,
            mygov_flusso_rendicontazione.dt_data_regolamento,
            mygov_flusso_rendicontazione.cod_ist_mitt_id_univ_mitt_tipo_identificativo_univoco,
            mygov_flusso_rendicontazione.cod_ist_mitt_id_univ_mitt_codice_identificativo_univoco,
            mygov_flusso_rendicontazione.de_ist_mitt_denominazione_mittente,
            mygov_flusso_rendicontazione.cod_ist_ricev_id_univ_ricev_tipo_identificativo_univoco,
            mygov_flusso_rendicontazione.cod_ist_ricev_id_univ_ricev_codice_identificativo_univoco,
            mygov_flusso_rendicontazione.de_ist_ricev_denominazione_ricevente,
            mygov_flusso_rendicontazione.num_numero_totale_pagamenti,
            mygov_flusso_rendicontazione.num_importo_totale_pagamenti,
            mygov_flusso_rendicontazione.cod_dati_sing_pagam_identificativo_univoco_versamento,
            mygov_flusso_rendicontazione.cod_dati_sing_pagam_identificativo_univoco_riscossione,
            mygov_flusso_rendicontazione.num_dati_sing_pagam_singolo_importo_pagato,
            mygov_flusso_rendicontazione.cod_dati_sing_pagam_codice_esito_singolo_pagamento,
            mygov_flusso_rendicontazione.dt_dati_sing_pagam_data_esito_singolo_pagamento,
            mygov_flusso_rendicontazione.dt_acquisizione,
            mygov_flusso_rendicontazione.indice_dati_singolo_pagamento,
            mygov_flusso_rendicontazione.codice_bic_banca_di_riversamento
           FROM public.mygov_flusso_rendicontazione
          WHERE ((mygov_flusso_rendicontazione.cod_dati_sing_pagam_codice_esito_singolo_pagamento)::text <> '3'::text)) rendicontazione ON (((export.mygov_ente_id = rendicontazione.mygov_ente_id) AND ((export.cod_rp_silinviarp_id_univoco_versamento)::text = (rendicontazione.cod_dati_sing_pagam_identificativo_univoco_versamento)::text) AND ((export.cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss)::text = (rendicontazione.cod_dati_sing_pagam_identificativo_univoco_riscossione)::text) AND (export.indice_dati_singolo_pagamento = rendicontazione.indice_dati_singolo_pagamento))))
     FULL JOIN ( SELECT mygov_flusso_tesoreria_iuf.mygov_ente_id,
            mygov_flusso_tesoreria_iuf.mygov_manage_flusso_id,
            mygov_flusso_tesoreria_iuf.cod_abi,
            mygov_flusso_tesoreria_iuf.cod_cab,
            mygov_flusso_tesoreria_iuf.cod_conto,
            mygov_flusso_tesoreria_iuf.cod_divisa,
            mygov_flusso_tesoreria_iuf.dt_data_contabile,
            mygov_flusso_tesoreria_iuf.dt_data_valuta,
            mygov_flusso_tesoreria_iuf.num_importo,
            mygov_flusso_tesoreria_iuf.cod_segno,
            mygov_flusso_tesoreria_iuf.de_causale,
            mygov_flusso_tesoreria_iuf.cod_numero_assegno,
            mygov_flusso_tesoreria_iuf.cod_riferimento_banca,
            mygov_flusso_tesoreria_iuf.cod_riferimento_cliente,
            mygov_flusso_tesoreria_iuf.dt_data_ordine,
            mygov_flusso_tesoreria_iuf.de_descrizione_ordinante,
            mygov_flusso_tesoreria_iuf.cod_bi2,
            mygov_flusso_tesoreria_iuf.cod_be1,
            mygov_flusso_tesoreria_iuf.cod_ib1,
            mygov_flusso_tesoreria_iuf.cod_ib2,
            mygov_flusso_tesoreria_iuf.cod_ib4,
            mygov_flusso_tesoreria_iuf.cod_tid,
            mygov_flusso_tesoreria_iuf.cod_dte,
            mygov_flusso_tesoreria_iuf.cod_dtn,
            mygov_flusso_tesoreria_iuf.cod_eri,
            mygov_flusso_tesoreria_iuf.cod_im2,
            mygov_flusso_tesoreria_iuf.cod_ma2,
            mygov_flusso_tesoreria_iuf.cod_ri3,
            mygov_flusso_tesoreria_iuf.cod_or1,
            mygov_flusso_tesoreria_iuf.cod_sc2,
            mygov_flusso_tesoreria_iuf.cod_tr1,
            mygov_flusso_tesoreria_iuf.cod_sec,
            mygov_flusso_tesoreria_iuf.cod_ior,
            mygov_flusso_tesoreria_iuf.cod_id_univoco_flusso,
            NULL::character varying AS cod_id_univoco_versamento,
            mygov_flusso_tesoreria_iuf.dt_acquisizione
           FROM public.mygov_flusso_tesoreria_iuf) tesoreria_iuf ON ((upper((tesoreria_iuf.cod_id_univoco_flusso)::text) = upper((rendicontazione.cod_identificativo_flusso)::text))))
     FULL JOIN ( SELECT mygov_flusso_tesoreria_iuv.mygov_ente_id,
            mygov_flusso_tesoreria_iuv.mygov_manage_flusso_id,
            mygov_flusso_tesoreria_iuv.cod_abi,
            mygov_flusso_tesoreria_iuv.cod_cab,
            mygov_flusso_tesoreria_iuv.cod_conto,
            mygov_flusso_tesoreria_iuv.cod_divisa,
            mygov_flusso_tesoreria_iuv.dt_data_contabile,
            mygov_flusso_tesoreria_iuv.dt_data_valuta,
            mygov_flusso_tesoreria_iuv.num_importo,
            mygov_flusso_tesoreria_iuv.cod_segno,
            mygov_flusso_tesoreria_iuv.de_causale,
            mygov_flusso_tesoreria_iuv.cod_numero_assegno,
            mygov_flusso_tesoreria_iuv.cod_riferimento_banca,
            mygov_flusso_tesoreria_iuv.cod_riferimento_cliente,
            mygov_flusso_tesoreria_iuv.dt_data_ordine,
            mygov_flusso_tesoreria_iuv.de_descrizione_ordinante,
            mygov_flusso_tesoreria_iuv.cod_bi2,
            mygov_flusso_tesoreria_iuv.cod_be1,
            mygov_flusso_tesoreria_iuv.cod_ib1,
            mygov_flusso_tesoreria_iuv.cod_ib2,
            mygov_flusso_tesoreria_iuv.cod_ib4,
            mygov_flusso_tesoreria_iuv.cod_tid,
            mygov_flusso_tesoreria_iuv.cod_dte,
            mygov_flusso_tesoreria_iuv.cod_dtn,
            mygov_flusso_tesoreria_iuv.cod_eri,
            mygov_flusso_tesoreria_iuv.cod_im2,
            mygov_flusso_tesoreria_iuv.cod_ma2,
            mygov_flusso_tesoreria_iuv.cod_ri3,
            mygov_flusso_tesoreria_iuv.cod_or1,
            mygov_flusso_tesoreria_iuv.cod_sc2,
            mygov_flusso_tesoreria_iuv.cod_tr1,
            mygov_flusso_tesoreria_iuv.cod_sec,
            mygov_flusso_tesoreria_iuv.cod_ior,
            NULL::character varying AS cod_id_univoco_flusso,
            mygov_flusso_tesoreria_iuv.cod_id_univoco_versamento,
            mygov_flusso_tesoreria_iuv.dt_acquisizione
           FROM public.mygov_flusso_tesoreria_iuv) tesoreria_iuv ON (((COALESCE(export.mygov_ente_id, rendicontazione.mygov_ente_id) = tesoreria_iuv.mygov_ente_id) AND ((COALESCE(export.cod_rp_silinviarp_id_univoco_versamento, rendicontazione.cod_dati_sing_pagam_identificativo_univoco_versamento))::text = (tesoreria_iuv.cod_id_univoco_versamento)::text))))
     FULL JOIN ( SELECT mygov_flusso_tesoreria.de_anno_bolletta,
            mygov_flusso_tesoreria.cod_bolletta,
            mygov_flusso_tesoreria.cod_conto,
            mygov_flusso_tesoreria.cod_id_dominio,
            mygov_flusso_tesoreria.de_causale,
            mygov_flusso_tesoreria.num_ip_bolletta,
            mygov_flusso_tesoreria.dt_bolletta,
            mygov_flusso_tesoreria.dt_ricezione,
            mygov_flusso_tesoreria.de_anno_documento,
            mygov_flusso_tesoreria.cod_documento,
            mygov_flusso_tesoreria.de_cognome,
            mygov_flusso_tesoreria.cod_abi,
            mygov_flusso_tesoreria.cod_cab,
            mygov_flusso_tesoreria.de_ae_provvisorio,
            mygov_flusso_tesoreria.cod_provvisorio,
            mygov_flusso_tesoreria.dt_data_valuta_regione,
            mygov_flusso_tesoreria.mygov_ente_id,
            mygov_flusso_tesoreria.cod_id_univoco_flusso,
            mygov_flusso_tesoreria.cod_id_univoco_versamento,
            mygov_flusso_tesoreria.dt_creazione,
            mygov_flusso_tesoreria.dt_ultima_modifica,
            mygov_flusso_tesoreria.dt_effettiva_sospeso,
            mygov_flusso_tesoreria.codice_gestionale_provvisorio
           FROM public.mygov_flusso_tesoreria
          WHERE ((mygov_flusso_tesoreria.cod_id_univoco_flusso IS NOT NULL) AND (mygov_flusso_tesoreria.cod_id_univoco_versamento IS NULL))) tesoreria_f2k_iuf ON (((COALESCE(export.mygov_ente_id, rendicontazione.mygov_ente_id) = tesoreria_f2k_iuf.mygov_ente_id) AND (upper((rendicontazione.cod_identificativo_flusso)::text) = upper((tesoreria_f2k_iuf.cod_id_univoco_flusso)::text)))))
     FULL JOIN ( SELECT mygov_flusso_tesoreria.de_anno_bolletta,
            mygov_flusso_tesoreria.cod_bolletta,
            mygov_flusso_tesoreria.cod_conto,
            mygov_flusso_tesoreria.cod_id_dominio,
            mygov_flusso_tesoreria.de_causale,
            mygov_flusso_tesoreria.num_ip_bolletta,
            mygov_flusso_tesoreria.dt_bolletta,
            mygov_flusso_tesoreria.dt_ricezione,
            mygov_flusso_tesoreria.de_anno_documento,
            mygov_flusso_tesoreria.cod_documento,
            mygov_flusso_tesoreria.de_cognome,
            mygov_flusso_tesoreria.cod_abi,
            mygov_flusso_tesoreria.cod_cab,
            mygov_flusso_tesoreria.de_ae_provvisorio,
            mygov_flusso_tesoreria.cod_provvisorio,
            mygov_flusso_tesoreria.dt_data_valuta_regione,
            mygov_flusso_tesoreria.mygov_ente_id,
            mygov_flusso_tesoreria.cod_id_univoco_flusso,
            mygov_flusso_tesoreria.cod_id_univoco_versamento,
            mygov_flusso_tesoreria.dt_creazione,
            mygov_flusso_tesoreria.dt_ultima_modifica,
            mygov_flusso_tesoreria.dt_effettiva_sospeso,
            mygov_flusso_tesoreria.codice_gestionale_provvisorio
           FROM public.mygov_flusso_tesoreria
          WHERE ((mygov_flusso_tesoreria.cod_id_univoco_flusso IS NULL) AND (mygov_flusso_tesoreria.cod_id_univoco_versamento IS NOT NULL))) tesoreria_f2k_iuv ON (((COALESCE(export.mygov_ente_id, rendicontazione.mygov_ente_id) = tesoreria_f2k_iuv.mygov_ente_id) AND ((COALESCE(export.cod_rp_silinviarp_id_univoco_versamento, rendicontazione.cod_dati_sing_pagam_identificativo_univoco_versamento))::text = (tesoreria_f2k_iuv.cod_id_univoco_versamento)::text))))
     FULL JOIN ( SELECT mygov_flusso_tesoreria.de_anno_bolletta,
            mygov_flusso_tesoreria.cod_bolletta,
            mygov_flusso_tesoreria.cod_conto,
            mygov_flusso_tesoreria.cod_id_dominio,
            mygov_flusso_tesoreria.de_causale,
            mygov_flusso_tesoreria.num_ip_bolletta,
            mygov_flusso_tesoreria.dt_bolletta,
            mygov_flusso_tesoreria.dt_ricezione,
            mygov_flusso_tesoreria.de_anno_documento,
            mygov_flusso_tesoreria.cod_documento,
            mygov_flusso_tesoreria.de_cognome,
            mygov_flusso_tesoreria.cod_abi,
            mygov_flusso_tesoreria.cod_cab,
            mygov_flusso_tesoreria.de_ae_provvisorio,
            mygov_flusso_tesoreria.cod_provvisorio,
            mygov_flusso_tesoreria.dt_data_valuta_regione,
            mygov_flusso_tesoreria.mygov_ente_id,
            mygov_flusso_tesoreria.cod_id_univoco_flusso,
            mygov_flusso_tesoreria.cod_id_univoco_versamento,
            mygov_flusso_tesoreria.dt_creazione,
            mygov_flusso_tesoreria.dt_ultima_modifica,
            mygov_flusso_tesoreria.dt_effettiva_sospeso,
            mygov_flusso_tesoreria.codice_gestionale_provvisorio
           FROM public.mygov_flusso_tesoreria
          WHERE ((mygov_flusso_tesoreria.cod_id_univoco_flusso IS NULL) AND (mygov_flusso_tesoreria.cod_id_univoco_versamento IS NULL))) tesoreria_f2k ON ((1 = 0)))
     LEFT JOIN public.mygov_classificazione_completezza classificazione ON (((((classificazione.mygov_classificazione_codice)::text = 'IUD_RT_IUF_TES'::text) AND (import.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento IS NOT NULL) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato IS NOT NULL) AND (import.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento = export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato) AND (((rendicontazione.num_dati_sing_pagam_singolo_importo_pagato IS NOT NULL) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato = rendicontazione.num_dati_sing_pagam_singolo_importo_pagato) AND (rendicontazione.num_importo_totale_pagamenti IS NOT NULL) AND (((tesoreria_iuf.num_importo IS NOT NULL) AND (rendicontazione.num_importo_totale_pagamenti = tesoreria_iuf.num_importo)) OR ((tesoreria_f2k_iuf.num_ip_bolletta IS NOT NULL) AND (rendicontazione.num_importo_totale_pagamenti = tesoreria_f2k_iuf.num_ip_bolletta)))) OR ((tesoreria_iuv.num_importo IS NOT NULL) AND (export.num_e_dati_pag_importo_totale_pagato = tesoreria_iuv.num_importo)) OR ((tesoreria_f2k_iuv.num_ip_bolletta IS NOT NULL) AND (export.num_e_dati_pag_importo_totale_pagato = tesoreria_f2k_iuv.num_ip_bolletta)))) OR (((classificazione.mygov_classificazione_codice)::text = 'IUD_RT_IUF'::text) AND (import.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento IS NOT NULL) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato IS NOT NULL) AND (import.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento = export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato) AND (rendicontazione.num_dati_sing_pagam_singolo_importo_pagato IS NOT NULL) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato = rendicontazione.num_dati_sing_pagam_singolo_importo_pagato)) OR (((classificazione.mygov_classificazione_codice)::text = 'RT_IUF_TES'::text) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato IS NOT NULL) AND (rendicontazione.num_dati_sing_pagam_singolo_importo_pagato IS NOT NULL) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato = rendicontazione.num_dati_sing_pagam_singolo_importo_pagato) AND (rendicontazione.num_importo_totale_pagamenti IS NOT NULL) AND (((tesoreria_iuf.num_importo IS NOT NULL) AND (rendicontazione.num_importo_totale_pagamenti = tesoreria_iuf.num_importo)) OR ((tesoreria_f2k_iuf.num_ip_bolletta IS NOT NULL) AND (rendicontazione.num_importo_totale_pagamenti = tesoreria_f2k_iuf.num_ip_bolletta)))) OR (((classificazione.mygov_classificazione_codice)::text = 'RT_IUF'::text) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato IS NOT NULL) AND (rendicontazione.num_dati_sing_pagam_singolo_importo_pagato IS NOT NULL) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato = rendicontazione.num_dati_sing_pagam_singolo_importo_pagato)) OR (((classificazione.mygov_classificazione_codice)::text = 'RT_NO_IUF'::text) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato IS NOT NULL) AND (NOT (((tesoreria_iuv.num_importo IS NOT NULL) AND (export.num_e_dati_pag_importo_totale_pagato = tesoreria_iuv.num_importo)) OR ((tesoreria_f2k_iuv.num_ip_bolletta IS NOT NULL) AND (export.num_e_dati_pag_importo_totale_pagato = tesoreria_f2k_iuv.num_ip_bolletta)) OR ((tesoreria_iuv.num_importo IS NOT NULL) AND (export.num_e_dati_pag_importo_totale_pagato <> tesoreria_iuv.num_importo)) OR ((tesoreria_f2k_iuv.num_ip_bolletta IS NOT NULL) AND (export.num_e_dati_pag_importo_totale_pagato <> tesoreria_f2k_iuv.num_ip_bolletta)))) AND ((rendicontazione.num_dati_sing_pagam_singolo_importo_pagato IS NULL) OR ((rendicontazione.num_dati_sing_pagam_singolo_importo_pagato IS NOT NULL) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato <> rendicontazione.num_dati_sing_pagam_singolo_importo_pagato)))) OR (((classificazione.mygov_classificazione_codice)::text = 'IUF_NO_TES'::text) AND (rendicontazione.num_importo_totale_pagamenti IS NOT NULL) AND (tesoreria_iuf.num_importo IS NULL) AND (tesoreria_iuv.num_importo IS NULL) AND (tesoreria_f2k_iuf.num_ip_bolletta IS NULL) AND (tesoreria_f2k_iuv.num_ip_bolletta IS NULL)) OR (((classificazione.mygov_classificazione_codice)::text = 'IUF_TES_DIV_IMP'::text) AND (rendicontazione.num_importo_totale_pagamenti IS NOT NULL) AND (((tesoreria_iuf.num_importo IS NOT NULL) AND (rendicontazione.num_importo_totale_pagamenti <> tesoreria_iuf.num_importo)) OR ((tesoreria_iuv.num_importo IS NOT NULL) AND (rendicontazione.num_importo_totale_pagamenti <> tesoreria_iuv.num_importo)) OR ((tesoreria_f2k_iuf.num_ip_bolletta IS NOT NULL) AND (rendicontazione.num_importo_totale_pagamenti <> tesoreria_f2k_iuf.num_ip_bolletta)) OR ((tesoreria_f2k_iuv.num_ip_bolletta IS NOT NULL) AND (rendicontazione.num_importo_totale_pagamenti <> tesoreria_f2k_iuv.num_ip_bolletta)))) OR (((classificazione.mygov_classificazione_codice)::text = 'TES_NO_IUF_OR_IUV'::text) AND ((tesoreria_iuf.num_importo IS NOT NULL) OR (tesoreria_iuv.num_importo IS NOT NULL) OR ((tesoreria_f2k_iuf.num_ip_bolletta IS NOT NULL) AND (tesoreria_f2k_iuf.cod_id_univoco_flusso IS NOT NULL) AND (tesoreria_f2k_iuf.cod_id_univoco_versamento IS NULL)) OR ((tesoreria_f2k_iuv.num_ip_bolletta IS NOT NULL) AND (tesoreria_f2k_iuv.cod_id_univoco_flusso IS NULL) AND (tesoreria_f2k_iuv.cod_id_univoco_versamento IS NOT NULL))) AND (rendicontazione.num_importo_totale_pagamenti IS NULL) AND (export.num_e_dati_pag_importo_totale_pagato IS NULL) AND (import.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento IS NULL)) OR (((classificazione.mygov_classificazione_codice)::text = 'IUV_NO_RT'::text) AND (rendicontazione.num_importo_totale_pagamenti IS NOT NULL) AND (export.num_e_dati_pag_importo_totale_pagato IS NULL)) OR (((classificazione.mygov_classificazione_codice)::text = 'IUD_NO_RT'::text) AND (import.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento IS NOT NULL) AND ((export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato IS NULL) OR ((export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato IS NOT NULL) AND (import.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento <> export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato)))) OR (((classificazione.mygov_classificazione_codice)::text = 'RT_NO_IUD'::text) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato IS NOT NULL) AND (import.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento IS NULL)) OR (((classificazione.mygov_classificazione_codice)::text = 'TES_NO_MATCH'::text) AND (import.num_rp_dati_vers_dati_sing_vers_importo_singolo_versamento IS NULL) AND (export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato IS NULL) AND (rendicontazione.num_importo_totale_pagamenti IS NULL) AND (tesoreria_iuf.num_importo IS NULL) AND (tesoreria_iuv.num_importo IS NULL) AND (tesoreria_f2k_iuf.cod_id_univoco_flusso IS NULL) AND (tesoreria_f2k_iuf.cod_id_univoco_versamento IS NULL) AND (tesoreria_f2k_iuv.cod_id_univoco_flusso IS NULL) AND (tesoreria_f2k_iuv.cod_id_univoco_versamento IS NULL) AND (tesoreria_f2k.cod_id_univoco_flusso IS NULL) AND (tesoreria_f2k.cod_id_univoco_versamento IS NULL)) OR (((classificazione.mygov_classificazione_codice)::text = 'RT_TES'::text) AND (export.num_e_dati_pag_importo_totale_pagato IS NOT NULL) AND (rendicontazione.num_dati_sing_pagam_singolo_importo_pagato IS NULL) AND (rendicontazione.num_importo_totale_pagamenti IS NULL) AND (((tesoreria_iuv.num_importo IS NOT NULL) AND (export.num_e_dati_pag_importo_totale_pagato = tesoreria_iuv.num_importo)) OR ((tesoreria_f2k_iuv.num_ip_bolletta IS NOT NULL) AND (export.num_e_dati_pag_importo_totale_pagato = tesoreria_f2k_iuv.num_ip_bolletta)))))));


ALTER TABLE public.v_mygov_import_export_rendicontazione_tesoreria_completa OWNER TO postgres;

--
-- TOC entry 256 (class 1259 OID 33125)
-- Name: vm_statistica_ente_anno_mese; Type: MATERIALIZED VIEW; Schema: public; Owner: mypay4
--

CREATE MATERIALIZED VIEW public.vm_statistica_ente_anno_mese AS
 SELECT subq.mygov_ente_id,
    subq.anno,
    subq.mese,
    sum(subq.num_pag) AS num_pag,
    sum(subq.imp_pag) AS imp_pag,
    sum(subq.imp_rend) AS imp_rend,
    sum(subq.imp_inc) AS imp_inc
   FROM ( SELECT mygov_flusso_export.mygov_ente_id,
            (date_part('years'::text, mygov_flusso_export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS anno,
            (date_part('month'::text, mygov_flusso_export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS mese,
            count(*) AS num_pag,
            sum(mygov_flusso_export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato) AS imp_pag,
            0 AS imp_rend,
            0 AS imp_inc
           FROM public.mygov_flusso_export
          GROUP BY mygov_flusso_export.mygov_ente_id, ((date_part('years'::text, mygov_flusso_export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('month'::text, mygov_flusso_export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer)
        UNION
         SELECT mygov_import_export_rendicontazione_tesoreria_completa.mygov_ente_id,
            (date_part('years'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_regolamento_r))::integer AS anno,
            (date_part('month'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_regolamento_r))::integer AS mese,
            0 AS num_pag,
            0 AS imp_pag,
            sum(mygov_import_export_rendicontazione_tesoreria_completa.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e) AS imp_rend,
            0 AS imp_inc
           FROM public.mygov_import_export_rendicontazione_tesoreria_completa
          WHERE ((mygov_import_export_rendicontazione_tesoreria_completa.classificazione_completezza)::text = 'RT_IUF'::text)
          GROUP BY mygov_import_export_rendicontazione_tesoreria_completa.mygov_ente_id, ((date_part('years'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_regolamento_r))::integer), ((date_part('month'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_regolamento_r))::integer)
        UNION
         SELECT mygov_import_export_rendicontazione_tesoreria_completa.mygov_ente_id,
            (date_part('years'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_valuta_t))::integer AS anno,
            (date_part('month'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_valuta_t))::integer AS mese,
            0 AS num_pag,
            0 AS imp_pag,
            0 AS imp_rend,
            sum(mygov_import_export_rendicontazione_tesoreria_completa.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e) AS imp_inc
           FROM public.mygov_import_export_rendicontazione_tesoreria_completa
          WHERE (((mygov_import_export_rendicontazione_tesoreria_completa.classificazione_completezza)::text = 'RT_IUF_TES'::text) OR ((mygov_import_export_rendicontazione_tesoreria_completa.classificazione_completezza)::text = 'RT_TES'::text))
          GROUP BY mygov_import_export_rendicontazione_tesoreria_completa.mygov_ente_id, ((date_part('years'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_valuta_t))::integer), ((date_part('month'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_valuta_t))::integer)) subq
  GROUP BY subq.mygov_ente_id, subq.anno, subq.mese
  ORDER BY subq.mygov_ente_id, subq.anno, subq.mese
  WITH NO DATA;


ALTER TABLE public.vm_statistica_ente_anno_mese OWNER TO mypay4;

--
-- TOC entry 257 (class 1259 OID 33133)
-- Name: vm_statistica_ente_anno_mese_giorno; Type: MATERIALIZED VIEW; Schema: public; Owner: mypay4
--

CREATE MATERIALIZED VIEW public.vm_statistica_ente_anno_mese_giorno AS
 SELECT subq.mygov_ente_id,
    subq.anno,
    subq.mese,
    subq.giorno,
    sum(subq.num_pag) AS num_pag,
    sum(subq.imp_pag) AS imp_pag,
    sum(subq.imp_rend) AS imp_rend,
    sum(subq.imp_inc) AS imp_inc
   FROM ( SELECT mygov_flusso_export.mygov_ente_id,
            (date_part('years'::text, mygov_flusso_export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS anno,
            (date_part('month'::text, mygov_flusso_export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS mese,
            (date_part('day'::text, mygov_flusso_export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS giorno,
            count(*) AS num_pag,
            sum(mygov_flusso_export.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato) AS imp_pag,
            0 AS imp_rend,
            0 AS imp_inc
           FROM public.mygov_flusso_export
          GROUP BY mygov_flusso_export.mygov_ente_id, ((date_part('years'::text, mygov_flusso_export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('month'::text, mygov_flusso_export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('day'::text, mygov_flusso_export.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer)
        UNION
         SELECT mygov_import_export_rendicontazione_tesoreria_completa.mygov_ente_id,
            (date_part('years'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_regolamento_r))::integer AS anno,
            (date_part('month'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_regolamento_r))::integer AS mese,
            (date_part('day'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_regolamento_r))::integer AS giorno,
            0 AS num_pag,
            0 AS imp_pag,
            sum(mygov_import_export_rendicontazione_tesoreria_completa.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e) AS imp_rend,
            0 AS imp_inc
           FROM public.mygov_import_export_rendicontazione_tesoreria_completa
          WHERE ((mygov_import_export_rendicontazione_tesoreria_completa.classificazione_completezza)::text = 'RT_IUF'::text)
          GROUP BY mygov_import_export_rendicontazione_tesoreria_completa.mygov_ente_id, ((date_part('years'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_regolamento_r))::integer), ((date_part('month'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_regolamento_r))::integer), ((date_part('day'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_regolamento_r))::integer)
        UNION
         SELECT mygov_import_export_rendicontazione_tesoreria_completa.mygov_ente_id,
            (date_part('years'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_valuta_t))::integer AS anno,
            (date_part('month'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_valuta_t))::integer AS mese,
            (date_part('day'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_valuta_t))::integer AS giorno,
            0 AS num_pag,
            0 AS imp_pag,
            0 AS imp_rend,
            sum(mygov_import_export_rendicontazione_tesoreria_completa.num_e_dati_pag_dati_sing_pag_singolo_importo_pagato_e) AS imp_inc
           FROM public.mygov_import_export_rendicontazione_tesoreria_completa
          WHERE (((mygov_import_export_rendicontazione_tesoreria_completa.classificazione_completezza)::text = 'RT_IUF_TES'::text) OR ((mygov_import_export_rendicontazione_tesoreria_completa.classificazione_completezza)::text = 'RT_TES'::text))
          GROUP BY mygov_import_export_rendicontazione_tesoreria_completa.mygov_ente_id, ((date_part('years'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_valuta_t))::integer), ((date_part('month'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_valuta_t))::integer), ((date_part('day'::text, mygov_import_export_rendicontazione_tesoreria_completa.dt_data_valuta_t))::integer)) subq
  GROUP BY subq.mygov_ente_id, subq.anno, subq.mese, subq.giorno
  ORDER BY subq.mygov_ente_id, subq.anno, subq.mese, subq.giorno
  WITH NO DATA;


ALTER TABLE public.vm_statistica_ente_anno_mese_giorno OWNER TO mypay4;

--
-- TOC entry 258 (class 1259 OID 33141)
-- Name: vm_statistica_ente_anno_mese_giorno_uff_td; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.vm_statistica_ente_anno_mese_giorno_uff_td AS
 SELECT subq.mygov_ente_id,
    subq.anno,
    subq.mese,
    subq.giorno,
    subq.cod_uff,
    COALESCE(uff.de_ufficio, 'n/a'::text) AS de_uff,
    subq.cod_td,
    td.de_tipo AS de_td,
    sum(subq.imp_pag) AS imp_pag,
    sum(subq.imp_rend) AS imp_rend,
    sum(subq.imp_inc) AS imp_inc
   FROM ((( SELECT p.mygov_ente_id,
            (date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS anno,
            (date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS mese,
            (date_part('day'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS giorno,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            sum(ad.num_importo) AS imp_pag,
            0 AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_flusso_export p
             JOIN public.mygov_accertamento_dettaglio ad ON ((((p.cod_iud)::text = (ad.cod_iud)::text) AND ((p.cod_tipo_dovuto)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (p.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text))
          GROUP BY p.mygov_ente_id, ((date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('day'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_regolamento_r))::integer AS anno,
            (date_part('month'::text, r.dt_data_regolamento_r))::integer AS mese,
            (date_part('day'::text, r.dt_data_regolamento_r))::integer AS giorno,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            0 AS imp_pag,
            sum(ad.num_importo) AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND ((r.classificazione_completezza)::text = 'RT_IUF'::text))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_regolamento_r))::integer), ((date_part('month'::text, r.dt_data_regolamento_r))::integer), ((date_part('day'::text, r.dt_data_regolamento_r))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_valuta_t))::integer AS anno,
            (date_part('month'::text, r.dt_data_valuta_t))::integer AS mese,
            (date_part('day'::text, r.dt_data_valuta_t))::integer AS giorno,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            0 AS imp_pag,
            0 AS imp_rend,
            sum(ad.num_importo) AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND (((r.classificazione_completezza)::text = 'RT_IUF_TES'::text) OR ((r.classificazione_completezza)::text = 'RT_TES'::text)))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_valuta_t))::integer), ((date_part('month'::text, r.dt_data_valuta_t))::integer), ((date_part('day'::text, r.dt_data_valuta_t))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto) subq
     JOIN public.mygov_ente_tipo_dovuto td ON (((td.mygov_ente_id = subq.mygov_ente_id) AND ((td.cod_tipo)::text = (subq.cod_td)::text))))
     LEFT JOIN ( SELECT mygov_anagrafica_uff_cap_acc.mygov_ente_id,
            mygov_anagrafica_uff_cap_acc.cod_tipo_dovuto,
            mygov_anagrafica_uff_cap_acc.cod_ufficio,
            mygov_anagrafica_uff_cap_acc.de_anno_esercizio,
            max((mygov_anagrafica_uff_cap_acc.de_ufficio)::text) AS de_ufficio
           FROM public.mygov_anagrafica_uff_cap_acc
          WHERE ((mygov_anagrafica_uff_cap_acc.de_ufficio)::text <> 'n/a'::text)
          GROUP BY mygov_anagrafica_uff_cap_acc.mygov_ente_id, mygov_anagrafica_uff_cap_acc.cod_tipo_dovuto, mygov_anagrafica_uff_cap_acc.cod_ufficio, mygov_anagrafica_uff_cap_acc.de_anno_esercizio) uff ON (((uff.mygov_ente_id = subq.mygov_ente_id) AND ((uff.cod_tipo_dovuto)::text = (subq.cod_td)::text) AND ((uff.cod_ufficio)::text = (subq.cod_uff)::text) AND ((uff.de_anno_esercizio)::text = (subq.anno)::text))))
  GROUP BY subq.mygov_ente_id, subq.anno, subq.mese, subq.giorno, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo
  ORDER BY subq.mygov_ente_id, subq.anno, subq.mese, subq.giorno, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo
  WITH NO DATA;


ALTER TABLE public.vm_statistica_ente_anno_mese_giorno_uff_td OWNER TO postgres;

--
-- TOC entry 259 (class 1259 OID 33149)
-- Name: vm_statistica_ente_anno_mese_giorno_uff_td_cap; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.vm_statistica_ente_anno_mese_giorno_uff_td_cap AS
 SELECT subq.mygov_ente_id,
    subq.anno,
    subq.mese,
    subq.giorno,
    subq.cod_uff,
    COALESCE(uff.de_ufficio, 'n/a'::text) AS de_uff,
    subq.cod_td,
    td.de_tipo AS de_td,
    subq.cod_cap,
    COALESCE(uff.de_capitolo, 'n/a'::text) AS de_cap,
    sum(subq.imp_pag) AS imp_pag,
    sum(subq.imp_rend) AS imp_rend,
    sum(subq.imp_inc) AS imp_inc
   FROM ((( SELECT p.mygov_ente_id,
            (date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS anno,
            (date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS mese,
            (date_part('day'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS giorno,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            sum(ad.num_importo) AS imp_pag,
            0 AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_flusso_export p
             JOIN public.mygov_accertamento_dettaglio ad ON ((((p.cod_iud)::text = (ad.cod_iud)::text) AND ((p.cod_tipo_dovuto)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (p.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text))
          GROUP BY p.mygov_ente_id, ((date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('day'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_regolamento_r))::integer AS anno,
            (date_part('month'::text, r.dt_data_regolamento_r))::integer AS mese,
            (date_part('day'::text, r.dt_data_regolamento_r))::integer AS giorno,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            0 AS imp_pag,
            sum(ad.num_importo) AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND ((r.classificazione_completezza)::text = 'RT_IUF'::text))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_regolamento_r))::integer), ((date_part('month'::text, r.dt_data_regolamento_r))::integer), ((date_part('day'::text, r.dt_data_regolamento_r))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_valuta_t))::integer AS anno,
            (date_part('month'::text, r.dt_data_valuta_t))::integer AS mese,
            (date_part('day'::text, r.dt_data_valuta_t))::integer AS giorno,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            0 AS imp_pag,
            0 AS imp_rend,
            sum(ad.num_importo) AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND (((r.classificazione_completezza)::text = 'RT_IUF_TES'::text) OR ((r.classificazione_completezza)::text = 'RT_TES'::text)))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_valuta_t))::integer), ((date_part('month'::text, r.dt_data_valuta_t))::integer), ((date_part('day'::text, r.dt_data_valuta_t))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo) subq
     JOIN public.mygov_ente_tipo_dovuto td ON (((td.mygov_ente_id = subq.mygov_ente_id) AND ((td.cod_tipo)::text = (subq.cod_td)::text))))
     LEFT JOIN ( SELECT mygov_anagrafica_uff_cap_acc.mygov_ente_id,
            mygov_anagrafica_uff_cap_acc.cod_tipo_dovuto,
            mygov_anagrafica_uff_cap_acc.cod_ufficio,
            mygov_anagrafica_uff_cap_acc.de_anno_esercizio,
            mygov_anagrafica_uff_cap_acc.cod_capitolo,
            max((mygov_anagrafica_uff_cap_acc.de_capitolo)::text) AS de_capitolo,
            max((mygov_anagrafica_uff_cap_acc.de_ufficio)::text) AS de_ufficio
           FROM public.mygov_anagrafica_uff_cap_acc
          WHERE (((mygov_anagrafica_uff_cap_acc.de_ufficio)::text <> 'n/a'::text) AND ((mygov_anagrafica_uff_cap_acc.de_capitolo)::text <> 'n/a'::text))
          GROUP BY mygov_anagrafica_uff_cap_acc.mygov_ente_id, mygov_anagrafica_uff_cap_acc.cod_tipo_dovuto, mygov_anagrafica_uff_cap_acc.cod_ufficio, mygov_anagrafica_uff_cap_acc.de_anno_esercizio, mygov_anagrafica_uff_cap_acc.cod_capitolo) uff ON (((uff.mygov_ente_id = subq.mygov_ente_id) AND ((uff.cod_tipo_dovuto)::text = (subq.cod_td)::text) AND ((uff.cod_ufficio)::text = (subq.cod_uff)::text) AND ((uff.de_anno_esercizio)::text = (subq.anno)::text))))
  GROUP BY subq.mygov_ente_id, subq.anno, subq.mese, subq.giorno, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo, subq.cod_cap, uff.de_capitolo
  ORDER BY subq.mygov_ente_id, subq.anno, subq.mese, subq.giorno, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo, subq.cod_cap, uff.de_capitolo
  WITH NO DATA;


ALTER TABLE public.vm_statistica_ente_anno_mese_giorno_uff_td_cap OWNER TO postgres;

--
-- TOC entry 260 (class 1259 OID 33157)
-- Name: vm_statistica_ente_anno_mese_giorno_uff_td_cap_acc; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.vm_statistica_ente_anno_mese_giorno_uff_td_cap_acc AS
 SELECT subq.mygov_ente_id,
    subq.anno,
    subq.mese,
    subq.giorno,
    subq.cod_uff,
    uff.de_ufficio AS de_uff,
    subq.cod_td,
    td.de_tipo AS de_td,
    subq.cod_cap,
    uff.de_capitolo AS de_cap,
    subq.cod_acc,
    uff.de_accertamento AS de_acc,
    sum(subq.imp_pag) AS imp_pag,
    sum(subq.imp_rend) AS imp_rend,
    sum(subq.imp_inc) AS imp_inc
   FROM ((( SELECT p.mygov_ente_id,
            (date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS anno,
            (date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS mese,
            (date_part('day'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS giorno,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            ad.cod_accertamento AS cod_acc,
            sum(ad.num_importo) AS imp_pag,
            0 AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_flusso_export p
             JOIN public.mygov_accertamento_dettaglio ad ON ((((p.cod_iud)::text = (ad.cod_iud)::text) AND ((p.cod_tipo_dovuto)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (p.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND ((ad.cod_accertamento)::text <> 'n/a'::text))
          GROUP BY p.mygov_ente_id, ((date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('day'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo, ad.cod_accertamento
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_regolamento_r))::integer AS anno,
            (date_part('month'::text, r.dt_data_regolamento_r))::integer AS mese,
            (date_part('day'::text, r.dt_data_regolamento_r))::integer AS giorno,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            ad.cod_accertamento AS cod_acc,
            0 AS imp_pag,
            sum(ad.num_importo) AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND ((ad.cod_accertamento)::text <> 'n/a'::text) AND ((r.classificazione_completezza)::text = 'RT_IUF'::text))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_regolamento_r))::integer), ((date_part('month'::text, r.dt_data_regolamento_r))::integer), ((date_part('day'::text, r.dt_data_regolamento_r))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo, ad.cod_accertamento
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_valuta_t))::integer AS anno,
            (date_part('month'::text, r.dt_data_valuta_t))::integer AS mese,
            (date_part('day'::text, r.dt_data_valuta_t))::integer AS giorno,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            ad.cod_accertamento AS cod_acc,
            0 AS imp_pag,
            0 AS imp_rend,
            sum(ad.num_importo) AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND ((ad.cod_accertamento)::text <> 'n/a'::text) AND (((r.classificazione_completezza)::text = 'RT_IUF_TES'::text) OR ((r.classificazione_completezza)::text = 'RT_TES'::text)))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_valuta_t))::integer), ((date_part('month'::text, r.dt_data_valuta_t))::integer), ((date_part('day'::text, r.dt_data_valuta_t))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo, ad.cod_accertamento) subq
     JOIN public.mygov_ente_tipo_dovuto td ON (((td.mygov_ente_id = subq.mygov_ente_id) AND ((td.cod_tipo)::text = (subq.cod_td)::text))))
     LEFT JOIN public.mygov_anagrafica_uff_cap_acc uff ON (((uff.mygov_ente_id = subq.mygov_ente_id) AND ((uff.cod_tipo_dovuto)::text = (subq.cod_td)::text) AND ((uff.cod_ufficio)::text = (subq.cod_uff)::text) AND ((uff.cod_capitolo)::text = (subq.cod_cap)::text) AND ((uff.cod_accertamento)::text = (subq.cod_acc)::text) AND ((uff.de_anno_esercizio)::text = (subq.anno)::text))))
  GROUP BY subq.mygov_ente_id, subq.anno, subq.mese, subq.giorno, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo, subq.cod_cap, uff.de_capitolo, subq.cod_acc, uff.de_accertamento
  ORDER BY subq.mygov_ente_id, subq.anno, subq.mese, subq.giorno, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo, subq.cod_cap, uff.de_capitolo, subq.cod_acc, uff.de_accertamento
  WITH NO DATA;


ALTER TABLE public.vm_statistica_ente_anno_mese_giorno_uff_td_cap_acc OWNER TO postgres;

--
-- TOC entry 261 (class 1259 OID 33165)
-- Name: vm_statistica_ente_anno_mese_uff_td; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.vm_statistica_ente_anno_mese_uff_td AS
 SELECT subq.mygov_ente_id,
    subq.anno,
    subq.mese,
    subq.cod_uff,
    COALESCE(uff.de_ufficio, 'n/a'::text) AS de_uff,
    subq.cod_td,
    td.de_tipo AS de_td,
    sum(subq.imp_pag) AS imp_pag,
    sum(subq.imp_rend) AS imp_rend,
    sum(subq.imp_inc) AS imp_inc
   FROM ((( SELECT p.mygov_ente_id,
            (date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS anno,
            (date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS mese,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            sum(ad.num_importo) AS imp_pag,
            0 AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_flusso_export p
             JOIN public.mygov_accertamento_dettaglio ad ON ((((p.cod_iud)::text = (ad.cod_iud)::text) AND ((p.cod_tipo_dovuto)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (p.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text))
          GROUP BY p.mygov_ente_id, ((date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_regolamento_r))::integer AS anno,
            (date_part('month'::text, r.dt_data_regolamento_r))::integer AS mese,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            0 AS imp_pag,
            sum(ad.num_importo) AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND ((r.classificazione_completezza)::text = 'RT_IUF'::text))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_regolamento_r))::integer), ((date_part('month'::text, r.dt_data_regolamento_r))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_valuta_t))::integer AS anno,
            (date_part('month'::text, r.dt_data_valuta_t))::integer AS mese,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            0 AS imp_pag,
            0 AS imp_rend,
            sum(ad.num_importo) AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND (((r.classificazione_completezza)::text = 'RT_IUF_TES'::text) OR ((r.classificazione_completezza)::text = 'RT_TES'::text)))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_valuta_t))::integer), ((date_part('month'::text, r.dt_data_valuta_t))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto) subq
     JOIN public.mygov_ente_tipo_dovuto td ON (((td.mygov_ente_id = subq.mygov_ente_id) AND ((td.cod_tipo)::text = (subq.cod_td)::text))))
     LEFT JOIN ( SELECT mygov_anagrafica_uff_cap_acc.mygov_ente_id,
            mygov_anagrafica_uff_cap_acc.cod_tipo_dovuto,
            mygov_anagrafica_uff_cap_acc.cod_ufficio,
            mygov_anagrafica_uff_cap_acc.de_anno_esercizio,
            max((mygov_anagrafica_uff_cap_acc.de_ufficio)::text) AS de_ufficio
           FROM public.mygov_anagrafica_uff_cap_acc
          WHERE ((mygov_anagrafica_uff_cap_acc.de_ufficio)::text <> 'n/a'::text)
          GROUP BY mygov_anagrafica_uff_cap_acc.mygov_ente_id, mygov_anagrafica_uff_cap_acc.cod_tipo_dovuto, mygov_anagrafica_uff_cap_acc.cod_ufficio, mygov_anagrafica_uff_cap_acc.de_anno_esercizio) uff ON (((uff.mygov_ente_id = subq.mygov_ente_id) AND ((uff.cod_tipo_dovuto)::text = (subq.cod_td)::text) AND ((uff.cod_ufficio)::text = (subq.cod_uff)::text) AND ((uff.de_anno_esercizio)::text = (subq.anno)::text))))
  GROUP BY subq.mygov_ente_id, subq.anno, subq.mese, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo
  ORDER BY subq.mygov_ente_id, subq.anno, subq.mese, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo
  WITH NO DATA;


ALTER TABLE public.vm_statistica_ente_anno_mese_uff_td OWNER TO postgres;

--
-- TOC entry 262 (class 1259 OID 33173)
-- Name: vm_statistica_ente_anno_mese_uff_td_cap; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.vm_statistica_ente_anno_mese_uff_td_cap AS
 SELECT subq.mygov_ente_id,
    subq.anno,
    subq.mese,
    subq.cod_uff,
    COALESCE(uff.de_ufficio, 'n/a'::text) AS de_uff,
    subq.cod_td,
    td.de_tipo AS de_td,
    subq.cod_cap,
    COALESCE(uff.de_capitolo, 'n/a'::text) AS de_cap,
    sum(subq.imp_pag) AS imp_pag,
    sum(subq.imp_rend) AS imp_rend,
    sum(subq.imp_inc) AS imp_inc
   FROM ((( SELECT p.mygov_ente_id,
            (date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS anno,
            (date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS mese,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            sum(ad.num_importo) AS imp_pag,
            0 AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_flusso_export p
             JOIN public.mygov_accertamento_dettaglio ad ON ((((p.cod_iud)::text = (ad.cod_iud)::text) AND ((p.cod_tipo_dovuto)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (p.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text))
          GROUP BY p.mygov_ente_id, ((date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_regolamento_r))::integer AS anno,
            (date_part('month'::text, r.dt_data_regolamento_r))::integer AS mese,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            0 AS imp_pag,
            sum(ad.num_importo) AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND ((r.classificazione_completezza)::text = 'RT_IUF'::text))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_regolamento_r))::integer), ((date_part('month'::text, r.dt_data_regolamento_r))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_valuta_t))::integer AS anno,
            (date_part('month'::text, r.dt_data_valuta_t))::integer AS mese,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            0 AS imp_pag,
            0 AS imp_rend,
            sum(ad.num_importo) AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND (((r.classificazione_completezza)::text = 'RT_IUF_TES'::text) OR ((r.classificazione_completezza)::text = 'RT_TES'::text)))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_valuta_t))::integer), ((date_part('month'::text, r.dt_data_valuta_t))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo) subq
     JOIN public.mygov_ente_tipo_dovuto td ON (((td.mygov_ente_id = subq.mygov_ente_id) AND ((td.cod_tipo)::text = (subq.cod_td)::text))))
     LEFT JOIN ( SELECT mygov_anagrafica_uff_cap_acc.mygov_ente_id,
            mygov_anagrafica_uff_cap_acc.cod_tipo_dovuto,
            mygov_anagrafica_uff_cap_acc.cod_ufficio,
            mygov_anagrafica_uff_cap_acc.de_anno_esercizio,
            mygov_anagrafica_uff_cap_acc.cod_capitolo,
            max((mygov_anagrafica_uff_cap_acc.de_capitolo)::text) AS de_capitolo,
            max((mygov_anagrafica_uff_cap_acc.de_ufficio)::text) AS de_ufficio
           FROM public.mygov_anagrafica_uff_cap_acc
          WHERE (((mygov_anagrafica_uff_cap_acc.de_ufficio)::text <> 'n/a'::text) AND ((mygov_anagrafica_uff_cap_acc.de_capitolo)::text <> 'n/a'::text))
          GROUP BY mygov_anagrafica_uff_cap_acc.mygov_ente_id, mygov_anagrafica_uff_cap_acc.cod_tipo_dovuto, mygov_anagrafica_uff_cap_acc.cod_ufficio, mygov_anagrafica_uff_cap_acc.de_anno_esercizio, mygov_anagrafica_uff_cap_acc.cod_capitolo) uff ON (((uff.mygov_ente_id = subq.mygov_ente_id) AND ((uff.cod_tipo_dovuto)::text = (subq.cod_td)::text) AND ((uff.cod_ufficio)::text = (subq.cod_uff)::text) AND ((uff.de_anno_esercizio)::text = (subq.anno)::text))))
  GROUP BY subq.mygov_ente_id, subq.anno, subq.mese, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo, subq.cod_cap, uff.de_capitolo
  ORDER BY subq.mygov_ente_id, subq.anno, subq.mese, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo, subq.cod_cap, uff.de_capitolo
  WITH NO DATA;


ALTER TABLE public.vm_statistica_ente_anno_mese_uff_td_cap OWNER TO postgres;

--
-- TOC entry 263 (class 1259 OID 33181)
-- Name: vm_statistica_ente_anno_mese_uff_td_cap_acc; Type: MATERIALIZED VIEW; Schema: public; Owner: postgres
--

CREATE MATERIALIZED VIEW public.vm_statistica_ente_anno_mese_uff_td_cap_acc AS
 SELECT subq.mygov_ente_id,
    subq.anno,
    subq.mese,
    subq.cod_uff,
    uff.de_ufficio AS de_uff,
    subq.cod_td,
    td.de_tipo AS de_td,
    subq.cod_cap,
    uff.de_capitolo AS de_cap,
    subq.cod_acc,
    uff.de_accertamento AS de_acc,
    sum(subq.imp_pag) AS imp_pag,
    sum(subq.imp_rend) AS imp_rend,
    sum(subq.imp_inc) AS imp_inc
   FROM ((( SELECT p.mygov_ente_id,
            (date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS anno,
            (date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer AS mese,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            ad.cod_accertamento AS cod_acc,
            sum(ad.num_importo) AS imp_pag,
            0 AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_flusso_export p
             JOIN public.mygov_accertamento_dettaglio ad ON ((((p.cod_iud)::text = (ad.cod_iud)::text) AND ((p.cod_tipo_dovuto)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (p.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND ((ad.cod_accertamento)::text <> 'n/a'::text))
          GROUP BY p.mygov_ente_id, ((date_part('years'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ((date_part('month'::text, p.dt_e_dati_pag_dati_sing_pag_data_esito_singolo_pagamento))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo, ad.cod_accertamento
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_regolamento_r))::integer AS anno,
            (date_part('month'::text, r.dt_data_regolamento_r))::integer AS mese,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            ad.cod_accertamento AS cod_acc,
            0 AS imp_pag,
            sum(ad.num_importo) AS imp_rend,
            0 AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND ((ad.cod_accertamento)::text <> 'n/a'::text) AND ((r.classificazione_completezza)::text = 'RT_IUF'::text))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_regolamento_r))::integer), ((date_part('month'::text, r.dt_data_regolamento_r))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo, ad.cod_accertamento
        UNION
         SELECT r.mygov_ente_id,
            (date_part('years'::text, r.dt_data_valuta_t))::integer AS anno,
            (date_part('month'::text, r.dt_data_valuta_t))::integer AS mese,
            ad.cod_ufficio AS cod_uff,
            ad.cod_tipo_dovuto AS cod_td,
            ad.cod_capitolo AS cod_cap,
            ad.cod_accertamento AS cod_acc,
            0 AS imp_pag,
            0 AS imp_rend,
            sum(ad.num_importo) AS imp_inc
           FROM ((((public.mygov_import_export_rendicontazione_tesoreria_completa r
             JOIN public.mygov_accertamento_dettaglio ad ON ((((r.cod_iud_e)::text = (ad.cod_iud)::text) AND ((r.cod_tipo_dovuto_e)::text = (ad.cod_tipo_dovuto)::text))))
             JOIN public.mygov_accertamento a ON ((a.mygov_accertamento_id = ad.mygov_accertamento_id)))
             JOIN public.mygov_anagrafica_stato st ON ((st.mygov_anagrafica_stato_id = a.mygov_anagrafica_stato_id)))
             JOIN public.mygov_ente e ON ((((e.cod_ipa_ente)::text = (ad.cod_ipa_ente)::text) AND (r.mygov_ente_id = e.mygov_ente_id))))
          WHERE (((st.de_tipo_stato)::text = 'ACCERTAMENTO'::text) AND ((st.cod_stato)::text = 'CHIUSO'::text) AND ((ad.cod_accertamento)::text <> 'n/a'::text) AND (((r.classificazione_completezza)::text = 'RT_IUF_TES'::text) OR ((r.classificazione_completezza)::text = 'RT_TES'::text)))
          GROUP BY r.mygov_ente_id, ((date_part('years'::text, r.dt_data_valuta_t))::integer), ((date_part('month'::text, r.dt_data_valuta_t))::integer), ad.cod_ufficio, ad.cod_tipo_dovuto, ad.cod_capitolo, ad.cod_accertamento) subq
     JOIN public.mygov_ente_tipo_dovuto td ON (((td.mygov_ente_id = subq.mygov_ente_id) AND ((td.cod_tipo)::text = (subq.cod_td)::text))))
     LEFT JOIN public.mygov_anagrafica_uff_cap_acc uff ON (((uff.mygov_ente_id = subq.mygov_ente_id) AND ((uff.cod_tipo_dovuto)::text = (subq.cod_td)::text) AND ((uff.cod_ufficio)::text = (subq.cod_uff)::text) AND ((uff.cod_capitolo)::text = (subq.cod_cap)::text) AND ((uff.cod_accertamento)::text = (subq.cod_acc)::text) AND ((uff.de_anno_esercizio)::text = (subq.anno)::text))))
  GROUP BY subq.mygov_ente_id, subq.anno, subq.mese, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo, subq.cod_cap, uff.de_capitolo, subq.cod_acc, uff.de_accertamento
  ORDER BY subq.mygov_ente_id, subq.anno, subq.mese, subq.cod_uff, uff.de_ufficio, subq.cod_td, td.de_tipo, subq.cod_cap, uff.de_capitolo, subq.cod_acc, uff.de_accertamento
  WITH NO DATA;


ALTER TABLE public.vm_statistica_ente_anno_mese_uff_td_cap_acc OWNER TO postgres;

--
-- TOC entry 2388 (class 2606 OID 33423)
-- Name: mygov_accertamento_dettaglio mygov_accertamento_dettaglio_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_accertamento_dettaglio
    ADD CONSTRAINT mygov_accertamento_dettaglio_pkey PRIMARY KEY (mygov_accertamento_dettaglio_id);


--
-- TOC entry 2382 (class 2606 OID 33425)
-- Name: mygov_accertamento mygov_accertamento_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_accertamento
    ADD CONSTRAINT mygov_accertamento_pkey PRIMARY KEY (mygov_accertamento_id);


--
-- TOC entry 2390 (class 2606 OID 33427)
-- Name: mygov_anagrafica_stato mygov_anagrafica_stato_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_anagrafica_stato
    ADD CONSTRAINT mygov_anagrafica_stato_pkey PRIMARY KEY (mygov_anagrafica_stato_id);


--
-- TOC entry 2396 (class 2606 OID 33429)
-- Name: mygov_anagrafica_uff_cap_acc mygov_anagrafica_uff_cap_acc_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_anagrafica_uff_cap_acc
    ADD CONSTRAINT mygov_anagrafica_uff_cap_acc_pkey PRIMARY KEY (mygov_anagrafica_uff_cap_acc_id);


--
-- TOC entry 2398 (class 2606 OID 33431)
-- Name: mygov_classificazione_completezza mygov_classificazione_completezza_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_classificazione_completezza
    ADD CONSTRAINT mygov_classificazione_completezza_pkey PRIMARY KEY (mygov_classificazione_codice);


--
-- TOC entry 2409 (class 2606 OID 33433)
-- Name: mygov_ente_flusso_rendicontazione mygov_ente_flusso_rendicontazione_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_ente_flusso_rendicontazione
    ADD CONSTRAINT mygov_ente_flusso_rendicontazione_pkey PRIMARY KEY (mygov_ente_flusso_rendicontazione_id);


--
-- TOC entry 2402 (class 2606 OID 33435)
-- Name: mygov_ente mygov_ente_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_ente
    ADD CONSTRAINT mygov_ente_pkey PRIMARY KEY (mygov_ente_id);


--
-- TOC entry 2413 (class 2606 OID 33437)
-- Name: mygov_ente_prenotazione mygov_ente_prenotazione_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_ente_prenotazione
    ADD CONSTRAINT mygov_ente_prenotazione_pkey PRIMARY KEY (mygov_ente_prenotazione_id);


--
-- TOC entry 2417 (class 2606 OID 33439)
-- Name: mygov_ente_tipo_dovuto mygov_ente_tipo_dovuto_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_ente_tipo_dovuto
    ADD CONSTRAINT mygov_ente_tipo_dovuto_pkey PRIMARY KEY (mygov_ente_tipo_dovuto_id);


--
-- TOC entry 2404 (class 2606 OID 33441)
-- Name: mygov_ente mygov_ente_ukey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_ente
    ADD CONSTRAINT mygov_ente_ukey UNIQUE (cod_ipa_ente);


--
-- TOC entry 2425 (class 2606 OID 33443)
-- Name: mygov_entepsp_flusso_rendicontazione mygov_entepsp_flusso_rendicontazione_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_entepsp_flusso_rendicontazione
    ADD CONSTRAINT mygov_entepsp_flusso_rendicontazione_pkey PRIMARY KEY (mygov_entepsp_flusso_rendicontazione_id);


--
-- TOC entry 2420 (class 2606 OID 33445)
-- Name: mygov_entepsp mygov_entepsp_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_entepsp
    ADD CONSTRAINT mygov_entepsp_pkey PRIMARY KEY (mygov_entepsp_id);


--
-- TOC entry 2435 (class 2606 OID 33447)
-- Name: mygov_flusso_export mygov_flusso_export_ente_iuv_iur_idsp_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_export
    ADD CONSTRAINT mygov_flusso_export_ente_iuv_iur_idsp_pkey PRIMARY KEY (mygov_ente_id, cod_rp_silinviarp_id_univoco_versamento, cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss, indice_dati_singolo_pagamento);


--
-- TOC entry 2439 (class 2606 OID 33449)
-- Name: mygov_flusso_import mygov_flusso_import_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_import
    ADD CONSTRAINT mygov_flusso_import_pkey PRIMARY KEY (mygov_ente_id, cod_iud);


--
-- TOC entry 2445 (class 2606 OID 33451)
-- Name: mygov_flusso_rendicontazione mygov_flusso_rendicontazione_ente_iuv_iur_idsp_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_rendicontazione
    ADD CONSTRAINT mygov_flusso_rendicontazione_ente_iuv_iur_idsp_pkey PRIMARY KEY (mygov_ente_id, cod_dati_sing_pagam_identificativo_univoco_versamento, cod_dati_sing_pagam_identificativo_univoco_riscossione, indice_dati_singolo_pagamento);


--
-- TOC entry 2460 (class 2606 OID 33453)
-- Name: mygov_flusso_tesoreria_iuv mygov_flusso_tesoreria_ente_iuv_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_tesoreria_iuv
    ADD CONSTRAINT mygov_flusso_tesoreria_ente_iuv_pkey PRIMARY KEY (mygov_ente_id, cod_id_univoco_versamento);


--
-- TOC entry 2450 (class 2606 OID 33455)
-- Name: mygov_flusso_tesoreria mygov_flusso_tesoreria_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_tesoreria
    ADD CONSTRAINT mygov_flusso_tesoreria_pkey PRIMARY KEY (mygov_flusso_tesoreria_id);


--
-- TOC entry 2456 (class 2606 OID 33457)
-- Name: mygov_flusso_tesoreria_iuf mygov_flusso_tesoreria_psp_iuf_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_tesoreria_iuf
    ADD CONSTRAINT mygov_flusso_tesoreria_psp_iuf_pkey PRIMARY KEY (cod_bi2, cod_id_univoco_flusso, mygov_ente_id);


--
-- TOC entry 2452 (class 2606 OID 33459)
-- Name: mygov_flusso_tesoreria mygov_flusso_tesoreria_ukey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_tesoreria
    ADD CONSTRAINT mygov_flusso_tesoreria_ukey UNIQUE (mygov_ente_id, de_anno_bolletta, cod_bolletta);


--
-- TOC entry 2465 (class 2606 OID 33461)
-- Name: mygov_info_flusso_poste_web mygov_info_flusso_poste_web_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_info_flusso_poste_web
    ADD CONSTRAINT mygov_info_flusso_poste_web_pkey PRIMARY KEY (mygov_info_flusso_poste_web_id);


--
-- TOC entry 2468 (class 2606 OID 33463)
-- Name: mygov_info_mapping_tesoreria mygov_info_mapping_tesoreria_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_info_mapping_tesoreria
    ADD CONSTRAINT mygov_info_mapping_tesoreria_pkey PRIMARY KEY (mygov_info_mapping_tesoreria_id);


--
-- TOC entry 2475 (class 2606 OID 33465)
-- Name: mygov_manage_flusso mygov_manage_flusso_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_manage_flusso
    ADD CONSTRAINT mygov_manage_flusso_pkey PRIMARY KEY (mygov_manage_flusso_id);


--
-- TOC entry 2482 (class 2606 OID 33467)
-- Name: mygov_operatore_ente_tipo_dovuto mygov_operatore_ente_tipo_dovuto_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_operatore_ente_tipo_dovuto
    ADD CONSTRAINT mygov_operatore_ente_tipo_dovuto_pkey PRIMARY KEY (mygov_operatore_ente_tipo_dovuto_id);


--
-- TOC entry 2477 (class 2606 OID 33469)
-- Name: mygov_operatore mygov_operatore_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mygov_operatore
    ADD CONSTRAINT mygov_operatore_pkey PRIMARY KEY (mygov_operatore_id);


--
-- TOC entry 2479 (class 2606 OID 33471)
-- Name: mygov_operatore mygov_operatore_ukey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mygov_operatore
    ADD CONSTRAINT mygov_operatore_ukey UNIQUE (cod_fed_user_id, cod_ipa_ente);


--
-- TOC entry 2497 (class 2606 OID 33473)
-- Name: mygov_prenotazione_flusso_riconciliazione mygov_pren_flus_ric_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_riconciliazione
    ADD CONSTRAINT mygov_pren_flus_ric_pkey PRIMARY KEY (mygov_prenotazione_flusso_riconciliazione_id);


--
-- TOC entry 2492 (class 2606 OID 33475)
-- Name: mygov_prenotazione_flusso_rendicontazione_ente mygov_prenotazione_flusso_rend_ente_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_rendicontazione_ente
    ADD CONSTRAINT mygov_prenotazione_flusso_rend_ente_pkey PRIMARY KEY (mygov_prenotazione_flusso_rendicontazione_ente_id);


--
-- TOC entry 2487 (class 2606 OID 33477)
-- Name: mygov_prenotazione_flusso_rendicontazione mygov_prenotazione_flusso_rendicontazione_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_rendicontazione
    ADD CONSTRAINT mygov_prenotazione_flusso_rendicontazione_pkey PRIMARY KEY (mygov_prenotazione_flusso_rendicontazione_id);


--
-- TOC entry 2502 (class 2606 OID 33479)
-- Name: mygov_segnalazione mygov_segnalazione_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_segnalazione
    ADD CONSTRAINT mygov_segnalazione_pkey PRIMARY KEY (mygov_segnalazione_id);


--
-- TOC entry 2504 (class 2606 OID 33481)
-- Name: mygov_tipo_flusso mygov_tipo_flusso_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_tipo_flusso
    ADD CONSTRAINT mygov_tipo_flusso_pkey PRIMARY KEY (mygov_tipo_flusso_id);


--
-- TOC entry 2506 (class 2606 OID 33483)
-- Name: mygov_utente mygov_utente_cod_fed_user_id_key; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_utente
    ADD CONSTRAINT mygov_utente_cod_fed_user_id_key UNIQUE (cod_fed_user_id);


--
-- TOC entry 2508 (class 2606 OID 33485)
-- Name: mygov_utente mygov_utente_pkey; Type: CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_utente
    ADD CONSTRAINT mygov_utente_pkey PRIMARY KEY (mygov_utente_id);


--
-- TOC entry 2383 (class 1259 OID 33486)
-- Name: fki_accertamento_dettaglio_mygov_accertamento_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_accertamento_dettaglio_mygov_accertamento_id_idx ON public.mygov_accertamento_dettaglio USING btree (mygov_accertamento_id);


--
-- TOC entry 2384 (class 1259 OID 33487)
-- Name: fki_accertamento_dettaglio_mygov_utente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_accertamento_dettaglio_mygov_utente_id_idx ON public.mygov_accertamento_dettaglio USING btree (mygov_utente_id);


--
-- TOC entry 2378 (class 1259 OID 33488)
-- Name: fki_accertamento_mygov_anagrafica_stato_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_accertamento_mygov_anagrafica_stato_id_idx ON public.mygov_accertamento USING btree (mygov_anagrafica_stato_id);


--
-- TOC entry 2379 (class 1259 OID 33489)
-- Name: fki_accertamento_mygov_ente_tipo_dovuto_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_accertamento_mygov_ente_tipo_dovuto_id_idx ON public.mygov_accertamento USING btree (mygov_ente_tipo_dovuto_id);


--
-- TOC entry 2380 (class 1259 OID 33490)
-- Name: fki_accertamento_mygov_utente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_accertamento_mygov_utente_id_idx ON public.mygov_accertamento USING btree (mygov_utente_id);


--
-- TOC entry 2391 (class 1259 OID 33491)
-- Name: fki_anagrafica_uff_cap_acc_mygov_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_anagrafica_uff_cap_acc_mygov_ente_id_idx ON public.mygov_anagrafica_uff_cap_acc USING btree (mygov_ente_id);


--
-- TOC entry 2405 (class 1259 OID 33492)
-- Name: fki_ente_flusso_rendicontazione_mygov_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_ente_flusso_rendicontazione_mygov_ente_id_idx ON public.mygov_ente_flusso_rendicontazione USING btree (mygov_ente_id);


--
-- TOC entry 2410 (class 1259 OID 33493)
-- Name: fki_ente_prenotazione_mygov_anagrafica_stato_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_ente_prenotazione_mygov_anagrafica_stato_id_idx ON public.mygov_ente_prenotazione USING btree (mygov_anagrafica_stato_id);


--
-- TOC entry 2421 (class 1259 OID 33494)
-- Name: fki_entepsp_flusso_rendicontazione_mygov_entepsp_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_entepsp_flusso_rendicontazione_mygov_entepsp_id_idx ON public.mygov_entepsp_flusso_rendicontazione USING btree (mygov_entepsp_id);


--
-- TOC entry 2418 (class 1259 OID 33495)
-- Name: fki_entepsp_mygov_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_entepsp_mygov_ente_id_idx ON public.mygov_entepsp USING btree (mygov_ente_id);


--
-- TOC entry 2431 (class 1259 OID 33496)
-- Name: fki_flusso_export_mygov_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_flusso_export_mygov_ente_id_idx ON public.mygov_flusso_export USING btree (mygov_ente_id);


--
-- TOC entry 2432 (class 1259 OID 33497)
-- Name: fki_flusso_export_mygov_manage_flusso_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_flusso_export_mygov_manage_flusso_id_idx ON public.mygov_flusso_export USING btree (mygov_manage_flusso_id);


--
-- TOC entry 2436 (class 1259 OID 33498)
-- Name: fki_flusso_import_mygov_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_flusso_import_mygov_ente_id_idx ON public.mygov_flusso_import USING btree (mygov_ente_id);


--
-- TOC entry 2437 (class 1259 OID 33499)
-- Name: fki_flusso_import_mygov_manage_flusso_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_flusso_import_mygov_manage_flusso_id_idx ON public.mygov_flusso_import USING btree (mygov_manage_flusso_id);


--
-- TOC entry 2440 (class 1259 OID 33500)
-- Name: fki_flusso_rendicontazione_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_flusso_rendicontazione_ente_id_idx ON public.mygov_flusso_rendicontazione USING btree (mygov_ente_id);


--
-- TOC entry 2453 (class 1259 OID 33501)
-- Name: fki_flusso_tesoreria_iuf_mygov_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_flusso_tesoreria_iuf_mygov_ente_id_idx ON public.mygov_flusso_tesoreria_iuf USING btree (mygov_ente_id);


--
-- TOC entry 2454 (class 1259 OID 33502)
-- Name: fki_flusso_tesoreria_iuf_mygov_manage_flusso_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_flusso_tesoreria_iuf_mygov_manage_flusso_id_idx ON public.mygov_flusso_tesoreria_iuf USING btree (mygov_manage_flusso_id);


--
-- TOC entry 2457 (class 1259 OID 33503)
-- Name: fki_flusso_tesoreria_iuv_mygov_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_flusso_tesoreria_iuv_mygov_ente_id_idx ON public.mygov_flusso_tesoreria_iuv USING btree (mygov_ente_id);


--
-- TOC entry 2458 (class 1259 OID 33504)
-- Name: fki_flusso_tesoreria_iuv_mygov_manage_flusso_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_flusso_tesoreria_iuv_mygov_manage_flusso_id_idx ON public.mygov_flusso_tesoreria_iuv USING btree (mygov_manage_flusso_id);


--
-- TOC entry 2446 (class 1259 OID 33505)
-- Name: fki_flusso_tesoreria_mygov_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_flusso_tesoreria_mygov_ente_id_idx ON public.mygov_flusso_tesoreria USING btree (mygov_ente_id);


--
-- TOC entry 2447 (class 1259 OID 33506)
-- Name: fki_flusso_tesoreria_mygov_manage_flusso_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_flusso_tesoreria_mygov_manage_flusso_id_idx ON public.mygov_flusso_tesoreria USING btree (mygov_manage_flusso_id);


--
-- TOC entry 2463 (class 1259 OID 33507)
-- Name: fki_info_flusso_poste_web_mygov_manage_flusso_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_info_flusso_poste_web_mygov_manage_flusso_id_idx ON public.mygov_info_flusso_poste_web USING btree (mygov_manage_flusso_id);


--
-- TOC entry 2466 (class 1259 OID 33508)
-- Name: fki_info_mapping_tesoreria_mygov_manage_flusso_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_info_mapping_tesoreria_mygov_manage_flusso_id_idx ON public.mygov_info_mapping_tesoreria USING btree (mygov_manage_flusso_id);


--
-- TOC entry 2469 (class 1259 OID 33509)
-- Name: fki_manage_flusso_mygov_anagrafica_stato_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_manage_flusso_mygov_anagrafica_stato_id_idx ON public.mygov_manage_flusso USING btree (mygov_anagrafica_stato_id);


--
-- TOC entry 2470 (class 1259 OID 33510)
-- Name: fki_manage_flusso_mygov_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_manage_flusso_mygov_ente_id_idx ON public.mygov_manage_flusso USING btree (mygov_ente_id);


--
-- TOC entry 2471 (class 1259 OID 33511)
-- Name: fki_manage_flusso_mygov_tipo_flusso_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_manage_flusso_mygov_tipo_flusso_id_idx ON public.mygov_manage_flusso USING btree (mygov_tipo_flusso_id);


--
-- TOC entry 2472 (class 1259 OID 33512)
-- Name: fki_manage_flusso_mygov_utente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_manage_flusso_mygov_utente_id_idx ON public.mygov_manage_flusso USING btree (mygov_utente_id);


--
-- TOC entry 2406 (class 1259 OID 33513)
-- Name: fki_mygov_ente_flusso_rend_mygov_anagrafica_stato_id; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_mygov_ente_flusso_rend_mygov_anagrafica_stato_id ON public.mygov_ente_flusso_rendicontazione USING btree (mygov_anagrafica_stato_id);


--
-- TOC entry 2407 (class 1259 OID 33514)
-- Name: fki_mygov_ente_flusso_rend_mygov_pren_flusso_rend_ente_id; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_mygov_ente_flusso_rend_mygov_pren_flusso_rend_ente_id ON public.mygov_ente_flusso_rendicontazione USING btree (mygov_prenotazione_flusso_rendicontazione_ente_id);


--
-- TOC entry 2411 (class 1259 OID 33515)
-- Name: fki_mygov_ente_prenotazione_mygov_ente_id_fkey; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_mygov_ente_prenotazione_mygov_ente_id_fkey ON public.mygov_ente_prenotazione USING btree (mygov_ente_id);


--
-- TOC entry 2414 (class 1259 OID 33516)
-- Name: fki_mygov_ente_tipo_dovuto_mygov_ente_fkey; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_mygov_ente_tipo_dovuto_mygov_ente_fkey ON public.mygov_ente_tipo_dovuto USING btree (mygov_ente_id);


--
-- TOC entry 2422 (class 1259 OID 33517)
-- Name: fki_mygov_entepsp_flusso_rend_mygov_prenotazione_flusso_rend_id; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_mygov_entepsp_flusso_rend_mygov_prenotazione_flusso_rend_id ON public.mygov_entepsp_flusso_rendicontazione USING btree (mygov_prenotazione_flusso_rendicontazione_id);


--
-- TOC entry 2423 (class 1259 OID 33518)
-- Name: fki_mygov_entepsp_flusso_rendicontaz_mygov_anagrafica_stato_id; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_mygov_entepsp_flusso_rendicontaz_mygov_anagrafica_stato_id ON public.mygov_entepsp_flusso_rendicontazione USING btree (mygov_anagrafica_stato_id);


--
-- TOC entry 2441 (class 1259 OID 33519)
-- Name: fki_mygov_flusso_rendicontazione_mygov_manage_flusso_id_fkey; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_mygov_flusso_rendicontazione_mygov_manage_flusso_id_fkey ON public.mygov_flusso_rendicontazione USING btree (mygov_manage_flusso_id);


--
-- TOC entry 2488 (class 1259 OID 33520)
-- Name: fki_mygov_prenotazione_flusso_rend_ente_mygov_anag_stato_id; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_mygov_prenotazione_flusso_rend_ente_mygov_anag_stato_id ON public.mygov_prenotazione_flusso_rendicontazione_ente USING btree (mygov_anagrafica_stato_id);


--
-- TOC entry 2489 (class 1259 OID 33521)
-- Name: fki_mygov_prenotazione_flusso_rend_ente_mygov_ente_id; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_mygov_prenotazione_flusso_rend_ente_mygov_ente_id ON public.mygov_prenotazione_flusso_rendicontazione_ente USING btree (mygov_ente_id);


--
-- TOC entry 2483 (class 1259 OID 33522)
-- Name: fki_mygov_prenotazione_flusso_rendic_mygov_anagrafica_stato_id; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_mygov_prenotazione_flusso_rendic_mygov_anagrafica_stato_id ON public.mygov_prenotazione_flusso_rendicontazione USING btree (mygov_anagrafica_stato_id);


--
-- TOC entry 2484 (class 1259 OID 33523)
-- Name: fki_mygov_prenotazione_flusso_rendicontazione_mygov_entepsp_id; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_mygov_prenotazione_flusso_rendicontazione_mygov_entepsp_id ON public.mygov_prenotazione_flusso_rendicontazione USING btree (mygov_entepsp_id);


--
-- TOC entry 2480 (class 1259 OID 33524)
-- Name: fki_operatore_ente_tipo_dovuto_mygov_ente_tipo_dovuto_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_operatore_ente_tipo_dovuto_mygov_ente_tipo_dovuto_id_idx ON public.mygov_operatore_ente_tipo_dovuto USING btree (mygov_ente_tipo_dovuto_id);


--
-- TOC entry 2490 (class 1259 OID 33525)
-- Name: fki_prenotazione_flusso_rendicontazione_ente_mygov_tipo_flusso_; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_prenotazione_flusso_rendicontazione_ente_mygov_tipo_flusso_ ON public.mygov_prenotazione_flusso_rendicontazione_ente USING btree (mygov_tipo_flusso_id);


--
-- TOC entry 2485 (class 1259 OID 33526)
-- Name: fki_prenotazione_flusso_rendicontazione_mygov_tipo_flusso_id_id; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_prenotazione_flusso_rendicontazione_mygov_tipo_flusso_id_id ON public.mygov_prenotazione_flusso_rendicontazione USING btree (mygov_tipo_flusso_id);


--
-- TOC entry 2493 (class 1259 OID 33527)
-- Name: fki_prenotazione_flusso_riconciliazione_mygov_anagrafica_stato_; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_prenotazione_flusso_riconciliazione_mygov_anagrafica_stato_ ON public.mygov_prenotazione_flusso_riconciliazione USING btree (mygov_anagrafica_stato_id);


--
-- TOC entry 2494 (class 1259 OID 33528)
-- Name: fki_prenotazione_flusso_riconciliazione_mygov_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_prenotazione_flusso_riconciliazione_mygov_ente_id_idx ON public.mygov_prenotazione_flusso_riconciliazione USING btree (mygov_ente_id);


--
-- TOC entry 2495 (class 1259 OID 33529)
-- Name: fki_prenotazione_flusso_riconciliazione_mygov_utente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_prenotazione_flusso_riconciliazione_mygov_utente_id_idx ON public.mygov_prenotazione_flusso_riconciliazione USING btree (mygov_utente_id);


--
-- TOC entry 2498 (class 1259 OID 33530)
-- Name: fki_segnalazione_mygov_ente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_segnalazione_mygov_ente_id_idx ON public.mygov_segnalazione USING btree (mygov_ente_id);


--
-- TOC entry 2499 (class 1259 OID 33531)
-- Name: fki_segnalazione_mygov_utente_id_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX fki_segnalazione_mygov_utente_id_idx ON public.mygov_segnalazione USING btree (mygov_utente_id);


--
-- TOC entry 2442 (class 1259 OID 33532)
-- Name: idx_flusso_rendicontazione_ente_id_idx_pag; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_flusso_rendicontazione_ente_id_idx_pag ON public.mygov_flusso_rendicontazione USING btree (mygov_ente_id, cod_identificativo_univoco_regolamento, upper((cod_identificativo_flusso)::text));


--
-- TOC entry 2392 (class 1259 OID 33533)
-- Name: idx_mygov_anagrafica_uff_cap_acc; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygov_anagrafica_uff_cap_acc ON public.mygov_anagrafica_uff_cap_acc USING btree (mygov_ente_id, cod_tipo_dovuto, cod_ufficio);


--
-- TOC entry 2399 (class 1259 OID 33534)
-- Name: idx_mygov_ente_cod_ipa_ente; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygov_ente_cod_ipa_ente ON public.mygov_ente USING btree (mygov_ente_id, lower((cod_ipa_ente)::text));


--
-- TOC entry 2400 (class 1259 OID 33535)
-- Name: idx_mygov_ente_e; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygov_ente_e ON public.mygov_ente USING btree (codice_fiscale_ente);


--
-- TOC entry 2433 (class 1259 OID 33536)
-- Name: idx_mygov_flusso_export_mygov_ente_id_iud; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygov_flusso_export_mygov_ente_id_iud ON public.mygov_flusso_export USING btree (mygov_ente_id, cod_iud);


--
-- TOC entry 2443 (class 1259 OID 33537)
-- Name: idx_mygov_flusso_rendicontazione_mygov_ente_id_dt_reg_fkey; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygov_flusso_rendicontazione_mygov_ente_id_dt_reg_fkey ON public.mygov_flusso_rendicontazione USING btree (mygov_ente_id, dt_data_regolamento);


--
-- TOC entry 2448 (class 1259 OID 33538)
-- Name: idx_mygov_flusso_tesoreria_bolletta_iuv; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygov_flusso_tesoreria_bolletta_iuv ON public.mygov_flusso_tesoreria USING btree (dt_bolletta, lower((cod_id_univoco_versamento)::text));


--
-- TOC entry 2358 (class 1259 OID 33539)
-- Name: idx_mygov_import_export_rend_tesoreria_completezza; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygov_import_export_rend_tesoreria_completezza ON public.mygov_import_export_rendicontazione_tesoreria USING btree (lower((classificazione_completezza)::text), lower((codice_ipa_ente)::text), lower((cod_iuv_key)::text), lower((cod_iud_key)::text));


--
-- TOC entry 2473 (class 1259 OID 33540)
-- Name: idx_mygov_manage_flusso_cod_request_token; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygov_manage_flusso_cod_request_token ON public.mygov_manage_flusso USING btree (cod_request_token);


--
-- TOC entry 2385 (class 1259 OID 33541)
-- Name: idx_mygovaccdett_ipa_iud; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovaccdett_ipa_iud ON public.mygov_accertamento_dettaglio USING btree (cod_ipa_ente, cod_iud);


--
-- TOC entry 2426 (class 1259 OID 33542)
-- Name: idx_mygovexprend_cod_identificativo_flusso; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovexprend_cod_identificativo_flusso ON public.mygov_export_rendicontazione_completa USING btree (mygov_ente_id_e, cod_identificativo_flusso_r);


--
-- TOC entry 2427 (class 1259 OID 33543)
-- Name: idx_mygovexprend_dt_acquisizione_e; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovexprend_dt_acquisizione_e ON public.mygov_export_rendicontazione_completa USING btree (mygov_ente_id_e, dt_acquisizione_e);


--
-- TOC entry 2428 (class 1259 OID 33544)
-- Name: idx_mygovexprend_dt_acquisizione_r; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovexprend_dt_acquisizione_r ON public.mygov_export_rendicontazione_completa USING btree (mygov_ente_id_e, dt_acquisizione_r);


--
-- TOC entry 2429 (class 1259 OID 33545)
-- Name: idx_mygovexprend_id_univoco_versamento; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovexprend_id_univoco_versamento ON public.mygov_export_rendicontazione_completa USING btree (mygov_ente_id_e, cod_e_dati_pag_id_univoco_versamento_e);


--
-- TOC entry 2430 (class 1259 OID 33546)
-- Name: idx_mygovexprend_tipo_dovuto; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovexprend_tipo_dovuto ON public.mygov_export_rendicontazione_completa USING btree (mygov_ente_id_e, cod_tipo_dovuto_e);


--
-- TOC entry 2359 (class 1259 OID 33547)
-- Name: idx_mygovimpexprendtes_causale_versamento; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_causale_versamento ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, causale_versamento);


--
-- TOC entry 2360 (class 1259 OID 33548)
-- Name: idx_mygovimpexprendtes_classificazione_completezza; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_classificazione_completezza ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, classificazione_completezza);


--
-- TOC entry 2361 (class 1259 OID 33549)
-- Name: idx_mygovimpexprendtes_codice_iud; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_codice_iud ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, codice_iud);


--
-- TOC entry 2362 (class 1259 OID 33550)
-- Name: idx_mygovimpexprendtes_de_importo; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_de_importo ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, de_importo);


--
-- TOC entry 2363 (class 1259 OID 33551)
-- Name: idx_mygovimpexprendtes_dt_data_contabile; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_dt_data_contabile ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, dt_data_contabile);


--
-- TOC entry 2364 (class 1259 OID 33552)
-- Name: idx_mygovimpexprendtes_dt_data_esito_singolo_pagamento; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_dt_data_esito_singolo_pagamento ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, dt_data_esito_singolo_pagamento);


--
-- TOC entry 2365 (class 1259 OID 33553)
-- Name: idx_mygovimpexprendtes_dt_data_regolamento; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_dt_data_regolamento ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, dt_data_regolamento);


--
-- TOC entry 2366 (class 1259 OID 33554)
-- Name: idx_mygovimpexprendtes_dt_data_valuta; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_dt_data_valuta ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, dt_data_valuta);


--
-- TOC entry 2367 (class 1259 OID 33555)
-- Name: idx_mygovimpexprendtes_id_key; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_id_key ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, classificazione_completezza, cod_iuv_key, cod_iuf_key, cod_iud_key);


--
-- TOC entry 2368 (class 1259 OID 33556)
-- Name: idx_mygovimpexprendtes_identificativo_flusso_rendicontazione; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_identificativo_flusso_rendicontazione ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, identificativo_flusso_rendicontazione);


--
-- TOC entry 2369 (class 1259 OID 33563)
-- Name: idx_mygovimpexprendtes_identificativo_univoco_regolamento; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_identificativo_univoco_regolamento ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, identificativo_univoco_regolamento);


--
-- TOC entry 2370 (class 1259 OID 33564)
-- Name: idx_mygovimpexprendtes_identificativo_univoco_riscossione; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_identificativo_univoco_riscossione ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, identificativo_univoco_riscossione);


--
-- TOC entry 2371 (class 1259 OID 33565)
-- Name: idx_mygovimpexprendtes_identificativo_univoco_versamento; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_identificativo_univoco_versamento ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, identificativo_univoco_versamento);


--
-- TOC entry 2372 (class 1259 OID 33566)
-- Name: idx_mygovimpexprendtes_importo_totale_pagamenti; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_importo_totale_pagamenti ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, importo_totale_pagamenti);


--
-- TOC entry 2373 (class 1259 OID 33567)
-- Name: idx_mygovimpexprendtes_tipo_dovuto; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtes_tipo_dovuto ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente, tipo_dovuto);


--
-- TOC entry 2461 (class 1259 OID 33568)
-- Name: idx_mygovimpexprendtescompl_data_aggiornamento; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtescompl_data_aggiornamento ON public.mygov_import_export_rendicontazione_tesoreria_completa USING btree (mygov_ente_id, classificazione_completezza, dt_data_ultimo_aggiornamento);


--
-- TOC entry 2462 (class 1259 OID 33569)
-- Name: idx_mygovimpexprendtescompl_tipo_dovuto; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idx_mygovimpexprendtescompl_tipo_dovuto ON public.mygov_import_export_rendicontazione_tesoreria_completa USING btree (mygov_ente_id, classificazione_completezza, cod_tipo_dovuto_i, cod_tipo_dovuto_e);


--
-- TOC entry 2393 (class 1259 OID 33570)
-- Name: idxu_mygov_anagrafica_uff_cap_acc_anno_eser; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX idxu_mygov_anagrafica_uff_cap_acc_anno_eser ON public.mygov_anagrafica_uff_cap_acc USING btree (mygov_ente_id, cod_ufficio, cod_capitolo, cod_accertamento, de_anno_esercizio);


--
-- TOC entry 2386 (class 1259 OID 33571)
-- Name: mygov_accertamento_dettaglio_cod_iud_cod_iuv_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX mygov_accertamento_dettaglio_cod_iud_cod_iuv_idx ON public.mygov_accertamento_dettaglio USING btree (cod_iud, cod_iuv);


--
-- TOC entry 2394 (class 1259 OID 33572)
-- Name: mygov_anagrafica_uff_cap_acc_combo_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX mygov_anagrafica_uff_cap_acc_combo_idx ON public.mygov_anagrafica_uff_cap_acc USING btree (cod_tipo_dovuto, mygov_ente_id, de_anno_esercizio, flg_attivo);


--
-- TOC entry 2415 (class 1259 OID 33573)
-- Name: mygov_ente_tipo_dovuto_enteid_codtipo; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX mygov_ente_tipo_dovuto_enteid_codtipo ON public.mygov_ente_tipo_dovuto USING btree (mygov_ente_id, cod_tipo);


--
-- TOC entry 2374 (class 1259 OID 33574)
-- Name: mygov_import_export_rendicontazione_tesoreria_cod_iud_key_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX mygov_import_export_rendicontazione_tesoreria_cod_iud_key_idx ON public.mygov_import_export_rendicontazione_tesoreria USING btree (cod_iud_key);


--
-- TOC entry 2375 (class 1259 OID 33575)
-- Name: mygov_import_export_rendicontazione_tesoreria_cod_iuf_key_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX mygov_import_export_rendicontazione_tesoreria_cod_iuf_key_idx ON public.mygov_import_export_rendicontazione_tesoreria USING btree (cod_iuf_key);


--
-- TOC entry 2376 (class 1259 OID 33576)
-- Name: mygov_import_export_rendicontazione_tesoreria_cod_iuv_key_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX mygov_import_export_rendicontazione_tesoreria_cod_iuv_key_idx ON public.mygov_import_export_rendicontazione_tesoreria USING btree (cod_iuv_key);


--
-- TOC entry 2377 (class 1259 OID 33577)
-- Name: mygov_import_export_rendicontazione_tesoreria_codice_ipa_ente_i; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX mygov_import_export_rendicontazione_tesoreria_codice_ipa_ente_i ON public.mygov_import_export_rendicontazione_tesoreria USING btree (codice_ipa_ente);


--
-- TOC entry 2500 (class 1259 OID 33578)
-- Name: mygov_segnalazione_flg_attivo_idx; Type: INDEX; Schema: public; Owner: mypay4
--

CREATE INDEX mygov_segnalazione_flg_attivo_idx ON public.mygov_segnalazione USING btree (flg_attivo, classificazione_completezza);


--
-- TOC entry 2512 (class 2606 OID 33579)
-- Name: mygov_accertamento_dettaglio mygov_accertamento_dettaglio_mygov_accertamento_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_accertamento_dettaglio
    ADD CONSTRAINT mygov_accertamento_dettaglio_mygov_accertamento_fkey FOREIGN KEY (mygov_accertamento_id) REFERENCES public.mygov_accertamento(mygov_accertamento_id);


--
-- TOC entry 2513 (class 2606 OID 33584)
-- Name: mygov_accertamento_dettaglio mygov_accertamento_dettaglio_mygov_utente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_accertamento_dettaglio
    ADD CONSTRAINT mygov_accertamento_dettaglio_mygov_utente_fkey FOREIGN KEY (mygov_utente_id) REFERENCES public.mygov_utente(mygov_utente_id);


--
-- TOC entry 2509 (class 2606 OID 33589)
-- Name: mygov_accertamento mygov_accertamento_mygov_anagrafica_stato_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_accertamento
    ADD CONSTRAINT mygov_accertamento_mygov_anagrafica_stato_id_fkey FOREIGN KEY (mygov_anagrafica_stato_id) REFERENCES public.mygov_anagrafica_stato(mygov_anagrafica_stato_id);


--
-- TOC entry 2510 (class 2606 OID 33594)
-- Name: mygov_accertamento mygov_accertamento_mygov_ente_tipo_dovuto_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_accertamento
    ADD CONSTRAINT mygov_accertamento_mygov_ente_tipo_dovuto_id_fkey FOREIGN KEY (mygov_ente_tipo_dovuto_id) REFERENCES public.mygov_ente_tipo_dovuto(mygov_ente_tipo_dovuto_id);


--
-- TOC entry 2511 (class 2606 OID 33599)
-- Name: mygov_accertamento mygov_accertamento_mygov_utente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_accertamento
    ADD CONSTRAINT mygov_accertamento_mygov_utente_id_fkey FOREIGN KEY (mygov_utente_id) REFERENCES public.mygov_utente(mygov_utente_id);


--
-- TOC entry 2514 (class 2606 OID 33604)
-- Name: mygov_anagrafica_uff_cap_acc mygov_anagrafica_uff_cap_acc_mygov_ente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_anagrafica_uff_cap_acc
    ADD CONSTRAINT mygov_anagrafica_uff_cap_acc_mygov_ente_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id);


--
-- TOC entry 2515 (class 2606 OID 33609)
-- Name: mygov_ente_flusso_rendicontazione mygov_ente_flusso_rend_mygov_prenotazione_flusso_rend_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_ente_flusso_rendicontazione
    ADD CONSTRAINT mygov_ente_flusso_rend_mygov_prenotazione_flusso_rend_id_fkey FOREIGN KEY (mygov_prenotazione_flusso_rendicontazione_ente_id) REFERENCES public.mygov_prenotazione_flusso_rendicontazione_ente(mygov_prenotazione_flusso_rendicontazione_ente_id);


--
-- TOC entry 2516 (class 2606 OID 33614)
-- Name: mygov_ente_flusso_rendicontazione mygov_ente_flusso_rendicontaz_mygov_anagrafica_stato_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_ente_flusso_rendicontazione
    ADD CONSTRAINT mygov_ente_flusso_rendicontaz_mygov_anagrafica_stato_id_fkey FOREIGN KEY (mygov_anagrafica_stato_id) REFERENCES public.mygov_anagrafica_stato(mygov_anagrafica_stato_id);


--
-- TOC entry 2517 (class 2606 OID 33619)
-- Name: mygov_ente_flusso_rendicontazione mygov_ente_flusso_rendicontazione_mygov_ente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_ente_flusso_rendicontazione
    ADD CONSTRAINT mygov_ente_flusso_rendicontazione_mygov_ente_id_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id) ON DELETE CASCADE;


--
-- TOC entry 2518 (class 2606 OID 33624)
-- Name: mygov_ente_prenotazione mygov_ente_prenotazione_mygov_anagrafica_stato_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_ente_prenotazione
    ADD CONSTRAINT mygov_ente_prenotazione_mygov_anagrafica_stato_id_fkey FOREIGN KEY (mygov_anagrafica_stato_id) REFERENCES public.mygov_anagrafica_stato(mygov_anagrafica_stato_id);


--
-- TOC entry 2519 (class 2606 OID 33630)
-- Name: mygov_ente_prenotazione mygov_ente_prenotazione_mygov_ente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_ente_prenotazione
    ADD CONSTRAINT mygov_ente_prenotazione_mygov_ente_id_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id) ON DELETE CASCADE;


--
-- TOC entry 2520 (class 2606 OID 33638)
-- Name: mygov_ente_tipo_dovuto mygov_ente_tipo_dovuto_mygov_ente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_ente_tipo_dovuto
    ADD CONSTRAINT mygov_ente_tipo_dovuto_mygov_ente_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id) ON DELETE CASCADE;


--
-- TOC entry 2522 (class 2606 OID 33643)
-- Name: mygov_entepsp_flusso_rendicontazione mygov_entepsp_flusso_rend_mygov_prenotazione_flusso_rend_id_fke; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_entepsp_flusso_rendicontazione
    ADD CONSTRAINT mygov_entepsp_flusso_rend_mygov_prenotazione_flusso_rend_id_fke FOREIGN KEY (mygov_prenotazione_flusso_rendicontazione_id) REFERENCES public.mygov_prenotazione_flusso_rendicontazione(mygov_prenotazione_flusso_rendicontazione_id);


--
-- TOC entry 2523 (class 2606 OID 33648)
-- Name: mygov_entepsp_flusso_rendicontazione mygov_entepsp_flusso_rendicontaz_mygov_anagrafica_stato_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_entepsp_flusso_rendicontazione
    ADD CONSTRAINT mygov_entepsp_flusso_rendicontaz_mygov_anagrafica_stato_id_fkey FOREIGN KEY (mygov_anagrafica_stato_id) REFERENCES public.mygov_anagrafica_stato(mygov_anagrafica_stato_id);


--
-- TOC entry 2524 (class 2606 OID 33653)
-- Name: mygov_entepsp_flusso_rendicontazione mygov_entepsp_flusso_rendicontazione_mygov_entepsp_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_entepsp_flusso_rendicontazione
    ADD CONSTRAINT mygov_entepsp_flusso_rendicontazione_mygov_entepsp_id_fkey FOREIGN KEY (mygov_entepsp_id) REFERENCES public.mygov_entepsp(mygov_entepsp_id) ON DELETE CASCADE;


--
-- TOC entry 2521 (class 2606 OID 33658)
-- Name: mygov_entepsp mygov_entepsp_mygov_ente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_entepsp
    ADD CONSTRAINT mygov_entepsp_mygov_ente_id_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id) ON DELETE CASCADE;


--
-- TOC entry 2525 (class 2606 OID 33663)
-- Name: mygov_flusso_export mygov_flusso_export_mygov_ente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_export
    ADD CONSTRAINT mygov_flusso_export_mygov_ente_id_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id) ON DELETE CASCADE;


--
-- TOC entry 2526 (class 2606 OID 33668)
-- Name: mygov_flusso_export mygov_flusso_export_mygov_manage_flusso_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_export
    ADD CONSTRAINT mygov_flusso_export_mygov_manage_flusso_id_fkey FOREIGN KEY (mygov_manage_flusso_id) REFERENCES public.mygov_manage_flusso(mygov_manage_flusso_id) ON DELETE CASCADE;


--
-- TOC entry 2527 (class 2606 OID 33673)
-- Name: mygov_flusso_import mygov_flusso_import_mygov_ente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_import
    ADD CONSTRAINT mygov_flusso_import_mygov_ente_id_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id) ON DELETE CASCADE;


--
-- TOC entry 2528 (class 2606 OID 33678)
-- Name: mygov_flusso_import mygov_flusso_import_mygov_manage_flusso_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_import
    ADD CONSTRAINT mygov_flusso_import_mygov_manage_flusso_id_fkey FOREIGN KEY (mygov_manage_flusso_id) REFERENCES public.mygov_manage_flusso(mygov_manage_flusso_id) ON DELETE CASCADE;


--
-- TOC entry 2529 (class 2606 OID 33683)
-- Name: mygov_flusso_rendicontazione mygov_flusso_rendicontazione_mygov_ente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_rendicontazione
    ADD CONSTRAINT mygov_flusso_rendicontazione_mygov_ente_id_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id) ON DELETE CASCADE;


--
-- TOC entry 2530 (class 2606 OID 33688)
-- Name: mygov_flusso_rendicontazione mygov_flusso_rendicontazione_mygov_manage_flusso_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_rendicontazione
    ADD CONSTRAINT mygov_flusso_rendicontazione_mygov_manage_flusso_id_fkey FOREIGN KEY (mygov_manage_flusso_id) REFERENCES public.mygov_manage_flusso(mygov_manage_flusso_id) ON DELETE CASCADE;


--
-- TOC entry 2533 (class 2606 OID 33693)
-- Name: mygov_flusso_tesoreria_iuf mygov_flusso_tesoreria_iuf_mygov_ente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_tesoreria_iuf
    ADD CONSTRAINT mygov_flusso_tesoreria_iuf_mygov_ente_id_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id) ON DELETE CASCADE;


--
-- TOC entry 2534 (class 2606 OID 33698)
-- Name: mygov_flusso_tesoreria_iuf mygov_flusso_tesoreria_iuf_mygov_manage_flusso_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_tesoreria_iuf
    ADD CONSTRAINT mygov_flusso_tesoreria_iuf_mygov_manage_flusso_id_fkey FOREIGN KEY (mygov_manage_flusso_id) REFERENCES public.mygov_manage_flusso(mygov_manage_flusso_id) ON DELETE CASCADE;


--
-- TOC entry 2535 (class 2606 OID 33703)
-- Name: mygov_flusso_tesoreria_iuv mygov_flusso_tesoreria_iuv_mygov_ente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_tesoreria_iuv
    ADD CONSTRAINT mygov_flusso_tesoreria_iuv_mygov_ente_id_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id) ON DELETE CASCADE;


--
-- TOC entry 2536 (class 2606 OID 33708)
-- Name: mygov_flusso_tesoreria_iuv mygov_flusso_tesoreria_iuv_mygov_manage_flusso_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_tesoreria_iuv
    ADD CONSTRAINT mygov_flusso_tesoreria_iuv_mygov_manage_flusso_id_fkey FOREIGN KEY (mygov_manage_flusso_id) REFERENCES public.mygov_manage_flusso(mygov_manage_flusso_id) ON DELETE CASCADE;


--
-- TOC entry 2531 (class 2606 OID 33713)
-- Name: mygov_flusso_tesoreria mygov_flusso_tesoreria_mygov_ente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_tesoreria
    ADD CONSTRAINT mygov_flusso_tesoreria_mygov_ente_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id);


--
-- TOC entry 2532 (class 2606 OID 33718)
-- Name: mygov_flusso_tesoreria mygov_flusso_tesoreria_mygov_manage_flusso_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_flusso_tesoreria
    ADD CONSTRAINT mygov_flusso_tesoreria_mygov_manage_flusso_id_fkey FOREIGN KEY (mygov_manage_flusso_id) REFERENCES public.mygov_manage_flusso(mygov_manage_flusso_id);


--
-- TOC entry 2537 (class 2606 OID 33723)
-- Name: mygov_info_flusso_poste_web mygov_info_flusso_poste_web_mygov_manage_flusso_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_info_flusso_poste_web
    ADD CONSTRAINT mygov_info_flusso_poste_web_mygov_manage_flusso_id_fkey FOREIGN KEY (mygov_manage_flusso_id) REFERENCES public.mygov_manage_flusso(mygov_manage_flusso_id) ON DELETE CASCADE;


--
-- TOC entry 2538 (class 2606 OID 33728)
-- Name: mygov_info_mapping_tesoreria mygov_info_mapping_tesoreria_mygov_manage_flusso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_info_mapping_tesoreria
    ADD CONSTRAINT mygov_info_mapping_tesoreria_mygov_manage_flusso_fkey FOREIGN KEY (mygov_manage_flusso_id) REFERENCES public.mygov_manage_flusso(mygov_manage_flusso_id);


--
-- TOC entry 2539 (class 2606 OID 33733)
-- Name: mygov_manage_flusso mygov_manage_flusso_mygov_anagrafica_stato_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_manage_flusso
    ADD CONSTRAINT mygov_manage_flusso_mygov_anagrafica_stato_fkey FOREIGN KEY (mygov_anagrafica_stato_id) REFERENCES public.mygov_anagrafica_stato(mygov_anagrafica_stato_id);


--
-- TOC entry 2540 (class 2606 OID 33738)
-- Name: mygov_manage_flusso mygov_manage_flusso_mygov_ente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_manage_flusso
    ADD CONSTRAINT mygov_manage_flusso_mygov_ente_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id) ON DELETE CASCADE;


--
-- TOC entry 2541 (class 2606 OID 33743)
-- Name: mygov_manage_flusso mygov_manage_flusso_mygov_tipo_flusso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_manage_flusso
    ADD CONSTRAINT mygov_manage_flusso_mygov_tipo_flusso_fkey FOREIGN KEY (mygov_tipo_flusso_id) REFERENCES public.mygov_tipo_flusso(mygov_tipo_flusso_id);


--
-- TOC entry 2542 (class 2606 OID 33748)
-- Name: mygov_manage_flusso mygov_manage_flusso_mygov_utente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_manage_flusso
    ADD CONSTRAINT mygov_manage_flusso_mygov_utente_fkey FOREIGN KEY (mygov_utente_id) REFERENCES public.mygov_utente(mygov_utente_id);


--
-- TOC entry 2544 (class 2606 OID 33753)
-- Name: mygov_operatore_ente_tipo_dovuto mygov_operatore_ente_tipo_dovuto_mygov_ente_tipo_dovuto_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_operatore_ente_tipo_dovuto
    ADD CONSTRAINT mygov_operatore_ente_tipo_dovuto_mygov_ente_tipo_dovuto_fkey FOREIGN KEY (mygov_ente_tipo_dovuto_id) REFERENCES public.mygov_ente_tipo_dovuto(mygov_ente_tipo_dovuto_id) ON DELETE CASCADE;


--
-- TOC entry 2545 (class 2606 OID 33758)
-- Name: mygov_operatore_ente_tipo_dovuto mygov_operatore_ente_tipo_dovuto_mygov_operatore_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_operatore_ente_tipo_dovuto
    ADD CONSTRAINT mygov_operatore_ente_tipo_dovuto_mygov_operatore_id_fkey FOREIGN KEY (mygov_operatore_id) REFERENCES public.mygov_operatore(mygov_operatore_id);


--
-- TOC entry 2543 (class 2606 OID 33763)
-- Name: mygov_operatore mygov_operatore_mygov_ente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mygov_operatore
    ADD CONSTRAINT mygov_operatore_mygov_ente_fkey FOREIGN KEY (cod_ipa_ente) REFERENCES public.mygov_ente(cod_ipa_ente);


--
-- TOC entry 2552 (class 2606 OID 33768)
-- Name: mygov_prenotazione_flusso_riconciliazione mygov_pren_flus_ric_mygov_anagrafica_stato_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_riconciliazione
    ADD CONSTRAINT mygov_pren_flus_ric_mygov_anagrafica_stato_fkey FOREIGN KEY (mygov_anagrafica_stato_id) REFERENCES public.mygov_anagrafica_stato(mygov_anagrafica_stato_id);


--
-- TOC entry 2553 (class 2606 OID 33773)
-- Name: mygov_prenotazione_flusso_riconciliazione mygov_pren_flus_ric_mygov_ente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_riconciliazione
    ADD CONSTRAINT mygov_pren_flus_ric_mygov_ente_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id);


--
-- TOC entry 2554 (class 2606 OID 33778)
-- Name: mygov_prenotazione_flusso_riconciliazione mygov_pren_flus_ric_mygov_utente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_riconciliazione
    ADD CONSTRAINT mygov_pren_flus_ric_mygov_utente_fkey FOREIGN KEY (mygov_utente_id) REFERENCES public.mygov_utente(mygov_utente_id);


--
-- TOC entry 2549 (class 2606 OID 33783)
-- Name: mygov_prenotazione_flusso_rendicontazione_ente mygov_prenotazione_flusso_rend_ente_mygov_ente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_rendicontazione_ente
    ADD CONSTRAINT mygov_prenotazione_flusso_rend_ente_mygov_ente_id_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id) ON DELETE CASCADE;


--
-- TOC entry 2550 (class 2606 OID 33788)
-- Name: mygov_prenotazione_flusso_rendicontazione_ente mygov_prenotazione_flusso_rend_ente_mygov_tipo_flusso_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_rendicontazione_ente
    ADD CONSTRAINT mygov_prenotazione_flusso_rend_ente_mygov_tipo_flusso_id_fkey FOREIGN KEY (mygov_tipo_flusso_id) REFERENCES public.mygov_tipo_flusso(mygov_tipo_flusso_id);


--
-- TOC entry 2551 (class 2606 OID 33793)
-- Name: mygov_prenotazione_flusso_rendicontazione_ente mygov_prenotazione_flusso_rend_mygov_anag_stato_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_rendicontazione_ente
    ADD CONSTRAINT mygov_prenotazione_flusso_rend_mygov_anag_stato_id_fkey FOREIGN KEY (mygov_anagrafica_stato_id) REFERENCES public.mygov_anagrafica_stato(mygov_anagrafica_stato_id);


--
-- TOC entry 2546 (class 2606 OID 33798)
-- Name: mygov_prenotazione_flusso_rendicontazione mygov_prenotazione_flusso_rendic_mygov_anagrafica_stato_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_rendicontazione
    ADD CONSTRAINT mygov_prenotazione_flusso_rendic_mygov_anagrafica_stato_id_fkey FOREIGN KEY (mygov_anagrafica_stato_id) REFERENCES public.mygov_anagrafica_stato(mygov_anagrafica_stato_id);


--
-- TOC entry 2547 (class 2606 OID 33803)
-- Name: mygov_prenotazione_flusso_rendicontazione mygov_prenotazione_flusso_rendicontaz_mygov_tipo_flusso_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_rendicontazione
    ADD CONSTRAINT mygov_prenotazione_flusso_rendicontaz_mygov_tipo_flusso_id_fkey FOREIGN KEY (mygov_tipo_flusso_id) REFERENCES public.mygov_tipo_flusso(mygov_tipo_flusso_id);


--
-- TOC entry 2548 (class 2606 OID 33808)
-- Name: mygov_prenotazione_flusso_rendicontazione mygov_prenotazione_flusso_rendicontazione_mygov_entepsp_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_prenotazione_flusso_rendicontazione
    ADD CONSTRAINT mygov_prenotazione_flusso_rendicontazione_mygov_entepsp_id_fkey FOREIGN KEY (mygov_entepsp_id) REFERENCES public.mygov_entepsp(mygov_entepsp_id) ON DELETE CASCADE;


--
-- TOC entry 2555 (class 2606 OID 33813)
-- Name: mygov_segnalazione mygov_segnalazione_mygov_ente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_segnalazione
    ADD CONSTRAINT mygov_segnalazione_mygov_ente_fkey FOREIGN KEY (mygov_ente_id) REFERENCES public.mygov_ente(mygov_ente_id);


--
-- TOC entry 2556 (class 2606 OID 33818)
-- Name: mygov_segnalazione mygov_segnalazione_mygov_utente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mypay4
--

ALTER TABLE ONLY public.mygov_segnalazione
    ADD CONSTRAINT mygov_segnalazione_mygov_utente_fkey FOREIGN KEY (mygov_utente_id) REFERENCES public.mygov_utente(mygov_utente_id);


-- Completed on 2022-09-23 10:01:28

--
-- PostgreSQL database dump complete
--

