package com.hpe.sis.sie.fe.ips.interactioncontext.model;

public class InteractionTxBO {

	private static final long serialVersionUID = -4733095795365413357L;

	private String startTime;
	private String endTime;
	private Actor actor;
	private Task task;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
