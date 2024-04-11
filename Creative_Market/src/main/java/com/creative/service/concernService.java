package com.creative.service;

import com.creative.domain.concern;
import com.creative.dto.Result;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

public interface concernService {
    @Transactional(rollbackFor = Exception.class)
    Result concernPerson(concern concern, HttpServletRequest request);
    @Transactional(rollbackFor = Exception.class)
    Result cancelConcern(concern concern,HttpServletRequest request);
    Result ifconcern(Integer uid,HttpServletRequest request);
    Result ObtainFans(Integer pageSize,Integer pageNumber,HttpServletRequest request);
    Result ObtainFocus(Integer pageSize,Integer pageNumber,HttpServletRequest request);
    Result selectLikeUser(String name);
    Result selectFansTotal(HttpServletRequest request);
    Result selectFocusTotal(HttpServletRequest request);
}
