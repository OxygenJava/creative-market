package com.creative.controller;

import com.creative.domain.likepost;
import com.creative.domain.post;
import com.creative.dto.Result;
import com.creative.service.likepostService;
import com.creative.service.postService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like/post")
public class likepostController {
    @Autowired
    private likepostService likepostService;

    @PutMapping("/click")
    public Result ClickLikes(@RequestBody likepost likepost){
        Result result = likepostService.ClickLikepost(likepost);
        return result;
    }

    @PutMapping("/cancel")
    public Result CancelLikes(@RequestBody likepost likepost){
        Result result = likepostService.CancelLikepost(likepost);
        return result;
    }

    @GetMapping("/{id}")
    public Result selectPostlikes(@PathVariable Integer id){
        Result result = likepostService.selectLikepost(id);
        return result;
    }
}
