package com.chengxin.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chengxin.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserMapper 接口
 * 继承 MyBatis-Plus 的 BaseMapper 获得基础 CRUD 能力
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
