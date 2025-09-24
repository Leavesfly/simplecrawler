package io.leavesfly.crawler.parse;

import java.util.ArrayList;
import java.util.List;

/**
 * 字段提取器工厂类
 * 根据URL规则匹配合适的字段提取器
 * 
 * @author yefei.yf
 */
public class FieldExtractorFactory {
    
    private static final List<FieldExtractor> extractors = new ArrayList<>();
    
    static {
        // 初始化注册各种提取器
        // extractors.add(new MeiTuanFieldExtractor());
        // 可以根据需要添加更多提取器
    }
    
    /**
     * 根据URL获取匹配的字段提取器
     * 
     * @param url 待解析的URL
     * @return 匹配的提取器列表
     */
    public static List<FieldExtractor> getExtractors(String url) {
        List<FieldExtractor> matchedExtractors = new ArrayList<>();
        
        for (FieldExtractor extractor : extractors) {
            if (extractor.accept(url)) {
                matchedExtractors.add(extractor);
            }
        }
        
        return matchedExtractors;
    }
    
    /**
     * 注册新的字段提取器
     * 
     * @param extractor 字段提取器
     */
    public static void registerExtractor(FieldExtractor extractor) {
        if (extractor != null && !extractors.contains(extractor)) {
            extractors.add(extractor);
        }
    }
    
    /**
     * 移除字段提取器
     * 
     * @param extractor 要移除的提取器
     */
    public static void removeExtractor(FieldExtractor extractor) {
        extractors.remove(extractor);
    }
    
    /**
     * 获取所有已注册的提取器
     * 
     * @return 提取器列表
     */
    public static List<FieldExtractor> getAllExtractors() {
        return new ArrayList<>(extractors);
    }
    
    /**
     * 清空所有提取器
     */
    public static void clearExtractors() {
        extractors.clear();
    }
}