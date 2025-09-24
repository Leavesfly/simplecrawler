package io.leavesfly.crawler.core.pipeline;

import io.leavesfly.crawler.domain.CrawlContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 流水线处理器测试
 * 
 * @author yefei.yf
 */
@DisplayName("流水线处理器测试")
class PipelineProcessorTest {
    
    private TestProcessor processor1;
    private TestProcessor processor2;
    private TestProcessor processor3;
    private CrawlContext context;
    
    @BeforeEach
    void setUp() {
        processor1 = new TestProcessor("Processor1", true, true);
        processor2 = new TestProcessor("Processor2", true, true);
        processor3 = new TestProcessor("Processor3", true, false);
        context = new CrawlContext("https://example.com");
    }
    
    @Test
    @DisplayName("单个处理器执行")
    void testSingleProcessor() {
        ProcessResult result = processor1.process(context);
        
        assertTrue(result.isSuccess());
        assertTrue(result.shouldContinue());
        assertTrue(processor1.isExecuted());
    }
    
    @Test
    @DisplayName("责任链执行")
    void testProcessorChain() {
        // 构建处理链
        processor1.setNext(processor2).setNext(processor3);
        
        ProcessResult result = processor1.process(context);
        
        // 验证所有处理器都被执行
        assertTrue(processor1.isExecuted());
        assertTrue(processor2.isExecuted());
        assertTrue(processor3.isExecuted());
        
        // 最终结果由最后一个处理器决定
        assertTrue(result.isSuccess());
        assertFalse(result.shouldContinue()); // processor3 设置为不继续
    }
    
    @Test
    @DisplayName("处理器失败停止链式执行")
    void testProcessorFailureStopsChain() {
        TestProcessor failingProcessor = new TestProcessor("FailingProcessor", false, false);
        
        // 构建处理链，中间的处理器会失败
        processor1.setNext(failingProcessor).setNext(processor3);
        
        ProcessResult result = processor1.process(context);
        
        // 验证执行状态
        assertTrue(processor1.isExecuted());
        assertTrue(failingProcessor.isExecuted());
        assertFalse(processor3.isExecuted()); // 不应该执行，因为前面失败了
        
        // 结果应该是失败的
        assertFalse(result.isSuccess());
    }
    
    @Test
    @DisplayName("处理器成功但不继续")
    void testProcessorSuccessButNoContinue() {
        TestProcessor noContinueProcessor = new TestProcessor("NoContinueProcessor", true, false);
        
        // 构建处理链
        processor1.setNext(noContinueProcessor).setNext(processor3);
        
        ProcessResult result = processor1.process(context);
        
        // 验证执行状态
        assertTrue(processor1.isExecuted());
        assertTrue(noContinueProcessor.isExecuted());
        assertFalse(processor3.isExecuted()); // 不应该执行，因为前面不继续
        
        // 结果应该是成功的但不继续
        assertTrue(result.isSuccess());
        assertFalse(result.shouldContinue());
    }
    
    @Test
    @DisplayName("链式设置next处理器")
    void testChainedSetNext() {
        PipelineProcessor lastProcessor = processor1
                .setNext(processor2)
                .setNext(processor3);
        
        // 验证返回的是最后一个处理器
        assertEquals(processor3, lastProcessor);
        
        // 验证链式关系
        ProcessResult result = processor1.process(context);
        assertTrue(processor1.isExecuted());
        assertTrue(processor2.isExecuted());
        assertTrue(processor3.isExecuted());
    }
    
    @Test
    @DisplayName("无下一个处理器的情况")
    void testNoNextProcessor() {
        ProcessResult result = processor1.process(context);
        
        assertTrue(result.isSuccess());
        assertTrue(result.shouldContinue());
        assertTrue(processor1.isExecuted());
    }
    
    /**
     * 测试用处理器实现
     */
    static class TestProcessor extends PipelineProcessor {
        private final String name;
        private final boolean success;
        private final boolean shouldContinue;
        private boolean executed = false;
        
        public TestProcessor(String name, boolean success, boolean shouldContinue) {
            this.name = name;
            this.success = success;
            this.shouldContinue = shouldContinue;
        }
        
        @Override
        protected ProcessResult doProcess(CrawlContext context) {
            executed = true;
            context.setAttribute(name + "_executed", true);
            
            if (success) {
                return shouldContinue ? 
                    ProcessResult.success() : 
                    ProcessResult.success(false);
            } else {
                return ProcessResult.failure("Test failure in " + name);
            }
        }
        
        @Override
        public String getProcessorName() {
            return name;
        }
        
        public boolean isExecuted() {
            return executed;
        }
    }
}