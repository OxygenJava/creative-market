package com.creative.service.impl;

import com.creative.domain.crow;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.CrowMapper;
import com.creative.service.CrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrowServiceImpl implements CrowService {

    @Autowired
    private CrowMapper crowMapper;


    //发布项目（插入）
    @Override
    public Result Crowinsert(crow crow) {
        int insert = crowMapper.insert(crow);
        Integer code = insert > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = insert > 0 ? "发布成功" : "发布失败";
        return new Result(code, msg, "");
    }

    //查询所有项目
    @Override
    public Result CrowselectAll() {
        List<crow> crows = crowMapper.selectList(null);
        Integer code = crows != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = crows != null ? "查询成功" : "查询失败";
        return new Result(code, msg, crows);
    }


}
