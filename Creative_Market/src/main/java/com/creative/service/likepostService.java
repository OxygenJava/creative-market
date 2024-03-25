package com.creative.service;

import com.creative.domain.likepost;
import com.creative.dto.Result;

public interface likepostService {
    Result ClickLikepost(likepost likepost);
    Result CancelLikepost(likepost likepost);
    Result selectLikepost(Integer id);
}
