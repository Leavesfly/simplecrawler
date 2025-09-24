package io.leavesfly.crawler.core.event.listener;

import io.leavesfly.crawler.core.event.CrawlEvent;
import io.leavesfly.crawler.core.event.CrawlEventListener;
import io.leavesfly.crawler.core.event.CrawlEventType;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 统计监听器
 * 统计爬虫运行过程中的各种指标
 * 
 * @author yefei.yf
 */
public class StatisticsEventListener implements CrawlEventListener {
    
    private final AtomicLong totalPagesFetched = new AtomicLong(0);
    private final AtomicLong totalPagesParsed = new AtomicLong(0);
    private final AtomicLong totalFetchErrors = new AtomicLong(0);
    private final AtomicLong totalParseErrors = new AtomicLong(0);
    private final AtomicLong totalUrlsQueued = new AtomicLong(0);
    
    private volatile long startTime = -1;
    private volatile long endTime = -1;
    
    @Override
    public void onEvent(CrawlEvent event) {
        switch (event.getType()) {
            case CRAWLER_STARTED:
                startTime = System.currentTimeMillis();
                resetCounters();
                break;
            case CRAWLER_STOPPED:
                endTime = System.currentTimeMillis();
                break;
            case PAGE_FETCH_SUCCESS:
                totalPagesFetched.incrementAndGet();
                break;
            case PAGE_FETCH_FAILED:
                totalFetchErrors.incrementAndGet();
                break;
            case PAGE_PARSE_SUCCESS:
                totalPagesParsed.incrementAndGet();
                break;
            case PAGE_PARSE_FAILED:
                totalParseErrors.incrementAndGet();
                break;
            case URL_QUEUED:
                totalUrlsQueued.incrementAndGet();
                break;
            default:
                // 其他事件不处理
                break;
        }
    }
    
    @Override
    public boolean supports(CrawlEventType eventType) {
        return eventType == CrawlEventType.CRAWLER_STARTED ||
               eventType == CrawlEventType.CRAWLER_STOPPED ||
               eventType == CrawlEventType.PAGE_FETCH_SUCCESS ||
               eventType == CrawlEventType.PAGE_FETCH_FAILED ||
               eventType == CrawlEventType.PAGE_PARSE_SUCCESS ||
               eventType == CrawlEventType.PAGE_PARSE_FAILED ||
               eventType == CrawlEventType.URL_QUEUED;
    }
    
    @Override
    public String getListenerName() {
        return "StatisticsEventListener";
    }
    
    /**
     * 重置计数器
     */
    public void resetCounters() {
        totalPagesFetched.set(0);
        totalPagesParsed.set(0);
        totalFetchErrors.set(0);
        totalParseErrors.set(0);
        totalUrlsQueued.set(0);
    }
    
    /**
     * 获取统计信息
     */
    public StatisticsReport getReport() {
        return new StatisticsReport(
            totalPagesFetched.get(),
            totalPagesParsed.get(),
            totalFetchErrors.get(),
            totalParseErrors.get(),
            totalUrlsQueued.get(),
            startTime,
            endTime
        );
    }
    
    /**
     * 统计报告类
     */
    public static class StatisticsReport {
        private final long totalPagesFetched;
        private final long totalPagesParsed;
        private final long totalFetchErrors;
        private final long totalParseErrors;
        private final long totalUrlsQueued;
        private final long startTime;
        private final long endTime;
        
        public StatisticsReport(long totalPagesFetched, long totalPagesParsed, 
                              long totalFetchErrors, long totalParseErrors,
                              long totalUrlsQueued, long startTime, long endTime) {
            this.totalPagesFetched = totalPagesFetched;
            this.totalPagesParsed = totalPagesParsed;
            this.totalFetchErrors = totalFetchErrors;
            this.totalParseErrors = totalParseErrors;
            this.totalUrlsQueued = totalUrlsQueued;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public long getTotalPagesFetched() { return totalPagesFetched; }
        public long getTotalPagesParsed() { return totalPagesParsed; }
        public long getTotalFetchErrors() { return totalFetchErrors; }
        public long getTotalParseErrors() { return totalParseErrors; }
        public long getTotalUrlsQueued() { return totalUrlsQueued; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        
        public long getElapsedTime() {
            if (startTime > 0 && endTime > 0) {
                return endTime - startTime;
            }
            return startTime > 0 ? System.currentTimeMillis() - startTime : 0;
        }
        
        public double getSuccessRate() {
            long total = totalPagesFetched + totalFetchErrors;
            return total > 0 ? (double) totalPagesFetched / total : 0.0;
        }
        
        @Override
        public String toString() {
            return "StatisticsReport{" +
                    "totalPagesFetched=" + totalPagesFetched +
                    ", totalPagesParsed=" + totalPagesParsed +
                    ", totalFetchErrors=" + totalFetchErrors +
                    ", totalParseErrors=" + totalParseErrors +
                    ", totalUrlsQueued=" + totalUrlsQueued +
                    ", elapsedTime=" + getElapsedTime() + "ms" +
                    ", successRate=" + String.format("%.2f%%", getSuccessRate() * 100) +
                    '}';
        }
    }
}