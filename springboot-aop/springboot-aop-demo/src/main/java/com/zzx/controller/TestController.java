package com.zzx.controller;

import com.zzx.aspect.WebLog;
import com.zzx.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/user/login")
    @WebLog(description = "登录接口")
    public String login(@RequestParam String username, @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user.toString();
    }


    @GetMapping("/user/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user.toString();
    }
}