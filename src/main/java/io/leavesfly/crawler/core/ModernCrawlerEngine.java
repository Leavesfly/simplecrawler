package io.leavesfly.crawler.core;

import io.leavesfly.crawler.conf.CrawlerConfig;
import io.leavesfly.crawler.core.event.CrawlEvent;
import io.leavesfly.crawler.core.event.CrawlEventPublisher;
import io.leavesfly.crawler.core.event.CrawlEventType;
import io.leavesfly.crawler.core.event.listener.LoggingEventListener;
import io.leavesfly.crawler.core.event.listener.StatisticsEventListener;
import io.leavesfly.crawler.core.pipeline.PipelineProcessor;
import io.leavesfly.crawler.core.pipeline.ProcessResult;
import io.leavesfly.crawler.core.pipeline.impl.FetchProcessor;
import io.leavesfly.crawler.core.pipeline.impl.ParseProcessor;
import io.leavesfly.crawler.domain.CrawlContext;
import io.leavesfly.crawler.strategy.CrawlStrategyManager;
import io.leavesfly.crawler.strategy.impl.MeiTuanCrawlStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 现代化爬虫引擎
 * 使用多种设计模式构建的高性能、可扩展爬虫系统
 * 
 * @author yefei.yf
 */
public class ModernCrawlerEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(ModernCrawlerEngine.class);
    
    private final CrawlerConfig config;
    private final CrawlEventPublisher eventPublisher;
    private final CrawlStrategyManager strategyManager;
    private final StatisticsEventListener statisticsListener;
    private final ExecutorService executorService;
    private final BlockingQueue<String> urlQueue;
    private final AtomicBoolean running = new AtomicBoolean(false);
    
    // 处理流水线
    private PipelineProcessor pipelineHead;
    
    /**
     * 构造函数
     * 
     * @param config 爬虫配置
     */
    public ModernCrawlerEngine(CrawlerConfig config) {
        this.config = config;
        this.eventPublisher = new CrawlEventPublisher(true); // 异步事件处理
        this.strategyManager = new CrawlStrategyManager();
        this.statisticsListener = new StatisticsEventListener();
        this.urlQueue = new LinkedBlockingQueue<>(config.getQueueCapacity());
        this.executorService = Executors.newFixedThreadPool(config.getThreadPoolSize());
        
        initializeComponents();
    }
    
    /**
     * 使用默认配置创建爬虫引擎
     */
    public static ModernCrawlerEngine createDefault() {
        return new ModernCrawlerEngine(CrawlerConfig.createDefault());
    }
    
    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 设置事件监听器
        eventPublisher.addListener(new LoggingEventListener());
        eventPublisher.addListener(statisticsListener);
        
        // 注册爬取策略
        strategyManager.registerStrategy(new MeiTuanCrawlStrategy());
        
        // 构建处理流水线
        buildPipeline();
        
        logger.info("爬虫引擎初始化完成 - 配置: {}", config);
    }
    
    /**
     * 构建处理流水线
     */
    private void buildPipeline() {
        // 使用责任链模式构建处理流水线
        FetchProcessor fetchProcessor = new FetchProcessor();
        ParseProcessor parseProcessor = new ParseProcessor();
        
        // 构建处理链：获取页面 -> 解析页面
        pipelineHead = fetchProcessor;
        fetchProcessor.setNext(parseProcessor);
        
        logger.debug("处理流水线构建完成");
    }
    
    /**
     * 启动爬虫
     * 
     * @param seedUrls 种子URL列表
     */
    public void start(List<String> seedUrls) {
        if (running.compareAndSet(false, true)) {
            logger.info("启动爬虫引擎...");
            
            // 发布启动事件
            eventPublisher.publishEvent(
                CrawlEvent.builder(CrawlEventType.CRAWLER_STARTED)
                    .data("seedUrls", seedUrls)
                    .build()
            );
            
            // 添加种子URL到队列
            for (String url : seedUrls) {
                addUrl(url);
            }
            
            // 启动工作线程
            for (int i = 0; i < config.getThreadPoolSize(); i++) {
                executorService.submit(this::workerLoop);
            }
            
            logger.info("爬虫引擎启动完成，工作线程数: {}", config.getThreadPoolSize());
        } else {
            logger.warn("爬虫引擎已经在运行中");
        }
    }
    
    /**
     * 停止爬虫
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            logger.info("停止爬虫引擎...");
            
            // 关闭线程池
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            
            // 关闭事件发布器
            eventPublisher.shutdown();
            
            // 发布停止事件
            eventPublisher.publishEvent(
                CrawlEvent.builder(CrawlEventType.CRAWLER_STOPPED).build()
            );
            
            logger.info("爬虫引擎已停止");
        }
    }
    
    /**
     * 添加URL到爬取队列
     * 
     * @param url 待爬取的URL
     */
    public void addUrl(String url) {
        if (url != null && !url.trim().isEmpty()) {
            try {
                if (urlQueue.offer(url.trim(), 1, TimeUnit.SECONDS)) {
                    eventPublisher.publishEvent(
                        CrawlEvent.builder(CrawlEventType.URL_QUEUED)
                            .url(url)
                            .build()
                    );
                    logger.debug("URL已入队: {}", url);
                } else {
                    logger.warn("URL队列已满，丢弃URL: {}", url);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("添加URL被中断: {}", url);
            }
        }
    }
    
    /**
     * 工作线程主循环
     */
    private void workerLoop() {
        Thread.currentThread().setName("CrawlerWorker-" + Thread.currentThread().getId());
        
        while (running.get()) {
            try {
                // 从队列获取URL
                String url = urlQueue.poll(1, TimeUnit.SECONDS);
                if (url == null) {
                    continue; // 超时，继续检查运行状态
                }
                
                // 处理URL
                processUrl(url);
                
                // 请求间延迟
                if (config.getDelayBetweenRequests() > 0) {
                    Thread.sleep(config.getDelayBetweenRequests());
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("工作线程发生异常", e);
                // 发布错误事件
                eventPublisher.publishEvent(
                    CrawlEvent.builder(CrawlEventType.ERROR_OCCURRED)
                        .data("message", "工作线程异常: " + e.getMessage())
                        .exception(e)
                        .build()
                );
            }
        }
        
        logger.debug("工作线程退出: {}", Thread.currentThread().getName());
    }
    
    /**
     * 处理单个URL
     * 
     * @param url 待处理的URL
     */
    private void processUrl(String url) {
        CrawlContext context = new CrawlContext(url);
        
        try {
            logger.debug("开始处理URL: {}", url);
            
            // 发布页面获取开始事件
            eventPublisher.publishEvent(
                CrawlEvent.builder(CrawlEventType.PAGE_FETCH_STARTED)
                    .url(url)
                    .build()
            );
            
            // 使用流水线处理
            ProcessResult result = pipelineHead.process(context);
            
            if (result.isSuccess()) {
                // 发布页面获取成功事件
                eventPublisher.publishEvent(
                    CrawlEvent.builder(CrawlEventType.PAGE_FETCH_SUCCESS)
                        .url(url)
                        .data("elapsedTime", context.getElapsedTime())
                        .build()
                );
                
                logger.debug("URL处理完成: {}, 耗时: {}ms", url, context.getElapsedTime());
            } else {
                // 发布页面获取失败事件
                eventPublisher.publishEvent(
                    CrawlEvent.builder(CrawlEventType.PAGE_FETCH_FAILED)
                        .url(url)
                        .data("errorMessage", result.getMessage())
                        .exception(result.getException())
                        .build()
                );
                
                logger.warn("URL处理失败: {}, 原因: {}", url, result.getMessage());
            }
            
        } catch (Exception e) {
            logger.error("处理URL时发生异常: {}", url, e);
            
            // 发布异常事件
            eventPublisher.publishEvent(
                CrawlEvent.builder(CrawlEventType.ERROR_OCCURRED)
                    .url(url)
                    .data("message", "URL处理异常: " + e.getMessage())
                    .exception(e)
                    .build()
            );
        }
    }
    
    /**
     * 获取统计信息
     * 
     * @return 统计报告
     */
    public StatisticsEventListener.StatisticsReport getStatistics() {
        return statisticsListener.getReport();
    }
    
    /**
     * 获取队列中剩余的URL数量
     * 
     * @return 队列大小
     */
    public int getQueueSize() {
        return urlQueue.size();
    }
    
    /**
     * 判断爬虫是否正在运行
     * 
     * @return 是否运行中
     */
    public boolean isRunning() {
        return running.get();
    }
    
    /**
     * 获取爬虫配置
     * 
     * @return 配置对象
     */
    public CrawlerConfig getConfig() {
        return config;
    }
    
    /**
     * 获取策略管理器
     * 
     * @return 策略管理器
     */
    public CrawlStrategyManager getStrategyManager() {
        return strategyManager;
    }
    
    /**
     * 获取事件发布器
     * 
     * @return 事件发布器
     */
    public CrawlEventPublisher getEventPublisher() {
        return eventPublisher;
    }
}