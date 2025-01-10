package com.zzx.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user") // 建议指定一个基础路径
public class UserController {

    @GetMapping("/hi")
    public String hi() { // 方法名首字母小写
        // 打印日志的方法
        log.trace("我是trace级别日志");
        log.debug("我是debug级别日志");
        log.info("我是info级别日志");
        log.warn("我是warn级别日志");
        log.error("我是error级别日志");

        return "Hello World!";
    }
}
