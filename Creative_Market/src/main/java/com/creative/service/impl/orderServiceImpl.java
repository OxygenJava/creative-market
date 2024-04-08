package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.*;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.addressInfoMapper;
import com.creative.mapper.buyTypeMapper;
import com.creative.mapper.commodityMapper;
import com.creative.mapper.orderMapper;
import com.creative.service.orderService;
import com.creative.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

    /**
     * 创建订单
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
            return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "添加订单成功" : "添加订单失败",orderTable);
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
        orderTable.setCommodity(commodity);
        orderTable.setBuyType(buyTypeMapper.selectById(orderTable.getBuyTypeId()));
        boolean flag = orderTable != null;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "查询成功" : "查询失败");
    }

    @Override
    public Result orderUpdateById(orderTable orderTable) {
        int i = orderMapper.updateById(orderTable);
        boolean flag = i > 0;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "修改成功" : "修改失败");
    }


}
