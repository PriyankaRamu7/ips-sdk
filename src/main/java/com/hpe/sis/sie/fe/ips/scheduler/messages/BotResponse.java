package com.hpe.sis.sie.fe.ips.scheduler.messages;

import com.hpe.sis.sie.fe.ips.processing.messages.BackendResponse;

public class BotResponse {
	
	private BotMessage botMessage;
	
	private BackendResponse backEndResponse;

	public BotMessage getBotMessage() {
		return botMessage;
	}

	public void setBotMessage(BotMessage botMessage) {
		this.botMessage = botMessage;
	}

	public BackendResponse getBackEndResponse() {
		return backEndResponse;
	}

	public void setBackEndResponse(BackendResponse backEndResponse) {
		this.backEndResponse = backEndResponse;
	}
	
	
	
}
