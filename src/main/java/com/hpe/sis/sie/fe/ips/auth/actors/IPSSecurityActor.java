package com.hpe.sis.sie.fe.ips.auth.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.BalancingPool;
import akka.routing.FromConfig;
import akka.routing.RoundRobinPool;

import com.hpe.sis.sie.fe.ips.auth.constants.AuthConstants;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthErrorResponse;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthRequest;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthSuccessResponse;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthenticationResponse;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthorizationResponse;
import com.hpe.sis.sie.fe.ips.auth.util.AuthModule;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.logging.actors.SecurityAuditLoggerActor;
import com.hpe.sis.sie.fe.ips.tracer.TraceConstants;
import com.hpe.sis.sie.fe.ips.tracer.TraceContext;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;
import com.typesafe.config.Config;

public class IPSSecurityActor extends UntypedAbstractActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private SisIpsActorSystem ipsActorSystem;
	private final ActorRef authenticationActor;
	private final ActorRef authorizationActor;
	
	IPSSecurityActor() {
		
		ipsActorSystem = SisIpsActorSystem.getInstance();
		Config config = ipsActorSystem.getIpsActorConfig();	
		int nr_of_instances;
		
		nr_of_instances = config.getInt("akka.actor.deployment./authentication_actor.nr-of-instances");
		authenticationActor = getContext().actorOf(Props.create(AuthenticationActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"authentication_actor"); 
		
		nr_of_instances = config.getInt("akka.actor.deployment./authorization_actor.nr-of-instances");
		authorizationActor = getContext().actorOf(Props.create(AuthorizationActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"authorization_actor");
		
		nr_of_instances = config.getInt("akka.actor.deployment./securityAuditLogging_actor.nr-of-instances");
		ipsActorSystem.securityAuditLoggingActor = getContext().actorOf(Props.create(SecurityAuditLoggerActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"securityAuditLogging_actor");
		
	}

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
			log.debug(getSelf().path() + " -- IPSSecurityActor received AuthRequest message" + authRequest.getTransactionId());
			authRequest.setNonActorSender(getSender());
			
			if(authRequest.getAuthVO().isRequestMadeFromConsole()) {
				TraceContext traceContext = Tracer.start(authRequest.getTransactionId(), "Authorization checks for Request made from test-console", "IPSSecurityActor.onReceive", getSelf().path().toString());
				AuthErrorResponse authErrorResponse = new AuthErrorResponse();
				String errorCode = null;
				errorCode = AuthModule.isDeploymentIdSame(authRequest.getApplicationVO().getDeployId(), authRequest.getAuthVO().getChannelDeploymentId());
				if (errorCode == null) {
					errorCode = AuthModule.isApplicationSuspended(authRequest.getApplicationVO().isSuspended());
				} else {		
					authErrorResponse.setErrorCode(errorCode);
					authRequest.getNonActorSender().tell(authErrorResponse, getSelf());
					return;
				}
				if (errorCode == null) {
					errorCode = AuthModule.checkApplicationValidity(authRequest.getApplicationVO().getValidity());
				} else {
					authErrorResponse.setErrorCode(errorCode);
					authRequest.getNonActorSender().tell(authErrorResponse, getSelf());
					return;
				}
				if(errorCode == null){
					AuthSuccessResponse authSuccessResponse = new AuthSuccessResponse();
					authSuccessResponse.setResponse(AuthConstants.AUTH_SUCCESS);
					Tracer.end(traceContext, "Authorization checks for Request made from test-console", TraceConstants.SUCCESS);
					authRequest.getNonActorSender().tell(authSuccessResponse, getSelf());
				} else {
					traceContext.setErrorDetail(IPSConfig.errorMsgProperties.getProperty(errorCode));
					Tracer.end(traceContext, "Authorization checks for Request made from test-console", TraceConstants.FAILED);
					authErrorResponse.setErrorCode(errorCode);
					authRequest.getNonActorSender().tell(authErrorResponse, getSelf());
				}
			} else {
				authenticationActor.tell(authRequest, getSelf());
			}
			
		} else if (message instanceof AuthenticationResponse) {
			AuthenticationResponse authenticationResponse = (AuthenticationResponse) message;
			AuthRequest authRequest = authenticationResponse.getAuthRequest();
			log.debug(getSelf().path() + " -- IPSSecurityActor received AuthenticationResponse message for transaction: " + authenticationResponse.getTransactionId());
			if (authenticationResponse.getResponse().equals(AuthConstants.AUTH_SUCCESS)) {
				log.debug(getSelf().path() + " -- Authentication is Successful. Going to perform Authorization check "
						+ authenticationResponse.getResponse());
				authorizationActor.tell(authRequest, getSelf());
			} else {
				log.debug(getSelf().path() + " -- Authentication is Failed");
				AuthErrorResponse authErrorResponse = new AuthErrorResponse();
				authErrorResponse.setErrorCode(authenticationResponse.getResponse());
				authRequest.getNonActorSender().tell(authErrorResponse, getSelf());
			}

		} else if (message instanceof AuthorizationResponse) {
			AuthorizationResponse authorizationResponse = (AuthorizationResponse) message;
			log.debug(getSelf().path() + " -- IPSSecurityActor received AuthorizationResponse message for transaction: " + authorizationResponse.getTransactionId());
			if (authorizationResponse.getResponse().equals(AuthConstants.AUTH_SUCCESS)) {
				log.debug(getSelf().path() + 
						"-- Authentication and Authorization is Successful. " + authorizationResponse.getResponse());
				AuthSuccessResponse authSuccessResponse = new AuthSuccessResponse();
				authSuccessResponse.setResponse(authorizationResponse.getResponse());
				authorizationResponse.getNonActorSender().tell(authSuccessResponse, getSelf());
			} else {
				log.debug(getSelf().path() + " -- Authorization is Failed for transaction," + authorizationResponse.getTransactionId());
				AuthErrorResponse authErrorResponse = new AuthErrorResponse();
				authErrorResponse.setErrorCode(authorizationResponse.getResponse());
				authorizationResponse.getNonActorSender().tell(authErrorResponse, getSelf());
			}

		}
		else {
			unhandled(message);
			log.debug(getSelf().path() + " -- IPSSecurityActor received unknown message for Authentication & Authorization" + message);
		}
	}

	/*@Override
	public void postStop() throws Exception {
		// TODO Auto-generated method stub
		super.postStop();
		System.out.println(getSelf().path() + " postStop called");
	}*/
}
