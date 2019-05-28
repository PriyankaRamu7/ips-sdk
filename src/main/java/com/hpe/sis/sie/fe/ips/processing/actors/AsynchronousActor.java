package com.hpe.sis.sie.fe.ips.processing.actors;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.typesafe.config.Config;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.routing.BalancingPool;

public class AsynchronousActor extends UntypedAbstractActor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousActor.class);
	
	private final ActorRef pipelineActor;
	
	public AsynchronousActor() {
		int nr_of_instances;
		Config config = SisIpsActorSystem.getInstance().getIpsActorConfig();
		nr_of_instances = config.getInt("akka.actor.deployment./pipeline_actor.nr-of-instances");
		
		pipelineActor = getContext().actorOf(Props.create(PipelineActor.class).withRouter(new BalancingPool(nr_of_instances)),"pipeline_actor");
		
	}

	@Override
	public void onReceive(Object arg0) throws Throwable {
		LOGGER.debug(getSelf().path() + " Entry");
		
		if (arg0 instanceof InteractionRequest) {
			InteractionRequest interactionRequest = (InteractionRequest) arg0; 
			LOGGER.debug(getSelf().path() +" Received InteractionRequest for Transaction: " + interactionRequest.getTransactionId() );
			pipelineActor.tell(interactionRequest , getSelf());
			
		} 
		LOGGER.debug(getSelf().path() + " Exit");
	}
	

}
