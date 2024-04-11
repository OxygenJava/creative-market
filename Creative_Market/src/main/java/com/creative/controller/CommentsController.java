package com.creative.controller;

import com.creative.dto.Result;
import com.creative.dto.childCommentsDTO;
import com.creative.dto.fatherCommentsDTO;
import com.creative.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/controller")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    /**
     * 发表父级评论
     * @param fatherCommentsDTO
     * @return
     */
    @PostMapping("/publicationFatherComment")
    public Result publicationFatherComment(@RequestBody fatherCommentsDTO fatherCommentsDTO){
        return commentsService.publicationFatherComment(fatherCommentsDTO);
    }

    /**
     * 发表子级评论
     * @param childCommentsDTO
     * @return
     */
    @PostMapping("/publicationChildComment")
    public Result publicationChildComment(@RequestBody childCommentsDTO childCommentsDTO){
        return commentsService.publicationChildComment(childCommentsDTO);
    }

    /**
     * 分页查询评论
     * @param pageSize
     * @param pageNumber
     * @return
     */
    @GetMapping("/getCommentByPage/{pageSize}/{pageNumber}")
    public Result getCommentByPage(@PathVariable Integer pageSize,@PathVariable Integer pageNumber){
        return commentsService.getCommentByPage(pageSize,pageNumber);
    }
}
