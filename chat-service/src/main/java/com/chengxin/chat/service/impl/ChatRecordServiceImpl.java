package com.chengxin.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chengxin.chat.entity.ChatRecord;
import com.chengxin.chat.mapper.ChatRecordMapper;
import com.chengxin.chat.service.ChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecord> implements ChatRecordService {

    @Autowired
    private ChatRecordMapper chatRecordMapper;

    public List<Map<String,Object>> getSevenDaysRiskTrend(){
        return chatRecordMapper.getSevenDaysRiskTrend();
    }

}
