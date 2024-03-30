package com.creative.service;

import com.creative.domain.orderTable;
import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface orderService {
    Result orderAdd(orderTable orderTable, HttpServletRequest request);

    Result orderSelectByUserId(HttpServletRequest request);

    Result orderSelectOneByOrderId(Integer orderId);

}
