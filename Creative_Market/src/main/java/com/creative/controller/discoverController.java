package com.creative.controller;

import com.creative.domain.discovered;
import com.creative.dto.Result;
import com.creative.service.discoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/discover")
public class discoverController {
    @Autowired
    private discoverService discoverService;

    @PostMapping("/uploadDiscover")
    public Result uploadDiscover(@RequestParam("file")MultipartFile[] file, discovered disc, HttpServletRequest request){
        return discoverService.uploadDiscover(file,disc,request);
    }

}
