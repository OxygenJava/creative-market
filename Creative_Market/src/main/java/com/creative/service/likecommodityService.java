package com.creative.service;

import com.creative.domain.likecommodity;
import com.creative.domain.likepost;
import com.creative.dto.Result;

public interface likecommodityService {
    Result ClickLikecommodity(likecommodity likecommodity);
    Result CancelLikecommodity(likecommodity likecommodity);
    Result selectLikecommodity(Integer id);
}
