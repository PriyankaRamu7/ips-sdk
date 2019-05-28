package com.hpe.sis.sie.fe.ips.scheduler.test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

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
import com.hpe.sis.sie.fe.ips.scheduler.actors.IPSSchedulerManager;
import com.hpe.sis.sie.fe.ips.scheduler.constants.SchedulerConstants;
import com.hpe.sis.sie.fe.ips.scheduler.model.ScheduleJob;
import com.hpe.sis.sie.fe.ips.scheduler.model.TaskDetails;
import com.hpe.sis.sie.fe.ips.scheduler.service.IPSScheduler;
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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SchedulerTest {

	static SisIpsActorSystem ipsSystem;
	static ActorSystem system;
	static Config actorConfig;
	
	private static TestKit testKit;
	private static TestActorRef<SynchronousActor> refSync;
	private static TestActorRef<AsynchronousActor> refAsync;
	private static TestActorRef<NotifyActor> refNotify;
	private static TestProbe testProbe;
	private static ScheduleJob scheduleJob;
	private static IPSScheduler ipsScheduler;
	
	@BeforeClass
	public static void setup() {
		actorConfig = ConfigFactory.load();
		ipsSystem = SisIpsActorSystem.getInstance();
		system = ipsSystem.getIpsActorSystem();
		
		testKit = new TestKit(system);
		testProbe = TestProbe.apply(system);

		refAsync = TestActorRef.create(system,
				Props.create(IPSSchedulerManager.class));
		createConfigData();
		createHttpClient();
		scheduleJob = createScheduleJobData();
		ipsScheduler = IPSScheduler.build();
		
		IPSConfig.CHANNEL_NAME = "bot-scheduler";
		IPSConfig.ACTOR_TIMEOUT = 20;
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
		try {
			ApplicationCAO.initializeDSSProperties(SIEConfigurationVO.getDssConfigJson(), "bot-scheduler");
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
	public void test01Schedule() throws IPSException {
		String result = ipsScheduler.schedule(scheduleJob);
		assertEquals(result, SchedulerConstants.SUCCESS);
	}

	@Test
	public void test02Pause() throws IPSException {
		String result = ipsScheduler.pause(scheduleJob.getJobId());
		assertEquals(result, SchedulerConstants.SUCCESS);
	}
	
	//@Test
	public void test03Resume() throws IPSException {
		String result = ipsScheduler.resume(scheduleJob.getJobId());
		assertEquals(result, SchedulerConstants.SUCCESS);
	}
	
	//@Test
	public void test03Delete() throws IPSException {
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = ipsScheduler.delete(scheduleJob.getJobId());
		assertEquals(result, SchedulerConstants.SUCCESS);
	}
	
	public static ScheduleJob createScheduleJobData(){
		ScheduleJob scheduleJob = new ScheduleJob();
		List<TaskDetails> taskList = new ArrayList<>();
		scheduleJob.setAppId("1103d433-54aa-4f9c-9a48-48829d8829d6");
		scheduleJob.setCronPattern("");
		scheduleJob.setEndDate(null);
		scheduleJob.setFrequencyInterval(0);
		scheduleJob.setJobId("JunitTestForScheduler");
		scheduleJob.setJobType("onetime");
		scheduleJob.setObeId("95d63737-2ab5-4119-9a9e-19c216bcac6b");
		scheduleJob.setRemoteHost("127.0.0.1");
		scheduleJob.setRepeatitionCount(0);
		scheduleJob.setRequestedTime(new Date().getTime());
		scheduleJob.setRetryCount(0);
		scheduleJob.setServiceId("com.hpe.clasp.example.helloWorld");
		scheduleJob.setStartDate(getDateFromStr("25-11-2017 21:41:00 IST"));
		TaskDetails taskDetails = new TaskDetails();
		taskDetails.setTaskDataJson(""); // TODO
		taskDetails.setTaskHeader("{\"mode\": \"synchronous\",\"services\": [{\"id\":\"com.hpe.clasp.example.helloWorld\", \"version\":\"1.0\", \"engine\":\"Java\"}]}");
		taskDetails.setTransactionId("JunitTestTransactionId");
		taskList.add(taskDetails);
		scheduleJob.setTaskDataList(taskList);
		return scheduleJob;
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
			ApplicationCAO.initializeDSSProperties(SIEConfigurationVO.getDssConfigJson(), "bot-scheduler");
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
		config.setChannelName("bot-scheduler");
		config.setBackendResponseDataTTL(25);
		config.setLogdelimiter('|');
		config.setLogBEServiceData(true);
		config.setTransactionFields("TRANSACTIONID,REQUESTEDTIME,LOCALHOST,REMOTEHOST,USER_NAME,APPLICATION,VIRTUAL_OBJECTS,SERVICE_ID,BOT_ID,INTERACTION_CHANNEL,INTERACTION_API_RESULTCODE,INTERACTION_API_ERROR_DETAIL,BACKEND_INVOCATION_STATUS,INTERACTION_API_RESPONSETIME,BACKEND_INVOCATION_TASKID,BACKEND_RESPONSETIME,INTERACTION_API_REQUEST,BACKEND_RESPONSE,INTERACTION_API_METHODTYPE,IS_TEST_REQUEST,OBE_ID,X_API4SAAS_TOKEN");
		config.setTransactionLogEnabled(true);
		config.setSecurityAuditEnabled(true);
		config.setActorTimeOut(20);
		config.setTracingLogEnabled(true);
		config.setTraceLogTimeStampFormat("yyyy-MM-dd HH:mm:ss.SSS");
		config.setTraceFields("TRANSACTIONID,EVENT,EVENT_TIME,METHOD,ACTOR_PATH,STATUS,ERROR_DETAIL,BOT_ID");
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
	public static Date getDateFromStr(String dateStr) {
		try {
			String format = "dd-MM-yyyy HH:mm:ss z";
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

