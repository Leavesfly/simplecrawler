package io.leavesfly.crawler.domain;

public class RawPage {
	private String url;
	private String contentCharSet;
	private String content;

	public RawPage(String url, String contentCharSet, String content) {
		this.url = url;
		this.contentCharSet = contentCharSet;
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentCharset() {
		return contentCharSet;
	}

	public void setContentCharset(String contentCharSet) {
		this.contentCharSet = contentCharSet;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
