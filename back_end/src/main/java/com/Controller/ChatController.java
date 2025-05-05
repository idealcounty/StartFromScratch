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
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 系统提示，定义模型的角色和输出格式
    private static final String SYSTEM_PROMPT =
        "你是“大学生活模拟器”，和用户进行交互，每轮对话需要：\n" +
        "1. 提出一个问题，描述一个大学生活中可能发生的场景。\n" +
        "2. 提供四个选项（A、B、C、D），每个选项是不同的应对方式。\n" +
        "3. 告诉用户可以选择选项（输入 A、B、C 或 D），或者自由发挥（输入其他内容）。\n" +
        "\n" +
        "输出格式必须如下：\n" +
        "1. **问题 X**：<场景描述>\n" +
        "2. **选项如下**：\n" +
        "   A. <选项 A>\n" +
        "   B. <选项 B>\n" +
        "   C. <选项 C>\n" +
        "   D. <选项 D>\n" +
        "请选择选项（输入 A、B、C 或 D），或者自由发挥。\n" +
        "\n" +
        "如果用户选择了选项（A、B、C 或 D），根据用户的选择继续生成下一个问题和选项。\n" +
        "如果用户自由发挥（输入非选项内容），根据用户的回答继续生成下一个问题和选项。\n" +
        "请严格按照以上格式输出，不要添加额外的说明。";

    public ChatController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 获取当前对话历史
            List<ChatMessage> history = chatMessageRepository.findAllByOrderByIdAsc();
            List<Map<String, String>> messages = new ArrayList<>();

            // 添加系统提示
            messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));

            // 如果是第一次对话，初始化问题
            if (history.isEmpty()) {
                messages.add(Map.of("role", "user", "content", "开始模拟"));
            } else {
                // 添加历史对话
                for (ChatMessage msg : history) {
                    messages.add(Map.of("role", "user", "content", msg.getUserInput()));
                    messages.add(Map.of("role", "assistant", "content", msg.getAiResponse()));
                }
                // 添加当前用户输入
                messages.add(Map.of("role", "user", "content", request.getMessage()));
            }

            // 构造请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-max");
            requestBody.put("input", Map.of("messages", messages));
            requestBody.put("parameters", Map.of("temperature", 0.7, "result_format", "message"));

            // 调试：打印请求体
            System.out.println("Request body: " + objectMapper.writeValueAsString(requestBody));

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
            return root.path("output").path("choices").get(0).path("message").path("content").asText("默认回复");
        } catch (Exception e) {
            e.printStackTrace();
            return "解析失败";
        }
    }
}