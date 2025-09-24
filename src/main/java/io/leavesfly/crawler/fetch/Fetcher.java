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

/**
 * 抽象获取器类
 * 提供网页获取的基础功能
 * 
 * @author yefei.yf
 */
public abstract class Fetcher {

	public static final HttpClient httpClient = new HttpClient();
	private static final Logger logger = LoggerFactory.getLogger(Fetcher.class);

	static {
		initializeHttpClient();
	}
	
	/**
	 * 初始化HTTP客户端
	 */
	private static void initializeHttpClient() {
		MultiThreadedHttpConnectionManager mutiThreadConnectionManager = new MultiThreadedHttpConnectionManager();
		httpClient.setHttpConnectionManager(mutiThreadConnectionManager);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(
				FetcherConstant.HTTP_CONN_TIMEOUT);
		
		// 尝试设置代理（如果有的话）
		ProxyItem proxyItem = ProxyUtil.nextProxyItem();
		if (proxyItem != null) {
			setProxy(proxyItem);
			logger.info("使用代理: {}:{}", proxyItem.getProxyIP(), proxyItem.getProxyPort());
		} else {
			logger.info("未配置代理，使用直连");
		}
	}

	/**
	 * 设置代理
	 * 
	 * @param proxyItem 代理项
	 */
	public static void setProxy(ProxyItem proxyItem) {
		if (proxyItem == null) {
			logger.warn("试图设置空代理");
			return;
		}
		
		// 设置代理服务器的ip地址和端口
		httpClient.getHostConfiguration()
				.setProxy(proxyItem.getProxyIP(), proxyItem.getProxyPort());
		// 使用抢占式认证
		httpClient.getParams().setAuthenticationPreemptive(true);
		
		logger.debug("已设置代理: {}:{}", proxyItem.getProxyIP(), proxyItem.getProxyPort());
	}

	/**
	 * 获取网页
	 * 
	 * @param url 目标URL
	 * @return 原始页面数据
	 */
	public RawPage fetchPage(String url) {
		RawPage rawPage = downloadPage(url);
		if (rawPage == null) {
			logger.error("URL: {} -> 下载失败!", url);
			return null;
		}
		
		int storedState = storePage(rawPage);
		if (storedState == RawPageStore.STORE_FAILURE) {
			logger.error("URL: {} -> 存储失败!", url);
		}
		return rawPage;
	}

	/**
	 * 下载页面（子类实现）
	 * 
	 * @param url 目标URL
	 * @return 原始页面数据
	 */
	public abstract RawPage downloadPage(String url);

	/**
	 * 存储页面（子类实现）
	 * 
	 * @param rawPage 原始页面数据
	 * @return 存储状态
	 */
	public abstract int storePage(RawPage rawPage);
}