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

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "mygovAccertamentoDettaglioId")
public class AccertamentoDettaglio extends BaseEntity {

  public final static String ALIAS = "AccertamentoDettaglio";
  public final static String FIELDS = ""+ALIAS+".mygov_accertamento_dettaglio_id as AccertamentoDettaglio_mygovAccertamentoDettaglioId"+
      ","+ALIAS+".mygov_accertamento_id as AccertamentoDettaglio_mygovAccertamentoId"+
      ","+ALIAS+".cod_ipa_ente as AccertamentoDettaglio_codIpaEnte"+
      ","+ALIAS+".cod_tipo_dovuto as AccertamentoDettaglio_codTipoDovuto,"+ALIAS+".cod_iud as AccertamentoDettaglio_codIud"+
      ","+ALIAS+".cod_iuv as AccertamentoDettaglio_codIuv,"+ALIAS+".cod_ufficio as AccertamentoDettaglio_codUfficio"+
      ","+ALIAS+".cod_capitolo as AccertamentoDettaglio_codCapitolo"+
      ","+ALIAS+".cod_accertamento as AccertamentoDettaglio_codAccertamento"+
      ","+ALIAS+".num_importo as AccertamentoDettaglio_numImporto"+
      ","+ALIAS+".flg_importo_inserito as AccertamentoDettaglio_flgImportoInserito"+
      ","+ALIAS+".dt_ultima_modifica as AccertamentoDettaglio_dtUltimaModifica"+
      ","+ALIAS+".dt_data_inserimento as AccertamentoDettaglio_dtDataInserimento"+
      ","+ALIAS+".mygov_utente_id as AccertamentoDettaglio_mygovUtenteId";

  private Long mygovAccertamentoDettaglioId;
  @Nested(Accertamento.ALIAS)
  private Accertamento mygovAccertamentoId;
  private String codIpaEnte;
  private String codTipoDovuto;
  private String codIud;
  private String codIuv;
  private String codUfficio;
  private String codCapitolo;
  private String codAccertamento;
  private BigDecimal numImporto;
  private boolean flgImportoInserito;
  private Date dtUltimaModifica;
  private Date dtDataInserimento;
  @Nested(Utente.ALIAS)
  private Utente mygovUtenteId;
}
