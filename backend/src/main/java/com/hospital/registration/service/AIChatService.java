package com.hospital.registration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AIChatService {

    @Value("${qwen.api.key:sk-e2402dc10eea46a696f548ab7f909696}")
    private String apiKey;

    @Value("${qwen.api.url:https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // 系统提示词 - 医疗助手人设
    private static final String SYSTEM_PROMPT = """
        你是"小樱"，优医预约系统的AI智能助手。你是一个可爱、温柔、专业的医疗咨询助手。
        
        你的职责：
        1. 帮助用户了解如何使用预约挂号系统
        2. 回答关于科室、医生、预约流程的问题
        3. 提供基础的健康咨询建议（但要提醒用户具体诊断需要就医）
        4. 用温暖友好的语气交流，适当使用emoji表情
        
        注意事项：
        - 不要给出具体的医疗诊断或用药建议
        - 遇到紧急情况要建议用户立即就医或拨打120
        - 回答要简洁明了，不要太长
        - 保持可爱亲切的语气，像朋友一样交流
        
        医院信息：
        - 医院名称：天津医科大学总医院
        - 地址：天津市和平区鞍山道154号
        - 电话：022-60362255
        - 门诊时间：周一至周五 8:00-17:00，周六周日 8:00-12:00
        """;

    public String chat(String userMessage, List<Map<String, String>> history) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 构建消息列表
            List<Map<String, Object>> messages = new ArrayList<>();
            
            // 添加系统提示
            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", SYSTEM_PROMPT);
            messages.add(systemMsg);

            // 添加历史消息
            if (history != null) {
                for (Map<String, String> msg : history) {
                    Map<String, Object> historyMsg = new HashMap<>();
                    historyMsg.put("role", msg.get("role"));
                    historyMsg.put("content", msg.get("content"));
                    messages.add(historyMsg);
                }
            }

            // 添加当前用户消息
            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-turbo");  // 使用qwen-turbo，速度快且便宜
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);

            System.out.println("=== AI Chat Request ===");
            System.out.println("API URL: " + apiUrl);
            System.out.println("User Message: " + userMessage);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );

            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map body = response.getBody();
                List<Map> choices = (List<Map>) body.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map message = (Map) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    System.out.println("AI Reply: " + content);
                    return content;
                }
            }

            return "抱歉，我暂时无法回答，请稍后再试~ 💦";

        } catch (Exception e) {
            System.err.println("=== AI Chat Error ===");
            System.err.println("Error Type: " + e.getClass().getName());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            return "网络好像有点问题呢，请稍后再试~ 🌸";
        }
    }
}
