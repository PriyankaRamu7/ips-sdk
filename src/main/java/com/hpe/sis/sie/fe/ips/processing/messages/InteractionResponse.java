package com.hpe.sis.sie.fe.ips.processing.messages;

import java.io.Serializable;
import akka.actor.ActorRef;

// TODO: Auto-generated Javadoc
/**
 * The Class InteractionResponse.
 */
public class InteractionResponse implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4890277878764025398L;
	
	/** The transaction id. */
	private String transactionId;
	
	/** The interaction context id. */
	private String interactionContextId;
	
	/** The back end response. */
	private BackendResponse backEndResponse;
	
	/** non Actor Sender reference. */
	private ActorRef nonActorSender;
	
	/**
	 * Gets the interaction context id.
	 *
	 * @return the interaction context id
	 */
	public String getInteractionContextId() {
		return interactionContextId;
	}

	/**
	 * Sets the interaction context id.
	 *
	 * @param interactionContextId the new interaction context id
	 */
	public void setInteractionContextId(String interactionContextId) {
		this.interactionContextId = interactionContextId;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Gets the transaction id.
	 *
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Sets the transaction id.
	 *
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * Gets the non actor sender.
	 *
	 * @return the nonActorSender
	 */
	public ActorRef getNonActorSender() {
		return nonActorSender;
	}

	/**
	 * Sets the non actor sender.
	 *
	 * @param nonActorSender the nonActorSender to set
	 */
	public void setNonActorSender(ActorRef nonActorSender) {
		this.nonActorSender = nonActorSender;
	}

	/**
	 * Gets the back end response.
	 *
	 * @return the back end response
	 */
	public BackendResponse getBackEndResponse() {
		return backEndResponse;
	}

	/**
	 * Sets the back end response.
	 *
	 * @param backEndResponse the new back end response
	 */
	public void setBackEndResponse(BackendResponse backEndResponse) {
		this.backEndResponse = backEndResponse;
	}

}
