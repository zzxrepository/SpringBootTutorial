

> <font color="green">**在前面一篇文章中毛毛张介绍了SpringBoot中数据源与数据库连接池相关概念，今天毛毛张要分享的是关于SpringBoot整合HicariCP连接池相关知识点以及底层源码分析**</font>



[toc]

# 1 HicariCP连接池概述

- 在上面介绍过`HicariCP`是`SpringBoot2.0`之后默认的数据库连接池，特点就是：简单，高效，史称最快的数据库连接池，毛毛张将从以下几个方面展开对`HicariCP`连接池的介绍：
    - 首先简单介绍以下`HicariCP`连接池
    - 然后通过一个案例来帮助大家能够快速使用`HicariCP`连接池
    - 接着通过分析`SpringBoot`的底层代码来解释`SringBoot`默认的数据库连接池为`HicariCP`


## 1.1 HicariCP连接池简介

- **HikariCP 是一个高性能的`JDBC`数据库连接池，基于`BoneCP`做了多项优化，旨在提供更高的并发性能和更低的延迟。自`SpringBoot 2.x`版本后（自然也包括`SpringBoot 3.x`），`HikariCP`成为默认的数据库连接池，只需导入`HikariCP`的`JAR`包并配置相关参数，即可无缝集成并优化数据库连接池管理。**

- **HikariCP 的高性能优化主要体现在以下两个方面：**
    - **FastList 替代 ArrayList**
        - 传统的数据库连接池大多使用`ArrayList`存储`Statement`，HikariCP 自定义了`FastList`来优化这一操作。`FastList`的优化主要体现在两个方面：
            - 取消 `ArrayList` 的 `get()` 方法中的范围检查（range check）。由于数据库连接池管理的 List 中的索引合法性有保证，因此不需要每次访问都进行索引合法性检查。
            - 改变 `ArrayList` 中 `remove()` 操作的遍历方式，采用从尾部开始遍历，而不是从头开始。由于连接池中的连接通常是逆序释放的（后获取的连接先释放），这样优化后，每次关闭连接时可以更高效地找到需要释放的资源，提升了效率。
    - **ConcurrentBag 替代阻塞队列**
        - 大多数传统数据库连接池使用两个阻塞队列（`idle` 和 `busy`）来管理空闲连接和忙碌连接，使用 `Lock` 机制来处理线程竞争，但这种方式在高并发场景下可能会导致性能瓶颈。`HikariCP`通过使用 `ConcurrentBag` 替代了传统的阻塞队列，极大地减少了锁的竞争，提高了并发性能。
        - `ConcurrentBag`的核心工作原理：
            - `sharedList`：存储所有数据库连接的共享队列，使用 `CopyOnWriteArrayList` 类型，支持并发操作。
            - `threadList`：线程本地存储，避免了线程竞争，每个线程会缓存自己获取的连接。
            - `waiters`：等待连接的线程数，使用 `AtomicInteger` 类型。
            - `handoffQueue`：分配数据库连接的核心队列，使用 `SynchronousQueue` 类型，负责将空闲连接分配给请求的线程。
        - 这种设计通过减少线程竞争和优化连接分配，提高了连接池的效率，特别适合高并发的环境。
- **其他优化**
    - **字节码精简**：HikariCP 在字节码上进行了优化，编译后的字节码量极少，这样可以使得更多的代码被 CPU 缓存，从而提高程序的执行效率。减少字节码的大小是提高性能的基础，HikariCP 在这方面做得非常好。
    - **优化代理和拦截器**：HikariCP 对代理和拦截器的实现进行了精简。例如，它的 `Statement` 代理类仅有 100 行代码，是 BoneCP 的十分之一。这减少了性能开销，确保数据库连接池在执行 SQL 时更加高效。
    - **自定义集合类型（FastStatementList）**：为了提高对 `Statement` 的管理效率，HikariCP 使用了自定义的集合类型 `FastStatementList` 来替代传统的 `ArrayList`。这样避免了每次获取元素时的范围检查，并且采用逆序遍历的方式来优化 `remove()` 操作，使得关闭连接时更加高效。
    - **自定义集合类型（ConcurrentBag）**：HikariCP 为了优化并发读写效率，采用了 `ConcurrentBag` 代替传统的阻塞队列。这不仅提高了数据库连接分配的效率，而且减少了锁的竞争，显著提升了高并发场景下的性能。
    - **针对 BoneCP 缺陷的优化**：HikariCP 在设计时，针对 BoneCP 中的一些缺陷进行了优化，特别是在处理 CPU 时间片内的耗时方法调用时，进一步提高了性能。

