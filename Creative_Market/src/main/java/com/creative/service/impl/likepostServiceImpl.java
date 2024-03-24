package com.creative.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.likepost;
import com.creative.domain.post;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.likepostMapper;
import com.creative.mapper.postMapper;
import com.creative.service.likepostService;
import com.mysql.cj.xdevapi.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class likepostServiceImpl implements likepostService {

    @Autowired
    private postMapper postMapper;

    @Autowired
    private likepostMapper likepostMapper;

    @Override
    public Result ClickPostlikes(likepost likepost) {
        post post = postMapper.selectById(likepost.getPid());
        if(post.getLikes()==null){
            post.setLikes(0);
        }
        post.setLikes(post.getLikes()+1);
        post.setLikesState(1);
        int update = postMapper.updateById(post);

        int insert = likepostMapper.insert(likepost);

        Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = update > 0 && insert>0? "点赞成功" : "点赞失败";
        return new Result(code, msg, "");
    }

    @Override
    public Result CancelPostlikes(likepost likepost) {
        post post = postMapper.selectById(likepost.getPid());
        if(post.getLikes()==null){
            post.setLikes(0);
        }
        post.setLikes(post.getLikes()-1);
        post.setLikesState(0);
        int update = postMapper.updateById(post);

        int delete = likepostMapper.deleteBylikepost(likepost);

        Integer code = update > 0 && delete>0? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = update > 0 && delete>0? "取消成功" : "取消失败";
        return new Result(code, msg, "");
    }

    @Override
    public Result selectPostlikes(Integer id) {
        LambdaQueryWrapper<likepost> lqw=new LambdaQueryWrapper<>();
        lqw.eq(likepost::getUid,id);
        List<likepost> likeposts = likepostMapper.selectList(lqw);
        ArrayList<Integer> list1=new ArrayList<>();
        ArrayList<Integer> list2=new ArrayList<>();
        int update=-1;
       if(likeposts==null){
           List<post> posts1 = postMapper.selectList(null);
           for (int i = 0; i < posts1.size(); i++) {
               posts1.get(i).setLikesState(0);
                update = postMapper.updateById(posts1.get(i));
           }
           Integer code = posts1 !=null && update>0? Code.NORMAL : Code.SYNTAX_ERROR;
           String msg = posts1 !=null && update>0? "查询成功" : "查询失败";
           return new Result(code, msg, posts1);
       }
           else{

           for (int i = 0; i < likeposts.size(); i++) {
               list1.add(likeposts.get(i).getPid());
           }
           List<post> posts1=new ArrayList<>();
           for (int i = 0; i < list1.size(); i++) {
               LambdaQueryWrapper<post> lqw1=new LambdaQueryWrapper<>();
               lqw1.eq(post::getId,list1.get(i));
               post post = postMapper.selectOne(lqw1);
               posts1.add(post);
           }
           if(posts1!=null){
               for (int i = 0; i < posts1.size(); i++) {
                   posts1.get(i).setLikesState(1);
                    update = postMapper.updateById(posts1.get(i));
               }
           }



           List<post> posts2=postMapper.selectList(null);
           for (int i = 0; i < posts2.size(); i++) {
               list2.add(posts2.get(i).getId());
           }

            list2.removeAll(list1);

           List<post> posts3=new ArrayList<>();
           for (int i = 0; i < list2.size(); i++) {
               LambdaQueryWrapper<post> lqw2=new LambdaQueryWrapper<>();
               lqw2.eq(post::getId,list2.get(i));
               post post = postMapper.selectOne(lqw2);
               posts3.add(post);
           }
           if(posts3!=null){
               for (int i = 0; i < posts3.size(); i++) {
                   posts3.get(i).setLikesState(0);
                   update = postMapper.updateById(posts3.get(i));
               }
           }
       }
            List<post> posts4=postMapper.selectList(null);
           Integer code = posts4 !=null && update>0? Code.NORMAL : Code.SYNTAX_ERROR;
           String msg = posts4 !=null && update>0? "查询成功" : "查询失败";
           return new Result(code, msg, posts4);
           }

       }



