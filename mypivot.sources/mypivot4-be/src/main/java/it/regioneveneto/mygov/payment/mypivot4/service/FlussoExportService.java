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

import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypay4.util.MaxResultsHelper;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.dao.FlussoExportDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.*;
import it.regioneveneto.mygov.payment.mypivot4.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class FlussoExportService {

  private final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

  @Value("${mypivot.customTransferIdByPsp:}")
  private String customTransferIdByPspParam;
  private Map<String,Integer> _customTransferIdByPsp;

  @Resource
  private FlussoExportService self;
  @Autowired
  private FlussoExportDao flussoExportDao;
  @Autowired
  private MaxResultsHelper maxResultsHelper;
  @Autowired
  private EnteService enteService;
  @Autowired
  private EnteTipoDovutoService enteTipoDovutoService;
  @Autowired
  private AnagraficaStatoService anagraficaStatoService;
  @Autowired
  private UtenteService utenteService;
  @Autowired
  private ManageFlussoService manageFlussoService;

  private AnagraficaStato anagraficaStatoManageFileCaricato;

  @Value("${ricevute-multibeneficiario.ente-secondario.cod_tipo_dovuto}")
  private String mbCodTipoDovuto;

  @Value("${ricevute-multibeneficiario.ente-secondario.de_nome_flusso}")
  private String mbDeNomeFlusso;

  @PostConstruct
  private void initialize(){
    anagraficaStatoManageFileCaricato = anagraficaStatoService.getByCodStatoAndTipoStato(Constants.COD_TIPO_STATO_MANAGE_FILE_CARICATO, Constants.DE_TIPO_STATO_MANAGE);
  }

  private Optional<Integer> getCustomTransferIdByPsp(String idPsp){
    if(_customTransferIdByPsp ==null){
      _customTransferIdByPsp = new HashMap<>();
      if(StringUtils.isNotBlank(customTransferIdByPspParam)){
        for(String s:StringUtils.split(customTransferIdByPspParam, ","))
          try{
            String[] t = StringUtils.split(s, ":", 2);
            if(t.length==1)
              _customTransferIdByPsp.put(t[0], 1);
            else
              _customTransferIdByPsp.put(t[0], Integer.parseInt(t[1]));
          }catch(Exception e){
            log.warn("error parsing customTransferIdByPsp [{}]: ignoring it", s);
          }
        log.info("parsed customTransferIdByPsp: {}", _customTransferIdByPsp);
      }
    }

    return Optional.ofNullable(idPsp).map(_customTransferIdByPsp::get);
  }

  public List<FlussoRicevutaTo> searchRicevuteTelematiche(Long mygovEnteId, String username, RicevutaSearchTo searchTo) {
    return maxResultsHelper.manageMaxResults(
      maxResults -> flussoExportDao
        .searchRicevuteTelematiche(mygovEnteId, username, searchTo, maxResults)
        .stream()
        .map(this::mapToPayload)
        .collect(toList()),
      () -> flussoExportDao.searchCountRt(mygovEnteId, username, searchTo));
  }

  public List<FlussoExport> findPagatiByKeySet(Set<FlussoExportKeysTo> keySet, RicevutaSearchTo searchTo) {
    return flussoExportDao.findPagatiByKeySet(keySet, searchTo);
  }

  public FlussoExportKeysTo mapToKeySet(FlussoRendicontazione rendicontazione) {
    return Optional.ofNullable(rendicontazione)
      .map(r -> FlussoExportKeysTo.builder()
        .mygov_ente_id(r.getMygovEnteId().getMygovEnteId())
        .cod_rp_silinviarp_id_univoco_versamento(r.getCodDatiSingPagamIdentificativoUnivocoVersamento())
        .cod_e_dati_pag_dati_sing_pag_id_univoco_riscoss(r.getCodDatiSingPagamIdentificativoUnivocoRiscossione())
        .indice_dati_singolo_pagamento(r.getIndiceDatiSingoloPagamento())
        .build()
      ).orElseGet(FlussoExportKeysTo::new);
  }

  public FlussoRicevutaTo mapToPayload(FlussoExport entity) {
    return Optional.ofNullable(entity)
      .map(f -> FlussoRicevutaTo.builder()
        .codiceIpaEnte(entity.getMygovEnteId().getCodIpaEnte())
        .codIud(entity.getCodIud())
        .codRpSilinviarpIdUnivocoVersamento(entity.getCodRpSilinviarpIdUnivocoVersamento())
        .codEDatiPagDatiSingPagIdUnivocoRiscoss(entity.getCodEDatiPagDatiSingPagIdUnivocoRiscoss())
        .numEDatiPagDatiSingPagSingoloImportoPagato(entity.getNumEDatiPagDatiSingPagSingoloImportoPagato())
        .dtEDatiPagDatiSingPagDataEsitoSingoloPagamento(Utilities.toLocalDate(entity.getDtEDatiPagDatiSingPagDataEsitoSingoloPagamento()))
        .deEIstitAttDenominazioneAttestante(entity.getDeEIstitAttDenominazioneAttestante())
        .codESoggPagAnagraficaPagatore(entity.getCodESoggPagAnagraficaPagatore())
        .codESoggPagIdUnivPagCodiceIdUnivoco(entity.getCodESoggPagIdUnivPagCodiceIdUnivoco())
        .codESoggPagIdUnivPagTipoIdUnivoco(Optional.ofNullable(entity.getCodESoggPagIdUnivPagTipoIdUnivoco())
          .map(Object::toString).orElse(null))
        .deEDatiPagDatiSingPagCausaleVersamento(entity.getDeEDatiPagDatiSingPagCausaleVersamento())
        .codESoggVersAnagraficaVersante(entity.getCodESoggVersAnagraficaVersante())
        .codESoggVersIdUnivVersCodiceIdUnivoco(entity.getCodESoggVersIdUnivVersCodiceIdUnivoco())
        .codESoggVersIdUnivVersTipoIdUnivoco(Optional.ofNullable(entity.getCodESoggVersIdUnivVersTipoIdUnivoco())
          .map(Object::toString).orElse(null))
        .deTipoDovuto(enteTipoDovutoService.getByCodTipo(entity.getCodTipoDovuto(), entity.getMygovEnteId().getCodIpaEnte())
          .map(EnteTipoDovuto::getDeTipo).orElse(null))
        .indiceDatiSingoloPagamento(entity.getIndiceDatiSingoloPagamento())
        .codFiscalePa1(entity.getCodFiscalePa1())
        .build()
      ).orElseGet(FlussoRicevutaTo::new);
  }

  public FlussoRicevutaTo mapToPayload(FlussoRendicontazione rend) {
    return Optional.ofNullable(rend).map(rendicontazione ->
      FlussoRicevutaTo.builder()
        .codiceIpaEnte(rendicontazione.getMygovEnteId().getCodIpaEnte())
        .codRpSilinviarpIdUnivocoVersamento(rendicontazione.getCodDatiSingPagamIdentificativoUnivocoVersamento())
        .codEDatiPagDatiSingPagIdUnivocoRiscoss(rendicontazione.getCodDatiSingPagamIdentificativoUnivocoRiscossione())
        .numEDatiPagDatiSingPagSingoloImportoPagato(rendicontazione.getNumDatiSingPagamSingoloImportoPagato())
        .dtEDatiPagDatiSingPagDataEsitoSingoloPagamento(Utilities.toLocalDate(rendicontazione.getDtDatiSingPagamDataEsitoSingoloPagamento()))
        .indiceDatiSingoloPagamento(rendicontazione.getIndiceDatiSingoloPagamento())
        .build()
    ).orElseGet(FlussoRicevutaTo::new);
  }

  public RicevutaSearchTo stripToNull(Optional<RicevutaSearchTo> optional) {
    return optional.map(obj -> RicevutaSearchTo.builder()
      .dateEsitoFrom(obj.getDateEsitoFrom())
      .dateEsitoTo(obj.getDateEsitoTo())
      .iud(StringUtils.stripToNull(obj.getIud()))
      .iuv(StringUtils.stripToNull(obj.getIuv()))
      .iur(StringUtils.stripToNull(obj.getIur()))
      .codFiscalePagatore(StringUtils.stripToNull(obj.getCodFiscalePagatore()))
      .anagPagatore(StringUtils.stripToNull(obj.getAnagPagatore()))
      .codFiscaleVersante(StringUtils.stripToNull(obj.getCodFiscaleVersante()))
      .anagVersante(StringUtils.stripToNull(obj.getAnagVersante()))
      .attestante(StringUtils.stripToNull(obj.getAttestante()))
      .tipoDovuto(StringUtils.stripToNull(obj.getTipoDovuto()))
      .build()
    ).orElseGet(RicevutaSearchTo::new);
  }

  public static Predicate<FlussoExport> filterByTipoDovutoOperatore(List<String> codTipoDovutoList) {
    return f -> Predicate.not(Objects::nonNull)
      .or(codTipoDovutoList::contains)
      .test(f.getCodTipoDovuto());
  }

  public static Predicate<FlussoRicevutaTo> filterByDeTipoDovutoOperatore(List<String> deTipoDovutoList) {
    return f -> Predicate.not(Objects::nonNull)
      .or(deTipoDovutoList::contains)
      .test(f.getDeTipoDovuto());
  }

  public Predicate<FlussoRicevutaTo> testEqualsKeys(FlussoRicevutaTo other) {
    return item -> item.getCodiceIpaEnte().equals(other.getCodiceIpaEnte()) &&
      item.getCodRpSilinviarpIdUnivocoVersamento().equals(other.getCodRpSilinviarpIdUnivocoVersamento()) &&
      item.getCodEDatiPagDatiSingPagIdUnivocoRiscoss().equals(other.getCodEDatiPagDatiSingPagIdUnivocoRiscoss()) &&
      item.getIndiceDatiSingoloPagamento() == other.getIndiceDatiSingoloPagamento();
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public int upsertFlussoExport(ReceiptExportTo receiptExportTo, ReceiptTransferExportTo transfer, Ente pa2, Long manageFlussoId) {
    FlussoExport fe = mapToFlussoExport(receiptExportTo, transfer, pa2, manageFlussoId);
    return flussoExportDao.upsert(fe);
  }

  private FlussoExport mapToFlussoExport(ReceiptExportTo receiptExportTo, ReceiptTransferExportTo transfer, Ente pa2, Long manageFlussoId) {
    Date now = new Date();
    Date dtExport;
    Date dtPayment;

    try {
      dtPayment = StringUtils.isNotBlank(receiptExportTo.getPaymentDateTime()) ? sdf.get().parse(receiptExportTo.getPaymentDateTime()) : null;
      dtExport = now; //StringUtils.isNotBlank(receiptExportTo.getTransferDate()) ? sdf.parse(receiptExportTo.getTransferDate()) : null;
    } catch (ParseException e) {
      throw new MyPayException("error parsing dtPayment", e);
    }

    //apply customTransferIdByPsp only if there is 1 secondary transfer in the receipt
    int indiceDatiSingoloPagamento = transfer.getIdTransfer();
    if(receiptExportTo.getReceiptTransferExportToList().size()==2)
      indiceDatiSingoloPagamento = getCustomTransferIdByPsp(receiptExportTo.getIdPSP()).orElse(indiceDatiSingoloPagamento);

    // verificare se esiste il tipo dovuto se non presente crearlo su mypivot
    //alla prima ricezione di pagamento ente secondario creerò il tipo dovuto
    return FlussoExport.builder()
      .version(0)
      .dtCreazione(now)
      .dtUltimaModifica(now)
      .mygovEnteId(pa2)
      .mygovManageFlussoId(ManageFlusso.builder().mygovManageFlussoId(manageFlussoId).build())
      .deNomeFlusso(mbDeNomeFlusso)
      .numRigaFlusso(0)
      .codIud(null)
      .codRpSilinviarpIdUnivocoVersamento(receiptExportTo.getCreditorReferenceId())
      .deEVersioneOggetto("6.2.0")
      .codEDomIdDominio(transfer.getFiscalCodePA())
      .codEDomIdStazioneRichiedente(null)
      .codEIdMessaggioRicevuta(receiptExportTo.getReceiptId())
      .dtEDataOraMessaggioRicevuta(dtPayment)
      .codERiferimentoMessaggioRichiesta(receiptExportTo.getReceiptId())
      .dtERiferimentoDataRichiesta(dtPayment)
      .codEIstitAttIdUnivAttTipoIdUnivoco('G')
      .codEIstitAttIdUnivAttCodiceIdUnivoco(receiptExportTo.getIdPSP())
      .deEIstitAttDenominazioneAttestante(receiptExportTo.getPspCompanyName())
      .codEIstitAttCodiceUnitOperAttestante(null)
      .deEIstitAttDenomUnitOperAttestante(null)
      .deEIstitAttIndirizzoAttestante(null)
      .deEIstitAttCivicoAttestante(null)
      .codEIstitAttCapAttestante(null)
      .deEIstitAttLocalitaAttestante(null)
      .deEIstitAttProvinciaAttestante(null)
      .codEIstitAttNazioneAttestante(null)
      .codEEnteBenefIdUnivBenefTipoIdUnivoco('G')
      .codEEnteBenefIdUnivBenefCodiceIdUnivoco(transfer.getFiscalCodePA())
      .deEEnteBenefDenominazioneBeneficiario(pa2.getDeNomeEnte())
      .codEEnteBenefCodiceUnitOperBeneficiario(null)
      .deEEnteBenefDenomUnitOperBeneficiario(null)
      .deEEnteBenefIndirizzoBeneficiario(transfer.getDeRpEnteBenefIndirizzoBeneficiario())
      .deEEnteBenefCivicoBeneficiario(transfer.getDeRpEnteBenefCivicoBeneficiario())
      .codEEnteBenefCapBeneficiario(transfer.getCodRpEnteBenefCapBeneficiario())
      .deEEnteBenefLocalitaBeneficiario(transfer.getDeRpEnteBenefLocalitaBeneficiario())
      .deEEnteBenefProvinciaBeneficiario(transfer.getDeRpEnteBenefProvinciaBeneficiario())
      .codEEnteBenefNazioneBeneficiario("IT")
      .codESoggVersIdUnivVersTipoIdUnivoco(Optional.ofNullable(receiptExportTo.getUniqueIdentifierTypePayer()).map(x -> x.charAt(0)).orElse(null))
      .codESoggVersIdUnivVersCodiceIdUnivoco(receiptExportTo.getUniqueIdentifierValuePayer())
      .codESoggVersAnagraficaVersante(receiptExportTo.getFullNamePayer())
      .deESoggVersIndirizzoVersante(receiptExportTo.getStreetNamePayer())
      .deESoggVersCivicoVersante(receiptExportTo.getCivicNumberPayer())
      .codESoggVersCapVersante(receiptExportTo.getPostalCodePayer())
      .deESoggVersLocalitaVersante(receiptExportTo.getCityPayer())
      .deESoggVersProvinciaVersante(receiptExportTo.getStateProvinceRegionPayer())
      .codESoggVersNazioneVersante(receiptExportTo.getCountryPayer())
      .deESoggVersEmailVersante(receiptExportTo.getEMailPayer())
      .codESoggPagIdUnivPagTipoIdUnivoco(Optional.ofNullable(receiptExportTo.getUniqueIdentifierTypeDebtor()).map(x -> x.charAt(0)).orElse(null))
      .codESoggPagIdUnivPagCodiceIdUnivoco(receiptExportTo.getUniqueIdentifierValueDebtor())
      .codESoggPagAnagraficaPagatore(receiptExportTo.getFullNameDebtor())
      .deESoggPagIndirizzoPagatore(receiptExportTo.getStreetNameDebtor())
      .deESoggPagCivicoPagatore(receiptExportTo.getCivicNumberDebtor())
      .codESoggPagCapPagatore(receiptExportTo.getPostalCodeDebtor())
      .deESoggPagLocalitaPagatore(receiptExportTo.getCityDebtor())
      .deESoggPagProvinciaPagatore(receiptExportTo.getStateProvinceRegionDebtor())
      .codESoggPagNazionePagatore(receiptExportTo.getCountryDebtor())
      .deESoggPagEmailPagatore(receiptExportTo.getEMailDebtor())
      .codEDatiPagCodiceEsitoPagamento('0')
      .numEDatiPagImportoTotalePagato(transfer.getTransferAmount())
      .codEDatiPagIdUnivocoVersamento(receiptExportTo.getCreditorReferenceId())
      .codEDatiPagCodiceContestoPagamento(receiptExportTo.getReceiptId())
      .numEDatiPagDatiSingPagSingoloImportoPagato(transfer.getTransferAmount())
      .deEDatiPagDatiSingPagEsitoSingoloPagamento(receiptExportTo.getOutcome())
      .dtEDatiPagDatiSingPagDataEsitoSingoloPagamento(dtPayment)
      .codEDatiPagDatiSingPagIdUnivocoRiscoss(receiptExportTo.getReceiptId())
      .deEDatiPagDatiSingPagCausaleVersamento(transfer.getRemittanceInformation())
      .deEDatiPagDatiSingPagDatiSpecificiRiscossione(transfer.getTransferCategory())
      .codTipoDovuto(mbCodTipoDovuto)
      .dtAcquisizione(dtExport)
      .indiceDatiSingoloPagamento(indiceDatiSingoloPagamento)
      .deImportaDovutoEsito(null)
      .deImportaDovutoFaultCode(null)
      .deImportaDovutoFaultString(null)
      .deImportaDovutoFaultId(null)
      .deImportaDovutoFaultDescription(null)
      .numImportaDovutoFaultSerial(0)
      .bilancio(null)

      .codTipoDovutoPa1(StringUtils.stripToNull(receiptExportTo.getCodTipoDovuto()))
      .deTipoDovutoPa1(StringUtils.stripToNull(receiptExportTo.getDeTipoDovuto()))
      .codTassonomicoDovutoPa1(StringUtils.stripToNull(receiptExportTo.getCodTassonomico()))
      .codFiscalePa1(StringUtils.stripToNull(receiptExportTo.getFiscalCode()))
      .deNomePa1(StringUtils.stripToNull(receiptExportTo.getDeNomeEnte()))

      .build();
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public boolean importReceiptForSecondaryEnte(ReceiptExportTo receiptExportTo) {

    int numUpserted = 0;
    for(ReceiptTransferExportTo transfer : receiptExportTo.getReceiptTransferExportToList()){
      if(StringUtils.equals(receiptExportTo.getFiscalCode(), transfer.getFiscalCodePA())){
        //this is a transfer on ente handling the payment -> skip
        log.debug("skip import of receipt [{} / {}] transfer id[{}] for ente[{}]: is ente handling the payment", receiptExportTo.getReceiptId(),
            receiptExportTo.getFiscalCode(), transfer.getIdTransfer(), transfer.getFiscalCodePA());
        continue;
      }
      if(StringUtils.isBlank(transfer.getIban())){
        //this is a transfer of a "marca da bollo" -> skip
        log.debug("skip import of receipt [{} / {}] transfer id[{}] for ente[{}]: is payment for marca da bollo", receiptExportTo.getReceiptId(),
            receiptExportTo.getFiscalCode(), transfer.getIdTransfer(), transfer.getFiscalCodePA());
        continue;
      }
      Ente ente = enteService.getEnteByCodFiscale(transfer.getFiscalCodePA());
      if(ente==null) {
        //ente is not managed by MyPivot ->skip
        log.debug("skip import of receipt [{} / {}] transfer id[{}] for ente[{}]: ente not found in MyPivot", receiptExportTo.getReceiptId(),
            receiptExportTo.getFiscalCode(), transfer.getIdTransfer(), transfer.getFiscalCodePA());
        continue;
      } else {
        //just to better readability on log messages
        ente.setDeLogoEnte(null);
      }
      Long manageFlussoId = manageFlussoService.getIdByTypeSecondaryEnte("S", ente.getCodiceFiscaleEnte());
      if (null == manageFlussoId) {
        Utente utente = utenteService.getUtenteWSByCodIpaEnte(ente.getCodIpaEnte());
        manageFlussoId = manageFlussoService.insertForEnteSecondario(Constants.TIPO_FLUSSO.of("S").getCod(),
            ente, anagraficaStatoManageFileCaricato, utente);
      }
      numUpserted += self.upsertFlussoExport(receiptExportTo, transfer, ente, manageFlussoId);
    }

    return numUpserted > 0;
  }

}
