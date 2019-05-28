// Copyright 2016-2017 Hewlett-Packard Enterprise Company, L.P. All rights reserved.
package com.hpe.sis.sie.fe.ips.transmap.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.TransformationRequest;
import com.hpe.sis.sie.fe.ips.transmap.exception.TransformationException;
import com.hpe.sis.sie.fe.ips.transmap.vo.ServiceVO;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Policies;
import com.hpe.sis.sie.fe.ips.utils.sieconfiguration.service.SIEConfigurationService;
import com.hpe.sis.sie.fe.ips.utils.sieconfiguration.vo.SIEConfigurationVO;

/**
 *
 * Main class to transform a incoming HttpServletRequest object into a format
 * that can be processed by Smart Interaction Designer Most of the heavy lifting
 * is performed in this class
 *
 */
public final class TransformationUtil {

	public static final String ENCODING_TYPE = "UTF-8";
	private static final Logger LOGGER = LoggerFactory.getLogger(TransformationUtil.class);

	private TransformationUtil() {
	}

	public static String fetchTaskHeader(InteractionRequest interactionRequest)
			throws UnsupportedEncodingException {
		String taskHeader = null;
		ServiceVO serviceVO = interactionRequest.getServiceVO();		
		try {
			LOGGER.info("Fetching task header for transaction : " + interactionRequest.getTransactionId());
			String encodingType = Charset.forName("UTF-8").name();
			JSONObject taskHeaderJson = new JSONObject();
			JSONObject serviceJson = new JSONObject();
			List<JSONObject> serviceList = new ArrayList<>();
		
			serviceJson.put("id", serviceVO.getTaskId());
			serviceJson.put("version", serviceVO.getVersion());
			serviceJson.put("engine", serviceVO.getEngine());
			serviceList.add(serviceJson);
			taskHeaderJson.put("mode", "synchronous");
			taskHeaderJson.put("applicationId", interactionRequest.getApplicationVO().getId());
			taskHeaderJson.put("virtualObjectId", interactionRequest.getVirtualObjectId());
			taskHeaderJson.put("services", serviceList);
			taskHeaderJson.put("timeout", interactionRequest.getBeTimeOut());

			String encodededtaskHeader = URLEncoder.encode(taskHeaderJson.toString(), encodingType);
			taskHeader = "/claspQueue/queue/1.0" + "?" + "taskHeader=" + encodededtaskHeader;
			LOGGER.info("Task header for transaction " + interactionRequest.getTransactionId() + " is: " + taskHeader);
			
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Unsupported Encoding Exception occured : ", e);
			// throw new SISException("500", "Internal Server Error"); //TODO
			// TODO Exception Handling

		}
		return taskHeader;
	}

	public static String fetchTaskData(InteractionRequest interactionRequest)
			throws TransformationException, JSONException, IOException {
		LOGGER.info("Fetching task data for transaction : " + interactionRequest.getTransactionId());
		String taskData = null;
		JSONObject requestBody = new JSONObject();
		JSONObject taskDataJson = new JSONObject();
		String queryStr = interactionRequest.getQueryParameters();
		
		if (isRequestBodyPresent(interactionRequest.getMethod())) {
			requestBody = new JSONObject(interactionRequest.getRequestBody());
		}
		insertSisHeaders(interactionRequest, taskDataJson);
		
		/**
		 * insertQueryParameters has been added to support queryString for all methods
		 */
		
		insertQueryParameters(queryStr, taskDataJson);
		
		switch (interactionRequest.getMethod()) {
		case TransformationConstants.GET:
			insertQueryParams(queryStr, taskDataJson);
			break;
		case TransformationConstants.PUT:
			insertRequestBody(requestBody, taskDataJson);
			break;
		case TransformationConstants.POST:
			insertRequestBody(requestBody, taskDataJson);
			break;
		case TransformationConstants.DELETE:
			insertQueryParams(queryStr, taskDataJson);
			break;
		case TransformationConstants.PATCH:
			insertRequestBody(requestBody, taskDataJson);
			break;
		}
		
		taskData = URLEncoder.encode(taskDataJson.toString(), ENCODING_TYPE);
		LOGGER.info("task data for transaction " + interactionRequest.getTransactionId() + " is: " + taskData);
		return taskData;
	}

	private static void insertQueryParameters(String queryStr,
			JSONObject taskDataJson) {
		LOGGER.debug("Inserting all query parameters to taskData _query_param_:");
		JSONObject queryParamObj = new JSONObject();
		if (StringUtils.isNotEmpty(queryStr)) {
			String[] params = queryStr.split("&");
			for (String param : params) {
				String[] p = param.split("=");
				String name = p[0];
				if (p.length > 1) {
					String value = p[1];
					queryParamObj.put(name, value);
				}
			}
			taskDataJson.put(TransformationConstants.QUERY_PARAM, queryParamObj);
		}
		LOGGER.debug("TaskData json with _query_param_:" + taskDataJson.toString());
		
	}

	private static void insertActivityStreamId(JSONObject taskDataJson,
			String activityStreamId) {
		if (StringUtils.isNotEmpty(activityStreamId)) {
			taskDataJson.put(TransformationConstants.ACTIVITY_STREAM_ID, activityStreamId);
		}
	}

	private static void insertChannelName(JSONObject taskDataJson,
			String fromChannel) {
		if (StringUtils.isNotEmpty(fromChannel)) {
			taskDataJson.put(TransformationConstants.CHANNEL, fromChannel);
		} else {
			LOGGER.warn("Channel name is empty and cannot be added to task-data: " + taskDataJson.toString());
		}
	}

