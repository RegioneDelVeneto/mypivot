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
package it.regioneveneto.mygov.payment.mypivot4;

import it.regioneveneto.mygov.payment.mypay4.logging.LogService;
import it.regioneveneto.mygov.payment.mypay4.service.common.CacheService;
import it.regioneveneto.mygov.payment.mypivot4.service.EnteService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationStartupService {

  @Autowired
  private ConfigurableEnvironment env;

  @Autowired
  private BuildProperties buildProperties;

  @Autowired
  private EnteService enteService;

  @Autowired
  private LogService logService;

  @Autowired
  private CacheService cacheService;

  @Getter
  private long applicationReadyTimestamp;

  @EventListener
  @Order(0)
  public void onApplicationEvent(ApplicationReadyEvent event) {
    log.info("execute onApplicationReadyEvent");

    this.applicationReadyTimestamp = System.currentTimeMillis();

    //print application properties
    nonBlockingOperation( logService::printApplicationProperties );

    //cache flush
    nonBlockingOperation( cacheService::cacheFlush );

  }

  @EventListener
  @Order(1)
  @ConditionalOnWebApplication
  public void onApplicationEventWebapp(ApplicationReadyEvent event) {
    log.info("execute onApplicationReadyEvent for WebApplication");

    //initialize ente cache (to improve first user requests)
    nonBlockingOperation( () -> enteService.getAllEnti() );
  }

  private void nonBlockingOperation(Runnable r){
    try{
      r.run();
    }catch(Exception e){
      log.error("error executing non-blocking startup operation, ignoring it", e);
    }
  }

}
