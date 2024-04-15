package com.creative.service;

import com.creative.domain.orderTable;
import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface orderService {
    Result orderAdd(orderTable orderTable, HttpServletRequest request);

    Result orderSelectByUserId(HttpServletRequest request);

    Result orderSelectOneByOrderId(Integer orderId);

    Result orderUpdateById(orderTable orderTable);

    Result orderPay(Integer orderId,HttpServletRequest request);

    Result orderDelete(Integer orderId);

    Result paySelect(Integer commodityId,Integer buyTypeId,HttpServletRequest request);

}
