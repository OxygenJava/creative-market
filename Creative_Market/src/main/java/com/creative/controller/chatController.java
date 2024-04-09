package com.creative.controller;

import com.creative.domain.post;
import com.creative.dto.Result;
import com.creative.service.chatService;
import com.creative.service.postService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/websocket")
@CrossOrigin
public class chatController {

    @Autowired
    private chatService chatService;

    @PostMapping("/{toUser}")
    public Result isFirstChat(HttpServletRequest request,@PathVariable String toUser){
        Result result = chatService.isFirstChat(request,toUser);
        return result;
    }



}
