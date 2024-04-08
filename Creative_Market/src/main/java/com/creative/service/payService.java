package com.creative.service;


import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface payService {

    Result paySelect(Integer commodityId,Integer buyTypeId,HttpServletRequest request);
}
