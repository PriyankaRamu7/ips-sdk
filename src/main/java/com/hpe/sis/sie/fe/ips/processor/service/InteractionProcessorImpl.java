/*******************************************************************************
 * © Copyright 2017 Hewlett Packard Enterprise Development LP. All Rights Reserved.
 * An unpublished and CONFIDENTIAL work. Reproduction,
 * adaptation, or translation without prior written permission
 * is prohibited except as allowed under the copyright laws.
 * ---------------------------------------------------------------------------
 * Project: SISv1.3
 * Module: SIS IPS
 * Author: HPE SIS Team
 * Organization: Hewlett Packard Enterprise
 * Revision: 1.0
 * Date: 20/07/2017
 * Contents: InteractionProcessorImpl.java
 * ---------------------------------------------------------------------------
 ******************************************************************************/
package com.hpe.sis.sie.fe.ips.processor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.dispatch.Futures;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.hpe.sis.sie.fe.dss.DSSService;
import com.hpe.sis.sie.fe.dss.exception.SISException;
import com.hpe.sis.sie.fe.ips.auth.constants.AuthConstants;
import com.hpe.sis.sie.fe.ips.auth.exception.AuthException;
import com.hpe.sis.sie.fe.ips.auth.exception.IPSException;
import com.hpe.sis.sie.fe.ips.auth.service.AuthenticationRequest;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.IPSConstants;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.interactioncontext.messages.InteractionContextMessage;
import com.hpe.sis.sie.fe.ips.processing.messages.BackendResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionResponse;
import com.hpe.sis.sie.fe.ips.processing.model.CallbackRequest;
import com.hpe.sis.sie.fe.ips.processing.model.Interaction;
import com.hpe.sis.sie.fe.ips.processing.model.InteractionResult;
import com.hpe.sis.sie.fe.ips.processing.model.Transaction;
import com.hpe.sis.sie.fe.ips.processing.utils.ProcessingPattern;
import com.hpe.sis.sie.fe.ips.processing.utils.TransactionUtil;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;
import com.hpe.sis.sie.fe.ips.transmap.vo.ServiceVO;
import com.hpe.sis.sie.fe.sisutils.channel.util.PoliciesFilter;
import com.hpe.sis.sie.fe.sisutils.channel.util.ResponseUtils;
import com.hpe.sis.sie.fe.ips.utils.activitystreams.util.ActivityStreamValidator;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Policies;

class InteractionProcessorImpl implements InteractionProcessor {
	
