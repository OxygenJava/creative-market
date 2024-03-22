package com.creative.service.impl;

import com.creative.domain.post;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.postMapper;
import com.creative.service.postService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class postServiceImpl implements postService {

    @Autowired
    private postMapper postMapper;

    @Override
    public Result insertPost(post post) {
        if(post.getTitle()!=null && post.getBody()!=null && post.getImage()!=null
        && post.getLableId()!=null && post.getUid()!=null && post.getCreativeTime()!=null){
            int insert = postMapper.insert(post);
            Integer code = insert > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = insert > 0 ? "添加成功" : "添加失败";
            return new Result(code, msg, "");
        }
        else {
            return new Result(Code.SYNTAX_ERROR, "帖子的信息填写不完整", "");
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
        if(post.getTitle()!=null && post.getBody()!=null && post.getImage()!=null
                && post.getLableId()!=null && post.getUid()!=null && post.getCreativeTime()!=null){
            int update = postMapper.updateById(post);
            Integer code = update > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 ? "修改成功" : "修改失败";
            return new Result(code, msg, "");
        }
        else {
            return new Result(Code.SYNTAX_ERROR, "修改的帖子的信息填写不完整", "");
        }
    }

    @Override
    public Result selectPostAll() {
        List<post> posts = postMapper.selectList(null);
        Integer code = posts != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = posts != null ? "查询成功" : "查询失败";
        return new Result(code, msg, posts);
    }
}
