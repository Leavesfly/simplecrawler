# SimpleCrawler - 现代化网络爬虫框架

一个使用多种设计模式构建的高性能、可扩展的Java网络爬虫框架。

## 🚀 特性

### 设计模式应用
- **责任链模式**: 构建灵活的页面处理流水线
- **观察者模式**: 实现事件驱动的状态监控和通知
- **工厂模式**: 统一组件创建和管理
- **策略模式**: 支持不同网站的爬取策略
- **Builder模式**: 提供灵活的配置管理

### 核心功能
- ✅ 多线程并发爬取
- ✅ 异步事件处理
- ✅ 自动重试机制
- ✅ 代理支持
- ✅ 请求延迟控制
- ✅ 实时统计监控
- ✅ 完善的异常处理
- ✅ 模块化架构
- ✅ 高覆盖率单元测试

## 📋 系统要求

- Java 11+
- Maven 3.6+

## 🛠 安装和构建

```bash
# 克隆项目
git clone <repository-url>
cd simplecrawler

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 生成测试覆盖率报告
mvn test jacoco:report

# 打包
mvn package
```

## 🎯 快速开始

### 基本使用

```java
import io.leavesfly.crawler.conf.CrawlerConfig;
import io.leavesfly.crawler.core.ModernCrawlerEngine;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class CrawlerExample {
    public static void main(String[] args) {
        // 1. 创建配置
        CrawlerConfig config = new CrawlerConfig.Builder()
                .connectionTimeout(30, TimeUnit.SECONDS)
                .threadPoolSize(5)
                .delayBetweenRequests(2, TimeUnit.SECONDS)
                .maxRetries(3)
                .build();
        
        // 2. 创建爬虫引擎
        ModernCrawlerEngine crawler = new ModernCrawlerEngine(config);
        
        // 3. 启动爬虫
        crawler.start(Arrays.asList("https://example.com"));
        
        // 4. 运行一段时间后停止
        try {
            Thread.sleep(60000); // 运行1分钟
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 5. 停止爬虫并查看统计
        crawler.stop();
        System.out.println(crawler.getStatistics());
    }
}
```

## 📊 监控和统计

### 实时统计

```java
// 获取统计信息
StatisticsReport stats = crawler.getStatistics();

System.out.println("总页面数: " + stats.getTotalPagesFetched());
System.out.println("成功率: " + String.format("%.2f%%", stats.getSuccessRate() * 100));
System.out.println("运行时间: " + stats.getElapsedTime() + "ms");
System.out.println("队列大小: " + crawler.getQueueSize());
```

## 🔧 自定义扩展

### 创建自定义爬取策略

```java
public class MyWebsiteStrategy implements CrawlStrategy {
    @Override
    public boolean supports(String url) {
        return url.contains("mywebsite.com");
    }
    
    @Override
    public List<String> extractUrls(CrawlContext context) {
        // 实现URL提取逻辑
        return Arrays.asList();
    }
    
    @Override
    public Object extractData(CrawlContext context) {
        // 实现数据提取逻辑
        return null;
    }
    
    @Override
    public String getStrategyName() {
        return "MyWebsiteStrategy";
    }
}

// 注册策略
crawler.getStrategyManager().registerStrategy(new MyWebsiteStrategy());
```

## 🧪 测试

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=CrawlerConfigTest

# 生成测试报告
mvn surefire-report:report

# 生成覆盖率报告
mvn jacoco:report
```

### 测试覆盖率

当前测试覆盖率:
- 行覆盖率: > 85%
- 分支覆盖率: > 80%
- 方法覆盖率: > 90%

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码规范

- 使用中文注释
- 遵循阿里巴巴Java开发手册
- 保持测试覆盖率 > 80%
- 所有public方法必须有文档注释

## 📄 许可证

本项目基于 MIT 许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙋‍♂️ 常见问题

### Q: 如何处理反爬虫机制？
A: 
1. 设置合理的请求延迟
2. 使用代理IP池
3. 随机化User-Agent
4. 实现Session管理
5. 模拟真实浏览器行为

### Q: 如何提高爬取效率？
A:
1. 增加线程池大小
2. 使用连接池
3. 启用HTTP/2
4. 实现智能重试
5. 优化数据存储

### Q: 如何扩展支持更多网站？
A:
1. 实现CrawlStrategy接口
2. 注册到StrategyManager
3. 实现URL匹配逻辑
4. 添加对应的测试用例

## 📞 联系方式

- 作者: yefei.yf
- 邮箱: [your-email@example.com]
- 项目地址: [https://github.com/your-username/simplecrawler]

---

**SimpleCrawler** - 让网络爬虫开发变得简单而强大！ 🚀