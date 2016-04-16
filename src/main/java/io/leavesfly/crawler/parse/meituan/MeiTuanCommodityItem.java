package io.leavesfly.crawler.parse.meituan;

import io.leavesfly.crawler.domain.CommodityItem;

public class MeiTuanCommodityItem extends CommodityItem {
	private String longTitle;

	public String getLongTitle() {
		return longTitle;
	}

	public void setLongTitle(String longTitle) {
		this.longTitle = longTitle;
	}

}
