> 【SpringBoot教程】万字长文详解SpringBoot整合MybatisPlus

[toc]

# 1.简介

- 今天毛毛张要分享的是SpringBoot整合Mybatis的教程，毛毛张将从前后端分离的方式、通过一个完整的任务来教大家整合的整个过程
- 版本：
  - 后端：
    - SpringBoot：2.7.6
    - JDK：17
    - Mybatis-Plus：3.5.2
  - 前端
    - vite：6.0.5
    - vue：3.5.13
    - vue-router：4.5.0
    - pinia：2.3.0
    - axios：1.7.9
    - element-plus：2.9.1

## 1.1 MybatisPlus简介

- **MyBatis-Plus是一个MyBatis的增强工具，在 MyBatis 的基础上只做增强不做改变，为简化开发、提高效率而生。它继承了 MyBatis 的所有特性，并且加入了强大的功能，例如自动填充、逻辑删除、乐观锁、性能分析等。 MybatisPlus可以节省大量时间，所有的CRUD代码都可以自动化完成**

![QQ_1735909371149](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735909371149.png)

- **特性：**
  - 无侵入：只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑
  - 损耗小：启动即会自动注入基本 CURD，性能基本无损耗，直接面向对象操作
  - 强大的 CRUD 操作：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求
  - 支持 Lambda 形式调用：通过 Lambda 表达式，方便的编写各类查询条件，无需再担心字段写错
  - 支持主键自动生成：支持多达 4 种主键策略（内含分布式唯一 ID 生成器 - Sequence），可自由配置，完美解决主键问题
  - 支持 ActiveRecord 模式：支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可进行强大的 CRUD 操作
  - 支持自定义全局通用操作：支持全局通用方法注入（ Write once, use anywhere ）
  - 内置代码生成器：采用代码或者 Maven 插件可快速生成 Mapper 、 Model 、 Service 、 Controller 层代码，支持模板引擎，更有超多自定义配置等您来使用
  - 内置分页插件：基于 MyBatis 物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于普通 List 查询
  - 分页插件支持多种数据库：支持 MySQL、MariaDB、Oracle、DB2、H2、HSQL、SQLite、Postgre、SQLServer 等多种数据库
  - 内置性能分析插件：可输出 SQL 语句以及其执行时间，建议开发测试时启用该功能，能快速揪出慢查询
  - 内置全局拦截插件：提供全表 delete 、 update 操作智能分析阻断，也可自定义拦截规则，预防误操作
- 支持数据库：

  -   MySQL，Oracle，DB2，H2，HSQL，SQLite，PostgreSQL，SQLServer，Phoenix，Gauss ，ClickHouse，Sybase，OceanBase，Firebird，Cubrid，Goldilocks，csiidb，informix，TDengine，redshift

  -   达梦数据库，虚谷数据库，人大金仓数据库，南大通用(华库)数据库，南大通用数据库，神通数据库，瀚高数据库，优炫数据库


- 总结：
  - 自动生成单表的CRUD功能
  - 提供丰富的条件拼接方式
  - 全自动ORM类型持久层框架

## 1.2 SpringBoot整合MybaitPlus入门案例

- 在本小节毛毛张将通过一个快速的入门案例，教大家如何快速使用Mybatis-Plus
- **任务需求：通过SpringBoot整合Mybatis-Plus，实现查询指定文章、删除指定文章、批量保存或更新文章**

### 1.2.1 项目搭建

- 如何快速创建一个Maven项目注意的细节可以参看毛毛张的上一篇文章：[【SpringBoot教程】IDEA快速搭建正确的SpringBoot版本和Java版本的项目](https://blog.csdn.net/weixin_48235955/article/details/144807998)
- 毛毛张在这里选择的`SpringBoot`版本为`2.7.6`，`JDK`选择的是`JDK17`，下面是毛毛张搭建好的整个项目的完整文件夹：

### 1.2.2 准备数据库

- 创建数据库`springboot`，并在该数据库下创建`t_article`表：

  ```mysql
  -- 创建文章信息表 t_article
  CREATE TABLE t_article (
      id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
      category VARCHAR(50) NOT NULL COMMENT '文章分类',
      tags VARCHAR(255) DEFAULT NULL COMMENT '文章标签',
      article_cover VARCHAR(255) DEFAULT NULL COMMENT '文章缩略图',
      article_title VARCHAR(255) NOT NULL COMMENT '标题',
      article_abstract TEXT DEFAULT NULL COMMENT '摘要',
      article_content TEXT NOT NULL COMMENT '内容',
      is_top TINYINT(1) DEFAULT 0 COMMENT '是否置顶，0-否，1-是',
      status TINYINT(1) DEFAULT 0 COMMENT '状态，0-草稿，1-已发布',
      is_delete TINYINT(1) DEFAULT 0 COMMENT '是否删除，0-否，1-是',
      create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发表时间',
      update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
  ) COMMENT '文章信息表';
  
  -- 插入测试数据
  INSERT INTO t_article (category, tags, article_cover, article_title, article_abstract, article_content, is_top, status, is_delete, create_time, update_time)
  VALUES
  -- 测试数据 1
  ('技术', 'Java,MySQL', 'https://example.com/cover1.jpg', 'MySQL创建与优化', '本文介绍了MySQL的创建与优化技巧。', 'MySQL 是一种关系型数据库管理系统...', 1, 1, 0, NOW(), NOW()),
  -- 测试数据 2
  ('生活', '旅行,美食', 'https://example.com/cover2.jpg', '一场难忘的旅行', '这是一场难忘的旅行，记录了...', '旅行是一种治愈心灵的方式...', 0, 1, 0, NOW(), NOW()),
  -- 测试数据 3
  ('技术', 'Python,数据分析', 'https://example.com/cover3.jpg', 'Python数据分析入门', 'Python 是数据分析领域的利器...', '本文详细介绍了如何使用 Python 进行数据分析...', 1, 0, 0, NOW(), NOW()),
  -- 测试数据 4
  ('教育', '学习技巧,考试', 'https://example.com/cover4.jpg', '如何高效学习？', '本文提供了一些高效学习的技巧...', '学习的方式多种多样，本文提供了...', 0, 1, 0, NOW(), NOW()),
  -- 测试数据 5
  ('娱乐', '电影,推荐', 'https://example.com/cover5.jpg', '2025年必看的电影推荐', '盘点2025年不可错过的电影...', '如果你是一个电影爱好者，这份推荐一定不能错过...', 0, 1, 0, NOW(), NOW()),
  -- 测试数据 6
  ('技术', 'Kubernetes,DevOps', 'https://example.com/cover6.jpg', 'Kubernetes 集群入门', '介绍 Kubernetes 的基础概念和使用...', 'Kubernetes 是一个开源的容器编排平台...', 1, 1, 0, NOW(), NOW());
  ```

- 创建完毕的数据库如下图所示（仅部分）：

![QQ_1735911602273](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735911602273.png)

### 1.2.3 导入依赖

- 核心依赖：

  - 下面导入的`Mybatis-Plus`依赖和数据库依赖，因此`Mybatis-Plus`是为操作数据库而生，所有我们还要导入数据库相关的依赖。**注意：由于`Mybatis-Plus`有已经整合了`JDBC`相关的依赖，见下图。**

  ![QQ_1736349519337](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736349519337.png)

  ```xml
  <!-- MySQL 连接驱动 -->
  <!-- 由于要操作数据库，所以把数据库的依赖也要导入 -->
  <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <scope>runtime</scope> <!-- 运行时依赖 -->
  </dependency>
  
  <!-- MyBatis-Plus Starter，用于集成 MyBatis-Plus Mybatis-Plus依赖以及包含了JDBC -->
  <dependency>
      <groupId>com.baomidou</groupId>
      <artifactId>mybatis-plus-boot-starter</artifactId>
      <version>3.5.2</version>
  </dependency>
  ```

- 其它依赖：为了简化实体类的开发，导入了`lombok`依赖；为了本地开发和测试的方便，导入了`spring-boot-starter-test`和`spring-boot-devtools`依赖；由于项目毛毛张还做了前端展示，所以导入了前后端分离的依赖`spring-boot-starter-web`

- 所以完整的`pom.xml`文件为：

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  
      <!-- Maven 模型版本 -->
      <modelVersion>4.0.0</modelVersion>
  
      <!-- 项目信息 -->
      <groupId>com.zzx</groupId> <!-- 项目的组织ID，通常为公司域名倒置 -->
      <artifactId>springboot-mybatis-plus-demo</artifactId> <!-- 项目名称 -->
      <version>0.0.1-SNAPSHOT</version> <!-- 项目版本号 -->
      <name>springboot-mybatis-plus-demo</name> <!-- 项目名称显示 -->
      <description>springboot-mybatis-plus-demo</description> <!-- 项目描述 -->
  
      <!-- 父项目 -->
      <parent>
          <groupId>org.springframework.boot</groupId> <!-- Spring Boot 的父项目组ID -->
          <artifactId>spring-boot-starter-parent</artifactId> <!-- Spring Boot 的父项目artifactID -->
          <version>2.7.6</version> <!-- 使用的 Spring Boot 版本 -->
          <relativePath/> <!-- 从中央仓库中查找父项目 -->
      </parent>
  
      <!-- Maven 属性配置 -->
      <properties>
          <java.version>1.8</java.version> <!-- 配置 Java 版本为 1.8 -->
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding> <!-- 编码格式 -->
          <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding> <!-- 报表输出编码 -->
          <spring-boot.version>2.7.6</spring-boot.version> <!-- Spring Boot 版本 -->
          <mybatis-plus.version>3.5.2</mybatis-plus.version> <!-- MyBatis-Plus 版本 -->
      </properties>
  
      <!-- 项目依赖 -->
      <dependencies>
          <!-- Spring Boot Web Starter，用于构建 Web 应用 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
  
          <!-- MySQL 连接驱动 -->
          <!-- 由于要操作数据库，所以把数据库的依赖也要导入 -->
          <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
              <scope>runtime</scope> <!-- 运行时依赖 -->
          </dependency>
  
          <!-- MyBatis-Plus Starter，用于集成 MyBatis-Plus -->
          <!-- 不同于整合Mybatis依赖，Mybatis-Plus依赖以及包含了JDBC -->
          <dependency>
              <groupId>com.baomidou</groupId>
              <artifactId>mybatis-plus-boot-starter</artifactId>
              <version>3.5.2</version>
          </dependency>
  
          <!-- Spring Boot DevTools，支持热部署 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-devtools</artifactId>
              <scope>runtime</scope>
              <optional>true</optional>
          </dependency>
  
          <!-- Lombok，用于简化 Java 实体类开发 -->
          <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <optional>true</optional>
          </dependency>
  
          <!-- Spring Boot Test Starter，用于测试 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-test</artifactId>
              <scope>test</scope>
          </dependency>
      </dependencies>
  
      <!-- 项目构建配置 -->
      <build>
          <plugins>
              <!-- Maven 编译插件 -->
              <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-compiler-plugin</artifactId>
                  <version>3.8.1</version>
                  <configuration>
                      <source>1.8</source> <!-- Java 源码版本 -->
                      <target>1.8</target> <!-- Java 目标版本 -->
                      <encoding>UTF-8</encoding> <!-- 编码格式 -->
                  </configuration>
              </plugin>
  
              <!-- Spring Boot 打包插件 -->
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
                  <version>${spring-boot.version}</version> <!-- 使用的 Spring Boot 版本 -->
                  <configuration>
                      <mainClass>com.zzx.SpringbootMybatisPlusDemoApplication</mainClass> <!-- 主类入口 -->
                      <skip>true</skip> <!-- 是否跳过重新打包 -->
                  </configuration>
                  <executions>
                      <execution>
                          <id>repackage</id> <!-- 重新打包的目标ID -->
                          <goals>
                              <goal>repackage</goal> <!-- 打包目标 -->
                          </goals>
                      </execution>
                  </executions>
              </plugin>
          </plugins>
      </build>
  
  </project>
  
  ```

### 1.2.4 配置文件

```yaml
server:
  port: 8080

spring:
  # HikariCP 连接池配置
  # 参考链接：https://www.cnblogs.com/goloving/p/14884802.html
  datasource:
    url: jdbc:mysql://localhost:3306/springboot?useSSL=false&autoReconnect=true&characterEncoding=utf8
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: abc123
    # 指定数据源类型为 HikariDataSource
    type: com.zaxxer.hikari.HikariDataSource
    # Hikari 连接池的详细配置
    hikari:
      # 连接池名称
      pool-name: HikariCP
      # 最小空闲连接数
      minimum-idle: 5
      # 空闲连接超时时间（毫秒）
      idle-timeout: 600000
      # 连接池的最大大小
      maximum-pool-size: 10
      # 是否自动提交事务
      auto-commit: true
      # 连接的最大生命周期（毫秒）
      max-lifetime: 1800000
      # 连接超时时间（毫秒）
      connection-timeout: 30000
      # 测试连接的 SQL 语句
      connection-test-query: SELECT 1

logging:
  level:
    org.springframework.jdbc.core.JdbcTemplate: DEBUG

mybatis-plus:
  # xml扫描，多个目录用逗号或者分号分隔（告诉 Mapper 所对应的 XML 文件位置）
  # 如果不设置，MyBatis-Plus 会自动扫描 resources/mapper/*.xml
  mapper-locations: classpath:mapper/*.xml
  configuration:
  	# 如果项目中存在嵌套对象映射需求（例如一对多、多对一映射），建议设置为 full
    auto-mapping-behavior: full
    # 开启驼峰映射
    map-underscore-to-camel-case: true 
    # 如果查询结果中包含空值的列，则 MyBatis 在映射的时候，不会映射这个字段
    call-setters-on-nulls: true
    # 这个配置会将执行的sql打印出来，在开发或测试的时候可以用
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 1.2.5 四件套

- 实体类`Article`：

  ```java
  package com.zzx.entity;
  
  import com.baomidou.mybatisplus.annotation.*;
  import lombok.AllArgsConstructor;
  import lombok.Builder;
  import lombok.Data;
  import lombok.NoArgsConstructor;
  
  import java.time.LocalDateTime;
  
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @TableName("t_article") //由于数据的表明带有前缀t_,我们可以通过`Mybatis-Plus`的注解来进行映射
  public class Article {
  
      @TableId(value = "id", type = IdType.AUTO)
      private Long id; // 主键
  
      private String category; // 文章分类
  
      private String tags; // 文章标签
  
      private String articleCover; // 文章缩略图
  
      private String articleTitle; // 标题
  
      private String articleAbstract; // 摘要
  
      private String articleContent; // 内容
  
      private Integer isTop; // 是否置顶：0-否，1-是
  
      private Integer status; // 状态：0-草稿，1-已发布
  
      private Integer isDelete; // 是否删除：0-否，1-是
  
      @TableField(fill = FieldFill.INSERT)
      private LocalDateTime createTime; // 创建时间
  
      @TableField(fill = FieldFill.UPDATE)
      private LocalDateTime updateTime; // 更新时间
  
  }
  

- `Mapper`接口：继承`Mybatis-Plus`提供的`BaseMapper`接口，自带CRUD方法！可以在`Service`层直接调用

  ```java
  package com.zzx.mapper;
  
  import com.zzx.entity.Article;
  import com.baomidou.mybatisplus.core.mapper.BaseMapper;
  
  /**
  * @description 针对表【t_article(文章信息表)】的数据库操作Mapper
  */
  public interface ArticleMapper extends BaseMapper<Article> {
  
  }
  ```

- `Service`接口：继承`Mybatis-Plus`提供的`IService`接口，提供了封装的CRUD方法，可以在`Controller`层直接调用

  ```java
  package com.zzx.service;
  
  import com.zzx.entity.Article;
  import com.baomidou.mybatisplus.extension.service.IService;
  
  import java.util.List;
  
  /**
  * @author flyvideo
  * @description 针对表【t_article(文章信息表)】的数据库操作Service
  * @createDate 2025-01-03 20:54:38
  */
  public interface ArticleService extends IService<Article> {
  
  //    List<Article> getArticlesByStatus(Integer status);
  }
  ```

- `ServiceImpl`实现类：

  ```java
  package com.zzx.service.impl;
  
  import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
  import com.zzx.entity.Article;
  import com.zzx.service.ArticleService;
  import com.zzx.mapper.ArticleMapper;
  import org.springframework.stereotype.Service;
  
  import java.util.List;
  
  /**
  * @author flyvideo
  * @description 针对表【t_article(文章信息表)】的数据库操作Service实现
  * @createDate 2025-01-03 20:54:38
  */
  @Service
  public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService{
  
  }
  ```

### 1.2.6 返回类以及相关枚举类型

- 返回类对象`ResultVo`：

  ```java
  package com.zzx.model.vo;
  
  import com.zzx.enums.StatusCodeEnum;
  import lombok.AllArgsConstructor;
  import lombok.Builder;
  import lombok.Data;
  import lombok.NoArgsConstructor;
  
  import static com.zzx.enums.StatusCodeEnum.*;
  
  
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @SuppressWarnings("all")
  public class ResultVO<T> {
  
      private Boolean flag;       //请求是否成功的标志
  
      private Integer code;       //响应状态码
  
      private String message;     //响应消息
  
      private T data;             //响应数据
  
      public static <T> ResultVO<T> ok() {
          return resultVO(true, SUCCESS.getCode(), SUCCESS.getDesc(), null);
      }
  
      public static <T> ResultVO<T> ok(T data) {
          return resultVO(true, SUCCESS.getCode(), SUCCESS.getDesc(), data);
      }
  
      public static <T> ResultVO<T> ok(T data, String message) {
          return resultVO(true, SUCCESS.getCode(), message, data);
      }
  
      public static <T> ResultVO<T> fail() {
          return resultVO(false, FAIL.getCode(), FAIL.getDesc(), null);
      }
  
      public static <T> ResultVO<T> fail(StatusCodeEnum statusCodeEnum) {
          return resultVO(false, statusCodeEnum.getCode(), statusCodeEnum.getDesc(), null);
      }
  
      public static <T> ResultVO<T> fail(String message) {
          return resultVO(false, message);
      }
  
      public static <T> ResultVO<T> fail(T data) {
          return resultVO(false, FAIL.getCode(), FAIL.getDesc(), data);
      }
  
      public static <T> ResultVO<T> fail(T data, String message) {
          return resultVO(false, FAIL.getCode(), message, data);
      }
  
      public static <T> ResultVO<T> fail(Integer code, String message) {
          return resultVO(false, code, message, null);
      }
  
      private static <T> ResultVO<T> resultVO(Boolean flag, String message) {
          return ResultVO.<T>builder()
                  .flag(flag)
                  .code(flag ? SUCCESS.getCode() : FAIL.getCode())
                  .message(message).build();
      }
  
      private static <T> ResultVO<T> resultVO(Boolean flag, Integer code, String message, T data) {
          return ResultVO.<T>builder()
                  .flag(flag)
                  .code(code)
                  .message(message)
                  .data(data).build();
      }
  
  }
  ```

- 响应状态枚举类`StatusCodeEnum`：

  ```java
  package com.zzx.enums;
  
  import lombok.AllArgsConstructor;
  import lombok.Getter;
  
  @Getter
  @AllArgsConstructor
  public enum StatusCodeEnum {
  
      SUCCESS(20000, "操作成功"),
  
      FAIL(51000, "操作失败"),;
  
      private final Integer code;
  
      private final String desc;
  
  }
  ```

### 1.2.7 Controller层

```java
package com.zzx.controller;

import com.zzx.entity.Article;
import com.zzx.model.vo.ResultVO;
import com.zzx.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 根据ID获取文章信息
     */
    @GetMapping("/{id}")
    public ResultVO<Article> getArticleById(@PathVariable("id") Long id) {
        Article article = articleService.getById(id);
        return article != null ? ResultVO.ok(article) : ResultVO.fail("文章不存在");
    }

    /**
     * 获取所有文章信息
     */
    @GetMapping("/list")
    public ResultVO<List<Article>> listAllArticles() {
        List<Article> articles = articleService.list();
        return ResultVO.ok(articles);
    }

    /**
     * 删除指定文章
     */
    @DeleteMapping("/{id}")
    public ResultVO deleteArticleById(@PathVariable("id") Long id) {
        boolean result = articleService.removeById(id);
        return result ? ResultVO.ok("文章删除成功") : ResultVO.fail("文章删除失败");
    }

    /**
     * 批量保存或更新文章
     */
    @PostMapping("/saveOrUpdateBatch")
    public ResultVO saveOrUpdateBatch(@RequestBody List<Article> articles) {
        boolean result = articleService.saveOrUpdateBatch(articles);
        return result ? ResultVO.ok("文章批量保存或更新成功") : ResultVO.fail("文章批量保存或更新失败");
    }
}
```

### 1.2.8 启动类

```java
package com.zzx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zzx.mapper")
public class SpringbootMybatisPlusDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootMybatisPlusDemoApplication.class, args);
    }

}
```

### 1.2.9 使用PostMan测试接口

- 根据`id`查询文章：

![QQ_1735913823820](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735913823820.png)

> **如果不是一个完整的前后端分离的项目，能正常启动并测试对了，就相当于`Mybatis-Plus`整合成功了**
>
> **但是毛毛张想通过一个完整的前后端分离的任务整合`Mybatis-Plus`，还需要进行下面的操作**

### 1.2.10 跨域资源共享

- 由于毛毛张要做的是一个前后端分离的任务，所以毛毛张在这里在拦截器接口里面实现跨域资源共享，并添加在拦截器，主要包含以下两个类：

- 跨域资源共享类`CorsInterceptor`：

  ```java
  package com.zzx.user.interceptor;
  
  import org.springframework.web.servlet.HandlerInterceptor;
  
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  
  public class CorsInterceptor implements HandlerInterceptor {
  
      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
          response.setHeader("Access-Control-Allow-Origin", "*");  // 允许所有来源
          response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
          response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
          response.setHeader("Access-Control-Allow-Credentials", "true");
  
          // 处理OPTIONS请求
          if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
              response.setStatus(HttpServletResponse.SC_OK);
              return false;  // 返回false，表示不再执行后续的Controller方法
          }
  
          return true;  // 继续执行其他拦截器或Controller方法
      }
  }
  
  ```

- 配置类`WebConfig`：

  ```java
  package com.zzx.user.config;
  
  import com.zzx.user.interceptor.CorsInterceptor;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
  import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
  
  @Configuration
  public class WebConfig implements WebMvcConfigurer {
  
      @Override
      public void addInterceptors(InterceptorRegistry registry) {
          // 注册 CORS 拦截器
          registry.addInterceptor(new CorsInterceptor())
                  .addPathPatterns("/**")  // 拦截所有路径
                  .excludePathPatterns("/login", "/error");  // 排除登录和错误页面
      }
  }
  ```

## 1.3 总结

- 通过以上几个简单的步骤，我们就实现了对`t_article`表的`CRUD`功能，甚至连`XML`文件都不用编写！
- 从以上步骤中，我们可以看到集成`MyBatis-Plus`非常的简单，只需要引入`starter`工程，并配置`mapper`扫描路径即可，采用`Mybatis-Plus`已经封装好的`CRUD`方法，这个让我们少些很多的增删改查语句，能帮助我们快速上手`SpringBoot`项目
- 相关的前后端毛毛张已经上传至`Github`：<https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mybatis-plus>
- 大家入门之后，想要学习更多教程可以参看**官网教程：<https://baomidou.com/>**
- 下面毛毛张将继续带着大家深入学习以下`Mybatis-Plus`的核心知识点

# 2 MyBatis-Plus核心功能

## 2.1 基于Mapper接口CRUD

- 通用`CRUD`封装`BaseMapper`接口， `Mybatis-Plus` 启动时自动解析实体表关系映射转换为 `Mybatis` 内部对象注入容器! 内部包含常见的单表操作！
- 下面将简单具体介绍一部分`BaseMapper`接口以及封装好的常见的`CRUD`方法

### 2.1.1 Insert方法

```java
// 插入一条记录
// T 就是要插入的实体对象
// 默认主键生成策略为雪花算法（后面讲解）
int insert(T entity);
```

| 类型 | 参数名 | 描述     |
| ---- | ------ | -------- |
| T    | entity | 实体对象 |

### 2.1.2 Delete方法

```java
// 根据 entity 条件，删除记录
int delete(@Param(Constants.WRAPPER) Wrapper<T> wrapper);

