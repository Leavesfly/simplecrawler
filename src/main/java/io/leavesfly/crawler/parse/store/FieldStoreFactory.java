package io.leavesfly.crawler.parse.store;

import io.leavesfly.crawler.parse.ItemStorage;

public class FieldStoreFactory {

	public static ItemStorage getDBStoreServiceInstance() {
		return MySQLItemStoreService.getInstance();
	}
}
