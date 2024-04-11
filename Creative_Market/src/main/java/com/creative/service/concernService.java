package com.creative.service;

import com.creative.domain.concern;
import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface concernService {
    Result concernPerson(concern concern, HttpServletRequest request);
    Result cancelConcern(concern concern,HttpServletRequest request);
    Result ifconcern(Integer uid,HttpServletRequest request);
    Result ObtainFans(Integer pageSize,Integer pageNumber,HttpServletRequest request);
    Result ObtainFocus(Integer pageSize,Integer pageNumber,HttpServletRequest request);
    Result selectLikeFocus(String name,HttpServletRequest request);
    Result selectLikeFans(String name,HttpServletRequest request);
    Result selectFansTotal(HttpServletRequest request);
    Result selectFocusTotal(HttpServletRequest request);
    Result cancelFans(concern concern,HttpServletRequest request);
}
