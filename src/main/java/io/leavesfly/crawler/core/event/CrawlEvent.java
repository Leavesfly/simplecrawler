package io.leavesfly.crawler.core.event;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 爬虫事件类
 * 包含事件类型、时间戳、相关数据等信息
 * 
 * @author yefei.yf
 */
public class CrawlEvent {
    
    private final CrawlEventType type;
    private final LocalDateTime timestamp;
    private final String url;
    private final Map<String, Object> data;
    private final Exception exception;
    
    private CrawlEvent(Builder builder) {
        this.type = builder.type;
        this.timestamp = builder.timestamp;
        this.url = builder.url;
        this.data = new HashMap<>(builder.data);
        this.exception = builder.exception;
    }
    
    public CrawlEventType getType() {
        return type;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getUrl() {
        return url;
    }
    
    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }
    
    public Object getData(String key) {
        return data.get(key);
    }
    
    public Exception getException() {
        return exception;
    }
    
    /**
     * 创建事件构建器
     */
    public static Builder builder(CrawlEventType type) {
        return new Builder(type);
    }
    
    /**
     * 事件构建器
     */
    public static class Builder {
        private final CrawlEventType type;
        private LocalDateTime timestamp = LocalDateTime.now();
        private String url;
        private final Map<String, Object> data = new HashMap<>();
        private Exception exception;
        
        private Builder(CrawlEventType type) {
            this.type = type;
        }
        
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder data(String key, Object value) {
            this.data.put(key, value);
            return this;
        }
        
        public Builder data(Map<String, Object> data) {
            this.data.putAll(data);
            return this;
        }
        
        public Builder exception(Exception exception) {
            this.exception = exception;
            return this;
        }
        
        public CrawlEvent build() {
            return new CrawlEvent(this);
        }
    }
    
    @Override
    public String toString() {
        return "CrawlEvent{" +
                "type=" + type +
                ", timestamp=" + timestamp +
                ", url='" + url + '\'' +
                ", dataSize=" + data.size() +
                ", hasException=" + (exception != null) +
                '}';
    }
}