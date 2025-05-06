![QQ_1743599722821](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1743599722821.png)







[toc]

# 1.前言

- **毛毛张今天要分享的是 Spring 中最基础也是最关键的概念之一：AOP（面向切面编程）**。AOP 是一种编程范式，用于将横切关注点（如日志、事务管理、安全等）从业务逻辑中分离出来。它通过动态地将这些横切关注点插入到程序的执行流程中，提高了代码的模块化和可维护性。Spring AOP 是 Spring 框架中实现 AOP 的核心模块。
- **AOP 是 Spring 框架中的一个核心内容。在 Spring 中，AOP 代理可以用 JDK 动态代理或者 CGLIB 代理 CglibAopProxy 实现。Spring 中 AOP 代理由 Spring 的 IOC 容器负责生成和管理，其依赖关系也由 IOC 容器负责管理。**
  - **JDK 动态代理**：适用于实现了接口的类，通过动态生成代理类来实现 AOP 功能。
  - **CGLIB 代理**：适用于没有实现接口的类，通过生成目标类的子类来实现 AOP 功能。
  - **IOC 容器**：Spring 的核心组件，负责管理 Bean 的生命周期和依赖注入。AOP 代理的生成和管理也依赖于 IOC 容器。
- **AOP 有两种主要实现方式：Spring AOP 和 AspectJ**
  - **Spring AOP**：
    - 基于运行时增强，使用动态代理实现。
    - 适用于 Spring 容器中的 Bean，功能相对简单，适合轻量级 AOP 场景。
    - 性能稍逊于 AspectJ，因为需要在运行时生成代理实例。
  - **AspectJ**：
    - 基于编译时增强，通过字节码操作实现静态织入。
    - 功能更强大，支持更丰富的切点表达式和织入方式。
    - 性能更优，因为织入在编译时完成，运行时没有额外开销。
- **Spring AOP 已经集成了 AspectJ 的核心功能，因此在 Spring AOP 中可以使用 AspectJ 的注解来实现 AOP 功能。Spring AOP 的底层实现基于 AspectJ，但它提供了一种更简单、更轻量的方式来使用 AOP 功能。在 Spring AOP 中，你可以使用 AspectJ 的注解，如 `@Aspect`、`@Pointcut`、`@Before`、`@After`、`@Around` 等，来定义切面、切点和通知。**
- **毛毛张今天将分为两部分来介绍 Spring AOP：**
  - **第一部分是通过一个 Spring AOP 实现切面日志的快速入门案例来介绍如何使用 AOP**
  - **第二部分再来深入介绍 Spring AOP 的底层以及相关的核心概念和面试的八股**

# 2.SpringAOP实现切面日志快速入门

- 在介绍快速入门案例之前，我们先看下切面日志输出效果咋样：

![QQ_1743645145048](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1743645145048.png)

- 从上图中可以看到，每个对于每个请求，开始与结束一目了然，并且打印了以下参数：

  - **URL**: 请求接口地址；

  - **Description**: 接口的中文说明信息；

  - **HTTP Method**: 请求的方法，是 `POST`, `GET`, 还是 `DELETE` 等；

  - **Class Method**: 被请求的方法路径 : **包名 + 方法名**;

  - **IP**: 请求方的 IP 地址；

  - **Request Args**: 请求入参，以 JSON 格式输出；

  - **Response Args**: 响应出参，以 JSON 格式输出；

  - **Time-Consuming**: 请求耗时，以此估算每个接口的性能指数；

> 怎么样？看上去效果还不错呢？接下来看看，我们要如何一步一步实现它呢？

## 1.1 创建SpringBoot项目

