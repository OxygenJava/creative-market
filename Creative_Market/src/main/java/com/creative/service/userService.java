package com.creative.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creative.domain.commodity;
import com.creative.domain.user;
import com.creative.dto.*;
import org.apache.catalina.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface userService extends IService<user>{
    loginResult loginByCode(loginByCodeFormDTO loginForm);

    Result sendCode(String phone);

    loginResult loginByPassword(loginByPasswordFormDTO login);

    Result userRegister(userRegisterForm userRegisterForm);

    Result updatePassword(updatePasswordForm updateForm, HttpServletRequest request);

    Result forgetPasswordSendCode(String phone);

    Result forgetPasswordCheckCode(loginByCodeFormDTO formDTO);

    Result forgetPasswordResetPassword(resetPasswordFrom passwordFrom);

    Result showUserInfoById(Integer id);

    Result selectAll();


    
}
