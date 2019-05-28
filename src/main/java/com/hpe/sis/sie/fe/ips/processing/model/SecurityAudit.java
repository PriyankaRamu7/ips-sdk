package com.hpe.sis.sie.fe.ips.processing.model;

import java.io.Serializable;

public class SecurityAudit implements Serializable {

	private static final long serialVersionUID = 1L;

	private String transactionId;
	private long date;
	private String username;
	private String localHost;
	private String role;
	private String clientNode;
	private String apiAccessDetails;
	private String authorization;
	private String errorCode;
	private String errorMessage;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLocalHost() {
		return localHost;
	}

	public void setLocalHost(String localHost) {
		this.localHost = localHost;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getClientNode() {
		return clientNode;
	}

	public void setClientNode(String clientNode) {
		this.clientNode = clientNode;
	}

	public String getApiAccessDetails() {
		return apiAccessDetails;
	}

	public void setApiAccessDetails(String apiAccessDetails) {
		this.apiAccessDetails = apiAccessDetails;
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
