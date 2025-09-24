package io.leavesfly.crawler;

import io.leavesfly.crawler.conf.CrawlerConfig;
import io.leavesfly.crawler.core.ModernCrawlerEngine;
import io.leavesfly.crawler.core.event.listener.StatisticsEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 爬虫系统主入口类
 * 提供了一个完整的爬虫应用示例，包含配置、启动、监控和停止功能
 * 
 * @author yefei.yf
 */
public class Crawler {
    
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);
    
    /**
     * 应用程序入口
     * 
     * @param args 命令行参数（可选：种子URL列表）
     */
    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("    SimpleCrawler 现代化爬虫系统      ");
        System.out.println("======================================");
        
        try {
            // 1. 初始化爬虫系统
            ModernCrawlerEngine crawler = initializeCrawler();
            
            // 2. 获取种子URL列表
            List<String> seedUrls = getSeedUrls(args);
            
            // 3. 启动爬虫
            startCrawler(crawler, seedUrls);
            
            // 4. 交互式监控
            interactiveMonitoring(crawler);
            
            // 5. 停止爬虫并显示最终统计
            stopCrawler(crawler);
            
        } catch (Exception e) {
            logger.error("爬虫系统运行异常", e);
            System.err.println("系统异常: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * 初始化爬虫引擎
     * 
     * @return 配置好的爬虫引擎
     */
    private static ModernCrawlerEngine initializeCrawler() {
        logger.info("正在初始化爬虫系统...");
        
        // 创建爬虫配置
        CrawlerConfig config = new CrawlerConfig.Builder()
                .connectionTimeout(30, TimeUnit.SECONDS)
                .socketTimeout(60, TimeUnit.SECONDS)
                .maxConnections(50)
                .maxRetries(3)
                .userAgent("SimpleCrawler/1.0 (Modern Web Crawler)")
                .threadPoolSize(5)
                .delayBetweenRequests(2, TimeUnit.SECONDS)
                .queueCapacity(1000)
                .storage("file", "./data")
                .build();
        
        // 创建爬虫引擎
        ModernCrawlerEngine crawler = new ModernCrawlerEngine(config);
        
        System.out.println("✅ 爬虫引擎初始化完成");
        System.out.println("📋 配置信息: " + config);
        
        return crawler;
    }
    
    /**
     * 获取种子URL列表
     * 
     * @param args 命令行参数
     * @return 种子URL列表
     */
    private static List<String> getSeedUrls(String[] args) {
        List<String> seedUrls;
        
        if (args.length > 0) {
            // 使用命令行参数作为种子URL
            seedUrls = Arrays.asList(args);
            System.out.println("📥 使用命令行参数作为种子URL");
        } else {
            // 使用默认种子URL
            seedUrls = Arrays.asList(
                "https://www.meituan.com/",
                "https://bj.meituan.com/"
            );
            System.out.println("📥 使用默认种子URL");
        }
        
        System.out.println("🌱 种子URL列表:");
        for (int i = 0; i < seedUrls.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + seedUrls.get(i));
        }
        System.out.println();
        
        return seedUrls;
    }
    
    /**
     * 启动爬虫
     * 
     * @param crawler 爬虫引擎
     * @param seedUrls 种子URL列表
     */
    private static void startCrawler(ModernCrawlerEngine crawler, List<String> seedUrls) {
        System.out.println("🚀 启动爬虫引擎...");
        
        // 启动爬虫
        crawler.start(seedUrls);
        
        System.out.println("✅ 爬虫已成功启动");
        System.out.println("⚡ 工作线程数: " + crawler.getConfig().getThreadPoolSize());
        System.out.println("⏱️  请求间隔: " + crawler.getConfig().getDelayBetweenRequests() + "ms");
        System.out.println();
    }
    
    /**
     * 交互式监控界面
     * 
     * @param crawler 爬虫引擎
     */
    private static void interactiveMonitoring(ModernCrawlerEngine crawler) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("📊 交互式监控模式");
        System.out.println("   输入 'stats' 查看统计信息");
        System.out.println("   输入 'queue' 查看队列状态");
        System.out.println("   输入 'config' 查看配置信息");
        System.out.println("   输入 'quit' 或 'exit' 退出程序");
        System.out.println("   直接按Enter键快速查看统计信息");
        System.out.println();
        
        while (true) {
            System.out.print("crawler> ");
            String input = scanner.nextLine().trim().toLowerCase();
            
            switch (input) {
                case "quit":
                case "exit":
                    System.out.println("\n👋 准备退出...");
                    return;
                    
                case "stats":
                case "":
                    displayStatistics(crawler);
                    break;
                    
                case "queue":
                    displayQueueStatus(crawler);
                    break;
                    
                case "config":
                    displayConfig(crawler);
                    break;
                    
                case "help":
                    displayHelp();
                    break;
                    
                default:
                    System.out.println("❌ 未知命令: " + input + "，输入 'help' 查看帮助");
                    break;
            }
            
            System.out.println();
        }
    }
    
    /**
     * 显示统计信息
     * 
     * @param crawler 爬虫引擎
     */
    private static void displayStatistics(ModernCrawlerEngine crawler) {
        StatisticsEventListener.StatisticsReport stats = crawler.getStatistics();
        
        System.out.println("📊 === 实时统计信息 ===");
        System.out.println("  📄 总页面数: " + stats.getTotalPagesFetched());
        System.out.println("  ✅ 成功页面: " + stats.getTotalPagesFetched());
        System.out.println("  ❌ 失败页面: " + stats.getTotalFetchErrors());
        System.out.println("  📊 成功率: " + String.format("%.2f%%", stats.getSuccessRate() * 100));
        System.out.println("  ⏱️  运行时间: " + formatDuration(stats.getElapsedTime()));
        System.out.println("  💾 队列大小: " + crawler.getQueueSize());
        System.out.println("  🏃 运行状态: " + (crawler.isRunning() ? "运行中" : "已停止"));
    }
    
    /**
     * 显示队列状态
     * 
     * @param crawler 爬虫引擎
     */
    private static void displayQueueStatus(ModernCrawlerEngine crawler) {
        System.out.println("📋 === 队列状态信息 ===");
        System.out.println("  📦 队列容量: " + crawler.getConfig().getQueueCapacity());
        System.out.println("  📄 当前大小: " + crawler.getQueueSize());
        System.out.println("  📊 使用率: " + String.format("%.2f%%", 
            (double) crawler.getQueueSize() / crawler.getConfig().getQueueCapacity() * 100));
    }
    
    /**
     * 显示配置信息
     * 
     * @param crawler 爬虫引擎
     */
    private static void displayConfig(ModernCrawlerEngine crawler) {
        CrawlerConfig config = crawler.getConfig();
        
        System.out.println("⚙️  === 爬虫配置信息 ===");
        System.out.println("  🔗 连接超时: " + config.getConnectionTimeout() + "ms");
        System.out.println("  📡 Socket超时: " + config.getSocketTimeout() + "ms");
        System.out.println("  🔄 最大连接数: " + config.getMaxConnections());
        System.out.println("  🔁 最大重试次数: " + config.getMaxRetries());
        System.out.println("  🕷️  User-Agent: " + config.getUserAgent());
        System.out.println("  🧵 线程池大小: " + config.getThreadPoolSize());
        System.out.println("  ⏱️  请求延迟: " + config.getDelayBetweenRequests() + "ms");
        System.out.println("  📦 队列容量: " + config.getQueueCapacity());
    }
    
    /**
     * 显示帮助信息
     */
    private static void displayHelp() {
        System.out.println("📖 === 帮助信息 ===");
        System.out.println("  stats    - 显示统计信息");
        System.out.println("  queue    - 显示队列状态");
        System.out.println("  config   - 显示配置信息");
        System.out.println("  help     - 显示此帮助信息");
        System.out.println("  quit     - 退出程序");
        System.out.println("  exit     - 退出程序");
        System.out.println("  (回车)   - 快速查看统计信息");
    }
    
    /**
     * 停止爬虫并显示最终统计
     * 
     * @param crawler 爬虫引擎
     */
    private static void stopCrawler(ModernCrawlerEngine crawler) {
        System.out.println("🛑 正在停止爬虫引擎...");
        
        // 停止爬虫
        crawler.stop();
        
        // 显示最终统计
        StatisticsEventListener.StatisticsReport finalStats = crawler.getStatistics();
        
        System.out.println();
        System.out.println("📊 === 最终统计报告 ===");
        System.out.println("  📄 总处理页面: " + finalStats.getTotalPagesFetched());
        System.out.println("  ✅ 成功页面: " + finalStats.getTotalPagesFetched());
        System.out.println("  ❌ 失败页面: " + finalStats.getTotalFetchErrors());
        System.out.println("  📊 最终成功率: " + String.format("%.2f%%", finalStats.getSuccessRate() * 100));
        System.out.println("  ⏱️  总运行时间: " + formatDuration(finalStats.getElapsedTime()));
        System.out.println();
        System.out.println("✅ 爬虫系统已正常退出");
        System.out.println("======================================");
    }
    
    /**
     * 格式化持续时间
     * 
     * @param milliseconds 毫秒数
     * @return 格式化的时间字符串
     */
    private static String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%d小时%d分钟%d秒", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, seconds % 60);
        } else {
            return String.format("%d秒", seconds);
        }
    }
}
