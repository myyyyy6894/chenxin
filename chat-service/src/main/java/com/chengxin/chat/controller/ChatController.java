package com.chengxin.chat.controller;

import com.alibaba.nacos.api.model.v2.Result;
import com.chengxin.chat.service.impl.ChatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * AI 聊天控制器
 * 作用：接收前端发来的聊天消息，调用AI服务，返回回答
 */
@RestController
@RequestMapping("/chat" )
public class ChatController {


    /**
     * 注入聊天服务层
     * 真正的AI对话、Redis记忆、调用大模型 都在这个类里
     */
    @Autowired
    private ChatServiceImpl chatService;


    /**
     * 用户发送聊天消息 → 获取AI心理咨询回答
     *
     * @param userId   从请求头里拿到的【当前登录用户ID】
     *                 网关帮我们自动传过来的，不用前端传
     * @param message  前端用户输入的聊天内容（比如：我好焦虑）
     * @return         Result.success(AI的回答) → 统一格式返回前端
     */
    @PostMapping("/send")
    public Result<String> sendMessage(@RequestHeader("X-User-Id") Long userId, @RequestParam String message){
        // 调用服务层 → 得到AI的回复（带记忆、带心理咨询prompt）
        String reply = chatService.chatWithAgent(userId,message);
        // 把AI回复包装成统一格式返回给前端
        return Result.success(reply);

    }
}
