package com.chengxin.admin.client;

import com.chengxin.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient("chat-service")
public interface ChatRecordClient {
    @GetMapping("/chat/record/sevenDaysRisk")
    Result<List<Map<String, Object>>> getSevenDaysRiskTrend();

}