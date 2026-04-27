package com.chengxin.admin.client;


import com.chengxin.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
public interface UserDataClient {
    @GetMapping("/archive/statistics/risk")
    Result<Object> getRiskStatistics();

}
