package io.leavesfly.crawler.conf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

/**
 * 爬虫配置类测试
 * 
 * @author yefei.yf
 */
@DisplayName("爬虫配置测试")
class CrawlerConfigTest {
    
    @Test
    @DisplayName("创建默认配置")
    void testCreateDefaultConfig() {
        CrawlerConfig config = CrawlerConfig.createDefault();
        
        assertNotNull(config);
        assertEquals(30000, config.getConnectionTimeout());
        assertEquals(60000, config.getSocketTimeout());
        assertEquals(100, config.getMaxConnections());
        assertEquals(3, config.getMaxRetries());
        assertEquals("SimpleCrawler/1.0", config.getUserAgent());
        assertEquals("http", config.getFetcherType());
        assertEquals(10, config.getThreadPoolSize());
        assertFalse(config.isUseProxy());
    }
    
    @Test
    @DisplayName("使用Builder构建配置")
    void testBuilderPattern() {
        CrawlerConfig config = new CrawlerConfig.Builder()
                .connectionTimeout(10, TimeUnit.SECONDS)
                .socketTimeout(30, TimeUnit.SECONDS)
                .maxConnections(50)
                .maxRetries(5)
                .userAgent("TestCrawler/1.0")
                .fetcherType("async")
                .threadPoolSize(20)
                .proxy("proxy.example.com", 8080)
                .storage("database", "/data")
                .build();
        
        assertEquals(10000, config.getConnectionTimeout());
        assertEquals(30000, config.getSocketTimeout());
        assertEquals(50, config.getMaxConnections());
        assertEquals(5, config.getMaxRetries());
        assertEquals("TestCrawler/1.0", config.getUserAgent());
        assertEquals("async", config.getFetcherType());
        assertEquals(20, config.getThreadPoolSize());
        assertTrue(config.isUseProxy());
        assertEquals("proxy.example.com", config.getProxyHost());
        assertEquals(8080, config.getProxyPort());
        assertEquals("database", config.getStorageType());
        assertEquals("/data", config.getStoragePath());
    }
    
    @Test
    @DisplayName("验证无效配置")
    void testValidation() {
        // 测试连接超时时间为0
        assertThrows(IllegalArgumentException.class, () -> {
            new CrawlerConfig.Builder()
                    .connectionTimeout(0)
                    .build();
        });
        
        // 测试Socket超时时间为负数
        assertThrows(IllegalArgumentException.class, () -> {
            new CrawlerConfig.Builder()
                    .socketTimeout(-1000)
                    .build();
        });
        
        // 测试最大连接数为0
        assertThrows(IllegalArgumentException.class, () -> {
            new CrawlerConfig.Builder()
                    .maxConnections(0)
                    .build();
        });
        
        // 测试线程池大小为负数
        assertThrows(IllegalArgumentException.class, () -> {
            new CrawlerConfig.Builder()
                    .threadPoolSize(-1)
                    .build();
        });
        
        // 测试User-Agent为空
        assertThrows(IllegalArgumentException.class, () -> {
            new CrawlerConfig.Builder()
                    .userAgent("")
                    .build();
        });
        
        // 测试User-Agent为null
        assertThrows(IllegalArgumentException.class, () -> {
            new CrawlerConfig.Builder()
                    .userAgent(null)
                    .build();
        });
    }
    
    @Test
    @DisplayName("测试延迟配置")
    void testDelayConfiguration() {
        CrawlerConfig config = new CrawlerConfig.Builder()
                .delayBetweenRequests(2, TimeUnit.SECONDS)
                .build();
        
        assertEquals(2000, config.getDelayBetweenRequests());
        
        // 测试毫秒配置
        config = new CrawlerConfig.Builder()
                .delayBetweenRequests(500)
                .build();
        
        assertEquals(500, config.getDelayBetweenRequests());
    }
    
    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        CrawlerConfig config = CrawlerConfig.createDefault();
        String configString = config.toString();
        
        assertNotNull(configString);
        assertTrue(configString.contains("CrawlerConfig"));
        assertTrue(configString.contains("connectionTimeout"));
        assertTrue(configString.contains("userAgent"));
    }
}