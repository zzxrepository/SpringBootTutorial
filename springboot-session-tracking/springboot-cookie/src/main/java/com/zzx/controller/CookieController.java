package com.zzx.controller;

import com.zzx.model.User;
import com.zzx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cookie")
public class CookieController {

    @Autowired
    private UserService userService;

    private static final String SESSION_COOKIE_NAME = "session_id";

    //http://localhost:8080/cookie/login?username=sam&password=password123
    /**
     * 登录(设置Cookie)
     */
    @GetMapping("/login")
    public Map<String, Object> login(@RequestParam Map<String, String> credentials, HttpServletResponse response) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return Map.of("status", "error", "message", "用户名和密码不能为空");
        }

        // 查询用户
        User user = userService.query().eq("user_name", username).one();
        if (user == null || !user.getPassword().equals(password)) {
            return Map.of("status", "error", "message", "用户名或密码错误");
        }

        // 创建Cookie
        // 生成随机Session ID
        String sessionId = UUID.randomUUID().toString();
        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        sessionCookie.setMaxAge(60); // 15分钟过期（秒）
        sessionCookie.setHttpOnly(true); // 防止客户端脚本访问
        sessionCookie.setSecure(false); // 在生产环境中应设置为true（仅通过HTTPS传输）
        sessionCookie.setPath("/");

        response.addCookie(sessionCookie);

        return Map.of("status", "success", "message", "登录成功！");
    }

    //http://localhost:8080/cookie/user
    /**
     * 查询用户信息
     */
    @GetMapping("/user")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Map.of("status", "error", "message", "未登录，请先登录");
        }

        for (Cookie cookie : cookies) {
            if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                // 这里可以结合Session机制，根据sessionId查询用户信息
                // 为了简化，这里直接返回模拟数据
                return Map.of(
                        "status", "success",
                        "data", Map.of(
                                "username", "user1",
                                "email", "user1@example.com"
                        )
                );
            }
        }

        return Map.of("status", "error", "message", "未登录，请先登录");
    }

    /**
     * 登出(清除Cookie)
     */
    @GetMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    cookie.setMaxAge(0); // 立即过期
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }

        return Map.of("status", "success", "message", "已登出");
    }
}