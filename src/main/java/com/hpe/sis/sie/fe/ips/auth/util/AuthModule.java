package com.hpe.sis.sie.fe.ips.auth.util;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hpe.sis.sie.fe.ips.auth.constants.AuthConstants;
import com.hpe.sis.sie.fe.ips.auth.exception.AuthException;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthRequest;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.IPSConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;
import com.hpe.sis.sie.fe.ips.utils.activitystreams.model.core.Activity;
import com.hpe.sis.sie.fe.ips.utils.activitystreams.util.ActivityStreamValidator;
import com.hpe.sis.sie.fe.ips.utils.auth.model.ApiKey;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Apis;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.auth.model.AuthVO;
import com.hpe.sis.sie.fe.ips.utils.auth.model.ConfigurationVO;
import com.hpe.sis.sie.fe.ips.utils.auth.model.OnDemandBots;
import com.hpe.sis.sie.fe.ips.utils.auth.model.SmartKey;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Validity;
import com.hpe.sis.sie.fe.ips.utils.auth.model.VirtualObjects;

public class AuthModule {

	private final static Logger log = LoggerFactory.getLogger(AuthModule.class);
	private static boolean isInfoEnabled = log.isInfoEnabled();
	private static boolean isDebugEnabled = log.isDebugEnabled();
	public static final Gson gson = new Gson();
	// private static String className = AuthModule.class.getCanonicalName();

	public static String authenticationCheck(String apiKey, List<ApiKey> appApikeyList, boolean isAPIKeyAuthRequired,
			String appId, String transactionId) {
		TraceContext traceContext = Tracer.start(transactionId, "APIKey check", "AuthModule.authenticationCheck");
		log.info("Performing Authentication check");
		boolean authenticationResult = false;
		ApiKey apiKeyObj = null;
		String errorCode = null;

		if (appId == null || appId.isEmpty()) {
			log.info("Invalid APPId " + appId);
			errorCode = AuthConstants.INVALID_APPID_ERR_CODE;
			
		} else if (isAPIKeyAuthRequired) {

			if (appApikeyList == null || appApikeyList.size() <= 0) {
				log.info("APIKey authentication is required for this Application, but APIKey not provisioned");
				errorCode = AuthConstants.APIKEY_NOT_CREATED;

			} else if (apiKey == null || apiKey.isEmpty()) {
				log.info("Invalid APIKey " + apiKey);
				errorCode = AuthConstants.MISSING_APIKEY_IN_REQUEST;

			} else {
				for (ApiKey apiKeyBO : appApikeyList) {
					String appApiKey = apiKeyBO.getId();
					if (appApiKey.equals(apiKey)) {
						authenticationResult = true;
						apiKeyObj = apiKeyBO;
						// appOwnerRole = apiKeyBO.getUserRole(); //TODO
						log.debug("AuthorizationModule : validateAuth]ApiKey Matched ");
						break;
					}
				}
				if (!authenticationResult) {
					log.info("AuthorizationModule : validateAuth] Authentication failed");
					errorCode = AuthConstants.APIKEY_MISMATCH_ERR_CODE;
				} else if(apiKeyObj.getExpiryTime() < Instant.now().toEpochMilli()) {
					log.info("AuthorizationModule : validateAuth] Authentication failed");
					errorCode = AuthConstants.APIKEY_MISMATCH_ERR_CODE;
				}
			}
		}
		if (errorCode != null) {
			traceContext.setErrorDetail(errorCode + "-" + IPSConfig.errorMsgProperties.getProperty(errorCode));
			Tracer.end(traceContext, "APIKey check", TraceConstants.FAILED );
		} else {
			Tracer.end(traceContext, "APIKey check", TraceConstants.SUCCESS);
		}
		return errorCode;
	}

