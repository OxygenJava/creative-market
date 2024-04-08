package com.creative.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creative.domain.commodity;
import com.creative.domain.user;
import com.creative.dto.*;
import org.apache.catalina.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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


    Result selectAll();


    Result getUserInfo() throws IOException;

    /**
     * 上传用户头像
     * @param file
     * @return
     */
    Result uploadUserIcon(MultipartFile file) throws IOException;

    /**
     * 按照用户id或者用户名或者nickName查找
     * @param id
     * @return
     */
    Result selectUserById(String id);
}
