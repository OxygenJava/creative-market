package com.creative.service;

import com.creative.domain.addressInfo;
import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface addressInfoService {
    Result addressInfoAdd(addressInfo addressInfo, HttpServletRequest request);
    Result addressInfoSelectAllByUserId(HttpServletRequest request);
    Result addressInfoUpdate(addressInfo addressInfo);
    Result addressInfoDeleteById(Integer id);
    Result addressInfoSelectOneByAddresseeId(Integer addresseeId);
    Result addressInfoUpdateState(Integer updateId);
    Result addressInfoCancelState(Integer cancelId,HttpServletRequest request);

}
