package com.hpe.sis.sie.fe.ips.processing.actors;

import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.interactioncontext.messages.InteractionContextResult;
import com.hpe.sis.sie.fe.ips.interactioncontext.model.Actor;
import com.hpe.sis.sie.fe.ips.processing.messages.BackendResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.CachedResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.NoCachedResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.ResponseCacheRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.ServiceInvokeRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.ServiceInvokeResponse;
import com.hpe.sis.sie.fe.ips.processing.messages.TransformationRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.TransformationResponse;
import com.hpe.sis.sie.fe.ips.processing.utils.ProcessingPattern;
import com.hpe.sis.sie.fe.ips.processing.utils.TransactionUtil;
import com.hpe.sis.sie.fe.ips.scheduler.messages.BotMessage;
import com.hpe.sis.sie.fe.ips.scheduler.messages.BotResponse;
import com.hpe.sis.sie.fe.ips.transmap.util.TransformationUtil;
import com.typesafe.config.Config;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.BalancingPool;
import akka.routing.RoundRobinPool;

public class PipelineActor extends UntypedAbstractActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final ActorRef transformerActor;
	private final ActorRef serviceInvokerActor;
	private final ActorRef responseCachingActor;
	private SisIpsActorSystem ipsActorSystem = SisIpsActorSystem.getInstance();

	public PipelineActor() {
		// System.out.println("Initializing PipelineActor child actors");
		Config config = ipsActorSystem.getIpsActorConfig();
		int nr_of_instances;
		
		nr_of_instances = config.getInt("akka.actor.deployment./transformer_actor.nr-of-instances");
		transformerActor = getContext().actorOf(
				Props.create(TransformerActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"transformer_actor");
		
		nr_of_instances = config.getInt("akka.actor.deployment./serviceInvoker_actor.nr-of-instances");
		serviceInvokerActor = getContext().actorOf(
				Props.create(ServiceInvokerActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"serviceInvoker_actor");
		
		nr_of_instances = config.getInt("akka.actor.deployment./responseCache_actor.nr-of-instances");
		responseCachingActor = getContext().actorOf(
				Props.create(ResponseCacheActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"responseCache_actor");
	}

	@Override
	public void onReceive(Object message) throws Throwable {

		if (message instanceof InteractionRequest) {
			log.debug(getSelf().path() + " -- Received InteractionRequest, invoking response cache actor");
			InteractionRequest interactionRequest = (InteractionRequest) message;
			System.out.println(getSelf().path() + " Received InteractionRequest for Transaction: "
					+ interactionRequest.getTransactionId());

			ResponseCacheRequest responseCacheRequest = new ResponseCacheRequest();
			responseCacheRequest.setInteractionRequest(interactionRequest);
			responseCachingActor.tell(responseCacheRequest, getSelf());

		} else if (message instanceof NoCachedResponse) {
			log.debug(getSelf().path() + " -- Received NoCachedResponse, invoking Transformer actor");
			NoCachedResponse noCachedResponse = (NoCachedResponse) message;
			InteractionRequest interactionRequest = noCachedResponse.getInteractionRequest();
			
			TransformationRequest transformationRequest = new TransformationRequest();
			transformationRequest.setInteractionRequest(interactionRequest);

			transformerActor.tell(transformationRequest, getSelf());
			
		} else if (message instanceof CachedResponse) {
			log.debug(getSelf().path() + " -- Received CachedResponse, returning back to Sync actor");
			CachedResponse cachedResponse = (CachedResponse) message;
			InteractionResponse interactionResponse = new InteractionResponse();
			interactionResponse.setBackEndResponse(cachedResponse.getBackendResponse());
			interactionResponse.setInteractionContextId(cachedResponse.getInteractionRequest().getInteractionContextId());
			interactionResponse.setTransactionId(cachedResponse.getInteractionRequest().getTransactionId());
			interactionResponse.setNonActorSender(cachedResponse.getInteractionRequest().getNonActorSender());
			
			if (cachedResponse.getInteractionRequest().getProcessingPattern().equals(ProcessingPattern.Synchronous.toString())) {
				log.debug(getSelf().path() + " -- Received CachedResponse, returning back to Sync actor");
				cachedResponse.getInteractionRequest().getSyncActorRef().tell(interactionResponse, getSelf());
			}
			else {
				log.error("Response caching should be enabled only for Synchronous pattern, transactionId:", interactionResponse.getTransactionId());
			}
			
		} else if (message instanceof TransformationResponse) {
			log.debug(getSelf().path() + " -- Received TransformationResponse, invoking ServiceInvoker actor");
			TransformationResponse transformationResponse = (TransformationResponse) message;
			System.out.println(getSelf().path() + " Received TransformationResponse for Transaction: "
					+ transformationResponse.getInteractionRequest().getTransactionId());

			ServiceInvokeRequest serviceInvokeRequest = new ServiceInvokeRequest();
			serviceInvokeRequest.setServiceURL(transformationResponse.getServiceURL());
			serviceInvokeRequest.setInteractionRequest(transformationResponse.getInteractionRequest());

			serviceInvokerActor.tell(serviceInvokeRequest, getSelf());

		} else if (message instanceof ServiceInvokeResponse) {
			log.debug(getSelf().path() + " -- Received ServiceInvokerResponse, returning back to Sync actor");
			ServiceInvokeResponse serviceInvokeResponse = (ServiceInvokeResponse) message;
			
			InteractionRequest interactionRequest = serviceInvokeResponse.getInteractionRequest();		
			System.out.println(getSelf().path() + " Received ServiceInvokeResponse for Transaction: " + interactionRequest.getTransactionId());
			
			InteractionResponse interactionResponse = new InteractionResponse();
			interactionResponse.setBackEndResponse(serviceInvokeResponse.getBackEndResponse());
			interactionResponse.setInteractionContextId(interactionRequest.getInteractionContextId());
			interactionResponse.setTransactionId(interactionRequest.getTransactionId());
			interactionResponse.setNonActorSender(interactionRequest.getNonActorSender());

			if (interactionRequest.getProcessingPattern().equals(ProcessingPattern.Synchronous.toString())) {
				interactionRequest.getSyncActorRef().tell(interactionResponse, getSelf());
			}
			else {
				if(IPSConfig.TRANSACTION_LOG_ENABLED) {
					ipsActorSystem.transactionLoggingActor.tell(TransactionUtil.getTransaction(serviceInvokeResponse), getSelf());
				}
			}
			if(IPSConfig.SAVE_ACTIVITYSTREAM_BYTIME) {
				InteractionContextResult interactionContextResult = new InteractionContextResult();
				interactionContextResult.setActorPath(getSelf().path());
				interactionContextResult.setInteractionRequest(interactionRequest);
				interactionContextResult.setInteractionResponse(interactionResponse);
				ipsActorSystem.interactionContextDSSLoggerActor.tell(interactionContextResult, getSelf());
				ipsActorSystem.interactionContextFileLoggerActor.tell(interactionContextResult, getSelf());
			}
		} else if (message instanceof BotMessage) {
			log.debug(getSelf().path() + " -- Received BotMessage, invoking ServiceInvoker actor");
			BotMessage botMessage = (BotMessage) message;
			
			botMessage.setServiceURL(TransformationUtil.fetchBotServiceURL(botMessage.getBotTaskHeader(), botMessage.getBotTaskData()));
			serviceInvokerActor.tell(botMessage, getSelf());
			
		} else if (message instanceof BotResponse) {
			log.debug(getSelf().path() + " -- Received BotResponse, invoking transactionLogging actor");
			BotResponse botResponse = (BotResponse) message;
			
			if(IPSConfig.TRANSACTION_LOG_ENABLED) {
				ipsActorSystem.transactionLoggingActor.tell(TransactionUtil.getTransaction(botResponse), getSelf());
			}
		}
		log.debug(getSelf().path() + " Exit");

	}

}