> 接下来毛毛张将结合一个完整的项目案例来介绍`SpringBoot`整合

# 2 快速入门案例

- 案例内容：基于`Spring Boot`，使用`MyBatis`框架，结合`HikariCP`连接池，查询并展示数据库中的全部用户信息
- - 项目版本依赖：
        - 后端：
            - SpringBoot：2.7.6
            - JDK：17
            - Mybatis：3.0.1
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

![image-20250124224817552](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250124224817552.png)

### 2.1.2 导入依赖

- 在 Spring Boot 项目中，默认使用 HikariCP 作为数据库连接池，因此在大多数情况下无需手动引入该依赖。若项目中使用了以下某些 starter 依赖，`HikariCP` 会自动作为连接池配置：
    - `spring-boot-starter-jdbc`
    - `spring-boot-starter-data-jpa`
    - `mybatis-spring-boot-starter`
    - `mybatis-plus-boot-starter`

- 需要特别注意的是，`mybatis` 和 `mybatis-plus` 已经间接依赖了 `spring-boot-starter-jdbc`，因此这两个 starter 会自动引入 `HikariCP`，从而避免重复配置。

#### **方式1：手动导入 `HikariCP` 依赖**

- 如果没有导入`HikariCP`，我们可以通过下面的方式手动导入`HikariCP`依赖

    ```xml
    <dependency>
       <groupId>com.zaxxer</groupId>
       <artifactId>HikariCP</artifactId>
       <version>6.2.1</version>
    </dependency>
    ```

#### 方式 2：自动引入依赖 - 推荐

- 在使用`SpringBoot`默认配置时，不需要手动添加 `HikariCP` 依赖，可以通过导入下面依赖的方式来自动引入`HikariCP`依赖：

    ```xml
    <!-- 引入 Spring Boot JDBC 模块 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    
    <!-- 引入 Spring Boot JPA 模块 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- 引入 MyBatis 模块 -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.1</version>
    </dependency>
    
    <!-- 引入 MyBatis Plus 模块 -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.4.2</version>
    </dependency>
    ```

#### 本项目完整依赖

- **需要注意的是，在使用 HikariCP 作为连接池的同时，还需要单独导入 MySQL 数据库驱动，通常通过引入 `mysql-connector-java` 依赖来实现**

- **毛毛张在这个任务中为了方便，使用了`Mybatis`框架，整个项目的`pom.xml`文件为：**

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
    
        <groupId>com.zzx</groupId>
        <artifactId>springboot-HicariCP-demo</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <name>springboot-HicariCP-demo</name>
        <description>springboot-HicariCP-demo</description>
    
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-parent</artifactId>
            <version>2.7.6</version>
        </parent>
    
    
        <properties>
            <java.version>1.8</java.version>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
            <spring-boot.version>2.7.6</spring-boot.version>
        </properties>
    
    
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
    
            <!-- MySQL 数据库的 JDBC 驱动 -->
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <scope>runtime</scope> <!-- 运行时依赖 -->
            </dependency>
    
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>3.0.1</version>
            </dependency>
    
            <!-- 热部署 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
                <scope>runtime</scope>
                <optional>true</optional>
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
                        <mainClass>com.zzx.SpringbootHicariCpDemoApplication</mainClass>
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

