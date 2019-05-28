package com.hpe.sis.sie.fe.ips.processing.test;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.hpe.sis.sie.fe.dss.exception.SISException;
import com.hpe.sis.sie.fe.ips.auth.exception.IPSException;
import com.hpe.sis.sie.fe.ips.common.ApplicationCAO;
import com.hpe.sis.sie.fe.ips.common.HttpClient;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.processing.messages.InteractionResponse;
import com.hpe.sis.sie.fe.ips.processing.model.Interaction;
import com.hpe.sis.sie.fe.ips.processing.model.InteractionResult;
import com.hpe.sis.sie.fe.ips.processing.utils.ProcessingPattern;
import com.hpe.sis.sie.fe.ips.processor.service.InteractionProcessor;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Task;
import com.hpe.sis.sie.fe.ips.utils.configuration.dao.ChannelConfig;
import com.hpe.sis.sie.fe.ips.utils.sieconfiguration.service.SIEConfigurationService;
import com.hpe.sis.sie.fe.ips.utils.sieconfiguration.vo.SIEConfigurationVO;
import com.hpe.sis.sie.fe.ips.utils.snmp.constants.SNMPConstants.ComponentName;

public class InteractionProcessingImplTest {
	
	@BeforeClass
	public static void setUp() {
		SisIpsActorSystem.getInstance();
		createConfigData();
		TestConfig config = new TestConfig();
		
		config.setConnectionTimeout(30);
		//ipsConfig.setHttpProxyHost(httpProxyHost);
		//ipsConfig.setHttpProxyPort(httpProxyPort);
		config.setKeepAliveDuration(60);
		config.setMaxIdleConnections(5);
		
		//ipsConfig.setProxyType(proxyType);
		config.setReadTimeout(30);
		config.setWriteTimeout(30);
		config.setBackendResponseDataTTL(200);
		config.setChannelName("ic-w3c-activitystreams");
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
		
		SisIpsActorSystem.getInstance();
	
	}

	static class TestConfig extends ChannelConfig {
		public TestConfig() {
		super();
		}
	}
	
