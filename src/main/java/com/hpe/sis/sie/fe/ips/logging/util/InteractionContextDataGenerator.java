package com.hpe.sis.sie.fe.ips.logging.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InteractionContextDataGenerator {
	
	private final static Logger log = LoggerFactory
			.getLogger(InteractionContextDataGenerator.class);

	public String formatTDRLine(Map<String, String> tdrInterContextData,
			String tdrFieldKeys, String fieldDelimiterTDR) {

		if(log.isDebugEnabled())
			log.debug("[InteractionContextDataGenerator::formatTDRLine] Entry: TDRDATA : "+ tdrInterContextData );
		
		String fieldSeparator = ",";
        StringBuffer tdrString = new StringBuffer(128);

        if(fieldDelimiterTDR == null)
        	fieldDelimiterTDR = "|";
		try{

        if(tdrInterContextData == null) {
        	log.warn("[InteractionContextDataGenerator::formatTDRLine] TdrException Data is null in the HashMap");
        } else {
        	List<String> defaultTDRFields = new ArrayList<String>();
        	if (tdrFieldKeys != null) {
        		defaultTDRFields = Arrays.asList(tdrFieldKeys.split(fieldSeparator));
        	}
        	Iterator<String> tdrKeys = defaultTDRFields.iterator();
        	while (tdrKeys.hasNext()) {
        		String tdrKey = tdrKeys.next();
        		if(log.isDebugEnabled())
					log.debug("tdrKey in InteractionContextDataGenerator:"+ tdrKey);
        		String value = tdrInterContextData.get(tdrKey);
        		value = (value != null) ? value : "";

        		tdrString.append(value);
        		if (tdrKeys.hasNext()) {
        			tdrString.append(fieldDelimiterTDR);
        		}
        	}



        if(log.isDebugEnabled())
        	log.debug("[InteractionContextDataGenerator::formatTDRLine] returning " + tdrString);
		}
		}
		catch(Exception e){
			log.warn("[InteractionContextDataGenerator::formatTDRLine] Exception occured",e);
			if(log.isDebugEnabled())
			log.debug("[InteractionContextDataGenerator::formatTDRLine] Exception occured ",e);
		}
		if(log.isDebugEnabled())
		log.debug("[InteractionContextDataGenerator::formatTDRLine] Exit");
		return tdrString.toString();
	}

}
