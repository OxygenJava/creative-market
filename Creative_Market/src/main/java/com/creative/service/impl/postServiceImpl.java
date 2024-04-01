package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.likepost;
import com.creative.domain.post;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.likepostMapper;
import com.creative.mapper.postMapper;
import com.creative.service.postService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class postServiceImpl implements postService {

    @Autowired
    private postMapper postMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Result insertPost(post post, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        post.setUid(user.getId());

            if(post.getUid()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
            }
            else if(post.getTitle()==null){
                return new Result(Code.SYNTAX_ERROR, "帖子的标题不能为空", "");
            }
            else if(post.getBody()==null){
                return new Result(Code.SYNTAX_ERROR, "帖子的正文不能为空", "");
            }
            else if(post.getLableId()==null){
                return new Result(Code.SYNTAX_ERROR, "帖子的标签不能为空", "");
            }
            else if(post.getCreateTime()==null){
                return new Result(Code.SYNTAX_ERROR, "帖子的发布时间不能为空", "");
            }
            else {
                int insert = postMapper.insert(post);
                post.setLikes(0);
                post.setLikesState(0);
                post.setCollection(0);
                post.setCollectionState(0);
                int update = postMapper.updateById(post);
                Integer code = insert > 0 && update >0? Code.NORMAL : Code.SYNTAX_ERROR;
                String msg = insert > 0 && update >0? "发布成功" : "发布失败";
                return new Result(code, msg, "");
            }


    }

    @Override
    public Result deletePost(Integer id) {
        int delete = postMapper.deleteById(id);
        Integer code = delete > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = delete > 0 ? "删除成功" : "删除失败";
        return new Result(code, msg, "");
    }

    @Override
    public Result updatePost(post post) {
            int update = postMapper.updateById(post);
            Integer code = update > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 ? "修改成功" : "修改失败";
            return new Result(code, msg, "");
        }


    @Override
    public Result selectPostAll() {
        List<post> posts = postMapper.selectList(null);
        Integer code = posts != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = posts != null ? "查询成功" : "查询失败";
        return new Result(code, msg, posts);
    }

    @Override
    public Result selectByUidAllPost(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        LambdaQueryWrapper<post> lqw=new LambdaQueryWrapper<>();
        lqw.eq(post::getUid,user.getId());
        List<post> posts = postMapper.selectList(lqw);
        Integer code = posts != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = posts != null ? "查询成功" : "查询失败";
        return new Result(code, msg, posts);
    }


}
