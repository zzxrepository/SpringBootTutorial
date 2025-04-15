



# 3.Swagger-UI与Knife4j

## 3.1 Spring Boot整合Swagger-UI实现在线API文档

### 3.1.1 Swagger简介

Swagger 是一个用于生成、描述和调用 RESTful 接口的 Web 服务。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/springboot/swagger-febf2633-5b02-425b-a513-c8583e14d621.png)

> 想要理解RESTful架构的话，可以戳链接查看阮一峰老师的博客：https://www.ruanyifeng.com/blog/2011/09/restful.html

换句话说，Swagger 就是将项目中想要暴露的接口展示在页面上，开发者可以直接进行接口调用和测试，能在很大程度上提升开发的效率。

比如说，一个后端程序员写了一个登录接口，想要测试自己写的接口是否符合预期的话，就得先模拟用户登录的行为，包括正常的行为（输入正确的用户名和密码）和异常的行为（输入错误的用户名和密码），这就要命了。

但有了 Swagger 后，可以通过简单的配置生成接口的展示页面，把接口的请求参数、返回结果通过可视化的形式展示出来，并且提供了便捷的测试服务。

- 前端程序员可以通过接口展示页面查看需要传递的请求参数和返回的数据格式，不需要后端程序员再编写接口文档了；
- 后端程序员可以通过接口展示页面测试验证自己的接口是否符合预期，降低了开发阶段的调试成本。

前后端分离就可以很完美的落地了，有没有？

> Swagger 官网地址：https://swagger.io/

那在 Swagger 出现之前，局面就比较糟糕。前端经常抱怨后端给的接口文档与实际情况不一致。后端又觉得编写及维护接口文档会耗费不少精力，经常来不及更新。

大家都被无情地折磨，痛不堪言。。。

Swagger 定义了一套规范，你只需要按照它的规范去定义接口及接口相关的信息，然后通过 Swagger 衍生出来的一系列工具，就可以生成各种格式的接口文档，甚至还可以生成多种语言的客户端和服务端代码，以及在线接口调试页面等等。

那只要及时更新 Swagger 的描述文件，就可以自动生成接口文档了，做到调用端代码、服务端代码以及接口文档的一致性。

### 整合 Swagger-UI

Swagger-UI 是一套 HTML/CSS/JS 框架，用于渲染 Swagger 文档，以便提供美观的 API 文档界面。

也就是说，Swagger-UI 是 Swagger 提供的一套可视化渲染组件，支持在线导入描述文件和本地部署UI项目。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/springboot/swagger-9cb36679-f1f7-469e-925e-2e54090f700f.png)

第一步，在 pom.xml 文件中添加 Swagger 的 starter。

```
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

咦，不是说添加 Swagger 的依赖吗？怎么添加的是 springfox-boot-starter 呢？

这是因为：

- Swagger 是一种规范。
- springfox-swagger 是一个基于 Spring 生态系统的，Swagger 规范的实现。
- springfox-boot-starter 是 springfox 针对 Spring Boot 项目提供的一个 starter，简化 Swagger 依赖的导入，否则我们就需要在 pom.xml 文件中添加 springfox-swagger、springfox-swagger-ui 等多个依赖。

第二步，添加 Swagger 的 Java 配置。

```
@Configuration
@EnableOpenApi
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        Docket docket = new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo()).enable(true)
                .select()
                //apis： 添加swagger接口提取范围
                .apis(RequestHandlerSelectors.basePackage("top.codingmore.controller"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("编程猫实战项目笔记")
                .description("编程喵是一个 Spring Boot+Vue 的前后端分离项目")
                .contact(new Contact("沉默王二", "https://codingmore.top","www.qing_gee@163.com"))
                .version("v1.0")
                .build();
    }
}
```

1）@Configuration 注解通常用来声明一个 Java 配置类，取代了以往的 xml 配置文件，让配置变得更加的简单和直接。

2）@EnableOpenApi 注解表明开启 Swagger。

3）SwaggerConfig 类中包含了一个 @Bean 注解声明的方法 `docket()`，该方法会被 Spring 的 AnnotationConfigApplicationContext 或 AnnotationConfigWebApplicationContext 类进行扫描，然后添加到 Spring 容器当中。

```
AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
  ctx.register(AppConfig.class);
  ctx.refresh();
  MyBean myBean = ctx.getBean(MyBean.class);
