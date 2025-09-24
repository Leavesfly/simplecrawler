package io.leavesfly.crawler.strategy;

import io.leavesfly.crawler.strategy.impl.MeiTuanCrawlStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * 爬取策略管理器测试
 * 
 * @author yefei.yf
 */
@DisplayName("爬取策略管理器测试")
class CrawlStrategyManagerTest {
    
    private CrawlStrategyManager manager;
    private TestCrawlStrategy testStrategy;
    
    @BeforeEach
    void setUp() {
        manager = new CrawlStrategyManager();
        testStrategy = new TestCrawlStrategy();
    }
    
    @Test
    @DisplayName("注册和移除策略")
    void testRegisterAndRemoveStrategy() {
        assertEquals(0, manager.getStrategyCount());
        
        // 注册策略
        manager.registerStrategy(testStrategy);
        assertEquals(1, manager.getStrategyCount());
        
        // 重复注册相同策略
        manager.registerStrategy(testStrategy);
        assertEquals(1, manager.getStrategyCount());
        
        // 移除策略
        manager.removeStrategy(testStrategy);
        assertEquals(0, manager.getStrategyCount());
    }
    
    @Test
    @DisplayName("选择策略")
    void testSelectStrategy() {
        manager.registerStrategy(testStrategy);
        
        // 测试支持的URL
        CrawlStrategy selected = manager.selectStrategy("https://test.com/page");
        assertNotNull(selected);
        assertEquals(testStrategy, selected);
        
        // 测试不支持的URL
        CrawlStrategy notSelected = manager.selectStrategy("https://other.com/page");
        assertNull(notSelected);
    }
    
    @Test
    @DisplayName("策略优先级排序")
    void testStrategyPriorityOrdering() {
        TestCrawlStrategy highPriorityStrategy = new TestCrawlStrategy(5, "HighPriority");
        TestCrawlStrategy lowPriorityStrategy = new TestCrawlStrategy(10, "LowPriority");
        
        // 注册低优先级策略
        manager.registerStrategy(lowPriorityStrategy);
        // 注册高优先级策略
        manager.registerStrategy(highPriorityStrategy);
        
        List<CrawlStrategy> strategies = manager.getAllStrategies();
        assertEquals(2, strategies.size());
        
        // 验证高优先级策略在前
        assertEquals(highPriorityStrategy, strategies.get(0));
        assertEquals(lowPriorityStrategy, strategies.get(1));
    }
    
    @Test
    @DisplayName("清空所有策略")
    void testClearStrategies() {
        manager.registerStrategy(testStrategy);
        manager.registerStrategy(new MeiTuanCrawlStrategy());
        assertEquals(2, manager.getStrategyCount());
        
        manager.clearStrategies();
        assertEquals(0, manager.getStrategyCount());
        assertTrue(manager.getAllStrategies().isEmpty());
    }
    
    @Test
    @DisplayName("获取所有策略")
    void testGetAllStrategies() {
        assertTrue(manager.getAllStrategies().isEmpty());
        
        manager.registerStrategy(testStrategy);
        MeiTuanCrawlStrategy meiTuanStrategy = new MeiTuanCrawlStrategy();
        manager.registerStrategy(meiTuanStrategy);
        
        List<CrawlStrategy> strategies = manager.getAllStrategies();
        assertEquals(2, strategies.size());
        assertTrue(strategies.contains(testStrategy));
        assertTrue(strategies.contains(meiTuanStrategy));
    }
    
    /**
     * 测试用爬取策略
     */
    static class TestCrawlStrategy implements CrawlStrategy {
        private final int priority;
        private final String name;
        
        public TestCrawlStrategy() {
            this(100, "TestStrategy");
        }
        
        public TestCrawlStrategy(int priority, String name) {
            this.priority = priority;
            this.name = name;
        }
        
        @Override
        public boolean supports(String url) {
            return url != null && url.contains("test.com");
        }
        
        @Override
        public String getStrategyName() {
            return name;
        }
        
        @Override
        public java.util.List<String> extractUrls(io.leavesfly.crawler.domain.CrawlContext context) {
            return java.util.Arrays.asList("https://test.com/extracted");
        }
        
        @Override
        public Object extractData(io.leavesfly.crawler.domain.CrawlContext context) {
            return "test data";
        }
        
        @Override
        public int getPriority() {
            return priority;
        }
    }
}