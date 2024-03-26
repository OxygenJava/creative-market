package com.creative.service;

import com.creative.domain.collectionpost;
import com.creative.dto.Result;

public interface collectionpostService {
    Result ClickCollectionpost(collectionpost collectionpost);
    Result CancelCollectionpost(collectionpost collectionpost);
    Result selectCollectionpost(Integer id);
}
