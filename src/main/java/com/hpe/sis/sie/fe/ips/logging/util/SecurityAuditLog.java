package com.hpe.sis.sie.fe.ips.logging.util;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.logging.constants.SecurityAuditConstants;
import com.hpe.sis.sie.fe.ips.processing.model.SecurityAudit;

public class SecurityAuditLog {

	private static final Logger log = LoggerFactory.getLogger(SecurityAuditLog.class);
	private static SecurityAuditDataGenerator dataGenerator = new SecurityAuditDataGenerator();

	public static void logSecurityAudit(SecurityAudit securityAudit) {

		log.info("Obtained SecurityAudit object for Security Audit logging :" + securityAudit);
		System.out.println("Obtained SecurityAudit object for Security Audit logging :" + securityAudit);
		persistInFile(securityAudit);
	}

	public static void persistInFile(SecurityAudit securityAudit) {
		Map<String, String> saData = new HashMap<>();

		saData.put(SecurityAuditConstants.TRANSACTIONID, securityAudit.getTransactionId());
		saData.put(SecurityAuditConstants.DATE, String.valueOf(securityAudit.getDate()));
		saData.put(SecurityAuditConstants.USERNAME, securityAudit.getUsername());
		saData.put(SecurityAuditConstants.LOCALHOST, securityAudit.getLocalHost());
		saData.put(SecurityAuditConstants.ROLE, securityAudit.getRole());
		saData.put(SecurityAuditConstants.CLIENTNODE, securityAudit.getClientNode());
		saData.put(SecurityAuditConstants.APIDETAILS, securityAudit.getApiAccessDetails());
		saData.put(SecurityAuditConstants.AUTHORIZATION, securityAudit.getAuthorization());
		saData.put(SecurityAuditConstants.ERRORCODE, securityAudit.getErrorCode());
		saData.put(SecurityAuditConstants.ERRORMESSAGE, securityAudit.getErrorMessage());

		String saLogCat = "SECURITY_AUDIT_LOG";

		Logger saLog = LoggerFactory.getLogger(saLogCat);

		String saFieldKeys = IPSConfig.SECURITY_AUDIT_FIELDS;

		log.info("SecurityAuditFieldKeys : " + saFieldKeys);
		
		String tdrLine = dataGenerator.formatSALine(saData, saFieldKeys, String.valueOf(IPSConfig.LOG_DELIMITER));
		
		try {
			log.debug("Logging now :" + tdrLine);
			
			saLog.info(tdrLine);
		} catch (Exception e) {
			log.error("Exception occured during Security Audit logging" + e.getStackTrace());
		}

	}

}
