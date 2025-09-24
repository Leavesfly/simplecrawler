package io.leavesfly.crawler.strategy;

import io.leavesfly.crawler.domain.CrawlContext;
import io.leavesfly.crawler.domain.RawPage;

import java.util.List;

/**
 * 爬取策略接口
 * 使用策略模式支持不同网站的爬取策略
 * 
 * @author yefei.yf
 */
public interface CrawlStrategy {
    
    /**
     * 判断是否支持指定的URL
     * 
     * @param url 待爬取的URL
     * @return 是否支持
     */
    boolean supports(String url);
    
    /**
     * 获取策略名称
     * 
     * @return 策略名称
     */
    String getStrategyName();
    
    /**
     * 获取请求头信息
     * 
     * @param url 请求URL
     * @return 请求头映射
     */
    default java.util.Map<String, String> getHeaders(String url) {
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        return headers;
    }
    
    /**
     * 获取请求延迟时间（毫秒）
     * 
     * @param url 请求URL
     * @return 延迟时间
     */
    default long getRequestDelay(String url) {
        return 1000; // 默认1秒延迟
    }
    
    /**
     * 从页面中提取URL链接
     * 
     * @param context 爬虫上下文
     * @return URL列表
     */
    List<String> extractUrls(CrawlContext context);
    
    /**
     * 从页面中提取数据
     * 
     * @param context 爬虫上下文
     * @return 提取的数据对象
     */
    Object extractData(CrawlContext context);
    
    /**
     * 处理页面内容前的预处理
     * 
     * @param rawPage 原始页面
     * @return 处理后的页面内容
     */
    default String preprocessContent(RawPage rawPage) {
        return rawPage.getContent();
    }
    
    /**
     * 获取最大重试次数
     * 
     * @return 最大重试次数
     */
    default int getMaxRetries() {
        return 3;
    }
    
    /**
     * 获取优先级，数值越小优先级越高
     * 
     * @return 优先级
     */
    default int getPriority() {
        return 100;
    }
}