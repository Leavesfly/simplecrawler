package io.leavesfly.crawler.fetch;

import io.leavesfly.crawler.constant.FetcherConstant;
import io.leavesfly.crawler.fetch.proxy.ProxyItem;
import io.leavesfly.crawler.fetch.store.RawPageStore;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.leavesfly.crawler.domain.RawPage;
import io.leavesfly.crawler.fetch.proxy.ProxyUtil;

public abstract class Fetcher {

	public static final HttpClient httpClient = new HttpClient();
	private static final Logger LOG = LoggerFactory.getLogger(Fetcher.class);

	static {
		MultiThreadedHttpConnectionManager mutiThreadConnectionManager = new MultiThreadedHttpConnectionManager();
		httpClient.setHttpConnectionManager(mutiThreadConnectionManager);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(
				FetcherConstant.HTTP_CONN_TIMEOUT);
		// ��ʼ��ʱ���ô���
		ProxyItem proxyItem = ProxyUtil.nextProxyItem();
		setProxy(proxyItem);

	}

	public static void setProxy(ProxyItem proxyItem) {

		// ���ô����������ip��ַ�Ͷ˿�
		httpClient.getHostConfiguration()
				.setProxy(proxyItem.getProxyIP(), proxyItem.getProxyPort());
		// ʹ��������֤
		httpClient.getParams().setAuthenticationPreemptive(true);
	}

	public RawPage fetchPage(String url) {
		RawPage rawPage = downloadPage(url);
		if (rawPage == null) {
			LOG.error("url:" + url + "--->Download error!");
			return null;
		}
		int storedState = storePage(rawPage);
		if (storedState == RawPageStore.STORE_FAILURE) {
			LOG.error("url:" + url + "--->Store error!");
		}
		return rawPage;
	}

	public abstract RawPage downloadPage(String url);

	public abstract int storePage(RawPage rawPage);
}