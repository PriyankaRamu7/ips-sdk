package com.hpe.sis.sie.fe.ips.processing.model;

import java.io.Serializable;

import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;

public class CallbackRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8833925840519616412L;
	
	private String channelName;
	
	private Applications application;
	
	private String virtualObjectId;
	
	private String botId;
	
	private String interactionContextId;
	
	private String transactionId;
	
	private long requestedTime;
	
	private String remoteHost;
	
	public long getRequestedTime() {
		return requestedTime;
	}

	public void setRequestedTime(long requestedTime) {
		this.requestedTime = requestedTime;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public String getBotId() {
		return botId;
	}

	public void setBotId(String botId) {
		this.botId = botId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @return the interactionContextId
	 */
	public String getInteractionContextId() {
		return interactionContextId;
	}

	/**
	 * @param interactionContextId the interactionContextId to set
	 */
	public void setInteractionContextId(String interactionContextId) {
		this.interactionContextId = interactionContextId;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Applications getApplication() {
		return application;
	}

	public void setApplication(Applications application) {
		this.application = application;
	}

	public String getVirtualObjectId() {
		return virtualObjectId;
	}

	public void setVirtualObjectId(String virtualObjectId) {
		this.virtualObjectId = virtualObjectId;
	}
	
	

}
