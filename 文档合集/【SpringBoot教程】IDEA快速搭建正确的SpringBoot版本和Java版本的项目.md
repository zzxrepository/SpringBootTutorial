> 今天毛毛张分享的是在做SpringBoot项目中遇到的三个问题，这三个问题看似都是小问题，但是却是做项目的基础常识

# 1.问题

**问题1：**

- 我们在通过Maven构建Java项目或者使用源代码进行Java编译时，常常遇到JDK版本和SpringBoot版本不匹配的问题，导致编译失败，比如出现如下错误：

```cmd
org/springframework/beans/factory/InitializingBean.class
[ERROR]     类文件具有错误的版本 61.0, 应为 55.0
[ERROR]     请删除该文件或确保该文件位于正确的类路径子目录中。
/org/springframework/boot/autoconfigure/AutoConfigureAfter.class
[ERROR]     类文件具有错误的版本 61.0, 应为 55.0
[ERROR]     请删除该文件或确保该文件位于正确的类路径子目录中。
```

- 这类错误的原因是：你本地的JDK版本太低，而项目里面使用的SpringBoot版本太高了，解决这个问题要么提升JDK版本，要么降低SpringBoot版本。但是如何查看SpringBoot版本和JDK版本的对应的关系呢？

**问题2：** 

- 不同的项目可能需要不同的SpringBoot版本，不同的SpringBoot版本可能需要不同的Java版本，当我们系统里面安装了多个Java版本的时候，如何快速切换呢？

**问题3：** 

- `SpringBoot 2.X`版本在2023年11月24日停止维护了，因此创建`SpringBoot`项目时不再有`2.X`版本的选项，只能从`3.1.X`版本开始选择。而`SpringBoot 3.X`版本不支持JDK8，最低支持JDK17，因此JDK8也无法主动选择了。那么怎么解决这个问题呢？

![image-20241229180024491](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229180024491.png)



# 2.使用IDEA快速搭建正版SpringBoot版本和JDK版本的项目

## 2.1 查看SpringBoot依赖的JDK版本

- 首先打开Spring官方网站：https://spring.io/，点击菜单栏`Projects`下的`Spring Boot`，然后点击`LEARN`页签，查看主流的SpringBoot版本

![image-20241229160538633](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229160538633.png)

> **官网上不同版本对应的不同的标识，不同版本标识的意义**：
>
> - **CURRENT**：代表了当前版本，最新发布版本，里程碑版本。
>
> - **GA**：通用正式发布版本，同release。
>
> - **SNAPSHOT**：快照版本，可用但非稳定版本。
>
> - **PRE**：预览版本。
>
> - **M版本：**M1,M2,M3中的M是milestone的简写，这个单词是里程碑的意思。
>
> - **Alpha**：也被称为内部测试版或预览版，这些版本通常不会对外部用户公开，因为它们可能包含许多尚未修复的漏洞和不完整的功能。通常只有开发团队和其他内部相关人士才能访问和使用 Alpha 版本。
>
> - **Beta**：是一种公开测试版，位于 Alpha 版本之后。这个阶段的版本通常会加入新功能，并且相较于 Alpha 版本来说会更加稳定。Beta 版本主要面向特定的用户群体进行测试，如合作伙伴、潜在客户或早期采用者。

- 然后查看`Reference Doc.`，比如我们想查看`SpringBoot 3.0.13`版本，点击`Reference Doc.`进入参考详情页面。

![image-20241229160910361](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229160910361.png)

