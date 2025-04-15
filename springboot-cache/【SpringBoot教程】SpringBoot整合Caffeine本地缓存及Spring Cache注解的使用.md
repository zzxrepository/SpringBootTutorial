> 毛毛张今天要介绍的是本地缓存之王！Caffeine！SpringBoot整合Caffeine本地缓存及Spring Cache注解的使用





[toc]

# 1.Caffeine本地缓存

## 1.1 本地缓存技术选型

- 缓存是一种常用的技术，通过将常用数据存储在缓存中，可以在一定程度上提升数据存取的速度，这正是局部性原理的应用。传统的缓存大多是分布式的，例如 Redis，虽然性能强大，但需要额外的服务支持，且在数据传输上会增加一定的耗时。对于一些小型应用，使用 Redis 可能会显得过于复杂和资源浪费，此时可以考虑使用本地缓存。

- 常见的本地缓存技术选型包括：

  - **使用 ConcurrentHashMap 作为缓存**：这是最简单的缓存实现方式，但功能较为单一，没有内存淘汰策略，需要开发人员进行定制化开发。

  - **Guava Cache**：Guava 是 Google 团队开源的一款 Java 核心增强库，包含集合、并发原语、缓存、IO、反射等工具箱，性能和稳定性都有保障，应用广泛。Guava Cache 支持最大容量限制、过期删除策略（基于插入时间和访问时间）、简单统计功能等，基于 LRU 算法实现。

  - **Caffeine 缓存**：Caffeine 是一个高性能的 Java 本地缓存库，设计用于提供快速响应时间和高并发处理能力。它具有类似于 Guava 缓存的简单易用的 API，同时提供了许多额外的功能和性能优化。Caffeine 支持缓存大小限制、缓存过期策略、异步加载数据等特性，可以帮助开发人员在应用程序中有效地管理和优化缓存。Caffeine 还提供了可自定义的缓存策略和监听器，以帮助开发人员根据实际需求定制缓存行为。

  - **基于 Ehcache 实现本地缓存**：Ehcache 是一个流行的 Java 开源缓存框架，用于在应用程序中管理缓存数据。它被广泛用于提高应用程序性能，减少数据库访问频率，和减少网络开销。与 Caffeine 和 Guava Cache 相比，Ehcache 的功能更加丰富，扩展性更强。

- **总结**：使用 Caffeine 作为本地缓存可以显著提升缓存数据的访问效率和读取性能。然而，本地缓存的使用受限于本地缓存的大小，对于缓存数据量大或数据结构复杂的情况，建议使用第三方缓存服务，例如 Redis。

## 1.2 Caffeine缓存介绍

