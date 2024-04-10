package com.creative.controller;

import com.creative.domain.likepost;
import com.creative.domain.post;
import com.creative.dto.Result;
import com.creative.service.postService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/post")
@CrossOrigin
public class postController {

    @Autowired
    private postService postService;


    /**
     * 上传
     * @param file
     * @param post
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadDiscover")
    public Result uploadDiscover(MultipartFile[] file, post post, HttpServletRequest request) throws IOException {
        return postService.uploadDiscover(file,post,request);
    }

    /**
     * 获取发现中所有数据
     * @return
     */
    @GetMapping("/{pageSize}/{pageNumber}")
    public Result getAllDiscover(@PathVariable int pageSize,@PathVariable int pageNumber,HttpServletRequest request) throws IOException {
        return postService.getAllDiscover(pageSize,pageNumber,request);
    }

    /**
     * 获取帖子详情
     * @param postId
     * @return
     */
    @GetMapping("/getPostDetail/{postId}")
    public Result getPostDetail(@PathVariable Integer postId){
        return postService.getPostDetail(postId);
    }

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
