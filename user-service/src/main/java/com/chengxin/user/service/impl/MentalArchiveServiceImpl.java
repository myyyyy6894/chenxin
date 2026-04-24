package com.chengxin.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chengxin.user.entity.MentalArchive;
import com.chengxin.user.mapper.MentalArchiveMapper;
import com.chengxin.user.service.MentalArchiveService;
import org.springframework.stereotype.Service;

@Service
public class MentalArchiveServiceImpl extends ServiceImpl<MentalArchiveMapper, MentalArchive> implements MentalArchiveService {

    // 💡 为什么这里是空的？
    // 因为继承了 ServiceImpl，MyBatis-Plus 已经在底层为你实现了所有的单表 CRUD 操作。
    // Controller 里调用的 .lambdaUpdate() 就是从父类直接“白嫖”来的，不需要你再手写任何 SQL 或更新逻辑！
    // 只有当你需要进行多张表联合查询（比如 JOIN 操作）时，才需要在这里写自定义方法。

}