	private static Logger log = LoggerFactory.getLogger(InteractionProcessorImpl.class);
	/* (non-Javadoc)
	 * @see com.hpe.sis.sie.fe.ips.processor.service.InteractionProcessor#process(com.hpe.sis.sie.fe.ips.sdk.security.model.InteractionVO)
	 */
	@Override
	public InteractionResult process(Interaction interaction) throws IPSException {

		InteractionRequest interactionRequest = populateInteractionRequestObject(interaction);
		TraceContext traceContext = Tracer.start(interaction.getTransactionId(), "New Interaction processing in IPS", "InteractionProcessorImpl.process");
		SisIpsActorSystem ipsActorSystem = SisIpsActorSystem.getInstance();
		InteractionResponse interactionResponse = new InteractionResponse();
		InteractionResult interactionResult = new InteractionResult();
		BackendResponse backEndResponseObj = new BackendResponse();
		String updatedResponseWithMetaData = null;
		String response = null;
		JSONObject json = new JSONObject();
		Interaction responseVO = new Interaction();
		
			if(interaction.getProcessingPattern().equalsIgnoreCase(ProcessingPattern.Synchronous.getPattern())) {
				try {
					Timeout timeout = new Timeout(Duration.create(interaction.getActorTimeOut(), TimeUnit.SECONDS));
					Future<Object> future = Patterns.ask(ipsActorSystem.synchronousActor, interactionRequest, timeout);
					Object result =  Await.result(future, timeout.duration());
					if (result != null && result instanceof String) {
						String res = (String) result;
						if ((res.indexOf(IPSConstants.FUTURES_TIMED_OUT) != -1
							|| res.indexOf(IPSConstants.ASKED_TIMED_OUT) != -1)) {
						throw new IPSException(IPSConfig.errorMsgProperties.getProperty(IPSConstants.REQUEST_TIMEDOUT_ERR), IPSConstants.REQUEST_TIMEDOUT_ERR );
						}
					} else if (result != null && result instanceof InteractionResponse) {
						interactionResponse = (InteractionResponse) result;
					} else {
						throw new IPSException(IPSConfig.errorMsgProperties.getProperty(IPSConstants.SYNC_REQUEST_PROCESSING_ERROR), IPSConstants.SYNC_REQUEST_PROCESSING_ERROR );
					}
					backEndResponseObj = interactionResponse.getBackEndResponse();
					updatedResponseWithMetaData = backEndResponseObj.getUpdatedResponseWithMetaData();
				
					log.info("Response::" + updatedResponseWithMetaData);
					
					interactionResult = generateInteractionResult(interactionRequest, interactionResponse);
					
					logTransaction(interaction, interactionResult);
				} catch (Exception e) {
					log.error("Error in processing interaction for transaction:" , interaction.getTransactionId(), e);
					String error = IPSConstants.SYNC_REQUEST_PROCESSING_ERROR + "-" + IPSConfig.errorMsgProperties.getProperty(IPSConstants.SYNC_REQUEST_PROCESSING_ERROR);
					traceContext.setErrorDetail(error);
					Tracer.end(traceContext, "Interaction processing in IPS", TraceConstants.FAILED);
					IPSException ipsEx = new IPSException(e);
					ipsEx.setErrorCode(IPSConstants.SYNC_REQUEST_PROCESSING_ERROR);
					ipsEx.setMessage(IPSConfig.errorMsgProperties.getProperty(IPSConstants.SYNC_REQUEST_PROCESSING_ERROR) + e.getMessage());
					logTransaction(interaction, ipsEx);
					throw ipsEx;
				}
		}
		else if(interaction.getProcessingPattern().equalsIgnoreCase(ProcessingPattern.Asynchronous.getPattern())){
			ipsActorSystem.asynchronousActor.tell(interactionRequest, ActorRef.noSender());
			response="Request submitted successfully for execution";
			interactionResult.setResponse(response);
		} else if(interaction.getProcessingPattern().equalsIgnoreCase(ProcessingPattern.Notify.getPattern())){
			ipsActorSystem.notifyActor.tell(interactionRequest, ActorRef.noSender());
			response="Request submitted successfully for execution";
			interactionResult.setResponse(response);
		}
		Tracer.end(traceContext, "Interaction processing in IPS", TraceConstants.SUCCESS);
		return interactionResult;
	}
	
	
	private InteractionResult generateInteractionResult(InteractionRequest interactionRequest,
			InteractionResponse interactionResponse) {
		InteractionResult res = new InteractionResult();
		if (interactionResponse != null) {
			BackendResponse beResponse = interactionResponse.getBackEndResponse();
			res.setStatus(beResponse.getStatus());
			res.setTaskId(beResponse.getTaskId());
			res.setServiceResponse(beResponse.getResponse());
			res.setUpdatedResponseWithMetaData(beResponse.getUpdatedResponseWithMetaData());
			
			res.setBeInvocationErrDetail(beResponse.getBeInvocationErrDetail());
			res.setHeaders(beResponse.getHeaders());
			res.setHttpStatusCode(beResponse.getHttpStatusCode());
			res.setBeResponseTime(beResponse.getBeResponseTime());
			res.setHttpStatusMessage(beResponse.getHttpStatusMessage());
			res.setExceptionFlowExecuted(beResponse.isExceptionFlowExecuted());
			
			/*
			 * Response headers not yet implemented in Task-engine as well as Channels
			 * Skipping Response headers filter implementation in the IPS
			 */
			
			/*if (p != null && p.getResponseHeader() != null) {
				log.info("Application: " + interactionRequest.getApplicationVO().getId() + " -- has Filter policy, response headers to be filtered");
				PoliciesFilter.filterResponseHeaders(res.getHeaders(), p.getResponseHeader() );
			}*/
			
			/* Filter response headers */
			res.setResponse(createResponse(interactionRequest.getApplicationVO(), res.getUpdatedResponseWithMetaData()));	
		
		}
		return res;
	}


