



> - 今天毛毛张又写了一万字来介绍`SpringBoot`配置文件相关的知识点，帮助大家快速入门`SpringBoot`。
>
> - 毛毛张并不是干瘪的介绍配置文件的语法，而是结合实际的代码来解释配置文件的语法，每个案例都是毛毛张精心设计的。
>
> - 涉及到的代码已经上传至`Github`：<https://github.com/zzxrepository/SpringBootTutorial/tree/master/springboot-config-file>
> - 还希望大家能多多一键三连！



[toc]

# 1.SpringBoot配置文件

## 1.1 什么是配置文件

-  **配置文件是包含应用程序或系统配置信息的文件**。这些文件通常用于存储和管理应用程序的设置，以便在运行时进行配置和自定义。配置文件可以采用不同的格式，如文本文件、`JSON`、`XML`、`YAML`等，具体格式取决于应用程序的需求和开发者的偏好。
-  计算机上有数以千计的配置⽂件，我们使⽤的绝⼤多数软件，比如浏览器，微信，`Idea`，甚⾄电脑，手机，都离不开配置⽂件。我们可能永远不会直接与其中的⼤部分⽂件打交道，但它们确实以不同的形式散落在我们的计算机上，⽐如`C:\Users`, `C:\Windows`⽂件夹，以及各种`*.config`, `*.xml` ⽂件

## 1.2 配置文件

- **常规作用**：配置⽂件主要是为了解决**硬编码**带来的问题, 把可能会发⽣改变的信息，放在⼀个集中的地⽅，当我们启动某个程序时，应⽤程序从配置⽂件中读取数据，并加载运行
  - **硬编码**是将数据直接嵌⼊到程序或其他可执⾏对象的源代码中, 也就是我们常说的“**代码写死**”
  - 使用配置文件，可以使程序完成用户和应用程序的交互，或者应用程序与其他应用程序的交互
- **SpringBoot中配置文件的作用**：在实际开发中，主要解决硬编码的问题，**很多项目或者框架的配置信息也放在配置文件中**，更加方便我们项目的部署和后续修改。比如：
  - 项目的启动端口
  - 数据库的连接信息（包含用户名和密码的设置）
  - 第三方系统的调用密钥等信息
  - 用于发现和定位问题的普通日志和异常日志等

## 1.3 SpringBoot配置文件

- **Spring Boot** 在工作中得到了越来越广泛的应用，它简单而高效，极大地提升了开发效率。对于 **Spring Boot** 来说，配置文件是入门和基础知识的关键。

