package com.chengxin.chat.controller;


import com.chengxin.common.Result;
import com.chengxin.chat.entity.ChatRecord;
import com.chengxin.chat.service.ChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat/record")
public class ChatRecordController {

    @Autowired
    private ChatRecordService chatRecordService;


    // 获取当前用户的所有历史聊天记录，按时间倒序排列
    @GetMapping("/history")
    public Result<List<ChatRecord>> getHistory(@RequestHeader("X-User-Id") Long userId) {
        List<ChatRecord> records = chatRecordService.lambdaQuery()
                .eq(ChatRecord::getUserId, userId)
                .orderByDesc(ChatRecord::getCreateTime)
                .list();
        return Result.success(records);
    }

    //获取最近7天的风险趋势
    @GetMapping("/sevenDaysRisk")
    public Result<List<Map<String,Object>>> getSevenDayRiskTrend() {
        return Result.success(chatRecordService.getSevenDaysRiskTrend());
    }

}
