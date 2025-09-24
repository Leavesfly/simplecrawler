package io.leavesfly.crawler.core;

import io.leavesfly.crawler.conf.CrawlerConfig;
import io.leavesfly.crawler.core.event.listener.StatisticsEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 现代化爬虫引擎集成测试
 * 
 * @author yefei.yf
 */
@DisplayName("爬虫引擎集成测试")
class ModernCrawlerEngineIntegrationTest {
    
    private CrawlerConfig config;
    private ModernCrawlerEngine crawler;
    
    @BeforeEach
    void setUp() {
        config = new CrawlerConfig.Builder()
                .connectionTimeout(5, TimeUnit.SECONDS)
                .socketTimeout(10, TimeUnit.SECONDS)
                .threadPoolSize(2)
                .delayBetweenRequests(100) // 短延迟用于测试
                .queueCapacity(100)
                .maxRetries(1)
                .build();
        
        crawler = new ModernCrawlerEngine(config);
    }
    
    @Test
    @DisplayName("引擎基本功能测试")
    void testBasicEngineFunction() {
        assertNotNull(crawler);
        assertFalse(crawler.isRunning());
        assertEquals(0, crawler.getQueueSize());
        
        // 验证配置
        assertEquals(config, crawler.getConfig());
        
        // 验证组件初始化
        assertNotNull(crawler.getEventPublisher());
        assertNotNull(crawler.getStrategyManager());
        
        // 验证策略管理器已注册策略
        assertTrue(crawler.getStrategyManager().getStrategyCount() > 0);
    }
    
    @Test
    @DisplayName("URL队列管理测试")
    void testUrlQueueManagement() {
        assertFalse(crawler.isRunning());
        assertEquals(0, crawler.getQueueSize());
        
        // 添加URL
        crawler.addUrl("https://example.com/1");
        crawler.addUrl("https://example.com/2");
        crawler.addUrl("https://example.com/3");
        
        assertEquals(3, crawler.getQueueSize());
        
        // 添加重复URL（应该被添加，因为没有去重逻辑）
        crawler.addUrl("https://example.com/1");
        assertEquals(4, crawler.getQueueSize());
        
        // 添加空URL
        crawler.addUrl(null);
        crawler.addUrl("");
        crawler.addUrl("   ");
        assertEquals(4, crawler.getQueueSize()); // 无效URL不会被添加
    }
    
    @Test
    @DisplayName("引擎启动和停止测试")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testEngineStartAndStop() throws InterruptedException {
        List<String> seedUrls = Arrays.asList(
            "https://httpbin.org/html", // 可靠的测试URL
            "https://httpbin.org/json"
        );
        
        assertFalse(crawler.isRunning());
        
        // 启动爬虫
        crawler.start(seedUrls);
        assertTrue(crawler.isRunning());
        
        // 等待一段时间让爬虫工作
        Thread.sleep(2000);
        
        // 检查统计信息
        StatisticsEventListener.StatisticsReport stats = crawler.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.getElapsedTime() > 0);
        
        // 停止爬虫
        crawler.stop();
        assertFalse(crawler.isRunning());
        
        // 验证最终统计
        StatisticsEventListener.StatisticsReport finalStats = crawler.getStatistics();
        assertNotNull(finalStats);
        assertTrue(finalStats.getElapsedTime() > 0);
    }
    
    @Test
    @DisplayName("重复启动和停止测试")
    void testMultipleStartStop() {
        List<String> seedUrls = Arrays.asList("https://httpbin.org/html");
        
        // 首次启动
        crawler.start(seedUrls);
        assertTrue(crawler.isRunning());
        
        // 重复启动应该被忽略
        crawler.start(seedUrls);
        assertTrue(crawler.isRunning());
        
        // 停止
        crawler.stop();
        assertFalse(crawler.isRunning());
        
        // 重复停止应该不出错
        crawler.stop();
        assertFalse(crawler.isRunning());
    }
    
    @Test
    @DisplayName("配置验证测试")
    void testConfigValidation() {
        // 测试有效配置
        CrawlerConfig validConfig = new CrawlerConfig.Builder()
                .threadPoolSize(1)
                .connectionTimeout(1, TimeUnit.SECONDS)
                .build();
        
        assertDoesNotThrow(() -> new ModernCrawlerEngine(validConfig));
        
        // 测试默认配置
        assertDoesNotThrow(() -> ModernCrawlerEngine.createDefault());
    }
    
    @Test
    @DisplayName("事件系统集成测试")
    void testEventSystemIntegration() throws InterruptedException {
        TestEventCollector eventCollector = new TestEventCollector();
        crawler.getEventPublisher().addListener(eventCollector);
        
        List<String> seedUrls = Arrays.asList("https://httpbin.org/html");
        
        // 启动爬虫
        crawler.start(seedUrls);
        
        // 等待一些事件
        Thread.sleep(1000);
        
        // 停止爬虫
        crawler.stop();
        
        // 验证事件被触发
        assertTrue(eventCollector.getEventCount() > 0);
        assertTrue(eventCollector.hasEventType(io.leavesfly.crawler.core.event.CrawlEventType.CRAWLER_STARTED));
        assertTrue(eventCollector.hasEventType(io.leavesfly.crawler.core.event.CrawlEventType.CRAWLER_STOPPED));
    }
    
    @Test
    @DisplayName("统计功能集成测试")
    void testStatisticsIntegration() throws InterruptedException {
        List<String> seedUrls = Arrays.asList("https://httpbin.org/status/200");
        
        // 获取初始统计
        StatisticsEventListener.StatisticsReport initialStats = crawler.getStatistics();
        assertEquals(0, initialStats.getTotalPagesFetched());
        assertEquals(0, initialStats.getTotalFetchErrors());
        
        // 启动爬虫
        crawler.start(seedUrls);
        Thread.sleep(2000);
        crawler.stop();
        
        // 检查最终统计
        StatisticsEventListener.StatisticsReport finalStats = crawler.getStatistics();
        assertTrue(finalStats.getElapsedTime() > 0);
        // 注意：由于网络请求的不确定性，我们只检查统计功能是否工作，不检查具体数值
    }
    
    /**
     * 测试用事件收集器
     */
    static class TestEventCollector implements io.leavesfly.crawler.core.event.CrawlEventListener {
        private int eventCount = 0;
        private final java.util.Set<io.leavesfly.crawler.core.event.CrawlEventType> receivedEventTypes = 
            new java.util.HashSet<>();
        
        @Override
        public void onEvent(io.leavesfly.crawler.core.event.CrawlEvent event) {
            eventCount++;
            receivedEventTypes.add(event.getType());
        }
        
        public int getEventCount() {
            return eventCount;
        }
        
        public boolean hasEventType(io.leavesfly.crawler.core.event.CrawlEventType eventType) {
            return receivedEventTypes.contains(eventType);
        }
        
        @Override
        public String getListenerName() {
            return "TestEventCollector";
        }
    }
}