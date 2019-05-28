package com.hpe.sis.sie.fe.ips.processing.actors;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionResponse;
import com.typesafe.config.Config;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.BalancingPool;
import akka.routing.RoundRobinPool;

public class SynchronousActor extends UntypedAbstractActor {
	
	LoggingAdapter LOGGER = Logging.getLogger(getContext().getSystem(), this);
	
	private final ActorRef pipelineActor;
	
	public SynchronousActor() {
		int nr_of_instances;
		Config config = SisIpsActorSystem.getInstance().getIpsActorConfig();
		nr_of_instances = config.getInt("akka.actor.deployment./pipeline_actor.nr-of-instances");
		
		pipelineActor = getContext().actorOf(Props.create(PipelineActor.class).withRouter(new RoundRobinPool(nr_of_instances)),"pipeline_actor");
		
	}

	@Override
	public void onReceive(Object arg0) throws Throwable {
		LOGGER.debug(getSelf().path() + " Entry");
		
		if (arg0 instanceof InteractionRequest) {
			InteractionRequest interactionRequest = (InteractionRequest) arg0; 
			LOGGER.debug(getSelf().path() +" Received InteractionRequest for Transaction: " + interactionRequest.getTransactionId());
			interactionRequest.setSyncActorRef(getSelf());
			interactionRequest.setNonActorSender(getSender());
			pipelineActor.tell(interactionRequest , getSelf());
			
		} else if (arg0 instanceof InteractionResponse) {
			System.out.println(getSelf().path() + " -- Received InteractionResponse, returning back to Non-Actor");
			InteractionResponse interactionResponse = (InteractionResponse) arg0;
			LOGGER.debug(getSelf().path() +" Received InteractionResponse for Transaction: " + interactionResponse.getTransactionId() );
			LOGGER.debug("Sync actor responding to non-actor:" + interactionResponse.getNonActorSender().path());
			
			interactionResponse.getNonActorSender().tell(interactionResponse, getSelf());
			//nonActorSender.tell(interactionResponse, getSelf());
		}
	}
}
