package com.zzx.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "github")
public class GitHubOAuthInfo {

    private String clientId;

    private String clientSecret;

    private String directUrl;
}
