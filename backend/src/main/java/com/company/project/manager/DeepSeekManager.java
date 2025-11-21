package com.company.project.manager;

import cn.hutool.core.util.StrUtil;

import com.company.project.model.vo.CalculationVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DeepSeekManager {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public DeepSeekManager() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public CalculationVO.Analysis getAnalysis(String prompt) {
        if (StrUtil.isBlank(apiKey) || "YOUR_API_KEY_HERE".equals(apiKey)) {
            log.warn("DeepSeek API Key not configured, returning mock data.");
            return getMockAnalysis();
        }

        MediaType mediaType = MediaType.parse("application/json");

        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", "deepseek-chat");
            root.put("response_format", objectMapper.createObjectNode().put("type", "json_object"));

            ArrayNode messages = root.putArray("messages");
            messages.addObject().put("role", "system").put("content",
                    "You are a professional numerology assistant. Output JSON only.");
            messages.addObject().put("role", "user").put("content", prompt);

            RequestBody body = RequestBody.create(objectMapper.writeValueAsString(root), mediaType);
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("DeepSeek API failed: {}", response);
                    throw new RuntimeException("DeepSeek API error: " + response.code());
                }

                String responseStr = response.body().string();
                log.info("DeepSeek Response: {}", responseStr);

                JsonNode jsonNode = objectMapper.readTree(responseStr);
                String content = jsonNode.path("choices").get(0).path("message").path("content").asText();

                return objectMapper.readValue(content, CalculationVO.Analysis.class);
            }
        } catch (Exception e) {
            log.error("Error calling DeepSeek API", e);
            // Fallback to mock data on error for stability
            return getMockAnalysis();
        }
    }

    private CalculationVO.Analysis getMockAnalysis() {
        CalculationVO.Analysis analysis = new CalculationVO.Analysis();
        analysis.setCharacter("（模拟数据）日主癸水，生于亥月，水旺之时。性格内敛深沉，智慧过人，但易多愁善感。");
        analysis.setSuggestion("（模拟数据）建议佩戴金银饰品，多穿白色、米色衣物。");
        analysis.setPoetry("（模拟数据）行到水穷处，坐看云起时。");
        return analysis;
    }
}