- 创建一个SpringBoot项目，这里详细的步骤毛毛张就省略了，更多的关于如何快速创建一个`SpringBoot`新项目可以参见毛毛张的这篇博客：[【SpringBoot教程】IDEA快速搭建正确的SpringBoot版本和Java版本的项目](https://blog.csdn.net/weixin_48235955/article/details/144807998)
- 下面是毛毛张的创建的项目的完整结构目录：

![QQ_1743645903123](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1743645903123.png)

- 后端完整代码毛毛张以及上传至`Github`：<>

## 1.2 依赖配置`pom.xml`

- 下面是实现AOP的核心依赖：

  ```xml
  <!-- aop 依赖 -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-aop</artifactId>
  </dependency>
  ```

- 但是为了项目的完整性和我们的测试，还引入了其它的依赖，下面是完整的项目依赖：

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!-- 
      Maven 项目的根元素，定义了项目的坐标、依赖、构建配置等信息
      xmlns 和 xmlns:xsi 是命名空间声明，用于定义 POM 文件的 XML Schema
      xsi:schemaLocation 指定了 POM 文件的 Schema 位置，用于验证 POM 文件的正确性
  -->
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
      <!-- POM 文件的模型版本号，固定值 4.0.0 -->
      <modelVersion>4.0.0</modelVersion>
      <groupId>com.zzx</groupId>
      <artifactId>springboot-aop-demo</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      
      <name>springboot-aop-demo</name>
      
      <description>springboot-aop-demo</description>
  
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
  
      <!-- 
          定义项目的依赖，Maven 会自动下载这些依赖并添加到项目的类路径中
      -->
      <dependencies>
          <!-- Spring Boot Web 依赖，用于构建 Web 应用 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
  
          <!-- MySQL 数据库连接驱动 -->
          <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
          </dependency>
  
          <!-- Spring Boot AOP 依赖，用于实现面向切面编程 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-aop</artifactId>
          </dependency>
          
          <!-- Google Gson 库，用于 JSON 数据的序列化和反序列化 -->
          <dependency>
              <groupId>com.google.code.gson</groupId>
              <artifactId>gson</artifactId>
              <version>2.8.5</version>
          </dependency>
  
          <!-- Alibaba Fastjson 库，用于 JSON 数据的处理 -->
          <dependency>
              <groupId>com.alibaba</groupId>
              <artifactId>fastjson</artifactId>
              <version>1.2.58</version>
          </dependency>
  
          <!-- Spring Boot 开发工具，提供热部署等功能 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-devtools</artifactId>
              <scope>runtime</scope>
              <optional>true</optional>
          </dependency>
  
          <!-- Lombok 库，用于简化 Java 代码 -->
          <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <optional>true</optional>
          </dependency>
  
          <!-- Spring Boot 测试依赖，用于编写和运行测试用例 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-test</artifactId>
              <scope>test</scope>
          </dependency>
      </dependencies>
  
      <!-- 
          管理项目的依赖版本，确保所有模块使用相同的依赖版本
      -->
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
  
      <!-- 
          定义项目的构建配置，包括编译、打包、部署等步骤
      -->
      <build>
          <plugins>
              <!-- 
                  Maven 编译插件，用于编译 Java 代码
                  配置了 Java 版本和编码
              -->
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
              
              <!-- 
                  Spring Boot Maven 插件，用于打包可执行的 JAR 文件
                  配置了主类和是否跳过插件执行
              -->
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
                  <version>${spring-boot.version}</version>
                  <configuration>
                      <mainClass>com.zzx.SpringbootAopDemoApplication</mainClass>
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

## 1.3 自定义日志注解

- 首先自定义注解`WebLog`：

  ```java
  package com.zzx.aspect;
  import java.lang.annotation.*;
  /** 
  * @date 2023/10/6 
  * @time 下午9:19 
  * @discription 
  **/ 
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD})
  @Documented
  public @interface WebLog {
      /**
      * 日志描述信息
      * @return
      **/ 
      String description() default "";
  }
  ```

- 代码解释：
  - **`@Retention(RetentionPolicy.RUNTIME)`**：什么时候使用该注解，我们定义为运行时；
  - **`@Target({ElementType.METHOD})`**：注解用于什么地方，我们定义为作用于方法上；
  - **`@Documented`**：注解是否将包含在 JavaDoc 中；
  - **`public @interface WebLog `**：注解名为 `WebLog`;
  - **`String description() default "";`**：定义一个属性，默认为空字符串；

## 1.4 配置 AOP 切面

- 在配置 AOP 切面之前，我们需要了解下 `aspectj` 相关注解的作用：

  - **@Aspect**：声明该类为一个注解类；

  - **@Pointcut**：定义一个切点，后面跟随一个表达式，表达式可以定义为切某个注解，也可以切某个 package 下的方法；

- 切点定义好后，就是围绕这个切点做文章了：

  - **@Before**: 在切点之前，织入相关代码；

  - **@After**: 在切点之后，织入相关代码;

  - **@AfterReturning**: 在切点返回内容后，织入相关代码，一般用于对返回值做些加工处理的场景；

  - **@AfterThrowing**: 用来处理当织入的代码抛出异常后的逻辑处理;

  - **@Around**: 环绕，可以在切入点前后织入代码，并且可以自由的控制何时执行切点；

![image](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/2583030-20231008093707295-53308869.png)

- 切面完整代码：

  ```java
  package com.zzx.aspect;
  
  import com.google.gson.Gson;
  import org.aspectj.lang.JoinPoint;
  import org.aspectj.lang.ProceedingJoinPoint;
  import org.aspectj.lang.Signature;
  import org.aspectj.lang.annotation.*;
  import org.aspectj.lang.reflect.MethodSignature;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.core.annotation.Order;
  import org.springframework.stereotype.Component;
  import org.springframework.web.context.request.RequestContextHolder;
  import org.springframework.web.context.request.ServletRequestAttributes;
  
  import javax.servlet.http.HttpServletRequest;
  import java.lang.reflect.Method;
  
  /**
   * WebLogAspect 是一个面向切面编程（AOP）的类，用于记录 Web 请求的日志。
   * 它在方法执行前后以及环绕方法执行时记录日志，包括请求的 URL、方法描述、HTTP 方法、类方法、IP 地址、请求参数、响应参数和耗时等信息。
   * 该类使用 Spring 的 @Component 注解标记为一个 Spring 组件，并使用 @Aspect 注解标记为一个切面类。
   * @Order 注解定义了该切面的执行顺序，数字越小优先级越高。
   */
  @Aspect
  @Component
  @Order(1)
  public class WebLogAspect {
  
      /**
       * 定义一个日志记录器，用于记录 WebLogAspect 类的日志。
       */
      private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);
  
      /**
       * 定义一个系统换行符常量，用于格式化日志输出。
       */
      private static final String LINE_SEPARATOR = System.lineSeparator();
  
      /**
       * 定义一个切点，以自定义 @WebLog 注解为切点。
       * 该切点使用 @Pointcut 注解标记，表示匹配带有 @WebLog 注解的方法。
       */
      @Pointcut("@annotation(com.zzx.aspect.WebLog)")
      public void webLogPointcut() {
      }
  
      /**
       * 定义另一个切点，使用 execution 表达式拦截 com.zzx.controller 包及其子包下的所有方法。
       * 该切点也使用 @Pointcut 注解标记。
       */
      @Pointcut("execution(* com.zzx.controller..*.*(..))")
      public void controllerMethodsPointcut() {
      }
  
      /**
       * 在带有 @WebLog 注解的方法执行前执行的前置通知。
       * 该方法记录请求的详细信息，包括 URL、方法描述、HTTP 方法、类方法、IP 地址和请求参数。
       * @param joinPoint 切点对象，包含方法执行的上下文信息。
       */
      @Before("webLogPointcut()")
      public void doBeforeWithWebLog(JoinPoint joinPoint) {
          ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
          HttpServletRequest request = attributes.getRequest();
  
          String methodDescription = getMethodDescription(joinPoint);
  
          logger.info("========================================== Start (WebLog) ===========================================");
          logger.info("URL            : {}", request.getRequestURL());
          logger.info("Description    : {}", methodDescription);
          logger.info("HTTP Method    : {}", request.getMethod());
          logger.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
          logger.info("IP             : {}", request.getRemoteAddr());
          logger.info("Request Args   : {}", new Gson().toJson(joinPoint.getArgs()));
      }
  
      /**
       * 在 com.zzx.controller 包及其子包下的所有方法执行前执行的前置通知。
       * 该方法记录请求的详细信息，包括 URL、HTTP 方法、类方法、IP 地址和请求参数。
       * @param joinPoint 切点对象，包含方法执行的上下文信息。
       */
      @Before("controllerMethodsPointcut()")
      public void doBeforeWithControllerMethods(JoinPoint joinPoint) {
          ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
          HttpServletRequest request = attributes.getRequest();
  
          logger.info("========================================== Start (Controller Methods) ===========================================");
          logger.info("URL            : {}", request.getRequestURL());
          logger.info("HTTP Method    : {}", request.getMethod());
          logger.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
          logger.info("IP             : {}", request.getRemoteAddr());
          logger.info("Request Args   : {}", new Gson().toJson(joinPoint.getArgs()));
      }
  
      /**
       * 环绕通知，用于在带有 @WebLog 注解的方法执行前后执行。
       * 该方法记录方法的执行时间以及响应参数。
       * @param joinPoint 切点对象，包含方法执行的上下文信息。
       * @return 方法的返回值。
       * @throws Throwable 如果方法执行过程中抛出异常，该异常将被抛出。
       */
      @Around("webLogPointcut()")
      public Object doAroundWithWebLog(ProceedingJoinPoint joinPoint) throws Throwable {
          long startTime = System.currentTimeMillis();
          Object result = joinPoint.proceed();
  
          logger.info("Response Args  : {}", new Gson().toJson(result));
          logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
  
          return result;
      }
  
      /**
       * 环绕通知，用于在 com.zzx.controller 包及其子包下的所有方法执行前后执行。
       * 该方法记录方法的执行时间以及响应参数。
       * @param joinPoint 切点对象，包含方法执行的上下文信息。
       * @return 方法的返回值。
       * @throws Throwable 如果方法执行过程中抛出异常，该异常将被抛出。
       */
      @Around("controllerMethodsPointcut()")
      public Object doAroundWithControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
          long startTime = System.currentTimeMillis();
          Object result = joinPoint.proceed();
  
          logger.info("Response Args  : {}", new Gson().toJson(result));
          logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
  
          return result;
      }
  
      /**
       * 后置通知，用于在带有 @WebLog 注解的方法或 com.zzx.controller 包及其子包下的所有方法执行后执行。
       * 该方法记录一个结束标志。
       */
      @After("webLogPointcut() || controllerMethodsPointcut()")
      public void doAfter() {
          logger.info("=========================================== End ===========================================" + LINE_SEPARATOR);
      }
  
      /**
       * 获取方法的描述信息。
       * 该方法检查方法是否带有 @WebLog 注解，如果有，则返回注解中的描述信息；否则返回空字符串。
       * @param joinPoint 切点对象，包含方法执行的上下文信息。
       * @return 方法的描述信息。
       */
      private String getMethodDescription(JoinPoint joinPoint) {
          Method method = getMethod(joinPoint);
          if (method != null && method.isAnnotationPresent(WebLog.class)) {
              return method.getAnnotation(WebLog.class).description();
          }
          return "";
      }
  
      /**
       * 获取方法对象。
       * 该方法从切点对象中提取方法签名，并尝试获取对应的方法对象。
       * @param joinPoint 切点对象，包含方法执行的上下文信息。
       * @return 方法对象，如果获取失败则返回 null。
       */
      private Method getMethod(JoinPoint joinPoint) {
          try {
              Signature signature = joinPoint.getSignature();
              if (signature instanceof MethodSignature) {
                  MethodSignature methodSignature = (MethodSignature) signature;
                  return methodSignature.getMethod();
              }
              return null;
          } catch (Exception e) {
              logger.error("Failed to get method description", e);
              return null;
          }
      }
  }
  ```

## 1.5 怎么使用呢？

- 在上面的代码中，毛毛张定义了两种切点，一种是通过自定义注解 `@WebLog`，另一种是通过包名拦截所有方法。以下是详细的使用方法：

  - 使用自定义注解 `@WebLog` 作为切点
    - **定义切点**：在 `WebLogAspect` 类中，通过 `@Pointcut("@annotation(com.zzx.aspect.WebLog)")` 定义了一个切点，表示拦截所有带有 `@WebLog` 注解的方法。
    - **使用注解**：在 `Controller` 类的每个接口方法上添加 `@WebLog` 注解即可启用切面日志。如果不想为某个接口打印出入参日志，只需不添加该注解即可。
  - 使用包名拦截所有方法作为切点
    - **定义切点**：在 `WebLogAspect` 类中，通过 `@Pointcut("execution(* com.zzx.controller..*.*(..))")` 定义了一个切点，表示拦截 `com.zzx.controller` 包及其子包下的所有方法。
    - **使用切点**：无需在方法上添加任何注解，只要方法位于指定的包或子包中，就会被拦截并记录日志。

- 为此，毛毛张写了两个测试方法分别对应上面的两种切点的使用：

  ```java
  package com.zzx.controller;
  
  import com.zzx.aspect.WebLog;
  import com.zzx.entity.User;
  import org.springframework.web.bind.annotation.*;
  
  @RestController
  public class TestController {
  
      @GetMapping("/user/login")
      @WebLog(description = "登录接口")
      public String login(@RequestParam String username, @RequestParam String password) {
          User user = new User();
          user.setUsername(username);
          user.setPassword(password);
          return user.toString();
      }
  
  
      @GetMapping("/user/register")
      public String register(@RequestParam String username, @RequestParam String password) {
          User user = new User();
          user.setUsername(username);
          user.setPassword(password);
          return user.toString();
      }
  }
  ```

- 由于登陆接口既使用了`@WebLog`注解，同时也是通过包名拦截所有方法作为切点的包下，所以会输出两次日志；而注册日志则只会输出一次

## 1.6 实体类

- 实体类：

  ```java
  package com.zzx.entity;
  
  
  import lombok.Data;
  
  @Data
  public class User {
      private String username;
      private String password;
  }
  
  ```

## 1.7 启动类

- 启动类代码：

  ```java
  package com.zzx;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  
  @SpringBootApplication
  public class SpringbootAopDemoApplication {
  
      public static void main(String[] args) {
          SpringApplication.run(SpringbootAopDemoApplication.class, args);
      }
  
  }
  ```

## 1.8 测试

- 可以在浏览器依次发送如下请求：
  - <http://localhost:8080/user/login?username=mmzhang&password=123>
  - <http://localhost:8080/user/register?username=mmzhang&password=123>

- 可得到如下测试结果：

![QQ_1743647967352](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1743647967352.png)



## 1.9 只想在开发环境和测试环境中使用？

- 对于那些性能要求较高的应用，不想在生产环境中打印日志，只想在开发环境或者测试环境中使用，要怎么做呢？我们只需为切面添加 `@Profile` 就可以了，如下图所示：

![QQ_1743648050919](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1743648050919.png)

> 这样就指定了只能作用于 `dev` 开发环境和 `test` 测试环境，生产环境 `prod` 是不生效的！

## 1.10 多切面如何指定优先级？

- 假设说我们的服务中不止定义了一个切面，比如说我们针对 Web 层的接口，不止要打印日志，还要校验 token 等。要如何指定切面的优先级呢？也就是如何指定切面的执行顺序？
  - 我们可以通过 `@Order(i)`注解来指定优先级，注意：**i 值越小，优先级则越高**。

- 假设说我们定义上面这个日志切面的优先级为 `@Order(10)`, 然后我们还有个校验 `token` 的切面 `CheckTokenAspect.java`，我们定义为了 `@Order(11)`, 那么它们之间的执行顺序如下：
  ![image](https://img2023.cnblogs.com/blog/2583030/202310/2583030-20231008093849196-1011587486.png)

- 我们可以总结一下：

  - 在切点之前，`@Order` 从小到大被执行，也就是说越小的优先级越高；

  - 在切点之后，`@Order` 从大到小被执行，也就是说越大的优先级越高；

## 1.11 总结

- 上面代码毛毛张给出了详细的注释，完整的项目代码已经上传至毛毛张Github仓库：<>

# 3.Spring AOP

### 1.谈谈自己对于 AOP 的了解

AOP（面向切面编程）是一种编程范式，其核心思想是**通过模块化手段将横切关注点**（如日志记录、事务管理、权限控制、性能监控等）从业务逻辑中剥离，**从而实现关注点分离**。AOP 是面向对象编程（OOP）的补充和扩展。

这些与核心业务无关但被多个模块共同调用的功能称为"切面"，通过切面（Aspect）、切点（Pointcut）和通知（Advice）等机制进行封装：切面定义功能模块，切点定位目标方法，通知则描述**增强逻辑的执行时机**（如方法执行前/后/异常时）。

**为什么要用 AOP ？**：这种设计显著减少了系统重复代码，降低了模块耦合度，同时通过集中化管理共通逻辑，为系统的可维护性和可扩展性提供了结构化支持。Spring框架的AOP实现正是这一范式的典型应用，**使开发者能够在不侵入业务代码的前提下，灵活地为程序添加通用能力。**

AOP 切面编程涉及到的一些专业术语：

| **术语**                 | **含义**                                                     | **理解**                                                     |
| :----------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| **目标 (Target)**        | 被通知的对象，通常是业务逻辑的实现，是我们希望增强的对象。   | 业务逻辑本身，Spring AOP 通过代理模式实现，目标对象是被代理的对象。 |
| **代理 (Proxy)**         | 向目标对象应用通知后创建的代理对象，用于拦截目标对象的方法调用。 | Spring AOP 使用的代理对象，负责拦截目标对象的方法调用并执行通知逻辑。 |
| **切面 (Aspect)**        | 切入点和通知的结合，表示在哪些连接点执行什么样的通知逻辑。   | 切面就是通知和切入点的结合，定义了“在哪些地方干什么”。       |
| **连接点 (JoinPoint)**   | 目标对象的类中定义的所有方法，都是潜在的拦截点。             | Spring 允许你用通知的地方，方法的前前后后（包括抛出异常）。  |
| **切点 (Pointcut)**      | 从连接点中选择特定的点，用于定义哪些连接点会被通知拦截。     | 指定通知到哪个方法，说明“在哪干”。                           |
| **通知 (Advice)**        | 拦截到连接点后执行的逻辑，分为前置、后置、异常、最终、环绕通知五类。 | 我们要实现的功能，如日志记录、性能统计、事务处理等，说明“什么时候要干什么”。 |
| **织入 (Weaving)**       | 将通知应用到目标对象，生成代理对象的过程。可以在编译期、类装载期或运行期完成。 | 切点定义了哪些连接点会得到通知，织入是将通知逻辑插入到目标对象的过程。 |
| **引入 (Introduction)**  | 在运行期为类动态添加方法和字段。                             | 引入是在一个接口/类的基础上引入新的接口或功能，增强类的能力。 |
| **AOP 代理 (AOP Proxy)** | Spring AOP 使用的代理对象，可以是 JDK 动态代理或 CGLIB 代理。 | 通过代理对目标对象应用切面，代理对象负责拦截目标对象的方法调用并执行通知逻辑。 |

#### 切面织入有哪几种方式？

①编译期织入：切面在目标类编译时被织入。

②类加载期织入：切面在目标类加载到 JVM 时被织入。需要特殊的类加载器，它可以在目标类被引入应用之前增强该目标类的字节码。

③运行期织入：切面在应用运行的某个时刻被织入。一般情况下，在织入切面时，AOP 容器会为目标对象动态地创建一个代理对象。

Spring AOP 采用运行期织入，而 AspectJ 可以在编译期织入和类加载时织入。

> **形象的解释：织入就像电影特效**
>
> 想象一下，您正在制作一部电影。电影的原始拍摄内容（目标对象）已经完成，但您希望在后期制作中添加一些特效（切面逻辑），比如爆炸、魔法效果等。这些特效并不是原始拍摄的一部分，但它们可以增强电影的视觉效果。



#### AOP常见注解

- 在配置 AOP 切面之前，我们需要了解下 `aspectj` 相关注解的作用：

  - **@Aspect**：声明该类为一个注解类；

  - **@Pointcut**：定义一个切点，后面跟随一个表达式，表达式可以定义为切某个注解，也可以切某个 package 下的方法；

- 切点定义好后，就是围绕这个切点做文章了：

  - **@Before**: 在切点之前，织入相关代码；

  - **@After**: 在切点之后，织入相关代码;

  - **@AfterReturning**: 在切点返回内容后，织入相关代码，一般用于对返回值做些加工处理的场景；

  - **@AfterThrowing**: 用来处理当织入的代码抛出异常后的逻辑处理;

  - **@Around**: 环绕，可以在切入点前后织入代码，并且可以自由的控制何时执行切点；

#### AOP 有哪些环绕方式？AOP 常见的通知类型有哪些？

AOP 一般有 **5 种**环绕方式：

- 前置通知 (@Before)：目标对象的方法调用之前触发
- 后置通知 (@After)：目标对象的方法调用之后触发
- 环绕通知 (@Around)：编程式控制目标对象的方法调用。环绕通知是所有通知类型中可操作范围最大的一种，因为它可以直接拿到目标对象，以及要执行的方法，所以环绕通知可以任意的在目标对象的方法调用前后搞事，甚至不调用目标对象的方法
- 返回通知 (@AfterReturning)：目标对象的方法调用完成，在返回结果值之后触发
- 异常通知 (@AfterThrowing)：目标对象的方法运行中抛出 / 触发异常后触发。AfterReturning 和 AfterThrowing 两者互斥。如果方法调用成功无异常，则会有返回值；如果方法抛出了异常，则不会有返回值。

<img src="https://oss.javaguide.cn/github/javaguide/system-design/framework/spring/aspectj-advice-types.jpg" alt="img" style="zoom:50%;" />

#### AspectJ 是什么？

AspectJ 是一个 AOP 框架，它可以做很多 Spring AOP 干不了的事情，比如说支持编译时、编译后和类加载时织入切面。并且提供更复杂的切点表达式和通知类型。

#### Spring AOP 发生在什么时候？

Spring AOP 基于运行时代理机制，这意味着 Spring AOP 是在运行时通过动态代理生成的，而不是在编译时或类加载时生成的。

在 Spring 容器初始化 Bean 的过程中，Spring AOP 会检查 Bean 是否需要应用切面。如果需要，Spring 会为该 Bean 创建一个代理对象，并在代理对象中织入切面逻辑。这一过程发生在 Spring 容器的后处理器（BeanPostProcessor）阶段。

#### 简单总结一下 AOP

AOP，也就是面向切面编程，是一种编程范式，旨在提高代码的模块化。比如说可以将日志记录、事务管理等分离出来，来提高代码的可重用性。

AOP 的核心概念包括切面（Aspect）、连接点（Join Point）、通知（Advice）、切点（Pointcut）和织入（Weaving）等。

① 像日志打印、事务管理等都可以抽离为切面，可以声明在类的方法上。像 `@Transactional` 注解，就是一个典型的 AOP 应用，它就是通过 AOP 来实现事务管理的。我们只需要在方法上添加 `@Transactional` 注解，Spring 就会在方法执行前后添加事务管理的逻辑。

② Spring AOP 是基于代理的，它默认使用 JDK 动态代理和 CGLIB 代理来实现 AOP。

③ Spring AOP 的织入方式是运行时织入，而 AspectJ 支持编译时织入、类加载时织入。

#### AOP和 OOP 的关系？

AOP 和 OOP 是互补的编程思想：

1. OOP 通过类和对象封装数据和行为，专注于核心业务逻辑。
2. AOP 提供了解决横切关注点（如日志、权限、事务等）的机制，将这些逻辑集中管理。

### 2.多个切面的执行顺序如何控制？

**1、通常使用`@Order` 注解直接定义切面顺序**

```java
// 值越小优先级越高
@Order(3)
@Component
@Aspect
public class LoggingAspect implements Ordered {
```

**2、实现`Ordered` 接口重写 `getOrder` 方法。**

```java
@Component
@Aspect
public class LoggingAspect implements Ordered {

    // ....

    @Override
    public int getOrder() {
        // 返回值越小优先级越高
        return 1;
    }
}
```

### 3.动态代理和静态代理的区别

- 代理是一种常用的设计模式，目的是：为其他对象提供一个代理以控制对某个对象的访问，将两个类的关系解耦。代理类和委托类都要实现相同的接口，因为代理真正调用的是委托类的方法。

- 区别：

  - 静态代理：由程序员创建或者是由特定工具创建，在代码编译时就确定了被代理的类是一个静态代理，静态代理通常只代理一个类

    ```java
    // 接口
    interface UserService {
        void saveUser();
    }
    
    // 目标类
    class UserServiceImpl implements UserService {
        public void saveUser() {
            System.out.println("保存用户");
        }
    }
    
    // 静态代理类（手动编写）
    class UserServiceProxy implements UserService {
        private UserService target;
    
        public UserServiceProxy(UserService target) {
            this.target = target;
        }
    
        public void saveUser() {
            System.out.println("执行前增强"); // 增强逻辑
            target.saveUser();               // 调用目标方法
            System.out.println("执行后增强"); // 增强逻辑
        }
    }
    ```

  - 动态代理：在代码运行期间，运用反射机制动态创建生成，动态代理代理的是一个接口下的多个实现类。

    ```java
    // 接口和目标类同上（略）
    
    // 动态代理处理器
    class MyInvocationHandler implements InvocationHandler {
        private Object target;
    
        public MyInvocationHandler(Object target) {
            this.target = target;
        }
    
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("执行前增强"); // 增强逻辑
            Object result = method.invoke(target, args); // 调用目标方法
            System.out.println("执行后增强"); // 增强逻辑
            return result;
        }
    }
    
    // 使用动态代理
    UserService proxy = (UserService) Proxy.newProxyInstance(
        UserServiceImpl.class.getClassLoader(),
        new Class[]{UserService.class},
        new MyInvocationHandler(new UserServiceImpl())
    );
    proxy.saveUser(); // 输出：增强 + 保存用户
    ```

### 4.说说 JDK 动态代理和 CGLIB 代理？

AOP 是通过动态代理实现的，代理方式有两种：JDK 动态代理和 CGLIB 代理。

**①、JDK 动态代理是基于接口的代理，只能代理实现了接口的类。**

使用 JDK 动态代理时，Spring AOP 会创建一个代理对象，该代理对象实现了目标对象所实现的接口，并在方法调用前后插入横切逻辑。

优点：只需依赖 JDK 自带的 `java.lang.reflect.Proxy` 类，不需要额外的库；缺点：只能代理接口，不能代理类本身。

**②、CGLIB 动态代理是基于继承的代理，可以代理没有实现接口的类。**使用 CGLIB 动态代理时，**Spring AOP 会生成目标类的子类，并在方法调用前后插入横切逻辑。**

<img src="https://cdn.tobebetterjavaer.com/stutymore/spring-20240321105653.png" alt="图片来源于网络" style="zoom:50%;" />

优点：可以代理没有实现接口的类，灵活性更高；缺点：需要依赖 CGLIB 库，创建代理对象的开销相对较大。

**JDK 动态代理示例代码：**

```java
public interface Service {
    void perform();
}

public class ServiceImpl implements Service {
    public void perform() {
        System.out.println("Performing service...");
    }
}

public class ServiceInvocationHandler implements InvocationHandler {
    private Object target;

    public ServiceInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before method");
        Object result = method.invoke(target, args);
        System.out.println("After method");
        return result;
    }
}

public class Main {
    public static void main(String[] args) {
        Service service = new ServiceImpl();
        Service proxy = (Service) Proxy.newProxyInstance(
            service.getClass().getClassLoader(),
            service.getClass().getInterfaces(),
            new ServiceInvocationHandler(service)
        );
        proxy.perform();
    }
}
```

**CGLIB 动态代理示例代码：**

```java
public class Service {
    public void perform() {
        System.out.println("Performing service...");
    }
}

public class ServiceInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("Before method");
        Object result = proxy.invokeSuper(obj, args);
        System.out.println("After method");
        return result;
    }
}

public class Main {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Service.class);
        enhancer.setCallback(new ServiceInterceptor());

        Service proxy = (Service) enhancer.create();
        proxy.perform();
    }
}
```

#### 选择 CGLIB 还是 JDK 动态代理？

- 如果目标对象没有实现任何接口，则只能使用 CGLIB 代理。如果目标对象实现了接口，通常首选 JDK 动态代理。
- 虽然 CGLIB **在代理类的生成过程中可能消耗更多资源，但在运行时具有较高的性能**。对于性能敏感且代理对象创建频率不高的场景，可以考虑使用 CGLIB。
- JDK 动态代理是 Java 原生支持的，不需要额外引入库。而 CGLIB 需要将 CGLIB 库作为依赖加入项目中。

#### 你会用 JDK 动态代理和 CGLIB 吗？

①、JDK 动态代理实现：

<img src="https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/sidebar/sanfene/spring-65b14a3f-2653-463e-af77-a8875d3d635c.png" alt="三分恶面渣逆袭：JDK动态代理类图" style="zoom:50%;" />

第一步，创建接口

```java
public interface ISolver {
    void solve();
}
```

第二步，实现对应接口

```java
public class Solver implements ISolver {
    @Override
    public void solve() {
        System.out.println("疯狂掉头发解决问题……");
    }
}
```

第三步，动态代理工厂:ProxyFactory，直接用反射方式生成一个目标对象的代理，这里用了一个匿名内部类方式重写 InvocationHandler 方法。

```java
public class ProxyFactory {

    // 维护一个目标对象
    private Object target;

    public ProxyFactory(Object target) {
        this.target = target;
    }

    // 为目标对象生成代理对象
    public Object getProxyInstance() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("请问有什么可以帮到您？");

                        // 调用目标对象方法
                        Object returnValue = method.invoke(target, args);

                        System.out.println("问题已经解决啦！");
                        return null;
                    }
                });
    }
}
```

第五步，客户端生成一个代理对象实例，通过代理对象调用目标对象方法

```java
public class Client {
    public static void main(String[] args) {
        //目标对象:程序员
        ISolver developer = new Solver();
        //代理：客服小姐姐
        ISolver csProxy = (ISolver) new ProxyFactory(developer).getProxyInstance();
        //目标方法：解决问题
        csProxy.solve();
    }
}
```

②、CGLIB 动态代理实现：

<img src="https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/sidebar/sanfene/spring-74da87af-20d1-4a5b-a212-3837a15f0bab.png" alt="三分恶面渣逆袭：CGLIB动态代理类图" style="zoom:50%;" />

第一步：定义目标类（Solver），目标类 Solver 定义了一个 solve 方法，模拟了解决问题的行为。目标类不需要实现任何接口，这与 JDK 动态代理的要求不同。

```java
public class Solver {

    public void solve() {
        System.out.println("疯狂掉头发解决问题……");
    }
}
```

第二步：动态代理工厂（ProxyFactory），ProxyFactory 类实现了 MethodInterceptor 接口，这是 CGLIB 提供的一个方法拦截接口，用于定义方法的拦截逻辑。

```java
public class ProxyFactory implements MethodInterceptor {

    //维护一个目标对象
    private Object target;

    public ProxyFactory(Object target) {
        this.target = target;
    }

    //为目标对象生成代理对象
    public Object getProxyInstance() {
        //工具类
        Enhancer en = new Enhancer();
        //设置父类
        en.setSuperclass(target.getClass());
        //设置回调函数
        en.setCallback(this);
        //创建子类对象代理
        return en.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("请问有什么可以帮到您？");
        // 执行目标对象的方法
        Object returnValue = method.invoke(target, args);
        System.out.println("问题已经解决啦！");
        return null;
    }

}
```

- ProxyFactory 接收一个 Object 类型的 target，即目标对象的实例。
- 使用 CGLIB 的 Enhancer 类来生成目标类的子类（代理对象）。通过 setSuperclass 设置代理对象的父类为目标对象的类，setCallback 设置方法拦截器为当前对象（this），最后调用 create 方法生成并返回代理对象。
- 重写 MethodInterceptor 接口的 intercept 方法以提供方法拦截逻辑。在目标方法执行前后添加自定义逻辑，然后通过 method.invoke 调用目标对象的方法。

第三步：客户端使用代理，首先创建目标对象（Solver 的实例），然后使用 ProxyFactory 创建该目标对象的代理。通过代理对象调用 solve 方法时，会先执行 intercept 方法中定义的逻辑，然后执行目标方法，最后再执行 intercept 方法中的后续逻辑。

```java
public class Client {
    public static void main(String[] args) {
        //目标对象:程序员
        Solver developer = new Solver();
        //代理：客服小姐姐
        Solver csProxy = (Solver) new ProxyFactory(developer).getProxyInstance();
        //目标方法：解决问题
        csProxy.solve();
    }
}
```

### 5.说说 Spring AOP 和 AspectJ AOP 区别?

1. **实现机制**：
   - **Spring AOP** 是基于 **运行时增强** 的动态代理技术，依赖于 Spring 容器。如果目标对象实现了接口，Spring AOP 使用 **JDK 动态代理**；如果没有实现接口，则使用 **Cglib** 生成目标对象的子类作为代理。
   - **AspectJ AOP** 是基于 **编译时增强** 的**字节码操作技术，通过修改字节码实现静态织入**。AspectJ 可以单独使用，也可以与 Spring 集成。
2. **织入时机**：
   - Spring AOP 是运行时动态织入。
   - AspectJ 支持多种织入时机：
     - **编译期织入**：在编译时修改字节码。如类 A 使用 AspectJ 添加了一个属性，类 B 引用了它，这个场景就需要编译期的时候就进行织入，否则没法编译类 B。
     - **编译后织入**：对已生成的 `.class` 文件或 `.jar` 包进行增强。
     - **类加载后织入**：在类加载时动态增强。
3. **性能对比**：
   - Spring AOP 是动态代理，运行时会增加方法调用的栈深度，性能稍逊于 AspectJ。
   - AspectJ 是静态织入，运行时没有额外开销，性能更优，尤其在切面较多时表现更好。
4. **功能对比**：
   - Spring AOP 功能相对简单，主要解决企业级开发中常见的方法织入问题。
   - AspectJ 功能更强大，支持更丰富的切点表达式和织入方式，适合复杂的 AOP 场景。
5. **使用场景**：
   - 如果切面逻辑简单且数量较少，Spring AOP 足够使用。
   - 如果切面逻辑复杂或数量较多，建议使用 AspectJ。
6. **集成关系**：
   - Spring AOP 已经集成了 AspectJ，开发者可以在 Spring 中同时使用两者。

整体对比如下：

![Spring AOP和AspectJ对比](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/sidebar/sanfene/spring-d1dbe9d9-c55f-4293-8622-d9759064d613.png)

### 6.说说 AOP 和反射的区别？

1. 反射：用于检查和操作类的方法和字段，动态调用方法或访问字段。反射是 Java 提供的内置机制，直接操作类对象。
2. 动态代理：**通过生成代理类来拦截方法调用**，通常用于 AOP 实现。动态代理使用反射来调用被代理的方法。

### 7.AOP的使用场景有哪些？日志记录、事务管理、权限控制、性能监控

AOP 的使用场景有很多，比如说**日志记录、事务管理、权限控制**、性能监控等。

# 参考文献

- https://www.cnblogs.com/shenMaQN/p/17748150.html
- https://developer.aliyun.com/article/723070
- https://blog.csdn.net/Fzyabc/article/details/142882899
- 沉默王二面渣逆袭
- 小林coding