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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.mygov.payment.mypay4.config.MyPay4AbstractSecurityConfig;
import it.regioneveneto.mygov.payment.mypay4.exception.BadRequestException;
import it.regioneveneto.mygov.payment.mypay4.exception.NotAuthorizedException;
import it.regioneveneto.mygov.payment.mypay4.exception.ValidatorException;
import it.regioneveneto.mygov.payment.mypay4.security.JwtTokenUtil;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore.Role;
import it.regioneveneto.mygov.payment.mypay4.security.UserWithAdditionalInfo;
import it.regioneveneto.mygov.payment.mypay4.service.MyBoxService;
import it.regioneveneto.mygov.payment.mypay4.service.common.AppErrorService;
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypivot4.dto.FlussoUploadRequestTo;
import it.regioneveneto.mygov.payment.mypivot4.dto.WsImportTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteService;
import it.regioneveneto.mygov.payment.mypivot4.service.FlussoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "MyBox", description = "Gestione di upload e download dei file")
@RestController
@Slf4j
@ConditionalOnWebApplication
public class MyBoxController {

  public final static String AUTHENTICATED_PATH = "mybox";
  public final static String ANONYMOUS_PATH = MyPay4AbstractSecurityConfig.PATH_PUBLIC+"/"+ AUTHENTICATED_PATH;
  public final static String UPLOAD_FLUSSO_PATH = ANONYMOUS_PATH+"/uploadFlusso";

  @Autowired
  MyBoxService myBoxService;

  @Autowired
  EnteService enteService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private FlussoService flussoService;

  @Autowired
  private AppErrorService appErrorService;

