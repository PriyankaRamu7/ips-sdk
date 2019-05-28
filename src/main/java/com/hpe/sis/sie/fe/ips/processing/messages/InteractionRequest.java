package com.hpe.sis.sie.fe.ips.processing.messages;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.hpe.sis.sie.fe.ips.transmap.vo.ServiceVO;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;

import akka.actor.ActorRef;

// TODO: Auto-generated Javadoc
/**
 * The Class InteractionRequest.
 */
public class InteractionRequest implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3038701769155252155L;

	/** The transaction id. */
	private String transactionId;
	
	/** The interaction context id. */
	private String interactionContextId;
	
	/** The virtual object id. */
	private String virtualObjectId;
	
	/** The virtual object id. */
	private String botId;
	
	/** The id for activity streams */
	private String activityStreamId;
	
	/** The from channel. */
	private String fromChannel;
	
	/** The service VO. */
	private ServiceVO serviceVO;// contains BE service details - id, name, version, engine// because provisioning for activities & operations are different under operations/activities for respective in app meta data)
	
	/** The be time out. */
	private int beTimeOut;// same. how to figure out req is for activity or VO. hence getting  from channel only 
	
	/** The processing pattern. */
	private String processingPattern;
	
	/** The query parameters. */
	private String queryParameters; // from req url
	
	/** The request headers. */
	private Map<String,String> requestHeaders; // from httpRequest
	
	/** The sis headers. */
	private String sisHeaders; // from yml
	
	/** The operation or method. */
	private String method; // incoming req http method
	
	/** The request body. */
	private String requestBody; // http request body
	
	/** The remote host. */
	private String remoteHost; // http request body
	
	/** The requested time. */
	private long requestedTime;
	
	/** The is test request. */
	private boolean isTestRequest;
	
	/** The application VO. */
	private Applications applicationVO;
	
	/** The sync actor ref. */
	private ActorRef syncActorRef;
	
	/** nonActorSender reference. */
	private ActorRef nonActorSender;
	
	
	/** The param list. */
	private List<String> paramList;
	
	private boolean isResponseCachingEnabled;
	
	private String queryString;
	
	private long cachedResponseTTL;
	
	private String requestURI;
	
	private String interactionId;
	
	private String interactionType;
	
	private String txType;

	/**
	 * Gets the param list.
	 *
	 * @return the param list
	 */
	public List<String> getParamList() {
		return paramList;
	}

	/**
	 * Sets the param list.
	 *
	 * @param paramList the new param list
	 */
	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}

	/**
	 * Gets the transaction id.
	 *
	 * @return the transaction id
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Sets the transaction id.
	 *
	 * @param transactionId the new transaction id
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * Gets the interaction context id.
	 *
	 * @return the interaction context id
	 */
	public String getInteractionContextId() {
		return interactionContextId;
	}

	/**
	 * Sets the interaction context id.
	 *
	 * @param interactionContextId the new interaction context id
	 */
	public void setInteractionContextId(String interactionContextId) {
		this.interactionContextId = interactionContextId;
	}

	/**
	 * Gets the virtual object id.
	 *
	 * @return the virtual object id
	 */
	public String getVirtualObjectId() {
		return virtualObjectId;
	}

	/**
	 * Sets the virtual object id.
	 *
	 * @param virtualObjectId the new virtual object id
	 */
	public void setVirtualObjectId(String virtualObjectId) {
		this.virtualObjectId = virtualObjectId;
	}

	public String getBotId() {
		return botId;
	}

	public void setBotId(String botId) {
		this.botId = botId;
	}

	/**
	 * Gets the from channel.
	 *
	 * @return the from channel
	 */
	public String getFromChannel() {
		return fromChannel;
	}

	/**
	 * Sets the from channel.
	 *
	 * @param fromChannel the new from channel
	 */
	public void setFromChannel(String fromChannel) {
		this.fromChannel = fromChannel;
	}

	/**
	 * Gets the service VO.
	 *
	 * @return the service VO
	 */
	public ServiceVO getServiceVO() {
		return serviceVO;
	}

	/**
	 * Sets the service VO.
	 *
	 * @param serviceVO the new service VO
	 */
	public void setServiceVO(ServiceVO serviceVO) {
		this.serviceVO = serviceVO;
	}

	

	/**
	 * Gets the be time out.
	 *
	 * @return the be time out
	 */
	public int getBeTimeOut() {
		return beTimeOut;
	}

	/**
	 * Sets the be time out.
	 *
	 * @param beTimeOut the new be time out
	 */
	public void setBeTimeOut(int beTimeOut) {
		this.beTimeOut = beTimeOut;
	}

	/**
	 * Gets the processing pattern.
	 *
	 * @return the processing pattern
	 */
	public String getProcessingPattern() {
		return processingPattern;
	}

	/**
	 * Sets the processing pattern.
	 *
	 * @param processingPattern the new processing pattern
	 */
	public void setProcessingPattern(String processingPattern) {
		this.processingPattern = processingPattern;
	}

	/**
	 * Gets the query parameters.
	 *
	 * @return the query parameters
	 */
	public String getQueryParameters() {
		return queryParameters;
	}

	/**
	 * Sets the query parameters.
	 *
	 * @param queryParameters the new query parameters
	 */
	public void setQueryParameters(String queryParameters) {
		this.queryParameters = queryParameters;
	}

	/**
	 * Gets the request headers.
	 *
	 * @return the request headers
	 */
	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	/**
	 * Sets the request headers.
	 *
	 * @param requestHeaders the request headers
	 */
	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	/**
	 * Gets the sis headers.
	 *
	 * @return the sis headers
	 */
	public String getSisHeaders() {
		return sisHeaders;
	}

	/**
	 * Sets the sis headers.
	 *
	 * @param sisHeaders the new sis headers
	 */
	public void setSisHeaders(String sisHeaders) {
		this.sisHeaders = sisHeaders;
	}


	/**
	 * Gets the request body.
	 *
	 * @return the request body
	 */
	public String getRequestBody() {
		return requestBody;
	}

	/**
	 * Sets the request body.
	 *
	 * @param requestBody the new request body
	 */
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	/**
	 * Gets the remote host, where the interaction request originates from
	 *
	 * @return the remote host
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * Sets the remote host, where the interaction request originates from
	 *
	 * @param remoteHost the new remote host
	 */
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	/**
	 * Gets the requested time, time at which the Interaction request is received by the inbound interaction channel
	 *
	 * @return the requested time
	 */
	public long getRequestedTime() {
		return requestedTime;
	}

	/**
	 * Sets the requested time, time at which the Interaction request is received by the inbound interaction channel
	 *
	 * @param requestedTime the requested time
	 */
	public void setRequestedTime(long requestedTime) {
		this.requestedTime = requestedTime;
	}

	/**
	 * Checks if is test request.
	 *
	 * @return true, if is test request
	 */
	public boolean isTestRequest() {
		return isTestRequest;
	}

	/**
	 * Sets the test request.
	 *
	 * @param isTestRequest the new test request
	 */
	public void setTestRequest(boolean isTestRequest) {
		this.isTestRequest = isTestRequest;
	}

	/**
	 * Gets the application VO.
	 *
	 * @return the application VO
	 */
	public Applications getApplicationVO() {
		return applicationVO;
	}

	/**
	 * Sets the application VO.
	 *
	 * @param applicationVO the new application VO
	 */
	public void setApplicationVO(Applications applicationVO) {
		this.applicationVO = applicationVO;
	}

	/**
	 * Gets the sync actor ref.
	 *
	 * @return the sync actor ref
	 */
	public ActorRef getSyncActorRef() {
		return syncActorRef;
	}

	/**
	 * Sets the sync actor ref.
	 *
	 * @param syncActorRef the new sync actor ref
	 */
	public void setSyncActorRef(ActorRef syncActorRef) {
		this.syncActorRef = syncActorRef;
	}

	/**
	 * Gets the non actor sender.
	 *
	 * @return the non actor sender
	 */
	public ActorRef getNonActorSender() {
		return nonActorSender;
	}

	/**
	 * Sets the non actor sender.
	 *
	 * @param nonActorSender the new non actor sender
	 */
	public void setNonActorSender(ActorRef nonActorSender) {
		this.nonActorSender = nonActorSender;
	}

	/**
	 * @return method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	public boolean isResponseCachingEnabled() {
		return isResponseCachingEnabled;
	}

	public void setResponseCachingEnabled(boolean isResponseCachingEnabled) {
		this.isResponseCachingEnabled = isResponseCachingEnabled;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public long getCachedResponseTTL() {
		return cachedResponseTTL;
	}

	public void setCachedResponseTTL(long cachedResponseTTL) {
		this.cachedResponseTTL = cachedResponseTTL;
	}

	/**
	 * @return the requestURI
	 */
	public String getRequestURI() {
		return requestURI;
	}

	/**
	 * @param requestURI the requestURI to set
	 */
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public String getActivityStreamId() {
		return activityStreamId;
	}

	public void setActivityStreamId(String activityStreamId) {
		this.activityStreamId = activityStreamId;
	}

	public String getInteractionId() {
		return interactionId;
	}

	public void setInteractionId(String interactionId) {
		this.interactionId = interactionId;
	}

	public String getInteractionType() {
		return interactionType;
	}

	public void setInteractionType(String interactionType) {
		this.interactionType = interactionType;
	}

	public String getTxType() {
		return txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

}
