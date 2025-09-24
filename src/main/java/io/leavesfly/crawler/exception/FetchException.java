package io.leavesfly.crawler.exception;

/**
 * 页面抓取异常
 * 
 * @author yefei.yf
 */
public class FetchException extends CrawlerException {
    
    public FetchException(String message, String url) {
        super("FETCH_ERROR", message, url);
    }
    
    public FetchException(String message, String url, Throwable cause) {
        super("FETCH_ERROR", message, url, cause);
    }
    
    /**
     * 网络连接异常
     */
    public static class NetworkException extends FetchException {
        public NetworkException(String message, String url) {
            super(message, url);
        }
        
        public NetworkException(String message, String url, Throwable cause) {
            super(message, url, cause);
        }
    }
    
    /**
     * HTTP状态码异常
     */
    public static class HttpStatusException extends FetchException {
        private final int statusCode;
        
        public HttpStatusException(int statusCode, String message, String url) {
            super("HTTP " + statusCode + ": " + message, url);
            this.statusCode = statusCode;
        }
        
        public int getStatusCode() {
            return statusCode;
        }
    }
    
    /**
     * 超时异常
     */
    public static class TimeoutException extends FetchException {
        public TimeoutException(String message, String url) {
            super(message, url);
        }
        
        public TimeoutException(String message, String url, Throwable cause) {
            super(message, url, cause);
        }
    }
}