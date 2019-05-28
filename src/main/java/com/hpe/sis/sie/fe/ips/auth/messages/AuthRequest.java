package com.hpe.sis.sie.fe.ips.auth.messages;

import java.io.Serializable;

import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.auth.model.AuthVO;

import akka.actor.ActorRef;

public class AuthRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private Applications applicationVO;
	private AuthVO authVO;
	private String transactionId;
	private ActorRef nonActorSender;

	public Applications getApplicationVO() {
		return applicationVO;
	}

	public void setApplicationVO(Applications applicationVO) {
		this.applicationVO = applicationVO;
	}

	public AuthVO getAuthVO() {
		return authVO;
	}

	public void setAuthVO(AuthVO authVO) {
		this.authVO = authVO;
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
