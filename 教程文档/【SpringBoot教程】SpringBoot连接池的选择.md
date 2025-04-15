





























#### 2.1.2.2 创建SpringBoot工程并导入依赖

- 创建工程：略

- 导入依赖

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
  
      <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>3.0.5</version>
      </parent>
      <groupId>com.atguigu</groupId>
      <artifactId>springboot-starter-mybatis-plus-06</artifactId>
      <version>1.0-SNAPSHOT</version>
  
      <properties>
          <maven.compiler.source>17</maven.compiler.source>
          <maven.compiler.target>17</maven.compiler.target>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
      </properties>
      
      <dependencies>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter</artifactId>
          </dependency>
          
          <!-- 测试环境 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-test</artifactId>
          </dependency>
          
          <!-- mybatis-plus  -->
          <dependency>
              <groupId>com.baomidou</groupId>
              <artifactId>mybatis-plus-boot-starter</artifactId>
              <version>3.5.3.1</version>
          </dependency>
  
          <!-- 数据库相关配置启动器 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-jdbc</artifactId>
          </dependency>
  
          <!-- druid启动器的依赖  -->
          <dependency>
              <groupId>com.alibaba</groupId>
              <artifactId>druid-spring-boot-3-starter</artifactId>
              <version>1.2.18</version>
          </dependency>
  
          <!-- 驱动类-->
          <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
              <version>8.0.28</version>
          </dependency>
  
          <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <version>1.18.28</version>
          </dependency>
  
      </dependencies>
          <!--    SpringBoot应用打包插件-->
      <build>
          <plugins>
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
              </plugin>
          </plugins>
      </build>
  </project>
  ```

#### 2.1.2.3 编写配置文件和启动类

- 配置文件

  - 完善连接池配置：

    - 在`resources`下创建文件夹：`META-INF.spring`
    - 并在`META-INF.spring`文件夹下创建文件名：`org.springframework.boot.autoconfigure.AutoConfiguration.imports`
    - 文件内容：`com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure`

  - 配置`application.yaml`文件

    ```yaml
    # 连接池配置
    spring:
      datasource:
        type: com.alibaba.druid.pool.DruidDataSource
        druid:
          url: jdbc:mysql:///day01
          username: root
          password: root
          driver-class-name: com.mysql.cj.jdbc.Driver
    ```

- 编写启动类

  ```java
  @MapperScan("com.atguigu.mapper")
  @SpringBootApplication
  public class MainApplication {
  
      public static void main(String[] args) {
          SpringApplication.run(MainApplication.class,args);
      }
      
  }
  ```























# 参考文献