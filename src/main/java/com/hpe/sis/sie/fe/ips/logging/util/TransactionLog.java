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
 * Contents: TranactionLog.java
 *-----------------------------------------------------------------------------
 */

package com.hpe.sis.sie.fe.ips.logging.util;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.logging.constants.TransactionConstants;
import com.hpe.sis.sie.fe.ips.processing.model.Transaction;

/**
 * The transaction Logging will be log the transaction details including
 * request, response, responseTime, error code, error reason, transactionId,
 * localhost and remotehost etc
 *
 */
public class TransactionLog {

	private final static Logger log = LoggerFactory.getLogger(TransactionLog.class);
	private static TransactionDataGenerator dataGenerator = new TransactionDataGenerator();

	public static void logTransaction(Transaction transaction) {

		log.info("Obtained Transaction object for transaction logging :" + transaction);
		persistInFile(transaction);
	}

	public static void persistInFile(Transaction transMessage) {
		Map<String, String> tdrData = new HashMap<>();

		tdrData.put(TransactionConstants.TRANSACTIONID, transMessage.getTransactionId());
		tdrData.put(TransactionConstants.REQUESTEDTIME, String.valueOf(transMessage.getRequestedTime()));
		tdrData.put(TransactionConstants.LOCALHOST, transMessage.getLocalHost());
		tdrData.put(TransactionConstants.REMOTEHOST, transMessage.getRemoteHost());
		tdrData.put(TransactionConstants.USER_NAME, transMessage.getUserName());
		tdrData.put(TransactionConstants.APPLICATION, transMessage.getApplication());
		tdrData.put(TransactionConstants.VIRTUAL_OBJECTS, transMessage.getVirtualObjects());
		tdrData.put(TransactionConstants.SERVICE_ID, transMessage.getServiceId());
		tdrData.put(TransactionConstants.BOT_ID, transMessage.getBotId());
		tdrData.put(TransactionConstants.INTERACTION_CHANNEL, transMessage.getInteractionChannel());
		tdrData.put(TransactionConstants.INTERACTION_API_RESULTCODE,
				String.valueOf(transMessage.getInteractionApiResultCode()));
		tdrData.put(TransactionConstants.INTERACTION_API_ERROR_DETAIL, transMessage.getInteractionApiErrDetail());
		tdrData.put(TransactionConstants.BACKEND_INVOCATION_STATUS, transMessage.getBackEndInvocationStatus());
		tdrData.put(TransactionConstants.INTERACTION_API_RESPONSETIME,
				String.valueOf(transMessage.getInteractionApiResponseTime()));
		tdrData.put(TransactionConstants.BACKEND_INVOCATION_TASKID, transMessage.getBackEndInvocationTaskId());
		tdrData.put(TransactionConstants.BACKEND_RESPONSETIME, String.valueOf(transMessage.getBackEndResponseTime()));
		if (transMessage.getInteractionApiRequest() != null) {
			String apiRequest = new String(transMessage.getInteractionApiRequest());
			tdrData.put(TransactionConstants.INTERACTION_API_REQUEST, apiRequest);
		} else
			tdrData.put(TransactionConstants.INTERACTION_API_REQUEST, "");

		if (transMessage.getBackEndResponse() != null) {
			String backendResponse = new String(transMessage.getBackEndResponse());
			tdrData.put(TransactionConstants.BACKEND_RESPONSE, backendResponse);
		} else
			tdrData.put(TransactionConstants.BACKEND_RESPONSE, "");

		tdrData.put(TransactionConstants.INTERACTION_API_METHODTYPE, transMessage.getInteractionApiMethodType());
		tdrData.put(TransactionConstants.IS_TEST_REQUEST, String.valueOf(transMessage.isTestRequest()));
		tdrData.put(TransactionConstants.OBE_ID, transMessage.getObeId());
		tdrData.put(TransactionConstants.XAPI4SAASTOKEN, transMessage.getX_API4SAAS_TOKEN());
		tdrData.put(TransactionConstants.IS_EXCEPTION_FLOW_EXECUTED, String.valueOf(transMessage.isExceptionFlowExecuted()));

		String tdrLogCat = "TRANSACTION_LOG";

		Logger tdrLog = LoggerFactory.getLogger(tdrLogCat);

		String tdrFieldKeys = IPSConfig.TRANSACTION_FIELDS;

		log.info("TdrFieldKeys : " + tdrFieldKeys);
		String tdrLine = dataGenerator.formatTDRLine(tdrData, tdrFieldKeys, String.valueOf(IPSConfig.LOG_DELIMITER));
		try {
			log.debug("Logging now :" + tdrLine);
			tdrLog.info(tdrLine);
		} catch (Exception e) {
			log.error("Exception occured during transaction logging :" + e.getStackTrace());
		}

	}

}
