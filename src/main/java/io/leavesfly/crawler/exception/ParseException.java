package io.leavesfly.crawler.exception;

/**
 * 页面解析异常
 * 
 * @author yefei.yf
 */
public class ParseException extends CrawlerException {
    
    public ParseException(String message, String url) {
        super("PARSE_ERROR", message, url);
    }
    
    public ParseException(String message, String url, Throwable cause) {
        super("PARSE_ERROR", message, url, cause);
    }
    
    /**
     * 内容格式异常
     */
    public static class ContentFormatException extends ParseException {
        public ContentFormatException(String message, String url) {
            super(message, url);
        }
        
        public ContentFormatException(String message, String url, Throwable cause) {
            super(message, url, cause);
        }
    }
    
    /**
     * 选择器异常
     */
    public static class SelectorException extends ParseException {
        private final String selector;
        
        public SelectorException(String selector, String message, String url) {
            super("选择器错误 '" + selector + "': " + message, url);
            this.selector = selector;
        }
        
        public String getSelector() {
            return selector;
        }
    }
    
    /**
     * 数据提取异常
     */
    public static class DataExtractionException extends ParseException {
        public DataExtractionException(String message, String url) {
            super(message, url);
        }
        
        public DataExtractionException(String message, String url, Throwable cause) {
            super(message, url, cause);
        }
    }
}