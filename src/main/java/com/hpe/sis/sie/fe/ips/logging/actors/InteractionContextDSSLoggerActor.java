package com.hpe.sis.sie.fe.ips.logging.actors;

import com.hpe.sis.sie.fe.ips.interactioncontext.messages.InteractionContextMessage;
import com.hpe.sis.sie.fe.ips.interactioncontext.messages.InteractionContextResult;
import com.hpe.sis.sie.fe.ips.logging.util.InteractionContextCAO;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;

import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class InteractionContextDSSLoggerActor extends UntypedAbstractActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private static InteractionContextCAO interactionContextCAO = new InteractionContextCAO();

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof InteractionContextMessage) {
			InteractionContextMessage interactionContextMessage = (InteractionContextMessage) message;
			log.info(getSelf().path() + " Received InteractionContextMessage for Transaction: " + interactionContextMessage.getInteractionContext().getTransactionId());
			TraceContext traceContext = Tracer.start(interactionContextMessage.getInteractionContext().getTransactionId(), "InteractionContextDSSLoggerActor -- DSS Logging" , "InteractionContextDSSLoggerActor.onReceive", getSelf().path().toString());
			interactionContextCAO.saveInteractionContext(interactionContextMessage.getInteractionContext(),
					interactionContextMessage.getChannelTransactionBO());
			Tracer.end(traceContext, "InteractionContextDSSLoggerActor -- DSS Logging", TraceConstants.SUCCESS);
		} else if (message instanceof InteractionContextResult) {
			InteractionContextResult interactionContextResult = (InteractionContextResult) message;
			log.info(getSelf().path() + " Received InteractionContextResult for Transaction: " + interactionContextResult.getInteractionRequest().getTransactionId());
			TraceContext traceContext = Tracer.start(interactionContextResult.getInteractionRequest().getTransactionId(), "InteractionContextDSSLoggerActor -- DSS Logging" , "InteractionContextDSSLoggerActor.onReceive", getSelf().path().toString());
			interactionContextCAO.updateInteractionContext(interactionContextResult.getInteractionRequest(),
					interactionContextResult.getInteractionResponse().getBackEndResponse(), interactionContextResult.getActorPath());
			Tracer.end(traceContext, "InteractionContextDSSLoggerActor -- DSS Logging", TraceConstants.SUCCESS);
		}

	}
}
