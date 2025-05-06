

[toc]

# 1.跨域

## 1.1 基本概念

- **跨域（Cross-Origin）**：指的是当网页中的资源或脚本试图与来自不同域名、协议或端口的服务器进行交互时，发生的情况。为了防止潜在的安全风险，浏览器实施了**同源策略（Same Origin Policy）**，以确保仅能与相同源的资源交互，除非明确允许跨域访问。跨域问题通常发生在需要从不同源获取资源时。

- **同源策略：** 同源策略是浏览器的核心安全机制。它规定，一个页面只能与具有相同协议、主机和端口号的资源进行交互。也就是说，如果页面和资源位于不同域名、协议或端口号上，浏览器将阻止脚本访问该资源。这一策略是为了防止恶意脚本通过跨域请求窃取敏感信息或执行不受用户控制的操作。
  - 举例说明：假设你在 `https://example.com` 网站上运行了一个 JavaScript 脚本，该脚本默认情况下只能访问来自 `https://example.com` 的资源。如果该脚本试图请求来自 `https://api.otherdomain.com` 的资源，由于跨域限制，该请求将被浏览器阻止。

- **跨域资源共享 (CORS)：** 为了应对跨域资源访问的需求，出现了**跨域资源共享（CORS）**机制。CORS 允许服务器通过在 HTTP 响应头中添加特定的字段，告诉浏览器允许特定的源进行跨域访问。CORS 通过浏览器的预检请求（preflight request）来确认请求是否安全，然后决定是否允许访问。
  - 举例说明：
    1. **跨域请求发生时：** 浏览器向 `https://api.otherdomain.com` 发送请求，但会先检查这个域是否允许跨域访问。
    2. **CORS 响应头：** 如果目标服务器在响应中包含了类似 `Access-Control-Allow-Origin: https://example.com` 这样的头部信息，那么浏览器就会允许 `https://example.com` 上的 JavaScript 脚本访问这个资源。
    3. **预检请求：** 当请求涉及修改数据（例如使用 `PUT`, `DELETE` 方法），浏览器会先发出一个 `OPTIONS` 请求，询问目标服务器是否接受该请求。

- CORS 的作用：CORS 的设计初衷是为了防止恶意网站通过跨域请求窃取敏感信息或执行未经用户授权的操作。例如，假设你已经登录了一个银行网站，然后无意中访问了一个恶意网站。如果没有同源策略和 CORS，恶意网站可能会发送请求获取你银行账户的信息。因此，CORS 是一种保护用户的安全机制。

- 总结：跨域是指不同域名、协议或端口间的请求，而同源策略则限制了这些请求，以保护用户信息安全。CORS 是一种允许合法跨域请求的机制，通过指定响应头信息来明确哪些源可以访问资源。

## 1.2 为什么会出现跨域问题

- 跨域问题的产生可以总结为以下几点：

1. **前后端分离架构的需求**：在现代 Web 应用中，前后端通常是分离的。前端负责显示页面，后端负责提供数据资源。前端服务器和后端服务器可能有不同的协议、IP 地址或端口，这种差异导致了跨域请求。例如，前端从 `https://frontend.com` 访问后端的 `https://api.backend.com` 数据时，就会触发跨域问题。
2. **浏览器的同源策略**：浏览器为了安全，实施了**同源策略（Same Origin Policy）**。该策略限制不同源的 AJAX 请求，防止来自其他域的脚本访问当前域的资源。不同源指的是协议、域名或端口的不同。没有同源策略的限制，用户可能会遭受跨站请求伪造（CSRF）等攻击。
3. **防止安全威胁**：跨域问题的根本原因是浏览器为了保护用户的安全。如果没有同源策略的限制，恶意网站可以通过发送未经用户允许的跨域请求，获取敏感信息或执行恶意操作。因此，跨域问题是浏览器为了防范安全风险（如 CSRF 攻击）而产生的。

- 跨域问题本质上是安全策略与前后端分离架构之间的矛盾引发的。
- 图解：

![1683364198087](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/1683364198087-1690533535579.png)

## 1.3 如何解决跨域限制

- 跨域问题是浏览器为了安全而产生的限制机制，主要通过同源策略来防止来自不同源的脚本请求访问数据。解决跨域问题的主要方法之一是使用 **CORS**（跨域资源共享）。下面详细介绍如何通过 CORS 机制以及其他方式解决跨域问题。

### 1.3.1 推荐：CORS（Cross-Origin Resource Sharing））

- CORS 是 W3C 推荐的标准，允许服务器通过设置 HTTP 头信息来允许特定的源进行跨域访问。使用 CORS，可以使服务器支持 `XMLHttpRequest` 或 `fetch` 发起的跨域请求。

#### 1.3.1.1 CORS 请求分为两类

CORS 请求可以分为 **简单请求** 和 **非简单请求**，浏览器对这两类请求的处理方式不同。

##### 简单请求

**简单请求** 是指请求满足以下两个条件之一：

