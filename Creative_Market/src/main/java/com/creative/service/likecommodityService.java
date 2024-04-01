package com.creative.service;

import com.creative.domain.likecommodity;
import com.creative.domain.likepost;
import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface likecommodityService {
    Result ClickLikecommodity(likecommodity likecommodity, HttpServletRequest request);
    Result CancelLikecommodity(likecommodity likecommodity, HttpServletRequest request);
    Result selectAllcommodity(HttpServletRequest request);
    Result selectLikecommodity(HttpServletRequest request);
}
