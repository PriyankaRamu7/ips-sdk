package com.hpe.sis.sie.fe.ips.processing.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionResponse;
import com.hpe.sis.sie.fe.ips.processing.utils.ProcessingPattern;
import com.typesafe.config.Config;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.routing.BalancingPool;

public class InteractionProcessingActor extends UntypedAbstractActor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InteractionProcessingActor.class);
	
	public InteractionProcessingActor() {
		/*int nr_of_instances;
		SisIpsActorSystem ipsSystem = SisIpsActorSystem.getInstance();
		Config config = SisIpsActorSystem.getInstance().getIpsActorConfig();
		nr_of_instances = config.getInt("akka.actor.deployment./sync_actor.nr-of-instances");
		ipsSystem.synchronousActor = getContext().actorOf(Props.create(SynchronousActor.class).withRouter(new BalancingPool(nr_of_instances)),"sync_actor");
		
		nr_of_instances = config.getInt("akka.actor.deployment./async_actor.nr-of-instances");
		ipsSystem.asynchronousActor = getContext().actorOf(Props.create(AsynchronousActor.class).withRouter(new BalancingPool(nr_of_instances)),"async_actor");
		
		nr_of_instances = config.getInt("akka.actor.deployment./notify_actor.nr-of-instances");
		ipsSystem.notifyActor = getContext().actorOf(Props.create(NotifyActor.class).withRouter(new BalancingPool(nr_of_instances)),"notify_actor");*/
		
		
	}

	@Override
	public void onReceive(Object arg0) throws Throwable {
	/*System.out.println(getSelf().path() + " Entry");
		if (arg0 instanceof InteractionRequest) {
			InteractionRequest interactionRequest = (InteractionRequest) arg0; 
			System.out.println("Received InteractionRequest for Transaction: " + interactionRequest.getTransactionId());
			if (ProcessingPattern.Synchronous.equals(interactionRequest.getProcessingPattern())) {
				.tell(interactionRequest , getSelf());
				sender = getSender();
			} else if (ProcessingPattern.Asynchronous.equals(interactionRequest.getProcessingPattern())) {
				asyncActor.tell(interactionRequest, getSelf());
			} else if (ProcessingPattern.Notify.equals(interactionRequest.getProcessingPattern())) {
				notifyActor.tell(interactionRequest, getSelf());
			}
		} else if (arg0 instanceof InteractionResponse) {
			InteractionResponse interactionResponse = (InteractionResponse) arg0;
			System.out.println("Received InteractionResponse for Transaction: " + interactionResponse.getTransactionId() );
			// This is a response for synchornous processing
			// return to caller
			sender.tell(interactionResponse, getSelf());
		}*/
	}

}