	private String createResponse(Applications application,
			String responseStr) {
		String jsonRespStr = responseStr;
		JSONObject obj = ResponseUtils.updateResponseDetails(responseStr);
		jsonRespStr = obj.toString();
		Policies p = application.getPolicies();
		//Filter response body
		if (p != null && p.getResponseBody() != null) {
			log.info("Application: " + application.getId() + " -- has Filter policy, response body to be filtered");
			jsonRespStr = PoliciesFilter.filterResponseBody(obj.toString(), p.getResponseBody());
		}
		
		return jsonRespStr;
	}


	/* (non-Javadoc)
	 * @see com.hpe.sis.sie.fe.ips.processor.service.InteractionProcessor#processParallelDistributed(com.hpe.sis.sie.fe.ips.processing.model.InteractionVO)
	 */
	@Deprecated
	public InteractionResult processParallelDistributed(Interaction interaction) throws IPSException {
		String response = "";
		InteractionResponse result =new InteractionResponse();
		InteractionResult interactionResult = new InteractionResult();
		Interaction responseVO = new Interaction();
		InteractionResponse interactionResponse = null;
		InteractionRequest req = null;
		BackendResponse backEndResponseObj = new BackendResponse();
		SisIpsActorSystem ipsSystem = SisIpsActorSystem.getInstance();
		List<InteractionRequest> listOfInteractions = new ArrayList<InteractionRequest>();
		if (ActivityStreamValidator.isAnExtendedActivityStream(interaction.getRequestBody())) {
			String modifiedReqBody = ActivityStreamValidator.getExtendedActivityStreamItemsArray(interaction.getRequestBody());
			if (ActivityStreamValidator.isASJsonArray(modifiedReqBody)) {
				// this is an array of activities
				List<String> activities = ActivityStreamValidator.getJSONArrayItemsAsList(modifiedReqBody);
				for (String activity : activities) {
					req = populateInteractionRequestObject(interaction, activity);
					listOfInteractions.add(req);
				}
				// Process the array
				if (ProcessingPattern.Synchronous.equals(interaction.getProcessingPattern())) {
					Timeout timeout = new Timeout(Duration.create(interaction.getActorTimeOut(), "seconds"));
					List<Future<Object>> interactionFutures = new ArrayList<Future<Object>>();
					final ExecutionContext ec = SisIpsActorSystem.getInstance().getIpsActorSystem().dispatcher();
					for (InteractionRequest interactionRequest : listOfInteractions) {
						Future<Object> future = Patterns.ask(ipsSystem.synchronousActor, interactionRequest, timeout);
						interactionFutures.add(future);
					}
					
					Future<Iterable<Object>> futureResponse = Futures.sequence(interactionFutures, ec);
					try {
						Iterable<Object> list = Await.result(futureResponse, timeout.duration());
						log.info("Consolidated response list: " + list);
						List<String> beResponses = new ArrayList<String>();
						
						if (list != null) {
							for (Object o : list) {
								interactionResponse = (InteractionResponse) o;
								beResponses.add(interactionResponse.getBackEndResponse().getResponse()); // TODO updatedResponseWithMetaData or response?
							}
							String consolidatedBEResponse = ActivityStreamValidator.getJSONfromList(beResponses);
						    log.info("Consolidated BE Response: " + consolidatedBEResponse);
						    response = consolidatedBEResponse;
						    
						    backEndResponseObj = interactionResponse.getBackEndResponse(); // doubt? backEndResponse of last interaction only.Though we can set response with consolidatedResponse, headers,
						    			
						    //  status, taskId .. are of last interaction processing only - reqd for logging.
							
						    interactionResult = generateInteractionResult(req, interactionResponse);
						    interactionResult.setResponse(consolidatedBEResponse);
						    logTransaction(interaction, interactionResult);									
						}
					} catch (Exception e) {
						log.error("Error in processing interaction for transaction:" , interaction.getTransactionId(), e);
						throw new IPSException(IPSConfig.errorMsgProperties.getProperty(IPSConstants.SYNC_REQUEST_PROCESSING_ERROR) + e.getMessage(), IPSConstants.SYNC_REQUEST_PROCESSING_ERROR );
					}
					
				} else if (ProcessingPattern.Asynchronous.equals(interaction.getProcessingPattern())) {
					for (InteractionRequest interactionRequest : listOfInteractions) {
						ipsSystem.asynchronousActor.tell(interactionRequest, ActorRef.noSender());
					}
					response="Request submitted successfully for execution";
				} else if (ProcessingPattern.Notify.equals(interaction.getProcessingPattern())) {
					for (InteractionRequest interactionRequest : listOfInteractions) {
						ipsSystem.notifyActor.tell(interactionRequest, ActorRef.noSender());
					}
					response="Request submitted successfully for execution";
				}
				
			}
		}
		interactionResult.setResponse(response);
		return interactionResult;
	}
	
