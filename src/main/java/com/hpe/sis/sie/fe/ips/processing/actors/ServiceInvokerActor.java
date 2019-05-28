package com.hpe.sis.sie.fe.ips.processing.actors;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.hpe.sis.sie.fe.dss.DSSService;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.processing.client.HttpClient;
import com.hpe.sis.sie.fe.ips.processing.client.HttpClientUtil;
import com.hpe.sis.sie.fe.ips.processing.messages.BackendResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.ServiceInvokeRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.ServiceInvokeResponse;
import com.hpe.sis.sie.fe.ips.processing.utils.ProcessingPattern;
import com.hpe.sis.sie.fe.ips.responsecache.ResponseCacheConstants;
import com.hpe.sis.sie.fe.ips.responsecache.ResponseCacheUtils;
import com.hpe.sis.sie.fe.ips.responsecache.ResponseCacheVO;
import com.hpe.sis.sie.fe.ips.scheduler.messages.BotMessage;
import com.hpe.sis.sie.fe.ips.scheduler.messages.BotResponse;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;
import com.hpe.sis.sie.fe.ips.utils.activitystreams.util.ActivityStreamValidator;
import com.hpe.sis.sie.fe.ips.utils.json.util.JsonUtility;
import com.hpe.sis.sie.fe.ips.utils.sieconfiguration.service.SIEConfigurationService;
import com.hpe.sis.sie.fe.sisutils.channel.util.ChannelUtility;
import com.hpe.sis.sie.fe.sisutils.channel.util.ResponseUtils;

