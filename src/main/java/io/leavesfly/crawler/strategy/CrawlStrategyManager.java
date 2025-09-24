package io.leavesfly.crawler.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 爬取策略管理器
 * 负责管理和选择合适的爬取策略
 * 
 * @author yefei.yf
 */
public class CrawlStrategyManager {
    
    private static final Logger logger = LoggerFactory.getLogger(CrawlStrategyManager.class);
    
    private final List<CrawlStrategy> strategies = new CopyOnWriteArrayList<>();
    
    /**
     * 注册爬取策略
     * 
     * @param strategy 爬取策略
     */
    public void registerStrategy(CrawlStrategy strategy) {
        if (strategy != null && !strategies.contains(strategy)) {
            strategies.add(strategy);
            // 按优先级排序
            strategies.sort(Comparator.comparingInt(CrawlStrategy::getPriority));
            logger.info("注册爬取策略: {}, 优先级: {}", strategy.getStrategyName(), strategy.getPriority());
        }
    }
    
    /**
     * 移除爬取策略
     * 
     * @param strategy 爬取策略
     */
    public void removeStrategy(CrawlStrategy strategy) {
        if (strategies.remove(strategy)) {
            logger.info("移除爬取策略: {}", strategy.getStrategyName());
        }
    }
    
    /**
     * 根据URL选择合适的爬取策略
     * 
     * @param url 待爬取的URL
     * @return 匹配的策略，如果没有找到返回null
     */
    public CrawlStrategy selectStrategy(String url) {
        for (CrawlStrategy strategy : strategies) {
            if (strategy.supports(url)) {
                logger.debug("为URL {} 选择策略: {}", url, strategy.getStrategyName());
                return strategy;
            }
        }
        
        logger.warn("未找到匹配的爬取策略: {}", url);
        return null;
    }
    
    /**
     * 获取所有已注册的策略
     * 
     * @return 策略列表
     */
    public List<CrawlStrategy> getAllStrategies() {
        return new ArrayList<>(strategies);
    }
    
    /**
     * 获取策略数量
     * 
     * @return 策略数量
     */
    public int getStrategyCount() {
        return strategies.size();
    }
    
    /**
     * 清空所有策略
     */
    public void clearStrategies() {
        strategies.clear();
        logger.info("清空所有爬取策略");
    }
}