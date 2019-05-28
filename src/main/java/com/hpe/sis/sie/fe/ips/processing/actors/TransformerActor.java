package com.hpe.sis.sie.fe.ips.processing.actors;

import com.hpe.sis.sie.fe.ips.processing.messages.TransformationRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.TransformationResponse;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;
import com.hpe.sis.sie.fe.ips.transmap.exception.TransformationException;
import com.hpe.sis.sie.fe.ips.transmap.service.TransformationService;
import com.hpe.sis.sie.fe.ips.transmap.service.TransformationServiceImpl;

import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class TransformerActor extends UntypedAbstractActor {
	
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private static TransformationService transformService = new TransformationServiceImpl();

	@Override
	public void onReceive(Object message) throws Throwable {

		if (message instanceof TransformationRequest) {
			TransformationRequest transformationRequest = (TransformationRequest) message;
			TraceContext traceContext = Tracer.start(transformationRequest.getInteractionRequest().getTransactionId(), "TransformerActor - Generating CLASP TASK URL", "TransformerActor.onReceive", getSelf().path().toString());
			log.info(getSelf().path() + " Received TransformationRequest for Transaction: "
					+ transformationRequest.getInteractionRequest().getTransactionId());
			try {
				String serviceURL = transformService.transform(transformationRequest);
				
				if (serviceURL != null) {
					TransformationResponse transformationResponse = new TransformationResponse();
					transformationResponse.setServiceURL(serviceURL);
					transformationResponse.setInteractionRequest(transformationRequest.getInteractionRequest());
					Tracer.end(traceContext, " TransformerActor - Generated CLASP TASK URL", TraceConstants.SUCCESS);
					getSender().tell(transformationResponse, getSelf());
				} else {
					Tracer.end(traceContext, " TransformerActor - Could not generate CLASP TASK URL", TraceConstants.FAILED);
					log.error("Error from TransformerActor. Transformed serviceURL cannot be null.");
					// TODO
				}
			} catch (TransformationException e) {
				log.error("Unable to generate TASK URL for: " + transformationRequest.getInteractionRequest().toString(), e);
				// TODO
				// Retry logic
			}
		} else {
			unhandled(message);
			log.info("TransformerActor received unknown message for url Transformation" + message);
		}

	}

}
