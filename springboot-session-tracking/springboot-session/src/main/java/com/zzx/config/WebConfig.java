package com.zzx.config;


import com.zzx.filter.PerformanceFilter;
import com.zzx.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
            .addPathPatterns("/api/cart/**"); // 保护购物车接口
    }

    @Bean
    public FilterRegistrationBean<PerformanceFilter> perfFilter() {
        FilterRegistrationBean<PerformanceFilter> f = new FilterRegistrationBean<>(new PerformanceFilter());
        f.addUrlPatterns("/api/cart/*"); // 仅测量登录后查询耗时
        return f;
    }
}