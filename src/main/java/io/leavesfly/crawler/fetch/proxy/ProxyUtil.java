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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 代理工具类
 * 提供代理IP的管理和验证功能
 * 
 * @author yefei.yf
 */
public class ProxyUtil {

	private static final Logger logger = LoggerFactory.getLogger(ProxyUtil.class);
	
	private static final String proxyFilePath = FetcherConstant.PROXY_LIST_FILE_PATH;
	private static final List<ProxyItem> proxyItemList = new LinkedList<ProxyItem>();
	private static int currentPosition = 0;
	private static final String checkProxyItemURL = FetcherConstant.CHECK_PROXY_ITEM_VALID_URL;
	private static final HttpClient httpClient = new HttpClient();
	private static final GetMethod method = new GetMethod(checkProxyItemURL);
	public static int proxyGetPageNum = 0;

	static {
		initializeProxyList();
	}
	
	/**
	 * 初始化代理列表
	 */
	private static void initializeProxyList() {
		try {
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(proxyFilePath)));
			String line;
			while ((line = bufferReader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				
				// 假设格式为: IP:PORT
				String[] parts = line.trim().split(":");
				if (parts.length == 2) {
					try {
						String ip = parts[0].trim();
						int port = Integer.parseInt(parts[1].trim());
						proxyItemList.add(new ProxyItem(ip, port));
					} catch (NumberFormatException e) {
						logger.warn("无效的代理格式: {}", line);
					}
				}
			}
			bufferReader.close();
			logger.info("成功加载 {} 个代理", proxyItemList.size());
		} catch (FileNotFoundException e) {
			logger.warn("代理文件不存在: {}", proxyFilePath);
			// 不使用代理，继续运行
		} catch (IOException e) {
			logger.error("读取代理文件失败", e);
		}
	}

	/**
	 * 检查代理是否有效
	 * 
	 * @param proxyItem 代理项
	 * @return 是否有效
	 */
	public static boolean isValidProxyItem(ProxyItem proxyItem) {
		if (proxyItem == null) {
			return false;
		}
		
		httpClient.getHostConfiguration().setProxy(proxyItem.getProxyIP(), proxyItem.getProxyPort());
		httpClient.getParams().setAuthenticationPreemptive(true);

		try {
			int statusCode = httpClient.executeMethod(method);
			if (statusCode == HttpStatus.SC_OK) {
				return true;
			}
		} catch (HttpException e) {
			logger.debug("代理HTTP异常: {}", e.getMessage());
		} catch (IOException e) {
			logger.debug("代理IO异常: {}", e.getMessage());
		}
		return false;
	}

	/**
	 * 获取下一个代理项
	 * 
	 * @return 代理项，如果没有可用代理则返回null
	 */
	public static ProxyItem nextProxyItem() {
		if (proxyItemList.isEmpty()) {
			logger.debug("没有可用的代理");
			return null;
		}
		
		ProxyItem proxyItem = proxyItemList.get(currentPosition % proxyItemList.size());
		++currentPosition;
		return proxyItem;
	}
	
	/**
	 * 获取代理列表大小
	 * 
	 * @return 代理数量
	 */
	public static int getProxyCount() {
		return proxyItemList.size();
	}
	
	/**
	 * 是否有可用代理
	 * 
	 * @return 是否有代理
	 */
	public static boolean hasProxy() {
		return !proxyItemList.isEmpty();
	}
}
