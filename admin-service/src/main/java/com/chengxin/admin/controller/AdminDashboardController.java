package com.chengxin.admin.controller;

import com.chengxin.admin.client.ChatRecordClient;
import com.chengxin.admin.client.UserDataClient;
import com.chengxin.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private UserDataClient userDataClient;

    @Autowired
    private ChatRecordClient chatRecordClient;

    @GetMapping("/risk-distribution")
    public Result<Object> getRiskDistribution() {

        // 作为管理端，不需要自己查数据库，直接向 user-service 要数据即可！
        return userDataClient.getRiskStatistics();
    }

    @GetMapping("/sevenDaysRiskTrend")
    public Result<List<Map<String, Object>>> sevenDaysRiskTrend() {
        return chatRecordClient.getSevenDaysRiskTrend();
    }

}
