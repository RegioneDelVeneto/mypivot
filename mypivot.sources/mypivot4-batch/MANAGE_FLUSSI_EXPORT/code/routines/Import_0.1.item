package routines;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;


public class Import {
	
	static boolean validateAgainstXSD(InputStream xml, InputStream xsd)
	{
	    try
	    {
	        SchemaFactory factory = 
	            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = factory.newSchema(new StreamSource(xsd));
	        Validator validator = schema.newValidator();
	        validator.validate(new StreamSource(xml));
	        return true;
	    }
	    catch(Exception ex)
	    {
	        return false;
	    }
	}
	
	public static void main(String[] args) throws Exception {
		InputStream xsd = new FileInputStream("d:/PagInf_Dovuti_Pagati_6_2_0.xsd");
		InputStream xml = new FileInputStream("d:/bilancio.xml");
		
		validateAgainstXSD(xml, xsd);
	}
}
