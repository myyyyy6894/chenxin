package com.chengxin.chat.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.chengxin.chat.service.ChatAsyncService;

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

    @Autowired
    private ChatAsyncService chatAsyncService;

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

            // 之前是纯文本提示，现在我们要把它改成强制输出 JSON 格式
            String systemPrompt = "你是一个名叫'澄心'的大学心理健康数字伙伴。请用温暖、共情、不评判的语气回答学生的问题。" +
                    "【【最高指令】你是一个API接口，请不要输出任何额外的问候语或解释！你只能、必须、绝对只返回一个合法的JSON对象。如果用户情绪极其崩溃，请将共情的话写在JSON的reply字段中。" +
                    "JSON的格式要求如下：\n" +
                    "{\n" +
                    "  \"reply\": \"这里写你对学生说的共情回复内容\",\n" +
                    "  \"riskLevel\": \"LOW或MEDIUM或HIGH，根据学生的话评估自杀、抑郁等风险等级\",\n" +
                    "  \"tags\": [\"学业压力\", \"人际关系\", \"焦虑\"等不超过3个情绪标签]\n" +
                    "}";
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", "systemPrompt");
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

        // ======= 核心防御性编程开始 =======
        JSONObject assistantReplyStr = new JSONObject();
        String actualReply = "";
        String riskLevel = "LOW"; // 默认低风险
        JSONArray tags = new JSONArray();

        try {
            // 1. 清理大模型可能带上的 Markdown 标记 (比如 ```json ... ```)
            String cleanStr = assistantReply.replaceAll("```json", "").replaceAll("```", "").trim();

            // 2. 截取第一个 { 和最后一个 } 之间的内容
            int startIndex = cleanStr.indexOf("{");
            int endIndex = cleanStr.lastIndexOf("}");

            // 判断：必须同时找到 { 和 }，并且 { 在 } 前面，才是合法JSON
            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                String jsonPart = cleanStr.substring(startIndex, endIndex + 1);
                // 尝试解析 JSON
                JSONObject parsedObj = JSON.parseObject(jsonPart);
                actualReply = parsedObj.getString("reply");
                riskLevel = parsedObj.getString("riskLevel") != null ? parsedObj.getString("riskLevel") : "LOW";
                tags = parsedObj.getJSONArray("tags");
            } else {
                // 如果找不到大括号，说明大模型彻底没按格式输出，直接抛出异常走 catch 兜底
                throw new Exception("未找到JSON结构");
            }
        } catch (Exception e) {
            System.err.println("【警告】大模型未按JSON格式输出，已触发兜底机制。原输出：" + assistantReply);
            // 兜底方案：把大模型说的所有废话当成回复，风险默认设为 LOW（或者你可以设为 HIGH 让人工介入）
            actualReply = assistantReply;
            riskLevel = "HIGH"; // 既然触发了安全机制没按格式输出，往往是遇到了极端负面情绪，安全起见标记为HIGH
        }
        // ======= 核心防御性编程结束 =======

//        //(1).解析大模型返回的特定JSON结构
//        JSONObject aiAnalysisResult = JSON.parseObject(assistantReply);
//        String actualReply = aiAnalysisResult.getString("replay");
//        String riskLevel = aiAnalysisResult.getString("riskLevel");
//        JSONArray tags = aiAnalysisResult.getJSONArray("tags");


        // ====================== 8. 把AI的回答也存入对话历史 ======================
        JSONObject newAssistantMsg = new JSONObject();
        newAssistantMsg.put("role", "assistant");
        newAssistantMsg.put("content", actualReply);
        messages.add(newAssistantMsg);

        // ====================== 9. 把【最新完整对话】存回Redis ======================
        /**
         * set( key, 值, 过期时间, 时间单位 )
         * 这里设置：30分钟后对话失效（超过30分钟不聊，视为新会话）
         */
        chatAsyncService.saveChatRecordAndRisk(userId,
                userMessage,
                actualReply,
                riskLevel);
        redisTemplate.opsForValue().set(redisKey, messages.toJSONString(), 30, TimeUnit.MINUTES);

        return actualReply;
    }
}