// Copyright 2016-2017 Hewlett-Packard Enterprise Company, L.P. All rights reserved.
package com.hpe.sis.sie.fe.ips.transmap.util;

/**
 * 
 * Class to describe Transformation Layer Constants
 *
 */
public final class TransformationConstants {

	private TransformationConstants(){}

	/**
	 * A constant to represent HTTP Header
	 */
	public final static String HTTP_HEADER = "1";

	/**
	 * A constant to represent Http Request Boddy
	 */
	public final static String HTTP_REQUEST_BODY = "2";

	/**
	 * A constant to represent Http Query Parameter
	 */
	public final static String HTTP_QUERY = "3";

	/**
	 * A constant to represent Http Request Paramaeter
	 */
	public final static String HTTP_REQUEST_PARAMETER = "4";

	/**
	 * Constant to represent Service Id
	 */
	public final static String SERVICE_ID = "SERVICE_ID";

	/**
	 * Error code for Http Header not found
	 */
	public final static String HEADER_NOT_FOUND_ERR_CODE = "TR_HEADER_NOT_FOUND_001";

	/**
	 * Error Message for Header not found
	 */
	public final static String HEADER_NOT_FOUND_ERR_CODE_MSG = "Required Header not Found or Empty: ";

	/**
	 * Error code for Http Query Parameter not found
	 */
	public final static String QUERY_NOT_FOUND_ERR_CODE = "TR_QUERY_NOT_FOUND_001";

	/**
	 * Error Message for Http Query Parameter not found
	 */
	public final static String QUERY_NOT_FOUND_ERR_CODE_MSG = "Query Parameter not Found or Empty: ";

	/**
	 * Error code for Http Request Parameter not found
	 */
	public final static String REQ_PARAM_NOT_FOUND_ERR_CODE = "TR_REQ_PARAM_NOT_FOUND_001";

	/**
	 * Error Message for Http Request Parameter not found
	 */
	public final static String REQ_PARAM_NOT_FOUND_ERR_CODE_MSG = "Request Parameter not Found or Empty: ";

	/**
	 * Error code if Http Request Body is not a valid json
	 */
	public final static String BODY_NOT_JSON = "BODY_NOT_JSON_001";

	/**
	 * Error message if Http Request Body is not a valid json
	 */
	public final static String BODY_NOT_JSON_001 = "Not a valid Json Request";
	
	public final static String GET = "GET";
	
	public final static String POST = "POST";
	
	public final static String PUT = "PUT";
	
	public final static String DELETE = "DELETE";
	
	public final static String PATCH = "PATCH";
	
	public final static String SISHEADERS = "__sisHeaders__";
	
	public final static String ACTIVITY_STREAM_ID = "activityStreamId";
	
	public final static String CHANNEL = "channel";
	
	public final static String INTERACTION_CONTEXT_ID = "interactionContextId";
	
	public final static String REQUEST_METHOD = "requestMethod";

	public static final String QUERY_PARAM = "_query_param_";
}
