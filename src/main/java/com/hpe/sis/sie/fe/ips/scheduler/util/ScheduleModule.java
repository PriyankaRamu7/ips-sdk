package com.hpe.sis.sie.fe.ips.scheduler.util;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.processing.utils.ProcessingPattern;
import com.hpe.sis.sie.fe.ips.scheduler.constants.SchedulerConstants;
import com.hpe.sis.sie.fe.ips.scheduler.messages.BotMessage;
import com.hpe.sis.sie.fe.ips.scheduler.messages.ScheduleRequest;
import com.hpe.sis.sie.fe.ips.scheduler.messages.ScheduleResponse;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import scala.concurrent.duration.Duration;

public class ScheduleModule {

	private static SisIpsActorSystem ipsActorSystem = SisIpsActorSystem.getInstance();;
	public static Map<String, Cancellable> cancellableMap = new HashMap<>();
	private static final Logger log = LoggerFactory.getLogger(ScheduleModule.class);

	public static ScheduleResponse scheduleJob(ScheduleRequest scheduleMessage) {

		Cancellable cancellable = null;
		log.info("Scheduling job with actorPath : " + scheduleMessage.getActorPath());
		ScheduleResponse scheduleResponse = new ScheduleResponse();
		ActorSystem actorSystem = ipsActorSystem.getIpsActorSystem();
		long diff = scheduleMessage.getStartDate().getTime() - Instant.now().toEpochMilli();
		try {
			if (scheduleMessage.getJobType().equals(SchedulerConstants.ONETIME)) {
				cancellable = actorSystem.scheduler().scheduleOnce(Duration.create(diff, TimeUnit.MILLISECONDS),
						ipsActorSystem.oneTimeActor, scheduleMessage, ipsActorSystem.getIpsActorSystem().dispatcher(), null);
			}
			if (scheduleMessage.getJobType().equals(SchedulerConstants.REPEAT)) {
				cancellable = actorSystem.scheduler().schedule(Duration.create(diff, TimeUnit.MILLISECONDS),
						Duration.create(scheduleMessage.getFrequencyInterval(), TimeUnit.MILLISECONDS), ipsActorSystem.repeatActor,
						scheduleMessage, ipsActorSystem.getIpsActorSystem().dispatcher(), null);
			}
			if (scheduleMessage.getJobType().equals(SchedulerConstants.CRON)) {
				List<DateTime> executionTimeList = CronUtils.getExceutionTimeList(scheduleMessage.getStartDate(),
						scheduleMessage.getCronPattern());
				DateTime firstExecution = executionTimeList.get(0);
				DateTime secondExecution = executionTimeList.get(1);
				/*long cronJobInterval = org.threeten.bp.Duration.between(executionTimeList.get(0), executionTimeList.get(1))
						.toMillis();*/
				org.joda.time.Duration duration = new org.joda.time.Duration(firstExecution, secondExecution);
				long cronJobInterval = duration.getMillis();
				//long cronJobInterval  = executionTimeList.get(1).toInstant().toEpochMilli() - executionTimeList.get(0).toInstant().toEpochMilli();
				scheduleMessage.setCronJobInterval(cronJobInterval); // TODO
				//long initialDelay = executionTimeList.get(0).toInstant().toEpochMilli() - ZonedDateTime.now().toInstant().toEpochMilli();
				long initialDelay = firstExecution.getMillis() - Instant.now().toEpochMilli();
				
				cancellable = actorSystem.scheduler().schedule(Duration.create(initialDelay, TimeUnit.MILLISECONDS),
						Duration.create(cronJobInterval, TimeUnit.MILLISECONDS), ipsActorSystem.cronActor, scheduleMessage,
						ipsActorSystem.getIpsActorSystem().dispatcher(), null);
			}
			cancellableMap.put(scheduleMessage.getJobId(), cancellable);
			scheduleResponse.setResult(SchedulerConstants.SUCCESS);
		} catch(Exception e) {
			scheduleResponse.setResult(e.getMessage());
		}
		return scheduleResponse;
	}
	
	public static String cancelJob(String jobId) {
		String result;
		try {
			Cancellable cancellable = cancellableMap.get(jobId);
			boolean cancel = false;
			if (cancellable != null) {
				if (!cancellable.isCancelled()) // check not reqd? TODO
					cancel = cancellable.cancel();
				cancellableMap.remove(jobId);
				result = SchedulerConstants.SUCCESS;
				log.info("Job with JobId: " + jobId + " Successfully Cancelled: " + cancel);
			} else {
				throw new Exception("No entry of Cancellable instance for job " + jobId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		return result;
	}
	
	public static BotMessage populateBotMessage(ScheduleRequest scheduleRequest) {
		BotMessage botMessage = new BotMessage();
		botMessage.setApplication(scheduleRequest.getApplication());
		//interactionRequest.setBeTimeOut(beTimeOut);
		botMessage.setBotId(scheduleRequest.getJobId());
		botMessage.setFromChannel(IPSConfig.CHANNEL_NAME);
		//interactionRequest.setInteractionContextId(interactionContextId);
		botMessage.setMethod("POST");
		//interactionRequest.setNonActorSender(nonActorSender);
		//interactionRequest.setParamList(paramList);
		botMessage.setProcessingPattern(ProcessingPattern.Notify.toString());
		//interactionRequest.setQueryParameters(queryParameters);
		botMessage.setRemoteHost(scheduleRequest.getRemoteHost());
		//interactionRequest.setRequestBody(requestBody);
		botMessage.setRequestedTime(scheduleRequest.getRequestedTime());
		//interactionRequest.setRequestHeaders(requestHeaders);
		botMessage.setServiceId(scheduleRequest.getServiceId());
		//interactionRequest.setSisHeaders(sisHeaders);
		//interactionRequest.setSyncActorRef(syncActorRef);
		botMessage.setTestRequest(false);
		//interactionRequest.setVirtualObjectId(virtualObjectId);
		
		return botMessage;
	}
	
}
