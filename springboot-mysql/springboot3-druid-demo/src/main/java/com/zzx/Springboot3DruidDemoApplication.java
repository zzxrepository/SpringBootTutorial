package com.zzx;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zzx.mapper")
public class Springboot3DruidDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(Springboot3DruidDemoApplication.class, args);
    }

}
