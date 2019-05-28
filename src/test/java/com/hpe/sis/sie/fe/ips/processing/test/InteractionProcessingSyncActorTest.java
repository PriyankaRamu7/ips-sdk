package com.hpe.sis.sie.fe.ips.processing.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.hpe.sis.sie.fe.dss.DSSService;
import com.hpe.sis.sie.fe.dss.exception.SISException;
import com.hpe.sis.sie.fe.ips.auth.exception.IPSException;
import com.hpe.sis.sie.fe.ips.common.ApplicationCAO;
import com.hpe.sis.sie.fe.ips.common.HttpClient;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.processing.actors.AsynchronousActor;
import com.hpe.sis.sie.fe.ips.processing.actors.NotifyActor;
import com.hpe.sis.sie.fe.ips.processing.actors.SynchronousActor;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionRequest;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionResponse;
import com.hpe.sis.sie.fe.ips.processing.utils.ProcessingPattern;
import com.hpe.sis.sie.fe.ips.transmap.vo.ServiceVO;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.configuration.dao.ChannelConfig;
import com.hpe.sis.sie.fe.ips.utils.sieconfiguration.service.SIEConfigurationService;
import com.hpe.sis.sie.fe.ips.utils.sieconfiguration.vo.SIEConfigurationVO;
import com.hpe.sis.sie.fe.ips.utils.snmp.constants.SNMPConstants.ComponentName;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;
import akka.testkit.javadsl.TestKit;
import okhttp3.OkHttpClient;

public class InteractionProcessingSyncActorTest {

	static SisIpsActorSystem ipsSystem;
	static ActorSystem system;
	static Config actorConfig;
	
	private static TestKit testKit;
	private static TestActorRef<SynchronousActor> refSync;
	private static TestActorRef<AsynchronousActor> refAsync;
	private static TestActorRef<NotifyActor> refNotify;
	private static TestProbe testProbe;
	private static InteractionRequest interactionRequest;
	
