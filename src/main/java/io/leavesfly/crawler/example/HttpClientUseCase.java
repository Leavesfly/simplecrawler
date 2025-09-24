package io.leavesfly.crawler.example;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpClientUseCase {

	public static void main(String[] args) {
		try {
			getMethonUseCase();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// printHttpClientResult();
	}

	public static void getMethonUseCase() throws HttpException, IOException {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(
				"http://hz.meituan.com/?fromsem=1&utm_campaign=baidu&utm_medium=brand&utm_source=baidu&utm_content=1&utm_term=&_rdt=1");
		client.executeMethod(method);
		System.out.println(method.getStatusLine());
		System.out.println(method.getResponseBodyAsString());
		method.releaseConnection();
	}

	public static void printHttpClientResult() {
		String url = "http://hz.meituan.com/?fromsem=1&utm_campaign=baidu&utm_medium=brand&utm_source=baidu&utm_content=1&utm_term=&_rdt=1";
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod(url);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary
			// data
			System.out.println(new String(responseBody));

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
	}

	public static void multiThreadedHttpConnectionUserCase() throws HttpException, IOException {
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpClient client = new HttpClient(connectionManager);

		GetMethod get = new GetMethod("http://jakarta.apache.org/");
		try {
			client.executeMethod(get);
			// print response to stdout
			System.out.println(get.getResponseBodyAsStream());
		} finally {
			// be sure the connection is released back to the connection
			// manager
			get.releaseConnection();
		}

	}

}
