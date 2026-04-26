package com.chengxin.chat.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.chengxin.chat.service.ChatAsyncService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.TimeUnit;

@Service
public class ChatServiceImpl {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ChatAsyncService chatAsyncService;

    private static final String LLM_API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final String API_KEY = "411dab5ef98249c7bf60b8f039e3c5cb.hQWPpIZl0HuJ6e24";

    // ====================== 普通对话（原样保留） ======================
    public String chatWithAgent(Long userId, String userMessage) {
        JSONArray messages = buildMessageContext(userId, userMessage);
        String aiFullResponse = callLLM(messages);
        JSONObject result = parseAIResponse(aiFullResponse);

        String actualReply = result.getString("reply");
        String riskLevel = result.getString("riskLevel");
        saveChatHistory(userId, messages, actualReply);
        chatAsyncService.saveChatRecordAndRisk(userId, userMessage, actualReply, riskLevel);

        return actualReply;
    }

    // ====================== 流式输出（修复完成） ======================
    public void chatWithAgentStream(Long userId, String userMessage, SseEmitter emitter) {
        try {
            // 1. 构建上下文
            JSONArray messages = buildMessageContext(userId, userMessage);

            // 2. 调用AI获取完整回答
            String aiFullResponse = callLLM(messages);
            JSONObject result = parseAIResponse(aiFullResponse);
            String actualReply = result.getString("reply");
            String riskLevel = result.getString("riskLevel");

            // 3. 模拟流式逐字输出
            for (char c : actualReply.toCharArray()) {
                emitter.send(SseEmitter.event().data(String.valueOf(c)));
                Thread.sleep(50);
            }

            // 4. 结束信号
            emitter.send(SseEmitter.event().data("[DONE]"));
            emitter.complete();

            // 5. 保存记录
            saveChatHistory(userId, messages, actualReply);
            chatAsyncService.saveChatRecordAndRisk(userId, userMessage, actualReply, riskLevel);

        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    // ====================== 抽取公共方法：构建对话上下文 ======================
    private JSONArray buildMessageContext(Long userId, String userMessage) {
        String redisKey = "chat:history:" + userId;
        String historyStr = redisTemplate.opsForValue().get(redisKey);
        JSONArray messages = new JSONArray();

        if (historyStr == null) {
            String systemPrompt =  "你是一个名叫'澄心'的大学心理健康数字伙伴。请用温暖、共情、不评判的语气回答学生的问题。" +
                    "【【最高指令】你是一个API接口，请不要输出任何额外的问候语或解释！你只能、必须、绝对只返回一个合法的JSON对象。如果用户情绪极其崩溃，请将共情的话写在JSON的reply字段中。" +
                    "JSON的格式要求如下：\n" +
                    "{\n" +
                    "  \"reply\": \"这里写你对学生说的共情回复内容\",\n" +
                    "  \"riskLevel\": \"LOW或MEDIUM或HIGH，根据学生的话评估自杀、抑郁等风险等级\",\n" +
                    "  \"tags\": [\"学业压力\", \"人际关系\", \"焦虑\"等不超过3个情绪标签]\n" +
                    "}";

            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt); // ✅ 修复：变量不是字符串
            messages.add(systemMsg);
        } else {
            messages = JSON.parseArray(historyStr);
        }

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        return messages;
    }

    // ====================== 公共方法：调用大模型 ======================
    private String callLLM(JSONArray messages) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "glm-4.5-air");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);

        JSONObject responseFormat = new JSONObject();
        responseFormat.put("type", "json_object");
        requestBody.put("response_format", responseFormat);

        return HttpRequest.post(LLM_API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute().body();
    }

    // ====================== 公共方法：解析AI返回的JSON ======================
    private JSONObject parseAIResponse(String responseBody) {
        try {
            JSONObject responseObj = JSON.parseObject(responseBody);
            String assistantReply = responseObj.getJSONArray("choices")
                    .getJSONObject(0).getJSONObject("message").getString("content");

            String cleanStr = assistantReply.replaceAll("```json|```", "").trim();
            int start = cleanStr.indexOf("{");
            int end = cleanStr.lastIndexOf("}");

            if (start != -1 && end != -1 && start < end) {
                return JSON.parseObject(cleanStr.substring(start, end + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject fallback = new JSONObject();
        fallback.put("reply", "我正在努力理解你的心情，请稍等~");
        fallback.put("riskLevel", "HIGH");
        fallback.put("tags", new JSONArray());
        return fallback;
    }

    // ====================== 保存对话到Redis ======================
    private void saveChatHistory(Long userId, JSONArray messages, String aiReply) {
        String redisKey = "chat:history:" + userId;
        JSONObject assistantMsg = new JSONObject();
        assistantMsg.put("role", "assistant");
        assistantMsg.put("content", aiReply);
        messages.add(assistantMsg);
        redisTemplate.opsForValue().set(redisKey, messages.toJSONString(), 30, TimeUnit.MINUTES);
    }
}
