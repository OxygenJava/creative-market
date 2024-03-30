package com.creative.service;

import com.creative.domain.collectionpost;
import com.creative.dto.Result;

import javax.servlet.http.HttpServletRequest;

public interface collectionpostService {
    Result ClickCollectionpost(collectionpost collectionpost, HttpServletRequest request);
    Result CancelCollectionpost(collectionpost collectionpost,HttpServletRequest request);
    Result selectCollectionpost(HttpServletRequest request);
    Result selectAllpost(HttpServletRequest request);
}
