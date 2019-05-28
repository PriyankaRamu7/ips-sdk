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
 * Contents: Transaction.java
 * ---------------------------------------------------------------------------
 ******************************************************************************/
package com.hpe.sis.sie.fe.ips.processing.model;

import java.io.Serializable;

/**
 * Transaction data model - contains all data required to log a record in Channel's transaction.log
 * 
 */
public class Transaction implements Serializable {

	private static final long serialVersionUID = 1L;

	private String transactionId; // TRANSACTIONID
	private long requestedTime; // REQUESTEDTIME == startTime?
	private String localHost; // LOCALHOST
	private String remoteHost; // REMOTEHOST
	private String userName; // USER_NAME
	private String application; //
	private String virtualObjects; // VIRTUAL_OBJECTS
	private String serviceId; // SERVICE_ID
	private String botId; // BOT_ID
	private String InteractionChannel; // INTERACTION_CHANNEL
	private int interactionApiResultCode; // INTERACTION_API_RESULTCODE
	private String interactionApiErrDetail; // INTERACTION_API_ERROR_DETAIL
	private String backEndInvocationStatus; // BACKEND_INVOCATION_STATUS
	private long InteractionApiResponseTime; // INTERACTION_API_RESPONSETIME
	private String backEndInvocationTaskId; // BACKEND_INVOCATION_TASKID
	private long backEndResponseTime; // BACKEND_RESPONSETIME
	private byte[] interactionApiRequest; // INTERACTION_API_REQUEST
	private byte[] backEndResponse; // BACKEND_RESPONSE -- filtered response in SIS 1.3
	private String interactionApiMethodType; // INTERACTION_API_METHODTYPE
	private boolean isTestRequest;
	private String obeId;
	private String x_API4SAAS_TOKEN; // X_API4SAAS_TOKEN
	private boolean isExceptionFlowExecuted;
    // backendResponsePostResponseFilter - present in sis 1.2
	
	/**
	 * @return transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return requestedTime - time at which the Interaction request is received by the inbound interaction channel
	 */
	public long getRequestedTime() {
		return requestedTime;
	}

	/**
	 * @param requestedTime, time at which the Interaction request is received by the inbound interaction channel
	 */
	public void setRequestedTime(long requestedTime) {
		this.requestedTime = requestedTime;
	}

	/**
	 * @return localhost (host address of the inbound interaction channel)
	 */
	public String getLocalHost() {
		return localHost;
	}

	/**
	 * @param localHost (host address of the inbound interaction channel)
	 */
	public void setLocalHost(String localHost) {
		this.localHost = localHost;
	}

	/**
	 * @return remoteHost (Host address where the interaction request originates from)
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * @param remoteHost (Host address where the interaction request originates from)
	 */
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	/**
	 * @return user name of the user who own this application
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param user name of the user who own this application
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return application id of this interaction request's application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * @param application id of this interaction request's application
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * @return virtual object id
	 */
	public String getVirtualObjects() {
		return virtualObjects;
	}

	/**
	 * @param virtual object id
	 */
	public void setVirtualObjects(String virtualObjects) {
		this.virtualObjects = virtualObjects;
	}

	/**
	 * @return service id of the SIE: BE HMS service that is executed for this interaction request
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * @param service id of the SIE: BE HMS service that is executed for this interaction request
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * @return bot id
	 */
	public String getBotId() {
		return botId;
	}

	/**
	 * @param bot id
	 */
	public void setBotId(String botId) {
		this.botId = botId;
	}

	/**
	 * @return in-bound interaction channel name that processes this interaction request
	 */
	public String getInteractionChannel() {
		return InteractionChannel;
	}

	/**
	 * @param interactionChannel, in-bound interaction channel name that processes this interaction request
	 */
	public void setInteractionChannel(String interactionChannel) {
		InteractionChannel = interactionChannel;
	}

	/**
	 * @return interactionApiResultCode, HTTP status code returned by the SIE: BE HMS 
	 */
	public int getInteractionApiResultCode() {
		return interactionApiResultCode;
	}

	/**
	 * @param interactionApiResultCode, HTTP status code returned by the SIE: BE HMS
	 */
	public void setInteractionApiResultCode(int interactionApiResultCode) {
		this.interactionApiResultCode = interactionApiResultCode;
	}

	/**
	 * @return interactionApiErrDetail, HTTP status message returned by the SIE: BE HMS
	 */
	public String getInteractionApiErrDetail() {
		return interactionApiErrDetail;
	}