1. 请求方法必须是以下三种之一：
   - `GET`
   - `POST`
   - `HEAD`
2. HTTP 头信息只能包含以下字段：
   - `Accept`
   - `Accept-Language`
   - `Content-Language`
   - `Last-Event-ID`
   - **`Content-Type`（仅限于：`application/x-www-form-urlencoded`、`multipart/form-data`、`text/plain`）**

对于简单请求，浏览器会自动在请求头中添加 `Origin` 字段，标明请求的来源（协议 + 域名 + 端口），服务器根据该信息决定是否允许该请求。

- **服务器的响应：** 如果服务器允许该请求，会在响应头中添加以下字段：

  ```http
  Access-Control-Allow-Origin: http://example.com
  Access-Control-Allow-Credentials: true
  Access-Control-Expose-Headers: FooBar
  ```

- **解释：**

  - `Access-Control-Allow-Origin`: 允许哪些源可以访问该资源，`*` 表示允许任何域（但如果请求需要发送 `cookie`，不能设置为 `*`）。
  - `Access-Control-Allow-Credentials`: 是否允许发送 `cookie`，默认为 `false`。
  - `Access-Control-Expose-Headers`: 指定浏览器可以访问的自定义头字段。

##### 非简单请求

**非简单请求** 是指请求方法是 `PUT`、`DELETE` 或者包含自定义头部信息的请求（如 `application/json`）。对于非简单请求，浏览器会在发送实际请求前，先发送一个 **预检请求（preflight request）**，以确保服务器允许该请求。

- **预检请求** 是一个 `OPTIONS` 请求，询问前端服务器（在本地部署的时候也就是浏览器）是否允许该跨域操作：

  ```http
  OPTIONS /cors HTTP/1.1
  Origin: http://frontend.com
  Access-Control-Request-Method: PUT
  Access-Control-Request-Headers: X-Custom-Header
  ```

- **服务器的响应**： 如果服务器允许该操作，响应会包含以下字段：

  ```http
  HTTP/1.1 200 OK
  Access-Control-Allow-Origin: http://frontend.com
  Access-Control-Allow-Methods: GET, POST, PUT
  Access-Control-Allow-Headers: X-Custom-Header
  ```

  - `Access-Control-Allow-Methods`: 允许哪些 HTTP 方法。
  - `Access-Control-Allow-Headers`: 允许客户端发送哪些自定义头信息。

一旦预检请求成功，浏览器就会发送实际的请求。

### 1.3.2 使用 JSONP

**JSONP** 是一种早期的跨域解决方案，主要用于 `GET` 请求。它通过 `<script>` 标签加载远程 JavaScript 文件，该标签不受同源策略的限制。

- **步骤**：
  1. 前端创建一个 `<script>` 标签，并将请求的 URL 设置为远程服务器的地址。
  2. 服务器将返回的数据包装成一个 JavaScript 函数调用，前端通过回调函数接收数据。
- **缺点**：只能用于 `GET` 请求，不支持其他方法（如 `POST`、`PUT`）。而且由于 JSONP 直接执行远程脚本，可能存在安全风险。

### 1.3.3 使用代理服务器

前端可以通过设置 **代理服务器** 来避免跨域问题。代理服务器将请求转发给目标服务器，这样客户端只与代理服务器交互，避免直接跨域请求。

- **步骤**：
  1. 前端发送请求到代理服务器，代理服务器与目标服务器同源。
  2. 代理服务器接收请求后，将其转发到目标服务器，并返回结果。
- **优点**：代理服务器可以控制请求的转发逻辑，支持复杂的跨域需求。
- **缺点**：需要额外的服务器配置和维护。

### 1.3.4 Nginx 反向代理

类似于代理服务器，可以通过 Nginx 进行反向代理，配置 Nginx 来处理跨域请求。

- **步骤**：

  1. Nginx 接收来自前端的请求，将其转发到后端 API 服务器。
  2. 前端请求的 URL 与 Nginx 同源，避免浏览器的跨域限制。

- **配置示例**：

  ```nginx
  server {
      location /api/ {
          proxy_pass http://backend.com;
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
      }
  }
  ```

### 1.3.5 总结

- **解决跨域问题的主要方式有以下几种：**
  - **使用 CORS**：这是最标准、推荐的方法，适用于大多数场景。
  - **JSONP**：仅用于 `GET` 请求，较为简单，但存在安全隐患。
  - **代理服务器**：通过前端与代理服务器的交互避免直接跨域。
  - **Nginx 反向代理**：通过服务器配置代理请求，也是一种有效的解决方法。

# 2.项目中如何用代码解决？

- **CORS 是目前最常用和最灵活的解决跨域问题的方案，通过正确配置服务器响应头，可以确保跨域请求的安全性与有效性，**所以毛毛张在这里也只介绍这种解决方案

## 2.1 常见报错

