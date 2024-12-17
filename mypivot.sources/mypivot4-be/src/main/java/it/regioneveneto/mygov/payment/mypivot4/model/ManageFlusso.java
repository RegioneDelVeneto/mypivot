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
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "mygovManageFlussoId")
public class ManageFlusso extends BaseEntity {

  public final static String ALIAS = "ManageFlusso";
  public final static String FIELDS = ""+ALIAS+".mygov_manage_flusso_id as ManageFlusso_mygovManageFlussoId,"+ALIAS+".version as ManageFlusso_version"+
      ","+ALIAS+".mygov_ente_id as ManageFlusso_mygovEnteId,"+ALIAS+".identificativo_psp as ManageFlusso_identificativoPsp"+
      ","+ALIAS+".cod_identificativo_flusso as ManageFlusso_codIdentificativoFlusso"+
      ","+ALIAS+".dt_data_ora_flusso as ManageFlusso_dtDataOraFlusso"+
      ","+ALIAS+".mygov_tipo_flusso_id as ManageFlusso_mygovTipoFlussoId"+
      ","+ALIAS+".mygov_utente_id as ManageFlusso_mygovUtenteId"+
      ","+ALIAS+".mygov_anagrafica_stato_id as ManageFlusso_mygovAnagraficaStatoId"+
      ","+ALIAS+".de_percorso_file as ManageFlusso_dePercorsoFile,"+ALIAS+".de_nome_file as ManageFlusso_deNomeFile"+
      ","+ALIAS+".num_dimensione_file_scaricato as ManageFlusso_numDimensioneFileScaricato"+
      ","+ALIAS+".cod_request_token as ManageFlusso_codRequestToken,"+ALIAS+".dt_creazione as ManageFlusso_dtCreazione"+
      ","+ALIAS+".dt_ultima_modifica as ManageFlusso_dtUltimaModifica"+
      ","+ALIAS+".cod_provenienza_file as ManageFlusso_codProvenienzaFile"+
      ","+ALIAS+".id_chiave_multitabella as ManageFlusso_idChiaveMultitabella"+
      ","+ALIAS+".de_nome_file_scarti as ManageFlusso_deNomeFileScarti,"+ALIAS+".cod_errore as ManageFlusso_codErrore"+
      ","+ALIAS+".num_righe_totali as ManageFlusso_numRigheTotali"+
      ","+ALIAS+".num_righe_importate_correttamente as ManageFlusso_numRigheImportateCorrettamente";


  private Long mygovManageFlussoId;
  private int version;
  @Nested(Ente.ALIAS)
  private Ente mygovEnteId;
  private String identificativoPsp;
  private String codIdentificativoFlusso;
  private Timestamp dtDataOraFlusso;
  @Nested(TipoFlusso.ALIAS)
  private TipoFlusso mygovTipoFlussoId;
  @Nested(Utente.ALIAS)
  private Utente mygovUtenteId;
  @Nested(AnagraficaStato.ALIAS)
  private AnagraficaStato mygovAnagraficaStatoId;
  private String dePercorsoFile;
  private String deNomeFile;
  private Long numDimensioneFileScaricato;
  private String codRequestToken;
  private Timestamp dtCreazione;
  private Timestamp dtUltimaModifica;
  private String codProvenienzaFile;
  private Long idChiaveMultitabella;
  private String deNomeFileScarti;
  private String codErrore;
  private Long numRigheTotali;
  private Long numRigheImportateCorrettamente;
}
