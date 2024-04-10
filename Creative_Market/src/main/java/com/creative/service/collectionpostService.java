package com.creative.service;

import com.creative.domain.collectionpost;
import com.creative.dto.Result;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

public interface collectionpostService {
    @Transactional(rollbackFor = Exception.class)
    Result ClickCollectionpost(Integer postId, HttpServletRequest request);
    @Transactional(rollbackFor = Exception.class)
    Result CancelCollectionpost(Integer postId,HttpServletRequest request);
    Result selectCollectionpost(HttpServletRequest request);
}
