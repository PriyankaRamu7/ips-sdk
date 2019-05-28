package com.hpe.sis.sie.fe.ips.common;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {
	
	private static final Logger log = LoggerFactory.getLogger(HttpClient.class);
	
	private static OkHttpClient httpclient;
	
	@SuppressWarnings("static-access")
	public static void createHttpClient() {

		log.info("Entry in createHttpClient");
		OkHttpClient client = new OkHttpClient();
		OkHttpClient.Builder builder= client.newBuilder();
		builder.readTimeout(IPSConfig.READ_TIMEOUT, TimeUnit.SECONDS);
		builder.connectTimeout(IPSConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS);
		builder.writeTimeout(IPSConfig.WRITE_TIMEOUT, TimeUnit.SECONDS);
		ConnectionPool pool = new ConnectionPool(IPSConfig.MAX_IDLE_CONNECTIONS,
				IPSConfig.KEEP_ALIVE_DURATION, TimeUnit.SECONDS);
		builder.connectionPool(pool);
		if(System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyPort") != null) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(System.getProperty("http.proxyHost"),
					Integer.parseInt(System.getProperty("http.proxyPort"))));
			builder.proxy(proxy);
		}
		log.info("Exit in createHttpClient");
		httpclient = builder.build();
		
	}
	
	public static OkHttpClient getHttpClient() {
		return httpclient;
	}
	
}


