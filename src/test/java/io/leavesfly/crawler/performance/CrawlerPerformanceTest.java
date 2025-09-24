package io.leavesfly.crawler.performance;

import io.leavesfly.crawler.conf.CrawlerConfig;
import io.leavesfly.crawler.core.ModernCrawlerEngine;
import io.leavesfly.crawler.core.event.listener.StatisticsEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 爬虫性能测试
 * 
 * 注意：这些测试依赖网络环境，在CI/CD环境中可能需要跳过
 * 使用环境变量 PERFORMANCE_TEST=true 来启用这些测试
 * 
 * @author yefei.yf
 */
@DisplayName("爬虫性能测试")
@EnabledIfEnvironmentVariable(named = "PERFORMANCE_TEST", matches = "true")
class CrawlerPerformanceTest {
    
    private CrawlerConfig config;
    
    @BeforeEach
    void setUp() {
        config = new CrawlerConfig.Builder()
                .connectionTimeout(10, TimeUnit.SECONDS)
                .socketTimeout(15, TimeUnit.SECONDS)
                .threadPoolSize(5)
                .delayBetweenRequests(100) // 减少延迟以提高测试速度
                .queueCapacity(1000)
                .maxRetries(2)
                .build();
    }
    
    @Test
    @DisplayName("单线程性能测试")
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void testSingleThreadPerformance() throws InterruptedException {
        CrawlerConfig singleThreadConfig = new CrawlerConfig.Builder()
                .connectionTimeout(5, TimeUnit.SECONDS)
                .threadPoolSize(1) // 单线程
                .delayBetweenRequests(100)
                .build();
        
        ModernCrawlerEngine crawler = new ModernCrawlerEngine(singleThreadConfig);
        
        List<String> testUrls = generateTestUrls(10);
        long startTime = System.currentTimeMillis();
        
        crawler.start(testUrls);
        Thread.sleep(20000); // 运行20秒
        crawler.stop();
        
        long elapsedTime = System.currentTimeMillis() - startTime;
        StatisticsEventListener.StatisticsReport stats = crawler.getStatistics();
        
        // 性能指标验证
        assertTrue(elapsedTime < 60000, "单线程测试应在60秒内完成");
        assertTrue(stats.getElapsedTime() > 0, "统计时间应大于0");
        
        // 计算吞吐量（页面/秒）
        if (stats.getTotalPagesFetched() > 0) {
            double throughput = (double) stats.getTotalPagesFetched() / (elapsedTime / 1000.0);
            System.out.println("单线程吞吐量: " + String.format("%.2f", throughput) + " 页面/秒");
        }
        
        System.out.println("单线程性能统计: " + stats);
    }
    
    @Test
    @DisplayName("多线程性能测试")
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void testMultiThreadPerformance() throws InterruptedException {
        CrawlerConfig multiThreadConfig = new CrawlerConfig.Builder()
                .connectionTimeout(5, TimeUnit.SECONDS)
                .threadPoolSize(10) // 多线程
                .delayBetweenRequests(100)
                .build();
        
        ModernCrawlerEngine crawler = new ModernCrawlerEngine(multiThreadConfig);
        
        List<String> testUrls = generateTestUrls(50);
        long startTime = System.currentTimeMillis();
        
        crawler.start(testUrls);
        Thread.sleep(30000); // 运行30秒
        crawler.stop();
        
        long elapsedTime = System.currentTimeMillis() - startTime;
        StatisticsEventListener.StatisticsReport stats = crawler.getStatistics();
        
        // 性能指标验证
        assertTrue(elapsedTime < 60000, "多线程测试应在60秒内完成");
        assertTrue(stats.getElapsedTime() > 0, "统计时间应大于0");
        
        // 计算吞吐量（页面/秒）
        if (stats.getTotalPagesFetched() > 0) {
            double throughput = (double) stats.getTotalPagesFetched() / (elapsedTime / 1000.0);
            System.out.println("多线程吞吐量: " + String.format("%.2f", throughput) + " 页面/秒");
            
            // 多线程应该比单线程有更好的性能（在网络IO密集型任务中）
            assertTrue(throughput > 0, "吞吐量应大于0");
        }
        
        System.out.println("多线程性能统计: " + stats);
    }
    
