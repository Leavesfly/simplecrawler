package io.leavesfly.crawler.core.event.listener;

import io.leavesfly.crawler.core.event.CrawlEvent;
import io.leavesfly.crawler.core.event.CrawlEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 统计事件监听器测试
 * 
 * @author yefei.yf
 */
@DisplayName("统计事件监听器测试")
class StatisticsEventListenerTest {
    
    private StatisticsEventListener listener;
    
    @BeforeEach
    void setUp() {
        listener = new StatisticsEventListener();
    }
    
    @Test
    @DisplayName("初始状态")
    void testInitialState() {
        StatisticsEventListener.StatisticsReport report = listener.getReport();
        
        assertEquals(0, report.getTotalPagesFetched());
        assertEquals(0, report.getTotalPagesParsed());
        assertEquals(0, report.getTotalFetchErrors());
        assertEquals(0, report.getTotalParseErrors());
        assertEquals(0, report.getTotalUrlsQueued());
        assertEquals(-1, report.getStartTime());
        assertEquals(-1, report.getEndTime());
        assertEquals(0.0, report.getSuccessRate());
    }
    
    @Test
    @DisplayName("处理爬虫启动事件")
    void testCrawlerStartedEvent() {
        CrawlEvent event = CrawlEvent.builder(CrawlEventType.CRAWLER_STARTED).build();
        listener.onEvent(event);
        
        StatisticsEventListener.StatisticsReport report = listener.getReport();
        assertTrue(report.getStartTime() > 0);
        assertEquals(-1, report.getEndTime());
        assertTrue(report.getElapsedTime() >= 0);
    }
    
    @Test
    @DisplayName("处理爬虫停止事件")
    void testCrawlerStoppedEvent() {
        // 先启动爬虫
        CrawlEvent startEvent = CrawlEvent.builder(CrawlEventType.CRAWLER_STARTED).build();
        listener.onEvent(startEvent);
        
        // 停止爬虫
        CrawlEvent stopEvent = CrawlEvent.builder(CrawlEventType.CRAWLER_STOPPED).build();
        listener.onEvent(stopEvent);
        
        StatisticsEventListener.StatisticsReport report = listener.getReport();
        assertTrue(report.getStartTime() > 0);
        assertTrue(report.getEndTime() > 0);
        assertTrue(report.getEndTime() >= report.getStartTime());
    }
    
    @Test
    @DisplayName("处理页面获取成功事件")
    void testPageFetchSuccessEvent() {
        CrawlEvent event = CrawlEvent.builder(CrawlEventType.PAGE_FETCH_SUCCESS)
                .url("https://example.com")
                .build();
        
        listener.onEvent(event);
        listener.onEvent(event);
        listener.onEvent(event);
        
        StatisticsEventListener.StatisticsReport report = listener.getReport();
        assertEquals(3, report.getTotalPagesFetched());
        assertEquals(0, report.getTotalFetchErrors());
        assertEquals(1.0, report.getSuccessRate());
    }
    
    @Test
    @DisplayName("处理页面获取失败事件")
    void testPageFetchFailedEvent() {
        CrawlEvent event = CrawlEvent.builder(CrawlEventType.PAGE_FETCH_FAILED)
                .url("https://example.com")
                .build();
        
        listener.onEvent(event);
        listener.onEvent(event);
        
        StatisticsEventListener.StatisticsReport report = listener.getReport();
        assertEquals(0, report.getTotalPagesFetched());
        assertEquals(2, report.getTotalFetchErrors());
        assertEquals(0.0, report.getSuccessRate());
    }
    
