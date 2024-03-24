package com.creative.service.impl;

import com.creative.domain.buyType;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.buyTypeMapper;
import com.creative.service.buyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class buyTypeServiceImpl implements buyTypeService {
    @Autowired
    private buyTypeMapper buyTypeMapper;

    /**
     * 添加商品购买类别
     * @param buyType 商品购买类别类
     * @return
     */
    @Override
    public Result buyTypeAdd(buyType buyType) {
        int i = buyTypeMapper.insert(buyType);
        Integer code = i > 0 ? Code.Add_OK : Code.Add_ERR;
        String msg = i > 0 ? "添加成功" : "添加失败";
        return new Result(code,msg);
    }

    @Override
    public Result buyTypeSelectAll() {
        return null;
    }
}
