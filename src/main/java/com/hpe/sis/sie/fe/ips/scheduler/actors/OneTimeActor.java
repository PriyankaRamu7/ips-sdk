package com.hpe.sis.sie.fe.ips.scheduler.actors;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.scheduler.constants.SchedulerConstants;
import com.hpe.sis.sie.fe.ips.scheduler.messages.BotMessage;
import com.hpe.sis.sie.fe.ips.scheduler.messages.ScheduleRequest;
import com.hpe.sis.sie.fe.ips.scheduler.model.TaskDetails;
import com.hpe.sis.sie.fe.ips.scheduler.util.ScheduleModule;
import com.hpe.sis.sie.fe.ips.scheduler.util.SchedulerCAO;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class OneTimeActor extends UntypedAbstractActor {
	
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private static SisIpsActorSystem ipsActorSystem = SisIpsActorSystem.getInstance();
		
	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof ScheduleRequest) {
			log.debug(getSelf().path() + " -- Received ScheduleRequest for processing");
			ScheduleRequest scheduleRequest = (ScheduleRequest) message;
			BotMessage botMessage = ScheduleModule.populateBotMessage(scheduleRequest);
			Map<String, String> traceDataMap = new HashMap<>();
			traceDataMap.put("BOT_ID", scheduleRequest.getJobId());

			if(scheduleRequest.getTaskDataList().size() > 0) {
				for(TaskDetails taskData : scheduleRequest.getTaskDataList()) {
					TraceContext traceContext = Tracer.start(taskData.getTransactionId(), "OneTime Bot execution", "OneTimeActor.onReceive", getSelf().path().toString(), traceDataMap);
					botMessage.setBotTaskData(taskData.getTaskDataJson()); // needed for transformation
					botMessage.setBotTaskHeader(taskData.getTaskHeader());
					botMessage.setTransactionId(taskData.getTransactionId());
					ipsActorSystem.notifyActor.tell(botMessage, ActorRef.noSender());
					Tracer.end(traceContext, "OneTime Bot execution - Invoked Notify actor for bot execution", TraceConstants.SUCCESS);
					
					scheduleRequest.setPreviousStatus(SchedulerConstants.RUNNING);
					scheduleRequest.setStatus(SchedulerConstants.COMPLETED);
					scheduleRequest.setNextExcetuionTime(null);
					scheduleRequest.setLastExecutionTime(new Date());
					scheduleRequest.setExecutionCount(1);
					SchedulerCAO.updateJobStatusToDss(scheduleRequest);
					SchedulerCAO.deleteJobFromDss(scheduleRequest.getJobId());
					ScheduleModule.cancellableMap.remove(scheduleRequest.getJobId());
				}
			} else {
				log.error("Scheduler taskData list cannot be empty");
			}
		}
	}

}
