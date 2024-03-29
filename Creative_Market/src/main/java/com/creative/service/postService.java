package com.creative.service;

import com.creative.domain.likepost;
import com.creative.domain.post;
import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface postService {

    Result insertPost(post post, HttpServletRequest request);
    Result deletePost(Integer id);
    Result updatePost(post post);
    Result selectPostAll();
    Result selectByUidAllPost(HttpServletRequest request);


}
