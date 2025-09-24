package io.leavesfly.crawler.exception;

/**
 * 爬虫异常基类
 * 所有爬虫相关异常的基类
 * 
 * @author yefei.yf
 */
public class CrawlerException extends Exception {
    
    private final String errorCode;
    private final String url;
    
    public CrawlerException(String message) {
        super(message);
        this.errorCode = "UNKNOWN";
        this.url = null;
    }
    
    public CrawlerException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN";
        this.url = null;
    }
    
    public CrawlerException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.url = null;
    }
    
    public CrawlerException(String errorCode, String message, String url) {
        super(message);
        this.errorCode = errorCode;
        this.url = url;
    }
    
    public CrawlerException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.url = null;
    }
    
    public CrawlerException(String errorCode, String message, String url, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.url = url;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getUrl() {
        return url;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append("{");
        sb.append("errorCode='").append(errorCode).append('\'');
        if (url != null) {
            sb.append(", url='").append(url).append('\'');
        }
        sb.append(", message='").append(getMessage()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}