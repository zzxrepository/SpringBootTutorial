package com.zzx;

import com.zzx.config.UserInfo;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class SpringbootConfigFileDemo02Application {
    @Autowired
    private UserInfo userInfo;

    @Value("${int-val}")
    private int intVal;
    @Value("${int-range-val}")
    private int intRangeVal;
    @Value("${uuid-val}")
    private String uuidVal;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootConfigFileDemo02Application.class, args);
    }

    @PostConstruct
    public void init() {
        // 打印配置的用户信息
        System.out.println("用户信息：");
        System.out.println(userInfo);

        System.out.println(intVal);
        System.out.println(intRangeVal);
        System.out.println(uuidVal);
    }
}
