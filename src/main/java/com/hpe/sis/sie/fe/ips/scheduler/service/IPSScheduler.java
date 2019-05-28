package com.hpe.sis.sie.fe.ips.scheduler.service;

import java.util.List;

import com.hpe.sis.sie.fe.ips.auth.exception.IPSException;
import com.hpe.sis.sie.fe.ips.scheduler.model.ScheduleJob;

public interface IPSScheduler {

	public String schedule(ScheduleJob scheduleJob) throws IPSException;
	public String pause(String jobId) throws IPSException;
	public String resume(String jobId) throws IPSException;
	public String delete(String jobId) throws IPSException;
	public void reschedule(List<ScheduleJob> jobList);
	
	public static IPSScheduler build() {
		return new IPSSchedulerImpl();
	}
}
