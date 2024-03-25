package com.creative.service;

import com.creative.domain.collectionpost;
import com.creative.dto.Result;

public interface collectionpostService {
    Result ClickPostcoll(collectionpost collectionpost);
    Result CancelPostcoll(collectionpost collectionpost);
    Result selectPostcoll(Integer id);
}
