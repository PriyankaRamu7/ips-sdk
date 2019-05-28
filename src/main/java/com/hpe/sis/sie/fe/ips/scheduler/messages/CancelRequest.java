package com.hpe.sis.sie.fe.ips.scheduler.messages;

public class CancelRequest {

	private String jobId;
	private String actorPath;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getActorPath() {
		return actorPath;
	}

	public void setActorPath(String actorPath) {
		this.actorPath = actorPath;
	}

}
