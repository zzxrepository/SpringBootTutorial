package com.zzx.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");  // 允许所有来源
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 处理OPTIONS请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;  // 返回false，表示不再执行后续的Controller方法
        }

        return true;  // 继续执行其他拦截器或Controller方法
    }
}
