package com.creative.service.impl;

import com.creative.domain.crow;
import com.creative.domain.lable;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.CrowMapper;
import com.creative.mapper.LableMapper;
import com.creative.service.CrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class CrowServiceImpl implements CrowService {

    @Autowired
    private CrowMapper crowMapper;


    //发布项目（插入）
    @Override
    public Result Crowinsert(crow crow) {
        ArrayList list1 = new ArrayList();
        Class class1 = crow.getClass();
        Field[] des = class1.getDeclaredFields();
        for (Field de : des) {
            try {
                PropertyDescriptor pd1 = new PropertyDescriptor(de.getName(), class1);
                //获得get方法
                Method getMethod1 = pd1.getReadMethod();
                //执行get方法返回一个Object
                Object obj1 = getMethod1.invoke(crow);
                list1.add(obj1);
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
        }

        list1.removeAll(Collections.singleton(null));

        if (list1.size() >= 11) {
                    int insert = crowMapper.insert(crow);
                    Integer code = insert > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
                    String msg = insert > 0  ? "发布成功" : "发布失败";
                    return new Result(code, msg, "");
            }else{
                return new Result(Code.SYNTAX_ERROR, "发布项目时信息不能为空，请重新填写", "");
            }
        }




    //查询所有项目
    @Override
    public Result CrowselectAll() {
        List<crow> crows = crowMapper.selectList(null);
        Integer code = crows != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = crows != null ? "查询成功" : "查询失败";
        return new Result(code, msg, crows);
    }

    //根据id删除项目
    @Override
    public Result Crowdelete(Integer id) {
        int delete = crowMapper.deleteById(id);
        Integer code = delete>0? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = delete >0 ? "删除成功" : "删除失败";
        return new Result(code, msg, "");
    }

    //修改项目
    @Override
    public Result Crowupdate(crow crow) {
        ArrayList list1=new ArrayList();
        Class class1 = crow.getClass();
        Field[] des = class1.getDeclaredFields();
        for (Field de : des) {
            try {
                PropertyDescriptor pd1 = new PropertyDescriptor(de.getName(), class1);
                //获得get方法
                Method getMethod1 = pd1.getReadMethod();
                //执行get方法返回一个Object
                Object obj1 = getMethod1.invoke(crow);
                list1.add(obj1);
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
        }

        list1.removeAll(Collections.singleton(null));

        if(list1.size()>=12){

                int update = crowMapper.updateById(crow);
                Integer code = update>0? Code.NORMAL : Code.SYNTAX_ERROR;
                String msg = update >0 ? "修改成功" : "修改失败";
                return new Result(code, msg, "");


        }
        else {
            return new Result( Code.SYNTAX_ERROR,"修改项目时信息不能为空，请重新填写","");
        }

    }


}
