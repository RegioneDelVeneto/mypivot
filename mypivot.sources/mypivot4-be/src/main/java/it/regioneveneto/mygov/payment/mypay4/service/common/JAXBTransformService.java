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
package it.regioneveneto.mygov.payment.mypay4.service.common;

import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.function.Function;

@Service
@Slf4j
public class JAXBTransformService {

  @Autowired
  private ResourceLoader resourceLoader;

  public <T> String marshalling(T element, Class<T> clazz) {
    return marshallingImpl(element, clazz, baos -> baos.toString(StandardCharsets.UTF_8));
  }

  public <T> byte[] marshallingAsBytes(T element, Class<T> clazz) {
    return marshallingImpl(element, clazz, ByteArrayOutputStream::toByteArray);
  }

  private <T, R> R marshallingImpl(T element, Class<T> clazz, Function<ByteArrayOutputStream, R> outConverterFun) {
    if(element == null)
      return null;
    try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {
      Marshaller marshaller = JAXBContext.newInstance(clazz).createMarshaller();
      marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false);
      marshaller.setProperty( Marshaller.JAXB_FRAGMENT, true);
      marshaller.marshal(element, baos);
      return outConverterFun.apply(baos);
    } catch ( JAXBException | IOException e ) {
      log.error("marshalling - Error due parsing", e);
      throw new MyPayException(e);
    }
  }

  public <T> String marshallingNoNamespace(T element, Class<T> clazz) {
    if(element == null)
      return null;
    try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {
      Marshaller marshaller = JAXBContext.newInstance(clazz).createMarshaller();
      marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false);
      marshaller.setProperty( Marshaller.JAXB_FRAGMENT, true);
      marshaller.marshal(element, NoNamesWriter.filter(new OutputStreamWriter(baos)));
      return baos.toString(StandardCharsets.UTF_8.name());
    } catch ( JAXBException | IOException | XMLStreamException e ) {
      log.error("marshalling - Error due parsing", e);
      throw new MyPayException(e);
    }
  }

  public <T> T unmarshalling(byte[] bytes, Class<T> clazz) {
    if(bytes == null || bytes.length==0)
      return null;
    try ( ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
      Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
      Source source = new StreamSource(bais);
      JAXBElement<T> element = unmarshaller.unmarshal(source, clazz);
      return element.getValue();
    } catch ( IOException | JAXBException e ) {
      log.error("marshalling - Error due parsing", e);
      throw new MyPayException(e);
    }
  }


  public <T> T unmarshallingBase64(byte[] bytes, Class<T> clazz) throws IOException, JAXBException {
    return unmarshallingBase64(bytes, clazz, null);
  }

  public <T> T unmarshallingBase64(byte[] bytes, Class<T> clazz, String xsdFile) throws IOException, JAXBException {
    if(bytes==null || bytes.length==0) {
      log.error("warning: unmarshallingBase64: bytes is null");
      return null;
    }

    if(Base64.isBase64(bytes)) {
      log.info("decode base64");
      bytes = Base64.decodeBase64(bytes);
    }

    try ( ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
      Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
      if(xsdFile!=null){
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(this.getResourcePath(xsdFile));
        unmarshaller.setSchema(schema);
      }
      //unmarshaller.setAdapter(base64Adapter);
      Source source = new StreamSource(bais);
      JAXBElement<T> element = unmarshaller.unmarshal(source, clazz);
      return element.getValue();
    } catch(SAXException se){
      throw new IOException(se);
    }
  }

  private URL getResourcePath(String resourcePath) throws IOException{
    if(resourcePath.startsWith("/"))
      resourcePath = "classpath:"+resourcePath.substring(1);
    return resourceLoader.getResource(resourcePath).getURL();
  }

}

class DelegatingXMLStreamWriter implements XMLStreamWriter {
  protected final XMLStreamWriter delegate;

  public DelegatingXMLStreamWriter(XMLStreamWriter del) {
    delegate = del;
  }

  public void close() throws XMLStreamException {
    delegate.close();
  }

  public void flush() throws XMLStreamException {
    delegate.flush();
  }

  public NamespaceContext getNamespaceContext() {
    return delegate.getNamespaceContext();
  }




