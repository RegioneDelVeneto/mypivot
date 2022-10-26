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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import it.regioneveneto.mygov.payment.mypay4.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "mygovEnteId")
public class Ente extends BaseEntity {

  public final static String ALIAS = "Ente";
  public final static String FIELDS = ""+ALIAS+".mygov_ente_id as Ente_mygovEnteId,"+ALIAS+".cod_ipa_ente as Ente_codIpaEnte"+
      ","+ALIAS+".codice_fiscale_ente as Ente_codiceFiscaleEnte,"+ALIAS+".de_nome_ente as Ente_deNomeEnte"+
      ","+ALIAS+".email_amministratore as Ente_emailAmministratore,"+ALIAS+".dt_creazione as Ente_dtCreazione"+
      ","+ALIAS+".dt_ultima_modifica as Ente_dtUltimaModifica,"+ALIAS+".mybox_client_key as Ente_myboxClientKey"+
      ","+ALIAS+".mybox_client_secret as Ente_myboxClientSecret,"+ALIAS+".num_giorni_pagamento_presunti as Ente_numGiorniPagamentoPresunti"+
      ","+ALIAS+".de_password as Ente_dePassword,"+ALIAS+".flg_pagati as Ente_flgPagati"+
      ","+ALIAS+".flg_tesoreria as Ente_flgTesoreria,"+ALIAS+".de_logo_ente as Ente_deLogoEnte";


  private Long mygovEnteId;
  private String codIpaEnte;
  private String codiceFiscaleEnte;
  private String deNomeEnte;
  private String emailAmministratore;
  private Timestamp dtCreazione;
  private Timestamp dtUltimaModifica;
  private String myboxClientKey;
  private String myboxClientSecret;
  private int numGiorniPagamentoPresunti;
  private String dePassword;
  private boolean flgPagati;
  private boolean flgTesoreria;
  private String deLogoEnte;
}
