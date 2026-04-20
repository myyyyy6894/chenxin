package com.chengxin.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chengxin.user.entity.UserProfile;
import com.chengxin.user.mapper.UserProfileMapper;
import com.chengxin.user.service.UserProfileService;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    @Override
    public UserProfile getProfileByUserId(Long userId) {
        // 使用 MyBatis-Plus 的 Lambda 表达式构建查询条件
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, userId);
        // 相当于 SQL: SELECT * FROM user_profile WHERE user_id = ? AND is_deleted = 0

        return this.getOne(wrapper);
    }
}