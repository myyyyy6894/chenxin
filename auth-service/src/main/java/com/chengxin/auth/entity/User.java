package com.chengxin.auth.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId
    private Long id;  //用户id
    private String username; //用户账号
    private String password; //用户密码
    private String nickname; //用户昵称
    private String role; //角色（管理员/用户）
    private Integer status; //用户状态
    private LocalDateTime createTime; //用户创建时间
}
