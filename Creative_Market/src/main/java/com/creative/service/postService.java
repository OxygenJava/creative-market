package com.creative.service;

import com.creative.domain.post;
import com.creative.dto.Result;

public interface postService {

    Result insertPost(post post);
    Result deletePost(Integer id);
    Result updatePost(post post);
    Result selectPostAll();

}
