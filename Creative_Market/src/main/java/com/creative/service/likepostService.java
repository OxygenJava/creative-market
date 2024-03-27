package com.creative.service;

import com.creative.domain.likepost;
import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface likepostService {
    Result ClickLikepost(likepost likepost, HttpServletRequest request);
    Result CancelLikepost(likepost likepost,HttpServletRequest request);
    Result selectLikepost(HttpServletRequest request);
}
