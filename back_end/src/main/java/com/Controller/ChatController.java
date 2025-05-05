package com.Controller;

import com.Entity.ChatMessage;
import com.Entity.ChatRequest;
import com.PO.Archive;
import com.PO.User;
import com.Repository.ArchiveRepository;
import com.Repository.ChatMessageRepository;
import com.Service.UserService;
import com.VO.UserVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Value("${dashscope.api-key}")
    private String apiKey;

    @Value("${dashscope.model-endpoint}")
    private String modelEndpoint;

    @Value("${style.file.path:dataTest/test.jsonl}")
    private String styleFilePath="dataTest\\test.jsonl";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    UserService userService;
    @Autowired
    ArchiveRepository archiveRepository;

    private UserVO userVO;
    private Archive archive;

    private static final String SYSTEM_PROMPT =
        "你是“大学生活模拟器”，和用户进行交互，每轮对话需要：\n" +
        "1. 根据当前存档的属性值（学习、健康、游戏、社交、金钱），生成一个最可能发生的事件。\n" +
        "2. 提供四个选项（A、B、C、D），每个选项是不同的应对方式。\n" +
        "3. 告诉用户可以选择选项（输入 A、B、C 或 D），或者自由发挥（输入其他内容）。\n" +
        "\n" +
        "在生成事件和选项时，请参考以下 JSONL 文件内容的风格：\n" +
        "%s\n" +  // 这里会插入 JSONL 文件内容
        "\n" +
        "输出格式必须如下：\n" +
        "1. **事件**：<事件描述>\n" +
        "2. **选项如下**：\n" +
        "   A. <选项 A>\n" +
        "   B. <选项 B>\n" +
        "   C. <选项 C>\n" +
        "   D. <选项 D>\n" +
        "请选择选项（输入 A、B、C 或 D），或者自由发挥。\n" +
        "\n" +
        "如果用户选择了选项（A、B、C 或 D），根据用户的选择分析对属性值的修正，并输出修正后的属性值，格式如下：\n" +
        "你的选择是 <用户选择>。\n" +
        "属性值修正：\n" +
        "- <属性名> <+/-值>\n" +
        "（例如：- 学习 +5\\n- 健康 -3）\n" +
        "如果用户自由发挥（输入非选项内容），根据用户的回答分析对属性值的修正，并输出修正后的属性值，使用相同格式。\n" +
        "请严格按照以上格式输出，不要添加额外的说明。";

    private String styleContent;

    public ChatController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
        // 读取 JSONL 文件内容
        styleContent = readStyleFile();
    }

