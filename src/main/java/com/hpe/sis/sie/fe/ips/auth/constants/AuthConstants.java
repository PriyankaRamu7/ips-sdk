package com.hpe.sis.sie.fe.ips.auth.constants;

public class AuthConstants {

	public static final String APPLICATION_NODE = "app";

	/**
	 * Error code for Authentication Failure
	 */
	public final static String AUTHENTICATION_FAILURE_ERR_CODE = "AUTH_001";


	/**
	 * Error code for Authentication Failure
	 */
	public final static String AUTHENTICATION_FAILURE_ERR_MSG = "Authentication Failed";


	/**
	 * Error code for Authorization Failure
	 */
	public final static String AUTHORTIZATION_FAILURE_ERR_CODE = "AUTH_002";

	/**
	 * Error code for Authorization Failure
	 */
	public final static String AUTHORTIZATION_FAILURE_ERR_MSG = "Authorization Failed";

	/**
	 * Error code for Configuration not found
	 */
	public final static String OBE_CONFIGURATION_NOT_FOUND_ERR_CODE = "AUTH_005";

	/**
	 * Error code for DSS Connection
	 */
	public final static String DSS_SERVER_ERR_CODE = "AUTH_003";
	/**
	 * Error code for DSS Connection Message
	 */
	public final static String DSS_SERVER_ERR_MSG = "DSS Server Error";

	/**
	 * Error code for Internal server error
	 */
	public final static String INTERNAL_SERVER_ERR_CODE = "AUTH_006";
	/**
	 * Error code for Internal Message
	 */
	public final static String INTERNAL_SERVER_ERR_MSG = "Internal Server Error";

	/**
	 * Error code for DSS Connection Parameters
	 */
	public final static String DSS_CONFIGURATION_MISSING_ERR_CODE = "AUTH_004";
	/**
	 * Error code for DSS Connection Message
	 */
	public final static String DSS_CONFIGURATION_MISSING_ERR_MSG = "DSS Configuration parameters missing";

	/**
	 * Error code for Authorization Method not allowed
	 */
	public final static String AUTHORTIZATION_METHOD_NOT_ALLOWED_ERR_CODE = "AUTH_007";

	/**
	 * Error code for Authorization Method not allowed
	 */
	public final static String AUTHORTIZATION_METHOD_NOT_ALLOWED_ERR_MSG = "Authorization Error - Method Not allowed";

	/**
	 * Error code for Authorization Day not allowed
	 */
	public final static String AUTHORTIZATION_DAY_NOT_ALLOWED_ERR_CODE = "AUTH_008";

	/**
	 * Error code for Authorization Day not allowed
	 */
	public final static String AUTHORTIZATION_DAY_NOT_ALLOWED_ERR_MSG = "Authorization Error - Permission denied in this day";

	/**
	 * Error code for Authorization Platform not allowed
	 */
	public final static String AUTHORTIZATION_PLATFORM_NOT_ALLOWED_ERR_CODE = "AUTH_009";

	/**
	 * Error code for Authorization Platform not allowed
	 */
	public final static String AUTHORTIZATION_PLATFORM_NOT_ALLOWED_ERR_MSG = "Authorization Error - Not allowed for this platform";

	/**
	 * Error code for Authorization Conversation type not allowed
	 */
	public final static String AUTHORTIZATION_CONVERSATION_TYPE_NOT_ALLOWED_ERR_CODE = "AUTH_010";

	/**
	 * Error code for Authorization Conversation type not allowed
	 */
	public final static String AUTHORTIZATION_CONVERSATION_TYPE_NOT_ALLOWED_ERR_MSG = "Authorization Error - Conversation type not allowed";

	/**
	 * Error code for Authorization Time not allowed
	 */
	public final static String AUTHORTIZATION_TIME_NOT_ALLOWED_ERR_CODE = "AUTH_011";

	/**
	 * Error code for Authorization Time not allowed
	 */
	public final static String AUTHORTIZATION_TIME_NOT_ALLOWED_ERR_MSG = "Authorization Error - Not within allowed time";

	/**
	 * Error code for Invalid Apikey
	 */
	public final static String MISSING_APIKEY_IN_REQUEST = "AUTH_012";

	/**
	 * Error code for Invalid AppId
	 */
	public final static String INVALID_APPID_ERR_CODE = "AUTH_013";


	/**
	 * Error code for Invalid APIPID
	 */
	public final static String INVALID_APPID_ERR_MSG = "Invalid APPID";

	/**
	 * Error code for Invalid VirtualObject
	 */
	public final static String INVALID_VirtualObject_ERR_CODE = "AUTH_014";


	/**
	 * Error code for Invalid APIPID
	 */
	public final static String INVALID_VirtualObject_ERR_MSG = "Invalid VirtualObject";

