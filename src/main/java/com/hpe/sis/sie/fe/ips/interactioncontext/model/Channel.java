package com.hpe.sis.sie.fe.ips.interactioncontext.model;

public class Channel {

	private static final long serialVersionUID = -4733095795365413357L;
	
    private String id;
    private UserLoc userLoc;
    private String txType;	
    private String userAgent;
    private String stType; 
    private String plType;
    private String name;
    private String method;
    private String clientIp;
    private String asId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserLoc getUserLoc() {
		return userLoc;
	}

	public void setUserLoc(UserLoc userLoc) {
		this.userLoc = userLoc;
	}

	public String getTxType() {
		return txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getStType() {
		return stType;
	}

	public void setStType(String stType) {
		this.stType = stType;
	}

	public String getPlType() {
		return plType;
	}

	public void setPlType(String plType) {
		this.plType = plType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getAsId() {
		return asId;
	}

	public void setAsId(String asId) {
		this.asId = asId;
	}
    
}
