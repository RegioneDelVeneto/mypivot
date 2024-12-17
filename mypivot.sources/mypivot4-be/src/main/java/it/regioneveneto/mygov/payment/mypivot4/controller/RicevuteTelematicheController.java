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
import it.regioneveneto.mygov.payment.mypay4.config.MyPay4AbstractSecurityConfig;
import it.regioneveneto.mygov.payment.mypay4.exception.BadRequestException;
import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import it.regioneveneto.mygov.payment.mypay4.exception.NotFoundException;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoRicevutaTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.ReceiptExportTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.ReceiptTransferExportTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.RicevutaSearchTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteService;
import it.regioneveneto.mygov.payment.mypivot4.service.FlussoExportService;
import it.regioneveneto.mygov.payment.mypivot4.service.MyPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "RicevuteTelematiche", description = "Ricevute Telematiche")
@RestController
@Slf4j
@ConditionalOnWebApplication
public class RicevuteTelematicheController {

  private final static String AUTHENTICATED_PATH ="ricevute-telematiche";
  private final static String A2A_PATH= MyPay4AbstractSecurityConfig.PATH_A2A+"/"+ AUTHENTICATED_PATH;

  @Autowired
  EnteService enteService;
  @Autowired
  FlussoExportService flussoExportService;
  @Autowired
  MyPayService myPayService;

  @PostMapping(AUTHENTICATED_PATH+"/search/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public List<FlussoRicevutaTo> searchRicevuteTelematiche(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                                      @PathVariable Long mygovEnteId, @RequestBody RicevutaSearchTo searchParams) {
    searchParams = flussoExportService.stripToNull(Optional.ofNullable(searchParams));
    return flussoExportService.searchRicevuteTelematiche(mygovEnteId, user.getUsername(), searchParams);
  }

  @GetMapping(AUTHENTICATED_PATH+"/mypayinfo/{mygovEnteId}/{iuv}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public FlussoRicevutaTo getInfoFromMyPay(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                           @PathVariable Long mygovEnteId, @PathVariable String iuv) {
    Ente ente = Optional.of(enteService.getEnteById(mygovEnteId)).orElseThrow(NotFoundException::new);
    return myPayService.getRtInfo(ente.getCodiceFiscaleEnte(), iuv);
  }

  @GetMapping(AUTHENTICATED_PATH+"/mypayinfo/{mygovEnteId}/{iuv}/{codFiscalePa1}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_VISUAL)
  public FlussoRicevutaTo getInfoEntePrimarioFromMyPay(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                           @PathVariable Long mygovEnteId, @PathVariable String iuv, @PathVariable String codFiscalePa1) {
    return myPayService.getRtInfo(codFiscalePa1, iuv);
  }

  @PostMapping(A2A_PATH+"/import/2/{mygovReceiptId}")
  public void importReceiptForSecondaryEnte(@RequestBody ReceiptExportTo receiptExportTo, @PathVariable Long mygovReceiptId) {
    try{
      if(!mygovReceiptId.equals(receiptExportTo.getMygovReceiptId())) {
        log.error("invalid input data, receiptTo null[{}] mygovReceiptId[{}]", receiptExportTo, mygovReceiptId);
        throw new BadRequestException("invalid receipt data");
      }
      String idDominioEntiSecondari = receiptExportTo.getReceiptTransferExportToList().stream()
          .map(ReceiptTransferExportTo::getFiscalCodePA)
          .distinct()
          .filter(fiscalCode -> !StringUtils.equals(receiptExportTo.getFiscalCode(), fiscalCode))
          .collect(Collectors.joining(";"));
      log.info("parsed message - dominio[{}] - IUV[{}] - receiptId[{}] - mygovReceiptId[{}]",
          idDominioEntiSecondari, receiptExportTo.getCreditorReferenceId(),
          receiptExportTo.getReceiptId(), receiptExportTo.getMygovReceiptId());
      boolean importOutcome = flussoExportService.importReceiptForSecondaryEnte(receiptExportTo);
      log.info("importOutcome: {}", importOutcome);
    }catch(MyPayException mpe){
      log.error("error importing receipt", mpe);
      throw mpe;
    }catch(Exception e){
      log.error("error importing receipt", e);
      throw new MyPayException("error importing receipt", e);
    }
  }
}
