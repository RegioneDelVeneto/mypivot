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

import it.regioneveneto.mygov.payment.mypay4.exception.BadRequestException;
import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import it.regioneveneto.mygov.payment.mypay4.exception.ValidatorException;
import it.regioneveneto.mygov.payment.mypay4.service.MyBoxService;
import it.regioneveneto.mygov.payment.mypay4.service.common.CacheService;
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypay4.util.MaxResultsHelper;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypivot4.dao.InfoMappingTesoreriaDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.ManageFlussoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.PrenotazioneFlussoRiconciliazioneDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoImportTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoUploadRequestTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.InfoMappingTesoreria;
import it.regioneveneto.mygov.payment.mypivot4.model.ManageFlusso;
import it.regioneveneto.mygov.payment.mypivot4.queue.QueueProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static it.regioneveneto.mygov.payment.mypay4.util.Constants.TIPO_FLUSSO.*;

@Service
@Slf4j
public class FlussoService {

  @Autowired
  private ManageFlussoDao manageFlussoDao;

  @Autowired
  private PrenotazioneFlussoRiconciliazioneDao prenotazioneFlussoRiconciliazioneDao;

  @Autowired
  private EnteService enteService;

  @Autowired
  private AnagraficaStatoService anagraficaStatoService;

  @Autowired
  private ManageFlussoService manageFlussoService;

  @Autowired
  private MessageSource messageSource;

  @Autowired(required = false)
  private QueueProducer queueProducer;

  @Autowired
  private MyBoxService myBoxService;

  @Autowired
  private InfoMappingTesoreriaDao infoMappingTesoreriaDao;

  @Autowired
  private MaxResultsHelper maxResultsHelper;

  @Value("${mypay.path.manage.log}")
  private String flussiLogRootDir;

  @Value("${app.be.absolute-path}")
  private String apiAbsolutePath;

  @Value("${dovuti.import-path}")
  private String dovutiImportPath;

  @Value("${flussiexport.import-path}")
  private String flussiExportImportPath;

  @Value("${rendicontazione.import-path}")
  private String rendicontazioneImportPath;

  @Value("${tesoreria.import-path}")
  private String tesoreriaImportPath;

  @Value("${flussoposte.import-path}")
  private String flussoPosteImportPath;

  @Cacheable(value= CacheService.CACHE_NAME_FLUSSO, key="{'id',#id}", unless="#result==null")
  public ManageFlusso getById(Long id) {
    return manageFlussoDao.getById(id);
  }

  @Cacheable(value=CacheService.CACHE_NAME_FLUSSO)
  public List<ManageFlusso> getByEnte(Long mygovEnteId) {
    return manageFlussoDao.getByEnte(mygovEnteId);
  }

  public List<FlussoImportTo> getByEnteCodIdentificativoFlussoCreateDt(
    Long mygovEnteId, String username, String codTipo, String codIdentificativoFlusso, LocalDate dateFrom, LocalDate dateTo) throws ValidatorException {
    if (dateTo.isBefore(dateFrom)) {
      throw new ValidatorException(messageSource.getMessage("pa.messages.invalidDataIntervallo", null, Locale.ITALY));
    }

    Ente ente = enteService.getEnteById(mygovEnteId);
    Constants.TIPO_FLUSSO tipoFlusso = Constants.TIPO_FLUSSO.of(codTipo);
    switch (tipoFlusso) {
      case TESORERIA:
      case GIORNALE_DI_CASSA:
      case GIORNALE_DI_CASSA_OPI:
      case ESTRATTO_CONTO_POSTE:
        if (!ente.isFlgTesoreria())
          throw new BadRequestException("Tesoreria not enabled for ente: "+ente.getCodIpaEnte());
      default:
    }

    final List<String> listCodStatoManage = Arrays.asList(
        Constants.COD_TIPO_STATO_MANAGE_FLUSSO_OBSOLETO, Constants.COD_TIPO_STATO_MANAGE_FILE_IN_DOWNLOAD,
        Constants.COD_TIPO_STATO_MANAGE_ERROR_DOWNLOAD, Constants.COD_TIPO_STATO_MANAGE_ERROR_LOAD,
        Constants.COD_TIPO_STATO_MANAGE_FILE_SCARICATO, Constants.COD_TIPO_STATO_MANAGE_FILE_IN_CARICAMENTO,
        Constants.COD_TIPO_STATO_MANAGE_FILE_CARICATO);

    return maxResultsHelper.manageMaxResults(
      maxResults -> manageFlussoDao.getByEnteCodIdentificativoFlussoCreateDt(mygovEnteId, username, tipoFlusso.getCod(),
        codIdentificativoFlusso, dateFrom, dateTo.plusDays(1), listCodStatoManage, maxResults),
      this::mapEntityToDto,
      () -> manageFlussoDao.getByEnteCodIdentificativoFlussoCreateDtCount(mygovEnteId, username, tipoFlusso.getCod(),
        codIdentificativoFlusso, dateFrom, dateTo.plusDays(1), listCodStatoManage) );
  }