- 下面是整个项目的配置文件`application.yaml`，关键部分已被注释出来了，更多详细的解释可以参见第三节的内容

    ```yaml
    server:
      port: 8080
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/springboot?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&serverTimezone=UTC
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
    import org.springframework.stereotype.Service;
    
    import javax.annotation.Resource;
    import java.util.List;
    
    @Service
    public class UserServiceImpl implements UserService {
        @Resource
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
    
    
    import com.zzx.entity.User;
    
    import java.util.List;
    
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

- 同时也能看见控制台输出的使用的连接池为`HikariCP`：

![image-20250126180142644](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250126180142644.png)

- **完整的后端代码已上传至毛毛张`Github`仓库：<https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mysql/springboot-HicariCP-demo>**

## 2.2 前端代码

- **前端代码和之前毛毛张介绍的`Mybatis`教程的代码是一样的，毛毛张在这里不做过多的介绍了，感兴趣的可以查看毛毛张的相关博客：[【SpringBoot教程】SpringBoot整合Mybatis - 前后端分离项目 - vue3](https://blog.csdn.net/weixin_48235955/article/details/144818393)**
- **完整前端代码已上传至毛毛张`Github`仓库：<https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mysql/springboot-mysql-demo-vue>**



## 2.3 可能会遇到的问题

- 问题1：**MySQL 连接器版本问题**：在 Spring Boot 2.7.6 版本中，MySQL 连接器的版本可以自动推断，但在 Spring Boot 3.x 中，MySQL 连接器的版本需要显式指定，否则会导致构建失败。解决办法是在 `pom.xml` 中显式指定 `mysql-connector-java` 的版本，例如：

    ```xml
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.30</version>  <!-- 替换为你需要的版本 -->
        <scope>runtime</scope>
    </dependency>
    ```

- 问题2：**`javax.annotation.Resource` 找不到包的问题**：Spring Boot 3.x 版本基于 Spring 6，而 `javax.annotation` 包已不再包含在 Spring 中。此时编译时会出现 `程序包javax.annotation不存在` 的错误。解决办法有两个选择：

    - 方案 1：显式添加`javax.annotation-api`依赖：

        ```xml
        <dependency>
            <groupId>javax.annotation</groupId>
            <artifactId>javax.annotation-api</artifactId>
            <version>1.3.2</version>
        </dependency>
        ```

    - 方案 2：直接使用 Spring 推荐的`@Autowired`注解替代`@Resource`，因为`@Autowired`是`Spring`的默认注解，且具有更好的兼容性：

- 注意事项：

    - 在 Spring Boot 3.x 版本中，升级了 Spring Framework 到 6.x，因此需要支持 JDK 17 及更高版本的兼容性，部分原本自动包含的库（如 `javax.annotation`）已被移除，需要显式添加相关依赖。

    - 对于依赖注入，建议使用 `@Autowired` 注解来替代 `@Resource`，这是 Spring 推荐的做法，能够更好地与 Spring 的生态系统兼容。

# 3 配置详解

## 3.1 导入依赖

- 在 Spring Boot 项目中，默认使用 HikariCP 作为数据库连接池，因此在大多数情况下无需手动引入该依赖。若项目中使用了以下某些 starter 依赖，`HikariCP` 会自动作为连接池配置：
    - `spring-boot-starter-jdbc`
    - `spring-boot-starter-data-jpa`
    - `mybatis-spring-boot-starter`
    - `mybatis-plus-boot-starter`

- **需要特别注意的是，`mybatis` 和 `mybatis-plus` 已经间接依赖了 `spring-boot-starter-jdbc`，因此这两个 starter 会自动引入 `HikariCP`，从而避免重复配置。**

#### **方式1：手动导入 `HikariCP` 依赖**

- 如果没有导入`HikariCP`，我们可以通过下面的方式手动导入`HikariCP`依赖

    ```xml
    <dependency>
       <groupId>com.zaxxer</groupId>
       <artifactId>HikariCP</artifactId>
       <version>6.2.1</version>
    </dependency>
    ```

#### 方式 2：自动引入依赖 - 推荐

- 在使用`SpringBoot`默认配置时，不需要手动添加 `HikariCP` 依赖，可以通过导入下面依赖的方式来自动引入`HikariCP`依赖：

    ```xml
    <!-- 引入 Spring Boot JDBC 模块 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    
    <!-- 引入 Spring Boot JPA 模块 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- 引入 MyBatis 模块 -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.1</version>
    </dependency>
    
    <!-- 引入 MyBatis Plus 模块 -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.4.2</version>
    </dependency>
    ```

## 3.2 常见配置

- 下面是`HicariCP`连接池常见的配置：

    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/springboot?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&serverTimezone=UTC
        driver-class-name: com.mysql.cj.jdbc.Driver
        username: root
        password: abc123
        type: com.zaxxer.hikari.HikariDataSource # 指定数据源类型为 HikariDataSource
        hikari: # Hikari 连接池的详细配置
          pool-name: HikariCP       # 连接池名称
          minimum-idle: 5 # 最小空闲连接数
          idle-timeout: 600000  # 空闲连接超时时间（毫秒）
          maximum-pool-size: 10 # 连接池的最大大小
          auto-commit: true # 是否自动提交事务
          max-lifetime: 1800000 # 连接的最大生命周期（毫秒）
          connection-timeout: 30000 # 连接超时时间（毫秒）
          connection-test-query: SELECT 1 # 测试连接的 SQL 语句
    ```
    
- **上面配置主要分为两部分：**

    - **一部分是数据源配置**
    - **一部分是数据库连接池配置**


## 3.3 数据源配置解析

- 下面部分属于数据源配置：

    ```yaml
    spring:
      datasource:
        # 数据库连接URL，指定数据库地址、端口、库名以及连接参数
        url: jdbc:mysql://localhost:3306/springboot?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&serverTimezone=UTC
        # MySQL 8.0 及以上需要 cj，5.7 以下可去掉 cj
        driver-class-name: com.mysql.cj.jdbc.Driver 
        # 数据库的用户名和密码
        username: root
        password: abc123
        # 指定使用的数据库连接池实现
        type: com.zaxxer.hikari.HikariDataSource
    ```

