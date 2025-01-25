package com.zzx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zzx.mapper")
public class SpringbootDruidDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootDruidDemoApplication.class, args);
    }

}