  public String getPrefix(String uri) throws XMLStreamException {
    return delegate.getPrefix(uri);
  }

  public Object getProperty(String name) {
    return delegate.getProperty(name);
  }

  public void setDefaultNamespace(String uri) throws XMLStreamException {
    delegate.setDefaultNamespace(uri);
  }

  public void setNamespaceContext(NamespaceContext ctx) throws XMLStreamException {
    delegate.setNamespaceContext(ctx);
  }

  public void setPrefix(String pfx, String uri) throws XMLStreamException {
    delegate.setPrefix(pfx, uri);
  }

  public void writeAttribute(String prefix, String uri,
                             String local, String value) throws XMLStreamException {
    delegate.writeAttribute(prefix, uri, local, value);
  }

  public void writeAttribute(String uri, String local, String value) throws XMLStreamException {
    delegate.writeAttribute(uri, local, value);
  }

  public void writeAttribute(String local, String value) throws XMLStreamException {
    delegate.writeAttribute(local, value);
  }

  public void writeCData(String cdata) throws XMLStreamException {
    delegate.writeCData(cdata);
  }

  public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException {
    delegate.writeCharacters(arg0, arg1, arg2);
  }

  public void writeCharacters(String text) throws XMLStreamException {
    delegate.writeCharacters(text);
  }

  public void writeComment(String text) throws XMLStreamException {
    delegate.writeComment(text);
  }

  public void writeDefaultNamespace(String uri) throws XMLStreamException {
    delegate.writeDefaultNamespace(uri);
  }

  public void writeDTD(String dtd) throws XMLStreamException {
    delegate.writeDTD(dtd);
  }

  public void writeEmptyElement(String prefix, String local, String uri) throws XMLStreamException {
    delegate.writeEmptyElement(prefix, local, uri);
  }

  public void writeEmptyElement(String uri, String local) throws XMLStreamException {
    delegate.writeEmptyElement(uri, local);
  }

  public void writeEmptyElement(String localName) throws XMLStreamException {
    delegate.writeEmptyElement(localName);
  }

  public void writeEndDocument() throws XMLStreamException {
    delegate.writeEndDocument();
  }

  public void writeEndElement() throws XMLStreamException {
    delegate.writeEndElement();
  }

  public void writeEntityRef(String ent) throws XMLStreamException {
    delegate.writeEntityRef(ent);
  }

  public void writeNamespace(String prefix, String uri) throws XMLStreamException {
    delegate.writeNamespace(prefix, uri);
  }

  public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
    delegate.writeProcessingInstruction(target, data);
  }

  public void writeProcessingInstruction(String target) throws XMLStreamException {
    delegate.writeProcessingInstruction(target);
  }

  public void writeStartDocument() throws XMLStreamException {
    delegate.writeStartDocument();
  }

  public void writeStartDocument(String encoding, String ver) throws XMLStreamException {
    delegate.writeStartDocument(encoding, ver);
  }

  public void writeStartDocument(String ver) throws XMLStreamException {
    delegate.writeStartDocument(ver);
  }

  public void writeStartElement(String prefix, String local, String uri) throws XMLStreamException {
    delegate.writeStartElement(prefix, local, uri);
  }

  public void writeStartElement(String uri, String local) throws XMLStreamException {
    delegate.writeStartElement(uri, local);
  }

  public void writeStartElement(String local) throws XMLStreamException {
    delegate.writeStartElement(local);
  }

}

class NoNamesWriter extends DelegatingXMLStreamWriter {

  private static final NamespaceContext emptyNamespaceContext = new NamespaceContext() {

    @Override
    public String getNamespaceURI(String prefix) {
      return "";
    }

    @Override
    public String getPrefix(String namespaceURI) {
      return "";
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
      return null;
    }

  };

  public static XMLStreamWriter filter(Writer writer) throws XMLStreamException {
    return new NoNamesWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(writer));
  }

  public NoNamesWriter(XMLStreamWriter writer) {
    super(writer);
  }

  @Override
  public NamespaceContext getNamespaceContext() {
    return emptyNamespaceContext;
  }

}