	//@Test
	public void testParallelDistributedSync() {
		Interaction vo = createInteractionVO();
		vo.setProcessingPattern(ProcessingPattern.Synchronous.getPattern());
		vo.setRequestBody("{ \"items\" : [{\"inputName\":\"Junit\"}, {\"inputName\":\"Mockito\"}, {\"inputName\":\"Spring\"}, {\"inputName\":\"Spock\"}] } ");
		InteractionProcessor impl = InteractionProcessor.build();
		InteractionResult response =  null;
		try {
			response = impl.processParallelDistributed(vo);
		} catch (IPSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(response);
		Assert.assertNotNull(response);
	}
	
	//@Test
	public void testParallelDistributedAsync() {
		Interaction vo = createInteractionVO();
		vo.setProcessingPattern(ProcessingPattern.Asynchronous.getPattern());
		vo.setRequestBody("{ \"items\" : [{\"inputName\":\"Junit\"}, {\"inputName\":\"Mockito\"}, {\"inputName\":\"Spring\"}, {\"inputName\":\"Spock\"}] } ");
		InteractionProcessor impl = InteractionProcessor.build();
		InteractionResult response = null;
		try {
			response = impl.processParallelDistributed(vo);
		} catch (IPSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(response);
		Assert.assertNotNull(response);
	}
	
	//@Test
	public void testParallelDistributedNotify() {
		Interaction vo = createInteractionVO();
		vo.setProcessingPattern(ProcessingPattern.Notify.getPattern());
		vo.setRequestBody("{ \"items\" : [{\"inputName\":\"Junit\"}, {\"inputName\":\"Mockito\"}, {\"inputName\":\"Spring\"}, {\"inputName\":\"Spock\"}] } ");
		InteractionProcessor impl = InteractionProcessor.build();
		InteractionResult response = null;
		try {
			response = impl.processParallelDistributed(vo);
		} catch (IPSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(response);
		Assert.assertNotNull(response);
	}
	
	@Test
	public void testAsync() {
		Interaction vo = createInteractionVO();
		vo.setProcessingPattern(ProcessingPattern.Asynchronous.getPattern());
		
		InteractionProcessor impl = InteractionProcessor.build();
		InteractionResult response = null;
		try {
			response = impl.process(vo);
		} catch (IPSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(response);
		Assert.assertNotNull(response);
	}
	
	@Test
		public void testSync() {
			Interaction vo = createInteractionVO();
			vo.setProcessingPattern(ProcessingPattern.Synchronous.getPattern());
			
			InteractionProcessor impl = InteractionProcessor.build();
			InteractionResult response = null;
			try {
				response = impl.process(vo);
			} catch (IPSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(response);
			Assert.assertNotNull(response);
		}

	private Interaction createInteractionVO() {
		Interaction vo = new Interaction();
		Task task = new Task();
		vo.setActorTimeOut(25);
		
		Applications applicationVO = null;
		String applicationJson = "{\n  \"id\": \"d66ad2f0-1094-48d8-8e4d-e1d5224a6a76\",\n  \"obeId\": \"1008cbb6-fa7e-4377-b411-47b43fa1f484\",\n  \"deployId\": \"468c7695-aa27-4683-9f29-7f0e696d4456\",\n  \"name\": \"JunitTestApp\",\n  \"description\": \"\",\n  \"longDescription\": \" \",\n  \"version\": \"1.3.0\",\n  \"createdBy\": \"priyankar\",\n  \"createdAt\": 1499409757201,\n  \"modifiedBy\": \"priyankar\",\n  \"modifiedAt\": 1499409757201,\n  \"userRole\": \"OBE-ADMIN\",\n  \"legal\": \" \",\n  \"tags\": [\n    \"\"\n  ],\n  \"validity\": {\n    \"startTime\": 1499409970139,\n    \"endTime\": 1546174692000\n  },\n  \"isTaxonomy\": false,\n  \"isComplete\": true,\n  \"isSuspended\": false,\n  \"smartKeys\": [\n    {\n      \"id\": \"d29bce1f-5016-445d-baff-e616fbac9ee0\",\n      \"name\": \"TestAuth\",\n      \"description\": \"\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499410432869,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410558297,\n      \"expiryTime\": 1546174692000,\n      \"methods\": [\n        \"POST\"\n      ],\n      \"txType\": \"onem2m\",\n      \"platforms\": [\n        \"NA\"\n      ],\n      \"dayOfWeek\": [\n        \"Monday\",\n        \"Tuesday\",\n        \"Wednesday\",\n        \"Thursday\",\n        \"Friday\",\n        \"Saturday\",\n        \"Sunday\"\n      ],\n      \"activeTimeStart\": \"00:00:00\",\n      \"activeTimeEnd\": \"23:30:00\",\n      \"userRole\": \"OBE-ADMIN\"\n    }\n  ],\n  \"apiKeys\": [\n    {\n      \"id\": \"00d3c64d-5ba4-4c62-80cc-cbd9a63e0cd2\",\n      \"name\": \"TestAuth\",\n      \"description\": \"\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499410379619,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410379619,\n      \"userRole\": \"OBE-ADMIN\",\n      \"expiryTime\": 1501484204000,\n      \"isAppSuspended\": false\n    }\n  ],\n  \"properties\": [],\n  \"virtualObjects\": [\n    {\n      \"id\": \"d7098c48-0821-4053-8a2f-477b6c8d877c\",\n      \"name\": \"TestVO\",\n      \"description\": \"desc\",\n      \"version\": \"1.3.0\",\n      \"type\": \"Thing\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499409945156,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410326034,\n      \"smartKeys\": [\n        \"d29bce1f-5016-445d-baff-e616fbac9ee0\"\n      ],\n      \"attributes\": [\n        {\n          \"name\": \"inputName\",\n          \"type\": \"String\",\n          \"mandatory\": true\n        }\n      ],\n      \"isAPIKeyAuthRequired\": true,\n      \"operations\": [\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"responseCache\": {\n            \"enabled\": true,\n            \"ttl\": 25000\n          },\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"GET\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"POST\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"PUT\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"PATCH\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"DELETE\"\n        }\n      ],\n      \"activities\": [\n        {\n          \"id\": \"2afead9b-5166-45c2-8db4-aecfdc753463\",\n          \"name\": \"onem2mTest\",\n          \"description\": \"\",\n          \"channelId\": \"onem2m\",\n          \"processing\": {\n            \"pattern\": \"ParallelProcessing\",\n            \"task\": {\n              \"id\": \"com.hpe.clasp.example.helloWorld\",\n              \"name\": \"Hello World Service\",\n              \"version\": \"1.0\",\n              \"engine\": \"Java\"\n            },\n            \"activityMap\": []\n          },\n          \"properties\": [],\n          \"timeout\": 25000,\n          \"tinyuri\": \"AyPTno8\",\n          \"schema\": \"\",\n          \"isAPIKeyAuthRequired\": true\n        },\n        {\n          \"id\": \"b961488c-ad6d-0a5e-e043-052985e41147\",\n          \"name\": \"callNotifyTest\",\n          \"description\": \"\",\n          \"channelId\": \"cnActivityStreams\",\n          \"processing\": {\n            \"pattern\": \"ParallelProcessing\",\n            \"task\": {\n              \"id\": \"com.hpe.clasp.example.helloWorld\",\n              \"name\": \"Hello World Service\",\n              \"version\": \"1.0\",\n              \"engine\": \"Java\"\n            },\n            \"activityMap\": []\n          },\n          \"properties\": [],\n          \"timeout\": 25000,\n          \"tinyuri\": \"AyPTno9\",\n          \"schema\": \"\",\n          \"isAPIKeyAuthRequired\": true\n        }\n      ]\n    }\n  ]\n}";
		
		Gson gson = new Gson();
		applicationVO = gson.fromJson(applicationJson, Applications.class);
		vo.setApplicationVO(applicationVO);
		
		vo.setBeTimeOut(25);
		
		task.setEngine("Java");
		
		task.setId("com.hpe.clasp.example.helloWorld");
		
		task.setName("Hello World Service");
		
		task.setVersion("1.0");
		vo.setTask(task);
		
		vo.setFromChannel("ic-vo-operations");
		
		vo.setMethod("POST");
		
		vo.setInteractionContextId("int1234");
		
		vo.setTestRequest(false);
		
		
		List list = new ArrayList<String>();
		list.add("inputName");
		vo.setParamList(list);
		
		//vo.setParamList(paramList);
		
		vo.setProcessingPattern(ProcessingPattern.Asynchronous.getPattern());
		
		vo.setRequestBody("{\"inputName\":\"Junit\"}");
		
		vo.setTransactionId("txn1234");
		
		return vo;
		
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
}