	private static void insertRequestMethod(JSONObject taskDataJson,
			String method) {
		if (StringUtils.isNotEmpty(method)) {
			taskDataJson.put(TransformationConstants.REQUEST_METHOD, method);
		} else {
			LOGGER.warn("Request method is empty and cannot be added to task-data: " + taskDataJson.toString());
		}
	}

	private static void insertInteractionContextId(JSONObject taskDataJson,
			String interactionContextId) {
		if (StringUtils.isNotEmpty(interactionContextId)) {
			taskDataJson.put(TransformationConstants.INTERACTION_CONTEXT_ID, interactionContextId);
		} else {
			LOGGER.warn("Interaction Context Id is empty and cannot be added to task-data: " + taskDataJson.toString());
		}
	}

	public static String fetchTaskUrl(final TransformationRequest transformationRequest)
			throws TransformationException, JSONException, IOException {
		
		InteractionRequest interactionRequest = transformationRequest.getInteractionRequest();
		SIEConfigurationVO sieConfigVO = SIEConfigurationService.sieconfigVO;
		
		String taskHeader = fetchTaskHeader(interactionRequest);
		String taskData = fetchTaskData(interactionRequest);
		
		String taskURL = sieConfigVO.getTaskEngineProtocol() + "://"+ sieConfigVO.getTaskEngineIp() + ":" + sieConfigVO.getTaskEnginePort() + taskHeader
				+ "&" + "taskData=" + taskData;
		return taskURL;
	}

	private static void insertSisHeaders(InteractionRequest interactionRequest, JSONObject taskDataJson) {
		LOGGER.debug("Inserting SisHeaders to taskData :");
		JSONObject jsonObj = new JSONObject();

		Map<String, String> headerMap = null;
		if (interactionRequest.getRequestHeaders() != null) {
			headerMap = interactionRequest.getRequestHeaders();
		}

		if (interactionRequest.getSisHeaders() != null && headerMap != null) {
			List<String> sisHeadersList = Arrays.asList(interactionRequest.getSisHeaders().split(","));
			for (String headerName : headerMap.keySet()) {
				if (!sisHeadersList.contains(headerName)) {
					if (headerMap != null)
						jsonObj.put(headerName, headerMap.get(headerName));
				}
			}
		} else {
			if (headerMap != null && headerMap.keySet() != null) {
				for (String headerName : headerMap.keySet()) {
					jsonObj.put(headerName, headerMap.get(headerName));
				}
			}
		}
		
		Policies p = interactionRequest.getApplicationVO().getPolicies();
		if (p != null) {
			List<String> listofReqHeadersToFilter = p.getRequestHeader();
			removeHeadersForReqFilterPolicy(jsonObj, listofReqHeadersToFilter );
		}
		
		/** changes for Enhancement request: 32777 **/
		insertChannelName(jsonObj, interactionRequest.getFromChannel());
		insertRequestMethod(jsonObj, interactionRequest.getMethod());
		insertInteractionContextId(jsonObj, interactionRequest.getInteractionContextId());
		insertActivityStreamId(jsonObj, interactionRequest.getActivityStreamId());
		
		taskDataJson.put(TransformationConstants.SISHEADERS, jsonObj);
		LOGGER.debug("TaskData json with sisheader:" + taskDataJson.toString());
	}

	public static void insertQueryParams(final String query, JSONObject taskDataJson) {
		LOGGER.debug("Inserting query parameters to taskData :");
		if (query != null && !query.trim().equals("")) {
			String[] params = query.split("&");
			for (String param : params) {
				String[] p = param.split("=");
				String name = p[0];
				if (p.length > 1) {
					String value = p[1];
					taskDataJson.put(name, value);
				}
			}
		}
		LOGGER.debug("TaskData json with query parameters:" + taskDataJson.toString());
	}

	public static void insertRequestBody(JSONObject requestBody, JSONObject taskDataJson) {
		LOGGER.debug("Inserting request body to taskData :");
			if (requestBody != null) {
				Iterator<String> itr = requestBody.keys();
				String key;
				while (itr.hasNext()) {
					key = itr.next();
					taskDataJson.put(key, requestBody.get(key));
				}
			}
		LOGGER.debug("TaskData json with request body:" + taskDataJson.toString());
	}

	public static boolean isRequestBodyPresent(final String method) {
		switch (method) {
		case "GET":
			return false;
		case "DELETE":
			return false;
		case "POST":
			return true;
		case "PUT":
			return true;
		case "PATCH":
			return true;
		default:
			return false;
		}
	}
	
	private static void removeHeadersForReqFilterPolicy(JSONObject json, List<String> listofReqHeadersToFilter) {
		if (json != null && listofReqHeadersToFilter != null) {
			for (String reqHeader : listofReqHeadersToFilter) {
				json.remove(reqHeader);
			}
		} else if (json == null) {
			LOGGER.info("No headers in the incoming HTTP request. Filtering request headers skipped");
		}
	}
	
	public static String fetchBotServiceURL(String taskHeader, String taskDataJson) {
		String encodingType = Charset.forName("UTF-8").name();
		SIEConfigurationVO sieConfigVO = SIEConfigurationService.sieconfigVO;
		String taskURL = null;
		try {
			String encodededtaskHeader = IPSConfig.CLASP_QUEUE_CONF + "?" + "taskHeader=" + URLEncoder.encode(taskHeader, encodingType);
			String encodedtaskData = URLEncoder.encode(taskDataJson, encodingType);
			taskURL = sieConfigVO.getTaskEngineProtocol() + "://"+ sieConfigVO.getTaskEngineIp() + ":" + sieConfigVO.getTaskEnginePort() + encodededtaskHeader
					+ "&" + "taskData=" + encodedtaskData;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return taskURL;
	}
}
