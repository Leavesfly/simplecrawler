package io.leavesfly.crawler.core.pipeline.impl;

import io.leavesfly.crawler.core.pipeline.PipelineProcessor;
import io.leavesfly.crawler.core.pipeline.ProcessResult;
import io.leavesfly.crawler.domain.CrawlContext;
import io.leavesfly.crawler.domain.RawPage;
import io.leavesfly.crawler.fetch.Fetcher;
import io.leavesfly.crawler.fetch.FetcherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 页面下载处理器
 * 负责下载指定URL的页面内容
 * 
 * @author yefei.yf
 */
public class FetchProcessor extends PipelineProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(FetchProcessor.class);
    
    private final Fetcher fetcher;
    
    public FetchProcessor() {
        this.fetcher = FetcherFactory.createDefaultFetcher();
    }
    
    public FetchProcessor(Fetcher fetcher) {
        this.fetcher = fetcher;
    }
    
    @Override
    protected ProcessResult doProcess(CrawlContext context) {
        String url = context.getCurrentUrl();
        
        try {
            logger.debug("开始下载页面: {}", url);
            
            RawPage rawPage = fetcher.fetchPage(url);
            if (rawPage == null) {
                logger.error("页面下载失败: {}", url);
                return ProcessResult.failure("页面下载失败: " + url);
            }
            
            context.setRawPage(rawPage);
            context.setMetadata("fetchTime", String.valueOf(System.currentTimeMillis()));
            
            logger.debug("页面下载成功: {}, 内容长度: {}", url, rawPage.getContent().length());
            return ProcessResult.success();
            
        } catch (Exception e) {
            logger.error("下载页面时发生异常: {}", url, e);
            return ProcessResult.failure("下载页面异常: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getProcessorName() {
        return "FetchProcessor";
    }
}