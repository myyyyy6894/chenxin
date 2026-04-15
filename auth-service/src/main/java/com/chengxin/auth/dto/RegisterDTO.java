package com.chengxin.auth.dto;

import lombok.Data;

@Data
//注册请求
public class RegisterDTO {
    private String username;//用户账号
    private String password;//用户密码
    private String nickname;//用户昵称


}
