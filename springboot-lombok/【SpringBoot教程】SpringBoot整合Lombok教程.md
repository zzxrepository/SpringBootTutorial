> **<font color="green">今天毛毛张分享的加快SpringBoot开发效率的`Lombok`插件以及`SpringBoot`内置的日志系统Logback的介绍</font>**



[toc]







# 1 Lombok使用教程

## 1.1 Lombok简介

- 在Java开发中，我们经常需要创建创建实体类以及编写对应的`getter`、`setter`等方法，这些代码虽然简单，但是但却非常繁琐，而且容易出错，而`Lombok`的出现解决了这个问题

- `Lombok`是一个Java类库，它可以通过注解自动生成样板代码，例如：`getter`、`setter`、构造方法、`equals()`、`hashCode()`等，从而减少代码冗余，使得代码变得简洁易读，提高开发效率，让我们能够更专注于业务逻辑的实现。

## 1.2 整合Lombok

### 1.2.1 添加Lombok依赖

- 在`SpringBoot`项目的`pom.xml`文件中添加依赖：其中`scope=provided`，就说明`Lombok`只在编译阶段生效，这表明`Lombok`会在编译期静悄悄地将带 `Lombok`注解的源码文件正确编译为完整的`class`文件

  ```xml
  <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>1.18.36</version>
      <scope>provided</scope> 
  </dependency>
  ```

- 由于`SpringBoot`早在`2.1.x`版本后就在`starter`中内置了`Lombok`依赖，因此不需要显示添加，可以通过下面方式添加：

  ```xml
  <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional> <!-- optional 设置为 true 表示该依赖是可选的，不会传递到依赖项目中，避免冲突 -->
  </dependency>
  ```

### 1.2.2 安装Lombok插件

- 为了使`IDEA`能够正确识别`Lombok`生成的代码，需要安装`Lombok`插件，`Intellij IDEA`也早在`IDEA 2020.3`版本的时候内置了`Lombok`插件，可以通过如下方式查看，如果没有的话可以通过下面两种方式安装。

![QQ_1736432674072](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736432674072.png)

- 方式1(推荐)：在`Setting`中选择`Plugins`，并在对话框中搜索`Lombok`，点击安装即可，安装完毕点击右下角`Apply`，上图为毛毛张以及安装完毕之后的图
- 方式2：直接从IDEA插件库官网进行下载：<https://plugins.jetbrains.com/plugin/6317-lombok>，然后放在`IDEA`安装文件下面的`plugins`文件夹里面，然后重启`IDEA`

![QQ_1736432328174](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736432328174.png)

![QQ_1736432448219](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736432448219.png)



### 1.2.3 为什么Lombok添加了依赖还要安装插件？

- Lombok是一个Java库，它通过注解（如`@Getter`, `@Setter`, `@Data`, `@Builder`等）自动生成常见的Java类方法（例如getter、setter、toString、equals、hashCode等），从而减少冗长的代码编写。在使用Lombok的过程中，编译器（如`javac`）会在编译阶段生成这些方法，而不是在源代码中显示定义。

- 然而，Lombok的注解会在编译时生成字节码中的相关方法，但源代码中并不包含这些方法的定义。由于IDE（如IntelliJ IDEA或Eclipse）依赖源代码来进行静态分析和代码补全，IDE无法直接看到这些生成的getter和setter方法，因此可能会报错或提示找不到相关方法。

- 为了解决这个问题，Lombok提供了插件，安装该插件后，IDE能够理解Lombok的注解，正确显示和处理这些生成的代码，避免提示错误或缺少方法的问题。这使得在使用Lombok时，IDE能够正常地进行代码补全、跳转到定义和其他代码分析功能。

## 1.3 常用的Lombok注解 

### 3.1 @Getter / @Setter

- 这两个注解用于生成`getter`和`setter`方法。如果属性是`final`的，通常不需要`setter`方法
- @Getter / @Setter 用起来很灵活，既可以作用于类、接口或者枚举类型上，也可以作用在属性名上

- 方式1：

  ```java
  package com.zzx.entity;
  
  import lombok.Data;
  import lombok.Getter;
  import lombok.Setter;
  
  @Getter
  @Setter
  public class User {
  
      private Long id;
  
      private String name;
  
      private Integer age;
  }
  ```

- 方式2：

  ```java
  package com.zzx.entity;
  
  import lombok.Getter;
  import lombok.Setter;
  
  public class User {
      
      @Setter
      @Getter
      private Long id;
      
      @Getter
      @Setter
      private String name;
      
      @Getter
      @Setter
      private Integer age;
  }
  
  ```

- 上面两种方式字节码文件反编译后的内容都是下面的内容：

  ```java
  package com.zzx.entity;
  
  import lombok.Getter;
  import lombok.Setter;
  
  public class User {
      
      private Long id;
      private String name;
      private Integer age;
  
      // Lombok 会自动生成下面的 getter 和 setter 方法
  
      public Long getId() {
          return id;
      }
  
      public void setId(Long id) {
          this.id = id;
      }
  
      public String getName() {
          return name;
      }
  
      public void setName(String name) {
          this.name = name;
      }
  
      public Integer getAge() {
          return age;
      }
  
      public void setAge(Integer age) {
          this.age = age;
      }
  }
  ```

### 3.2 @ToString

- 自动生成`toString`方法，可以作用于类或者接口上

- 案例：

  ```java
  package com.zzx.entity;
  
  import lombok.Data;
  import lombok.Getter;
  import lombok.Setter;
  import lombok.ToString;
  
  @ToString
  public class User {
  
      private Long id;
  
      private String name;
  
      private Integer age;
  }
  ```

- 字节码文件反编译后的内容是：

  ```java
  package com.zzx.entity;
  
  import lombok.ToString;
  
  @ToString
  public class User {
  
      private Long id;
      private String name;
      private Integer age;
  
      // Lombok 会自动生成 toString 方法
      @Override
      public String toString() {
          return "User(id=" + id + ", name=" + name + ", age=" + age + ")";
      }
  }
  ```

### 3.3 @Data

