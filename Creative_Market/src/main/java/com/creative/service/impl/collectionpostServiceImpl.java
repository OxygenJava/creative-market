package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.collectionpost;
import com.creative.domain.likepost;
import com.creative.domain.post;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.collectionpostMapper;
import com.creative.mapper.likepostMapper;
import com.creative.mapper.postMapper;
import com.creative.service.collectionpostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class collectionpostServiceImpl implements collectionpostService {

    @Autowired
    private postMapper postMapper;

    @Autowired
    private collectionpostMapper collectionpostMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Result ClickCollectionpost(collectionpost collectionpost) {
//                String authorization = request.getHeader("Authorization");
//        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
//        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
//        collectionpost.setUid(user.getId());

        if(collectionpost.getUid()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        else {
            post post = postMapper.selectById(collectionpost.getPid());
            post.setCollection(post.getCollection()+1);
            post.setCollectionState(1);
            int update = postMapper.updateById(post);
            int insert = collectionpostMapper.insert(collectionpost);
            Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 && insert>0? "收藏成功" : "收藏失败";
            return new Result(code, msg, "");
        }

    }

    @Override
    public Result CancelCollectionpost(collectionpost collectionpost) {
//                String authorization = request.getHeader("Authorization");
//        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
//        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
//        collectionpost.setUid(user.getId());

        if(collectionpost.getUid()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }else {
            post post = postMapper.selectById(collectionpost.getPid());
            post.setCollection(post.getCollection()-1);
            post.setCollectionState(0);
            int update = postMapper.updateById(post);
            int insert = collectionpostMapper.deleteBycollpost(collectionpost);
            Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 && insert>0? "取消收藏成功" : "取消收藏失败";
            return new Result(code, msg, "");
        }

    }

    @Override
    public Result selectCollectionpost(Integer id)
    {
//                String authorization = request.getHeader("Authorization");
//        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
//        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
//        collectionpost.setUid(user.getId());


        LambdaQueryWrapper<collectionpost> lqw=new LambdaQueryWrapper<>();
        lqw.eq(collectionpost::getUid,id);
        List<collectionpost> collectionposts = collectionpostMapper.selectList(lqw);
        ArrayList<Integer> list1=new ArrayList<>();
        ArrayList<Integer> list2=new ArrayList<>();
        ArrayList<post> list=new ArrayList<>();
        if(collectionposts==null){
            List<post> posts1 = postMapper.selectList(null);
            for (int i = 0; i < posts1.size(); i++) {
                posts1.get(i).setCollectionState(0);
            }
            Integer code = posts1 !=null ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = posts1 !=null? "查询成功" : "查询失败";
            return new Result(code, msg, posts1);
        }
        else {
            for (int i = 0; i < collectionposts.size(); i++) {
                list1.add(collectionposts.get(i).getPid());
            }
            List<post> posts2=new ArrayList<>();
            for (int i = 0; i < list1.size(); i++) {
                LambdaQueryWrapper<post> lqw1=new LambdaQueryWrapper<>();
                lqw1.eq(post::getId,list1.get(i));
                post post = postMapper.selectOne(lqw1);
                posts2.add(post);
            }
            if(posts2!=null){
                for (int i = 0; i < posts2.size(); i++) {
                    posts2.get(i).setCollectionState(1);
                }
            }

            List<post> posts3=postMapper.selectList(null);
            for (int i = 0; i < posts3.size(); i++) {
                list2.add(posts3.get(i).getId());
            }

            list2.removeAll(list1);

            List<post> posts4=new ArrayList<>();
            for (int i = 0; i < list2.size(); i++) {
                LambdaQueryWrapper<post> lqw2=new LambdaQueryWrapper<>();
                lqw2.eq(post::getId,list2.get(i));
                post post = postMapper.selectOne(lqw2);
                posts4.add(post);
            }
            if(posts4!=null){
                for (int i = 0; i < posts4.size(); i++) {
                    posts4.get(i).setCollectionState(0);
                }
            }

            list.addAll(posts2);
            list.addAll(posts4);
        }

        Integer code = list !=null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = list !=null? "查询成功" : "查询失败";
        return new Result(code, msg, list);
    }
}
