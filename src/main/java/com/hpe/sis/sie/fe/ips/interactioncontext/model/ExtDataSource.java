package com.hpe.sis.sie.fe.ips.interactioncontext.model;

public class ExtDataSource {

	private static final long serialVersionUID = -4733095795365413357L;

	private boolean isUsed;
	private String url;

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