- 该注解是`Lombok`提供的一个非常方便的注解，它等价于`@Setter`、`@Getter`、`@RequiredArgsConstructor`、`@ToString`、`@EqualsAndHashCode`这几个注解的总和

- 当你在代码中使用Lombok的`@Data`注解时，它会自动为你的类生成以下几个常见方法：
  - **`@Getter`**：为所有字段生成getter方法。
  - **`@Setter`**：为所有字段生成setter方法。
  - **`@ToString`**：为类生成`toString()`方法。
  - **`@EqualsAndHashCode`**：为类生成`equals()`和`hashCode()`方法。
  - **`@RequiredArgsConstructor`**：生成带有`final`字段或`@NonNull`注解字段的构造函数。

- 案例：

  ```java
  package com.zzx.entity;
  
  import lombok.Data;
  
  @Data
  public class User {
  
      private Long id;
  
      private String name;
  
      private Integer age;
  }
  
  ```

- 字节码文件反编译后的内容是：

  ```java
  package com.zzx.entity;
  
  import lombok.Data;
  
  @Data
  public class User {
  
      private Long id;
      private String name;
      private Integer age;
  
      // Lombok 会自动生成 getter 和 setter 方法
  
      public Long getId() {
          return id;
      }
  
      public void setId(Long id) {
          this.id = id;
      }
  
      public String getName() {
          return name;
      }
  
      public void setName(String name) {
          this.name = name;
      }
  
      public Integer getAge() {
          return age;
      }
  
      public void setAge(Integer age) {
          this.age = age;
      }
  
      // Lombok 会自动生成 toString 方法
      @Override
      public String toString() {
          return "User(id=" + id + ", name=" + name + ", age=" + age + ")";
      }
  
      // Lombok 会自动生成 equals 方法
      @Override
      public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          User user = (User) o;
          return id != null && id.equals(user.id) && name != null && name.equals(user.name) && age != null && age.equals(user.age);
      }
  
      // Lombok 会自动生成 hashCode 方法
      @Override
      public int hashCode() {
          return Objects.hash(id, name, age);
      }
  
      // Lombok 会自动生成无参构造函数（默认无参构造是隐式生成的）
  }
  ```

### 3.4 @NoArgsConstructor、@AllArgsConstructor和@RequiredArgsConstructor

- 这三个注解用于生成构造方法：
  - `@NoArgsConstructor`：生成无参构造方法。
  - `@AllArgsConstructor`：生成包含所有属性的构造方法。
  - `@RequiredArgsConstructor`：生成带有`final`字段或`@NonNull`注解字段的构造函数。

- 案例：

  ```java
  package com.zzx.entity;
  
  import lombok.AllArgsConstructor;
  import lombok.NoArgsConstructor;
  import lombok.RequiredArgsConstructor;
  
  @NoArgsConstructor
  @AllArgsConstructor
  @RequiredArgsConstructor
  public class User {
  
      private Long id;
  
      private String name;
  
      private Integer age;
  }
  ```

### 3.5 通常的案例

- 因此通常创建一个实体类如下：

  ```java
  package com.zzx.entity;
  
  import lombok.*;
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public class User {
  
      private Long id;
  
      private String name;
  
      private Integer age;
  }
  ```

### 3.6 val

- `val`是`Lombok`提供的一个宏指令，用于简化`Java`中的局部变量声明，可以让你省略变量类型的显式声明，自动推断类型。这类似于` JavaScript`中的`var`，可以使代码更加简洁。
- 使用场景：在你的代码中，`val`用来声明变量，而`Lombok`会自动根据右侧的赋值推断变量的类型

- 案例：

  ```java
  package com.zzx.entity;
  
  import lombok.*;
  
  import java.util.ArrayList;
  
  public class User {
      public static void main(String[] args) {
          val names = new ArrayList<String>();
          names.add("神马都会亿点点的毛毛张");
          val name = names.get(0);
          System.out.println(name);
      }
  }
  ```

- 字节码文件反编译后的内容是：

  ```java
  package com.zzx.entity;
  
  import lombok.*;
  
  import java.util.ArrayList;
  
  public class User {
      public static void main(String[] args) {
          ArrayList<String> names = new ArrayList();
  		names.add("神马都会亿点点的毛毛张");
  		String name = (String) names.get(0);
  		System.out.println(name);
      }
  }
  ```

### 3.7 @Log

- Lombok 提供了 `@Log` 注解系列，用于简化日志记录的创建过程。通过这些注解，你可以在 Java 类中快速生成日志对象，并且不需要显式地初始化日志实例。
- **Lombok 提供了六种不同的日志实现，方便你根据项目中使用的日志框架选择合适的注解，只需要在类上添加适当的注解，Lombok 就会为你自动生成一个名为 `log` 的日志对象，你可以直接使用它记录日志，而不需要手动创建日志实例**

| 注解          | 生成的日志对象类型                    | 依赖框架                         |
| ------------- | ------------------------------------- | -------------------------------- |
| `@CommonsLog` | `org.apache.commons.logging.Log`      | Apache Commons Logging           |
| `@Log`        | `java.util.logging.Logger`            | Java标准库的 `java.util.logging` |
| `@Log4j`      | `org.apache.log4j.Logger`             | Apache Log4j 1.x                 |
| `@Log4j2`     | `org.apache.logging.log4j.LogManager` | Apache Log4j 2.x                 |
| `@Slf4j`      | `org.slf4j.Logger`                    | SLF4J                            |
| `@XSlf4j`     | `org.slf4j.ext.XLogger`               | SLF4J 扩展 (`slf4j-ext`)         |

- 但是更常用的是`@Slf4j`注解，下面简单介绍一下该注解的用法

### 3.8 @Slf4j

- `@Slf4j`可以用来生成注解对象，通过使用 `@Slf4j` 注解，你不需要手动声明和初始化日志对象，`Lombok`会在编译时为你自动生成 `log` 变量，并使用`SLF4J`提供的 `LoggerFactory` 进行初始化

