package io.leavesfly.crawler.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 爬虫上下文类
 * 用于在处理流水线中传递数据和状态
 * 
 * @author yefei.yf
 */
public class CrawlContext {
    
    /**
     * 当前处理的URL
     */
    private String currentUrl;
    
    /**
     * 原始页面数据
     */
    private RawPage rawPage;
    
    /**
     * 上下文属性，线程安全
     */
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    
    /**
     * 元数据信息
     */
    private final Map<String, String> metadata = new HashMap<>();
    
    /**
     * 开始时间
     */
    private final long startTime;
    
    /**
     * 重试次数
     */
    private int retryCount = 0;
    
    /**
     * 构造函数
     * 
     * @param url 待处理的URL
     */
    public CrawlContext(String url) {
        this.currentUrl = url;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * 获取当前URL
     * 
     * @return 当前URL
     */
    public String getCurrentUrl() {
        return currentUrl;
    }
    
    /**
     * 设置当前URL
     * 
     * @param currentUrl 当前URL
     */
    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }
    
    /**
     * 获取原始页面数据
     * 
     * @return 原始页面数据
     */
    public RawPage getRawPage() {
        return rawPage;
    }
    
    /**
     * 设置原始页面数据
     * 
     * @param rawPage 原始页面数据
     */
    public void setRawPage(RawPage rawPage) {
        this.rawPage = rawPage;
    }
    
    /**
     * 设置属性
     * 
     * @param key 属性键
     * @param value 属性值
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    /**
     * 获取属性
     * 
     * @param key 属性键
     * @return 属性值
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    /**
     * 获取属性（带类型转换）
     * 
     * @param key 属性键
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, Class<T> clazz) {
        Object value = attributes.get(key);
        if (value != null && clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 移除属性
     * 
     * @param key 属性键
     * @return 被移除的属性值
     */
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }
    
    /**
     * 设置元数据
     * 
     * @param key 元数据键
     * @param value 元数据值
     */
    public void setMetadata(String key, String value) {
        metadata.put(key, value);
    }
    
    /**
     * 获取元数据
     * 
     * @param key 元数据键
     * @return 元数据值
     */
    public String getMetadata(String key) {
        return metadata.get(key);
    }
    
    /**
     * 获取开始时间
     * 
     * @return 开始时间戳
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * 获取重试次数
     * 
     * @return 重试次数
     */
    public int getRetryCount() {
        return retryCount;
    }
    
    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    /**
     * 获取执行时长（毫秒）
     * 
     * @return 执行时长
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * 判断是否包含指定属性
     * 
     * @param key 属性键
     * @return 是否包含
     */
    public boolean containsAttribute(String key) {
        return attributes.containsKey(key);
    }
    
    @Override
    public String toString() {
        return "CrawlContext{" +
                "currentUrl='" + currentUrl + '\'' +
                ", retryCount=" + retryCount +
                ", elapsedTime=" + getElapsedTime() + "ms" +
                ", attributeCount=" + attributes.size() +
                '}';
    }
}