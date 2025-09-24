package io.leavesfly.crawler.exception.handler;

import io.leavesfly.crawler.exception.CrawlerException;
import io.leavesfly.crawler.exception.FetchException;
import io.leavesfly.crawler.exception.ParseException;
import io.leavesfly.crawler.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常处理器接口
 * 
 * @author yefei.yf
 */
public interface ExceptionHandler {
    
    /**
     * 处理异常
     * 
     * @param exception 异常对象
     * @return 处理结果
     */
    ExceptionHandleResult handle(CrawlerException exception);
    
    /**
     * 判断是否支持处理指定类型的异常
     * 
     * @param exception 异常对象
     * @return 是否支持
     */
    boolean supports(CrawlerException exception);
    
    /**
     * 获取处理器优先级，数值越小优先级越高
     * 
     * @return 优先级
     */
    default int getPriority() {
        return 100;
    }
}

/**
 * 抓取异常处理器
 */
class FetchExceptionHandler implements ExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(FetchExceptionHandler.class);
    
    @Override
    public ExceptionHandleResult handle(CrawlerException exception) {
        FetchException fetchException = (FetchException) exception;
        
        logger.error("页面抓取失败: {}, 错误: {}", fetchException.getUrl(), fetchException.getMessage());
        
        if (fetchException instanceof FetchException.HttpStatusException) {
            FetchException.HttpStatusException httpException = (FetchException.HttpStatusException) fetchException;
            int statusCode = httpException.getStatusCode();
            
            if (statusCode == 404) {
                // 404错误不重试
                return ExceptionHandleResult.abort("页面不存在");
            } else if (statusCode >= 500) {
                // 服务器错误，建议重试
                return ExceptionHandleResult.retry("服务器错误，建议重试");
            } else if (statusCode == 403 || statusCode == 429) {
                // 被封或限流，延长重试间隔
                return ExceptionHandleResult.retryWithDelay("访问被限制，延长重试间隔", 5000);
            }
        } else if (fetchException instanceof FetchException.TimeoutException) {
            // 超时异常，建议重试
            return ExceptionHandleResult.retry("请求超时，建议重试");
        } else if (fetchException instanceof FetchException.NetworkException) {
            // 网络异常，建议重试
            return ExceptionHandleResult.retry("网络异常，建议重试");
        }
        
        return ExceptionHandleResult.retry("抓取异常，建议重试");
    }
    
    @Override
    public boolean supports(CrawlerException exception) {
        return exception instanceof FetchException;
    }
    
    @Override
    public int getPriority() {
        return 10;
    }
}

/**
 * 解析异常处理器
 */
class ParseExceptionHandler implements ExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ParseExceptionHandler.class);
    
    @Override
    public ExceptionHandleResult handle(CrawlerException exception) {
        ParseException parseException = (ParseException) exception;
        
        logger.error("页面解析失败: {}, 错误: {}", parseException.getUrl(), parseException.getMessage());
        
        if (parseException instanceof ParseException.ContentFormatException) {
            // 内容格式错误，通常不需要重试
            return ExceptionHandleResult.abort("页面格式不支持");
        } else if (parseException instanceof ParseException.SelectorException) {
            // 选择器错误，通常是配置问题
            return ExceptionHandleResult.abort("选择器配置错误");
        }
        
        // 其他解析异常可以重试
        return ExceptionHandleResult.retry("解析异常，建议重试");
    }
    
    @Override
    public boolean supports(CrawlerException exception) {
        return exception instanceof ParseException;
    }
    
    @Override
    public int getPriority() {
        return 20;
    }
}

/**
 * 存储异常处理器
 */
class StorageExceptionHandler implements ExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(StorageExceptionHandler.class);
    
    @Override
    public ExceptionHandleResult handle(CrawlerException exception) {
        StorageException storageException = (StorageException) exception;
        
        logger.error("数据存储失败: {}", storageException.getMessage());
        
        if (storageException instanceof StorageException.FileStorageException) {
            StorageException.FileStorageException fileException = (StorageException.FileStorageException) storageException;
            logger.error("文件存储失败: {}", fileException.getFilePath());
        } else if (storageException instanceof StorageException.DatabaseStorageException) {
            StorageException.DatabaseStorageException dbException = (StorageException.DatabaseStorageException) storageException;
            logger.error("数据库存储失败: {}", dbException.getTable());
        }
        
        // 存储异常通常可以重试
        return ExceptionHandleResult.retry("存储异常，建议重试");
    }
    
    @Override
    public boolean supports(CrawlerException exception) {
        return exception instanceof StorageException;
    }
    
    @Override
    public int getPriority() {
        return 30;
    }
}