	public static String authorizationCheck(AuthRequest authRequest) {
		String errorCode = null;
		AuthVO authVO = authRequest.getAuthVO();
		
		Applications applicationVO = authRequest.getApplicationVO();
		
		TraceContext traceContext = Tracer.start(authRequest.getTransactionId(),  "Tenant Deployment ID check", "AuthModule.isDeploymentIdSame");
		errorCode = isDeploymentIdSame(applicationVO.getDeployId(), authVO.getChannelDeploymentId());
		if (errorCode != null) {
			traceContext.setErrorDetail(errorCode + "-" + IPSConfig.errorMsgProperties.getProperty(errorCode));
			Tracer.end(traceContext, "Tenant Deployment ID check", TraceConstants.FAILED);
			return errorCode;
		}
		Tracer.end(traceContext, "Tenant Deployment ID check", TraceConstants.SUCCESS);
		
		traceContext = Tracer.start(authRequest.getTransactionId(),  "Application Status check", "AuthModule.authorizationCheck");
		errorCode = isApplicationSuspended(applicationVO.isSuspended());
		if (errorCode != null) {
			traceContext.setErrorDetail(errorCode + "-" + IPSConfig.errorMsgProperties.getProperty(errorCode));
			Tracer.end(traceContext, "Application Status check", TraceConstants.FAILED);
			return errorCode;
		}
		
		errorCode = isApplicationMarkedAsCompleted(applicationVO.isComplete());
		if (errorCode != null) {
			traceContext.setErrorDetail(errorCode + "-" + IPSConfig.errorMsgProperties.getProperty(errorCode));
			Tracer.end(traceContext, "Application Status check", TraceConstants.FAILED);
			return errorCode;
		}
		Tracer.end(traceContext, "Application Status check", TraceConstants.SUCCESS);
		
		traceContext = Tracer.start(authRequest.getTransactionId(),  "Application Validity check", "AuthModule.checkApplicationValidity");
		errorCode = checkApplicationValidity(applicationVO.getValidity());
		if (errorCode != null) {
			traceContext.setErrorDetail(errorCode + "-" + IPSConfig.errorMsgProperties.getProperty(errorCode));
			Tracer.end(traceContext, "Application Validity check", TraceConstants.FAILED);
			return errorCode;
		}
		Tracer.end(traceContext, "Application Validity check", TraceConstants.SUCCESS);
		
		traceContext = Tracer.start(authRequest.getTransactionId(),  "Smart Key check", "AuthModule.checkAccess");
		errorCode = checkAccess(applicationVO, authVO);
		if (errorCode != null) {
			traceContext.setErrorDetail(errorCode + "-" + IPSConfig.errorMsgProperties.getProperty(errorCode));
			Tracer.end(traceContext, "Smart Key check", TraceConstants.FAILED);
			return errorCode;
		}
		Tracer.end(traceContext, "Smart Key check", TraceConstants.SUCCESS);
		
		return errorCode;
	}

	public static String checkApplicationValidity(Validity validity) {

		boolean isAppExpired = isDateBetweenTwoDate(validity.getStartTime(), validity.getEndTime(), new Date());

		if (!isAppExpired) {
			log.debug("AuthorizationModule : validateAuth]Not allowed in this time");
			return AuthConstants.APPLICATION_VALIDITY_ERROR_CODE;
		} else {
			return null;
		}
	}

	public static String checkAccess(Applications applicationVO, AuthVO authVO) {

		if (isDebugEnabled)
			log.debug("Performing SmartKey validation");
		String errorCode = null;
		List<String> voSmartKeys = new ArrayList<String>();
		if(authVO.getChannelName().equalsIgnoreCase(IPSConstants.BOT_ONDEMAND)){
		
			OnDemandBots onDemandBots =getOndemandBot(authVO.getBotId(),applicationVO.getBots().getOnDemandBots());
			if(null!=onDemandBots){
			    voSmartKeys = onDemandBots.getSmartKeys();
			}
			else{
				return AuthConstants.BOTID_NOT_FOUND;
			}
		}
			
		else{
			VirtualObjects virtualObjects = AuthModule.getVirtualObject(authVO.getVirtualObject(),
					applicationVO.getVirtualObjects());
			if (virtualObjects != null) {
				voSmartKeys = virtualObjects.getSmartKeys();
			} else {
				return AuthConstants.VOID_NOT_FOUND;
			}
		}
			
			if (voSmartKeys != null && voSmartKeys.size() >= 1) {
				
				if (authVO.getConversationType() == null || authVO.getConversationType().isEmpty())
					return AuthConstants.INVALID_CONVERSATION_TYPE_ERR_CODE;

				if (authVO.getOperation() == null || authVO.getOperation().isEmpty())
					return AuthConstants.INVALID_METHOD_TYPE_ERR_CODE;

				if (authVO.getPlatform() == null || authVO.getPlatform().isEmpty())
					return AuthConstants.INVALID_PLATFORM_TYPE_ERR_CODE;

				if(authVO.getChannelName().equalsIgnoreCase(IPSConstants.BOT_ONDEMAND)){
				if (authVO.getBotId() == null || authVO.getBotId().isEmpty())
					return AuthConstants.INVALID_BOT_ID_ERR_CODE;
				}
				else{
				if (authVO.getVirtualObject() == null || authVO.getVirtualObject().isEmpty())
					return AuthConstants.INVALID_VirtualObject_ERR_CODE;
				}

				if (authVO.getSmartkey() == null || authVO.getSmartkey().isEmpty())
					return AuthConstants.INVALID_SMARTKEY_ERR_CODE;
			}
		errorCode = validateAuth(applicationVO.getObeId(), applicationVO.getApiKeys(), applicationVO.getSmartKeys(),
				voSmartKeys, authVO);

		return errorCode;
	}

