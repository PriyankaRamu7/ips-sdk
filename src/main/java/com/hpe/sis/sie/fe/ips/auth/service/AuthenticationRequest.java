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
 * Contents: AuthenticationRequest.java
 * ---------------------------------------------------------------------------
 ******************************************************************************/
package com.hpe.sis.sie.fe.ips.auth.service;

/**
 * Authentication Request sent by the inbound interaction channel to IPS in order to authenticate the interaction requests
 */
public class AuthenticationRequest {

	/**
	 * apikey retrieved from the inbound interaction request
	 */
	private String apikey;
	
	/**
	 * Application Id of this interaction request's application
	 */
	private String appId;
	
	/**
	 * Smart Key retrieved from the inbound interaction request
	 */
	private String smartkey;
	
	/**
	 * VirtualObject Id of this interaction request's Virtual Object
	 */
	private String virtualObject;
	
	/**
	 * Activity-Stream Id of this interaction request's activity-stream
	 */
	private String activityStreamId;
	
	/**
	 * Operation (method) invoked on this Interaction request's Virtual object
	 * In case of HTTP protocol based channels, this is the HTTP method invoked on Virtual object
	 */
	private String operation;
	
	/**
	 * Conversation type (Operations or activity-stream)
	 * Complete list of Conversation Types: Operations,w3cactivitystreams,onem2m,cnActivityStreams,(onDemandBots?? TODO)
	 */
	private String conversationType;
	
	/**
	 * Platform from where the interaction request originates from
	 * Example: Windows NT, Android, iPhone, Windows Phone
	 */
	private String platform;
	
	/**
	 * If the interaction request is a test request (originating from the Smart Interaction Designer Test console)
	 */
	private boolean isRequestMadeFromConsole;
	
	/**
	 * Deployment-ID of the SIE: FE channel (indicating if this is a part of Shared deployment or OBE-dedicated or Application-dedicated deployment)
	 */
	private String channelDeploymentId;
	
	/**
	 * 
	 */
	private String appOwnerRole;
	
	/**
	 * Name of the SIE: FE channel
	 * Example: ic-vo-operations
	 */
	private String channelName;
	
	/** The obe ID. */
	private String obeID;
	
	/** The actor time out. */
	private int actorTimeOut;
	
	/** The request body. */
	private String requestBody;
	
	/** The request URI. */
	private String requestURI;
	
	/** The bot id. */
	private String botId;
	
	/** The transaction id. */
	private String transactionId;
	
	/** The requested time. */
	private long requestedTime;
	
	/** The remote host. */
	private String remoteHost;
	
	/** The request method. */
	private String requestMethod;
	
	/** The xapi4saas token. */
	private String xApi4SaasToken;
	
	/** The service id. */
	private String serviceId;
	
	/** The created by. */
	private String createdBy;
	
	
	private String resourceUri;
	
	
	public String getResourceUri() {
		return resourceUri;
	}

	public void setResourceUri(String resourceUri) {
		this.resourceUri = resourceUri;
	}

	/**
	 * Gets the bot id.
	 *
	 * @return the bot id
	 */
	public String getBotId() {
		return botId;
	}

	/**
	 * Sets the bot id.
	 *
	 * @param botId the new bot id
	 */
	public void setBotId(String botId) {
		this.botId = botId;
	}

	/**
	 * Gets the request body.
	 *
	 * @return the request body
	 */
	public String getRequestBody() {
		return requestBody;
	}
	
	/**
	 * Sets the request body.
	 *
	 * @param requestBody the new request body
	 */
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	
	/**
	 * Gets the request URI.
	 *
	 * @return the request URI
	 */
	public String getRequestURI() {
		return requestURI;
	}

	/**
	 * Sets the request URI.
	 *
	 * @param requestURI the new request URI
	 */
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	/**
	 * Gets the actor time out.
	 *
	 * @return actor timeout
	 */
	public int getActorTimeOut() {
		return actorTimeOut;
	}
	
	/**
	 * @param actorTimeOut
	 */
	public void setActorTimeOut(int actorTimeOut) {
		this.actorTimeOut = actorTimeOut;
	}
	
	/**
	 * @return obe ID
	 */
	public String getObeID() {
		return obeID;
	}
	
	/**
	 * @param obeID
	 */
	public void setObeID(String obeID) {
		this.obeID = obeID;
	}
	
	/**
	 * @return channel name
	 */
	public String getChannelName() {
		return channelName;
	}
	
	/**
	 * @param channelName
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
	/**
	 * @return api key
	 */
	public String getApikey() {
		return apikey;
	}
	
	/**
	 * @param apikey (retrieved from the inbound interaction request)
	 */
	public void setApikey(String apikey) {
		this.apikey = apikey;
	}
	
	/**
	 * @return application Id
	 * Application Id of this interaction request's application
	 */
	public String getAppId() {
		return appId;
	}
	
	/**
	 * @param appId
	 * Application Id of this interaction request's application
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	/**
	 * @return Smart Key
	 * Smart Key retrieved from the inbound interaction request
	 */
	public String getSmartkey() {
		return smartkey;
	}
	
	/**
	 * @param smartkey
	 * Smart Key retrieved from the inbound interaction request
	 */
	public void setSmartkey(String smartkey) {
		this.smartkey = smartkey;
	}
	
