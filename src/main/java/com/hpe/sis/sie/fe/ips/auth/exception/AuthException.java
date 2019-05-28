package com.hpe.sis.sie.fe.ips.auth.exception;
//Copyright 2016-2017 Hewlett-Packard Enterprise Company, L.P. All rights reserved.

/**
* Class to represent Transformation exception
*/
public class AuthException extends Exception {

	/**
	 * serial Version Id
	 */
	private static final long serialVersionUID = 1970914588890312163L;

	/**
	 * Exception Message
	 */
	private String message = null;

	/**
	 * Exception Code 
	 */
	private String code = null;

	/**
	 * Default Constructor for Transformation Exception
	 */
	public AuthException() {
		super();
	}

	/**
	 * Constructor for Transformation Exception with Message as input
	 * 
	 * @param message
	 */
	public AuthException(final String message) {
		super(message);
		this.message = message;
	}

	/**
	 * Constructor for Transformation Exception with Message and code as input
	 * 
	 * @param code
	 * @param message
	 */
	public AuthException(final String code, final String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * Throwable Constructor
	 * 
	 * @param cause
	 */
	public AuthException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Getter method for Message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Getter method for Error Code
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}

}
