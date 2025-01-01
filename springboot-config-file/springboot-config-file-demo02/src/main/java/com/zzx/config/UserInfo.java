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
