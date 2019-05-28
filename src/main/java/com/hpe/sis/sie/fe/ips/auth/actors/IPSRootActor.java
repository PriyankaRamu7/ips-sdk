package com.hpe.sis.sie.fe.ips.auth.actors;

import java.util.Optional;

import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.logging.actors.InteractionContextDSSLoggerActor;
import com.hpe.sis.sie.fe.ips.logging.actors.InteractionContextFileLoggerActor;
import com.hpe.sis.sie.fe.ips.logging.actors.TraceLoggerActor;
import com.hpe.sis.sie.fe.ips.logging.actors.TransactionLoggerActor;
import com.hpe.sis.sie.fe.ips.processing.actors.AsynchronousActor;
import com.hpe.sis.sie.fe.ips.processing.actors.NotifyActor;
import com.hpe.sis.sie.fe.ips.processing.actors.SynchronousActor;

import com.hpe.sis.sie.fe.ips.scheduler.actors.IPSSchedulerManager;
import com.typesafe.config.Config;

import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import akka.routing.RoundRobinPool;

public class IPSRootActor extends UntypedAbstractActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private SisIpsActorSystem ipsActorSystem;
	
	public IPSRootActor() {
		ipsActorSystem = SisIpsActorSystem.getInstance();
		Config config = ipsActorSystem.getIpsActorConfig();
		/*int lower_bound = config.getInt("akka.actor.deployment./ips_security_actor.resizer.lower-bound");
		int upper_bound = config.getInt("akka.actor.deployment./ips_security_actor.resizer.upper-bound");
		DefaultResizer resizer = new DefaultResizer(lower_bound, upper_bound);
		
		ipsActorSystem.ipsSecurityActor = getContext().actorOf(new RoundRobinPool(5).withResizer(resizer).props(
				    Props.create(IPSSecurityActor.class)), "ips_security_actor");// new RoundRobinPool(5)- 5? need to test and check on this.
		 */		
		/*lower_bound = config.getInt("akka.actor.deployment./proc_actor.resizer.lower-bound");
		upper_bound = config.getInt("akka.actor.deployment./proc_actor.resizer.upper-bound");
		DefaultResizer procResizer = new DefaultResizer(lower_bound, upper_bound);
		
		ipsActorSystem.interactionProcessingActor = getContext().actorOf(new RoundRobinPool(5).withResizer(procResizer).props(
			    Props.create(InteractionProcessingActor.class)), "proc_actor");*/
		
		int nr_of_instances;
		
		nr_of_instances = config.getInt("akka.actor.deployment./ips_security_actor.nr-of-instances");
		ipsActorSystem.ipsSecurityActor = getContext().actorOf(Props.create(IPSSecurityActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"ips_security_actor");
		
		nr_of_instances = config.getInt("akka.actor.deployment./sync_actor.nr-of-instances");
		ipsActorSystem.synchronousActor = getContext().actorOf(Props.create(SynchronousActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"sync_actor");
		
		 nr_of_instances = config.getInt("akka.actor.deployment./async_actor.nr-of-instances");
		 ipsActorSystem.asynchronousActor = getContext().actorOf(Props.create(AsynchronousActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"async_actor");
		 
		 nr_of_instances = config.getInt("akka.actor.deployment./notify_actor.nr-of-instances");
		 ipsActorSystem.notifyActor = getContext().actorOf(Props.create(NotifyActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"notify_actor");
		 
		 nr_of_instances = config.getInt("akka.actor.deployment./transactionLogging_actor.nr-of-instances");
		 ipsActorSystem.transactionLoggingActor = getContext().actorOf(Props.create(TransactionLoggerActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"transactionLogging_actor");
		 
		 nr_of_instances = config.getInt("akka.actor.deployment./traceLogging_actor.nr-of-instances");
		 ipsActorSystem.traceLoggingActor = getContext().actorOf(Props.create(TraceLoggerActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"traceLogging_actor");
	
		 nr_of_instances = config.getInt("akka.actor.deployment./interactionContextDSSLogger_actor.nr-of-instances");
		 ipsActorSystem.interactionContextDSSLoggerActor = getContext().actorOf(Props.create(InteractionContextDSSLoggerActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"interactionContextDSSLogger_actor");
		 
		 nr_of_instances = config.getInt("akka.actor.deployment./interactionContextFileLogger_actor.nr-of-instances");
		 ipsActorSystem.interactionContextFileLoggerActor = getContext().actorOf(Props.create(InteractionContextFileLoggerActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"interactionContextFileLogger_actor");
		 
		 /*nr_of_instances = config.getInt("akka.actor.deployment./scheduler_actor.nr-of-instances");
		 ipsActorSystem.ipsSchedulerActor = getContext().actorOf(Props.create(IPSSchedulerActor.class).withRouter(new BalancingPool(nr_of_instances)),"scheduler_actor");*/
		// ipsActorSystem.ipsSchedulerManager = ipsActorSystem.getIpsActorSystem().actorOf(Props.create(IPSSchedulerManager.class), "scheduler_manager");
		 nr_of_instances = config.getInt("akka.actor.deployment./scheduler_manager.nr-of-instances");
		 ipsActorSystem.ipsSchedulerManager = getContext().actorOf(Props.create(IPSSchedulerManager.class).withRouter(new RoundRobinPool(nr_of_instances)),"scheduler_manager");
	}
	
	
	@Override
	public void preStart() throws Exception {
		// TODO Auto-generated method stub
		super.preStart();
		//System.out.println(getSelf().path() + " preStart called");
	}

	@Override
	public void preRestart(Throwable reason, Optional<Object> message) throws Exception {
		// TODO Auto-generated method stub
		super.preRestart(reason, message);
		System.out.println(getSelf().path() + " preRestart called");
	}

	@Override
	public void postRestart(Throwable reason) throws Exception {
		// TODO Auto-generated method stub
		super.postRestart(reason);
		System.out.println(getSelf().path() + " postRestart called");
	}

	@Override
	public void onReceive(Object message) throws Throwable {

		if (message instanceof Terminated) {

			log.info("*********** IPSRootActor received Terminated message ***********:  " + message);
		}
	}

	@Override
	public void postStop() throws Exception {
		// TODO Auto-generated method stub
		super.postStop();
		//System.out.println(getSelf().path() + " postStop called");
	}
}
