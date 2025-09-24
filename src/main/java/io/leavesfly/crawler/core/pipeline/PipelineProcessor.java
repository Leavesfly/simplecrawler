package io.leavesfly.crawler.core.pipeline;

import io.leavesfly.crawler.domain.CrawlContext;

/**
 * 爬虫处理流水线处理器抽象类
 * 使用责任链模式实现灵活的处理流程
 * 
 * @author yefei.yf
 */
public abstract class PipelineProcessor {
    
    private PipelineProcessor next;
    
    /**
     * 设置下一个处理器
     * 
     * @param processor 下一个处理器
     * @return 返回下一个处理器，支持链式调用
     */
    public PipelineProcessor setNext(PipelineProcessor processor) {
        this.next = processor;
        return processor;
    }
    
    /**
     * 处理请求
     * 
     * @param context 爬虫上下文
     * @return 处理结果
     */
    public final ProcessResult process(CrawlContext context) {
        ProcessResult result = doProcess(context);
        
        // 如果当前处理器处理成功且需要继续处理，则调用下一个处理器
        if (result.isSuccess() && result.shouldContinue() && next != null) {
            return next.process(context);
        }
        
        return result;
    }
    
    /**
     * 具体的处理逻辑，由子类实现
     * 
     * @param context 爬虫上下文
     * @return 处理结果
     */
    protected abstract ProcessResult doProcess(CrawlContext context);
    
    /**
     * 获取处理器名称
     * 
     * @return 处理器名称
     */
    public abstract String getProcessorName();
}