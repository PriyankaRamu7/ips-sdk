/*******************************************************************************
 * © Copyright 2017 Hewlett Packard Enterprise Development LP. All Rights Reserved.
 * An unpublished and CONFIDENTIAL work. Reproduction,
 * adaptation, or translation without prior written permission
 * is prohibited except as allowed under the copyright laws.
 * ---------------------------------------------------------------------------
 * Project: SISv1.3
 * Module: SIS IPS
 * Author: HPE SIS Team
 * Organization: Hewlett Packard Enterprise
 * Revision: 1.0
 * Date: 20/07/2017
 * Contents: IPSConfig.java
 * ---------------------------------------------------------------------------
 ******************************************************************************/
package com.hpe.sis.sie.fe.ips.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.dss.exception.SISException;
import com.hpe.sis.sie.fe.ips.auth.exception.IPSException;
import com.hpe.sis.sie.fe.ips.common.actorsystem.SisIpsActorSystem;
import com.hpe.sis.sie.fe.ips.scheduler.constants.SchedulerConstants;
import com.hpe.sis.sie.fe.ips.tracer.Tracer;
import com.hpe.sis.sie.fe.ips.tracer.TracingUtil;
import com.hpe.sis.sie.fe.ips.utils.configuration.dao.ChannelConfig;
import com.hpe.sis.sie.fe.ips.utils.sieconfiguration.service.SIEConfigurationService;
import com.hpe.sis.sie.fe.ips.utils.sieconfiguration.vo.SIEConfigurationVO;
import com.hpe.sis.sie.fe.sisutils.channel.util.ChannelUtility;

import akka.actor.ActorRef;

/**
 * Class that initializes and configures the SIS Interaction Processing Subsystem(IPS) with parameters required to perform its functions.
 *
 */