    @Test
    @DisplayName("内存使用测试")
    @Timeout(value = 120, unit = TimeUnit.SECONDS)
    void testMemoryUsage() throws InterruptedException {
        ModernCrawlerEngine crawler = new ModernCrawlerEngine(config);
        
        // 记录初始内存使用
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        List<String> testUrls = generateTestUrls(100);
        
        crawler.start(testUrls);
        Thread.sleep(60000); // 运行1分钟
        crawler.stop();
        
        // 强制垃圾回收
        System.gc();
        Thread.sleep(1000);
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;
        
        System.out.println("初始内存: " + formatBytes(initialMemory));
        System.out.println("最终内存: " + formatBytes(finalMemory));
        System.out.println("内存增长: " + formatBytes(memoryIncrease));
        
        // 验证内存增长在合理范围内（小于100MB）
        assertTrue(memoryIncrease < 100 * 1024 * 1024, 
                   "内存增长应小于100MB，实际增长: " + formatBytes(memoryIncrease));
        
        StatisticsEventListener.StatisticsReport stats = crawler.getStatistics();
        System.out.println("内存测试统计: " + stats);
    }
    
    @Test
    @DisplayName("并发压力测试")
    @Timeout(value = 120, unit = TimeUnit.SECONDS)
    void testConcurrencyStress() throws InterruptedException {
        CrawlerConfig stressConfig = new CrawlerConfig.Builder()
                .connectionTimeout(5, TimeUnit.SECONDS)
                .threadPoolSize(20) // 高并发
                .delayBetweenRequests(50) // 很短的延迟
                .queueCapacity(2000)
                .build();
        
        ModernCrawlerEngine crawler = new ModernCrawlerEngine(stressConfig);
        
        // 生成大量测试URL
        List<String> testUrls = generateTestUrls(200);
        
        long startTime = System.currentTimeMillis();
        crawler.start(testUrls);
        
        // 监控队列大小变化
        for (int i = 0; i < 60; i++) { // 监控60秒
            Thread.sleep(1000);
            int queueSize = crawler.getQueueSize();
            if (queueSize == 0) {
                break; // 队列已空，可以提前结束
            }
            if (i % 10 == 0) {
                System.out.println("队列大小: " + queueSize + ", 已运行: " + (i + 1) + "秒");
            }
        }
        
        crawler.stop();
        long elapsedTime = System.currentTimeMillis() - startTime;
        
        StatisticsEventListener.StatisticsReport stats = crawler.getStatistics();
        
        // 验证系统在高并发下的稳定性
        assertTrue(stats.getElapsedTime() > 0, "统计时间应大于0");
        assertFalse(crawler.isRunning(), "爬虫应已停止");
        
        // 计算处理效率
        if (stats.getTotalPagesFetched() > 0) {
            double efficiency = (double) stats.getTotalPagesFetched() / testUrls.size() * 100;
            System.out.println("处理效率: " + String.format("%.1f", efficiency) + "%");
        }
        
        System.out.println("并发压力测试统计: " + stats);
        System.out.println("测试总时长: " + elapsedTime + "ms");
    }
    
    /**
     * 生成测试URL列表
     */
    private List<String> generateTestUrls(int count) {
        List<String> urls = new ArrayList<>();
        
        // 使用httpbin.org作为测试服务，它提供可靠的HTTP响应
        String[] endpoints = {
            "https://httpbin.org/html",
            "https://httpbin.org/json",
            "https://httpbin.org/xml",
            "https://httpbin.org/status/200",
            "https://httpbin.org/delay/1",
            "https://httpbin.org/uuid",
            "https://httpbin.org/base64/SFRUUCBpcyBhd2Vzb21l"
        };
        
        for (int i = 0; i < count; i++) {
            String endpoint = endpoints[i % endpoints.length];
            // 添加随机参数避免缓存
            urls.add(endpoint + "?test=" + i + "&timestamp=" + System.currentTimeMillis());
        }
        
        return urls;
    }
    
    /**
     * 格式化字节数为可读格式
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}