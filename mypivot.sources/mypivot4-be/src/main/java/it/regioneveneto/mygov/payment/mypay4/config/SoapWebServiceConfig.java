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

import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import it.regioneveneto.mygov.payment.mypay4.ws.util.MyWsdl11Definition;
import it.regioneveneto.mygov.payment.mypivot4.ws.server.PagamentiTelematiciPagatiRiconciliatiEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.support.WebUtils;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.transport.http.WsdlDefinitionHandlerAdapter;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.xml.xsd.XsdSchemaCollection;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EnableWs
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
public class SoapWebServiceConfig extends WsConfigurerAdapter {

  @Value("${ws.wsdl}")
  private String dynamicwsdl;

  @Value("${app.be.absolute-path}")
  private String appBeAbsolutePath;

  @Autowired
  private ResourceLoader resourceLoader;

  public final static String WS_PATH_MYPIVOT = MyPay4AbstractSecurityConfig.PATH_WS+"/pivot/";

  public final static String XSD_PagInf_RP_Esito_6_0_2 = "PagInf_RP_Esito_6_0_2";

  public final static String XSD_FlussoRiversamento_1_0_4 = "FlussoRiversamento_1_0_4";

  public static Set<String> WS_PATH_NAME_SET = new HashSet<>();


  public final static Map<String, String> XSD_NAME_PATH_MAP = Map.of(
      XSD_PagInf_RP_Esito_6_0_2, WS_PATH_MYPIVOT,
      XSD_FlussoRiversamento_1_0_4, WS_PATH_MYPIVOT
  );

  private void registerWsdlDefinition(String path){
    String contextRoot;
    try{
      contextRoot = new URI(appBeAbsolutePath).getPath().replaceAll("/$", "");
    } catch(Exception e){
      throw new MyPayException("invalid app.be.absolute-path ["+appBeAbsolutePath+"]", e);
    }
    log.debug("register ws soap: {}",contextRoot + path);
    WS_PATH_NAME_SET.add(contextRoot + path);
    log.trace("WS_PATH_NAME_SET contains now: {}",WS_PATH_NAME_SET);
  }

  private static String extractPathFromUrlPath(String urlPath) {
    int end = urlPath.indexOf('?');
    if (end == -1) {
      end = urlPath.indexOf('#');
      if (end == -1) {
        end = urlPath.length();
      }
    }
    int begin = urlPath.lastIndexOf('/', end) + 1;
    int paramIndex = urlPath.indexOf(';', begin);
    return urlPath.substring(0, begin);
  }

  @Bean
  public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
    MessageDispatcherServlet servlet = new MessageDispatcherServlet(){
      @Override
      protected WsdlDefinition getWsdlDefinition(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String name = WebUtils.extractFilenameFromUrlPath(uri);
        String path = extractPathFromUrlPath(uri);
        log.trace("getWsdlDefinition uri:{} path:{} name:{} found:{}", uri, name, path, WS_PATH_NAME_SET.contains(path+name));
        if(WS_PATH_NAME_SET.contains(path+name))
          return super.getWsdlDefinition(request);
        else
          return null;
      }

      @Override
      protected XsdSchema getXsdSchema(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String name = WebUtils.extractFilenameFromUrlPath(uri);
        String path = extractPathFromUrlPath(uri);
        String xsdPath = request.getContextPath()+XSD_NAME_PATH_MAP.getOrDefault(name,"__NOT_FOUND__");
        if(xsdPath.equals(path))
          return super.getXsdSchema(request);
        else
          return null;
      }
    };
    servlet.setApplicationContext(applicationContext);
    servlet.setTransformWsdlLocations(true);
    return new ServletRegistrationBean(servlet, MyPay4AbstractSecurityConfig.PATH_WS+"/*");
  }

  @Bean("wsdlDefinitionHandlerAdapter")
  public WsdlDefinitionHandlerAdapter getWsdlDefinitionHandlerAdapter(){
    return new WsdlDefinitionHandlerAdapter(){
      private WsdlDefinitionHandlerAdapter instance = this;
      @Override
      protected String transformLocation(String location, HttpServletRequest request) {
        //do not take url from request, because it may be changed by proxy / ingress. Use application property
        StringBuilder url = new StringBuilder(appBeAbsolutePath);
        if (location.startsWith("/")) {
          url.append(location);
          return url.toString();
        } else {
          log.error("wsdl url in location must start with / : [{}]", request.getRequestURL());
          return super.transformLocation(location, request);
        }
      }
    };
  }

  @Bean
  public XsdSchemaCollection getXsdSchemaCollection() {
    if("dynamic".equalsIgnoreCase(dynamicwsdl)) {
      final String XSD_PATH = "/xsd/";
      CommonsXsdSchemaCollection xsds = new CommonsXsdSchemaCollection(
          new ClassPathResource(XSD_PATH + "PagInf_RP_Esito_6_0_2.xsd"),
          new ClassPathResource(XSD_PATH + "FlussoRiversamento_1_0_4.xsd")
      );
      xsds.setInline(true);
      return xsds;
    } else {
      return null;
    }
  }

  @Bean(name = PagamentiTelematiciPagatiRiconciliatiEndpoint.NAME)
  public Wsdl11Definition pagamentiTelematiciEndpoint(XsdSchemaCollection xsdSchemaCollection) {
    registerWsdlDefinition(WS_PATH_MYPIVOT+PagamentiTelematiciPagatiRiconciliatiEndpoint.NAME);
    if("dynamic".equalsIgnoreCase(dynamicwsdl)) {
      MyWsdl11Definition def = new MyWsdl11Definition();
      def.setPortTypeName(PagamentiTelematiciPagatiRiconciliatiEndpoint.NAME + "Port");
      def.setServiceName(PagamentiTelematiciPagatiRiconciliatiEndpoint.NAME + "Service");
      def.setLocationUri(WS_PATH_MYPIVOT + PagamentiTelematiciPagatiRiconciliatiEndpoint.NAME);
      def.setRequestSuffix("");
      def.setResponseSuffix("Risposta");
      def.setTargetNamespace(PagamentiTelematiciPagatiRiconciliatiEndpoint.NAMESPACE_URI);
      def.setSchemaCollection(xsdSchemaCollection);
      return def;
    } else {
      return new SimpleWsdl11Definition(resourceLoader.getResource("classpath:wsdl/mypivot/mypivot-per-ente.wsdl"));
    }
  }

  @Bean(name = XSD_PagInf_RP_Esito_6_0_2)
  public XsdSchema getPagInf_RP_Esito_6_0_2Xsd() {
    return new SimpleXsdSchema(new ClassPathResource("wsdl/mypivot/"+XSD_PagInf_RP_Esito_6_0_2+".xsd"));
  }

  @Bean(name = XSD_FlussoRiversamento_1_0_4)
  public XsdSchema getFlussoRiversamento_1_0_4Xsd() {
    return new SimpleXsdSchema(new ClassPathResource("wsdl/mypivot/"+XSD_FlussoRiversamento_1_0_4+".xsd"));
  }


}


