package com.hpe.sis.sie.fe.ips.processing.messages;

import java.io.Serializable;

public class ResponseCacheRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3846302546865315437L;
	
	private InteractionRequest interactionRequest;

	public InteractionRequest getInteractionRequest() {
		return interactionRequest;
	}

	public void setInteractionRequest(InteractionRequest interactionRequest) {
		this.interactionRequest = interactionRequest;
	}
	
	

}
