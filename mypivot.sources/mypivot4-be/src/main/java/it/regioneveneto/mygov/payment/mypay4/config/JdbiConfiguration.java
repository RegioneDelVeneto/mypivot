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
package it.regioneveneto.mygov.payment.mypay4.config;

import it.regioneveneto.mygov.payment.mypay4.logging.JdbiSqlLogger;
import it.regioneveneto.mygov.payment.mypivot4.dao.AccertamentoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.AccertamentoDettaglioDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.AnagraficaStatoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.AnagraficaUffCapAccDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.EnteDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.EnteTipoDovutoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.ExportRendicontazioneCompletaDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.FlussoExportDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.FlussoRendicontazioneDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.FlussoTesoreriaDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.InfoMappingTesoreriaDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.ManageFlussoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.OperatoreDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.OperatoreEnteTipoDovutoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.PrenotazioneFlussoRiconciliazioneDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.RiconciliazioneDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.SegnalazioneDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.TipoFlussoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.UtenteDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.VmStatisticaEnteAnnoMeseGiornoDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.VmStatisticaEnteAnnoMeseGiornoUffTdCapAccDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.VmStatisticaEnteAnnoMeseGiornoUffTdCapDao;
import it.regioneveneto.mygov.payment.mypivot4.dao.VmStatisticaEnteAnnoMeseGiornoUffTdDao;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class JdbiConfiguration {

  @Value("${sql-logging.enabled:false}")
  private String sqlLogginEnabled;

  @Autowired
  JdbiSqlLogger jdbiSqlLogger;

  @Primary
  @Bean("jdbiPivot")
  public Jdbi paJdbi(@Qualifier("dsPivot") DataSource ds, List<JdbiPlugin> jdbiPlugins, List<RowMapper<?>> rowMappers) {
    return _createJdbi(ds, jdbiPlugins, rowMappers);
  }

  private Jdbi _createJdbi (DataSource ds, List<JdbiPlugin> jdbiPlugins, List<RowMapper<?>> rowMappers) {
    TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(ds);
    Jdbi jdbi = Jdbi.create(proxy);
    if(!"false".equalsIgnoreCase(sqlLogginEnabled)) {
      jdbiSqlLogger.setBehaviour(sqlLogginEnabled);
      jdbi.setSqlLogger(jdbiSqlLogger);
    }
    // Register all available plugins
    String dsString;
    try{
      dsString = ds.getConnection().getMetaData().getURL();
    } catch (Exception e){
      dsString = ds.toString();
    }
    log.debug("Datasource {} - Installing jdbi plugins... ({} found): {}"
        , dsString, jdbiPlugins.size()
        , jdbiPlugins.stream().map(x -> x.getClass().getName()).collect(Collectors.joining(", ")) );
    jdbiPlugins.forEach(jdbi::installPlugin);
    // Register all available rowMappers
    log.debug("Datasource {} - Installing jdbi rowMappers... ({} found): {}"
        , dsString, rowMappers.size()
        , rowMappers.stream().map(x -> x.getClass().getName()).collect(Collectors.joining(", ")) );
    rowMappers.forEach(jdbi::registerRowMapper);
    return jdbi;
  }

  @Bean
  public JdbiPlugin sqlObjectPlugin() {
    return new SqlObjectPlugin();
  }

  @Bean
  public ResourceBundleMessageSource messageSource() {
    var source = new ResourceBundleMessageSource();
    source.setBasenames("messages/messages");
    source.setUseCodeAsDefaultMessage(true);
    return source;
  }


  @Bean
  public EnteDao enteDao(Jdbi jdbi) {
    return jdbi.onDemand(EnteDao.class);
  }

  @Bean
  public EnteTipoDovutoDao enteTipoDovutoDao(Jdbi jdbi) {
    return jdbi.onDemand(EnteTipoDovutoDao.class);
  }

  @Bean
  public ManageFlussoDao manageFlussiDao(Jdbi jdbi) {
    return jdbi.onDemand(ManageFlussoDao.class);
  }

  @Bean
  public AnagraficaStatoDao anagraficaStatoDao(Jdbi jdbi) {
    return jdbi.onDemand(AnagraficaStatoDao.class);
  }

  @Bean
  public UtenteDao utenteDao(Jdbi jdbi) {
    return jdbi.onDemand(UtenteDao.class);
  }

  @Bean
  public TipoFlussoDao tipoFlussoDao(Jdbi jdbi) {
    return jdbi.onDemand(TipoFlussoDao.class);
  }

  @Bean
  public OperatoreEnteTipoDovutoDao operatoreEnteTipoDovutoDao(Jdbi jdbi) {
    return jdbi.onDemand(OperatoreEnteTipoDovutoDao.class);
  }

  @Bean
  public PrenotazioneFlussoRiconciliazioneDao prenotazioneFlussoRiconciliazioneDao(Jdbi jdbi) {
    return jdbi.onDemand(PrenotazioneFlussoRiconciliazioneDao.class);
  }

  @Bean
  public InfoMappingTesoreriaDao infoMappingTesoreriaDao(Jdbi jdbi) {
    return jdbi.onDemand(InfoMappingTesoreriaDao.class);
  }

  @Bean
  public FlussoExportDao flussoExportDao(Jdbi jdbi) {
    return jdbi.onDemand(FlussoExportDao.class);
  }

  @Bean
  public FlussoRendicontazioneDao flussoRendicontazioneDao(Jdbi jdbi) {
    return jdbi.onDemand(FlussoRendicontazioneDao.class);
  }

  @Bean
  public FlussoTesoreriaDao flussoTesoreriaDao(Jdbi jdbi) {
    return jdbi.onDemand(FlussoTesoreriaDao.class);
  }

  @Bean
  public AccertamentoDao accertamentoDao(Jdbi jdbi) {
    return jdbi.onDemand(AccertamentoDao.class);
  }

  @Bean
  public AccertamentoDettaglioDao accertamentoDettaglioDao(Jdbi jdbi) {
    return jdbi.onDemand(AccertamentoDettaglioDao.class);
  }

  @Bean
  public ExportRendicontazioneCompletaDao exportRendicontazioneCompletaDao(Jdbi jdbi) {
    return jdbi.onDemand(ExportRendicontazioneCompletaDao.class);
  }

  @Bean
  public VmStatisticaEnteAnnoMeseGiornoDao vmStatisticaEnteAnnoMeseGiornoDao(Jdbi jdbi) {
    return jdbi.onDemand(VmStatisticaEnteAnnoMeseGiornoDao.class);
  }

  @Bean
  public VmStatisticaEnteAnnoMeseGiornoUffTdDao vmStatisticaEnteAnnoMeseGiornoUffTdDao(Jdbi jdbi) {
    return jdbi.onDemand(VmStatisticaEnteAnnoMeseGiornoUffTdDao.class);
  }

  @Bean
  public VmStatisticaEnteAnnoMeseGiornoUffTdCapDao vmStatisticaEnteAnnoMeseGiornoUffTdCapDao(Jdbi jdbi) {
    return jdbi.onDemand(VmStatisticaEnteAnnoMeseGiornoUffTdCapDao.class);
  }

  @Bean
  public VmStatisticaEnteAnnoMeseGiornoUffTdCapAccDao vmStatisticaEnteAnnoMeseGiornoUffTdCapAccDao(Jdbi jdbi) {
    return jdbi.onDemand(VmStatisticaEnteAnnoMeseGiornoUffTdCapAccDao.class);
  }

  @Bean
  public AnagraficaUffCapAccDao anagraficaUffCapAccDao(Jdbi jdbi) {
    return jdbi.onDemand(AnagraficaUffCapAccDao.class);
  }

  @Bean
  public OperatoreDao operatoreDao(Jdbi jdbi) {
    return jdbi.onDemand(OperatoreDao.class);
  }

  @Bean
  public RiconciliazioneDao riconciliazioneDao(Jdbi jdbi){
    return jdbi.onDemand(RiconciliazioneDao.class);
  }

  @Bean
  public SegnalazioneDao segnalazioneDao(Jdbi jdbi){
    return jdbi.onDemand(SegnalazioneDao.class);
  }
}