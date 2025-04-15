# [SpringBoot+mail 轻松实现各类邮件自动推送](https://www.cnblogs.com/dxflqm/p/18279869)

### 一、简介

在实际的项目开发过程中，经常需要用到邮件通知功能。例如，通过邮箱注册，邮箱找回密码，邮箱推送报表等等，实际的应用场景非常的多。

早期的时候，为了能实现邮件的自动发送功能，通常会使用 JavaMail 相关的 api 来完成。后来 Spring 推出的 JavaMailSender 工具，进一步简化了邮件的自动发送过程，调用其 send 方法即可发送邮件。再之后， Spring Boot 针对邮件推送功能推出了`spring-boot-starter-mail`工具包，开发者可以通过它来快速实现邮件发送服务。

今天通过这篇文章，我们一起来学习如何在 Spring Boot 中快速实现一个自动发送邮件的功能。

### 二、环境准备

在介绍邮件推送实现之前，我们需要先准备一台邮件推送的服务器，以便实现相关功能。

这里以腾讯邮箱为例，将其作为邮件发送的中转平台。

#### 2.1、开启 SMTP 服务

登陆腾讯邮箱，打开【设置】-》【收发信设置】，开启 SMTP 服务，最后点击【保存更改】。

![img](https://img2024.cnblogs.com/blog/1078540/202407/1078540-20240702145553009-626699022.jpg)

#### 2.2、生成客户端专用密码

点击【设置】-》【账户】，进入页面后点击【开启安全登陆】，点击【生成新密码】。

![img](https://img2024.cnblogs.com/blog/1078540/202407/1078540-20240702145552763-1463515481.jpg)

![img](https://img2024.cnblogs.com/blog/1078540/202407/1078540-20240702145553058-556084887.jpg)

![img](https://img2024.cnblogs.com/blog/1078540/202407/1078540-20240702145552977-57868187.jpg)

这个新密码会用于邮箱的自动发送，因此需要记录下来，最后点击【保存更改】。

#### 2.3、相关扩展知识

- 什么是 SMTP？

SMTP（simple mail transfer protocol），也被称为**简单邮件传输协议**，主要用于发送电子邮件的，通过它可以实现邮件的发送或者中转。遵循 SMTP 协议的服务器，通常称为发送邮件服务器。

- 什么是 POP3？

POP3（Post Office Protocol），一种邮局通信协议。主要用于接受电子邮件的，POP3 允许用户从服务器上把邮件存储到自己的计算机上，同时删除保存在邮件服务器上的邮件。同理，遵循 POP3 协议的服务器，通常称为接收邮件服务器。

- 什么是 IMAP？

IMAP（Internet Mail Access Protocol），一种交互式邮件存取协议。与 POP3 协议类似，主要用于接收电子邮件，稍有不同的是：IMAP 允许电子邮件客户端收取的邮件仍然保留在服务器上，同时在客户端上的操作都会反馈到服务器上，例如删除邮件，标记已读等，服务器上的邮件也会做相应的动作。所以无论从浏览器登录邮箱或者客户端软件登录邮箱，看到的邮件以及状态都是一致的。

总结下来就是：SMTP 负责发送邮件，POP3/IMAP 负责接收邮件。

常见邮箱发、收服务器如下！

![img](https://img2024.cnblogs.com/blog/1078540/202407/1078540-20240702145552909-514713189.png)

### 三、邮件推送实现

用于发送邮件的服务器、账户和密码准备好了之后，就可以正式使用了。下面我们以 Spring Boot 的`2.1.0`版本为基础，实现过程如下。

#### 2.1、添加依赖包

在`pom.xml`文件中，添加`spring-boot-starter-mail`依赖包。

```xml
<!--mail 支持-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

#### 2.2、添加相关配置

在`application.properties`中添加邮箱相关配置。

```ini
# 配置邮件发送主机地址
spring.mail.host=smtp.exmail.qq.com
# 配置邮件发送服务端口号
spring.mail.port=465
# 配置邮件发送服务协议
spring.mail.protocol=smtp
# 配置邮件发送者用户名或者账户
spring.mail.username=xxx@qq.com
# 配置邮件发送者密码或者授权码
spring.mail.password=xxxxxxx
# 配置邮件默认编码
spring.mail.default-encoding=UTF-8
# 配置smtp相关属性
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.ssl.required=true
```

#### 2.3、简单发送一封邮件

通过单元测试来实现一封简单邮件的发送，示例如下：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailSimpleTest {

    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void sendSimpleMail() throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        // 配置发送者邮箱
        message.setFrom("xxxx@qq.com");
        // 配置接受者邮箱
        message.setTo("xxxxxx@qq.com");
        // 配置邮件主题
        message.setSubject("主题：简单邮件");
        // 配置邮件内容
        message.setText("测试邮件内容");
        // 发送邮件
        mailSender.send(message);
    }
}
```

运行单元测试之后，如果不出意外的话，接受者会收到这样的一封邮件。

![img](https://img2024.cnblogs.com/blog/1078540/202407/1078540-20240702145552898-760263951.jpg)

至此，邮件发送成功！

#### 2.4、发送 HTML 格式邮件

在实际的业务开发中，邮件的内容通常会要求丰富，比如会发送一些带有图片的内容，包括字体大小，各种超链接等，这个时候如何实现呢？

实际上，邮件内容支持 HTML 格式，因此可以借助页面模板引擎来实现绚丽多彩的内容。

下面我们以`freemarker`模板引擎为例，发送一封内容为 HTML 格式的邮件。

##### 2.4.1、引入 freemarker 依赖包

首先，在`pom.xml`文件中，添加`freemarker`依赖包。

```xml
<!--freemarker 支持-->
<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.23</version>
</dependency>
```

##### 2.4.2、编写邮件页面模板

然后，在`resources/templates`目录下，创建一个`demo.ftl`文件，示例如下！

```html
<html>
<head>
	<meta charset="utf-8">
	<title></title>
</head>
<body>
<div>您好：${userName}</div>
<div>这是html文本内容</div>
<img src="https://rescdn.qqmail.com/zh_CN/htmledition/images/logo/logo_0_0@2X1f1937.png" />
</body>
</html>
```

##### 2.4.3、编写一个邮件推送服务

虽然采用 Spring Boot 提供的自动配置属性来实现邮件推送，可以极大的简化开发过程。而实际开发的时候，通常更推荐自定义一个邮件统一推送服务，这样更便于灵活的控制代码实现以及排查相关问题。

邮件统一发送服务，示范如下。

```java
@Component
public class MailPushService {

    private final Logger LOGGER = LoggerFactory.getLogger(MailPushService.class);

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private String port;

    @Value("${mail.protocol}")
    private String protocol;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.fromEmail}")
    private String fromEmail;

    @Value("${mail.fromPersonal}")
    private String fromPersonal;

    @Autowired
    private JavaMailSender mailSender;


    /**
     * 发送邮件（简单模式）
     * @param toEmail
     * @param subject
     * @param content
     */
    public void sendMail(String toEmail, String subject,String content)  {
        try {
            final Properties props = new Properties();
            //服务器
            props.put("mail.smtp.host", host);
            //端口
            props.put("mail.smtp.port", port);
            //协议
            props.setProperty("mail.transport.protocol", protocol);
            //用户名
            props.put("mail.user", username);
            //密码
            props.put("mail.password", password);
            //使用smtp身份验证
            props.put("mail.smtp.auth", "true");

            //开启安全协议
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.socketFactory", sf);
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(props.getProperty("mail.user"),
                            props.getProperty("mail.password"));
                }
            };

            Session session = Session.getDefaultInstance(props, authenticator);
            session.setDebug(true);
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(fromEmail, MimeUtility.encodeText(fromPersonal)));
            mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toEmail));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(content, "text/html;charset=UTF-8");

            //保存信息
            mimeMessage.saveChanges();
            //发送消息
            Transport.send(mimeMessage);
            LOGGER.info("简单邮件已经发送。");
        } catch (Exception e) {
            LOGGER.error("发送简单邮件时发生异常！", e);
        }
    }
}
```

代码中相关自定义的全局参数配置如下：

```ini
mail.host=smtp.exmail.qq.com
mail.port=465
mail.protocol=smtp
mail.username=xxx@qq.com
mail.password=xxxxxx
mail.fromEmail=xxxxxx@qq.com
mail.fromPersonal=发送者昵称
```

##### 2.4.4、测试服务的正确性

最后，编写一个单元测试来验证服务的正确性，示例如下：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {

    @Autowired
    private MailPushService mailPushService;

    @Test
    public void testSendHtmlMail() throws Exception {
        String sendHtml = buildHtmlContent("张三");
        mailPushService.sendMail("xxxxx@qq.com","简单标题", sendHtml);
    }

    /**
     * 封装html页面
     * @return
     * @throws Exception
     */
    private static String buildHtmlContent(String userName) throws Exception {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setDefaultEncoding(Charset.forName("UTF-8").name());
        configuration.setClassForTemplateLoading(MailTest.class, "/templates");
        // 获取页面模版
        Template template = configuration.getTemplate("demo.ftl");
        // 动态变量替换
        Map<String,Object> map = new HashMap<>();
        map.put("userName", userName);
        String htmlStr = FreeMarkerTemplateUtils.processTemplateIntoString(template,map);
        return htmlStr;
    }

}
```

运行单元测试之后，如果没有报错，接受者会收到这样的一封邮件。

![img](https://img2024.cnblogs.com/blog/1078540/202407/1078540-20240702145553237-593638969.jpg)

#### 2.5、发送带附件的邮件

某些业务场景，用户希望发送的邮件中能带上附件，比如上文中，在发送 HTML 格式的邮件时，同时也带上文件附件，这个时候如何实现呢？

##### 2.5.1、编写带附件的邮件发送

此时可以在邮件推送服务中，新增一个支持带附件的方法，实现逻辑如下。

```java
/**
 * 发送邮件（复杂模式）
 * @param toEmail    接受者邮箱
 * @param subject    主题
 * @param sendHtml   内容
 * @param attachment 附件
 */
public void sendMail(String toEmail, String subject, String sendHtml, File attachment) {
    try {
        //设置了附件名过长问题
        System.setProperty("mail.mime.splitlongparameters", "false");
        final Properties props = new Properties();
        //服务器
        props.put("mail.smtp.host", host);
        //端口
        props.put("mail.smtp.port", port);
        //协议
        props.setProperty("mail.transport.protocol", protocol);
        //用户名
        props.put("mail.user", username);
        //密码
        props.put("mail.password", password);
        //使用smtp身份验证
        props.put("mail.smtp.auth", "true");

        //开启安全协议
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(props.getProperty("mail.user"),
                        props.getProperty("mail.password"));
            }
        };

        Session session = Session.getDefaultInstance(props, authenticator);
        session.setDebug(true);
        MimeMessage mimeMessage = new MimeMessage(session);
        // 发送者邮箱
        mimeMessage.setFrom(new InternetAddress(fromEmail, MimeUtility.encodeText(fromPersonal)));
        // 接受者邮箱
        mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toEmail));
        // 邮件主题
        mimeMessage.setSubject(subject);
        // 定义邮件内容
        Multipart multipart = new MimeMultipart();

        // 添加邮件正文
        BodyPart contentPart = new MimeBodyPart();
        contentPart.setContent(sendHtml, "text/html;charset=UTF-8");
        multipart.addBodyPart(contentPart);

        // 添加附件
        if (attachment != null) {
            BodyPart attachmentBodyPart = new MimeBodyPart();
            // MimeUtility.encodeWord可以避免文件名乱码
            FileDataSource fds=new FileDataSource(attachment);
            attachmentBodyPart.setDataHandler(new DataHandler(fds));
            attachmentBodyPart.setFileName(MimeUtility.encodeText(fds.getName()));
            multipart.addBodyPart(attachmentBodyPart);
        }

        // 将multipart对象放到message中
        mimeMessage.setContent(multipart);

        //保存信息
        mimeMessage.saveChanges();
        //发送消息
        Transport.send(mimeMessage);
        LOGGER.info("邮件已经发送。");
    } catch (Exception e) {
        LOGGER.error("发送邮件时发生异常！", e);
    }
}
```

##### 2.5.2、测试服务的正确性

最后，编写一个单元测试来验证服务的正确性，示例如下：

```java
@Test
public void doSendHtmlEmail() throws Exception {
    // 获取正文内容
    String sendHtml = buildHtmlContent("张三");

    // 获取附件
    File file = new File( "~/doc/Java开发手册.pdf");
    // 发送邮件
    mailPushService.sendMail("xxxxx@qq.com","带附件的邮件推送", sendHtml, file);
}
```

运行单元测试之后，如果没有报错，接受者会收到这样的一封邮件。

![img](https://img2024.cnblogs.com/blog/1078540/202407/1078540-20240702145553220-195253765.jpg)

### 三、小结

最后总结一下，邮件自动推送功能在实际的业务系统中应用非常广，在发送过程中也可能会因为网络问题出现各种失败现象，因此推荐采用异步的方式来发送邮件，例如采用异步编程或者消息队列来实现，以便加快主流程的执行速度。

想要获取项目源代码的小伙伴，可以访问如下地址获取！

> https://gitee.com/pzblogs/spring-boot-example-demo



### 发送失败

因为各种原因，总会有邮件发送失败的情况，比如：邮件发送过于频繁、网络异常等。在出现这种情况的时候，我们一般会考虑重新重试发送邮件，会分为以下几个步骤来实现：

- 1、接收到发送邮件请求，首先记录请求并且入库。
- 2、调用邮件发送接口发送邮件，并且将发送结果记录入库。
- 3、启动定时系统扫描时间段内，未发送成功并且重试次数小于3次的邮件，进行再次发送

### 异步发送

很多时候邮件发送并不是我们主业务必须关注的结果，比如通知类、提醒类的业务可以允许延时或者失败。这个时候可以采用异步的方式来发送邮件，加快主交易执行速度，在实际项目中可以采用MQ发送邮件相关参数，监听到消息队列之后启动发送邮件。

可以参考前期文章：[Spring Boot(八)：RabbitMQ 详解](http://www.ityouknow.com/springboot/2016/11/30/spring-boot-rabbitMQ.html) 来实现。

> 文章内容已经升级到 Spring Boot 2.x



```
curl -X POST -F "files=d:/Downloads/2410.07166v3.pdf" -F "files=d:/Downloads/Hot100.pdf" "http://localhost:8080/api/mail/html-with-attachment?to=1849975198@qq.com&userName=毛毛张"
```

# 参考文献

- <https://www.cnblogs.com/dxflqm/p/18279869>
- <https://blog.csdn.net/qq_26383975/article/details/121957917>
- <http://www.ityouknow.com/springboot/2017/05/06/spring-boot-mail.html>
- <https://cloud.tencent.com/developer/article/2374527>
- 重要：<https://dunwu.github.io/spring-tutorial/pages/2586f1/#java-%E4%BB%A3%E7%A0%81>