```

简单描述一下 Swagger 的配置内容：

- `new Docket(DocumentationType.OAS_30)`，使用 3.0 版本的 Swagger API。OAS 是 OpenAPI Specification 的简称，翻译成中文就是 OpenAPI 说明书，Swagger 遵循的就是这套规范。
- `apiInfo(apiInfo())`，配置 API 文档基本信息，标题、描述、作者、版本等。
- `apis(RequestHandlerSelectors.basePackage("top.codingmore.controller"))` 指定 API 的接口范围为 controller 控制器。
- `paths(PathSelectors.any())` 指定匹配所有的 URL。

第三步，添加控制器类。

```
@Api(tags = "测试 Swagger")
@RestController
@RequestMapping("/swagger")
public class SwaggerController {

    @ApiOperation("测试")
    @RequestMapping("/test")
    public String test() {
        return "沉默王二又帅又丑";
    }
}
```

1）@Api注解，用在类上，该注解将控制器标注为一个 Swagger 资源。该注解有 3 个属性：

- tags，具有相同标签的 API 会被归在一组内展示
- value，如果 tags 没有定义，value 将作为 API 的 tags 使用。
- description，已废弃

2）@ApiOperation 注解，用在方法上，描述这个方法是做什么用的。该注解有 4 个属性：

- value 操作的简单说明，长度为120个字母，60个汉字。
- notes 对操作的详细说明。
- httpMethod HTTP请求的动作名，可选值有："GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS" and "PATCH"。
- code 默认为200，有效值必须符合标准的HTTP Status Code Definitions。

3）@RestController 注解，用在类上，是@ResponseBody和@Controller的组合注解，如果方法要返回 JSON 的话，可省去 @ResponseBody 注解。

4）@RequestMapping 注解，可用在类（父路径）和方法（子路径）上，主要用来定义 API 的请求路径和请求类型。该注解有 6 个属性：

- value，指定请求的实际地址
- method，指定请求的method类型， GET、POST、PUT、DELETE等
- consumes，指定处理请求的提交内容类型（Content-Type），例如 application/json, text/html
- produces，指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回
- params，指定request中必须包含某些参数值
- headers，指定request中必须包含某些指定的header值

第四步，启动服务，在浏览器中输入 `http://localhost:8080/swagger-ui/` 就可以访问 Swagger 生成的 API 文档了。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/springboot/swagger-25187213-723a-4120-8485-06759a509659.png)

点开 get 请求的面板，点击「try it out」再点击「excute」可以查看接口返回的数据。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/springboot/swagger-0f2b9c42-bae4-4712-be29-3771ab3bd3a8.png)

### 版本不兼容

在 Spring Boot 整合 Swagger 的过程中，我发现一个大 bug，Spring Boot 2.6.7 版本和 springfox 3.0.0 版本不兼容，启动的时候直接就报错了。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/springboot/swagger-529160e4-aa31-410a-aa04-93e9576322b6.png)

> Caused by: java.lang.NullPointerException: Cannot invoke "org.springframework.web.servlet.mvc.condition.PatternsRequestCondition.getPatterns()" because "this.condition" is null

一路跟踪下来，发现 GitHub 上确认有人在 Spring Boot 仓库下提到了这个 bug。

> https://github.com/spring-projects/spring-boot/issues/28794

Spring Boot 说这是 springfox 的 bug。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/springboot/swagger-d0d336e1-2cba-49f4-bd65-6df7f89a6c9f.png)

追踪过来一看，确实。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/springboot/swagger-cb3c15e7-ecfd-4e5e-92b7-673acb966a54.png)

