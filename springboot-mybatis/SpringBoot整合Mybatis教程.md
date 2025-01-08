> SpringBoot整合Mybatis教程

[toc]

- 再看看这个参考文献补充一下内容：
  - https://zhuanlan.zhihu.com/p/143798465
  - https://javabetter.cn/springboot/mybatis.html#%E6%95%B4%E5%90%88-mybatis



# 1.前言

- 今天毛毛张要分享的是SpringBoot整合Mybatis的教程，毛毛张将从前后端分离的方式、通过一个完整的任务来教大家整合的整个过程
- 版本：
  - 后端：
    - SpringBoot：2.7.6
    - JDK：17
    - Mybatis：2.2.2
  - 前端
    - vite：6.0.5
    - vue：3.5.13
    - vue-router：4.5.0
    - pinia：2.3.0
    - axios：1.7.9

## 1.1 Mybatis简介

- MyBatis 是一款优秀的持久层框架，它支持定制化 SQL、存储过程以及高级映射。MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。MyBatis 可以使用简单的 XML 或注解来配置和映射原生信息，将接口和 Java 的 POJOs(Plain Ordinary Java Object,普通的 Java对象)映射成数据库中的记录。
- MyBatis 是支持普通 SQL查询，存储过程和高级映射的优秀持久层框架。MyBatis 消除了几乎所有的JDBC代码和参数的手工设置以及结果集的检索。MyBatis 使用简单的 XML或注解用于配置和原始映射，将接口和 Java 的POJOs（Plain Ordinary Java Objects，普通的 Java对象）映射成数据库中的记录。

## 1.2 任务描述

- 通过SpringBoot整合Mybatis框架，查询数据库所有用户信息，并展示给前端

## 1.3 SpringBoot整合Mybatis概述

- 毛毛张首先在这里介绍整合SpringBoot的几个关键点

- **首先是导入依赖：不光是只导入Mybatis依赖**

  ```xml
  <!-- MyBatis 依赖 -->
  <dependency>
      <groupId>org.mybatis.spring.boot</groupId>
      <artifactId>mybatis-spring-boot-starter</artifactId>
      <version>2.2.2</version>
  </dependency>
  
  <!-- MySQL 数据库的 JDBC 驱动 -->
  <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <scope>runtime</scope> <!-- 运行时依赖 -->
  </dependency>
  ```
  
- 然后是根据数据库编写对应的实体类，比如说用户信息表对应的实体类：

  ```java
  package com.zzx.user.repository.entity;
  
  
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

- 接着编写MyBatis 框架中用于映射 SQL 语句的配置文件`XXXMapper.xml`文件以及对应的`Mapper`接口类：

  ```java
  package com.zzx.user.repository.mapper;
  
  
  import com.zzx.user.repository.entity.User;
  
  import java.util.List;
  
  public interface UserMapper {
      //方式1：具体的查询语句写在对应的xml文件中（推荐）
      List<User> queryAllUserInfo();
      
      //方式2：直接使用注解：
      /*
      @Select("SELECT id, user_name, pass_word, age, gender FROM user_info")
      List<User> queryAllUserInfo();
      */
  }
  ```

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "https://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <!-- namespace = mapper 对应接口的全限定符 -->
  <mapper namespace="com.zzx.user.repository.mapper.UserMapper">
      <!-- 声明标签写sql语句  crud  select  insert update  delete
            每个标签对应接口的一个方法！  方法的一个实现！
            注意：mapper接口不能重载！！！ 因为mapper.xml无法识别！ 根据方法名识别！
       -->
          <!-- 查询所有用户的mysql语句 -->
          <!-- 方式1 ：我已经把实体包的路径写在了yaml文件的mybatis配置下了，所以下面可以直接写resultType="User"-->
          <select id="queryAllUserInfo" resultType="User">
              SELECT
              id,
              user_name,
              pass_word,
              age,
              gender
              FROM user_info  <!-- 使用带有前缀的表名 -->
          </select>
      
      	<!-- 方式2 ：如果没有把实体包的路径写在了yaml文件的mybatis配置下，可以使用如下方式
      	<!-- <select id="queryAllUserInfo" resultType="com.zzx.user.repository.entity.User">-->
      	<!--<select id="queryAllUserInfo" resultType="User">      -->
          <!--   SELECT                                             -->
          <!--   id,                                                -->
          <!--   user_name,                                         -->
          <!--  pass_word,                                          -->
          <!--   age,                                               -->
          <!--  gender                                              -->
          <!--  FROM user_info                                      -->
          <!-- </select>                                            -->
  </mapper>
  ```

