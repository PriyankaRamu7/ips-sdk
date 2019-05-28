package com.hpe.sis.sie.fe.ips.auth.actors;

import com.hpe.sis.sie.fe.ips.auth.constants.AuthConstants;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthRequest;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthorizationResponse;
import com.hpe.sis.sie.fe.ips.auth.util.AuthModule;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;

import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class AuthorizationActor extends UntypedAbstractActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	/*@Override
	public void preStart() throws Exception {
		// TODO Auto-generated method stub
		super.preStart();
		System.out.println(getSelf().path() + " preStart called");
	}

	@Override
	public void preRestart(Throwable reason, Optional<Object> message) throws Exception {
		// TODO Auto-generated method stub
		super.preRestart(reason, message);
		System.out.println(getSelf().path() + " preRestart called");
	}

	@Override
	public void postRestart(Throwable reason) throws Exception {
		// TODO Auto-generated method stub
		super.postRestart(reason);
		System.out.println(getSelf().path() + " postRestart called");
	}*/

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof AuthRequest) {
			AuthRequest authRequest = (AuthRequest) message;
			TraceContext traceContext = Tracer.start(authRequest.getTransactionId(), "Authorization checks", "AuthorizationActor.onReceive", getSelf().path().toString());
			log.debug(getSelf().path() + " -- AuthorizationActor received request for transactionId: " + authRequest.getTransactionId() );
			
			String errorCode = null;
			AuthorizationResponse authorizationResponse = new AuthorizationResponse();
			authorizationResponse.setTransactionId(authRequest.getTransactionId());
			authorizationResponse.setNonActorSender(authRequest.getNonActorSender());
			errorCode = AuthModule.authorizationCheck(authRequest);
			if(errorCode == null) {
				authorizationResponse.setResponse(AuthConstants.AUTH_SUCCESS);
				Tracer.end(traceContext, "Authentication checks", TraceConstants.SUCCESS);
			}
			else {
				authorizationResponse.setResponse(errorCode);
				traceContext.setErrorDetail(errorCode + "-" + IPSConfig.errorMsgProperties.getProperty(errorCode));
				Tracer.end(traceContext, "Authentication checks", TraceConstants.FAILED);
			}
			getSender().tell(authorizationResponse, getSelf());
		} else {
			unhandled(message);
			log.debug(getSelf().path() + " -- AuthorizationRequest received unknown message for Authentication" + message);
		}
	}

	/*@Override
	public void postStop() throws Exception {
		// TODO Auto-generated method stub
		super.postStop();
		System.out.println(getSelf().path() + " postStop called");
	}*/

}
