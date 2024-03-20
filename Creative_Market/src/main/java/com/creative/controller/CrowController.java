package com.creative.controller;

import com.creative.domain.crow;
import com.creative.dto.Result;
import com.creative.service.impl.CrowServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crow")
@CrossOrigin
public class CrowController {
    @Autowired
    private CrowServiceImpl crowImpl;

    //发布项目（插入）
    @PostMapping
    public Result Crowinsert(@RequestBody crow crow) {
        Result insert = crowImpl.Crowinsert(crow);
        return insert;
    }

    //查询所有项目
    @GetMapping
    public Result CrowselectAll(){
        Result result = crowImpl.CrowselectAll();
        return result;
    }

    //删除项目
    @DeleteMapping("/{id}")
    public Result Crowdelete(@PathVariable Integer id){
        Result crowdelete = crowImpl.Crowdelete(id);
        return crowdelete;
    }

    //修改项目
    @PutMapping
    public Result Crowupdate(@RequestBody crow crow){
        Result crowupdate = crowImpl.Crowupdate(crow);
        return crowupdate;
    }
}