    private FlussoImportTo mapEntityToDto(ManageFlusso manageFlusso) {
      FlussoImportTo flussoTo = new FlussoImportTo();
      flussoTo.setId(manageFlusso.getMygovManageFlussoId());
      flussoTo.setNomeFlusso(manageFlusso.getDeNomeFile());
      flussoTo.setDataCaricamento(Utilities.toLocalDateTime(manageFlusso.getDtCreazione()));
      flussoTo.setOperatore(getOperatore(manageFlusso));
      flussoTo.setCodStato(manageFlusso.getMygovAnagraficaStatoId().getCodStato());
      flussoTo.setDeStato(manageFlusso.getMygovAnagraficaStatoId().getDeStato());
      flussoTo.setCodTipo(manageFlusso.getMygovTipoFlussoId().getCodTipo());
      flussoTo.setDeTipo(manageFlusso.getMygovTipoFlussoId().getDeTipo());

      if(Constants.COD_TIPO_STATO_MANAGE_FILE_CARICATO.equals(manageFlusso.getMygovAnagraficaStatoId().getCodStato())) {
        if(StringUtils.isNotBlank(manageFlusso.getDeNomeFile()) && (manageFlusso.getDeNomeFile().length() > 4) ) {
          flussoTo.setFilePathOriginale(manageFlusso.getDePercorsoFile() + File.separator + manageFlusso.getDeNomeFile());
        }
      }
      if (StringUtils.isNotBlank(manageFlusso.getDeNomeFileScarti()) && (manageFlusso.getDeNomeFileScarti().length() > 4)) {
        flussoTo.setFilePathScarti(manageFlusso.getDeNomeFileScarti());
        if(StringUtils.isNotBlank(manageFlusso.getDeNomeFile()) && (manageFlusso.getDeNomeFile().length() > 4) ) {
          flussoTo.setFilePathOriginale(manageFlusso.getDePercorsoFile() + File.separator + manageFlusso.getDeNomeFile());
        }
      }
      flussoTo.setShowDownload(StringUtils.isNotBlank(flussoTo.getFilePathOriginale()) || StringUtils.isNotBlank(flussoTo.getFilePathScarti()));
      File file = new File(flussiLogRootDir+File.separator+manageFlusso.getMygovEnteId().getCodIpaEnte(), manageFlusso.getDeNomeFile()+".log");
      flussoTo.setLog(file.exists()?manageFlusso.getDeNomeFile():null);

    return flussoTo;
  }

