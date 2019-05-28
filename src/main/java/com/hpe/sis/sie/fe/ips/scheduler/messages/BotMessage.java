package com.hpe.sis.sie.fe.ips.scheduler.messages;

import java.io.Serializable;

import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;

public class BotMessage implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The transaction id. */
	private String transactionId;
	
	/** The interaction context id. */
	private String interactionContextId;
	
	/** The virtual object id. */
	private String botId;

	/** The processing pattern. */
	private String processingPattern;

	/** The operation or method. */
	private String method; // incoming req http method

	/** The remote host. */
	private String remoteHost; // http request body
	
	/** The requested time. */
	private long requestedTime;
	
	/** The application VO. */
	private Applications application;
	
	private String botTaskData;
	
	private String botTaskHeader;
	
	/** The from channel. */
	private String fromChannel;
	
	/** The is test request. */
	private boolean isTestRequest;
	
	private String serviceId;
	
	private String serviceURL;

	public String getFromChannel() {
		return fromChannel;
	}

	public void setFromChannel(String fromChannel) {
		this.fromChannel = fromChannel;
	}

	public boolean isTestRequest() {
		return isTestRequest;
	}

	public void setTestRequest(boolean isTestRequest) {
		this.isTestRequest = isTestRequest;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getInteractionContextId() {
		return interactionContextId;
	}

	public void setInteractionContextId(String interactionContextId) {
		this.interactionContextId = interactionContextId;
	}

	public String getBotId() {
		return botId;
	}

	public void setBotId(String botId) {
		this.botId = botId;
	}

	public String getProcessingPattern() {
		return processingPattern;
	}

	public void setProcessingPattern(String processingPattern) {
		this.processingPattern = processingPattern;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public long getRequestedTime() {
		return requestedTime;
	}

	public void setRequestedTime(long requestedTime) {
		this.requestedTime = requestedTime;
	}

	public Applications getApplication() {
		return application;
	}

	public void setApplication(Applications application) {
		this.application = application;
	}

	public String getBotTaskData() {
		return botTaskData;
	}

	public void setBotTaskData(String botTaskData) {
		this.botTaskData = botTaskData;
	}

	public String getBotTaskHeader() {
		return botTaskHeader;
	}

	public void setBotTaskHeader(String botTaskHeader) {
		this.botTaskHeader = botTaskHeader;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}
	
	
}
