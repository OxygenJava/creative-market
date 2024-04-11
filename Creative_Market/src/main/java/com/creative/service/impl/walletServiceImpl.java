package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.user;
import com.creative.domain.wallet;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.payPasswordForm;
import com.creative.dto.walletDTO;
import com.creative.mapper.walletMapper;
import com.creative.service.walletService;
import com.creative.utils.RegexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class walletServiceImpl implements walletService {
    //redis缓存
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private walletMapper walletMapper;

    /**
     * 查询用户是否已开启钱包功能
     *
     * @param request
     * @return
     */
    @Override
    public Result isOpenWallet(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        Integer userId = user.getId();
        if (userId != null) {
            LambdaQueryWrapper<wallet> lqw = new LambdaQueryWrapper<>();
            lqw.eq(wallet::getUserId, userId);
            wallet wallet = walletMapper.selectOne(lqw);
            if (wallet == null) {
                return new Result(Code.NORMAL, "钱包未开启", 0);
            } else {
                walletDTO walletDTO = BeanUtil.copyProperties(wallet, walletDTO.class);
                return new Result(Code.NORMAL, "钱包已开启", walletDTO);
            }
        } else {
            return new Result(Code.NORMAL, "请先登录");
        }
    }

    /**
     * 开启钱包功能
     *
     * @param payPasswordForm 确定支付密码类
     * @param request
     * @return
     */
    @Override
    public Result openWallet(payPasswordForm payPasswordForm, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        Integer userId = user.getId();
        //判断用户是否登录
        if (userId != null) {
            wallet wallet = new wallet();
            wallet.setUserId(userId);
            //判断输入的密码格式
            if (!RegexUtils.payPasswordMatches(payPasswordForm.getOriginalPassword())) {
                return Result.fail(Code.SYNTAX_ERROR, "支付密码格式为6位0-9数字");
            }
            if (!RegexUtils.payPasswordMatches(payPasswordForm.getConfirmNewPassword())) {
                return Result.fail(Code.SYNTAX_ERROR, "支付密码格式为6位0-9数字");
            }
            //判断两次密码是否一致
            if (payPasswordForm.getOriginalPassword().equals(payPasswordForm.getConfirmNewPassword())) {
                wallet.setPayPassword(DigestUtil.md5Hex(payPasswordForm.getConfirmNewPassword()));
                wallet.setIsOpen(1);
                wallet.setCreateTime(LocalDateTime.now());
                int insert = walletMapper.insert(wallet);
                boolean flag = insert > 0;
                return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "成功开启钱包" : "开启钱包失败");
            } else {
                return Result.fail(Code.SYNTAX_ERROR, "两次输入的密码不相同");
            }
        } else {
            //未登录
            return new Result(Code.SYNTAX_ERROR, "请先登录");
        }
    }

    /**
     * 修改支付密码
     * @param payPasswordForm
     * @param request
     * @return
     */
    @Override
    public Result updatePayPassword(payPasswordForm payPasswordForm, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        Integer userId = user.getId();
        if (userId != null) {
            LambdaQueryWrapper<wallet> lqw = new LambdaQueryWrapper<>();
            lqw.eq(wallet::getUserId, userId);
            wallet wallet = walletMapper.selectOne(lqw);
            //比对原密码是否一致
            String originalPassword = payPasswordForm.getOriginalPassword();
            originalPassword = DigestUtil.md5Hex(originalPassword);
            if (!wallet.getPayPassword().equals(originalPassword)) {
                return Result.fail(Code.SYNTAX_ERROR, "原密码错误");
            }
            //一致则判断输入的新密码和确认密码格式是否正确
            if (!RegexUtils.payPasswordMatches(payPasswordForm.getNewPassword())) {
                return Result.fail(Code.SYNTAX_ERROR, "支付密码格式为6位0-9数字");
            }
            if (!RegexUtils.payPasswordMatches(payPasswordForm.getConfirmNewPassword())) {
                return Result.fail(Code.SYNTAX_ERROR, "支付密码格式为6位0-9数字");
            }
            //格式都正确则看新密码和确认密码是否一致
            if (payPasswordForm.getNewPassword().equals(payPasswordForm.getConfirmNewPassword())) {
                //一致则修改密码，并设置修改时间
                wallet.setPayPassword(DigestUtil.md5Hex(payPasswordForm.getConfirmNewPassword()));
                wallet.setUpdateTime(LocalDateTime.now());
                //更新到库中
                int i = walletMapper.updateById(wallet);
                boolean flag = i > 0;
                return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "修改密码成功" : "修改密码失败");
            } else {
                return Result.fail(Code.SYNTAX_ERROR, "两次输入的密码不相同");
            }

        } else {
            return new Result(Code.SYNTAX_ERROR, "请先登录");
        }
    }

    /**
     * 查询用户钱包信息
     * @param request
     * @return
     */
    @Override
    public Result selectWallet(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        Integer userId = user.getId();
        if (userId != null){
            LambdaQueryWrapper<wallet> lqw = new LambdaQueryWrapper<>();
            lqw.eq(wallet::getUserId,userId);
            wallet wallet = walletMapper.selectOne(lqw);
            if (wallet == null){
                return new Result(Code.NORMAL,"用户未开启钱包功能");
            }
            walletDTO walletDTO = BeanUtil.copyProperties(wallet, walletDTO.class);
            return new Result(Code.NORMAL,"查询成功",walletDTO);
        }else {
            return new Result(Code.SYNTAX_ERROR, "请先登录");
        }
    }

    /**
     * 充值功能
     * @param investMoney 要充值的金额
     * @param request
     * @return
     */
    @Override
    public Result investMoney(BigDecimal investMoney, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        Integer userId = user.getId();
        if (userId != null){
            LambdaQueryWrapper<wallet> lqw = new LambdaQueryWrapper<>();
            lqw.eq(wallet::getUserId,userId);
            wallet wallet = walletMapper.selectOne(lqw);
            if (wallet != null){
                if (investMoney.compareTo(BigDecimal.ZERO) < 0){
                    return new Result(Code.SYNTAX_ERROR,"充值金额不能小于0");
                }else {
                    BigDecimal balanceAccount = wallet.getBalanceAccount();
                    wallet.setBalanceAccount(balanceAccount.add(investMoney));
                    int i = walletMapper.updateById(wallet);
                    boolean flag = i > 0;
                    return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "充值成功" : "充值失败");
                }
            }else {
                return new Result(Code.NORMAL,"用户未开启钱包功能");
            }

        }else {
            return new Result(Code.SYNTAX_ERROR, "请先登录");
        }
    }


}
