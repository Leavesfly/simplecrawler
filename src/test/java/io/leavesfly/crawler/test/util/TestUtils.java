package io.leavesfly.crawler.test.util;

import io.leavesfly.crawler.domain.RawPage;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 测试工具类
 * 提供测试过程中需要的各种辅助方法
 * 
 * @author yefei.yf
 */
public class TestUtils {
    
    /**
     * 创建模拟的RawPage对象
     * 
     * @param url URL
     * @param content 页面内容
     * @return RawPage对象
     */
    public static RawPage createMockRawPage(String url, String content) {
        return new RawPage(url, "UTF-8", content);
    }
    
    /**
     * 创建包含链接的HTML内容
     * 
     * @param baseUrl 基础URL
     * @param linkCount 链接数量
     * @return HTML内容
     */
    public static String createHtmlWithLinks(String baseUrl, int linkCount) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        
        for (int i = 0; i < linkCount; i++) {
            html.append("<a href=\"").append(baseUrl).append("/page").append(i).append("\">")
                .append("链接").append(i).append("</a>");
        }
        
        html.append("</body></html>");
        return html.toString();
    }
    
    /**
     * 创建包含商家信息的HTML内容
     * 
     * @param shopName 商家名称
     * @param rating 评分
     * @param address 地址
     * @return HTML内容
     */
    public static String createShopHtml(String shopName, String rating, String address) {
        return "<html><body>" +
               "<h1 class=\"shop-name\">" + shopName + "</h1>" +
               "<div class=\"rating-num\">" + rating + "</div>" +
               "<div class=\"address\">" + address + "</div>" +
               "</body></html>";
    }
    
    /**
     * 生成随机测试URL
     * 
     * @param count URL数量
     * @return URL列表
     */
    public static java.util.List<String> generateRandomUrls(int count) {
        java.util.List<String> urls = new java.util.ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            String url = "https://example.com/page" + i + "?rand=" + 
                        ThreadLocalRandom.current().nextInt(1000, 9999);
            urls.add(url);
        }
        
        return urls;
    }
    
    /**
     * 等待指定时间（毫秒）
     * 
     * @param milliseconds 等待时间
     */
    public static void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 等待条件满足或超时
     * 
     * @param condition 条件函数
     * @param timeoutMs 超时时间（毫秒）
     * @param checkIntervalMs 检查间隔（毫秒）
     * @return 是否在超时前满足条件
     */
    public static boolean waitForCondition(java.util.function.BooleanSupplier condition, 
                                         long timeoutMs, long checkIntervalMs) {
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (condition.getAsBoolean()) {
                return true;
            }
            
            waitFor(checkIntervalMs);
        }
        
        return false;
    }
    
    /**
     * 验证URL格式是否正确
     * 
     * @param url 待验证的URL
     * @return 是否为有效URL
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        try {
            new java.net.URL(url);
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (java.net.MalformedURLException e) {
            return false;
        }
    }
    
    /**
     * 获取测试资源文件路径
     * 
     * @param fileName 文件名
     * @return 文件路径
     */
    public static String getTestResourcePath(String fileName) {
        return TestUtils.class.getClassLoader().getResource(fileName).getPath();
    }
    
    /**
     * 格式化字节数为可读格式
     * 
     * @param bytes 字节数
     * @return 格式化后的字符串
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * 创建测试用的Exception
     * 
     * @param message 异常消息
     * @return RuntimeException
     */
    public static RuntimeException createTestException(String message) {
        return new RuntimeException("Test Exception: " + message);
    }
}