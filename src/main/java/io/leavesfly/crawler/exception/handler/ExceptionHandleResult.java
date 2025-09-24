package io.leavesfly.crawler.exception.handler;

/**
 * 异常处理结果
 * 
 * @author yefei.yf
 */
public class ExceptionHandleResult {
    
    public enum Action {
        RETRY,    // 重试
        ABORT,    // 终止
        IGNORE    // 忽略
    }
    
    private final Action action;
    private final String message;
    private final long retryDelay;
    private final int maxRetries;
    
    private ExceptionHandleResult(Action action, String message, long retryDelay, int maxRetries) {
        this.action = action;
        this.message = message;
        this.retryDelay = retryDelay;
        this.maxRetries = maxRetries;
    }
    
    /**
     * 创建重试结果
     */
    public static ExceptionHandleResult retry(String message) {
        return new ExceptionHandleResult(Action.RETRY, message, 1000, 3);
    }
    
    /**
     * 创建重试结果（指定延迟）
     */
    public static ExceptionHandleResult retryWithDelay(String message, long delay) {
        return new ExceptionHandleResult(Action.RETRY, message, delay, 3);
    }
    
    /**
     * 创建重试结果（指定次数和延迟）
     */
    public static ExceptionHandleResult retryWithOptions(String message, long delay, int maxRetries) {
        return new ExceptionHandleResult(Action.RETRY, message, delay, maxRetries);
    }
    
    /**
     * 创建终止结果
     */
    public static ExceptionHandleResult abort(String message) {
        return new ExceptionHandleResult(Action.ABORT, message, 0, 0);
    }
    
    /**
     * 创建忽略结果
     */
    public static ExceptionHandleResult ignore(String message) {
        return new ExceptionHandleResult(Action.IGNORE, message, 0, 0);
    }
    
    // Getter方法
    public Action getAction() {
        return action;
    }
    
    public String getMessage() {
        return message;
    }
    
    public long getRetryDelay() {
        return retryDelay;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    @Override
    public String toString() {
        return "ExceptionHandleResult{" +
                "action=" + action +
                ", message='" + message + '\'' +
                ", retryDelay=" + retryDelay +
                ", maxRetries=" + maxRetries +
                '}';
    }
}