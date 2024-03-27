package com.creative.controller;

import com.creative.dto.Result;
import com.creative.dto.userSearchDTO;
import com.creative.service.userSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/userSearch")
@CrossOrigin
public class userSearchController {
    @Autowired
    private userSearchService userSearchService;

    @PostMapping("/getSearchInfo")
    public Result getSearchInfo(@RequestBody userSearchDTO userSearch) throws IOException {
        return userSearchService.getSearchInfo(userSearch);
    }
}
