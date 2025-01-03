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

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
@ComponentScan(basePackages = "it.regioneveneto.mygov.payment")
@EnableCaching
@EnableTransactionManagement
@Slf4j
@ConditionalOnProperty(name=AbstractApplication.NAME_KEY, havingValue=WebApplication.NAME)
public class WebApplication extends AbstractApplication {

  public WebApplication(){
    log.debug("constructor WebApplication");
  }

  final static public String NAME = "WebApplication";
  public static void main(String[] args) {
    log.debug("starting main class WebApplication");
    System.setProperty(NAME_KEY, NAME);
    // allows to use %2F in path segments
    System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
    SpringApplication.run(WebApplication.class, args);
    log.info("started WebApplication");
  }

  @Bean
  public HttpFirewall httpFirewall() {
    log.info("setting custom settings on StrictHttpFirewall");
    StrictHttpFirewall firewall = new StrictHttpFirewall();
    //firewall.setAllowedHttpMethods(Arrays.asList("GET", "POST"));
    //firewall.setAllowSemicolon(true);
    firewall.setAllowUrlEncodedSlash(true);
    //firewall.setAllowBackSlash(true);
    //firewall.setAllowUrlEncodedPercent(true);
    //firewall.setAllowUrlEncodedPeriod(true);
    return firewall;
  }

  //HTTP port
  @Value("${server.http.port:0}")
  private int httpPort;

  // Let's configure additional connector to enable support for both HTTP and HTTPS
  @Bean
  public ServletWebServerFactory servletContainer() {
    if(httpPort>0) {
      TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
      Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
      connector.setPort(httpPort);
      tomcat.addAdditionalTomcatConnectors(connector);
      return tomcat;
    } else {
      return new TomcatServletWebServerFactory();
    }
  }

}