package com.hpe.sis.sie.fe.ips.auth.util;

import com.hpe.sis.sie.fe.ips.auth.service.AuthenticationRequest;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.processing.model.SecurityAudit;

public class SecurityAuditUtil {

	public static SecurityAudit populateSecurityAudit(AuthenticationRequest authRequest, String errorCode , String errorMsg) {
		SecurityAudit securityAudit = new SecurityAudit();

		securityAudit.setTransactionId(authRequest.getTransactionId());
		securityAudit.setDate(authRequest.getRequestedTime());
		securityAudit.setUsername(authRequest.getCreatedBy());
		securityAudit.setLocalHost(IPSConfig.HOST_IP);
		securityAudit.setRole(authRequest.getAppOwnerRole());
		securityAudit.setClientNode(authRequest.getRemoteHost());
		securityAudit.setApiAccessDetails(authRequest.getRequestURI());
		securityAudit.setAuthorization(authRequest.getRequestMethod());
		securityAudit.setErrorCode(errorCode);
		securityAudit.setErrorMessage(errorMsg);
		return securityAudit;

	}
	
}
