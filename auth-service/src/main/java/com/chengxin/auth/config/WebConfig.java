package com.chengxin.auth.config;

import com.chengxin.auth.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//springmvc配置类，用来注册拦截器
@Configuration
public class WebConfig implements WebMvcConfigurer {

    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**")  //拦截所有接口
                .excludePathPatterns("/login","/auth/login","/auth/register"); //放行这两个接口 不用登陆
    }
}
