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
import it.regioneveneto.mygov.payment.mypay4.security.Operatore.Role;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoRicevutaTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.VmStatisticaCapitoloTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.VmStatisticaTo;
import it.regioneveneto.mygov.payment.mypivot4.model.AnagraficaUffCapAcc;
import it.regioneveneto.mygov.payment.mypivot4.service.StatisticaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Statistiche", description = "Totali Ripartiti")
@RestController
@RequestMapping("statistiche")
@Slf4j
@ConditionalOnWebApplication
public class StatisticaController {

  @Autowired
  private StatisticaService statisticaService;

  /** For combo box **/
  @GetMapping("/uffici/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_STATS)
  public List<AnagraficaUffCapAcc> getUfficiByEnteTipo(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                             @RequestParam String codTipo) {
    return statisticaService.getAnagraficaByEnteTipo(mygovEnteId, user.getUsername(), codTipo);
  }

  /** For combo box **/
  @GetMapping("/uffici/all/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_STATS)
  public List<AnagraficaUffCapAcc> getUfficiByEnte(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId) {
    return statisticaService.getAnagraficaByEnte(mygovEnteId, user.getUsername());
  }

  /** For combo box **/
  @GetMapping("/capitoli/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_STATS)
  public List<AnagraficaUffCapAcc> getCapitoli(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                               @RequestParam String codTipo, @RequestParam String codUfficio) {
    return statisticaService.getAnagraficaByEnteTipoUfficio(mygovEnteId, user.getUsername(), codTipo, codUfficio);
  }

  @GetMapping("/ufficio/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_STATS)
  public List<VmStatisticaTo> ufficio(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                      @PathVariable Long mygovEnteId, @RequestParam(required = false) Integer anno,
                                      @RequestParam(required = false) Integer mese, @RequestParam(required = false) Integer giorno) {
    return statisticaService.getTotaliRipartitiPerUffici(mygovEnteId, anno, mese, giorno, user.getUsername());
  }

  @GetMapping("/tipoDovuto/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_STATS)
  public List<VmStatisticaTo> tipoDovuto(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                         @PathVariable Long mygovEnteId, @RequestParam(required = false) Integer anno,
                                         @RequestParam(required = false) Integer mese, @RequestParam(required = false) Integer giorno) {
    return statisticaService.getTotaliRipartitiPerTipiDovuto(mygovEnteId, anno, mese, giorno, user.getUsername());
  }

  @GetMapping("/capitolo/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_STATS)
  public List<VmStatisticaCapitoloTo> capitolo(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                               @PathVariable Long mygovEnteId, @RequestParam(required = false) Integer anno,
                                               @RequestParam(required = false) Integer mese, @RequestParam(required = false) Integer giorno,
                                               @RequestParam(required = false) String codTipo, @RequestParam(required = false) String codUfficio) {
    return statisticaService.getTotaliRipartitiPerCapitoli(mygovEnteId, anno, mese, giorno, codTipo, codUfficio, user.getUsername());
  }

  @GetMapping("/accertamento/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_STATS)
  public List<VmStatisticaTo> accertamento(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                       @PathVariable Long mygovEnteId, @RequestParam(required = false) Integer anno,
                                       @RequestParam(required = false) Integer mese, @RequestParam(required = false) Integer giorno,
                                       @RequestParam String codTipo, @RequestParam String codUfficio, String codCapitolo) {
    return statisticaService.getTotaliRipartitiPerAccertamenti(mygovEnteId, anno, mese, giorno, codTipo, codUfficio, codCapitolo, user.getUsername());
  }

  @GetMapping("/anno/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_STATS)
  public List<VmStatisticaTo> anno(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                   @PathVariable Long mygovEnteId, @RequestParam List<Integer> anni) {
    return statisticaService.getTotaliRipartitiPerAnno(mygovEnteId, anni);
  }

  @GetMapping("/mese/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_STATS)
  public List<VmStatisticaTo> mese(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                   @RequestParam Integer anno, @RequestParam List<Integer> mesi) {
    return statisticaService.getTotaliRipartitiPerMese(mygovEnteId, anno, mesi);
  }

  @GetMapping("/giorno/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_STATS)
  public List<VmStatisticaTo> giorno(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                    @RequestParam LocalDate from, @RequestParam LocalDate to) {
    return statisticaService.getTotaliRipartitiPerGiorno(mygovEnteId, from, to);
  }

  @GetMapping("/dettaglio/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_STATS)
  public List<FlussoRicevutaTo> getRicevuteTelematiche(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                                       @RequestParam String codTipo, @RequestParam String codUfficio, @RequestParam String codCapitolo,
                                                       @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to,
                                                       @RequestParam(required = false) String iuv, @RequestParam(required = false) String iur, @RequestParam(required =  false) String attestante,
                                                       @RequestParam(required = false) String cfPagatore, @RequestParam(required = false) String anagPagatore,
                                                       @RequestParam(required = false) String cfVersante, @RequestParam(required = false) String anagVersante) {
    return statisticaService.getRicevuteTelematiche(mygovEnteId, user.getUsername(), codTipo, codUfficio, codCapitolo, from, to,
                                                  iuv, iur, attestante, cfPagatore, anagPagatore, cfVersante,anagVersante);
  }
}
