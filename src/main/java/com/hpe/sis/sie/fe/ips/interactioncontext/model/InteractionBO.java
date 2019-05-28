package com.hpe.sis.sie.fe.ips.interactioncontext.model;

public class InteractionBO {

	private static final long serialVersionUID = -4733095795365413357L;

	private String interactionId;
	private String sessionId;
	private Channel channel;
	private String custom;
	private Schedule schedule;

	public String getInteractionId() {
		return interactionId;
	}

	public void setInteractionId(String interactionId) {
		this.interactionId = interactionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	
}
