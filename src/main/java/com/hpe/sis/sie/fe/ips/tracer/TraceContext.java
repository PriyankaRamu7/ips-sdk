package com.hpe.sis.sie.fe.ips.tracer;

import java.util.HashMap;
import java.util.Map;

public class TraceContext {
	
	private String transactionId;
	
	private String event;
	
	private Map<String, String> fields;
	
	private String timeStamp;
	
	private String classAndMethod;
	
	private String actorPath;
	
	private String status;
	
	private String errorDetail;

	public TraceContext(String transactionId, String event, String classAndMethod) {
		super();
		this.setTransactionId(transactionId);
		this.setEvent(event);
		this.setClassAndMethod(classAndMethod);
		this.fields = new HashMap<String, String>();
	}
	
	public TraceContext(String transactionId, String event, String classAndMethod, String actorPath) {
		super();
		this.setTransactionId(transactionId);
		this.setEvent(event);
		this.setClassAndMethod(classAndMethod);
		this.setActorPath(actorPath);
		this.fields = new HashMap<String, String>();
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
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

	

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TraceContext [transactionId=" + transactionId + ", event="
				+ event + ", timeStamp=" + timeStamp + ", classAndMethod="
				+ classAndMethod + ", actorPath=" + actorPath + ", status="
				+ status + ", errorDetail=" + errorDetail + "]";
	}
	
	

}
