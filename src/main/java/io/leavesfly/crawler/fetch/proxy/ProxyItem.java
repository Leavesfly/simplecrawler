package io.leavesfly.crawler.fetch.proxy;

public class ProxyItem {
	private String proxyIP;
	private int proxyPort;

	public ProxyItem(String proxyIP, int proxyPort) {
		super();
		this.proxyIP = proxyIP;
		this.proxyPort = proxyPort;
	}

	public String getProxyIP() {
		return proxyIP;
	}

	public void setProxyIP(String proxyIP) {
		this.proxyIP = proxyIP;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

}
