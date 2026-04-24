package com.chengxin.user.controller;

import com.chengxin.common.Result;
import com.chengxin.user.entity.UserProfile;
import com.chengxin.user.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // 获取当前用户资料
    @GetMapping("/info")
    public Result<UserProfile> getUserProfile(@RequestHeader("X-User-Id") Long userId) {
        // 注意：这里的 userId 未来在引入微服务网关后，会通过解析 JWT 直接从请求头里拿
        // 今天我们先用参数传进来测试
        UserProfile profile = userProfileService.getProfileByUserId(userId);
        return Result.success(profile);
    }

    //保存/更新用户资料
    @PostMapping("/save")
    public Result<Boolean> saveOrUpdateUserProfile(@RequestBody UserProfile userProfile) {

        //// saveOrUpdate 也是 MyBatis-Plus 提供的方法，有 ID 就更新，没 ID 就插入
        boolean success = userProfileService.saveOrUpdate(userProfile);
        if(success) {
            return Result.success(true);
        }else {
            return Result. error(500,"操作失败");
        }
    }

}
