package io.leavesfly.crawler.util;

import io.leavesfly.crawler.constant.CommConstant;

public class URLStrUtil {

	public static void main(String[] args) {
		System.out.println(getCityCodeByURLString("http://www.dianping.com/shanghai"));
	}

	// http://hz.meituan.com/deal/8778059.html
	// http://www.dianping.com/shanghai
	public static String getCityCodeByURLString(String url) {
		if (url.contains(CommConstant.SITE_MEI_TUAN_DOMAIN_NAME)) {
			String[] strArr = url.split("\\.");
			strArr = strArr[0].split("//");
			return strArr[strArr.length - 1];
		}
		if (url.contains(CommConstant.SITE_DA_ZHONG_DIAN_PING_DOMAIN_NAME)) {
			String[] strArr = url.split("/");
			return strArr[strArr.length - 1];
		}
		return "hz";
	}
	
	
}
