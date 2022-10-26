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
package it.regioneveneto.mygov.payment.mypivot4.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypivot4.dto.BilancioTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.TesoreriaTo;
import it.regioneveneto.mygov.payment.mypivot4.service.TesoreriaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Tesoreria", description = "Giornale di Cassa")
@RestController
@RequestMapping("tesoreria")
@Slf4j
@ConditionalOnWebApplication
public class TesoreriaController {

  @Autowired
  private TesoreriaService tesoreriaService;

  @GetMapping("/search/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public List<TesoreriaTo> search(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                  @RequestParam(required = false) String iuv, @RequestParam(required = false) String annoBolletta,
                                  @RequestParam(required = false) String codBolletta, @RequestParam(required = false) String idr,
                                  @RequestParam(required = false) BigDecimal importo, @RequestParam(required = false) String annoDocumento,
                                  @RequestParam(required = false) String codDocumento, @RequestParam(required = false) String annoProvvisorio,
                                  @RequestParam(required = false) String codProvvisorio, @RequestParam(required = false) String ordinante,
                                  @RequestParam(required = false) LocalDate dtContabileFrom, @RequestParam(required = false) LocalDate dtContabileTo,
                                  @RequestParam(required = false) LocalDate dtValutaFrom, @RequestParam(required = false) LocalDate dtValutaTo) {
    return tesoreriaService.search(mygovEnteId, user.getUsername(), iuv, annoBolletta,codBolletta, idr, importo, annoDocumento, codDocumento, annoProvvisorio, codProvvisorio,
        ordinante, dtContabileFrom, dtContabileTo, dtValutaFrom, dtValutaTo);
  }

  @GetMapping("/dettaglio/{mygovEnteId}/{tesoreriaId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public TesoreriaTo detail(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @PathVariable Long tesoreriaId) {
    return tesoreriaService.getDtoById(mygovEnteId, user.getUsername(), tesoreriaId);
  }

  @GetMapping("/dettaglio/bilanci/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public List<BilancioTo> bilanci(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                  @RequestParam String annoBolletta, @RequestParam String codBolletta) {
    return tesoreriaService.bilanci(mygovEnteId, user.getUsername(), annoBolletta, codBolletta);
  }
}
