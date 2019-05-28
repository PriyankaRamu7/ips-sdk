package com.hpe.sis.sie.fe.ips.scheduler.actors;

import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.scheduler.messages.CancelRequest;
import com.hpe.sis.sie.fe.ips.scheduler.messages.ScheduleRequest;
import com.hpe.sis.sie.fe.ips.scheduler.messages.ScheduleResponse;
import com.typesafe.config.Config;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.BalancingPool;

public class IPSSchedulerManager extends UntypedAbstractActor{
	
	private ActorRef nonActorSender;
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private SisIpsActorSystem ipsActorSystem;
	
	public IPSSchedulerManager() {
		ipsActorSystem = SisIpsActorSystem.getInstance();
		Config config = ipsActorSystem.getIpsActorConfig();	
		int nr_of_instances;
		nr_of_instances = config.getInt("akka.actor.deployment./oneTime_actor.nr-of-instances");
		ipsActorSystem.oneTimeActor = getContext().actorOf(Props.create(OneTimeActor.class).withRouter(new BalancingPool(nr_of_instances)),"oneTime_actor");
		nr_of_instances = config.getInt("akka.actor.deployment./repeat_actor.nr-of-instances");
		ipsActorSystem.repeatActor = getContext().actorOf(Props.create(RepeatActor.class).withRouter(new BalancingPool(nr_of_instances)),"repeat_actor");
		nr_of_instances = config.getInt("akka.actor.deployment./cron_actor.nr-of-instances");
		ipsActorSystem.cronActor = getContext().actorOf(Props.create(CronActor.class).withRouter(new BalancingPool(nr_of_instances)),"cron_actor");
	}
	@Override
	public void onReceive(Object message) throws Throwable {
		
		SisIpsActorSystem ipsActorSystem = SisIpsActorSystem.getInstance();
		
		if(message instanceof ScheduleRequest) {
			log.debug(getSelf().path() + " -- Received ScheduleRequest");
			ScheduleRequest scheduleMessage = (ScheduleRequest) message;
			nonActorSender = getSender();
			
			ipsActorSystem.ipsSchedulerActor.tell(scheduleMessage, getSelf());
		}
		if(message instanceof CancelRequest) {
			log.debug(getSelf().path() + " -- Received cancelRequest");
			CancelRequest cancelRequest = (CancelRequest) message;
			nonActorSender = getSender();
			getContext().actorSelection(cancelRequest.getActorPath() + "/user/scheduler_actor").tell(cancelRequest.getJobId(), getSelf());
		}
		
		if(message instanceof ScheduleResponse) {
			log.debug(getSelf().path() + " -- Received ScheduleResponse");
			nonActorSender.tell((ScheduleResponse) message, getSelf());
		}
		
		if(message instanceof String) {
			nonActorSender.tell((String) message, getSelf());
		}
	}

}
