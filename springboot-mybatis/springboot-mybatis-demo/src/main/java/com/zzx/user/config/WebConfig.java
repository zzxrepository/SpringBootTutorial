package com.zzx.user.config;

import com.zzx.user.interceptor.CorsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 CORS 拦截器
        registry.addInterceptor(new CorsInterceptor())
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns("/login", "/error");  // 排除登录和错误页面
    }
}