  /**
   * Insert into mygov_manage_flusso and Save the file in the file system.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void onFlussoUpload(String codFedUserId, Ente ente, MultipartFile file, FlussoUploadRequestTo requestTo){
    Constants.TIPO_FLUSSO tipoFlusso = Constants.TIPO_FLUSSO.of(requestTo.getCodTipo());
    Timestamp tsCreazione = new Timestamp(System.currentTimeMillis());
    String path = StringUtils.firstNonBlank(requestTo.getImportPath(), getImportPath(tipoFlusso, tsCreazione));
    String relativePath = path;

    this.validateFile(ente, file, tipoFlusso);

    path = ente.getCodIpaEnte()+File.separator+path;
    if(manageFlussoService.isDuplicateRequestToken(requestTo.getRequestToken())){
      log.error("duplicate request token {} - ente: {} - tipo: {}", requestTo.getRequestToken(), ente.getCodIpaEnte(), requestTo.getCodTipo());
      throw new ValidatorException("duplicate request token");
    }
    Pair<String, String> uploaded = myBoxService.uploadFile(path, file, requestTo.getRequestToken());
    long mygovManageFlussoId = manageFlussoService.insert(tipoFlusso.getCod(), ente.getCodIpaEnte(), codFedUserId, uploaded.getRight(),
        requestTo.getCodProvenienza(), Constants.DE_TIPO_STATO_MANAGE, Constants.COD_TIPO_STATO_MANAGE_FILE_SCARICATO,
        relativePath, org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename()), tsCreazione);
    log.debug("onFlussoUpload - file: "+file.getOriginalFilename()+" type: "+tipoFlusso);
    if (tipoFlusso.equals(GIORNALE_DI_CASSA) || tipoFlusso.equals(ESTRATTO_CONTO_POSTE)) {
      log.debug("info_mapping_tesoreria - manage_flusso_id: "+mygovManageFlussoId);
      InfoMappingTesoreria info = InfoMappingTesoreria.builder()
          .mygovManageFlussoId(ManageFlusso.builder().mygovManageFlussoId(mygovManageFlussoId).build())
          .posDeAnnoBolletta(requestTo.getPosDeAnnoBolletta())
          .posCodBolletta(requestTo.getPosCodBolletta()).posCodBolletta(requestTo.getPosCodBolletta())
          .posDtContabile(requestTo.getPosDtContabile()).posDtContabile(requestTo.getPosDtContabile())
          .posDeDenominazione(requestTo.getPosDeDenominazione()).posDeDenominazione(requestTo.getPosDeDenominazione())
          .posDeCausale(requestTo.getPosDeCausale()).posDeCausale(requestTo.getPosDeCausale())
          .posNumImporto(requestTo.getPosNumImporto()).posNumImporto(requestTo.getPosNumImporto())
          .posDtValuta(requestTo.getPosDtValuta()).posDtValuta(requestTo.getPosDtValuta()).build();
      infoMappingTesoreriaDao.insert(info);
      log.debug("info_mapping_tesoreria record inserted");
    }
    queueProducer.enqueueFlussoUpload(tipoFlusso, Long.toString(mygovManageFlussoId));
  }

  private void validateFile(Ente ente, MultipartFile file, Constants.TIPO_FLUSSO TIPO) {
    if (!StringUtils.endsWithIgnoreCase(file.getOriginalFilename(),".zip")) {
      throw new ValidatorException("Il formato del file deve essere lo zip.");
    } else
      if (! GIORNALE_DI_CASSA_OPI.equals(TIPO)){
        try {
          String fileNameNoExtension = StringUtils.removeEndIgnoreCase(file.getOriginalFilename(),".zip");
          ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(file.getBytes()));
          ZipEntry entry;
          while((entry = zis.getNextEntry()) != null) {
            if(!StringUtils.startsWith(entry.getName(), fileNameNoExtension + "."))
              throw new ValidatorException("Il nome del file contenuto deve essere uguale al nome dello zip.");
          }
        } catch (IOException ex) {
          throw new ValidatorException("Verificato un errore durante la lettura dello zip caricato.");
        }

        if (!StringUtils.startsWith(file.getOriginalFilename(), ente.getCodIpaEnte()) && (EXPORT_PAGATI.equals(TIPO) || DOVUTI.equals(TIPO)))
          throw new ValidatorException("Il nome del file deve iniziare col codice dell'ente.");
      }

    if (manageFlussoService.isDuplicateFileName(file.getOriginalFilename()))
      throw new ValidatorException("Lo stesso nome di file esiste gia'.");
  }

  private String getOperatore(ManageFlusso manageFlusso){
    String operatore = "";
    if (manageFlusso.getMygovUtenteId()!=null){
      if (manageFlusso.getMygovUtenteId().getDeFirstname()!=null){
        operatore+= manageFlusso.getMygovUtenteId().getDeFirstname().trim();
      }
      if (manageFlusso.getMygovUtenteId().getDeFirstname()!=null){
        if (operatore.length()>0){
          operatore+= " ";
        }
        operatore+=manageFlusso.getMygovUtenteId().getDeLastname();
      }
    }
    return operatore;
  }

  public String getImportPath(Constants.TIPO_FLUSSO tipo, Timestamp ts) {
    String path = "";
    switch (tipo) {
      case DOVUTI:
        path = dovutiImportPath;
        break;
      case EXPORT_PAGATI:
        path = flussiExportImportPath;
        break;
      case RENDICONTAZIONE_STANDARD:
        path = rendicontazioneImportPath;
        break;
      case TESORERIA:
      case GIORNALE_DI_CASSA:
      case GIORNALE_DI_CASSA_OPI:
        path = tesoreriaImportPath;
        break;
      case ESTRATTO_CONTO_POSTE:
        path = flussoPosteImportPath;
        break;
      default:
        throw new MyPayException("invalid tipo flusso "+tipo);
    }
    return path+Utilities.getYYYY_MM(ts);
  }

  public String getApiAbsolutePath() {
    return apiAbsolutePath;
  }
}
