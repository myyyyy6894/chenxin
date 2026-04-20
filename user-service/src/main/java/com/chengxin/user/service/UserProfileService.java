package com.chengxin.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chengxin.user.entity.UserProfile;

public interface UserProfileService extends IService<UserProfile> {
    // IService 已经写好了几十个基础增删改查方法
    // 这里我们定义一个自定义的业务方法：根据 Auth 服务的用户 ID 查询资料
    UserProfile getProfileByUserId(Long userId);
}
