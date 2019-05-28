package com.hpe.sis.sie.fe.ips.processing.messages;

import java.io.Serializable;

public class CachedResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3576990009230449929L;
	
	private BackendResponse backendResponse;
	
	private InteractionRequest interactionRequest;

	public BackendResponse getBackendResponse() {
		return backendResponse;
	}

	public void setBackendResponse(BackendResponse backendResponse) {
		this.backendResponse = backendResponse;
	}

	public InteractionRequest getInteractionRequest() {
		return interactionRequest;
	}

	public void setInteractionRequest(InteractionRequest interactionRequest) {
		this.interactionRequest = interactionRequest;
	}
	

}
