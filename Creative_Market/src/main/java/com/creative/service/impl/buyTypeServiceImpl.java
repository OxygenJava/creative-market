package com.creative.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.buyType;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.buyTypeMapper;
import com.creative.service.buyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class buyTypeServiceImpl implements buyTypeService {
    @Autowired
    private buyTypeMapper buyTypeMapper;

    /**
     * 添加商品购买类别
     *
     * @param buyType 商品购买类别类
     * @return
     */
    @Override
    public Result buyTypeAdd(buyType buyType) {
        int i = buyTypeMapper.insert(buyType);
        Integer code = i > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = i > 0 ? "添加成功" : "添加失败";
        return new Result(code, msg);
    }

    @Override
    @Transactional
    public Result buyTypeAddMore(List<buyType> buyTypes) {
        ArrayList<Integer> arrayList = new ArrayList();
        boolean flag = true;
        for (buyType buyType : buyTypes) {
            System.out.println(buyType);
            if (buyType.getCommodityId() == null){
                throw new RuntimeException("未传递商品id");
            }
            int insert = buyTypeMapper.insert(buyType);
            arrayList.add(insert);
        }
        for (Integer integer : arrayList) {
            if (integer <= 0) {
                flag = false;
            }
        }
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "添加多条成功" : "添加多条失败");
    }

    /**
     * 查询商品所有可购买类别
     *
     * @param commodityId 传入的商品id
     * @return
     */
    @Override
    public Result buyTypeSelectAllTypeByCommodityId(Integer commodityId) {
        LambdaQueryWrapper<buyType> lqw = new LambdaQueryWrapper<>();
        lqw.eq(buyType::getCommodityId, commodityId);
        List<buyType> buyTypes = buyTypeMapper.selectList(lqw);
        boolean flag = buyTypes != null;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "查询成功" : "查询失败", buyTypes);
    }

    /**
     * 用于删除商品可购买类别
     *
     * @param id 要删除的类别id
     * @return
     */
    @Override
    public Result buyTypeDeleteById(Integer id) {
        int i = buyTypeMapper.deleteById(id);
        boolean flag = i > 0;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "删除成功" : "删除失败");
    }

    /**
     * 用于修改商品可购买类别
     *
     * @param buyType
     * @return
     */
    @Override
    public Result buyTypeUpdate(buyType buyType) {
        int i = buyTypeMapper.updateById(buyType);
        boolean flag = i > 0;
        return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "修改成功" : "修改失败");
    }


}
