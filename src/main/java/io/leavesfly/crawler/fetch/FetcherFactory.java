package io.leavesfly.crawler.fetch;

import io.leavesfly.crawler.conf.CrawlerConfig;

/**
 * 抓取器工厂类
 * 使用工厂模式创建不同类型的抓取器
 * 
 * @author yefei.yf
 */
public class FetcherFactory {
    
    /**
     * 创建默认的抓取器
     * 
     * @return 默认抓取器实例
     */
    public static Fetcher createDefaultFetcher() {
        return new PageFetcher();
    }
    
    /**
     * 根据配置创建抓取器
     * 
     * @param config 爬虫配置
     * @return 抓取器实例
     */
    public static Fetcher createFetcher(CrawlerConfig config) {
        // 根据配置选择不同的抓取器实现
        String fetcherType = config.getFetcherType();
        
        switch (fetcherType.toLowerCase()) {
            case "default":
            case "http":
                return new PageFetcher();
            case "async":
                // 异步抓取器（可扩展）
                return new PageFetcher(); // 暂时返回默认实现
            default:
                throw new IllegalArgumentException("不支持的抓取器类型: " + fetcherType);
        }
    }
    
    /**
     * 创建带重试机制的抓取器
     * 
     * @param maxRetries 最大重试次数
     * @return 抓取器实例
     */
    public static Fetcher createRetryableFetcher(int maxRetries) {
        PageFetcher fetcher = new PageFetcher();
        // 这里可以包装一个重试装饰器
        return fetcher;
    }
}