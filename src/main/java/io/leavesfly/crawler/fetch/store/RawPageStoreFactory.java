package io.leavesfly.crawler.fetch.store;

public class RawPageStoreFactory {

	public static RawPageStore generateStoreInstance(String className) {
		RawPageStore instance = null;
		try {
			Class<?> clazz = Class.forName(className);
			instance = (RawPageStore) clazz.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return instance;
	}
}
