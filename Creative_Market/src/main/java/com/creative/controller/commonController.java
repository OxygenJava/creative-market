package com.creative.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.creative.dto.Code;
import com.creative.dto.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/common")
@CrossOrigin(origins = "http://localhost:8080")

public class commonController {

    @Value("${creativeMarket.filePath}")
    private String basePath;

    @PostMapping("/upload")
    public Result upload(MultipartFile file, HttpServletResponse response) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null){
            return Result.fail(Code.SYNTAX_ERROR,"传输的照片不能为空");
        }
        //图片后缀
        String lastName = originalFilename.substring(originalFilename.lastIndexOf("."));
        File baseFile = new File(basePath);
        if (!baseFile.exists()){
            baseFile.mkdirs();
        }
        String imageName = UUID.randomUUID().toString();
        file.transferTo(new File(baseFile,imageName+lastName));
        return Result.success("操作成功");
    }

    //使用base64格式的图片在前端显示需要加上：“data:image/jpeg;base64,”
    @PostMapping("/getSwiperImage")
    public Result getSwiperImage() throws IOException {
        // 假设imagePaths为存储图片路径的数组
        String[] imagePaths = {"D:\\img\\hello1.png", "D:\\img\\hello2.png"};

        List<String> base64ImageList = new ArrayList<>();
        for (int i = 0; i < imagePaths.length; i++) {
            String imagePath = imagePaths[i];
            String base64Image = encodeImageToBase64(imagePath);
           base64ImageList.add(base64Image);
        }

        return Result.success("操作成功",base64ImageList);
    }

    private String encodeImageToBase64(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return base64Image;
    }
}
