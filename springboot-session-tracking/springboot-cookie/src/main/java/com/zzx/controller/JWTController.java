package com.zzx.controller;

import com.zzx.model.User;
import com.zzx.service.UserService;
import com.zzx.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/jwt")
public class JWTController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";


    //http://localhost:8080/jwt/login?username=sam&password=password123
    /**
     * 登录(生成Token)
     */
    @GetMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.query().eq("user_name", username).one();
        if (user != null && user.getPassword().equals(password)) {
            String token = jwtUtil.generateToken(username);
            return Map.of("status", "success", "message", "登录成功！", "token", token);
        } else {
            return Map.of("status", "error", "message", "用户名或密码错误");
        }
    }

    //http://localhost:8080/jwt/user  需要带上token
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

        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            String username = claims.getSubject();
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
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Token无效或已过期");
        }
    }

    /**
     * 登出(销毁Token)
     */
    @GetMapping("/logout")
    public Map<String, Object> logout() {
        return Map.of("status", "success", "message", "已登出");
    }
}