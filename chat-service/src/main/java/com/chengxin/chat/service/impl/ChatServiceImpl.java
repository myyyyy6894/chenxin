package com.chengxin.chat.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * AI 心理咨询对话服务实现类
 * 功能：
 * 1. 从Redis读取用户历史对话（上下文记忆）
 * 2. 拼接心理咨询专业提示词
 * 3. 调用大模型API
 * 4. 把新对话存回Redis，实现连续对话
 */
@Service
public class ChatServiceImpl {


    // ====================== 注入 Redis 工具 ======================
    /**
     * StringRedisTemplate：Spring 提供的 Redis 操作类
     * 作用：存、取、删除字符串类型的缓存数据
     */

    @Autowired
    private StringRedisTemplate redisTemplate;

    // ====================== 大模型接口配置 ======================

    // 这里以目前兼容性最好的 OpenAI 接口格式为例（国内的大模型API基本都兼容此格式）
    private static final String LLM_API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final String API_KEY = "411dab5ef98249c7bf60b8f039e3c5cb.hQWPpIZl0HuJ6e24";


    // ====================== 核心对话方法 ======================
    /**
     * 用户发送消息 → AI 返回心理咨询回答
     * @param userId        当前登录用户ID（从网关 X-User-Id 拿到）
     * @param userMessage   用户今天说的话
     * @return              AI 心理咨询回答
     */

    public String chatWithAgent(Long userId, String userMessage) {

        // ====================== 1. 拼接 Redis 的 key ======================
        /**
         * redisKey = "chat:history:1001"
         * 意思：用户ID=1001 的聊天历史记录
         * 每个用户的聊天记录是分开的，互不干扰
         */
        String redisKey = "chat:history:" + userId;


        // ====================== 2. 从 Redis 读取该用户的历史对话 ======================
        /**
         * redisTemplate.opsForValue().get(redisKey)
         * 作用：从 Redis 中取出这个用户之前的所有对话
         */


        String historyStr = redisTemplate.opsForValue().get(redisKey);

        /**
         * messages = 对话上下文数组
         * 结构：
         * [
         *   { "role":"system", "content":"你是心理咨询师..." },
         *   { "role":"user", "content":"老师我心情不好" },
         *   { "role":"assistant", "content":"别难过..." }
         * ]
         */
        JSONArray messages = new JSONArray();


        // ====================== 3. 判断是不是第一次聊天 ======================
        if (historyStr == null) {

            // ============== 情况A：第一次聊天（Redis里没有记录） ==============
            /**
             * 第一次聊天必须加：system 角色
             * 作用：告诉AI你是【心理咨询师】，不是普通聊天机器人
             * 这就是你的【澄心】数字伙伴灵魂！
             */
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是一个名叫'澄心'的大学心理健康数字伙伴。请用温暖、共情、不评判的语气回答学生的问题。如果感知到高危风险（如自杀倾向），请立即建议寻求人工辅导员帮助。");
            messages.add(systemMsg);
        } else {

            // ============== 情况B：不是第一次聊天（有历史记录） ==============
            /**
             * 把之前存在Redis里的对话，转回数组格式
             * 这样AI就知道：你们之前聊过什么
             */
            messages = JSON.parseArray(historyStr);
        }


        // ====================== 4. 把用户【刚刚说的话】加入对话列表 ======================
        JSONObject newUserMsg = new JSONObject();
        newUserMsg.put("role", "user");
        newUserMsg.put("content", userMessage);
        messages.add(newUserMsg);

        // ====================== 5. 构造发送给大模型的请求 ======================
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "glm-4.5-air"); // 比如 gpt-3.5-turbo 或 qwen-turbo
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7); // 稍微高一点，让回答更具温度和多样性 // 回答温度：0.7=温和自然，0=死板严谨


        // ====================== 6. 发送 HTTP 请求调用大模型 ======================
        String responseBody = HttpRequest.post(LLM_API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute().body();

        // ====================== 7. 解析AI的回答 ======================
        JSONObject responseObj = JSON.parseObject(responseBody);

        /**
         * 从大模型返回的JSON里，把回答内容取出来
         * 固定格式：choices → 第一个 → message → content
         */
        String assistantReply = responseObj.getJSONArray("choices")
                .getJSONObject(0).getJSONObject("message").getString("content");


        // ====================== 8. 把AI的回答也存入对话历史 ======================
        JSONObject newAssistantMsg = new JSONObject();
        newAssistantMsg.put("role", "assistant");
        newAssistantMsg.put("content", assistantReply);
        messages.add(newAssistantMsg);

        // ====================== 9. 把【最新完整对话】存回Redis ======================
        /**
         * set( key, 值, 过期时间, 时间单位 )
         * 这里设置：30分钟后对话失效（超过30分钟不聊，视为新会话）
         */
        redisTemplate.opsForValue().set(redisKey, messages.toJSONString(), 30, TimeUnit.MINUTES);

        return assistantReply;
    }
}