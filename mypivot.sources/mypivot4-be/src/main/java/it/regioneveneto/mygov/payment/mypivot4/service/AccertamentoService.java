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
package it.regioneveneto.mygov.payment.mypivot4.service;

import it.regioneveneto.mygov.payment.mypay4.exception.ValidatorException;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypay4.util.MaxResultsHelper;
import it.regioneveneto.mygov.payment.mypivot4.dao.AccertamentoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.AnagraficaUffCapAccDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.AccertamentoCapitoloTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.AccertamentoTo;
import it.regioneveneto.mygov.payment.mypivot4.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Transactional
public class AccertamentoService {

  @Autowired
  private EnteService enteService;

  @Autowired
  private OperatoreEnteTipoDovutoService operatoreEnteTipoDovutoService;

  @Autowired
  private EnteTipoDovutoService enteTipoDovutoService;

  @Autowired
  private UtenteService utenteService;

  @Autowired
  private AnagraficaStatoService anagraficaStatoService;

  @Autowired
  private AccertamentoDao accertamentoDao;

  @Autowired
  private AccertamentoDettaglioService accertamentoDettaglioService;

  @Autowired
  private AnagraficaUffCapAccDao anagraficaUffCapAccDao;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  MaxResultsHelper maxResultsHelper;

  public Accertamento getById(Long mygovAccertamentoId) {
    return accertamentoDao.getById(mygovAccertamentoId);
  }

  public AccertamentoTo getDtoById(Long mygovAccertamentoId) {
    return this.mapToDto(accertamentoDao.getById(mygovAccertamentoId));
  }

