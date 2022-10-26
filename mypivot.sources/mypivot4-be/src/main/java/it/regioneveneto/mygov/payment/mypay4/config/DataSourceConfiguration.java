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

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class DataSourceConfiguration {

  @Value("${spring.datasource.pivot.minimumIdle:-1}")
  private int dataSourcePivotMinimumIdle;
  @Value("${spring.datasource.pivot.maximumPoolSize:-1}")
  private int dataSourcePivotMaximumPoolSize;

  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource.pivot")
  public DataSourceProperties pivotDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name="dsPivot")
  @Primary
  @ConfigurationProperties("spring.datasource.pivot.configuration")
  public DataSource pivotDataSource() {
    HikariDataSource ds = pivotDataSourceProperties().initializeDataSourceBuilder()
        .type(HikariDataSource.class).build();
    ds.setMinimumIdle(dataSourcePivotMinimumIdle);
    ds.setMaximumPoolSize(dataSourcePivotMaximumPoolSize);
    ds.setAutoCommit(false);
    log.info("creating data source [pivot] with minimumIdle:"+ds.getMinimumIdle()+" maximumPoolSize:"+ds.getMaximumPoolSize());
    return ds;
  }


  @Bean(name="tmPivot")
  @Autowired
  @Primary
  DataSourceTransactionManager pivotTransactionManager(@Qualifier ("dsPivot") DataSource datasource) {
    return new DataSourceTransactionManager(datasource);
  }
}
