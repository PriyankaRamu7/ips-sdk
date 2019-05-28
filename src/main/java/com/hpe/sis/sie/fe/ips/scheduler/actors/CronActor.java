package com.hpe.sis.sie.fe.ips.scheduler.actors;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hpe.sis.sie.fe.dss.exception.SISException;
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

public class CronActor extends UntypedAbstractActor {
	
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
					TraceContext traceContext = Tracer.start(taskData.getTransactionId(), "Cron Bot execution", "CronActor.onReceive", getSelf().path().toString(), traceDataMap);
					botMessage.setBotTaskData(taskData.getTaskDataJson()); 																					
					botMessage.setBotTaskHeader(taskData.getTaskHeader());
					botMessage.setTransactionId(taskData.getTransactionId());
					
					if (scheduleRequest.getEndDate() != null) {
						
						//if (scheduleRequest.getEndDate().equals(new Date()) || scheduleRequest.getEndDate().after(new Date())) {
							ipsActorSystem.notifyActor.tell(botMessage, ActorRef.noSender());
							Tracer.end(traceContext, "Cron Bot execution - Invoked Notify actor for bot execution", TraceConstants.SUCCESS);
							
							Date lastExecution = new Date(Instant.now().toEpochMilli());
							Date nextExecution = new Date(Instant.now().toEpochMilli() + scheduleRequest.getCronJobInterval()); // cronJobInterval TODO					
							scheduleRequest.setStatus(SchedulerConstants.RUNNING);
							scheduleRequest.setNextExcetuionTime(nextExecution);;
							scheduleRequest.setLastExecutionTime(lastExecution);
							scheduleRequest.setExecutionCount(scheduleRequest.getExecutionCount()+1);
							SchedulerCAO.updateJobToDss(scheduleRequest);
							SchedulerCAO.updateJobStatusToDss(scheduleRequest);
							
							if(scheduleRequest.getNextExcetuionTime().after(scheduleRequest.getEndDate())) {
								TraceContext traceContext2 = Tracer.start(botMessage.getTransactionId(), "Cancelling Cron Bot execution since end date is reached", "CronActor.onReceive", getSelf().path().toString(), traceDataMap);
								ScheduleModule.cancelJob(scheduleRequest.getJobId());
								Tracer.end(traceContext, "Cancelled Cron Bot execution since end date is reached", TraceConstants.SUCCESS);
								updateJobDetails(scheduleRequest);
							}

						/*} else {
							ScheduleModule.cancelJob(scheduleRequest.getJobId());
							updateJobDetails(scheduleRequest);
						}*/
					} else {
						log.error("End date cannot be null:" + scheduleRequest.getJobId());
					}
					
					
				}
			}
		}
		
	}

	private void updateJobDetails(ScheduleRequest scheduleRequest) throws SISException {
		scheduleRequest.setPreviousStatus(SchedulerConstants.RUNNING);
		scheduleRequest.setStatus(SchedulerConstants.COMPLETED);
		scheduleRequest.setNextExcetuionTime(null);
		scheduleRequest.setLastExecutionTime(new Date());
		SchedulerCAO.updateJobStatusToDss(scheduleRequest);
		SchedulerCAO.deleteJobFromDss(scheduleRequest.getJobId());
	}
}