- 配置解析：
    - **`url`**：定义数据库连接的详细信息，包括：
        - `localhost:3306`（服务器地址和端口）
        - `springboot`（数据库名称）
        - 连接参数
    - **`driver-class-name`**：指定数据库连接的驱动，MySQL 8.x使用 `com.mysql.cj.jdbc.Driver`
    - **`username` 和 `password`**：提供访问数据库的身份凭证。
    - **`type`**：指定数据库连接池的实现，配置为 `com.zaxxer.hikari.HikariDataSource`，表示使用 **HikariCP** 作为数据库连接池。如果需要使用 **Druid**，应将其替换为 `com.alibaba.druid.pool.DruidDataSource`。
- `url`连接参数详解：
    - **`useUnicode=true&characterEncoding=UTF-8`**：
      - 确保数据库与应用之间的字符编码一致，防止乱码问题。
      - 推荐使用 `utf8mb4` 以支持更多字符（如表情符号）。

    - **`serverTimezone=UTC`**：
      - 指定数据库服务器的时区，避免时间处理错误。
      - 可选值：
        - `UTC`：世界标准时间，比北京时间（CST）早 8 小时。
        - `Asia/Shanghai`：中国标准时间（推荐）。
      - **说明**：MySQL 6.0 及以上的 `com.mysql.cj.jdbc.Driver` 需要显式指定时区，否则可能导致时区不匹配的异常。

    - **`useSSL=false`**：
      - 指定是否启用 SSL 连接。
      - 在生产环境（如 Linux 服务器部署）通常关闭 SSL 连接，以减少额外的安全配置。
      - **建议**：在公共网络或云数据库环境中建议开启 SSL。

    - **`autoReconnect=true&failOverReadOnly=false`**：
      - `autoReconnect=true`：允许 JDBC 驱动在连接意外断开时自动重连（已弃用，推荐使用连接池）。
      - `failOverReadOnly=false`：防止发生故障转移时连接错误地设置为只读模式。

    - **`allowPublicKeyRetrieval=true`**：
      - 允许客户端从 MySQL 服务器检索公钥，以进行密码验证。
      - **安全建议**：生产环境下尽量避免使用该参数，建议启用 SSL 进行安全通信。

    - **`zeroDateTimeBehavior=convertToNull`**：
      - 当数据库中的日期时间值为 `0000-00-00 00:00:00` 时，转换为 `null`，避免 Java 应用程序在处理无效日期值时出错。

    - **`rewriteBatchedStatements=true`**：
      - 启用批量执行优化，提高批量 `INSERT`、`UPDATE` 的执行效率。
      - 适用于大数据量操作，不适用于 `SELECT` 查询。

## 3.4 连接池配置详解

- 下面是关于连接池的配置部分：

    ```yaml
    spring:
      datasource:
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
    ```

- 关于`HikariCP`连接池的详细配置解析如下：