import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ServiceInvokerActor extends UntypedAbstractActor {

	private OkHttpClient httpClient = HttpClient.getInstance();
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private ServiceInvokeRequest serviceInvokeRequest;
	private ServiceInvokeResponse serviceResponse;
	/*
	 * @Override public void preStart() throws Exception { // TODO
	 * Auto-generated method stub super.preStart();
	 * System.out.println(getSelf().path() + " preStart called"); }
	 * 
	 * @Override public void preRestart(Throwable reason, Optional<Object>
	 * message) throws Exception { // TODO Auto-generated method stub
	 * super.preRestart(reason, message); System.out.println(getSelf().path() +
	 * " preRestart called"); }
	 * 
	 * @Override public void postRestart(Throwable reason) throws Exception { //
	 * TODO Auto-generated method stub super.postRestart(reason);
	 * System.out.println(getSelf().path() + " postRestart called"); }
	 */

	@Override
	public void onReceive(Object message) throws Throwable {
		BackendResponse beRespObj = new BackendResponse();
		long startTime = 0;
		TraceContext serviceTraceContext = null;
		TraceContext botTraceContext = null;
		BotMessage botMessage = null;
		BotResponse botResponse = null;
		try {
			if (message instanceof ServiceInvokeRequest) {
				serviceInvokeRequest = (ServiceInvokeRequest) message;
				serviceResponse = new ServiceInvokeResponse();
				String transactionId = serviceInvokeRequest.getInteractionRequest().getTransactionId();
				serviceTraceContext = Tracer.start(transactionId, " ServiceInvokerActor - Invoking service", "ServiceInvokerActor.onReceive", getSelf().path().toString());
				log.info(getSelf().path() + " Received ServiceInvokeRequest for Transaction: " + transactionId);

				String url = serviceInvokeRequest.getServiceURL();
				startTime = System.currentTimeMillis();
				beRespObj = callBackEnd(url, transactionId, serviceInvokeRequest.getInteractionRequest().getInteractionContextId(), startTime, beRespObj);	
				
				if (ProcessingPattern.Asynchronous.getPattern()
						.equals(serviceInvokeRequest.getInteractionRequest().getProcessingPattern())) {
					persistBackendResponse(serviceInvokeRequest.getInteractionRequest().getTransactionId(),
							beRespObj);
				}
				if (serviceInvokeRequest.getInteractionRequest().getMethod().equals(ResponseCacheConstants.GET_METHOD)
						&& serviceInvokeRequest.getInteractionRequest().isResponseCachingEnabled() ) {
						// Response caching is enabled
						// Persist response in cache
						TraceContext context1 = Tracer.start(transactionId, "ServiceInvokerActor - Caching Response", "ServiceInvokerActor.onReceive", getSelf().path().toString());
						InteractionRequest interactionRequest = serviceInvokeRequest.getInteractionRequest();
						if (ChannelUtility.BOT_ONDEMAND.equals(IPSConfig.CHANNEL_NAME)) {
							ResponseCacheVO responseCache = ResponseCacheUtils.fetchResponseCacheEntity(interactionRequest.getBotId(), interactionRequest.getMethod(), interactionRequest.getQueryString(),
									interactionRequest.getCachedResponseTTL(), interactionRequest.getRequestURI());
									ResponseCacheUtils.insertResponseCacheDetails(responseCache, JsonUtility.javaToJson(beRespObj));
						} else {
							ResponseCacheVO responseCache = ResponseCacheUtils.fetchResponseCacheEntity(interactionRequest.getVirtualObjectId(), interactionRequest.getMethod(), interactionRequest.getQueryString(),
									interactionRequest.getCachedResponseTTL(), interactionRequest.getRequestURI());
									ResponseCacheUtils.insertResponseCacheDetails(responseCache, JsonUtility.javaToJson(beRespObj));
						}
						Tracer.end(context1, "ServiceInvokerActor - Cached Response",TraceConstants.SUCCESS);
					}
				
			} else if (message instanceof BotMessage) {
				botMessage = (BotMessage) message;
				Map<String, String> traceDataMap = new HashMap<>();
				traceDataMap.put("BOT_ID", botMessage.getBotId());
				botTraceContext = Tracer.start(botMessage.getTransactionId(), " ServiceInvokerActor - Invoking service for Scheduled BOT", "ServiceInvokerActor.onReceive", getSelf().path().toString(), traceDataMap);
				botResponse = new BotResponse();
				String transactionId = botMessage.getTransactionId();
				log.info(getSelf().path() + " Received ServiceInvokeRequest for Transaction: " + transactionId);

				String url = botMessage.getServiceURL();
				startTime = System.currentTimeMillis();
				beRespObj = callBackEnd(url, transactionId, botMessage.getInteractionContextId(), startTime, beRespObj);
				
			} else {
				unhandled(message);
				System.out.println("ServiceInvokerActor received unknown message for service invocation" + message);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception occured in Service Invoker Actor: " + e.getMessage());
			beRespObj.setHttpStatusCode(500);
			beRespObj.setStatus("FAILED");
			beRespObj.setBeInvocationErrDetail(e.getMessage());
			beRespObj.setBeResponseTime(System.currentTimeMillis() - startTime);
		
		} finally {
			if(ChannelUtility.BOT_SCHEDULER.equals(IPSConfig.CHANNEL_NAME)) {
				if (botMessage != null && botResponse != null) {
					botResponse.setBackEndResponse(beRespObj);
					botResponse.setBotMessage(botMessage);
					Tracer.end(botTraceContext, "ServiceInvokerActor - Completed service execution for Scheduled BOT", TraceConstants.SUCCESS);
					getSender().tell(botResponse, getSelf());
			} 
			}else {
				if (serviceInvokeRequest != null && serviceResponse != null) {
					serviceResponse.setBackEndResponse(beRespObj);
					serviceResponse.setInteractionRequest(serviceInvokeRequest.getInteractionRequest());
					Tracer.end(serviceTraceContext, "ServiceInvokerActor - Completed service execution", TraceConstants.SUCCESS);
					getSender().tell(serviceResponse, getSelf());
				}
			}
		}
	}
	private void createBackendResponse(Response response, String responseBody, BackendResponse beRespObj,
			String transactionId, String interactionContextId) {

		String updatedResponseWithMetaData = (responseBody != null)
				? ResponseUtils.updateResponseWithMetaData(responseBody, transactionId, interactionContextId) : null;

		beRespObj.setUpdatedResponseWithMetaData(updatedResponseWithMetaData);
		beRespObj.setResponse(responseBody);
		JSONParser parser = new JSONParser();
		JSONObject responseObj = new JSONObject();
		String taskId = null;
		String status = null;
		boolean isExceptionFlowExecuted = false;
		try {
			responseObj = (JSONObject) parser.parse(beRespObj.getUpdatedResponseWithMetaData());
			if (null != responseObj.get("taskId")) {
				taskId = responseObj.get("taskId").toString();
				beRespObj.setTaskId(taskId);
			} else {
				System.out.println("ERROR: No taskID found in BE response");
			}
			if (null != responseObj.get("status")) {
				status = responseObj.get("status").toString();
				beRespObj.setStatus(status);
			}
			if (null != responseObj.get("isExceptionFlowExecuted")) {
				isExceptionFlowExecuted = (boolean) responseObj.get("isExceptionFlowExecuted");
				beRespObj.setExceptionFlowExecuted(isExceptionFlowExecuted);
			} else {
				System.out.println("ERROR: No status found in BE response");
			}
			String statusCode = String.valueOf(response.code());
			if (statusCode.startsWith("2")) {
				beRespObj.setStatus("PROCESSED");
			} else {
				beRespObj.setStatus("FAILED");
			}
			Headers headers = response.headers();
			beRespObj.setHeaders(headers.toMultimap());
			beRespObj.setHttpStatusCode(response.code());
			beRespObj.setHttpStatusMessage(response.message());
		} catch (ParseException p) {
			System.out.println("ERROR: unable to parse BE response");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: Error while reading back end response");
		}

	}

	private void persistBackendResponse(String transactionId, BackendResponse beResponseObj) {
		TraceContext traceContext = Tracer.start(transactionId, "ServiceInvokerActor - Persist Asynchronous Interaction Response in DSS", "ServiceInvokerActor.persistBackendResponse", getSelf().path().toString());
		try {
			String key = IPSConfig.CHANNEL_NAME + ":response:" + transactionId;

			Long result;
			long unixTime = (System.currentTimeMillis() / 1000L) + IPSConfig.BE_RESPONSE_TTL;

			String messageJSON = ActivityStreamValidator.getASJson(beResponseObj.getUpdatedResponseWithMetaData());

			result = DSSService.setValueInMap(key, beResponseObj.getTaskId(), messageJSON, unixTime);
			
			if (result == 0) {
				log.error("Unable to persist Asynchronous Interaction Response in DSS");
				Tracer.end(traceContext,"ServiceInvokerActor - Persist Asynchronous Interaction Response in DSS" , TraceConstants.FAILED);
			} else {
				Tracer.end(traceContext,"ServiceInvokerActor - Persist Asynchronous Interaction Response in DSS" , TraceConstants.SUCCESS);
			}
		} catch (Exception e) {
			log.error("ERROR: Unable to persis Asynchronous Interaction's BE response in DSS", e);
			traceContext.setErrorDetail("Unable to persis Asynchronous Interaction's BE response in DSS - " + e.getMessage());
			Tracer.end(traceContext,"ServiceInvokerActor - Persist Asynchronous Interaction Response in DSS" , TraceConstants.FAILED);
		}

	}

	/*
	 * @Override public void postStop() throws Exception { // TODO
	 * Auto-generated method stub super.postStop();
	 * System.out.println(getSelf().path() + " postStop called"); }
	 */

	
	private BackendResponse callBackEnd(String url, String transactionId, String interactionContextId, long startTime, BackendResponse beRespObj) throws Exception {

		String responseBody = null;
		long   endTime = 0;

		try {
			String beSecretToken = SIEConfigurationService.sieconfigVO.getBeSecretToken();
			Response response = null;
			log.info("Complete Task URL to invoke : " + url);
			
			// if (IPSConfig.getProxyType() != null) {
			response = HttpClientUtil.run(url, beSecretToken, transactionId);
			/*
			 * } else { response = HttpClientUtil.run(url, httpClient,
			 * beSecretToken, transactionId); }
			 */
			endTime = System.currentTimeMillis();

			if (response != null) {
				log.info("run():TxID:" + transactionId + " HTTP response status is:" + response.code());
				ResponseBody res = response.body();
				responseBody = res.string();
				log.info("BE response : " + responseBody);
				res.close();

				createBackendResponse(response, responseBody, beRespObj, transactionId, interactionContextId);
				beRespObj.setBeResponseTime(endTime - startTime);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception occured while calling BE: " + e.getMessage());
			throw new Exception(e);
		}
		return beRespObj; 

	}

}
