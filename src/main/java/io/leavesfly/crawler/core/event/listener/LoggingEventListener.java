package io.leavesfly.crawler.core.event.listener;

import io.leavesfly.crawler.core.event.CrawlEvent;
import io.leavesfly.crawler.core.event.CrawlEventListener;
import io.leavesfly.crawler.core.event.CrawlEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志记录监听器
 * 将爬虫事件记录到日志中
 * 
 * @author yefei.yf
 */
public class LoggingEventListener implements CrawlEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingEventListener.class);
    
    @Override
    public void onEvent(CrawlEvent event) {
        switch (event.getType()) {
            case CRAWLER_STARTED:
                logger.info("爬虫启动");
                break;
            case CRAWLER_STOPPED:
                logger.info("爬虫停止");
                break;
            case PAGE_FETCH_SUCCESS:
                logger.info("页面获取成功: {}", event.getUrl());
                break;
            case PAGE_FETCH_FAILED:
                logger.warn("页面获取失败: {}", event.getUrl());
                if (event.getException() != null) {
                    logger.warn("失败原因", event.getException());
                }
                break;
            case PAGE_PARSE_SUCCESS:
                logger.info("页面解析成功: {}", event.getUrl());
                break;
            case PAGE_PARSE_FAILED:
                logger.warn("页面解析失败: {}", event.getUrl());
                break;
            case URL_QUEUED:
                logger.debug("URL已入队: {}", event.getUrl());
                break;
            case ERROR_OCCURRED:
                logger.error("发生错误: {}", event.getData("message"));
                if (event.getException() != null) {
                    logger.error("错误详情", event.getException());
                }
                break;
            default:
                logger.debug("收到事件: {}", event);
        }
    }
    
    @Override
    public boolean supports(CrawlEventType eventType) {
        // 支持所有事件类型
        return true;
    }
    
    @Override
    public String getListenerName() {
        return "LoggingEventListener";
    }
}