	public List<InteractionResult> processParallelDistributed(Interaction interaction, List<String> messages) throws IPSException {
		String response = "";
		TraceContext traceContext = Tracer.start(interaction.getTransactionId(), "New interaction, parallel-distributed processing in IPS", "InteractionProcessorImpl.processParallelDistributed");
		InteractionResponse result =new InteractionResponse();
		List<InteractionResult> listofResults = new ArrayList<InteractionResult>();
		InteractionResult interactionResult = new InteractionResult();
		Interaction responseVO = new Interaction();
		InteractionResponse interactionResponse = null;
		InteractionRequest interactionRequest = null;
		BackendResponse backEndResponseObj = new BackendResponse();
		SisIpsActorSystem ipsSystem = SisIpsActorSystem.getInstance();
		
				// Process the array
				if (ProcessingPattern.Synchronous.equals(interaction.getProcessingPattern())) {
					Timeout timeout = new Timeout(Duration.create(interaction.getActorTimeOut(), "seconds"));
					List<Future<Object>> interactionFutures = new ArrayList<Future<Object>>();
					final ExecutionContext ec = SisIpsActorSystem.getInstance().getIpsActorSystem().dispatcher();
					for (String requestBody : messages) {
						interactionRequest = populateInteractionRequestObject(interaction, requestBody);
						Future<Object> future = Patterns.ask(ipsSystem.synchronousActor, interactionRequest, timeout);
						interactionFutures.add(future);
					}
					
					Future<Iterable<Object>> futureResponse = Futures.sequence(interactionFutures, ec);
					try {
						Iterable<Object> list = Await.result(futureResponse, timeout.duration());
						log.info("Consolidated response list: " + list);
						List<String> beResponses = new ArrayList<String>();
						
						if (list != null) {
							for (Object o : list) {
								interactionResponse = (InteractionResponse) o;
								interactionResult = generateInteractionResult(interactionRequest, interactionResponse);
							    
							    logTransaction(interaction, interactionResult);
							    listofResults.add(interactionResult);
							}
						    									
						}
					} catch (Exception e) {
						log.error("Error in processing interaction for transaction:" , interaction.getTransactionId(), e);
						String error = IPSConstants.SYNC_REQUEST_PROCESSING_ERROR + "-" + IPSConfig.errorMsgProperties.getProperty(IPSConstants.SYNC_REQUEST_PROCESSING_ERROR);
						traceContext.setErrorDetail(error);
						Tracer.end(traceContext, "New interaction, parallel-distributed processing in IPS", TraceConstants.FAILED);
						IPSException ipsEx = new IPSException(e);
						ipsEx.setErrorCode(IPSConstants.SYNC_REQUEST_PROCESSING_ERROR);
						ipsEx.setMessage(IPSConfig.errorMsgProperties.getProperty(IPSConstants.SYNC_REQUEST_PROCESSING_ERROR) + e.getMessage());
						logTransaction(interaction, ipsEx);
						throw ipsEx;
					}
					
				} else if (ProcessingPattern.Asynchronous.equals(interaction.getProcessingPattern())) {
					for (String requestBody : messages) {
						interactionRequest = populateInteractionRequestObject(interaction, requestBody);
						ipsSystem.asynchronousActor.tell(interactionRequest, ActorRef.noSender());
					}
					response="Request submitted successfully for execution";
					interactionResult.setResponse(response);
					listofResults.add(interactionResult);
				} else if (ProcessingPattern.Notify.equals(interaction.getProcessingPattern())) {
					for (String requestBody : messages) {
						interactionRequest = populateInteractionRequestObject(interaction, requestBody);
						ipsSystem.notifyActor.tell(interactionRequest, ActorRef.noSender());
					}
					response="Request submitted successfully for execution";
					interactionResult.setResponse(response);
					listofResults.add(interactionResult);
				}
		Tracer.end(traceContext, "New interaction, parallel-distributed processing in IPS", TraceConstants.SUCCESS);
		return listofResults;
	}