	/**
	 * @param interactionApiErrDetail, HTTP status message returned by the SIE: BE HMS
	 */
	public void setInteractionApiErrDetail(String interactionApiErrDetail) {
		this.interactionApiErrDetail = interactionApiErrDetail;
	}

	/**
	 * @return backEndInvocationStatus, status of the SIE: BE HMS execution
	 */
	public String getBackEndInvocationStatus() {
		return backEndInvocationStatus;
	}

	/**
	 * @param backEndInvocationStatus, status of the SIE: BE HMS execution
	 */
	public void setBackEndInvocationStatus(String backEndInvocationStatus) {
		this.backEndInvocationStatus = backEndInvocationStatus;
	}

	/**
	 * @return interactionApiResponseTime, time taken to receive response from the SIE: BE HMS
	 */
	public long getInteractionApiResponseTime() {
		return InteractionApiResponseTime;
	}

	/**
	 * @param interactionApiResponseTime, time taken to receive response from the SIE: BE HMS
	 */
	public void setInteractionApiResponseTime(int interactionApiResponseTime) {
		InteractionApiResponseTime = interactionApiResponseTime;
	}

	/**
	 * @return backEndInvocationTaskId, task ID generated at SIE: BE for executing the HMS for this interaction request
	 */
	public String getBackEndInvocationTaskId() {
		return backEndInvocationTaskId;
	}

	/**
	 * @param backEndInvocationTaskId, task ID generated at SIE: BE for executing the HMS for this interaction request
	 */
	public void setBackEndInvocationTaskId(String backEndInvocationTaskId) {
		this.backEndInvocationTaskId = backEndInvocationTaskId;
	}

	/**
	 * @return backEndResponseTime, time at which the response was received from SIE: BE
	 */
	public long getBackEndResponseTime() {
		return backEndResponseTime;
	}

	/**
	 * @param backEndResponseTime, time at which the response was received from SIE: BE
	 */
	public void setBackEndResponseTime(int backEndResponseTime) {
		this.backEndResponseTime = backEndResponseTime;
	}

	/**
	 * @return interactionApiRequest, message body of this interaction request
	 */
	public byte[] getInteractionApiRequest() {
		return interactionApiRequest;
	}

	/**
	 * @param interactionApiRequest, message body of this interaction request
	 */
	public void setInteractionApiRequest(byte[] interactionApiRequest) {
		this.interactionApiRequest = interactionApiRequest;
	}

	/**
	 * @return backEndResponse, response message body, as received from the SIE: BE HMS
	 */
	public byte[] getBackEndResponse() {
		return backEndResponse;
	}

	/**
	 * @param backEndResponse, response message body, as received from the SIE: BE HMS
	 */
	public void setBackEndResponse(byte[] backEndResponse) {
		this.backEndResponse = backEndResponse;
	}

	/**
	 * @return interactionApiMethodType, method type of the interaction request
	 */
	public String getInteractionApiMethodType() {
		return interactionApiMethodType;
	}

	/**
	 * @param interactionApiMethodType, method type of the interaction request
	 */
	public void setInteractionApiMethodType(String interactionApiMethodType) {
		this.interactionApiMethodType = interactionApiMethodType;
	}

	/**
	 * @return
	 */
	public String getX_API4SAAS_TOKEN() {
		return x_API4SAAS_TOKEN;
	}

	/**
	 * @param x_API4SAAS_TOKEN
	 */
	public void setX_API4SAAS_TOKEN(String x_API4SAAS_TOKEN) {
		this.x_API4SAAS_TOKEN = x_API4SAAS_TOKEN;
	}

	/**
	 * @return true, if this interaction request is a test request, requests from the Smart Interaction Designer
	 */
	public boolean isTestRequest() {
		return isTestRequest;
	}

	/**
	 * @param isTestRequest
	 */
	public void setTestRequest(boolean isTestRequest) {
		this.isTestRequest = isTestRequest;
	}

	/**
	 * @return obeId, interaction request's application belongs to this OBE
	 */
	public String getObeId() {
		return obeId;
	}

	/**
	 * @param obeId, interaction request's application belongs to this OBE
	 */
	public void setObeId(String obeId) {
		this.obeId = obeId;
	}

	public boolean isExceptionFlowExecuted() {
		return isExceptionFlowExecuted;
	}

	public void setExceptionFlowExecuted(boolean isExceptionFlowExecuted) {
		this.isExceptionFlowExecuted = isExceptionFlowExecuted;
	}

}
