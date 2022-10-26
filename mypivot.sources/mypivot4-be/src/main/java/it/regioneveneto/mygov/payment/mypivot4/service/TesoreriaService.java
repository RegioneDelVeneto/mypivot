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

import it.regioneveneto.mygov.payment.mypay4.exception.NotAuthorizedException;
import it.regioneveneto.mygov.payment.mypay4.exception.NotFoundException;
import it.regioneveneto.mygov.payment.mypay4.exception.ValidatorException;
import it.regioneveneto.mygov.payment.mypay4.util.MaxResultsHelper;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.dao.FlussoExportDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.FlussoRendicontazioneDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.FlussoTesoreriaDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.BilancioTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.TesoreriaTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.FlussoExport;
import it.regioneveneto.mygov.payment.mypivot4.model.FlussoRendicontazione;
import it.regioneveneto.mygov.payment.mypivot4.model.FlussoTesoreria;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class TesoreriaService {

  @Value("${pivot.identificativoIntermediarioPA.name}")
  private String defaultIntermediario;

  @Autowired
  private EnteService enteService;

  @Autowired
  private EnteTipoDovutoService enteTipoDovutoService;

  @Autowired
  private OperatoreEnteTipoDovutoService operatoreEnteTipoDovutoService;

  @Autowired
  private FlussoTesoreriaDao flussoTesoreriaDao;

  @Autowired
  private FlussoRendicontazioneDao flussoRendicontazioneDao;

  @Autowired
  private FlussoExportDao flussoExportDao;

  @Autowired
  private AccertamentoDettaglioService accertamentoDettaglioService;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private MaxResultsHelper maxResultsHelper;

  public List<TesoreriaTo> search(Long enteId, String codFedUserId, String iuv, String annoBolletta, String codBolletta, String idr, BigDecimal importo,
                               String annoDocumento, String codDocumento, String annoProvvisorio, String codProvvisorio, String ordinante,
                               LocalDate dtContabileFrom, LocalDate dtContabileTo, LocalDate dtValutaFrom, LocalDate dtValutaTo) {
    Ente ente = Optional.of(enteService.getEnteById(enteId)).orElseThrow(NotFoundException::new);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto)) {
      log.warn("Utente[codFedUserId: " + codFedUserId + "] :: NON è autorizzato.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    final LocalDate dtContabileToFinal = dtContabileTo != null ? dtContabileTo.plusDays(1L): null;
    final LocalDate dtValutaToFinal = dtValutaTo != null ? dtValutaTo.plusDays(1L): null;
    return maxResultsHelper.manageMaxResults(
        maxResults -> flussoTesoreriaDao.search(ente.getCodIpaEnte(), iuv, annoBolletta, codBolletta, idr, importo, annoDocumento, codDocumento, annoProvvisorio,
            codProvvisorio, ordinante, dtContabileFrom, dtContabileToFinal, dtValutaFrom, dtValutaToFinal, maxResults)
          .stream().map(this::mapToDto).collect(Collectors.toList()),
        () -> flussoTesoreriaDao.searchCount(ente.getCodIpaEnte(), iuv, annoBolletta, codBolletta, idr, importo, annoDocumento, codDocumento, annoProvvisorio,
            codProvvisorio, ordinante, dtContabileFrom, dtContabileToFinal, dtValutaFrom, dtValutaToFinal)
        );
  }

  public FlussoTesoreria getById(Long flussoTesoreriaId) {
    return flussoTesoreriaDao.getById(flussoTesoreriaId);
  }

  public TesoreriaTo getDtoById(Long enteId, String codFedUserId, Long flussoTesoreriaId) {
    Ente ente = Optional.of(enteService.getEnteById(enteId)).orElseThrow(NotFoundException::new);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto)) {
      log.warn("Utente[codFedUserId: " + codFedUserId + "] :: NON è autorizzato.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    FlussoTesoreria flussoTesoreria = flussoTesoreriaDao.getById(flussoTesoreriaId);
    if(flussoTesoreria!=null && !Objects.equals(flussoTesoreria.getMygovEnteId().getMygovEnteId(), enteId)) {
      log.warn("user tried to see flussoTesoreria: "+flussoTesoreriaId+" using ente: "+enteId);
      throw new NotAuthorizedException("User not authorized to see flussoTesoreria "+flussoTesoreriaId);
    }
    return flussoTesoreria != null ? this.mapToDto(flussoTesoreria) : null;
  }

  public List<BilancioTo> bilanci(Long enteId, String codFedUserId, String annoBolletta, String codBolletta) {
    Ente ente = Optional.of(enteService.getEnteById(enteId)).orElseThrow(NotFoundException::new);
    List<String> codTipiDovuto = operatoreEnteTipoDovutoService.getTipoByCodIpaCodFedUser(ente.getCodIpaEnte(), codFedUserId);
    if (CollectionUtils.isEmpty(codTipiDovuto)) {
      log.warn("Utente[codFedUserId: " + codFedUserId + "] :: NON è autorizzato.");
      throw new ValidatorException(messageSource.getMessage("mypivot.messages.error.nessunTipoDovutoAssegnato", null, Locale.ITALY));
    }
    FlussoTesoreria tesoreria = flussoTesoreriaDao.getByCodIpaDeAnnoBollettaCodBolletta(ente.getCodIpaEnte(), annoBolletta, codBolletta);

    List<FlussoExport> flussi;
    if (tesoreria!=null && StringUtils.isNotBlank(tesoreria.getCodIdUnivocoFlusso())) {
      List<FlussoRendicontazione> rendicontazioni = flussoRendicontazioneDao.getByCodIpaIUF(ente.getCodIpaEnte(), tesoreria.getCodIdUnivocoFlusso());
      flussi = rendicontazioni.stream().flatMap(rend -> {
        List<FlussoExport> exports = flussoExportDao.getByCodIpaIUVIdDtSinPag(ente.getCodIpaEnte(), rend.getCodDatiSingPagamIdentificativoUnivocoVersamento(), rend.getIndiceDatiSingoloPagamento());
        return !CollectionUtils.isEmpty(exports) ? Stream.of(exports.get(0)) : Stream.empty();
      }).collect(Collectors.toList());
    } else if (tesoreria!=null && StringUtils.isNotBlank(tesoreria.getCodIdUnivocoVersamento())) {
      flussi = flussoExportDao.getByCodIpaIUV(ente.getCodIpaEnte(), tesoreria.getCodIdUnivocoVersamento());
    } else {
      flussi = new ArrayList<>();
    }

    List<BilancioTo> listBilancioTo = accertamentoDettaglioService.getBilancios(ente.getCodIpaEnte(), flussi);
    for(BilancioTo bilancioTo : listBilancioTo){
      enteTipoDovutoService.getByCodTipo(bilancioTo.getCodTipoDovuto(), ente.getMygovEnteId())
          .ifPresent(etd -> {
            bilancioTo.setDeTipoDovuto(etd.getDeTipo());
            bilancioTo.setIntermediario(etd.isEsterno() ? "Altro intermediario" : defaultIntermediario);
          });
    }
    return listBilancioTo;
  }

  private TesoreriaTo mapToDto(FlussoTesoreria flusso) {
    return TesoreriaTo.builder()
        .id(flusso.getMygovFlussoTesoreriaId())
        .annoBolletta(flusso.getDeAnnoBolletta())
        .codBolletta(flusso.getCodBolletta())
        .dtValuta(Utilities.toLocalDate(flusso.getDtDataValutaRegione()))
        .dtContabile(Utilities.toLocalDate(flusso.getDtBolletta()))
        .idRendicontazione(flusso.getCodIdUnivocoFlusso())
        .importoTesoreria(flusso.getNumIpBolletta())
        .annoCodDocumento(flusso.getDeAnnoDocumento())
        .codDocumento(flusso.getCodDocumento())
        .annoCodProvvisorio(flusso.getDeAeProvvisorio())
        .codProvvisorio(flusso.getCodProvvisorio())
        .ordinante(flusso.getDeCognome())
        .causale(StringUtils.isNotBlank(flusso.getDeCausale()) ? flusso.getDeCausale() : flusso.getCodCausale())
        .codConto(flusso.getCodConto())
        .build();
  }
}
