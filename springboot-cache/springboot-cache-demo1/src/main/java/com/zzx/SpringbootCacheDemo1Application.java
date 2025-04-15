package com.zzx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SpringbootCacheDemo1Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootCacheDemo1Application.class, args);
    }

}