- 然后是在配置文件中编写Mybatis相关的配置，主要有两个地方

  - 一个是编写Mybatis框架映射SQL 语句的配置文件`XXXMapper.xml`存放的位置
  - 另一个是`XXXMapper.xml`对应的实体类的包的名称

  ```yaml
  mybatis:
    # Mapper 文件的位置
    mapper-locations: classpath:mappers/*Mapper.xml
    # 实体类的包路径
    type-aliases-package: com.zzx.user.repository.entity  # 修改成自己的
    configuration:
      # 自动下划线转驼峰
      map-underscore-to-camel-case: true
  ```

> 毛毛张在这里统一写在了`yaml`文件下，对于告诉SpringBoot中`XXXMapper.xml`文件的位置还可以存放在`pom.xml`文件中，方式如下：
>
> ```xml
> <!-- 在 pom.xml文件的build属性中添加如下代码 -->
> <build>
>     <resources>
>         <!--此处的配置是识别到mapper.xml文件，也可以在application.properties中配置-->
>         <resources>
>         <resource>
>             <directory>src/main/java</directory>
>             <includes>
>                 <include>**/*.xml</include>
>             </includes>
>         </resource>
>         <resource>
>             <directory>src/main/resources</directory>
>         </resource>
>     </resources>
>     </resources>
> </build>
> ```

- 最后一步，最重要的一步，不要忘记扫描`Mapper`接口所在的包：

  ```java
  package com.zzx;
  
  import org.mybatis.spring.annotation.MapperScan;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  @SpringBootApplication
  @MapperScan("com.zzx.user.repository.mapper")  // 扫描 Mapper 接口所在的包
  public class SpringbootMybatisDemoApplication {
  
      public static void main(String[] args) {
          SpringApplication.run(SpringbootMybatisDemoApplication.class, args);
      }
  
  }
  ```

## 1.3 完整项目文件

- 访问毛毛张Github仓库：https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mybatis

# 2.后端工程搭建

## 2.1 项目搭建

