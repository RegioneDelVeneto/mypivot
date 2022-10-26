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
import it.regioneveneto.mygov.payment.mypay4.service.MyBoxService;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoExportTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoImportTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoTo;
import it.regioneveneto.mygov.payment.mypivot4.model.ManageFlusso;
import it.regioneveneto.mygov.payment.mypivot4.queue.QueueProducer;
import it.regioneveneto.mygov.payment.mypivot4.service.FlussoService;
import it.regioneveneto.mygov.payment.mypivot4.service.PrenotazioneFlussoRiconciliazioneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Flussi", description = "Gestione dei flussi di pagamento e di rendicontazione")
@RestController
@RequestMapping("flussi")
@Slf4j
@ConditionalOnWebApplication
public class FlussoController {

  public final static String FILE_TYPE_FLUSSI_IMPORT = "FLUSSI_IMPORT";
  public final static String FILE_TYPE_FLUSSI_EXPORT = "FLUSSI_EXPORT";

  @Autowired
  FlussoService flussoService;

  @Autowired
  PrenotazioneFlussoRiconciliazioneService prenotazioneFlussoRiconciliazioneService;

  @Autowired
  MyBoxService myBoxService;

  @Autowired
  QueueProducer queueProducer;

  @GetMapping("byEnteId/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_VISUAL)
  public List<FlussoTo> getByEnte(@PathVariable Long mygovEnteId){
    List<ManageFlusso> listFlussi = flussoService.getByEnte(mygovEnteId);
    return listFlussi.stream().map(this::mapManageFlussoToDto).collect(Collectors.toList());
  }

  //TODO: verificare
  private FlussoTo mapManageFlussoToDto(ManageFlusso manageFlusso) {
    return manageFlusso == null ? null : FlussoTo.builder()
        .id(manageFlusso.getMygovManageFlussoId())
        .nome(manageFlusso.getCodIdentificativoFlusso())
        .build();
  }

  @GetMapping("import/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_ADMIN)
  public List<FlussoImportTo> flussiImport(@AuthenticationPrincipal UserWithAdditionalInfo user,
                                           @PathVariable Long mygovEnteId,
                                           @RequestParam String codTipo,
                                           @RequestParam(required = false) String nomeFlusso,
                                           @RequestParam LocalDate from,
                                           @RequestParam LocalDate to){
    List<FlussoImportTo> flussi = flussoService.getByEnteCodIdentificativoFlussoCreateDt(mygovEnteId, codTipo, nomeFlusso, from, to);
    //generate security token (to allow download)
    flussi.stream().forEach(flussoImportTo -> {
      flussoImportTo.setSecurityToken(myBoxService.generateSecurityToken(FILE_TYPE_FLUSSI_IMPORT, flussoImportTo.getFilePathOriginale(), user, mygovEnteId));
    });

    return flussi;
  }

  @GetMapping("export/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_VISUAL)
  public List<FlussoExportTo> flussiExport(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                           @RequestParam(required = false) String nomeFlusso, @RequestParam LocalDate from, @RequestParam LocalDate to){
    List<FlussoExportTo> flussi = prenotazioneFlussoRiconciliazioneService.flussiExport(mygovEnteId, user.getCodiceFiscale(), nomeFlusso, from, to);

    //generate security token (to allow download)
    flussi.stream().forEach(flussoExportTo -> {
      flussoExportTo.setSecurityToken(myBoxService.generateSecurityToken(FILE_TYPE_FLUSSI_EXPORT, flussoExportTo.getPath(), user, mygovEnteId));
    });

    return flussi;
  }
}
