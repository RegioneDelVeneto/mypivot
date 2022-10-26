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
import it.regioneveneto.mygov.payment.mypivot4.dto.EnteTipoDovutoTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.EnteTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.EnteTipoDovuto;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteService;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteTipoDovutoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Enti", description = "Gestione degli enti beneficiari")
@RestController
@RequestMapping("enti")
@Slf4j
@ConditionalOnWebApplication
public class EnteController {

  @Autowired
  EnteService enteService;

  @Autowired
  EnteTipoDovutoService enteTipoDovutoService;

  @GetMapping("{id}/tipiDovuto")
  @Operatore("id")
  public List<EnteTipoDovutoTo> getTipiDovutoByEnteId(@PathVariable Long id){
    List<EnteTipoDovuto> listEnteTipiDovuto = enteTipoDovutoService.getByMygovEnteIdAndFlags(id, null);
    return listEnteTipiDovuto.stream().map(enteTipoDovutoService::mapEnteTipoDovutoToDto).collect(Collectors.toList());
  }

  /** For combo box **/
  @GetMapping("/tipiDovuto/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_STATS)
  public List<EnteTipoDovuto> getTipiDovuto(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId) {
    return enteTipoDovutoService.getByEnteCodFedUserId(mygovEnteId, user.getUsername());
  }

  @GetMapping("{id}/tipiDovutoOperatore")
  @Operatore("id")
  public List<EnteTipoDovutoTo> getByMygovEnteIdAndOperatoreUsername(
      @PathVariable Long id,
      @AuthenticationPrincipal UserWithAdditionalInfo user){
    List<EnteTipoDovuto> listEnteTipiDovuto = enteTipoDovutoService.getByMygovEnteIdAndOperatoreUsername(id,user.getUsername());
    return listEnteTipiDovuto.stream().map(enteTipoDovutoService::mapEnteTipoDovutoToDto).collect(Collectors.toList());
  }

  @GetMapping("byUtente")
  @Operatore()
  public ResponseEntity<List<EnteTo>> getEntiByUtente(@AuthenticationPrincipal UserWithAdditionalInfo user) {
    List<Ente> listEnti = enteService.getEntiByOperatoreUsername(user.getUsername()).stream()
        //filter list enti according to user profile
        .filter(ente -> user.getEntiRoles().containsKey(ente.getCodIpaEnte()))
        .collect(Collectors.toList());
    if(listEnti.isEmpty())
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    return ResponseEntity.ok(listEnti.stream().map(enteService::mapEnteToDtoWithThumbnail).collect(Collectors.toList()));
  }

}