  public List<AccertamentoCapitoloTo> getAccertamentiCapitoli(Long enteId, String codFedUserId, String codTipo, String codUfficio, String deUfficio, Boolean flgUfficioAttivo,
                                                              String codCapitolo, String deCapitolo, String annoCapitolo, String codAccertamento, String deAccertamento) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto) || (StringUtils.isNotBlank(codTipo) && !codTipo.equals("n/a") && !codTipiDovuto.contains(codTipo))) {
      log.warn("user[codFedUserId: " + codFedUserId + "] :: not authorized for TIPO_DOVUTO["+ codTipo +"].");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }

    List<AnagraficaUffCapAcc> anagrafiche;
    if("n/a".equals(codTipo)) {
      anagrafiche = anagraficaUffCapAccDao.getAccertamentiCapitoliNA(enteId, codTipo, codUfficio, deUfficio, flgUfficioAttivo, codCapitolo, deCapitolo, annoCapitolo, codAccertamento, deAccertamento);
    } else if (codTipo == null) {
      anagrafiche = ListUtils.union(
          anagraficaUffCapAccDao.getAccertamentiCapitoliNA(enteId, codTipo, codUfficio, deUfficio, flgUfficioAttivo, codCapitolo, deCapitolo, annoCapitolo, codAccertamento, deAccertamento),
          anagraficaUffCapAccDao.getAccertamentiCapitoli(enteId, codTipo, codUfficio, deUfficio, flgUfficioAttivo, codCapitolo, deCapitolo, annoCapitolo, codAccertamento, deAccertamento)
      );
    } else {
        anagrafiche = anagraficaUffCapAccDao.getAccertamentiCapitoli(enteId, codTipo, codUfficio,deUfficio, flgUfficioAttivo, codCapitolo, deCapitolo, annoCapitolo, codAccertamento, deAccertamento);
    }
    return anagrafiche.stream().map(anag -> this.mapToDto(anag, codTipo)).collect(Collectors.toList());
  }

  public AccertamentoCapitoloTo getAccertamentoCapitolo(Long enteId, String codFedUserId, Long anagraficaId) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto)) {
      log.warn("user[codFedUserId: " + codFedUserId + "] :: not authorized.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    AnagraficaUffCapAcc anagrafica = anagraficaUffCapAccDao.getById(anagraficaId);
    return this.mapToDto(anagrafica, anagrafica.getCodTipoDovuto());
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public AccertamentoCapitoloTo upsertAccertamentoCapitolo(Long enteId, String codFedUserId, AccertamentoCapitoloTo accertamento) {
    if (StringUtils.isBlank(accertamento.getCodTipoDovuto()))
      throw new ValidatorException("Tipo Dovuto è campo obbligatorio");
    if (StringUtils.isBlank(accertamento.getCodUfficio()))
      throw new ValidatorException("Codice Ufficio è campo obbligatorio");
    if (StringUtils.isBlank(accertamento.getDeUfficio()))
      throw new ValidatorException("Denominazione Ufficio è campo obbligatorio");
    if (StringUtils.isBlank(accertamento.getCodCapitolo()))
      throw new ValidatorException("Codice Capitolo è campo obbligatorio");
    if (StringUtils.isBlank(accertamento.getDeAnnoEsercizio()))
      throw new ValidatorException("Anno Esercizio Capitolo è campo obbligatorio");
    if (StringUtils.isBlank(accertamento.getDeAccertamento()))
      throw new ValidatorException("Denominazione Accertamento è campo obbligatorio");
    if (StringUtils.isBlank(accertamento.getDeAccertamento()))
      throw new ValidatorException("Denominazione Accertamento è campo obbligatorio");

    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(accertamento.getCodTipoDovuto())) {
      log.warn("user[codFedUserId: " + codFedUserId + "] :: not authorized for TIPO_DOVUTO["+ accertamento.getCodTipoDovuto() +"].");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }

    AnagraficaUffCapAcc anagraficaUffCapAcc;
    Long mygovAnagraficaUffCapAccId;
    if (accertamento.getId() == null) {
      anagraficaUffCapAcc = AnagraficaUffCapAcc.builder()
          .mygovEnteId(enteId)
          .codTipoDovuto(accertamento.getCodTipoDovuto())
          .codUfficio(accertamento.getCodUfficio())
          .deUfficio(accertamento.getDeUfficio())
          .codCapitolo(accertamento.getCodCapitolo())
          .deCapitolo(accertamento.getDeCapitolo())
          .deAnnoEsercizio(accertamento.getDeAnnoEsercizio())
          .codAccertamento(accertamento.getCodAccertamento())
          .deAccertamento(accertamento.getDeAccertamento())
          .flgAttivo(true)
          .build();
      mygovAnagraficaUffCapAccId = anagraficaUffCapAccDao.insert(anagraficaUffCapAcc);
    } else {
      anagraficaUffCapAcc = anagraficaUffCapAccDao.getById(accertamento.getId());
      anagraficaUffCapAcc.setCodUfficio(accertamento.getCodUfficio());
      anagraficaUffCapAcc.setDeUfficio(accertamento.getDeUfficio());
      anagraficaUffCapAcc.setFlgAttivo(accertamento.isFlgAttivo());
      anagraficaUffCapAcc.setCodCapitolo(accertamento.getCodCapitolo());
      anagraficaUffCapAcc.setDeCapitolo(accertamento.getDeCapitolo());
      anagraficaUffCapAcc.setDeAnnoEsercizio(accertamento.getDeAnnoEsercizio());
      anagraficaUffCapAcc.setCodAccertamento(accertamento.getCodAccertamento());
      anagraficaUffCapAcc.setDeAccertamento(accertamento.getDeAccertamento());
      anagraficaUffCapAccDao.update(anagraficaUffCapAcc);
      mygovAnagraficaUffCapAccId = anagraficaUffCapAcc.getMygovAnagraficaUffCapAccId();
    }
    return this.mapToDto(anagraficaUffCapAccDao.getById(mygovAnagraficaUffCapAccId), accertamento.getCodTipoDovuto());
  }

  public List<AccertamentoTo> getAccertamenti(Long enteId, UserWithAdditionalInfo user, LocalDate from, LocalDate to, String codIuv, String codTipo, String codStato, String deNomeAccertamento) {
    Ente ente = enteService.getEnteById(enteId);
    String codFedUserId = user.getUsername();
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto) || (StringUtils.isNotBlank(codTipo) && !codTipiDovuto.contains(codTipo))) {
      log.warn("user[codFedUserId: " + codFedUserId + "] :: not authorized for TIPO_DOVUTO["+ codTipo +"].");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    codTipiDovuto = StringUtils.isNotBlank(codTipo) ? List.of(codTipo) : codTipiDovuto;
    final List<String> codTipiDovutoFinal = codTipiDovuto;
    boolean isAdminForEnte = user.getEntiRoles().getOrDefault(ente.getCodIpaEnte(), Set.of()).contains(Operatore.Role.ROLE_ADMIN.name());
    Long utenteId = isAdminForEnte ? null : utenteService.getByCodFedUserId(user.getUsername()).map(Utente::getMygovUtenteId).orElseThrow();
    final LocalDate finalTo = to!=null ? to.plusDays(1L) : to;

    return maxResultsHelper.manageMaxResults(
        maxResults -> accertamentoDao.search(enteId, utenteId, codTipiDovutoFinal, codStato, from, finalTo, codIuv, deNomeAccertamento, maxResults)
          .stream().map(this::mapToDto).collect(toList()),
        () -> accertamentoDao.searchCount(enteId, utenteId, codTipiDovutoFinal, codStato, from, finalTo, codIuv, deNomeAccertamento) );
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public AccertamentoTo updateStato(Long enteId, String codFedUserId, Long accertamentoId, String codStato) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto)) {
      log.warn("user[codFedUserId: " + codFedUserId + "] :: not authorized");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    Accertamento accertamento = accertamentoDao.getById(accertamentoId);
    if (!accertamento.getMygovAnagraficaStatoId().getCodStato().equals(Constants.COD_TIPO_STATO_ACCERTAMENTO_INSERITO)) {
      log.warn("SET " + codStato + " :: ACCERTAMENTO :: GET :: Fields[accertamentoID: " + accertamentoId + ", codFedUserId: " + codFedUserId + "] :: L'accertamento è in stato[" + accertamento.getMygovAnagraficaStatoId().getCodStato() + "] perciò non può essere chiuso.");
      throw new ValidatorException(
          messageSource.getMessage("mypivot.accertamenti.errore.notAuthorizedClosed", new Object[]{accertamento.getDeNomeAccertamento(), accertamento.getMygovAnagraficaStatoId().getCodStato()}, Locale.ITALY)
      );
    }
    List<AccertamentoDettaglio> dettagli = accertamentoDettaglioService.getByAccertamentoId(accertamentoId);
    if (codStato.equals(Constants.COD_TIPO_STATO_ACCERTAMENTO_CHIUSO) && CollectionUtils.isEmpty(dettagli)) {
      log.warn("SET " + codStato + " :: ACCERTAMENTO :: GET :: Fields[accertamentoID: " + accertamentoId + ", codFedUserId: " + codFedUserId + "] :: L'accertamento non presenta pagamenti associati e quindi non può essere chiuso.");
      throw new ValidatorException("L'accertamento non presenta pagamenti associati e quindi non può essere chiuso.");
    }
    Utente utente = utenteService.getByCodFedUserId(codFedUserId).orElseThrow(
        () -> new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunUtenteTrovato", null, Locale.ITALY))
    );
    AnagraficaStato stato = anagraficaStatoService.getByCodStatoAndTipoStato(codStato, Constants.DE_TIPO_STATO_ACCERTAMENTO);
    accertamento.setMygovUtenteId(utente);
    accertamento.setMygovAnagraficaStatoId(stato);
    accertamento.setDtUltimaModifica(new Date());
    accertamentoDao.update(accertamento);
    return this.mapToDto(accertamento);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public AccertamentoTo insert(Long enteId, String codFedUserId, AccertamentoTo accertamento) {
    EnteTipoDovuto enteTipoDovuto = enteTipoDovutoService.getByCodTipo(accertamento.getCodTipoDovuto(), enteId).orElseThrow(
        () -> new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY))
    );
    Utente utente = utenteService.getByCodFedUserId(codFedUserId).orElseThrow(
        () -> new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunUtenteTrovato", null, Locale.ITALY))
    );
    AnagraficaStato anagraficaStato = anagraficaStatoService.getByCodStatoAndTipoStato(
        Constants.COD_TIPO_STATO_ACCERTAMENTO_INSERITO, Constants.DE_TIPO_STATO_ACCERTAMENTO
    );
    Accertamento newAccertamento = Accertamento.builder()
        .mygovEnteTipoDovutoId(enteTipoDovuto)
        .mygovAnagraficaStatoId(anagraficaStato)
        .mygovUtenteId(utente)
        .deNomeAccertamento(accertamento.getDeNomeAccertamento()).build();
    long mygovAccertamentoId = accertamentoDao.insert(newAccertamento);
    newAccertamento.setMygovAccertamentoId(mygovAccertamentoId);
    return this.mapToDto(newAccertamento);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public AccertamentoTo update(Long enteId, String codFedUserId, AccertamentoTo accertamento) {
    EnteTipoDovuto enteTipoDovuto = enteTipoDovutoService.getByCodTipo(accertamento.getCodTipoDovuto(), enteId).orElseThrow(
        () -> new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY))
    );
    Utente utente = utenteService.getByCodFedUserId(codFedUserId).orElseThrow(
        () -> new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunUtenteTrovato", null, Locale.ITALY))
    );
    Accertamento accertamentoToUpdate = accertamentoDao.getById(accertamento.getId());
    accertamentoToUpdate.setMygovEnteTipoDovutoId(enteTipoDovuto);
    accertamentoToUpdate.setDeNomeAccertamento(accertamento.getDeNomeAccertamento());
    accertamentoToUpdate.setDtUltimaModifica(new Date());
    accertamentoDao.update(accertamentoToUpdate);
    return this.mapToDto(accertamentoToUpdate);
  }

  private AccertamentoCapitoloTo mapToDto(AnagraficaUffCapAcc anagrafica, String codTipo) {
    String deTipo = null;
    if ("n/a".equals(codTipo)) {
      deTipo = "N/A";
    } else if(StringUtils.isNotBlank(anagrafica.getCodTipoDovuto())) {
      deTipo = enteTipoDovutoService.getByCodTipo(anagrafica.getCodTipoDovuto(), anagrafica.getMygovEnteId()).get().getDeTipo();
    }
    return AccertamentoCapitoloTo.builder()
        .id(anagrafica.getMygovAnagraficaUffCapAccId())
        .codTipoDovuto(anagrafica.getCodTipoDovuto())
        .deTipoDovuto(deTipo)
        .codUfficio(anagrafica.getCodUfficio())
        .deUfficio(anagrafica.getDeUfficio())
        .deAnnoEsercizio(anagrafica.getDeAnnoEsercizio())
        .codCapitolo(anagrafica.getCodCapitolo())
        .deCapitolo(anagrafica.getDeCapitolo())
        .codAccertamento(anagrafica.getCodAccertamento())
        .deAccertamento(anagrafica.getDeAccertamento())
        .dtCreazione(anagrafica.getDtCreazione())
        .dtUltimaModifica(anagrafica.getDtUltimaModifica())
        .flgAttivo(anagrafica.isFlgAttivo())
        .build();
  }

  private AccertamentoTo mapToDto(Accertamento accertamento) {
    return AccertamentoTo.builder()
        .id(accertamento.getMygovAccertamentoId())
        .deNomeAccertamento(accertamento.getDeNomeAccertamento())
        .codTipoDovuto(accertamento.getMygovEnteTipoDovutoId().getCodTipo())
        .deTipoDovuto(accertamento.getMygovEnteTipoDovutoId().getDeTipo())
        .deStato(accertamento.getMygovAnagraficaStatoId().getCodStato())
        .creatore(accertamento.getMygovUtenteId().getDeFirstname() + ' ' + accertamento.getMygovUtenteId().getDeLastname())
        .dtUltimaModifica(accertamento.getDtUltimaModifica())
        .build();
  }
}
