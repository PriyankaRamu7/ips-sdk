package com.hpe.sis.sie.fe.ips.logging.actors;

import com.hpe.sis.sie.fe.ips.interactioncontext.messages.InteractionContextMessage;
import com.hpe.sis.sie.fe.ips.interactioncontext.messages.InteractionContextResult;
import com.hpe.sis.sie.fe.ips.logging.util.InteractionContextLog;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;

import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class InteractionContextFileLoggerActor extends UntypedAbstractActor{

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof InteractionContextMessage) {
			InteractionContextMessage interactionContextMessage = (InteractionContextMessage)message;
			log.info(getSelf().path() + " Received InteractionContextMessage for Transaction: " + interactionContextMessage.getInteractionContext().getTransactionId());
			TraceContext traceContext = Tracer.start(interactionContextMessage.getInteractionContext().getTransactionId(), "InteractionContextFileLoggerActor -- CDR Logging" , "InteractionContextFileLoggerActor.onReceive", getSelf().path().toString());
			InteractionContextLog.logInteractionContext(interactionContextMessage.getInteractionContext());
			Tracer.end(traceContext, "InteractionContextFileLoggerActor -- CDR Logging", TraceConstants.SUCCESS);
		} else if (message instanceof InteractionContextResult) {
			InteractionContextResult interactionContextResult = (InteractionContextResult) message;
			log.info(getSelf().path() + " Received InteractionContextResult for Transaction: " + interactionContextResult.getInteractionRequest().getTransactionId());
			TraceContext traceContext = Tracer.start(interactionContextResult.getInteractionRequest().getTransactionId(), "InteractionContextFileLoggerActor -- CDR Logging" , "InteractionContextFileLoggerActor.onReceive", getSelf().path().toString());
			InteractionContextLog.logInteractionContext(interactionContextResult);
			Tracer.end(traceContext, "InteractionContextFileLoggerActor -- CDR Logging", TraceConstants.SUCCESS);
		}
		
	}
}
