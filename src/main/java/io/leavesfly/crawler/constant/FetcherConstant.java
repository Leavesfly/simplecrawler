package io.leavesfly.crawler.constant;

public class FetcherConstant {
	public static final String CHARSET = "UTF-8";
	public static final String PAGE_STORE_ROOT_DIR = "D:/CrawlerRootDir/";
	public static final int RETRY_TIMES = 3;

	public static final int HTTP_CONN_TIMEOUT = 5000;
	public static final int SINGLE_PROXY_GET_PAGE_MAX = 1000;

	public static final String PROXY_LIST_FILE_PATH = PAGE_STORE_ROOT_DIR + "proxy.txt";
	public static final String CHECK_PROXY_ITEM_VALID_URL = "http://www.taobao.com/";
	public static final int MAX_PAGE_LENGTH = 512 * 1024;

	public static final String RAW_PAGE_FILE_STORE = "com.taobao.ju.crawler.fetch.store.RawPageFileStore";
	public static final String RAW_PAGE_HBASE_STORE = "com.taobao.ju.crawler.fetch.store.RawPageHbaseStore";

	public static final int MAX_FILE_NUM_IN_SINGLE_DIR = 1000;
	public static final String MEI_TUAN_DIR = "MeiTuan";
}
