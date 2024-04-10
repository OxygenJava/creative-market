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
     * @return
     */
    Result getCommentByPage(Integer pageSize, Integer pageNumber);
}
