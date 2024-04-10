package com.creative.service;

import com.creative.domain.collectioncommodity;
import com.creative.domain.collectionpost;
import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface collectioncommodityService {
    Result ClickCollectioncommodity(collectioncommodity collectioncommodity, HttpServletRequest request);
    Result CancelCollectioncommodity(collectioncommodity collectioncommodity, HttpServletRequest request);
    Result selectCollectioncommodity(HttpServletRequest request);
}
