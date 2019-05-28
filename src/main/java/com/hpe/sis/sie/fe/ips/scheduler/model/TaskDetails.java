package com.hpe.sis.sie.fe.ips.scheduler.model;

public class TaskDetails {

	private String taskDataJson;
	private String taskHeader;
	private String transactionId;

	public String getTaskDataJson() {
		return taskDataJson;
	}

	public void setTaskDataJson(String taskDataJson) {
		this.taskDataJson = taskDataJson;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTaskHeader() {
		return taskHeader;
	}

	public void setTaskHeader(String taskHeader) {
		this.taskHeader = taskHeader;
	}

}
