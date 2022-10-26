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
package it.regioneveneto.mygov.payment.mypivot4.mapper;

import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoExportTo;
import org.apache.commons.lang3.StringUtils;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FlussoExportToMapper implements RowMapper<FlussoExportTo> {

  @Override
  public FlussoExportTo map(ResultSet rs, StatementContext ctx) throws SQLException {

    FlussoExportTo flussoExport = new FlussoExportTo();
    flussoExport.setIdFlusso(rs.getLong("mygov_prenotazione_flusso_riconciliazione_id"));

    flussoExport.setPath(rs.getString("de_nome_file_generato"));
    if (StringUtils.isNotBlank(flussoExport.getPath())) {
      int idx = flussoExport.getPath().lastIndexOf(File.separator);
      flussoExport.setNome(flussoExport.getPath().substring(idx + 1));
      flussoExport.setDimensione(rs.getLong("num_dimensione_file_generato"));
    }

    flussoExport.setDataPrenotazione(Utilities.toLocalDateTime(rs.getDate("dt_creazione")));
    flussoExport.setOperatore((rs.getString("de_firstname") + " " + rs.getString("de_lastname")).trim());
    flussoExport.setCodStato(rs.getString("cod_stato"));
    flussoExport.setStato(rs.getString("de_stato"));

    flussoExport.setShowDownload(rs.getString("de_tipo_stato").equals(Constants.DE_TIPO_STATO_PRENOTA_FLUSSO_RICONCILIAZIONE)
        && flussoExport.getCodStato().equals(Constants.COD_TIPO_STATO_EXPORT_FLUSSO_RICONCILIAZIONE_EXPORT_ESEGUITO));

    flussoExport.setClassificazione(rs.getString("cod_codice_classificazione"));
    flussoExport.setVersioneTracciato(rs.getString("versione_tracciato"));
    return flussoExport;
  }
}
