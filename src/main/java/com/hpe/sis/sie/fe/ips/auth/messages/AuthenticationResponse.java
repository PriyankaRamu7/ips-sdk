package com.hpe.sis.sie.fe.ips.auth.messages;

import akka.actor.ActorRef;

public class AuthenticationResponse {

	private AuthRequest authRequest;
	private String response;
	private String transactionId;
	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public AuthRequest getAuthRequest() {
		return authRequest;
	}

	public void setAuthRequest(AuthRequest authRequest) {
		this.authRequest = authRequest;
	}

}
