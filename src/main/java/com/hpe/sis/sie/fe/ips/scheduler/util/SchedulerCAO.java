package com.hpe.sis.sie.fe.ips.scheduler.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.sis.sie.fe.dss.DSSService;
import com.hpe.sis.sie.fe.dss.exception.SISException;
import com.hpe.sis.sie.fe.ips.scheduler.constants.SchedulerConstants;
import com.hpe.sis.sie.fe.ips.scheduler.messages.ScheduleRequest;
import com.hpe.sis.sie.fe.ips.scheduler.model.ScheduleJob;

public class SchedulerCAO {

	public static void saveJobToDss(ScheduleJob job) throws SISException {
		ObjectMapper mapper = new ObjectMapper();

		String key = "bot-scheduler:jobs:" + job.getJobId();

		try {
			String jsonString = mapper.writeValueAsString(job);
			DSSService.setValue(key, jsonString);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (SISException e) {
			e.printStackTrace();
			throw new SISException("Exception occured while saving schedule job in dss");
		}

	}

	public static void saveJobStatusToDss(ScheduleJob job) throws SISException {

		String key = "bot:" + job.getJobId();

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("applicationId", job.getAppId());
		jsonObj.put("actorPath", job.getActorPath());
		if (job.getLastExecutionTime() == null) {
			jsonObj.put("previousFireTime", SchedulerConstants.EMPTY);
		} else {
			jsonObj.put("previousFireTime", job.getLastExecutionTime().toString());
		}
		jsonObj.put("nextFireTime", job.getNextExcetuionTime().toString());
		jsonObj.put("previousStatus", job.getPreviousStatus());
		jsonObj.put("executionCount", job.getExecutionCount());
		jsonObj.put("status", job.getStatus());
		try {
			DSSService.setValue(key, jsonObj.toJSONString());
		} catch (SISException e) {
			e.printStackTrace();
			throw new SISException("Exception occured while inserting bot status in dss");
		}
	}

	public static ScheduleJob getJobFromDss(String jobId) throws SISException {

		ObjectMapper mapper = new ObjectMapper();

		String key = "bot-scheduler:jobs:" + jobId;
		ScheduleJob job = null;

		try {

			String jobString = DSSService.getValue(key);
			job = mapper.readValue(jobString, ScheduleJob.class);

		} catch (SISException | IOException e) {
			e.printStackTrace();
			throw new SISException("Exception occured while reading bot from dss");
		} 
		return job;

	}
	
	public static boolean isCompletedJob(String jobId) throws SISException{
		String key = "bot-scheduler:jobs:" + jobId;
		long keyNum = DSSService.exists(key);
		if(keyNum > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public static void deleteBotStatus(String jobId) throws SISException {
		String key = "bot:" + jobId;
		try {
			DSSService.deleteKey(key);
		} catch (SISException e) {
			e.printStackTrace();
			throw new SISException("Exception occured while deleting bot status in dss");
		}
	}
	
	public static void deleteJobFromDss(String jobId) throws SISException {

		String key = "bot-scheduler:jobs:" + jobId;

		try {
			DSSService.deleteKey(key);
		} catch (SISException e) {
			e.printStackTrace();
			throw new SISException("Exception occured while deleting bot from dss");
		}

	}
	
	public static List<ScheduleJob> getJobForReschedule() throws SISException {

		ObjectMapper mapper = new ObjectMapper();

		String pattern = "bot-scheduler:jobs*";

		ScheduleJob job = null;
		List<ScheduleJob> jobsList = new ArrayList<>();
		try {
			try {
				List<String> jobKeys = DSSService.keys(pattern);
				for (String key : jobKeys) {
					String jobString = DSSService.getValue(key);
					job = mapper.readValue(jobString, ScheduleJob.class);
					if(!job.getStatus().equals(SchedulerConstants.COMPLETED))
						jobsList.add(job);
				}

			} catch (IOException e) {
				e.printStackTrace();
				throw new SISException("Exception occured while reading bots for rescheduling from dss");
			}
		} catch (SISException e) {
			e.printStackTrace();
			throw new SISException("Exception occured while reading bots for rescheduling from dss");
		}
		return jobsList;

	}
	
	public static List<ScheduleJob> getJobForReschedule(String actorPath) throws SISException {

		ObjectMapper mapper = new ObjectMapper();

		String pattern = "bot-scheduler:jobs*";

		ScheduleJob job = null;
		List<ScheduleJob> jobsList = new ArrayList<>();
		try {
			try {
				List<String> jobKeys = DSSService.keys(pattern);
				for (String key : jobKeys) {
					String jobString = DSSService.getValue(key);
					job = mapper.readValue(jobString, ScheduleJob.class);
					if (job.getActorPath().equals(actorPath) && !job.getStatus().equals(SchedulerConstants.COMPLETED))
						jobsList.add(job);
				}

			} catch (IOException e) {
				e.printStackTrace();
				throw new SISException("Exception occured while reading bot from dss");
			}
		} catch (SISException e) {
			e.printStackTrace();
			throw new SISException("Exception occured while deleting bot status in dss");
		}
		return jobsList;

	}
	
	public static void updateJobStatusToDss(ScheduleRequest scheduleRequest) throws SISException {

		String key = "bot:" + scheduleRequest.getJobId();

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("applicationId", scheduleRequest.getAppId());
		jsonObj.put("actorPath", scheduleRequest.getActorPath());
		if (scheduleRequest.getLastExecutionTime() == null) {
			jsonObj.put("previousFireTime", SchedulerConstants.EMPTY);
		} else {
			jsonObj.put("previousFireTime", scheduleRequest.getLastExecutionTime().toString());
		}
		if (scheduleRequest.getNextExcetuionTime() == null) {
			jsonObj.put("nextFireTime", SchedulerConstants.EMPTY);
		} else {
		jsonObj.put("nextFireTime", scheduleRequest.getNextExcetuionTime().toString());
		}
		jsonObj.put("previousStatus", scheduleRequest.getPreviousStatus());
		jsonObj.put("executionCount", scheduleRequest.getExecutionCount());
		jsonObj.put("status", scheduleRequest.getStatus());

		try {
			DSSService.setValue(key, jsonObj.toJSONString());
		} catch (SISException e) {
			e.printStackTrace();
			throw new SISException("Exception occured while inserting bot status in dss");
		}
	}
	
	public static long getExecutionCountFromDss(String jobId) throws SISException {

		String key = "bot:" + jobId;
		long executionCount;

		try {
			try {
				String jobString = DSSService.getValue(key);
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(jobString);
				executionCount = (Long) json.get("executionCount");
			} catch (ParseException e) {
				e.printStackTrace();
				throw new SISException("Exception occured while reading actorpath from dss");
			}
		} catch (SISException e) {
			e.printStackTrace();
			throw new SISException("Exception occured while reading actorpath from dss");
		}
		return executionCount;

	}
	
	public static void updateJobToDss(ScheduleRequest scheduleRequest) {
		try {
			ScheduleJob job = getJobFromDss(scheduleRequest.getJobId());
			job.setActorPath(scheduleRequest.getActorPath());
			job.setPreviousStatus(scheduleRequest.getPreviousStatus());
			job.setStatus(scheduleRequest.getStatus());
			job.setExecutionCount(scheduleRequest.getExecutionCount());
			job.setLastExecutionTime(scheduleRequest.getLastExecutionTime());
			job.setNextExcetuionTime(scheduleRequest.getNextExcetuionTime());
			saveJobToDss(job);
		} catch (SISException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
