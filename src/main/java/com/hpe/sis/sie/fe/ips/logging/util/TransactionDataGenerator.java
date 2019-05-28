/* ############################################################################
 * Copyright 2014 Hewlett-Packard Co. All Rights Reserved.
 * An unpublished and CONFIDENTIAL work. Reproduction,
 * adaptation, or translation without prior written permission
 * is prohibited except as allowed under the copyright laws.
 *-----------------------------------------------------------------------------
 * Project: SIS
 * Module:  TranactionLogging
 * Source: TransactionLogging
 * Author: HPE
 * Organization: HPE
 * Revision: 1.0
 * Date:
 * Contents: TransactionDataGenerator.java
 *-----------------------------------------------------------------------------
 */
package com.hpe.sis.sie.fe.ips.logging.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.ips.logging.constants.TransactionConstants;

public class TransactionDataGenerator {

	private final static Logger log = LoggerFactory
			.getLogger(TransactionDataGenerator.class);

/* This method will map the incoming transaction details and log in the format required only the configured field from the transactionLogging properties
 *
 */
	public String formatTDRLine(Map<String,String> tdrData, String tdrFieldKeys, String fieldDelimiterTDR) {
		if(log.isDebugEnabled())
		log.debug("[TransactionDataGenerator::formatTDRLine] Entry: TDRDATA : "+ tdrData );

		String fieldSeparator = ",";
        StringBuffer tdrString = new StringBuffer(128);

		try{

        if(tdrData == null) {
        	log.warn("[TransactionDataGenerator::formatTDRLine] TdrException Data is null in the HashMap");
        } else {
        	List<String> defaultTDRFields = new ArrayList<String>();
        	if (tdrFieldKeys != null) {
        		defaultTDRFields = Arrays.asList(tdrFieldKeys.split(fieldSeparator));
        	}
        	Iterator<String> tdrKeys = defaultTDRFields.iterator();
        	while (tdrKeys.hasNext()) {
        		String tdrKey = tdrKeys.next();
        		if(log.isDebugEnabled())
					log.debug("tdrKey :"+ tdrKey);
        		String value = tdrData.get(tdrKey);
        		value = (value != null) ? value : "";

        		if(tdrKey.equalsIgnoreCase(TransactionConstants.INTERACTION_API_ERROR_DETAIL)){
					if(log.isDebugEnabled())
					log.debug("[TransactionDataGenerator::formatTDRLine] Initial TDR reason for failure "+value);
					int index=value.indexOf("\n");
					if(log.isDebugEnabled())
					log.debug("[TransactionDataGenerator::formatTDRLine] Index value of '\n' is "+index);
					if(index>0){
						if(log.isDebugEnabled())
						log.debug("[TransactionDataGenerator::formatTDRLine] field has multiple lines");
						String valueStr=value.substring(0, index);
						if(log.isDebugEnabled())
						log.debug("[TransactionDataGenerator::formatTDRLine] TDR reason for failure after removing multiple lines"+valueStr);
						value = valueStr.replaceAll(fieldSeparator, "");
					}else{
						if(log.isDebugEnabled())
						log.debug("[TransactionDataGenerator::formatTDRLine] field has only a single line ");
						value = value.replaceAll(fieldSeparator, "");
					}
				}

        		tdrString.append(value);
        		if (tdrKeys.hasNext()) {
        			tdrString.append(fieldDelimiterTDR);
        		}
        	}

        if(log.isDebugEnabled())
        	log.debug("[TransactionDataGenerator::formatTDRLine] returning " + tdrString);
		}
		}
		catch(Exception e){
			log.warn("[TransactionDataGenerator::formatTDRLine] Exception occured",e);
			if(log.isDebugEnabled())
			log.debug("[TransactionDataGenerator::formatTDRLine] Exception occured ",e);
		}
		if(log.isDebugEnabled())
		log.debug("[TransactionDataGenerator::formatTDRLine] Exit");
		return tdrString.toString();
	}



}
