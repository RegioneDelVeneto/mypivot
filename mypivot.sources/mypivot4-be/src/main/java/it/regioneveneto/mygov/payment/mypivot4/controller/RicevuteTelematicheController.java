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
import it.regioneveneto.mygov.payment.mypay4.exception.NotFoundException;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoRicevutaTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.RicevutaSearchTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteService;
import it.regioneveneto.mygov.payment.mypivot4.service.FlussoExportService;
import it.regioneveneto.mygov.payment.mypivot4.service.MyPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "RicevuteTelematiche", description = "Ricevute Telematiche")
@RestController
@RequestMapping("ricevute-telematiche")
@Slf4j
@ConditionalOnWebApplication
public class RicevuteTelematicheController {

  @Autowired
  EnteService enteService;
  @Autowired
  FlussoExportService flussoExportService;
  @Autowired
  MyPayService myPayService;

  @PostMapping("/search/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public List<FlussoRicevutaTo> searchRicevuteTelematiche(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                                      @PathVariable Long mygovEnteId, @RequestBody RicevutaSearchTo searchParams) {
    searchParams = flussoExportService.stripToNull(Optional.ofNullable(searchParams));
    return flussoExportService.searchRicevuteTelematiche(mygovEnteId, user.getUsername(), searchParams);
  }

  @GetMapping("/mypayinfo/{mygovEnteId}/{iuv}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public FlussoRicevutaTo getInfoFromMyPay(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                           @PathVariable Long mygovEnteId, @PathVariable String iuv) {
    Ente ente = Optional.of(enteService.getEnteById(mygovEnteId)).orElseThrow(NotFoundException::new);
    return myPayService.getRtInfo(ente.getCodIpaEnte(), iuv);
  }
}
