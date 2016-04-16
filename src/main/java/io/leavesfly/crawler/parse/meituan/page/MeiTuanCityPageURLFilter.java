package io.leavesfly.crawler.parse.meituan.page;

import java.util.List;

import io.leavesfly.crawler.constant.ParseConstant;
import io.leavesfly.crawler.domain.RawPage;
import io.leavesfly.crawler.parse.URLFilter;

public class MeiTuanCityPageURLFilter extends URLFilter {

	private static MeiTuanCityPageURLFilter meiTuanCityPageURLFilter;

	private MeiTuanCityPageURLFilter() {
		init();
	}

	public static MeiTuanCityPageURLFilter getInstance() {
		if (meiTuanCityPageURLFilter == null) {
			synchronized (meiTuanCityPageURLFilter) {
				if (meiTuanCityPageURLFilter == null) {
					return new MeiTuanCityPageURLFilter();
				}

			}
		}
		return meiTuanCityPageURLFilter;

	}

	@Override
	// http://www.meituan.com/index/changecity/initiative
	public boolean accept(String url) {
		if (url.equals(ParseConstant.MEI_TUAN_CITY_LIST_PAGE_URL)) {
			return true;
		}
		return false;
	}

	@Override
	public List<String> filterURLFromPageText(RawPage rawPage) {
		// TODO Auto-generated method stub
		
		return null;
	}

}
