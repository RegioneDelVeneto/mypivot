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
package it.regioneveneto.mygov.payment.mypivot4.model;

import it.regioneveneto.mygov.payment.mypay4.model.BaseEntity;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class RiconciliazioneSearch extends BaseEntity {
  private String cod_fed_user_id;
  private String codice_ipa_ente;
  private String cod_iud;
  private String cod_iuv;
  private String denominazione_attestante;
  private String identificativo_univoco_riscossione;
  private String codice_identificativo_univoco_versante;
  private String anagrafica_versante;
  private String codice_identificativo_univoco_pagatore;
  private String anagrafica_pagatore;
  private String causale_versamento;
  private Date data_esecuzione_singolo_pagamento_da;
  private Date data_esecuzione_singolo_pagamento_a;
  private Date data_esito_singolo_pagamento_da;
  private Date data_esito_singolo_pagamento_a;
  private String identificativo_flusso_rendicontazione;
  private String identificativo_univoco_regolamento;
  private Date data_regolamento_da;
  private Date data_regolamento_a;
  private Date dt_data_contabile_da;
  private Date dt_data_contabile_a;
  private Date dt_data_valuta_da;
  private Date dt_data_valuta_a;
  private Date dt_data_ultimo_aggiornamento_da;
  private Date dt_data_ultimo_aggiornamento_a;
  private String cod_tipo_dovuto;
  private Boolean codtipodovutopresent;
  private String importo;
  private String conto;
  private String cod_or1;
  private String cod_bolletta;
  private String de_anno_bolletta;
  private String cod_documento;
  private String de_anno_documento;
  private String cod_provvisorio;
  private String de_anno_provvisorio;
  private Boolean flagnascosto;
  private String classificazione_completezza;
}
