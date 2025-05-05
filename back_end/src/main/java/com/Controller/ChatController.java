package com.Controller;

import com.Entity.ChatMessage;
import com.Entity.ChatRequest;
import com.Repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-max");
            requestBody.put("input", Map.of("prompt", request.getMessage()));
            requestBody.put("parameters", Map.of("temperature", 0.7));

            ResponseEntity<String> response = restTemplate.postForEntity(
                modelEndpoint,
                new HttpEntity<>(requestBody, headers),
                String.class
            );
            System.out.println("Raw response: " + response.getBody());

            String aiReply = parseAiResponse(response.getBody());

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
    try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);
        return root.path("output").path("text").asText("默认回复");
    } catch (Exception e) {
        e.printStackTrace();
        return "解析失败";
    }
    }
}