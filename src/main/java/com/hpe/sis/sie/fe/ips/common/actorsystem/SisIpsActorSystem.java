package com.hpe.sis.sie.fe.ips.common.actorsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.BalancingPool;
import akka.routing.FromConfig;

import com.hpe.sis.sie.fe.ips.auth.actors.IPSRootActor;
import com.hpe.sis.sie.fe.ips.processing.actors.InteractionProcessingActor;
import com.hpe.sis.sie.fe.ips.scheduler.actors.IPSSchedulerActor;
import com.hpe.sis.sie.fe.ips.scheduler.actors.IPSSchedulerManager;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SisIpsActorSystem {

	private static final Logger log = LoggerFactory.getLogger(SisIpsActorSystem.class);

	private static SisIpsActorSystem ipsSystem;
	
	private ActorSystem ipsActorSystem;
	private Config ipsActorConfig;
	
	public ActorRef ipsRootActor;
	public ActorRef ipsSecurityActor;
	public ActorRef interactionProcessingActor;
	public ActorRef synchronousActor;
	public ActorRef asynchronousActor;
	public ActorRef notifyActor;
	public ActorRef transactionLoggingActor;
	public ActorRef securityAuditLoggingActor;
	public ActorRef interactionContextDSSLoggerActor;
	public ActorRef interactionContextFileLoggerActor;
	public ActorRef ipsSchedulerManager;
	public ActorRef ipsSchedulerActor;
	public ActorRef clusterSingletonProxy;
	public ActorRef oneTimeActor;
	public ActorRef repeatActor;
	public ActorRef cronActor;
	public ActorRef traceLoggingActor;
	
	public static SisIpsActorSystem getInstance() {
		if (ipsSystem == null) {
			ipsSystem = new SisIpsActorSystem();
			ipsSystem.initializeActorSystem();
		}
		return ipsSystem;
	}

	private void initializeActorSystem() {
		ipsSystem.ipsActorConfig = ConfigFactory.load();
		ipsSystem.ipsActorSystem = ActorSystem.create("SIS-IPS-ActorSystem", ipsActorConfig);
		
		initializeRootActors();
	}

	private void initializeRootActors() {
		ipsSystem.ipsRootActor = ipsActorSystem.actorOf(FromConfig.getInstance().props(Props.create(IPSRootActor.class)), "ips_root_actor");

		ipsSystem.ipsSchedulerActor = ipsActorSystem.actorOf(Props.create(IPSSchedulerActor.class), "scheduler_actor");
		
	}

	/**
	 * @return the ipsActorConfig
	 */
	public Config getIpsActorConfig() {
		return ipsActorConfig;
	}

	/**
	 * @param ipsActorConfig the ipsActorConfig to set
	 */
	public void setIpsActorConfig(Config ipsActorConfig) {
		this.ipsActorConfig = ipsActorConfig;
	}

	/**
	 * @return the ipsActorSystem
	 */
	public ActorSystem getIpsActorSystem() {
		return ipsActorSystem;
	}

	/**
	 * @param ipsActorSystem the ipsActorSystem to set
	 */
	public void setIpsActorSystem(ActorSystem ipsActorSystem) {
		this.ipsActorSystem = ipsActorSystem;
	}
	
}