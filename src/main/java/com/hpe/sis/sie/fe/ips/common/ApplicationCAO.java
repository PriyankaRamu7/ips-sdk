package com.hpe.sis.sie.fe.ips.common;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.hpe.sis.sie.fe.dss.DSSService;
import com.hpe.sis.sie.fe.dss.exception.SISException;
import com.hpe.sis.sie.fe.ips.auth.constants.AuthConstants;
import com.hpe.sis.sie.fe.ips.auth.exception.AuthException;
import com.hpe.sis.sie.fe.ips.utils.auth.model.Applications;
import com.hpe.sis.sie.fe.ips.utils.snmp.constants.SNMPConstants;

public class ApplicationCAO {

	private static Logger log = LoggerFactory.getLogger(ApplicationCAO.class);
	private static boolean isDebugEnabled = log.isDebugEnabled();
	private static boolean isInfoEnabled = log.isInfoEnabled();
	private static Gson gson = new Gson();

	public ApplicationCAO() {

	}

	public static void initializeDSSProperties(String dssVo, String componentName)
			throws SISException {
		DSSService.initialize(dssVo, componentName);
	}

	public static Applications getApplicationData(String appId) throws AuthException {
		if (isInfoEnabled)
			log.info("ServiceMappingCAO : getServiceMapping] Entry. appId :" + appId);
		if (isDebugEnabled)
			log.debug("Obtained appId : " + appId);
		String dsskey = AuthConstants.APPLICATION_NODE + AuthConstants.DELIMITER + appId;
		Applications applications = getApplicationDSSData(dsskey);
		return applications;
	}
	
	private static Applications getApplicationDSSData(String dsskey) throws AuthException {
		if (isInfoEnabled)
			log.info("ServiceMappingCAO : getApplicationDSSData] Entry. key :" + dsskey);
		Applications applications = null;
		List<String> parameterList = new ArrayList<String>();
		try {
			log.debug("Obtaining jedis from pool ***");

			// Pipeline pipe = jedis.pipelined();
			String applicationJson = DSSService.getValue(dsskey);

			log.debug("key : " + dsskey + " ***applicationJson :" + applicationJson);
			if (isDebugEnabled)
				log.debug("ServiceMappingCAO : getApplicationDSSData] applicationJson :" + applicationJson);
			if (applicationJson != null) {
				applications = gson.fromJson(applicationJson, Applications.class);
			} else {
				throw new AuthException(AuthConstants.INVALID_APPID_ERR_CODE, IPSConfig.errorMsgProperties.getProperty(AuthConstants.INVALID_APPID_ERR_CODE));
			}
			// pipe.sync();
		} catch (com.hpe.sis.sie.fe.dss.exception.SISException e) {
			e.printStackTrace();
			log.error("Exception in redis processing : " + e.getMessage());
			if (e.getMessage().contains("Unexpected end of stream")) {
				log.error(
						"ServiceMappingCAO:chkin smembers: caught an unexpected end of stream error as the jedis connection was closed");
			}
			throw new AuthException(AuthConstants.INVALID_APPID_ERR_CODE, IPSConfig.errorMsgProperties.getProperty(AuthConstants.INVALID_APPID_ERR_CODE));
		} catch (AuthException e) {

			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in redis processing : " + e.getCause());
			throw new AuthException(AuthConstants.INVALID_APPID_ERR_CODE, IPSConfig.errorMsgProperties.getProperty(AuthConstants.INVALID_APPID_ERR_CODE));
		}

		if (isInfoEnabled)
			log.info("ServiceMappingCAO : getServiceMapping] Exit.");

		return applications;
	}

}
