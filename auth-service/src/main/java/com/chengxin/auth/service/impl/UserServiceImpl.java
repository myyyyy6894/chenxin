package com.chengxin.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chengxin.common.util.JwtUtil;
import com.chengxin.auth.dto.LoginDTO;
import com.chengxin.auth.dto.RegisterDTO;
import com.chengxin.auth.entity.User;
import com.chengxin.auth.mapper.UserMapper;
import com.chengxin.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @Service 表示这是一个【业务层实现类】
 * Spring 会自动管理这个类，把它放进容器里
 * 别的地方可以直接注入使用
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * @Autowired 自动注入
     * 把 UserMapper 这个操作数据库的对象拿过来用
     */
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 实现 UserService 接口里的 register 方法
     * 功能：用户注册
     * @param dto 前端传过来的：用户名、密码、昵称
     */

    @Override
    public void register(RegisterDTO dto){

        // ====================== 第一步：查询用户名是否已经被注册 ======================
        /**
         * QueryWrapper：MyBatis-Plus 提供的查询条件构造器
         * eq("username", dto.getUsername()) 等价于 SQL 中的 WHERE username = ?
         */
        User oldUser = userMapper.selectOne(
                new QueryWrapper<User>()
                        .eq("username",dto.getUsername())
        );

        // 如果查询到了，说明用户名已存在，直接抛出异常
        if(oldUser != null){
            throw new RuntimeException("用户名已经存在");
        }

        // ====================== 第二步：密码加密（绝对不能明文存库） ======================
        /**
         * PasswordEncoder：Spring 提供的安全加密工具接口
         * 加密后是一串乱码，无法解密，只能验证
         * 数据库永远不能存明文密码！！！
         */

        // ====================== 第三步：组装要存入数据库的 User 对象
        User user = new User();
        user.setUsername(dto.getUsername());//用户名
        user.setPassword(passwordEncoder.encode(dto.getPassword())); //加密后的密码
        user.setNickname((dto.getNickname()));//昵称

        userMapper.insert(user);
    }

    @Override
    public String login(LoginDTO dto){

        // 1. 根据用户名查询用户
        User user = userMapper.selectOne(
                new QueryWrapper<User>()
                        .eq("username",dto.getUsername())
        );

        // 2. 如果用户不存在，直接抛异常
        if(user == null){
            throw new RuntimeException("用户不存在");
        }


        // 3. 密码比对（前端传的明文 → 和数据库密文对比）
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if(!passwordEncoder.matches(dto.getPassword(),user.getPassword())){
            throw new RuntimeException("密码错误");
        }


        return JwtUtil.createToken(user.getId(),user.getUsername());
    }

}
