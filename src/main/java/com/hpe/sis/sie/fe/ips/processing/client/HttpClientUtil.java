package com.hpe.sis.sie.fe.ips.processing.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.sis.sie.fe.ips.common.HttpClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	static OkHttpClient client = new OkHttpClient();

	public static String run(final String url, final OkHttpClient client, String beSecretToken, String transactionId)
			throws IOException {

		logger.debug("run():TxID:" + transactionId + " Complete task url is:" + url);
		String responseBody = null;
		try {
			logger.debug("complete task url is:" + url);
			Request request = new Request.Builder().url(url).addHeader("X-SIEBE-TOKEN", beSecretToken)
					.addHeader("transactionId", transactionId).build();

			long startTime = System.currentTimeMillis();
			Response response = client.newCall(request).execute();
			long endTime = System.currentTimeMillis();
			logger.debug("run():TxID:" + transactionId + "Time Taken to fetch response from Backend : "
					+ (endTime - startTime));

			if (response != null) {
				logger.info("run():TxID:" + transactionId + " HTTP response status is:" + response.code());
				ResponseBody res = response.body();
				responseBody = res.string();
				res.close();
			}
		} catch (IOException | IllegalArgumentException | IllegalStateException exception) {
			exception.printStackTrace(); // TODO
			throw exception;
		}
		return responseBody;
	}

	public static Response run(final String url, String beSecretToken, String transactionId)
			throws IOException, IllegalArgumentException, IllegalStateException {
		Response response = null;
		logger.debug("run():TxID:" + transactionId + " Complete task url is:" + url);
		String responseBody = null;
		try {
			logger.debug("complete task url is:" + url);
			Request request = new Request.Builder().url(url).addHeader("X-SIEBE-TOKEN", beSecretToken)
					.addHeader("transactionId", transactionId).build();

			client = HttpClient.getHttpClient();
			if (client != null) {
				long startTime = System.currentTimeMillis();
				response = client.newCall(request).execute();
				long endTime = System.currentTimeMillis();
				logger.info("run():TxID:" + transactionId + " Time Taken to get response from Backend service : "
						+ (endTime - startTime));

			} else {
				System.out.println("OkHttpClient instance cannot be null" + client); // TODO
			}

		} catch (IOException | IllegalArgumentException | IllegalStateException exception) {
			exception.printStackTrace(); // TODO
			throw exception;
		}
		return response;

	}

}
