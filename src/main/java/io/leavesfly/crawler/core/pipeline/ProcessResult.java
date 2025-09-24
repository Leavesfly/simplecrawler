package io.leavesfly.crawler.core.pipeline;

/**
 * 处理结果类
 * 封装处理器的执行结果
 * 
 * @author yefei.yf
 */
public class ProcessResult {
    
    private final boolean success;
    private final boolean shouldContinue;
    private final String message;
    private final Exception exception;
    
    private ProcessResult(boolean success, boolean shouldContinue, String message, Exception exception) {
        this.success = success;
        this.shouldContinue = shouldContinue;
        this.message = message;
        this.exception = exception;
    }
    
    /**
     * 创建成功结果
     * 
     * @return 成功结果
     */
    public static ProcessResult success() {
        return new ProcessResult(true, true, null, null);
    }
    
    /**
     * 创建成功结果并指定是否继续
     * 
     * @param shouldContinue 是否继续处理
     * @return 处理结果
     */
    public static ProcessResult success(boolean shouldContinue) {
        return new ProcessResult(true, shouldContinue, null, null);
    }
    
    /**
     * 创建成功结果并指定消息
     * 
     * @param message 处理消息
     * @return 处理结果
     */
    public static ProcessResult success(String message) {
        return new ProcessResult(true, true, message, null);
    }
    
    /**
     * 创建失败结果
     * 
     * @param message 错误消息
     * @return 失败结果
     */
    public static ProcessResult failure(String message) {
        return new ProcessResult(false, false, message, null);
    }
    
    /**
     * 创建失败结果
     * 
     * @param message 错误消息
     * @param exception 异常信息
     * @return 失败结果
     */
    public static ProcessResult failure(String message, Exception exception) {
        return new ProcessResult(false, false, message, exception);
    }
    
    /**
     * 判断是否成功
     * 
     * @return 是否成功
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * 判断是否应该继续处理
     * 
     * @return 是否继续处理
     */
    public boolean shouldContinue() {
        return shouldContinue;
    }
    
    /**
     * 获取处理消息
     * 
     * @return 处理消息
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * 获取异常信息
     * 
     * @return 异常信息
     */
    public Exception getException() {
        return exception;
    }
}