package com.creative.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.creative.dto.UserDTO;
import com.creative.utils.userHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class loginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //跨域问题解决
        response.setHeader("Access-Control-Allow-Origin", " http://localhost:8080"); // 允许的来源
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT"); // 允许的方法
        response.setHeader("Access-Control-Max-Age", "3600"); // 预检请求的缓存时间
        response.setHeader("Access-Control-Allow-Headers",
                "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN"); // 允许的头部
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 是否支持cookie跨域
        

        //没有传递token，直接放行到第二拦截器进行拦截
        String authorization = request.getHeader("Authorization");
        if (authorization == null){
            return true;
        }
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(authorization);
        //缓存内无数据，放行..
        if (entries.isEmpty()){
            return true;
        }
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
        System.out.println(userDTO);
        userHolder.saveUser(userDTO);
        //刷新缓存时间
        stringRedisTemplate.expire(authorization,30, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        userHolder.removeUser();
    }
}
