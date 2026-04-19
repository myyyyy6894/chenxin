package com.chengxin.auth.interceptor;

import com.chengxin.auth.common.JwtUtil;
import com.chengxin.auth.common.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

//请求拦截器
public class JwtInterceptor implements HandlerInterceptor {
    //进入接口之前先执行该方法
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从请求中拿token
        String authHeader = request.getHeader("Authorization");

        //没有/错误直接返回401
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            response.setStatus(401);
            return false;
        }

        //去掉bearer
        String token = authHeader.replace("Bearer", "");

        try {
            //解析token
            Claims claims = JwtUtil.parseToken(token);

            //存入 UserContext 上下文
            UserContext.setUserId(claims.getSubject());

            //把用户id和名字存起来 后面接口可以直接访问
            request.setAttribute("userId", Long.parseLong(claims.getSubject()));
            request.setAttribute("username", claims.get("username"));

        }
        //若解析失败返回401
        catch (Exception e) {
            response.setStatus(401);
            return false;
        }
        //解析成功可以放行
        return true;
    }

    //请求结束后清空上下文
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清空ThreadLocal，避免内存泄漏、下次请求串用户
        UserContext.clear();

    }
}
