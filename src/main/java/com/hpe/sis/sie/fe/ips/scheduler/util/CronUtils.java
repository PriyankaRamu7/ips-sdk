package com.hpe.sis.sie.fe.ips.scheduler.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

public class CronUtils {
	// for 6.1.0 CronUtils lib
	/*public static List<ZonedDateTime> getExceutionTimeList(Date startDate, String cornPattern) {

		List<ZonedDateTime> executionTimeList = new ArrayList<>();
		CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
		CronParser parser = new CronParser(cronDefinition);
		Instant instant = DateTimeUtils.toInstant(startDate);
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(cornPattern));
		ZonedDateTime firstExecution = executionTime.nextExecution(zonedDateTime).get();
		ZonedDateTime secondExecution = executionTime.nextExecution(firstExecution).get();
		executionTimeList.add(firstExecution);
		executionTimeList.add(secondExecution);
		return executionTimeList;
	}*/
	
	public static List<DateTime> getExceutionTimeList(Date startDate, String cronPattern) {

		List<DateTime> executionTimeList = new ArrayList<>();
		CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
		CronParser parser = new CronParser(cronDefinition);
		DateTime startTime = new DateTime(startDate);
		// DateTime startTime = DateTime.now();
		ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(cronPattern));
		// org.joda.time.Duration initialDelay =
		// executionTime.timeToNextExecution(startTime);//can build akka
		// Duration instance for delay

		DateTime firstExecution = executionTime.nextExecution(startTime);
		DateTime secondExecution = executionTime.nextExecution(firstExecution);
		executionTimeList.add(firstExecution);
		executionTimeList.add(secondExecution);
		return executionTimeList;
	}
}
