package com.chengxin.chat.client;

import com.chengxin.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service")
public interface UserClient {

    // 之前写的：获取用户信息
    @GetMapping("/user/profile/info")
    Object getProfile(@RequestHeader("X-User-Id") Long userId);

    // 新增的：更新风险等级
    @PostMapping("/archive/updateRisk")
    Result<Boolean> updateRiskLevel(@RequestParam("userId") Long userId,
                                    @RequestParam("riskLevel") String riskLevel);
}
