package com.zzx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class SpringbootConfigFileDemo03Application {
    @Autowired
    private User user;

    @Autowired
    private Author author;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootConfigFileDemo03Application.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println("作者信息：" + author);
        System.out.println("用户信息：" + user);
    }
}