有人提到的解决方案是切换到 SpringDoc。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/springboot/swagger-0597f86d-1188-4fe1-8de8-fdb57c5cd524.png)

这样就需要切换注解 `@Api → @Tag`，`@ApiOperation(value = "foo", notes = "bar") → @Operation(summary = "foo", description = "bar")`，对旧项目不是很友好，如果是新项目的话，倒是可以直接尝试 SpringDoc。

还有人提出的解决方案是：

- 先将匹配策略调整为 ant-path-matcher（application.yml）。

```
spring:
  mvc:
    path match:
      matching-strategy: ANT_PATH_MATCHER
```

- 然后在 Spring 容器中注入下面这个 bean，可以放在 SwaggerConfig 类中。

```
@Bean
public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
    return new BeanPostProcessor() {

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
                customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
            }
            return bean;
        }

        private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
            List<T> copy = mappings.stream()
                    .filter(mapping -> mapping.getPatternParser() == null)
                    .collect(Collectors.toList());
            mappings.clear();
            mappings.addAll(copy);
        }

        @SuppressWarnings("unchecked")
        private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
            try {
                Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                field.setAccessible(true);
                return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    };
}
```

> 解决方案地址：https://github.com/springfox/springfox/issues/3462

重新编译项目，就会发现错误消失了，我只能说GitHub 仓库的 issue 区都是大神！

查看 Swagger 接口文档，发现一切正常。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/springboot/swagger-05265d24-5242-48ac-9776-58e72798a545.png)

我只能再强调一次，GitHub 仓库的 issue 区都是大神！大家遇到问题的时候，一定要多到 issue 区看看。

至于为什么要这样做，问题的解决者给出了自己的答案。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/springboot/swagger-8b3d90d6-4eac-4db8-ab52-69c55078df36.png)

大致的意思就是 springfox 和 Spring 在 pathPatternsCondition 上产生了分歧，这两个步骤就是用来消除这个分歧的。

除此之外，还有另外一种保守的做法，直接将 Spring Boot 的版本回退到更低的版本，比如说 2.4.5。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/springboot/swagger-41096e72-bd7c-4663-b57e-fbc8506ec1cc.png)

### 小结

Swagger 虽然解决了调用端代码、服务端代码以及接口文档的不一致的问题，但有一说一，Swagger-UI 实在是太丑了。









## 3.2 Spring Boot整合Knife4j，美化强化丑陋的Swagger

一般在使用 Spring Boot 开发前后端分离项目的时候，都会用到 [Swagger](https://javabetter.cn/springboot/swagger.html)（戳链接详细了解）。

但随着系统功能的不断增加，接口数量的爆炸式增长，Swagger 的使用体验就会变得越来越差，比如请求参数为 JSON 的时候没办法格式化，返回结果没办法折叠，还有就是没有提供搜索功能。

今天我们介绍的主角 Knife4j 弥补了这些不足，赋予了 Swagger 更强的生命力和表现力。

### 3.2.1 Knife4j简介

Knife4j 的前身是 swagger-bootstrap-ui，是 springfox-swagger-ui 的增强 UI 实现。swagger-bootstrap-ui 采用的是前端 UI 混合后端 Java 代码的打包方式，在微服务的场景下显得非常臃肿，改良后的 Knife4j 更加小巧、轻量，并且功能更加强大。

springfox-swagger-ui 的界面长这个样子，说实话，确实略显丑陋。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-1.png)

swagger-bootstrap-ui 增强后的样子长下面这样。单纯从直观体验上来看，确实增强了。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-2.png)

那改良后的 Knife4j 不仅在界面上更加优雅、炫酷，功能上也更加强大：后端 Java 代码和前端 UI 模块分离了出来，在微服务场景下更加灵活；还提供了专注于 Swagger 的增强解决方案。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-3.png)

官方文档：

> https://doc.xiaominfo.com/knife4j/documentation/

码云地址：

> https://gitee.com/xiaoym/knife4j

示例地址：