|            属性             |                         描述                         |    构造器默认值    |         默认配置 validate 之后的值          |                   validate 重置                    |
| :-------------------------: | :--------------------------------------------------: | :----------------: | :-----------------------------------------: | :------------------------------------------------: |
|        `autoCommit`         |               自动提交从池中返回的连接               |       `true`       |                   `true`                    |                        `–`                         |
|     `connectionTimeout`     |             等待来自池的连接的最大毫秒数             |   `30000` (30秒)   |                   `30000`                   |         如果小于 250 毫秒，则重置为 30 秒          |
|        `idleTimeout`        |             连接允许在池中闲置的最长时间             | `600000` (10分钟)  | 受 `maxLifetime` 影响，条件不符可能重置为 0 |          如果设置小于 10 秒则重置为 10 秒          |
|        `maxLifetime`        |                 池中连接最长生命周期                 | `1800000` (30分钟) |                  `1800000`                  |           如果小于 30 秒则重置为 30 分钟           |
|    `connectionTestQuery`    |           如果支持 JDBC4，建议不设置此属性           |       `null`       |                   `null`                    |                        `–`                         |
|        `minimumIdle`        |               池中维护的最小空闲连接数               |        `10`        |                    `10`                     | 小于 0 或大于 `maxPoolSize` 则重置为 `maxPoolSize` |
|      `maximumPoolSize`      |        池中最大连接数，包括闲置和使用中的连接        |        `10`        |                    `10`                     |   如果小于 1，则重置为默认 10 或 `minIdle` 的值    |
|      `metricRegistry`       |    指定 Codahale/Dropwizard MetricRegistry 的实例    |       `null`       |                   `null`                    |                        `–`                         |
|    `healthCheckRegistry`    |      指定 Dropwizard HealthCheckRegistry 的实例      |       `null`       |                   `null`                    |                        `–`                         |
|         `poolName`          |    连接池的用户定义名称，主要用于日志和 JMX 管理     |       `null`       |               `HikariPool-1`                |                        `–`                         |
| `initializationFailTimeout` |         控制池是否在初始化连接失败时快速失败         |        `1`         |                     `1`                     |                        `–`                         |
|  `isolateInternalQueries`   |            是否在独立事务中隔离内部池查询            |      `false`       |                   `false`                   |                        `–`                         |
|    `allowPoolSuspension`    |            是否允许通过 JMX 暂停和恢复池             |      `false`       |                   `false`                   |                        `–`                         |
|         `readOnly`          |         从池中获取的连接是否默认处于只读模式         |      `false`       |                   `false`                   |                        `–`                         |
|      `registerMbeans`       |           是否注册 JMX 管理 Bean（MBeans）           |      `false`       |                   `false`                   |                        `–`                         |
|          `catalog`          |                   设置默认 catalog                   |     `default`      |                   `null`                    |                        `–`                         |
|     `connectionInitSql`     |            在新连接创建后执行的 SQL 语句             |       `null`       |                   `null`                    |                        `–`                         |
|      `driverClassName`      |        JDBC URL 解析失败时指定驱动程序类名称         |       `null`       |                   `null`                    |                        `–`                         |
|   `transactionIsolation`    |                连接的默认事务隔离级别                |       `null`       |                   `null`                    |                        `–`                         |
|     `validationTimeout`     |                连接测试活动的最大时间                |    `5000` (5秒)    |                   `5000`                    |          如果小于 250 毫秒，则重置为 5 秒          |
|  `leakDetectionThreshold`   |             检测潜在的连接泄漏的时间阈值             |        `0`         |                     `0`                     |    必须 > 0 且 ≥ 2 秒，且不能大于 `maxLifetime`    |
|        `dataSource`         |                设置要包装的数据源实例                |       `null`       |                   `null`                    |                        `–`                         |
|          `schema`           |                   设置默认 schema                    |     `default`      |                   `null`                    |                        `–`                         |
|       `threadFactory`       |      设置用于创建池线程的 `ThreadFactory` 实例       |       `null`       |                   `null`                    |                        `–`                         |
|     `scheduledExecutor`     | 设置用于池内部任务的 `ScheduledExecutorService` 实例 |       `null`       |                   `null`                    |                        `–`                         |

- 更具体的可以看[官方配置文档](https://github.com/brettwooldridge/HikariCP)

# 4 底层源码解析

- 前面毛毛张介绍了`HikariCP`连接池的配置，下面毛毛张通过分析源码的方式来介绍以下为什么说`SoringBoot2.x`之后默认使用的连接池是`HikariCP`，从SpringBoot自动初始化配置 和 默认的数据源 两个角度理解。

## 4.1 SpringBoot自动初始化配置

- 找到`spring-boot-autocinfigure-2.7.6.jar`依赖下面的`org.springframework.boot.autoconfigure.jdbc`包

![image-20250125140727917](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250125140727917.png)

- 关键代码如下：

![image-20250125140535976](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250125140535976.png)

- 找到`HikariCP`数据源的配置：你可以发现，为了支持动态更新配置（基于MXBean)，这里还设计了一层`HikariConfigMXBean`接口

![image-20250125141106379](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250125141106379.png)

## 4.2 默认的数据源

- 首先，`springboot-starter-jdbc`中默认加载了`HikariCP`

    ![image-20250125143209056](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250125143209056.png)

    ![image-20250125143333154](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250125143333154.png)

- 其次，在配置初始化或者加载时都是第一个被加载的

![image-20250125144401033](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250125144401033.png)

- **补充**：通过对源码的查看，从 Spring Boot 2.0 版本开始，默认支持四种数据库连接池：**HikariCP**、**Tomcat JDBC Connection Pool**、**DBCP2** 和 **Oracle UCP**。默认情况下，Spring Boot 会优先使用 **HikariCP** 作为连接池，因为 HikariCP 提供了卓越的性能、低延迟和高效的资源利用。如果 classpath 下存在 HikariCP，Spring Boot 会自动将其作为默认的数据库连接池；如果没有 HikariCP，Spring Boot 会依次检测其他连接池的存在，并按照以下顺序选择：

![image-20250126174920618](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/tree/image-20250126174920618.png)

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

