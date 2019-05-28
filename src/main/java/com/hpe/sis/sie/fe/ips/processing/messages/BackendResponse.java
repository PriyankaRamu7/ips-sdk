package com.hpe.sis.sie.fe.ips.processing.messages;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class BackendResponse.
 */
public class BackendResponse implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5754687045007075248L;
	
	/** status of the SIE:BE task-engine's task invocation. */
	private String status;
	
	/** unique id for the SIE:BE task invocation. */
	private String taskId;
	
	/** HTTP headers returned by the SIE:BE task execution. */
	private Map<String, List<String>> headers;
	
	/** message body returned by the SIE:BE  task execution. */
	private String response;
	
	/** The updated response with metadata. */
	private String updatedResponseWithMetaData;
	
	/** The http status code. */
	private int httpStatusCode;
	
	/** The http statu message. */
	private String httpStatusMessage;

	/** The be response time. */
	private long beResponseTime;
	
	/** The be invocation err detail. */
	private String beInvocationErrDetail;
	
	/** The isExceptionFlowExecuted returned by SIE:BE. */
	private boolean isExceptionFlowExecuted;

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}


	/**
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	/**
	 * Gets the task id.
	 *
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}


	/**
	 * Sets the task id.
	 *
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}


	/**
	 * Gets the headers.
	 *
	 * @return the headers
	 */
	public Map<String, List<String>> getHeaders() {
		return headers;
	}


	/**
	 * Sets the headers.
	 *
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	/**
	 * Gets the http status code.
	 *
	 * @return the http status code
	 */
	public int getHttpStatusCode() {
		return httpStatusCode;
	}


	/**
	 * Sets the http status code.
	 *
	 * @param httpStatusCode the new http status code
	 */
	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}


	/**
	 * Gets the http status message.
	 *
	 * @return the http status message
	 */
	public String getHttpStatusMessage() {
		return httpStatusMessage;
	}


	/**
	 * Sets the http status message.
	 *
	 * @param httpStatusMessage the new http status message
	 */
	public void setHttpStatusMessage(String httpStatusMessage) {
		this.httpStatusMessage = httpStatusMessage;
	}


	/**
	 * Gets the be response time.
	 *
	 * @return the be response time
	 */
	public long getBeResponseTime() {
		return beResponseTime;
	}


	/**
	 * Sets the be response time.
	 *
	 * @param beResponseTime the new be response time
	 */
	public void setBeResponseTime(long beResponseTime) {
		this.beResponseTime = beResponseTime;
	}


	/**
	 * Gets the be invocation err detail.
	 *
	 * @return the be invocation err detail
	 */
	public String getBeInvocationErrDetail() {
		return beInvocationErrDetail;
	}


	/**
	 * Sets the be invocation err detail.
	 *
	 * @param beInvocationErrDetail the new be invocation err detail
	 */
	public void setBeInvocationErrDetail(String beInvocationErrDetail) {
		this.beInvocationErrDetail = beInvocationErrDetail;
	}


	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}


	/**
	 * Sets the response.
	 *
	 * @param response the new response
	 */
	public void setResponse(String response) {
		this.response = response;
	}


	/**
	 * Gets the updated response with meta data.
	 *
	 * @return the updated response with meta data
	 */
	public String getUpdatedResponseWithMetaData() {
		return updatedResponseWithMetaData;
	}


	/**
	 * Sets the updated response with meta data.
	 *
	 * @param updatedResponseWithMetaData the new updated response with meta data
	 */
	public void setUpdatedResponseWithMetaData(String updatedResponseWithMetaData) {
		this.updatedResponseWithMetaData = updatedResponseWithMetaData;
	}


	/**
	 * Checks if is exception flow executed.
	 *
	 * @return true, if is exception flow executed
	 */
	public boolean isExceptionFlowExecuted() {
		return isExceptionFlowExecuted;
	}


	/**
	 * Sets the exception flow executed.
	 *
	 * @param isExceptionFlowExecuted the new exception flow executed
	 */
	public void setExceptionFlowExecuted(boolean isExceptionFlowExecuted) {
		this.isExceptionFlowExecuted = isExceptionFlowExecuted;
	}	
	
}
