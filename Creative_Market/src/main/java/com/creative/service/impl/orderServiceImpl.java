package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.*;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.payDTO;
import com.creative.mapper.*;
import com.creative.service.orderService;
import com.creative.utils.RandomUtil;
import com.creative.utils.imgUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class orderServiceImpl implements orderService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private orderMapper orderMapper;

    @Autowired
    private addressInfoMapper addressInfoMapper;

    @Autowired
    private commodityMapper commodityMapper;

    @Autowired
    private buyTypeMapper buyTypeMapper;

    @Autowired
    private walletMapper walletMapper;

    @Value("${creativeMarket.shopImage}")
    private String imgAddress;

    /**
     * 创建订单
     *
     * @param orderTable 订单类
     * @param request
     * @return
     */
    @Override
    public Result orderAdd(orderTable orderTable, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        if (user.getId() != null) {
            orderTable.setOrderCode(RandomUtil.generateOrderCode());
            orderTable.setOrderTime(LocalDateTime.now());
            orderTable.setUserId(user.getId());

            Integer buyTypeId = orderTable.getBuyTypeId();
            buyType buyType = buyTypeMapper.selectById(buyTypeId);
            orderTable.setPayMoney(buyType.getBuyMoney());

            int insert = orderMapper.insert(orderTable);
            boolean flag = insert > 0;
            return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "添加订单成功" : "添加订单失败", orderTable);
        } else {
            return new Result(Code.SYNTAX_ERROR, "请先登录");
        }
    }

    @Override
    public Result orderSelectByUserId(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        if (user.getId() != null) {
            LambdaQueryWrapper<orderTable> lqw = new LambdaQueryWrapper<>();
            lqw.eq(orderTable::getUserId, user.getId());
            List<orderTable> orderTables = orderMapper.selectList(lqw);
            for (orderTable orderTable : orderTables) {
                addressInfo addressInfo = addressInfoMapper.selectById(orderTable.getAddresseeId());
                orderTable.setAddressInfo(addressInfo);
                commodity commodity = commodityMapper.selectById(orderTable.getCommodityId());
                String homePageImage = imgAddress + "\\" + commodity.getHomePageImage();
                try {
                    homePageImage = imgUtils.encodeImageToBase64(homePageImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                commodity.setHomePageImage(homePageImage);
                orderTable.setCommodity(commodity);
                buyType buyType = buyTypeMapper.selectById(orderTable.getBuyTypeId());
                orderTable.setBuyType(buyType);
            }
            boolean flag = orderTables != null;
            return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "查询成功" : "查询失败", orderTables);
        } else {
            return new Result(Code.SYNTAX_ERROR, "请先登录");
        }
    }

    @Override
    public Result orderSelectOneByOrderId(Integer orderId) {
        orderTable orderTable = orderMapper.selectById(orderId);
        addressInfo addressInfo = addressInfoMapper.selectById(orderTable.getAddresseeId());
        orderTable.setAddressInfo(addressInfo);
        commodity commodity = commodityMapper.selectById(orderTable.getCommodityId());
        String homePageImage = imgAddress + "\\" + commodity.getHomePageImage();
        try {
            homePageImage = imgUtils.encodeImageToBase64(homePageImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        commodity.setHomePageImage(homePageImage);
        orderTable.setCommodity(commodity);
        orderTable.setBuyType(buyTypeMapper.selectById(orderTable.getBuyTypeId()));
        boolean flag = orderTable != null;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "查询成功" : "查询失败",orderTable);
    }

    @Override
    public Result orderUpdateById(orderTable orderTable) {
        int i = orderMapper.updateById(orderTable);
        boolean flag = i > 0;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "修改成功" : "修改失败");
    }

    @Override
    public Result orderPay(Integer orderId,HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        Integer userId = user.getId();
        if (userId != null){
            LambdaQueryWrapper<wallet> lqw = new LambdaQueryWrapper<>();
            lqw.eq(wallet::getUserId,userId);
            wallet wallet = walletMapper.selectOne(lqw);
            if (wallet == null){
                return new Result(Code.NORMAL,"该用户未开启钱包功能");
            }
            BigDecimal balanceAccount = wallet.getBalanceAccount();
            orderTable orderTable = orderMapper.selectById(orderId);
            Integer payState = orderTable.getPayState();
            if (payState == 1) return new Result(Code.NORMAL,"订单已支付");
            BigDecimal payMoney = BigDecimal.valueOf(orderTable.getPayMoney());
            if (balanceAccount.compareTo(payMoney) >= 0 && balanceAccount.compareTo(BigDecimal.ZERO) >= 0){
                wallet.setBalanceAccount(balanceAccount.subtract(payMoney));
                Integer commodityId = orderTable.getCommodityId();
                commodity commodity = commodityMapper.selectById(commodityId);
                commodity.setCrowdfundedAmount(commodity.getCrowdfundedAmount() + payMoney.doubleValue());
                commodityMapper.updateById(commodity);
                orderTable.setPayState(1);
                orderTable.setPayTime(LocalDateTime.now());
                int i = walletMapper.updateById(wallet);
                int i1 = orderMapper.updateById(orderTable);
                boolean flag = i > 0 && i1 > 0;
                return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "支付成功" : "支付失败");
            }else {
                return new Result(Code.NORMAL,"支付失败，账户余额不足");
            }
        }else {
            return new Result(Code.SYNTAX_ERROR, "请先登录");
        }
    }

    /**
     * 取消订单
     * @param orderId
     * @return
     */
    @Override
    public Result orderDelete(Integer orderId) {
        boolean flag = false;
        orderTable orderTable = orderMapper.selectById(orderId);
        if (orderTable.getPayState() == 0){
            int i = orderMapper.deleteById(orderId);
            flag = i > 0;
        }
        return new Result(flag?Code.NORMAL:Code.SYNTAX_ERROR,flag?"取消订单成功":"取消订单失败");
    }


    /**
     * 支付界面查询
     * @param commodityId   商品id
     * @param buyTypeId     购买类型id
     * @param request
     * @return
     */
    @Override
    public Result paySelect(Integer commodityId,Integer buyTypeId, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        payDTO payDTO = new payDTO();
        if (user.getId() != null) {
            LambdaQueryWrapper<addressInfo> lqw = new LambdaQueryWrapper<>();
            lqw.eq(addressInfo::getState, 1)
                .eq(addressInfo::getUserId,user.getId());
            addressInfo addressInfo = addressInfoMapper.selectOne(lqw);
            payDTO.setAddressInfo(addressInfo);
            commodity commodity = commodityMapper.selectById(commodityId);
            String homePageImage = imgAddress +"\\" + commodity.getHomePageImage();
            String Base64homePageImage = null;
            try {
                Base64homePageImage = imgUtils.encodeImageToBase64(homePageImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            commodity.setHomePageImage(Base64homePageImage);
            payDTO.setCommodity(commodity);
            buyType buyType = buyTypeMapper.selectById(buyTypeId);
            payDTO.setBuyType(buyType);
            boolean flag = addressInfo != null && commodity != null && buyType != null;
            return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "查询成功": "查询失败", payDTO);
        } else {
            return new Result(Code.SYNTAX_ERROR, "请先登录");
        }
    }
}
