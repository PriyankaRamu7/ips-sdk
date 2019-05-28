package com.hpe.sis.sie.fe.ips.processing.actors;

import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.processing.messages.BackendResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.CachedResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.NoCachedResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.ResponseCacheRequest;
import com.hpe.sis.sie.fe.ips.responsecache.ResponseCacheConstants;
import com.hpe.sis.sie.fe.ips.responsecache.ResponseCacheUtils;
import com.hpe.sis.sie.fe.ips.responsecache.ResponseCacheVO;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.json.util.JsonUtility;
import com.hpe.sis.sie.fe.sisutils.channel.util.ChannelUtility;

import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ResponseCacheActor extends UntypedAbstractActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	public void onReceive(Object arg0) throws Throwable {
		if (arg0 instanceof ResponseCacheRequest) {
			ResponseCacheRequest responseCacheRequest = (ResponseCacheRequest) arg0;
			InteractionRequest interactionRequest = responseCacheRequest.getInteractionRequest();
			TraceContext traceContext = Tracer.start(interactionRequest.getTransactionId(), "ResponseCacheActor - Checking for Cached Responses", "ResponseCacheActor.onReceive", getSelf().path().toString());
			if (interactionRequest.getMethod().equals(ResponseCacheConstants.GET_METHOD) && interactionRequest.isResponseCachingEnabled()) {
				Applications application = interactionRequest.getApplicationVO();
				ResponseCacheVO responseCache;
				if (ChannelUtility.BOT_ONDEMAND.equals(IPSConfig.CHANNEL_NAME)) {
					// This is a on-demand bot request
					responseCache = ResponseCacheUtils.fetchResponseCacheEntity(interactionRequest.getBotId(),
							interactionRequest.getMethod(), interactionRequest.getQueryString(), interactionRequest.getCachedResponseTTL(), interactionRequest.getRequestURI());
				} else {
					responseCache = ResponseCacheUtils.fetchResponseCacheEntity(interactionRequest.getVirtualObjectId(),
						interactionRequest.getMethod(), interactionRequest.getQueryString(), interactionRequest.getCachedResponseTTL(), interactionRequest.getRequestURI());
				}
				if (ResponseCacheUtils.responseCacheContentsExists(responseCache)) {
					// Cached response exists
					// Create the response
					String cacheResponse = ResponseCacheUtils.fetchResponseCacheContents(responseCache);
					BackendResponse beRespObj = JsonUtility.jsonToJava(cacheResponse, BackendResponse.class);
					log.debug("Response from cache : " + beRespObj.getUpdatedResponseWithMetaData());
					CachedResponse cachedResponse = new CachedResponse();
					cachedResponse.setInteractionRequest(interactionRequest);
					cachedResponse.setBackendResponse(beRespObj);
					Tracer.end(traceContext, "ResponseCacheActor - Cached response available", TraceConstants.SUCCESS);
					getSender().tell(cachedResponse, getSelf());
				} else {
					NoCachedResponse noCachedResponse = new NoCachedResponse();
					noCachedResponse.setInteractionRequest(interactionRequest);
					Tracer.end(traceContext, "ResponseCacheActor - Cached response NOT available", TraceConstants.SUCCESS);
					getSender().tell(noCachedResponse, getSelf());
				}
				
			} else {
				NoCachedResponse noCachedResponse = new NoCachedResponse();
				noCachedResponse.setInteractionRequest(interactionRequest);
				Tracer.end(traceContext, "ResponseCacheActor - Caching NOT Applicable", TraceConstants.SUCCESS);
				getSender().tell(noCachedResponse, getSelf());
			}
		}
		
	}

}
