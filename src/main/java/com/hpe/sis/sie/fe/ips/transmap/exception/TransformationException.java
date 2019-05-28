// Copyright 2016-2017 Hewlett-Packard Enterprise Company, L.P. All rights reserved.
package com.hpe.sis.sie.fe.ips.transmap.exception;

/**
 * Class to represent Transformation exception
 */
public class TransformationException extends Exception {

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
	public TransformationException() {
		super();
	}

	/**
	 * Constructor for Transformation Exception with Message as input
	 * 
	 * @param message
	 */
	public TransformationException(final String message) {
		super(message);
		this.message = message;
	}

	/**
	 * Constructor for Transformation Exception with Message and code as input
	 * 
	 * @param code
	 * @param message
	 */
	public TransformationException(final String code, final String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * Throwable Constructor
	 * 
	 * @param cause
	 */
	public TransformationException(final Throwable cause) {
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
