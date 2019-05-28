package com.hpe.sis.sie.fe.ips.interactioncontext.messages;

import com.hpe.sis.sie.fe.ips.interactioncontext.model.ChannelTransactionBO;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.InteractionContext;

public class InteractionContextMessage {

	private InteractionContext interactionContext;
	private ChannelTransactionBO channelTransactionBO;
	
	public InteractionContext getInteractionContext() {
		return interactionContext;
	}

	public void setInteractionContext(InteractionContext interactionContext) {
		this.interactionContext = interactionContext;
	}

	public ChannelTransactionBO getChannelTransactionBO() {
		return channelTransactionBO;
	}

	public void setChannelTransactionBO(ChannelTransactionBO channelTransactionBO) {
		this.channelTransactionBO = channelTransactionBO;
	}
}
