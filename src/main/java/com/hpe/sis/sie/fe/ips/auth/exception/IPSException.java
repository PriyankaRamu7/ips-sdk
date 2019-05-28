package com.hpe.sis.sie.fe.ips.auth.exception;

public class IPSException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message=null;
	private String errorCode=null;
	
	
	public IPSException(){
		super();
	}
	
	public IPSException(final String message){
		super(message);
		this.message=message;
	}
	
	public IPSException(final String message ,final String errorCode){
		this.message=message;
		this.errorCode=errorCode;
	}
	
	public IPSException(final String message ,final Throwable cause){
		super(cause);
		this.message=message;
	}
	
	public IPSException(final Throwable cause){
		super(cause);
	}

	public String getMessage() {
		return message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	

}