	/**
	 * Error code for Invalid Conversation Type
	 */
	public final static String INVALID_CONVERSATION_TYPE_ERR_CODE = "AUTH_015";


	/**
	 * Error code for Invalid Conversation Type
	 */
	public final static String INVALID_CONVERSATION_TYPE_ERR_MSG = "Invalid Conversation Type";

	/**
	 * Error code for Invalid Platform value
	 */
	public final static String INVALID_PLATFORM_TYPE_ERR_CODE = "AUTH_017";


	/**
	 * Error code for Invalid PLATFORM
	 */
	public final static String INVALID_PLATFORM_TYPE_ERR_MSG = "Invalid PLATFORM";

	/**
	 * Error code for Invalid Method value
	 */
	public final static String INVALID_METHOD_TYPE_ERR_CODE = "AUTH_016";


	/**
	 * Error code for Invalid METHOD
	 */
	public final static String INVALID_METHOD_TYPE_ERR_MSG = "Invalid METHOD";


	/**
	 * Error code for Invalid Smartkey
	 */
	public final static String INVALID_SMARTKEY_ERR_CODE = "AUTH_018";

	
	/**
	 * Error code for DEPLOYMENT_ID_MISSMATCH_ERR_CODE Failure
	 */
	public final static String DEPLOYMENT_ID_MISSMATCH_ERR_CODE = "AUTH_021";
	
	/**
	 * Msg for DEPLOYMENT_ID_MISSMATCH_MSG Failure
	 */
	public final static String DEPLOYMENT_ID_MISSMATCH_MSG = "Application does not belongs to this territory, Deployment ID mismatch.";
	
	/**
	 * Error code for DEPLOYMENT_ID_NOT_FOUND_ERR_CODE Failure
	 */
	public final static String DEPLOYMENT_ID_NOT_FOUND_ERR_CODE = "AUTH_022";
	
	/**
	 * Msg for DEPLOYMENT_ID_NOT_FOUND_MSG Failure
	 */
	public final static String DEPLOYMENT_ID_NOT_FOUND_MSG= "Deployment ID is not found either in the Application or in the Channel";

		/**
		 * Error code for APPID_NOT_FOUND_ERR_CODE Failure
		 */
	public final static String APPID_NOT_FOUND_ERR_MSG = "AppID not found";

	public final static String ERROR_CODE = "ERROR_CODE";

	public final static String ERROR_MESSGAE = "ERROR_MESSAGE";

    public static final String CONFIGURATION_NODE = "configuration";
    public static final String OBE = "obe";
    public static final String DELIMITER = ":";
    public static final String APIKEY = "apikey";
    public static final String SMARTKEY = "smartkey";

    /**
	 * Error code for Data Not Found
	 */
	public final static String DATA_NOT_FOUND_ERR_CODE = "SIS_SM_04";
	/**
	 * Error code for Data Not Found error Message
	 */
	public final static String DATA_NOT_FOUND_ERR_MSG = "DATA Not Found";
	
	public final static String APPLICATION_VALIDITY_ERROR_CODE = "AUTH_023";
	
	public final static String APPLICATION_VALIDITY_ERROR_MSG = "Application validity has expired";
	
	public final static String APP_NOT_COMPLETED_ERROR_CODE = "AUTH_024";
	
	public final static String APP_NOT_COMPLETED_ERROR_MSG = "Application is not marked as completed";
	
	public final static String APP_SUSPENDED_ERROR_CODE = "AUTH_025";
	
	public final static String APP_SUSPENDED_ERROR_MSG = "Application is suspended";
	
	public final static String APIKEY_NOT_CREATED = "AUTH_026";
	
	public final static String APIKEY_NOT_CREATED_MSG = "APIKey not created for this application";
	
	public static final String ALL_ACTIVITY_STREAM_CONV_TYPE = "All Activity Streams";
	
	public static final String AUTH_SUCCESS = "AUTH_SUCCESS";
	
	public final static String SMARTKEY_MISMATCH_ERR_CODE = "AUTH_027";
	
	public final static String APIKEY_MISMATCH_ERR_CODE = "AUTH_028";
	
	public final static String BAD_JSON_PAYLOAD="AUTH_029";
	
	public final static String MISSING_ACTIVITYSTREAM_ID="AUTH_030";
	
	public final static String MISMATCH_ACTIVITYSTREAM_ID="AUTH_031";
	
	public final static String SECURITY_ACTOR_ERROR = "AUTH_032";
	
	public final static String VOID_NOT_FOUND = "AUTH_033";
	
	public final static String BOTID_NOT_FOUND = "AUTH_034";
	
	public final static String INVALID_BOT_ID_ERR_CODE="AUTH_035";
	
	public final static String AUTHORTIZATION_RESOURCE_URI_NOT_ALLOWED_ERR_CODE="AUTH_036";
	
}