  @PostMapping(path=UPLOAD_FLUSSO_PATH, consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<?> uploadFlussoByWS(@RequestParam("authorizationToken") String authorizationToken, MultipartHttpServletRequest request) {
    Map<String, String> responseMap = new HashMap<>();

    try {
      MultipartFile file = getMultipartFileFromRequest(request);
      WsImportTo wsImportTo;
      try {
        wsImportTo = jwtTokenUtil.parseWsAuthorizationToken(authorizationToken);
      } catch (Exception e) {
        log.error("invalid Ws authorization token for uploadFlusso [{}] - file [{}]", authorizationToken, file != null ? file.getOriginalFilename() : null, e);
        throw new NotAuthorizedException("token autorizzazione non valido o scaduto");
      }
      FlussoUploadRequestTo requestTo = FlussoUploadRequestTo.DEFAULT_POSITION()
        .toBuilder()
        .codTipo(wsImportTo.getTipoFlusso())
        .importPath(wsImportTo.getImportPath())
        .requestToken(wsImportTo.getRequestToken())
        .codProvenienza(Constants.COD_PROVENIENZA_FILE_BATCH)
        .build();
      String codIpa = wsImportTo.getCodIpaEnte();
      Ente ente = enteService.getEnteByCodIpa(codIpa);
      this.uploadImpl(
        ente,
        codIpa + "-" + Constants.WS_USER,
        FlussoController.FILE_TYPE_FLUSSI_IMPORT,
        file,
        requestTo);
      responseMap.put("fileName",file.getOriginalFilename());
      responseMap.put("fileSize",file.getSize()+"");
      responseMap.put("fileType",file.getContentType());
    }catch(Exception e){
      String code;
      if(e instanceof ValidatorException)
        code = "400";
      else if(e instanceof NotAuthorizedException)
        code = "401";
      else
        code = "500";
      Pair<String, String> errorUid = appErrorService.generateNowStringAndErrorUid();
      log.error("uploadByWS exception now[{}] errorUid[{}]", errorUid.getLeft(), errorUid.getRight(), e);
      responseMap.put("codice",code);
      responseMap.put("descrizione","["+errorUid.getRight()+"] "+e.getMessage());
    }

    return ResponseEntity.status(HttpStatus.OK).body(responseMap);
  }

  @PostMapping(path=AUTHENTICATED_PATH+"/upload/{mygovEnteId}", consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_ADMIN)
  public ResponseEntity<?> uploadByWebapp(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                               @RequestParam String type, MultipartHttpServletRequest request,
                               @RequestParam(name = "postedJson", required = false) String postedJson) {
    FlussoUploadRequestTo requestTo;
    try {
      requestTo = new Gson().fromJson(postedJson, new TypeToken<FlussoUploadRequestTo>(){}.getType());
      requestTo
          .toBuilder()
          .codProvenienza(Constants.COD_PROVENIENZA_FILE_WEB)
          .requestToken(UUID.randomUUID().toString())
          .build();
    } catch (RuntimeException ex) {
      throw new ValidatorException(HttpStatus.BAD_REQUEST.getReasonPhrase());
    }
    MultipartFile file = getMultipartFileFromRequest(request);
    Ente ente = enteService.getEnteById(mygovEnteId);
    this.uploadImpl(ente, user.getUsername(), type, file, requestTo);
    return ResponseEntity.ok().build();
  }

  private void uploadImpl(Ente ente, String codFedUserId, String type, MultipartFile file, FlussoUploadRequestTo requestTo){
    if(ente==null)
      throw new ValidatorException("invalid ente");
    if (FlussoController.FILE_TYPE_FLUSSI_IMPORT.equals(type)) {
      flussoService.onFlussoUpload(codFedUserId, ente, file, requestTo);
    } else {
      throw new ValidatorException("invalid upload file type");
    }
  }

  @GetMapping(ANONYMOUS_PATH+"/download/{mygovEnteId}")
  public ResponseEntity<?> downloadPublic(@PathVariable Long mygovEnteId, @RequestParam String type,
                                          @RequestParam String securityToken, @RequestParam String filename) {
    return this.download(null, mygovEnteId, type, filename, securityToken);
  }

  @GetMapping(AUTHENTICATED_PATH+"/download/{mygovEnteId}")
  @Operatore(value = "mygovEnteId", roles = Role.ROLE_VISUAL)
  public ResponseEntity<?> download(@AuthenticationPrincipal UserWithAdditionalInfo user, @PathVariable Long mygovEnteId,
                                    @RequestParam String type, @RequestParam String filename,
                                    @RequestParam String securityToken) {

    //check that is downloading something attached to a security token
    Optional<ResponseEntity<?>> errorResponse = myBoxService.checkSecurityToken(user, securityToken, type, filename, mygovEnteId);
    if(errorResponse.isPresent())
      return errorResponse.get();

    Ente ente = enteService.getEnteById(mygovEnteId);
    if(ente==null)
      throw new ValidatorException("invalid ente");
    String path = ente.getCodIpaEnte();

    Pair<Resource, Long> file = myBoxService.downloadFile(path, filename);
    if(file==null){
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File \""+filename+"\" non disponibile");
    }

    String realFileName = FilenameUtils.getName(filename);
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+realFileName);
    log.debug("downloading file with path[{}], filename[{}] - length[{} bytes]", path, filename, file.getRight());
    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(file.getRight())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(file.getLeft());
  }

  //retrieve the file this way and not with @RequestParam because we don't know the param name used in the request and may allow any value
  private MultipartFile getMultipartFileFromRequest(MultipartHttpServletRequest request){
    MultipartFile file;
    Map<String, MultipartFile> multipartFileMap = request.getMultiFileMap().toSingleValueMap();
    List<Map.Entry<String, MultipartFile>> listEntryStreamNotEmpty = multipartFileMap.entrySet().stream().filter(entry -> entry.getValue().getSize()>0).collect(Collectors.toList());
    if(listEntryStreamNotEmpty.size()==1){
      String key = listEntryStreamNotEmpty.get(0).getKey();
      file = listEntryStreamNotEmpty.get(0).getValue();
      log.info("retrieving MultipartFile with key [{}] name [{}] size [{}]", key, file.getOriginalFilename(), file.getSize());
    } else if(listEntryStreamNotEmpty.size()>1){
      log.error("invalid MultipartFile size {}", listEntryStreamNotEmpty.size());
      throw new BadRequestException("request multipart non valida, file presenti: "+listEntryStreamNotEmpty.size());
    } else if(multipartFileMap.size() > 0){
      log.error("invalid MultipartFile size, empty file");
      throw new BadRequestException("request multipart non valida, file vuoto");
    } else {
      log.error("invalid MultipartFile size, missing file");
      throw new BadRequestException("request multipart non valida, file non presente");
    }
    return file;
  }
}