- Caffeine是一个基于Java8开发的提供了[近乎最佳](https://github.com/ben-manes/caffeine/wiki/Efficiency-zh-CN)命中率的[高性能](https://github.com/ben-manes/caffeine/wiki/Benchmarks-zh-CN)的缓存库。缓存和[ConcurrentMap](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentMap.html)有点相似，但还是有所区别。最根本的区别是[ConcurrentMap](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentMap.html)将会持有所有加入到缓存当中的元素，直到它们被从缓存当中手动移除。但是，Caffeine的缓存`Cache` 通常会被配置成自动驱逐缓存中元素，以限制其内存占用。在某些场景下，`LoadingCache`和`AsyncLoadingCache` 因为其自动加载缓存的能力将会变得非常实用。

- Caffeine提供了灵活的构造器去创建一个拥有下列特性的缓存：

  - [自动加载](https://github.com/ben-manes/caffeine/wiki/Population-zh-CN)元素到缓存当中，异步加载的方式也可供选择

  - 当达到最大容量的时候可以使用基于[就近度和频率](https://github.com/ben-manes/caffeine/wiki/Efficiency-zh-CN)的算法进行[基于容量的驱逐](https://github.com/ben-manes/caffeine/wiki/Eviction-zh-CN#基于容量)

  - 将根据缓存中的元素上一次访问或者被修改的时间进行[基于过期时间的驱逐](https://github.com/ben-manes/caffeine/wiki/Eviction-zh-CN#基于时间)

  - 当向缓存中一个已经过时的元素进行访问的时候将会进行[异步刷新](https://github.com/ben-manes/caffeine/wiki/Refresh-zh-CN)

  - key将自动被[弱引用](https://github.com/ben-manes/caffeine/wiki/Eviction-zh-CN#基于引用)所封装

  - value将自动被[弱引用或者软引用](https://github.com/ben-manes/caffeine/wiki/Eviction-zh-CN#基于引用)所封装

  - 驱逐(或移除)缓存中的元素时将会进行[通知](https://github.com/ben-manes/caffeine/wiki/Removal-zh-CN)

  - [写入传播](https://github.com/ben-manes/caffeine/wiki/Writer-zh-CN)到一个外部数据源当中

  - 持续计算缓存的访问[统计指标](https://github.com/ben-manes/caffeine/wiki/Statistics-zh-CN)

- 为了提高集成度，扩展模块提供了[JSR-107 JCache](https://github.com/ben-manes/caffeine/wiki/JCache-zh-CN)和[Guava](https://github.com/ben-manes/caffeine/wiki/Guava-zh-CN)适配器。JSR-107规范了基于Java 6的API，在牺牲了功能和性能的代价下使代码更加规范。Guava的Cache是Caffeine的原型库并且Caffeine提供了适配器以供简单的迁移策略。

- **总结： Caffeine 是基于 JAVA 8 的高性能缓存库。并且在Spring5 (SpringBoot2.x) 后，Spring 官方放弃了 Guava，而使用了性能更优秀的Caffeine作为默认缓存组件。**

- **缓存性能对比图：可以通过下图观测到，在下面缓存组件中 Caffeine 性能是其中最好的**

![Benchmarks zh CN · ben-manes/caffeine Wiki · GitHub](https://raw.githubusercontent.com/ben-manes/caffeine/master/wiki/throughput/read.png)

# 2.SpringBoot集成Caffeine

- **SpringBoot提供了对缓存的良好支持，并且可以轻松地整合Caffeine本地缓存作为缓存提供者，结合Spring Cache注解，可以实现对方法级别的缓存，从而提高系统的性能和响应速度。**

- **SpringBoot有两种使用Caffeine作为缓存的方式：**
  - **方式一：引入Caffeine和Spring Cache依赖，使用SpringCache注解方法实现缓存**
  - **方式二：直接引入Caffeine依赖，然后使用Caffeine方法实现缓存。**

> 下面将介绍下，这两种集成方式都是如何实现的，毛毛张首先教大家构建出来一个代码，然后再解释具体的配置说明

## 2.1 SpringBoot集成Caffeine方式1-推荐

### 2.1.1 创建项目

- 首先创建一个SpringBoot项目，SpringBoot版本为2.7.6，JDK版本为1.8，具体的步骤毛毛张就不详细说了，下图是毛毛张整个项目的目录结构：

![QQ_1740023460809](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740023460809.png)

### 2.1.2 导入依赖

- 和缓存相关的依赖：

  ```xml
  <!-- Spring Boot 缓存启动器依赖，用于支持 Spring Boot 的缓存功能 -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-cache</artifactId>
  </dependency>
  <!-- Caffeine 缓存库依赖，Spring Boot 缓存的一个实现 -->
  <dependency>
      <groupId>com.github.ben-manes.caffeine</groupId>
      <artifactId>caffeine</artifactId>
  </dependency>
  ```

- 整个项目的完整依赖：

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
      <!-- 定义 POM 模型的版本，固定为 4.0.0 -->
      <modelVersion>4.0.0</modelVersion>
      <groupId>com.zzx</groupId>
      <artifactId>springboot-cache-demo1</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <name>springboot-cache-demo1</name>
      <description>springboot-cache-demo1</description>
  
      <!-- 定义项目的父项目，继承父项目的配置和依赖管理 -->
      <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-parent</artifactId>
          <version>2.7.6</version>
      </parent>
  
      <!-- 定义项目的属性，可在 POM 中引用 -->
      <properties>
          <java.version>1.8</java.version>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
          <spring-boot.version>2.7.6</spring-boot.version>
      </properties>
      <!-- 定义项目的依赖项 -->
      <dependencies>
          <!-- Spring Boot Web 启动器依赖，用于创建基于 Spring Boot 的 Web 应用 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
  
          <!-- Spring Boot 缓存启动器依赖，用于支持 Spring Boot 的缓存功能 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-cache</artifactId>
          </dependency>
          <!-- Caffeine 缓存库依赖，Spring Boot 缓存的一个实现 -->
          <dependency>
              <groupId>com.github.ben-manes.caffeine</groupId>
              <artifactId>caffeine</artifactId>
          </dependency>
  
          <!-- Lombok 依赖，用于减少 Java 代码中的样板代码，如 getter、setter 等 -->
          <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <!-- optional 为 true 表示该依赖不会传递给依赖本项目的其他项目 -->
              <optional>true</optional>
          </dependency>
      </dependencies>
      
      <!-- 依赖管理部分，用于统一管理依赖的版本 -->
      <dependencyManagement>
          <dependencies>
              <!-- 引入 Spring Boot 依赖管理 POM，确保依赖的版本一致性 -->
              <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-dependencies</artifactId>
                  <!-- 使用之前定义的 Spring Boot 版本号 -->
                  <version>${spring-boot.version}</version>
                  <!-- 依赖类型为 POM -->
                  <type>pom</type>
                  <!-- 导入范围，表示导入该 POM 的依赖管理 -->
                  <scope>import</scope>
              </dependency>
          </dependencies>
      </dependencyManagement>
  
      <build>
          <plugins>
              <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-compiler-plugin</artifactId>
                  <version>3.8.1</version>
                  <configuration>
                      <source>1.8</source>
                      <target>1.8</target>
                      <encoding>UTF-8</encoding>
                  </configuration>
              </plugin>
              <!-- Spring Boot Maven 插件，用于打包和运行 Spring Boot 应用 -->
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
                  <version>${spring-boot.version}</version>
                  <configuration>
                      <mainClass>com.zzx.SpringbootCacheDemo1Application</mainClass>
                      <skip>true</skip>
                  </configuration>
                  <executions>
                      <execution>
                          <id>repackage</id>
                          <goals>
                              <goal>repackage</goal>
                          </goals>
                      </execution>
                  </executions>
              </plugin>
          </plugins>
      </build>
  
  </project>
  ```

### 2.1.3 配置文件application.yaml

- 配置文件：

  ```yaml
  # 应用服务 WEB 访问端口
  server:
    port: 8080
  ```

### 2.1.4 实体类UserInfo

- 创建实体类对象：

  ```java
  package com.zzx.entity;
  
  import lombok.Data;
  import lombok.ToString;
  
  import java.io.Serializable;
  
  @Data
  @ToString
  public class UserInfo implements Serializable {
      private Integer id;
      private String name;
      private String sex;
      private Integer age;
  }
  ```

### 2.1.5 配置缓存配置类CacheManagerConfig

- 缓存配置类：

  ```java
  package com.zzx.config;
  
  import com.github.benmanes.caffeine.cache.Caffeine;
  import org.springframework.cache.CacheManager;
  import org.springframework.cache.annotation.EnableCaching;
  import org.springframework.cache.caffeine.CaffeineCacheManager;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  
  import java.util.concurrent.TimeUnit;
  
  @EnableCaching
  @Configuration
  public class CacheManagerConfig {
  
      @Bean("caffeineCacheManager")
      public CacheManager cacheManager() {
          CaffeineCacheManager cacheManager = new CaffeineCacheManager();
          cacheManager.setCaffeine(Caffeine.newBuilder()
                  // 设置过期时间，写入后五分钟过期
                  .expireAfterWrite(5, TimeUnit.MINUTES)
                  // 初始化缓存空间大小
                  .initialCapacity(100)
                  // 最大的缓存条数
                  .maximumSize(200)
          );
          return cacheManager;
      }
  }
  ```

### 2.1.6 定义服务接口类和实现类

- 服务接口类：

  ```java
  package com.zzx.service;
  
  import com.zzx.entity.UserInfo;
  
  public interface UserInfoService {
  
      /**
       * 增加用户信息
       *
       * @param userInfo 用户信息
       */
      UserInfo addUserInfo(UserInfo userInfo);
  
      /**
       * 获取用户信息
       *
       * @param id 用户ID
       * @return 用户信息
       */
      UserInfo getByName(Integer id);
  
      /**
       * 修改用户信息
       *
       * @param userInfo 用户信息
       * @return 用户信息
       */
      UserInfo updateUserInfo(UserInfo userInfo);
  
      /**
       * 删除用户信息
       *
       * @param id 用户ID
       */
      void deleteById(Integer id);
  
  }
  ```

- 服务实现类：

  ```java
  package com.zzx.service.impl;
  
  import com.zzx.entity.UserInfo;
  import com.zzx.service.UserInfoService;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.cache.Cache;
  import org.springframework.cache.CacheManager;
  import org.springframework.cache.annotation.CacheConfig;
  import org.springframework.cache.annotation.CacheEvict;
  import org.springframework.cache.annotation.CachePut;
  import org.springframework.cache.annotation.Cacheable;
  import org.springframework.stereotype.Service;
  import org.springframework.util.StringUtils;
  
  import java.util.HashMap;
  
  @Slf4j
  @Service
  @CacheConfig(cacheNames = "userInfo")
  public class UserInfoServiceImpl implements UserInfoService {
      /**
       * 模拟数据库存储数据
       */
      private HashMap<Integer, UserInfo> userInfoMap = new HashMap<>();
  
      @Override
      @CachePut(key = "#userInfo.id")
      public UserInfo addUserInfo(UserInfo userInfo) {
          userInfoMap.put(userInfo.getId(), userInfo);
          log.info("数据添加成功！");
          log.info(userInfoMap.toString());
          return userInfo;
      }
  
  
      @Override
      @Cacheable(key = "#id")
      public UserInfo getByName(Integer id) {
          //如果经过这个函数就说明没有走缓存
          UserInfo userInfo = userInfoMap.get(id);
          log.info("没有走缓存！");
          return userInfo;
      }
  
      @Override
      @CachePut(key = "#userInfo.id")
      public UserInfo updateUserInfo(UserInfo userInfo) {
          if (!userInfoMap.containsKey(userInfo.getId())) {
              return null;
          }
          // 取旧的值
          UserInfo oldUserInfo = userInfoMap.get(userInfo.getId());
          // 替换内容
          if (!StringUtils.isEmpty(oldUserInfo.getAge())) {
              oldUserInfo.setAge(userInfo.getAge());
          }
          if (!StringUtils.isEmpty(oldUserInfo.getName())) {
              oldUserInfo.setName(userInfo.getName());
          }
          if (!StringUtils.isEmpty(oldUserInfo.getSex())) {
              oldUserInfo.setSex(userInfo.getSex());
          }
          // 将新的对象存储，更新旧对象信息
          userInfoMap.put(oldUserInfo.getId(), oldUserInfo);
          log.info("数据更新成功");
          log.info(userInfoMap.toString());
          // 返回新对象信息
          return oldUserInfo;
      }
  
      @Override
      @CacheEvict(key = "#id")
      public void deleteById(Integer id) {
          log.info("delete");
          userInfoMap.remove(id);
      }
  
  }
  ```

### 2.1.7 控制层

- 控制层代码：

  ```java
  package com.zzx.controller;
  
  import com.zzx.entity.UserInfo;
  import com.zzx.service.UserInfoService;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.web.bind.annotation.*;
  
  @Slf4j
  @RestController
  @RequestMapping
  public class UserInfoController {
  
      @Autowired
      private UserInfoService userInfoService;
  
      @GetMapping("/userInfo/{id}")
      public Object getUserInfo(@PathVariable Integer id) {
          log.info("查询用户接口");
          UserInfo userInfo = userInfoService.getByName(id);
          if (userInfo == null) {
              return "没有该用户";
          }
          return userInfo;
      }
  
      @PostMapping("/userInfo")
      public Object createUserInfo(@RequestBody UserInfo userInfo) {
          log.info("创建用户接口");
          userInfoService.addUserInfo(userInfo);
          return "SUCCESS";
      }
  
      @PutMapping("/userInfo")
      public Object updateUserInfo(@RequestBody UserInfo userInfo) {
          log.info("更新用户接口");
          UserInfo newUserInfo = userInfoService.updateUserInfo(userInfo);
          if (newUserInfo == null){
              return "不存在该用户";
          }
          return newUserInfo;
      }
  
      @DeleteMapping("/userInfo/{id}")
      public Object deleteUserInfo(@PathVariable Integer id) {
          log.info("删除用户接口");
          userInfoService.deleteById(id);
          return "SUCCESS";
      }
  
  }
  ```

### 2.1.8 启动类

- 启动类代码：

  ```java
  package com.zzx;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cache.annotation.EnableCaching;
  
  @EnableCaching
  @SpringBootApplication
  public class SpringbootCacheDemo1Application {
  
      public static void main(String[] args) {
          SpringApplication.run(SpringbootCacheDemo1Application.class, args);
      }
  
  }
  ```



## 2.2 SpringBoot集成Caffeine方式2

### 2.2.1 创建项目

- 首先创建一个SpringBoot项目，SpringBoot版本为2.7.6，JDK版本为1.8，具体的步骤毛毛张就不详细说了，下图是毛毛张整个项目的目录结构：

![QQ_1740061669751](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740061669751.png)

### 2.2.2 导入依赖

- 和缓存相关的依赖：

  ```xml
  <!-- Caffeine 缓存库依赖，Spring Boot 缓存的一个实现 -->
  <dependency>
      <groupId>com.github.ben-manes.caffeine</groupId>
      <artifactId>caffeine</artifactId>
  </dependency>
  ```

- 整个项目的完整依赖：

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <groupId>com.zzx</groupId>
      <artifactId>springboot-cache-demo2</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <name>springboot-cache-demo2</name>
      <description>springboot-cache-demo2</description>
      <properties>
          <java.version>1.8</java.version>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
          <spring-boot.version>2.7.6</spring-boot.version>
      </properties>
      <dependencies>
          <!-- Spring Boot Web 启动器依赖，用于创建基于 Spring Boot 的 Web 应用 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
  
          <!-- Caffeine 缓存库依赖，Spring Boot 缓存的一个实现 -->
          <dependency>
              <groupId>com.github.ben-manes.caffeine</groupId>
              <artifactId>caffeine</artifactId>
          </dependency>
  
          <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <optional>true</optional>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-test</artifactId>
              <scope>test</scope>
          </dependency>
      </dependencies>
      <dependencyManagement>
          <dependencies>
              <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-dependencies</artifactId>
                  <version>${spring-boot.version}</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
          </dependencies>
      </dependencyManagement>
  
      <build>
          <plugins>
              <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-compiler-plugin</artifactId>
                  <version>3.8.1</version>
                  <configuration>
                      <source>1.8</source>
                      <target>1.8</target>
                      <encoding>UTF-8</encoding>
                  </configuration>
              </plugin>
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
                  <version>${spring-boot.version}</version>
                  <configuration>
                      <mainClass>com.zzx.SpringbootCacheDemo2Application</mainClass>
                      <skip>true</skip>
                  </configuration>
                  <executions>
                      <execution>
                          <id>repackage</id>
                          <goals>
                              <goal>repackage</goal>
                          </goals>
                      </execution>
                  </executions>
              </plugin>
          </plugins>
      </build>
  
  </project>
  
  ```

### 2.2.3 配置文件application.yaml

- 配置文件：

  ```yaml
  # 应用服务 WEB 访问端口
  server:
    port: 8080
  ```

### 2.2.4 实体类UserInfo

- 创建实体类对象：

  ```java
  package com.zzx.entity;
  
  import lombok.Data;
  import lombok.ToString;
  
  import java.io.Serializable;
  
  @Data
  @ToString
  public class UserInfo implements Serializable {
      private Integer id;
      private String name;
      private String sex;
      private Integer age;
  }
  ```

### 2.2.5 配置缓存配置类CacheManagerConfig

- 缓存配置类：

  ```java
  package com.zzx.config;
  
  import com.github.benmanes.caffeine.cache.Cache;
  import com.github.benmanes.caffeine.cache.Caffeine;
  import org.springframework.cache.annotation.EnableCaching;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import java.util.concurrent.TimeUnit;
  
  @Configuration
  public class CacheConfig {
  
      @Bean
      public Cache<String, Object> caffeineCache() {
          return Caffeine.newBuilder()
                  // 设置最后一次写入或访问后经过固定时间过期
                  .expireAfterWrite(60, TimeUnit.SECONDS)
                  // 初始的缓存空间大小
                  .initialCapacity(100)
                  // 缓存的最大条数
                  .maximumSize(1000)
                  .build();
      }
  
  }
  ```

### 2.2.6 定义服务接口类和实现类

- 服务接口类：

  ```java
  package com.zzx.service;
  
  import com.zzx.entity.UserInfo;
  
  public interface UserInfoService {
  
      /**
       * 增加用户信息
       *
       * @param userInfo 用户信息
       */
      UserInfo addUserInfo(UserInfo userInfo);
  
      /**
       * 获取用户信息
       *
       * @param id 用户ID
       * @return 用户信息
       */
      UserInfo getByName(Integer id);
  
      /**
       * 修改用户信息
       *
       * @param userInfo 用户信息
       * @return 用户信息
       */
      UserInfo updateUserInfo(UserInfo userInfo);
  
      /**
       * 删除用户信息
       *
       * @param id 用户ID
       */
      void deleteById(Integer id);
  
  }
  ```

- 服务实现类：

  ```java
  package com.zzx.service.impl;
  
  import com.zzx.entity.UserInfo;
  import com.zzx.service.UserInfoService;
  import com.github.benmanes.caffeine.cache.Cache;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.cache.annotation.CacheConfig;
  import org.springframework.stereotype.Service;
  import org.springframework.util.StringUtils;
  import java.util.HashMap;
  
  @Slf4j
  @Service
  @CacheConfig(cacheNames = "userInfo")
  public class UserInfoServiceImpl implements UserInfoService {
      /**
       * 模拟数据库存储数据
       */
      private HashMap<Integer, UserInfo> userInfoMap = new HashMap<>();
  
      @Autowired
      Cache<String, Object> caffeineCache;
  
      @Override
      public UserInfo addUserInfo(UserInfo userInfo) {
          log.info("create");
          userInfoMap.put(userInfo.getId(), userInfo);
          // 加入缓存
          caffeineCache.put(String.valueOf(userInfo.getId()),userInfo);
          return userInfo;
      }
  
      @Override
      public UserInfo getByName(Integer id) {
          // 先从缓存读取
          caffeineCache.getIfPresent(id);
          UserInfo userInfo = (UserInfo) caffeineCache.asMap().get(String.valueOf(id));
          if (userInfo != null){
              return userInfo;
          }
          // 如果缓存中不存在，则从库中查找
          log.info("get");
          userInfo = userInfoMap.get(id);
          // 如果用户信息不为空，则加入缓存
          if (userInfo != null){
              caffeineCache.put(String.valueOf(userInfo.getId()),userInfo);
          }
          return userInfo;
      }
  
      @Override
      public UserInfo updateUserInfo(UserInfo userInfo) {
          log.info("update");
          if (!userInfoMap.containsKey(userInfo.getId())) {
              return null;
          }
          // 取旧的值
          UserInfo oldUserInfo = userInfoMap.get(userInfo.getId());
          // 替换内容
          if (!StringUtils.isEmpty(oldUserInfo.getAge())) {
              oldUserInfo.setAge(userInfo.getAge());
          }
          if (!StringUtils.isEmpty(oldUserInfo.getName())) {
              oldUserInfo.setName(userInfo.getName());
          }
          if (!StringUtils.isEmpty(oldUserInfo.getSex())) {
              oldUserInfo.setSex(userInfo.getSex());
          }
          // 将新的对象存储，更新旧对象信息
          userInfoMap.put(oldUserInfo.getId(), oldUserInfo);
          // 替换缓存中的值
          caffeineCache.put(String.valueOf(oldUserInfo.getId()),oldUserInfo);
          return oldUserInfo;
      }
  
      @Override
      public void deleteById(Integer id) {
          log.info("delete");
          userInfoMap.remove(id);
          // 从缓存中删除
          caffeineCache.asMap().remove(String.valueOf(id));
      }
  
  
  }
  ```

### 2.1.7 控制层

- 控制层代码：

  ```java
  package com.zzx.controller;
  
  import com.zzx.entity.UserInfo;
  import com.zzx.service.UserInfoService;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.web.bind.annotation.*;
  
  @Slf4j
  @RestController
  @RequestMapping
  public class UserInfoController {
  
      @Autowired
      private UserInfoService userInfoService;
  
      @GetMapping("/userInfo/{id}")
      public Object getUserInfo(@PathVariable Integer id) {
          log.info("查询用户接口");
          UserInfo userInfo = userInfoService.getByName(id);
          if (userInfo == null) {
              return "没有该用户";
          }
          return userInfo;
      }
  
      @PostMapping("/userInfo")
      public Object createUserInfo(@RequestBody UserInfo userInfo) {
          log.info("创建用户接口");
          userInfoService.addUserInfo(userInfo);
          return "SUCCESS";
      }
  
      @PutMapping("/userInfo")
      public Object updateUserInfo(@RequestBody UserInfo userInfo) {
          log.info("更新用户接口");
          UserInfo newUserInfo = userInfoService.updateUserInfo(userInfo);
          if (newUserInfo == null){
              return "不存在该用户";
          }
          return newUserInfo;
      }
  
      @DeleteMapping("/userInfo/{id}")
      public Object deleteUserInfo(@PathVariable Integer id) {
          log.info("删除用户接口");
          userInfoService.deleteById(id);
          return "SUCCESS";
      }
  
  }
  ```

### 2.1.8 启动类

- 启动类代码：

  ```java
  package com.zzx;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cache.annotation.EnableCaching;
  
  @EnableCaching
  @SpringBootApplication
  public class SpringbootCacheDemo1Application {
  
      public static void main(String[] args) {
          SpringApplication.run(SpringbootCacheDemo1Application.class, args);
      }
  
  }
  ```

## 2.3 接口测试

第一次查询ID为1的用户：`http://localhost:8080/userInfo/1` （Get请求）

- 返回结果：

![QQ_1740062248079](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062248079.png)

- 控制台输出：

![QQ_1740062259299](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062259299.png)

第二次查询ID为1的用户：`http://localhost:8080/userInfo/1` （Get请求）

- 返回结果：

![QQ_1740062248079](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062248079.png)

- 控制台输出：可以发现第一次查询的时候缓存的结果为空，第二次查询的时候直接走的缓存，不用查了，没有该用户

![QQ_1740062372246](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062372246.png)

第三次添加ID为1的用户：

![QQ_1740062461225](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062461225.png)

- 返回结果：说明添加成功

![QQ_1740062491549](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062491549.png)

- 控制台输出：

![QQ_1740062519562](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062519562.png)

第四次查询ID为1的用户：`http://localhost:8080/userInfo/1` （Get请求）

- 返回结果：

![QQ_1740062576318](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062576318.png)

- 控制台输出：可以发现第三次添加用户的时候缓存的该用户，第四次查询的时候直接走的缓存，不用查了，直接从缓存中取值

![QQ_1740062615801](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062615801.png)

第五次删除ID为1的用户：`http://localhost:8080/userInfo/1`（Delete请求）

- **返回结果：用户删除成功，缓存也删除成功**

![QQ_1740062776463](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062776463.png)

- **控制台输出：删除成功，那么第六次查的时候就找不到该用户了，并且不会走缓存**

![QQ_1740062798151](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062798151.png)

第六次查询ID为1的用户：`http://localhost:8080/userInfo/1` （Get请求）

- 返回结果：用户删除成功了，找不到ID为1的用户

![QQ_1740062882972](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062882972.png)

- 控制台输出：没有走缓存，第五次删除请求的时候，缓存也删除了

![QQ_1740062913944](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740062913944.png)

# 3.配置详解

- **上面毛毛张介绍了SpringBoot集成Caffeine的两种方式，但是对于SpringBoot的项目更推荐方式1，所以下面毛毛张将着重围绕方式1的配置来进行介绍；对于方式2，可以在非 Spring 项目中使用。**

## 3.1 配置依赖

- 使用Caffeine需要在`pom.xml`文件中添加`Caffeine`的依赖，如果结合Spring Cache注解还需要添加Spring Cache的依赖，主要是下面两个依赖：

  ```xml
  <!-- Spring Boot 缓存启动器依赖，用于支持 Spring Boot 的缓存功能 即Spring Cache的依赖-->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-cache</artifactId>
  </dependency>
  <!-- Caffeine 缓存库依赖，Spring Boot 缓存的一个实现 -->
  <dependency>
      <groupId>com.github.ben-manes.caffeine</groupId>
      <artifactId>caffeine</artifactId>
  </dependency>
  ```

## 3.2 配置Caffeine缓存管理器

### 3.2.1 通过配置类的方式配置

```java
package com.zzx.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheManagerConfig {
	
    //将cacheManager方法返回的对象注册为 Spring 容器中的 Bean，Bean 的名称为caffeineCacheManager
    @Bean("caffeineCacheManager")
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置过期时间，写入后五分钟过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 初始化缓存空间大小
                .initialCapacity(100)
                // 最大的缓存条数
                .maximumSize(200)
        );
        return cacheManager;
    }
}

```

#### Caffeine 配置说明

**配置参数总览：**

| 参数                | 类型       | 描述                                                         |
| ------------------- | ---------- | ------------------------------------------------------------ |
| `initialCapacity`   | `integer`  | 缓存的初始空间大小，预先分配一定空间存储缓存项，减少扩容操作以提高性能。 |
| `maximumSize`       | `long`     | 缓存的最大条数，当缓存中的条目数量达到该值时，Caffeine 会按指定策略清理缓存。 |
| `maximumWeight`     | `long`     | 缓存的最大权重，允许为每个缓存项分配不同权重，当所有缓存项权重总和超该值时触发淘汰。 |
| `expireAfterAccess` | `duration` | 缓存项在最后一次读或写操作后，经过指定时间过期。             |
| `expireAfterWrite`  | `duration` | 缓存项在最后一次写操作后，经过指定时间过期。                 |
| `refreshAfterWrite` | `duration` | 缓存项在创建或最近一次更新后，经过指定时间间隔自动刷新，刷新在后台异步进行。 |
| `weakKeys`          | `boolean`  | 打开 key 的弱引用。弱引用对象生命周期短暂，垃圾回收器扫描时，只要发现只具弱引用的对象，无论内存是否充足都会回收其内存。 |
| `weakValues`        | `boolean`  | 打开 value 的弱引用。                                        |
| `softValues`        | `boolean`  | 打开 value 的软引用。若对象仅具软引用，内存充足时垃圾回收器不回收，内存不足时则回收其内存。 |
| `recordStats`       | `boolean`  | 开启统计功能，用于监控缓存的命中率、加载时间等统计信息。     |

**配置注意事项：**

- `weakValues` 和 `softValues` 不能同时使用，因为它们是不同的引用类型，同时使用会产生冲突。
- `maximumSize` 和 `maximumWeight` 不能同时使用，它们是不同的缓存容量限制方式，同时使用会导致冲突。
- `expireAfterWrite`和`expireAfterAccess`同事存在时，以`expireAfterWrite`为准。

**软引用与弱引用：**

- 软引用：如果一个对象只具有软引用，则内存空间足够，垃圾回收器就不会回收它；如果内存空间不足了，就会回收这些对象的内存。
- 弱引用：弱引用的对象拥有更短暂的生命周期。在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存

```java
// 软引用
Caffeine.newBuilder().softValues().build();

// 弱引用
Caffeine.newBuilder().weakKeys().weakValues().build();
```

### 3.2.2 通过配置文件的方式配置-不推荐

```yaml
# 应用程序基本信息配置
spring:
  cache:
    type: caffeine  # 指定使用 Caffeine 作为缓存实现
    caffeine:
      spec: initialCapacity=100,maximumSize=200,expireAfterWrite=5m  # Caffeine 缓存具体参数配置

# 开启缓存自动配置（通常 Spring Boot 默认开启，这里列出供参考）
cache:
  enabled: true
```

### 3.2.3 总结

- **上面毛毛张介绍了两种方式配置Caffeine缓存管理器，大家觉得哪种方式好呢？**
  - 答案：**第一种方式**，为什么呢？因为若要配置多个缓存，第一种方式可通过创建多个带有不同 `@Bean` 名称的 `CacheManager` 方法实现。比如在当前配置基础上，新增一个 `@Bean("anotherCaffeineCacheManager")` 注解的方法，在该方法里创建新的 `CaffeineCacheManager` 实例，并使用 `Caffeine.newBuilder()` 配置不同的缓存参数，像设置不同的过期时间、初始容量和最大缓存条数等，例如 `.expireAfterWrite(10, TimeUnit.MINUTES).initialCapacity(200).maximumSize(300)` ，最后返回新配置好的 `CaffeineCacheManager` 实例，这样就可在 Spring 容器中注册多个不同配置的缓存管理器，供不同场景使用。然而第二种方式通过配置类的方式就比较复杂。

## 3.3 Spring Cache框架注解介绍和使用

- Caffeine缓存库结合Spring Cache框架来使用，主要通过下面五个注解来实现

### 3.3.1 @EnableCaching

**作用： 该注解的作用是开启Spring缓存功能，一般放在启动类上，或者也可以放在上面的配置类上，如下面代码所示：**

```java
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, Object> caffeineCache() {
		....
    }
}
```

### 3.3.2 @Cacheable

#### 注解简介

**作用：**

- 该注解的作用是对方法的返回结果进行缓存。当再次请求该方法时，会先检查缓存里是否已经存在对应的结果：

  - 若缓存存在，就直接从缓存中读取数据并返回，无需再次执行方法体。

  - 若缓存不存在，才会执行方法体，并把返回的结果存入缓存，以便后续使用。
    这个注解通常应用在查询方法上。

**属性解释：**

| 属性 / 方法名 | 解释                                                         |
| ------------- | ------------------------------------------------------------ |
| value         | 缓存名，必填，指定缓存存储的命名空间，可将不同类型的缓存进行区分 |
| cacheNames    | 与 `value` 作用相同，二选一使用，用于指定缓存的名称          |
| key           | 可选属性，使用 SpEL 表达式自定义缓存的键，默认使用方法的参数作为键。例如 `#id` 表示使用方法的 `id` 参数作为键 |
| keyGenerator  | key 的生成器，与 `key` 二选一。可自定义实现 `KeyGenerator` 接口来生成复杂的键 |
| cacheManager  | 指定使用的缓存管理器的名称，即`@Bean("缓存管理器名称")`。在一个 Spring 应用中，可能会配置多个不同的 `CacheManager` 实例，每个实例对应不同的缓存实现（如 Caffeine、Redis 等）或者不同的缓存配置。通过 `cacheManager` 属性，可以明确告知 Spring 使用哪个 `CacheManager` 来处理当前方法的缓存操作。 |
| cacheResolver | 指定缓存解析器，用于解析缓存的相关信息，如缓存名称、键等     |
| condition     | 条件表达式，当表达式结果为 `true` 时才进行缓存操作           |
| unless        | 条件表达式，当表达式结果为 `true` 时，即使方法执行完成也不将结果存入缓存 |
| sync          | 布尔值，是否使用异步模式，默认 `false`。设置为 `true` 时，在缓存未命中时会异步执行方法并更新缓存 |

**注意事项：** 

- `cacheManager` 关注的是使用哪个具体的 `CacheManager` 实例来管理缓存，它决定了缓存的底层实现和配置；而 `value` 和 `cacheNames` 关注的是缓存数据存放在哪个命名空间下，用于区分不同类型的缓存数据。

- `cacheManager` 需要指定 `CacheManager` 实例在 Spring 容器中的名称；而 `value` 和 `cacheNames` 直接以字符串形式指定缓存的名称，二者功能相同，使用时二选一即可。

#### **简单使用案例**

##### 案例1：

```java
@Cacheable(value = "say", cacheManager = "caffeineCacheManager", key = "'p_' + #name")
@GetMapping(path = "say")
public String sayHello(String name) {
    return "hello " + name + "-->" + UUID.randomUUID();
}
```

注意：key为SpEL表达式，因此如果要写字符串时要用单引号括起来。如果name参数为高启强，缓存key的值为`p_高启强`

第一次请求：`http://localhost:8080/api/cache/say?name=高启强`

返回信息：`hello 高启强-->79c86d44-abbc-4892-b66f-f7786d2df0c0`

第二次请求：`http://localhost:8080/api/cache/say?name=高启强`

返回信息：`hello 高启强-->79c86d44-abbc-4892-b66f-f7786d2df0c0`

**两次返回信息相同，说明缓存生效了！**

##### 案例2：

```java
@Cacheable(value = "condition", cacheManager = "caffeineCacheManager", key = "#age", condition = "#age % 2 == 0")
@GetMapping(path = "condition")
public String setByCondition(Integer age) {
    return "condition: " + age + "-->" + UUID.randomUUID();
}
```

当age为偶数时才写缓存，否则不写。

请求奇数5：`http://localhost:8080/api/cache/condition?age=5`
页面打印返回信息：`condition: 5-->1b10c7aa-e7da-4d0a-976c-9a28056ae268`

再次请求奇数5：`http://localhost:8080/api/cache/condition?age=5`
页面打印返回信息：`condition: 5-->f8d2c8f7-33e8-42fa-aef3-08d3f97f7592`

**说明请求奇数时，不写缓存。**

请求奇数6：`http://localhost:8080/api/cache/condition?age=6`
页面打印返回信息：`condition: 6-->500aece9-9a6f-4f77-b3ff-78b317c9fbdf`

再次请求奇数6：`http://localhost:8080/api/cache/condition?age=6`
页面打印返回信息：`condition: 6-->500aece9-9a6f-4f77-b3ff-78b317c9fbdf`

**说明请求偶数时，写缓存。**

##### 案例三：

```java
@Cacheable(value = "unless", cacheManager = "caffeineCacheManager", key = "#age", unless = "#age % 2 == 0")
@GetMapping(path = "unless")
public String setByUnless(Integer age) {
    return "unless: " + age + "-->" + UUID.randomUUID();
}
```

与案例二相反，不满足条件时，才写入缓存。

### 3.3.3 @CachePut

#### 注解简介

**作用：**

- **该注解会让方法每次都执行，并将方法的返回值更新到缓存中，无论缓存中是否已存在该键对应的值，常用于更新缓存数据的场景，如新增数据时更新缓存。**

**注意事项：**

- **使用该注解的时候一定要有返回值，如果没有返回值就不会把对象缓存在注解中**

属性解释：

| 属性 / 方法名 | 解释                                                         |
| ------------- | ------------------------------------------------------------ |
| value         | 缓存名，必填，指定缓存存储的命名空间                         |
| cacheNames    | 与 `value` 作用相同，二选一，用于指定缓存的名称              |
| key           | 可选属性，使用 SpEL 表达式自定义缓存的键                     |
| keyGenerator  | 与 `key` 二选一，可自定义实现 `KeyGenerator` 接口来生成键    |
| cacheManager  | 指定使用的缓存管理器的名称，即`@Bean("缓存管理器名称")`。在一个 Spring 应用中，可能会配置多个不同的 `CacheManager` 实例，每个实例对应不同的缓存实现（如 Caffeine、Redis 等）或者不同的缓存配置。通过 `cacheManager` 属性，可以明确告知 Spring 使用哪个 `CacheManager` 来处理当前方法的缓存操作。 |
| cacheResolver | 指定缓存解析器，用于解析缓存相关信息                         |
| condition     | 条件表达式，结果为 `true` 时才进行缓存更新操作               |
| unless        | 条件表达式，结果为 `true` 时，不将方法结果存入缓存           |

#### 简单使用案例

```java
@CachePut(value = "say", cacheManager = "caffeineCacheManager", key = "'p_' + #name")
@GetMapping(path = "cachePut")
public String cachePut(String name) {
    return "hello " + name + "-->" + UUID.randomUUID();
}
```

测试流程：

第一次请求：`http://localhost:8080/api/cache/say?name=高启虎`
页面打印返回信息：`hello 高启虎-->3fcc2d80-ce32-46bb-8c51-de79cfd7e8fe`

第二次请求：`http://localhost:8080/api/cache/cachePut?name=高启虎`
页面打印返回信息：`hello 高启虎-->9e459753-f1e2-4372-a707-bc48b173c28c`

第三次请求：`http://localhost:8080/api/cache/say?name=高启虎`
页面打印返回信息：`hello 高启虎-->9e459753-f1e2-4372-a707-bc48b173c28c`

测试结果分析：第一次请求@Cacheable注解，第二次请求@CachePut注解更新了缓存内容，第三次再次请求@Cacheable注解返回的是第二次请求更新的内容。

### 3.3.4 @CacheEvict

#### 注解简介

- 该注解用于从缓存中移除指定的键对应的值，保证数据的一致性，常用于更新或删除数据后清理缓存。
- 属性解释：

| 属性 / 方法名    | 解释                                                         |
| ---------------- | ------------------------------------------------------------ |
| value            | 缓存名，必填，指定要清除的缓存所在的命名空间                 |
| cacheNames       | 与 `value` 作用相同，二选一，指定要清除的缓存名称            |
| key              | 可选属性，使用 SpEL 表达式指定要清除的缓存键                 |
| keyGenerator     | 与 `key` 二选一，确定要清除的缓存键的生成方式                |
| cacheManager     | 指定使用的缓存管理器的名称，即`@Bean("缓存管理器名称")`。在一个 Spring 应用中，可能会配置多个不同的 `CacheManager` 实例，每个实例对应不同的缓存实现（如 Caffeine、Redis 等）或者不同的缓存配置。通过 `cacheManager` 属性，可以明确告知 Spring 使用哪个 `CacheManager` 来处理当前方法的缓存操作。 |
| cacheResolver    | 指定缓存解析器，用于解析要清除的缓存相关信息                 |
| condition        | 条件表达式，结果为 `true` 时才执行缓存清除操作               |
| allEntries       | 布尔值，默认 `false`。设置为 `true` 时，会清除指定缓存命名空间下的所有缓存条目 |
| beforeInvocation | 布尔值，默认 `false`。设置为 `true` 时，在方法执行前就清除缓存；否则在方法执行后清除 |

#### 简单使用案例

```java
@CacheEvict(value = "say", cacheManager = "caffeineCacheManager", key = "'p_' + #name")
@GetMapping(path = "evict")
public String evict(String name) {
    return "hello " + name + "-->" + UUID.randomUUID();
}
```

测试流程：

第一次请求：`http://localhost:8080/api/cache/say?name=高启虎`
页面打印返回信息：`hello 高启虎-->52a64043-d3c4-45c6-9118-679a2e47dcef`

第二次请求：`http://localhost:8080/api/cache/evict?name=高启虎`
页面打印返回信息：`hello 高启虎-->3baadce2-1283-4362-8765-c7ce81f3fc25`

第三次请求：`http://localhost:8080/api/cache/say?name=高启虎`
页面打印返回信息：`hello 高启虎-->3baadce2-1283-4362-8765-c7ce81f3fc25`

测试结果分析：第一次请求@Cacheable注解，第二次请求@CacheEvict注解删除了缓存内容，第三次再次请求@Cacheable注解返回的内容也更新了。

### 3.3.5 @Caching

#### 注解简介

- 该注解用于在一个方法上组合多个不同类型的缓存注解（如 `@Cacheable`、`@CachePut`、`@CacheEvict`），以实现复杂的缓存操作逻辑。可从其源码看出：

![image-20240716171300420](https://i-blog.csdnimg.cn/blog_migrate/fe386e166244564ef76fa10798bfdf24.png)

#### 简单使用案例

```java
@Caching(cacheable = @Cacheable(value = "say", cacheManager = "caffeineCacheManager", key = "'p_' + #name"), evict = @CacheEvict(value = "condition", cacheManager = "caffeineCacheManager", key = "#age"))
@GetMapping(path = "caching")
public String caching(String name, Integer age) {
    return "caching " + name + "-->" + UUID.randomUUID();
}
```

- 上面代码就是组合操作：
  - @Cacheable：先读取缓存，缓存不存在再执行方法并写入缓存
  - @CacheEvict：删除缓存key为`#age`的缓存

### 3.3.6 @CacheConfig

- 在实际开发中，当项目里需要使用缓存的地方逐渐增多时，为了避免在每个缓存注解里重复指定 `value`（也就是缓存的命名空间），可以使用 `@CacheConfig` 注解。
- **使用方式**：将 `@CacheConfig(cacheNames = {"cacheName"})` 注解添加到类上，就能统一为该类里所有使用缓存注解的方法指定 `value` 值。在这种情况下，方法上的缓存注解就可以省略 `value` 属性。不过，如果在方法的缓存注解里依旧明确写上了 `value` 值，那么会以方法上的 `value` 值为准。
- **SpringBoot集成Caffeine方式1的代码中毛毛张就是这样写的**

# 4.可能的问题

## 4.1 缓存不刷新

- **一种可能是添加@CachePut注解的方法没有返回值，导致缓存没有保存在本地**
- 另一种可能是下游服务出现异常，把返回的异常值给缓存了，当下游服务异常时不应该缓存，如下图所示

![QQ_1740060114652](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1740060114652.png)

# 5.Caffeine缓存进阶

- 本章将详细介绍 Caffeine 的缓存添加策略、移除策略以及驱逐策略。

## 5.0 定义实体类

```java
public class User {
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User(name=" + name + ", age=" + age + ")";
    }
}
```

## 5.1 缓存添加策略

- Caffeine 提供了四种缓存添加策略：手动加载、自动加载、手动异步加载和自动异步加载。

### 5.1.1 手动加载

手动加载通过 `cache.get(key, k -> value)` 和 `cache.put(key, value)` 方法实现。

- **`cache.get(key, k -> value)`**：如果缓存中不存在指定的 `key`，则会通过计算生成值并写入缓存；如果存在，则直接返回缓存值。如果生成过程中抛出异常，则返回 `null`。

- **`cache.put(key, value)`**：直接写入或更新缓存中的值，已存在的值会被覆盖。

**示例代码：**

```java
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class ManualLoadingExample {
    public static void main(String[] args) {
        Cache<String, User> cache = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS) // 设置缓存过期时间
                .maximumSize(10) // 设置缓存最大容量
                .build();

        // 查找缓存，如果不存在则生成
        System.out.println(cache.getIfPresent("111")); // 输出：null
        cache.get("111", key -> new User("张三", 23)); // 缓存中没有 "111"，生成并写入缓存
        cache.put("222", new User("李四", 23)); // 添加或更新缓存

        // 输出结果
        System.out.println(cache.getIfPresent("111")); // 输出：User(name=张三, age=23)
        System.out.println(cache.getIfPresent("222")); // 输出：User(name=李四, age=23)
    }
}
```

**输出结果：**

```
null
User(name=张三, age=23)
User(name=李四, age=23)
```

### 5.1.2 自动加载

自动加载通过 `LoadingCache` 实现，它在初始化时通过 `CacheLoader` 配置生成策略。

**示例代码：**

```java
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

public class AutoLoadingExample {
    public static void main(String[] args) {
        LoadingCache<String, User> cache = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS) // 设置缓存过期时间
                .maximumSize(10) // 设置缓存最大容量
                .build(key -> new User("张三", 23)); // 自动加载逻辑

        // 获取缓存值，如果不存在则自动生成
        System.out.println(cache.get("111")); // 输出：User(name=张三, age=23)
    }
}
```

**输出结果：**

```
User(name=张三, age=23)
```

### 5.1.3 手动异步加载

手动异步加载通过 `AsyncCache` 实现，支持异步生成缓存值并返回 `CompletableFuture`。

**示例代码：**

```java
import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ManualAsyncLoadingExample {
    public static void main(String[] args) throws Exception {
        AsyncCache<String, User> cache = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS) // 设置缓存过期时间
                .maximumSize(10) // 设置缓存最大容量
                .buildAsync();

        // 查找缓存，如果不存在则生成
        cache.get("111", key -> new User("张三", 23)); // 缓存中没有 "111"，生成并写入缓存
        CompletableFuture<User> userFuture = cache.get("111"); // 异步获取缓存值

        // 添加或更新缓存
        CompletableFuture<User> newUserFuture = new CompletableFuture<>();
        newUserFuture.complete(new User("李四", 23));
        cache.put("222", newUserFuture);

        // 输出结果
        System.out.println(userFuture.get()); // 输出：User(name=张三, age=23)
        System.out.println(cache.get("222").get()); // 输出：User(name=李四, age=23)
    }
}
```

**输出结果：**

```
User(name=张三, age=23)
User(name=李四, age=23)
```

### 5.1.4 自动异步加载

自动异步加载通过 `AsyncLoadingCache` 实现，支持异步加载并自动处理缓存生成。

**示例代码：**

```java
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AutoAsyncLoadingExample {
    public static void main(String[] args) throws Exception {
        AsyncLoadingCache<String, User> cache = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS) // 设置缓存过期时间
                .maximumSize(10) // 设置缓存最大容量
                .buildAsync(key -> new User("张三", 23)); // 自动加载逻辑

        // 异步获取缓存值，如果不存在则自动生成
        CompletableFuture<User> userFuture = cache.get("111");
        System.out.println(userFuture.get()); // 输出：User(name=张三, age=23)
    }
}
```

**输出结果：**

```
User(name=张三, age=23)
```

## 5.2 缓存移除策略

- 缓存移除分为显式移除和监听器移除。

### 5.2.1 显式移除

显式移除支持单个移除、批量移除和全部移除。

- **单个移除**：`cache.invalidate(key)`
- **批量移除**：`cache.invalidateAll(keys)`
- **全部移除**：`cache.invalidateAll()`

**示例代码：**

```java
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class ExplicitRemovalExample {
    public static void main(String[] args) {
        Cache<String, User> cache = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS) // 设置缓存过期时间
                .maximumSize(10) // 设置缓存最大容量
                .build();

        // 添加缓存
        cache.get("111", key -> new User("张三", 23));
        System.out.println(cache.getIfPresent("111")); // 输出：User(name=张三, age=23)

        // 单个移除
        cache.invalidate("111");
        System.out.println(cache.getIfPresent("111")); // 输出：null

        // 批量移除
        cache.put("222", new User("李四", 23));
        cache.put("333", new User("王五", 23));
        cache.invalidateAll(new String[]{"222", "333"});
        System.out.println(cache.getIfPresent("222")); // 输出：null
        System.out.println(cache.getIfPresent("333")); // 输出：null

        // 全部移除
        cache.put("444", new User("赵六", 23));
        cache.invalidateAll();
        System.out.println(cache.getIfPresent("444")); // 输出：null
    }
}
```

**输出结果：**

```
User(name=张三, age=23)
null
null
null
null
```

### 5.2.2 移除监听器

移除监听器通过 `Caffeine.removalListener(RemovalListener)` 定义，用于在元素被移除时执行操作。

**示例代码：**

```java
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import java.util.concurrent.TimeUnit;

public class RemovalListenerExample {
    public static void main(String[] args) {
        Cache<String, User> cache = Caffeine.newBuilder()
                .expireAfterWrite(500, TimeUnit.MILLISECONDS) // 设置缓存过期时间
                .maximumSize(10) // 设置缓存最大容量
                .removalListener((key, value, cause) -> {
                    System.out.println("Key: " + key + " removed due to " + cause);
                })
                .build();

        // 添加缓存
        for (int i = 0; i < 15; i++) {
            cache.get(i + "", key -> new User(key + "", 23));
        }

        // 手动移除
        cache.invalidate("5");
        cache.invalidate("6");
    }
}
```

**输出结果：**

```
Key: 0 removed due to SIZE
Key: 1 removed due to SIZE
Key: 2 removed due to SIZE
Key: 3 removed due to SIZE
Key: 4 removed due to SIZE
Key: 5 removed due to EXPLICIT
Key: 6 removed due to EXPLICIT
```

## 5.3 驱逐策略

- Caffeine 提供了三种驱逐策略：基于容量、基于时间和基于引用。

### 5.3.1 基于容量

基于容量的驱逐策略分为两种：

- **基于个数**：通过 `Caffeine.maximumSize(long)` 设置缓存的最大个数。
- **基于权重**：通过 `Caffeine.weigher(Weigher)` 和 `Caffeine.maximumWeight(long)` 设置每个元素的权重和最大权重。

**示例代码（基于个数）：**

```java
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import java.util.concurrent.TimeUnit;

public class SizeBasedEvictionExample {
    public static void main(String[] args) {
        Cache<String, User> cache = Caffeine.newBuilder()
                .maximumSize(10) // 设置缓存最大容量
                .removalListener((key, value, cause) -> {
                    System.out.println("Key: " + key + " removed due to " + cause);
                })
                .build();

        // 添加缓存
        for (int i = 0; i < 15; i++) {
            cache.get(i + "", key -> new User(key + "", 23));
        }
    }
}
```

**输出结果：**

```
Key: 0 removed due to SIZE
Key: 1 removed due to SIZE
Key: 2 removed due to SIZE
Key: 3 removed due to SIZE
Key: 4 removed due to SIZE
```

**示例代码（基于权重）：**

```java
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.Weigher;

import java.util.concurrent.TimeUnit;

public class WeightBasedEvictionExample {
    public static void main(String[] args) {
        Cache<String, User> cache = Caffeine.newBuilder()
                .maximumWeight(100) // 设置最大权重
                .weigher((key, value) -> value.getAge()) // 设置权重计算逻辑
                .removalListener((key, value, cause) -> {
                    System.out.println("Key: " + key + " removed due to " + cause);
                })
                .build();

        // 添加缓存
        for (int i = 0; i < 6; i++) {
            cache.get(i + "", key -> new User(key + "", 23));
        }
    }
}
```

**输出结果：**

```
Key: 0 removed due to SIZE
Key: 1 removed due to SIZE
```

### 5.3.2 基于时间

基于时间的驱逐策略包括：

- **`expireAfterAccess(long, TimeUnit)`**：基于访问时间的驱逐。
- **`expireAfterWrite(long, TimeUnit)`**：基于写入时间的驱逐。
- **`expireAfter(Expiry)`**：自定义过期策略。

**示例代码（`expireAfterAccess`）：**

```java
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import java.util.concurrent.TimeUnit;

public class AccessBasedEvictionExample {
    public static void main(String[] args) throws InterruptedException {
        Cache<String, User> cache = Caffeine.newBuilder()
                .expireAfterAccess(2, TimeUnit.SECONDS) // 设置访问过期时间
                .removalListener((key, value, cause) -> {
                    System.out.println("Key: " + key + " removed due to " + cause);
                })
                .build();

        // 添加缓存
        for (int i = 0; i < 6; i++) {
            cache.get(i + "", key -> new User(key + "", 23));
        }

        // 等待缓存过期
        Thread.sleep(5000);
        System.out.println(cache.getIfPresent("1")); // 输出：null
    }
}
```

**输出结果：**

复制

```
Key: 0 removed due to EXPIRED
Key: 1 removed due to EXPIRED
Key: 2 removed due to EXPIRED
Key: 3 removed due to EXPIRED
Key: 4 removed due to EXPIRED
Key: 5 removed due to EXPIRED
null
```

### 5.3.3 基于引用

Caffeine 支持基于引用的驱逐策略，包括弱引用和软引用。

- **弱引用**：通过 `Caffeine.weakKeys()` 或 `Caffeine.weakValues()` 实现，允许 GC 在适当的时候回收缓存元素。
- **软引用**：通过 `Caffeine.softValues()` 实现，允许 GC 在内存不足时回收缓存元素。

**示例代码（弱引用）：**

```java
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class ReferenceBasedEvictionExample {
    public static void main(String[] args) {
        // 弱引用键和值
        Cache<String, User> cache = Caffeine.newBuilder()
                .weakKeys()
                .weakValues()
                .build(key -> new User(key, 23));

        // 添加缓存
        cache.get("111", key -> new User(key, 23));

        // 当 key 和 value 都没有强引用时，缓存会被自动回收
        System.gc(); // 触发垃圾回收
    }
}
```

# 参考文献

- <https://blog.csdn.net/qq_45607784/article/details/135409207>
- <https://zhuanlan.zhihu.com/p/109226599>
- <https://www.cnblogs.com/dw3306/p/15881537.html>
- <https://blog.csdn.net/pig_boss/article/details/140472989>
- <https://blog.csdn.net/qq_45825178/article/details/137643858>
- [Caffeine缓存不刷新问题_caffeine不生效-CSDN博客](https://blog.csdn.net/u014800975/article/details/124163767)
