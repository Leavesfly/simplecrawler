package io.leavesfly.crawler.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUseCase {

	public static void main(String[] args) {
		String str = "sasas\"deals\":\"6816825,2467873,8180875,1883378,5566068,7763057,5135008,4320132\"sasss";
		Pattern pattern = Pattern.compile("\"deals\":\"(.*?)\"");
		Matcher matcher = pattern.matcher(str);
		String[] strArr = null;
		while (matcher.find()) {

			String script = matcher.group();
			strArr = script.substring(script.indexOf(":\"") + 2, script.lastIndexOf("\"")).split(
					",");
			break;
		}
		for (String strq : strArr) {
			System.out.println(strq);
		}

	}
}
