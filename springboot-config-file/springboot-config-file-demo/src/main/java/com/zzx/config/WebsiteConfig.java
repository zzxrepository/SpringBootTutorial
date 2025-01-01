package com.zzx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@PropertySource("website.properties")
@ConfigurationProperties(prefix = "website")
public class WebsiteConfig {
    private String name;
    private String description;
    private String url;
    private String logoUrl;
    private String contactEmail;
    private String footerText;
    private String theme;
    private String icp;
    
    private List<String> socialMediaLinks;   // 社交媒体链接
    private Map<String, Object> websiteStatus; // 网站状态信息
    private Map<String, String> languages;   // 多语言支持
}