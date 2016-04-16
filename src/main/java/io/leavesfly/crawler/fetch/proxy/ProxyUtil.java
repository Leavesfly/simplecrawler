package io.leavesfly.crawler.fetch.proxy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import io.leavesfly.crawler.constant.FetcherConstant;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

public class ProxyUtil {

	private static final String proxyFilePath = FetcherConstant.PROXY_LIST_FILE_PATH;
	private static final List<ProxyItem> proxyItemList = new LinkedList<ProxyItem>();
	private static int currentPosition = 0;
	private static final String checkProxyItemURL = FetcherConstant.CHECK_PROXY_ITEM_VALID_URL;
	private static final HttpClient httpClient = new HttpClient();
	private static final GetMethod method = new GetMethod(checkProxyItemURL);
	public static int proxyGetPageNum = 0;

	static {
		try {
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(proxyFilePath)));
			// TODO ��ȡ�����ļ��ɶ���

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean isValidProxyItem(ProxyItem proxyItem) {
		// ���ô����������ip��ַ�Ͷ˿�
		httpClient.getHostConfiguration()
				.setProxy(proxyItem.getProxyIP(), proxyItem.getProxyPort());
		// ʹ��������֤
		httpClient.getParams().setAuthenticationPreemptive(true);

		try {
			int statusCode = httpClient.executeMethod(method);
			if (statusCode == HttpStatus.SC_OK) {
				return true;
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static ProxyItem nextProxyItem() {
		ProxyItem proxyItem;
		proxyItem = proxyItemList.get(currentPosition % proxyItemList.size());
		++currentPosition;

		return proxyItem;

	}
}
