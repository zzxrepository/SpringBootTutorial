package com.zzx;

import com.zzx.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ImportResource("application-context.xml")
public class SpringbootConfigFileDemoApplication {

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private UserConfig userConfig;

    @Autowired
    private WebsiteConfig websiteConfig;


    @Autowired
    private User user;

    @Autowired
    private Person person;

    public static void main(String[] args) {
        // 通过 SpringApplication 运行应用程序
        SpringApplication.run(SpringbootConfigFileDemoApplication.class, args);
    }

    // 使用 @PostConstruct 或者在 Spring Boot 启动完成后进行打印
    @PostConstruct
    public void printServerConfig() {
        // 在 Spring 容器启动后打印 ServerConfig 内容
        System.out.println("配置文件中的值为：");
        System.out.println(websiteConfig);
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

    @PostConstruct
    public void printUserConfig() {
        System.out.println("配置文件中的值为：");
        System.out.println(user);
    }

    @PostConstruct
    public void displayPerson() {
        // 打印 Person Bean
        System.out.println("Person Details: " + person);
    }
}
