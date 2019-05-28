package com.hpe.sis.sie.fe.ips.responsecache;

public class ResponseCacheVO {
	
	 private String virtualObjectOrBotId;
	 
	 private String queryString;
	 
	 private long timeToLive;
	 
	 private String requestURI;

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public String getVirtualObjectOrBotId() {
		return virtualObjectOrBotId;
	}

	public void setVirtualObjectOrBotId(String virtualObjectOrBotId) {
		this.virtualObjectOrBotId = virtualObjectOrBotId;
	}
	 

}
