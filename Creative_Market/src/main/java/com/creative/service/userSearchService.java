package com.creative.service;

import com.creative.dto.Result;
import com.creative.dto.userSearchDTO;

import java.io.IOException;

public interface userSearchService {
    Result getSearchInfo(userSearchDTO userSearch) throws IOException;
}