- 如何快速创建一个Maven项目注意的细节可以参看毛毛张的上一篇文章：[【SpringBoot教程】IDEA快速搭建正确的SpringBoot版本和Java版本的项目](https://blog.csdn.net/weixin_48235955/article/details/144807998)
- 毛毛张在这里选择的`SpringBoot`版本为`2.7.6`，`JDK`选择的是`JDK17`，下面是毛毛张搭建好的整个项目的完整文件夹：

![image-20241229190701198](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229190701198.png)

- 毛毛张在这里使用的是阿里云的国内源创建的SpringBoot项目，这样才能创建2.7.x的项目，具体原因参见这篇文章：[【SpringBoot教程】IDEA快速搭建正确的SpringBoot版本和Java版本的项目](https://blog.csdn.net/weixin_48235955/article/details/144807998)

![image-20241229193423125](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229193423125.png)

![image-20241229193549210](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229193549210.png)

- 创建好之后的项目如下所示，可用把下图中两个没用的文件去掉

![image-20241229193732999](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229193732999.png)

## 2.2 导入依赖pom.xml

- **在整合`Mybatis`的时候很显然的是需要导入`Mybatis`依赖，MyBatis 用于数据库操作，在 Spring Boot 中使用时，也需要导入数据库驱动的依赖，这是很多教程忽略的地方**

  ```xml
  <!-- MyBatis 依赖 -->
  <dependency>
      <groupId>org.mybatis.spring.boot</groupId>
      <artifactId>mybatis-spring-boot-starter</artifactId>
      <version>2.2.2</version>
  </dependency>
  
  <!-- MySQL驱动 mybatis底层依赖jdbc驱动实现,本次不需要导入连接池,mybatis自带! -->
  <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <scope>runtime</scope> <!-- 运行时依赖 -->
  </dependency>
  ```

- 对于一个完整的项目来说，为了简化实体类对象的构造方法以及Getter\Setter方法，还需要导入`Lombok`依赖，不熟悉该依赖的可以直接手写；用于测试代码的依赖，毛毛张这里选择的是SpringBoot的`spring-boot-starter-test`，如果熟悉的选择`juint`也行；这是一个前后端分离的项目，还需要导入用于构建`Web`应用的依赖`spring-boot-starter-web`

- 还需要注意的是，毛毛张在这里使用的是SpringBoot默认的连接池`hikari`，而没有选择`Druid`连接池，所以没有导入连接池的依赖，毛毛张后面还会出专门的教程分享这个知识点

- 项目完整的`pom.xml`文件如下：

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                               https://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
  
      <!-- 父项目配置：使用 Spring Boot 的父项目来简化配置 -->
      <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>2.7.6</version> <!-- Spring Boot 的版本 -->
          <relativePath/> <!-- 从仓库中查找父项目 -->
      </parent>
  
      <!-- 项目基础信息 -->
      <groupId>com.zzx</groupId> <!-- 组织标识 -->
      <artifactId>springboot-mybatis-demo</artifactId> <!-- 项目名称 -->
      <version>0.0.1-SNAPSHOT</version> <!-- 项目版本 -->
      <name>springboot-mybatis-demo</name> <!-- 项目名称 -->
      <description>springboot-mybatis-demo</description> <!-- 项目描述 -->
  
      <!-- Java 版本配置 -->
      <properties>
          <java.version>17</java.version> <!-- 项目使用的 Java 版本 -->
      </properties>
  
      <!-- 依赖项配置 -->
      <dependencies>
          <!-- MySQL 数据库的 JDBC 驱动 -->
          <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
              <scope>runtime</scope> <!-- 运行时依赖 -->
          </dependency>
  
          <!-- MyBatis 依赖 -->
          <dependency>
              <groupId>org.mybatis.spring.boot</groupId>
              <artifactId>mybatis-spring-boot-starter</artifactId>
              <version>2.2.2</version>
          </dependency>
  
          <!-- Lombok 依赖 -->
          <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <version>1.18.36</version>
              <scope>provided</scope> <!-- 可选：标记为 provided -->
          </dependency>
  
          <!-- Spring Boot Starter Web 依赖，用于构建 Web 应用 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
  
          <!-- Spring Boot Starter Test 依赖，用于测试 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-test</artifactId>
              <scope>test</scope> <!-- 测试范围依赖 -->
          </dependency>
      </dependencies>
  
      <!-- 构建配置 -->
      <build>
          <plugins>
              <!-- Maven 编译插件，用于处理注解处理器（如 Lombok） -->
              <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-compiler-plugin</artifactId>
                  <version>3.8.1</version> <!-- 指定插件版本 -->
                  <configuration>
                      <source>${java.version}</source> <!-- Java 源代码版本 -->
                      <target>${java.version}</target> <!-- Java 字节码版本 -->
                      <annotationProcessorPaths>
                          <path>
                              <groupId>org.projectlombok</groupId>
                              <artifactId>lombok</artifactId>
                              <version>1.18.36</version> <!-- 确保版本匹配 -->
                          </path>
                      </annotationProcessorPaths>
                  </configuration>
              </plugin>
  
              <!-- Spring Boot Maven 插件，用于构建可执行的 Spring Boot JAR -->
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
                  <version>2.7.1</version> <!-- 确保版本与父项目一致 -->
                  <!-- 不需要排除 Lombok，除非有特定需求 -->
              </plugin>
          </plugins>
      </build>
  </project>
  
  ```

## 2.3 初始化数据

- 通过Navicat工具创建数据库`springboot`，并执行下面`mysql`语句，创建用户信息表`user_info`

![image-20241229194103478](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229194103478.png)

- `mysql`语句：

  ```mysql
  /*
   Navicat Premium Dump SQL
  
   Source Server         : 本地数据库
   Source Server Type    : MySQL
   Source Server Version : 80026 (8.0.26)
   Source Host           : localhost:3306
   Source Schema         : springboot
  
   Target Server Type    : MySQL
   Target Server Version : 80026 (8.0.26)
   File Encoding         : 65001
  
   Date: 29/12/2024 18:39:38
  */
  
  SET NAMES utf8mb4;
  SET FOREIGN_KEY_CHECKS = 0;
  
  -- ----------------------------
  -- Table structure for user_info
  -- ----------------------------
  DROP TABLE IF EXISTS `user_info`;
  CREATE TABLE `user_info`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
    `pass_word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
    `age` int NULL DEFAULT NULL,
    `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;
  
  -- ----------------------------
  -- Records of user_info
  -- ----------------------------
  INSERT INTO `user_info` VALUES (1, 'sam', 'password123', 32, 'M');
  INSERT INTO `user_info` VALUES (2, 'hah', 'password456', 10, 'F');
  
  SET FOREIGN_KEY_CHECKS = 1;
  ```

## 2.4 编写配置文件`application.yaml`

- 根据注释的指示修改对应的地方（数据库名、数据库用户名、数据库密码、实体类对应的包）

```yaml
server:
  port: 8080

spring:
  # HikariCP 连接池配置
  # 参考链接：https://www.cnblogs.com/goloving/p/14884802.html
  datasource:
    url: jdbc:mysql://localhost:3306/springboot?useSSL=false&autoReconnect=true&characterEncoding=utf8  
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root    # 改成自己的
    password: abc123 # 改成自己的
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
  mapper-locations: classpath:mappers/*Mapper.xml
  # 实体类的包路径
  type-aliases-package: com.zzx.user.repository.entity  # 修改成自己的
  configuration:
    # 自动下划线转驼峰
    map-underscore-to-camel-case: true
```

- 注意，由于在数据库的设计中，毛毛张对用户信息的用户名和密码使用的是下划线的方式，所以在配置文件中开启了自动下划线转驼峰`map-underscore-to-camel-case: true`，这是为了考察知识点，毛毛张特地设计的

## 2.5 创建User类

```java
package com.zzx.user.repository.entity;


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

## 2.6  编写controller | service | mapper

- controller类：查询所有用户信息

  ```java
  package com.zzx.user.controller;
  
  import com.zzx.user.converter.UserConverter;
  import com.zzx.user.model.vo.dto.UserInfoDTO;
  import com.zzx.user.reponse.ResVo;
  import com.zzx.user.repository.entity.User;
  import com.zzx.user.service.UserService;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;
  
  import javax.annotation.Resource;
  import java.util.List;
  
  @RestController
  @RequestMapping("user")
  public class UserController {
      @Resource
      private UserService userService;
  
      @GetMapping("queryAllUserInfo")  // 使用 GET 请求
      public ResVo<List<UserInfoDTO>> queryAllUserInfo() {
          List<User> userList = userService.queryAllUserInfo();
          List<UserInfoDTO> userInfoDTOList = UserConverter.toUserInfoDTOList(userList);
          return ResVo.success(userInfoDTOList);
      }
  }
  ```

- service类：

  ```java
  package com.zzx.user.service;
  
  import com.zzx.user.repository.entity.User;
  import org.springframework.stereotype.Service;
  
  import java.util.List;
  
  
  public interface UserService {
  
      List<User> queryAllUserInfo();
  }
  
  ```

- service实现类：

  ```java
  package com.zzx.user.service.impl;
  
  
  import com.zzx.user.repository.entity.User;
  import com.zzx.user.repository.mapper.UserMapper;
  import com.zzx.user.service.UserService;
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

- mapper接口：

  ```java
  package com.zzx.user.repository.mapper;
  
  
  import com.zzx.user.repository.entity.User;
  
  import java.util.List;
  
  public interface UserMapper {
      List<User> queryAllUserInfo();
  }
  ```

- mapper.xml：

  ```java
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "https://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <!-- namespace = mapper 对应接口的全限定符 -->
  <mapper namespace="com.zzx.user.repository.mapper.UserMapper">
      <!-- 声明标签写sql语句  crud  select  insert update  delete
            每个标签对应接口的一个方法！  方法的一个实现！
            注意：mapper接口不能重载！！！ 因为mapper.xml无法识别！ 根据方法名识别！
       -->
          <!-- 查询所有用户 -->
          <!-- <select id="queryAllUserInfo" resultType="com.zzx.user.repository.entity.User">-->
          <!--  我已经把实体包的路径写在了yaml文件的mybatis配置下了，所以下面可以直接写resultType="User"      -->
          <select id="queryAllUserInfo" resultType="User">
              SELECT
              id,
              user_name,
              pass_word,
              age,
              gender
              FROM user_info  <!-- 使用带有前缀的表名 -->
          </select>
  </mapper>
  ```

## 2.7 工具类

- 响应类ResVo：

  ```java
  package com.zzx.user.reponse;
  
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

- 前端对象返回类`UserInfoDTO`，在查询用户信息时通过是把查询到的结果封装在一个实体类中，但是返回给前端的信息不一定是用户的全部信息，例如用户的密码就不能直接返回给前端，或者不要返回，所以毛毛张设计了一个这个类：

  ```java
  package com.zzx.user.model.vo.dto;
  
  import lombok.Data;
  
  @Data
  public class UserInfoDTO {
      // 返回给前端的用户信息，不需要密码，同时要将性别转化成数字
      private Integer id;
      private String username;
      private Integer age;
      private Integer gender;
  }
  
  ```

- 为了节省传输的字符，毛毛张将用户的信息对应的内容转化成数字再返回给前端，因此设计了一个枚举类型`UserSexEnum`：

  ```java
  package com.zzx.user.model.enums;
  
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

- 由于`User`类和`UserInfoDTO`不是完全一一对应的，所以为了数据转换的方便，毛毛张再这里专门写了一个转换类`UserConverter`：

  ```java
  package com.zzx.user.converter;
  
  import com.zzx.user.model.enums.UserSexEnum;
  import com.zzx.user.model.vo.dto.UserInfoDTO;
  import com.zzx.user.repository.entity.User;
  
  import java.util.List;
  import java.util.stream.Collectors;
  
  public class UserConverter {
  
      // 单个转换
      public static UserInfoDTO toUserInfoDTO(User user) {
          UserInfoDTO userInfoDTO = new UserInfoDTO();
          userInfoDTO.setId(user.getId());
          userInfoDTO.setUsername(user.getUserName());
          userInfoDTO.setAge(user.getAge());
          userInfoDTO.setGender(UserSexEnum.getCodeByString(user.getGender()));
          return userInfoDTO;
      }
  
      // 批量转换
      public static List<UserInfoDTO> toUserInfoDTOList(List<User> users) {
          // 使用 Java 8 的 stream API 进行批量转换
          return users.stream()
                  .map(UserConverter::toUserInfoDTO)  // 对每个 User 对象进行转换
                  .collect(Collectors.toList());     // 收集成 List<UserInfoDTO>
      }
  }
  ```

## 2.8 拦截器-跨域资源共享CorsInterceptor

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

## 2.9 配置类WebConfig

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

## 2.10 最重要一步：修改启动类扫描Mapper接口

```java
package com.zzx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zzx.user.repository.mapper")  // 扫描 Mapper 接口所在的包
public class SpringbootMybatisDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootMybatisDemoApplication.class, args);
    }

}

```

## 2.11 编写测试类

- 测试类位于`test`文件夹下：

  ![image-20241229192950906](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229192950906.png)

- 测试类代码：

```java
package com.zzx;

import com.zzx.user.repository.entity.User;
import com.zzx.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@MapperScan("com.zzx.user.repository.mapper")  // 扫描 Mapper 接口所在的包
class SpringbootMybatisDemoApplicationTests {

    @Resource
    private UserService userService;

    @Test
    public void getAllUserInfo(){
        List<User> users = userService.queryAllUserInfo();
        users.forEach(System.out::println);
    }

}

```

## 2.12 使用Postman测试接口

- 使用Postman测试接口

![image-20241229200735053](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229200735053.png)

> 后端工程的搭建到此结束了，如果只想学习后端的人可以直接到此结束了

# 3.前端工程搭建

- 前端代码编写步骤：
   * 1.初始化项目：使用 Vite 创建 Vue 项目并进入项目目录。
   * 2.安装依赖：安装 Pinia、Vue Router、Axios 等必要的依赖。
   * 3.配置 Vite：编辑 vite.config.js，设置开发服务器和代理。
   * 4.配置 Axios：在 src/services/axios.js 中创建自定义 Axios 实例，并配置拦截器。
   * 5.设置 Pinia：创建 src/pinia.js，初始化 Pinia 并在 main.js 中引入。
   * 6.创建 Store：在 src/store/userStore.js 中定义用户相关的状态和操作。
   * 7.配置 Router：在 src/router/router.js 中定义路由规则和全局守卫。
   * 8.编写主入口文件：在 src/main.js 中创建 Vue 应用实例，挂载路由和 Pinia。
   * 9.创建根组件：编写 src/App.vue，主要负责渲染路由视图。
   * 10.创建展示组件：编写 src/components/ShowAllUserInfo.vue，展示用户信息并处理加载和错误状态。

## 3.1 前端项目搭建

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

- 由于前端项目还用到了`axios`（ajax异步请求封装技术实现前后端数据交互）、`pinia`(通过状态管理实现组件数据传递)、`router`（通过路由实现页面切换），因此还需要安装下面三个包：

  ```cmd
  npm install axios vue-router pinia
  ```

- 毛毛张搭建好的前端完整的文件目录如下：

![image-20241229202135283](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241229202135283.png)

## 3.2 修改配置文件vite.config.js

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

// 创建 Vue 应用实例，并通过链式调用注册插件和功能
export const app = createApp(App)
                    .use(router) // 注册路由
                    .use(pinia) // 注册 Pinia 状态管理
app.mount('#app'); // 挂载到 DOM 中的 #app 节点
```

## 3.4 修改主页App.vue

- 文件路径：`src/App.vue`

```java
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

## 3.5 用户信息展示逻辑showAllUserInfo.vue

- 文件路径：`src/components/showAllUserInfo.vue`

```javascript
<template>
  <div class="user-info">
    <h2>用户信息</h2>
    <table class="user-table">
      <thead>
        <tr>
          <th>用户名</th>
          <th>年龄</th>
          <th>性别</th>
        </tr>
      </thead>
      <tbody>
        <!-- 使用 v-for 循环显示多个用户，每行显示一个用户的属性 -->
        <tr v-for="user in userStore.users" :key="user.id">
          <td>{{ user.username }}</td>
          <td>{{ user.age }}</td>
          <td>{{ user.gender === 1 ? '男' : '女' }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../store/userStore'  // 引入 Pinia store

// 使用 Pinia store
const userStore = useUserStore()

// 计算属性，根据 gender 字段返回对应的文字
const genderText = computed(() => {
  return userStore.users.length && userStore.users[0].gender === 1 ? '男' : '女'
})

// 在组件挂载时获取用户信息
onMounted(() => {
  userStore.fetchUserInfo()  // 获取用户信息
})
</script>

<style scoped>
.b1b {
  background-color: #007bff;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin: 10px;
}

.b1b:hover {
  background-color: #0056b3;
}

.user-info {
  margin-top: 30px;
  text-align: center;  /* 使整个 .user-info 区域的内容居中 */
}

.user-info h2 {
  text-align: center;  /* 使标题居中 */
}

.user-table {
  width: 80%;
  margin: 0 auto;
  border-collapse: collapse;
}

.user-table th, .user-table td {
  padding: 8px 16px;
  border: 1px solid #ccc;
  text-align: center;
}

.user-table th {
  background-color: #f4f4f4;
}

.user-table tr:nth-child(even) {
  background-color: #f9f9f9;
}
</style>

```

## 3.6 数据传递与请求发送

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

- 文件路径：`/src/store/userStore.js`

```javascript
// 从 Pinia 中导入 defineStore 函数，用于创建一个新的 Store
import { defineStore } from 'pinia';

// 导入自定义的 Axios 实例，用于发起 HTTP 请求
import axios from '../services/axios';

// 使用 defineStore 创建一个名为 'userStore' 的 Store
export const useUserStore = defineStore('userStore', {
  // 定义 Store 的状态
  state: () => ({
    users: [],  // 用于存储多个用户的信息，初始为空数组
  }),

  // 定义 Store 的动作（方法）
  actions: {
    /**
     * 异步方法：获取所有用户信息
     * 该方法使用 Axios 实例发送 GET 请求到后端 API，
     * 并将返回的用户数据存储在 state 中的 users 数组里
     */
    async fetchUserInfo() {
      try {
        // 使用自定义的 Axios 实例发送 GET 请求到 '/user/queryAllUserInfo' 端点
        const response = await axios.get('/user/queryAllUserInfo');

        // 检查响应数据中的 code 是否为 200，表示请求成功
        if (response.data.code === 200) {
          // 假设返回的数据结构为 { code: 200, message: '成功', content: [...] }
          // 将返回的用户数组赋值给 state 中的 users
          this.users = response.data.content;
        } else {
          // 如果 code 不是 200，表示请求成功但业务逻辑失败
          // 打印错误信息到控制台，方便调试
          console.error('获取用户信息失败', response.data.message);
        }
      } catch (error) {
        // 捕捉并处理请求过程中发生的任何错误
        // 例如网络问题、服务器错误等
        console.error('请求失败', error);
      }
    },
  },
});

```

## 3.7 路由代码

- 文件路径：`/src/router/router.js`

```javascript
// 导入路由创建的相关方法
import { createRouter, createWebHashHistory,createWebHistory } from 'vue-router'



const routes = [
  {
    path: '/',
    redirect: '/user/queryAllUserInfo'  // 重定向到 /user/queryAllUserInfo 路由
  },
  {
    path: '/user/queryAllUserInfo',
    name: 'ShowAllUserInfo',
    component: () => import('../components/ShowAllUserInfo.vue') // 动态导入
  }
];



// 创建路由对象，声明路由规则
const router = createRouter({
  history: createWebHistory(),
  // history: createWebHashHistory(),
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

## 3.8 请求拦截处理

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

# 4.源代码

- 源代码可用访问毛毛张的github仓库：
  - 前端代码：https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mybatis/springboot-mybatis-demo-vue
  - 后端代码：https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-mybatis/springboot-mybatis-demo







