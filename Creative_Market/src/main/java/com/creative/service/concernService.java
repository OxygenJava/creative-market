package com.creative.service;

import com.creative.domain.concern;
import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface concernService {
    Result concernPerson(concern concern, HttpServletRequest request);
    Result cancelConcern(concern concern,HttpServletRequest request);
    Result countConcern(HttpServletRequest request);
}
