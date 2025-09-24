package io.leavesfly.crawler.core.event;

/**
 * 爬虫事件监听器接口
 * 实现观察者模式，用于监听爬虫运行过程中的各种事件
 * 
 * @author yefei.yf
 */
public interface CrawlEventListener {
    
    /**
     * 处理爬虫事件
     * 
     * @param event 爬虫事件
     */
    void onEvent(CrawlEvent event);
    
    /**
     * 判断是否支持处理指定类型的事件
     * 
     * @param eventType 事件类型
     * @return 是否支持
     */
    default boolean supports(CrawlEventType eventType) {
        return true;
    }
    
    /**
     * 获取监听器名称
     * 
     * @return 监听器名称
     */
    default String getListenerName() {
        return this.getClass().getSimpleName();
    }
}