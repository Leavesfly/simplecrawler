package io.leavesfly.crawler.core.pipeline.impl;

import io.leavesfly.crawler.core.pipeline.PipelineProcessor;
import io.leavesfly.crawler.core.pipeline.ProcessResult;
import io.leavesfly.crawler.domain.CrawlContext;
import io.leavesfly.crawler.domain.RawPage;
import io.leavesfly.crawler.parse.FieldExtractor;
import io.leavesfly.crawler.parse.FieldExtractorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 内容解析处理器
 * 负责从页面中提取有用信息
 * 
 * @author yefei.yf
 */
public class ParseProcessor extends PipelineProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(ParseProcessor.class);
    
    @Override
    protected ProcessResult doProcess(CrawlContext context) {
        RawPage rawPage = context.getRawPage();
        if (rawPage == null) {
            logger.warn("原始页面数据为空，跳过解析: {}", context.getCurrentUrl());
            return ProcessResult.success();
        }
        
        try {
            logger.debug("开始解析页面: {}", rawPage.getUrl());
            
            // 获取适合的字段提取器
            List<FieldExtractor> extractors = FieldExtractorFactory.getExtractors(rawPage.getUrl());
            
            for (FieldExtractor extractor : extractors) {
                if (extractor.accept(rawPage.getUrl())) {
                    logger.debug("使用提取器 {} 解析页面: {}", 
                               extractor.getClass().getSimpleName(), rawPage.getUrl());
                    
                    extractor.extractFile(rawPage);
                }
            }
            
            context.setMetadata("parseTime", String.valueOf(System.currentTimeMillis()));
            logger.debug("页面解析完成: {}", rawPage.getUrl());
            
            return ProcessResult.success();
            
        } catch (Exception e) {
            logger.error("解析页面时发生异常: {}", rawPage.getUrl(), e);
            return ProcessResult.failure("页面解析异常: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getProcessorName() {
        return "ParseProcessor";
    }
}