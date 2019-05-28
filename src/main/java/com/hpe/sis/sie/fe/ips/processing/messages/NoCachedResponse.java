package com.hpe.sis.sie.fe.ips.processing.messages;

import java.io.Serializable;

public class NoCachedResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -643914811496404566L;

	private InteractionRequest interactionRequest;

	public InteractionRequest getInteractionRequest() {
		return interactionRequest;
	}

	public void setInteractionRequest(InteractionRequest interactionRequest) {
		this.interactionRequest = interactionRequest;
	}
	
	
}
