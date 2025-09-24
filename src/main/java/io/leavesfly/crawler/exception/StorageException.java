package io.leavesfly.crawler.exception;

/**
 * 存储异常
 * 
 * @author yefei.yf
 */
public class StorageException extends CrawlerException {
    
    public StorageException(String message) {
        super("STORAGE_ERROR", message);
    }
    
    public StorageException(String message, Throwable cause) {
        super("STORAGE_ERROR", message, cause);
    }
    
    public StorageException(String message, String url) {
        super("STORAGE_ERROR", message, url);
    }
    
    public StorageException(String message, String url, Throwable cause) {
        super("STORAGE_ERROR", message, url, cause);
    }
    
    /**
     * 文件存储异常
     */
    public static class FileStorageException extends StorageException {
        private final String filePath;
        
        public FileStorageException(String filePath, String message) {
            super(message + " (文件: " + filePath + ")");
            this.filePath = filePath;
        }
        
        public FileStorageException(String filePath, String message, Throwable cause) {
            super(message + " (文件: " + filePath + ")", cause);
            this.filePath = filePath;
        }
        
        public String getFilePath() {
            return filePath;
        }
    }
    
    /**
     * 数据库存储异常
     */
    public static class DatabaseStorageException extends StorageException {
        private final String table;
        
        public DatabaseStorageException(String table, String message) {
            super(message + " (表: " + table + ")");
            this.table = table;
        }
        
        public DatabaseStorageException(String table, String message, Throwable cause) {
            super(message + " (表: " + table + ")", cause);
            this.table = table;
        }
        
        public String getTable() {
            return table;
        }
    }
}