// 删除（根据ID 批量删除）
int deleteBatchIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> idList);

// 根据 ID 删除
int deleteById(Serializable id);

// 根据 columnMap 条件，删除记录
int deleteByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap);
```

| 类型                                | 参数名    | 描述                                 |
| ----------------------------------- | --------- | ------------------------------------ |
| Wrapper\<T>                         | wrapper   | 实体对象封装操作类（可以为 null）    |
| Collection\<? extends Serializable> | idList    | 主键 ID 列表(不能为 null 以及 empty) |
| Serializable                        | id        | 主键 ID                              |
| Map\<String, Object>                | columnMap | 表字段 map 对象                      |

### 2.1.3 Update方法

```java
// 根据 whereWrapper 条件，更新记录
int update(@Param(Constants.ENTITY) T updateEntity, 
            @Param(Constants.WRAPPER) Wrapper<T> whereWrapper);

// 根据 ID 修改  主键属性必须有值
int updateById(@Param(Constants.ENTITY) T entity);
```

| 类型        | 参数名        | 描述                                                         |
| ----------- | ------------- | ------------------------------------------------------------ |
| T           | entity        | 实体对象 (set 条件值,可为 null)                              |
| Wrapper\<T> | updateWrapper | 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句） |

### 2.1.4 Select方法

```java
// 根据 ID 查询
T selectById(Serializable id);

// 根据 entity 条件，查询一条记录
T selectOne(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

// 查询（根据ID 批量查询）
List<T> selectBatchIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> idList);

// 根据 entity 条件，查询全部记录
List<T> selectList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

// 查询（根据 columnMap 条件）
List<T> selectByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap);

