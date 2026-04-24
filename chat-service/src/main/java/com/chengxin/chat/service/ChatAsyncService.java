package com.chengxin.chat.service;

import com.chengxin.chat.client.UserClient;
import com.chengxin.chat.entity.ChatRecord;
import com.chengxin.chat.service.ChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipal;

@Service
public class ChatAsyncService {

    @Autowired
    private ChatRecordService chatRecordService;

    @Autowired
    private UserClient userClient;

    @Async
    public void saveChatRecordAndRisk(Long userId, String userMsg, String aiReply, String riskLevel) {
        try {
            // 1. 保存聊天记录到当前服务的 MySQL
            ChatRecord record = new ChatRecord();
            record.setUserId(userId);
            record.setUserMessage(userMsg);
            record.setAiReply(aiReply);
            record.setRiskLevel(riskLevel);
            chatRecordService.save(record);

            // 2. 如果检测到风险不是 LOW，通过 Feign 跨微服务更新用户的心理档案
            if (!"LOW".equalsIgnoreCase(riskLevel)) {
                // 这里底层会发起 HTTP 请求去调用 user-service
                userClient.updateRiskLevel(userId, riskLevel);
                System.out.println("【高危预警】已通过Feign通知User服务更新用户 " + userId + " 的风险等级！");
            }

        } catch (Exception e) {
            System.err.println("异步保存记录失败：" + e.getMessage());
        }
    }
}