	private InteractionRequest populateInteractionRequestObject(
			Interaction interactionVO) {
		
		InteractionRequest interactionRequest = new InteractionRequest();
		ServiceVO serviceVO = new ServiceVO();
		
		serviceVO.setEngine(interactionVO.getTask().getEngine());
		serviceVO.setName(interactionVO.getTask().getName());
		serviceVO.setTaskId(interactionVO.getTask().getId());
		serviceVO.setVersion(interactionVO.getTask().getVersion());
		interactionRequest.setBeTimeOut(interactionVO.getBeTimeOut());
		interactionRequest.setFromChannel(interactionVO.getFromChannel());
		interactionRequest.setMethod(interactionVO.getMethod());
		interactionRequest.setInteractionContextId(interactionVO.getInteractionContextId());
		interactionRequest.setProcessingPattern(interactionVO.getProcessingPattern());
		interactionRequest.setQueryParameters(interactionVO.getQueryParameters());
		interactionRequest.setRequestBody(interactionVO.getRequestBody());
		interactionRequest.setRequestHeaders(interactionVO.getRequestHeaders());
		interactionRequest.setServiceVO(serviceVO);
		interactionRequest.setSisHeaders(interactionVO.getSisHeaders());
		interactionRequest.setTransactionId(interactionVO.getTransactionId());
		interactionRequest.setVirtualObjectId(interactionVO.getVirtualObject());
		interactionRequest.setApplicationVO(interactionVO.getApplicationVO());
		interactionRequest.setParamList(interactionVO.getParamList());
		interactionRequest.setRequestedTime(interactionVO.getRequestedTime());
		interactionRequest.setRemoteHost(interactionVO.getRemoteHost());
		interactionRequest.setTestRequest(interactionVO.isTestRequest());
		interactionRequest.setResponseCachingEnabled(interactionVO.isResponseCachingEnabled());
		interactionRequest.setQueryString(interactionVO.getQueryString());
		interactionRequest.setCachedResponseTTL(interactionVO.getCachedResponseTTL());
		interactionRequest.setRequestURI(interactionVO.getRequestURI());
		interactionRequest.setActivityStreamId(interactionVO.getActivityStreamId());
		interactionRequest.setInteractionId(interactionVO.getInteractionId());
		interactionRequest.setBotId(interactionVO.getBotId());
		interactionRequest.setInteractionType(interactionVO.getInteractionType());
		interactionRequest.setTxType(interactionVO.getTxType());
		return interactionRequest;
	}
	
	private InteractionRequest populateInteractionRequestObject(Interaction interactionVO, String requestBody) {
		InteractionRequest request = populateInteractionRequestObject(interactionVO);
		request.setRequestBody(requestBody);
		return request;
	}

	@Override
	public String retrieveInteractionResponse(CallbackRequest callbackRequest) throws IPSException {
		String respStr = "";
		String beResponse = null;
		SisIpsActorSystem ipsActorSystem = SisIpsActorSystem.getInstance();
		TraceContext traceContext = Tracer.start(callbackRequest.getTransactionId(), "Get Asynchronous Interaction result, IPS received CallbackRequest to get Asynchronous interaction result", "InteractionProcessorImpl.retrieveInteractionResponse");
		String key = callbackRequest.getChannelName() +":response:"+ callbackRequest.getTransactionId();
		
		// Fetch data from DSS with the key
		Map<String, String> backend_status;
		try {
			backend_status = DSSService.getMap(key);
			
		} catch (SISException e2) {
			traceContext.setErrorDetail(IPSConfig.errorMsgProperties.getProperty(AuthConstants.DSS_SERVER_ERR_CODE));
			Tracer.end(traceContext, "Get Asynchronous Interaction result", TraceConstants.FAILED);
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(AuthConstants.DSS_SERVER_ERR_CODE), AuthConstants.DSS_SERVER_ERR_MSG);
		}
		
		org.json.simple.JSONObject jsonObject = null;
		String filteredResponse = null;
		ArrayList<String> pramList = null;
		JSONParser parser = new JSONParser();
		