public class IPSConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(IPSConfig.class);
	
	private IPSConfig() {
		
	}
	
	
	/**
	 * Configures IPS with the channel configuration and initializes it. This is the first API to be used/invoked in the channel implementations
	 * @since SIS v1.3
	 * @param channelConfig
	 * @throws IPSException
	 */
	public static void initializeConfig(ChannelConfig channelConfig) throws IPSException {
		READ_TIMEOUT = channelConfig.getReadTimeout();
		WRITE_TIMEOUT = channelConfig.getWriteTimeout();
		CONNECTION_TIMEOUT = channelConfig.getConnectionTimeout();
		MAX_IDLE_CONNECTIONS = channelConfig.getMaxIdleConnections();
		KEEP_ALIVE_DURATION = channelConfig.getKeepAliveDuration();
		CLASP_QUEUE_CONF = channelConfig.getClaspQueueConf();
		CHANNEL_NAME = channelConfig.getChannelName();
		BE_RESPONSE_TTL = channelConfig.getBackendResponseDataTTL();
		TRANSACTION_FIELDS = channelConfig.getTransactionFields();
		SECURITY_AUDIT_FIELDS = channelConfig.getSecurityAuditFields();
		INTERACTION_CONTEXT_FIELDS = channelConfig.getInteractionContextFields();
		LOG_BE_SERVICE_DATA = channelConfig.isLogBEServiceData();
		LOG_DELIMITER = channelConfig.getLogdelimiter();
		DEPLOYMENT_ID = channelConfig.getDeploymentId();
		HOST_IP = channelConfig.getHost();
		TRANSACTION_LOG_ENABLED = channelConfig.isTransactionLogEnabled();
		SECURITY_AUDIT_ENABLED = channelConfig.isSecurityAuditEnabled();
		ACTOR_TIMEOUT = channelConfig.getActorTimeOut();
		
		TRACING_ENABLED = channelConfig.isTracingLogEnabled();
		TRACE_TIMESTAMP_FORMAT = channelConfig.getTraceLogTimeStampFormat();
		TRACE_FIELDS = channelConfig.getTraceFields();
		SAVE_ACTIVITYSTREAM_BYTIME = channelConfig.isSaveActivitystreamByTime();
		INTERACTION_CONTEXT_TTL = channelConfig.getInteractionContextTTL();
		
		try {
			HttpClient.createHttpClient();
			SisIpsActorSystem.getInstance();
			
			DESIGNER_API_URL = channelConfig.getDesignerApiUrl();
		
			if (sieConfigurationVO == null) {
				sieConfigurationVO = SIEConfigurationService.getSIEConfigurationVO(SIEConfigurationService.buildSIEURL(DESIGNER_API_URL, DEPLOYMENT_ID, CHANNEL_NAME,HOST_IP));
			}
			
			ApplicationCAO.initializeDSSProperties(sieConfigurationVO.getDssConfigJson(), ChannelUtility.getSNMPComponentNameForChannel(channelConfig.getChannelName()));
			loadProperties();
			if(CHANNEL_NAME.equals(ChannelUtility.BOT_SCHEDULER)) {
				SisIpsActorSystem.getInstance().clusterSingletonProxy.tell(SchedulerConstants.RESCHEDULE_ON_STARTUP, ActorRef.noSender());
			}
			
			Tracer.initializeTracer();
			TracingUtil.initialize();
		} catch (SISException e) {
			LOGGER.error(e.getMessage(), e);
			throw new IPSException(errorMsgProperties.getProperty(IPSConstants.IPS_INIT_ERROR) + e.getMessage(), IPSConstants.IPS_INIT_ERROR);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new IPSException(errorMsgProperties.getProperty(IPSConstants.IPS_INIT_ERROR) + e.getMessage(), IPSConstants.IPS_INIT_ERROR);
		}
	}
	
	/**
	 * Default read timeout for new connections. A value of 0 means no timeout, otherwise values must be between 1 and Integer.MAX_VALUE when converted to milliseconds.
	 * This is internally set as socket timeout for receive data on HTTP connections to SIE: BE
	 */
	public static int READ_TIMEOUT;
	
	
	/**
	 * Default write timeout for new connections. A value of 0 means no timeout, otherwise values must be between 1 and Integer.MAX_VALUE when converted to milliseconds.
	 * This is internally set as socket timeout for write data on HTTP connections to SIE: BE
	 */
	public static int WRITE_TIMEOUT;
	
	/**
	 * Default connect timeout for new connections. A value of 0 means no timeout, otherwise values must be between 1 and Integer.MAX_VALUE when converted to milliseconds
	 */
	public static int CONNECTION_TIMEOUT;
	
	/**
	 * Maximum number of idle connections in the HTTP connection pool for SIE: BE task invocations
	 */
	public static int MAX_IDLE_CONNECTIONS;
	
	/**
	 * Maximum time (in seconds) after which idle HTTP connections are evicted from the connection pool
	 */
	public static int KEEP_ALIVE_DURATION;
	
	/**
	 * SIE:BE task-engine URI
	 */
	public static String CLASP_QUEUE_CONF;
	
	/**
	 * Inbound interaction channel name
	 */
	public static String CHANNEL_NAME;
	
	/**
	 * Time to live (in seconds) for the responses of Asynchronous interaction requests saved in DSS
	 */
	public static int BE_RESPONSE_TTL;
	
	/**
	 * Field names for Transaction log
	 */
	public static String TRANSACTION_FIELDS;
	
	/**
	 * Field names for SecurityAudit log
	 */
	public static String SECURITY_AUDIT_FIELDS;
	
	/**
	 * Field names for InteractionContext log
	 */
	public static String INTERACTION_CONTEXT_FIELDS;
	/**
	 * 
	 */
	public static boolean LOG_BE_SERVICE_DATA;
	
	/**
	 * Delimiter for Transaction logs
	 */
	public static char LOG_DELIMITER;
	
	/**
	 * 
	 */
	public static String PERSISTENT_TYPE;
	
	/**
	 * 
	 */
	public static String PERSISTENT_TIME_INTERVAL;
	
	/**
	 * SID-API URL used to fetch the Multi-tenancy data.
	 */
	public static String DESIGNER_API_URL;
	
	/**
	 * Deployment-ID for this Channel instance. The channel could be a part of Shared or Dedicated deployment. Obtain the deployment id from SID -> Configuration
	 */
	public static String DEPLOYMENT_ID;
	
	/**
	 * Host-IP of the channel deployment
	 */
	public static String HOST_IP;
	
	public static boolean TRANSACTION_LOG_ENABLED;
	
	public static boolean SECURITY_AUDIT_ENABLED;
	
	public static Properties errorMsgProperties;
	
	public static int ACTOR_TIMEOUT;
	
	public static Boolean TRACING_ENABLED;

	public static String TRACE_TIMESTAMP_FORMAT;
	
	public static String TRACE_FIELDS;
	
	public static boolean SAVE_ACTIVITYSTREAM_BYTIME;
	
	public static int INTERACTION_CONTEXT_TTL;
	
	/**
	 * @see SIEConfigurationVO
	 * Provides access to the inbound interaction channel's deployment data
	 */
	public static SIEConfigurationVO sieConfigurationVO;
	
	private static Properties loadProperties() {
		
		String resourceName = "IpsErrorMsg.properties";
		errorMsgProperties = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
			errorMsgProperties.load(resourceStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return errorMsgProperties;
	}
	
}