	/**
	 * @return virtual object Id
	 * VirtualObject Id of this interaction request's Virtual Object
	 */
	public String getVirtualObject() {
		return virtualObject;
	}
	
	/**
	 * @param virtualObject
	 * VirtualObject Id of this interaction request's Virtual Object
	 */
	public void setVirtualObject(String virtualObject) {
		this.virtualObject = virtualObject;
	}
	
	/**
	 * @return activityStreamId
	 * Activity-Stream Id of this interaction request's activity-stream
	 */
	public String getActivityStreamId() {
		return activityStreamId;
	}
	
	/**
	 * @param activityStreamId
	 * Activity-Stream Id of this interaction request's activity-stream
	 */
	public void setActivityStreamId(String activityStreamId) {
		this.activityStreamId = activityStreamId;
	}
	
	/**
	 * @return operation
	 * Operation (method) invoked on this Interaction request's Virtual object
	 */
	public String getOperation() {
		return operation;
	}
	
	/**
	 * @param operation
	 * Operation (method) invoked on this Interaction request's Virtual object
	 * In case of HTTP protocol based channels, this is the HTTP method invoked on Virtual object
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	/**
	 * @return conversation type
	 * Conversation type (Operations or activity-stream)
	 * Complete list of applicable Conversation Types: Operations,w3cactivitystreams,onem2m,cnActivityStreams,(onDemandBots?? TODO)
	 */
	public String getConversationType() {
		return conversationType;
	}
	
	/**
	 * @param conversationType
	 * Conversation type (Operations or activity-stream)
	 * Complete list of applicable Conversation Types: Operations,w3cactivitystreams,onem2m,cnActivityStreams,(onDemandBots?? TODO)
	 */
	public void setConversationType(String conversationType) {
		this.conversationType = conversationType;
	}
	
	/**
	 * @return platform
	 * Platform from where the interaction request originates from
	 * Example: Windows NT, Android, iPhone, Windows Phone
	 */
	public String getPlatform() {
		return platform;
	}
	
	/**
	 * @param platform
	 */
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	/**
	 * @return isRequestMadeFromConsole
	 * If the interaction request is a test request (originating from the Smart Interaction Designer Test console)
	 */
	public boolean isRequestMadeFromConsole() {
		return isRequestMadeFromConsole;
	}
	
	/**
	 * @param isRequestMadeFromConsole
	 * If the interaction request is a test request (originating from the Smart Interaction Designer Test console)
	 */
	public void setRequestMadeFromConsole(boolean isRequestMadeFromConsole) {
		this.isRequestMadeFromConsole = isRequestMadeFromConsole;
	}
	
	/**
	 * @return channel deployment Id
	 * Deployment-ID of the SIE: FE channel (indicating if this is a part of Shared deployment or OBE-dedicated or Application-dedicated deployment)
	 */
	public String getChannelDeploymentId() {
		return channelDeploymentId;
	}
	
	/**
	 * @param channelDeploymentId
	 * Deployment-ID of the SIE: FE channel (indicating if this is a part of Shared deployment or OBE-dedicated or Application-dedicated deployment)
	 */
	public void setChannelDeploymentId(String channelDeploymentId) {
		this.channelDeploymentId = channelDeploymentId;
	}
	
	/**
	 * @return 
	 */
	public String getAppOwnerRole() {
		return appOwnerRole;
	}
	
	/**
	 * @param appOwnerRole
	 */
	public void setAppOwnerRole(String appOwnerRole) {
		this.appOwnerRole = appOwnerRole;
	}

	/**
	 * Gets the transaction id.
	 *
	 * @return the transaction id
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Sets the transaction id.
	 *
	 * @param transactionId the new transaction id
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * Gets the requested time.
	 *
	 * @return the requested time
	 */
	public long getRequestedTime() {
		return requestedTime;
	}

	/**
	 * Sets the requested time.
	 *
	 * @param requestedTime the new requested time
	 */
	public void setRequestedTime(long requestedTime) {
		this.requestedTime = requestedTime;
	}

	/**
	 * Gets the remote host.
	 *
	 * @return the remote host
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * Sets the remote host.
	 *
	 * @param remoteHost the new remote host
	 */
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	/**
	 * Gets the request method.
	 *
	 * @return the request method
	 */
	public String getRequestMethod() {
		return requestMethod;
	}

	/**
	 * Sets the request method.
	 *
	 * @param requestMethod the new request method
	 */
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	/**
	 * Gets the xapi4saastoken.
	 *
	 * @return the xapi4saastoken
	 */
	public String getxApi4SaasToken() {
		return xApi4SaasToken;
	}

	/**
	 * Sets the xapi4saastoken token.
	 *
	 * @param xApi4SaasToken the new xapi4saastoken
	 */
	public void setxApi4SaasToken(String xApi4SaasToken) {
		this.xApi4SaasToken = xApi4SaasToken;
	}

	/**
	 * Gets the service id.
	 *
	 * @return the service id
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * Sets the service id.
	 *
	 * @param serviceId the new service id
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * Gets the created by.
	 *
	 * @return the created by
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the created by.
	 *
	 * @param createdBy the new created by
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
}
