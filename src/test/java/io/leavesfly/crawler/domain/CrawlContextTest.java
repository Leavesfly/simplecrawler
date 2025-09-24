package io.leavesfly.crawler.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 爬虫上下文测试
 * 
 * @author yefei.yf
 */
@DisplayName("爬虫上下文测试")
class CrawlContextTest {
    
    private CrawlContext context;
    private static final String TEST_URL = "https://example.com";
    
    @BeforeEach
    void setUp() {
        context = new CrawlContext(TEST_URL);
    }
    
    @Test
    @DisplayName("创建上下文")
    void testCreateContext() {
        assertNotNull(context);
        assertEquals(TEST_URL, context.getCurrentUrl());
        assertTrue(context.getStartTime() > 0);
        assertEquals(0, context.getRetryCount());
        assertNull(context.getRawPage());
    }
    
    @Test
    @DisplayName("属性操作")
    void testAttributeOperations() {
        // 设置属性
        context.setAttribute("key1", "value1");
        context.setAttribute("key2", 42);
        context.setAttribute("key3", true);
        
        // 获取属性
        assertEquals("value1", context.getAttribute("key1"));
        assertEquals(42, context.getAttribute("key2"));
        assertEquals(true, context.getAttribute("key3"));
        
        // 类型转换获取
        assertEquals("value1", context.getAttribute("key1", String.class));
        assertEquals(Integer.valueOf(42), context.getAttribute("key2", Integer.class));
        assertEquals(Boolean.TRUE, context.getAttribute("key3", Boolean.class));
        
        // 获取不存在的属性
        assertNull(context.getAttribute("nonexistent"));
        assertNull(context.getAttribute("nonexistent", String.class));
        
        // 类型不匹配
        assertNull(context.getAttribute("key1", Integer.class));
    }
    
    @Test
    @DisplayName("属性存在性检查")
    void testContainsAttribute() {
        assertFalse(context.containsAttribute("key1"));
        
        context.setAttribute("key1", "value1");
        assertTrue(context.containsAttribute("key1"));
        
        context.removeAttribute("key1");
        assertFalse(context.containsAttribute("key1"));
    }
    
    @Test
    @DisplayName("移除属性")
    void testRemoveAttribute() {
        context.setAttribute("key1", "value1");
        assertEquals("value1", context.getAttribute("key1"));
        
        Object removed = context.removeAttribute("key1");
        assertEquals("value1", removed);
        assertNull(context.getAttribute("key1"));
        
        // 移除不存在的属性
        assertNull(context.removeAttribute("nonexistent"));
    }
    
    @Test
    @DisplayName("元数据操作")
    void testMetadataOperations() {
        context.setMetadata("source", "web");
        context.setMetadata("timestamp", "2023-01-01");
        
        assertEquals("web", context.getMetadata("source"));
        assertEquals("2023-01-01", context.getMetadata("timestamp"));
        assertNull(context.getMetadata("nonexistent"));
    }
    
    @Test
    @DisplayName("重试次数操作")
    void testRetryCount() {
        assertEquals(0, context.getRetryCount());
        
        context.incrementRetryCount();
        assertEquals(1, context.getRetryCount());
        
        context.incrementRetryCount();
        context.incrementRetryCount();
        assertEquals(3, context.getRetryCount());
    }
    
    @Test
    @DisplayName("原始页面操作")
    void testRawPageOperations() {
        assertNull(context.getRawPage());
        
        RawPage rawPage = new RawPage(TEST_URL, "UTF-8", "<html></html>");
        context.setRawPage(rawPage);
        
        assertNotNull(context.getRawPage());
        assertEquals(rawPage, context.getRawPage());
        assertEquals(TEST_URL, context.getRawPage().getUrl());
    }
    
    @Test
    @DisplayName("执行时长计算")
    void testElapsedTime() throws InterruptedException {
        long elapsed1 = context.getElapsedTime();
        assertTrue(elapsed1 >= 0);
        
        Thread.sleep(10); // 等待10毫秒
        
        long elapsed2 = context.getElapsedTime();
        assertTrue(elapsed2 >= elapsed1);
        assertTrue(elapsed2 >= 10);
    }
    
    @Test
    @DisplayName("URL修改")
    void testUrlModification() {
        assertEquals(TEST_URL, context.getCurrentUrl());
        
        String newUrl = "https://newexample.com";
        context.setCurrentUrl(newUrl);
        assertEquals(newUrl, context.getCurrentUrl());
    }
    
    @Test
    @DisplayName("toString方法")
    void testToString() {
        String contextString = context.toString();
        
        assertNotNull(contextString);
        assertTrue(contextString.contains("CrawlContext"));
        assertTrue(contextString.contains(TEST_URL));
        assertTrue(contextString.contains("retryCount=0"));
        assertTrue(contextString.contains("elapsedTime"));
        assertTrue(contextString.contains("attributeCount=0"));
    }
}