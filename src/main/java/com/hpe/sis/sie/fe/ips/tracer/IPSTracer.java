/**
 * 
 */
package com.hpe.sis.sie.fe.ips.tracer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;

import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.logging.actors.TraceLoggerActor;

/**
 * @author krisrivi
 *
 */
public class IPSTracer implements Tracer {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TraceLoggerActor.class);
	
	public static String traceLogTimeStampFormat;
	public static String timeZone;
	public static String timeStampFormat;
	
	public static void initializeTracer() {
		String[] ts = StringUtils.split(IPSConfig.TRACE_TIMESTAMP_FORMAT, ',');
		if (ts != null && ts.length == 2) {
			timeStampFormat = ts[0];
			timeZone = ts[1];
		} else {
			timeStampFormat = "yyyy-MM-dd HH:mm:ss.SSS";
			timeZone = "UTC";
		}
	}
	
	public static TraceContext start(String transactionId, String event, String classAndMethod) {
		TraceContext traceContext = createTraceContext(transactionId, event,
				classAndMethod);
		if (IPSConfig.TRACING_ENABLED == true) {
			LOGGER.info("Calling traceLogging actor for: " + traceContext.toString());
			TraceMessage traceMessage = populateTraceMessage(traceContext, "Starting " + traceContext.getEvent());
			SisIpsActorSystem.getInstance().traceLoggingActor.tell(traceMessage, ActorRef.noSender());
		}
		return traceContext;
	}

	private static TraceContext createTraceContext(String transactionId,
			String event, String classAndMethod) {
		TraceContext traceContext = new TraceContext(transactionId, event, classAndMethod);
		SimpleDateFormat sdf = new SimpleDateFormat(timeStampFormat);
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		String timeStamp = sdf.format(new Date());
		traceContext.setTimeStamp(timeStamp);
		return traceContext;
	}
	
	public static TraceContext start(String transactionId, String event, String classAndMethod, String actorPath) {
		TraceContext traceContext = createTraceContext(transactionId, event, classAndMethod);
		traceContext.setActorPath(actorPath);
		
		if (IPSConfig.TRACING_ENABLED == true) {
			LOGGER.info("Calling traceLogging actor for: " + traceContext.toString());
			TraceMessage traceMessage = populateTraceMessage(traceContext, "Starting " + traceContext.getEvent());
			SisIpsActorSystem.getInstance().traceLoggingActor.tell(traceMessage, ActorRef.noSender());
		}
		return traceContext;
	}
	
	public static TraceContext start(String transactionId, String event, String classAndMethod, String actorPath, Map<String, String> dataMap) {
		TraceContext traceContext = createTraceContext(transactionId, event, classAndMethod);
		traceContext.setActorPath(actorPath);
		traceContext.setFields(dataMap);
		if (IPSConfig.TRACING_ENABLED == true) {
			LOGGER.info("Calling traceLogging actor for: " + traceContext.toString());
			TraceMessage traceMessage = populateTraceMessage(traceContext, "Starting " + traceContext.getEvent());
			SisIpsActorSystem.getInstance().traceLoggingActor.tell(traceMessage, ActorRef.noSender());
		}
		return traceContext;
	}
	
	private static TraceMessage populateTraceMessage(TraceContext traceContext, String event) {
		TraceMessage traceMessage = new TraceMessage();
		traceMessage.setTransactionId(traceContext.getTransactionId());
		traceMessage.setEvent(event);
		traceMessage.setFields(traceContext.getFields());
		traceMessage.setStatus(traceContext.getStatus());
		traceMessage.setClassAndMethod(traceContext.getClassAndMethod());
		traceMessage.setActorPath(traceContext.getActorPath());
		traceMessage.setErrorDetail(traceContext.getErrorDetail());
		traceMessage.setTimeStamp(traceContext.getTimeStamp());
		return traceMessage;
	}

	public static void end(TraceContext traceContext, String event, String status) {
		traceContext.setEvent(event);
		traceContext.setStatus(status);
		if (IPSConfig.TRACING_ENABLED == true) {
			SimpleDateFormat sdf = new SimpleDateFormat(timeStampFormat);
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			String timeStamp = sdf.format(new Date());
			traceContext.setTimeStamp(timeStamp);
			LOGGER.info("Calling traceLogging actor for: " + traceContext.toString());
			TraceMessage traceMessage = populateTraceMessage(traceContext, "Ending " + traceContext.getEvent());
			SisIpsActorSystem.getInstance().traceLoggingActor.tell(traceMessage, ActorRef.noSender());
		}
			
	}
}
