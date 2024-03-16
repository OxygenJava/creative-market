package com.creative.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.creative.dto.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/common")
@CrossOrigin
public class commonController {

    @Value("${creativeMarket.filePath}")
    private String basePath;

    @PostMapping("/upload")
    public Result upload(MultipartFile file, HttpServletResponse response) throws IOException {
        file.transferTo(new File(basePath+"hello.jpg"));
        FileInputStream fileInputStream = new FileInputStream(new File(basePath+"hello.jpg"));
        ServletOutputStream outputStream = response.getOutputStream();
//        response.setContentType("image/jpeg");
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = fileInputStream.read(bytes)) != -1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        outputStream.close();
        fileInputStream.close();
        return Result.success("操作成功","hello.jpg");
    }
}
