package com.hpe.sis.sie.fe.ips.interactioncontext.messages;

import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionResponse;

import akka.actor.ActorPath;

public class InteractionContextResult {

	private InteractionRequest interactionRequest;
	private InteractionResponse interactionResponse;
	private ActorPath actorPath;

	public InteractionRequest getInteractionRequest() {
		return interactionRequest;
	}

	public void setInteractionRequest(InteractionRequest interactionRequest) {
		this.interactionRequest = interactionRequest;
	}

	public InteractionResponse getInteractionResponse() {
		return interactionResponse;
	}

	public void setInteractionResponse(InteractionResponse interactionResponse) {
		this.interactionResponse = interactionResponse;
	}

	public ActorPath getActorPath() {
		return actorPath;
	}

	public void setActorPath(ActorPath actorPath) {
		this.actorPath = actorPath;
	}

}
