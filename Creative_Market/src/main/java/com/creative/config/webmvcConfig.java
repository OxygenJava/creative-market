package com.creative.config;

import com.creative.interceptor.loginInterceptor;
import com.creative.interceptor.userInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class webmvcConfig implements WebMvcConfigurer {
    @Autowired
    private loginInterceptor loginInterceptor;
    @Autowired
    private userInterceptor userInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").order(0);
        registry.addInterceptor(userInterceptor).excludePathPatterns(
                "/api/user/**",
                "/api/common/**"
        ).order(1);
    }
}
