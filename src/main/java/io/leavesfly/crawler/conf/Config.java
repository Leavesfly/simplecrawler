package io.leavesfly.crawler.conf;

import java.util.concurrent.TimeUnit;

/**
 * 爬虫配置类
 * 使用Builder模式构建配置对象
 * 
 * @author yefei.yf
 */
public class CrawlerConfig {
    
    // HTTP配置
    private final int connectionTimeout;
    private final int socketTimeout;
    private final int maxConnections;
    private final int maxRetries;
    private final String userAgent;
    
    // 抓取配置
    private final String fetcherType;
    private final int maxPageSize;
    private final long delayBetweenRequests;
    
    // 代理配置
    private final boolean useProxy;
    private final String proxyHost;
    private final int proxyPort;
    
    // 线程配置
    private final int threadPoolSize;
    private final int queueCapacity;
    
    // 存储配置
    private final String storageType;
    private final String storagePath;
    
    private CrawlerConfig(Builder builder) {
        this.connectionTimeout = builder.connectionTimeout;
        this.socketTimeout = builder.socketTimeout;
        this.maxConnections = builder.maxConnections;
        this.maxRetries = builder.maxRetries;
        this.userAgent = builder.userAgent;
        this.fetcherType = builder.fetcherType;
        this.maxPageSize = builder.maxPageSize;
        this.delayBetweenRequests = builder.delayBetweenRequests;
        this.useProxy = builder.useProxy;
        this.proxyHost = builder.proxyHost;
        this.proxyPort = builder.proxyPort;
        this.threadPoolSize = builder.threadPoolSize;
        this.queueCapacity = builder.queueCapacity;
        this.storageType = builder.storageType;
        this.storagePath = builder.storagePath;
    }
    
    // Getter方法
    public int getConnectionTimeout() { return connectionTimeout; }
    public int getSocketTimeout() { return socketTimeout; }
    public int getMaxConnections() { return maxConnections; }
    public int getMaxRetries() { return maxRetries; }
    public String getUserAgent() { return userAgent; }
    public String getFetcherType() { return fetcherType; }
    public int getMaxPageSize() { return maxPageSize; }
    public long getDelayBetweenRequests() { return delayBetweenRequests; }
    public boolean isUseProxy() { return useProxy; }
    public String getProxyHost() { return proxyHost; }
    public int getProxyPort() { return proxyPort; }
    public int getThreadPoolSize() { return threadPoolSize; }
    public int getQueueCapacity() { return queueCapacity; }
    public String getStorageType() { return storageType; }
    public String getStoragePath() { return storagePath; }
    
    /**
     * 创建默认配置
     */
    public static CrawlerConfig createDefault() {
        return new Builder().build();
    }
    
    /**
     * 配置构建器
     */
    public static class Builder {
        // 默认值
        private int connectionTimeout = 30000;
        private int socketTimeout = 60000;
        private int maxConnections = 100;
        private int maxRetries = 3;
        private String userAgent = "SimpleCrawler/1.0";
        private String fetcherType = "http";
        private int maxPageSize = 1024 * 1024; // 1MB
        private long delayBetweenRequests = 1000; // 1秒
        private boolean useProxy = false;
        private String proxyHost = "";
        private int proxyPort = 8080;
        private int threadPoolSize = 10;
        private int queueCapacity = 1000;
        private String storageType = "file";
        private String storagePath = "./data";
        
        public Builder connectionTimeout(int timeout) {
            this.connectionTimeout = timeout;
            return this;
        }
        
        public Builder connectionTimeout(int timeout, TimeUnit unit) {
            this.connectionTimeout = (int) unit.toMillis(timeout);
            return this;
        }
        
        public Builder socketTimeout(int timeout) {
            this.socketTimeout = timeout;
            return this;
        }
        
        public Builder socketTimeout(int timeout, TimeUnit unit) {
            this.socketTimeout = (int) unit.toMillis(timeout);
            return this;
        }
        
        public Builder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }
        
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }
        
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public Builder fetcherType(String fetcherType) {
            this.fetcherType = fetcherType;
            return this;
        }
        
        public Builder maxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
            return this;
        }
        
        public Builder delayBetweenRequests(long delay) {
            this.delayBetweenRequests = delay;
            return this;
        }
        
        public Builder delayBetweenRequests(long delay, TimeUnit unit) {
            this.delayBetweenRequests = unit.toMillis(delay);
            return this;
        }
        
        public Builder useProxy(boolean useProxy) {
            this.useProxy = useProxy;
            return this;
        }
        
        public Builder proxy(String host, int port) {
            this.useProxy = true;
            this.proxyHost = host;
            this.proxyPort = port;
            return this;
        }
        
        public Builder threadPoolSize(int size) {
            this.threadPoolSize = size;
            return this;
        }
        
        public Builder queueCapacity(int capacity) {
            this.queueCapacity = capacity;
            return this;
        }
        
        public Builder storage(String type, String path) {
            this.storageType = type;
            this.storagePath = path;
            return this;
        }
        
        public CrawlerConfig build() {
            validate();
            return new CrawlerConfig(this);
        }
        
        private void validate() {
            if (connectionTimeout <= 0) {
                throw new IllegalArgumentException("连接超时时间必须大于0");
            }
            if (socketTimeout <= 0) {
                throw new IllegalArgumentException("Socket超时时间必须大于0");
            }
            if (maxConnections <= 0) {
                throw new IllegalArgumentException("最大连接数必须大于0");
            }
            if (threadPoolSize <= 0) {
                throw new IllegalArgumentException("线程池大小必须大于0");
            }
            if (userAgent == null || userAgent.trim().isEmpty()) {
                throw new IllegalArgumentException("User-Agent不能为空");
            }
        }
    }
    
    @Override
    public String toString() {
        return "CrawlerConfig{" +
                "connectionTimeout=" + connectionTimeout +
                ", socketTimeout=" + socketTimeout +
                ", maxConnections=" + maxConnections +
                ", maxRetries=" + maxRetries +
                ", userAgent='" + userAgent + '\'' +
                ", fetcherType='" + fetcherType + '\'' +
                ", threadPoolSize=" + threadPoolSize +
                '}';
    }
}
