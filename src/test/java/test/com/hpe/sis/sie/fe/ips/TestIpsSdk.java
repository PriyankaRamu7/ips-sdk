package test.com.hpe.sis.sie.fe.ips;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import akka.testkit.TestActorRef;
import akka.testkit.javadsl.TestKit;

import com.google.gson.Gson;
import com.hpe.sis.sie.fe.ips.auth.actors.IPSSecurityActor;
import com.hpe.sis.sie.fe.ips.auth.exception.AuthException;
import com.hpe.sis.sie.fe.ips.auth.exception.IPSException;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthRequest;
import com.hpe.sis.sie.fe.ips.auth.service.IPSSecurity;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.processing.actors.SynchronousActor;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionResponse;
import com.hpe.sis.sie.fe.ips.processor.service.InteractionProcessor;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.auth.model.AuthVO;
import com.hpe.sis.sie.fe.ips.utils.auth.model.InteractionVO;
import com.typesafe.config.Config;


public class TestIpsSdk {
	
	
	
	private IPSSecurity ipsSecurity;
	private InteractionProcessor interactionProcessor;
	private AuthRequest authRequest;
	private Applications applicationsVO;
	private AuthVO authVO;
	private InteractionVO interactionRequest;
	static ActorSystem system;
	static Config actorConfig;
	private static SisIpsActorSystem ipsSystem;
	private static ActorSystem actorSystem;
	private static FromConfig akkaconfig;

	
	@AfterClass
	public static void teardown() {
		/*try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TestKit.shutdownActorSystem(actorSystem);
		actorSystem = null;
		actorConfig = null;
		akkaconfig = null;*/
	}

	@BeforeClass
	public static void setUp(){
		
		ipsSystem = SisIpsActorSystem.getInstance();
		actorSystem = ipsSystem.getIpsActorSystem();
		akkaconfig = new FromConfig();
		
		/*ipsSystem.ipsSecurityActor = actorSystem.actorOf(Props.create(IPSSecurityActor.class));
		
		ipsSystem.interactionProcessingActor = actorSystem.actorOf(Props.create(InteractionProcessingActor.class));*/
		
	}
	
	@Before
	public void beforeEachTest() {
		ipsSecurity = IPSSecurity.build();
		interactionProcessor = InteractionProcessor.build();
		authRequest = new AuthRequest();
		interactionRequest = new InteractionVO();
		authVO = new AuthVO();
	}
	
