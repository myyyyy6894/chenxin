package com.chengxin.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chengxin.chat.entity.ChatRecord;
import com.chengxin.chat.mapper.ChatRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ChatRecordService extends IService<ChatRecord> {

    public List<Map<String,Object>> getSevenDaysRiskTrend();

}
