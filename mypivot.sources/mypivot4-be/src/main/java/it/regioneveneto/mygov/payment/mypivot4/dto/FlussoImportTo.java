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
package it.regioneveneto.mygov.payment.mypivot4.dto;

import it.regioneveneto.mygov.payment.mypay4.dto.BaseTo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlussoImportTo extends BaseTo {

  private Long id;
  private String nomeFlusso;
  private String deTipo;
  private String codTipo;
  private LocalDateTime dataCaricamento;
  private String operatore;
  private String deStato;
  private String codStato;
  private String codErrore;
  private String log;
  private String filePathOriginale;
  private String filePathScarti;
  private Boolean showDownload;
  private String securityToken;
}