	/*@Test
	public void testIpsSecurity(){
		
		System.out.println("in testips security::");
		Gson gson = new Gson();
	//	String appMetaDataStr = "{\n  \"id\": \"d66ad2f0-1094-48d8-8e4d-e1d5224a6a76\",\n  \"obeId\": \"1008cbb6-fa7e-4377-b411-47b43fa1f484\",\n  \"deployId\": \"468c7695-aa27-4683-9f29-7f0e696d4456\",\n  \"name\": \"JunitTestApp\",\n  \"description\": \"\",\n  \"longDescription\": \" \",\n  \"version\": \"1.3.0\",\n  \"createdBy\": \"priyankar\",\n  \"createdAt\": 1499409757201,\n  \"modifiedBy\": \"priyankar\",\n  \"modifiedAt\": 1499409757201,\n  \"userRole\": \"OBE-ADMIN\",\n  \"legal\": \" \",\n  \"tags\": [\n    \"\"\n  ],\n  \"validity\": {\n    \"startTime\": 1499409970139,\n    \"endTime\": 1504204140000\n  },\n  \"isTaxonomy\": false,\n  \"isComplete\": true,\n  \"isSuspended\": false,\n  \"smartKeys\": [\n    {\n      \"id\": \"d29bce1f-5016-445d-baff-e616fbac9ee0\",\n      \"name\": \"TestAuth\",\n      \"description\": \"\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499410432869,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410558297,\n      \"expiryTime\": 1501484220000,\n      \"methods\": [\n        \"POST\"\n      ],\n      \"txType\": \"onem2m\",\n      \"platforms\": [\n        \"NA\"\n      ],\n      \"dayOfWeek\": [\n        \"Monday\",\n        \"Tuesday\",\n        \"Wednesday\",\n        \"Thursday\",\n        \"Friday\",\n        \"Saturday\",\n        \"Sunday\"\n      ],\n      \"activeTimeStart\": \"00:00:00\",\n      \"activeTimeEnd\": \"23:30:00\",\n      \"userRole\": \"OBE-ADMIN\"\n    }\n  ],\n  \"apiKeys\": [\n    {\n      \"id\": \"00d3c64d-5ba4-4c62-80cc-cbd9a63e0cd2\",\n      \"name\": \"TestAuth\",\n      \"description\": \"\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499410379619,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410379619,\n      \"userRole\": \"OBE-ADMIN\",\n      \"expiryTime\": 1501484204000,\n      \"isAppSuspended\": false\n    }\n  ],\n  \"properties\": [],\n  \"virtualObjects\": [\n    {\n      \"id\": \"d7098c48-0821-4053-8a2f-477b6c8d877c\",\n      \"name\": \"TestVO\",\n      \"description\": \"desc\",\n      \"version\": \"1.3.0\",\n      \"type\": \"Thing\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499409945156,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410326034,\n      \"smartKeys\": [\n        \"d29bce1f-5016-445d-baff-e616fbac9ee0\"\n      ],\n      \"attributes\": [\n        {\n          \"name\": \"inputName\",\n          \"type\": \"String\",\n          \"mandatory\": true\n        }\n      ],\n      \"isAPIKeyAuthRequired\": true,\n      \"operations\": [\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"responseCache\": {\n            \"enabled\": true,\n            \"ttl\": 25000\n          },\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"GET\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"POST\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"PUT\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"PATCH\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"DELETE\"\n        }\n      ],\n      \"activities\": [\n        {\n          \"id\": \"2afead9b-5166-45c2-8db4-aecfdc753463\",\n          \"name\": \"onem2mTest\",\n          \"description\": \"\",\n          \"channelId\": \"onem2m\",\n          \"processing\": {\n            \"pattern\": \"ParallelProcessing\",\n            \"task\": {\n              \"id\": \"com.hpe.clasp.example.helloWorld\",\n              \"name\": \"Hello World Service\",\n              \"version\": \"1.0\",\n              \"engine\": \"Java\"\n            },\n            \"activityMap\": []\n          },\n          \"properties\": [],\n          \"timeout\": 25000,\n          \"tinyuri\": \"AyPTno8\",\n          \"schema\": \"\",\n          \"isAPIKeyAuthRequired\": true\n        },\n        {\n          \"id\": \"b961488c-ad6d-0a5e-e043-052985e41147\",\n          \"name\": \"callNotifyTest\",\n          \"description\": \"\",\n          \"channelId\": \"cnActivityStreams\",\n          \"processing\": {\n            \"pattern\": \"ParallelProcessing\",\n            \"task\": {\n              \"id\": \"com.hpe.clasp.example.helloWorld\",\n              \"name\": \"Hello World Service\",\n              \"version\": \"1.0\",\n              \"engine\": \"Java\"\n            },\n            \"activityMap\": []\n          },\n          \"properties\": [],\n          \"timeout\": 25000,\n          \"tinyuri\": \"AyPTno9\",\n          \"schema\": \"\",\n          \"isAPIKeyAuthRequired\": true\n        }\n      ]\n    }\n  ]\n}";
		
		String appMetaDataStr = "{\n  \"id\": \"d66ad2f0-1094-48d8-8e4d-e1d5224a6a76\",\n  \"obeId\": \"1008cbb6-fa7e-4377-b411-47b43fa1f484\",\n  \"deployId\": \"468c7695-aa27-4683-9f29-7f0e696d4456\",\n  \"name\": \"JunitTestApp\",\n  \"description\": \"\",\n  \"longDescription\": \" \",\n  \"version\": \"1.3.0\",\n  \"createdBy\": \"priyankar\",\n  \"createdAt\": 1499409757201,\n  \"modifiedBy\": \"priyankar\",\n  \"modifiedAt\": 1499409757201,\n  \"userRole\": \"OBE-ADMIN\",\n  \"legal\": \" \",\n  \"tags\": [\n    \"\"\n  ],\n  \"validity\": {\n    \"startTime\": 1499409970139,\n    \"endTime\": 1504204140000\n  },\n  \"isTaxonomy\": false,\n  \"isComplete\": true,\n  \"isSuspended\": false,\n  \"smartKeys\": [\n    {\n      \"id\": \"d29bce1f-5016-445d-baff-e616fbac9ee0\",\n      \"name\": \"TestAuth\",\n      \"description\": \"\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499410432869,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410558297,\n      \"expiryTime\": 1501484220000,\n      \"methods\": [\n        \"POST\"\n      ],\n      \"txType\": \"onem2m\",\n      \"platforms\": [\n        \"NA\"\n      ],\n      \"dayOfWeek\": [\n        \"Monday\",\n        \"Tuesday\",\n        \"Wednesday\",\n        \"Thursday\",\n        \"Friday\",\n        \"Saturday\",\n        \"Sunday\"\n      ],\n      \"activeTimeStart\": \"00:00:00\",\n      \"activeTimeEnd\": \"23:30:00\",\n      \"userRole\": \"OBE-ADMIN\"\n    }\n  ],\n  \"apiKeys\": [\n    {\n      \"id\": \"00d3c64d-5ba4-4c62-80cc-cbd9a63e0cd2\",\n      \"name\": \"TestAuth\",\n      \"description\": \"\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499410379619,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410379619,\n      \"userRole\": \"OBE-ADMIN\",\n      \"expiryTime\": 1501484204000,\n      \"isAppSuspended\": false\n    }\n  ],\n  \"properties\": [],\n  \"virtualObjects\": [\n    {\n      \"id\": \"d7098c48-0821-4053-8a2f-477b6c8d877c\",\n      \"name\": \"TestVO\",\n      \"description\": \"desc\",\n      \"version\": \"1.3.0\",\n      \"type\": \"Thing\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499409945156,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410326034,\n      \"smartKeys\": [],\n      \"isAPIKeyAuthRequired\": true,\n      \"operations\": [\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"responseCache\": {\n            \"enabled\": true,\n            \"ttl\": 25000\n          },\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"GET\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"POST\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"PUT\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"PATCH\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"DELETE\"\n        }\n      ],\n      \"activities\": [\n        {\n          \"id\": \"2afead9b-5166-45c2-8db4-aecfdc753463\",\n          \"name\": \"onem2mTest\",\n          \"description\": \"\",\n          \"channelId\": \"onem2m\",\n          \"processing\": {\n            \"pattern\": \"ParallelProcessing\",\n            \"task\": {\n              \"id\": \"com.hpe.clasp.example.helloWorld\",\n              \"name\": \"Hello World Service\",\n              \"version\": \"1.0\",\n              \"engine\": \"Java\"\n            },\n            \"activityMap\": []\n          },\n          \"properties\": [],\n          \"timeout\": 25000,\n          \"tinyuri\": \"AyPTno8\",\n          \"schema\": \"\",\n          \"isAPIKeyAuthRequired\": true\n        },\n        {\n          \"id\": \"b961488c-ad6d-0a5e-e043-052985e41147\",\n          \"name\": \"callNotifyTest\",\n          \"description\": \"\",\n          \"channelId\": \"cnActivityStreams\",\n          \"processing\": {\n            \"pattern\": \"ParallelProcessing\",\n            \"task\": {\n              \"id\": \"com.hpe.clasp.example.helloWorld\",\n              \"name\": \"Hello World Service\",\n              \"version\": \"1.0\",\n              \"engine\": \"Java\"\n            },\n            \"activityMap\": []\n          },\n          \"properties\": [],\n          \"timeout\": 25000,\n          \"tinyuri\": \"AyPTno9\",\n          \"schema\": \"\",\n          \"isAPIKeyAuthRequired\": true\n        }\n      ]\n    }\n  ]\n}";
		
		try {
			byte[] appData = Files.readAllBytes(Paths.get("C:\\Users\\nnik\\SIS-1.3\\ips-sdk\\src\\test\\resources\\app-metadata.json"));
			String appMetaDataStr = new String(appData);
			Gson gson = new Gson();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		applicationsVO =gson.fromJson(appMetaDataStr, Applications.class);
		authRequest.setApplicationVO(applicationsVO);
		
		authVO.setApikey("00d3c64d-5ba4-4c62-80cc-cbd9a63e0cd2");
		authVO.setAppId("d66ad2f0-1094-48d8-8e4d-e1d5224a6a76");
		authVO.setAppOwnerRole("");
		authVO.setChannelDeploymentId("468c7695-aa27-4683-9f29-7f0e696d4456");
		authVO.setConversationType("");
		authVO.setOperation("");
		authVO.setPlatform("");
		authVO.setSmartkey("");
		authVO.setVirtualObject("d7098c48-0821-4053-8a2f-477b6c8d877c");
		
		authRequest.setAuthVO(authVO);
		Applications response = null;

		final Props props = Props.create(IPSSecurityActor.class);
		
		 TestActorRef<IPSSecurityActor> ipsRef = TestActorRef.create(actorSystem, props);
		
		final IPSSecurityActor actor = ipsRef.underlyingActor();
		
		try {
			 response=ipsSecurity.authenticateAndValidate(authRequest);
		} catch (AuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assert(false);
		}
		
		assertEquals("AUTH_SUCCESS", response);
			
	}*/
	
	@Test
	public void testIpsProcessor() throws InterruptedException, ExecutionException, IPSException{
		
		System.out.println("Entry in Junit");
		interactionRequest.setTimeOut(10);
		interactionRequest.setProcessingPattern("Synchronous");
		interactionRequest.setTransactionId("abc");
		
		final Props props = Props.create(SynchronousActor.class);
		
		TestActorRef<SynchronousActor> ref =  TestActorRef.create(actorSystem, props);
		
		final SynchronousActor actor = ref.underlyingActor();
		
		/*InteractionResponse res = interactionProcessor.process(interactionRequest);
		
		System.out.println(res.getTransactionId());
		assertEquals(interactionRequest.getTransactionId(), res.getTransactionId());*/
		
	}
}
