









在分布式系统中，消息队列是一种重要的通信方式，它能够有效地将消息从一个应用程序传递到另一个应用程序。[RabbitMQ](https://so.csdn.net/so/search?q=RabbitMQ&spm=1001.2101.3001.7020)是一款流行的开源消息队列系统，简单易用且功能强大。本文将介绍如何使用SpringBoot快速整合RabbitMQ，实现消息的发送和接收。

![img](https://i-blog.csdnimg.cn/blog_migrate/a1887a16cb24bba4af0e3f8fd2e5e1f0.png) 

**交换机**： **主要负责接收生产者发送的消息，并根据特定的规则将这些消息路由到一个或多个队列中**。[交换机](https://so.csdn.net/so/search?q=交换机&spm=1001.2101.3001.7020)的类型有：

-  **Fanout Exchange（扇出交换机）**

​    Fanout交换机会将接收到的所有消息广播到它知道的所有队列中。这种类型的交换机不考虑路由键，只是简单地将消息复制到所有绑定的队列中。适用于不需要选择性地发送消息给特定队列的情况，例如，广播系统通知或有多个服务需要消费同一份数据的场景。

扇型交换机，这个交换机没有路由键概念，就算你绑了路由键也是无视的。 这个交换机在接收到消息后，会直接转发到绑定到它上面的所有队列。

- **Direct Exchange（直连交换机）**

  直连型交换机，根据消息携带的路由键将消息投递给对应队列。

  大致流程，有一个队列绑定到一个直连交换机上，同时赋予一个路由键 routing key 。
  然后当一个消息携带着路由值为X，这个消息通过生产者发送给交换机时，交换机就会根据这个路由值X去寻找绑定值也是X的队列。

​    Direct交换机根据消息的路由键将消息发送到与之匹配的队列中。只有当路由键与绑定关键字完全匹配时，消息才会被路由到相应的队列。适合于精确控制消息投递的场景，如特定的服务或功能模块只关心特定类型的消息。



- **Topic Exchange（主题交换机）**

​    Topic交换机允许更复杂的匹配规则，通过模式匹配的方式将消息路由到一个或多个队列。路由键和绑定键都使用点分隔的字符串，可以包含特殊字符如“#”和“*”来实现模糊匹配。*"\*"*用于匹配一个单词，而“#”则用于匹配零个或多个单词。适合于需要按内容分类消息的系统，如日志处理系统，可以根据日志等级或来源将日志消息分发到不同的队列。

主题交换机，这个交换机其实跟直连交换机流程差不多，但是它的特点就是在它的路由键和绑定键之间是有规则的。
简单地介绍下规则：

\* **(星号) 用来表示一个单词 (必须出现的)**
\# **(井号) 用来表示任意数量（零个或多个）单词**
通配的绑定键是跟队列进行绑定的，举个小例子
队列Q1 绑定键为 *.TT.*     队列Q2绑定键为 TT.#
如果一条消息携带的路由键为 A.TT.B，那么队列Q1将会收到；
如果一条消息携带的路由键为TT.AA.BB，那么队列Q2将会收到；

**主题交换机是非常强大的，为啥这么膨胀？**
当一个队列的绑定键为 "#"（井号） 的时候，这个队列将会无视消息的路由键，接收所有的消息。
当 * (星号) 和 # (井号) 这两个特殊字符都未在绑定键中出现的时候，此时主题交换机就拥有的直连交换机的行为。
所以主题交换机也就实现了扇形交换机的功能，和直连交换机的功能。

- **Headers Exchange（头交换机）**

​    Headers交换机使用消息头的一组键值对来决定消息应该被路由到哪个队列。这种交换机允许更细粒度的路由控制，但配置和使用较为复杂。适合需要基于消息多个属性来动态决定路由的场景，例如某些高级的路由策略或复杂的事件处理系统。

Default Exchange 默认交换机，Dead Letter Exchange 死信交换机

**队列：主要用于存储消息，实现先进先出（FIFO）的特性。**

#### **一、引入依赖**

这里引入了两个依赖。一个是rabbitmq的依赖，另一个是配置json[转换器](https://so.csdn.net/so/search?q=转换器&spm=1001.2101.3001.7020)所需要的依赖。生产者和消费者服务都需要引入这两个依赖。

> <dependency>
>    <groupId>org.springframework.boot</groupId>
>    <artifactId>spring-boot-starter-amqp</artifactId>
> </dependency>
> <dependency>
>    <groupId>com.fasterxml.jackson.dataformat</groupId>
>     <artifactId>jackson-dataformat-xml</artifactId>
>  </dependency>

#### 二、配置rabbitmq的连接信息等

##### 1、生产者配置

>  rabbitmq:
>   host: 170.40.20.16
>   port: 5672
>   username: zhuoye
>   password: zy521
>   virtual-host: /

##### 2、消费者配置 

>   rabbitmq:
>   host: 170.40.20.16
>  port: 5672
>   username: zhuoye
>   password: zy521
>   virtual-host: /
>   listener:
>    simple:
>     prefetch: 1 #每次只能处理一个，处理完成才能获取下一个消息

#### 三、设置消息转换器

​    默认情况下Spring采用的序列化方式是JDK序列化，而JDK的序列化存在可读性性差、占用内存大、存在安全漏洞等问题。所以，这里我们一般使用Jackson的序列化代替JDk的序列化。

在生产者和消费者的启动类上加上如下代码：  

```java

```

#### 四、生产者代码示例

#####  1、配置交换机和队列信息

```java

```

##### 2、生产消息代码

```java

```

#### 五、消费者代码示例

##### 1、消费层代码

```java

```

##### 2、业务层代码 

```java

```

**注：以上代码为对接告警信息和对接告警确认消息的示例。**



- https://www.cnblogs.com/qlsem/p/11538340.html

## 1.2. RabbitMQ 简介

### 1.2.1.基本特性

官网 https://www.rabbitmq.com/getstarted.html

高可靠：RabbitMQ 提供了多种多样的特性让你在可靠性和性能之间做出权衡，包括持久化、发送应答、发布确认以及高可用性。

灵活的路由：通过交换机（Exchange）实现消息的灵活路由。

支持多客户端：对主流开发语言（Python、Java、Ruby、PHP、C#、JavaScript、Go、Elixir、Objective-C、Swift 等）都有客户端实现。

集群与扩展性：多个节点组成一个逻辑的服务器，支持负载。

高可用队列：通过镜像队列实现队列中数据的复制。

权限管理：通过用户与虚拟机实现权限管理。

插件系统：支持各种丰富的插件扩展，同时也支持自定义插件。

与 Spring 集成：Spring 对 AMQP 进行了封装。

### 1.2.2.AMQP 协议

#### 1.2.2.1.总体介绍

http://www.amqp.org/sites/amqp.org/files/amqp.pdf

AMQP：高级消息队列协议，是一个工作于应用层的协议，最新的版本是 1.0 版本。

![img](https://img2018.cnblogs.com/blog/1725845/201909/1725845-20190919212155341-1628508872.png)

除了 RabbitMQ 之外，AMQP 的实现还有 OpenAMQ、Apache Qpid、Redhat、Enterprise MRG、AMQP Infrastructure、ØMQ、Zyre。

除了 AMQP 之外，RabbitMQ 支持多种协议，STOMP、MQTT、HTTP and WebSockets。

可以使用 WireShark 等工具对 RabbitMQ 通信的 AMQP 协议进行抓包。

下图是RabbitMQ的基本结构：
[![http://dtstack-static.oss-cn-hangzhou.aliyuncs.com/2021bbs/files_user1/article/7dcca2108da6208fdd0c625c5cb257d0..jpg](http://dtstack-static.oss-cn-hangzhou.aliyuncs.com/2021bbs/files_user1/article/7dcca2108da6208fdd0c625c5cb257d0..jpg)](http://dtstack-static.oss-cn-hangzhou.aliyuncs.com/2021bbs/files_user1/article/7dcca2108da6208fdd0c625c5cb257d0..jpg)
 
组成部分说明：
Broker：消息队列服务进程，此进程包括两个部分：Exchange和Queue
Exchange：消息队列交换机，按一定的规则将消息路由转发到某个队列，对消息进行过虑。
Queue：消息队列，存储消息的队列，消息到达队列并转发给指定的消费者
Producer：消息生产者，即生产方客户端，生产方客户端将消息发送
Consumer：消息消费者，即消费方客户端，接收MQ转发的消息。

生产者发送消息流程：
1、生产者和Broker建立TCP连接。
2、生产者和Broker建立通道。
3、生产者通过通道消息发送给Broker，由Exchange将消息进行转发。
4、Exchange将消息转发到指定的Queue（队列）

消费者接收消息流程：
1、消费者和Broker建立TCP连接
2、消费者和Broker建立通道
3、消费者监听指定的Queue（队列）
4、当有消息到达Queue时Broker默认将消息推送给消费者。
5、消费者接收到消息。
6、ack回复







- https://blog.csdn.net/weixin_50348837/article/details/139276462









- https://blog.csdn.net/u011709538/article/details/131396367

- https://blog.csdn.net/zhuizhu761303826/article/details/132492950
- https://blog.csdn.net/weixin_45983377/article/details/125559797





```shell
docker run -d \
--name nacos \
-e JVM_XMS=256m -e JVM_XMX=256m \
--env-file ./nacos/custom.env \
-p 8848:8848 \
-p 9848:9848 \
-p 9849:9849 \
--restart=always \
--network=hm-net \
nacos/nacos-server
```

