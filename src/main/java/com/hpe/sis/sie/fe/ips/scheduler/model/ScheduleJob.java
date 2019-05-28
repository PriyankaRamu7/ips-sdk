package com.hpe.sis.sie.fe.ips.scheduler.model;

import java.util.Date;
import java.util.List;

public class ScheduleJob {

	private String jobId;
	private String jobType;
	private String obeId;
	private String appId;
	private Date startDate;
	private Date endDate;
	private String cronPattern;
	private int repeatitionCount;
	private long frequencyInterval;
	private long cronJobInterval;
	private int retryCount;
	private String actorPath;
	private int executionCount;
	private String status;
	private String previousStatus;
	private Date lastExecutionTime;
	private Date nextExcetuionTime;
	private String remoteHost;
	private long requestedTime;
	private String serviceId;
	private List<TaskDetails> taskDataList;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getObeId() {
		return obeId;
	}

	public void setObeId(String obeId) {
		this.obeId = obeId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getCronPattern() {
		return cronPattern;
	}

	public void setCronPattern(String cronPattern) {
		this.cronPattern = cronPattern;
	}

	public int getRepeatitionCount() {
		return repeatitionCount;
	}

	public void setRepeatitionCount(int repeatitionCount) {
		this.repeatitionCount = repeatitionCount;
	}

	public long getFrequencyInterval() {
		return frequencyInterval;
	}

	public void setFrequencyInterval(long frequencyInterval) {
		this.frequencyInterval = frequencyInterval;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public List<TaskDetails> getTaskDataList() {
		return taskDataList;
	}

	public void setTaskDataList(List<TaskDetails> taskDataList) {
		this.taskDataList = taskDataList;
	}

	public String getActorPath() {
		return actorPath;
	}

	public void setActorPath(String actorPath) {
		this.actorPath = actorPath;
	}

	public int getExecutionCount() {
		return executionCount;
	}

	public void setExecutionCount(int executionCount) {
		this.executionCount = executionCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}

	public Date getLastExecutionTime() {
		return lastExecutionTime;
	}

	public void setLastExecutionTime(Date lastExecutionTime) {
		this.lastExecutionTime = lastExecutionTime;
	}

	public Date getNextExcetuionTime() {
		return nextExcetuionTime;
	}

	public void setNextExcetuionTime(Date nextExcetuionTime) {
		this.nextExcetuionTime = nextExcetuionTime;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public long getRequestedTime() {
		return requestedTime;
	}

	public void setRequestedTime(long requestedTime) {
		this.requestedTime = requestedTime;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public long getCronJobInterval() {
		return cronJobInterval;
	}

	public void setCronJobInterval(long cronJobInterval) {
		this.cronJobInterval = cronJobInterval;
	}

}
