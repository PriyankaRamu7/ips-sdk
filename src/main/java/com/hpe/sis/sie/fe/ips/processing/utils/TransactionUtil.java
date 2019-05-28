package com.hpe.sis.sie.fe.ips.processing.utils;

import java.util.Calendar;
import java.util.TimeZone;

import org.json.simple.JSONObject;

import com.hpe.sis.sie.fe.ips.auth.service.AuthenticationRequest;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.processing.messages.BackendResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.ServiceInvokeResponse;
import com.hpe.sis.sie.fe.ips.processing.model.CallbackRequest;
import com.hpe.sis.sie.fe.ips.processing.model.Interaction;
import com.hpe.sis.sie.fe.ips.processing.model.InteractionResult;
import com.hpe.sis.sie.fe.ips.processing.model.Transaction;
import com.hpe.sis.sie.fe.ips.scheduler.messages.BotMessage;
import com.hpe.sis.sie.fe.ips.scheduler.messages.BotResponse;
import com.hpe.sis.sie.fe.sisutils.channel.util.ResponseUtils;

public class TransactionUtil {
	
	public static Transaction populateTransaction(Interaction interaction, InteractionResult interactionResult) {
		Transaction transaction = new Transaction();
		
		transaction.setApplication(interaction.getApplicationVO().getId());
		if (interactionResult != null) {
			if(interactionResult.getServiceResponse() != null)
				transaction.setBackEndResponse(interactionResult.getServiceResponse().getBytes());
			transaction.setBackEndResponseTime((int) interactionResult.getBeResponseTime());
		
			transaction.setInteractionApiErrDetail(interactionResult.getBeInvocationErrDetail());
			transaction.setBackEndInvocationStatus(interactionResult.getStatus());
			transaction.setInteractionApiResultCode(interactionResult.getHttpStatusCode());
			transaction.setBackEndInvocationTaskId(interactionResult.getTaskId());
			transaction.setBackEndInvocationStatus(interactionResult.getStatus());
			transaction.setExceptionFlowExecuted(interactionResult.isExceptionFlowExecuted());
		}
		transaction.setInteractionApiRequest(interaction.getRequestBody().replaceAll("[\r\n]+", "").getBytes());
		transaction.setInteractionApiResponseTime(
				(int) (Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() - interaction.getRequestedTime()));
	
		transaction.setBotId(interaction.getBotId());
		transaction.setInteractionApiMethodType(interaction.getMethod());
		transaction.setInteractionChannel(IPSConfig.CHANNEL_NAME);
		transaction.setLocalHost(IPSConfig.HOST_IP);
		transaction.setRemoteHost(interaction.getRemoteHost());
		transaction.setRequestedTime(interaction.getRequestedTime());
		transaction.setServiceId(interaction.getTask().getId());
		transaction.setTransactionId(interaction.getTransactionId());
		transaction.setUserName(interaction.getApplicationVO().getCreatedBy());
		transaction.setVirtualObjects(interaction.getVirtualObject());
		transaction.setTestRequest(interaction.isTestRequest());
		transaction.setObeId(interaction.getApplicationVO().getObeId());
		
		return transaction;

	}

	public static Transaction getTransaction(ServiceInvokeResponse serviceInvokeResponse) {
		Transaction transaction = new Transaction();
		InteractionRequest interactionRequest = serviceInvokeResponse.getInteractionRequest();
		BackendResponse beResponse = serviceInvokeResponse.getBackEndResponse();
		transaction.setApplication(interactionRequest.getApplicationVO().getId());
		if(beResponse.getResponse() != null)
			transaction.setBackEndResponse(beResponse.getResponse().getBytes());
		transaction.setBackEndResponseTime((int) beResponse.getBeResponseTime());
		// transaction.setBotId(botId); //TODO
		transaction.setInteractionApiErrDetail(beResponse.getBeInvocationErrDetail());
		transaction.setBackEndInvocationStatus(beResponse.getStatus());
		transaction.setInteractionApiErrDetail(beResponse.getHttpStatusMessage());
		transaction.setInteractionApiMethodType(interactionRequest.getMethod());
		transaction.setInteractionApiRequest(interactionRequest.getRequestBody().replaceAll("[\r\n]+", "").getBytes());
		transaction.setInteractionApiResponseTime(
				(int) (Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() - interactionRequest.getRequestedTime()));
		transaction.setInteractionApiResultCode(beResponse.getHttpStatusCode());
		transaction.setBackEndInvocationTaskId(beResponse.getTaskId());
		transaction.setBackEndInvocationStatus(beResponse.getStatus());
		transaction.setExceptionFlowExecuted(beResponse.isExceptionFlowExecuted());
		transaction.setInteractionChannel(IPSConfig.CHANNEL_NAME);
		transaction.setLocalHost(IPSConfig.HOST_IP);
		transaction.setRemoteHost(interactionRequest.getRemoteHost());
		transaction.setRequestedTime(interactionRequest.getRequestedTime());
		transaction.setServiceId(interactionRequest.getServiceVO().getTaskId());
		transaction.setTransactionId(interactionRequest.getTransactionId());
		transaction.setUserName(interactionRequest.getApplicationVO().getCreatedBy());
		transaction.setVirtualObjects(interactionRequest.getVirtualObjectId());
		transaction.setBotId(interactionRequest.getBotId());
		transaction.setTestRequest(interactionRequest.isTestRequest());
		transaction.setObeId(interactionRequest.getApplicationVO().getObeId());
		
		return transaction;

	}
	
