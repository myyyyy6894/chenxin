package com.chengxin.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mental_archive")
public class MentalArchive {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String currentRiskLevel;
    private LocalDateTime lastEvalTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
