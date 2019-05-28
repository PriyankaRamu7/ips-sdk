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
 * Contents: IPSSecurityImpl.java
 * ---------------------------------------------------------------------------
 ******************************************************************************/
package com.hpe.sis.sie.fe.ips.auth.service;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.hpe.sis.sie.fe.ips.auth.constants.AuthConstants;
import com.hpe.sis.sie.fe.ips.auth.exception.AuthException;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthErrorResponse;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthRequest;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthSuccessResponse;
import com.hpe.sis.sie.fe.ips.auth.util.SecurityAuditUtil;
import com.hpe.sis.sie.fe.ips.common.ApplicationCAO;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.IPSConstants;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.processing.model.CallbackRequest;
import com.hpe.sis.sie.fe.ips.processing.model.Interaction;
import com.hpe.sis.sie.fe.ips.processing.model.SecurityAudit;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Activities;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.auth.model.AuthVO;
import com.hpe.sis.sie.fe.ips.utils.auth.model.OnDemandBots;
import com.hpe.sis.sie.fe.ips.utils.auth.model.VirtualObjects;
import com.hpe.sis.sie.fe.sisutils.channel.util.ChannelUtility;

/**
 * IPS Security default implementation to validate the interaction requests
 */
class IPSSecurityImpl implements IPSSecurity {

	private static final Logger logger = LoggerFactory.getLogger(IPSSecurityImpl.class);
	private static boolean isInfoEnabled = logger.isInfoEnabled();
	private static boolean isDebugEnabled = logger.isDebugEnabled();
	private static String className = IPSSecurityImpl.class.getCanonicalName();

	/* (non-Javadoc)
	 * @see com.hpe.sis.sie.fe.ips.auth.service.IPSSecurity#authenticateAndValidate(com.hpe.sis.sie.fe.ips.auth.service.AuthenticationRequest)
	 */
	@Override
	public Applications authenticateAndValidate(AuthenticationRequest authenticationRequest) throws AuthException {
		TraceContext traceContext = Tracer.start(authenticationRequest.getTransactionId(), "Authentication of interaction", this.className + ".authenticateAndValidate");
		if(isDebugEnabled)
			logger.debug("authenticateAndValidate : start:");
		String errorCode = null;
		String errorMsg = null;
		try {
		Applications applications = getApplicationData(authenticationRequest.getAppId());

		if(applications==null){
			throw new AuthException(AuthConstants.INVALID_APPID_ERR_CODE, IPSConfig.errorMsgProperties.getProperty(errorCode));
		}
		authenticationRequest.setAppOwnerRole(applications.getUserRole());
		authenticationRequest.setCreatedBy(applications.getCreatedBy());
		AuthRequest authRequest = populateAuthRequestObject(authenticationRequest,applications);
		Object response = null;
		String result = null;
		SisIpsActorSystem ipsSystem = SisIpsActorSystem.getInstance(); 
		Timeout timeout = new Timeout(Duration.create(authenticationRequest.getActorTimeOut(), "seconds"));
		Future<Object> future = Patterns.ask(ipsSystem.ipsSecurityActor, authRequest, timeout);
		try {
			response = (Object) Await.result(future, timeout.duration());
			if(isInfoEnabled)
			logger.info("AUTH RESULT" + response);
		} catch (Exception e) {
			logger.error("Error in getting response :"+e.getMessage());
			throw new AuthException(AuthConstants.SECURITY_ACTOR_ERROR, IPSConfig.errorMsgProperties.getProperty(errorCode) + e.getMessage()); // RequestTimeOur err? TODO
		}
		
		if(response instanceof AuthSuccessResponse){
			result = ((AuthSuccessResponse) response).getResponse();
		}
		else if(response instanceof AuthErrorResponse){
			result = ((AuthErrorResponse) response).getErrorCode();
		}
		
		if(null!=result) {
			if (!result.equalsIgnoreCase(AuthConstants.AUTH_SUCCESS)) {
				 errorMsg = getErrorMsg(result);
				 throw new AuthException(result, errorMsg);
			} else {
				Tracer.end(traceContext, "Authentication of Interaction", TraceConstants.SUCCESS);
				return applications;
			}
			
		 } else {
			logger.error("Error in getting response from Security Actor");
			throw new AuthException(AuthConstants.SECURITY_ACTOR_ERROR, IPSConfig.errorMsgProperties.getProperty(errorCode));
		 }
	
		} catch(AuthException ae) {
			errorCode = ae.getCode();
			errorMsg = ae.getMessage();
			traceContext.setErrorDetail(errorCode + "-" + errorMsg);
			Tracer.end(traceContext, "Authentication of Interaction", TraceConstants.FAILED);
			throw new AuthException(errorCode, errorMsg);
		} finally {
			logSecurityAudit(authenticationRequest, errorCode, errorMsg);
		}
	}


