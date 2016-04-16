package io.leavesfly.crawler.fetch.store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.leavesfly.crawler.constant.FetcherConstant;
import io.leavesfly.crawler.domain.RawPage;
import io.leavesfly.crawler.util.FileUtil;
import io.leavesfly.crawler.util.URLStrUtil;

public class RawPageFileStore extends RawPageStore {
	private static int singleDirMaxFileNowNum = 0;
	private static int FileMark = 0;

	private FileOutputStream fileOutStream;

	private void init(String url) {
		try {
			fileOutStream = new FileOutputStream(emergeStoreFile(url));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}

	private File emergeStoreFile(String url) {
		if (singleDirMaxFileNowNum > FetcherConstant.MAX_FILE_NUM_IN_SINGLE_DIR) {
			singleDirMaxFileNowNum = 0;
			FileMark++;
		}
		String cityCode = URLStrUtil.getCityCodeByURLString(url);
		String dirPath = FetcherConstant.PAGE_STORE_ROOT_DIR + getCurrentDay() + "/" + cityCode
				+ "/" + FileMark;

		File file = new File(dirPath);
		if (file.isDirectory() && !file.exists()) {
			FileUtil.createDirByName(file.getAbsolutePath());
		}

		singleDirMaxFileNowNum++;

		return new File(dirPath + "/" + getFileNameByURL(url));
	}

	@Override
	public int store(RawPage rawPage) {
		init(rawPage.getUrl());
		try {
			fileOutStream.write(rawPage.getContent().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return STORE_FAILURE;
		}

		return STORE_SUCCESS;

	}
}