- 案例：

  ```java 
  package com.zzx.entity;
  
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  
  @Slf4j
  public class Log4jDemo {
      public static void main(String[] args) {
          log.info("level:{}","info");
          log.warn("level:{}","warn");
          log.error("level:{}", "error");
      }
  }
  ```

- 字节码文件反编译后的内容是：

  ```java
  package com.zzx.entity;
  
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  
  public class Log4jDemo {
  
      // Lombok 会自动生成这个字段
      private static final Logger log = LoggerFactory.getLogger(Log4jDemo.class);
  
      public static void main(String[] args) {
          // 使用 log 进行日志输出
          log.info("level:{}", "info");
          log.warn("level:{}", "warn");
          log.error("level:{}", "error");
      }
  }
  
  ```

### 3.9 @Builder

- 使用  `@Builder` 注解，Lombok 会自动为你的类生成一个内嵌的构建器类，这个构建器类允许你以流式 API 的方式设置字段值，并最终构建出目标对象

- 更详细的介绍可以参见官网：<https://projectlombok.org/features/Builder>

- 案例：

  ```java
  import lombok.Builder;
  
  @Builder
  @ToString
  public class Person {
      private String name;
      private int age;
      private String address;
  }
  ```

- 字节码文件反编译后的内容是：

  ```java
  public class PersonBuilder {
      private String name;
      private int age;
      private String address;
  
      public PersonBuilder name(String name) {
          this.name = name;
          return this;
      }
  
      public PersonBuilder age(int age) {
          this.age = age;
          return this;
      }
  
      public PersonBuilder address(String address) {
          this.address = address;
          return this;
      }
  
      public Person build() {
          return new Person(name, age, address);
      }
  }
  ```

- 我们就可以通过下面这样的构建起来创建`Person`对象

  ```java
  public class Main {
      public static void main(String[] args) {
          Person person = Person.builder()
                  .name("John Doe")
                  .age(30)
                  .address("123 Main St")
                  .build();
  
          System.out.println(person);
      }
  }
  ```

### 3.10 其它注解

- `@Accessors`：用在类上，必须与`@Getter/@Setter`连用，用于指定生成的get/set方法的格式，`chain = true`属性声明为链式调用，即生成的set方法不返回`void`而返回`this`；`fluent = true`生成的get/set方法不包含get/set前缀
- `@Value`：用在类上，是@Data的不可变形式，相当于为属性添加final声明，只提供getter方法，而不提供setter方法
- `@SneakyThrows`：自动抛受检异常，而无需显式在方法上使用throws语句
- `@Synchronized`：用在方法上，将方法声明为同步的，并自动加锁，而锁对象是一个私有的属性`$lock`或`$LOCK`，而java中的synchronized关键字锁对象是this，锁在this或者自己的类对象上存在副作用，就是你不能阻止非受控代码去锁this或者类对象，这可能会导致竞争条件或者其它线程错误

## 1.4 Lombok工作原理

- 在使用 **Lombok** 时，开发者只需要为类或字段添加相应的注解，无需编写额外的代码。那么，Lombok 如何生成这些代码呢？其核心在于注解的解析机制。自 **JDK 5** 引入注解以来，Java 提供了两种注解解析方式：**运行时解析** 和 **编译时解析**

- **运行时解析**：对于需要在运行时解析的注解，必须将 `@Retention` 设置为 `RUNTIME`，这样才能通过反射机制获取注解信息。Java 提供了 `java.lang.reflect` 包来支持反射，而 `AnnotatedElement` 接口定义了获取注解信息的相关方法。所有 `Class`、`Constructor`、`Field`、`Method` 和 `Package` 等元素都实现了这个接口，可以通过它们获取注解数据。

- **编译时解析**：编译时解析分为以下两种机制：

  - **Annotation Processing Tool (APT)**：自 **JDK 5** 引入，提供了一个基础的注解处理机制。`APT` 被设计为在编译时处理注解，并生成相应代码。然而，`APT` 从 **JDK 7** 开始标记为过时，并且在 **JDK 8** 中彻底删除。它被 `Pluggable Annotation Processing API`（也就是 **JSR 269**）所取代。`APT` 被弃用的主要原因包括：

    - `APT` 的 API 位于 `com.sun.mirror` 包下，这个包并不是标准包。

    - 它未与 `javac` 集成，需要额外的运行步骤。

  - **Pluggable Annotation Processing API**：作为 **APT** 的替代方案，`JSR 269` 引入于 **JDK 6**，并成为 Java 标准的一部分。通过实现 `JSR 269`，开发者可以在编译过程中为 `javac` 增强处理逻辑。`javac` 在执行过程中会调用所有注册的注解处理器，这些处理器可以在编译过程中修改源代码或生成新的文件，其简化流程图如下：

![QQ_1736476330877](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736476330877.png)

- **Lombok** 本质上就是一个实现了 **JSR 269 API** 的注解处理器。在编译过程中，Lombok 通过以下步骤进行代码生成：
  - **`javac` 分析源代码**，并生成抽象语法树（AST）
  - **Lombok 调用其注解处理器**，对 `javac` 生成的 AST 进行修改
  - **Lombok 处理 AST**，查找标记有 `@Data` 等注解的类，并在 AST 中为该类添加生成 `getter`、`setter` 等方法的相应代码节点
  - **`javac` 使用修改后的 AST** 来生成字节码文件，最终将新增的方法代码嵌入到对应的类文件中，生成新的 `class` 文件

![image-20241014192352733](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241014192352733.png)



# 2 Logback日志

