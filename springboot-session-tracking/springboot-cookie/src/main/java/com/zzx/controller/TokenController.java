package com.zzx.controller;

import com.zzx.model.User;
import com.zzx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/token")
public class TokenController {

    @Autowired
    private UserService userService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    // 存储Token和用户信息的映射（实际项目中应该存储在数据库或缓存中）
    private static final Map<String, String> tokenMap = new HashMap<>();

    //http://localhost:8080/token/login?username=sam&password=password123
    /**
     * 登录(生成Token)
     */
    @GetMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.query().eq("user_name", username).one();
        if (user != null && user.getPassword().equals(password)) {
            String token = UUID.randomUUID().toString(); // 生成随机Token
            tokenMap.put(token, username); // 将Token和用户名存储在映射中
            return Map.of("status", "success", "message", "登录成功！", "token", token);
        } else {
            return Map.of("status", "error", "message", "用户名或密码错误");
        }
    }

    //http://localhost:8080/token/user  需要带上token
    /**
     * 查询用户信息
     */
    @GetMapping("/user")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            token = token.substring(BEARER_PREFIX.length());
        }

        if (token == null) {
            return Map.of("status", "error", "message", "未提供Token，请先登录");
        }

        String username = tokenMap.get(token); // 从映射中获取用户名
        if (username != null) {
            return Map.of(
                    "status", "success",
                    "data", Map.of(
                            "username", username
                    )
            );
        } else {
            return Map.of("status", "error", "message", "Token无效或已过期");
        }
    }

    /**
     * 登出(销毁Token)
     */
    @GetMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            token = token.substring(BEARER_PREFIX.length());
        }

        if (token != null) {
            tokenMap.remove(token); // 从映射中移除Token
        }
        return Map.of("status", "success", "message", "已登出");
    }
}