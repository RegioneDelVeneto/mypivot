package routines;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;


public class JobStringManage {

    /**
     * getValue: Ritorna null se stringa isBlank.
     * 
     * {param} string("world") input: The string need to return.
     * 
     * 
     */
    public static String getValue(String message) {    
    	if (StringUtils.isNotBlank(message)) 
			return message;
        return null;
    }
    
    public static String tagliaStr(String message, int width) {
		return StringUtils.abbreviate(message, width);
	}
    
    public static String getFormatStringToDouble(Double val){
		Locale loc = Locale.UK;
		NumberFormat nf = NumberFormat.getNumberInstance(loc);
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("###.##");
		return df.format(val);
    }
    
    public static String formatDecimal(Double number) { 
    	if (number != null) {
			return String.format("%15.2f", number).trim();
		}
    	return null;	
    }
    
    public static String retrieveVersionByFileName(String fileName) {
    	String version = null;
    	try{
    		String[] fileParts;
    		fileParts = fileName.split("-");
    		if(fileParts.length == 3) {
    			String parteFinale = fileParts[2];
    			if(parteFinale.equals("1_1.zip"))
    				version = "1.1";
    			if(parteFinale.equals("1_2.zip"))
    				version = "1.2";
    		}
       	}catch(Exception e){
    		version = "1.0";
    	}
    	
    	if(StringUtils.isBlank(version))
			version = "1.0";
    	return version;
    }
}
