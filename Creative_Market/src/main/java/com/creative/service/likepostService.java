package com.creative.service;

import com.creative.domain.likepost;
import com.creative.dto.Result;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

public interface likepostService {
    @Transactional(rollbackFor = Exception.class)
    Result ClickLikepost(likepost likepost, HttpServletRequest request);
    Result CancelLikepost(likepost likepost,HttpServletRequest request);
    Result selectAllpost(HttpServletRequest request);
    Result selectLikepost(HttpServletRequest request);
}
