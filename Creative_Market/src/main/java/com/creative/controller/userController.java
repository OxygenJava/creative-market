package com.creative.controller;

import cn.hutool.core.bean.BeanUtil;
import com.creative.domain.user;
import com.creative.dto.*;
import com.creative.service.userService;
import com.creative.utils.userHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class userController {
    @Autowired
    private userService userService;

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @GetMapping("/sendCode/{phone}")
    public Result sendCode(@PathVariable String phone){
        return userService.sendCode(phone);
    }

    /**
     * 验证码登录
     * @param loginForm
     * @return
     */
    @PostMapping("/loginByCode")
    public loginResult loginByCode(@RequestBody loginByCodeFormDTO loginForm){
        return userService.loginByCode(loginForm);
    }

    /**
     * 密码登录
     * @param login
     * @return
     */
    @PostMapping("/loginByPassword")
    public loginResult loginByPassword(@RequestBody loginByPasswordFormDTO login){
        return userService.loginByPassword(login);
    }

    /**
     * 用户注册
     * @param userRegisterForm
     * @return
     */
    @PostMapping("/userRegister")
    public Result userRegister(@RequestBody userRegisterForm userRegisterForm){
        return userService.userRegister(userRegisterForm);
    }

    @GetMapping("/showUserInfoById/{id}")
    public Result showUserInfoById(@PathVariable Integer id){
        return userService.showUserInfoById(id);
    }
}
