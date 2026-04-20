package com.chengxin.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_profile")
public class UserProfile {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String nickname;
    private String avatar;
    private String studentId;
    private String major;

    //自动填充时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableLogic //逻辑删除注解
    private Integer isDeleted;


}