- 毛毛张最近在做一个基于`SpringBoot + vue`的《音乐播放管理系统》，是一个练手的项目，初始化项目在做登录功能的时候，在前端网页按F12打开网页的控制台发现如下报错内容：

  ```js
  Access to XMLHttpRequest at ‘http://localhost:8080/xxx’ from origin ‘http://localhost:63342’ has been blocked by CORS policy: No ‘Access-Control-Allow-Origin’ header is present on the requested resource.
  ```

- **浏览器控制台输出**：

![image-20241020171403788](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/javaweb/image-20241020171403788.png)

## 2.2 报错原因

- **说明Origin指定的域名不在许可范围内，需要在后端添加允许跨域访问的配置，也就是在后端响应没有带上`access-control-allow-origin`和`access-control-allow-methods`的标头**

## 2.3 解决方案

### 2.3.1 如果使用了SpringBoot框架

- **方式1：添加配置文件（推荐），全局配置**

  ```java
  @Configuration
  public class CORSConfig implements WebMvcConfigurer {
      public void addCorsMappings(CorsRegistry registry) {
          // 设置允许跨域的路径
          registry.addMapping("/**")
                  // 设置允许跨域请求的域名
                  .allowedOrigins("*")
                  .allowedMethods("*")
                  // 是否允许cookie
                  .allowCredentials(true)
                  // 设置允许的请求方式
                  .allowedMethods("GET", "POST", "DELETE", "PUT")
                  // 设置允许的header属性
                  .allowedHeaders("*")
                  // 跨域允许时间
                  .maxAge(3600);
      }
  }
  ```

- 方式2：在控制层添加注解，这种是局部配置

  ```java
  @CrossOrigin(origins = "*",allowCredentials="true",allowedHeaders = "*",methods = {POST,GET})
  ```

### 2.3.2 如果没有使用框架

- 添加跨域过滤器CrosFilter

  ```java
  package com.atguigu.schedule.filter;
  
  import com.atguigu.schedule.common.Result;
  import com.atguigu.schedule.util.WebUtil;
  import jakarta.servlet.*;
  import jakarta.servlet.annotation.WebFilter;
  import jakarta.servlet.annotation.WebServlet;
  import jakarta.servlet.http.HttpServletRequest;
  import jakarta.servlet.http.HttpServletResponse;
  
  import java.io.IOException;
  
  @WebFilter("/*")
  public class CrosFilter implements Filter {
      @Override
      public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
  
          HttpServletRequest request = (HttpServletRequest) servletRequest;
          System.out.println(request.getMethod());
          HttpServletResponse response = (HttpServletResponse) servletResponse;
          response.setHeader("Access-Control-Allow-Origin", "*");
          response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT,OPTIONS, DELETE, HEAD");
          response.setHeader("Access-Control-Max-Age", "3600");
          response.setHeader("Access-Control-Allow-Headers", "access-control-allow-origin, authority, content-type, version-info, X-Requested-With");
          // 如果是跨域预检请求,则直接在此响应200业务码
          if(request.getMethod().equalsIgnoreCase("OPTIONS")){
              WebUtil.writeJson(response, Result.ok(null));
          }else{
              // 非预检请求,放行即可
              filterChain.doFilter(servletRequest, servletResponse);
          }
      }
  }
  
  ```

- **相关参数解释：**
  - **Access-Control-Allow-Origin**
    - **必填**。该字段的值可以是请求时 `Origin` 字段的具体值，或是 `*`，表示接受任意域名的请求。
  - **Access-Control-Allow-Methods**
    - **必填**。该字段的值是逗号分隔的具体字符串或 `*`，表明服务器支持的所有跨域请求的方法。注意，返回的是所有支持的方法，而不仅仅是浏览器请求的那个方法，以避免多次 "预检" 请求。
  - **Access-Control-Expose-Headers**
    - **可选**。CORS 请求时，`XMLHttpRequest` 对象的 `getResponseHeader()` 方法只能获取六个基本字段：`Cache-Control`、`Content-Language`、`Content-Type`、`Expires`、`Last-Modified`、`Pragma`。如果想要获取其他字段，必须在 `Access-Control-Expose-Headers` 中进行指定。
  - **Access-Control-Allow-Credentials**
    - **可选**。该字段的值是一个布尔值，表示是否允许发送 Cookie。默认情况下，不发送 Cookie，即 `false`。对服务器有特殊要求的请求（例如请求方法为 `PUT` 或 `DELETE`，或 `Content-Type` 字段类型为 `application/json`）时，该值只能设为 `true`。如果服务器不希望浏览器发送 Cookie，则可以删除该字段。
  - **Access-Control-Max-Age**
    - **可选**。该字段用来指定本次预检请求的有效期，单位为秒。在有效期间，不需要发出另一条预检请求。

# 参考文献

- <https://blog.csdn.net/weixin_44299027/article/details/126074192>
- <https://blog.csdn.net/qq_37896194/article/details/102833430>
- <https://blog.csdn.net/qq_39825705/article/details/125038907>