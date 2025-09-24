package io.leavesfly.crawler.strategy.impl;

import io.leavesfly.crawler.domain.CrawlContext;
import io.leavesfly.crawler.domain.RawPage;
import io.leavesfly.crawler.strategy.CrawlStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 美团网站爬取策略实现
 * 
 * @author yefei.yf
 */
public class MeiTuanCrawlStrategy implements CrawlStrategy {
    
    @Override
    public boolean supports(String url) {
        return url != null && url.contains("meituan.com");
    }
    
    @Override
    public String getStrategyName() {
        return "MeiTuanCrawlStrategy";
    }
    
    @Override
    public Map<String, String> getHeaders(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Referer", "https://www.meituan.com/");
        return headers;
    }
    
    @Override
    public long getRequestDelay(String url) {
        // 美团网站设置较长的延迟以避免被封
        return 2000; // 2秒延迟
    }
    
    @Override
    public List<String> extractUrls(CrawlContext context) {
        List<String> urls = new ArrayList<>();
        RawPage rawPage = context.getRawPage();
        
        if (rawPage == null || rawPage.getContent() == null) {
            return urls;
        }
        
        try {
            Document doc = Jsoup.parse(rawPage.getContent());
            String baseUrl = rawPage.getUrl();
            
            // 提取商家详情页链接
            Elements shopLinks = doc.select("a[href*='/shop/']");
            for (Element link : shopLinks) {
                String href = link.attr("href");
                if (href.startsWith("/")) {
                    href = "https://www.meituan.com" + href;
                }
                urls.add(href);
            }
            
            // 提取分类页面链接
            Elements categoryLinks = doc.select("a[href*='/category/']");
            for (Element link : categoryLinks) {
                String href = link.attr("href");
                if (href.startsWith("/")) {
                    href = "https://www.meituan.com" + href;
                }
                urls.add(href);
            }
            
            // 提取下一页链接
            Elements nextPageLinks = doc.select("a.next, a[title*='下一页']");
            for (Element link : nextPageLinks) {
                String href = link.attr("href");
                if (!href.isEmpty()) {
                    if (href.startsWith("/")) {
                        href = "https://www.meituan.com" + href;
                    }
                    urls.add(href);
                }
            }
            
        } catch (Exception e) {
            // 记录解析异常，但不抛出
            context.setAttribute("extractUrlsError", e.getMessage());
        }
        
        return urls;
    }
    
    @Override
    public Object extractData(CrawlContext context) {
        RawPage rawPage = context.getRawPage();
        if (rawPage == null || rawPage.getContent() == null) {
            return null;
        }
        
        try {
            Document doc = Jsoup.parse(rawPage.getContent());
            Map<String, Object> data = new HashMap<>();
            
            // 判断页面类型并提取相应数据
            if (rawPage.getUrl().contains("/shop/")) {
                // 商家详情页
                data = extractShopData(doc);
            } else if (rawPage.getUrl().contains("/category/")) {
                // 分类列表页
                data = extractCategoryData(doc);
            } else {
                // 通用数据提取
                data = extractGeneralData(doc);
            }
            
            data.put("url", rawPage.getUrl());
            data.put("extractTime", System.currentTimeMillis());
            
            return data;
            
        } catch (Exception e) {
            context.setAttribute("extractDataError", e.getMessage());
            return null;
        }
    }
    
    /**
     * 提取商家数据
     */
    private Map<String, Object> extractShopData(Document doc) {
        Map<String, Object> data = new HashMap<>();
        
        // 商家名称
        Element nameElement = doc.selectFirst("h1.shop-name, .poi-name");
        if (nameElement != null) {
            data.put("shopName", nameElement.text().trim());
        }
        
        // 评分
        Element ratingElement = doc.selectFirst(".rating-num, .shop-star");
        if (ratingElement != null) {
            data.put("rating", ratingElement.text().trim());
        }
        
        // 地址
        Element addressElement = doc.selectFirst(".address, .poi-address");
        if (addressElement != null) {
            data.put("address", addressElement.text().trim());
        }
        
        // 电话
        Element phoneElement = doc.selectFirst(".phone, .poi-phone");
        if (phoneElement != null) {
            data.put("phone", phoneElement.text().trim());
        }
        
        data.put("pageType", "shop");
        return data;
    }
    
    /**
     * 提取分类数据
     */
    private Map<String, Object> extractCategoryData(Document doc) {
        Map<String, Object> data = new HashMap<>();
        
        // 分类名称
        Element titleElement = doc.selectFirst("h1, .category-title");
        if (titleElement != null) {
            data.put("categoryName", titleElement.text().trim());
        }
        
        // 商家列表
        Elements shopElements = doc.select(".shop-item, .poi-item");
        List<Map<String, String>> shops = new ArrayList<>();
        
        for (Element shopElement : shopElements) {
            Map<String, String> shop = new HashMap<>();
            
            Element nameEl = shopElement.selectFirst(".shop-name, .poi-name");
            if (nameEl != null) {
                shop.put("name", nameEl.text().trim());
            }
            
            Element linkEl = shopElement.selectFirst("a");
            if (linkEl != null) {
                shop.put("link", linkEl.attr("href"));
            }
            
            if (!shop.isEmpty()) {
                shops.add(shop);
            }
        }
        
        data.put("shops", shops);
        data.put("pageType", "category");
        return data;
    }
    
    /**
     * 提取通用数据
     */
    private Map<String, Object> extractGeneralData(Document doc) {
        Map<String, Object> data = new HashMap<>();
        
        // 页面标题
        data.put("title", doc.title());
        
        // 页面描述
        Element descElement = doc.selectFirst("meta[name=description]");
        if (descElement != null) {
            data.put("description", descElement.attr("content"));
        }
        
        data.put("pageType", "general");
        return data;
    }
    
    @Override
    public String preprocessContent(RawPage rawPage) {
        // 美团页面可能包含大量JavaScript，进行一些预处理
        String content = rawPage.getContent();
        if (content != null) {
            // 移除一些不需要的脚本和样式
            content = content.replaceAll("<script[^>]*>.*?</script>", "");
            content = content.replaceAll("<style[^>]*>.*?</style>", "");
        }
        return content;
    }
    
    @Override
    public int getMaxRetries() {
        return 5; // 美团网站可能不稳定，增加重试次数
    }
    
    @Override
    public int getPriority() {
        return 10; // 高优先级
    }
}