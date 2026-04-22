package com.chengxin.chat.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "user-service")
public interface UserCLient {

    @GetMapping("/user/profile/info")
    Object getProfile(@RequestHeader("X-User_Id") Long userId);

}