	public static Transaction populateTransaction(AuthenticationRequest authRequest, String authErrorCode, String authErrorMsg) {

		Transaction transaction = new Transaction();		
		transaction.setApplication(authRequest.getAppId());
		transaction.setInteractionApiErrDetail(authErrorMsg);		
		transaction.setInteractionApiMethodType(authRequest.getRequestMethod());
		transaction.setInteractionApiRequest((authRequest.getRequestBody() != null) ? authRequest.getRequestBody().replaceAll("[\r\n]+", "").getBytes() : "".getBytes());
		transaction.setInteractionApiResponseTime((int) (Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() - authRequest.getRequestedTime()));
		transaction.setInteractionApiResultCode(Integer.parseInt(authErrorCode));	
		transaction.setInteractionChannel(IPSConfig.CHANNEL_NAME);
		transaction.setLocalHost(IPSConfig.HOST_IP);
		transaction.setRemoteHost(authRequest.getRemoteHost());
		transaction.setRequestedTime(authRequest.getRequestedTime());
		transaction.setServiceId(authRequest.getServiceId());
		transaction.setTransactionId(authRequest.getTransactionId());
		transaction.setUserName(authRequest.getCreatedBy());
		transaction.setVirtualObjects(authRequest.getVirtualObject());
		transaction.setBotId(authRequest.getBotId());
		transaction.setTestRequest(authRequest.isRequestMadeFromConsole());
		transaction.setObeId(authRequest.getObeID());

		return transaction;
	}

	public static Transaction populateTransaction(Interaction interaction , String ipsErrorCode, String ipsErrorMsg) {

		Transaction transaction = new Transaction();		
		transaction.setApplication(interaction.getApplicationVO().getId());
		transaction.setInteractionApiErrDetail(ipsErrorMsg);		
		transaction.setInteractionApiMethodType(interaction.getMethod());
		transaction.setInteractionApiRequest((interaction.getRequestBody() != null) ? interaction.getRequestBody().replaceAll("[\r\n]+", "").getBytes() : "".getBytes());
		transaction.setInteractionApiResponseTime((int) (Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() - interaction.getRequestedTime()));
		transaction.setInteractionApiResultCode(Integer.parseInt(ipsErrorCode));	
		transaction.setInteractionChannel(IPSConfig.CHANNEL_NAME);
		transaction.setLocalHost(IPSConfig.HOST_IP);
		transaction.setRemoteHost(interaction.getRemoteHost());
		transaction.setRequestedTime(interaction.getRequestedTime());
		transaction.setServiceId(interaction.getTask().getId());
		transaction.setTransactionId(interaction.getTransactionId());
		transaction.setUserName(interaction.getApplicationVO().getCreatedBy());
		transaction.setVirtualObjects(interaction.getVirtualObject());
		transaction.setBotId(interaction.getBotId());
		transaction.setTestRequest(interaction.isTestRequest());
		transaction.setObeId(interaction.getApplicationVO().getObeId());

		return transaction;
	}
	
	public static Transaction getTransaction(BotResponse botResponse) {
		Transaction transaction = new Transaction();
		BotMessage botMessage = botResponse.getBotMessage();
		BackendResponse beResponse = botResponse.getBackEndResponse();
		transaction.setApplication(botMessage.getApplication().getId());
		if(beResponse.getResponse() != null)
			transaction.setBackEndResponse(beResponse.getResponse().getBytes());
		transaction.setBackEndResponseTime((int) beResponse.getBeResponseTime());
		transaction.setInteractionApiErrDetail(beResponse.getBeInvocationErrDetail());
		transaction.setBackEndInvocationStatus(beResponse.getStatus());
		transaction.setInteractionApiErrDetail(beResponse.getHttpStatusMessage());
		transaction.setInteractionApiMethodType("POST");
		transaction.setInteractionApiResponseTime(
				(int) (Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() - botMessage.getRequestedTime()));
		transaction.setInteractionApiResultCode(beResponse.getHttpStatusCode());
		transaction.setBackEndInvocationTaskId(beResponse.getTaskId());
		transaction.setBackEndInvocationStatus(beResponse.getStatus());
		transaction.setExceptionFlowExecuted(beResponse.isExceptionFlowExecuted());
		transaction.setInteractionChannel(IPSConfig.CHANNEL_NAME);
		transaction.setLocalHost(IPSConfig.HOST_IP);
		transaction.setRemoteHost(botMessage.getRemoteHost());
		transaction.setRequestedTime(botMessage.getRequestedTime());
		transaction.setServiceId(botMessage.getServiceId());
		transaction.setTransactionId(botMessage.getTransactionId());
		transaction.setUserName(botMessage.getApplication().getCreatedBy());
		transaction.setBotId(botMessage.getBotId());
		transaction.setTestRequest(botMessage.isTestRequest());
		transaction.setObeId(botMessage.getApplication().getObeId());
		
		return transaction;

	}

