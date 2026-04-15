package com.chengxin.auth.service;


import com.chengxin.auth.dto.RegisterDTO;
//定义业务功能
public interface UserService {
    void register(RegisterDTO dto);
}