- 对于一个应用程序来说，日志记录是必不可少的。日志不仅用于线上问题追踪，还支持基于日志的业务逻辑统计分析等功能。因此，选择合适的日志框架对于保障应用程序的稳定性和可维护性至关重要。
- 在`Java`领域，存在多种日志框架：
  - `Commons Logging`：**Commons Logging**是Apache基金会的一个项目，提供了一套Java日志接口。最初名为**Jakarta Commons Logging**，后更名为**Commons Logging**。它的设计目的是作为各种日志框架的门面，允许开发者在不更改代码的情况下切换底层日志实现。
  - `SLF4J (Simple Logging Facade for Java)`：**SLF4J**类似于Commons Logging，是一套简易的Java日志门面。它本身不提供日志的具体实现，而是通过绑定（bindings）与实际的日志框架（如Logback、Log4j等）集成，提供了一致的日志接口，简化了日志框架的切换和配置。
  - `Log4j`：**Log4j**是一个基于Java的日志记录工具，由Ceki Gülcü首创，目前由Apache软件基金会维护。Log4j是Java日志框架中的经典之一，提供了灵活的日志记录功能和丰富的配置选项。
  - `Log4j 2`： **Log4j 2**是Log4j的升级版本，旨在解决Log4j 1.x的一些性能和功能上的限制。Log4j 2引入了异步日志记录、插件架构和更高的性能，成为更现代和高效的日志解决方案。
  - `Logback`：**Logback**是由Ceki Gülcü开发的日志框架，被认为是Log4j的继任者。它天然支持SLF4J，不需要像Log4j和JUL那样添加适配层。Logback提供了更高的性能、更丰富的功能以及更灵活的配置选项，是现代Java应用程序中广泛使用的日志框架之一。
  - `Java Util Logging (JUL)`：**JUL**是Java自1.4版本以来的官方日志实现。作为Java标准库的一部分，JUL无需额外的依赖，但在功能和性能上相比其他第三方日志框架略显不足。

- `SpringBoot`作为目前流行的`Java`后端开发框架，也提供了一个强大的内置日志系统，帮助开发者在开发和部署应用程序时轻松记录重要信息和错误。具体特点包括支持多种日志记录器，如`Logback`和`Log4j2`，开发者可以根据具体需求，通过配置文件灵活选择和配置日志记录器。
- **默认情况下，Spring Boot 使用 Logback 作为日志记录器，并将所有日志输出到控制台，为开发者提供了开箱即用的日志功能。此外，Spring Boot 还支持将日志记录到文件中，方便后续的分析和查看，这对于生产环境中的问题排查尤为重要。**

## 2.1 Logback简介

- **选择 Logback 作为日志框架有以下几个原因：**
  - `Log4j`、`SLF4J`和`Logback`这三者实际上是亲兄弟，它们的创始人都是 Ceki Gülcü。这保证了它们之间的兼容性和协同工作能力。
  - 由于`Log4j2`曾经爆出严重的漏洞，出于安全考虑，许多项目选择使用`Logback`作为替代方案，尤其是在`Spring Boot`默认使用`Logback`的情况下。
  - `Logback`无需额外的适配层即可与`SLF4J`无缝集成，简化了日志系统的配置和维护。
- `Logback`完美实现了`SLF4J（Simple Logging Facade for Java）`，无需像`Log4j`和`JUL`那样增加适配层，简化了日志配置和管理，如下图所示

<img src="https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241014193231520.png" alt="image-20241014193231520" style="zoom:50%;" />



- `Logback`分为三个模块：
  - **logback-core**：核心模块，提供了关键的通用机制，是其他两个模块的基础。
  - **logback-classic**：可以看作是`Log4j`的改进版，实现了完整的`SLF4J API`，使您可以轻松地切换到其他日志系统，如`Log4j`或`JDK14 Logging`
  - **logback-access**：主要用于与`Servlet`容器（如`Tomcat`）集成，提供通过`HTTP`访问日志的功能。


## 2.2 自定义日志打印

### 2.2.1 导入依赖

- 方式1：在`SpringBoot`项目中，当引入`spring-boot-starter`依赖时，则默认导入`spring-boot-starter-logging`的依赖，相继也就导入了`Logback`所需要的依赖

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
  </dependency>
  ```

![img](https://img2020.cnblogs.com/blog/1771072/202011/1771072-20201101195356759-1799026575.png)

- **方式2（推荐）：** 当构建`JavaWeb`应用程序时，导入`spring-boot-starter-web`依赖，也就导入了`spring-boot-starter-logging`的依赖，所以我们只需要导入`spring-boot-starter-web`依赖就行

  ```xml
  <!-- Spring Boot Starter Web -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  ```

![img](https://ask.qcloudimg.com/http-save/yehe-7813369/wwgbryf7n5.png)

### 2.2.2 获取日志对象

- 推荐方式2，需要导入`Lombok`依赖和安装`Lombok`插件，具体的步骤可以看上一章

#### 2.2.2.1 方式1

```java
package com.zzx.controller;

import org.slf4j.Logger;//必须导入这个包
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user") // 建议指定一个基础路径
public class UserController {

    //方式1：获取日志对象（当前类的日志）
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
    ....
}
```

- 注意：
  - 日志对象`Logger`是属于`org.slf4j`包下的，`Spring Boot` 中内置了日志框架 `Slf4j`，可以让开发人员使用一致的`API`来调用不同的日志记录框架
  - 一般每个类都有自己的日志文件，`getLogger`方法参数推荐为当前这个类，并且这个日志文件不能轻易修改，所以是`private static final`
  - `Logger`和`LoggerFactory`都来自`SLF4J`，所以如果项目是从`Log4j + SLF4J`切换到`Logback`的话，此时的代码是零改动的

#### 2.2.2.2 方式2：@Slf4j

- 如果每个接口类或者其它的类需要记录日志信息的时候，每次都需要通过上面的方式去创建日志对象，虽然代码很简单，但是每次都要写这么上面那一长串，这是很麻烦的，因此`Lombok`依赖提供了注解`@Slf4j`，帮我们省去了每次都要创建日志对象的麻烦

- **使用方法：**

  - **只需要在需要记录日志的类的上面加上`@Slf4j`注解就等同于`private static final Logger log = LoggerFactory.getLogger(XXXController.class);`**
  - **`Lombok`会在编译时为你自动生成 `log` 变量，并使用`SLF4J`提供的 `LoggerFactory` 进行初始化，因此在下面调用对象打印日志的时候，对象名为`log`**

- 案例：

  ```java
  package com.zzx.controller;
  
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;
  
  @Slf4j  //方式2：使用Lombok提供的注解，不需要我们自己创建日志对象了
  @RestController
  @RequestMapping("/user") // 建议指定一个基础路径
  public class UserController {
      
      ....
  }
  ```

### 2.2.3 使用对象打印日志

- `Logger` 提供了很多的方法用来打印日志

- 代码实例：

  ```java
  package com.zzx.controller;
  
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;
  
  @Slf4j
  @RestController
  @RequestMapping("/user") // 建议指定一个基础路径
  public class UserController {
      
      @GetMapping("/hi")
      public String hi() { // 方法名首字母小写
          // 打印日志的方法
          log.trace("我是trace级别日志");
          log.debug("我是debug级别日志");
          log.info("我是info级别日志");
          log.warn("我是warn级别日志");
          log.error("我是error级别日志");
  
          return "Hello World!";
      }
  }
  ```

- 启动程序后，并在浏览器访问`http://localhost:8080/user/hi`即可看见我们自定义的日志信息了

