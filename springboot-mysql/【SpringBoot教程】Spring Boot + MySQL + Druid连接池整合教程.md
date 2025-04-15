> **前面毛毛张介绍过`HikariCP`连接池，今天毛毛张来介绍一下`Druid`连接池，`SpringBoot 2.0`以上默认使用`HikariCP`数据源，但是也要学会使用Druid连接池，可以说`HikariCP`与`Driud`都是当前`JavaWeb`上最优秀的数据源**

[toc]



# 1 Druid连接池简介

- Druid 是阿里巴巴开源的一款高性能数据库连接池，集成了 C3P0、DBCP 等传统连接池的优点，并针对高并发和大规模生产环境进行了优化。它提供了高效的数据库连接管理，具备强大的监控和统计功能，能够实时监控 SQL 执行、连接池状态，并提供慢查询分析，帮助开发者优化数据库性能。Druid 具有灵活的配置选项，支持连接泄露检测、SQL 防火墙、黑白名单过滤等安全特性，确保数据库的稳定性与安全性。**相比于 Spring Boot 默认使用的 HikariCP，Druid 在可视化监控、扩展性和 SQL 分析方面更具优势，适用于对性能和安全要求较高的 Java 应用。**

- **特性和功能：**
    - 高性能：Druid连接池通过一些优化策略实现高性能的数据库连接获取和释放。其中包括使用预创建连接来减少连接获取的开销，以及通过连接池扩展机制来快速处理并发请求。此外，Druid还提供了连接的闲置检测和定时回收机制，以避免连接长时间占用资源。
    - 监控统计：Druid连接池内置了强大的监控统计功能，可以实时监控连接池的状态、活跃连接数、请求频率、SQL执行情况等。它提供了一个内置的Web界面，可以方便地查看连接池的监控数据，并进行性能分析和故障排查。
    - 防止泄露：Druid连接池可以检测和关闭泄露的连接，防止长时间占用数据库连接资源。它提供了一套完善的连接泄露检测和回收机制，以保证连接资源的有效利用。
    - 数据库访问优化：Druid连接池支持连接的预处理、批量更新等优化操作，可以提高数据库的访问效率。它还提供了SQL执行的慢查询日志功能，可以帮助开发人员找出慢查询语句，并进行性能优化。
    - 安全防护：Druid连接池提供了一些安全防护机制，如SQL防火墙、黑白名单过滤等。这些机制可以保护数据库免受恶意SQL注入等攻击。
    - 配置灵活：Druid连接池的配置选项非常灵活，可以根据应用程序的需求进行定制。您可以设置连接池大小、最大连接数、连接超时时间、验证方式等参数，以满足不同场景的需求。

- 更多的介绍可以参见官网：<https://github.com/alibaba/druid/>


# 2 入门使用教程

- 案例内容：基于`SpringBoot3.x`，使用`MyBatis`框架，结合`Druid`连接池，查询并展示数据库中的全部用户信息
- 项目版本依赖：
    - 后端：
        - SpringBoot：3.1.0
        - JDK：17
        - Mybatis：3.0.1
        - druid-spring-boot-3-starter：1.2.24
    - 前端
        - vite：6.0.5
        - vue：3.5.13
        - vue-router：4.5.0
        - pinia：2.3.0
        - axios：1.7.9

## 2.1 后端代码

### 2.1.1 创建项目