	private AuthRequest populateAuthRequestObject(
			AuthenticationRequest authenticationRequest, Applications applications) {
		
		AuthRequest authRequest = new AuthRequest();
		AuthVO authVO = new AuthVO();
		authRequest.setApplicationVO(applications);
		authVO.setApikey(authenticationRequest.getApikey());
		authVO.setAppId(authenticationRequest.getAppId());
		authVO.setAppOwnerRole(authenticationRequest.getAppOwnerRole());
		authVO.setChannelDeploymentId(authenticationRequest.getChannelDeploymentId());
		authVO.setConversationType(ChannelUtility.getConversationtype(authenticationRequest.getChannelName()));
		authVO.setOperation(authenticationRequest.getOperation());
		authVO.setPlatform(authenticationRequest.getPlatform());
		authVO.setRequestMadeFromConsole(authenticationRequest.isRequestMadeFromConsole());
		authVO.setSmartkey(authenticationRequest.getSmartkey());
		authVO.setVirtualObject(authenticationRequest.getVirtualObject());
		authVO.setBotId(authenticationRequest.getBotId());
		authVO.setRequestMadeFromConsole(authenticationRequest.isRequestMadeFromConsole());
		authVO.setActivityStreamId(authenticationRequest.getActivityStreamId());
		authVO.setRequestBody(authenticationRequest.getRequestBody());
		authVO.setChannelName(authenticationRequest.getChannelName());
		authVO.setAPIKeyAuthRequired(getApiKeyRequiredFlag(authenticationRequest,applications));
		authVO.setResourceUri(authenticationRequest.getResourceUri());
		authVO.setRequestedTime(authenticationRequest.getRequestedTime());
		authRequest.setAuthVO(authVO);
		authRequest.setTransactionId(authenticationRequest.getTransactionId());
		return authRequest;
	}



	/**
	 * Gets the api key required.
	 *
	 * @param authenticationRequest the authentication request
	 * @param applications the applications
	 * @return the api key required
	 */
	private boolean getApiKeyRequiredFlag(AuthenticationRequest authenticationRequest,
			Applications applications) {

		boolean isAPIKeyAuthRequired = false;

		if(authenticationRequest.getChannelName().equalsIgnoreCase(IPSConstants.BOT_ONDEMAND)){
			for (OnDemandBots onDemandBots : applications.getBots().getOnDemandBots()) {
				if (onDemandBots.getId().equals(authenticationRequest.getBotId())) {
					isAPIKeyAuthRequired = onDemandBots.isAPIKeyAuthRequired();
					break;
				}
			}
		}
		
		else if(Arrays.asList(IPSConstants.ACTIVITY_STREAM_CHANNEL_NAMES).contains(authenticationRequest.getChannelName())){

			List<VirtualObjects> VOList = applications.getVirtualObjects();
			for(VirtualObjects VO : VOList){
				if(VO.getId().equals(authenticationRequest.getVirtualObject())){
					List<Activities>  activitiesList =VO.getActivities();
					for(Activities activities : activitiesList){
						if(activities.getId().equals(authenticationRequest.getActivityStreamId())){
							isAPIKeyAuthRequired = activities.isAPIKeyAuthRequired();
							break;
						}
					}
				}
			}

		}
		
		else{
			List<VirtualObjects> VOList = applications.getVirtualObjects();
			for(VirtualObjects VO : VOList){
				if(VO.getId().equals(authenticationRequest.getVirtualObject())){
					isAPIKeyAuthRequired = VO.isAPIKeyAuthRequired();
					break;
				}
			}
		}

		return isAPIKeyAuthRequired;
	}


	/**
	 * Gets the error msg.
	 *
	 * @param result the result
	 * @return the error msg
	 */
	private String getErrorMsg(String result) {
		
		Properties props =IPSConfig.errorMsgProperties;
		String errorMsg =null;
		
		if(null!=props)
		errorMsg = props.getProperty(result);
		
		return errorMsg;
	}



	/* (non-Javadoc)
	 * @see com.hpe.sis.sie.fe.ips.auth.service.IPSSecurity#getApplicationData(java.lang.String)
	 */
	@Override
	public Applications getApplicationData(String appId) throws AuthException {
		return ApplicationCAO.getApplicationData(appId);
	}

	@Override
	public void logSecurityAudit(AuthenticationRequest authRequest, String errorCode , String errorMsg) {
		TraceContext traceContext = Tracer.start(authRequest.getTransactionId(), "Invoke Security audit logging actor", this.className + ".logSecurityAudit");
		if(IPSConfig.SECURITY_AUDIT_ENABLED) {
			SecurityAudit securityAudit = SecurityAuditUtil.populateSecurityAudit(authRequest, errorCode, errorMsg);
			SisIpsActorSystem.getInstance().securityAuditLoggingActor.tell(securityAudit, ActorRef.noSender());
		}
		Tracer.end(traceContext, "Invoke Security audit logging actor", TraceConstants.SUCCESS);
	}

}
