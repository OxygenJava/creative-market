package com.creative.controller;

import com.creative.domain.commodity;
import com.creative.dto.Result;
import com.creative.dto.commodityDTO;
import com.creative.service.commodityDetailsImageService;
import com.creative.service.commodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/commodity")
@CrossOrigin
public class commodityController {
    @Autowired
    private commodityService commodityService;
    @Autowired
    private commodityDetailsImageService commodityDetailsImageService;

    @GetMapping("/{id}")
    public Result selectCommodityById(@PathVariable Integer id, HttpServletRequest request) throws IOException {
        return commodityService.selectCommodityById(id,request);
    }

    /**
     * 获取商品详情
     * @param commodityId
     * @return
     */
    @GetMapping("/getDetailsImage/{commodityId}")
    public Result getDetailsImage(@PathVariable Integer commodityId){
        return commodityDetailsImageService.getAllByCommodityId(commodityId);
    }

    //发布
    @PostMapping("/publishCommodity")
    public Result insertCom(MultipartFile[] file,commodityDTO commodityDTO, HttpServletRequest request) throws IOException {
        return commodityService.insertCom(file, commodityDTO,request);
    }

    //删除
    @DeleteMapping("/{id}")
    public Result deleteCom(@PathVariable Integer id){
        Result result = commodityService.deleteCom(id);
        return result;
    }

    //修改
    @PutMapping
    public Result updateCom(@RequestBody commodity commodity){
        Result result = commodityService.updateCom(commodity);
        return result;
    }

    //查询所有
    @GetMapping
    public Result selectComAll(){
        Result result = commodityService.selectComAll();
        return result;
    }

    //根据商品id查询其所有标签
    @GetMapping("/lable/{id}")
    public Result selectComLable(@PathVariable Integer id){
        Result result = commodityService.selectComLable(id);
        return result;
    }

    //根据商品id查询其所有团队成员
    @GetMapping("/team/{id}")
    public Result selectComTeam(@PathVariable Integer id){
        Result result = commodityService.selectComTeam(id);
        return result;
    }

    //根据用户id查询该用户发布过的商品
    @GetMapping("/insert")
    public Result selectByUidAllCommodity(HttpServletRequest request){
        Result result = commodityService.selectByUidAllCommodity(request);
        return result;
    }

}
