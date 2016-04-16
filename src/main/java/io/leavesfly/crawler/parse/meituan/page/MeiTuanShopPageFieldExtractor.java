package io.leavesfly.crawler.parse.meituan.page;

import io.leavesfly.crawler.domain.RawPage;
import io.leavesfly.crawler.domain.ShopItem;
import io.leavesfly.crawler.parse.FieldExtractor;

public class MeiTuanShopPageFieldExtractor extends FieldExtractor {

	private static MeiTuanShopPageFieldExtractor meiTuanShopPageFieldExtractor;

	private MeiTuanShopPageFieldExtractor() {
		init();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	public static MeiTuanShopPageFieldExtractor getInstance() {
		if (meiTuanShopPageFieldExtractor == null) {
			synchronized (meiTuanShopPageFieldExtractor) {
				if (meiTuanShopPageFieldExtractor == null) {
					return new MeiTuanShopPageFieldExtractor();
				}
			}
		}
		return meiTuanShopPageFieldExtractor;

	}

	public ShopItem getShopItem(RawPage rawPage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean accept(String url) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getItemAndStore(String pageText) {
		// TODO Auto-generated method stub
		return 0;
	}

}
