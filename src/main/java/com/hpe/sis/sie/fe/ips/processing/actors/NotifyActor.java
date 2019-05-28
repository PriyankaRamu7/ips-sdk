package com.hpe.sis.sie.fe.ips.processing.actors;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.scheduler.messages.BotMessage;
import com.typesafe.config.Config;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.routing.BalancingPool;
import akka.routing.FromConfig;

public class NotifyActor extends UntypedAbstractActor {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(NotifyActor.class);
	
	private final ActorRef pipelineActor;
	
	public NotifyActor() {
		int nr_of_instances;
		Config config = SisIpsActorSystem.getInstance().getIpsActorConfig();
		nr_of_instances = config.getInt("akka.actor.deployment./pipeline_actor.nr-of-instances");
		
		pipelineActor = getContext().actorOf(Props.create(PipelineActor.class).withRouter(new BalancingPool(nr_of_instances)),"pipeline_actor");
		
	}

	@Override
	public void onReceive(Object arg0) throws Throwable {
		System.out.println(getSelf().path() + " Entry");
		
		if (arg0 instanceof InteractionRequest) {
			InteractionRequest interactionRequest = (InteractionRequest) arg0; 
			System.out.println(getSelf().path() +" Received InteractionRequest for Transaction: " + interactionRequest.getTransactionId() );
			pipelineActor.tell(interactionRequest , getSelf());
			
		} 
		if (arg0 instanceof BotMessage) {
			BotMessage botMessage = (BotMessage) arg0; 
			System.out.println(getSelf().path() +" Received BotMessage for Transaction: " + botMessage.getTransactionId() );
			pipelineActor.tell(botMessage , getSelf());
			
		}
		System.out.println(getSelf().path() + " Exit");
	}
	

}
