package io.leavesfly.crawler.parse;

import io.leavesfly.crawler.domain.RawPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FieldExtractor implements Accepter {

	private static final Logger LOG = LoggerFactory.getLogger(FieldExtractor.class);
	protected ItemStorage itemStorage;

	public abstract void init();

	public void extractFile(RawPage rawPage) {
		if (accept(rawPage.getUrl())) {
			int state = getItemAndStore(rawPage.getContent());
			if (state != ItemStorage.ITEM_STORM_SUCCESS) {
				LOG.error("extractFile() occur error!");
				return;
			}
		}

	}

	public abstract int getItemAndStore(String pageText);

	public boolean accept(String url) {
		// TODO Auto-generated method stub
		return false;
	}

}
