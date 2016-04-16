package io.leavesfly.crawler.util;

import java.io.File;

public class FileUtil {
	
	
	public static void createDirByName(String dirName) {

		File dir = new File(dirName);
		if (dir.exists()) {
			if (dir.isDirectory()) {
				deleteDir(dir);
			} else {
				dir.delete();
			}
		}

		dir.mkdir();
	}

	public static boolean deleteDir(File dir) {

		if (dir == null || dir.isDirectory() == false) {
			return false;
		}

		for (String filename : dir.list()) {
			boolean isSuccess = false;
			File f = new File(dir, filename);
			if (f.isDirectory()) {
				isSuccess = deleteDir(f);
			} else {
				isSuccess = f.delete();
			}
			if (!isSuccess) {
				return isSuccess;
			}
		}

		return dir.delete();
	}
}
