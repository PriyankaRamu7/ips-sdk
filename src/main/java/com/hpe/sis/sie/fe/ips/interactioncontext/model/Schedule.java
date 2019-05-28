package com.hpe.sis.sie.fe.ips.interactioncontext.model;

public class Schedule {

	private static final long serialVersionUID = -4733095795365413357L;

	private String type;
	private String cronExp;
	private String inParams;
	private ExtDataSource extData;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCronExp() {
		return cronExp;
	}

	public void setCronExp(String cronExp) {
		this.cronExp = cronExp;
	}

	public String getInParams() {
		return inParams;
	}

	public void setInParams(String inParams) {
		this.inParams = inParams;
	}

	public ExtDataSource getExtData() {
		return extData;
	}

	public void setExtData(ExtDataSource extData) {
		this.extData = extData;
	}

}