		if (backend_status.size() <= 0) {
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(IPSConstants.ASYNC_RESULT_NOT_FOUND), IPSConstants.ASYNC_RESULT_NOT_FOUND);
		} else if (backend_status.size() > 0) {
			List<String> beResponses = new ArrayList<String>();
			String consolidatedResponseStr = "";
			for (Map.Entry<String, String> entry : backend_status.entrySet()) {
				
				jsonObject = new org.json.simple.JSONObject();
				try {
					 beResponse = (String) parser.parse(entry.getValue());
					beResponses.add(createResponse(callbackRequest.getApplication(), beResponse));
				} catch (ParseException e1) {
					log.error("Unable to generate response for callback request for application: " + callbackRequest.getApplication().getId(), e1);
				} catch (Exception e) {
					log.error("Unable to generate response for callback request for application: " + callbackRequest.getApplication().getId(), e);
				}
			}
			
			try {
				if (beResponses.size() == 1) {
					consolidatedResponseStr = beResponses.get(0).toString();
					jsonObject = (org.json.simple.JSONObject) parser.parse(consolidatedResponseStr);
					respStr = jsonObject.toString();
				} else {
					consolidatedResponseStr = ActivityStreamValidator.getJSONfromList(beResponses);
					JSONArray jsonArray = (org.json.simple.JSONArray) parser.parse(consolidatedResponseStr);
					respStr = jsonArray.toString();	
				}
			} catch (ParseException e) {
				log.error("Unable to parse consolidated backend responses: ", e);
			}
			
			if(IPSConfig.TRANSACTION_LOG_ENABLED) {
				log.info("Logging transaction for call back api: ");
				Transaction transaction = TransactionUtil.populateTransaction(beResponse,callbackRequest);
				SisIpsActorSystem.getInstance().transactionLoggingActor.tell(transaction, ActorRef.noSender());	
			}
		}
		Tracer.end(traceContext, "Get Asynchronous Interaction result", TraceConstants.SUCCESS);
		return respStr;
	}

	@Override
	public void logTransaction(Interaction interaction, InteractionResult interactionResult) {
		if(IPSConfig.TRANSACTION_LOG_ENABLED) {
			log.info("Received request for logging transaction: " + interaction.getTransactionId());
			Transaction transaction = TransactionUtil.populateTransaction(interaction, interactionResult);
			SisIpsActorSystem.getInstance().transactionLoggingActor.tell(transaction, ActorRef.noSender());	
		}
	}
	
	@Override
	public void logTransaction(Interaction interaction, IPSException ipsException) {
		if(IPSConfig.TRANSACTION_LOG_ENABLED) {
			log.info("Received request for logging transaction: " + interaction.getTransactionId());
			Transaction transaction = TransactionUtil.populateTransaction(interaction, ipsException.getErrorCode(), ipsException.getMessage());
			SisIpsActorSystem.getInstance().transactionLoggingActor.tell(transaction, ActorRef.noSender());	
		}
	}
	
	@Override
	public void logTransaction(CallbackRequest callbackRequest, IPSException ipsException) {
		if(IPSConfig.TRANSACTION_LOG_ENABLED) {
			log.info("Received request for logging transaction: " + callbackRequest.getTransactionId());
			Transaction transaction = TransactionUtil.populateTransaction(callbackRequest, ipsException.getErrorCode(), ipsException.getMessage());
			SisIpsActorSystem.getInstance().transactionLoggingActor.tell(transaction, ActorRef.noSender());	
		}
	}
	
	@Override
	public void logTransaction(AuthenticationRequest authRequest, AuthException authException) {
		if(IPSConfig.TRANSACTION_LOG_ENABLED) {
			log.info("Received request for logging transaction: " + authRequest.getTransactionId());
			Transaction transaction = TransactionUtil.populateTransaction(authRequest, authException.getCode(), authException.getMessage());
			SisIpsActorSystem.getInstance().transactionLoggingActor.tell(transaction, ActorRef.noSender());	
		}
	}
	
	@Override
	public void logInteractionContext(Interaction interaction) {
		if(IPSConfig.SAVE_ACTIVITYSTREAM_BYTIME) {
			log.info("Received request for logging InteractionContext: " + interaction.getTransactionId());
			InteractionContextMessage interactionContextMessage = com.hpe.sis.sie.fe.ips.interactioncontext.util.InteractionUtil.populateInteractionContextMessage(interaction);
			SisIpsActorSystem.getInstance().interactionContextDSSLoggerActor.tell(interactionContextMessage, ActorRef.noSender());
			SisIpsActorSystem.getInstance().interactionContextFileLoggerActor.tell(interactionContextMessage, ActorRef.noSender());
		}
	}
}