	private static OnDemandBots getOndemandBot(String botId, OnDemandBots[] onDemandBots) {

		OnDemandBots onDemandBotsObj = null;
			for (OnDemandBots onDemandBotsVO : onDemandBots) {
				if (botId.equals(onDemandBotsVO.getId())) {
					onDemandBotsObj =onDemandBotsVO; 
					break;
				}
			}
		return onDemandBotsObj;
	}

	public static VirtualObjects getVirtualObject(String virtualObjectId, List<VirtualObjects> virtualObjects) {
		VirtualObjects virtualObjectVO = null;
		Iterator<VirtualObjects> voIterator = virtualObjects.iterator();
		while (voIterator.hasNext()) {
			VirtualObjects virtualObjects2 = voIterator.next();
			if (virtualObjects2.getId().equalsIgnoreCase(virtualObjectId)) {
				virtualObjectVO = virtualObjects2;
				break;
			}
		}
		return virtualObjectVO;
	}

	private static boolean isDateBetweenTwoDate(long startDate, long endDate, Date currentTime) {
		try {
			Date stDate = new Date(startDate);
			Date enDate = new Date(endDate);
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date formatedStartDate = sd.parse(sd.format(stDate));

			Calendar startingCalendar = Calendar.getInstance();
			startingCalendar.setTime(formatedStartDate);

			Date formatedEndDate = sd.parse(sd.format(enDate));
			Calendar endingCalender = Calendar.getInstance();
			endingCalender.setTime(formatedEndDate);

			if (currentTime.after(startingCalendar.getTime()) && currentTime.before(endingCalender.getTime())) {
				log.info("AuthorizationModule : isDateBetweenTwoDate]Date is in between..");
				return true;
			} else {
				log.info("AuthorizationModule : isDateBetweenTwoDate]Date is not in between..");
				return false;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String isDeploymentIdSame(String sourceID, String destID) {
		String errorCode = null;
		if (sourceID == null || destID == null) {
			// tracingUtil.log("Validating application","failed",className,"isDeploymentIdSame");
			return errorCode = AuthConstants.DEPLOYMENT_ID_NOT_FOUND_ERR_CODE;
		}

		if (!sourceID.equals(destID)) {
			// tracingUtil.log("Validating application","failed",className,"isDeploymentIdSame");
			return errorCode = AuthConstants.DEPLOYMENT_ID_MISSMATCH_ERR_CODE;
		}
		// tracingUtil.log("Validating application","success",className,"isDeploymentIdSame");
		return errorCode;

	}

	public static String isApplicationMarkedAsCompleted(boolean isCompleted) {

		if (!isCompleted) {
			// tracingUtil.log("Validating application","failed",className,"isApplicationMarkedAsCompleted");
			return AuthConstants.APP_NOT_COMPLETED_ERROR_CODE;
		} else {
			// tracingUtil.log("Validating application","success",className,"isApplicationMarkedAsCompleted");
			return null;
		}
	}

	public static String isApplicationSuspended(boolean isSuspended) {

		if (isSuspended) {
			// tracingUtil.log("Validating application","failed",className,"isApplicationSuspended");
			return AuthConstants.APP_SUSPENDED_ERROR_CODE;
		} else {
			return null;
		}
		// tracingUtil.log("Validating application","success",className,"isApplicationSuspended");
	}

	public static String validateAuth(String obeId, List<ApiKey> appApikeyList, List<SmartKey> smartKeysList,
			List<String> voSmartKeys, AuthVO authVO) {
		if (isInfoEnabled)
			log.info("AuthModule : validateAuth] Entry. ApiKey :" + authVO.getApikey() + "  Smartkey : "
					+ authVO.getSmartkey());

		ConfigurationVO configurationVO = null;
		String appOwnerRole = null;
		boolean authourizationResult = false;
		SmartKey permissions = null;

		if (isDebugEnabled)
			log.debug("Obtained appName : " + authVO.getAppId() + ", smartkey :" + authVO.getSmartkey());

		if (authVO.getSmartkey() != null && voSmartKeys != null && voSmartKeys.contains(authVO.getSmartkey())) {

			for (SmartKey smartKeyBO : smartKeysList) {
				String appSmartKey = smartKeyBO.getId();
				if (appSmartKey.equals(authVO.getSmartkey())) {
					authourizationResult = true;
					permissions = smartKeyBO;
					log.debug("AuthorizationModule : validateAuth]Smartkey Matched ");
				}
			}
		}
		log.debug("AuthorizationModule : validateAuth]permissions : " + permissions);
		if (!authourizationResult && voSmartKeys != null && voSmartKeys.size() > 0) {
			log.debug("Authorization failed");
			return AuthConstants.SMARTKEY_MISMATCH_ERR_CODE; 
		}

		if (null != permissions) {
			List<String> allowedMethods = permissions.getMethods();
			String supportedConversationType = permissions.getTxType();
			List<String> allowedPlatforms = permissions.getPlatforms();
			List<String> allowedDays = permissions.getDayOfWeek();
			String allowedFromTimeOfDay = permissions.getActiveTimeStart();
			String allowedToTimeOfDay = permissions.getActiveTimeEnd();
			long expiryTime = permissions.getExpiryTime();
			List<Apis> apiList = permissions.getApis();

			log.debug("AuthorizationModule : validateAuth]supportedConversationType : " + supportedConversationType
					+ "allowedMethods : " + allowedMethods + "allowedPlatforms : " + allowedPlatforms + "allowedDays : "
					+ allowedDays + "allowedFromTimeOfDay : " + allowedFromTimeOfDay + "allowedToTimeOfDay : "
					+ allowedToTimeOfDay + "expiryTime" + expiryTime);
			
			if(expiryTime < Instant.now().toEpochMilli()) {
				log.debug("AuthorizationModule : validateAuth]SmartKey has expired. Access Denied");
				return AuthConstants.SMARTKEY_MISMATCH_ERR_CODE;
			}
			
			// IF api list is not empty then its considered as structured bot request
			if(null!=apiList && apiList.size()>0){
				
				Apis apiObj = new Apis();
				apiObj.setMethod(authVO.getOperation());
				apiObj.setUri(authVO.getResourceUri());
				
				if(!apiList.contains(apiObj)){
					log.debug("AuthorizationModule : validateAuth]ResourceUri not allowed for method type. Access Denied");
					return AuthConstants.AUTHORTIZATION_RESOURCE_URI_NOT_ALLOWED_ERR_CODE;
				}
			
			}
			else{
			
				if (allowedMethods == null || !(allowedMethods.contains(authVO.getOperation()))) {
					log.debug("AuthorizationModule : validateAuth]Method not allowed. Access Denied");
					return AuthConstants.AUTHORTIZATION_METHOD_NOT_ALLOWED_ERR_CODE;
				}
			}
			
			if (supportedConversationType == null) {
				log.debug("AuthorizationModule : validateAuth]conversationType is null only for Bot. So skipping the validation");
				//return AuthConstants.AUTHORTIZATION_CONVERSATION_TYPE_NOT_ALLOWED_ERR_CODE;
			} else {
				
				
				String[] txTypes = authVO.getConversationType().split(",");
				List<String> txTypeList = Arrays.asList(txTypes);
				if (!txTypeList.contains(supportedConversationType)) {
					log.debug("AuthorizationModule : validateAuth]conversationType: " + supportedConversationType
							+ " not allowed. Access Denied");
					return AuthConstants.AUTHORTIZATION_CONVERSATION_TYPE_NOT_ALLOWED_ERR_CODE;
				}
			}

			if (allowedPlatforms == null
					|| !withinAllowedPlatforms(allowedPlatforms, authVO.getPlatform().toLowerCase())) {
				log.debug("AuthorizationModule : validateAuth]Platform not allowed. Access Denied");
				return AuthConstants.AUTHORTIZATION_PLATFORM_NOT_ALLOWED_ERR_CODE;
			}
			String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
			log.debug("AuthorizationModule : validateAuth]weekday_name  :" + weekday_name);
			if (allowedDays == null || !(allowedDays.contains(weekday_name))) {
				log.debug("AuthorizationModule : validateAuth]Day not allowed. Access Denied");
				return AuthConstants.AUTHORTIZATION_DAY_NOT_ALLOWED_ERR_CODE;
			}

			System.out.println("allowedFromTimeOfDay : " + allowedFromTimeOfDay);
			System.out.println("allowedToTimeOfDay " + allowedToTimeOfDay);
			boolean allowedTime = isTimeBetweenTwoTime(allowedFromTimeOfDay, allowedToTimeOfDay, new Date(authVO.getRequestedTime()));
			if (!allowedTime) {
				log.debug("AuthorizationModule : validateAuth]Not allowed in this time");
				return AuthConstants.AUTHORTIZATION_TIME_NOT_ALLOWED_ERR_CODE;
			}
		}
		if (obeId == null || StringUtils.isEmpty(obeId)) {
			return AuthConstants.OBE_CONFIGURATION_NOT_FOUND_ERR_CODE;
		}
		
		if (isInfoEnabled)
			log.info("AuthModule : validateAuth] Exit. Result = " + AuthConstants.AUTH_SUCCESS);
		return null;
	}

	public static boolean isTimeBetweenTwoTime(String startTime, String stopTime, Date requestTime) {
		try {

			Date startTimeDate = new SimpleDateFormat("HH:mm:ss").parse(startTime);
			Calendar current = Calendar.getInstance();
			Calendar startingCalendar = Calendar.getInstance();
			startingCalendar.setTime(startTimeDate);
			startingCalendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));

			Date endTimeDate = new SimpleDateFormat("HH:mm:ss").parse(stopTime);
			Calendar endingCalender = Calendar.getInstance();
			endingCalender.setTime(endTimeDate);
			endingCalender.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
			
			Date roundedCurrentDate = DateUtils.truncate(requestTime, Calendar.SECOND);

			log.info("AuthorizationModule : isTimeBetweenTwoTime]Starting Calender :" + startingCalendar.getTime());
			log.info("AuthorizationModule : isTimeBetweenTwoTime]endingCalender Calender :" + endingCalender.getTime());
			log.info("AuthorizationModule : isTimeBetweenTwoTime]Starting currentTime :" + roundedCurrentDate);

			if  (!roundedCurrentDate.before(startingCalendar.getTime()) && !roundedCurrentDate.after(endingCalender.getTime()))
			{
				log.info("AuthorizationModule : isTimeBetweenTwoTime]Time is in between..");
				return true;
			} else {
				log.info("AuthorizationModule : isTimeBetweenTwoTime]Time is not in between..");
				return false;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean withinAllowedPlatforms(List<String> allowedPlatforms, String userAgent) {

		boolean resultMatch = false;
		StringUtils utils = new StringUtils();
		if (allowedPlatforms.contains("NA"))
			resultMatch = true;
		if (!resultMatch) {
			for (String platform : allowedPlatforms) {
				log.debug("Checking with platform : " + platform);
				if (utils.containsIgnoreCase(userAgent, platform)) {
					resultMatch = true;
				}
			}
		}
		log.debug("Is Platform within allowedPlatforms :" + resultMatch);
		return resultMatch;
	}
}
