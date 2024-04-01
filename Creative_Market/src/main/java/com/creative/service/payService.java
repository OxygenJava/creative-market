package com.creative.service;


import com.creative.domain.pay;
import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface payService {
    Result payAdd(pay pay, HttpServletRequest request);


}
