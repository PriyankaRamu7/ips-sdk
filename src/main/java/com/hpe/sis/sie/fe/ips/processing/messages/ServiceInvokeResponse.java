package com.hpe.sis.sie.fe.ips.processing.messages;

import java.io.Serializable;

public class ServiceInvokeResponse implements Serializable {

	private static final long serialVersionUID = 3038701769155252155L;

	private BackendResponse backEndResponse;
	
	private InteractionRequest interactionRequest;

	public InteractionRequest getInteractionRequest() {
		return interactionRequest;
	}

	public void setInteractionRequest(InteractionRequest interactionRequest) {
		this.interactionRequest = interactionRequest;
	}

	public BackendResponse getBackEndResponse() {
		return backEndResponse;
	}

	public void setBackEndResponse(BackendResponse backEndResponse) {
		this.backEndResponse = backEndResponse;
	}

}
