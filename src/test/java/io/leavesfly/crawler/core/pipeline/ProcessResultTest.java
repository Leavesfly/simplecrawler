package io.leavesfly.crawler.core.pipeline;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 处理结果测试
 * 
 * @author yefei.yf
 */
@DisplayName("处理结果测试")
class ProcessResultTest {
    
    @Test
    @DisplayName("创建成功结果")
    void testCreateSuccessResult() {
        ProcessResult result = ProcessResult.success();
        
        assertTrue(result.isSuccess());
        assertTrue(result.shouldContinue());
        assertNull(result.getMessage());
        assertNull(result.getException());
    }
    
    @Test
    @DisplayName("创建成功结果 - 指定是否继续")
    void testCreateSuccessResultWithContinue() {
        ProcessResult result1 = ProcessResult.success(true);
        assertTrue(result1.isSuccess());
        assertTrue(result1.shouldContinue());
        
        ProcessResult result2 = ProcessResult.success(false);
        assertTrue(result2.isSuccess());
        assertFalse(result2.shouldContinue());
    }
    
    @Test
    @DisplayName("创建成功结果 - 带消息")
    void testCreateSuccessResultWithMessage() {
        String message = "处理成功";
        ProcessResult result = ProcessResult.success(message);
        
        assertTrue(result.isSuccess());
        assertTrue(result.shouldContinue());
        assertEquals(message, result.getMessage());
        assertNull(result.getException());
    }
    
    @Test
    @DisplayName("创建失败结果")
    void testCreateFailureResult() {
        String message = "处理失败";
        ProcessResult result = ProcessResult.failure(message);
        
        assertFalse(result.isSuccess());
        assertFalse(result.shouldContinue());
        assertEquals(message, result.getMessage());
        assertNull(result.getException());
    }
    
    @Test
    @DisplayName("创建失败结果 - 带异常")
    void testCreateFailureResultWithException() {
        String message = "处理异常";
        Exception exception = new RuntimeException("测试异常");
        ProcessResult result = ProcessResult.failure(message, exception);
        
        assertFalse(result.isSuccess());
        assertFalse(result.shouldContinue());
        assertEquals(message, result.getMessage());
        assertEquals(exception, result.getException());
    }
    
    @Test
    @DisplayName("结果对象不可变性")
    void testResultImmutability() {
        ProcessResult result = ProcessResult.success("测试消息");
        
        // 验证结果对象是不可变的
        assertTrue(result.isSuccess());
        assertEquals("测试消息", result.getMessage());
        
        // 创建另一个结果不会影响原有结果
        ProcessResult anotherResult = ProcessResult.failure("失败消息");
        assertTrue(result.isSuccess()); // 原结果不变
        assertFalse(anotherResult.isSuccess());
    }
    
    @Test
    @DisplayName("测试空消息和异常")
    void testNullMessageAndException() {
        ProcessResult successResult = ProcessResult.success(null);
        assertNull(successResult.getMessage());
        assertNull(successResult.getException());
        
        ProcessResult failureResult = ProcessResult.failure(null);
        assertNull(failureResult.getMessage());
        assertNull(failureResult.getException());
        
        ProcessResult failureWithNullException = ProcessResult.failure("消息", null);
        assertEquals("消息", failureWithNullException.getMessage());
        assertNull(failureWithNullException.getException());
    }
}