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
        // 全新写法！Spring Boot 4.x 必须这样写！
        http
                .csrf(csrf -> csrf.disable())   // 正确关闭 CSRF（Lambda 风格）
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()   // 全部放行
                );

        return http.build();
    }
}
