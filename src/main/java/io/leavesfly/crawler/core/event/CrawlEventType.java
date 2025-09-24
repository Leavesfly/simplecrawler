package io.leavesfly.crawler.core.event;

/**
 * 爬虫事件类型枚举
 * 
 * @author yefei.yf
 */
public enum CrawlEventType {
    
    /**
     * 爬虫启动事件
     */
    CRAWLER_STARTED("爬虫启动"),
    
    /**
     * 爬虫停止事件
     */
    CRAWLER_STOPPED("爬虫停止"),
    
    /**
     * 页面获取开始事件
     */
    PAGE_FETCH_STARTED("页面获取开始"),
    
    /**
     * 页面获取成功事件
     */
    PAGE_FETCH_SUCCESS("页面获取成功"),
    
    /**
     * 页面获取失败事件
     */
    PAGE_FETCH_FAILED("页面获取失败"),
    
    /**
     * 页面解析开始事件
     */
    PAGE_PARSE_STARTED("页面解析开始"),
    
    /**
     * 页面解析成功事件
     */
    PAGE_PARSE_SUCCESS("页面解析成功"),
    
    /**
     * 页面解析失败事件
     */
    PAGE_PARSE_FAILED("页面解析失败"),
    
    /**
     * URL入队事件
     */
    URL_QUEUED("URL入队"),
    
    /**
     * 异常事件
     */
    ERROR_OCCURRED("异常发生");
    
    private final String description;
    
    CrawlEventType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}