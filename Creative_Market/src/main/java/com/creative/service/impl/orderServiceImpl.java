package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.addressInfo;
import com.creative.domain.commodity;
import com.creative.domain.orderTable;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.addressInfoMapper;
import com.creative.mapper.commodityMapper;
import com.creative.mapper.orderMapper;
import com.creative.service.orderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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

    @Override
    public Result orderAdd(orderTable orderTable, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        if (user.getId() != null) {
            orderTable.setUserId(user.getId());
            int insert = orderMapper.insert(orderTable);
            boolean flag = insert > 0;
            return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "添加成功" : "添加失败");
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
        boolean flag = orderTable != null;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR,flag ? "查询成功" : "查询失败",orderTable);
    }
}
