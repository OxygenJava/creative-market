package com.creative.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.lable;
import com.creative.dto.Code;
import com.creative.dto.Result;

import com.creative.mapper.LableMapper;

import com.creative.service.LableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.StringJoiner;

@Service
@Transactional
public class LableServiceImpl extends ServiceImpl<LableMapper,lable> implements LableService {

    @Autowired
    private LableMapper lableMapper;


    @Override
    public Result insertLable(lable lable) {
        if(lable.getName()!=null && lable.getCreateTime()!=null && lable.getVisitsNumber()!=null){
            int l=-1;
            List<lable> lables = lableMapper.selectList(null);
            for (int i = 0; i < lables.size(); i++) {
                if(lables.get(i).getName().equals(lable.getName())){
                    l=1;
                    break;
                }
                else {
                    l=0;
                }
            }
            if(l==1){ return new Result(Code.SYNTAX_ERROR, "该标签已存在", "");
            }
            else {
                int insert = lableMapper.insert(lable);
                Integer code = insert > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
                String msg = insert > 0 ? "添加成功" : "添加失败";
                return new Result(code, msg, "");
            }
        }
        else {
            return new Result(Code.SYNTAX_ERROR, "标签信息不能为空", "");
        }


    }

    @Override
    public Result selectLableAll() {
        List<lable> lables = lableMapper.selectList(null);
        Integer code = lables != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = lables != null ? "查询成功" : "查询失败";
        return new Result(code, msg, lables);
    }

    @Override
    public Result deleteLable(Integer id) {
        int delete = lableMapper.deleteById(id);
        Integer code = delete > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = delete > 0 ? "删除成功" : "删除失败";
        return new Result(code, msg, "");
    }

    @Override
    public Result updateLable(lable lable) {
        if(lable.getName()!=null && lable.getCreateTime()!=null && lable.getVisitsNumber()!=null){
            int update = lableMapper.updateById(lable);
            Integer code = update > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 ? "修改成功" : "修改失败";
            return new Result(code, msg, "");
        }
        else {
            return new Result(Code.SYNTAX_ERROR, "修改标签时信息不能为空", "");
        }

    }


}
