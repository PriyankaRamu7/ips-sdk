package com.hpe.sis.sie.fe.ips.auth.exception;

public enum IpsErrorCode {

	
	AUTH_DEPLOYMENT_ID_INVALID("SIS_AUTH_001"),
	AUTH_DEPLOYMENT_ID_CANNOT_BE_NULL("SIS_AUTH_002"),
	APPLICATION_VALIDITY_ERROR_CODE("SIS_AUTH_003"),
	APP_NOT_COMPLETED_ERROR_CODE("SIS_AUTH_004"),
	APP_SUSPENDED_ERROR_CODE("SIS_AUTH_005");
	
	private String errorCode;
	
	private  IpsErrorCode(String errCode){
		this.errorCode=errCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
	
	
}
