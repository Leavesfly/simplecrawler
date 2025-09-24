package io.leavesfly.crawler.core.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 爬虫事件发布器
 * 负责管理事件监听器并发布事件
 * 
 * @author yefei.yf
 */
public class CrawlEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(CrawlEventPublisher.class);
    
    private final List<CrawlEventListener> listeners = new CopyOnWriteArrayList<>();
    private final ExecutorService executor;
    private final boolean asyncMode;
    
    /**
     * 构造函数，默认同步模式
     */
    public CrawlEventPublisher() {
        this(false);
    }
    
    /**
     * 构造函数
     * 
     * @param asyncMode 是否异步模式
     */
    public CrawlEventPublisher(boolean asyncMode) {
        this.asyncMode = asyncMode;
        this.executor = asyncMode ? Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r, "CrawlEventPublisher-" + System.currentTimeMillis());
            thread.setDaemon(true);
            return thread;
        }) : null;
    }
    
    /**
     * 添加事件监听器
     * 
     * @param listener 监听器
     */
    public void addListener(CrawlEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            logger.debug("添加事件监听器: {}", listener.getListenerName());
        }
    }
    
    /**
     * 移除事件监听器
     * 
     * @param listener 监听器
     */
    public void removeListener(CrawlEventListener listener) {
        if (listeners.remove(listener)) {
            logger.debug("移除事件监听器: {}", listener.getListenerName());
        }
    }
    
    /**
     * 发布事件
     * 
     * @param event 事件
     */
    public void publishEvent(CrawlEvent event) {
        if (event == null) {
            return;
        }
        
        logger.debug("发布事件: {}", event);
        
        for (CrawlEventListener listener : listeners) {
            if (listener.supports(event.getType())) {
                if (asyncMode && executor != null) {
                    executor.submit(() -> notifyListener(listener, event));
                } else {
                    notifyListener(listener, event);
                }
            }
        }
    }
    
    /**
     * 通知监听器
     * 
     * @param listener 监听器
     * @param event 事件
     */
    private void notifyListener(CrawlEventListener listener, CrawlEvent event) {
        try {
            listener.onEvent(event);
        } catch (Exception e) {
            logger.error("事件监听器 {} 处理事件时发生异常: {}", 
                       listener.getListenerName(), event, e);
        }
    }
    
    /**
     * 获取监听器数量
     * 
     * @return 监听器数量
     */
    public int getListenerCount() {
        return listeners.size();
    }
    
    /**
     * 清空所有监听器
     */
    public void clearListeners() {
        listeners.clear();
        logger.debug("清空所有事件监听器");
    }
    
    /**
     * 关闭事件发布器
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            logger.debug("事件发布器已关闭");
        }
    }
}