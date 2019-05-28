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
 * Contents: Interaction.java
 * ---------------------------------------------------------------------------
 ******************************************************************************/
package com.hpe.sis.sie.fe.ips.processing.model;

import java.util.List;
import java.util.Map;

import com.hpe.sis.sie.fe.ips.processing.messages.BackendResponse;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Task;

/**
 * The Class InteractionVO.
 */
public class Interaction {

	/** transaction id. */
	private String transactionId;
	
	/** interaction context id. */
	private String interactionContextId;
	
	/** from channel. */
	private String fromChannel;
	
	/** be time out. */
	private int beTimeOut;
	
	/** actor time out. */
	private int actorTimeOut;
	
	/** processing pattern. */
	private String processingPattern;
	
	/** query parameters. */
	private String queryParameters; // from req url
	
	/** request headers. */
	private Map<String, String> requestHeaders; // from httpRequest
	
	/** sis headers. */
	private String sisHeaders; // from yml
	
	/** method. */
	private String method; // incoming req http method
	
	/** request body. */
	private String requestBody;
	
	/** remote host. */
	private String remoteHost;
	
	/** requested time. */
	private long requestedTime;
//	private String taskId;
//	private String name;
//	private String version;

//	private String engine;
	
	/** virtual object id. */
	private String virtualObject;
	
	/** activity stream id */
	private String activityStreamId;
	
	/** param list */
	private List<String> paramList;
	
	/** application */
	private Applications applicationVO;
	
	/** The is test request. */
	private boolean isTestRequest;
	
	/** The Back-End Service to be executed (task) */
	private Task task;
	
	/** The bot id. */
	private String botId;
	
	private boolean isResponseCachingEnabled;
	
	private String queryString;

	private long cachedResponseTTL;
	
	private String requestURI;
	
	private String interactionType;
	
	private String txType;
	
	private String interactionId;
	
