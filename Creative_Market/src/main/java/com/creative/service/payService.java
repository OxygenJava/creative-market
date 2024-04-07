package com.creative.service;


import com.creative.domain.pay;
import com.creative.dto.Result;
import com.creative.dto.payDTO;

import javax.servlet.http.HttpServletRequest;

public interface payService {
    Result payAdd(pay pay, HttpServletRequest request);

    Result paySelect(Integer commodityId,HttpServletRequest request);
}
