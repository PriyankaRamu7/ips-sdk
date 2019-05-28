package com.hpe.sis.sie.fe.ips.processing.client;

import okhttp3.OkHttpClient;

public class HttpClient {

	private static OkHttpClient client;

	private HttpClient() {
	}

	public static synchronized OkHttpClient getInstance() {
		if (HttpClient.client == null) {
			HttpClient.client = new OkHttpClient();
		}
		return HttpClient.client;
	}

}
