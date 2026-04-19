package com.chengxin.auth.controller;

import com.chengxin.auth.common.Result;
import com.chengxin.auth.common.UserContext;
import com.chengxin.auth.dto.LoginDTO;
import com.chengxin.auth.dto.RegisterDTO;
import com.chengxin.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;



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

//    @GetMapping("/me")
//    public Result<?> me(
//            // 从请求头里拿到 Authorization = Bearer xxxxx(token)
//            @RequestHeader("Authorization")  String token) {
//
//        // 把前面的 "Bearer " 去掉，只留下真正的 Token
//        token = token.replace("Bearer ","");
//
//        // 用 JwtUtil 解析 Token，拿到里面的 用户ID、用户名
//        Claims claims = JwtUtil.parseToken(token);
//
//        // 返回解析出来的用户信息给前端
//        return Result.success(claims);
//    }

    //改写/auth/me
    @GetMapping("/me")
    public Result<?> me(HttpServletRequest request) {

        // 从request中取出拦截器存放的用户信息
        //这里用上下文取用户id
        String userId = UserContext.getUserId();
//      Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");

        // 组装成前端友好的格式返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("username", username);

        return Result.success(map);
    }


}
