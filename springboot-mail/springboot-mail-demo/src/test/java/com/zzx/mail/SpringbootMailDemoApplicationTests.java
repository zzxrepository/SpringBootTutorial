package com.zzx.mail;

import com.zzx.mail.service.MailPushService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SpringbootMailDemoApplicationTests {

    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void sendSimpleMail() throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        // 配置发送者邮箱
        message.setFrom("1849975198@qq.com");
        // 配置接受者邮箱
        message.setTo("1849975198@qq.com");
        // 配置邮件主题
        message.setSubject("主题：简单邮件");
        // 配置邮件内容
        message.setText("测试邮件内容");
        // 发送邮件
        mailSender.send(message);
    }


    @Autowired
    private MailPushService mailPushService;

    @Test
    public void testSendHtmlMail() throws Exception {
        String sendHtml = buildHtmlContent("张三");
        mailPushService.sendMail("1849975198@qq.com","简单标题", sendHtml);
    }

    /**
     * 封装html页面
     * @return
     * @throws Exception
     */
    private static String buildHtmlContent(String userName) throws Exception {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setDefaultEncoding(Charset.forName("UTF-8").name());
        configuration.setClassForTemplateLoading(SpringbootMailDemoApplicationTests.class, "/templates");
        // 获取页面模版
        Template template = configuration.getTemplate("demo.ftl");
        // 动态变量替换
        Map<String,Object> map = new HashMap<>();
        map.put("userName", userName);
        String htmlStr = FreeMarkerTemplateUtils.processTemplateIntoString(template,map);
        return htmlStr;
    }

    @Test
    public void doSendHtmlEmail() throws Exception {
        // 获取正文内容
        String sendHtml = buildHtmlContent("张三");

        // 获取附件
        File file = new File( "d:/Downloads/2202.01771v4.pdf");
        // 发送邮件
        mailPushService.sendMail("1849975198@qq.com","带附件的邮件推送", sendHtml, file);
    }


}
