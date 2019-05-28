package com.hpe.sis.sie.fe.ips.interactioncontext.model;

public class ChannelTransactionBO {

	private static final long serialVersionUID = 1L;
	private String transactionId;
	private String channelType;
	private String interactionContextId;
	private String createdBy;
	private String appId;
	private long timestamp;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getInteractionContextId() {
		return interactionContextId;
	}

	public void setInteractionContextId(String interactionContextId) {
		this.interactionContextId = interactionContextId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