	public static Transaction populateTransaction(String beResponse,
			CallbackRequest callbackRequest) {
		Transaction transaction = new Transaction();
		BackendResponse response = getBackEndResponse(beResponse);
		transaction.setApplication(callbackRequest.getApplication().getId());
		transaction.setBackEndInvocationStatus(response.getStatus());
		transaction.setBackEndInvocationTaskId(response.getTaskId());
		transaction.setBackEndResponse(response.getResponse().getBytes());
		//transaction.setBackEndResponseTime(backEndResponseTime);
		transaction.setInteractionApiResultCode(response.getHttpStatusCode());
		transaction.setInteractionApiErrDetail(response.getHttpStatusMessage());
		transaction.setInteractionChannel(callbackRequest.getChannelName());
		transaction.setInteractionApiMethodType("GET");
		transaction.setLocalHost(IPSConfig.HOST_IP);
		transaction.setObeId(callbackRequest.getApplication().getObeId());
		transaction.setRemoteHost(callbackRequest.getRemoteHost());
		transaction.setRequestedTime(callbackRequest.getRequestedTime());
		//transaction.setServiceId(serviceId);
		transaction.setTestRequest(false);
		transaction.setTransactionId(callbackRequest.getTransactionId());
		transaction.setUserName(callbackRequest.getApplication().getCreatedBy());
		transaction.setVirtualObjects(callbackRequest.getVirtualObjectId());
		
		return transaction;
	}

	
	public static Transaction populateTransaction(CallbackRequest callbackRequest,String ipsErrorCode, String ipsErrorMsg) {
		Transaction transaction = new Transaction();
		//BackendResponse response = getBackEndResponse(beResponse);
		transaction.setApplication(callbackRequest.getApplication().getId());
		//transaction.setBackEndInvocationStatus(response.getStatus());
		//transaction.setBackEndInvocationTaskId(response.getTaskId());
		//transaction.setBackEndResponse(response.getResponse().getBytes());
		//transaction.setBackEndResponseTime(backEndResponseTime);
		//transaction.setInteractionApiResultCode(response.getHttpStatusCode());
		//transaction.setInteractionApiErrDetail(response.getHttpStatusMessage());
		transaction.setInteractionApiErrDetail(ipsErrorMsg);
		transaction.setInteractionApiResultCode(Integer.parseInt(ipsErrorCode));
		transaction.setInteractionChannel(callbackRequest.getChannelName());
		transaction.setInteractionApiMethodType("GET");
		transaction.setLocalHost(IPSConfig.HOST_IP);
		transaction.setObeId(callbackRequest.getApplication().getObeId());
		transaction.setRemoteHost(callbackRequest.getRemoteHost());
		transaction.setRequestedTime(callbackRequest.getRequestedTime());
		//transaction.setServiceId(serviceId);
		transaction.setTestRequest(false);
		transaction.setTransactionId(callbackRequest.getTransactionId());
		transaction.setUserName(callbackRequest.getApplication().getCreatedBy());
		transaction.setVirtualObjects(callbackRequest.getVirtualObjectId());
		
		return transaction;
	}
	
	
	private static BackendResponse getBackEndResponse(String beResponse) {
		BackendResponse response = new BackendResponse();
		JSONObject jsonObj = ResponseUtils.getJsonObj(beResponse);
		String status = null;
		String taskId = null;
		if(null!=jsonObj.get("status")){
			status = (String) jsonObj.get("status");
			if("PROCESSED".equals(status)){
				response.setHttpStatusCode(200);
			}
			else{
				response.setHttpStatusCode(500);
				response.setHttpStatusMessage("Error in getting response");
			}
			response.setStatus(status);
		}
		if(null!=jsonObj.get("taskId")){
			taskId = (String) jsonObj.get("taskId");
			response.setTaskId(taskId);
		}
		
		
		response.setResponse(beResponse);
		
		return response;
	}
	
}
