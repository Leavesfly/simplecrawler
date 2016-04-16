package io.leavesfly.crawler.fetch.store;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.leavesfly.crawler.domain.RawPage;

public abstract class RawPageStore {
	public static final int STORE_SUCCESS = 1;
	public static final int STORE_FAILURE = 0;
	
	private String currentDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	public String getFileNameByURL(String url) {
		String fileName = url + "-" + currentDay + ".html";
		return fileName;
	}

	public abstract int store(RawPage rawPage);

	public String getCurrentDay() {
		return currentDay;
	}

	public void setCurrentDay(String currentDay) {
		this.currentDay = currentDay;
	}

}
