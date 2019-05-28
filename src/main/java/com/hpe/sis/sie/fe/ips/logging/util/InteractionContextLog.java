package com.hpe.sis.sie.fe.ips.logging.util;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.interactioncontext.messages.InteractionContextResult;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.InteractionContext;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionResponse;
import com.hpe.sis.sie.fe.ips.utils.auth.model.VirtualObjects;
import com.hpe.sis.sie.fe.sisutils.channel.util.ChannelUtility;

public class InteractionContextLog {

	private final static Logger log = LoggerFactory.getLogger(InteractionContextLog.class);
	public static Gson gson = new Gson();
	private static InteractionContextDataGenerator dataGenerator = new InteractionContextDataGenerator();

	public static void logInteractionContext(InteractionContext interactionContext) {
		log.info("Obtained RequestObject in InteractionContextLog:" + interactionContext);
		Map<String, String> tdrInterContextData = populateTdrInterContextData(interactionContext);
		persistInFile(tdrInterContextData);
	}

	public static void logInteractionContext(InteractionContextResult interactionContextResult) {
		log.info("Obtained RequestObject in InteractionContextLog:" + interactionContextResult);
		Map<String, String> tdrInterContextData = populateTdrInterContextData(interactionContextResult);
		persistInFile(tdrInterContextData);
	}
	
	private static void persistInFile(Map<String, String> tdrInterContextData) {

		String tdrLogCat = "INTERACTION_CONTEXT_LOG";

		Logger tdrLog = LoggerFactory.getLogger(tdrLogCat);

		String tdrFieldKeys = IPSConfig.INTERACTION_CONTEXT_FIELDS;
		log.info("TdrFieldKeys of InteractionContext log :: " + tdrFieldKeys);
		String tdrLine = dataGenerator.formatTDRLine(tdrInterContextData, tdrFieldKeys,
				String.valueOf(IPSConfig.LOG_DELIMITER));
		try {
			log.debug("Logging InteractionContext now :" + tdrLine);
			tdrLog.info(tdrLine);
		} catch (Exception e) {
			log.error("BadRequestException occured in InteractionContextLog:" + e.getStackTrace());
		}

	}

	private static Map<String, String> populateTdrInterContextData(InteractionContext interactionContext) {
		Map<String, String> tdrInterContextData = new HashMap<>();
		
		tdrInterContextData.put("TRANSACTIONID", interactionContext.getTransactionId());
		tdrInterContextData.put("INTERACTION_ID", interactionContext.getInteractionId());
		tdrInterContextData.put("CONTEXT_ID", interactionContext.getInteractionContextId());
		tdrInterContextData.put("APP_ID", interactionContext.getAppId());
		tdrInterContextData.put("START_TIME", interactionContext.getBasicInfo().getStartTime());
		tdrInterContextData.put("UPDATE_TIME", interactionContext.getBasicInfo().getUpdateTime());
		tdrInterContextData.put("END_TIME", interactionContext.getBasicInfo().getEndTime());
		if (null != interactionContext.getVoId()) {
			tdrInterContextData.put("VO_ID", interactionContext.getVoId());
			tdrInterContextData.put("NAME", interactionContext.getAppVOBot().getName());
			tdrInterContextData.put("TYPE", interactionContext.getAppVOBot().getType());
		}
		if (null != interactionContext.getBotId())
			tdrInterContextData.put("BOT_ID", interactionContext.getBotId());
		tdrInterContextData.put("PLTYPE", interactionContext.getInteractionBO().getChannel().getPlType());
		tdrInterContextData.put("STTYPE", interactionContext.getInteractionBO().getChannel().getStType());
		tdrInterContextData.put("TXTYPE", interactionContext.getInteractionBO().getChannel().getTxType());
		tdrInterContextData.put("METHOD", interactionContext.getInteractionBO().getChannel().getMethod());
		tdrInterContextData.put("AS_ID", interactionContext.getInteractionBO().getChannel().getAsId());
		if (null != interactionContext.getInterationTxBO().getActor()) {
			tdrInterContextData.put("ACTOR_PATH",
					interactionContext.getInterationTxBO().getActor().getActorPath().toString());
		}

		if (null != interactionContext.getInterationTxBO().getTask().getId())
			tdrInterContextData.put("TASK_ID", interactionContext.getInterationTxBO().getTask().getId());

		if (null != interactionContext.getInterationTxBO().getTask().getResult()) {
			tdrInterContextData.put("TASK_STATUS",
					interactionContext.getInterationTxBO().getTask().getResult().getStatus());
		}
		return tdrInterContextData;
	}
	
	private static Map<String, String> populateTdrInterContextData(InteractionContextResult interactionContextResult) {
		Map<String, String> tdrInterContextData = new HashMap<>();
		InteractionRequest interactionRequest = interactionContextResult.getInteractionRequest();
		InteractionResponse interactionResponse = interactionContextResult.getInteractionResponse();
		tdrInterContextData.put("TRANSACTIONID", interactionRequest.getTransactionId());
		tdrInterContextData.put("INTERACTION_ID", interactionRequest.getInteractionId());
		tdrInterContextData.put("CONTEXT_ID", interactionRequest.getInteractionContextId());
		tdrInterContextData.put("APP_ID", interactionRequest.getApplicationVO().getId());
		tdrInterContextData.put("START_TIME", String.valueOf(interactionRequest.getRequestedTime()));
		tdrInterContextData.put("UPDATE_TIME", String.valueOf(System.currentTimeMillis()));
		tdrInterContextData.put("END_TIME", String.valueOf(System.currentTimeMillis()));
		if (null != interactionRequest.getVirtualObjectId()) {
			tdrInterContextData.put("VO_ID", interactionRequest.getVirtualObjectId());
			VirtualObjects virtualObjects = ChannelUtility.getVirtualObject(interactionRequest.getVirtualObjectId(),
					interactionRequest.getApplicationVO().getVirtualObjects());
			tdrInterContextData.put("NAME", virtualObjects.getName());
			tdrInterContextData.put("TYPE", virtualObjects.getType());
		}
		if (null != interactionRequest.getBotId()) {
			tdrInterContextData.put("BOT_ID", interactionRequest.getBotId());
		}
		tdrInterContextData.put("PLTYPE", interactionRequest.getInteractionType());
		tdrInterContextData.put("STTYPE", "Stateless");
		tdrInterContextData.put("TXTYPE", interactionRequest.getTxType());
		tdrInterContextData.put("METHOD", interactionRequest.getMethod());
		tdrInterContextData.put("AS_ID", interactionRequest.getActivityStreamId());
		if (null != interactionContextResult.getActorPath()) {
			tdrInterContextData.put("ACTOR_PATH",
					interactionContextResult.getActorPath().toString());
		}

		if (null != interactionRequest.getServiceVO())
			tdrInterContextData.put("TASK_ID", interactionRequest.getServiceVO().getTaskId());

		if (null != interactionResponse.getBackEndResponse()) {
			tdrInterContextData.put("TASK_STATUS",
					interactionResponse.getBackEndResponse().getStatus());
		}
		return tdrInterContextData;
	}
}
