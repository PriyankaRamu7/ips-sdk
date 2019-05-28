package com.hpe.sis.sie.fe.ips.auth.test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Test;

import com.google.gson.Gson;
import com.hpe.sis.sie.fe.dss.exception.SISException;
import com.hpe.sis.sie.fe.ips.auth.actors.IPSSecurityActor;
import com.hpe.sis.sie.fe.ips.auth.constants.AuthConstants;
import com.hpe.sis.sie.fe.ips.auth.exception.IPSException;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthErrorResponse;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthRequest;
import com.hpe.sis.sie.fe.ips.auth.messages.AuthSuccessResponse;
import com.hpe.sis.sie.fe.ips.common.ApplicationCAO;
import com.hpe.sis.sie.fe.ips.common.HttpClient;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.auth.model.AuthVO;
import com.hpe.sis.sie.fe.ips.utils.auth.model.VirtualObjects;
import com.hpe.sis.sie.fe.ips.utils.configuration.dao.ChannelConfig;
import com.hpe.sis.sie.fe.ips.utils.sieconfiguration.service.SIEConfigurationService;
import com.hpe.sis.sie.fe.ips.utils.sieconfiguration.vo.SIEConfigurationVO;
import com.typesafe.config.Config;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;
import akka.testkit.javadsl.TestKit;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IpsSecurityActorTest {

	static SisIpsActorSystem ipsSystem;
	static ActorSystem system;
	static Config actorConfig;
	
	static TestKit testKit;
	static TestProbe testProbe;
	
	private static TestActorRef<IPSSecurityActor> ref = null;
	private static AuthRequest authRequest = null;
	private String authResponse = null;
	private Object response = null;
	private CompletableFuture<Object> future = null;

	@BeforeClass
	public static void setup() {
		ipsSystem = SisIpsActorSystem.getInstance();
		system = ipsSystem.getIpsActorSystem();
		
		testKit = new TestKit(system);
		testProbe = TestProbe.apply(system);
		
		final Props props = Props.create(IPSSecurityActor.class);
		ref = TestActorRef.create(system, props);
		authRequest = createData();
		createConfigData();
		createHttpClient();
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
		config.setChannelName("ic-w3c-activitystreams");
		config.setBackendResponseDataTTL(25);
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
	public void test01AuthSuccess() throws InterruptedException, ExecutionException {
		
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Success check: " + authResponse);
		assertEquals(authResponse, AuthConstants.AUTH_SUCCESS);
	}

	@Test
	public void test02InvalidAppId() throws InterruptedException, ExecutionException {

		authRequest.getAuthVO().setAppId(null);
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setAppId("d66ad2f0-1094-48d8-8e4d-e1d5224a6a76"); //restoring appId for further testcases
		assertEquals(authResponse, AuthConstants.INVALID_APPID_ERR_CODE);
	}
	@Test
	public void test03ApiKeyforNull() throws InterruptedException, ExecutionException {

		authRequest.getAuthVO().setApikey(null);
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setApikey("00d3c64d-5ba4-4c62-80cc-cbd9a63e0cd2"); //restoring apiKey for further testcases
		assertEquals(authResponse, AuthConstants.MISSING_APIKEY_IN_REQUEST);
	}

	@Test
	public void test04InvalidApiKey() throws InterruptedException, ExecutionException {

		authRequest.getAuthVO().setApikey("xxxxxxxxxxxxxxx");
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setApikey("00d3c64d-5ba4-4c62-80cc-cbd9a63e0cd2"); //restoring apiKey for further testcases
		assertEquals(authResponse, AuthConstants.APIKEY_MISMATCH_ERR_CODE);
	}
	
	@Test
	public void test05IsAPIKeyAuthRequired() throws InterruptedException, ExecutionException {

		List<VirtualObjects> VOList = authRequest.getApplicationVO().getVirtualObjects();
		VirtualObjects virtualObj = null;
		for(VirtualObjects VO : VOList){
			if(VO.getId().equals(authRequest.getAuthVO().getVirtualObject()))
				virtualObj = VO;
		}
		authRequest.getAuthVO().setAPIKeyAuthRequired(false);
		authRequest.getAuthVO().setApikey("xxxxxxxxxxxxxxx");
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setApikey("00d3c64d-5ba4-4c62-80cc-cbd9a63e0cd2"); //restoring isAPIKeyAuthRequired for further testcases
		authRequest.getAuthVO().setAPIKeyAuthRequired(true);
		assertEquals(authResponse, AuthConstants.AUTH_SUCCESS);
	}
	
	@Test
	public void test06IsApplicationSuspended() throws InterruptedException, ExecutionException {

		authRequest.getApplicationVO().setSuspended(true);
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getApplicationVO().setSuspended(false);//restoring isApplicationSuspended for further testcases
		assertEquals(authResponse, AuthConstants.APP_SUSPENDED_ERROR_CODE);
	}
	
	
	@Test
	public void test07IsApplicationMarkedAsCompleted() throws InterruptedException, ExecutionException {

		authRequest.getApplicationVO().setComplete(false);
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getApplicationVO().setComplete(true);//restoring isApplicationMarkedAsCompleted for further testcases
		assertEquals(authResponse, AuthConstants.APP_NOT_COMPLETED_ERROR_CODE);
	}
	
	@Test
	public void test08InvalidConversationType() throws InterruptedException, ExecutionException {

		authRequest.getAuthVO().setConversationType(null);
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setConversationType("onem2m");//restoring conversationType for further testcases
		assertEquals(authResponse, AuthConstants.INVALID_CONVERSATION_TYPE_ERR_CODE);
	}
	
	@Test
	public void test09InvalidMethodType() throws InterruptedException, ExecutionException {

		authRequest.getAuthVO().setOperation(null);
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setOperation("POST");//restoring methodType for further testcases
		assertEquals(authResponse, AuthConstants.INVALID_METHOD_TYPE_ERR_CODE);
	}
	
	@Test
	public void test10InvalidMethodType() throws InterruptedException, ExecutionException {

		authRequest.getAuthVO().setOperation("GET");
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setOperation("POST");//restoring methodType for further testcases
		assertEquals(authResponse, AuthConstants.AUTHORTIZATION_METHOD_NOT_ALLOWED_ERR_CODE);
	}
		
	@Test
	public void test11InvalidSmartKeyNullCheck() throws InterruptedException, ExecutionException {

		authRequest.getAuthVO().setSmartkey(null);
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setSmartkey("d29bce1f-5016-445d-baff-e616fbac9ee0");//restoring smartKey for further testcases
		assertEquals(authResponse, AuthConstants.INVALID_SMARTKEY_ERR_CODE);
	}
	
	@Test
	public void test12InvalidSmartKey() throws InterruptedException, ExecutionException {

		authRequest.getAuthVO().setSmartkey("xxxxxxxxxxxxxxx");
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setSmartkey("d29bce1f-5016-445d-baff-e616fbac9ee0");//restoring smartKey for further testcases
		assertEquals(authResponse, AuthConstants.SMARTKEY_MISMATCH_ERR_CODE);
	}
	
	@Test
	public void test13InvalidPlatformType() throws InterruptedException, ExecutionException {

		authRequest.getAuthVO().setPlatform(null);;
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setPlatform("Windows NT");//restoring paltformType for further testcases
		assertEquals(authResponse, AuthConstants.INVALID_PLATFORM_TYPE_ERR_CODE);
	}
	
	@Test
	public void test14InvalidOBE() throws InterruptedException, ExecutionException {

		authRequest.getApplicationVO().setObeId(null);
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getApplicationVO().setObeId("1008cbb6-fa7e-4377-b411-47b43fa1f484");//restoring obeId for further testcases
		assertEquals(authResponse, AuthConstants.OBE_CONFIGURATION_NOT_FOUND_ERR_CODE);
	}
	
	@Test
	public void test15CheckIfRequestMadeFromConsole() throws InterruptedException, ExecutionException {
		
		authRequest.getAuthVO().setRequestMadeFromConsole(true);
		authRequest.getAuthVO().setSmartkey("xxxxxxxxxxxxxxx"); // Smart Key validation is not done if RequestMadeFromConsole
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setSmartkey("d29bce1f-5016-445d-baff-e616fbac9ee0");//restoring smartKey for further testcases
		assertEquals(authResponse, AuthConstants.AUTH_SUCCESS);
	}
	
	@Test
	public void test16CheckIfRequestMadeFromConsoleInvalidDeployId() throws InterruptedException, ExecutionException {
		
		authRequest.getAuthVO().setRequestMadeFromConsole(true);
		authRequest.getAuthVO().setChannelDeploymentId("XXXXXXXXXXXX");
		authRequest.getAuthVO().setSmartkey("xxxxxxxxxxxxxxx"); // Smart Key validation is not done if RequestMadeFromConsole
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getAuthVO().setSmartkey("d29bce1f-5016-445d-baff-e616fbac9ee0");//restoring smartKey for further testcases
		authRequest.getAuthVO().setChannelDeploymentId("468c7695-aa27-4683-9f29-7f0e696d4456");
		assertEquals(authResponse, AuthConstants.DEPLOYMENT_ID_MISSMATCH_ERR_CODE);
	}
	@Test
	public void test17CheckApplicationValidity() throws InterruptedException, ExecutionException {

		authRequest.getApplicationVO().getValidity().setStartTime(1499409970);
		authRequest.getApplicationVO().getValidity().setEndTime(1499509970);
		future = PatternsCS.ask(ref, authRequest, 5000).toCompletableFuture();	
		response = future.get();
		if (response instanceof AuthSuccessResponse) {
			authResponse = ((AuthSuccessResponse) response).getResponse();
		} else if (response instanceof AuthErrorResponse) {
			authResponse = ((AuthErrorResponse) response).getErrorCode();
		}
		System.out.println("Response from Authentication and Authorization for Failure check: " + authResponse);
		authRequest.getApplicationVO().getValidity().setEndTime(1504204140);//restoring applicationValidity for further testcases
		assertEquals(authResponse, AuthConstants.APPLICATION_VALIDITY_ERROR_CODE);
	}
	
	public static AuthRequest createData() {
		AuthRequest authRequest = new AuthRequest();

		String applicationJson = "{\n  \"id\": \"d66ad2f0-1094-48d8-8e4d-e1d5224a6a76\",\n  \"obeId\": \"1008cbb6-fa7e-4377-b411-47b43fa1f484\",\n  \"deployId\": \"468c7695-aa27-4683-9f29-7f0e696d4456\",\n  \"name\": \"JunitTestApp\",\n  \"description\": \"\",\n  \"longDescription\": \" \",\n  \"version\": \"1.3.0\",\n  \"createdBy\": \"priyankar\",\n  \"createdAt\": 1499409757201,\n  \"modifiedBy\": \"priyankar\",\n  \"modifiedAt\": 1499409757201,\n  \"userRole\": \"OBE-ADMIN\",\n  \"legal\": \" \",\n  \"tags\": [\n    \"\"\n  ],\n  \"validity\": {\n    \"startTime\": 1499409970139,\n    \"endTime\": 1546174692000\n  },\n  \"isTaxonomy\": false,\n  \"isComplete\": true,\n  \"isSuspended\": false,\n  \"smartKeys\": [\n    {\n      \"id\": \"d29bce1f-5016-445d-baff-e616fbac9ee0\",\n      \"name\": \"TestAuth\",\n      \"description\": \"\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499410432869,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410558297,\n      \"expiryTime\": 1546174692000,\n      \"methods\": [\n        \"POST\"\n      ],\n      \"txType\": \"onem2m\",\n      \"platforms\": [\n        \"NA\"\n      ],\n      \"dayOfWeek\": [\n        \"Monday\",\n        \"Tuesday\",\n        \"Wednesday\",\n        \"Thursday\",\n        \"Friday\",\n        \"Saturday\",\n        \"Sunday\"\n      ],\n      \"activeTimeStart\": \"00:00:00\",\n      \"activeTimeEnd\": \"23:59:00\",\n      \"userRole\": \"OBE-ADMIN\"\n    }\n  ],\n  \"apiKeys\": [\n    {\n      \"id\": \"00d3c64d-5ba4-4c62-80cc-cbd9a63e0cd2\",\n      \"name\": \"TestAuth\",\n      \"description\": \"\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499410379619,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410379619,\n      \"userRole\": \"OBE-ADMIN\",\n      \"expiryTime\": 1514528560000,\n      \"isAppSuspended\": false\n    }\n  ],\n  \"properties\": [],\n  \"virtualObjects\": [\n    {\n      \"id\": \"d7098c48-0821-4053-8a2f-477b6c8d877c\",\n      \"name\": \"TestVO\",\n      \"description\": \"desc\",\n      \"version\": \"1.3.0\",\n      \"type\": \"Thing\",\n      \"createdBy\": \"priyankar\",\n      \"createdAt\": 1499409945156,\n      \"modifiedBy\": \"priyankar\",\n      \"modifiedAt\": 1499410326034,\n      \"smartKeys\": [\n        \"d29bce1f-5016-445d-baff-e616fbac9ee0\"\n      ],\n      \"attributes\": [\n        {\n          \"name\": \"inputName\",\n          \"type\": \"String\",\n          \"mandatory\": true\n        }\n      ],\n      \"isAPIKeyAuthRequired\": true,\n      \"operations\": [\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"responseCache\": {\n            \"enabled\": true,\n            \"ttl\": 25000\n          },\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"GET\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"POST\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"PUT\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"PATCH\"\n        },\n        {\n          \"resourceUrl\": \"/sis/sie/api/v1/applications/d66ad2f0-1094-48d8-8e4d-e1d5224a6a76/virtualobjects/d7098c48-0821-4053-8a2f-477b6c8d877c\",\n          \"tinyresourceUrl\": \"/AyPTno7\",\n          \"processingPattern\": \"RequestResponse\",\n          \"task\": {\n            \"id\": \"com.hpe.clasp.example.helloWorld\",\n            \"name\": \"Hello World Service\",\n            \"version\": \"1.0\",\n            \"engine\": \"Java\"\n          },\n          \"timeout\": 25000,\n          \"method\": \"DELETE\"\n        }\n      ],\n      \"activities\": [\n        {\n          \"id\": \"2afead9b-5166-45c2-8db4-aecfdc753463\",\n          \"name\": \"onem2mTest\",\n          \"description\": \"\",\n          \"channelId\": \"onem2m\",\n          \"processing\": {\n            \"pattern\": \"ParallelProcessing\",\n            \"task\": {\n              \"id\": \"com.hpe.clasp.example.helloWorld\",\n              \"name\": \"Hello World Service\",\n              \"version\": \"1.0\",\n              \"engine\": \"Java\"\n            },\n            \"activityMap\": []\n          },\n          \"properties\": [],\n          \"timeout\": 25000,\n          \"tinyuri\": \"AyPTno8\",\n          \"schema\": \"\",\n          \"isAPIKeyAuthRequired\": true\n        },\n        {\n          \"id\": \"b961488c-ad6d-0a5e-e043-052985e41147\",\n          \"name\": \"callNotifyTest\",\n          \"description\": \"\",\n          \"channelId\": \"cnActivityStreams\",\n          \"processing\": {\n            \"pattern\": \"ParallelProcessing\",\n            \"task\": {\n              \"id\": \"com.hpe.clasp.example.helloWorld\",\n              \"name\": \"Hello World Service\",\n              \"version\": \"1.0\",\n              \"engine\": \"Java\"\n            },\n            \"activityMap\": []\n          },\n          \"properties\": [],\n          \"timeout\": 25000,\n          \"tinyuri\": \"AyPTno9\",\n          \"schema\": \"\",\n          \"isAPIKeyAuthRequired\": true\n        }\n      ]\n    }\n  ]\n}";
		
		Gson gson = new Gson();
		Applications applicationVO = null;
		String resourceUri=null;
		applicationVO = gson.fromJson(applicationJson, Applications.class);
		authRequest.setApplicationVO(applicationVO);
		AuthVO authVO = new AuthVO();
		authVO.setApikey("00d3c64d-5ba4-4c62-80cc-cbd9a63e0cd2");
		authVO.setAppId("d66ad2f0-1094-48d8-8e4d-e1d5224a6a76");
		authVO.setAppOwnerRole("");
		authVO.setChannelDeploymentId("468c7695-aa27-4683-9f29-7f0e696d4456");
		authVO.setConversationType("onem2m");
		authVO.setOperation("POST");
		authVO.setPlatform("Windows NT");
		authVO.setSmartkey("d29bce1f-5016-445d-baff-e616fbac9ee0");
		authVO.setVirtualObject("d7098c48-0821-4053-8a2f-477b6c8d877c");
		authVO.setChannelName("ic-vo-operations");
		authVO.setAPIKeyAuthRequired(true);
		authVO.setResourceUri(resourceUri);
		authVO.setRequestedTime(System.currentTimeMillis());
		authRequest.setAuthVO(authVO);
		authRequest.setTransactionId("1234");
		
		return authRequest;
	}
	
	static class TestConfig extends ChannelConfig {
		public TestConfig() {
			super();
		}
	}

}
