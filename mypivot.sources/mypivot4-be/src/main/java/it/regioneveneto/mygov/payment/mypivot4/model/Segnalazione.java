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
import org.jdbi.v3.core.mapper.Nested;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "mygovManageFlussoId")
public class Segnalazione extends BaseEntity {

  public final static String ALIAS = "Segnalazione";
  public final static String FIELDS = ""+ALIAS+".mygov_segnalazione_id as Segnalazione_mygovSegnalazioneId,"+ALIAS+".mygov_ente_id as Segnalazione_mygovEnteId"+
      ","+ALIAS+".mygov_utente_id as Segnalazione_mygovUtenteId"+
      ","+ALIAS+".classificazione_completezza as Segnalazione_classificazioneCompletezza"+
      ","+ALIAS+".cod_iud as Segnalazione_codIud,"+ALIAS+".cod_iuv as Segnalazione_codIuv"+
      ","+ALIAS+".cod_iuf as Segnalazione_codIuf,"+ALIAS+".de_nota as Segnalazione_deNota"+
      ","+ALIAS+".flg_nascosto as Segnalazione_flgNascosto,"+ALIAS+".flg_attivo as Segnalazione_flgAttivo"+
      ","+ALIAS+".dt_creazione as Segnalazione_dtCreazione,"+ALIAS+".dt_ultima_modifica as Segnalazione_dtUltimaModifica"+
      ","+ALIAS+".version as Segnalazione_version";

  private Long mygovSegnalazioneId;
  @Nested(Ente.ALIAS)
  private Ente mygovEnteId;
  @Nested(Utente.ALIAS)
  private Utente mygovUtenteId;
  private String classificazioneCompletezza;
  private String codIud;
  private String codIuv;
  private String codIuf;
  private String deNota;
  private boolean flgNascosto;
  private boolean flgAttivo;
  private Timestamp dtCreazione;
  private Timestamp dtUltimaModifica;
  private int version;

}
