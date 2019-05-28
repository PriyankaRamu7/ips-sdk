/*******************************************************************************
 * © Copyright 2017 Hewlett Packard Enterprise Development LP. All Rights Reserved.
 * An unpublished and CONFIDENTIAL work. Reproduction,
 * adaptation, or translation without prior written permission
 * is prohibited except as allowed under the copyright laws.
 * ---------------------------------------------------------------------------
 * Project: SISv1.3
 * Module: SIS IPS
 * Author: HPE SIS Team
 * Organization: Hewlett Packard Enterprise
 * Revision: 1.0
 * Date: 20/07/2017
 * Contents: IPSSecurity.java
 * ---------------------------------------------------------------------------
 ******************************************************************************/
package com.hpe.sis.sie.fe.ips.auth.service;

import com.hpe.sis.sie.fe.ips.auth.exception.AuthException;
import com.hpe.sis.sie.fe.ips.processing.model.CallbackRequest;
import com.hpe.sis.sie.fe.ips.processing.model.Interaction;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;


/**
 * IPSSecurity API for validating, authenticating and authorizing interaction requests
 *
 */
public interface IPSSecurity {

	/**
	 * Validates the interaction requests, checks for authentication and authorization
	 * @param authRequest
	 * @return Applications if all validations, authentication(based on APIKey) and authorization(based on SmartKey) are successful
	 * @throws AuthException in case of validation and auth failures
	 */
	public Applications authenticateAndValidate(AuthenticationRequest authRequest) throws AuthException;
	
	
	/**
	 * Fetches application meta-data
	 * @param appId
	 * @return Applications
	 * @throws AuthException
	 */
	public Applications getApplicationData(String appId) throws AuthException ;
	
	public void logSecurityAudit(AuthenticationRequest authRequest, String errorCode, String errorMsg);
	
	/**
	 * Builder method for instantiating IPSSecurity
	 */
	public static IPSSecurity build() {
		return new IPSSecurityImpl();
	}
}
