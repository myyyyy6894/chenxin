package com.chengxin.auth.controller;

import com.chengxin.auth.common.Result;
import com.chengxin.auth.dto.LoginDTO;
import com.chengxin.auth.dto.RegisterDTO;
import com.chengxin.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @RestController
 * 作用：
 * 1. 标记这是一个【接口控制器类】
 * 2. 所有方法返回值自动转为 JSON / 字符串
 * 3. 专门用来接收前端发送的 HTTP 请求
 */
@RestController

/**
 * @RequestMapping("/auth")
 * 作用：给这个类统一加一个访问前缀
 * 访问路径：http://localhost:端口号/auth/xxx
 */
@RequestMapping("/auth")
public class AuthController {

    /**
     * @Autowired
     * 自动注入 UserService（业务层）
     * Controller 只负责接收请求，不写业务逻辑
     * 真正的逻辑交给 Service 去做
     */
    @Autowired
    private UserService userService;

    /**
     * @PostMapping("/register")
     * 作用：
     * 1. 接收 POST 请求
     * 2. 访问路径：/auth/register
     * 这就是【用户注册接口】
     * 前端调用方式：POST http://localhost:端口/auth/register
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO dto) {
        try {
            userService.register(dto);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO dto) {
        String token = userService.login(dto);
        return Result.success(token);
    }



}
