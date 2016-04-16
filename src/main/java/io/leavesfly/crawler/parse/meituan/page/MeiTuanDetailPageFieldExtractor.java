package io.leavesfly.crawler.parse.meituan.page;

import io.leavesfly.crawler.domain.CommodityItem;
import io.leavesfly.crawler.parse.FieldExtractor;
import io.leavesfly.crawler.parse.store.FieldStoreFactory;

public class MeiTuanDetailPageFieldExtractor extends FieldExtractor {

	private static MeiTuanDetailPageFieldExtractor meiTuanDetailPageFieldExtractor;

	private MeiTuanDetailPageFieldExtractor() {
		init();
		
	}

	@Override
	public void init() {
		itemStorage = FieldStoreFactory.getDBStoreServiceInstance();

	}

	public static MeiTuanDetailPageFieldExtractor getInstance() {
		if (meiTuanDetailPageFieldExtractor == null) {
			synchronized (meiTuanDetailPageFieldExtractor) {
				if (meiTuanDetailPageFieldExtractor == null) {
					return new MeiTuanDetailPageFieldExtractor();
				}
			}
		}
		return meiTuanDetailPageFieldExtractor;

	}

	@Override
	public boolean accept(String url) {
		return MeiTuanDetailPageURLFilter.getInstance().accept(url);

	}

	@Override
	public int getItemAndStore(String pageText) {
		CommodityItem commodityItem = getCommodityItem(pageText);
		if (commodityItem != null) {
			int statue = itemStorage.storeCommodityItem(commodityItem);
			return statue;
		}

		return 0;
	}

	// ����ҳ�������װ���CommodityItem
	private CommodityItem getCommodityItem(String pageText) {
		// TODO Auto-generated method stub
		return null;
	}

}
