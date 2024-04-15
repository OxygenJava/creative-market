package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.addressInfo;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.addressInfoMapper;
import com.creative.service.addressInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class addressInfoServiceImpl implements addressInfoService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private addressInfoMapper addressInfoMapper;

    /**
     * 添加收货信息
     * @param addressInfo 收货信息类
     * @param request 用于获取token
     * @return
     */
    @Override
    public Result addressInfoAdd(addressInfo addressInfo, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        if (user.getId() == null){
            return new Result(Code.SYNTAX_ERROR,"请先登录");
        }else {
            addressInfo.setUserId(user.getId());
            int insert = addressInfoMapper.insert(addressInfo);
            boolean flag = insert > 0 ;
            return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR,flag ? "添加成功" : "添加失败");
        }
    }

    /**
     * 通过用户id查询所有收货信息
     * @param request
     * @return
     */
    @Override
    public Result addressInfoSelectAllByUserId(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        if (user.getId() != null){
            LambdaQueryWrapper<addressInfo> lqw = new LambdaQueryWrapper<>();
            lqw.eq(addressInfo::getUserId,user.getId());
            List<addressInfo> addressInfos = addressInfoMapper.selectList(lqw);
            boolean flag = addressInfos != null;
            return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR,flag ? "查询成功" : "查询失败",addressInfos);
        }else {
            return new Result(Code.SYNTAX_ERROR,"请先登录");
        }
    }

    /**
     * 修改收货信息
     * @param addressInfo
     * @return
     */
    @Override
    public Result addressInfoUpdate(addressInfo addressInfo) {
        int i = addressInfoMapper.updateById(addressInfo);
        boolean flag = i > 0;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR,flag ? "修改成功" : "修改失败");
    }

    /**
     * 根据id删除收货信息
     * @param id
     * @return
     */
    @Override
    public Result addressInfoDeleteById(Integer id) {
        int i = addressInfoMapper.deleteById(id);
        boolean flag = i > 0;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR,flag ? "删除成功" : "删除失败");
    }

    /**
     * 查询单个收货信息
     * @param addresseeId
     * @return
     */
    @Override
    public Result addressInfoSelectOneByAddresseeId(Integer addresseeId) {
        addressInfo addressInfo = addressInfoMapper.selectById(addresseeId);
        boolean flag = addressInfo != null;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR,flag ? "查询成功" : "查询失败",addressInfo);
    }

    /**
     * 修改收货信息是否默认
     * @param updateId 要设置默认的收货信息id
     * @return
     */
    @Override
    public Result addressInfoUpdateState(Integer updateId) {
        LambdaQueryWrapper<addressInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(addressInfo::getState,1);
        addressInfo addressInfo = addressInfoMapper.selectOne(lqw);
        addressInfo.setState(0);
        int i = addressInfoMapper.updateById(addressInfo);
        addressInfo addressInfoUpdate = addressInfoMapper.selectById(updateId);
        addressInfoUpdate.setState(1);
        int i1 = addressInfoMapper.updateById(addressInfoUpdate);
        boolean flag = i > 0 && i1 > 0;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR,flag ? "修改成功" : "修改失败");
    }

    @Override
    public Result addressInfoCancelState(Integer cancelId, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        if (user.getId() != null){
            LambdaQueryWrapper<addressInfo> lqw = new LambdaQueryWrapper<>();
            lqw.eq(addressInfo::getUserId,user.getId()).eq(addressInfo::getState,1);
            addressInfo addressInfo = addressInfoMapper.selectOne(lqw);
            addressInfo.setState(0);
            int i = addressInfoMapper.updateById(addressInfo);
            boolean flag = i > 0;
            return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR,flag ? "取消默认成功" : "取消默认失败");
        }else {
            return new Result(Code.SYNTAX_ERROR, "请先登录");

        }
    }
}
