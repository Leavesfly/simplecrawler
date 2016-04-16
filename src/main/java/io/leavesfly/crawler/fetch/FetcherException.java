package io.leavesfly.crawler.fetch;

/**
 * 
 * @author yefei.yf
 * 
 */
public class FetcherException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FetcherException(String message, Throwable t) {
		super(message, t);
	}

	public FetcherException(String message) {
		super(message);
	}
}
