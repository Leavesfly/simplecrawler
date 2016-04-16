package io.leavesfly.crawler.parse;

import io.leavesfly.crawler.domain.CommodityItem;
import io.leavesfly.crawler.domain.ShopItem;

/**
 * 
 * @author yefei.yf
 * 
 */
public interface ItemStorage {
	public static final int ITEM_STORM_SUCCESS = 1;
	public static final int ITEM_STORM_FAILURE = 0;

	public int storeCommodityItem(CommodityItem commodityItem);

	public int storeShopItem(ShopItem shopItem);
}
