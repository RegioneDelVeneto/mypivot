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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.mygov.payment.mypay4.exception.ValidatorException;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypivot4.dto.AccertamentoCapitoloTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.AccertamentoFlussoExportTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.AccertamentoTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoRicevutaTo;
import it.regioneveneto.mygov.payment.mypivot4.model.AnagraficaStato;
import it.regioneveneto.mygov.payment.mypivot4.model.AnagraficaUffCapAcc;
import it.regioneveneto.mygov.payment.mypivot4.model.CapitoloRT;
import it.regioneveneto.mygov.payment.mypivot4.service.AccertamentoDettaglioService;
import it.regioneveneto.mygov.payment.mypivot4.service.AccertamentoService;
import it.regioneveneto.mygov.payment.mypivot4.service.AnagraficaStatoService;
import it.regioneveneto.mygov.payment.mypivot4.service.ImportMassivoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Accertamenti", description = "Accertamenti")
@RestController
@RequestMapping("accertamenti")
@Slf4j
@ConditionalOnWebApplication
public class AccertamentoController {

  @Autowired
  private AnagraficaStatoService anagraficaStatoService;

  @Autowired
  private AccertamentoService accertamentoService;

  @Autowired
  private AccertamentoDettaglioService accertamentoDettaglioService;

  @Autowired
  private ImportMassivoService importMassivoService;

  @Autowired
  Jackson2ObjectMapperBuilder mapperBuilder;

  /** For combo box **/
  @GetMapping("/uffici/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public List<AnagraficaUffCapAcc> getUfficiByEnteTipo(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                                       @RequestParam String codTipo) {
    return accertamentoDettaglioService.getAnagraficaByEnteTipo(mygovEnteId, user.getUsername(), codTipo);
  }

  /** For combo box **/
  @GetMapping("/capitoli/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public List<AnagraficaUffCapAcc> getCapitoli(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                               @RequestParam String codTipo, @RequestParam String annoEsercizio, @RequestParam String codUfficio) {
    return accertamentoDettaglioService.getAnagraficaByEnteTipoUfficio(mygovEnteId, user.getUsername(), codTipo, codUfficio, annoEsercizio);
  }

  /** For combo box **/
  @GetMapping("/accertamenti/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public List<AnagraficaUffCapAcc> getAccertamenti(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                                   @RequestParam String codTipo, @RequestParam String codUfficio, @RequestParam String annoEsercizio, @RequestParam String codCapitolo) {
    return accertamentoDettaglioService.getAnagraficaByEnteTipoUfficioCapitolo(mygovEnteId, user.getUsername(), codTipo, codUfficio, annoEsercizio, codCapitolo);
  }

  @GetMapping("/stati")
  @Operatore(roles = Operatore.Role.ROLE_ACC)
  public List<AnagraficaStato> stati(@AuthenticationPrincipal UserWithAdditionalInfo user) {
    return anagraficaStatoService.getByTipoStato(Constants.DE_TIPO_STATO_ACCERTAMENTO);
  }

  @GetMapping("/capitolo/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public List<AccertamentoCapitoloTo> getcapitoli(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @RequestParam(required = false) String codTipo,
                                                  @RequestParam(required = false) String codUfficio, @RequestParam(required = false) String deUfficio, @RequestParam(required = false) Boolean flgUfficioAttivo,
                                                  @RequestParam(required = false) String codCapitolo, @RequestParam(required = false) String deCapitolo, @RequestParam(required = false) String annoCapitolo,
                                                  @RequestParam(required = false) String codAccertamento, @RequestParam(required = false) String deAccertamento) {
    return accertamentoService.getAccertamentiCapitoli(mygovEnteId, user.getUsername(), codTipo, codUfficio, deUfficio, flgUfficioAttivo, codCapitolo, deCapitolo, annoCapitolo, codAccertamento, deAccertamento);
  }

