package io.leavesfly.crawler.parse.meituan;

import java.util.LinkedList;
import java.util.List;

import io.leavesfly.crawler.parse.URLFilter;
import io.leavesfly.crawler.parse.meituan.page.MeiTuanCityPageURLFilter;
import io.leavesfly.crawler.parse.meituan.page.MeiTuanDetailPageURLFilter;
import io.leavesfly.crawler.parse.meituan.page.MeiTuanListPageURLFilter;

public class MeiTuanURLFilter {

	public static List<URLFilter> getMeiTuanURLFilterList() {
		List<URLFilter> meiTuanURLFilterList = new LinkedList<URLFilter>();

		meiTuanURLFilterList.add(MeiTuanCityPageURLFilter.getInstance());
		meiTuanURLFilterList.add(MeiTuanDetailPageURLFilter.getInstance());
		meiTuanURLFilterList.add(MeiTuanListPageURLFilter.getInstance());

		return meiTuanURLFilterList;
	}
}
