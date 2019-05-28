package com.hpe.sis.sie.fe.ips.interactioncontext.model;

import akka.actor.ActorPath;

public class Actor {

	private static final long serialVersionUID = -4733095795365413357L;

	private String id;

	private String parent;

	private String type;

	private String actorPath;

	private String name;

	private String system;

	private int uid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getActorPath() {
		return actorPath;
	}

	public void setActorPath(String actorPath) {
		this.actorPath = actorPath;
	}

}
