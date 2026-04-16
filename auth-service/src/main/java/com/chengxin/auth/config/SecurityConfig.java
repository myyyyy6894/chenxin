package com.chengxin.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭 CSRF（前后端分离项目通用做法）
                .csrf(csrf -> csrf.disable())
                // 2. 核心放行规则
                .authorizeHttpRequests(auth -> auth
                        // ✅ 只放行这 3 个接口
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/auth/me"
                        ).permitAll()
                        // 其他所有接口，都需要认证（带Token）
                        .anyRequest().authenticated()
                );

        return http.build();
    }



//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())   // 正确关闭 CSRF（Lambda 风格）
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()   // 全部放行
//                );
//
//        return http.build();
//    }
}
