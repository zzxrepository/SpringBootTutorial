package com.zzx.controller;

import com.zzx.model.User;
import com.zzx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private UserService userService;

    private static final String SESSION_ATTRIBUTE_NAME = "user";

    //http://localhost:8080/session/login?username=sam&password=password123
    /**
     * 登录(创建Session)
     */
    @GetMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        User user = userService.query().eq("user_name", username).one();
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute(SESSION_ATTRIBUTE_NAME, username);
            return Map.of("status", "success", "message", "登录成功！");
        } else {
            return Map.of("status", "error", "message", "用户名或密码错误");
        }
    }

    //http://localhost:8080/session/user
    /**
     * 查询用户信息
     */
    @GetMapping("/user")
    public Map<String, Object> getUserInfo(HttpSession session) {
        Object username = session.getAttribute(SESSION_ATTRIBUTE_NAME);
        if (username != null) {
            return Map.of(
                    "status", "success",
                    "data", Map.of(
                            "username", username
                    )
            );
        } else {
            return Map.of("status", "error", "message", "未登录，请先登录");
        }
    }

    /**
     * 登出(销毁Session)
     */
    @GetMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate(); // 销毁Session
        return Map.of("status", "success", "message", "已登出");
    }
}