> https://gitee.com/xiaoym/swagger-bootstrap-ui-demo

### 3.2.2 整合 Knife4j

Knife4j 完全遵循了 Swagger 的使用方式，所以可以无缝切换。

第一步，在 pom.xml 文件中添加 Knife4j 的依赖（**不需要再引入 springfox-boot-starter**了，因为 Knife4j 的 starter 里面已经加入过了）。

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-boot-starter</artifactId>
    <!--在引用时请在maven中央仓库搜索3.X最新版本号-->
    <version>3.0.2</version>
</dependency>
```

第二步，配置类 SwaggerConfig 还是 Swagger 时期原来的配方。

```java
@Configuration
@EnableOpenApi
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        Docket docket = new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo()).enable(true)
                .select()
                //apis： 添加swagger接口提取范围
                .apis(RequestHandlerSelectors.basePackage("com.zzx.controller"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("xxxx项目实战")
                .description("项目描述")
                .contact(new Contact("毛毛张", "https://www.mmzhang.cn","zzxkingdom@163.com"))
                .version("v1.0")
                .build();
    }
}
```

第三步，新建测试控制器类 Knife4jController.java：

```java
@Api(tags = "测试 Knife4j")
@RestController
@RequestMapping("/knife4j")
public class Knife4jController {

    @ApiOperation("测试")
    @RequestMapping(value ="/test", method = RequestMethod.POST)
    public String test() {
        return "沉默王二又帅又丑";
    }
}
```

第四步，由于 springfox 3.0.x 版本 和 Spring Boot 2.6.x 版本有冲突，所以还需要先解决这个 bug，一共分两步（在[Swagger](https://javabetter.cn/springboot/swagger.html) 那篇已经解释过了，这里不再赘述，但防止有小伙伴在学习的时候再次跳坑，这里就重复一下步骤）。

先在 application.yml 文件中加入：

```yaml
spring:
  mvc:
    path match:
      matching-strategy: ANT_PATH_MATCHER
