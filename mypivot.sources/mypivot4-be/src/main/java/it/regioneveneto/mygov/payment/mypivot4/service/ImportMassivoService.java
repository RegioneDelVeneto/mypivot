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
import it.regioneveneto.mygov.payment.mypivot4.dao.AnagraficaUffCapAccDao;
import it.regioneveneto.mygov.payment.mypivot4.exception.MalformCSVException;
import it.regioneveneto.mygov.payment.mypivot4.model.AnagraficaUffCapAcc;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.regioneveneto.mygov.payment.mypivot4.model.OperatoreEnteTipoDovuto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
@Transactional
public class ImportMassivoService {

  private static Integer MAX_NUMBER_CHAR_COD = 64;
  private static Integer MAX_NUMBER_CHAR_DE = 512;

  @Autowired
  private EnteService enteService;

  @Autowired
  private OperatoreEnteTipoDovutoService operatoreEnteTipoDovutoService;

  @Autowired
  private AnagraficaUffCapAccDao anagraficaUffCapAccDao;

  @Transactional(propagation = Propagation.REQUIRED)
  public void importMassivo(Long enteId, String codFedUserId, MultipartFile file) throws Exception {

    Ente ente = enteService.getEnteById(enteId);
    if (ente.getCodIpaEnte().equals("R_VENETO")) {
      log.warn("Non è consentito l'inserimento massivo per la regione Veneto.");
      throw new ValidatorException("Non è consentito l'inserimento massivo per la regione Veneto.");
    }

    InputStream inputStream = this.requestValidation(ente, codFedUserId, file);

    BufferedReader fileBufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));

    String line = "";
    String[] split = {};
    int lineNumber = 0;
    List<OperatoreEnteTipoDovuto> activeOperatoreEnteTdAsObj = operatoreEnteTipoDovutoService.getByCodIpaCodTipoCodFed(ente.getCodIpaEnte(), null, codFedUserId);

    while ((line = fileBufferedReader.readLine()) != null) {
      if (lineNumber == 0) {
        lineNumber++;
        continue;
      }

      // controllo che il csv abbia come separatori i punti e virgola,
      if (!line.contains("\";\"")) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber + ": il file non è formattato correttamente");
      }

      // rimuovo " ad inizio e fine riga
      line = line.substring(1, line.length() - 1);

      split = line.split("\";\"");

      // controllo che ogni riga abbia esattamente 10 valori
      if (split.length != 10) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException(
            "Errore di conversione alla linea " + lineNumber + ": il file non è formattato correttamente");
      }

      for (String s : split) {
        String temp = new String(s.getBytes("UTF-8"), "UTF-8");
        if (!temp.equals(s)) {
          // if (!s.matches("^[a-zA-Z0-9 _]*[^èéìòàù]$")){
          fileBufferedReader.close();
          log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
          throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
              + ": non sono permessi caratteri non UTF-8");
        }
      }

      if (StringUtils.isBlank(split[0]) && StringUtils.isBlank(split[1]) && StringUtils.isBlank(split[2])
          && StringUtils.isBlank(split[3]) && StringUtils.isBlank(split[4]) && StringUtils.isBlank(split[5])
          && StringUtils.isBlank(split[6]) && StringUtils.isBlank(split[7]) && StringUtils.isBlank(split[8])
          && StringUtils.isBlank(split[9])) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException(
            "Errore di conversione alla linea " + lineNumber + ": non sono permesse righe senza valori");
      }

      if (StringUtils.isBlank(split[0])) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException(
            "Errore di conversione alla linea " + lineNumber + ": il campo cod_ufficio non è valorizzato");
      } else if (split[0].length() > MAX_NUMBER_CHAR_COD) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo cod_ufficio deve avere una lunghezza massima di " + MAX_NUMBER_CHAR_COD
            + " caratteri");
      }
      if (StringUtils.isBlank(split[1])) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException(
            "Errore di conversione alla linea " + lineNumber + ": il campo de_ufficio non è valorizzato");
      } else if (split[1].length() > MAX_NUMBER_CHAR_DE) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo de_ufficio deve avere una lunghezza massima di " + MAX_NUMBER_CHAR_DE
            + " caratteri");
      }
      if (StringUtils.isBlank(split[2])) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException(
            "Errore di conversione alla linea " + lineNumber + ": il campo flg_attivo non è valorizzato");
      } else if (!split[2].equalsIgnoreCase("true") && !split[2].equalsIgnoreCase("false")) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo flg_attivo non è valido (true/false valori accettati)");
      }
      if (StringUtils.isBlank(split[3])) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException(
            "Errore di conversione alla linea " + lineNumber + ": il campo cod_capitolo non è valorizzato");
      } else if (split[3].length() > MAX_NUMBER_CHAR_COD) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo cod_capitolo deve avere una lunghezza massima di " + MAX_NUMBER_CHAR_COD
            + " caratteri");
      }
      if (StringUtils.isBlank(split[4])) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException(
            "Errore di conversione alla linea " + lineNumber + ": il campo de_capitolo non è valorizzato");
      } else if (split[4].length() > MAX_NUMBER_CHAR_DE) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo de_capitolo deve avere una lunghezza massima di " + MAX_NUMBER_CHAR_DE
            + " caratteri");
      }
      if (StringUtils.isBlank(split[5])) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo de_anno_esercizio non è valorizzato");
      } else if (!StringUtils.isNumeric(split[5]) || split[5].length() > 4) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo de_anno_esercizio non è un numero valido");
      }
      if (StringUtils.isBlank(split[8])) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo cod_tipo_dovuto non è valorizzato");
      } else if (split[8].length() > 64) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo cod_tipo_dovuto deve avere una lunghezza massima di 64 caratteri");
      }
      if (StringUtils.isBlank(split[9])) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException(
            "Errore di conversione alla linea " + lineNumber + ": il campo azione non è valorizzato");
      }

      AnagraficaUffCapAcc anag = new AnagraficaUffCapAcc();
      anag.setMygovEnteId(enteId);
      anag.setCodUfficio(split[0]); // chiave di ricerca
      anag.setDeUfficio(split[1]);
      anag.setFlgAttivo(Boolean.parseBoolean(split[2]));
      anag.setCodCapitolo(split[3]); // chiave di ricerca
      anag.setDeCapitolo(split[4]);
      anag.setDeAnnoEsercizio(split[5]);

      if (StringUtils.isBlank(split[6])) {
        anag.setCodAccertamento("n/a"); // chiave di ricerca//opzionale
        anag.setDeAccertamento("n/a");
      } else if (split[6].length() > MAX_NUMBER_CHAR_COD) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo cod_accertamento deve avere una lunghezza massima di " + MAX_NUMBER_CHAR_COD
            + " caratteri");
      } else {
        anag.setCodAccertamento(split[6]);

        // se il codice accertamento è settato, controllo la descrizione
        if (StringUtils.isBlank(split[7]))
          anag.setDeAccertamento("n/a");
        else if (split[7].length() > MAX_NUMBER_CHAR_DE)
          anag.setDeAccertamento(split[7].substring(0, MAX_NUMBER_CHAR_DE));
        else
          anag.setDeAccertamento(split[7]);
      }

      boolean contain = false;
      if (StringUtils.isNotBlank(split[8])) {
        /**
         * Recupero la lista dei tipi dovuto dell'ente per cui l'utente è un operatore
         * attivo. Se la lista è vuota, mostro un messaggio di errore perchè vuol dire
         * che l'utente non può ne consultare ne creare accertamenti.
         */
        for (OperatoreEnteTipoDovuto oetd: activeOperatoreEnteTdAsObj) {
          if (oetd.getMygovEnteTipoDovutoId().getCodTipo().equals(split[8]))
            contain = true;
        }
      }
      if (!contain) {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo codice_tipo_dovuto non è legato all'ente");
      }
      anag.setCodTipoDovuto(split[8]); // chiave di ricerca

      anag.setDtCreazione(new Date());
      anag.setDtUltimaModifica(new Date());

      // se il valore è I devo eseguire una insert
      if (split[9].equals("I")) {
        AnagraficaUffCapAcc temp = new AnagraficaUffCapAcc();

        // check per controllare che il codice accertamento sia presente
        List<AnagraficaUffCapAcc> anagrafiche = anagraficaUffCapAccDao.getAccertamentiCapitoliByCod(enteId, anag.getCodTipoDovuto(),
            anag.getCodUfficio(), anag.getCodCapitolo(), anag.getDeAnnoEsercizio(), anag.getCodAccertamento());
        temp = !CollectionUtils.isEmpty(anagrafiche) ? anagrafiche.get(0) : null;

        // se non esiste, controllo che le descrizioni dei codici siano corrette
        if (temp == null) {

          anagrafiche = anagraficaUffCapAccDao.getAccertamentiCapitoliByCod(enteId, null, anag.getCodUfficio(), null, null, null);
          String deUfficio = !CollectionUtils.isEmpty(anagrafiche) ? anagrafiche.get(0).getDeUfficio() : null;

          if (deUfficio != null && !deUfficio.equals(anag.getDeUfficio())) {
            fileBufferedReader.close();
            log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
            throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
                + ":  non puoi inserire un de_ufficio diverso da un de_ufficio già esistente");
          }

          anagrafiche = anagraficaUffCapAccDao.getAccertamentiCapitoliByCod(enteId, null, anag.getCodUfficio(), anag.getCodCapitolo(), null, null);
          String deCapitolo = !CollectionUtils.isEmpty(anagrafiche) ? anagrafiche.get(0).getDeCapitolo() : null;

          if (deCapitolo != null && !deCapitolo.equals(anag.getDeCapitolo())) {
            fileBufferedReader.close();
            log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
            throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
                + ": non puoi inserire un de_capitolo diverso da un de_capitolo già esistente");
          }

          anagrafiche = anagraficaUffCapAccDao.getAccertamentiCapitoliByCod(enteId, null, anag.getCodUfficio(), anag.getCodCapitolo(), null, anag.getCodAccertamento());
          String deAccertamento = !CollectionUtils.isEmpty(anagrafiche) ? anagrafiche.get(0).getDeAccertamento() : null;

          if (deAccertamento != null && !deAccertamento.equals(anag.getDeAccertamento())) {
            fileBufferedReader.close();
            log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
            throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
                + ": non puoi inserire un de_accertamento diverso da un de_accertamento già esistente");
          }

          log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: PERSIST :: ENTRY CODUFF: "
              + anag.getCodUfficio() + ", CODCAP: " + anag.getCodCapitolo() + ", CODTIPODOV: "
              + anag.getCodTipoDovuto() + " e CODACC: " + anag.getCodAccertamento());
          // entityManager.persist(anag);
          anagraficaUffCapAccDao.insert(anag);
        } else {
          fileBufferedReader.close();
          log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
         /* throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
              + ": l'entry con codice ufficio: " + anag.getCodUfficio() + ", codice capitolo: "
              + anag.getCodCapitolo() + ", codice tipo dovuto: " + anag.getCodTipoDovuto()
              + ",codice accertamento: " + anag.getCodAccertamento()
              + " e anno esercizio: " + anag.getDeAnnoEsercizio() + " è già esistente");*/

          throw new MalformCSVException("Errore di import alla riga " + lineNumber
                  + ": l'entry è già esistente");
        }
      }

      // se il valore è D devo eseguire una delete
      else if (split[9].equals("D")) {
        AnagraficaUffCapAcc temp = new AnagraficaUffCapAcc();

        // check per controllare che il codice accertamento sia presente
        List<AnagraficaUffCapAcc> anagrafiche = anagraficaUffCapAccDao.getAccertamentiCapitoliByCod(enteId, anag.getCodTipoDovuto(),
            anag.getCodUfficio(), anag.getCodCapitolo(), anag.getDeAnnoEsercizio(), anag.getCodAccertamento());
        temp = !CollectionUtils.isEmpty(anagrafiche) ? anagrafiche.get(0) : null;

        // se esiste posso eseguire la delete
        if (temp != null) {
          log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: REMOVE :: ENTRY CODUFF: "
              + anag.getCodUfficio() + ", CODCAP: " + anag.getCodCapitolo() + ", CODTIPODOV: "
              + anag.getCodTipoDovuto() + ", CODACC: " + anag.getCodAccertamento()
              + " e ANNO_ESERCIZIO " + anag.getDeAnnoEsercizio());
          anagraficaUffCapAccDao.deleteById(temp.getMygovAnagraficaUffCapAccId());
        } else {
          fileBufferedReader.close();
          log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
          throw new MalformCSVException(
              "Errore di conversione alla linea " + lineNumber + ": l'entry con codice ufficio: "
                  + anag.getCodUfficio() + ", codice capitolo: " + anag.getCodCapitolo()
                  + ", codice tipo dovuto: " + anag.getCodTipoDovuto()
                  + ", codice accertamento: "	+ anag.getCodAccertamento()
                  + " e anno esercizio: "	+ anag.getDeAnnoEsercizio()
                  + " non esiste e quindi non può essere eliminata");
        }
      } else {
        fileBufferedReader.close();
        log.debug("MANAGE FILE CSV :: ANAGRAFICA UFF CAP ACC :: THROW ROLLBACK :: END");
        throw new MalformCSVException("Errore di conversione alla linea " + lineNumber
            + ": il campo azione può contenere solo i caratteri I (insert) e D (delete)");
      }
      lineNumber++;
    }

    // Chiudo il file
    fileBufferedReader.close();
  }

  private InputStream requestValidation(Ente ente, String codFedUserId, MultipartFile file) throws ValidatorException {
    String split[] = {};
    String filename = file.getOriginalFilename();
    InputStream inputStream = null;
    FileInputStream fileInputStream = null;
    ZipInputStream zipInputStream = null;
    String endName="1_0.zip";
    try {

      if(!filename.endsWith(".zip")){
        log.error("Il file deve avere estensione zip [(0)].");
        throw new ValidatorException("Il formato del file deve essere zip..");
      }

      zipInputStream = new ZipInputStream(new ByteArrayInputStream(file.getBytes()));
      ZipEntry zipEntry = zipInputStream.getNextEntry();
      byte[] bytes = IOUtils.toByteArray(zipInputStream);
      if (zipInputStream.getNextEntry() != null){
        zipInputStream.close();
        log.error("Il file zip contiene più di un file [(1)].");
        throw new ValidatorException("Il file zip contiene più di un file.");
      }
      //controllo che i nomi del file zip e del csv all'interno siano uguali tranne le estensioni
      String zipNameNoExt= new StringTokenizer(filename,".").nextToken();
      String csvNameNoExt= new StringTokenizer(zipEntry.getName(),".").nextToken();

   //   if (!zipEntry.getName().substring(0, zipEntry.getName().length()-4).equals(filename.substring(0, filename.length()-4))){
      if (!zipNameNoExt.equals(csvNameNoExt)){
        zipInputStream.close();
        log.error("Il nome del file non è corretto [(2) {} - {}].", zipEntry.getName(), filename);
        throw new ValidatorException("Il nome del file contenuto deve essere uguale al nome dello zip.");
      }
      if (!zipEntry.getName().endsWith(".csv")){
        zipInputStream.close();
        log.error("Il file .zip deve contenere un tracciato csv [(3) {}].", zipEntry.getName());
        throw new ValidatorException("Il file .zip deve contenere un tracciato csv.");
      }
      //il nome del file deve essere scritto con il formato C_IPA-NOME FLUSSO-VERSIONE. es: R_VENETO-FLUSSO_IMPORT_MASSIVO-1_0
      if (!filename.contains("-")){
        zipInputStream.close();
        log.error("Il nome del file non è corretto [(4) {}].", filename);
        throw new ValidatorException("Il nome del file non corrisponde a 'C_IPA-NOME FLUSSO-VERSIONE'.");
      }
      split = filename.split("-");
      if (split.length != 3 || !split[0].equals(ente.getCodIpaEnte()) || !split[2].equals(endName)){
        zipInputStream.close();
        log.error("Il nome del file non è corretto [(5) {}].", filename);
        throw new ValidatorException("Il nome del file non corrisponde a 'C_IPA-NOME FLUSSO-VERSIONE'.");
      }

      return new ByteArrayInputStream(bytes);
    } catch (IOException e1) {
      log.error("Errore durante la lettura del file .zip", e1);
      throw new ValidatorException("Errore durante la lettura del file .zip");
    }
  }
}
