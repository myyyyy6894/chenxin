package com.chengxin.user.controller;

import com.chengxin.common.Result;
import com.chengxin.user.entity.MentalArchive;
import com.chengxin.user.service.MentalArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/archive")
public class MentalArchiveController {

    @Autowired
    private MentalArchiveService mentalArchiveService;

    /**
     * 接口 1：接收 Chat 服务的 Feign 调用，更新或初始化风险等级
     */
    @PostMapping("/updateRisk")
    public Result<Boolean> updateRiskLevel(@RequestParam("userId") Long userId,
                                           @RequestParam("riskLevel") String riskLevel) {

        // 尝试更新数据库中已有的档案（lambdaUpdate() 是 MyBatis-Plus 自动提供的方法）
        boolean success = mentalArchiveService.lambdaUpdate()
                .eq(MentalArchive::getUserId, userId)
                .set(MentalArchive::getCurrentRiskLevel, riskLevel)
                .set(MentalArchive::getLastEvalTime, LocalDateTime.now()) // 顺便记录一下这次被评估的时间
                .update();

        // 容错处理：如果 success 为 false，说明数据库里根本没有这个 userId 的记录
        // 意味着这是用户第一次被检测出风险，我们需要帮他新建一份档案
        if (!success) {
            MentalArchive newArchive = new MentalArchive();
            newArchive.setUserId(userId);
            newArchive.setCurrentRiskLevel(riskLevel);
            newArchive.setLastEvalTime(LocalDateTime.now());
            // save() 也是 MyBatis-Plus 自动提供的方法
            success = mentalArchiveService.save(newArchive);
        }

        return Result.success(success);
    }

    /**
     * 接口 2：顺手补充一个查询接口，以后前端查看个人主页时肯定用得到
     */
    @GetMapping("/info")
    public Result<MentalArchive> getArchiveByUserId(@RequestParam("userId") Long userId) {
        MentalArchive archive = mentalArchiveService.lambdaQuery()
                .eq(MentalArchive::getUserId, userId)
                .one();
        return Result.success(archive);
    }
}