	private String stType; 
		
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
	 * Gets the task, BE service to be executed for this interaction request
	 *
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}
	
	/**
	 * Sets the task, BE service to be executed for this interaction request
	 *
	 * @param task the new task
	 */
	public void setTask(Task task) {
		this.task = task;
	}
	
	/**
	 * Gets the list of parameters for this interaction request
	 *
	 * @return the param list
	 */
	public List<String> getParamList() {
		return paramList;
	}

	/**
	 * Sets the list of parameters for this interaction request
	 *
	 * @param paramList the new param list
	 */
	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}

	/**
	 * Gets the virtual object id
	 *
	 * @return the virtual object
	 */
	public String getVirtualObject() {
		return virtualObject;
	}

	/**
	 * Sets the virtual object id
	 *
	 * @param virtual object
	 */
	public void setVirtualObject(String virtualObject) {
		this.virtualObject = virtualObject;
	}

	/**
	 * Gets the actor time out.
	 *
	 * @return the actor time out
	 */
	public int getActorTimeOut() {
		return actorTimeOut;
	}

	/**
	 * Sets the actor time out.
	 *
	 * @param actorTimeOut the new actor time out
	 */
	public void setActorTimeOut(int actorTimeOut) {
		this.actorTimeOut = actorTimeOut;
	}

	/**
	 * Gets the application VO.
	 *
	 * @return the application VO
	 */
	public Applications getApplicationVO() {
		return applicationVO;
	}

	/**
	 * Sets the application VO.
	 *
	 * @param applicationVO the new application VO
	 */
	public void setApplicationVO(Applications applicationVO) {
		this.applicationVO = applicationVO;
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
	 * @param transaction id
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * Gets the interaction context id.
	 *
	 * @return the interaction context id
	 */
	public String getInteractionContextId() {
		return interactionContextId;
	}

	/**
	 * Sets the interaction context id.
	 *
	 * @param interactionContextId
	 */
	public void setInteractionContextId(String interactionContextId) {
		this.interactionContextId = interactionContextId;
	}

	/**
	 * Gets channel from where the interaction request is being sent to IPS
	 *
	 * @return from channel
	 */
	public String getFromChannel() {
		return fromChannel;
	}

	/**
	 * Sets the from channel.
	 *
	 * @param fromChannel
	 */
	public void setFromChannel(String fromChannel) {
		this.fromChannel = fromChannel;
	}

	/**
	 * Gets the BE time out, timeout to read/write data while invoking the SIE: BE HMS
	 *
	 * @return the be time out
	 */
	public int getBeTimeOut() {
		return beTimeOut;
	}

	/**
	 * Sets the be time out.
	 *
	 * @param beTimeOut the new be time out
	 */
	public void setBeTimeOut(int beTimeOut) {
		this.beTimeOut = beTimeOut;
	}

	/**
	 * Gets the processing pattern.
	 *
	 * @return the processing pattern
	 */
	public String getProcessingPattern() {
		return processingPattern;
	}

	/**
	 * Sets the processing pattern.
	 *
	 * @param processingPattern the new processing pattern
	 */
	public void setProcessingPattern(String processingPattern) {
		this.processingPattern = processingPattern;
	}

	/**
	 * Gets the query parameters
	 *
	 * @return the query parameters
	 */
	public String getQueryParameters() {
		return queryParameters;
	}

	/**
	 * Sets the query parameters
	 * In case of Channels implementing HTTP protocol, the queryString should be set here
	 * @param queryParameters the new query parameters
	 */
	public void setQueryParameters(String queryParameters) {
		this.queryParameters = queryParameters;
	}

	/**
	 * Gets the request headers.
	 * 
	 * @return the request headers
	 */
	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	/**
	 * Sets the request headers.
	 * In case of Channels implementing HTTP protocol, the HTTP request headers should be set here
	 * @param requestHeaders the request headers
	 */
	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	/**
	 * Gets the sis headers.
	 * 
	 * @return the sis headers
	 */
	public String getSisHeaders() {
		return sisHeaders;
	}

	/**
	 * Sets the sis headers.
	 *
	 * @param sisHeaders the new sis headers
	 */
	public void setSisHeaders(String sisHeaders) {
		this.sisHeaders = sisHeaders;
	}

	/**
	 * Gets the method
	 * 
	 * @return the http method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the method.
	 * In case of Channels implementing HTTP protocol, the HTTP method should be set here
	 * @param httpMethod the new http method
	 */
	public void setMethod(String method) {
		this.method = method;
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
	 * In case of Channels implementing HTTP protocol, the HTTP request body should be set here
	 * @param requestBody the new request body
	 */
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	/**
	 * Gets the remote host, where the interaction request originates from
	 *
	 * @return the remote host
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * Sets the remote host, where the interaction request originates from
	 *
	 * @param remoteHost the new remote host
	 */
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	/**
	 * Gets the task id.
	 *
	 * @return the task id
	 */
	/*public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getEngine() {
		return engine;
	}
	public void setEngine(String engine) {
		this.engine = engine;
	}*/


	/**
	 * Gets the requested time, time at which the Interaction request is received by the inbound interaction channel
	 *
	 * @return the requested time
	 */
	public long getRequestedTime() {
		return requestedTime;
	}

	/**
	 * Sets the requested time - time at which the Interaction request is received by the inbound interaction channel
	 *
	 * @param requestedTime the requested time
	 */
	public void setRequestedTime(long requestedTime) {
		this.requestedTime = requestedTime;
	}

	/**
	 * Checks if is test request, test requests from the Smart Interaction Designer
	 *
	 * @return true, if is test request
	 */
	public boolean isTestRequest() {
		return isTestRequest;
	}

	/**
	 * Sets the test request, test requests from the Smart Interaction Designer
	 *
	 * @param isTestRequest the test request
	 */
	public void setTestRequest(boolean isTestRequest) {
		this.isTestRequest = isTestRequest;
	}

	public boolean isResponseCachingEnabled() {
		return isResponseCachingEnabled;
	}

	public void setResponseCachingEnabled(boolean isResponseCachingEnabled) {
		this.isResponseCachingEnabled = isResponseCachingEnabled;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public long getCachedResponseTTL() {
		return cachedResponseTTL;
	}

	public void setCachedResponseTTL(long cachedResponseTTL) {
		this.cachedResponseTTL = cachedResponseTTL;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public String getActivityStreamId() {
		return activityStreamId;
	}

	public void setActivityStreamId(String activityStreamId) {
		this.activityStreamId = activityStreamId;
	}

	public String getInteractionType() {
		return interactionType;
	}

	public void setInteractionType(String interactionType) {
		this.interactionType = interactionType;
	}

	public String getTxType() {
		return txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

	public String getInteractionId() {
		return interactionId;
	}

	public void setInteractionId(String interactionId) {
		this.interactionId = interactionId;
	}

	public String getStType() {
		return stType;
	}

	public void setStType(String stType) {
		this.stType = stType;
	}
}
