package io.leavesfly.crawler.parse.meituan;

import java.util.List;

import io.leavesfly.crawler.domain.ShopItem;

public class MeiTuanShopItem extends ShopItem {

	private List<Long> grouponList;
	private List<Long> onceGrouponList;

	public List<Long> getGrouponList() {
		return grouponList;
	}

	public void setGrouponList(List<Long> grouponList) {
		this.grouponList = grouponList;
	}

	public List<Long> getOnceGrouponList() {
		return onceGrouponList;
	}

	public void setOnceGrouponList(List<Long> onceGrouponList) {
		this.onceGrouponList = onceGrouponList;
	}

}
