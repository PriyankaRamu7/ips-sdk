package com.hpe.sis.sie.fe.ips.interactioncontext.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.interactioncontext.messages.InteractionContextMessage;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.AppVOBot;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.Channel;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.ChannelTransactionBO;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.InteractionBO;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.InteractionContext;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.InteractionCtxBasicInfo;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.InteractionTxBO;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.Result;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.Task;
import com.hpe.sis.sie.fe.ips.processing.model.Interaction;
import com.hpe.sis.sie.fe.ips.utils.auth.model.VirtualObjects;
import com.hpe.sis.sie.fe.sisutils.channel.util.ChannelUtility;

public class InteractionUtil {

	private static final Logger log = LoggerFactory.getLogger(InteractionUtil.class);

	public static InteractionContextMessage populateInteractionContextMessage(Interaction interaction) {

			InteractionContext interactionContext = new InteractionContext();

			InteractionCtxBasicInfo interactionCtxBasicInfo = getInteractionCtxBasicInfo(interaction);
			if(interaction.getVirtualObject() != null) {
				AppVOBot appVOBot = getAppVOBot(interaction);
				interactionContext.setAppVOBot(appVOBot);
			}
			InteractionBO interactionBO = getInteractionBO(interaction);
			InteractionTxBO interationTxBO = getInteractionTxBO(interaction);
			ChannelTransactionBO channelTransactionBO = getChannelTransactionBO(interaction);
			interactionContext.setAppId(interaction.getApplicationVO().getId());
			interactionContext.setVoId(interaction.getVirtualObject());
			interactionContext.setObeId(interaction.getApplicationVO().getObeId());
			interactionContext.setInteractionContextId(interaction.getInteractionContextId());
			interactionContext.setInteractionId(interaction.getInteractionId());
			interactionContext.setTransactionId(interaction.getTransactionId());
			interactionContext.setInteractionBO(interactionBO);
			interactionContext.setInterationTxBO(interationTxBO);
			interactionContext.setBasicInfo(interactionCtxBasicInfo);
			interactionContext.setBotId(interaction.getBotId());
			InteractionContextMessage interactionContextMessage = new InteractionContextMessage();
			interactionContextMessage.setChannelTransactionBO(channelTransactionBO);
			interactionContextMessage.setInteractionContext(interactionContext);
			
			return interactionContextMessage;

	}

	public static InteractionCtxBasicInfo getInteractionCtxBasicInfo(Interaction interaction) {
		InteractionCtxBasicInfo interactionCtxBasicInfo = new InteractionCtxBasicInfo();
		interactionCtxBasicInfo.setStartTime(String.valueOf(interaction.getRequestedTime()));
		interactionCtxBasicInfo.setEndTime(String.valueOf(System.currentTimeMillis()));
		interactionCtxBasicInfo.setUpdateTime(String.valueOf(System.currentTimeMillis()));
		
		return interactionCtxBasicInfo;
	}

	public static AppVOBot getAppVOBot(Interaction interaction) {
		AppVOBot appVOBot = new AppVOBot();
		VirtualObjects virtualObjects = ChannelUtility.getVirtualObject(interaction.getVirtualObject(),
				interaction.getApplicationVO().getVirtualObjects());
		appVOBot.setName(virtualObjects.getName());
		appVOBot.setType(virtualObjects.getType());

		return appVOBot;
	}

	public static InteractionBO getInteractionBO(Interaction interaction) {

		InteractionBO interactionBO = new InteractionBO();
		/*
		 * interactionBO.setCustom(custom); interactionBO.setSchedule(schedule);
		 * interactionBO.setSessionId(sessionId);
		 */
		interactionBO.setInteractionId(interaction.getInteractionId());
		Channel channel = new Channel();
		channel.setId(interaction.getFromChannel());
		channel.setClientIp(interaction.getRemoteHost());
		channel.setMethod(interaction.getMethod());
		channel.setName(interaction.getVirtualObject());
		channel.setPlType(interaction.getInteractionType());
		channel.setStType(interaction.getStType());
		channel.setTxType(interaction.getTxType());
		channel.setAsId(interaction.getActivityStreamId());
		if(null!=interaction.getRequestHeaders().get("User-Agent"))
		channel.setUserAgent(interaction.getRequestHeaders().get("User-Agent"));
		// channel.setUserLoc(userLoc)
		interactionBO.setChannel(channel);
		return interactionBO;
	}

	public static InteractionTxBO getInteractionTxBO(Interaction interaction) {
		InteractionTxBO interactionTxBO = new InteractionTxBO();
		interactionTxBO.setEndTime(String.valueOf(System.currentTimeMillis()));
		interactionTxBO.setStartTime(String.valueOf(interaction.getRequestedTime()));
		/*
		 * Actor actor = new Actor(); actor.setId(id);
		 * actor.setActorPath(actorPath); actor.setName(name);
		 * actor.setParent(parent); actor.setSystem(system);
		 * actor.setType(type); actor.setUid(uid);
		 * interactionTxBO.setActor(actor);
		 */
		Task task = new Task();
		task.setEngine(interaction.getTask().getEngine());
		task.setId(interaction.getTask().getId());
		task.setMethod(interaction.getMethod());
		task.setVersion(interaction.getTask().getVersion());
		Result result = new Result();
		result.setStatus("PROCESSING");
		task.setResult(result);

		interactionTxBO.setTask(task);

		return interactionTxBO;
	}

	public static ChannelTransactionBO getChannelTransactionBO(Interaction interaction) {
		ChannelTransactionBO channelTransactionBO = new ChannelTransactionBO();
		channelTransactionBO.setAppId(interaction.getApplicationVO().getId());
		channelTransactionBO.setChannelType(interaction.getInteractionType());
		channelTransactionBO.setCreatedBy(interaction.getApplicationVO().getCreatedBy());
		channelTransactionBO.setInteractionContextId(interaction.getInteractionContextId());
		channelTransactionBO.setTimestamp(System.currentTimeMillis());
		channelTransactionBO.setTransactionId(interaction.getTransactionId());

		return channelTransactionBO;
	}

}
