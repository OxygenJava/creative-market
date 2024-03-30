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

    @PostMapping
    public Result insertPost(@RequestBody  post post, HttpServletRequest request){
        Result result = postService.insertPost(post,request);
        return result;
    }

    @DeleteMapping("/{id}")
    public Result deletePost(@PathVariable Integer id){
        Result result = postService.deletePost(id);
        return result;
    }

    @PutMapping
    public Result updatePost(@RequestBody  post post){
        Result result = postService.updatePost(post);
        return result;
    }

    @GetMapping
    public Result selectByUidAllPost(HttpServletRequest request){
        Result result = postService.selectByUidAllPost(request);
        return result;
    }


}
