package io.leavesfly.crawler.fetch;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.leavesfly.crawler.constant.FetcherConstant;
import io.leavesfly.crawler.domain.RawPage;
import io.leavesfly.crawler.fetch.proxy.ProxyItem;
import io.leavesfly.crawler.fetch.proxy.ProxyUtil;
import io.leavesfly.crawler.fetch.store.RawPageStore;
import io.leavesfly.crawler.fetch.store.RawPageStoreFactory;

public class PageFetcher extends Fetcher {

	private static final Logger LOG = LoggerFactory.getLogger(PageFetcher.class);

	@Override
	public RawPage downloadPage(String url) {
		RawPage pawPage;
		GetMethod method = new GetMethod(url);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(FetcherConstant.RETRY_TIMES, false));
		if (ProxyUtil.proxyGetPageNum > FetcherConstant.SINGLE_PROXY_GET_PAGE_MAX) {
			ProxyItem proxyItem = ProxyUtil.nextProxyItem();
			setProxy(proxyItem);
		}
		try {
			int statusCode = httpClient.executeMethod(method);
			while (statusCode != HttpStatus.SC_OK) {
				if (statusCode == HttpStatus.SC_FORBIDDEN) {
					ProxyItem proxyItem = ProxyUtil.nextProxyItem();
					setProxy(proxyItem);
					statusCode = httpClient.executeMethod(method);
				} else {
					LOG.error("HttpClient error! statusCode:" + statusCode);
					return null;
				}
			}
			pawPage = generateRawPage(url, method);

			ProxyUtil.proxyGetPageNum++;

		} catch (HttpException e) {
			LOG.error("HttpException occur:" + e.toString());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			LOG.error("IOException occur:" + e.toString());
			e.printStackTrace();
			return null;
		} finally {
			method.releaseConnection();
		}
		return pawPage;

	}

	private RawPage generateRawPage(String targetURL, GetMethod method) throws IOException {
		String contentCharSet = method.getResponseCharSet();
		String content = method.getResponseBodyAsString(FetcherConstant.MAX_PAGE_LENGTH);
		return new RawPage(targetURL, contentCharSet, content);
	}

	@Override
	public int storePage(RawPage rawPage) {
		RawPageStore rawPageStore = RawPageStoreFactory
				.generateStoreInstance(FetcherConstant.RAW_PAGE_FILE_STORE);
		if (rawPageStore != null) {
			int storeState = rawPageStore.store(rawPage);
			return storeState;
		}

		return RawPageStore.STORE_FAILURE;
	}

}
