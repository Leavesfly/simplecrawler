package io.leavesfly.crawler.parse.meituan.page;

import java.util.List;

import io.leavesfly.crawler.constant.ParseConstant;
import io.leavesfly.crawler.domain.RawPage;
import io.leavesfly.crawler.parse.URLFilter;
import io.leavesfly.crawler.util.URLStrUtil;

public class MeiTuanDetailPageURLFilter extends URLFilter {
	private static MeiTuanDetailPageURLFilter meiTuanDetailPageURLFilter;

	private MeiTuanDetailPageURLFilter() {
		init();
	}

	public static MeiTuanDetailPageURLFilter getInstance() {
		if (meiTuanDetailPageURLFilter == null) {
			synchronized (meiTuanDetailPageURLFilter) {
				if (meiTuanDetailPageURLFilter == null) {
					return new MeiTuanDetailPageURLFilter();
				}

			}
		}
		return meiTuanDetailPageURLFilter;

	}

	@Override
	public boolean accept(String url) {
		String cityCode = URLStrUtil.getCityCodeByURLString(url);
		if (url.contains(ParseConstant.HTTP_PROTOCOL + cityCode
				+ ParseConstant.MEI_TUAN_DETAIL_PAGE_URL_MODE)) {
			return true;
		}
		return false;

	}

	@Override
	public List<String> filterURLFromPageText(RawPage rawPage) {
		// TODO ��url��������������Ĳ������磺
		// http://hz.meituan.com/shop/692205?evaluateNum=21
		return null;
	}

}