private String readStyleFile() {
    if (styleFilePath == null || styleFilePath.trim().isEmpty()) {
        System.err.println("styleFilePath is null or empty, using default style.");
        return "未能加载风格文件，请使用默认风格。";
    }

    StringBuilder content = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(styleFilePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
    } catch (IOException e) {
        System.err.println("Failed to read style file: " + e.getMessage());
        return "未能加载风格文件，请使用默认风格。";
    }
    return content.toString();
}

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest request) {
        try {
            // 初始化 userVO 和 archive
            if (userVO == null) {
                userVO = userService.getInformation();
                System.out.println("UserVO initialized: " + userVO);
                archive = archiveRepository.findByArchiveId(userVO.getArchiveId());
                if (archive == null) {
                    // 如果存档不存在，创建一个新的存档并设置初始值
                    archive = new Archive();
                    Random random = new Random();
                    archive.setArchiveScience(60 + random.nextInt(21)); // 60~80
                    archive.setArchiveHealth(60 + random.nextInt(21));
                    archive.setArchiveGame(60 + random.nextInt(21));
                    archive.setArchiveSocial(60 + random.nextInt(21));
                    archive.setArchiveMoney(60 + random.nextInt(21));
                    archive.setArchiveSuccessFinish(0);
                    archive = archiveRepository.save(archive);
                    // 更新 userVO 的 archiveId
                    userVO.setArchiveId(archive.getArchiveId());
                }
                System.out.println("Archive initialized: " + archive.getArchiveId());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // Get current conversation history
            List<ChatMessage> history = chatMessageRepository.findAllByOrderByIdAsc();
            List<Map<String, String>> messages = new ArrayList<>();

            // Add system prompt with style content
            String formattedSystemPrompt = String.format(SYSTEM_PROMPT, styleContent);
            messages.add(Map.of("role", "system", "content", formattedSystemPrompt));

            // Add current archive attribute values
            String archiveInfo = String.format(
                "当前存档属性值：学习=%d，健康=%d，游戏=%d，社交=%d，金钱=%d",
                archive.getArchiveScience(),
                archive.getArchiveHealth(),
                archive.getArchiveGame(),
                archive.getArchiveSocial(),
                archive.getArchiveMoney()
            );
            messages.add(Map.of("role", "user", "content", archiveInfo));

            // If it's the first conversation, initialize the event
            if (history.isEmpty()) {
                messages.add(Map.of("role", "user", "content", "开始模拟"));
            } else {
                // Add conversation history
                for (ChatMessage msg : history) {
                    messages.add(Map.of("role", "user", "content", msg.getUserInput()));
                    messages.add(Map.of("role", "assistant", "content", msg.getAiResponse()));
                }
                // Add current user input
                messages.add(Map.of("role", "user", "content", request.getMessage()));
            }

            // Construct request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-max");
            requestBody.put("input", Map.of("messages", messages));
            requestBody.put("parameters", Map.of("temperature", 0.7, "result_format", "message"));

            // Debug: Print request body
            System.out.println("Request body: " + objectMapper.writeValueAsString(requestBody));

            ResponseEntity<String> response = restTemplate.postForEntity(
                modelEndpoint,
                new HttpEntity<>(requestBody, headers),
                String.class
            );
            System.out.println("Raw response: " + response.getBody());

            String aiReply = parseAiResponse(response.getBody());

            // Parse attribute changes and update archive
            Map<String, Integer> attributeChanges = parseAttributeChanges(aiReply);
            updateArchive(attributeChanges);

            ChatMessage message = new ChatMessage();
            message.setUserInput(request.getMessage());
            message.setAiResponse(aiReply);
            message.setTimestamp(LocalDateTime.now());
            chatMessageRepository.save(message);

            return ResponseEntity.ok(Map.of("reply", aiReply));
        } catch (Exception e) {
            e.printStackTrace();
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

    private Map<String, Integer> parseAttributeChanges(String aiReply) {
        Map<String, Integer> changes = new HashMap<>();
        String[] lines = aiReply.split("\n");
        boolean inChangesSection = false;

        for (String line : lines) {
            if (line.startsWith("属性值修正：")) {
                inChangesSection = true;
                continue;
            }
            if (inChangesSection && line.startsWith("- ")) {
                String[] parts = line.substring(2).trim().split("\\s+");
                if (parts.length >= 2) {
                    String attribute = parts[0];
                    String changeStr = parts[1].replace("+", "").replace("-", "");
                    try {
                        int changeValue = Integer.parseInt(changeStr);
                        // 根据前缀确定正负
                        if (line.contains("-")) changeValue = -changeValue;
                        switch (attribute) {
                            case "学习":
                                changes.put("archiveScience", changeValue);
                                break;
                            case "健康":
                                changes.put("archiveHealth", changeValue);
                                break;
                            case "游戏":
                                changes.put("archiveGame", changeValue);
                                break;
                            case "社交":
                                changes.put("archiveSocial", changeValue);
                                break;
                            case "金钱":
                                changes.put("archiveMoney", changeValue);
                                break;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Failed to parse change value: " + changeStr + " in line: " + line);
                        continue; // 跳过无效的行
                    }
                }
            }
        }
        return changes;
    }

    private void updateArchive(Map<String, Integer> changes) {
        archive.setArchiveScience(Math.min(100, Math.max(0, archive.getArchiveScience() + changes.getOrDefault("archiveScience", 0))));
        archive.setArchiveHealth(Math.min(100, Math.max(0, archive.getArchiveHealth() + changes.getOrDefault("archiveHealth", 0))));
        archive.setArchiveGame(Math.min(100, Math.max(0, archive.getArchiveGame() + changes.getOrDefault("archiveGame", 0))));
        archive.setArchiveSocial(Math.min(100, Math.max(0, archive.getArchiveSocial() + changes.getOrDefault("archiveSocial", 0))));
        archive.setArchiveMoney(Math.min(100, Math.max(0, archive.getArchiveMoney() + changes.getOrDefault("archiveMoney", 0))));
        archiveRepository.save(archive);
    }
}