	@BeforeClass
	public static void setup() {
		actorConfig = ConfigFactory.load();
		ipsSystem = SisIpsActorSystem.getInstance();
		system = ipsSystem.getIpsActorSystem();
		
		testKit = new TestKit(system);
		testProbe = TestProbe.apply(system);

		refSync = TestActorRef.create(system,
				Props.create(SynchronousActor.class));
		createConfigData();
		createHttpClient();
		interactionRequest = createInteractionReqData();
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

	@Test
	public void testSync1() throws InterruptedException, ExecutionException {
		InteractionRequest interactionRequest =  createInteractionReqData();
		 interactionRequest = createInteractionReqDataForHelloWorldService();
		 interactionRequest.setTransactionId("txn11");
		final CompletableFuture<Object> future = PatternsCS
				.ask(refSync, interactionRequest, 5000).toCompletableFuture();
		
		InteractionResponse interactionResponse = (InteractionResponse) future.get();
		assertEquals(interactionRequest.getTransactionId(), interactionResponse.getTransactionId());
	}

	@Test
	public void testSync2() throws InterruptedException, ExecutionException {
		InteractionRequest interactionRequest =  createInteractionReqData();
		 interactionRequest = createInteractionReqDataForStringComparatorService();
		 interactionRequest.setTransactionId("txn22");
		final CompletableFuture<Object> future = PatternsCS
				.ask(refSync, interactionRequest, 5000).toCompletableFuture();

		InteractionResponse interactionResponse = (InteractionResponse) future.get();
		assertEquals(interactionRequest.getTransactionId(), interactionResponse.getTransactionId());
	}

	@Test
	public void testSync3() throws InterruptedException, ExecutionException {
		InteractionRequest interactionRequest =  createInteractionReqData();
		 interactionRequest = createInteractionReqDataForJsonValueExtractorService();
		 interactionRequest.setTransactionId("txn33");
		final CompletableFuture<Object> future = PatternsCS
				.ask(refSync, interactionRequest, 5000).toCompletableFuture();

		InteractionResponse interactionResponse = (InteractionResponse) future.get();
		assertEquals(interactionRequest.getTransactionId(), interactionResponse.getTransactionId());
	}
	
	@Test
	public void testTransactionLogging() throws InterruptedException, ExecutionException {
		InteractionRequest interactionRequest =  createInteractionReqData();
		 interactionRequest = createInteractionReqDataForStringComparatorService();
		 interactionRequest.setTransactionId("txn44");		 
			try
			{
				final CompletableFuture<Object> future = PatternsCS
						.ask(refSync, interactionRequest, 5000).toCompletableFuture();
				InteractionResponse interactionResponse = (InteractionResponse) future.get();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				assert(false);
			}
		
			assert(true);
		}
	
	public static InteractionRequest createInteractionReqData(){
		InteractionRequest interactionRequest = new InteractionRequest();
	
		interactionRequest.setTransactionId("aa996b43-4849-4603-beac-43a899764c50");
		interactionRequest.setVirtualObjectId("d7098c48-0821-4053-8a2f-477b6c8d877c");
		interactionRequest.setInteractionContextId("ab674ee0-1f5b-4d10-aa71-b7abc6208474");
		interactionRequest.setFromChannel("ic-vo-operations");
		interactionRequest.setMethod("POST");
		//interactionRequest.setQueryParameters("inputName=junitTest");
		Map<String, String> requestHeaderMap = new HashMap<>();
		requestHeaderMap.put("SISApiKey", "00d3c64d-5ba4-4c62-80cc-cbd9a63e0cd2");
		requestHeaderMap.put("Content-Type", "application/json");
		requestHeaderMap.put("SISSmartKey", "d29bce1f-5016-445d-baff-e616fbac9ee0");
		interactionRequest.setRequestHeaders(requestHeaderMap);
		interactionRequest.setSisHeaders("Cookie,Accept,SISApiKey,SISSmartKey");
		interactionRequest.setProcessingPattern("RequestResponse");
		interactionRequest.setBeTimeOut(Integer.parseInt("25000"));
		interactionRequest.setTestRequest(false);
		
		Applications applicationVO = null;
		String applicationJson = "{\n  \"id\": \"d66ad2f0-1094-48d8-8e4d-e1d5224a6a76\",\n  \"obeId\": \"1008cbb6-fa7e-4377-b411-47b43fa1f484\",\n  \"deployId\": \"468c7695-aa27-4683-9f29-7f0e696d4456\",\n  \"name\": \"JunitTestApp\",\n  \"description\": \"\",\n  \"longDescription\": \" \",\n  \"version\": \"1.3.0\",\n  \"createdBy\": \"priyankar\",\n  \"createdAt\": 1499409757201,\n  \"modifiedBy\": \"priyankar\",\n  \"modifiedAt\": 1499409757201,\n  \"userRole\": \"OBE-ADMIN\",\n  \"legal\": \" \",\n  \"tags\": [\n    \"\"\n  ],\n  \"validity\": {\n    \"startTime\": 1499409970139,\n    \"endTime\": 1546174692000\n  },\n  \"isTaxonomy\": false,\n  \"isComplete\": true,\n  \"isSuspended\": false,\n  \"smartKeys\": [\n    {\n      \"id\": \"d29bce1f-5016-445d-baff-e616fbac9ee0\",\n      \"name\": \"TestAuth\",\n      \"description\": \"\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499410432869,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410558297,\n      \"expiryTime\": 1546174692000,\n      \"methods\": [\n        \"POST\"\n      ],\n      \"txType\": \"onem2m\",\n      \"platforms\": [\n        \"NA\"\n      ],\n      \"dayOfWeek\": [\n        \"Monday\",\n        \"Tuesday\",\n        \"Wednesday\",\n        \"Thursday\",\n        \"Friday\",\n        \"Saturday\",\n        \"Sunday\"\n      ],\n      \"activeTimeStart\": \"00:00:00\",\n      \"activeTimeEnd\": \"23:30:00\",\n      \"userRole\": \"OBE-ADMIN\"\n    }\n  ],\n  \"apiKeys\": [\n    {\n      \"id\": \"00d3c64d-5ba4-4c62-80cc-cbd9a63e0cd2\",\n      \"name\": \"TestAuth\",\n      \"description\": \"\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499410379619,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410379619,\n      \"userRole\": \"OBE-ADMIN\",\n      \"expiryTime\": 1501484204000,\n      \"isAppSuspended\": false\n    }\n  ],\n  \"properties\": [],\n  \"virtualObjects\": [\n    {\n      \"id\": \"d7098c48-0821-4053-8a2f-477b6c8d877c\",\n      \"name\": \"TestVO\",\n      \"description\": \"desc\",\n      \"version\": \"1.3.0\",\n      \"type\": \"Thing\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499409945156,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410326034,\n      \"smartKeys\": [\n        \"d29bce1f-5016-445d-baff-e616fbac9ee0\"\n      ],\n      \"attributes\": [\n        {\n          \"name\": \"inputName\",\n          \"type\": \"String\",\n          \"mandatory\": true\n        }\n      ],\n      \"isAPIKeyAuthRequired\": true,\n      \"operations\": [\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"responseCache\": {\n            \"enabled\": true,\n            \"ttl\": 25000\n          },\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"GET\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"POST\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"PUT\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"PATCH\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"DELETE\"\n        }\n      ],\n      \"activities\": [\n        {\n          \"id\": \"2afead9b-5166-45c2-8db4-aecfdc753463\",\n          \"name\": \"onem2mTest\",\n          \"description\": \"\",\n          \"channelId\": \"onem2m\",\n          \"processing\": {\n            \"pattern\": \"ParallelProcessing\",\n            \"task\": {\n              \"id\": \"com.hpe.clasp.example.helloWorld\",\n              \"name\": \"Hello World Service\",\n              \"version\": \"1.0\",\n              \"engine\": \"Java\"\n            },\n            \"activityMap\": []\n          },\n          \"properties\": [],\n          \"timeout\": 25000,\n          \"tinyuri\": \"AyPTno8\",\n          \"schema\": \"\",\n          \"isAPIKeyAuthRequired\": true\n        },\n        {\n          \"id\": \"b961488c-ad6d-0a5e-e043-052985e41147\",\n          \"name\": \"callNotifyTest\",\n          \"description\": \"\",\n          \"channelId\": \"cnActivityStreams\",\n          \"processing\": {\n            \"pattern\": \"ParallelProcessing\",\n            \"task\": {\n              \"id\": \"com.hpe.clasp.example.helloWorld\",\n              \"name\": \"Hello World Service\",\n              \"version\": \"1.0\",\n              \"engine\": \"Java\"\n            },\n            \"activityMap\": []\n          },\n          \"properties\": [],\n          \"timeout\": 25000,\n          \"tinyuri\": \"AyPTno9\",\n          \"schema\": \"\",\n          \"isAPIKeyAuthRequired\": true\n        }\n      ]\n    }\n  ]\n}";
		
		Gson gson = new Gson();
		applicationVO = gson.fromJson(applicationJson, Applications.class);
		interactionRequest.setApplicationVO(applicationVO);
		return interactionRequest;
	}
	
	public static InteractionRequest createInteractionReqDataForHelloWorldService(){
		interactionRequest.setRequestBody("{\"inputName\":\"Junit\"}");
		ServiceVO serviceVO = new ServiceVO();
		serviceVO.setTaskId("com.hpe.clasp.example.helloWorld");
		serviceVO.setName("Hello World Service");
		serviceVO.setEngine("Java");
		serviceVO.setVersion("1.0");
		interactionRequest.setServiceVO(serviceVO);
		List<String> paramList = Arrays.asList("inputName");
		interactionRequest.setParamList(paramList);
		return interactionRequest;
	}
	
	public static InteractionRequest createInteractionReqDataForStringComparatorService(){
		interactionRequest.setRequestBody("{\"fact\": \"sampleString\",\"expression\": \"EQUALS\",\"evaluateTo\": \"string\",\"regularExpression\": \"PLACE Regular Expression HERE\"}");
		ServiceVO serviceVO = new ServiceVO();
		serviceVO.setTaskId("hpe.string.comparator");
		serviceVO.setName("String-Comparator");
		serviceVO.setEngine("Java");
		serviceVO.setVersion("1.0");
		interactionRequest.setServiceVO(serviceVO);
		List<String> paramList = Arrays.asList("fact","expression","evaluateTo","regularExpression");
		interactionRequest.setParamList(paramList);
		return interactionRequest;
	}

	public static InteractionRequest createInteractionReqDataForJsonValueExtractorService(){
		interactionRequest.setRequestBody("{\"input_json\":{\"test\":\"value\"},\"json_path\":\"test\"}");
		ServiceVO serviceVO = new ServiceVO();
		serviceVO.setTaskId("hpe.sis.microservices.json.value.extractor");
		serviceVO.setName("HPE SIS Json Value Extractor");
		serviceVO.setEngine("Java");
		serviceVO.setVersion("1.0");
		interactionRequest.setServiceVO(serviceVO);
		List<String> paramList = Arrays.asList("input_json","json_path");
		interactionRequest.setParamList(paramList);
		return interactionRequest;
	}
	
	public static void createConfigData() {
		String siePayload = "{  \"dss\": { 		\"mode\": \"standalone\", 		\"password\": \"redis123\", 		\"ipPort\": \"15.154.114.85:6379\", 		\"rdbNumber\": 0, 		\"masterName\": \"\", 		\"timeout\": 1000, 		\"maxActivePoolSize\": 50, 		\"maxIdle\": 20, 		\"minIdle\": 1, 		\"retryCount\": 3, 		\"waitTime\": 1000, 		\"isTLS\": false, 		\"isHostLB\": false 	},   \"sie\": {     \"be\": {       \"taskEngine\": {         \"port\": 10090,         \"proto\": \"http\",         \"host\": \"15.154.114.81\",         \"isTLS\": false,         \"isHostLB\": true       },       \"appEngine\": {         \"port\": 1228,         \"proto\": \"http\",         \"host\": \"127.0.0.28\",         \"isTLS\": false,         \"isHostLB\": false       },       \"secretToken\": \"edcb38ae-a850-743e-96ec-ab36f97d5329\"     },     \"fe\": {       \"icSet\": [         {           \"icName\": \"CallNotification\",           \"icId\": \"683b4535-4db8-441d-a5d3-53dae9dba858\",           \"port\": 1221,           \"proto\": \"https\",           \"host\": \"127.0.0.21\",           \"isTLS\": true,           \"isHostLB\": true         },         {          \"icName\": \"W3C Activity Stream HTTP\",           \"icId\": \"8bdaf2bb-a9d2-454c-852d-de5095b5a7a5\",           \"port\": 1222,           \"proto\": \"https\",           \"host\": \"127.0.0.22\",           \"isTLS\": true,           \"isHostLB\": false         },         {           \"icName\": \"W3C Activity Stream WS\",           \"icId\": \"79a51875-586c-4fc5-a1ba-13b5679d3729\",           \"port\": 1223,           \"proto\": \"ws\",           \"host\": \"127.0.0.23\",           \"isTLS\": false,           \"isHostLB\": true         },         {           \"icName\": \"OneM2M\",           \"icId\": \"40b619f9-85bc-4185-a1db-fa984e6bf1b9\",           \"port\": 1224,           \"proto\": \"http\",           \"host\": \"127.0.0.24\",           \"isTLS\": false,           \"isHostLB\": false         },         {           \"icName\": \"VO Operation\",           \"icId\": \"05622041-5ba3-4e35-97ef-48c21192cf0a\",           \"port\": 1225,           \"proto\": \"https\",           \"host\": \"127.0.0.25\",           \"isTLS\": true,           \"isHostLB\": true         }       ],       \"botEngine\": {         \"port\": 1226,         \"proto\": \"https\",         \"host\": \"127.0.0.1\",         \"isTLS\": true,         \"isHostLB\": false       }     }   } } ";
		//String siePayload = "{  \"dss\": { 		\"mode\": \"standalone\", 		\"password\": \"redis123\", 		\"ipPort\": \"15.154.114.81:6379\", 		\"rdbNumber\": 0, 		\"masterName\": \"\", 		\"timeout\": 1000, 		\"maxActivePoolSize\": 50, 		\"maxIdle\": 20, 		\"minIdle\": 1, 		\"retryCount\": 3, 		\"waitTime\": 1000, 		\"isTLS\": false, 		\"isHostLB\": false 	},   \"sie\": {     \"be\": {       \"taskEngine\": {         \"port\": 19080,         \"proto\": \"http\",         \"host\": \"30.208.177.14\",         \"isTLS\": false,         \"isHostLB\": true       },       \"appEngine\": {         \"port\": 1228,         \"proto\": \"http\",         \"host\": \"127.0.0.28\",         \"isTLS\": false,         \"isHostLB\": false       },       \"secretToken\": \"edcb38ae-a850-743e-96ec-ab36f97d5329\"     },     \"fe\": {       \"icSet\": [         {           \"icName\": \"CallNotification\",           \"icId\": \"683b4535-4db8-441d-a5d3-53dae9dba858\",           \"port\": 1221,           \"proto\": \"https\",           \"host\": \"127.0.0.21\",           \"isTLS\": true,           \"isHostLB\": true         },         {          \"icName\": \"W3C Activity Stream HTTP\",           \"icId\": \"8bdaf2bb-a9d2-454c-852d-de5095b5a7a5\",           \"port\": 1222,           \"proto\": \"https\",           \"host\": \"127.0.0.22\",           \"isTLS\": true,           \"isHostLB\": false         },         {           \"icName\": \"W3C Activity Stream WS\",           \"icId\": \"79a51875-586c-4fc5-a1ba-13b5679d3729\",           \"port\": 1223,           \"proto\": \"ws\",           \"host\": \"127.0.0.23\",           \"isTLS\": false,           \"isHostLB\": true         },         {           \"icName\": \"OneM2M\",           \"icId\": \"40b619f9-85bc-4185-a1db-fa984e6bf1b9\",           \"port\": 1224,           \"proto\": \"http\",           \"host\": \"127.0.0.24\",           \"isTLS\": false,           \"isHostLB\": false         },         {           \"icName\": \"VO Operation\",           \"icId\": \"05622041-5ba3-4e35-97ef-48c21192cf0a\",           \"port\": 1225,           \"proto\": \"https\",           \"host\": \"127.0.0.25\",           \"isTLS\": true,           \"isHostLB\": true         }       ],       \"botEngine\": {         \"port\": 1226,         \"proto\": \"https\",         \"host\": \"127.0.0.1\",         \"isTLS\": true,         \"isHostLB\": false       }     }   } } ";
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = (JSONObject) parser.parse(siePayload);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		SIEConfigurationVO sieConfigurationVO = SIEConfigurationService.getSIEConfigurationVO(jsonObject);
		SIEConfigurationService.sieconfigVO = sieConfigurationVO;
		IPSConfig.sieConfigurationVO = SIEConfigurationService.sieconfigVO;
		try {
			ApplicationCAO.initializeDSSProperties(SIEConfigurationVO.getDssConfigJson(), "ic-vo-operations");
		} catch (SISException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void createHttpClient() {
		TestConfig config = new TestConfig();
		
		config.setConnectionTimeout(30);
		//ipsConfig.setHttpProxyHost(httpProxyHost);
		//ipsConfig.setHttpProxyPort(httpProxyPort);
		config.setKeepAliveDuration(60);
		config.setMaxIdleConnections(5);
		
		//ipsConfig.setProxyType(proxyType);
		config.setReadTimeout(30);
		config.setWriteTimeout(30);
		config.setChannelName("JUnit");
		config.setBackendResponseDataTTL(5);
		config.setLogdelimiter('|');
		config.setLogBEServiceData(true);
		config.setTransactionFields("TRANSACTIONID,REQUESTEDTIME,LOCALHOST,REMOTEHOST,USER_NAME,APPLICATION,VIRTUAL_OBJECTS,SERVICE_ID,BOT_ID,INTERACTION_CHANNEL,INTERACTION_API_RESULTCODE,INTERACTION_API_ERROR_DETAIL,BACKEND_INVOCATION_STATUS,INTERACTION_API_RESPONSETIME,BACKEND_INVOCATION_TASKID,BACKEND_RESPONSETIME,INTERACTION_API_REQUEST,BACKEND_RESPONSE,INTERACTION_API_METHODTYPE,IS_TEST_REQUEST,OBE_ID,X_API4SAAS_TOKEN");
		config.setTransactionLogEnabled(true);
		config.setSecurityAuditEnabled(true);
		config.setActorTimeOut(20);
		config.setTracingLogEnabled(false);
		config.setTraceLogTimeStampFormat("yyyy-MM-dd HH:mm:ss.SSS");
		config.setTraceFields("TRANSACTIONID,EVENT,EVENT_TIME,METHOD,ACTOR_PATH,STATUS,ERROR_DETAIL");
		config.setSaveActivitystreamByTime(true);
		config.setInteractionContextTTL(10);
		config.setInteractionContextFields("TRANSACTIONID,INTERACTION_ID,CONTEXT_ID,APP_ID,START_TIME,UPDATE_TIME,END_TIME,VO_ID,BOT_ID,NAME,TYPE,PLTYPE,STTYPE,TXTYPE,METHOD,ACTOR_PATH,TASK_ID,TASK_STATUS");
		try {
			IPSConfig.initializeConfig(config);
		} catch (IPSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpClient.createHttpClient();
		System.out.println("OkHttpClient created successfully" + HttpClient.getHttpClient());
	}
	
	static class TestConfig extends ChannelConfig {
		public TestConfig() {
			super();
		}
	}
}
