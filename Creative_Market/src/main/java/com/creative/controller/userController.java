package com.creative.controller;

import cn.hutool.core.bean.BeanUtil;
import com.creative.domain.commodity;
import com.creative.dto.*;
import com.creative.service.userService;
import com.creative.utils.imgUtils;
import com.creative.utils.userHolder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
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

    /**
     * 修改密码
     * 需要token
     * @param updateForm
     * @return
     */
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody updatePasswordForm updateForm, HttpServletRequest request){
        return userService.updatePassword(updateForm,request);
    }

    /**
     * 忘记密码发送验证码
     * @param phone
     * @return
     */
    @GetMapping("/forgetPassword/sendCode/{phone}")
    public Result forgetPasswordSendCode(@PathVariable String phone){
        return userService.forgetPasswordSendCode(phone);
    }

    /**
     * 忘记密码（校验验证码）
     * @param formDTO
     * @return
     */
    @PostMapping("/forgetPassword/checkCode")
    public Result forgetPasswordCheckCode(@RequestBody loginByCodeFormDTO formDTO){
        return userService.forgetPasswordCheckCode(formDTO);
    }

    /**
     * 忘记密码（重设密码）
     * @param passwordFrom
     * @return
     */
    @PostMapping("/forgetPassword/resetPassword")
    public Result forgetPasswordResetPassword(@RequestBody resetPasswordFrom passwordFrom) {
        return userService.forgetPasswordResetPassword(passwordFrom);
    }

    /**
     * 获取用户个人信息
     * @return
     */
    @GetMapping("/showUserInformation")
    public Result showUserInformation() throws IOException {
        return userService.getUserInfo();
    }

    @PostMapping("/uploadUserIcon")
    public Result uploadUserIcon(MultipartFile file) throws IOException {
        return userService.uploadUserIcon(file);
    }

    @GetMapping("/selectUserById/{id}")
    public Result selectUserById(@PathVariable String id){
        return userService.selectUserById(id);
    }
}
