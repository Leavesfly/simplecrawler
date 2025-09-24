package io.leavesfly.crawler;

import io.leavesfly.crawler.conf.CrawlerConfig;
import io.leavesfly.crawler.core.ModernCrawlerEngine;
import io.leavesfly.crawler.core.event.listener.StatisticsEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 爬虫应用主类
 * 演示如何使用现代化爬虫引擎
 * 
 * @author yefei.yf
 */
public class App {
    
    public static void main(String[] args) {
        // 创建爬虫配置
        CrawlerConfig config = new CrawlerConfig.Builder()
                .connectionTimeout(30, TimeUnit.SECONDS)
                .socketTimeout(60, TimeUnit.SECONDS)
                .maxConnections(50)
                .maxRetries(3)
                .userAgent("ModernCrawler/1.0")
                .threadPoolSize(5)
                .delayBetweenRequests(2, TimeUnit.SECONDS)
                .queueCapacity(1000)
                .storage("file", "./data")
                .build();
        
        // 创建爬虫引擎
        ModernCrawlerEngine crawler = new ModernCrawlerEngine(config);
        
        // 种子URL列表
        List<String> seedUrls = Arrays.asList(
            "https://www.meituan.com/",
            "https://bj.meituan.com/"
        );
        
        System.out.println("=== 现代化爬虫系统演示 ===");
        System.out.println("配置信息: " + config);
        System.out.println("种子URL: " + seedUrls);
        System.out.println();
        
        // 启动爬虫
        crawler.start(seedUrls);
        
        // 等待用户输入来停止爬虫
        System.out.println("爬虫已启动，按Enter键查看统计信息，输入'quit'退出...");
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            String input = scanner.nextLine();
            
            if ("quit".equalsIgnoreCase(input.trim())) {
                break;
            }
            
            // 显示统计信息
            StatisticsEventListener.StatisticsReport stats = crawler.getStatistics();
            System.out.println("\n=== 统计信息 ===");
            System.out.println(stats);
            System.out.println("队列剩余URL数: " + crawler.getQueueSize());
            System.out.println("是否运行中: " + crawler.isRunning());
            System.out.println();
        }
        
        // 停止爬虫
        System.out.println("正在停止爬虫...");
        crawler.stop();
        
        // 显示最终统计
        StatisticsEventListener.StatisticsReport finalStats = crawler.getStatistics();
        System.out.println("\n=== 最终统计 ===");
        System.out.println(finalStats);
        
        System.out.println("爬虫已停止，程序退出。");
        scanner.close();
    }
}