![image-20250110153742745](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20250110153742745.png)

![QQ_1736494686795](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736494686795.png)

> 当我们访问带日志接口之后会看到我们自定义的打印日志才会出现，这才能体现日志的作用嘛；既然都是打印日志，为什么会提供这么多的方法呢？打印出来的内容都是什么意思呢？上面明明写了5个方法，为什么只打印了3个呢？后面毛毛张都会一一解答

### 2.2.4 默认日志格式

- **默认输出的日志信息如下：**
  - 日期和时间：精确到毫秒级别
  - 日志级别：INFO，【日志级别默认从高到低：ERROR，WARN，INFO，DEBUG，TRACE】
  - 进程ID
  - 分隔符：来标识实际日志消息的开始
  - 线程名：用方括号括起来(在控制台输出时可能被截断)
  - 日志记录器名称：这通常是要记录日志的类的源代码的类名
  - 日志内容
- 当我们启动`SpingBoot`应用时，控制台将会显示`INFO`级别的日志输出

![QQ_1736496686865](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736496686865.png)



## 2.3 日志的级别

- 为什么我明明写了5个方法，为什么只打印了3个呢？这就是与日志级别相关了。

### 2.3.1 日志级别的种类

- `SpringBoot`日志的级别用于控制输出日志的详细程度，每种不同的日志等级对应一组不同的日志信息，级别越高，输出的日志信息就越详细。各种日志级别的含义如下：

  - `trace`：微量，少许的意思，级别最低；

  - `debug`：需要调试时候的关键信息打印；

  - `info`：普通的打印信息（默认⽇志级别）；

  - `warn`：警告，不影响使用，但需要注意的问题；

  - `error`：错误信息，级别较⾼的错误⽇志信息；

  - `fatal`：致命的，因为代码异常导致程序退出执⾏的事件

- `SpringBoot`中日志级别从低到高依次为：`trace < debug < info < warn < error < fatal`

- 日志输出规则：`SpringBoot`的日志默认输出级别为`info`，只有当前级别及其更高级别的日志会被输出（`fatal`级别的日志不会输出）。**上面提到的5种方法对应不同的日志级别**，需要注意的是，`fatal()`方法并不存在，因为一旦日志级别为`fatal`，程序将终止。（这也解释了为什么虽然列出了5个方法，但实际上只打印了3个的原因）。

### 2.3.2 自定义日志的级别

- 我们可以在`SpringBoot`的配置文件`application.yaml`中通过如下方式指定日志的输出级别：

  ```yaml
  # 自定义日志级别
  logging
    level
      root: debug  # 对根目录设置
  ```

- 我们在配置文件中修改日志级别为`error`，再次访问该接口，就可以看见只有`error`级别及以上的日志信息可以输出了

![QQ_1736498413704](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736498413704.png)

![QQ_1736498366521](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736498366521.png)

- 上面是对根路径设置级别，还可以分别对不同的目录进行同时设置：

  ```yaml
  # 自定义日志级别
  logging
    level
      root: error  # 对根目录设置
      com.zzx.controller: trace   # 对controller目录设置一个级别
  # 它们不会冲突，除了 com.zzx.controller 下为 trace，其它地方都为error
  ```

![QQ_1736498460311](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736498460311.png)

> 解释：由于其它目录下的日志的级别被定义为`error`，由于程序没有报错，所以不会出现日志，当访问`http://localhost:8080/user/hi`接口时，可以看见该目录下的设置的`trace`级别及以上的日志信息输出



## 2.4 日志的持久化

- 持久化就是把日志放在硬盘上，存储为日志文件，这一步是非常重要的

### 2.4.1 配置日志文件存放路径

- 在配置文件中通过如下方式设置日志文件存放的路径

  ```yaml
  logging:
    file:
      path: ./logs/  # 设置日志文件保存的目录
  ```

- **注意事项：**

  - 可以不用自己去创建这个路径，`SpringBoot`程序启动后会自动创建，通常情况下日志文件可以在项目目录下单独创建一个名为`logs`的文件夹存放
  - 如果不指定文件名，那么日志信息则自动保存在默认创建的`spring.log`文件中
  - **如果不修改路径，程序再次启动，日志会追加在`spring.log`文件中，而不是覆盖**

- 案例演示：

  - 修改配置：

    ```yaml
    # 自定义日志级别
    logging:
      level:
        root: info  # 对根目录设置
      file:
        path: ./logs/  # 设置日志文件保存的目录
    ```

  - 效果展示：

![QQ_1736503239638](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736503239638.png)



![QQ_1736499858755](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736499858755.png)

### 2.4.2 配置日志文件名

- 上面设置了日志文件保存路径，如果不设置日志文件名，那么会默认创建名为`spring.log`的日志文件，我们还可以通过下面的属性来配置日志文件名：

  ```yaml
  # 自定义日志级别
  logging:
    level:
      root: info  # 对根目录设置
    file:
      name: ./logs/mySpring.log   # 配置日志文件名
  ```

- 启动`SpringBoot`应用之后会自动创建如下图所示日志文件

![QQ_1736502990987](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736502990987.png)

### 2.4.3 配置日志文件存储最大容量

