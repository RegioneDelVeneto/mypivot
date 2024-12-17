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
package it.regioneveneto.mygov.payment.mypay4.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LogService {

  @Autowired
  private ConfigurableEnvironment env;

  @Autowired
  private BuildProperties buildProperties;

  private String[] privateProperties;

  public void printApplicationProperties(){

    if(log.isErrorEnabled()){
      log.error("Build properties");
      log.error("gitHash: {}", buildProperties.get("gitHash"));
      log.error("lastTag: {}", buildProperties.get("lastTag"));
      log.error("version: {}", buildProperties.get("version"));
      log.error("buildTime: {}", buildProperties.get("buildTime"));
    }

    if(log.isErrorEnabled())
      try {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        log.error("configured loggers: \n{}", lc.getLoggerList().stream()
            .filter(logger -> logger.getLevel() != null)
            .map(logger -> logger.getName() + " -> " + logger.getLevel())
            .collect(Collectors.joining("\n")));
        Set<String> appenders = new HashSet<>();
        lc.getLoggerList().forEach(logger -> {
          for (Iterator<Appender<ILoggingEvent>> it = logger.iteratorForAppenders(); it.hasNext(); ) {
            Appender<?> appender = it.next();
            if (FileAppender.class.isAssignableFrom(appender.getClass()))
              appenders.add(appender.getName() + "-" + appender.getClass().getName() + "-" + ((FileAppender<?>) appender).getFile());
            else
              appenders.add(appender.getName() + "-" + appender.getClass().getName());
          }
        });
        log.error("configured appenders: \n{}", String.join("\n", appenders));
      }catch(Exception e){
        log.error("error printing configured loggers", e);
      }

    if(log.isInfoEnabled())
      try {
        String forceLogPropertiesString = env.getProperty("properties.force-log", "");
        List<String> forceLogProperties = Arrays.asList(forceLogPropertiesString.split(","));
        String privatePropertiesString = env.getProperty("properties.hidden", "password,pwd,secret,prv");
        this.privateProperties = privatePropertiesString.toLowerCase().split(",");
        log.info("*** start listing application properties by source");
        for (PropertySource<?> propertySource : env.getPropertySources()) {
          if (propertySource instanceof EnumerablePropertySource) {
            String[] propertyNames = ((EnumerablePropertySource<?>) propertySource).getPropertyNames();
            Arrays.stream(propertyNames).sorted().forEach(propertyName -> this.printProperty("", env, propertySource, propertyName));
          } else {
            log.info("[{}] not enumerable: {}", propertySource.getName(), propertySource.getSource().getClass());
          }
          forceLogProperties.forEach(propertyName ->{
            if(propertySource.containsProperty(propertyName))
              this.printProperty("FORCED", env, propertySource, propertyName);
          });
        }
        log.info("*** end listing application properties by source");
      } catch (Exception e) {
        log.warn("error printing application properties", e);
      }

  }

  private void printProperty(String prefix, ConfigurableEnvironment env, PropertySource<?> propertySource, String propertyName){
    try {
      String resolvedProperty = env.getProperty(propertyName);
      String sourceProperty = String.valueOf(propertySource.getProperty(propertyName));
      if (StringUtils.containsAnyIgnoreCase(propertyName, this.privateProperties)) {
        resolvedProperty = StringUtils.equals(sourceProperty, resolvedProperty) ? "***hidden***" : "***overriden hidden***";
        sourceProperty = "***hidden***";
      }
      if (StringUtils.equals(sourceProperty, resolvedProperty)) {
        log.info("{} [{}] {}={}", prefix, propertySource.getName(), propertyName, resolvedProperty);
      } else {
        log.info("{} [{}] {}={} OVERRIDDEN to {}", prefix, propertySource.getName(), propertyName, sourceProperty, resolvedProperty);
      }
    } catch (Exception e){
      log.warn("error printing application property {} [{}] {}: {}", prefix, propertySource.getName(), propertyName, e.getMessage());
    }
  }
}
