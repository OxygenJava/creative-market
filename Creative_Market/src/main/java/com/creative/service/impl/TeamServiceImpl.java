package com.creative.service.impl;

import com.creative.domain.team;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.TeamMapper;
import com.creative.mapper.userMapper;
import com.creative.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private userMapper userMapper;


    //根据团队id查询所有成员（user）的消息
    @Override
    public Result selectTeamAll(Integer id) {
        team team = teamMapper.selectById(id);
        ArrayList list=new ArrayList();

        Class class1 = team.class;
        Field[] des = class1.getDeclaredFields();
        for (Field de : des) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(de.getName(), class1);
                //获得get方法
                Method getMethod = pd.getReadMethod();
                //执行get方法返回一个Object
                Object obj = getMethod.invoke(team);
                list.add(obj);
            }catch (Exception e){
                System.out.println(e.getStackTrace());
            }

        }
        list.removeAll(Collections.singleton(null));
        list.remove(0);
        List userList = userMapper.selectBatchIds(list);
        Integer code = userList != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = userList != null ? "查询成功" : "查询失败";
        return new Result(code,msg,userList);
    }
}
