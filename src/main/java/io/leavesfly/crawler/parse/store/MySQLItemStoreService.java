package io.leavesfly.crawler.parse.store;

import io.leavesfly.crawler.domain.CommodityItem;
import io.leavesfly.crawler.domain.ShopItem;
import io.leavesfly.crawler.parse.ItemStorage;
import io.leavesfly.crawler.parse.meituan.MeiTuanCommodityItem;
import io.leavesfly.crawler.parse.meituan.MeiTuanShopItem;

public class MySQLItemStoreService implements ItemStorage {
	private static MySQLItemStoreService mySQLItemStoreService;

	

	private MySQLItemStoreService() {
		init();
	}

	private void init() {
		
	}

	
	public static MySQLItemStoreService getInstance() {
		if (mySQLItemStoreService == null) {
			synchronized (mySQLItemStoreService) {
				if (mySQLItemStoreService == null) {
					return new MySQLItemStoreService();
				}

			}
		}
		return mySQLItemStoreService;

	}

	public int storeCommodityItem(CommodityItem commodityItem) {

		if (commodityItem instanceof MeiTuanCommodityItem) {

		}

		// TODO Auto-generated method stub
		return 0;
	}

	public int storeShopItem(ShopItem shopItem) {
		if (shopItem instanceof MeiTuanShopItem) {

		}
		return 0;
	}

}
