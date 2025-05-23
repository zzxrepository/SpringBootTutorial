package com.zzx.interceptor;

import com.zzx.context.RequestContext;
import com.zzx.util.JwtUtil;
import com.zzx.util.RedisUtil;
import com.zzx.util.ResVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired private JwtUtil jwtUtil;
    @Autowired private RedisUtil redisUtil;
    @Autowired private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler)
            throws Exception {
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            Long userId = jwtUtil.validateToken(token);
            Object inRedis = redisUtil.get(token);
            if (userId != null && inRedis != null && inRedis.toString().equals(userId.toString())) {
                RequestContext.setUserId(userId);
                return true;
            }
        }
        res.setCharacterEncoding("UTF-8");
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ResVo<?> resp = ResVo.error(401, "未授权或会话过期");
        String json = objectMapper.writeValueAsString(resp);
        res.getWriter().write(json);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res,
                                Object handler, Exception ex) {
        RequestContext.clear();
    }
}