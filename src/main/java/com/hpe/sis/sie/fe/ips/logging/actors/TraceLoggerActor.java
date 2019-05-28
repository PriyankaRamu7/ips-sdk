package com.hpe.sis.sie.fe.ips.logging.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.TraceMessage;
import com.hpe.sis.sie.fe.ips.tracer.TracingUtil;

import akka.actor.UntypedAbstractActor;

public class TraceLoggerActor extends UntypedAbstractActor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TraceLoggerActor.class);

	@Override
	public void onReceive(Object arg0) throws Throwable {
		
		if (arg0 instanceof TraceMessage) {
			TraceMessage traceMessage = (TraceMessage) arg0;
			LOGGER.info(getSelf().path() + " -- Trace logging for transaction: " + traceMessage.getTransactionId());
			TracingUtil.logTracing(traceMessage);
		}
		
	}
}