- 在我们构建完 Spring Boot 项目后，**`src/main/resources` 目录下会自动生成一个默认的全局配置文件 `application.properties`**。这个文件初始为空，因为 Spring Boot 在底层已经完成了大部分的自动配置。当我们需要调整 Spring Boot 默认的配置值时，可以通过修改该配置文件来实现。

  ![image-20220915223918315](https://img2022.cnblogs.com/blog/650581/202209/650581-20220916185140718-1647075110.png)

- Spring Boot 默认使用的配置文件是 **`application.properties`**，此外，还有一种格式是以 **`.yaml`** 或 **`.yml`** 后缀结尾的 YAML 文件（如 **`application.yml` / `application.yaml`**）。这两种文件格式本质上是相同的，主要区别在于语法不同。**相比于 properties 格式，YAML 是一种更现代、更易读的格式，尤其在云原生开发中得到了广泛应用，因此在实际的应用中毛毛张更推荐使用`yaml`格式的配置文件**
- **注意事项：**
  - **文件名必须为`application`**
  - **文件后缀有三种形式：`properties`、`yml`、`yaml`**
  - **其中`application.yml` 和`application.yaml`两者其实是同一种类型的配置文件，`yml`为`yaml`的简写，两者的使用方式是一样的，所以毛毛张在后面的介绍中就只介绍这两种格式：`properties`、`yaml`**

# 2.配置文件的优先级

## 2.1 位置优先级

- **Spring Boot 会在启动时自动扫描并加载以下五个目录中的配置文件：**
  - From the classpath：类路径
    - ①The classpath root：类路径的根目录下的[配置文件]（`classpath:/application.properties`）
    - ②The classpath `/config` package：类路径下的`/config`目录下的[配置文件]（`classpath:/config/application.properties`）
  - From the current directory：当前工作目录
    - ③The current directory：当前工作目录下的[配置文件]（`./application.properties`）
    - ④The `config/` subdirectory in the current directory：当前工作目录下的`config/`目录下的[配置文件]（`./config/application.properties`）
    - ⑤Immediate child directories of the `config/` subdirectory：当前工作目录下的`config/` 子目录下的子目录下的[配置文件]（`./config/subdir/application.properties`）

- **注意事项：**

  - **上面列举了五个目录中的配置文件，其中越往下，配置文件的优先级越高，也就是数字越大，优先级越高**
  - **如果在这五个目录下的全局配置文件都配置了相同的属性，优先级高的配置文件会覆盖优先级低的配置文件，顺序越往下，优先级越高**

  - **如果在五个全局配置文件中配置了不同的属性，`SpringBoot`则会对上面五个位置的配置文件都进行加载，会形成一个互补设置，也就是说不同的属性可以分散在这五个配置文件中，`SpringBoot`启动的时候会见分散的不同属性合并在一起**

- 上面的问题描述可能比较抽象，毛毛张创建了一个项目并创建了五个目录下的五个配置文件，序号和上面的序号一一对应，如下图：

![QQ_1735645796742](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735645796742.png)

- 官网说明：[SpringBoot官网关于配置文件顺序的说明](https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.files.profile-specific)

![image-20241231192023366](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241231192023366.png)

## 2.2 文件名优先级

- **上面毛毛张介绍过两种后缀名的配置文件`properties`和`yaml`，如果这两个配置文件出现在同一目录下，谁的优先级更高呢？**
  - **在SpringBoot2.4.0以前，优先级`properties > yaml`**
  - **在SpringBoot2.4.0以后，优先级`yaml > properties`**
- 如果同一个配置属性在多个配置文件中都配置了，**优先级较高的文件中的值会覆盖优先级较低的文件中的值**

## 2.3 总结

- 白学警告：**上面介绍了不同文件目录下配置文件的优先级以及不同文件名的配置文件的优先级，但是在真实的开发过程中不会这么花里胡哨，一般的配置文件都是放在`src/main/resources/`目录下，同时在该目录下创建一个名为`application.yaml`的配置文件，后缀名为`yaml`格式的配置文件更常用。**

# 3.配置文件语法

- 在这里毛毛张通过对比的方式来分别介绍两种类型的配置文件对于不同类型的配置的写法，而不是语法，更多的是一种书写的规范，更多的是以`yaml`格式的写法为主，因为`properties`格式的配置文件比较简单，就是以单纯的键值对的形式表示出来，例如：`key=value`
- 而`yaml`采用的配置格式不像`properties`的配置那样以单纯的键值对形式来表示，而是以类似大纲的缩进形式来表示
- 本博客会介绍一些常用的配置信息，更多的配置信息，可以参考官网：[SpringBoot配置](https://springdoc.cn/spring-boot/application-properties.html#appendix.application-properties.devtools)

## 3.1 properties格式配置文件语法

### 3.1.1 基本语法

- `properties`是以键值对的形式配置的，键和值之间是用等号连接的

- 键的格式建议是小写，单词之间使用`.`分割

- 配置文件中使用`#`来添加注释信息

- 示例：

  ```properties
  # 配置项⽬端⼝号
  server.port=8080
  
  # 配置数据库连接信息
  spring.datasource.url=jdbc:mysql://127.0.0.1:3306/mycnblog?characterEncoding=utf8&useSSL=false
  spring.datasource.username=root
  spring.datasource.password=root
  ```

### 3.1.2 字面量：数字、字符串、布尔值

```properties
# server 配置
server.port=8080  # 数字，设置端口号
server.ssl.enabled=true  # 布尔值，启用 SSL

# spring 配置
spring.application.name=MySpringBootApp  # 字符串，应用名称
spring.datasource.hikari.maximum-pool-size=10  # 数字，数据库连接池最大连接数

# logging 配置
logging.level.org.springframework=INFO  # 字符串，设置日志级别

# app 配置
app.featureEnabled=true  # 布尔值，启用某些功能
```

### 3.1.3 对象或Map

```properties
# 配置朋友信息
friends.name=zhangsan
friends.age=20

# 配置个人信息
person.name=zhangsan
person.age=25
person.address.city=Beijing
person.address.street=Wangfujing
```

### 3.1.4 数组(List、Set)

```properties
# 配置宠物信息
pets[0]=cat
pets[1]=dog
pets[2]=pig
pets[3]=cat  # 重复元素会被当作List处理

# 配置个人爱好信息
person.hobby[0]=篮球
person.hobby[1]=跑步
person.hobby[2]=读书  # 没有重复元素，会被当作Set处理
```

### 3.1.5 配置文件占位符

- `SpringBoot`中的占位符语法通常是 `${...}`，用来引用配置文件中的其他属性或系统环境变量。可以使用这种语法来动态替换或传递值。

  ```properties
  # 文件名：application.properties 全局配置文件的内容
  # 通过占位符引用启动命令中的参数 spring.profiles.active
  # 如果在启动命令中未提供 --spring.profiles.active 参数，默认使用 'dev'
  spring.profiles.active=${spring.profiles.active:dev}
  
  # 使用占位符引用其他配置项的值
  app.property.name=JohnDoe
  app.desc=your name is ${app.property.name}
  
  # 使用占位符读取系统环境变量
  app.env=${APP_ENV:production}
  
  # 开发环境配置文件：application-dev.properties
  # 配置开发环境特有的属性
  app.db.url=jdbc:mysql://localhost:3306/dev_db
  app.db.username=dev_user
  app.db.password=dev_password
  
  # 生产环境配置文件：application-prod.properties
  # 配置生产环境特有的属性
  app.db.url=jdbc:mysql://prod-db-server:3306/prod_db
  app.db.username=prod_user
  app.db.password=prod_password
  ```

- 启动命令：

  ```yaml
  # 启动时通过命令行传入 active profile (dev 或 prod)
  java -jar your-application.jar --spring.profiles.active=prod
  ```

- 解释：在实际的开发环境中会存在多个环境的配置，不同的环境配置文件的名称不能，例如：开发环境`application-dev.properties`和生产环境 `application-prod.properties`，而**`spring.profiles.active`** 就是一个用于指定活动配置文件的属性。通过启动命令中的 `--spring.profiles.active=prod`，应用会加载 `application-prod.properties` 配置文件，而不是 `application-dev.properties`。如果没有指定 `spring.profiles.active`，默认会使用 `dev` 环境。

> 这部分内容，在后面的多配置文件中还会讲到

### 3.2.6 配置随机值

- `SpringBoot`内部提供了一个`random.*`属性，专门用于生成随机值，用于配置一些随机属性值

  | 属性                 | 描述                                                     |
  | :------------------- | :------------------------------------------------------- |
  | random.int           | 随机产生正负的整数                                       |
  | random.int(max)      | 随机产生 [0, max) 区间的整数                             |
  | random.int(min,max)  | 随机产生 [min, max) 区间的整数                           |
  | random.long          | 随机产生正负的长整数                                     |
  | random.long(max)     | 随机产生 [0, max) 区间的长整数                           |
  | random.long(min,max) | 随机产生 [min, max) 区间的长整数                         |
  | random.uuid          | 产生 UUID 字符串（含`-`字符）                            |
  | random.*             | `*表示除上面列举之外的其他字符，用于随机产生 32 位字符串 |

- 示例：

  ```yaml
  int-val=${random.int}
  int-range-val=${random.int(2)}
  uuid-val=${random.uuid}
  ```

### 3.1.7 缺点分析

- 从上面的配置`key`可以看出，`properties`配置文件中会有很多冗余的信息

![QQ_1735649090743](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735649090743.png)

- 想要解决这个问题，**可以使用yaml格式的配置文件(实际工作中也是yaml用的更多)**

## 3.2 yaml格式配置文件语法

- 简介：YAML（/ˈjæməl/）是一个可读性高，用来表达资料序列的格式。目前已经有数种编程语言或脚本语言支援（或者说解析）这种语言。YAML是`YAML Ain’t a Markup Language`（YAML不是一种标记语言）的递回缩写。在开发的这种语言时，YAML 的意思其实是：`Yet Another Markup Language`（仍是一种标记语言），但为了强调这种语言以数据做为中心，而不是以标记语言为重点，而用反向缩略语重新命名。AML的语法和其他高阶语言类似，并且可以简单表达清单、散列表，标量等资料形态。它使用空白符号缩排和大量依赖外观的特色，特别适合用来表达或编辑数据结构、各种设定档、倾印除错内容、文件大纲（例如：许多电子邮件标题格式和YAML非常接近）。尽管它比较适合用来表达阶层式的数据结构，不过也有精致的语法可以表示关联性的资料。由于YAML使用空白字元和分行来分隔资料，使得它特别适合用grep／Python／Perl／Ruby操作。其让人最容易上手的特色是巧妙避开各种封闭符号，如：引号、各种括号等，这些符号在巢状结构时会变得复杂而难以辨认
- 毛毛张在下面只介绍一些在`SpringBoot`项目中常用的语法，更多更详细的教程可以参考[菜鸟教程](https://www.runoob.com/w3cnote/yaml-intro.html)

### 3.2.1 基本语法

- `yaml`格式配置文件通过：`key: value` 表示键值对关系，**冒号后面必须加一个空格**
- `yaml`格式是**大小写敏感**的，也就是论文是键、还是值，都是区分大小写的
- 使用缩进表示层级关系；但是缩进不允许使用tab，只允许空格；缩进的空格数不重要，只要相同层级的元素左对齐即可
- `#`表示注释

- 示例：

  ```yaml
  # 配置项⽬端⼝号
  server:
      port: 8081   # 注意：冒号后面必须加一个空格
      
  # 配置数据库连接信息
  spring:
  	datasource:
  		url: jdbc:mysql://127.0.0.1:3306/mycnblog?characterEncoding=utf8&useSSL=false
  		username: root
  		password: root
  ```

### 3.2.2 字面量：数字、字符串、布尔值

- 这三类普通的值就是直接写，毛毛张只在下面给个案例就行：

  ```yaml
  server:
    port: 8080  # 数字，设置端口号
    ssl:
      enabled: true  # 布尔值，启用 SSL
  
  spring:
    application:
      name: MySpringBootApp  # 字符串，应用名称
    datasource:
      hikari:
        maximum-pool-size: 10  # 数字，数据库连接池最大连接数
  
  logging:
    level:
      org.springframework: INFO  # 字符串，设置日志级别
  
  app:
    featureEnabled: true  # 布尔值，启用某些功能
  ```

- 如果值是字符串需要注意：

  - **双引号 `"`**：**支持转义字符**，在双引号字符串中，可以使用转义字符（例如 `\n`、`\t`、`\\` 等），这些转义字符会被解析和转义。

    ```yaml
    name: "zhangsan \n lisi"
    # 输出：
    zhangsan
    lisi
    ```

  - **单引号 `'`**：**不支持转义字符**，单引号中的字符串会被原样输出，任何转义字符（如 `\n`）都会被视作普通字符，而不会被解析为换行符；**所有内容都按字面值处理**，即使是 `\n` 这样的转义字符，也会被视作普通字符 `\` 和 `n`。**如果字符串什么引号都不加，默认也是不支持转义字符**

    ```yaml
    name: 'zhangsan \n lisi'
    name: zhangsan \n lisi
    # 输出：
    zhangsan \n lisi
    zhangsan \n lisi
    ```

- 如果冒号后面什么都不加则表示空字符串。当这种方式不直观，更多的表示是使用引号括起来

  ```yaml
  spring:
  	datasource:
  		url: "" 	 # 空字符串
  		username: '' # 单引号也表示空字符串
  		password:    # 什么都不写表示空字符串
  ```

### 3.2.3 对象或Map（属性和值）

- 方式1：块级表示法

  ```yaml
  friends:
      name: zhangsan
      age: 20
      
  person:
    name: "zhangsan"
    age: 25
    address:
      city: "Beijing"
      street: "Wangfujing"
  ```

- 方式2：行内表示法，也就是写在一行

  ```yaml
  friends: {name: zhangsan,age: 18}
  person: {name: "zhangsan", age: 25, address: {city: "Beijing", street: "Wangfujing"}}
  ```

### 3.2.4 数组(List、Set)

- 方式1：块级表示法

  ```yaml
  pets:
    - cat
    - dog
    - pig
    - cat  # 重复元素 会被当作List处理
   
  person: # 没有重复元素，会被当做set处理
    hobby:
      - 篮球
      - 跑步
      - 读书
  ```

- 方式2：行内表示法

  ```yaml
  pets: [cat,dog,pig,cat]
  person:
    hobby: [篮球, 跑步, 读书]
  ```

### 3.2.5 配置文件占位符

- `SpringBoot`中的占位符语法通常是 `${...}`，用来引用配置文件中的其他属性或系统环境变量。可以使用这种语法来动态替换或传递值。

  ```yaml
  # 文件名：application.yml 全局配置文件的内容
  # 通过占位符引用启动命令中的参数 spring.profiles.active
  # 如果在启动命令中未提供 --spring.profiles.active 参数，默认使用 'dev'
  spring:
    profiles:
      active: ${spring.profiles.active:dev}
  
  # 使用占位符引用其他配置项的值
  app:
    property:
      name: JohnDoe
    desc: your name is ${app.property.name}
  
  # 使用占位符读取系统环境变量
  app:
    env: ${APP_ENV:production}
    
    
  # 开发环境配置文件名：application-dev.yml
  # 配置开发环境特有的属性
  app:
    db:
      url: jdbc:mysql://localhost:3306/dev_db
      username: dev_user
      password: dev_password
  
  # 生产环境配置文件名：application-prod.yml
  # 配置生产环境特有的属性
  app:
    db:
      url: jdbc:mysql://prod-db-server:3306/prod_db
      username: prod_user
      password: prod_password
  ```

- 启动命令：

  ```yaml
  # 启动时通过命令行传入 active profile (dev 或 prod)
  java -jar your-application.jar --spring.profiles.active=prod
  ```

- 解释：在实际的开发环境中会存在多个环境的配置，不同的环境配置文件的名称不能，例如：开发环境`application-dev.yaml`和生产环境 `application-prod.yaml`，而**`spring.profiles.active`** 就是一个用于指定活动配置文件的属性。通过启动命令中的 `--spring.profiles.active=prod`，应用会加载 `application-prod.yaml` 配置文件，而不是 `application-dev.yaml`。如果没有指定 `spring.profiles.active`，默认会使用 `dev` 环境。

### 3.2.6 配置随机值

- 在一些特殊情况下，有些参数我们希望它每次加载的时候不是一个固定的值，比如：密钥、服务端口等。`SpringBoot`内部提供了一个`random.*`属性，专门用于生成随机值，具体的生成的随机值的函数如下：

  |          属性          |                           描述                            |
  | :--------------------: | :-------------------------------------------------------: |
  |      `random.int`      |                    随机产生正负的整数                     |
  |   `random.int(max)`    |               随机产生 [0, max) 区间的整数                |
  | `random.int(min,max)`  |              随机产生 [min, max) 区间的整数               |
  |     `random.long`      |                   随机产生正负的长整数                    |
  |   `random.long(max)`   |              随机产生 [0, max) 区间的长整数               |
  | `random.long(min,max)` |             随机产生 [min, max) 区间的长整数              |
  |     `random.uuid`      |               产生`UUID`字符串（含`-`字符）               |
  |       `random.*`       | `*`表示除上面列举之外的其他字符，用于随机产生 32 位字符串 |

- 语法：在`SpringBoot`的属性配置文件中，我们可以通过使用`${random}`配置来产生随机的`int`值、`long`值或者`string`字符串

- 示例：

  ```yaml
  int-val: ${random.int}
  int-range-val: ${random.int(2)}
  uuid-val: ${random.uuid}
  ```

### 3.2.7 优缺点分析

- 优点：
  - 可读性⾼，写法简单, 易于理解
  - ⽀持更多的数据类型, 可以简单表达对象, 数组, List，Map等数据形态.
  - ⽀持更多的编程语⾔, 不⽌是Java中可以使⽤, 在Golang, Python, Ruby, JavaScript中也可以使⽤

- 缺点：
  - 不适合写复杂的配置⽂件
  - 对格式有较强的要求(⼀个空格可能会引起⼀场⾎案)

## 3.3 总结

- **总结就是`yaml`格式的配置文件更好一点**
- **上面毛毛张讲解了配置文件如何书写，下面毛毛张将来介绍写好的配置文件如何通过程序读取呢？**

# 4.配置文件注入

- 也可以理解成读取配置文件的信息

## 4.1 绑定配置的一些规则

- `SpringBoot` 对 `*.properties` 和 `*.yml` 文件中配置的属性名称提供了一些绑定规则，它不要求配置的属性名称完全与`Bean`中的属性名称相同。它支持以下几种规则的命名方式：

|     属性     |                            描述                             |
| :----------: | :---------------------------------------------------------: |
| `firstName`  |                    **标准的驼峰式命名**                     |
| `first-name` |        单词之间通过`-`分隔，**`SpringBoot`推荐这种**        |
| `first_name` |                     单词之间通过`_`分隔                     |
| `FIRST_NAME` | 单词全部大写并通过`_`分隔，**在使用系统环境变量时推荐这种** |

## 4.2 `@Value`注解

### 4.2.1 语法

- **语法：**
  - **通过使用注解 `@Value("${属性名称}")` 来将配置文件里面的值注入到程序属性中**
  - **同时还需要使用`@Component`或者`@Configuration`注解将该组件加入`SpringBoot`容器，只有这个组件是容器中的组件，配置才生效**
- **应用场景**：如果我们只是在某个业务逻辑中需要获取一下配置文件中的某项值就使用可以使用该注解

### 4.2.2 案例

- 配置文件`application.yaml`：假设毛毛张需要读取配置文件中指定的端口和ID

  ```yaml
  server:
    port: 8082
    ip:   127.0.0.1
  ```

- 配置文件`application.properties`：路径：`src/main/resources/application.yaml`

  ```properties
  server.port=8082
  server.ip=127.0.0.1
  ```

- 注入代码：

  ```java
  @Data  		// 毛毛张在这里使用了Lombok插件，免得去写构造方法和getter和setter方法了
  @Component  // 或者@Configuration  需要将组件添加到SpringBoot中
  public class ServerConfig {
      
      @Value("${server.port}")
      public String Port;
      
      @Value("${server.ip}")
      public String IP;
  }
  ```

- 测试代码：

  ```java
  package com.zzx;
  
  import com.zzx.config.ServerConfig;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  import javax.annotation.PostConstruct;
  
  @SpringBootApplication
  public class SpringbootConfigFileDemoApplication {
  
      @Autowired
      private ServerConfig serverConfig;
  
      public static void main(String[] args) {
          // 通过 SpringApplication 运行应用程序
          SpringApplication.run(SpringbootConfigFileDemoApplication.class, args);
      }
  
      // 使用 @PostConstruct 或者在 Spring Boot 启动完成后进行打印
      @PostConstruct
      public void printServerConfig() {
          // 在 Spring 容器启动后打印 ServerConfig 内容
          System.out.println(serverConfig);
      }
  }
  ```

- 输出：

![QQ_1735701186940](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735701186940.png)

## 4.3 `@ConfigurationProperties`注解

### 4.3.1 语法

- 语法：
  - `@ConfigurationProperties`注解向`SpringBoot`声明该类中的所有属性和配置文件中相关的配置进行绑定，所以该注解是一个作用在类上的注解。该注解还有一个参数，这个参数可选
    - `prefix = "xxx"`：声明配置前缀，将该前缀下的所有属性进行映射
  - 同时还需要使用`@Component`或者`@Configuration`注解将该组件加入`SpringBoot`容器，只有这个组件是容器中的组件，配置才生效
- 应用场景：当我们需要将配置文件中的属性映射成一个类的时候就可以使用该属性，但是配置文件属性的值要满足我们在上面提到的**绑定配置文件的一些规则**

### 4.3.2 案例

#### 案例1：

- `application.yaml`：路径：`src/main/resources/application.yaml`

  ```yaml
  server:
    port: 80
    ip:   127.0.0.1
  ```

- `Java`代码：

  ```java
  @Component
  @ConfigurationProperties(prefix = "server")
  public class ServerConfig {
      public String port;
      public String ip;
  }
  ```

#### 案例2：

- `application.yaml`文件：路径：`src/main/resources/application.yaml`

  ```yaml
  server:
    port: 80
    ip:   127.0.0.1
    list-server:
       - BJ-Server
       - GZ-Server
    map-server: {bj-server: 192.168.0.2, gz-server: 192.168.0.3}
    dns:
       bj: bj.dns.one
       gz: gz.dns.two
    arr-port: 8081, 8082
  ```

- 对应的配置类：

  ```java
  package com.zzx.config;
  
  import lombok.Data;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.boot.context.properties.ConfigurationProperties;
  import org.springframework.stereotype.Component;
  
  import java.util.List;
  import java.util.Map;
  
  @Data  		// 毛毛张在这里使用了Lombok插件，免得去写构造方法和getter和setter方法了
  @Component  // 或者@Configuration  需要将组件添加到SpringBoot中
  @ConfigurationProperties(prefix = "server")
  public class ServerConfig {
      public String port;
      public String ip;
      private List<String> listServer;
      private Map<String, String> mapServer;
      private int[] arrPort;
  
      private Dns dns;
  
      public static class Dns {
          private String bj;
          private String gz;
      }
  }
  ```

- 测试输出代码：

  ```java
  package com.zzx;
  
  import com.zzx.config.ServerConfig;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  import javax.annotation.PostConstruct;
  
  @SpringBootApplication
  public class SpringbootConfigFileDemoApplication {
  
      @Autowired
      private ServerConfig serverConfig;
  
      public static void main(String[] args) {
          // 通过 SpringApplication 运行应用程序
          SpringApplication.run(SpringbootConfigFileDemoApplication.class, args);
      }
  
      // 使用 @PostConstruct 或者在 Spring Boot 启动完成后进行打印
      @PostConstruct
      public void printServerConfig() {
          // 在 Spring 容器启动后打印 ServerConfig 内容
          System.out.println("配置文件中的值为：");
          System.out.println(serverConfig);
      }
  }
  ```

- 打印配置：

![QQ_1735701392060](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735701392060.png)

## 4.4 `@Validated`配置属性校验注解

### 4.4.1 语法

- `SpringBoot`提供了 `@Validated` 注解来对注入的配置文件的值进行一些简单的校验。使用 `@Validated` 注解，可以在配置类中应用各种常见的校验注解，确保配置的合法性。
- 常见的注解有：
  - **`@AssertFalse`**：校验字段值是否为 `false`，如果值为 `true`，则校验失败。
  - **`@AssertTrue`**：校验字段值是否为 `true`，如果值为 `false`，则校验失败。
  - **`@DecimalMax(value, inclusive)`**：校验字段值是否小于等于指定的 `value`，`inclusive=true` 表示包含该值。
  - **`@DecimalMin(value, inclusive)`**：校验字段值是否大于等于指定的 `value`，`inclusive=true` 表示包含该值。
  - **`@Max(value)`**：校验字段值是否小于等于指定的 `value`。
  - **`@Min(value)`**：校验字段值是否大于等于指定的 `value`。
  - **`@NotNull`**：校验字段是否为 `null`，如果为 `null`，则校验失败。
  - **`@Past`**：校验日期是否为过去的时间。
  - **`@Pattern(regex, flag)`**：校验字段值是否匹配指定的正则表达式。
  - **`@Size(min, max)`**：校验字符串、集合、数组或 `Map` 的大小是否在指定的范围内。
  - **`@Validated`**：对 POJO 实体类进行校验，通常与其他校验注解一起使用。
  - **`@Email`** ：会对mail字段的注入值进行检验，如果注入的不是一个合法的邮件地址则会抛出异常。

- 上述的这些注解位于`javax.validation.constraints`包下，因此使用时需要导入相关依赖，在`SpringBoot`中，可以使用`Hibernate Validator`

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
  ```

### 4.4.2 案例

- `application.yaml`配置文件

  - 路径：`src/main/resources/application.xml`
  - 假设在配置文件中配置了一个超级管理员的用户信息，我们需要读取该管理员的信息

  ```yaml
  user:
    username: "john_doe"
    password: "myPassword123"
    age: 25
    balance: 5000.00
    inviteCode: "ABCD1234"
    birthDate: "1996-05-15"
    agreeToTerms: true
    subscribeToNewsletter: false
    hobbies:
      - "篮球"
      - "跑步"
      - "读书"
    email: "john.doe@example.com"
  ```

- 对应的配置类：

  ```java
  package com.zzx.config;
  
  import javax.validation.constraints.*;
  import lombok.Data;
  import org.springframework.boot.context.properties.ConfigurationProperties;
  import org.springframework.format.annotation.DateTimeFormat;
  import org.springframework.stereotype.Component;
  import org.springframework.validation.annotation.Validated;
  
  import java.time.LocalDate;
  import java.util.List;
  
  @Data
  @Component
  @ConfigurationProperties(prefix = "user")
  @Validated  // 启用对配置类的校验
  public class UserConfig {
  
      @NotNull(message = "用户名不能为空")
      private String username;
  
      @Size(min = 8, max = 20, message = "密码长度必须在8到20个字符之间")
      private String password;
  
      @Min(value = 18, message = "年龄不能小于18岁")
      @Max(value = 100, message = "年龄不能大于100岁")
      private int age;
  
      @DecimalMin(value = "1000.00", inclusive = true, message = "账户余额不能小于1000")
      @DecimalMax(value = "100000.00", inclusive = true, message = "账户余额不能大于100000")
      private double balance;
  
      @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "邀请码只能包含字母和数字")
      private String inviteCode;
  
      @Past(message = "出生日期必须是过去的日期")
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      private LocalDate birthDate;
  
      @AssertTrue(message = "必须同意服务条款")
      private boolean agreeToTerms;
  
      @AssertFalse(message = "不能勾选订阅邮件")
      private boolean subscribeToNewsletter;
  
      @Size(min = 1, max = 5, message = "兴趣爱好数量必须在1到5个之间")
      private List<String> hobbies;
  
      @Email(message = "邮箱格式不正确")
      private String email;  // 添加邮箱字段
  
      @Override
      public String toString() {
          return "UserConfig {\n" +
                  "  username='" + username + "'\n" +
                  "  password='" + password + "'\n" +
                  "  age=" + age + "\n" +
                  "  balance=" + balance + "\n" +
                  "  inviteCode='" + inviteCode + "'\n" +
                  "  birthDate=" + birthDate + "\n" +
                  "  agreeToTerms=" + agreeToTerms + "\n" +
                  "  subscribeToNewsletter=" + subscribeToNewsletter + "\n" +
                  "  hobbies=" + hobbies + "\n" +
                  "  email='" + email + "'\n" +
                  "}";
      }
  }
  ```

- 打印输出：

![QQ_1735703274835](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735703274835.png)

## 4.5 加载外部配置文件

- 有时候如果我们将所有的配置信息都写在`application.yaml`文件中，则到时候`yaml`文件会变得非常庞大，不太方便进行维护。
- 因此我们可以对配置文件中的内容进行拆分，拆分到多个文件中。这样就提高了配置的可维护性。
- 如何引入这些外部的配置文件呢？就需要用到下面两个注解

### 4.5.1 `@PropertySource`加载指定配置文件

#### 4.5.1.1 讲解

- **该注解可以指定要加载的配置文件，例如：`@PropertySource("website.properties")`就是加载 指定的`website.properties` 配置文件**
- **注意：该注解只适用于加载`properties`格式的配置文件**

#### 4.5.1.2 案例

- 配置文件`website.yaml`：

  - 路径：`src/main/resources/website.yaml`
  - 假设我们要创建一个个人博客网站，网站上线的时候肯定有一些关于网站介绍的信息，这时候我们就可以单独存放在一个配置文件中

  ```properties
  # 配置个人网站基本信息
  website.name=MyPersonalWebsite
  website.description=这是我的个人网站，用于展示个人博客、项目以及联系方式
  website.url=https://www.mypersonalwebsite.com
  website.logoUrl=https://www.mypersonalwebsite.com/logo.png
  website.contactEmail=contact@mypersonalwebsite.com
  website.footerText=© 2024 MyPersonalWebsite. All rights reserved.
  website.theme=dark
  website.icp=沪ICP备2024000001号
  
  # 网站的社交媒体链接
  website.socialMediaLinks[0]=https://github.com/myusername
  website.socialMediaLinks[1]=https://twitter.com/myusername
  
  # 网站的运营状态信息
  website.websiteStatus.active=true
  website.websiteStatus.maintenance=false
  website.websiteStatus.lastUpdated=2024-12-01
  
  # 网站的多语言支持配置
  website.languages.en=English
  website.languages.cn=中文
  ```

- 对应的配置类：

  ```java
  package com.zzx.config;
  
  import lombok.Data;
  import org.springframework.boot.context.properties.ConfigurationProperties;
  import org.springframework.context.annotation.PropertySource;
  import org.springframework.stereotype.Component;
  
  import java.util.List;
  import java.util.Map;
  
  @Data
  @Component
  @PropertySource("classpath:website.properties")  // 使用classpath下的properties文件
  @ConfigurationProperties(prefix = "website")  // 使用"website"前缀
  public class WebsiteConfig {
      private String name;
      private String description;
      private String url;
      private String logoUrl;
      private String contactEmail;
      private String footerText;
      private String theme;
      private String icp;
      
      private List<String> socialMediaLinks;  // 社交媒体链接
      private Map<String, Object> websiteStatus; // 网站状态信息
      private Map<String, String> languages;   // 多语言支持
  }
  ```

- 启动类：

  ```java
  package com.zzx;
  
  import com.zzx.config.WebsiteConfig;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  import javax.annotation.PostConstruct;
  
  @SpringBootApplication
  public class SpringbootConfigFileDemoApplication {
  
      @Autowired
      private WebsiteConfig websiteConfig;
  
      public static void main(String[] args) {
          SpringApplication.run(SpringbootConfigFileDemoApplication.class, args);
      }
  
      @PostConstruct
      public void printWebsiteConfig() {
          // 打印加载的配置内容
          System.out.println("配置文件中的值为：");
          System.out.println("网站名称: " + websiteConfig.getName());
          System.out.println("网站描述: " + websiteConfig.getDescription());
          System.out.println("网站URL: " + websiteConfig.getUrl());
          System.out.println("Logo URL: " + websiteConfig.getLogoUrl());
          System.out.println("联系邮箱: " + websiteConfig.getContactEmail());
          System.out.println("页脚文本: " + websiteConfig.getFooterText());
          System.out.println("网站主题: " + websiteConfig.getTheme());
          System.out.println("备案号: " + websiteConfig.getIcp());
          System.out.println("社交媒体链接: " + websiteConfig.getSocialMediaLinks());
          System.out.println("网站状态: " + websiteConfig.getWebsiteStatus());
          System.out.println("多语言支持: " + websiteConfig.getLanguages());
      }
  }
  ```

- 输出：

![QQ_1735743356602](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735743356602.png)

### 4.5.2 `@ImportResource` 注解

#### 4.5.2.1 简介

- `@ImportResource` 是 Spring 框架中的一个注解，用于导入外部的 Spring 配置文件（通常是 XML 格式的配置文件）。这个注解使得我们可以在基于 Java 配置的 Spring 应用中，继续使用 XML 配置文件，而不必完全转向 Java 配置。
- `@ImportResource` 注解用来指定要导入的`XML`格式配置文件的路径。路径可以是类路径中的资源，也可以是绝对路径。

#### 4.5.2.2 案例

- 配置文件`application-context.xml`：路径：`src/main/resources/application-context.xml`

  ```xml
  <!-- application-context.xml -->
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.springframework.org/schema/beans
             http://www.springframework.org/schema/beans/spring-beans.xsd">
  
      <!-- User Bean -->
      <bean id="user" class="com.zzx.config.User">
          <property name="userName" value="JohnDoe" />
          <property name="email" value="john.doe@example.com" />
      </bean>
  
  </beans>
  ```

- 对应的配置类：

  ```java
  package com.zzx.config;
  
  import lombok.Data;
  
  @Data
  public class User {
      private String userName;
      private String email;
  }
  ```

- 对应的启动类：

  ```java
  package com.zzx;
  
  import com.zzx.config.ServerConfig;
  import com.zzx.config.User;
  import com.zzx.config.UserConfig;
  import com.zzx.config.WebsiteConfig;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.context.annotation.ImportResource;
  
  import javax.annotation.PostConstruct;
  
  @SpringBootApplication
  @ImportResource("application-context.xml")
  public class SpringbootConfigFileDemoApplication {
      @Autowired
      private User user;
  
      public static void main(String[] args) {
          // 通过 SpringApplication 运行应用程序
          SpringApplication.run(SpringbootConfigFileDemoApplication.class, args);
      }
  
      // 使用 @PostConstruct 或者在 Spring Boot 启动完成后进行打印
      @PostConstruct
      public void printUserConfig() {
          System.out.println("配置文件中的值为：");
          System.out.println(user);
      }
  }
  ```

- 输出：

![QQ_1735717907404](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735717907404.png)

### 4.5.3 @Configuration和@Bean

#### 4.5.3.1 简介

- 在 Spring 框架中，`@Configuration` 和 `@Bean` 注解为我们提供了另一种配置方式，这种方式完全基于 Java 配置，能够避免使用传统的 XML 配置文件。Spring 提供的 `@Configuration` 注解和 `@Bean` 注解可以帮助我们在 Java 类中创建 Bean，配置依赖注入以及管理 Spring 容器中的对象。

  - **`@Configuration`**: 标注在配置类上，表示该类是一个配置类，Spring 容器会自动扫描并识别其中的 `@Bean` 注解方法，从而生成对应的 Bean 定义。

  - **`@Bean`**: 用于方法上，表示该方法返回的对象会被 Spring 容器管理，作为一个 Bean 注入到容器中。

- 这种方式我感觉是对上一个注解的改进，不用写`xml`配置文件，但是还是比较复杂

#### 4.5.3.2 案例

- 配置类：

  ```java
  package com.zzx.config;
  
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  
  @Configuration
  public class BeanConfig {
  
      @Bean
      public Person person() {
          Person person = new Person();
          person.setName("John");
          person.setAge(22);
          return person;
      }
  }
  ```

- 用户类：

  ```java
  package com.zzx.config;
  
  import lombok.Data;
  
  @Data
  public class Person {
      private String name;
      private int age;
  }
  ```

- 启动类：

  ```java
  package com.zzx;
  
  import com.zzx.config.*;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.context.annotation.ImportResource;
  
  import javax.annotation.PostConstruct;
  
  @SpringBootApplication
  public class SpringbootConfigFileDemoApplication {
      @Autowired
      private Person person;
  
      public static void main(String[] args) {
          // 通过 SpringApplication 运行应用程序
          SpringApplication.run(SpringbootConfigFileDemoApplication.class, args);
      }
  
      // 使用 @PostConstruct 或者在 Spring Boot 启动完成后进行打印
      @PostConstruct
      public void displayPerson() {
          // 打印 Person Bean
          System.out.println("Person Details: " + person);
      }
  }
  
  ```

- 输出：

![QQ_1735733907717](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735733907717.png)

## 4.6 导入外部配置文件

### 4.6.1 简介

- 在前面的内容中，我们介绍了通过代码方式注入配置文件的方法。那么，是否可以直接导入配置文件呢？在Spring Boot 2.4及以上版本中，Spring Boot 支持通过 `spring.config.import` 属性导入外部配置文件。这是Spring Boot官方推荐的方式，能够方便地在多个配置文件之间共享配置。

- 与 `spring.config.additional-location` 不同，`spring.config.import` 不需要提前设置，并且支持更多类型的导入文件。使用时，只需在 `application.properties` 或 `application.yml` 配置文件中，通过 `spring.config.import` 属性列出需要导入的文件路径即可。

- 具体来说，Spring Boot 会在启动时按顺序加载通过 `spring.config.import` 指定的配置文件。如果文件路径正确，配置文件将被依次加载。

- 通过 `spring.config.import` 导入的外部配置文件通常分为以下三种情况：
  - **类路径根目录**：即放在 `src/main/resources/` 目录下的配置文件
  - **当前工作目录**
  - **其他指定目录**

- 下面毛毛张将通过一个具体的案例来说明如何实现外部配置文件的导入

### 4.6.2 案例

- 假设在`src/main/resources/`目录下有一个 `application.yaml` 文件，并希望将 `config1.yaml` 和 `config2.yml` 导入到 `application.yaml` 中。具体配置如下：

```yaml
spring:
  config:
    import: 
      - classpath:config1.yaml
      - classpath:config2.yml
      - file:./config3.yaml
      - classpath:/default/default.properties
      - classpath:/service/service.yml
      - optional:file:/Users/yuqiyu/Downloads/system.properties
```

- 配置说明

  - **导入类路径下的配置文件**：

    - `classpath:config1.yaml` 和 `classpath:config2.yml` 表示从类路径根目录下导入 `config1.yaml` 和 `config2.yml` 文件

    - `classpath:/default/default.properties` 导入 `src/main/resources/default` 目录下的 `default.properties` 文件

    - `classpath:/service/service.yml` 导入 `src/main/resources/service` 目录下的 `service.yml` 文件

  - **导入文件系统中的配置文件**：

    - `file:./config3.yaml` 表示从当前工作目录导入 `config3.yaml` 文件

    - `optional:file:/Users/yuqiyu/Downloads/system.properties` 表示可选地从指定的文件系统路径导入 `system.properties` 文件。如果文件不存在，应用仍能正常启动

- **配置文件导入完成后，可以通过 `@ConfigurationProperties` 或 `@Value` 注解在项目中注入和使用这些配置**

## 4.7 配置自动提示

- 在配置自定义属性时，如果想要获得和配置`SpringBoot`属性自动提示一样的功能，则需要在`pom.xml`文件中加入下面的依赖：

  ```xml
  <!--导入配置文件处理器，配置文件进行绑定就会有提示-->
  <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-configuration-processor</artifactId>
       <optional>true</optional>
  </dependency>
  ```

- 配置完成后可以看见属性下面有下划线，并且按住`Ctrl`，同时将鼠标放上去，就能显示值了

![QQ_1735743402004](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735743402004.png)

- 若是依旧无法自动提示，可以尝试开启`IDEA`设置中的`Enable Annonation Processing`

![QQ_1735705669002](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735705669002.png)

## 4.8总结

### 4.8.1 应用场景

- @Value注解**应用场景**：用于注入配置文件中的单一配置项，适用于需要从配置文件获取简单的、单一的属性值的情况。

- @ConfigurationProperties注解**应用场景**：适用于将配置文件中的一组相关配置映射为一个类，尤其适用于配置项较多、具有层级结构或者需要映射复杂类型（如List、Map）的场景。

- @Validated注解**应用场景**：与`@ConfigurationProperties`配合使用，对注入的配置项进行校验，确保配置文件中的值符合预期的格式和范围，适用于需要对配置项进行约束和验证的场景。

- @PropertySource注解**应用场景**：用于加载指定的`properties`格式的配置文件，适用于加载自定义的非默认配置文件，或者当项目中需要加载多个配置文件时使用。

- @ImportResource注解**应用场景**：用于加载XML格式的配置文件，适用于需要将现有的XML配置与Spring Boot的Java配置结合使用的场景。
- @Configuration和@Bean注解应用场景：适用于那些需要简化配置、避免冗长 XML 配置的应用场景。它特别适合用于简单的应用程序，或者在需要动态生成或根据条件调整配置时，也能够提供更高的灵活性。此外，Java 配置类使得集成测试变得更加便捷，因为可以直接通过测试类加载和验证配置，减少了配置错误的可能性，提升了代码的可维护性和类型安全性。
- **更多的是上面三种注解比较常见，下面三种方式比较少见**

### 4.8.2 **@Value 和 @ConfigurationProperties 区别**

|                  | `@ConfigurationProperties` |    `@Value`    |
| :--------------: | :------------------------: | :------------: |
|       功能       |  批量注入配置文件中的属性  | 一个个指定属性 |
|     松散绑定     |            支持            |     不支持     |
|  `SPEL`(计算式)  |           不支持           |      支持      |
| `JSR303`数据校验 |            支持            |     不支持     |
|     复杂类型     |            支持            |     不支持     |

- **功能**

  - `@ConfigurationProperties`：用于批量注入配置文件中的属性。你可以通过它将整个配置类与外部配置（如 `application.properties` 或 `application.yml`）进行绑定，直接将配置文件中的多个属性注入到类的字段中。

  - `@Value`：用于一个一个指定属性。你需要在代码中逐个使用 `@Value` 注解为每个字段注入单个配置项的值。它适合于注入单个配置值。

- **松散绑定**

  - `@ConfigurationProperties`：支持松散绑定（Loose Binding）。这意味着它可以自动匹配配置文件中的属性名称和 Java 类字段，即使它们的大小写不同（例如 `myProperty` 和 `my-property` 可以成功绑定），并且能够处理嵌套的配置对象。

  - `@Value`：不支持松散绑定。它要求配置文件中的属性名称与 `@Value` 注解中指定的路径完全匹配。

- **`SPEL`（Spring Expression Language）计算式** 

  - `@ConfigurationProperties`：不支持 Spring EL（`SPEL`）。它只能绑定配置值，而不能执行复杂的表达式或计算。

  - `@Value`：支持 `SPEL`，可以在注解中使用 Spring Expression Language（例如 `${1 + 2}` 或 `${systemProperties['user.name']}`）来动态计算属性值。

- **JSR303数据校验**
  - `@ConfigurationProperties`：支持 JSR303 数据校验。你可以在 `@ConfigurationProperties` 类中使用 JSR303 注解（如 `@NotNull`、`@Min`）来对配置文件中的值进行校验，这在处理复杂配置时非常有用。
    - `@Value`：不支持 JSR303 数据校验。如果你需要对通过 `@Value` 注解注入的单个属性进行校验，通常需要手动编写验证逻辑。

- **复杂类型**

  - `@ConfigurationProperties`：支持复杂类型。对于复杂的配置对象（例如嵌套的配置类或集合类型），`@ConfigurationProperties` 能够将它们直接映射到 Java 对象中。

  - `@Value`：不支持复杂类型。`@Value` 只能注入简单的类型（如 `String`、`Integer`、`Boolean` 等），对于嵌套的配置或更复杂的数据结构，它无法直接处理。

# 5.多环境配置文件

- 在上面毛毛张介绍的配置文件都是指明了在`/src/main/resources/`目录下，也就是类路径的根目录下，本节所介绍的配置文件也是都存放在类路径的根目录下，这么做的目的是为了告诉大家在实际的开发过程中不会把配置文件夹零散的放在不同的配置文件下。
- **在真实的环境中，有时候需要配置多个配置文件，不同的环境（如开发环境(application-dev.yaml)、测试环境(application-test.yaml)、生产环境(application-prod.yaml)）可能需要不同的配置，例如数据库连接信息、日志级别、缓存设置等。为了让同一个应用在不同环境下使用不同的配置，Spring Boot 提供了基于 profile 的配置文件机制。**
- **Spring Boot 提供了 `spring.profiles.active` 属性来切换不同的配置文件。我们可以通过该属性来指定当前活动的配置文件，Spring Boot 会自动加载与指定环境相关的配置文件。**

## 5.1 多环境配置方式

- 毛毛张将通过一个真实的案例来进行演示：**假设我们现在有两个环境，一个是开发环境（dev），一个是生产环境（prod），这两个环境使用的数据库名不同，因此我们需要采用不同的配置。在Spring Boot中，通常有三种方式来管理这种情况：**

  - **使用不同的配置文件（多配置文件方式）**：将不同环境的配置分别存放在独立的文件中，例如 `application-dev.yaml` 和 `application-prod.yaml`。在全局的 `application.yaml` 文件中，通过设置 `spring.profiles.active` 来选择激活特定的配置文件，从而加载对应环境的配置。

  - **使用Spring Profiles来切换不同的配置（多Profile的方式）**：将所有环境的配置整合在同一个 `application.yaml` 文件中，通过 `---` 分隔符区分不同的Profile块。例如，一个Profile块用于开发环境（dev），另一个用于生产环境（prod）。通过设置 `spring.profiles.active` 来激活相应的Profile块，从而应用对应环境的配置。
  - **通过Maven Profile实现环境切换**：利用Maven的Profile功能，在 `pom.xml` 中定义不同的Maven Profile（如 `dev` 和 `prod`）。每个Maven Profile配置相应的 `spring.profiles.active` 属性。在构建项目时，通过指定Maven Profile（例如使用 `-Pdev` 或 `-Pprod`）来自动激活对应的Spring Profile，实现环境配置的自动切换，适合需要自动化和集成化管理的项目。

### 5.1.1  方式1：多文件配置方式（支持）

- 这种方式就是在`/src/main/resources/`目录下创建三个配置文件
  - `application.yaml`文件必须要有
  - 在`application.yaml`文件中通过`spring.profiles`属性指定不同的环境，如果没有指定，默认就使用`application.yaml`文件中的配置；如果指定了`spring.profiles.active=dev` ，就是激活`application-dev.yaml`配置文件中的配置
  - 不同环境下的配置文件的命名方式：`application-{profile}.properties`

- **`application.yaml`**：全局通用配置文件

  ```yaml
  spring:
    application:
      name: MyApp
    profiles:
      active: dev  # 激活开发环境配置
  logging:
    level:
      org.springframework: INFO
      
  # 开发环境的配置
  server:
    port: 8081  # 开发环境的服务器端口
  
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/devdb  # 开发环境的数据库URL
      username: devuser
      password: devpassword
  ```

- **`application-dev.yaml`**：开发环境配置

  ```yaml
  # 开发环境的配置
  server:
    port: 8081  # 开发环境的服务器端口
  
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/devdb  # 开发环境的数据库URL
      username: devuser
      password: devpassword
  ```

- **`application-prod.yaml`**：生产环境配置

  ```yaml
  # 生产环境的配置
  server:
    port: 8080  # 生产环境的服务器端口
  
  spring:
    datasource:
      url: jdbc:mysql://prod-db:3306/proddb  # 生产环境的数据库URL
      username: produser
      password: prodpassword
  ```

> **<font color="red">注意：这种方式对于`properties`格式的配置文件也适用的！</font>** 方式是差不多的，毛毛张就不在这里过多赘述了

### 5.1.2 方式2：多profile方式 - 文档块方式

- 这种方式将所有环境的配置放在一个配置文件中，并通过 Profile 块来区分不同的环境配置。
- 在 `application.yaml` 或 `application.yml` 中，**全局配置**通常是最先加载的配置，通常在文件的顶部定义。之后，针对不同的环境或 Profile，你可以通过 `---` 来分隔不同的配置块（如开发环境、生产环境等）。

```yaml
# 全局配置
server:
  port: 8081
spring:
  application:
    name: MyApp  # 全局应用名称
  profiles:
  	active: prod

# 开发环境配置
---
server:
  port: 8082  # 开发环境端口
spring:
  profiles: dev
  datasource:
    url: jdbc:mysql://localhost:3306/devdb  # 开发环境数据库
    username: devuser
    password: devpassword


# 生产环境配置
---
server:
  port: 8083  # 生产环境端口
spring:
  profiles: prod
  datasource:
    url: jdbc:mysql://prod-db:3306/proddb  # 生产环境数据库
   	username: produser
    password: prodpassword
```

> **<font color="red">注意：这种方式对于`properties`格式的配置文件是不适用的！</font>**

### 5.1.3 方式3：通过Maven来实现多环境配置

- Maven允许你根据不同的构建配置（Profile）来定制不同的资源文件，进而实现针对不同环境的配置。
- 在Maven中，可以通过`<profiles>`和`<profile>`标签来来定义不同的构建环境，每个环境对应不同的属性和资源文件。结合Maven的资源过滤功能，可以在构建过程中自动替换资源文件中的占位符，从而实现多环境的配置切换。

#### 5.1.3.1 配置Maven Profile

- Maven的`<profile>`元素允许你为不同的环境指定不同的构建配置。每个`<profile>`可以包含不同的属性、资源文件路径等。

- 下面是一个示例，定义了多个构建环境，包括开发、测试、预发布和生产环境：

  ```xml
  <profiles>
      <!-- 本地开发环境 -->
      <profile>
          <id>dev</id>
          <properties>
              <env>dev</env>
          </properties>
          <activation>
              <activeByDefault>true</activeByDefault>
          </activation>
      </profile>
      
      <!-- 测试环境 -->
      <profile>
          <id>test</id>
          <properties>
              <env>test</env>
          </properties>
      </profile>
      
      <!-- 生产环境 -->
      <profile>
          <id>prod</id>
          <properties>
              <env>prod</env>
          </properties>
      </profile>
  </profiles>
  ```

#### 5.1.3.2 配置资源文件过滤

- Maven提供了资源文件过滤功能，通过在`<build>`中配置`<resources>`来指定哪些资源文件需要进行过滤。启用`<filtering>`后，Maven会在构建过程中替换资源文件中定义的占位符。配置如下：

  ```xml
  <build>
      <resources>
          <resource>
              <directory>src/main/resources</directory>
          </resource>
          <resource>
              <directory>src/main/resources-env/${env}</directory>
              <filtering>true</filtering>  <!-- 开启过滤，替换占位符 -->
          </resource>
      </resources>
  </build>
  ```

#### 5.1.3.3 配置环境加载

- 假设我们有以下的资源文件目录结构：

![QQ_1735741571536](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735741571536.png)

- **方式1：借助IDEA工具（推荐）。如果我们配置好了`pom.xml`文件以及在指定目录下创建好了配置文件，就会出现如下图所示选项：**

![QQ_1735741640877](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735741640877.png)

- 方式2：通过Maven工具，在打包的时候指定参数。例如：

  ```cmd
  mvn clean package -Pdev  # 开发环境构建
  mvn clean package -Ptest # 测试环境构建
  mvn clean package -Pprod # 生产环境构建
  ```

#### 5.1.3.4 案例

- 案例内容：通过Maven来实现多环境配置，并通过IDEA来指定生产环境为`prod`

- 编写`pom.xml`文件：

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <groupId>com.zzx</groupId>
      <artifactId>springboot-config-file-demo03</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <name>springboot-config-file-demo03</name>
      <description>springboot-config-file-demo03</description>
      <properties>
          <java.version>1.8</java.version>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
          <spring-boot.version>2.6.13</spring-boot.version>
      </properties>
      <dependencies>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter</artifactId>
          </dependency>
  
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-configuration-processor</artifactId>
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
  
      <profiles>
          <!-- 本地开发环境 -->
          <profile>
              <id>dev</id>
              <properties>
                  <env>dev</env>
              </properties>
              <activation>
                  <activeByDefault>true</activeByDefault>
              </activation>
          </profile>
  
          <!-- 测试环境 -->
          <profile>
              <id>test</id>
              <properties>
                  <env>test</env>
              </properties>
          </profile>
  
          <!-- 生产环境 -->
          <profile>
              <id>prod</id>
              <properties>
                  <env>prod</env>
              </properties>
          </profile>
      </profiles>
  
      <build>
          <resources>
              <resource>
                  <directory>src/main/resources</directory>
              </resource>
              <resource>
                  <directory>src/main/resources-env/${env}</directory>
                  <filtering>true</filtering>  <!-- 开启过滤，替换占位符 -->
                  <includes>
                      <include>**/*.yml</include>
                      <include>**/*.yaml</include>
                      <include>**/*.properties</include>
                  </includes>
              </resource>
          </resources>
  
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
                      <mainClass>com.zzx.SpringbootConfigFileDemo03Application</mainClass>
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

- 配置文件目录：

![QQ_1735741928375](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735741928375-1735741933601-28.png)

- `src/main/resources/application.yaml`：

  ```yaml
  spring:
    config:
      import: application-user-info.yaml
  
  # 作者信息
  author:
    name: 毛毛张
    email: 1849975198@qq.com
  
  # 用户信息
  app:
    user:
      name: defaultUser
      email: defaultUser@example.com
  ```

- `src/main/resources-env/dev/application-user-info.yaml`：

  ```yaml
  # 用户信息
  app:
    user:
      name: TestUser
      email: testuser@example.com
  ```

- `src/main/resources-env/prod/application-user-info.yaml`：

  ```yaml
  # 用户信息
  app:
    user:
      name: ProdUser
      email: produser@example.com
  ```

- `src/main/resources-env/test/application-user-info.yaml`：

  ```yaml
  # 用户信息
  app:
    user:
      name: TestUser
      email: testuser@example.com
  ```

- 配置类

  ```java
  package com.zzx;
  
  
  import lombok.Data;
  import org.springframework.boot.context.properties.ConfigurationProperties;
  import org.springframework.stereotype.Component;
  
  @Data
  @Component
  @ConfigurationProperties(prefix = "app.user")
  public class User {
      private String name;
      private String email;
  }
  ```

  ```java
  package com.zzx;
  
  
  import lombok.Data;
  import org.springframework.boot.context.properties.ConfigurationProperties;
  import org.springframework.stereotype.Component;
  
  @Data
  @Component
  @ConfigurationProperties(prefix = "author")
  public class Author {
      private String name;
      private String email;
  }
  ```

- 启动类：

  ```java
  package com.zzx;
  
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  import javax.annotation.PostConstruct;
  
  @SpringBootApplication
  public class SpringbootConfigFileDemo03Application {
      @Autowired
      private User user;
  
      @Autowired
      private Author author;
  
      public static void main(String[] args) {
          SpringApplication.run(SpringbootConfigFileDemo03Application.class, args);
      }
  
      @PostConstruct
      public void init() {
          System.out.println("作者信息：" + author);
          System.out.println("用户信息：" + user);
      }
  }
  
  ```

- 设置环境为生产：

![QQ_1735742280320](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735742280320.png)

- 打印输出：



> 毛毛张在这个案例中还把前面介绍的`spring.config.import`导入配置文件这个知识点也介绍进去了，可谓是用心良苦，精心设计

## 5.2 多环境配置的激活

### 5.2.1 在主配置文件中激活指定配置文件

- 该方式就是在`application.yaml`中通过`spring.profile.active = dev/prod`来选择需要激活的配置文件

### 5.2.2 命令行参数激活指定配置文件

- 这种方式是在命令行将项目打包成`jar`包时，在打包命令中加入参数`--spring.profiles.active=dev/prod`

  ```cmd
  java -jar spring-boot-demo-config-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod  # 指定激活生产环境
  ```

### 5.2.3 IDEA 中配置参数激活指定配置文件

- 现在有现成的IDEA编辑器，因此用命令行指定你就慢了，我们可以通过IDEA来进行配置

![QQ_1735722719082](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735722719082.png)

#### 5.2.3.1 设置运行参数

- 将`Active profiles`的参数设置为`dev`，表示激活开发环境

![QQ_1735729258552](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735729258552.png)

#### 5.2.3.2 设置虚拟机参数

- 将`VM options`的参数设置为`-Dspring.profiles.active=dev`

![QQ_1735729470160](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735729470160.png)

### 5.2.4 激活方式的优先级

- 上面毛毛张介绍了三种指定配置文件的方式，**三种方式的优先级的顺序为：`命令行参数激活 > IDEA参数激活 > 配置文件激活`，优先级高的会覆盖优先级低的激活配置**

## 5.3 案例

- 毛毛张在这一节做个综合案例，来介绍一下前面介绍的知识点
- **案例需求**：我在测试环境的配置文件中和生产环境的配置文件中配置两个不同用户的信息，我在全局配置文件中指定激活测试环境，并读取测试环境中的用户的信息

- 全局配置文件`application.properties`：

  ```properties
  # 全局配置
  spring.profiles.active=dev
  ```

- 开发环境配置文件`application-dev.properties`：

  ```properties
  # 用户配置 - 测试环境
  person.name=testUser
  person.email=testuser@example.com
  ```

- 生产环境配置文件`application-prod.properties`：

  ```properties
  # 用户配置 - 生产环境
  person.name=prodUser
  person.email=produser@example.com
  ```

- 用户类：

  ```java
  package com.zzx.config;
  
  import lombok.Data;
  import org.springframework.boot.context.properties.ConfigurationProperties;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.stereotype.Component;
  
  @Data
  @Component
  @ConfigurationProperties(prefix = "person")  // 绑定配置文件中的 "person" 前缀
  public class UserInfo {
      private String name;
      private String email;
  }
  ```

- 启动类：

  ```java
  package com.zzx;
  
  import com.zzx.config.UserInfo;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  import javax.annotation.PostConstruct;
  
  @SpringBootApplication
  public class SpringbootConfigFileDemo02Application {
      @Autowired
      private UserInfo userInfo;
  
      public static void main(String[] args) {
          SpringApplication.run(SpringbootConfigFileDemo02Application.class, args);
      }
  
      @PostConstruct
      public void init() {
          // 打印配置的用户信息
          System.out.println("用户信息：");
          System.out.println(userInfo);
      }
  }
  ```

- 项目结构：

![QQ_1735729830670](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735729830670.png)

- 配置激活那个开发环境，毛毛张以及在配置文件中配置了，或者如下方式配置也行

![QQ_1735722719082](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735722719082.png)

![QQ_1735729537405](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735729537405.png)



# 6.其它形式配置

- **在前面讲属性配置的时候，配置都是写在项目目录下，或者写在类目录下；配置文件的格式是`properties`和`yaml`这两种格式。其实上面已经有使用到其它形式的配置，比如读取命令行参数信息到配置文件里，通过设置命令行的参数激活不同的配置环境。这里的命令行参数也是一种动态配置信息。**
- 这一部分的内容，毛毛张暂时还没有接触很深，讲解的比较浅，如果知道前面的知识点就能开始做项目了

## 6.1 其它形式配置

### 6.1.1 系统环境变量配置

系统环境变量可以用于配置 Spring Boot 应用中的属性。

### 6.1.2 命令行参数

- 所有的配置都可以在命令行上进行指定

```cmd
java -jar spring-boot-02-config-02-0.0.1-SNAPSHOT.jar --server.port=8087 --server.context-path=/abc
```

- 多个配置项可以通过空格分开，格式为 `--配置项=值`。

### 6.1.3 Java系统属性

- 通过 `System.getProperties()` 获取的 Java 系统属性也可以用来配置应用。

### 6.1.4 注解读取的配置文件

- Spring 通过注解（如 `@PropertySource`）读取外部配置文件中的属性。

### 6.1.5 启动类属性

- 启动类通过 `SpringApplication.setDefaultProperties` 设置的默认属性。

## 6.2 配置加载优先级

- Spring Boot 从不同位置加载配置，优先级顺序如下：**以下配置优先级从高到低；高优先级的配置覆盖低优先级的配置；所有的配置会形成互补配置**
  1. 命令行参数
  2. 来自 `java:comp/env` 的 JNDI 属性
  3. Java 系统属性 (`System.getProperties()`)
  4. 操作系统环境变量
  5. RandomValuePropertySource 配置的 `random.*` 属性值
  6. jar 包外部的 `application-{profile}.properties` 或 `application.yml`（带 profile 配置）
  7. jar 包内部的 `application-{profile}.properties` 或 `application.yml`（带 profile 配置）
  8. jar 包外部的 `application.properties` 或 `application.yml`（不带 profile 配置）
  9. jar 包内部的 `application.properties` 或 `application.yml`（不带 profile 配置）
  10. `@PropertySource` 注解配置的属性文件
  11. 通过 `SpringApplication.setDefaultProperties` 设置的默认属性

# 7 常见问题

## 7.1 配置文件乱码

- 我们在编写配置文件的时候，可能会出现中文乱码的情况，那是因为我们的文件的编码格式不是`UTF-8`，解决方案见下图
- **首先设置编码：`File -> Setting -> Editor -> File encodings --> 设置编码`**

![QQ_1735743461061](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735743461061.png)

- **然后配置文件中文乱码：`File -> Setting -> Editor -> File encodings -> 勾选 "Transparent native-to-ascll conversion" 即可 `**

![QQ_1735704823147](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/QQ_1735704823147.png)

# 参考文献

- <https://zhuanlan.zhihu.com/p/57693064>
- <https://juejin.cn/post/7002889675656413192>
- <https://www.cnblogs.com/jiujuan/p/16700983.html>
- <https://juejin.cn/post/7083325770755489800>
- <https://cloud.tencent.com/developer/article/1668079>
- <https://blog.csdn.net/dream_ready/article/details/134278773>
- <https://blog.csdn.net/IT__learning/article/details/119964806>
- <https://blog.csdn.net/HaHa_Sir/article/detailIdea>
- <https://blog.csdn.net/m0_64363449/article/details/132314842>