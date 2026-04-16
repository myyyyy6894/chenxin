package com.chengxin.auth.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 工具类
 * 用于生成 token、解析 token
 */
public class JwtUtil {

    // 密钥（必须足够长，否则会报错）
    private static final String SECRET_KEY = "chengxin1234567890chengxin1234567890";
    // 令牌过期时间：24小时
    private static final long EXPIRE = 1000 * 60 * 60 * 24;

    /**
     * 生成 JWT Token
     * @param userId 用户ID
     * @param username 用户名
     * @return token字符串
     */
    public static String createToken(Long userId, String username) {
        // 生成密钥
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        // 开始构建 JWT
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 设置主题（用户ID）
                .claim("username", username)      // 存放用户名
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE)) // 过期时间
                .signWith(key)                    // 签名
                .compact();
    }

    /**
     * 解析 Token
     */
    public static Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

