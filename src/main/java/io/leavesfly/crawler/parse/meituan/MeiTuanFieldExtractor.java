package io.leavesfly.crawler.parse.meituan;

import java.util.LinkedList;
import java.util.List;

import io.leavesfly.crawler.parse.FieldExtractor;
import io.leavesfly.crawler.parse.meituan.page.MeiTuanDetailPageFieldExtractor;
import io.leavesfly.crawler.parse.meituan.page.MeiTuanShopPageFieldExtractor;

public class MeiTuanFieldExtractor {

	public static List<FieldExtractor> getMeiTuanFileDescriptorList() {
		List<FieldExtractor> meiTuanFileDescriptorList = new LinkedList<FieldExtractor>();

		meiTuanFileDescriptorList.add(MeiTuanDetailPageFieldExtractor.getInstance());
		meiTuanFileDescriptorList.add(MeiTuanShopPageFieldExtractor.getInstance());

		return meiTuanFileDescriptorList;
	}
}
