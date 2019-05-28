package com.hpe.sis.sie.fe.ips.logging.util;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.hpe.sis.sie.fe.dss.DSSService;
import com.hpe.sis.sie.fe.dss.exception.SISException;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.Actor;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.ChannelTransactionBO;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.InteractionContext;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.InteractionTxBO;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.Result;
import com.hpe.sis.sie.fe.ips.interactioncontext.util.InteractionContextConstants;
import com.hpe.sis.sie.fe.ips.processing.messages.BackendResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import akka.actor.ActorPath;

public class InteractionContextCAO {

	private final static Logger log = LoggerFactory.getLogger(InteractionContextLog.class);
	private static Gson gson = new Gson();
	
	public void saveInteractionContext(InteractionContext interactionContext, ChannelTransactionBO channelTransactionBO)  throws SISException{
		
		log.info("SaveInteractionContext : Entry");
		
		try {
			//Storing timebased data
			long expiryTime = IPSConfig.INTERACTION_CONTEXT_TTL;
			if (expiryTime != -1)
				expiryTime = (System.currentTimeMillis() / 1000L) + IPSConfig.INTERACTION_CONTEXT_TTL;		
			
			String timebasedkey = InteractionContextConstants.DSS_PREFIX_INTERACTION_CONTEXT + InteractionContextConstants.COLON + InteractionContextConstants.OBE +
					InteractionContextConstants.COLON + interactionContext.getObeId(); // interactionContext:obe:obeId
			String channelTransactionjson = gson.toJson(channelTransactionBO);		
			DSSService.setSortedSet(timebasedkey, channelTransactionjson, System.currentTimeMillis(), expiryTime);
			
			//Storing detail interaction report
			String key = InteractionContextConstants.DSS_PREFIX_INTERACTION_CONTEXT_APP + InteractionContextConstants.COLON + 
					interactionContext.getAppId() + InteractionContextConstants.COLON + InteractionContextConstants.DSS_PREFIX_INTERACTION_CONTEXT_CTX +
					InteractionContextConstants.COLON + interactionContext.getInteractionContextId(); // app:appId:ctxId:contextId
			HashMap<String, String> map = new HashMap<String, String>();
			
			if(interactionContext.getBasicInfo() != null) {
				String fieldName = InteractionContextConstants.DSS_FIELDNAME_BASICINFO;			
				String basicInfoJson = gson.toJson(interactionContext.getBasicInfo());
				map.put(fieldName, basicInfoJson);
			}
			if(interactionContext.getVoId()!= null && interactionContext.getAppVOBot() != null)	{
				String fieldName = InteractionContextConstants.DSS_FIELDNAME_VOID + InteractionContextConstants.COLON + interactionContext.getVoId(); // voId:virtualObjId
				String strJson = gson.toJson(interactionContext.getAppVOBot()); 
				map.put(fieldName, strJson);
			}
			
			if(interactionContext.getVoId()!= null && interactionContext.getInteractionBO() != null) {
				String fieldName = InteractionContextConstants.DSS_FIELDNAME_VOID + InteractionContextConstants.COLON + interactionContext.getVoId()+ InteractionContextConstants.COLON+ InteractionContextConstants.DSS_FIELDNAME_ITRID +
						InteractionContextConstants.COLON+  interactionContext.getInteractionId(); //voId:voId:itrId:interactionId
				String strJson = gson.toJson(interactionContext.getInteractionBO());
				map.put(fieldName, strJson);
			}
			
			if(interactionContext.getVoId()!= null && interactionContext.getInterationTxBO() != null) { //voId:virtualObjId:itrId:interactionId:txId:transactionId
				String fieldName = InteractionContextConstants.DSS_FIELDNAME_VOID + InteractionContextConstants.COLON +  interactionContext.getVoId()+ InteractionContextConstants.COLON +  InteractionContextConstants.DSS_FIELDNAME_ITRID +
						InteractionContextConstants.COLON +  interactionContext.getInteractionId() + InteractionContextConstants.COLON + InteractionContextConstants.DSS_FIELDNAME_TXID + InteractionContextConstants.COLON +
						interactionContext.getTransactionId();
				String strJson = gson.toJson(interactionContext.getInterationTxBO());
				map.put(fieldName, strJson);
			}
			
			if(interactionContext.getBotId()!= null && interactionContext.getAppVOBot() != null) { // botId:botId 
				String fieldName = InteractionContextConstants.DSS_FIELDNAME_BOTID + InteractionContextConstants.COLON + interactionContext.getBotId();
				String strJson = gson.toJson(interactionContext.getAppVOBot());
				map.put(fieldName, strJson);
			}
			
			if(interactionContext.getBotId()!= null && interactionContext.getInteractionBO() != null) { // botId:botId:itrId:interactionId
				String fieldName = InteractionContextConstants.DSS_FIELDNAME_BOTID + InteractionContextConstants.COLON + interactionContext.getBotId()+ InteractionContextConstants.COLON+ InteractionContextConstants.DSS_FIELDNAME_ITRID +
						InteractionContextConstants.COLON+  interactionContext.getInteractionId();
				String strJson = gson.toJson(interactionContext.getInteractionBO());
				map.put(fieldName, strJson);
			}
			
			if(interactionContext.getBotId()!= null && interactionContext.getInterationTxBO() != null) { //botId:botId:itrId:interactionId:txId:transactionId
				String fieldName = InteractionContextConstants.DSS_FIELDNAME_BOTID + InteractionContextConstants.COLON +  interactionContext.getBotId()+ InteractionContextConstants.COLON +  InteractionContextConstants.DSS_FIELDNAME_ITRID +
						InteractionContextConstants.COLON +  interactionContext.getInteractionId() + InteractionContextConstants.COLON + InteractionContextConstants.DSS_FIELDNAME_TXID + InteractionContextConstants.COLON +
						interactionContext.getTransactionId();
				String strJson = gson.toJson(interactionContext.getInterationTxBO());
				map.put(fieldName, strJson);
			}
				
			DSSService.setMap(key, map, expiryTime);
		} catch (com.hpe.sis.sie.fe.dss.exception.SISException e) {
			log.error("Transaction failed while saving txID in "
					+ " DSS due to:"+e.getMessage());
			throw new SISException(e);    
		}
	}
	
