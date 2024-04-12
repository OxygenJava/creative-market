package com.creative.service;

import com.creative.dto.Result;
import com.creative.dto.childCommentsDTO;
import com.creative.dto.fatherCommentsDTO;
import org.springframework.web.bind.annotation.PathVariable;

public interface CommentsService {

    /**
     * 发表父级评论
     * @param fatherCommentsDTO
     * @return
     */
    Result publicationFatherComment(fatherCommentsDTO fatherCommentsDTO);

    /**
     * 发表子级评论
     * @param childCommentsDTO
     * @return
     */
    Result publicationChildComment(childCommentsDTO childCommentsDTO);

    /**
     * 分页查询评论
     * @param pageSize
     * @param pageNumber
     * @param postId
     * @return
     */
    Result getCommentByPage(Integer pageSize, Integer pageNumber,Integer postId);

    /**
     * 获取该帖子的父级标签总数
     * @param postId
     * @return
     */
    Result getTotalNumber(Integer postId);
}
