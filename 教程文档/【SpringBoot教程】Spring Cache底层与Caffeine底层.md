- ### [41.Spring Cache 了解吗？](https://javabetter.cn/sidebar/sanfene/spring.html#_41-spring-cache-了解吗)

  Spring Cache 是 Spring 框架提供的一个缓存抽象，它通过统一的接口来支持多种缓存实现（如 Redis、Caffeine 等）。

  它通过注解（如 `@Cacheable`、`@CachePut`、`@CacheEvict`）来实现缓存管理，极大简化了代码实现。

  - @Cacheable：缓存方法的返回值。
  - @CachePut：用于更新缓存，每次调用方法都会将结果重新写入缓存。
  - @CacheEvict：用于删除缓存。

  使用示例：

  ![二哥的Java 进阶之路：Spring Cache](https://cdn.tobebetterjavaer.com/stutymore/spring-20241031111306.png)二哥的Java 进阶之路：Spring Cache

  #### [Spring Cache 和 Redis 有什么区别？](https://javabetter.cn/sidebar/sanfene/spring.html#spring-cache-和-redis-有什么区别)

  1. **Spring Cache** 是 Spring 框架提供的一个缓存抽象，它通过注解来实现缓存管理，支持多种缓存实现（如 Redis、Caffeine 等）。
  2. **Redis** 是一个分布式的缓存中间件，支持多种数据类型（如 String、Hash、List、Set、ZSet），还支持持久化、集群、主从复制等。

  Spring Cache 适合用于单机、轻量级和短时缓存场景，能够通过注解轻松控制缓存管理。

  Redis 是一种分布式缓存解决方案，支持多种数据结构和高并发访问，适合分布式系统和高并发场景，可以提供数据持久化和多种淘汰策略。

  在实际开发中，Spring Cache 和 Redis 可以结合使用，Spring Cache 提供管理缓存的注解，而 Redis 则作为分布式缓存的实现，提供共享缓存支持。

  #### [有了 Redis 为什么还需要 Spring Cache？](https://javabetter.cn/sidebar/sanfene/spring.html#有了-redis-为什么还需要-spring-cache)

  虽然 Redis 非常强大，但 Spring Cache 提供了一层缓存抽象，简化了缓存的管理。我们可以直接在方法上通过注解来实现缓存逻辑，减少了手动操作 Redis 的代码量。

  Spring Cache 还能灵活切换底层缓存实现。此外，Spring Cache 支持事务性缓存和条件缓存，便于在复杂场景中确保数据一致性。

  #### [说说Spring Cache 的底层原理？](https://javabetter.cn/sidebar/sanfene/spring.html#说说spring-cache-的底层原理)

  Spring Cache 是基于 AOP 和缓存抽象层实现的。它通过 AOP 拦截被 @Cacheable、@CachePut 和 @CacheEvict 注解的方法，在方法调用前后自动执行缓存逻辑。

  ![铿然架构：Spring Cache 架构](https://cdn.tobebetterjavaer.com/stutymore/spring-20241031113743.png)铿然架构：Spring Cache 架构

  其提供的 CacheManager 和 Cache 等接口，不依赖具体的缓存实现，因此可以灵活地集成 Redis、Caffeine 等多种缓存。

  - ConcurrentMapCacheManager：基于 Java ConcurrentMap 的本地缓存实现。
  - RedisCacheManager：基于 Redis 的分布式缓存实现。
  - CaffeineCacheManager：基于 Caffeine 的缓存实现。