- 如何快速创建一个`SpringBoot`新项目可以参见毛毛张的这篇博客：[【SpringBoot教程】IDEA快速搭建正确的SpringBoot版本和Java版本的项目](https://blog.csdn.net/weixin_48235955/article/details/144807998)
- 下面是毛毛张的完整后端代码文件结构如下图：

![image-20250127001559008](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250127001559008.png)

### 2.1.2 导入依赖

- 更多的关于依赖的介绍可以看第三节，毛毛张在这里只介绍关于这个入门项目的依赖的内容

- **毛毛张针对`SpringBoot3.1.0`选择的`Druid`适配的依赖：`druid-spring-boot-3-starter`：1.2.24**

- **需要注意的是，在使用Druid作为连接池的同时，还需要单独导入 MySQL 数据库驱动，通常通过引入 `mysql-connector-java` 依赖来实现**

- **毛毛张在这个任务中为了方便，使用了`Mybatis`框架，整个项目的`pom.xml`文件为：**

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <groupId>com.zzx</groupId>
        <artifactId>springboot3-druid-demo</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <name>springboot3-druid-demo</name>
        <description>springboot3-druid-demo</description>
    
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-parent</artifactId>
            <version>3.1.4</version>
        </parent>
    
        <properties>
            <java.version>17</java.version>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
            <spring-boot.version>3.1.4</spring-boot.version>
        </properties>
    
    
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
    
    
            <dependency>
                <groupId>com.mysql</groupId>
                <artifactId>mysql-connector-j</artifactId>
                <version>8.0.33</version>
                <scope>runtime</scope>
            </dependency>
    
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <optional>true</optional>
            </dependency>
    
            <!-- druid启动器的依赖  -->
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-3-starter</artifactId>
                <version>1.2.24</version>
            </dependency>
    
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>3.0.1</version>
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
                        <source>17</source>
                        <target>17</target>
                        <encoding>UTF-8</encoding>
                    </configuration>
                </plugin>
    
                <!--    SpringBoot应用打包插件-->
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                    <configuration>
                        <mainClass>com.zzx.Springboot3DruidDemoApplication</mainClass>
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

### 2.1.3 编写配置文件

- 下面是整个项目的配置文件`application.yaml`，关键部分已被注释出来了，**更多详细的解释可以参见第三节的内容**

    ```yaml
    server:
      port: 8080  # 服务端口号
    
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/springboot?useSSL=false&autoReconnect=true&characterEncoding=utf8&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8&allowMultiQueries=true  # 数据库地址
        driver-class-name: com.mysql.cj.jdbc.Driver  # MySQL 8.0 及以上需要 cj，5.7 以下可去掉 cj
        username: root  # 数据库用户名
        password: abc123  # 数据库密码
        type: com.alibaba.druid.pool.DruidDataSource  # 使用 Druid 数据源
        druid:
          # 连接池基本配置
          initial-size: 5  # 初始化连接数
          min-idle: 10  # 最小空闲连接数
          max-active: 20  # 最大连接数
          max-wait: 60000  # 最大等待时间（毫秒）
          time-between-eviction-runs-millis: 60000  # 多少毫秒执行一次空闲连接回收
          min-evictable-idle-time-millis: 600000  # 连接最小生存时间（毫秒）
          max-evictable-idle-time-millis: 1800000  # 连接最大生存时间（毫秒）
    
          # 连接校验配置
          validation-query: SELECT 1  # 用于测试连接是否可用的 SQL 语句
          validation-query-timeout: 2000  # SQL 校验超时时间（毫秒）
          test-on-borrow: false  # 获取连接时是否检测（影响性能，建议关闭）
          test-on-return: false  # 归还连接时是否检测（影响性能，建议关闭）
          test-while-idle: true  # 空闲时是否检测（推荐开启）
    
          # 连接池优化配置
          phy-max-use-count: 1000  # 每个连接的最大使用次数
          pool-prepared-statements: false  # 是否开启 PSCache
          max-open-prepared-statements: 50  # PSCache 允许的最大预编译 SQL 数
    
          # Druid 监控配置
          filters: stat,wall,slf4j  # 启用监控统计拦截的过滤器（SQL监控、防火墙、日志）
    
          # 监控页面配置
          stat-view-servlet:
            enabled: true  # 启用内置监控页面
            url-pattern: /druid/*  # 监控页面路径
            login-username: admin  # 访问监控页面的用户名
            login-password: admin  # 访问监控页面的密码
            reset-enable: false  # 是否允许重置统计数据
            allow: 127.0.0.1  # 允许访问的 IP 白名单
            deny: 192.168.0.1  # 禁止访问的 IP 黑名单
    
          # Web 监控配置
          web-stat-filter:
            enabled: true  # 开启 Web-JDBC 关联监控
            url-pattern: /*  # 监控所有请求
            exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"  # 排除静态资源和监控路径
            session-stat-enable: true  # 启用 Session 统计
            session-stat-max-count: 1000  # Session 最大数量
    
          # SQL 慢查询日志配置
          filter:
            stat:
              # 是否开启 FilterStat，默认true
              enabled: true
              # 是否开启 慢SQL 记录，默认false
              log-slow-sql: true
              # 慢 SQL 的标准，默认 3000，单位：毫秒
              slow-sql-millis: 5000
              # 合并多个连接池的监控数据，默认false
              merge-sql: false
    
            # 日志配置，采用 SLF4J
            slf4j:
              enabled: true  # 启用日志
              statement-log-error-enabled: true  # 开启 SQL 语句错误日志
              statement-create-after-log-enabled: false
              statement-close-after-log-enabled: false
              result-set-open-after-log-enabled: false
              result-set-close-after-log-enabled: false
    
    logging:
      level:
        org.springframework.jdbc.core.JdbcTemplate: DEBUG  # 开启 SQL 执行日志
    
    mybatis:
      # Mapper 文件的位置
      mapper-locations: classpath:mapper/*Mapper.xml
      # 实体类的包路径
      type-aliases-package: com.zzx.entity
      configuration:
        # 自动下划线转驼峰
        map-underscore-to-camel-case: true
    ```

### 2.1.4 初始化数据库并创建对应实体类

- 创建名为`springboot`的数据库，并创建`user_info`表：

    ```mysql
    -- 创建数据库
    CREATE DATABASE IF NOT EXISTS springboot CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
    USE springboot;
    
    -- 删除已存在的 user_info 表（如果存在）
    DROP TABLE IF EXISTS `user_info`;
    
    -- 创建 user_info 表
    CREATE TABLE `user_info`  (
      `id` INT NOT NULL AUTO_INCREMENT,
      `user_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
      `pass_word` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
      `age` INT NULL DEFAULT NULL,
      `gender` VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
      PRIMARY KEY (`id`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 3 
      DEFAULT CHARSET = utf8mb4 
      COLLATE = utf8mb4_0900_ai_ci 
      ROW_FORMAT = Dynamic;
    
    -- 插入示例数据
    INSERT INTO `user_info` (`id`, `user_name`, `pass_word`, `age`, `gender`) VALUES 
    (1, 'sam', 'password123', 32, 'M'),
    (2, 'hah', 'password456', 10, 'F');
    
    -- 确保外键检查被重新启用
    SET FOREIGN_KEY_CHECKS = 1;
    ```

- 对应实体类`User`：

    ```java
    package com.zzx.entity;
    
    
    import lombok.Data;
    
    @Data
    public class User {
        private Integer id;        // 对应数据库中的 `u_id`
        private String userName;   // 对应数据库中的 `u_username`
        private String passWord;   // 对应数据库中的 `u_password`
        private Integer age;       // 对应数据库中的 `u_age`
        private String gender;     // 对应数据库中的 `u_gender`
    }
    ```

### 2.1.5 编写响应封装与前端展示VO

- 定义统一的响应封装类 ResVo：

    ```java
    package com.zzx.reponse;
    
    public class ResVo<T> {
        private Integer code;      // 状态码
        private String message;    // 消息内容
        private T content;         // 内容，可以是任何类型的数据
    
        // 构造方法
        public ResVo(Integer code, String message, T content) {
            this.code = code;
            this.message = message;
            this.content = content;
        }
    
        // 成功的返回，通常是常用的，内容可以为空
        public static <T> ResVo<T> success(T content) {
            return new ResVo<>(200, "成功", content);
        }
    
        // 失败的返回，通常返回错误信息
        public static <T> ResVo<T> error(Integer code, String message) {
            return new ResVo<>(code, message, null);
        }
    
        // Getters and Setters
        public Integer getCode() {
            return code;
        }
    
        public void setCode(Integer code) {
            this.code = code;
        }
    
        public String getMessage() {
            return message;
        }
    
        public void setMessage(String message) {
            this.message = message;
        }
    
        public T getContent() {
            return content;
        }
    
        public void setContent(T content) {
            this.content = content;
        }
    }
    
    ```

- 前端展示对象`UserInfoVo`，在查询用户信息时通过是把查询到的结果封装在一个实体类中，但是返回给前端的信息不一定是用户的全部信息，例如用户的密码就不能直接返回给前端，或者不要返回，所以毛毛张设计了一个这个类：

    ```java
    package com.zzx.model.vo;
    
    import lombok.Data;
    
    @Data
    public class UserInfoVo {
        //返回给前端展示的数据，密码不能展示，性别转化成数字
        private Integer id;
        private String username;
        private Integer age;
        private Integer gender;
    }
    
    ```

- 为了节省传输的字符，毛毛张将用户的信息对应的内容转化成数字再返回给前端，因此设计了一个枚举类型`UserSexEnum`：

    ```java
    package com.zzx.enums;
    
    public enum UserSexEnum {
        M(1, "男"), // M对应男，值为 1
        F(0, "女"); // F对应女，值为 0
    
        private int code;         // 对应的数字值（1 或 0）
        private String description; // 性别描述（男 或 女）
    
        // 构造方法，用于设置枚举常量的描述和对应的代码
        UserSexEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }
    
        // 获取性别描述
        public String getDescription() {
            return description;
        }
    
        // 获取对应的数字代码
        public int getCode() {
            return code;
        }
    
        // 根据传入的字符串 'M' 或 'F' 获取对应的性别枚举
        public static UserSexEnum fromString(String sexStr) {
            for (UserSexEnum sex : UserSexEnum.values()) {
                if (sex.name().equalsIgnoreCase(sexStr)) {
                    return sex;
                }
            }
            throw new IllegalArgumentException("无效的性别字符串: " + sexStr);
        }
    
        // 根据 'M' 或 'F' 获取对应的数字代码
        public static int getCodeByString(String sexStr) {
            UserSexEnum sex = fromString(sexStr);
            return sex.getCode();
        }
    }
    ```

- 由于`User`类和`UserInfoVo`不是完全一一对应的，所以为了数据转换的方便，毛毛张再这里专门写了一个转换类`UserConverter`：

    ```java
    package com.zzx.converter;
    
    import com.zzx.entity.User;
    import com.zzx.enums.UserSexEnum;
    import com.zzx.model.vo.UserInfoVo;
    
    import java.util.List;
    import java.util.stream.Collectors;
    
    public class UserConverter{
        // 单个转换
        public static UserInfoVo toUserInfoDTO(User user) {
            UserInfoVo userInfoVo = new UserInfoVo();
            userInfoVo.setId(user.getId());
            userInfoVo.setUsername(user.getUserName());
            userInfoVo.setAge(user.getAge());
            userInfoVo.setGender(UserSexEnum.getCodeByString(user.getGender()));
            return userInfoVo;
        }
    
        // 批量转换
        public static List<UserInfoVo> toUserInfoDTOList(List<User> users) {
            // 使用 Java 8 的 stream API 进行批量转换
            return users.stream()
                    .map(UserConverter::toUserInfoDTO)  // 对每个 User 对象进行转换
                    .collect(Collectors.toList());     // 收集成 List<UserInfoDTO>
        }
    }
    ```

### 2.1.6 编写业务逻辑

- controller层`UserController`：

    ```java
    package com.zzx.controller;
    
    import com.zzx.converter.UserConverter;
    import com.zzx.entity.User;
    import com.zzx.model.vo.UserInfoVo;
    import com.zzx.reponse.ResVo;
    import com.zzx.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import java.util.List;
    
    
    @RestController
    @RequestMapping("user")
    public class UserController {
        @Autowired
        private UserService userService;
    
        @GetMapping("queryAllUserInfo")  // 使用 GET 请求
        public ResVo<List<UserInfoVo>> queryAllUserInfo() {
            List<User> userInfoList = userService.queryAllUserInfo();
            return ResVo.success(UserConverter.toUserInfoDTOList(userInfoList));
        }
    }
    ```

- `service`层接口`UserService`：

    ```java
    package com.zzx.service;
    
    import com.zzx.entity.User;
    
    import java.util.List;
    
    
    public interface UserService {
    
        List<User> queryAllUserInfo();
    }
    ```

- 服务层实现类`UserServiceImpl`：

    ```java
    package com.zzx.service.impl;
    
    
    import com.zzx.entity.User;
    import com.zzx.mapper.UserMapper;
    import com.zzx.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    
    import java.util.List;
    
    @Service
    public class UserServiceImpl implements UserService {
        @Autowired
        private UserMapper userMapper;
    
        @Override
        public List<User> queryAllUserInfo() {
            return userMapper.queryAllUserInfo();
        }
    }
    ```

- mapper层`UserMapper`：

    ```java
    package com.zzx.mapper;
    
    
    import org.apache.ibatis.annotations.Mapper;
    import com.zzx.entity.User;
    
    import java.util.List;
    
    @Mapper
    public interface UserMapper {
        List<User> queryAllUserInfo();
    }
    ```

- Mapper 层 SQL 映射配置文件`UserMapper.xml`：

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="com.zzx.mapper.UserMapper">
        <!-- 声明标签写sql语句  crud  select  insert update  delete
              每个标签对应接口的一个方法！  方法的一个实现！
              注意：mapper接口不能重载！！！ 因为mapper.xml无法识别！ 根据方法名识别！
         -->
        <!-- 查询所有用户 -->
        <select id="queryAllUserInfo" resultType="com.zzx.entity.User">
            SELECT id, user_name, pass_word, age, gender FROM user_info
        </select>
    
    </mapper>
    ```

### 2.1.7 跨域资源共享

- 由于毛毛张这个代码还有前端代码，涉及到和前端交互，还需要做一个跨域资源共享的配置，毛毛张没有使用`@CrossOrigin`，而是通过拦截器的方式实现的：

    ```java
    package com.zzx.interceptor;
    
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

- 配置类：

    ```java
    package com.zzx.config;
    
    import com.zzx.interceptor.CorsInterceptor;
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

### 2.1.8 启动类

- 启动类`SpringbootHicariCpDemoApplication`：

    ```java
    package com.zzx;
    
    import org.mybatis.spring.annotation.MapperScan;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    
    @SpringBootApplication
    @MapperScan("com.zzx.mapper")
    public class SpringbootHicariCpDemoApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(SpringbootHicariCpDemoApplication.class, args);
        }
    
    }
    ```

### 2.1.9 测试

- 启动后端程序，可以在浏览器中输入`http://localhost:8080/user/queryAllUserInfo`，返回结果如下则表示后端代码正确无误：

![image-20250125120253993](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250125120253993.png)

- 同时也能看见控制台输出使用的连接池为`Druid`连接池：

![image-20250126180009473](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250126180009473.png)

- 完整的后端代码已上传至毛毛张`Github`仓库：<hhttps://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mysql/springboot3-druid-demo>

### 2.1.10 Druid后台监控查看

- 上一个步骤执行完了`http://localhost:8080/user/queryAllUserInfo`请求，我们可以通过`http://127.0.01:8080/druid/login.html`来查看`Druid`的`SQL`监控页面，查看做了哪些SQL语句。访问该页面首先需要登陆，如下图所示。

![image-20250127000826837](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250127000826837.png)

- 登陆密码可以查看配置文件，毛毛张在上面的配置文件中用户名和密码全是`admin`

![image-20250127000929459](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250127000929459.png)



- 登陆成功之后即可查看`SQL`监控：

![image-20250127001054572](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250127001054572.png)

## 2.2 前端代码

- 前端代码和之前毛毛张介绍的`Mybatis`教程的代码是一样的，毛毛张在这里不做过多的介绍了，感兴趣的可以查看毛毛张的相关博客：[【SpringBoot教程】SpringBoot整合Mybatis - 前后端分离项目 - vue3](https://blog.csdn.net/weixin_48235955/article/details/144818393)
- 完整前端代码已上传至毛毛张`Github`仓库：<https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mysql/springboot-mysql-demo-vue>

## 2.3 可能会遇到的问题

- 毛毛张在之前的项目使用的是`SpringBoot2.x`版本，本项目使用的是`3.x`版本的，遇到了一些问题，在这里也记录一下

### 2.3.1 MySQL 连接器版本问题

- 在 Spring Boot 2.7.6 版本中，MySQL 连接器的版本可以自动推断，但在 Spring Boot 3.x 中，MySQL 连接器的版本需要显式指定，否则会导致构建失败。解决办法是在 `pom.xml` 中显式指定 `mysql-connector-java` 的版本，例如：

    ```xml
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.30</version>  <!-- 替换为你需要的版本 -->
        <scope>runtime</scope>
    </dependency>
    ```

### 2.3.2 javax.annotation.Resource找不到包的问题

- 在``SpringBoot2.x`中毛毛张使用的是`@Resource`注解，但是报错： javax.annotation.Resource找不到包

- 原因：Spring Boot 3.x 版本基于 Spring 6，而 `javax.annotation` 包已不再包含在 Spring 中。此时编译时会出现 `程序包javax.annotation不存在` 的错误。解决办法有两个选择：

    - 方案 1：显式添加`javax.annotation-api`依赖：

        ```xml
        <dependency>
            <groupId>javax.annotation</groupId>
            <artifactId>javax.annotation-api</artifactId>
            <version>1.3.2</version>
        </dependency>
        ```

    - 方案 2：直接使用 Spring 推荐的`@Autowired`注解替代`@Resource`，因为`@Autowired`是`Spring`的默认注解，且具有更好的兼容性

### 2.3.3 监控页面报`404`

- **启动服务后，打开Druid监控地址`http://127.0.0.1:8080/druid/`**，出现如下图所示错误

![image-20250126214150008](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250126214150008.png)

- **错误原因：主要是因为`Springboot3.x`使用`Jakarta EE 10`，从 Java EE 变更为 Jakarta EE，包名以 javax开头的需要相应地变更为jakarta，如`javax.servlet.*`，修改为`jakarta.servlet.`，目前Druid提供的`druid-spring-boot-3-starter`中`1.2.23`之前的依赖仍使用javax的旧包，直到`1.2.24`才解决，因此毛毛张在上面项目中使用的就是`1.2.24`版本的依赖**

### 2.3.4 之前SpringBoot3不兼容Druid - 可忽略

- 在 **Spring Boot 3** 发布之前，`Druid` 数据源与 Spring Boot 3 的兼容性较差，特别是在自动配置方面。`druid-spring-boot-starter` 版本（如 1.2.20）在与 Spring Boot 3 集成时，缺少自动装配配置类，导致无法正常启动，通常会出现 `ClassNotFoundException` 错误。

    ```java
    Caused by: java.lang.ClassNotFoundException: com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure
    ```

- 解决方案：开发者需要手动在 `resources` 目录下创建 `META-INF/spring/` 文件夹，并添加一个名为 `org.springframework.boot.autoconfigure.AutoConfiguration.imports` 的文件，内容为 `com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure`，这样 Spring Boot 就能加载并配置 Druid 数据源。

![image](https://img2024.cnblogs.com/blog/3382744/202403/3382744-20240305175937010-1908040584.png)

```avrasm
com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure
```

- **当前，使用兼容 Spring Boot 3 的 `druid-spring-boot-3-starter` 版本（如 1.2.24）已经解决了这个问题，不再需要手动配置自动装配类，因此这个问题不会再出现**

## 2.4 总结

- 毛毛张关于这个入门案例编写的了两个版本的后端代码，已经上传至`Github`：
    - `SpringBoot2.x`：<https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mysql/springboot-druid-demo>
    - `SpringBoot3.x`：<https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mysql/springboot3-druid-demo>
- 前端代码：<https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mysql/springboot-mysql-demo-vue>

# 3 配置详解

## 3.1 导入依赖

- **不同于`HikariCP`连接池，`Druid`连接池需要显示的导入相关依赖**

### 3.1.1 Druid三种依赖

- 当我们使用`Maven Search`插件搜索`Druid`连接池的时候发现阿里巴巴提供了三种依赖，如下图

![image-20250126205641714](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250126205641714.png)

- **这三种依赖毛毛张简单介绍一下：**
    - **druid**：Druid 是一个高效、稳定、功能丰富的数据库连接池，广泛应用于 Java 项目中。它具备出色的性能和监控功能，支持 SQL 执行日志、连接池监控、SQL 防火墙等高级功能，尤其在处理大规模高并发数据库请求时表现优异。在 Spring Boot 项目中使用 Druid 时，通常需要手动配置连接池属性，手动注入 `DruidDataSource`，并且需要开发者自己编写相关的配置类。这种方式适用于不使用 Spring Boot 或对连接池有高度自定义需求的项目。
    - **druid-spring-boot-starter**：`druid-spring-boot-starter` 是为 Spring Boot 2.x 项目提供的一款自动化配置的启动器，它简化了 Druid 集成过程，减少了手动配置的步骤。通过该 Starter，开发者只需在 `application.properties` 或 `application.yml` 中配置数据源的相关信息，Spring Boot 会自动为你配置并初始化 Druid 连接池。它不仅支持 Druid 数据源的连接池功能，还提供了 Druid 的监控、日志、慢查询等功能的自动配置，非常适合大多数 Spring Boot 2.x 项目，推荐使用此依赖来整合 Druid，避免手动配置的繁琐。
    - **druid-spring-boot-3-starter**：是专门为 Spring Boot 3.x 版本设计的自动化配置依赖。由于 Spring Boot 3.x 引入了一些不兼容的 API 变更，因此 `druid-spring-boot-3-starter` 对 Druid 的集成进行了适配，以确保其能够与 Spring Boot 3.x 的特性兼容。与 `druid-spring-boot-starter` 类似，开发者只需要在配置文件中提供数据库连接信息，Spring Boot 3.x 会自动配置 Druid 数据源以及其他相关功能，如监控、SQL 日志等。这款依赖是 Spring Boot 3.x 项目中使用 Druid 的推荐选择，简化了集成步骤，适合希望快速配置和使用 Druid 数据源的开发者。
- **总结：因此作为`SpringBoot`项目**
    - **Spring Boot 2.x** 项目使用 `druid-spring-boot-starter`
    - **Spring Boot 3.x** 项目使用 `druid-spring-boot-3-starter`
- <font color="red">**说在前面：毛毛张为什么这么强调版本的原因是因为，如果使用的版本不正确，不匹配，就不能使用Druid 提供的 SQL 监控、连接池监控、慢查询日志以及 Web 和 SQL 的监控页面等功能，如下图所示。因此版本的选择非常重要！**</font>

![image-20250126211129303](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250126211129303.png)

### 3.1.2 SpringBoot 2.x 依赖

- **下面是毛毛张针对`SpringBoot2.7.6`选择的四个适配的依赖：**

    - **`druid-spring-boot-starter`：**
        - 1.1.10
        - 1.1.21
        - 1.2.15
        - 1.2.18

- 依赖文件如下图所示：

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
        ........
    
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-parent</artifactId>
            <version>2.7.6</version>
        </parent>
    
    
        <dependencies>
           ......
    		
            <!-- 以下四个版本任选一个 -->
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-starter</artifactId>
                <version>1.1.10</version>
            </dependency>
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-starter</artifactId>
                <version>1.1.21</version>
            </dependency>
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-starter</artifactId>
                <version>1.2.15</version>
            </dependency>
            </dependency>
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-starter</artifactId>
                <version>1.2.18</version>
            </dependency>
            
    		......
        </dependencies>
    
        ......
    </project>
    ```

### 3.1.3 SpringBoot 3.x 依赖

- **下面是毛毛张针对`SpringBoot3.1.0`选择的依赖：**

    - **`druid-spring-boot-3-starter`：1.2.24**

- 完整的项目依赖为：

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
        ......
    
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-parent</artifactId>
            <version>3.1.0</version>
        </parent>
        
    	......
    
        <dependencies>
    		......
    
            <!-- druid启动器的依赖  -->
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-3-starter</artifactId>
                <version>1.2.24</version>
            </dependency>
    
            ......
        </dependencies>
    	......
    
    </project>
    ```

## 3.2 常见配置

- 下面是`HicariCP`连接池常见的配置：

    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/springboot?useSSL=false&autoReconnect=true&characterEncoding=utf8&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8&allowMultiQueries=true  # 数据库地址
        driver-class-name: com.mysql.cj.jdbc.Driver  # MySQL 8.0 及以上需要 cj，5.7 以下可去掉 cj
        username: root  # 数据库用户名
        password: abc123  # 数据库密码
        type: com.alibaba.druid.pool.DruidDataSource  # 使用 Druid 数据源
        druid:
          # 连接池基本配置
          initial-size: 5  # 初始化连接数
          min-idle: 10  # 最小空闲连接数
          max-active: 20  # 最大连接数
          max-wait: 60000  # 最大等待时间（毫秒）
          time-between-eviction-runs-millis: 60000  # 多少毫秒执行一次空闲连接回收
          min-evictable-idle-time-millis: 600000  # 连接最小生存时间（毫秒）
          max-evictable-idle-time-millis: 1800000  # 连接最大生存时间（毫秒）
    
          # 连接校验配置
          validation-query: SELECT 1  # 用于测试连接是否可用的 SQL 语句
          validation-query-timeout: 2000  # SQL 校验超时时间（毫秒）
          test-on-borrow: false  # 获取连接时是否检测（影响性能，建议关闭）
          test-on-return: false  # 归还连接时是否检测（影响性能，建议关闭）
          test-while-idle: true  # 空闲时是否检测（推荐开启）
    
          # 连接池优化配置
          phy-max-use-count: 1000  # 每个连接的最大使用次数
          pool-prepared-statements: false  # 是否开启 PSCache
          max-open-prepared-statements: 50  # PSCache 允许的最大预编译 SQL 数
    
          # Druid 监控配置
          filters: stat,wall,log4j2  # 启用监控统计拦截的过滤器（SQL监控、防火墙、日志）
    
          # 监控页面配置
          stat-view-servlet:
            enabled: true  # 启用内置监控页面
            url-pattern: /druid/*  # 监控页面路径
            login-username: admin  # 访问监控页面的用户名
            login-password: admin  # 访问监控页面的密码
            reset-enable: false  # 是否允许重置统计数据
            allow: 127.0.0.1  # 允许访问的 IP 白名单
            deny: 192.168.0.1  # 禁止访问的 IP 黑名单
    
          # Web 监控配置
          web-stat-filter:
            enabled: true  # 开启 Web-JDBC 关联监控
            url-pattern: /*  # 监控所有请求
            exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"  # 排除静态资源和监控路径
            session-stat-enable: true  # 启用 Session 统计
            session-stat-max-count: 1000  # Session 最大数量
    
          # SQL 慢查询日志配置
          filter:
            stat:
              # 是否开启 FilterStat，默认true
              enabled: true
              # 是否开启 慢SQL 记录，默认false
              log-slow-sql: true
              # 慢 SQL 的标准，默认 3000，单位：毫秒
              slow-sql-millis: 5000
              # 合并多个连接池的监控数据，默认false
              merge-sql: false
    
            # 日志配置，采用 SLF4J
            slf4j:
              enabled: true  # 启用日志
              statement-log-error-enabled: true  # 开启 SQL 语句错误日志
              statement-create-after-log-enabled: false
              statement-close-after-log-enabled: false
              result-set-open-after-log-enabled: false
              result-set-close-after-log-enabled: false
    ```

- **上面配置主要分为三部分：数据源配置、数据库连接池配置和Druid监控配置**

## 3.3 数据源配置解析

- 下面部分属于数据源配置：

    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/springboot?useSSL=false&autoReconnect=true&characterEncoding=utf8&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8&allowMultiQueries=true  # 数据库地址
        driver-class-name: com.mysql.cj.jdbc.Driver  # MySQL 8.0 及以上需要 cj，5.7 以下可去掉 cj
        username: root  # 数据库用户名
        password: abc123  # 数据库密码
        type: com.alibaba.druid.pool.DruidDataSource  # 使用 Druid 数据源
    ```

- 配置解析：

    - **`url`**：定义数据库连接的详细信息，包括：
        - `localhost:3306`（服务器地址和端口）
        - `springboot`（数据库名称）
        - 连接参数
    - **`driver-class-name`**：指定数据库连接的驱动，MySQL 8.x使用 `com.mysql.cj.jdbc.Driver`
    - **`username` 和 `password`**：提供访问数据库的身份凭证
    - **`type`**：指定数据库连接池的实现，配置为  `com.alibaba.druid.pool.DruidDataSource`

- `url`连接参数详解：

    - **`useUnicode=true&characterEncoding=UTF-8`**：
        - 确保数据库与应用之间的字符编码一致，防止乱码问题
        - 推荐使用 `utf8mb4` 以支持更多字符（如表情符号）

    - **`serverTimezone=UTC`**：
        - 指定数据库服务器的时区，避免时间处理错误
        - 可选值：
            - `UTC`：世界标准时间，比北京时间（CST）早 8 小时
            - `Asia/Shanghai`：中国标准时间（推荐）
        - **说明**：MySQL 6.0 及以上的 `com.mysql.cj.jdbc.Driver` 需要显式指定时区，否则可能导致时区不匹配的异常

    - **`useSSL=false`**：
        - 指定是否启用 SSL 连接
        - 在生产环境（如 Linux 服务器部署）通常关闭 SSL 连接，以减少额外的安全配置
        - **建议**：在公共网络或云数据库环境中建议开启 SSL

    - **`autoReconnect=true&failOverReadOnly=false`**：
        - `autoReconnect=true`：允许 JDBC 驱动在连接意外断开时自动重连（已弃用，推荐使用连接池）
        - `failOverReadOnly=false`：防止发生故障转移时连接错误地设置为只读模式

    - **`allowPublicKeyRetrieval=true`**：
        - 允许客户端从 MySQL 服务器检索公钥，以进行密码验证
        - **安全建议**：生产环境下尽量避免使用该参数，建议启用 SSL 进行安全通信

    - **`zeroDateTimeBehavior=convertToNull`**：
        - 当数据库中的日期时间值为 `0000-00-00 00:00:00` 时，转换为 `null`，避免 Java 应用程序在处理无效日期值时出错

    - **`rewriteBatchedStatements=true`**：
        - 启用批量执行优化，提高批量 `INSERT`、`UPDATE` 的执行效率
        - 适用于大数据量操作，不适用于 `SELECT` 查询

## 3.4 连接池配置详解

- 下面是`Druid`连接池的配置：

    ```yaml
    druid:
      # 连接池基本配置
      initial-size: 5  # 初始化连接数
      min-idle: 10  # 最小空闲连接数
      max-active: 20  # 最大连接数
      max-wait: 60000  # 最大等待时间（毫秒）
      time-between-eviction-runs-millis: 60000  # 多少毫秒执行一次空闲连接回收
      min-evictable-idle-time-millis: 600000  # 连接最小生存时间（毫秒）
      max-evictable-idle-time-millis: 1800000  # 连接最大生存时间（毫秒）
      
      # 连接校验配置
      validation-query: SELECT 1  # 用于测试连接是否可用的 SQL 语句
      validation-query-timeout: 2000  # SQL 校验超时时间（毫秒）
      test-on-borrow: false  # 获取连接时是否检测（影响性能，建议关闭）
      test-on-return: false  # 归还连接时是否检测（影响性能，建议关闭）
      test-while-idle: true  # 空闲时是否检测（推荐开启）
      
      # 连接池优化配置
      phy-max-use-count: 1000  # 每个连接的最大使用次数
      pool-prepared-statements: false  # 是否开启 PSCache
      max-open-prepared-statements: 50  # PSCache 允许的最大预编译 SQL 数
    ```

- 配置属性列表介绍：

| 配置项名称                                | 缺省值          | 说明                                                         |
| ----------------------------------------- | --------------- | ------------------------------------------------------------ |
| connectProperties                         | {}              | map方式放入自定义的key和value，在Filter等地方可以获取该信息进行相应逻辑控制 |
| connectionProperties                      | null            | 字符串方式放入自定义的key和value，键值对用分号隔开，比如“a=b;c=d”，传入空白字符串表示清空属性，实际拆分字符串后赋值给connectProperties，在Filter等地方可以获取该信息进行相应逻辑控制 |
| connectTimeout                            | 0               | 新增的控制创建连接时的socket连接最大等待超时，单位是毫秒，默认0表示永远等待，工作原理是在创建连接时将该值设置到对应数据库驱动的属性信息中由其JDBC驱动进行控制 |
| connectionInitSqls                        | []              | 数组方式定义物理连接初始化的时候执行的1到多条sql语句，比如连接MySQL数据库使用低版本驱动的情况下，想使用utf8mb4,则可以配置sql为： set NAMES 'utf8mb4' |
| createScheduler                           | null            | 可以使用定时线程池方式异步创建连接，比起默认的单线程创建连接方式，经实际验证这种更可靠 |
| dbType                                    | null            | 对于不是Druid自动适配支持的db类型，可以强制指定db类型，字符串值来自com.alibaba.druid.DbType的枚举名 |
| destroyScheduler                          | null            | 可以使用定时线程池方式异步创建连接，比起默认的单线程创建连接方式，经实际验证这种更可靠 |
| driverClassName                           | 根据url自动识别 | 这一项可配可不配，如果不配置druid会根据url自动识别dbType，然后选择相应的driverClassName |
| exceptionSorter                           | null            | 当数据库抛出一些不可恢复的异常时，抛弃连接                   |
| failFast                                  | false           | null                                                         |
| filters                                   |                 | 属性类型是逗号隔开的字符串，通过别名的方式配置扩展插件，插件别名列表请参考druid jar包中的 /META-INF/druid-filter.properties,常用的插件有： 监控统计用的filter:stat 日志用的filter:log4j 防御sql注入的filter:wall 防御sql注入的filter:wall |
| initialSize                               | 0               | 初始化数据源时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时 |
| keepAlive                                 | false           | 连接池中的minIdle数量以内的连接，空闲时间超过minEvictableIdleTimeMillis，则会执行keepAlive操作。实际项目中建议配置成true |
| keepAliveBetweenTimeMillis                | 120000          | null                                                         |
| logAbandoned                              | false           | 在开启removeAbandoned为true的情况，可以开启该设置，druid在销毁未及时关闭的连接时，则会输出日志信息，便于定位连接泄露问题 |
| loginTimeout                              |                 | 单位是秒，底层调用DriverManager全局静态方法                  |
| maxActive                                 | 8               | 连接池最大活跃连接数量，当连接数量达到该值时，再获取新连接时，将处于等待状态，直到有连接被释放，才能借用成功 |
| maxEvictableIdleTimeMillis                | 25200000        | null                                                         |
| maxIdle                                   | 8               | 已经彻底废弃，配置了也没效果，以maxActive为准                |
| maxOpenPreparedStatements                 | 10              | null                                                         |
| maxPoolPreparedStatementPerConnectionSize | 10              | 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100 |
| maxWait                                   | -1              | 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。 |
| minEvictableIdleTimeMillis                | 1800000         | 连接保持空闲而不被驱逐的最小时间                             |
| minIdle                                   | 0               | 连接池最小空闲数量                                           |
| name                                      | DataSource-**** | 配置这个属性的意义在于，如果存在多个数据源，监控的时候可以通过名字来区分开来。如果没有配置，将会生成一个名字，格式是："DataSource-" + System.identityHashCode(this). 另外配置此属性至少在1.0.5版本中是不起作用的，强行设置name会出错。[详情-点此处](http://blog.csdn.net/lanmo555/article/details/41248763)。 |
| numTestsPerEvictionRun                    | 3               | 不再使用，已经彻底废弃，一个DruidDataSource只支持一个EvictionRun |
| password                                  | null            | 连接数据库的密码。如果你不希望密码直接写在配置文件中，可以使用passwordCallback进行配置，或者使用ConfigFilter。[详细看这里](https://github.com/alibaba/druid/wiki/使用ConfigFilter) |
| passwordCallback                          | null            | 可以自定义实现定制的PasswordCallback，然后实现定制的密码解密效果 |
| phyTimeoutMillis                          | -1              | 强制回收物理连接的最大超时时长，大于0的情况下才生效，当物理创建之后存活的时长超过该值时，该连接会强制销毁，便于重新创建新连接，建议可以配置成7小时的毫秒值，比如25200000，这样可以规避MySQL的8小时连接断开问题 |
| poolPreparedStatements                    | false           | 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。 |
| proxyFilters                              |                 | 类型是List<com.alibaba.druid.filter.Filter>，如果同时配置了filters和proxyFilters，是组合关系，并非替换关系 |
| queryTimeout                              | 0               | 控制查询结果的最大超时，单位是秒，大于0才生效，最终底层调用是java.sql.Statement.setQueryTimeout(int) |
| removeAbandoned                           | false           | 是否回收泄露的连接,默认不开启，建议只在测试环境设置未开启，利用测试环境发现业务代码中未正常关闭连接的情况 |
| removeAbandonedTimeoutMillis              | 300000          | 开启回收泄露连接的最大超时，默认300秒表示连接被借出超过5分钟后，且removeAbandoned开启的情况下，强制关闭该泄露连接 |
| socketTimeout                             | 0               | 新增的控制创建连接时的socket最大读超时，单位是毫秒，默认0表示永远等待，配置成10000则表示db操作如果在10秒内未返回应答，将抛出异常，工作原理是在创建连接时将该值设置到对应数据库驱动的属性信息中由其JDBC驱动进行控制 |
| testOnBorrow                              | false           | 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能，其实一般情况下都可以开启，只有性能要求极其高且连接使用很频繁的情况下才有必要禁用。 |
| testOnReturn                              | false           | 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能，这个一般不需要开启。 |
| testWhileIdle                             | true            | 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 |
| timeBetweenEvictionRunsMillis             | 60000           | 有两个含义： 1) Destroy线程会检测连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接。 2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明 |
| transactionQueryTimeout                   | 0               | 控制查询结果的最大超时，单位是秒，大于0才生效，最终是在开启事务的情况下底层调用java.sql.Statement.setQueryTimeout(int) |
| url                                       |                 | 连接数据库的url，不同数据库不一样。例如： mysql : jdbc:mysql://10.20.153.104:3306/druid2 oracle : jdbc:oracle:thin:@10.20.149.85:1521:ocnauto |
| username                                  | null            | 连接数据库的用户名                                           |
| validationQuery                           | null            | 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。 |
| validationQueryTimeout                    | -1              | 单位：秒，检测连接是否有效的超时时间，大于0才生效。底层调用jdbc Statement对象的void setQueryTimeout(int seconds)方法 |

## 3.5 Druid监控配置

- 下面是`Druid`监控配置：

    ```yaml
      # Druid 监控配置
      filters: stat,wall,slf4j  # 启用监控统计拦截的过滤器（SQL监控、防火墙、日志）
    
      # 监控页面配置
      stat-view-servlet:
        enabled: true  # 启用内置监控页面
        url-pattern: /druid/*  # 监控页面路径
        login-username: admin  # 访问监控页面的用户名
        login-password: admin  # 访问监控页面的密码
        reset-enable: false  # 是否允许重置统计数据
        allow: 127.0.0.1  # 允许访问的 IP 白名单
        deny: 192.168.0.1  # 禁止访问的 IP 黑名单
    
      # Web 监控配置
      web-stat-filter:
        enabled: true  # 开启 Web-JDBC 关联监控
        url-pattern: /*  # 监控所有请求
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"  # 排除静态资源和监控路径
        session-stat-enable: true  # 启用 Session 统计
        session-stat-max-count: 1000  # Session 最大数量
    
      # SQL 慢查询日志配置
      filter:
        stat:
          enabled: true  # 是否开启 FilterStat，默认true
          log-slow-sql: true  # 是否开启 慢SQL 记录，默认false
          slow-sql-millis: 5000  # 慢 SQL 的标准，默认 3000，单位：毫秒
          merge-sql: false  # 合并多个连接池的监控数据，默认false
    
        # 日志配置，采用 SLF4J
        slf4j:
          enabled: true  # 启用日志
          statement-log-error-enabled: true  # 开启 SQL 语句错误日志
          statement-create-after-log-enabled: false
          statement-close-after-log-enabled: false
          result-set-open-after-log-enabled: false
          result-set-close-after-log-enabled: false
    ```

- 配置详解：
    - `filters`: 启用 Druid 的过滤器，`stat` 用于SQL监控，`wall` 用于防火墙，`log4j2` 用于日志记录。
    - `stat-view-servlet`: 启用 Druid 内置监控页面，配置路径、用户名、密码等。可以通过 `url-pattern` 指定访问路径。
    - `web-stat-filter`: 配置 Web-JDBC 关联监控，可以监控 HTTP 请求的详细数据，如 Session 信息。
    - `filter.stat`: 配置 Druid 的 SQL 慢查询日志功能，记录执行时间超过设定阈值的 SQL 查询。
    - `slf4j`: 配置 Druid 使用 SLF4J 进行日志输出，开启 SQL 语句错误日志、创建语句日志等。

## 3.6 总结

- 使用 `Druid` 连接池的核心关键点在于选择与项目所使用的 `Spring Boot` 版本兼容的 `Druid` 依赖版本。
    - **如果版本选择不当，虽然 `Druid` 连接池仍然可以正常使用，但后台的 SQL 监控功能可能无法正常工作。**
    - 毛毛张在其文章中详细介绍了不同版本的兼容性问题，通常，如果遇到 SQL 后台监控无法显示的情况，可以尝试升级到 `Druid` 的最新版本，以解决此类问题。

# 4 总结

- 更多关于Druid连接池的介绍可以查看官网连接：<https://github.com/alibaba/druid/tree/master>
- 更多关于`Druid`配置文件的介绍可以查看官网的配置文件的介绍：<https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter#%E5%A6%82%E4%BD%95%E9%85%8D%E7%BD%AE-filter>

# 参考文献

- <https://juejin.cn/post/7329033419328307226>
- <https://zhuanlan.zhihu.com/p/686960884>
- <https://blog.csdn.net/hansome_hong/article/details/124320410>
- <https://juejin.cn/post/7224499191962714171#heading-2>
- <https://blog.csdn.net/hansome_hong/article/details/124320410>
- <https://pdai.tech/md/spring/springboot/springboot-x-mysql-HikariCP.html>
- <https://blog.csdn.net/doubiy/article/details/131578389>
- <https://www.cnblogs.com/lyluoye/p/16627840.html>
- <https://github.com/brettwooldridge/HikariCP>
- <https://www.cnblogs.com/zhaojinhui/p/17579010.html>
- <https://javabetter.cn/springboot/mysql-druid.html>
- <https://juejin.cn/post/7374356945702322214>
- <https://blog.csdn.net/munangs/article/details/124724091>
- <https://zhuanlan.zhihu.com/p/636184214>
- <https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8>



