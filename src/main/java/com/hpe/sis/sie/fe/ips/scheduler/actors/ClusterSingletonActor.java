package com.hpe.sis.sie.fe.ips.scheduler.actors;

import java.util.List;

import com.hpe.sis.sie.fe.ips.scheduler.constants.SchedulerConstants;
import com.hpe.sis.sie.fe.ips.scheduler.model.ScheduleJob;
import com.hpe.sis.sie.fe.ips.scheduler.service.IPSScheduler;
import com.hpe.sis.sie.fe.ips.scheduler.service.IPSSchedulerImpl;
import com.hpe.sis.sie.fe.ips.scheduler.util.SchedulerCAO;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClusterSingletonActor extends UntypedAbstractActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private SchedulerCAO schedulerCAO = new SchedulerCAO();
	private IPSScheduler ipsScheduler = new IPSSchedulerImpl();
	private static boolean rescheduled = false;

	public static Props props() {
		return Props.create(ClusterSingletonActor.class);
	}

	@Override
	public void preStart() {
		log.info(" ********************** Inside preStart() of Cluster Singleton **********************"
				+ getSelf().path());
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		
		if (message instanceof String) {

			if (message.equals(SchedulerConstants.RESCHEDULE_ON_STARTUP)) {
				
				if (!rescheduled) {
					log.info("********************** Cluster Singleton Actor Received message to reschedule jobs at startUp: **********************"+ message);
					rescheduled = true;					
					List<ScheduleJob> jobsList = schedulerCAO.getJobForReschedule();
					log.info("********************** Cluster Singleton Actor retrieved jobs for reschedule : **********************" + jobsList);
					ipsScheduler.reschedule(jobsList);
				}
			} else if(((String) message).indexOf("akka.tcp://SisIpsActorSystem@") != -1){
				log.info("********************** Cluster Singleton Actor Received message to reschedule jobs handled by unreachable member: **********************"+ message);
				String actorPath = (String) message;
				rescheduled = true;
				List<ScheduleJob> jobsList = schedulerCAO.getJobForReschedule(actorPath);
				log.info("********************** Cluster Singleton Actor going to reschedule jobs handled by unreachable member: **********************" + jobsList + "Unreachable member actorpath" + actorPath);
				if(!jobsList.isEmpty())
					ipsScheduler.reschedule(jobsList);
			}
		}else
	      {
	        unhandled(message);
	      }
	}

	@Override
	public void postStop() {
		log.info(" $$$$$$$$$$$$$$$$$$$$$$$$$$$ Inside postStop() of Cluster Singleton $$$$$$$$$$$$$$$$$$$$$$$$$$$");
	}

}
