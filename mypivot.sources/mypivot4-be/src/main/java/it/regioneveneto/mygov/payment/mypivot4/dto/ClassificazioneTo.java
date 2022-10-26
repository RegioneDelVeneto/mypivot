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

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.regioneveneto.mygov.payment.mypay4.dto.BaseTo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class ClassificazioneTo extends BaseTo {
  private String key;
  private String label;
  private String infoText;
  @JsonIgnore
  private boolean externalFlow;
  @JsonIgnore
  private String type;
  @JsonIgnore
  private Boolean flgPagati;
  @JsonIgnore
  private Boolean flgTesoreria;
  private Set<String> fields;
  private List<String> exportVersions;
}
