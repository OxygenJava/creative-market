package com.creative.service;

import com.creative.domain.likepost;
import com.creative.domain.post;
import com.creative.dto.Result;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface postService {

    /**
     * 上传
     * @param file
     * @param post
     * @param request
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    Result uploadDiscover(MultipartFile[] file, post post, HttpServletRequest request) throws IOException;

    /**
     * 分页获取发现中内容
     * @param pageSize
     * @param pageNumber
     * @return
     */
    Result getAllDiscover(int pageSize, int pageNumber,HttpServletRequest request) throws IOException;

    Result deletePost(Integer id);
    Result updatePost(post post);
    Result selectPostAll();
    Result selectByUidAllPost(HttpServletRequest request);


    /**
     * 获取帖子详情
     * @param postId
     * @return
     */
    Result getPostDetail(Integer postId);
}
