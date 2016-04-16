package io.leavesfly.crawler.parse;

import java.util.List;

import io.leavesfly.crawler.domain.RawPage;
import io.leavesfly.crawler.schedule.BlockQueue;
import io.leavesfly.crawler.schedule.URLMutilQueue;

/**
 * 
 * @author yefei.yf
 * 
 */
public abstract class URLFilter implements Accepter {
	private BlockQueue urlBlockQueue;

	public void init() {
		urlBlockQueue = URLMutilQueue.getURLQueueInstace();
	}

	public void filterURL(RawPage rawPage) {
		if (accept(rawPage.getUrl())) {
			List<String> urlList = filterURLFromPageText(rawPage);
			urlBlockQueue.enQueue(urlList);
		}
	}

	public abstract List<String> filterURLFromPageText(RawPage rawPage);

	public boolean accept(String url) {
		// TODO Auto-generated method stub
		return false;
	}
}
