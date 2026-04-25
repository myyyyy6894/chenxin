package com.chengxin.admin.controller;

import com.chengxin.admin.client.UserDataClient;
import com.chengxin.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class AdminDashboardController {

    @Autowired
    private UserDataClient userDataClient;

    @GetMapping("/risk-distribution")
    public Result<Object> getRiskDistribution() {

        // 作为管理端，不需要自己查数据库，直接向 user-service 要数据即可！
        return userDataClient.getRiskStatistics();
    }

}