- 接着点击新页面左侧的[Getting Started](https://docs.spring.io/spring-boot/docs/3.0.13/reference/html/getting-started.html" /l "getting-started)菜单

![image-20241229161057158](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229161057158.png)

- 接着点击左侧的`2. System Requirements`菜单，就可以看到SpringBoot版本对应的Java JDK版本：**Spring Boot 3.0.13 requires [Java 17](https://www.java.com/) and is compatible up to and including Java 21. [Spring Framework 6.0.14](https://docs.spring.io/spring-framework/reference/6.0/) or above is also required.（Spring Boot 3.0.13 要求使用 [Java 17](https://www.java.com/)，并且兼容至包括 Java 21 在内的版本。同时，也需要使用 [Spring Framework 6.0.14](https://docs.spring.io/spring-framework/reference/6.0/) 或更高版本。）**

![4-查看springboot3依赖jdk版本](https://img2024.cnblogs.com/blog/2435483/202407/2435483-20240711195157351-1558126405.png)

## 2.2 主流的SpringBoot和JDK版本的对应关系

| Spring Boot 版本                      | Jdk版本                | Spring Framework 版本    |
| ------------------------------------- | ---------------------- | ------------------------ |
| Spring Boot 1.3.x - Spring Boot 1.5.x | Java JDK 7 及以上版本  |                          |
| Spring Boot 2.0.x - Spring Boot 2.3.x | Java JDK 8 及以上版本  |                          |
| Spring Boot 2.4.x                     | Java JDK 11 及以上版本 |                          |
| Spring Boot 2.5.x                     | Java JDK 16 及以上版本 |                          |
| Spring Boot 2.7.18                    | Java JDK 8版本—21版本  | Spring Framework 5.3.31+ |
| Spring Boot 3.0.13                    | Java JDK 17版本-21版本 | Spring Framework 6.0.14+ |
| Spring Boot 3.1.8                     | Java JDK 17版本-21版本 | Spring Framework 6.0.16+ |
| Spring Boot 3.2.2                     | Java JDK 17版本-21版本 | Spring Framework 6.0.13+ |
| Spring Boot 3.3.0-M1                  | Java JDK 17版本-21版本 | Spring Framework 6.0.13+ |

## 2.3 SpringBoot 2和SpringBoot 3有什么区别

- 最低环境的区别
  - **Java版本**：SpringBoot2的最低版本要求为Java8，支持Java9；而SpringBoot3决定使用Java17作为最低版本，并支持Java19。
  - **Spring Framework 版本**： SpringBoot2基于Spring Framework5开发；而SpringBoot3构建基于Spring Framework6之上。

- **GraalVM支持的区别** ：相比SpringBoot2，SpringBoot3的Spring Native也是升级的一个重大特性，支持使用GraalVM将Spring的应用程序编译成本地可执行的镜像文件，可以显著提升启动速度、峰值性能以及减少内存使用。

- **片Banner支持的区别**：在SpringBoot2中，自定义Banner支持图片类型；而现在Spring Boot3自定义Banner只支持文本类型（banner.txt），不再支持图片类型。

- **依赖项的区别**：相比SpringBoot2，Spring Boot3.0.0-M1删除了对一些附加依赖项的支持，包括Apache ActiveMQ、Atomikos、EhCache2和HazelCast3。Jersey是另一个值得注意的弃用，在它提供对Spring Framework6的支持之前已被删除。
- 除了上述内容外，相比SpringBoot2，SpringBoot3还增加了很多其它的新特性，如：Java EE已经变更为Jakarta EE、Log4j2增强、三方包升级等。

# 3.快速切换JDK版本

- 看了上面我们发现如果使用较低的SpringBoot版本的时候，我们需要将系统JDK版本切换到JDK8，当我们系统中存在多个版本的时候如何快速切换呢？
- 毛毛张在这里就不讲述JDK的安装教程了，如果还熟悉JDK的安装教程了可以参看毛毛张的这个教程：[Java学习笔记 | JavaSE基础语法 | 0 | Java入门概述](https://blog.csdn.net/weixin_48235955/article/details/136032980)
- 下面毛毛张将介绍Windows系统下安装和随意切换多个版本的JDK

## 3.1 配置不同的JAVA_HOME

- 大部分情况下，可以通过以下两个环境变量来确定当前应用程序使用的是哪个 Java 版本：
  - **JAVA_HOME**：多数脚本使用该环境变量来确定 Java 版本的位置。
  - **Path**：当从控制台运行 Java 二进制文件（如 java 和 javac）时，使用的是该环境变量

- 在只有一个 JDK 的情况下，Windows 只需要在环境变量中配置 `JAVA_HOME`，并且在 `PATH` 中配置 `bin` 的位置，这样就可以在任意目录下使用 `java` 命令了。只需配置 `bin` 目录即可，不必配置 `jre` 目录。
- 当我们安装了多个JDK的时候，这里假设电脑上下载了 JDK 8 和 JDK 17 两个版本，他们的安装路径分别为 `D:\software\IDE\Java\JDK\JDK1_8` 和 `D:\software\IDE\Java\JDK\JDK17`，这是毛毛张的安装配置，如下图：

![image-20241229172940359](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229172940359.png)

- 操作步骤：

  - 增加一个名字叫 `JAVA_HOME8` 的环境变量，使其指向目录 `D:\software\IDE\Java\JDK\JDK1_8`
  - 增加一个名字叫 `JAVA_HOME17` 的环境变量，使其指向目录 `D:\software\IDE\Java\JDK\JDK17`
  - 修改原来的 `JAVA_HOME`（没有的话就新增一个），使其值指向你想使用的 JDK 版本，比如我想使用 JDK 17，那就设置其值为 `%JAVA_HOME17%`，这样就不需要动之前`Path`环境变量中配置好的`%JAVA_HOME%\bin`

  ![image-20241229173211916](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229173211916.png)

![image-20241229173420674](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229173420674.png)

- 最后保存环境变量配置，然后打开一个新的命令行窗口输入 `java --version` 即可看到当前使用的 java 版本了
- 每次当我们切换JDK版本的时候，只需要修改`JAVA_HOME`的值就行，如果需要使用`JDK8`就将`JAVa_HOME`替换成`%JAVA_HOME8%`

![image-20241229173533765](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229173533765.png)

## 3.2 安装两个版本的Java显示Java版本总是高的那个版本

- 这个问题困惑了毛毛张许久，原因在于安装 JDK 时使用了 `.exe` 安装包版本。在安装过程中，Windows 系统会自动将最后安装的 JDK 设置为系统默认的版本，因此每次切换 JDK 时，系统仍然会显示安装的最高的那个版本。
- 当尝试配置低版本 JDK 时，使用 `java --version` 命令时，可能会看到依然是高版本的 JDK，而低版本 JDK 没有生效。这是因为 `java --version` 命令会根据环境变量 `PATH` 来查找 Java 可执行文件，而默认路径中的 `java` 可能指向了安装的最高版本。

- 但是不必担心，只要修改了正确的环境变量并在 IDEA 中选择了相应的 JDK 版本，就不会出现版本不一致的错误了。这个问题可以通过以下步骤解决：
  - 修改环境变量 `JAVA_HOME` 和 `PATH`，确保指向你想要使用的低版本 JDK 路径。
  - 在 IDE（如 IntelliJ IDEA）中，手动选择对应的 JDK 版本。IDEA 会读取你配置的环境变量，并使用你选择的 JDK 版本。

- 由于不影响使用，所以毛毛张就没用去解决了，如果你们安装的也是`exe`版本的，想解决这个问题，解决方案可以参看下面在两篇文章：
  - [安装两个版本的Java显示Java版本总是高的那个版本 安装多个版本的jdk](https://blog.51cto.com/u_16099170/7627500)
  - https://blog.csdn.net/weixin_39068791/article/details/108140194

# 4.使用IDEA快速创建SpringBoot 2.x版本的项目

## 4.1 使用阿里云的国内源创建SpringBoot2.x版本的项目

- 在使用IDAE的快速创建工具`SpringBoot`的时候，把创建项目页面上面的`Server URL`修改成`https://start.aliyun.com`，原始的是`https://start.spring.io`

![image-20241229180212974](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229180212974.png)

- 然后就可以愉快的创建项目了，需要注意的是，通过阿里云创建的项目，初始结构与通过Spring官方创建的项目有所不同，但完全不影响使用



## 4.2 拥抱变化，手动修改成SpringBoot 2.x 和JDK8

- 首先修改`pom.xml`文件

![image-20241229180716904](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229180716904.png)

- 修改`Project Struture`，此处也修改成`JDK8`

![image-20241229180830462](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229180830462.png)

![image-20241229180840421](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229180840421.png)

- `Modules`这里也修改成JDK8

![image-20241229181813263](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229181813263.png)

- 这里也需要修改：

![image-20241229180954375](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229180954375.png)-

- 都修改完毕后记得点击Apply，再点击OK
- 修改`Settings`

![image-20241229181040095](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229181040095.png)

![image-20241229181050144](https://cdn.jsdelivr.net/gh/zzxrepository/image_bed@master/springboot/image-20241229181050144.png)

# 参考文献

- <https://blog.csdn.net/nadeal/article/details/136504100#:~:text=Spring%20Boot%E5%90%84%E7%89%88%E6%9C%AC%E5%AF%B9%E5%BA%94,JDK%2011%20%E5%8F%8A%E4%BB%A5%E4%B8%8A%E7%89%88%E6%9C%AC%E3%80%82>
- <https://www.cnblogs.com/hibpm/p/18297051>
- <https://www.hsuyeung.com/article/how-to-install-multiple-jdk-and-switch-version>
- <https://blog.csdn.net/dream_ready/article/details/134639886>