package com.hpe.sis.sie.fe.ips.scheduler.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.dss.exception.SISException;
import com.hpe.sis.sie.fe.ips.auth.constants.AuthConstants;
import com.hpe.sis.sie.fe.ips.auth.exception.IPSException;
import com.hpe.sis.sie.fe.ips.common.ApplicationCAO;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.IPSConstants;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.scheduler.constants.SchedulerConstants;
import com.hpe.sis.sie.fe.ips.scheduler.messages.CancelRequest;
import com.hpe.sis.sie.fe.ips.scheduler.messages.ScheduleRequest;
import com.hpe.sis.sie.fe.ips.scheduler.messages.ScheduleResponse;
import com.hpe.sis.sie.fe.ips.scheduler.model.ScheduleJob;
import com.hpe.sis.sie.fe.ips.scheduler.util.DateTimeUtils;
import com.hpe.sis.sie.fe.ips.scheduler.util.SchedulerCAO;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;

import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class IPSSchedulerImpl implements IPSScheduler {

	private static Logger log = LoggerFactory.getLogger(IPSSchedulerImpl.class);
	private static SisIpsActorSystem ipsActorSystem = SisIpsActorSystem.getInstance();
	
	@Override
	public String schedule(ScheduleJob scheduleJob) throws IPSException {
		try {
			String actorPath = scheduleJob(scheduleJob);
			scheduleJob.setActorPath(actorPath);
			saveJobDetails(scheduleJob, SchedulerConstants.SCHEDULED);
		} catch (SISException e) {
			e.printStackTrace();
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(AuthConstants.DSS_SERVER_ERR_CODE),
					AuthConstants.DSS_SERVER_ERR_MSG);
		}
		return SchedulerConstants.SUCCESS;
	}

	@Override
	public String pause(String jobId) throws IPSException {
		String result;
		try {
			ScheduleJob job = SchedulerCAO.getJobFromDss(jobId); // reading job should be done here since we need actorpath here only															
			result = cancelJob(jobId, job.getActorPath());
			saveJobDetails(job, SchedulerConstants.PAUSED);
		} catch (SISException e) {
			e.printStackTrace();
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(AuthConstants.DSS_SERVER_ERR_CODE),
					AuthConstants.DSS_SERVER_ERR_MSG);
		}
		return result;
	}

	@Override
	public String resume(String jobId) throws IPSException {
		try {
			ScheduleJob scheduleJob = checkEligibilityAndSchedule(SchedulerCAO.getJobFromDss(jobId));
			saveJobDetails(scheduleJob, scheduleJob.getStatus());
		} catch (SISException e) {
			e.printStackTrace();
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(AuthConstants.DSS_SERVER_ERR_CODE),
					AuthConstants.DSS_SERVER_ERR_MSG);
		}
		return SchedulerConstants.SUCCESS;
	}

	@Override
	public String delete(String jobId) throws IPSException {
		String result;
		try {
			boolean isCompletedJob = SchedulerCAO.isCompletedJob(jobId);
			if (isCompletedJob) {
				SchedulerCAO.deleteBotStatus(jobId);
				result = SchedulerConstants.SUCCESS;
			} else {
				ScheduleJob scheduleJob = SchedulerCAO.getJobFromDss(jobId);
				result = cancelJob(jobId, scheduleJob.getActorPath());
				saveJobDetails(scheduleJob, SchedulerConstants.DELETE);
			}
		} catch (SISException e) {
			e.printStackTrace();
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(AuthConstants.DSS_SERVER_ERR_CODE),
					AuthConstants.DSS_SERVER_ERR_MSG);
		}
		return result;
	}

	@Override
	public void reschedule(List<ScheduleJob> jobsList) {
		
		for (ScheduleJob job : jobsList) {
			ScheduleJob scheduleJob = new ScheduleJob();
			try {
				if(!job.getStatus().equals(SchedulerConstants.PAUSED)) {
					scheduleJob = checkEligibilityAndSchedule(job);
				}
				saveJobDetails(scheduleJob, scheduleJob.getStatus());
			} catch (IPSException | SISException e) {
				log.error("Exception while rescheduling jobs" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public String scheduleJob(ScheduleJob scheduleJob) throws IPSException {
		ScheduleResponse scheduleResponse = null;
		try {
			ScheduleRequest scheduleRequest = populateScheduleRequest(scheduleJob);
			Timeout timeOut = new Timeout(Duration.create(IPSConfig.ACTOR_TIMEOUT, "seconds"));
			Future<Object> future = Patterns.ask(ipsActorSystem.ipsSchedulerManager, scheduleRequest, timeOut);

			scheduleResponse = (ScheduleResponse) Await.result(future, timeOut.duration());
		} catch (SISException e) {
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(AuthConstants.DSS_SERVER_ERR_CODE),
					AuthConstants.DSS_SERVER_ERR_MSG);
		} catch (TimeoutException e) {
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(IPSConstants.SCHEDULE_TIMEDOUT_ERR),
					IPSConstants.SCHEDULE_TIMEDOUT_ERR);
		} catch (Exception e) {
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(IPSConstants.SCHEDULE_ERR),
					IPSConstants.SCHEDULE_ERR);
		}
		if (scheduleResponse.getResult().equals(SchedulerConstants.SUCCESS)) {
			return scheduleResponse.getActorPath();
		} else {
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(IPSConstants.SCHEDULE_ERR),
					IPSConstants.SCHEDULE_ERR);
		}
	}

	public String cancelJob(String jobId, String actorPath) throws IPSException {
		String result;
		try {
			CancelRequest cancelRequest = new CancelRequest();
			cancelRequest.setJobId(jobId);
			cancelRequest.setActorPath(actorPath);
			Timeout timeOut = new Timeout(Duration.create(IPSConfig.ACTOR_TIMEOUT, "seconds"));
			Future<Object> future = Patterns.ask(ipsActorSystem.ipsSchedulerManager, cancelRequest, timeOut);

			result = (String) Await.result(future, timeOut.duration());
			
		} catch (TimeoutException e) {
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(IPSConstants.SCHEDULE_TIMEDOUT_ERR),
					IPSConstants.SCHEDULE_TIMEDOUT_ERR);
		} catch (Exception e) {
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(IPSConstants.SCHEDULE_ERR),
					IPSConstants.SCHEDULE_ERR);
		}
		if (result.equals(SchedulerConstants.SUCCESS)) {
			return result;
		} else {
			throw new IPSException(result, IPSConstants.SCHEDULE_ERR); // TODO
																		// errorMsg
		}
	}

	private ScheduleJob checkEligibilityAndSchedule(ScheduleJob scheduleJob) throws IPSException {

		String actorPath = null;
		try {

			scheduleJob.setPreviousStatus(scheduleJob.getStatus());

			if (scheduleJob.getStartDate().after(new Date())) {
				actorPath = scheduleJob(scheduleJob);
				scheduleJob.setStatus(SchedulerConstants.SCHEDULED);
			} else {
				switch (scheduleJob.getJobType()) {
				case SchedulerConstants.ONETIME:
					scheduleJob.setStatus(SchedulerConstants.TIMEDOUT);
					break;
				case SchedulerConstants.REPEAT:
					if ((scheduleJob.getRepeatitionCount() - scheduleJob.getExecutionCount()) >= 0) {
						scheduleJob.setStartDate(DateTimeUtils
								.getDateFromStr(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z").format(new Date())));
						actorPath = scheduleJob(scheduleJob);
						scheduleJob.setStatus(SchedulerConstants.RUNNING);
					} else {
						scheduleJob.setStatus(SchedulerConstants.COMPLETED);
					}
					break;
				case SchedulerConstants.CRON:
					if (scheduleJob.getEndDate().after(new Date())) {
						if (scheduleJob.getStartDate().before(new Date())) {
							scheduleJob.setStartDate(DateTimeUtils
									.getDateFromStr(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z").format(new Date())));
							actorPath = scheduleJob(scheduleJob);
							scheduleJob.setStatus(SchedulerConstants.RUNNING);
						}
					} else {
						scheduleJob.setStatus(SchedulerConstants.TIMEDOUT);
					}

					break;
				default:
					log.error("Invalid Schedule type for resuming job" + scheduleJob.getJobType());

				}
				scheduleJob.setActorPath(actorPath);
			}

		} catch (SISException e) {
			e.printStackTrace();
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(AuthConstants.DSS_SERVER_ERR_CODE),
					AuthConstants.DSS_SERVER_ERR_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IPSException(IPSConfig.errorMsgProperties.getProperty(IPSConstants.SCHEDULE_ERR),
					IPSConstants.SCHEDULE_ERR);
		}
		return scheduleJob;

	}

	private ScheduleRequest populateScheduleRequest(ScheduleJob scheduleJob) throws SISException {
		ScheduleRequest scheduleRequest = new ScheduleRequest();
		try {
			Applications appication = ApplicationCAO.getApplicationData(scheduleJob.getAppId());
			scheduleRequest.setJobId(scheduleJob.getJobId());
			scheduleRequest.setJobType(scheduleJob.getJobType());
			scheduleRequest.setAppId(scheduleJob.getAppId());
			scheduleRequest.setObeId(scheduleJob.getObeId());
			scheduleRequest.setApplication(appication);
			scheduleRequest.setStartDate(scheduleJob.getStartDate());
			if (scheduleJob.getEndDate() == null && scheduleJob.getJobType().equals(SchedulerConstants.CRON)) {
				Date endDate = getAppExpiryDate(appication);
				if (endDate != null)
					scheduleJob.setEndDate(endDate);
			}
			scheduleRequest.setEndDate(scheduleJob.getEndDate());
			scheduleRequest.setCronPattern(scheduleJob.getCronPattern());
			scheduleRequest.setFrequencyInterval(scheduleJob.getFrequencyInterval());
			scheduleRequest.setRepeatitionCount(scheduleJob.getRepeatitionCount());
			scheduleRequest.setRetryCount(scheduleJob.getRetryCount());
			scheduleRequest.setTaskDataList(scheduleJob.getTaskDataList());
		} catch (Exception e) {
			e.printStackTrace();
			throw new SISException();
		}
		return scheduleRequest;
	}

	public Date getAppExpiryDate(Applications app) throws Exception {
		String endDate = null;
		if (app.getValidity() != null) {
			long appEndTime = app.getValidity().getEndTime();
			endDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z").format(new Date(appEndTime));
			log.info("App Expiry Date  " + endDate);
		}
		return DateTimeUtils.getDateFromStr(endDate);
	}

	private void saveJobDetails(ScheduleJob job, String operation) throws SISException {

		if (operation.equals(SchedulerConstants.DELETE)) {
			SchedulerCAO.deleteJobFromDss(job.getJobId());
			SchedulerCAO.deleteBotStatus(job.getJobId());
		} else {
			if (operation.equals(SchedulerConstants.COMPLETED) || operation.equals(SchedulerConstants.TIMEDOUT)) {
				job.setPreviousStatus(job.getStatus());		
				SchedulerCAO.deleteJobFromDss(job.getJobId());
			} else {
				if (operation.equals(SchedulerConstants.SCHEDULED)) {
					job.setPreviousStatus(job.getStatus());												
					job.setStatus(operation);
					job.setLastExecutionTime(null);
					job.setNextExcetuionTime(job.getStartDate());
				} else if (operation.equals(SchedulerConstants.PAUSED)) {
					job.setPreviousStatus(job.getStatus());
					job.setStatus(operation);
				}
				SchedulerCAO.saveJobToDss(job);
			}
			SchedulerCAO.saveJobStatusToDss(job);
		}

	}
}
