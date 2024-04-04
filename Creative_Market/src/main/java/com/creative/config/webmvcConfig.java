package com.creative.config;

import com.creative.interceptor.loginInterceptor;
import com.creative.interceptor.userInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
                "/api/user/sendCode/{phone}",
                "/api/user/loginByCode",
                "/api/user/loginByPassword",
                "/api/user/userRegister",
                "/api/user/forgetPassword/**",
                "/api/common/**",
                "/api/homePage/**",
                "/api/commodity/**",
                "api/lable/**",
                "/api/post/**",
                "/api/like/**",
                "/api/crow/**",
                "/api/crow/team/**",
                "/api/homePage/**",
                "/api/buyType/**",
                "/api/collection/**",
                "/api/userSearch/**",
                "/api/concern/**"
                "/api/addressInfo/**",
                "/api/pay/**",
                "/api/order/**",
                "/api/discover/{pageSize}/{pageNumber}"
        ).order(1);
    }

}
