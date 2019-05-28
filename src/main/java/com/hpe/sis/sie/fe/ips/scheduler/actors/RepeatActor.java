package com.hpe.sis.sie.fe.ips.scheduler.actors;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

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

public class RepeatActor extends UntypedAbstractActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private static SisIpsActorSystem ipsActorSystem = SisIpsActorSystem.getInstance();
	
	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof ScheduleRequest) {
			log.debug(getSelf().path() + " -- Received ScheduleRequest for processing");
			ScheduleRequest scheduleRequest = (ScheduleRequest) message;
			BotMessage botMessage = ScheduleModule.populateBotMessage(scheduleRequest);
			Map<String, String> traceDataMap = new HashMap<>();
			traceDataMap.put("BOT_ID", scheduleRequest.getJobId());

			if (scheduleRequest.getTaskDataList().size() > 0) {
				for (TaskDetails taskData : scheduleRequest.getTaskDataList()) {
					TraceContext traceContext1 = Tracer.start(taskData.getTransactionId(), "Repeat Bot execution", "RepeatActor.onReceive", getSelf().path().toString(), traceDataMap);
					botMessage.setBotTaskData(taskData.getTaskDataJson()); 																					
					botMessage.setBotTaskHeader(taskData.getTaskHeader());
					botMessage.setTransactionId(taskData.getTransactionId());
					
					int executionCount = (int) SchedulerCAO.getExecutionCountFromDss(scheduleRequest.getJobId());
					int requiredCount = scheduleRequest.getRepeatitionCount() + 1;
					if (requiredCount >= executionCount) {
						executionCount++;
						scheduleRequest.setPreviousStatus(scheduleRequest.getStatus());
						if (requiredCount == executionCount) {
							TraceContext traceContext2 = Tracer.start(botMessage.getTransactionId(), "Cancelling Repeat Bot execution since repeat count is achieved", "RepeatActor.onReceive", getSelf().path().toString(), traceDataMap);
							ScheduleModule.cancelJob(scheduleRequest.getJobId());
							Tracer.end(traceContext2, "Cancelled Repeat Bot execution since repeat count is achieved", TraceConstants.SUCCESS);
							scheduleRequest.setStatus(SchedulerConstants.COMPLETED);
							scheduleRequest.setNextExcetuionTime(null);
							scheduleRequest.setLastExecutionTime(new Date());
							SchedulerCAO.deleteJobFromDss(scheduleRequest.getJobId());
							log.info("Repeat job execution completed with status:  " + scheduleRequest.getStatus());
						} else {
							scheduleRequest.setStatus(SchedulerConstants.RUNNING);
							DateTime lastexecution = new DateTime();
							scheduleRequest.setNextExcetuionTime(lastexecution.plus(scheduleRequest.getFrequencyInterval()).toDate());
							scheduleRequest.setLastExecutionTime(lastexecution.toDate());
							scheduleRequest.setExecutionCount(executionCount);
							SchedulerCAO.updateJobToDss(scheduleRequest);
						}
						ipsActorSystem.notifyActor.tell(botMessage, ActorRef.noSender());
						Tracer.end(traceContext1, "Repeat Bot execution - Invoked Notify actor for bot execution", TraceConstants.SUCCESS);
						scheduleRequest.setExecutionCount(executionCount);
						SchedulerCAO.updateJobStatusToDss(scheduleRequest);
						
				}
			} 
			}else {
				log.error("Scheduler taskData list cannot be empty");
			}

		}

	}
}
