package com.creative.controller;

import com.creative.domain.discovered;
import com.creative.dto.Result;
import com.creative.service.discoverService;
import com.creative.service.discoveredCollectionService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/discover")
@CrossOrigin
public class discoverController {
    @Autowired
    private discoverService discoverService;
    /**
     * 上传
     * @param file
     * @param disc
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadDiscover")
    public Result uploadDiscover(MultipartFile[] file, discovered disc, HttpServletRequest request) throws IOException {
        return discoverService.uploadDiscover(file,disc,request);
    }

    /**
     * 获取发现中所有数据
     * @return
     */
    @GetMapping("/{pageSize}/{pageNumber}")
    public Result getAllDiscover(@PathVariable int pageSize,@PathVariable int pageNumber) throws IOException {
        return discoverService.getAllDiscover(pageSize,pageNumber);
    }

    /**
     * 用户点赞
     * @return
     */
    @PutMapping("/discoveredLike")
    public Result discoveredLike(Integer discoveredId,HttpServletRequest request){
        return discoverService.discoveredLike(request,discoveredId);
    }

    /**
     * 用户取消点赞
     * @param request
     * @param discoveredId
     * @return
     */
    @DeleteMapping("/discoveredLike")
    public Result cancelDiscoveredLike(HttpServletRequest request,Integer discoveredId){
        return discoverService.cancelDiscoveredLike(request,discoveredId);
    }

    /**
     * 用户收藏
     * @param discoveredId
     * @param request
     * @return
     */
    @PutMapping("/discoveredCollection")
    public Result discoveredCollection(Integer discoveredId,HttpServletRequest request){
        return discoverService.discoveredCollection(discoveredId,request);
    }
}
