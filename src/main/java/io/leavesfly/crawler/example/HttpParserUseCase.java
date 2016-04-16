package io.leavesfly.crawler.example;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class HttpParserUseCase {

	public static void main(String[] args) throws ParserException {
		
	}

	// ��ȡһ����ҳ�����е����Ӻ�ͼƬ����
	public static void extracLinks(String url) {
		try {
			Parser parser = new Parser(url);
			parser.setEncoding("gb2312");
			// ���� <frame> ��ǩ�� filter��������ȡ frame ��ǩ��� src ����������ʾ������
			NodeFilter frameFilter = new NodeFilter() {
				public boolean accept(Node node) {
					if (node.getText().startsWith("frame src=")) {
						return true;
					} else {
						return false;
					}
				}
			};
			// OrFilter �����ù��� <a> ��ǩ��<img> ��ǩ�� <frame> ��ǩ��������ǩ�� or �Ĺ�ϵ
			OrFilter rorFilter = new OrFilter(new NodeClassFilter(LinkTag.class),
					new NodeClassFilter(ImageTag.class));
			OrFilter linkFilter = new OrFilter(rorFilter, frameFilter);
			// �õ����о������˵ı�ǩ
			NodeList list = parser.extractAllNodesThatMatch(linkFilter);
			for (int i = 0; i < list.size(); i++) {
				Node tag = list.elementAt(i);
				if (tag instanceof LinkTag)// <a> ��ǩ
				{
					LinkTag link = (LinkTag) tag;
					String linkUrl = link.getLink();// url
					String text = link.getLinkText();// ��������
					System.out.println(linkUrl + "**********" + text);
				} else if (tag instanceof ImageTag)// <img> ��ǩ
				{
					ImageTag image = (ImageTag) list.elementAt(i);
					System.out.print(image.getImageURL() + "********");// ͼƬ��ַ
					System.out.println(image.getText());// ͼƬ����
				} else// <frame> ��ǩ
				{
					// ��ȡ frame �� src ���Ե������� <frame src="test.html"/>
					String frame = tag.getText();
					int start = frame.indexOf("src=");
					frame = frame.substring(start);
					int end = frame.indexOf(" ");
					if (end == -1)
						end = frame.indexOf(">");
					frame = frame.substring(5, end - 1);
					System.out.println(frame);
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

}
