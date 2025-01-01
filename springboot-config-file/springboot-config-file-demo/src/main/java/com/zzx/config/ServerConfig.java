package com.zzx.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data  		// 毛毛张在这里使用了Lombok插件，免得去写构造方法和getter和setter方法了
@Component  // 或者@Configuration  需要将组件添加到SpringBoot中
@ConfigurationProperties(prefix = "server")
public class ServerConfig {
    public String port;
    public String ip;
    private List<String> listServer;
    private Map<String, String> mapServer;
    private int[] arrPort;

    private Dns dns;

    public static class Dns {
        private String bj;
        private String gz;
    }
}