技术派



- https://www.javaxing.com/2024/01/30/SpringBoot%E5%AE%9E%E7%8E%B0%E5%A4%9A%E6%95%B0%E6%8D%AE%E6%BA%90%E5%88%87%E6%8D%A2%E7%9A%84%E5%A4%9A%E7%A7%8D%E6%96%B9%E6%B3%95/index.html
- https://www.cnblogs.com/xfeiyun/p/16185740.html











```mermaid
sequenceDiagram
    participant App as 应用程序
    participant MyBatis as MyBatis框架
    participant Interceptor as SqlStateInterceptor
    participant DB as 数据库

    App->>MyBatis: 调用 userMapper.findUser("Alice", new Date())
    MyBatis->>Interceptor: 触发拦截器 intercept()
    Interceptor->>MyBatis: 生成完整 SQL（替换占位符）
    Interceptor->>DB: 执行 SQL（invocation.proceed()）
    DB-->>Interceptor: 返回结果
    Interceptor->>App: 返回结果
    Interceptor->>Interceptor: 记录日志（SQL、用户、耗时）
```