	public void updateInteractionContext(InteractionRequest interactionRequest, BackendResponse backEndResponse, ActorPath actorPath)  throws SISException{
		log.info("updateInteractionContext : Entry");

		try {
			String key = InteractionContextConstants.DSS_PREFIX_INTERACTION_CONTEXT_APP + InteractionContextConstants.COLON + 
					interactionRequest.getApplicationVO().getId() + InteractionContextConstants.COLON + InteractionContextConstants.DSS_PREFIX_INTERACTION_CONTEXT_CTX +
					InteractionContextConstants.COLON + interactionRequest.getInteractionContextId(); // app:appId:ctxId:ctxId
			
			String fieldName = null;
			if(interactionRequest.getVirtualObjectId() != null)
			 fieldName = InteractionContextConstants.DSS_FIELDNAME_VOID + InteractionContextConstants.COLON + interactionRequest.getVirtualObjectId() + InteractionContextConstants.COLON+  InteractionContextConstants.DSS_FIELDNAME_ITRID +
					InteractionContextConstants.COLON +  interactionRequest.getInteractionId() + InteractionContextConstants.COLON+ InteractionContextConstants.DSS_FIELDNAME_TXID + InteractionContextConstants.COLON +
					interactionRequest.getTransactionId();//voId:voId:itrId:uniqueInteractionId:txId:txId
			else
				fieldName = InteractionContextConstants.DSS_FIELDNAME_BOTID + InteractionContextConstants.COLON + interactionRequest.getBotId() + InteractionContextConstants.COLON+  InteractionContextConstants.DSS_FIELDNAME_ITRID +
				InteractionContextConstants.COLON+ interactionRequest.getInteractionId() + InteractionContextConstants.COLON+ InteractionContextConstants.DSS_FIELDNAME_TXID + InteractionContextConstants.COLON +
				interactionRequest.getTransactionId(); // botId:botId:itrId:uniqueInteractionId:txId:txId
			
			HashMap<String, String> map = new HashMap<String, String>();
			String transactionjson = DSSService.getFieldValueInMap(key, fieldName);
			InteractionTxBO interactionTxBO =  gson.fromJson(transactionjson, InteractionTxBO.class);
			if(interactionTxBO != null) {
				Actor actor = new Actor();
				if(null != actorPath) {
					actor.setActorPath(actorPath.toString());
					actor.setName(actorPath.name());
					actor.setParent(actorPath.parent().name());
					actor.setSystem(SisIpsActorSystem.getInstance().getIpsActorSystem().provider().getDefaultAddress().toString());
					actor.setType(actorPath.toString());
					actor.setUid(actorPath.uid());
				}
				
				interactionTxBO.setActor(actor);
				Result result = new Result();
				result.setInfo(backEndResponse.getResponse());
				result.setStatus(backEndResponse.getStatus());
				interactionTxBO.getTask().setResult(result);
				
				if(null != interactionTxBO.getTask())
					interactionTxBO.getTask().setResult(result);
				
				long expiryTime = IPSConfig.INTERACTION_CONTEXT_TTL;
				if (expiryTime != -1)
					expiryTime = (System.currentTimeMillis() / 1000L) + IPSConfig.INTERACTION_CONTEXT_TTL;
				
				DSSService.setValueInMap(key, fieldName, gson.toJson(interactionTxBO), expiryTime);
			}
		} catch (com.hpe.sis.sie.fe.dss.exception.SISException e) {
			log.error("Updating InteractionContext failed while saving txID in "
					+ " DSS due to:"+e.getMessage());
			throw new SISException(e);    
		}
	}
}
