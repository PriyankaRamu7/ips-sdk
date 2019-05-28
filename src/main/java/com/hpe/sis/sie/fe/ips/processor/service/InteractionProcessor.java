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
 * Contents: InteractionProcessor.java
 * ---------------------------------------------------------------------------
 ******************************************************************************/
package com.hpe.sis.sie.fe.ips.processor.service;

import java.util.List;

import com.hpe.sis.sie.fe.dss.exception.SISException;
import com.hpe.sis.sie.fe.ips.auth.exception.AuthException;
import com.hpe.sis.sie.fe.ips.auth.exception.IPSException;
import com.hpe.sis.sie.fe.ips.auth.service.AuthenticationRequest;
import com.hpe.sis.sie.fe.ips.processing.model.CallbackRequest;
import com.hpe.sis.sie.fe.ips.processing.model.Interaction;
import com.hpe.sis.sie.fe.ips.processing.model.InteractionResult;

/**
 * Main API for processing interaction requests
 * Internally handles IPS functions like response caching, 
 * request/response filtering, transformations, SIE: BE task invocations, 
 * transaction logging, tracing, interaction-context logging
 */
public interface InteractionProcessor {

	/**
	 * Processes the interaction request, invokes the SIE: BE service using the specified processing pattern
	 * Internally handles other IPS functions like response caching, request/response filtering, transformations, SIE: BE task invocations, 
	 * transaction logging, tracing, interaction-context logging
	 * @param interactionVO - consisting of data from the in-bound interaction request (channel, message body, headers, BEservice)
	 * @return InteractionVO - updated with response headers and message body for the interaction request
	 * @throws IPSException
	 */
	public InteractionResult process(Interaction interaction) throws IPSException;
	
	/**
	 * Process the interaction request that contains a list of homogeneous items in parallel, using multiple actors
	 * Consolidates the responses' message body
	 * Internally handles other IPS functions like response caching, request/response filtering, transformations, SIE: BE task invocations, 
	 * transaction logging, tracing, interaction-context logging
	 * @param interactionVO - consisting of data from the in-bound interaction request (channel, message body, headers, BEservice)
	 * @return InteractionVO - updated with response headers and consolidated message body for the interaction request
	 */
	public InteractionResult processParallelDistributed(Interaction interaction) throws IPSException;
	
	public List<InteractionResult> processParallelDistributed(Interaction interaction, List<String> messages) throws IPSException;
	
	/**
	 * @param key
	 * @param interactionContextId
	 * @param pramList
	 * @param responseBody
	 * @return
	 * @throws SISException
	 * @throws Throwable
	 */
	public String retrieveInteractionResponse(CallbackRequest asyncInteractionResult) throws IPSException;
	
	/**
	 * logs a entry in the Channel's transaction.log
	 * This API should be used in case of Synchronous processing, after successful service invocation.
	 * @param interaction data object
	 * 
	 */
	public void logTransaction(Interaction interaction, InteractionResult interactionResult);
	
	/**
	 * logs a entry in the Channel's transaction.log
	 * Transaction logging will be done internally by IPS, while processing the Interaction requests
	 * This API should be used in case of errors, after the process/processParallelDistributed APIs are used
	 * @param interaction data object
	 * @param ipsException
	 * 
	 */
	public void logTransaction(Interaction interaction, IPSException ipsException);
	
	
	/**
	 * logs a entry in the Channel's transaction.log
	 * Transaction logging will be done internally
	 * This API should be used in case of errors, in retrieving call back api
	 *
	 * @param callbackRequest the callback request
	 * @param ipsException the ips exception
	 */
	public void logTransaction(CallbackRequest callbackRequest, IPSException ipsException);
	
	
	/**
	 * logs a entry in the Channel's transaction.log
	 * This API should be used in case of errors, while performing authentication and authorization checks
	 * @param authRequest data object
	 * @param authException
	 * 
	 */
	public void logTransaction(AuthenticationRequest authRequest, AuthException authException);
	
	/**
	 * logs a entry in the Channel's interactionContext.log and also saves interaction report in DSS
	 * This API should be used before interaction request processing
	 * @param Interaction data object
	 */
	public void logInteractionContext(Interaction interaction);
	
	/**
	 * Builder method for instantiating InteractionProcessor
	 */
	public static InteractionProcessor build() {
		return new InteractionProcessorImpl();
	}

}
