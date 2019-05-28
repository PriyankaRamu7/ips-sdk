package com.hpe.sis.sie.fe.ips.scheduler.actors;

import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.scheduler.listener.ClusterEventListener;
import com.hpe.sis.sie.fe.ips.scheduler.messages.ScheduleRequest;
import com.hpe.sis.sie.fe.ips.scheduler.messages.ScheduleResponse;
import com.hpe.sis.sie.fe.ips.scheduler.util.ScheduleModule;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class IPSSchedulerActor extends UntypedAbstractActor {

	private SisIpsActorSystem ipsActorSystem;

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public IPSSchedulerActor() {

		ipsActorSystem = SisIpsActorSystem.getInstance();
		ActorSystem actorSystem = ipsActorSystem.getIpsActorSystem();
		ClusterSingletonManagerSettings settings = ClusterSingletonManagerSettings.create(actorSystem)
				.withRole("reschedule");
		actorSystem.actorOf(ClusterSingletonManager.props(Props.create(ClusterSingletonActor.class),
				PoisonPill.getInstance(), settings), "cluster_Singleton_Actor");
		ClusterSingletonProxySettings proxySettings = ClusterSingletonProxySettings.create(actorSystem)
				.withRole("scheduleProxy");
		SisIpsActorSystem.getInstance().clusterSingletonProxy = actorSystem.actorOf(
				ClusterSingletonProxy.props("/user/cluster_Singleton_Actor", proxySettings), "cluster_Singleton_Proxy");

		ActorRef clusterEventListener = actorSystem.actorOf(Props.create(ClusterEventListener.class), "listener_actor");
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof ScheduleRequest) {
			log.debug(getSelf().path() + " -- Received ScheduleRequest");
			ScheduleRequest scheduleRequest = (ScheduleRequest) message;
			scheduleRequest.setActorPath(ipsActorSystem.getIpsActorSystem().provider().getDefaultAddress().toString());
			ScheduleResponse scheduleResponse = ScheduleModule.scheduleJob(scheduleRequest);
			scheduleResponse.setActorPath(scheduleRequest.getActorPath());
			getSender().tell(scheduleResponse, getSelf());
		}
		if (message instanceof String) {
			String jobId = (String) message;
			log.debug(getSelf().path() + " -- Received cancel request for jobId : " + jobId);
			String result = ScheduleModule.cancelJob(jobId);
			getSender().tell(result, getSelf());
		}
	}

}