```

再在 SwaggerConfig.java 中添加：

```java
@Bean
public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
    return new BeanPostProcessor() {

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
                customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
            }
            return bean;
        }

        private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
            List<T> copy = mappings.stream()
                    .filter(mapping -> mapping.getPatternParser() == null)
                    .collect(Collectors.toList());
            mappings.clear();
            mappings.addAll(copy);
        }

        @SuppressWarnings("unchecked")
        private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
            try {
                Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                field.setAccessible(true);
                return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    };
}
```

以上步骤均完成后，开始下一步，否则要么项目启动的时候报错，要么在文档中看不到测试的文档接口。

第五步，运行 Spring Boot 项目，浏览器地址栏输入以下地址访问 API 文档，查看效果。

> 访问地址（和 Swagger 不同）：http://localhost:8080/doc.html

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-0a9eb2b1-bace-4f47-ace9-8a5f9f280279.png)

是不是比 Swagger 简洁大方多了？如果想测试接口的话，可以直接点击接口，然后点击「测试」，点击发送就可以看到返回结果了。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-16b1b553-1667-4222-9f29-2e5dfc8917a0.png)

### Knife4j 的功能特点

编程喵🐱实战项目中已经整合好了 Knife4j，在本地跑起来后，就可以查看所有 API 接口了。编程喵中的管理端（codingmore-admin）端口为 9002，启动服务后，在浏览器中输入 http://localhost:9002/doc.html 就可以访问到了。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-3cfbf598-b94a-4081-aab3-06af1eef612c.png)

简单来介绍下 Knife4j 的 功能特点：

**1）支持登录认证**

Knife4j 和 Swagger 一样，也是支持头部登录认证的，点击「authorize」菜单，添加登录后的信息即可保持登录认证的 token。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-6.png)

如果某个 API 需要登录认证的话，就会把之前填写的信息带过来。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-7.png)

**2）支持 JSON 折叠**

Swagger 是不支持 JSON 折叠的，当返回的信息非常多的时候，界面就会显得非常的臃肿。Knife4j 则不同，可以对返回的 JSON 节点进行折叠。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-8.png)

**3）离线文档**

Knife4j 支持把 API 文档导出为离线文档（支持 markdown 格式、HTML 格式、Word 格式），

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-9.png)

使用 Typora 打开后的样子如下，非常的大方美观。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-10.png)

**4）全局参数**

当某些请求需要全局参数时，这个功能就很实用了，Knife4j 支持 header 和 query 两种方式。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-11.png)

之后进行请求的时候，就会把这个全局参数带过去。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-12.png)

**5）搜索 API 接口**

Swagger 是没有搜索功能的，当要测试的接口有很多的时候，当需要去找某一个 API 的时候就傻眼了，只能一个个去拖动滚动条去找。

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-13.png)

在文档的右上角，Knife4j 提供了文档搜索功能，输入要查询的关键字，就可以检索筛选了，是不是很方便？

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-14.png)

目前支持搜索接口的地址、名称和描述。

### 尾声

除了我上面提到的增强功能，Knife4j 还提供了很多实用的功能，大家可以通过官网的介绍一一尝试下，生产效率会提高不少。

> https://doc.xiaominfo.com/knife4j/documentation/enhance.html

![img](https://cdn.tobebetterjavaer.com/tobebetterjavaer/images/gongju/knife4j-15.png)









# 6.RESTFull风格设计和实战

## 6.1RESTFul风格概述

### 6.1.1 RESTFul风格简介

- RESTful（Representational State Transfer）是一种软件架构风格，用于设计网络应用程序和服务之间的通信。它是一种基于标准 HTTP 方法的简单和轻量级的通信协议，广泛应用于现代的Web服务开发。
- 通过遵循 RESTful 架构的设计原则，可以构建出易于理解、可扩展、松耦合和可重用的 Web 服务。RESTful API 的特点是简单、清晰，并且易于使用和理解，它们使用标准的 HTTP 方法和状态码进行通信，不需要额外的协议和中间件。
- 总而言之，RESTful 是一种基于 HTTP 和标准化的设计原则的软件架构风格，用于设计和实现可靠、可扩展和易于集成的 Web 服务和应用程序！学习RESTful设计原则可以帮助我们更好去设计HTTP协议的API接口！！

![](./assets/image_X8M-XfzI_A.png)

### 6.1.2 RESTFul风格特点

- 每一个URI代表1种资源（URI 是名词）；
- 客户端使用GET、POST、PUT、DELETE 4个表示操作方式的动词对服务端资源进行操作：
  - GET用来获取资源
  - POST用来新建资源（也可以用于更新资源）
  - PUT用来更新资源
  - DELETE用来删除资源；

- 资源的表现形式是XML或者**JSON**；
- 客户端与服务端之间的交互在请求之间是无状态的，从客户端到服务端的每个请求都必须包含理解请求所必需的信息。

### 6.1.3 **RESTFul风格设计规范**

1. **HTTP协议请求方式要求**

   REST 风格主张在项目设计、开发过程中，具体的操作符合**HTTP协议定义的请求方式的语义**。

   | 操作     | 请求方式 |
   | -------- | -------- |
   | 查询操作 | GET      |
   | 保存操作 | POST     |
   | 删除操作 | DELETE   |
   | 更新操作 | PUT      |

2. **URL路径风格要求**

   REST风格下每个资源都应该有一个唯一的标识符，例如一个 URI（统一资源标识符）或者一个 URL（统一资源定位符）。资源的标识符应该能明确地说明该资源的信息，同时也应该是可被理解和解释的！

   使用URL+请求方式确定具体的动作，他也是一种标准的HTTP协议请求！

   | 操作 | 传统风格                | REST 风格                                  |
   | ---- | ----------------------- | ------------------------------------------ |
   | 保存 | /CRUD/saveEmp           | URL 地址：/CRUD/emp&#xA;请求方式：POST     |
   | 删除 | /CRUD/removeEmp?empId=2 | URL 地址：/CRUD/emp/2&#xA;请求方式：DELETE |
   | 更新 | /CRUD/updateEmp         | URL 地址：/CRUD/emp&#xA;请求方式：PUT      |
   | 查询 | /CRUD/editEmp?empId=2   | URL 地址：/CRUD/emp/2&#xA;请求方式：GET    |

- 总结

  根据接口的具体动作，选择具体的HTTP协议请求方式

  路径设计从原来携带动标识，改成名词，对应资源的唯一标识即可！

### 6.1.4 RESTFul风格好处

1. 含蓄，安全

   使用问号键值对的方式给服务器传递数据太明显，容易被人利用来对系统进行破坏。使用 REST 风格携带数据不再需要明显的暴露数据的名称。

2. 风格统一

   URL 地址整体格式统一，从前到后始终都使用斜杠划分各个单词，用简单一致的格式表达语义。

3. 无状态

   在调用一个接口（访问、操作资源）的时候，可以不用考虑上下文，不用考虑当前状态，极大的降低了系统设计的复杂度。

4. 严谨，规范

   严格按照 HTTP1.1 协议中定义的请求方式本身的语义进行操作。

5. 简洁，优雅

   > 过去做增删改查操作需要设计4个不同的URL，现在一个就够了。
   >
   > | 操作 | 传统风格                | REST 风格                                  |
   > | ---- | ----------------------- | ------------------------------------------ |
   > | 保存 | /CRUD/saveEmp           | URL 地址：/CRUD/emp&#xA;请求方式：POST     |
   > | 删除 | /CRUD/removeEmp?empId=2 | URL 地址：/CRUD/emp/2&#xA;请求方式：DELETE |
   > | 更新 | /CRUD/updateEmp         | URL 地址：/CRUD/emp&#xA;请求方式：PUT      |
   > | 查询 | /CRUD/editEmp?empId=2   | URL 地址：/CRUD/emp/2&#xA;请求方式：GET    |

6. 丰富的语义：通过 URL 地址就可以知道资源之间的关系。它能够把一句话中的很多单词用斜杠连起来，反过来说就是可以在 URL 地址中用一句话来充分表达语义。

   > [http://localhost:8080/shop](http://localhost:8080/shop "http://localhost:8080/shop") [http://localhost:8080/shop/product](http://localhost:8080/shop/product "http://localhost:8080/shop/product") [http://localhost:8080/shop/product/cellPhone](http://localhost:8080/shop/product/cellPhone "http://localhost:8080/shop/product/cellPhone") [http://localhost:8080/shop/product/cellPhone/iPhone](http://localhost:8080/shop/product/cellPhone/iPhone "http://localhost:8080/shop/product/cellPhone/iPhone")

## 6.2 RESTFul风格实战

### 6.2.1 需求分析

-   数据结构： User {id 唯一标识,name 用户名，age 用户年龄}
-   功能分析
    -   用户数据分页展示功能（条件：page 页数 默认1，size 每页数量 默认 10）
    -   保存用户功能
    -   根据用户id查询用户详情功能
    -   根据用户id更新用户数据功能
    -   根据用户id删除用户数据功能
    -   多条件模糊查询用户功能（条件：keyword 模糊关键字，page 页数 默认1，size 每页数量 默认 10）

### 6.2.2 RESTFul风格接口设计

- **接口设计：**

| 功能     | 接口和请求方式   | 请求参数                        | 返回值     |
| -------- | ---------------- | ------------------------------- | ---------- |
| 分页查询 | GET  /user       | page=1\&size=10                 | {响应数据} |
| 用户添加 | POST /user       | { user 数据 }                   | {响应数据} |
| 用户详情 | GET /user/1      | 路径参数                        | {响应数据} |
| 用户更新 | PUT /user        | { user 更新数据}                | {响应数据} |
| 用户删除 | DELETE /user/1   | 路径参数                        | {响应数据} |
| 条件模糊 | GET /user/search | page=1\&size=10\&keywork=关键字 | {响应数据} |

- 问题讨论：
  - 为什么查询用户详情，就使用路径传递参数，而多条件模糊查询，就使用请求参数传递？
  - 误区：restful风格下，不是所有请求参数都是路径传递！可以使用其他方式传递！

- 在 RESTful API 的设计中，路径和请求参数和请求体都是用来向服务器传递信息的方式。

  -   对于查询用户详情，使用路径传递参数是因为这是一个单一资源的查询，即查询一条用户记录。使用路径参数可以明确指定所请求的资源，便于服务器定位并返回对应的资源，也符合 RESTful 风格的要求。

  -   而对于多条件模糊查询，使用请求参数传递参数是因为这是一个资源集合的查询，即查询多条用户记录。使用请求参数可以通过组合不同参数来限制查询结果，路径参数的组合和排列可能会很多，不如使用请求参数更加灵活和简洁。
      此外，还有一些通用的原则可以遵循：

  -   路径参数应该用于指定资源的唯一标识或者 ID，而请求参数应该用于指定查询条件或者操作参数。

  -   请求参数应该限制在 10 个以内，过多的请求参数可能导致接口难以维护和使用。

  -   对于敏感信息，最好使用 POST 和请求体来传递参数。

- 使用分类：
  - 没有请求体：使用路径传递参数`/url路径参数`或者请求参数`?key=value param参数`
    - 获取：GET
    - 删除：DELETE
  - 有请求体：使用请求体传递`JSON`
    - 保存：POST
    - 修改：PUT

### 6.2.3 后台接口实现

- 准备用户实体类：

  ```java
  package com.atguigu.pojo;
  
  /**
   * projectName: com.atguigu.pojo
   * 用户实体类
   */
  public class User {
  
      private Integer id;
      private String name;
  
      private Integer age;
  
      public Integer getId() {
          return id;
      }
  
      public void setId(Integer id) {
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
  
      @Override
      public String toString() {
          return "User{" +
                  "id=" + id +
                  ", name='" + name + '\'' +
                  ", age=" + age +
                  '}';
      }
  }
  
  ```

- 准备用户Controller：

  ```java
  /**
   * projectName: com.atguigu.controller
   *
   * description: 用户模块的控制器
   */
  @RequestMapping("user")
  @RestController
  public class UserController {
  
      /**
       * 模拟分页查询业务接口
       */
      @GetMapping
      public Object queryPage(@RequestParam(name = "page",required = false,defaultValue = "1")int page,
                              @RequestParam(name = "size",required = false,defaultValue = "10")int size){
          System.out.println("page = " + page + ", size = " + size);
          System.out.println("分页查询业务!");
          return "{'status':'ok'}";
      }
  
  
      /**
       * 模拟用户保存业务接口
       */
      @PostMapping
      public Object saveUser(@RequestBody User user){
          System.out.println("user = " + user);
          System.out.println("用户保存业务!");
          return "{'status':'ok'}";
      }
  
      /**
       * 模拟用户详情业务接口
       */
      @PostMapping("/{id}")
      public Object detailUser(@PathVariable Integer id){
          System.out.println("id = " + id);
          System.out.println("用户详情业务!");
          return "{'status':'ok'}";
      }
  
  
      /**
       * 模拟用户更新业务接口
       */
      @PutMapping
      public Object updateUser(@RequestBody User user){
          System.out.println("user = " + user);
          System.out.println("用户更新业务!");
          return "{'status':'ok'}";
      }
  
  
      /**
       * 模拟条件分页查询业务接口
       */
      @GetMapping("search")
      public Object queryPage(@RequestParam(name = "page",required = false,defaultValue = "1")int page,
                              @RequestParam(name = "size",required = false,defaultValue = "10")int size,
                              @RequestParam(name = "keyword",required= false)String keyword){
          System.out.println("page = " + page + ", size = " + size + ", keyword = " + keyword);
          System.out.println("条件分页查询业务!");
          return "{'status':'ok'}";
      }
  }
  ```

  