  @PutMapping("/capitolo/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public AccertamentoCapitoloTo upsertCapitolo(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @RequestParam(required = false) String codTipo,
                                             @RequestBody AccertamentoCapitoloTo accertamentoCapitoloTo) {
    return accertamentoService.upsertAccertamentoCapitolo(mygovEnteId, user.getUsername(), accertamentoCapitoloTo);
  }

  @GetMapping("/capitolo/{mygovEnteId}/{anagraficaId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public AccertamentoCapitoloTo getCapitolo(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @RequestParam(required = false) String codTipo,
                                            @PathVariable Long anagraficaId) {
    return accertamentoService.getAccertamentoCapitolo(mygovEnteId, user.getUsername(), anagraficaId);
  }

  @GetMapping("/list/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public List<AccertamentoTo> list(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to,
                                   @RequestParam(required = false) String codIuv, @RequestParam(required = false) String codTipo, @RequestParam(required = false) String codStato, @RequestParam(required = false) String deNomeAccertamento) {
    return accertamentoService.getAccertamenti(mygovEnteId, user, from, to, codIuv, codTipo, codStato, deNomeAccertamento);
  }

  @GetMapping("/list/{mygovEnteId}/{accertamentoId}/anagrafica")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public AccertamentoTo accertamentoAnagrafica(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @PathVariable Long accertamentoId,
                                               @RequestParam(required = false) LocalDate dtEsitoFrom, @RequestParam(required = false) LocalDate dtEsitoTo,
                                               @RequestParam(required = false) LocalDate dtUltimoAggFrom, @RequestParam(required = false) LocalDate dtUltimoAggTo,
                                               @RequestParam(required = false) String codIud, @RequestParam(required = false) String codIuv, @RequestParam(required = false) String cfPagatore) {
    return accertamentoService.getDtoById(accertamentoId);
  }

  @PutMapping("/list/{mygovEnteId}/{accertamentoId}/close")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public AccertamentoTo close(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @PathVariable Long accertamentoId) {
    return accertamentoService.updateStato(mygovEnteId, user.getUsername(), accertamentoId, Constants.COD_TIPO_STATO_ACCERTAMENTO_CHIUSO);
  }

  @PutMapping("/list/{mygovEnteId}/{accertamentoId}/cancel")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public AccertamentoTo cancel(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @PathVariable Long accertamentoId) {
    return accertamentoService.updateStato(mygovEnteId, user.getUsername(), accertamentoId, Constants.COD_TIPO_STATO_ACCERTAMENTO_ANNULLATO);
  }

  @GetMapping("/list/{mygovEnteId}/{accertamentoId}/toAddRT")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public List<AccertamentoFlussoExportTo> getAccertamentiPagamentiInseribili(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @PathVariable Long accertamentoId,
                                                                             @RequestParam(required = false) LocalDate dtEsitoFrom, @RequestParam(required = false) LocalDate dtEsitoTo,
                                                                             @RequestParam(required = false) LocalDate dtUltimoAggFrom, @RequestParam(required = false) LocalDate dtUltimoAggTo,
                                                                             @RequestParam(required = false) String codIud, @RequestParam(required = false) String codIuv, @RequestParam(required = false) String cfPagatore) {
    return accertamentoDettaglioService.getAccertamentiPagamentiInseribili(user.getUsername(), mygovEnteId, accertamentoId, codIud, codIuv, cfPagatore, dtEsitoFrom, dtEsitoTo, dtUltimoAggFrom, dtUltimoAggTo);
  }

  @GetMapping("/list/{mygovEnteId}/{accertamentoId}/capitoli")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public List<CapitoloRT> getcapitoliRT(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @PathVariable Long accertamentoId,
                                        @RequestParam String codTipo, @RequestParam String codIuv, @RequestParam String codIud) {
    return accertamentoDettaglioService.getCapitoliByRT(user.getUsername(), mygovEnteId, accertamentoId, codTipo, codIud, codIuv);
  }

