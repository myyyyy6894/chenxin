package com.chengxin.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_record")
public class ChatRecord {

    @TableId(type = IdType.AUTO)
    private Long Id;

    private Long userId;        // 用户ID

    private String userMessage; // 用户说的话

    private String aiReply;    // AI的回复

    private String riskLevel;  // 风险等级

    private LocalDateTime createTime; // 创建时间
    
}
