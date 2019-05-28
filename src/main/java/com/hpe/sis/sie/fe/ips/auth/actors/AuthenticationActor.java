package com.hpe.sis.sie.fe.ips.auth.actors;


import com.hpe.sis.sie.fe.ips.auth.constants.AuthConstants;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthRequest;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthenticationResponse;
import com.hpe.sis.sie.fe.ips.auth.util.AuthModule;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.auth.model.AuthVO;

import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class AuthenticationActor extends UntypedAbstractActor {

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
		if(message instanceof AuthRequest) {
			AuthRequest authRequest = (AuthRequest) message;
			TraceContext traceContext = Tracer.start(authRequest.getTransactionId(), "Authentication checks", "AuthenticationActor.onReceive", getSelf().path().toString());
			log.debug(getSelf().path() + " Received InteractionRequest for Transaction: " + authRequest.getTransactionId());
			AuthVO authVO = authRequest.getAuthVO();
			Applications applicationVO = authRequest.getApplicationVO();
			String errorCode = null;
			AuthenticationResponse authenticationResponse = new AuthenticationResponse();
			authenticationResponse.setAuthRequest(authRequest);
			authenticationResponse.setTransactionId(authRequest.getTransactionId());
			errorCode = AuthModule.authenticationCheck(authVO.getApikey(), applicationVO.getApiKeys(),authVO.isAPIKeyAuthRequired(), authVO.getAppId(), authRequest.getTransactionId());
			if (errorCode == null) {
				authenticationResponse.setResponse(AuthConstants.AUTH_SUCCESS);
				Tracer.end(traceContext, "Authentication checks", TraceConstants.SUCCESS);
			}
			else {
				traceContext.setErrorDetail(errorCode + "-" + IPSConfig.errorMsgProperties.getProperty(errorCode));
				Tracer.end(traceContext, "Authentication checks", TraceConstants.FAILED);
				authenticationResponse.setResponse(errorCode);
			}
			getSender().tell(authenticationResponse, getSelf());
		} else {
			unhandled(message);
			log.debug("AuthenticationActor received unknown message for Authentication" + message);
		}
		
	}
	
	/*@Override
	public void postStop() throws Exception {
		// TODO Auto-generated method stub
		super.postStop();
		System.out.println(getSelf().path() + " postStop called");
	}*/
}
