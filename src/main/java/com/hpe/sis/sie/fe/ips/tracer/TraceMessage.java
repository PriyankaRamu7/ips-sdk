package com.hpe.sis.sie.fe.ips.tracer;

import java.util.Map;

public class TraceMessage {
	
	private String transactionId;
	
	private String event;
	
	private Map<String, String> fields;
	
	private String timeStamp;
	
	private String classAndMethod;
	
	private String actorPath;
	
	private String status;
	
	private String errorDetail;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getClassAndMethod() {
		return classAndMethod;
	}

	public void setClassAndMethod(String classAndMethod) {
		this.classAndMethod = classAndMethod;
	}

	public String getActorPath() {
		return actorPath;
	}

	public void setActorPath(String actorPath) {
		this.actorPath = actorPath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorDetail() {
		return errorDetail;
	}

	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}
	
	public String getField(String key) {
		if (fields != null) {
			return fields.get(key);
		} else {
			return null;
		}
	}
	
	public void addField(String key, String value) {
		fields.put(key, value);
	}

}