// 根据 Wrapper 条件，查询全部记录
List<Map<String, Object>> selectMaps(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

// 根据 Wrapper 条件，查询全部记录。注意： 只返回第一个字段的值
List<Object> selectObjs(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

// 根据 entity 条件，查询全部记录（并翻页）
IPage<T> selectPage(IPage<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

// 根据 Wrapper 条件，查询全部记录（并翻页）
IPage<Map<String, Object>> selectMapsPage(IPage<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

// 根据 Wrapper 条件，查询总记录数
Integer selectCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
```

- 参数说明

| 类型                                | 参数名       | 描述                                     |
| ----------------------------------- | ------------ | ---------------------------------------- |
| Serializable                        | id           | 主键 ID                                  |
| Wrapper\<T>                         | queryWrapper | 实体对象封装操作类（可以为 null）        |
| Collection\<? extends Serializable> | idList       | 主键 ID 列表(不能为 null 以及 empty)     |
| Map\<String, Object>                | columnMap    | 表字段 map 对象                          |
| IPage\<T>                           | page         | 分页查询条件（可以为 RowBounds.DEFAULT） |

### 2.1.5 自定义SQL语句和多表映射

- 根据上面的入门案例可以知道，毛毛张并没有创建对应的`SQL`语句映射文件，而是选择使用`Mybatis-Plus`以及封装好的`CRUD`方法。**刚开始学的时候打开可以依赖这些内置方法，但是如果我们想自定义`SQL`语句怎么办呢？很简单，退回到`Mybatis`框架的方式呗**

- 引入`Mybatis-Plus`不会对项目现有的`Mybatis`构架产生任何影响，而且`Mybatis-Plus`支持所有`Mybatis`原生的特性，由于某些业务复杂，我们可能要自己去写一些比较复杂的SQL语句

- **自定义SQL语句意味着我们需要创建对应的`XML`文件，同时还需要添加扫描，`mybatis-plus`的默认扫描`xxxMapper.xml`位置为：`src/main/resources/mapper`（如果不在该目录下，也可以通过下面参数指定）**

  ```yaml
  mybatis-plus: # mybatis-plus的配置
    # 默认位置 private String[] mapperLocations = new String[]{"classpath*:/mapper/**/*.xml"};    
    mapper-locations: classpath:/mapper/*.xml
  ```

- 下面毛毛张还是通过一个案例来介绍`SpringBoot`整合`Mybatis-Plus`如何实现自定义的`SQL`语句

- 案例需求：自定义查询语句查询所有文章的信息

#### 2.1.5.1 案例代码

- 在`ArticleController`类新增方法：

  ```java
  package com.zzx.controller;
  
  import com.zzx.entity.Article;
  import com.zzx.model.vo.ResultVO;
  import com.zzx.service.ArticleService;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.web.bind.annotation.*;
  
  import java.util.List;
  
  @RestController
  @RequestMapping("/article")
  public class ArticleController {
  
      @Autowired
      private ArticleService articleService;
  
      /**
       * 自定义方法查询所有文章信息
       */
      @GetMapping("/queryAllArticleInfo")
      public ResultVO<List<Article>> queryAllArticles() {
          List<Article> articles = articleService.queryAllArticleInfo();
          return ResultVO.ok(articles);
      }
  }
  

- 在服务层`ArticleService`接口中添加：

  ```java
  package com.zzx.service;
  
  import com.zzx.entity.Article;
  import com.baomidou.mybatisplus.extension.service.IService;
  
  import java.util.List;
  
  /**
  * @author flyvideo
  * @description 针对表【t_article(文章信息表)】的数据库操作Service
  * @createDate 2025-01-03 20:54:38
  */
  public interface ArticleService extends IService<Article> {
      /**
       * 查询所有文章的服务接口方法
       * @return
       */
      List<Article> queryAllArticleInfo();
  
  }
  ```

- 服务层实现类`ArticleServiceImpl`中添加：

  ```java
  package com.zzx.service.impl;
  
  import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
  import com.zzx.entity.Article;
  import com.zzx.service.ArticleService;
  import com.zzx.mapper.ArticleMapper;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  
  import java.util.List;
  
  /**
  * @author flyvideo
  * @description 针对表【t_article(文章信息表)】的数据库操作Service实现
  * @createDate 2025-01-03 20:54:38
  */
  @Service
  public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService{
      @Autowired
      private ArticleMapper articleMapper;
  
      @Override
      public List<Article> queryAllArticleInfo() {
          return articleMapper.queryAllArticleInfo();
      }
  }
  ```

- `ArticleMapper`接口类：

  ```java
  package com.zzx.mapper;
  
  import com.zzx.entity.Article;
  import com.baomidou.mybatisplus.core.mapper.BaseMapper;
  
  import java.util.List;
  
  /**
  * @author flyvideo
  * @description 针对表【t_article(文章信息表)】的数据库操作Mapper
  * @createDate 2025-01-03 20:54:38
  * @Entity com.zzx.Article
  */
  public interface ArticleMapper extends BaseMapper<Article> {
  
      List<Article> queryAllArticleInfo();
  }
  ```

- 基于`mapper.xml`实现：

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="com.zzx.mapper.ArticleMapper">
  
      <resultMap id="BaseResultMap" type="com.zzx.entity.Article">
              <id property="id" column="id" jdbcType="BIGINT"/>
              <result property="category" column="category" jdbcType="VARCHAR"/>
              <result property="tags" column="tags" jdbcType="VARCHAR"/>
              <result property="articleCover" column="article_cover" jdbcType="VARCHAR"/>
              <result property="articleTitle" column="article_title" jdbcType="VARCHAR"/>
              <result property="articleAbstract" column="article_abstract" jdbcType="VARCHAR"/>
              <result property="articleContent" column="article_content" jdbcType="VARCHAR"/>
              <result property="isTop" column="is_top" jdbcType="TINYINT"/>
              <result property="status" column="status" jdbcType="TINYINT"/>
              <result property="isDelete" column="is_delete" jdbcType="TINYINT"/>
              <result property="createTime" column="create_time" jdbcType="TIMESTAMP"/>
              <result property="updateTime" column="update_time" jdbcType="TIMESTAMP"/>
      </resultMap>
  
      <sql id="Base_Column_List">
          id,category,tags,
          article_cover,article_title,article_abstract,
          article_content,is_top,status,
          is_delete,create_time,update_time
      </sql>
  
  
      <!-- 写法1：查询所有文章信息 -->
      <!-- 由于毛毛张在配置文件中配置了驼峰映射 所有在这里不用给数据库的字段名起别名了 -->
      <select id="queryAllArticleInfo" resultType="com.zzx.entity.Article">
          SELECT
              id,
              category,
              tags,
              article_cover,
              article_title,
              article_abstract,
              article_content,
              is_top,
              status,
              is_delete,
              create_time,
              update_time
          FROM t_article
          WHERE is_delete = 0
      </select>
  
  </mapper>
  ```

## 2.2 基于Service接口CRUD

- 通用 Service CRUD 封装IService接口，进一步封装 CRUD 采用 `get 查询单行` `remove 删除` `list 查询集合` `page 分页` 前缀命名方式区分 `Mapper` 层避免混淆

### 2.2.1 对比Mapper接口CRUD区别

-   service层添加了批量方法
-   service层的方法自动添加事务

### 2.2.2 使用Iservice接口方式

- 接口继承IService接口

```java
public interface UserService extends IService<User> {
}
```

- 类继承ServiceImpl实现类

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{
}
```

### 2.2.3 CRUD方法介绍

```java
保存：
// 插入一条记录（选择字段，策略插入）
boolean save(T entity);
// 插入（批量）
boolean saveBatch(Collection<T> entityList);
// 插入（批量）
boolean saveBatch(Collection<T> entityList, int batchSize);

修改或者保存：
// TableId 注解存在更新记录，否插入一条记录
boolean saveOrUpdate(T entity);
// 根据updateWrapper尝试更新，否继续执行saveOrUpdate(T)方法
boolean saveOrUpdate(T entity, Wrapper<T> updateWrapper);
// 批量修改插入
boolean saveOrUpdateBatch(Collection<T> entityList);
// 批量修改插入
boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize);

移除：
// 根据 queryWrapper 设置的条件，删除记录
boolean remove(Wrapper<T> queryWrapper);
// 根据 ID 删除
boolean removeById(Serializable id);
// 根据 columnMap 条件，删除记录
boolean removeByMap(Map<String, Object> columnMap);
// 删除（根据ID 批量删除）
boolean removeByIds(Collection<? extends Serializable> idList);

更新：
// 根据 UpdateWrapper 条件，更新记录 需要设置sqlset
boolean update(Wrapper<T> updateWrapper);
// 根据 whereWrapper 条件，更新记录
boolean update(T updateEntity, Wrapper<T> whereWrapper);
// 根据 ID 选择修改
boolean updateById(T entity);
// 根据ID 批量更新
boolean updateBatchById(Collection<T> entityList);
// 根据ID 批量更新
boolean updateBatchById(Collection<T> entityList, int batchSize);

数量： 
// 查询总记录数
int count();
// 根据 Wrapper 条件，查询总记录数
int count(Wrapper<T> queryWrapper);

查询：
// 根据 ID 查询
T getById(Serializable id);
// 根据 Wrapper，查询一条记录。结果集，如果是多个会抛出异常，随机取一条加上限制条件 wrapper.last("LIMIT 1")
T getOne(Wrapper<T> queryWrapper);
// 根据 Wrapper，查询一条记录
T getOne(Wrapper<T> queryWrapper, boolean throwEx);
// 根据 Wrapper，查询一条记录
Map<String, Object> getMap(Wrapper<T> queryWrapper);
// 根据 Wrapper，查询一条记录
<V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);

集合：
// 查询所有
List<T> list();
// 查询列表
List<T> list(Wrapper<T> queryWrapper);
// 查询（根据ID 批量查询）
Collection<T> listByIds(Collection<? extends Serializable> idList);
// 查询（根据 columnMap 条件）
Collection<T> listByMap(Map<String, Object> columnMap);
// 查询所有列表
List<Map<String, Object>> listMaps();
// 查询列表
List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper);
// 查询全部记录
List<Object> listObjs();
// 查询全部记录
<V> List<V> listObjs(Function<? super Object, V> mapper);
// 根据 Wrapper 条件，查询全部记录
List<Object> listObjs(Wrapper<T> queryWrapper);
// 根据 Wrapper 条件，查询全部记录
<V> List<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);
```

## 2.3 分页查询实现

- **`MyBatis-Plus`的分页插件 `PaginationInnerInterceptor` 提供了强大的分页功能，支持多种数据库，使得分页查询变得简单高效。**
- **注意：于 `v3.5.9` 起，`PaginationInnerInterceptor` 已分离出来。如需使用，则需单独引入 `mybatis-plus-jsqlparser` 依赖。由于毛毛张没有使用这么新的版本，还是使用的旧版本，所有就不需要单独引入依赖了。更多内容可以查看官网：<https://baomidou.com/plugins/pagination/>**
- 毛毛张继续通过入门案例的代码来继续作为演示，通过下面任务来实现下面两个任务需求：
  - **查询所有文章信息并按照默认分页配置返回**
  - **查询所有文章信息并按照前端的分页参数返回**
- <font color="red">**这部分毛毛张做了前端展示，具体的代码可以参看第5节的前端内容**</font>

### 2.3.1 配置MybatisPlus分页插件

- 在`Spring Boot`项目中，可以通过`Java`配置来添加分页插件：

```java
package com.zzx.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 如果配置多个插件, 切记分页最后添加
        // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
        return interceptor;
    }
}
```

### 2.3.2 方式1：使用基于Mapper接口层的方法实现分页查询

- 创建`ArticleController`层接口：

  ```java
  package com.zzx.controller;
  
  import com.zzx.entity.Article;
  import com.zzx.model.vo.PageResult;
  import com.zzx.model.vo.ResultVO;
  import com.zzx.service.ArticleService;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.web.bind.annotation.*;
  
  import java.util.List;
  
  @RestController
  @RequestMapping("/article")
  public class ArticleController {
  
      @Autowired
      private ArticleService articleService;
      
      /**
       * 分页查询1：
       * 使用BaseMapper接口内置的分页方法查询所有文章
       */
      @GetMapping("/queryAllArticleInfoByPagination")
      public ResultVO<Page<Article>> queryAllArticleInfoByPagination() {
          Page<Article> pageVo = articleService.queryAllArticleInfoByPagination();
          return ResultVO.ok(pageVo);
      }
  
      /**
       * 分页查询2:
       * 根据前端的分页参数查询所有文章信息
       * @param current 当前页码
       * @param size    每页记录数
       * @return 包含分页结果的 ResultVO
       */
      @GetMapping("/queryAllArticleInfo")
      public ResultVO<Page<Article>> queryAllArticleInfoByParam(
              @RequestParam(value = "current", defaultValue = "1") Integer current,
              @RequestParam(value = "size", defaultValue = "10") Integer size) {
          Page<Article> page = new Page<>(current, size);
          Page<Article> pageVo = articleService.queryAllArticleInfoByParam(page);
          return ResultVO.ok(pageVo);
      }
  }
  
  ```

- `Servce`层接口：

  ```java
  package com.zzx.service;
  
  import com.baomidou.mybatisplus.core.metadata.IPage;
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.zzx.entity.Article;
  import com.baomidou.mybatisplus.extension.service.IService;
  import com.zzx.model.vo.PageResult;
  import org.apache.ibatis.annotations.Param;
  
  import java.util.List;
  
  /**
   * @author flyvideo
   * @description 针对表【t_article(文章信息表)】的数据库操作Service
   */
  public interface ArticleService extends IService<Article> {
      Page<Article> queryAllArticleInfoByPagination();
  
      Page<Article> queryAllArticleInfoByParam(Page<Article> page);
  }
  ```

- 服务实现类`ArticleServiceImpl`:

  ```java
  package com.zzx.service.impl;
  
  import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
  import com.baomidou.mybatisplus.core.metadata.IPage;
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
  import com.zzx.entity.Article;
  import com.zzx.model.vo.PageResult;
  import com.zzx.service.ArticleService;
  import com.zzx.mapper.ArticleMapper;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  
  import java.util.List;
  
  /**
  * @author flyvideo
  * @description 针对表【t_article(文章信息表)】的数据库操作Service实现
  */
  @Service
  public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService{
      @Autowired
      private ArticleMapper articleMapper;
  
      @Override
      public Page<Article> queryAllArticleInfoByPagination() {
          // 分页参数（当前页：1，每页条数：10）
          Page<Article> page = new Page<>(1, 10);
  
          // 调用 MyBatis-Plus 提供的分页查询方法
          page = articleMapper.selectPage(page, new QueryWrapper<Article>().eq("is_delete", 0));
  
          return page;
      }
  
      @Override
      public Page<Article> queryAllArticleInfoByParam(Page<Article> page) {
          Page<Article> articlePage = this.baseMapper.selectPage(page, new QueryWrapper<Article>().eq("is_delete", 0));
          return articlePage;
      }
  }
  ```

### 2.3.3 方式2：自定义的mapper方法实现分页查询

- 创建一个分页结果封装对象`PageResult`:

  ```java
  package com.zzx.model.vo;
  
  import lombok.AllArgsConstructor;
  import lombok.Data;
  import lombok.NoArgsConstructor;
  
  import java.util.List;
  
  /**
   * 分页结果封装
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public class PageResult<T> {
      private int current; // 当前页
      private int size; // 每页条数
      private int total; // 总记录数
      private int pages; // 总页数
      private List<T> records; // 当前页数据
  }
  ```

- 创建接口`ArtcileController`层接口：

  ```java
  package com.zzx.controller;
  
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.zzx.entity.Article;
  import com.zzx.model.vo.PageResult;
  import com.zzx.model.vo.ResultVO;
  import com.zzx.service.ArticleService;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.web.bind.annotation.*;
  
  import java.util.List;
  
  @RestController
  @RequestMapping("/article")
  public class ArticleController {
  
      @Autowired
      private ArticleService articleService;
  
      /**
       * 分页查询3:
       * 自定义方法实现分页查询
       * @param current 当前页码
       * @param size    每页记录数
       * @return 包含分页结果的 ResultVO
       */
      @GetMapping("/queryAllArticleInfoBySelf")
      public ResultVO<PageResult<Article>> queryAllArticleInfoBySelf(
              @RequestParam(value = "current", defaultValue = "1") Integer current,
              @RequestParam(value = "size", defaultValue = "10") Integer size) {
  
          // 调用服务层方法获取分页结果
          PageResult<Article> pageResult = articleService.queryAllArticleInfoBySelf(current, size);
  
          return ResultVO.ok(pageResult);
      }
  }
  ```

- `ArticleSerivce`层：

  ```java
  package com.zzx.service;
  
  import com.baomidou.mybatisplus.core.metadata.IPage;
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.zzx.entity.Article;
  import com.baomidou.mybatisplus.extension.service.IService;
  import com.zzx.model.vo.PageResult;
  import org.apache.ibatis.annotations.Param;
  
  import java.util.List;
  
  /**
   * @author flyvideo
   * @description 针对表【t_article(文章信息表)】的数据库操作Service
   */
  public interface ArticleService extends IService<Article> {
  
      PageResult<Article> queryAllArticleInfoBySelf(int current, int size);
  }
  ```

- 服务实现类`ArticleServiceImpl`：

  ```java
  package com.zzx.service.impl;
  
  import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
  import com.baomidou.mybatisplus.core.metadata.IPage;
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
  import com.zzx.entity.Article;
  import com.zzx.model.vo.PageResult;
  import com.zzx.service.ArticleService;
  import com.zzx.mapper.ArticleMapper;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  
  import java.util.List;
  
  /**
  * @author flyvideo
  * @description 针对表【t_article(文章信息表)】的数据库操作Service实现
  */
  @Service
  public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService{
      @Autowired
      private ArticleMapper articleMapper;
  
      @Override
      public PageResult<Article> queryAllArticleInfoBySelf(int current, int size) {
          PageResult<Article> pageResult = new PageResult<>();
          pageResult.setCurrent(current);
          pageResult.setSize(size);
  
          // 计算偏移量
          int offset = (current - 1) * size;
  
          // 查询当前页的数据
          List<Article> articleList = articleMapper.selectArticles(offset, size);
          pageResult.setRecords(articleList);
  
          // 查询总记录数
          int total = articleMapper.countArticles();
          pageResult.setTotal(total);
  
          // 计算总页数
          int pages = (int) Math.ceil((double) total / size);
          pageResult.setPages(pages);
  
          return pageResult;
      }
  }
  ```

- `ArticleMapper`接口：

  ```java
  package com.zzx.mapper;
  
  import com.zzx.entity.Article;
  import com.baomidou.mybatisplus.core.mapper.BaseMapper;
  import com.zzx.model.vo.PageResult;
  import org.apache.ibatis.annotations.Param;
  
  import java.util.List;
  
  /**
  * @author flyvideo
  * @description 针对表【t_article(文章信息表)】的数据库操作Mapper
  */
  public interface ArticleMapper extends BaseMapper<Article> {
  
      /**
       * 查询文章列表（带分页）
       * @param offset 偏移量
       * @param limit  每页记录数
       * @return 文章列表
       */
      List<Article> selectArticles(@Param("offset") int offset, @Param("limit") int limit);
  
      /**
       * 查询文章总数
       * @return 总记录数
       */
      int countArticles();
  
  }
  ```

- SQL语句映射文件`ArticleMapper.xml`:

  ```java
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="com.zzx.mapper.ArticleMapper">
  	<!-- 注意，毛毛张在前面的配置文件中已经设置过驼峰映射 -->
      <!-- 查询文章列表（带分页） -->
      <select id="selectArticles" resultType="com.zzx.entity.Article">
          SELECT
              id,
              category,
              tags,
              article_cover,
              article_title,
              article_abstract,
              article_content,
              is_top,
              status,
              is_delete,
              create_time,
              update_time
          FROM t_article
          WHERE is_delete = 0
          ORDER BY create_time DESC
              LIMIT #{limit} OFFSET #{offset}
      </select>
  
      <!-- 查询未删除的文章总数 -->
      <select id="countArticles" resultType="int">
          SELECT COUNT(*) FROM t_article WHERE is_delete = 0
      </select>
  
  </mapper>
  ```

### 2.3.4 方式3：使用PageHelper插件实现分页查询

- 添加依赖：

  ```xml
          <dependency>
              <groupId>com.github.pagehelper</groupId>
              <artifactId>pagehelper-spring-boot-starter</artifactId>
              <version>1.4.5</version>
          </dependency>
  ```

- 创建控制层接口`ArticleController`：

  ```java
  package com.zzx.controller;
  
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.github.pagehelper.PageInfo;
  import com.zzx.entity.Article;
  import com.zzx.model.vo.PageResult;
  import com.zzx.model.vo.ResultVO;
  import com.zzx.service.ArticleService;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.web.bind.annotation.*;
  
  import java.util.List;
  
  @RestController
  @RequestMapping("/article")
  public class ArticleController {
  
      @Autowired
      private ArticleService articleService;
  
  
      /**
       * 分页查询4:
       * 自定义方法实现分页查询
       * @param current 当前页码
       * @param size    每页记录数
       * @return 包含分页结果的 ResultVO
       */
      @GetMapping("/queryAllArticleInfoByPageHelper")
      public ResultVO<PageInfo<Article>> queryAllArticleInfoByPageHelper(
              @RequestParam(value = "current", defaultValue = "1") Integer current,
              @RequestParam(value = "size", defaultValue = "10") Integer size) {
          // 调用服务层方法获取分页结果
          PageInfo<Article> pageResult = articleService.queryAllArticleInfoByPageHelper(current, size);
  
          return ResultVO.ok(pageResult);
      }
  }
  ```

- 服务层接口：

  ```java
  package com.zzx.service;
  
  import com.baomidou.mybatisplus.core.metadata.IPage;
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.github.pagehelper.PageInfo;
  import com.zzx.entity.Article;
  import com.baomidou.mybatisplus.extension.service.IService;
  import com.zzx.model.vo.PageResult;
  import org.apache.ibatis.annotations.Param;
  
  import java.util.List;
  
  /**
   * @author flyvideo
   * @description 针对表【t_article(文章信息表)】的数据库操作Service
   */
  public interface ArticleService extends IService<Article> {
  
      PageInfo<Article> queryAllArticleInfoByPageHelper(Integer current, Integer size);
  }
  ```

- 服务层实现类：

  ```java
  package com.zzx.service.impl;
  
  import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
  import com.baomidou.mybatisplus.core.metadata.IPage;
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
  import com.github.pagehelper.PageHelper;
  import com.github.pagehelper.PageInfo;
  import com.zzx.entity.Article;
  import com.zzx.model.vo.PageResult;
  import com.zzx.service.ArticleService;
  import com.zzx.mapper.ArticleMapper;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  
  import java.util.List;
  
  /**
  * @author flyvideo
  */
  @Service
  public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService{
      @Autowired
      private ArticleMapper articleMapper;
  
      @Override
      public PageInfo<Article> queryAllArticleInfoByPageHelper(Integer current, Integer size) {
          // 使用 PageHelper 启动分页，参数为当前页码和每页大小
          PageHelper.startPage(current, size);
  
          // 调用查询mybatis-plus中的通用 Mapper 方法，返回所有文章的列表
          List<Article> articleList = articleMapper.selectList(null);
  
          // 使用 PageInfo 来封装查询结果
          PageInfo<Article> pageInfo = new PageInfo<>(articleList);
  
          return pageInfo;
      }
  }
  ```

- `PostMan`测试：

![QQ_1736338967132](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736338967132.png)

- 很多时候我们可以根据别人插件里面返回的对象来设计自己的返回的分页对象：

![QQ_1736339031378](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736339031378.png)



## 2.4 条件构造器使用

- 条件构造器的使用毛毛张就不做单独的案例了，因为有IDEA的强大提示功能以及案例，毛毛张在这里主要是总结一下知识点

### 2.4.1 条件构造器作用

- 使用MyBatis-Plus的条件构造器，你可以构建灵活、高效的查询条件，而不需要手动编写复杂的 SQL 语句。它提供了许多方法来支持各种条件操作符，并且可以通过链式调用来组合多个条件。这样可以简化查询的编写过程，并提高开发效率。

- 实例代码：

  ```java
  QueryWrapper<User> queryWrapper = new QueryWrapper<>();
  queryWrapper.eq("name", "John"); // 添加等于条件
  queryWrapper.ne("age", 30); // 添加不等于条件
  queryWrapper.like("email", "@gmail.com"); // 添加模糊匹配条件
  等同于： 
  delete from user where name = "John" and age != 30 and email like "%@gmail.com%"
      
  // 根据 entity 条件，删除记录
  int delete(@Param(Constants.WRAPPER) Wrapper<T> wrapper);
  ```

### 2.4.2 条件构造器继承结构

- 条件构造器类结构：

![](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image_JPU5SqXQMh.png)

- Wrapper ： 条件构造抽象类，最顶端父类

-   AbstractWrapper ： 用于查询条件封装，生成 sql 的 where 条件
    -   QueryWrapper ： 查询/删除条件封装
    -   UpdateWrapper ： 修改条件封装
    -   AbstractLambdaWrapper ： 使用Lambda 语法
        -   LambdaQueryWrapper ：用于Lambda语法使用的查询Wrapper
        -   LambdaUpdateWrapper ： Lambda 更新封装Wrapper

### 2.4.3 基于QueryWrapper组装条件

![](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image_k_cPdiIiy4.png)

组装查询条件：

```java
@Test
public void test01(){
    //查询用户名包含a，年龄在20到30之间，并且邮箱不为null的用户信息
    //SELECT id,username AS name,age,email,is_deleted FROM t_user WHERE is_deleted=0 AND (username LIKE ? AND age BETWEEN ? AND ? AND email IS NOT NULL)
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.like("username", "a")
            .between("age", 20, 30)
            .isNotNull("email");
    List<User> list = userMapper.selectList(queryWrapper);
    list.forEach(System.out::println);

```

组装排序条件:

```java
@Test
public void test02(){
    //按年龄降序查询用户，如果年龄相同则按id升序排列
    //SELECT id,username AS name,age,email,is_deleted FROM t_user WHERE is_deleted=0 ORDER BY age DESC,id ASC
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper
            .orderByDesc("age")
            .orderByAsc("id");
    List<User> users = userMapper.selectList(queryWrapper);
    users.forEach(System.out::println);
}
```

组装删除条件:

```java
@Test
public void test03(){
    //删除email为空的用户
    //DELETE FROM t_user WHERE (email IS NULL)
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.isNull("email");
    //条件构造器也可以构建删除语句的条件
    int result = userMapper.delete(queryWrapper);
    System.out.println("受影响的行数：" + result);
}
```

and和or关键字使用(修改)：

```java
@Test
public void test04() {
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    //将年龄大于20并且用户名中包含有a或邮箱为null的用户信息修改
    //UPDATE t_user SET age=?, email=? WHERE username LIKE ? AND age > ? OR email IS NULL)
    //条件直接调用方法 默认使用and 拼接
    //or().第一个条件是or  -> 后面拼接条件还是and
    queryWrapper
            .like("username", "a")
            .gt("age", 20)
            .or()
            .isNull("email");
    User user = new User();
    user.setAge(18);
    user.setEmail("user@atguigu.com");
    int result = userMapper.update(user, queryWrapper);
    System.out.println("受影响的行数：" + result);
}
```

指定列映射查询：

```java
@Test
public void test05() {
    //查询用户信息的username和age字段
    //SELECT username,age FROM t_user
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.select("username", "age");
    //selectMaps()返回Map集合列表，通常配合select()使用，避免User对象中没有被查询到的列值为null
    List<Map<String, Object>> maps = userMapper.selectMaps(queryWrapper);
    maps.forEach(System.out::println);
}
```

condition判断组织条件:

```java
 @Test
public void testQuick3(){
    
    String name = "root";
    int    age = 18;

    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    //判断条件拼接
    //当name不为null拼接等于, age > 1 拼接等于判断
    //方案1: 手动判断
    if (!StringUtils.isEmpty(name)){
        queryWrapper.eq("name",name);
    }
    if (age > 1){
        queryWrapper.eq("age",age);
    }
    
    //方案2: 拼接condition判断
    //每个条件拼接方法都condition参数,这是一个比较运算,为true追加当前条件!
    //eq(condition,列名,值)
    queryWrapper.eq(!StringUtils.isEmpty(name),"name",name)
            .eq(age>1,"age",age);   
}
```

### 2.3.4 基于 UpdateWrapper组装条件

使用queryWrapper:

```java
@Test
public void test04() {
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    //将年龄大于20并且用户名中包含有a或邮箱为null的用户信息修改
    //UPDATE t_user SET age=?, email=? WHERE username LIKE ? AND age > ? OR email IS NULL)
    queryWrapper
            .like("username", "a")
            .gt("age", 20)
            .or()
            .isNull("email");
    User user = new User();
    user.setAge(18);
    user.setEmail("user@atguigu.com");
    int result = userMapper.update(user, queryWrapper);
    System.out.println("受影响的行数：" + result);
}
```

注意：使用queryWrapper + 实体类形式可以实现修改，但是无法将列值修改为null值！

使用updateWrapper:

```java
@Test
public void testQuick2(){

    UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
    //将id = 3 的email设置为null, age = 18
    updateWrapper.eq("id",3)
            .set("email",null)  // set 指定列和结果
            .set("age",18);
    //如果使用updateWrapper 实体对象写null即可!
    int result = userMapper.update(null, updateWrapper);
    System.out.println("result = " + result);

}
```

使用updateWrapper可以随意设置列的值！！

### 2.3.5 基于LambdaQueryWrapper组装条件

#### 1.LambdaQueryWrapper对比QueryWrapper优势

QueryWrapper 示例代码：

```java
QueryWrapper<User> queryWrapper = new QueryWrapper<>();
queryWrapper.eq("name", "John")
  .ge("age", 18)
  .orderByDesc("create_time")
  .last("limit 10");
List<User> userList = userMapper.selectList(queryWrapper);
```

LambdaQueryWrapper 示例代码：

```java
LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();

lambdaQueryWrapper.eq(User::getName, "John")
  .ge(User::getAge, 18)
  .orderByDesc(User::getCreateTime)
  .last("limit 10");
List<User> userList = userMapper.selectList(lambdaQueryWrapper);
```

从上面的代码对比可以看出，相比于 QueryWrapper，LambdaQueryWrapper 使用了实体类的属性引用（例如 `User::getName`、`User::getAge`），而不是字符串来表示字段名，这提高了代码的可读性和可维护性。

#### 2.lambda表达式回顾

- `Lambda`表达式是`Java 8`引入的一种函数式编程特性，它提供了一种更简洁、更直观的方式来表示匿名函数或函数式接口的实现。
- `Lambda`表达式可以用于简化代码，提高代码的可读性和可维护性。

- `Lambda`表达式的语法可以分为以下几个部分：
  - **参数列表：** 参数列表用小括号 `()` 括起来，可以指定零个或多个参数；如果没有参数，可以省略小括号；如果只有一个参数，可以省略小括号。示例：`(a, b)`, `x ->`, `() ->`
  - **箭头符号：** 箭头符号 `->` 分割参数列表和 Lambda 表达式的主体部分。示例：`->`
  - **Lambda 表达式的主体：** `Lambda`表达式的主体部分可以是一个表达式或一个代码块。如果是一个表达式，可以省略`return`关键字；如果是多条语句的代码块，需要使用大括号 `{}` 括起来，并且需要明确指定`return`关键字。

- 示例：

  -   单个表达式：`x -> x * x`

  -   代码块：`(x, y) -> { int sum = x + y; return sum; }`

- 代码案例：

  ```java
  // 使用 Lambda 表达式实现一个接口的方法
  interface Greeting {
      void sayHello();
  }
  
  public class LambdaExample {
      public static void main(String[] args) {
      
          //原始匿名内部类方式
          Greeting greeting = new Greeting() {
              @Override
              public void sayHello(int a) {
                  System.out.println("Hello, world!");
              }
          };
          
          a->System.out.println("Hello, world!")
          
          // 使用 Lambda 表达式实现接口的方法
          greeting = () -> System.out.println("Hello, world!");
  
            System.out::println;
             () ->  类.XXX(); -> 类：：方法名
          // 调用接口的方法
          greeting.sayHello();
      }
  }
  ```

#### 3.方法引用回顾

- 方法引用是`Java 8`中引入的一种语法特性，它提供了一种简洁的方式来直接引用已有的方法或构造函数。
- 方法引用可以替代`Lambda`表达式，使代码更简洁、更易读。

- `Java 8`支持以下几种方法引用的形式：
  - **静态方法引用：** 引用静态方法，语法为 `类名::静态方法名`
  - **实例方法引用：** 引用实例方法，语法为 `实例对象::实例方法名`
  - **对象方法引用：** 引用特定对象的实例方法，语法为 `类名::实例方法名`
  - **构造函数引用：** 引用构造函数，语法为 `类名::new`。

- 演示代码：

  ```java
  import java.util.Arrays;
  import java.util.List;
  import java.util.function.Consumer;
  
  public class MethodReferenceExample {
      public static void main(String[] args) {
          List<String> names = Arrays.asList("John", "Tom", "Alice");
          // 使用 Lambda 表达式
          names.forEach(name -> System.out.println(name));
          // 使用方法引用
          names.forEach(System.out::println);
      }
  }
  ```

#### 4.lambdaQueryWrapper使用案例

```java
@Test
public void testQuick4(){

    String name = "root";
    int    age = 18;

    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    //每个条件拼接方法都condition参数,这是一个比较运算,为true追加当前条件!
    //eq(condition,列名,值)
    queryWrapper.eq(!StringUtils.isEmpty(name),"name",name).eq(age>1,"age",age);

    //TODO: 使用lambdaQueryWrapper
    LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    //注意: 需要使用方法引用
    //技巧: 类名::方法名
    lambdaQueryWrapper.eq(!StringUtils.isEmpty(name), User::getName,name);
    List<User> users= userMapper.selectList(lambdaQueryWrapper);
    System.out.println(users);
}
```

### 2.3.6 基于LambdaUpdateWrapper组装条件

- 使用案例：

  ```java
  @Test
  public void testQuick2(){
  
      UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
      //将id = 3 的email设置为null, age = 18
      updateWrapper.eq("id",3)
              .set("email",null)  // set 指定列和结果
              .set("age",18);
  
      //使用lambdaUpdateWrapper
      LambdaUpdateWrapper<User> updateWrapper1 = new LambdaUpdateWrapper<>();
      updateWrapper1.eq(User::getId,3)
              .set(User::getEmail,null)
              .set(User::getAge,18);
      
      //如果使用updateWrapper 实体对象写null即可!
      int result = userMapper.update(null, updateWrapper);
      System.out.println("result = " + result);
  }
  ```

## 2.5 核心注解使用

### 2.5.1 背景介绍

- MyBatis-Plus是一个基于MyBatis框架的增强工具，提供了一系列简化和增强的功能，用于加快开发人员在使用MyBatis进行数据库访问时的效率。

- MyBatis-Plus提供了一种基于注解的方式来定义和映射数据库操作，其中的注解起到了重要作用。

- 理解：

  ```java
  public interface UserMapper extends BaseMapper<User> {
  
  }
  ```

- 解释：此接口对应的方法为什么会自动触发 user表的crud呢？

  - 默认情况下， 根据指定的<实体类>的名称对应数据库表名，属性名对应数据库的列名
  - **但是不是所有数据库的信息和实体类都完全映射！例如： 表名 t_user  → 实体类 User 这时候就不对应了！**

- 自定义映射关系就可以使用mybatis-plus提供的注解即可！

### 2.5.2 @TableName注解

-   描述：表名注解，标识实体类对应的表
-   属性值：

| 属性             | 类型     | 必须指定 | 默认值 | 描述                                                         |
| :--------------- | :------- | :------- | :----- | :----------------------------------------------------------- |
| value            | String   | 否       | ""     | 表名                                                         |
| schema           | String   | 否       | ""     | schema                                                       |
| keepGlobalPrefix | boolean  | 否       | false  | 是否保持使用全局的 tablePrefix 的值（当全局 tablePrefix 生效时） |
| resultMap        | String   | 否       | ""     | xml 中 resultMap 的 id（用于满足特定类型的实体类对象绑定）   |
| autoResultMap    | boolean  | 否       | false  | 是否自动构建 resultMap 并使用（如果设置 resultMap 则不会进行 resultMap 的自动构建与注入） |
| excludeProperty  | String[] | 否       | {}     | 需要排除的属性名 @since 3.3.1                                |

-   使用位置：实体类

```java
@TableName("sys_user") //对应数据库表名
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
```

- 注解说明：

  - 当我们的**实体类名**跟我们的数据库表名不一致时，可根据`@TableName`来指定数据库表名

  - 如果表名和实体类名相同（忽略大小写）可以省略该注解！

  - 如果表明和数据库的关键字冲突，需要加上反向单引号

    ```java
    @TableName("`rank`") //对应数据库表名
    public class User {
        private Long id;
        private String name;
        private Integer age;
        private String email;
    }
    ```

- 其他解决方案：全局设置前缀

  ```yaml
  mybatis-plus: # mybatis-plus的配置
    global-config:
      db-config:
        table-prefix: sys_ # 表名前缀字符串
  ```

### 2.5.3 @TableId 注解

-   描述：主键注解
-   属性值：

| 属性  | 类型   | 必须指定 | 默认值      | 描述         |
| :---- | :----- | :------- | :---------- | :----------- |
| value | String | 否       | ""          | 主键字段名   |
| type  | Enum   | 否       | IdType.NONE | 指定主键类型 |

-   `IdType`属性：

| 值          | 描述                                                         |
| :---------- | :----------------------------------------------------------- |
| AUTO        | 数据库 ID 自增 (mysql配置主键自增长)                         |
| NONE        | 无状态，该类型为未设置主键类型（注解里等于跟随全局，全局里约等于 INPUT）雪花算法实现 |
| INPUT       | 需要开发者手动赋值                                           |
| ASSIGN_ID   | 分配 ID(主键类型为 Number(Long )或 String)(since 3.3.0)，使用接口`IdentifierGenerator`的方法`nextId`(默认实现类为`DefaultIdentifierGenerator`雪花算法) |
| ASSIGN_UUID | 分配 UUID，Strinig                                           |

-   使用位置：实体类主键字段

```java
@TableName("sys_user")
public class User {
    @TableId(value="主键列名",type=主键策略)
    private Long id;
    private String name;
    private Integer age;
    private String email;
}

```

- 全局配置修改主键策略:

```yaml
mybatis-plus:
  configuration:
    # 配置MyBatis日志
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      # 配置MyBatis-Plus操作表的默认前缀
      table-prefix: t_
      # 配置MyBatis-Plus的主键策略
      id-type: auto
```

> 下面详细介绍IdType中两个最常见的属性：**AUTO**和**INPUT**

#### 2.5.3.1 AUTO属性

- **使用前提**：需要在数据库中将**主键设置为自增**
- **生成ID算法**：【**雪花算法】**，基本可以保证全球唯一ID值
- **特点**：
  - 生成的ID值在原来的ID基础上**自增**，并且会将ID值**回填**到我们的Java对象中
  - 对主键使用了AUTO，那么我们**自行注入的ID值将会被自动生成的ID覆盖**


**代码演示：**

```java
package com.zzx.mybatis_plus.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
```

**测试：**

我们在插入时**自定义ID值**。

```java
@Test
void test4() {
    // 我们没有自定义id，它会帮我们自动生成id
    Person person =new Person();
    person.setId(2L);
    person.setName("一心同学");
    person.setAge(18);
    person.setEmail("123456@qq.com");
    int result=personMapper.insert(person);
    System.out.println(result);//受影响的行数
    System.out.println(person);//可以发现，id会自动回填
}
```

**结果：**

> **发现1：可以发现MP并没有采纳我们的ID值，因为其注入语句中根本就没有我们的主键id，而是选择用【雪花算法】自动生成的ID值。**
>
> **发现2：自动生成的ID值自动回填到我们的Java对象中了。**

![image-20241018171925866](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241018171925866.png)

#### 2.5.3.2 INPUT属性

- **生成ID算法**：【**雪花算法**】，基本可以保证全球唯一ID值。

- **特点**：
  - **开发者没有手动赋值，则数据库通过自增的方式给主键赋值，但是并不会回填到我们的Java对象中**
  - **如果开发者手动赋值，则存入该值，开发者手动复制的，那肯定就会显示在对象的属性值里面**


**代码演示：**

```java
package com.zzx.mybatis_plus.pojo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
```

**(1)自定义ID测试：**

```java
@Test
void test4() {
    //我们自定义ID，则会使用我们的ID，否则自动生成ID

    Person person =new Person();
    person.setId(5L);
    person.setName("一心同学2");
    person.setAge(18);
    person.setEmail("123456@qq.com");
    int result=personMapper.insert(person);
    System.out.println(result);// 受影响的行数
    System.out.println(person);
}
```

> **结果：**可以发现，**MP是采用我们自定义的ID值的**。

![img](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/ed251860e8efcce88b6052363f0317de.png)

**(2)不定义ID值测试：**

```java
@Test
void test4() {
    //我们自定义ID，则会使用我们的ID，否则自动生成ID
    Person person =new Person();
    person.setName("一心同学3");
    person.setAge(18);
    person.setEmail("123456@qq.com");
    int result=personMapper.insert(person);
    System.out.println(result);// 受影响的行数
    System.out.println(person);//发现不会回填！
}
```

> **结果**：可以发现，当我们**没有进行ID的定义时，MP将会通过自增的方式给主键赋值，并且不会将赋完的值注入到我们的Java对象，但在我们数据库中是有Id值的，只是没回填到Java对象而已**

 

![img](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/b134e5adca40d344b5c5e4afab633a3d.png)

![img](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/f253a0a2adad9c49d99723dc69c2c006.png)

#### 2.5.3.3 AUTO与INPUT对比

| Type  | 算法     | 特点                                                         | 是否覆盖自定义值 | 是否回填 |
| ----- | -------- | ------------------------------------------------------------ | ---------------- | -------- |
| AUTO  | 雪花算法 | 生成的ID值在原来的ID基础上自增                               | 是               | 是       |
| INPUT | 雪花算法 | 开发者没有手动赋值，则数据库通过自增的方式给主键赋值，如果开发者手动赋值，则存入该值。 | 否               | 否       |

**总结：**

- 在以下场景下，添加`@TableId`注解是必要的：
  1. 实体类的字段与数据库表的主键字段不同名：如果实体类中的字段与数据库表的主键字段不一致，需要使用`@TableId`注解来指定实体类中表示主键的字段。
  2. 主键生成策略不是默认策略：如果需要使用除了默认主键生成策略以外的策略，也需要添加`@TableId`注解，并通过`value`属性指定生成策略。

- 雪花算法使用场景
  - 雪花算法（Snowflake Algorithm）是一种用于生成唯一ID的算法。它由Twitter公司提出，用于解决分布式系统中生成全局唯一ID的需求。
  - 在传统的自增ID生成方式中，使用单点数据库生成ID会成为系统的瓶颈，而雪花算法通过在分布式系统中生成唯一ID，避免了单点故障和性能瓶颈的问题。
  - 雪花算法生成的ID是一个64位的整数，由以下几个部分组成：
    1. 时间戳：41位，精确到毫秒级，可以使用69年。
    2. 节点ID：10位，用于标识分布式系统中的不同节点。
    3. 序列号：12位，表示在同一毫秒内生成的不同ID的序号。
  - 通过将这三个部分组合在一起，雪花算法可以在分布式系统中生成全局唯一的ID，并保证ID的生成顺序性。
  - 雪花算法的工作方式如下：
    1. 当前时间戳从某一固定的起始时间开始计算，可以用于计算ID的时间部分。
    2. 节点ID是分布式系统中每个节点的唯一标识，可以通过配置或自动分配的方式获得。
    3. 序列号用于记录在同一毫秒内生成的不同ID的序号，从0开始自增，最多支持4096个ID生成。
  - 需要注意的是，雪花算法依赖于系统的时钟，需要确保系统时钟的准确性和单调性，否则可能会导致生成的ID不唯一或不符合预期的顺序。
  - 雪花算法是一种简单但有效的生成唯一ID的算法，广泛应用于分布式系统中，如微服务架构、分布式数据库、分布式锁等场景，以满足全局唯一标识的需求。
- **你需要记住的: 雪花算法生成的数字，需要使用Long 或者 String类型主键!!**
  - 默认：雪花算法  
    - 1.数据库主键  bigint/varchar(64)
    - 2.实体类Long类型
    - 3.随机生成一个数字，给与主键值(不重复)
  - auto
    - mysql 数据库 表主键的时候 类型  数字  auto_increment
    - 插入数据自增长了

### 2.5.4 @TableField

##### 注解介绍

**描述：** 字段注解（非主键）

**属性值：**

| 属性             | 类型                         | 必须指定 | 默认值                   | 描述                                                         |
| :--------------- | :--------------------------- | :------- | :----------------------- | :----------------------------------------------------------- |
| **value**        | **String**                   | **否**   | **""**                   | **数据库字段名**                                             |
| **exist**        | **boolean**                  | **否**   | **true**                 | **是否为数据库表字段**                                       |
| condition        | String                       | 否       | ""                       | 字段 `where` 实体查询比较条件，有值设置则按设置的值为准，没有则为默认全局的 `%s=#{%s}` |
| update           | String                       | 否       | ""                       | 字段 `update set` 部分注入，例如：当在version字段上注解`update="%s+1"` 表示更新时会 `set version=version+1` （该属性优先级高于 `el` 属性） |
| insertStrategy   | Enum                         | 否       | FieldStrategy.DEFAULT    | 举例：NOT_NULL `insert into table_a(<if test="columnProperty != null">column</if>) values (<if test="columnProperty != null">#{columnProperty}</if>)` |
| updateStrategy   | Enum                         | 否       | FieldStrategy.DEFAULT    | 举例：IGNORED `update table_a set column=#{columnProperty}`  |
| whereStrategy    | Enum                         | 否       | FieldStrategy.DEFAULT    | 举例：NOT_EMPTY `where <if test="columnProperty != null and columnProperty!=''">column=#{columnProperty}</if>` |
| fill             | Enum                         | 否       | FieldFill.DEFAULT        | 字段自动填充策略                                             |
| select           | boolean                      | 否       | true                     | 是否进行 select 查询                                         |
| keepGlobalFormat | boolean                      | 否       | false                    | 是否保持使用全局的 format 进行处理                           |
| jdbcType         | JdbcType                     | 否       | JdbcType.UNDEFINED       | JDBC 类型 (该默认值不代表会按照该值生效)                     |
| typeHandler      | Class<? extends TypeHandler> | 否       | UnknownTypeHandler.class | 类型处理器 (该默认值不代表会按照该值生效)                    |
| numericScale     | String                       | 否       | ""                       | 指定小数点后保留的位数                                       |

- FieldStrategy 属性

| 值        | 描述                                                        |
| :-------- | :---------------------------------------------------------- |
| IGNORED   | 忽略判断                                                    |
| NOT_NULL  | 非 NULL 判断                                                |
| NOT_EMPTY | 非空判断(只对字符串类型字段,其他类型字段依然为非 NULL 判断) |
| DEFAULT   | 追随全局配置                                                |

- FieldFill属性值

| 值            | 描述                 |
| :------------ | :------------------- |
| DEFAULT       | 默认不处理           |
| INSERT        | 插入时填充字段       |
| UPDATE        | 更新时填充字段       |
| INSERT_UPDATE | 插入和更新时填充字段 |

**使用位置：实体类的属性值**

```java
@TableName("sys_user")
public class User {
    @TableId
    private Long id;
    @TableField("nickname")
    private String name;
    private Integer age;
    private String email;
}
```

> **MyBatis-Plus会自动开启驼峰命名风格映射!!!**

##### 普通属性

**代码演示：**

```java
package com.zzx.mybatis_plus.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "person")
public class User {
    @TableId(type=IdType.AUTO)
    private Long id;
    
    // 当该字段名称与数据库名字不一致
    @TableField(value = "name")
    private String name;
    
    // 不查询该字段
    @TableField(select = false)
    private Integer age;

    // 是否为数据库表字段,设置为false，则插入时不会对其操作
    @TableField(exist = false)
    private String email;
}
```

##### 自动填充属性

- **步骤一**：**数据库**增加两个属性，**create_time**和**update_time。**

![image-20241018171855066](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241018171855066.png)

- **步骤二**：实体类**字段属性添加注解。**

```java
// 第一次添加填充
@TableField(fill= FieldFill.INSERT)
private Date createTime;


// 第一次添加的时候填充，但之后每次更新也会进行填充
@TableField(fill=FieldFill.INSERT_UPDATE)
private Date updateTime;
```

- **步骤三**：编写**处理器**来处理这个注解

> **注意：在处理器类上方添加注解@Component!**

```java
package com.yixin.mybatis_plus.handler;
 
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
 
import java.util.Date;
 
@Slf4j
@Component // 一定不要忘记把处理器加到IOC容器中！
public class MyMetaObjectHandler implements MetaObjectHandler {
    // 插入时的填充策略
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill.....");
        // setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject
        this.setFieldValByName("createTime",new Date(),metaObject);
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }
 
    // 更新时的填充策略
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill.....");
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }
}
```

- **测试：**

```java
@Test
void test9() {
    User person =new User();
    person.setName("一心同学呀");
    person.setAge(20);
    person.setEmail("test6@qq.com");
    int result=personsMapper.insert(person);
    System.out.println(result);
    System.out.println(person);
}
```

**数据库**

![img](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/8fde395b408acf97a1a7be427e3b1a34.png)

# 3 MyBatis-Plus高级扩展

## 3.1 逻辑删除实现

### 3.3.1 概念

- 逻辑删除，可以方便地实现对数据库记录的逻辑删除而不是物理删除。

- **逻辑删除是指通过更改记录的状态或添加标记字段来模拟删除操作，从而保留了删除前的数据，便于后续的数据分析和恢复。**

  -   物理删除：真实删除，将对应数据从数据库中删除，之后查询不到此条被删除的数据

  -   逻辑删除：假删除，将对应数据中代表是否被删除字段的状态修改为“被删除状态”，之后在数据库中仍旧能看到此条数据记录


### 3.3.2 逻辑删除实现

#### 1.数据库和实体类添加逻辑删除字段

1. 表添加逻辑删除字段：可以是一个布尔类型、整数类型或枚举类型

   ```sql
   ALTER TABLE USER ADD isDelete INT DEFAULT 0 ;  # int 类型 1 逻辑删除 0 未逻辑删除
   ```
   
2. 实体类添加逻辑删除属性

   ```sql
   @Data
   public class User {
   
      // @TableId
       private Integer id;
       private String name;
       private Integer age;
       private String email;
       
       @TableLogic
       //逻辑删除字段 int mybatis-plus下,默认 逻辑删除值为1 未逻辑删除 0 
       private Integer deleted;
   }
   
   ```

#### 2.指定逻辑删除字段和属性值

- 方式1：单一指定

  ```java
  @Data
  public class User {
  
     // @TableId
      private Integer id;
      private String name;
      private Integer age;
      private String email;
       @TableLogic
      //逻辑删除字段 int mybatis-plus下,默认 逻辑删除值为1 未逻辑删除 0 
      private Integer deleted;
  }
  ```

- 方式2：

  ```java
  mybatis-plus:
    global-config:
      db-config:
        logic-delete-field: deleted # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
        logic-delete-value: 1 # 逻辑已删除值(默认为 1)
        logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
  ```

#### 3.演示逻辑删除操作

- 逻辑删除以后,没有真正的删除语句,删除改为修改语句!

- 删除代码:

  ```java
  //逻辑删除
  @Test
  public void testQuick5(){
      //逻辑删除
      userMapper.deleteById(5);
  }
  ```

- 执行效果:

  ```shell
  JDBC Connection \[com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl\@5871a482] will not be managed by Spring
  \==> Preparing: UPDATE user SET deleted=1 WHERE id=? AND deleted=0
  \==> Parameters: 5(Integer)
  <==    Updates: 1
  ```

#### 4.测试查询数据

```java
@Test
public void testQuick6(){
    //正常查询.默认查询非逻辑删除数据
    userMapper.selectList(null);
}

//SELECT id,name,age,email,deleted FROM user WHERE deleted=0
```

## 3.2 乐观锁实现

### 3.2.1 悲观锁和乐观锁场景和介绍

**并发问题场景演示:**

![](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image_fmiYphLjW-.png)

**解决思路: **

- 乐观锁和悲观锁是在并发编程中用于处理并发访问和资源竞争的两种不同的锁机制!!

- 悲观锁：
  - 悲观锁的基本思想是，在整个数据访问过程中，将共享资源锁定，以确保其他线程或进程不能同时访问和修改该资源。
  - 悲观锁的核心思想是"先保护，再修改"。在悲观锁的应用中，线程在访问共享资源之前会获取到锁，并在整个操作过程中保持锁的状态，阻塞其他线程的访问。只有当前线程完成操作后，才会释放锁，让其他线程继续操作资源。这种锁机制可以确保资源独占性和数据的一致性，但是在高并发环境下，悲观锁的效率相对较低。

- 乐观锁：
  - 乐观锁的基本思想是，认为并发冲突的概率较低，因此不需要提前加锁，而是在数据更新阶段进行冲突检测和处理。
  - 乐观锁的核心思想是"先修改，后校验"。在乐观锁的应用中，线程在读取共享资源时不会加锁，而是记录特定的版本信息。当线程准备更新资源时，会先检查该资源的版本信息是否与之前读取的版本信息一致，如果一致则执行更新操作，否则说明有其他线程修改了该资源，需要进行相应的冲突处理。乐观锁通过避免加锁操作，提高了系统的并发性能和吞吐量，但是在并发冲突较为频繁的情况下，乐观锁会导致较多的冲突处理和重试操作。

- 理解点: 悲观锁和乐观锁是两种解决并发数据问题的思路,不是具体技术!!!

**具体技术和方案:**

1.  乐观锁实现方案和技术：
    -   版本号/时间戳：为数据添加一个版本号或时间戳字段，每次更新数据时，比较当前版本号或时间戳与期望值是否一致，若一致则更新成功，否则表示数据已被修改，需要进行冲突处理。
    -   CAS（Compare-and-Swap）：使用原子操作比较当前值与旧值是否一致，若一致则进行更新操作，否则重新尝试。
    -   无锁数据结构：采用无锁数据结构，如无锁队列、无锁哈希表等，通过使用原子操作实现并发安全。
2.  悲观锁实现方案和技术：
    -   锁机制：使用传统的锁机制，如互斥锁（Mutex Lock）或读写锁（Read-Write Lock）来保证对共享资源的独占访问。
    -   数据库锁：在数据库层面使用行级锁或表级锁来控制并发访问。
    -   信号量（Semaphore）：使用信号量来限制对资源的并发访问。

**介绍版本号乐观锁技术的实现流程:**

-   每条数据添加一个版本号字段version
-   取出记录时，获取当前 version
-   更新时，检查获取版本号是不是数据库当前最新版本号
-   如果是\[证明没有人修改数据], 执行更新, set 数据更新 , version = version+ 1&#x20;
-   如果 version 不对\[证明有人已经修改了]，我们现在的其他记录就是失效数据!就更新失败

### 3.2.2 使用mybatis-plus数据使用乐观锁

1. 添加版本号更新插件

   ```java
   @Bean
   public MybatisPlusInterceptor mybatisPlusInterceptor() {
       MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
       interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
       return interceptor;
   }
   ```

2. 乐观锁字段添加@Version注解

   ```mysql
   ALTER TABLE USER ADD VERSION INT DEFAULT 1 ;  # int 类型 乐观锁字段
   ```

   ```java
   @Version
   private Integer version;
   ```

> 注意: 
>
> - 数据库也需要添加version字段
> - 支持的数据类型只有:int,Integer,long,Long,Date,Timestamp,LocalDateTime
> - 整数类型下 newVersion = oldVersion + 1
> - newVersion 会回写到 entity 中
> - 仅支持 updateById(id) 与 update(entity, wrapper) 方法
> - 在 update(entity, wrapper) 方法下， wrapper 不能复用

3. 正常更新使用即可

```java
//演示乐观锁生效场景
@Test
public void testQuick7(){
    //步骤1: 先查询,在更新 获取version数据
    //同时查询两条,但是version唯一,最后更新的失败
    User user  = userMapper.selectById(5);
    User user1  = userMapper.selectById(5);

    user.setAge(20);
    user1.setAge(30);

    userMapper.updateById(user);
    //乐观锁生效,失败!
    userMapper.updateById(user1);
}
```

### 3.2.3 实例

- **步骤一：给数据库中增加version字段！**

![image-20241018171834109](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241018171834109.png)

- **步骤二：实体类添加对应的字段**

```java
package com.yixin.mybatis_plus.pojo;
 
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
 
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String email;
    
    @Version //乐观锁Version注解
    private Integer version;
}
```

- **步骤三：注册组件**

```java
package com.yixin.mybatis_plus.config;
 
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
 
// 扫描我们的 mapper 文件夹
@MapperScan("com.yixin.mybatis_plus.mapper")
@EnableTransactionManagement
@Configuration // 配置类
public class MyBatisPlusConfig {
 
    // 注册乐观锁插件
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }
 
}
```

**测试1：测试乐观锁成功情况**

```java
@Test
void testOptimisticLocker() {

    // 1、查询用户信息
    Person person=personMapper.selectById(1L);

    // 2、修改用户信息
    person.setName("一心同学");

    int result=personMapper.updateById(person);

    System.out.println(result);// 受影响的行数
}
```

**结果：可以发现MP会自动帮我们进行乐观锁判定，并且每修改一次，version就会+1。**

![img](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/efaa1c313be809f2e2c10e631a856ef6.png)

![img](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/46c03f2729c5fd31348cb1a82ae66052.png)

**测试2：测试失败情况！多线程下！**

```java
@Test
void testOptimisticLocker2() {
    // 线程 1
    Person person=personMapper.selectById(3L);
    person.setName("一心同学在写博客");

    // 模拟另外一个线程执行了插队操作
    Person person2=personMapper.selectById(3L);
    person2.setName("一心同学在吃饭");

    personMapper.updateById(person2);

    int result=personMapper.updateById(person);// 如果没有乐观锁就会覆盖插队线程的值！

    System.out.println(result);// 受影响的行数
}
```

数据库：**也就是说我们的线程一没有执行成功**

![image-20241018171813228](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241018171813228.png)

## 3.3 防全表更新和删除实现

- **针对 update 和 delete 语句 作用：阻止恶意的全表更新删除**

- **实现：添加防止全表更新和删除拦截器**

  ```java
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
    return interceptor;
  }
  ```

- 测试全部更新或者全部删除

  ```java
  @Test
  public void testQuick8(){
      User user = new User();
      user.setName("custom_name");
      user.setEmail("xxx@mail.com");
      //Caused by: com.baomidou.mybatisplus.core.exceptions.MybatisPlusException: Prohibition of table update operation
      //全局更新,报错
      userService.saveOrUpdate(user,null);
  }
  ```

# 4 Mybatis-Plus配置

- 前面毛毛张介绍了一部分`Mybatis-Plus`的配置，这一小节毛毛张就来接单介绍一下这些配置

  ```yaml
  # mybatis-plus相关配置
  mybatis-plus:
    # xml扫描，多个目录用逗号或者分号分隔（告诉 Mapper 所对应的 XML 文件位置）
    mapper-locations: classpath:mapper/*.xml
    # 以下配置均有默认值,可以不设置
    global-config:
      db-config:
        #主键类型 AUTO:"数据库ID自增" INPUT:"用户输入ID",ID_WORKER:"全局唯一ID (数字类型唯一ID)", UUID:"全局唯一ID UUID";
        id-type: auto
        #字段策略 IGNORED:"忽略判断"  NOT_NULL:"非 NULL 判断")  NOT_EMPTY:"非空判断"
        field-strategy: NOT_EMPTY
        #数据库类型
        db-type: MYSQL
    configuration:
      # 是否开启自动驼峰命名规则映射:从数据库列名到Java属性驼峰命名的类似映射
      map-underscore-to-camel-case: true
      # 如果查询结果中包含空值的列，则 MyBatis 在映射的时候，不会映射这个字段
      call-setters-on-nulls: true
      # 这个配置会将执行的sql打印出来，在开发或测试的时候可以用
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  ```


- `Mybatis-Plus`的配置很多，所以大家遇到没用见过的配置可以查看`Mybatis-Plus`官网：<https://baomidou.com/reference/>

![QQ_1735963292124](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735963292124.png)

## 4.1 使用方式

- 本部分配置包含了大部分用户的常用配置，其中一部分为 MyBatis 原生所支持的配置

- 使用方法：

  ```yaml
  mybatis-plus:
    configuration:
      # MyBatis 配置
      map-underscore-to-camel-case: true
    global-config:
      # 全局配置
      db-config:
        # 数据库配置
        id-type: auto
  ```

## 4.2 基本配置

### configLocation

- **类型**：`String`
- **默认值**：`null`
- 说明：指定 MyBatis 配置文件的位置。如果有单独的 MyBatis 配置文件，应将其路径配置到 `configLocation`。

- **配置示例**：

  ```yaml
  mybatis-plus:
    config-location: classpath:/mybatis-config.xml
  ```

### mapperLocations

- **类型**：`String[]`
- **默认值**：`["classpath:/mapper/**/*.xml"]`
- 说明：指定 MyBatis Mapper 对应的 XML 文件位置。如果在 Mapper 中有自定义方法，需要配置此项。

- **配置示例**：

  ```yaml
  mybatis-plus:
    mapper-locations: classpath:/mapper/**.xml
  ```

- 注意：对于 Maven 多模块项目，扫描路径应以 `classpath*:` 开头，以加载多个 JAR 包中的 XML 文件。

### typeAliasesPackage

- **类型**：`String`
- **默认值**：`null`
- 说明：指定 MyBatis 别名包扫描路径，用于给包中的类注册别名。注册后，在 Mapper 对应的 XML 文件中可以直接使用类名，无需使用全限定类名。

- **配置示例**：

  ```yaml
  mybatis-plus:
    type-aliases-package: com.your.domain
  ```

### typeAliasesSuperType

- **类型**：`Class<?>`
- **默认值**：`null`
- 说明：与 `typeAliasesPackage` 一起使用，仅扫描指定父类的子类。

- **配置示例**：

  ```java
  mybatis-plus:
    type-aliases-super-type: com.your.domain.BaseEntity
  ```

### configurationProperties

- **类型**：`Properties`
- **默认值**：`null`
- 说明：指定外部化 MyBatis Properties 配置，用于抽离配置，实现不同环境的配置部署。

- **配置示例**：

  ```yaml
  mybatis-plus:
  
    configuration-properties: classpath:/mybatis-properties.properties
  ```

### configuration

- **类型**：`Configuration`
- **默认值**：`null`
- 说明：原生 MyBatis 所支持的配置，具体的看下一节

## 4.3 Configuration

- `MyBatis-Plus`的 `Configuration` 配置继承自 MyBatis 原生支持的配置，这意味着您可以通过 MyBatis XML 配置文件的形式进行配置，也可以通过 Spring Boot 或 Spring MVC 的配置文件进行设置。

### mapUnderscoreToCamelCase

- **类型**：`boolean`
- **默认值**：`true`
- 说明：开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN（下划线命名） 到经典 Java 属性名 aColumn（驼峰命名） 的类似映射。

- **配置示例**：

  ```yaml
  mybatis-plus:
    configuration:
      map-underscore-to-camel-case: true
  ```

- 提示：在 MyBatis-Plus 中，此属性也将用于生成最终的 SQL 的 select body。如果您的数据库命名符合规则，无需使用 `@TableField` 注解指定数据库字段名。

### autoMappingBehavior

- **类型**：`AutoMappingBehavior`

- **默认值**：`partial`

- 说明：MyBatis 自动映射策略，通过该配置可指定 MyBatis 是否并且如何来自动映射数据表字段与对象的属性。

  - AutoMappingBehavior.NONE：不启用自动映射

  - AutoMappingBehavior.PARTIAL：只对非嵌套的 resultMap 进行自动映射

  - AutoMappingBehavior.FULL：对所有的 resultMap 都进行自动映射

- **配置示例**：

  ```yaml
  mybatis-plus:
    configuration:
      auto-mapping-behavior: full
  ```

### autoMappingUnknownColumnBehavior

- **类型**：`AutoMappingUnknownColumnBehavior`
- **默认值**：`NONE`

- 说明：MyBatis 自动映射时未知列或未知属性处理策略，通过该配置可指定 MyBatis 在自动映射过程中遇到未知列或者未知属性时如何处理。

  - AutoMappingUnknownColumnBehavior.NONE：不做任何处理 (默认值)

  - AutoMappingUnknownColumnBehavior.WARNING：以日志的形式打印相关警告信息

  - AutoMappingUnknownColumnBehavior.FAILING：当作映射失败处理，并抛出异常和详细信息

- **配置示例**：

  ```yaml
  mybatis-plus:
    configuration:
      auto-mapping-unknown-column-behavior: warning
  ```

## 4.4 GlobalConfig

- `GlobalConfig` 是 MyBatis-Plus 提供的全局策略配置，它允许开发者对 MyBatis-Plus 的行为进行全局性的定制。

### banner

- **类型**：`boolean`
- **默认值**：`true`

- 说明：控制是否在控制台打印 MyBatis-Plus 的 LOGO。

- **配置示例**：

  ```yaml
  mybatis-plus:
    global-config:
      banner: false
  ```

### dbConfig

- **类型**：`com.baomidou.mybatisplus.core.config.GlobalConfig$DbConfig`
- **默认值**：`null`

- 说明：MyBatis-Plus 全局策略中的 DB 策略配置，具体请查看下一节。

- **配置示例**：

  ```yaml
  mybatis-plus:
    global-config:
      db-config:
        table-prefix: tbl_
        id-type: ASSIGN_ID
  ```

## 4.5 DbConfig

### idType

- 类型：`com.baomidou.mybatisplus.annotation.IdType`
- 默认值：`ASSIGN_ID`

- 说明：全局默认主键类型。

  - `IdType.AUTO`：使用数据库自增 ID 作为主键。

  - `IdType.NONE`：无特定生成策略，如果全局配置中有 IdType 相关的配置，则会跟随全局配置。

  - `IdType.INPUT`：在插入数据前，由用户自行设置主键值。

  - `IdType.ASSIGN_ID`：自动分配 `ID`，适用于 `Long`、`Integer`、`String` 类型的主键。默认使用雪花算法通过 `IdentifierGenerator` 的 `nextId` 实现。@since 3.3.0

  - `IdType.ASSIGN_UUID`：自动分配 `UUID`，适用于 `String` 类型的主键。默认实现为 `IdentifierGenerator` 的 `nextUUID` 方法。@since 3.3.0

- **配置示例**：

  ```yaml
  mybatis-plus:
    global-config:
      db-config:
        id-type: ASSIGN_ID
  ```

### tablePrefix

- 类型：`String`
- 默认值：`null`

- 说明：全局配置表名前缀

- **配置示例**：

  ```yaml
  mybatis-plus:
    global-config:
      db-config:
        table-prefix: tbl_
  ```

### schema

- 类型：`String`
- 默认值：`null`

- 说明：指定数据库的 Schema 名称，通常不用设置。

- **配置示例**：

  ```yaml
  mybatis-plus:
    global-config:
      db-config:
        schema: my_schema
  ```

### logicDeleteField

- 类型：`String`
- 默认值：`null`

- 说明：全局的 Entity 逻辑删除字段属性名，仅在逻辑删除功能打开时有效。

- **配置示例**：

  ```yaml
  mybatis-plus:
    global-config:
      db-config:
        logic-delete-field: deleted
  ```

### logicDeleteValue

- 类型：`String`
- 默认值：`1`

- 说明：逻辑已删除值，仅在逻辑删除功能打开时有效。

- **配置示例**：

  ```yaml
  mybatis-plus:
    global-config:
      db-config:
        logic-delete-value: true
  ```

### logicNotDeleteValue

- 类型：`String`
- 默认值：`0`

- 说明：逻辑未删除值，仅在逻辑删除功能打开时有效。

- **配置示例**：

  ```yaml
  mybatis-plus:
    global-config:
      db-config:
        logic-not-delete-value: false
  ```

# 5 前端工程搭建

- 

## 5.1 前端项目搭建

- 打开vscode，打开控制台，在指定目录下执行下面命令：

  ```cmd
  npm create vite@latest
  ```

![image-20241229201059329](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229201059329.png)

- 输入项目名称后回车，选择`vue`

![image-20241229201142073](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229201142073.png)

- 然后选择`JavaScripts`

![image-20241229201214296](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229201214296.png)

- 进入到该目录下，即可看见初始化的项目

![image-20241229201418441](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229201418441.png)

- 执行如下命令：

  ```cmd
  npm install
  ```

- 由于前端项目还用到了`axios`（ajax异步请求封装技术实现前后端数据交互）、`pinia`(通过状态管理实现组件数据传递)、`router`（通过路由实现页面切换）和`elememt-plu`（基于 Vue 3 的开源 UI 组件库）因此还需要安装下面四个包：

  ```cmd
  npm install axios vue-router pinia element-plus
  ```

- 毛毛张搭建好的前端完整的文件目录如下：

![QQ_1736348802727](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736348802727.png)

## 5.2 修改配置文件vite.config.js

```javascript
// vite.config.js

// 从 Vite 中导入 defineConfig 工具，用于类型提示和配置验证
import { defineConfig } from 'vite';

// 从 Vite 插件库中导入 Vue 插件，以支持 Vue 单文件组件（.vue 文件）
import vue from '@vitejs/plugin-vue';

// 导出 Vite 的配置，使用 defineConfig 包装配置对象
export default defineConfig(() => {
  return {
    // 配置 Vite 使用的插件
    plugins: [
      vue()  // 启用 Vue 插件，允许 Vite 识别和处理 .vue 文件
    ],

    // 配置开发服务器相关设置
    server: {
      port: 8081,  // 设置前端开发服务器的端口号为 8081
      open: true,  // 启动开发服务器后，自动在默认浏览器中打开应用

      // 配置开发服务器的代理，用于解决前后端分离时的跨域问题
      proxy: {
        '/api': {  // 代理匹配以 /api 开头的所有请求路径
          target: 'http://localhost:8080/',  // 代理目标地址，即后端服务器的地址
          changeOrigin: true,  // 改变请求头中的 Origin 字段为目标地址，避免因跨域而导致的问题
          
          // 重写请求路径，将 /api 前缀替换为空字符串
          // 例如，/api/user/queryAllUserInfo 会被重写为 /user/queryAllUserInfo
          rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    }
  };
});

```

## 3.3 引入插件main.js

- 文件路径：`src/main.js`

```javascript
import { createApp } from 'vue';// 引入 Vue 的 createApp 方法，用于创建应用实例
import App from './App.vue';// 引入根组件
import router from './router/router';// 引入路由配置
import pinia from './store/pinia';// 引入 Pinia 状态管理
import ElementPlus from 'element-plus';//引入element-plus UI组件库
//导入element-plus相关内容
import 'element-plus/dist/index.css'

// 创建 Vue 应用实例，并通过链式调用注册插件和功能
export const app = createApp(App)
                    .use(router) // 注册路由
                    .use(pinia) // 注册 Pinia 状态管理
                    .use(ElementPlus)
app.mount('#app'); // 挂载到 DOM 中的 #app 节点
```

## 5.4 修改主页App.vue

- 文件路径：`src/App.vue`

```vue
<script setup>


</script>

<template>
  <div>
    <!-- 用于路由切换视图-->
    <router-view></router-view>
  </div>
  
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
```

## 5.5 分页展示文章信息

- 文件路径：`src/components/showAllArticleInfo.vue`

```vue
<template>
  <div class="user-info">
    <h3 class="section-title">方式1</h3>

    <!-- 数据表格 -->
    <el-table :data="articleStoreByPagination.articles" border stripe class="article-table">
      <el-table-column prop="id" label="文章ID" width="180" />
      <el-table-column prop="articleTitle" label="文章标题" width="250" />
      <el-table-column prop="category" label="类别" width="180" />
      <el-table-column prop="articleAbstract" label="摘要" />
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        :current-page="articleStoreByPagination.currentPage"
        :page-size="articleStoreByPagination.pageSize"
        :total="articleStoreByPagination.total"
        layout="prev, pager, next"
        @current-change="loadPageByPagination"
        background
        size="small"
        class="custom-pagination mt-4"
    />

    <h3 class="section-title">方式2</h3>

    <!-- 数据表格 -->
    <el-table :data="articleStoreByParam.articles" border stripe class="article-table">
      <el-table-column prop="id" label="文章ID" width="180" />
      <el-table-column prop="articleTitle" label="文章标题" width="250" />
      <el-table-column prop="category" label="类别" width="180" />
      <el-table-column prop="articleAbstract" label="摘要" />
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        :current-page="articleStoreByParam.currentPage"
        :page-size="articleStoreByParam.pageSize"
        :total="articleStoreByParam.total"
        layout="prev, pager, next"
        @current-change="loadPageByParam"
        background
        size="small"
        class="custom-pagination mt-4"
    />

    <h3 class="section-title">方式3</h3>

    <!-- 数据表格 -->
    <el-table :data="articleStoreBySelf.articles" border stripe class="article-table">
      <el-table-column prop="id" label="文章ID" width="180" />
      <el-table-column prop="articleTitle" label="文章标题" width="250" />
      <el-table-column prop="category" label="类别" width="180" />
      <el-table-column prop="articleAbstract" label="摘要" />
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        :current-page="articleStoreBySelf.currentPage"
        :page-size="articleStoreBySelf.pageSize"
        :total="articleStoreBySelf.total"
        layout="prev, pager, next"
        @current-change="loadPageBySelf"
        background
        size="small"
        class="custom-pagination mt-4"
    />

    <h3 class="section-title">方式4</h3>

    <!-- 数据表格 -->
    <el-table :data="articleStoreByPageHelper.articles" border stripe class="article-table">
      <el-table-column prop="id" label="文章ID" width="180" />
      <el-table-column prop="articleTitle" label="文章标题" width="250" />
      <el-table-column prop="category" label="类别" width="180" />
      <el-table-column prop="articleAbstract" label="摘要" />
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        :current-page="articleStoreByPageHelper.currentPage"
        :page-size="articleStoreByPageHelper.pageSize"
        :total="articleStoreByPageHelper.total"
        layout="prev, pager, next"
        @current-change="loadPageByPageHelper"
        background
        size="small"
        class="custom-pagination mt-4"
    />

  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useArticleStoreByPagination,useArticleStoreByParam,useArticleStoreBySelf, useArticleStoreByPageHelper } from "../store/articleStore";

// 使用 Pinia store
const articleStoreByPagination= useArticleStoreByPagination();
const articleStoreByParam = useArticleStoreByParam();
const articleStoreBySelf = useArticleStoreBySelf();
const articleStoreByPageHelper = useArticleStoreByPageHelper();

// 加载指定页的数据
const loadPageByPagination = (page) => {
  articleStoreByPagination.fetchArticleInfo(page);  // 更新当前页并获取数据
};

// 加载指定页的数据
const loadPageByParam = (page) => {
  articleStoreByParam.fetchArticleInfo(page);  // 更新当前页并获取数据
};

// 加载指定页的数据
const loadPageBySelf = (page) => {
  articleStoreBySelf.fetchArticleInfo(page);  // 更新当前页并获取数据
};

// 加载指定页的数据
const loadPageByPageHelper = (page) => {
  articleStoreByPageHelper.fetchArticleInfo(page);  // 更新当前页并获取数据
};



// 在组件挂载时获取第一页数据
onMounted(() => {
  loadPageByPagination();  // 初始加载第一页数据
  loadPageByParam(1);  // 初始加载第一页数据
  loadPageBySelf(1);  // 初始加载第一页数据
  loadPageByPageHelper(1);  // 初始加载第一页数据
});
</script>

<style scoped>
.user-info {
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  max-width: 1200px;
  margin: 40px auto;
}

.section-title {
  text-align: center;
  font-size: 24px;
  color: #333333;
  margin-bottom: 20px;
  font-weight: bold;
}

.article-table {
  width: 100%;
  margin-bottom: 20px;
}

.article-table th {
  background-color: #f5f7fa;
  color: #333333;
  font-weight: 500;
  text-align: center;
}

.article-table td {
  text-align: center;
  padding: 12px 8px; /* 增加内边距以提升可读性 */
}

.article-table tr:hover {
  background-color: #f1f1f1; /* 添加悬停效果 */
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 10px 0;
}

.custom-pagination {
  /* 自定义分页样式 */
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.custom-pagination .el-pager li.active {
  background-color: #409EFF; /* 激活页码背景色 */
}

.custom-pagination .el-pagination__prev,
.custom-pagination .el-pagination__next,
.custom-pagination .el-pagination__jump,
.custom-pagination .el-pagination__total {
  color: #606266;
}

.custom-pagination .el-pagination__jump input {
  width: 60px;
  text-align: center;
}

.mt-4 {
  margin-top: 1rem; /* 添加额外的顶部间距 */
}

@media (max-width: 768px) {
  .user-info {
    padding: 15px;
    margin: 20px auto;
  }

  .section-title {
    font-size: 20px;
    margin-bottom: 15px;
  }

  .article-table th,
  .article-table td {
    padding: 10px 5px;
    font-size: 14px;
  }

  .pagination-container {
    padding: 8px 0;
  }
}
</style>
```

## 5.6 数据传递与请求发送

- 文件路径：`/src/store/pinia.js`

```javascript
// 从 Pinia 中导入 createPinia 函数，用于创建 Pinia 实例
import { createPinia } from 'pinia';

// 使用 createPinia 函数创建一个 Pinia 实例
// Pinia 是 Vue 3 官方推荐的状态管理工具，用于管理应用中的全局状态
let pinia = createPinia();

// 导出创建好的 Pinia 实例
// 在 main.js 中引入并注册到 Vue 应用中，使得所有组件都可以使用 Pinia 管理状态
export default pinia;

```

- 文件路径：`/src/store/articleStore.js`

```javascript
import { defineStore } from 'pinia';
import { fetchArticleInfoByPagination,fetchArticleInfoByParam,fetchArticleInfoBySelf,fetchArticleInfoByPageHelper } from "../api/api.js";

export const useArticleStoreByPagination = defineStore('articleStoreByPagination', {
    state: () => ({
        articles: [], // 存储文章列表
        currentPage: 1, // 当前页
        pageSize: 10,   // 每页显示条数
        total: 0, // 总文章数
    }),
    actions: {
        async fetchArticleInfo() {
            try {

                // 发起请求，传递当前页和每页条数
                const response = await fetchArticleInfoByPagination();

                // 检查响应是否成功
                if (response.data.flag && response.data.code === 20000) {
                    const { records, total, current} = response.data.data;

                    // 更新 Store 状态
                    this.articles = records;         // 当前页文章数据
                    this.total = total;              // 总记录数
                    this.currentPage = current;      // 当前页码
                } else {
                    console.error('获取文章失败:', response.data.message);
                }
            } catch (error) {
                console.error('请求失败:', error);
            }
        },
    },
});


export const useArticleStoreByParam = defineStore('articleStoreByParam', {
    state: () => ({
        articles: [], // 存储文章列表
        currentPage: 1, // 当前页
        pageSize: 3,   // 每页显示条数
        total: 0, // 总文章数
    }),
    actions: {
        async fetchArticleInfo(page) {
            try {
                // 设置当前页
                this.currentPage = page;

                // 发起请求，传递当前页和每页条数
                const response = await fetchArticleInfoByParam(page, this.pageSize);

                // 检查响应是否成功
                if (response.data.flag && response.data.code === 20000) {
                    const { records, total, current} = response.data.data;

                    // 更新 Store 状态
                    this.articles = records;         // 当前页文章数据
                    this.total = total;              // 总记录数
                    this.currentPage = current;      // 当前页码
                } else {
                    console.error('获取文章失败:', response.data.message);
                }
            } catch (error) {
                console.error('请求失败:', error);
            }
        },
    },
});


export const useArticleStoreBySelf = defineStore('articleStoreBySelf', {
    state: () => ({
        articles: [], // 存储文章列表
        currentPage: 1, // 当前页
        pageSize: 3,   // 每页显示条数
        total: 0, // 总文章数
    }),
    actions: {
        async fetchArticleInfo(page) {
            try {
                // 设置当前页
                this.currentPage = page;

                // 发起请求，传递当前页和每页条数
                const response = await fetchArticleInfoBySelf(page, this.pageSize);

                // 检查响应是否成功
                if (response.data.flag && response.data.code === 20000) {
                    const { records, total, current} = response.data.data;

                    // 更新 Store 状态
                    this.articles = records;         // 当前页文章数据
                    this.total = total;              // 总记录数
                    this.currentPage = current;      // 当前页码
                } else {
                    console.error('获取文章失败:', response.data.message);
                }
            } catch (error) {
                console.error('请求失败:', error);
            }
        },
    },
});


export const useArticleStoreByPageHelper = defineStore('articleStoreByPageHelper', {
    state: () => ({
        articles: [], // 存储文章列表
        currentPage: 1, // 当前页
        pageSize: 3,   // 每页显示条数
        total: 0, // 总文章数
    }),
    actions: {
        async fetchArticleInfo(page) {
            try {
                // 设置当前页
                this.currentPage = page;

                // 发起请求，传递当前页和每页条数
                const response = await fetchArticleInfoByPageHelper(page, this.pageSize);

                // 检查响应是否成功
                if (response.data.code === 20000) {
                    const { list, total, pageNum} = response.data.data;

                    // 更新 Store 状态
                    this.articles = list;         // 当前页文章数据
                    this.total = total;              // 总记录数
                    this.currentPage = pageNum;      // 当前页码
                } else {
                    console.error('获取文章失败:', response.data.message);
                }
            } catch (error) {
                console.error('请求失败:', error);
            }
        },
    },
});
```

- 由于请求的api比较多，毛毛张把请求的API专门抽取出来，放在`src/api/api.js`文件中：

```javascript
import axios from '../services/axios';  // 引入自定义的 axios 实例

/**
 * 方式1：请求分页文章信息
 */
export const fetchArticleInfoByPagination = () => axios.get('/article/queryAllArticleInfoByPagination');

/**
 * 方式2：请求分页文章信息
 */
export const fetchArticleInfoByParam = (page, pageSize) => {
    return axios.get('/article/queryAllArticleInfoByParam', {
        params: {
            current: page,
            size: pageSize
        }
    });
};

/**
 * 方式3：请求分页文章信息
 */
export const fetchArticleInfoBySelf = (page, pageSize) => {
    return axios.get('/article/queryAllArticleInfoBySelf', {
        params: {
            current: page,
            size: pageSize
        }
    });
};

/**
 * 方式4：请求分页文章信息
 */
export const fetchArticleInfoByPageHelper = (page, pageSize) => {
    return axios.get('/article/queryAllArticleInfoByPageHelper', {
        params: {
            current: page,
            size: pageSize
        }
    });
};
```

## 5.7 路由代码

- 文件路径：`/src/router/router.js`

```javascript
// 导入路由创建的相关方法
import { createRouter, createWebHashHistory,createWebHistory } from 'vue-router'



const routes = [
  {
    path: '/',
    redirect: '/article/queryAllArticleInfoByParam'  // 重定向到 /user/queryAllUserInfo 路由
  },
  {
    path: '/article/queryAllArticleInfoByParam',
    name: 'ShowAllArticleInfo',
    component: () => import('../components/ShowAllArticleInfo.vue') // 动态导入
  }
];


// 创建路由对象，声明路由规则
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 设置路由的全局前置守卫
router.beforeEach((to, from, next) => {
  /* 
  to 要去哪里
  from 从哪里来
  next 放行路由时需要调用的方法，不调用则不放行
  */
  console.log(to)
  next() // 直接放行所有路由
})

// 设置路由的全局后置守卫
router.afterEach((to, from) => {
  console.log(`从哪里来:${from.path}, 到哪里去:${to.path}`)
})

// 对外暴露路由对象
export default router
```

## 5.8 请求拦截处理

- 文件路径：`/src/services/axios.js`

```javascript
import axios from 'axios';// 导入 Axios 库

// 创建 Axios 实例
const instance = axios.create({
    // 基础 URL，所有请求都会在此基础上拼接路径
    // '/api' 是前端代理路径，实际会被 Vite 配置代理到后端地址
    baseURL: "/api",
    
    // 超时时间，单位是毫秒。如果请求超过这个时间没有响应，将抛出超时错误
    timeout: 10000,
});

// 添加请求拦截器,在请求发送之前做一些处理
instance.interceptors.request.use(
    // 成功拦截的回调函数
    config => {
        // 处理请求头，例如添加 Content-Type 或自定义头
        console.log("before request"); // 打印日志，便于调试
        config.headers.Accept = 'application/json, text/plain, text/html,*/*'; // 设置通用的 Accept 请求头
        return config; // 返回配置好的请求对象，继续发送请求
    },
    // 请求错误的回调函数
    error => {
        console.log("request error"); // 打印错误信息
        return Promise.reject(error); // 返回错误，阻止请求发送
    }
);

// 添加响应拦截器,在服务器响应后统一处理数据
instance.interceptors.response.use(
    // 处理成功响应的回调函数
    response => {
        console.log("after success response"); // 打印日志
        console.log("这是拦截器"); // 打印标志信息，便于调试
        console.log(response); // 打印完整的响应对象
        return response; // 返回响应数据，供后续逻辑使用
    },
    // 处理失败响应的回调函数
    error => {
        console.log("after fail response"); // 打印错误日志
        console.log(error); // 打印错误对象，便于调试
        return Promise.reject(error); // 返回错误，供调用者处理
    }
);

// 导出创建的 Axios 实例，便于在项目中复用
export default instance;
```

## 5.9 前端显示效果

![QQ_1736349796232](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736349796232.png)

# 6.源代码

- 源代码可用访问毛毛张的github仓库：
  - 前端代码：<https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mybatis-plus/springboot-mybatis-plus-demo-vue>
  - 后端代码：<https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mybatis-plus/springboot-mybatis-plus-demo>

# 参考文献

- <https://www.cnblogs.com/qwlscn/p/11490071.html>
- <https://blog.csdn.net/weixin_47316183/article/details/132044019>
- <https://blog.csdn.net/weixin_46146718/article/details/126962110>
- <https://juejin.cn/post/7303347466219946035>
- <https://www.cnblogs.com/7456Ll/p/17842820.html>





