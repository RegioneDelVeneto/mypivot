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

import it.regioneveneto.mygov.payment.mypay4.exception.NotFoundException;
import it.regioneveneto.mygov.payment.mypay4.exception.ValidatorException;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.dao.*;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoRicevutaTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.VmStatisticaCapitoloTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.VmStatisticaTo;
import it.regioneveneto.mygov.payment.mypivot4.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class StatisticaService {

  private enum AnnoMeseGiorno {
    ANNO, MESE, GIORNO
  }

  @Autowired
  private VmStatisticaEnteAnnoMeseGiornoDao vmStatisticaEnteAnnoMeseGiornoDao ;

  @Autowired
  private VmStatisticaEnteAnnoMeseGiornoUffTdDao vmStatisticaEnteAnnoMeseGiornoUffTdDao;

  @Autowired
  private VmStatisticaEnteAnnoMeseGiornoUffTdCapDao vmStatisticaEnteAnnoMeseGiornoUffTdCapDao;

  @Autowired
  private VmStatisticaEnteAnnoMeseGiornoUffTdCapAccDao vmStatisticaEnteAnnoMeseGiornoUffTdCapAccDao;

  @Autowired
  private FlussoExportDao flussoExportDao;

  @Autowired
  private AnagraficaUffCapAccDao anagraficaUffCapAccDao;

  @Autowired
  private EnteService enteService;

  @Autowired
  private EnteTipoDovutoService enteTipoDovutoService;

  @Autowired
  private OperatoreEnteTipoDovutoService operatoreEnteTipoDovutoService;

  @Autowired
  private MessageSource messageSource;

  public List<AnagraficaUffCapAcc> getAnagraficaByEnteTipo(Long enteId, String codFedUserId, String codTipo) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("RICERCA :: STATISTICA :: RIPARTITI per UFFICI :: Utente[codFedUserId: " + codFedUserId + "] :: NON risulta OPERATORE per nessun tipo dovuto.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    return anagraficaUffCapAccDao.findDistinctUfficiByFilter(enteId, null, List.of(codTipo));
  }

  public List<AnagraficaUffCapAcc> getAnagraficaByEnte(Long enteId, String codFedUserId) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto)) {
      log.warn("RICERCA :: STATISTICA :: RIPARTITI per UFFICI :: Utente[codFedUserId: " + codFedUserId + "] :: NON risulta OPERATORE per nessun tipo dovuto.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    return anagraficaUffCapAccDao.findDistinctUfficiByFilter(enteId, null, codTipiDovuto);
  }

  public List<AnagraficaUffCapAcc> getAnagraficaByEnteTipoUfficio(Long enteId, String codFedUserId, String codTipo, String codUfficio) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("RICERCA :: STATISTICA :: RIPARTITI per UFFICI :: Utente[codFedUserId: " + codFedUserId + "] :: NON risulta OPERATORE per nessun tipo dovuto.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    return anagraficaUffCapAccDao.findDistinctCapitoliByEnteDovutoUfficio(enteId, codTipo, codUfficio, null, null);
  }

  public List<VmStatisticaTo> getTotaliRipartitiPerUffici(Long enteId, Integer year, Integer month, Integer day, String codFedUserId) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);

    if (CollectionUtils.isEmpty(codTipiDovuto)) {
      log.warn("RICERCA :: STATISTICA :: RIPARTITI per UFFICI :: Utente[codFedUserId: " + codFedUserId + "] :: NON risulta OPERATORE per nessun tipo dovuto.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    if (year == null)
      year = Calendar.getInstance().get(Calendar.YEAR);

    List<VmStatisticaEnteAnnoMeseGiornoUffTd> vmStatisticaDos;
    if (month == null) {
      vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdDao.getTotaliRipartitiPerUfficiByAnno(enteId, year, codTipiDovuto);
    } else if (day == null) {
      vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdDao.getTotaliRipartitiPerUfficiByAnnoMese(enteId, year, month, codTipiDovuto);
    } else {
      vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdDao.getTotaliRipartitiPerUfficiByAnnoMeseGiorno(enteId, year, month, day, codTipiDovuto);
    }
    return vmStatisticaDos.stream().map(d -> this.mapToDto(d, false)).collect(Collectors.toList());
  }

  public List<VmStatisticaTo> getTotaliRipartitiPerTipiDovuto(Long enteId, Integer year, Integer month, Integer day, String codFedUserId) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);

    if (CollectionUtils.isEmpty(codTipiDovuto)) {
      log.warn("RICERCA :: STATISTICA :: RIPARTITI per TIPI DOVUTO :: Utente[codFedUserId: " + codFedUserId + "] :: NON risulta OPERATORE per nessun tipo dovuto.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    if (year == null)
      year = Calendar.getInstance().get(Calendar.YEAR);

    List<VmStatisticaEnteAnnoMeseGiornoUffTd> vmStatisticaDos;
    if (month == null) {
      vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdDao.getTotaliRipartitiPerTipiDovutoByAnno(enteId, year, codTipiDovuto);
    } else if (day == null) {
      vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdDao.getTotaliRipartitiPerTipiDovutoByAnnoMese(enteId, year, month, codTipiDovuto);
    } else {
      vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdDao.getTotaliRipartitiPerTipiDovutoByAnnoMeseGiorno(enteId, year, month, day, codTipiDovuto);
    }
    return vmStatisticaDos.stream().map(d -> this.mapToDto(d, true)).collect(Collectors.toList());
  }

  //TODO Activate buttons for Dettalio Cruscotto.
  public List<VmStatisticaCapitoloTo> getTotaliRipartitiPerCapitoli(Long enteId, Integer year, Integer month, Integer day, String codTipo, String codUfficio, String codFedUserId) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);

    if (CollectionUtils.isEmpty(codTipiDovuto)) {
      log.warn("RICERCA :: STATISTICA :: RIPARTITI per CAPITOLI :: Utente[codFedUserId: " + codFedUserId + "] :: NON risulta OPERATORE per nessun capitolo.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    if (year == null)
      year = Calendar.getInstance().get(Calendar.YEAR);

    List<VmStatisticaEnteAnnoMeseGiornoUffTdCap> vmStatisticaDos;
    if (StringUtils.isNotBlank(codTipo) && StringUtils.isNotBlank(codUfficio)) {
      if (month == null) {
        vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapDao.getTotaliRipartitiPerCapitoliByAnnoUfficioDovuto(enteId, year, codUfficio, codTipo);
      } else if (day == null) {
        vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapDao.getTotaliRipartitiPerCapitoliByAnnoMeseUfficioDovuto(enteId, year, month, codUfficio, codTipo);
      } else {
        vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapDao.getTotaliRipartitiPerCapitoliByAnnoMeseGiornoUfficioDovuto(enteId, year, month, day, codUfficio, codTipo);
      }
    } else if (StringUtils.isNotBlank(codTipo) /*&& StringUtils.isBlank(codUfficio)*/) {
      if (month == null) {
        vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapDao.getTotaliRipartitiPerCapitoliByAnnoDovuto(enteId, year, codTipo);
      } else if (day == null) {
        vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapDao.getTotaliRipartitiPerCapitoliByAnnoMeseDovuto(enteId, year, month, codTipo);
      } else {
        vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapDao.getTotaliRipartitiPerCapitoliByAnnoMeseGiornoDovuto(enteId, year, month, day, codTipo);
      }
    } else if (/*StringUtils.isBlank(codTipo) &&*/ StringUtils.isNotBlank(codUfficio)) {
      if (month == null) {
        vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapDao.getTotaliRipartitiPerCapitoliByAnnoUfficio(enteId, year, codUfficio);
      } else if (day == null) {
        vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapDao.getTotaliRipartitiPerCapitoliByAnnoMeseUfficio(enteId, year, month, codUfficio);
      } else {
        vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapDao.getTotaliRipartitiPerCapitoliByAnnoMeseGiornoUfficio(enteId, year, month, day, codUfficio);
      }
    } else {
      throw new ValidatorException("Codice TipoDovuto e Codice Ufficio entrambi sono vuoti.");
    }
    return vmStatisticaDos.stream().map(this::mapToDto).collect(Collectors.toList());
  }

  //TODO Activate buttons for Dettalio Cruscotto.
  public List<VmStatisticaTo> getTotaliRipartitiPerAccertamenti(Long enteId, Integer year, Integer month, Integer day, String codTipo, String codUfficio, String codCapitolo, String codFedUserId) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);

    if (CollectionUtils.isEmpty(codTipiDovuto)) {
      log.warn("RICERCA :: STATISTICA :: RIPARTITI per ACCERTAMENTI :: Utente[codFedUserId: " + codFedUserId + "] :: NON risulta OPERATORE per nessun accertamento.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    if (year == null)
      year = Calendar.getInstance().get(Calendar.YEAR);

    List<VmStatisticaEnteAnnoMeseGiornoUffTdCapAcc> vmStatisticaDos;
    if (month == null) {
      vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapAccDao.getTotaliRipartitiPerAccertamentiByAnno(enteId, year, codTipo, codUfficio, codCapitolo);
    } else if (day == null) {
      vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapAccDao.getTotaliRipartitiPerAccertamentiByAnnoMese(enteId, year, month, codTipo, codUfficio, codCapitolo);
    } else {
      vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoUffTdCapAccDao.getTotaliRipartitiPerAccertamentiByAnnoMeseGiorno(enteId, year, month, day, codTipo, codUfficio, codCapitolo);
    }
    return vmStatisticaDos.stream().map(this::mapToDto).collect(Collectors.toList());
  }

  public List<VmStatisticaTo> getTotaliRipartitiPerAnno(Long mygovEnteId, List<Integer> anni) {
    List<VmStatisticaEnteAnnoMeseGiorno> vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoDao.getTotaliPerAnno(mygovEnteId, anni);
    return vmStatisticaDos.stream().map(d -> this.mapToDto(d, AnnoMeseGiorno.ANNO)).collect(Collectors.toList());
  }

  public List<VmStatisticaTo> getTotaliRipartitiPerMese(Long mygovEnteId, Integer anno, List<Integer> mesi) {
    List<VmStatisticaEnteAnnoMeseGiorno> vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoDao.getTotaliPerAnnoMese(mygovEnteId, anno, mesi);
    return vmStatisticaDos.stream().map(d -> this.mapToDto(d, AnnoMeseGiorno.MESE)).collect(Collectors.toList());
  }

  public List<VmStatisticaTo> getTotaliRipartitiPerGiorno(Long mygovEnteId, LocalDate from, LocalDate to) {
    List<VmStatisticaEnteAnnoMeseGiorno> vmStatisticaDos = vmStatisticaEnteAnnoMeseGiornoDao.getTotaliPerAnnoMeseGiorno(mygovEnteId, from, to);
    return vmStatisticaDos.stream().map(d -> this.mapToDto(d, AnnoMeseGiorno.GIORNO)).collect(Collectors.toList());
  }

  private VmStatisticaTo mapToDto(VmStatisticaEnteAnnoMeseGiornoUffTd vmStatisticaDo, boolean flgTipoDovuto) {
    VmStatisticaTo vmStatisticaTo = new VmStatisticaTo();
    vmStatisticaTo.setImportoPagato(vmStatisticaDo.getImpPag());
    vmStatisticaTo.setImportoRendicontato(vmStatisticaDo.getImpRend());
    vmStatisticaTo.setImportoIncassato(vmStatisticaDo.getImpInc());
    if (!flgTipoDovuto) {
      vmStatisticaTo.setCodice(vmStatisticaDo.getCodUff());
      vmStatisticaTo.setDesc(StringUtils.isNotBlank(vmStatisticaDo.getDeUff()) ? vmStatisticaDo.getDeUff() : vmStatisticaDo.getCodUff());
    } else {
      vmStatisticaTo.setCodice(vmStatisticaDo.getCodTd());
      vmStatisticaTo.setDesc(StringUtils.isNotBlank(vmStatisticaDo.getDeTd()) ? vmStatisticaDo.getDeTd() : vmStatisticaDo.getCodTd());
    }
    return vmStatisticaTo;
  }

  private VmStatisticaCapitoloTo mapToDto(VmStatisticaEnteAnnoMeseGiornoUffTdCap vmStatisticaDo) {
    VmStatisticaCapitoloTo vmStatisticaTo = new VmStatisticaCapitoloTo();
    vmStatisticaTo.setImportoPagato(vmStatisticaDo.getImpPag());
    vmStatisticaTo.setImportoRendicontato(vmStatisticaDo.getImpRend());
    vmStatisticaTo.setImportoIncassato(vmStatisticaDo.getImpInc());
    vmStatisticaTo.setCodTipoDovuto(vmStatisticaDo.getCodTd());
    vmStatisticaTo.setDeTipoDovuto(StringUtils.isNotBlank(vmStatisticaDo.getDeTd()) ? vmStatisticaDo.getDeTd() : vmStatisticaDo.getCodTd());
    vmStatisticaTo.setCodUfficio(vmStatisticaDo.getCodUff());
    vmStatisticaTo.setDeUfficio(StringUtils.isNotBlank(vmStatisticaDo.getDeUff()) ? vmStatisticaDo.getDeUff() : vmStatisticaDo.getCodUff());
    vmStatisticaTo.setCodCapitolo(vmStatisticaDo.getCodCap());
    vmStatisticaTo.setDeCapitolo(StringUtils.isNotBlank(vmStatisticaDo.getDeCap()) ? vmStatisticaDo.getDeCap() : vmStatisticaDo.getCodCap());
    return vmStatisticaTo;
  }

  private VmStatisticaTo mapToDto(VmStatisticaEnteAnnoMeseGiornoUffTdCapAcc vmStatisticaDo) {
    VmStatisticaTo vmStatisticaTo = new VmStatisticaTo();
    vmStatisticaTo.setImportoPagato(vmStatisticaDo.getImpPag());
    vmStatisticaTo.setImportoRendicontato(vmStatisticaDo.getImpRend());
    vmStatisticaTo.setImportoIncassato(vmStatisticaDo.getImpInc());
    vmStatisticaTo.setCodice(vmStatisticaDo.getCodAcc());
    vmStatisticaTo.setDesc(StringUtils.isNotBlank(vmStatisticaDo.getDeAcc()) ? vmStatisticaDo.getDeAcc().replaceAll("\"","&#34;") : vmStatisticaDo.getCodAcc());
    return vmStatisticaTo;
  }

  private VmStatisticaTo mapToDto(VmStatisticaEnteAnnoMeseGiorno vmStatisticaDo, AnnoMeseGiorno mode) {
    VmStatisticaTo vmStatisticaTo = new VmStatisticaTo();
    vmStatisticaTo.setImportoPagato(vmStatisticaDo.getImpPag());
    vmStatisticaTo.setImportoRendicontato(vmStatisticaDo.getImpRend());
    vmStatisticaTo.setImportoIncassato(vmStatisticaDo.getImpInc());
    vmStatisticaTo.setNumPagamenti(vmStatisticaDo.getNumPag());
    switch(mode) {
      case ANNO:
        vmStatisticaTo.setDesc(String.format("%04d", vmStatisticaDo.getAnno())); break;
      case MESE:
        vmStatisticaTo.setDesc(String.format("%02d/%04d", vmStatisticaDo.getMese(), vmStatisticaDo.getAnno())); break;
      case GIORNO:
        vmStatisticaTo.setDesc(String.format("%02d/%02d/%04d", vmStatisticaDo.getGiorno(), vmStatisticaDo.getMese(), vmStatisticaDo.getAnno())); break;
    }
    return vmStatisticaTo;
  }

  public List<FlussoRicevutaTo> getRicevuteTelematiche(Long enteId, String codFedUserId, String codTipo, String codUfficio, String codCapitolo, LocalDate from, LocalDate to,
                                                       String iuv, String iur, String attestante, String cfPagatore, String anagPagatore, String cfVersante, String anagVersante) {
    Ente ente = enteService.getEnteById(enteId);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto) || !codTipiDovuto.contains(codTipo)) {
      log.warn("Utente[codFedUserId: " + codFedUserId + "] :: NON è autorizzato per TIPO DOVUTO["+ codTipo +"].");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }

    //List<String> codIud = flussoExportDao.get_dettaglio_pagamenti_cruscotto(null,null, null, codUfficio, codTipo, codCapitolo, enteId, null);
    to = to != null ? to.plusDays(1) : null;
    List<FlussoExport> flussiExport = flussoExportDao.getDettaglioCruscotto(enteId, codTipo, from, to, iuv, iur, attestante, cfPagatore, anagPagatore, cfVersante, anagVersante,
      null, null, null, codUfficio, codCapitolo, null);
    return flussiExport.stream().map(this::mapToDto).collect(Collectors.toList());
  }

  public FlussoRicevutaTo mapToDto(FlussoExport flussoExport) {
    return FlussoRicevutaTo.builder()
        .codiceIpaEnte(flussoExport.getMygovEnteId().getCodIpaEnte())
        .codIud(flussoExport.getCodIud())
        .codRpSilinviarpIdUnivocoVersamento(flussoExport.getCodRpSilinviarpIdUnivocoVersamento())
        .codEDatiPagDatiSingPagIdUnivocoRiscoss(flussoExport.getCodEDatiPagDatiSingPagIdUnivocoRiscoss())
        //.numEDatiPagImportoTotalePagato(flussoExport.getNumEDatiPagImportoTotalePagato())
        .numEDatiPagDatiSingPagSingoloImportoPagato(flussoExport.getNumEDatiPagDatiSingPagSingoloImportoPagato())
        .dtEDatiPagDatiSingPagDataEsitoSingoloPagamento(Utilities.toLocalDate(flussoExport.getDtEDatiPagDatiSingPagDataEsitoSingoloPagamento()))
        .deEIstitAttDenominazioneAttestante(flussoExport.getDeEIstitAttDenominazioneAttestante())
        .codESoggPagAnagraficaPagatore(flussoExport.getCodESoggPagAnagraficaPagatore())
        .codESoggPagIdUnivPagCodiceIdUnivoco(flussoExport.getCodESoggPagIdUnivPagCodiceIdUnivoco())
        .codESoggPagIdUnivPagTipoIdUnivoco(
            flussoExport.getCodESoggPagIdUnivPagTipoIdUnivoco() != null ? flussoExport.getCodESoggPagIdUnivPagTipoIdUnivoco().toString() : null
        )
        .deEDatiPagDatiSingPagCausaleVersamento(flussoExport.getDeEDatiPagDatiSingPagCausaleVersamento())
        .codESoggVersAnagraficaVersante(flussoExport.getCodESoggVersAnagraficaVersante())
        .codESoggVersIdUnivVersCodiceIdUnivoco(flussoExport.getCodESoggVersIdUnivVersCodiceIdUnivoco())
        .codESoggVersIdUnivVersTipoIdUnivoco(
            flussoExport.getCodESoggVersIdUnivVersTipoIdUnivoco() != null ? flussoExport.getCodESoggVersIdUnivVersTipoIdUnivoco().toString() : null
        )
        .deTipoDovuto(
            enteTipoDovutoService.getByCodTipo(flussoExport.getCodTipoDovuto(), flussoExport.getMygovEnteId().getCodIpaEnte())
                .orElseThrow(NotFoundException::new).getDeTipo() )
        .build();
  }
}
