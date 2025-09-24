package io.leavesfly.crawler.core.event;

import io.leavesfly.crawler.core.event.listener.LoggingEventListener;
import io.leavesfly.crawler.core.event.listener.StatisticsEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 爬虫事件发布器测试
 * 
 * @author yefei.yf
 */
@DisplayName("爬虫事件发布器测试")
class CrawlEventPublisherTest {
    
    private CrawlEventPublisher publisher;
    private TestEventListener testListener;
    
    @BeforeEach
    void setUp() {
        publisher = new CrawlEventPublisher();
        testListener = new TestEventListener();
    }
    
    @Test
    @DisplayName("添加和移除监听器")
    void testAddAndRemoveListener() {
        assertEquals(0, publisher.getListenerCount());
        
        publisher.addListener(testListener);
        assertEquals(1, publisher.getListenerCount());
        
        // 重复添加相同监听器
        publisher.addListener(testListener);
        assertEquals(1, publisher.getListenerCount());
        
        publisher.removeListener(testListener);
        assertEquals(0, publisher.getListenerCount());
    }
    
    @Test
    @DisplayName("发布事件")
    void testPublishEvent() {
        publisher.addListener(testListener);
        
        CrawlEvent event = CrawlEvent.builder(CrawlEventType.CRAWLER_STARTED)
                .url("https://example.com")
                .data("test", "value")
                .build();
        
        publisher.publishEvent(event);
        
        assertTrue(testListener.eventReceived);
        assertEquals(event, testListener.lastEvent);
    }
    
    @Test
    @DisplayName("发布null事件")
    void testPublishNullEvent() {
        publisher.addListener(testListener);
        publisher.publishEvent(null);
        
        assertFalse(testListener.eventReceived);
    }
    
    @Test
    @DisplayName("监听器支持的事件类型")
    void testListenerSupportsEventType() {
        CrawlEventListener selectiveListener = new CrawlEventListener() {
            @Override
            public void onEvent(CrawlEvent event) {
                // 处理事件
            }
            
            @Override
            public boolean supports(CrawlEventType eventType) {
                return eventType == CrawlEventType.CRAWLER_STARTED;
            }
        };
        
        TestEventListener generalListener = new TestEventListener();
        
        publisher.addListener(selectiveListener);
        publisher.addListener(generalListener);
        
        // 发布支持的事件类型
        CrawlEvent supportedEvent = CrawlEvent.builder(CrawlEventType.CRAWLER_STARTED).build();
        publisher.publishEvent(supportedEvent);
        assertTrue(generalListener.eventReceived);
        
        // 重置状态
        generalListener.reset();
        
        // 发布不支持的事件类型
        CrawlEvent unsupportedEvent = CrawlEvent.builder(CrawlEventType.PAGE_FETCH_SUCCESS).build();
        publisher.publishEvent(unsupportedEvent);
        assertTrue(generalListener.eventReceived); // 通用监听器仍会收到
    }
    
    @Test
    @DisplayName("异步模式")
    void testAsyncMode() throws InterruptedException {
        CrawlEventPublisher asyncPublisher = new CrawlEventPublisher(true);
        CountDownLatch latch = new CountDownLatch(1);
        
        CrawlEventListener asyncListener = new CrawlEventListener() {
            @Override
            public void onEvent(CrawlEvent event) {
                latch.countDown();
            }
        };
        
        asyncPublisher.addListener(asyncListener);
        
        CrawlEvent event = CrawlEvent.builder(CrawlEventType.CRAWLER_STARTED).build();
        asyncPublisher.publishEvent(event);
        
        // 等待异步处理完成
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        
        asyncPublisher.shutdown();
    }
    
    @Test
    @DisplayName("清空监听器")
    void testClearListeners() {
        publisher.addListener(testListener);
        publisher.addListener(new LoggingEventListener());
        assertEquals(2, publisher.getListenerCount());
        
        publisher.clearListeners();
        assertEquals(0, publisher.getListenerCount());
    }
    
    @Test
    @DisplayName("监听器异常处理")
    void testListenerExceptionHandling() {
        CrawlEventListener faultyListener = new CrawlEventListener() {
            @Override
            public void onEvent(CrawlEvent event) {
                throw new RuntimeException("Test exception");
            }
        };
        
        publisher.addListener(faultyListener);
        publisher.addListener(testListener);
        
        CrawlEvent event = CrawlEvent.builder(CrawlEventType.CRAWLER_STARTED).build();
        
        // 异常监听器不应影响其他监听器
        assertDoesNotThrow(() -> publisher.publishEvent(event));
        assertTrue(testListener.eventReceived);
    }
    
    @Test
    @DisplayName("关闭发布器")
    void testShutdown() {
        CrawlEventPublisher asyncPublisher = new CrawlEventPublisher(true);
        asyncPublisher.shutdown();
        
        // 同步模式的发布器关闭应该不会出错
        publisher.shutdown();
        assertDoesNotThrow(() -> publisher.shutdown()); // 重复关闭
    }
    
    /**
     * 测试用监听器
     */
    static class TestEventListener implements CrawlEventListener {
        boolean eventReceived = false;
        CrawlEvent lastEvent = null;
        
        @Override
        public void onEvent(CrawlEvent event) {
            this.eventReceived = true;
            this.lastEvent = event;
        }
        
        void reset() {
            this.eventReceived = false;
            this.lastEvent = null;
        }
        
        @Override
        public String getListenerName() {
            return "TestEventListener";
        }
    }
}