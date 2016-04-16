package io.leavesfly.crawler.parse.meituan.page;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.leavesfly.crawler.constant.ParseConstant;
import io.leavesfly.crawler.domain.RawPage;
import io.leavesfly.crawler.parse.URLFilter;
import io.leavesfly.crawler.util.URLStrUtil;

public class MeiTuanListPageURLFilter extends URLFilter {
	private static final Logger LOG = LoggerFactory.getLogger(MeiTuanListPageURLFilter.class);

	private static MeiTuanListPageURLFilter meiTuanListPageURLFilter;

	private Pattern pattern = Pattern.compile("\"deals\":\"(.*?)\"");

	private OrFilter orFilter;

	private MeiTuanListPageURLFilter() {
		init();

		AndFilter leftAndFilter = new AndFilter(new NodeClassFilter(ScriptTag.class),
				new NodeFilter() {
					private static final long serialVersionUID = 1L;

					public boolean accept(Node node) {
						if (node.getText().contains("\"deals\":\"")) {
							return true;
						}
						return false;
					}
				});

		AndFilter rightAndFilter = new AndFilter(new NodeClassFilter(LinkTag.class),
				new NodeFilter() {

					private static final long serialVersionUID = 1L;

					public boolean accept(Node node) {
						LinkTag linkTag = (LinkTag) node;
						if (linkTag.getLinkText().equals("��һҳ")) {
							return true;
						}
						return false;
					}
				});

		orFilter = new OrFilter(leftAndFilter, rightAndFilter);

	}

	public static MeiTuanListPageURLFilter getInstance() {
		if (meiTuanListPageURLFilter == null) {
			synchronized (meiTuanListPageURLFilter) {
				if (meiTuanListPageURLFilter == null) {
					return new MeiTuanListPageURLFilter();
				}

			}
		}
		return meiTuanListPageURLFilter;

	}

	@Override
	public boolean accept(String url) {
		// http://XX.meituan.com
		// http://XX.meituan.com/index/default/XXXXX
		String cityCode = URLStrUtil.getCityCodeByURLString(url);

		if (url.equals(ParseConstant.HTTP_PROTOCOL + cityCode + ParseConstant.MEI_TUAN_DOMAIN)
				|| url.contains(ParseConstant.HTTP_PROTOCOL + cityCode
						+ ParseConstant.MEI_TUAN_DETAIL_PAGE_URL_MODE)) {
			return true;
		}

		return false;
	}

	@Override
	public List<String> filterURLFromPageText(RawPage rawPage) {
		String pageText = rawPage.getContent();
		String url = rawPage.getUrl();
		List<String> urlList = new LinkedList<String>();
		String cityCode = URLStrUtil.getCityCodeByURLString(url);
		Parser parser = new Parser();
		try {
			parser.setInputHTML(pageText);
			NodeList nodeList = parser.extractAllNodesThatMatch(orFilter);

			for (int i = 0; i < nodeList.size(); i++) {
				Node node = nodeList.elementAt(i);
				if (node instanceof ScriptTag) {
					ScriptTag scriptTag = (ScriptTag) node;
					String script = scriptTag.getStringText();
					Matcher matcher = pattern.matcher(script);
					if (matcher.find()) {
						script = matcher.group();
						String[] strArr = script.substring(script.indexOf(":\"") + 2,
								script.lastIndexOf("\"")).split(",");
						for (String str : strArr) {
							urlList.add(ParseConstant.HTTP_PROTOCOL + cityCode
									+ ParseConstant.MEI_TUAN_DETAIL_PAGE_URL_MODE + str + ".htm");
						}
					}
				} else {
					LinkTag linkTag = (LinkTag) node;
					String nextPageURL = ParseConstant.MEI_TUAN_URL + linkTag.getLink();
					urlList.add(nextPageURL);
				}
			}
			return urlList;
		} catch (ParserException e) {
			LOG.error("filterURLFromPageText() occur error!");
			e.printStackTrace();

		}

		return Collections.emptyList();
	}
}
