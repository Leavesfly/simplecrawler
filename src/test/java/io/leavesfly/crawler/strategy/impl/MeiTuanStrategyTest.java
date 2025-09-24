package io.leavesfly.crawler.strategy.impl;

import io.leavesfly.crawler.domain.CrawlContext;
import io.leavesfly.crawler.domain.RawPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * 美团爬取策略测试
 * 
 * @author yefei.yf
 */
@DisplayName("美团爬取策略测试")
class MeiTuanCrawlStrategyTest {
    
    private MeiTuanCrawlStrategy strategy;
    private CrawlContext context;
    
    @BeforeEach
    void setUp() {
        strategy = new MeiTuanCrawlStrategy();
        context = new CrawlContext("https://www.meituan.com/shop/123");
    }
    
    @Test
    @DisplayName("URL支持性检查")
    void testSupports() {
        // 支持的URL
        assertTrue(strategy.supports("https://www.meituan.com/"));
        assertTrue(strategy.supports("https://bj.meituan.com/shop/123"));
        assertTrue(strategy.supports("http://meituan.com/category/food"));
        
        // 不支持的URL
        assertFalse(strategy.supports("https://www.taobao.com/"));
        assertFalse(strategy.supports("https://example.com"));
        assertFalse(strategy.supports(null));
        assertFalse(strategy.supports(""));
    }
    
    @Test
    @DisplayName("策略名称")
    void testGetStrategyName() {
        assertEquals("MeiTuanCrawlStrategy", strategy.getStrategyName());
    }
    
    @Test
    @DisplayName("请求头配置")
    void testGetHeaders() {
        String url = "https://www.meituan.com/shop/123";
        Map<String, String> headers = strategy.getHeaders(url);
        
        assertNotNull(headers);
        assertTrue(headers.containsKey("User-Agent"));
        assertTrue(headers.containsKey("Accept"));
        assertTrue(headers.containsKey("Referer"));
        assertEquals("https://www.meituan.com/", headers.get("Referer"));
    }
    
    @Test
    @DisplayName("优先级和配置")
    void testPriorityAndConfig() {
        assertEquals(10, strategy.getPriority()); // 高优先级
        assertEquals(2000, strategy.getRequestDelay("https://test.com")); // 2秒延迟
        assertEquals(5, strategy.getMaxRetries()); // 5次重试
    }
    
    @Test
    @DisplayName("提取URL - 空内容")
    void testExtractUrlsWithEmptyContent() {
        context.setRawPage(null);
        List<String> urls = strategy.extractUrls(context);
        assertNotNull(urls);
        assertTrue(urls.isEmpty());
        
        context.setRawPage(new RawPage("https://test.com", "UTF-8", null));
        urls = strategy.extractUrls(context);
        assertNotNull(urls);
        assertTrue(urls.isEmpty());
    }
    
    @Test
    @DisplayName("提取数据 - 空内容")
    void testExtractDataWithEmptyContent() {
        context.setRawPage(null);
        Object data = strategy.extractData(context);
        assertNull(data);
        
        context.setRawPage(new RawPage("https://test.com", "UTF-8", null));
        data = strategy.extractData(context);
        assertNull(data);
    }
    
    @Test
    @DisplayName("内容预处理")
    void testPreprocessContent() {
        String htmlWithScripts = "<html><head><style>.test{color:red;}</style><script>alert('test');</script></head>" +
                "<body><h1>测试内容</h1><script>console.log('test');</script></body></html>";
        
        RawPage rawPage = new RawPage("https://test.com", "UTF-8", htmlWithScripts);
        String processed = strategy.preprocessContent(rawPage);
        
        assertNotNull(processed);
        // 验证脚本和样式被移除
        assertFalse(processed.contains("<script>"));
        assertFalse(processed.contains("<style>"));
        assertTrue(processed.contains("<h1>测试内容</h1>"));
    }
    
    @Test
    @DisplayName("异常处理")
    void testExceptionHandling() {
        // 测试解析异常不会导致程序崩溃
        String invalidHtml = "<html><body><div><span></div></span></body></html>";
        RawPage rawPage = new RawPage("https://www.meituan.com/shop/123", "UTF-8", invalidHtml);
        context.setRawPage(rawPage);
        
        assertDoesNotThrow(() -> {
            List<String> urls = strategy.extractUrls(context);
            assertNotNull(urls);
            
            Object data = strategy.extractData(context);
            // 可能返回null或者部分数据，但不应该抛出异常
        });
    }
}