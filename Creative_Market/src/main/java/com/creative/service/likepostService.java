package com.creative.service;

import com.creative.domain.likepost;
import com.creative.dto.Result;

public interface likepostService {
    Result ClickPostlikes(likepost likepost);
    Result CancelPostlikes(likepost likepost);
    Result selectPostlikes(Integer id);
}
