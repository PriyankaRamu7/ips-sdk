package com.hpe.sis.sie.fe.ips.processing.test;


import static org.junit.Assert.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.processing.actors.PipelineActor;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionResponse;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;
import akka.testkit.javadsl.TestKit;


public class PipelineActorTest {
	
	static SisIpsActorSystem ipsSystem;
	static ActorSystem system;
	static Config actorConfig;
	
	static TestKit testKit;
	static TestActorRef<PipelineActor> ref;
	static TestProbe testProbe;

	@BeforeClass
	public static void setup() {
		
		//actorConfig = ConfigFactory.load();
		//system = ActorSystem.create("IPSSystem", actorConfig);
		
		ipsSystem = SisIpsActorSystem.getInstance();
		system = ipsSystem.getIpsActorSystem();
		testKit = new TestKit(ipsSystem.getIpsActorSystem());
        testProbe = TestProbe.apply(testKit.getSystem());
        
        ref = TestActorRef.create(system, Props.create(PipelineActor.class));		
	}

	@AfterClass
	public static void teardown() {
		/*try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TestKit.shutdownActorSystem(system);
		system = null;
		actorConfig = null;*/
	}

	//@Test
	public void test() throws InterruptedException, ExecutionException {/*
		//final Props props = FromConfig.getInstance().props(Props.create(PipelineActor.class));
		//final TestActorRef<PipelineActor> ref = TestActorRef.create(system, props, "pipeline-actor");
		
		final PipelineActor actor = ref.underlyingActor();
		InteractionRequest req = new InteractionRequest();
		req.setTransactionId("abcd");
		
		final CompletableFuture<Object> future = PatternsCS.ask(ref, req, 3000).toCompletableFuture();
		
		InteractionResponse res = (InteractionResponse) future.get();;
		System.out.println(res.getTransactionId());
		assertEquals(req.getTransactionId(), res.getTransactionId());
	*/}


}
