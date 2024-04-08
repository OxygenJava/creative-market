package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.commodity;
import com.creative.domain.user;
import com.creative.dto.*;
import com.creative.mapper.userMapper;
import com.creative.service.userService;
import com.creative.utils.RegexUtils;
import com.creative.utils.imgUtils;
import com.creative.utils.userHolder;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class userServiceImpl extends ServiceImpl<userMapper, user> implements userService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private userMapper userMapper;

    @Value("${creativeMarket.shopImage}")
    private String imgAddress;

    @Autowired
    private HttpServletRequest request;
    @Value("creativeMarket.iconImage")
    private String iconImage;
    @Override
    public Result sendCode(String phone) {

        if (!RegexUtils.phoneMatches(phone)){
            return Result.fail(Code.SYNTAX_ERROR,"手机号填写有误");
        }

        String code = RandomUtil.randomNumbers(6);
        System.out.println("发送的验证码: "+code);

        //将验证码放入缓存
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("login:code"+phone,code,1, TimeUnit.MINUTES);
        return Result.success();
    }

    @Override
    public loginResult loginByCode(loginByCodeFormDTO loginForm) {
        //校验手机号
        if (!RegexUtils.phoneMatches(loginForm.getPhoneNumber())){
            return loginResult.fail(Code.SYNTAX_ERROR,"手机号填写有误");
        }
        //缓存中取出验证码
        String code = redisTemplate.opsForValue().get("login:code" + loginForm.getPhoneNumber());
        System.out.println(code);
        if (code == null){
            return loginResult.fail(Code.SYNTAX_ERROR,"请重新获取验证码");
        }
        if (!code.equals(loginForm.getCode())){
            return loginResult.fail(Code.SYNTAX_ERROR,"验证码填写有误");
        }
        //判断账号是否存在
        user one = lambdaQuery().eq(user::getPhoneNumber, loginForm.getPhoneNumber()).one();
        //当账号不存在，自动创建账号
        if (one == null){
            one = new user();
            one.setUsername(loginForm.getPhoneNumber());
            one.setPhoneNumber(loginForm.getPhoneNumber());
            one.setState(1);
            //设置创建时间
            one.setCreateTime(LocalDateTime.now());
            //设置随机昵称
            one.setNickName(getRandomString());
            save(one);
        }else {
            //当账号存在，但是状态为0
            if (one.getState() == 0){
                return loginResult.fail(Code.SYNTAX_ERROR,"账号已被封禁");
            }
        }

        //重新设置最后一次登录时间
        one.setLastLoginTime(LocalDateTime.now());
        //修改用户信息(最后一次登录时间)
        updateById(one);

        //获取一个map集合，用于放入缓存
        Map<String, Object> stringObjectMap = getUserDTOMap(one);

        String token = UUID.randomUUID().toString();
        redisTemplate.opsForHash().putAll(token,stringObjectMap);
        redisTemplate.expire(token,30,TimeUnit.MINUTES);
        return loginResult.successLogin(token);
    }


    @Override
    public loginResult loginByPassword(loginByPasswordFormDTO login) {
        String usernameForm = login.getUsername();
        //查看输入的账号是否为用户名
        user user1 = lambdaQuery().eq(user::getUsername, usernameForm).one();
        //查看输入的账号是否为手机号

        user user2 = lambdaQuery().eq(user::getPhoneNumber, usernameForm).one();

        if (user1 == null && user2 == null){
            return loginResult.fail(Code.SYNTAX_ERROR,"账号不存在");
        }

        //校验密码
        if (user1 != null){
            return checkPassword(login,user1);
        }else {
            return checkPassword(login,user2);
        }

    }

    @Override
    public Result userRegister(userRegisterForm userRegisterForm) {
        String userRegisterFormUsername = userRegisterForm.getUsername();
        user one = lambdaQuery().eq(user::getUsername, userRegisterFormUsername).one();
        //判断注册的用户名是否已经存在
        if (one != null){
            return Result.fail(Code.SYNTAX_ERROR,"用户名已存在");
        }
        //判断注册的用户名是否符合规定
        if (!RegexUtils.usernameMatches(userRegisterFormUsername)){
            return Result.fail(Code.SYNTAX_ERROR,"用户名格式为8-16位字符或是电子邮箱");
        }
        //判断输入的密码是否符合规定
        if (!RegexUtils.passwordMatches(userRegisterForm.getPassword())){
            return Result.fail(Code.SYNTAX_ERROR,"密码格式为8-16位字符");
        }
        //校验电子邮箱是否符合规定
        String e_mail = userRegisterForm.getE_mail();
        if (e_mail != null && !"".equals(e_mail)){
            if (!RegexUtils.emailMatches(e_mail)){
                return Result.fail(Code.SYNTAX_ERROR,"电子邮箱格式有误");
            }
        }
        //校验电话号码是否符合规定
        if (!RegexUtils.phoneMatches(userRegisterForm.getPhoneNumber())){
            return Result.fail(Code.SYNTAX_ERROR,"手机号码格式有误");
        }
        //判断手机号是否注册过
        user one1 = lambdaQuery().eq(user::getPhoneNumber, userRegisterForm.getPhoneNumber()).one();
        if (one1 != null){
            return Result.fail(Code.SYNTAX_ERROR,"该手机号已被注册");
        }
        //判断用户是否自行设置了名字
        String nickName = userRegisterForm.getNickName();
        if (nickName == null || "".equals(nickName)){
            userRegisterForm.setNickName(getRandomString());
        }
        //所有数据校验完成，数据入库
        user user = BeanUtil.copyProperties(userRegisterForm, user.class);
        //设置账号创建时间
        user.setCreateTime(LocalDateTime.now());
        //给密码加密
        user.setPassword(DigestUtil.md5Hex(user.getPassword()));
        //设置初始状态
        user.setState(1);
        save(user);
        return Result.success();
    }

    @Override
    public Result updatePassword(updatePasswordForm updateForm, HttpServletRequest request) {
        //请求头获取token
        String authorization = request.getHeader("Authorization");
        //缓存中获取user对象
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        //使用糊涂包把map集合转换为储存的对象
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
        user updateUser = lambdaQuery().eq(user::getId, userDTO.getId()).one();
        if (updateUser == null){
            return Result.fail(Code.SYNTAX_ERROR,"查找不到该用户");
        }
        //该账号是否有密码
        if (updateUser.getPassword() == null){
            return Result.fail(Code.SYNTAX_ERROR,"该账号暂无设置密码");
        }
        //比对原始密码是否正确
        String originalPassword = updateForm.getOriginalPassword();
        originalPassword = DigestUtil.md5Hex(originalPassword);
        if (!updateUser.getPassword().equals(originalPassword)){
            return Result.fail(Code.SYNTAX_ERROR,"原密码错误");
        }
        //比对输入的密码是否符合规则
        String newPassword = updateForm.getNewPassword();
        if (!RegexUtils.passwordMatches(newPassword)){
            return Result.fail(Code.SYNTAX_ERROR,"密码格式为8-16位字符");
        }

        //比对第二次输入的密码是否与第一次相同
        if (!newPassword.equals(updateForm.getConfirmNewPassword())){
            return Result.fail(Code.SYNTAX_ERROR,"两次输入的密码不相同");
        }
        //设置新密码
        updateUser.setPassword(DigestUtil.md5Hex(updateForm.getConfirmNewPassword()));
        //设置修改时间
        updateUser.setUpdateTime(LocalDateTime.now());
        //保存用户
        updateById(updateUser);
        return Result.success();
    }

    @Override
    public Result forgetPasswordSendCode(String phone) {
        if (phone == null || "".equals(phone)){
            return Result.fail(Code.SYNTAX_ERROR,"手机号不能为空");
        }
        //校验手机号
        if (!RegexUtils.phoneMatches(phone)){
            return Result.fail(Code.SYNTAX_ERROR,"手机号填写有误");
        }
        //查看该手机号是否有账号
        user forgetPassword = lambdaQuery().eq(user::getPhoneNumber, phone).one();
        if (forgetPassword == null){
            return Result.fail(Code.SYNTAX_ERROR,"该手机号下无账号");
        }
        if (forgetPassword.getPassword() == null){
            return Result.fail(Code.SYNTAX_ERROR,"该账号无密码,请进行登录后设置");
        }
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String code = RandomUtil.randomNumbers(6);
        ops.set("forget:code"+phone,code,1,TimeUnit.MINUTES);
        System.out.println("忘记密码：code: "+code);
        return Result.success();
    }

    @Override
    public Result forgetPasswordCheckCode(loginByCodeFormDTO formDTO) {
        //校验手机号
        if (!RegexUtils.phoneMatches(formDTO.getPhoneNumber())){
            return Result.fail(Code.SYNTAX_ERROR,"手机号填写有误");
        }
        //缓存中取出验证码
        String code = redisTemplate.opsForValue().get("forget:code" + formDTO.getPhoneNumber());
        if (code == null){
            return Result.fail(Code.SYNTAX_ERROR,"请重新获取验证码");
        }
        if (!code.equals(formDTO.getCode())){
            return Result.fail(Code.SYNTAX_ERROR,"验证码填写有误");
        }
        return Result.success("验证码正确");
    }

    @Override
    public Result forgetPasswordResetPassword(resetPasswordFrom passwordFrom) {
        String password = passwordFrom.getPassword();
        //校验密码
        if(!RegexUtils.passwordMatches(password)){
            return Result.fail(Code.SYNTAX_ERROR,"密码格式为8-16位字符");
        }
        //校验第二次输入的密码是否与第一次相同
        if (!password.equals(passwordFrom.getConfirmPassword())){
            return Result.fail(Code.SYNTAX_ERROR,"两次输入的密码不一致");
        }
        String phoneNumber = passwordFrom.getPhoneNumber();
        user one = lambdaQuery().eq(user::getPhoneNumber, phoneNumber).one();
        one.setPassword(DigestUtil.md5Hex(password));
        one.setUpdateTime(LocalDateTime.now());
        updateById(one);
        return Result.success("设置成功");
    }


    @Override
    public Result selectAll() {
        List<user> users = userMapper.selectList(null);
        Integer code = users != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = users != null ? "查询成功" : "查询失败";
        return new Result(code, msg, users);
    }

    @Override
    public Result getUserInfo() throws IOException {
        UserDTO user = userHolder.getUser();
        if (user == null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }
        String s = imgUtils.encodeImageToBase64(iconImage + "\\" + user.getIconImage());
        user.setIconImage(s);

        return Result.success(user);
    }


    /******************************  userService内部方法  **********************************/
    /**
     * 检测密码是否正确
     * @param login
     * @param user
     * @return
     */
    public loginResult checkPassword(loginByPasswordFormDTO login,user user){
        if (user.getState() == 0){
            return loginResult.fail(Code.SYNTAX_ERROR,"账号已被封禁");
        }
        //数据库密码
        String password = user.getPassword();
        //获取前端传回来的密码
        String passwordFromLogin = login.getPassword();
        //给传回来的密码进行md5加密
        String passwordFromLoginMd5 = DigestUtil.md5Hex(passwordFromLogin);
        //加密后与数据库的密码做比对
        if (!passwordFromLoginMd5.equals(password)){
            //比对错误
            return loginResult.fail(Code.SYNTAX_ERROR,"密码错误");
        }else {
            //获取token
            String token = UUID.randomUUID().toString();
            //重新设置最后一次登录时间
            user.setLastLoginTime(LocalDateTime.now());
            updateById(user);
            //获取一个map集合，用于放入缓存
            Map<String, Object> stringObjectMap = getUserDTOMap(user);
            redisTemplate.opsForHash().putAll(token,stringObjectMap);
            redisTemplate.expire(token,30,TimeUnit.MINUTES);


            return loginResult.successLogin(token);
        }
    }

    /**
     * 返回一个集合存入缓存
     * @param user
     * @return
     */
    public Map<String, Object> getUserDTOMap(user user){
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(userDTO);
        stringObjectMap.put("id",userDTO.getId().toString());
        stringObjectMap.put("createTime",userDTO.getCreateTime().toString());
        stringObjectMap.put("lastLoginTime",userDTO.getLastLoginTime().toString());
        return stringObjectMap;
    }

    public String getRandomString(){
        return "user_"+RandomUtil.randomString(15);
    }
}

