package com.zzx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zzx.mapper")
public class SpringbootHicariCpDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootHicariCpDemoApplication.class, args);
    }

}
