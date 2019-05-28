package com.hpe.sis.sie.fe.ips.interactioncontext.model;

public class InteractionContext {

	private static final long serialVersionUID = 1L;

	private String appId;
	private String InteractionContextId;
	private String interactionId;
	private String voId;
	private String botId;
	private String obeId;
	private String transactionId;
	private InteractionCtxBasicInfo basicInfo;
	private AppVOBot appVOBot;
	private InteractionBO interactionBO;
	private InteractionTxBO interationTxBO;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getInteractionContextId() {
		return InteractionContextId;
	}

	public void setInteractionContextId(String interactionContextId) {
		InteractionContextId = interactionContextId;
	}

	public String getInteractionId() {
		return interactionId;
	}

	public void setInteractionId(String interactionId) {
		this.interactionId = interactionId;
	}

	public String getVoId() {
		return voId;
	}

	public void setVoId(String voId) {
		this.voId = voId;
	}

	public String getBotId() {
		return botId;
	}

	public void setBotId(String botId) {
		this.botId = botId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public InteractionCtxBasicInfo getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(InteractionCtxBasicInfo basicInfo) {
		this.basicInfo = basicInfo;
	}

	public AppVOBot getAppVOBot() {
		return appVOBot;
	}

	public void setAppVOBot(AppVOBot appVOBot) {
		this.appVOBot = appVOBot;
	}

	public InteractionBO getInteractionBO() {
		return interactionBO;
	}

	public void setInteractionBO(InteractionBO interactionBO) {
		this.interactionBO = interactionBO;
	}

	public InteractionTxBO getInterationTxBO() {
		return interationTxBO;
	}

	public void setInterationTxBO(InteractionTxBO interationTxBO) {
		this.interationTxBO = interationTxBO;
	}

	public String getObeId() {
		return obeId;
	}

	public void setObeId(String obeId) {
		this.obeId = obeId;
	}

}
