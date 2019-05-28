package com.hpe.sis.sie.fe.ips.responsecache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.dss.DSSService;
import com.hpe.sis.sie.fe.dss.exception.SISException;
import com.hpe.sis.sie.fe.ips.auth.exception.IPSException;
import com.hpe.sis.sie.fe.ips.common.IPSConfig;

public class ResponseCacheUtils {
	
	private static String RESPONSECACHE_PREFIX = "responsecache";
	private static String DSS_DELIMITER = ":";
	
	private static Logger log = LoggerFactory.getLogger(ResponseCacheUtils.class);

	public static String fetchResponseCacheKey(final ResponseCacheVO vo) throws NoSuchAlgorithmException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(vo.getVirtualObjectOrBotId());
		if (StringUtils.isNotEmpty(vo.getRequestURI())) {
			buffer.append(ResponseCacheConstants.SEPARATOR);
			buffer.append(vo.getRequestURI());
		}
		buffer.append(ResponseCacheConstants.SEPARATOR);
		buffer.append(vo.getQueryString());
		String s = buffer.toString();
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(s.getBytes(), 0, s.length());
		byte[] mdbytes = m.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return RESPONSECACHE_PREFIX + DSS_DELIMITER + sb.toString();
	}

	/**
	 * validate for null and empty String
	 * 
	 * @param s
	 * @return boolean
	 */
	public static boolean isNullOrBlank(final String s) {
		return (s == null || s.trim().equals(""));
	}

	public static ResponseCacheVO fetchResponseCacheEntity(final String id, final String requestMethod,
			final String requestBody, long ttl, String requestURI) {
		// For now using only virtual Object ID and request body
		ResponseCacheVO vo = new ResponseCacheVO();
		vo.setVirtualObjectOrBotId(id);
		vo.setQueryString(requestBody);
		vo.setRequestURI(requestURI);
		vo.setTimeToLive(ttl);
		return vo;
	}

	public static boolean isResponseCachingEnabled() {
		boolean result = false;
		/*if (serviceMappingVO.getCacheResponse().equals("true")) {
			result = true;
		}*/
		return result;
	}
	
	public static void insertResponseCacheDetails(ResponseCacheVO vo, String repsonse) throws IPSException, NoSuchAlgorithmException {
		String key = ResponseCacheUtils.fetchResponseCacheKey(vo);
		log.info("Inserting the data in DSS with key :"+ key + " and ttl :"+ vo.getTimeToLive());
		
		try {
			DSSService.setValue(key, repsonse, vo.getTimeToLive());
		} catch (SISException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String fetchResponseCacheContents(ResponseCacheVO vo) throws IPSException, NoSuchAlgorithmException {
		String key = ResponseCacheUtils.fetchResponseCacheKey(vo);
		String result = "";
		try {
			result = DSSService.getValue(key);
		} catch (SISException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static boolean responseCacheContentsExists(ResponseCacheVO vo) throws IPSException, NoSuchAlgorithmException {
		boolean result = false;
		String value = "";
		try {
			value = DSSService.getValue(ResponseCacheUtils.fetchResponseCacheKey(vo));
		} catch (SISException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = ResponseCacheUtils.isNullOrBlank(value);
		return !result;
	}

}
