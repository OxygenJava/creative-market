package com.creative.interceptor;

import com.creative.dto.Code;
import com.creative.dto.UserDTO;
import com.creative.utils.userHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class userInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserDTO user = userHolder.getUser();
        if (user == null){
            //在线程中获取不到对象，则响应401状态码
            response.setStatus(Code.INSUFFICIENT_PERMISSIONS);
            return false;
        }
        return true;
    }
}
