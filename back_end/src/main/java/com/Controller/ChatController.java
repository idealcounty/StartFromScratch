package com.Controller;

import com.Entity.ChatMessage;
import com.Entity.ChatRequest;
import com.Repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Value("${dashscope.api-key}")
    private String apiKey;

    @Value("${dashscope.model-endpoint}")
    private String modelEndpoint;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ChatMessageRepository chatMessageRepository;

    public ChatController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * 发送消息并获取AI回复
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest request) {
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-max");
            requestBody.put("input", Map.of("prompt", request.getMessage()));
            requestBody.put("parameters", Map.of("temperature", 0.7));

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                modelEndpoint,
                new HttpEntity<>(requestBody, headers),
                String.class
            );

            // 解析响应
            String aiReply = parseAiResponse(response.getBody());

            // 保存聊天记录到数据库
            ChatMessage message = new ChatMessage();
            message.setUserInput(request.getMessage());
            message.setAiResponse(aiReply);
            message.setTimestamp(LocalDateTime.now());
            chatMessageRepository.save(message);

            return ResponseEntity.ok(Map.of("reply", aiReply));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory() {
        return ResponseEntity.ok(chatMessageRepository.findAllByOrderByIdAsc());
    }

    private String parseAiResponse(String responseBody) {
        // 实际应使用Jackson或JSON库解析
        return "AI回复内容"; // 替换为实际解析逻辑
    }
}