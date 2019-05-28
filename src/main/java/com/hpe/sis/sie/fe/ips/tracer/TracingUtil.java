/* ############################################################################
 * Copyright 2017 Hewlett-Packard Co. All Rights Reserved.
 * An unpublished and CONFIDENTIAL work. Reproduction,
 * adaptation, or translation without prior written permission
 * is prohibited except as allowed under the copyright laws.
 *-----------------------------------------------------------------------------
 * Project: SIS
 * Module:  TranactionLogging
 * Source: TransactionLogging
 * Author: HPE
 * Organization: HPE
 * Revision: 1.2
 * Date:
 * Contents: TracingLog.java
 *-----------------------------------------------------------------------------
 */

package com.hpe.sis.sie.fe.ips.tracer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.ips.common.IPSConfig;

public class TracingUtil {

	private final static Logger log = LoggerFactory.getLogger(TracingUtil.class);
	private static final String traceLogCategory = "TRACING_LOG";
	private static List<String> traceFieldKeys;
	
	public static void initialize() {
		if (IPSConfig.TRACE_FIELDS != null) {
			log.info("TRACE FIELD KEYS: " + IPSConfig.TRACE_FIELDS);
			traceFieldKeys = Arrays.asList(IPSConfig.TRACE_FIELDS.split(","));
		}
	}

	public static void logTracing(TraceMessage traceMessage) {
		Logger traceLogger = LoggerFactory.getLogger(traceLogCategory);

		try {
			traceLogger.info(generateTraceEntry(traceMessage));
		} catch (Exception e) {
			log.error("BadRequestException occured :" + e.getStackTrace());
		}
	}

	private static String generateTraceEntry(TraceMessage traceMessage) {
		StringBuffer traceEntry = new StringBuffer();
		if (traceMessage == null) {
			log.warn("Something wrong.. Tracing is enabled, but the TraceContext data is empty");
		} else {
			if (traceFieldKeys != null) {
				Iterator<String> iterator = traceFieldKeys.iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					String value = "";
					if (StringUtils.equals(key, TraceConstants.TRANSACTION_ID)) {
						value = traceMessage.getTransactionId();
					} else if (StringUtils.equals(key, TraceConstants.EVENT)) {
						value = traceMessage.getEvent();
					} else if (StringUtils.equals(key, TraceConstants.EVENT_TIME)) {
						value = traceMessage.getTimeStamp();
					} else if (StringUtils.equals(key, TraceConstants.METHOD)) {
						value = traceMessage.getClassAndMethod();
					} else if (StringUtils.equals(key, TraceConstants.ACTOR_PATH)) {
						value = traceMessage.getActorPath();
					}  else if (StringUtils.equals(key, TraceConstants.STATUS)) {
						value = traceMessage.getStatus();
					} else if (StringUtils.equals(key, TraceConstants.ERROR_DETAIL)) {
						value = traceMessage.getErrorDetail();
					} else {
						value = traceMessage.getField(key);
					}
					
					if (StringUtils.isNotEmpty(value)) {
						traceEntry.append(value.trim());
					}
					if (iterator.hasNext()) {
						traceEntry.append(IPSConfig.LOG_DELIMITER);
					}
				}
			}
		}
		return traceEntry.toString();
	}

}