  @GetMapping("/list/{mygovEnteId}/{accertamentoId}/dettaglioRT")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public List<AccertamentoFlussoExportTo> getAccertamentiPagamentiInseriti(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @PathVariable Long accertamentoId,
                                                                           @RequestParam(required = false) LocalDate dtEsitoFrom, @RequestParam(required = false) LocalDate dtEsitoTo,
                                                                           @RequestParam(required = false) LocalDate dtUltimoAggFrom, @RequestParam(required = false) LocalDate dtUltimoAggTo,
                                                                           @RequestParam(required = false) String codIud, @RequestParam(required = false) String codIuv, @RequestParam(required = false) String cfPagatore) {
    return accertamentoDettaglioService.getAccertamentiPagamentiInseriti(user.getUsername(), mygovEnteId, accertamentoId, codIud, codIuv, cfPagatore, dtEsitoFrom, dtEsitoTo, dtUltimoAggFrom, dtUltimoAggTo);
  }

  @DeleteMapping("/list/{mygovEnteId}/{accertamentoId}/dettaglioRT")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public int deleteAccertamentiPagamentiInseriti(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @PathVariable Long accertamentoId,
                                                 @RequestParam String json){
    try {
      List<AccertamentoFlussoExportTo> accertamenti = mapperBuilder.build().readValue(json, new TypeReference<>() { });
      return accertamentoDettaglioService.deleteAccertamentiPagamentiInseriti(user.getUsername(), mygovEnteId, accertamentoId, accertamenti);
    } catch (JsonProcessingException e) {
      throw new ValidatorException(e.getMessage());
    }
  }

  @PutMapping("/list/{mygovEnteId}/{accertamentoId}/dettaglioRT")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public int putAccertamentiPagamentiInseriti(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @PathVariable Long accertamentoId,
                                              @RequestParam String codUfficio, @RequestParam String annoEsercizio, @RequestParam String codCapitolo, @RequestParam String codAccertamento,
                                              @RequestParam String json ) {
    try {
      List<AccertamentoFlussoExportTo> accertamenti = mapperBuilder.build().readValue(json, new TypeReference<>() { });
      return accertamentoDettaglioService.insertAccertamentiPagamenti(user.getUsername(), mygovEnteId, accertamentoId, accertamenti, codUfficio, annoEsercizio, codCapitolo, codAccertamento);
    } catch (JsonProcessingException e) {
      throw new ValidatorException(e.getMessage());
    }
  }

  @GetMapping("/ricevuteTelematiche/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public List<FlussoRicevutaTo> getRicevuteTelematiche(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                                       @RequestParam String codTipo, @RequestParam String codIud,
                                                       @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to,
                                                       @RequestParam(required = false) String iuv, @RequestParam(required = false) String iur, @RequestParam(required =  false) String attestante,
                                                       @RequestParam(required = false) String cfPagatore, @RequestParam(required = false) String anagPagatore,
                                                       @RequestParam(required = false) String cfVersante, @RequestParam(required = false) String anagVersante) {
    return accertamentoDettaglioService.getRicevuteTelematiche(mygovEnteId, user.getUsername(), codTipo, codIud, from, to, iuv, iur, attestante, cfPagatore, anagPagatore, cfVersante,anagVersante);
  }

  @PostMapping("/insert/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public AccertamentoTo insert(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to,
                                     @RequestBody AccertamentoTo accertamentoTo) {
    return accertamentoService.insert(mygovEnteId, user.getUsername(), accertamentoTo);
  }

  @PutMapping("/update/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ACC)
  public AccertamentoTo update(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId, @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to,
                               @RequestBody AccertamentoTo accertamentoTo) {
    return accertamentoService.update(mygovEnteId, user.getUsername(), accertamentoTo);
  }

  @PostMapping("/importMassivo/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Operatore.Role.ROLE_ADMIN)
  public ResponseEntity importMassivo(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                      @RequestParam String type, @RequestParam("file") MultipartFile file) throws Exception {
    try {
      importMassivoService.importMassivo(mygovEnteId, user.getUsername(), file);
      return ResponseEntity.ok().build();
    } catch(ValidatorException ex) {
      throw ex;
    } catch(Exception ex) {
      log.error(ex.getMessage());
      throw ex;
    }
  }
}
