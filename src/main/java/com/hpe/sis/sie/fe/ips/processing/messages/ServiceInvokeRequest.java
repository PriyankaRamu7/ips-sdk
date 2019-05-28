package com.hpe.sis.sie.fe.ips.processing.messages;

import java.io.Serializable;

public class ServiceInvokeRequest implements Serializable {

	private static final long serialVersionUID = 3038701769155252155L;

	private String serviceURL;
	private InteractionRequest interactionRequest;

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public InteractionRequest getInteractionRequest() {
		return interactionRequest;
	}

	public void setInteractionRequest(InteractionRequest interactionRequest) {
		this.interactionRequest = interactionRequest;
	}

}