- 如果程序运行时间周期很长(比如生产环境)，那么日志一定是非常多的，用一个文件来存储显然不现实，那么如何让它自动地分成多个文件呢？

  - `SpringBoot`默认设置日志文件达到10 MB时会触发滚动策略【切分】，默认情况下会记录INFO以上级别的信息，可以使用`logging.file.max-size`属性更改大小限制。 除非已设置`logging.file.max-history`属性，否则默认情况下**将保留最近7天的轮转日志文件**。 
  - 可以使用`logging.file.total-size-cap`限制日志归档文件的总大小。 当日志归档的总大小超过该阈值时，将删除备份。
  - 要在应用程序启动时强制清除日志存档，请使用`logging.file.clean-history-on-start`属性

- 我们可以通过下面的属性来配置日志文件保存的最大容量：

  ```yaml
  # 自定义日志级别
  logging:
    level:
      root: info  # 对根目录设置
    file:
      name: ./logs/mySpring.log   # 配置日志文件名
    logback:
      rollingpolicy:
        max-file-size: 10KB #设置日志大小的最大大小 1KB（一般不会这么小，这里用于演示）
  ```

- 说明：这个配置项用于指定一个日志文件的最大大小，支持的单位包括 `KB`、`MB`、`GB` 等，当日志文件达到指定大小后，将自动创建一个新的日志文件来继续记录日志信息
- 案例演示：还是上面的代码，经过多次发送请求之后，可以得到下面所示日志文件

![QQ_1736503371118](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736503371118.png)



## 2.5 自定义Log配置

- 虽然毛毛张介绍的默认日志配置对于大多数情况很有用，但在实际开发中可能无法满足所有需求，因此可以自定义日志配置文件。

- 日志服务通常在`ApplicationContext`创建之前就已初始化，因此不一定需要通过Spring的配置文件来控制。可以通过系统属性或传统的Spring Boot外部配置文件来管理日志。

- 根据你使用的日志系统，可以选择以下方式来配置日志：

  | **Logging System**          | **Customization**                                            |
  | :-------------------------- | :----------------------------------------------------------- |
  | **Logback**                 | `logback-spring.xml`, `logback.xml`, `logback-spring.groovy`, `logback.groovy` |
  | **Log4j2**                  | `log4j2-spring.xml`, `log4j2.xml`                            |
  | **JDK (Java Util Logging)** | `logging.properties`                                         |

- 本文将重点介绍`Logback`日志的配置，Spring Boot官方推荐使用带有`-spring`后缀的配置文件（例如`logback-spring.xml`），而不是`logback.xml`。原因是`logback.xml`会在较早阶段加载，无法使用Spring的扩展功能。使用`logback-spring.xml`可以利用Spring Boot的Profile功能，为不同环境（如开发、测试、生产）提供不同的配置项。

> 如果你希望使用自定义的文件名或路径，可以通过`logging.config`属性进行配置，例如：`logging.config=classpath:logging-config.xml`

### logback-spring.xml

