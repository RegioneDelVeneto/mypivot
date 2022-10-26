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
import it.regioneveneto.mygov.payment.mypay4.exception.BadRequestException;
import it.regioneveneto.mygov.payment.mypay4.exception.NotFoundException;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypivot4.dto.EnteTipoDovutoTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.EnteTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.OperatoreTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.EnteTipoDovuto;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteService;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteTipoDovutoService;
import it.regioneveneto.mygov.payment.mypivot4.service.OperatoreEnteTipoDovutoService;
import it.regioneveneto.mygov.payment.mypivot4.service.OperatoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Gestione tipi dovuto esterni", description = "Backoffice - gestione dei Tipi dovuto esterni")
@RestController
@RequestMapping("admin")
@Slf4j
@ConditionalOnWebApplication
public class BackofficeEnteTipoDovutoController {

  @Autowired
  private EnteService enteService;

  @Autowired
  private EnteTipoDovutoService enteTipoDovutoService;

  @Autowired
  private OperatoreEnteTipoDovutoService operatoreEnteTipoDovutoService;

  @Autowired
  private OperatoreService operatoreService;

  @GetMapping("enti")
  @Operatore(appAdmin = true)
  public List<EnteTo> searchEnti(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                 @RequestParam(required = false) String codIpaEnte,
                                 @RequestParam(required = false) String deNome,
                                 @RequestParam(required = false) String codFiscale){
    List<Ente> listEnti = enteService.searchEnti(codIpaEnte, deNome, codFiscale);
    return listEnti.stream().map(enteService::mapEnteToDtoWithThumbnail).collect(Collectors.toList());
  }

  @GetMapping("enti/{id}")
  @Operatore(appAdmin = true)
  public EnteTo getEnte(@AuthenticationPrincipal UserWithAdditionalInfo user,
                        @PathVariable Long id){
    return Optional.of(enteService.getEnteById(id))
        .map(enteService::mapEnteToDtoWithThumbnail)
        .orElseThrow(NotFoundException::new);
  }

  @GetMapping("enti/{id}/tipiDovutoEsterni")
  @Operatore(appAdmin = true)
  public List<EnteTipoDovutoTo> getTipiDovutoEsterniByEnteId(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                                             @PathVariable Long id){
    List<EnteTipoDovuto> listEnteTipiDovuto = enteTipoDovutoService.getByMygovEnteIdAndFlags(id, true);
    return listEnteTipiDovuto.stream().map(enteTipoDovutoService::mapEnteTipoDovutoToDto).collect(Collectors.toList());
  }

  @GetMapping("tipiDovuto/{id}")
  @Operatore(appAdmin = true)
  public EnteTipoDovutoTo getTipoDovutoEsternoById(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                                   @PathVariable Long id){
    return enteTipoDovutoService.getById(id)
        .filter(t -> t.isEsterno())
        .map(enteTipoDovutoService::mapEnteTipoDovutoToDto)
        .orElseThrow(NotFoundException::new);
  }

  @PostMapping("enti/{enteId}/tipiDovutoEsterni")
  @Operatore(appAdmin = true)
  public void insertTipoDovutoEsterno(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                      @PathVariable Long enteId,
                                      @RequestBody EnteTipoDovutoTo enteTipoDovuto){
    //check compulsory fields
    if(enteTipoDovuto==null || StringUtils.isBlank(enteTipoDovuto.getCodTipo())
        || StringUtils.isBlank(enteTipoDovuto.getDeTipo())
        || !Boolean.TRUE.equals(enteTipoDovuto.getEsterno()) )
      throw new BadRequestException();

    enteTipoDovuto.setMygovEnteId(enteId);

    enteTipoDovutoService.insertTipoDovuto(enteTipoDovuto);
  }

  @PostMapping("tipiDovuto/{id}")
  @Operatore(appAdmin = true)
  public void updateTipoDovutoEsterno(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                          @PathVariable Long id, @RequestBody EnteTipoDovutoTo enteTipoDovuto){
    //check compulsory fields
    if(enteTipoDovuto==null || StringUtils.isBlank(enteTipoDovuto.getCodTipo())
        || StringUtils.isBlank(enteTipoDovuto.getDeTipo()) )
      throw new BadRequestException();

    enteTipoDovutoService.getById(id)
        .filter(t -> t.isEsterno())
        .map(t -> enteTipoDovutoService.updateTipoDovuto(t.getMygovEnteTipoDovutoId(), enteTipoDovuto.getCodTipo(), enteTipoDovuto.getDeTipo()))
        .orElseThrow(NotFoundException::new);
  }

  @DeleteMapping("tipiDovuto/{id}")
  @Operatore(appAdmin = true)
  public void deleteTipoDovutoEsterno(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                                      @PathVariable Long id){
    enteTipoDovutoService.getById(id)
        .filter(t -> t.isEsterno())
        .map(t -> enteTipoDovutoService.deleteTipoDovuto(t.getMygovEnteTipoDovutoId()))
        .orElseThrow(NotFoundException::new);
  }

  @GetMapping("tipiDovuto/{id}/operatori")
  @Operatore(appAdmin = true)
  public List<OperatoreTo> getOperatoriTipoDovutoEsternoById(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                                             @PathVariable Long id){
    return enteTipoDovutoService.getById(id)
        .filter(EnteTipoDovuto::isEsterno)
        .map(t -> operatoreEnteTipoDovutoService.getOperatoriByTipoDovutoId(t.getMygovEnteTipoDovutoId()))
        .orElseThrow(NotFoundException::new)
        .stream()
        .map(operatoreService::mapOperatoreToDto)
        .sorted((OperatoreTo o1, OperatoreTo o2) -> o1.isNotRegisteredUser()!=o2.isNotRegisteredUser() ? (o1.isNotRegisteredUser()?1:-1) : 0)
        .collect(Collectors.toList());
  }

  @PostMapping("tipiDovuto/{tipoDovutoId}/operatori/{operatoreId}/enabled/{newState}")
  @Operatore(appAdmin = true)
  public void swtichStateOperatoreTipoDovuto(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                                          @PathVariable Long tipoDovutoId, @PathVariable Long operatoreId,
                                                          @PathVariable boolean newState){
    //check tipoDovuto exists and is external
    enteTipoDovutoService.getById(tipoDovutoId)
        .filter(EnteTipoDovuto::isEsterno)
        .orElseThrow(NotFoundException::new);

    //check operatore exists
    operatoreService.getById(operatoreId)
        .orElseThrow(NotFoundException::new);

    operatoreEnteTipoDovutoService.upsertOperatoreEnteTipoDovuto(tipoDovutoId, operatoreId, newState);
  }

}
