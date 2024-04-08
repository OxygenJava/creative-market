package com.creative.controller;

import com.creative.domain.likepost;
import com.creative.domain.post;
import com.creative.dto.Result;
import com.creative.service.postService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/post")
@CrossOrigin
public class postController {

    @Autowired
    private postService postService;

    //发布帖子
    @PostMapping
    public Result insertPost(@RequestBody  post post, HttpServletRequest request){
        Result result = postService.insertPost(post,request);
        return result;
    }

    //删除帖子
    @DeleteMapping("/{id}")
    public Result deletePost(@PathVariable Integer id){
        Result result = postService.deletePost(id);
        return result;
    }

    //修改帖子
    @PutMapping
    public Result updatePost(@RequestBody  post post){
        Result result = postService.updatePost(post);
        return result;
    }

    //根据用户id查询发布过的帖子
    @GetMapping
    public Result selectByUidAllPost(HttpServletRequest request){
        Result result = postService.selectByUidAllPost(request);
        return result;
    }


}
