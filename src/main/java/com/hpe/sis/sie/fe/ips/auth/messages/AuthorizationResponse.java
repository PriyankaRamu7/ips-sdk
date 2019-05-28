package com.hpe.sis.sie.fe.ips.auth.messages;

import akka.actor.ActorRef;

public class AuthorizationResponse {

	private String response;
	private String transactionId;
	private ActorRef nonActorSender;

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

	public ActorRef getNonActorSender() {
		return nonActorSender;
	}

	public void setNonActorSender(ActorRef nonActorSender) {
		this.nonActorSender = nonActorSender;
	}

}