    @Test
    @DisplayName("处理页面解析事件")
    void testPageParseEvents() {
        CrawlEvent successEvent = CrawlEvent.builder(CrawlEventType.PAGE_PARSE_SUCCESS)
                .url("https://example.com")
                .build();
        
        CrawlEvent failedEvent = CrawlEvent.builder(CrawlEventType.PAGE_PARSE_FAILED)
                .url("https://example.com")
                .build();
        
        listener.onEvent(successEvent);
        listener.onEvent(successEvent);
        listener.onEvent(failedEvent);
        
        StatisticsEventListener.StatisticsReport report = listener.getReport();
        assertEquals(2, report.getTotalPagesParsed());
        assertEquals(1, report.getTotalParseErrors());
    }
    
    @Test
    @DisplayName("处理URL入队事件")
    void testUrlQueuedEvent() {
        CrawlEvent event = CrawlEvent.builder(CrawlEventType.URL_QUEUED)
                .url("https://example.com")
                .build();
        
        for (int i = 0; i < 5; i++) {
            listener.onEvent(event);
        }
        
        StatisticsEventListener.StatisticsReport report = listener.getReport();
        assertEquals(5, report.getTotalUrlsQueued());
    }
    
    @Test
    @DisplayName("成功率计算")
    void testSuccessRateCalculation() {
        // 成功2次，失败1次
        CrawlEvent successEvent = CrawlEvent.builder(CrawlEventType.PAGE_FETCH_SUCCESS).build();
        CrawlEvent failedEvent = CrawlEvent.builder(CrawlEventType.PAGE_FETCH_FAILED).build();
        
        listener.onEvent(successEvent);
        listener.onEvent(successEvent);
        listener.onEvent(failedEvent);
        
        StatisticsEventListener.StatisticsReport report = listener.getReport();
        assertEquals(2.0 / 3.0, report.getSuccessRate(), 0.001);
    }
    
    @Test
    @DisplayName("重置计数器")
    void testResetCounters() {
        // 添加一些事件
        listener.onEvent(CrawlEvent.builder(CrawlEventType.PAGE_FETCH_SUCCESS).build());
        listener.onEvent(CrawlEvent.builder(CrawlEventType.PAGE_PARSE_SUCCESS).build());
        listener.onEvent(CrawlEvent.builder(CrawlEventType.URL_QUEUED).build());
        
        StatisticsEventListener.StatisticsReport report = listener.getReport();
        assertTrue(report.getTotalPagesFetched() > 0);
        
        // 重置计数器
        listener.resetCounters();
        
        report = listener.getReport();
        assertEquals(0, report.getTotalPagesFetched());
        assertEquals(0, report.getTotalPagesParsed());
        assertEquals(0, report.getTotalUrlsQueued());
    }
    
    @Test
    @DisplayName("事件类型支持")
    void testEventTypeSupport() {
        assertTrue(listener.supports(CrawlEventType.CRAWLER_STARTED));
        assertTrue(listener.supports(CrawlEventType.CRAWLER_STOPPED));
        assertTrue(listener.supports(CrawlEventType.PAGE_FETCH_SUCCESS));
        assertTrue(listener.supports(CrawlEventType.PAGE_FETCH_FAILED));
        assertTrue(listener.supports(CrawlEventType.PAGE_PARSE_SUCCESS));
        assertTrue(listener.supports(CrawlEventType.PAGE_PARSE_FAILED));
        assertTrue(listener.supports(CrawlEventType.URL_QUEUED));
        
        assertFalse(listener.supports(CrawlEventType.ERROR_OCCURRED));
    }
    
    @Test
    @DisplayName("统计报告toString")
    void testStatisticsReportToString() {
        CrawlEvent startEvent = CrawlEvent.builder(CrawlEventType.CRAWLER_STARTED).build();
        listener.onEvent(startEvent);
        
        StatisticsEventListener.StatisticsReport report = listener.getReport();
        String reportString = report.toString();
        
        assertNotNull(reportString);
        assertTrue(reportString.contains("StatisticsReport"));
        assertTrue(reportString.contains("totalPagesFetched"));
        assertTrue(reportString.contains("successRate"));
        assertTrue(reportString.contains("elapsedTime"));
    }
}