package com.chengxin.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chengxin.chat.entity.ChatRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChatRecordMapper extends BaseMapper<ChatRecord> {

    //依旧白嫖方法

    /**
     * 统计最近 7 天，每天的高风险对话数量
     * DATE() 函数：提取日期部分（去掉时分秒）
     * DATE_SUB() 函数：当前时间往前推算
     * SUM(IF/CASE) 函数：条件求和
     */
    @Select("SELECT DATE(create_time) as date, " +
            "SUM(CASE WHEN risk_level = 'HIGH' THEN 1 ELSE 0 END) as high_count, " +
            "SUM(CASE WHEN risk_level = 'LOW' THEN 1 ELSE 0 END) as low_count " +
            "FROM chat_record " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY date ASC")
    List<Map<String, Object>> getSevenDaysRiskTrend();
}
