package com.creative.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.creative.domain.team;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.CrowMapper;
import com.creative.mapper.TeamMapper;
import com.creative.mapper.userMapper;
import com.creative.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private userMapper userMapper;



    //根据团队id查询所有成员（user）的信息
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

    //删除某个团队成员
    @Override
    public Result updateTeam(team team) {
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", team.getId());

        updateWrapper.set("uid"+String.valueOf(team.getUidlocation()), null);

        int update = teamMapper.update(null, updateWrapper);
        Integer code = update>0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = update>0 ? "删除成功" : "删除失败";
        return new Result(code,msg,"");
    }

    //添加团队
    @Override
    public Result insertTeam() {
        int insert = teamMapper.insertTeam();
        Integer code = insert>0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = insert>0 ? "添加成功" : "添加失败";
        return new Result(code,msg,"");
    }

    //根据团队id删除团队
    @Override
    public Result deleteTeam(Integer id) {
        int delete = teamMapper.deleteById(id);
        Integer code = delete>0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = delete>0 ? "删除成功" : "删除失败";
        return new Result(code,msg,"");
    }

    //查询所有团队
    @Override
    public Result selectTeam() {
        List<team> teams = teamMapper.selectList(null);
        Integer code = teams != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = teams != null ? "查询成功" : "查询失败";
        return new Result(code,msg,teams);
    }

    //在团队里添加新的成员
    @Override
    public Result insertTeamUser(team team) {
        Integer s=-1;

        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("id", team.getId());

        ArrayList list1=new ArrayList();
        team teams = teamMapper.selectById(team.getId());

        if(teams==null){
            return new Result(Code.SYNTAX_ERROR,"没有该团队","");
        }
        else {
            Class class2 = teams.getClass();
            Field[] des = class2.getDeclaredFields();
            for (Field de : des) {
                try {
                    PropertyDescriptor pd1 = new PropertyDescriptor(de.getName(), class2);
                    //获得get方法
                    Method getMethod1 = pd1.getReadMethod();
                    //执行get方法返回一个Object
                    Object obj1 = getMethod1.invoke(teams);
                    list1.add(obj1);
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }

            }

            list1.removeAll(Collections.singleton(null));

            list1.remove(0);

            if (list1.size() == 0) {
              s=0;
            }

                for (int i = 0; i < list1.size(); i++) {
                    if (list1.get(i)==team.getUid()) {
                        s=1;
                        break;
                    } else {
                       s=0;
                    }
                }



                if (s==1) {
                    return new Result(Code.SYNTAX_ERROR, "该成员已经在团队里了", "");
                } else {

                    updateWrapper.set("uid" + String.valueOf(team.getUidlocation()), team.getUid());
                    int update = teamMapper.update(null, updateWrapper);
                    Integer code = update > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
                    String msg = update > 0 ? "添加新成员成功" : "添加新成员失败";
                    return new Result(code, msg, "");
                }

            }
        }
    }