- 接下来分享一份详细的`logback-spring.xml`配置，注释中写明了每个部分的含义，大家可以参考并了解。生产环境下的Logback配置基本如此，可以根据需要进行微调`APP_NAME`和`LOG_PATH`（应用名称随便你自己；日志保存的路径这里使用的`${user.home}/${APP_NAME}/logs`表明保存在用户目录下）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="10 seconds" debug="false">
    <!--
    scan：当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true。
    scanPeriod：设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒，当scan为true时，此属性生效。默认的时间间隔为10秒。
    debug：当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。
    -->

    <!-- 引入Spring Boot默认日志配置 -->
    <include resource="org/springframework/boot/logging/logback/defaults.xml" />

    <!-- 配置日志文件的基本属性 -->
    <!-- name的值是变量的名称，value的值时变量定义的值。通过定义的值会被插入到logger上下文中。定义变量后，可以使“${}”来使用变量。 -->
    <property name="APP_NAME" value="ProjectName" />
    <property name="LOG_PATH" value="${user.home}/${APP_NAME}/logs" />
    <property name="LOG_FILE" value="${LOG_PATH}/${APP_NAME}.log" />

    <!-- 控制台日志输出格式定义（带颜色） -->
    <property name="console_log_pattern" value="%black(%contextName-) %red(%d{yyyy-MM-dd HH:mm:ss}) %green([%thread]) %highlight(%-5level) %boldMagenta(%logger{36}) - %gray(%msg%n)"/>
    <property name="charset" value="UTF-8"/>

    <!-- 文件日志输出格式定义（不带颜色） -->
    <property name="file_log_pattern" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [ %thread ] - [ %-5level ] [ %logger{50} : %line ] - %msg%n"/>

    <!-- 定义输出到控制台的appender（带颜色） -->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <!--此日志appender是为开发使用，只配置最底级别，控制台输出的日志级别是大于或等于此级别的日志信息-->
        <!-- 例如：如果此处配置了INFO级别，则后面其他位置即使配置了DEBUG级别的日志，也不会被输出 -->
         <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
             <level>DEBUG</level>
         </filter>
        <encoder>
            <!-- 使用带颜色的日志格式 -->
            <pattern>${console_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
    </appender>

    <!-- 定义输出到INFO级别日志文件的appender（不带颜色） -->
    <appender name="info_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 每天日志归档路径以及格式 -->
            <fileNamePattern>${LOG_PATH}/info/log-info-%d{yyyy-MM-dd}.%i.log</fileNamePattern> <!-- 按日期和序号滚动 -->
            <maxFileSize>100MB</maxFileSize> <!-- 每个文件最大100MB -->
            <maxHistory>15</maxHistory> <!-- 保留最近15个历史文件 -->
            <totalSizeCap>1GB</totalSizeCap> <!-- 总大小上限1GB -->
        </rollingPolicy>
        <encoder>
            <!-- 使用不带颜色的日志格式 -->
            <pattern>${file_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
        <!-- 仅记录INFO级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 定义输出到WARN级别日志文件的appender（不带颜色） -->
    <appender name="warn_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/warn/log-warn-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>15</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${file_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
        <!-- 仅记录WARN级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 定义输出到ERROR级别日志文件的appender（不带颜色） -->
    <appender name="error_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/error/log-error-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>15</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${file_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
        <!-- 仅记录ERROR级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 定义应用程序日志的appender（不带颜色） -->
    <appender name="APPLICATION" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>50MB</maxFileSize>
            <maxHistory>7</maxHistory>
            <totalSizeCap>500MB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${file_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
    </appender>

    <!-- 处理日志滚动的最大历史日志数量（不带颜色） -->
    <appender name="roll_log" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>10</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${file_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
    </appender>

    <!--
    	root节点是必选节点，用来指定最基础的日志输出级别，只有一个level属性
    	level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，默认是DEBUG
    	可以包含零个或多个appender元素。
    -->
    <root level="info">
        <!-- 将日志输出到控制台 -->
        <appender-ref ref="console" />
        <!-- 将日志输出到INFO级别的文件 -->
        <appender-ref ref="info_file" />
        <!-- 将日志输出到WARN级别的文件 -->
        <appender-ref ref="warn_file" />
        <!-- 将日志输出到ERROR级别的文件 -->
        <appender-ref ref="error_file" />
        <!-- 将日志输出到APPLICATION appender -->
        <appender-ref ref="APPLICATION" />
        <!-- 将日志输出到roll_log appender -->
        <appender-ref ref="roll_log" />
    </root>
    
    <!--
        <logger>用来设置某一个包或者具体的某一个类的日志打印级别、以及指定<appender>。
        <logger>仅有一个name属性，
        一个可选的level和一个可选的additivity属性。
        name:用来指定受此logger约束的某一个包或者具体的某一个类。
        level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，
              如果未设置此属性，那么当前logger将会继承上级的级别。
        additivity:是否向上级logger传递打印信息,默认是true
    -->
    <!-- 使用mybatis的时候，sql语句是debug下才会打印，而这里我们只配置了info，所以想要查看sql语句的话，有以下两种操作：
         第一种把<root level="INFO">改成<root level="DEBUG">这样就会打印sql，不过这样日志那边会出现很多其他消息
         第二种就是单独给mapper下目录配置DEBUG模式，代码如下，这样配置sql语句会打印，其他还是正常DEBUG级别：
    -->
    <logger name="com.hyh.logback.web.LogTestController" level="WARN" additivity="false">
        <appender-ref ref="console"/>
        <appender-ref ref="warn_file"/>
        <appender-ref ref="error_file"/>
    </logger>

    <!-- 使用Spring Profile为不同环境提供不同的日志配置 -->
    <springProfile name="dev">
        <!-- 开发环境下的日志设置 -->
        <!--可以输出项目中的debug日志，包括mybatis的sql日志-->
        <logger name="com.hyh.logback.web" level="DEBUG">
            <!-- 输出日志到控制台 -->
            <appender-ref ref="console"/>
        </logger>
        <!-- 在dev环境中root日志级别设置为DEBUG -->
        <!--
            root节点是必选节点，用来指定最基础的日志输出级别，只有一个level属性
            level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，默认是DEBUG
            可以包含零个或多个appender元素。
        -->
        <root level="DEBUG">
            <!-- 输出日志到控制台 -->
            <appender-ref ref="console"/>
        </root>
    </springProfile>
</configuration>
```

# 3 完整案例

- 创建初始化项目，毛毛张就省略了，直接介绍相关代码，**毛毛张依旧是通过一个完整的前后端任务来教大家如何整合整合Lombok，同时使用使用Logback日志**
- 下面是毛毛张的创建的项目的完整结构目录：

![QQ_1736513562218](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736513562218.png)

- 后端完整代码毛毛张以及上传至`Github`：<https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-lombok/springboot-lombok-demo>

## 3.1 后端代码

### 3.1.1 依赖配置`pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.zzx</groupId>
    <artifactId>springboot-lombok-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>springboot-lombok-demo</name>
    <description>springboot-lombok-demo</description>

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

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

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
                    <mainClass>com.zzx.SpringbootLombokDemoApplication</mainClass>
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

### 3.1.2 日志配置文件编写

- 毛毛张在这里就简单的修改了一下`APP_NAME`、`LOG_PATH`，项目名根据自己的实际来改，日志路径毛毛张保存在项目目录下，所以去掉了前面的

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="10 seconds" debug="false">
    <!--
    scan：当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true。
    scanPeriod：设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒，当scan为true时，此属性生效。默认的时间间隔为10秒。
    debug：当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。
    -->

    <!-- 引入Spring Boot默认日志配置 -->
    <include resource="org/springframework/boot/logging/logback/defaults.xml" />

    <!-- 配置日志文件的基本属性 -->
    <!-- name的值是变量的名称，value的值时变量定义的值。通过定义的值会被插入到logger上下文中。定义变量后，可以使“${}”来使用变量。 -->
    <property name="APP_NAME" value="ApplicationName" />
    <property name="LOG_PATH" value="logs" />
    <property name="LOG_FILE" value="${LOG_PATH}/${APP_NAME}.log" />

    <!-- 控制台日志输出格式定义（带颜色） -->
    <property name="console_log_pattern" value="%black(%contextName-) %red(%d{yyyy-MM-dd HH:mm:ss}) %green([%thread]) %highlight(%-5level) %boldMagenta(%logger{36}) - %gray(%msg%n)"/>
    <property name="charset" value="UTF-8"/>

    <!-- 文件日志输出格式定义（不带颜色） -->
    <property name="file_log_pattern" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [ %thread ] - [ %-5level ] [ %logger{50} : %line ] - %msg%n"/>

    <!-- 定义输出到控制台的appender（带颜色） -->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <!--此日志appender是为开发使用，只配置最底级别，控制台输出的日志级别是大于或等于此级别的日志信息-->
        <!-- 例如：如果此处配置了INFO级别，则后面其他位置即使配置了DEBUG级别的日志，也不会被输出 -->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>DEBUG</level>
        </filter>
        <encoder>
            <!-- 使用带颜色的日志格式 -->
            <pattern>${console_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
    </appender>

    <!-- 定义输出到INFO级别日志文件的appender（不带颜色） -->
    <appender name="info_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 每天日志归档路径以及格式 -->
            <fileNamePattern>${LOG_PATH}/info/log-info-%d{yyyy-MM-dd}.%i.log</fileNamePattern> <!-- 按日期和序号滚动 -->
            <maxFileSize>100MB</maxFileSize> <!-- 每个文件最大100MB -->
            <maxHistory>15</maxHistory> <!-- 保留最近15个历史文件 -->
            <totalSizeCap>1GB</totalSizeCap> <!-- 总大小上限1GB -->
        </rollingPolicy>
        <encoder>
            <!-- 使用不带颜色的日志格式 -->
            <pattern>${file_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
        <!-- 仅记录INFO级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 定义输出到WARN级别日志文件的appender（不带颜色） -->
    <appender name="warn_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/warn/log-warn-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>15</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${file_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
        <!-- 仅记录WARN级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 定义输出到ERROR级别日志文件的appender（不带颜色） -->
    <appender name="error_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/error/log-error-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>15</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${file_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
        <!-- 仅记录ERROR级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 定义应用程序日志的appender（不带颜色） -->
    <appender name="APPLICATION" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>50MB</maxFileSize>
            <maxHistory>7</maxHistory>
            <totalSizeCap>500MB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${file_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
    </appender>

    <!-- 处理日志滚动的最大历史日志数量（不带颜色） -->
    <appender name="roll_log" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>10</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${file_log_pattern}</pattern>
            <charset>${charset}</charset>
        </encoder>
    </appender>

    <!--
    	root节点是必选节点，用来指定最基础的日志输出级别，只有一个level属性
    	level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，默认是DEBUG
    	可以包含零个或多个appender元素。
    -->
    <root level="info">
        <!-- 将日志输出到控制台 -->
        <appender-ref ref="console" />
        <!-- 将日志输出到INFO级别的文件 -->
        <appender-ref ref="info_file" />
        <!-- 将日志输出到WARN级别的文件 -->
        <appender-ref ref="warn_file" />
        <!-- 将日志输出到ERROR级别的文件 -->
        <appender-ref ref="error_file" />
        <!-- 将日志输出到APPLICATION appender -->
        <appender-ref ref="APPLICATION" />
        <!-- 将日志输出到roll_log appender -->
        <appender-ref ref="roll_log" />
    </root>

    <!--
        <logger>用来设置某一个包或者具体的某一个类的日志打印级别、以及指定<appender>。
        <logger>仅有一个name属性，
        一个可选的level和一个可选的additivity属性。
        name:用来指定受此logger约束的某一个包或者具体的某一个类。
        level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，
              如果未设置此属性，那么当前logger将会继承上级的级别。
        additivity:是否向上级logger传递打印信息,默认是true
    -->
    <!-- 使用mybatis的时候，sql语句是debug下才会打印，而这里我们只配置了info，所以想要查看sql语句的话，有以下两种操作：
         第一种把<root level="INFO">改成<root level="DEBUG">这样就会打印sql，不过这样日志那边会出现很多其他消息
         第二种就是单独给mapper下目录配置DEBUG模式，代码如下，这样配置sql语句会打印，其他还是正常DEBUG级别：
    -->
    <logger name="com.hyh.logback.web.LogTestController" level="WARN" additivity="false">
        <appender-ref ref="console"/>
        <appender-ref ref="warn_file"/>
        <appender-ref ref="error_file"/>
    </logger>

    <!-- 使用Spring Profile为不同环境提供不同的日志配置 -->
    <springProfile name="dev">
        <!-- 开发环境下的日志设置 -->
        <!--可以输出项目中的debug日志，包括mybatis的sql日志-->
        <logger name="com.hyh.logback.web" level="DEBUG">
            <!-- 输出日志到控制台 -->
            <appender-ref ref="console"/>
        </logger>
        <!-- 在dev环境中root日志级别设置为DEBUG -->
        <!--
            root节点是必选节点，用来指定最基础的日志输出级别，只有一个level属性
            level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，默认是DEBUG
            可以包含零个或多个appender元素。
        -->
        <root level="DEBUG">
            <!-- 输出日志到控制台 -->
            <appender-ref ref="console"/>
        </root>
    </springProfile>
</configuration>
```

### 3.1.3 Controlle层

- `UserController`代码：

  ```java
  package com.zzx.controller;
  
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;
  
  @Slf4j
  @RestController
  @RequestMapping("/user") // 建议指定一个基础路径
  public class UserController {
  
      @GetMapping("/hi")
      public String hi() { // 方法名首字母小写
          // 打印日志的方法
          log.trace("我是trace级别日志");
          log.debug("我是debug级别日志");
          log.info("我是info级别日志");
          log.warn("我是warn级别日志");
          log.error("我是error级别日志");
  
          return "Hello World!";
      }
  }
  ```

### 3.1.4 实体类

- `User`：

  ```java
  package com.zzx.entity;
  
  import lombok.*;
  
  import java.util.ArrayList;
  
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public class User {
  
      private Long id;
  
      private String name;
  
      private Integer age;
  }
  ```

### 3.1.5 启动类

```java
package com.zzx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootLombokDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootLombokDemoApplication.class, args);
    }
}
```

### 3.1.6 测试

- 启动`SpringBoot`项目后，在浏览器输入`http://localhost:8080/user/hi`，向后端发送请求

![QQ_1736513811605](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736513811605.png)

- 即可查看日志信息已经保存在不同目录下：

![QQ_1736513891503](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1736513891503.png)

# 参考文献

- <https://blog.csdn.net/weixin_64178283/article/details/142754354>
- <https://blog.csdn.net/NiNg_1_234/article/details/143694155>
- <https://blog.csdn.net/han12398766/article/details/116245583>
- <https://blog.csdn.net/weixin_42440768/article/details/107999786>
- <https://juejin.cn/post/7357957809071702050#heading-15>
- <https://blog.csdn.net/ryuenkyo/article/details/132716598>
- <https://javabetter.cn/springboot/logback.html#%E7%9B%B4%E6%8E%A5%E4%B8%8A%E6%89%8B>
