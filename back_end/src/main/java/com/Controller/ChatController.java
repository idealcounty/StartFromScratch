package com.Controller;

import com.Entity.ChatMessage;
import com.Entity.ChatRequest;
import com.PO.Archive;
import com.Repository.ArchiveRepository;
import com.Repository.ChatMessageRepository;
import com.Service.UserService;
import com.alibaba.dashscope.app.*;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Value("${dashscope.api-key}")
    private String apiKey;

    @Value("${dashscope.app-id}")
    private String appId;

    private final ChatMessageRepository chatMessageRepository;
    private final ArchiveRepository archiveRepository;
    private final UserService userService;

    public ChatController(ChatMessageRepository chatMessageRepository, ArchiveRepository archiveRepository, UserService userService) {
        this.chatMessageRepository = chatMessageRepository;
        this.archiveRepository = archiveRepository;
        this.userService = userService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest request) {
        try {
            // 动态获取用户存档
            Integer userId = userService.getInformation().getUserId();
            Archive archive = archiveRepository.findByUserId(userId);
            if (archive == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "用户存档不存在");
                return ResponseEntity.status(404).body(errorResponse);
            }

            // 获取当前存档的属性值
            int health = archive.getArchiveHealth();
            int science = archive.getArchiveScience();
            int social = archive.getArchiveSocial();
            int game = archive.getArchiveGame();
            int money = archive.getArchiveMoney();

            // 验证用户输入
            String userInput = request.getMessage();
            if (userInput == null || userInput.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "用户输入不能为空");
                return ResponseEntity.status(400).body(errorResponse);
            }

            // 拼接属性值到用户输入
            String modifiedInput = String.format(
                "%s\n当前属性值：学习 %d, 健康 %d, 游戏 %d, 社交 %d, 金钱 %d",
                userInput, science, health, game, social, money
            );

            // 构建 ApplicationParam
            ApplicationParam param = ApplicationParam.builder()
                    .apiKey(apiKey)
                    .appId(appId)
                    .prompt(modifiedInput)
                    .build();

            // 调用 DashScope API
            Application application = new Application();
            ApplicationResult result = application.call(param);
            String aiReply = result.getOutput().getText();

            // 提取属性值修正并更新 Archive
            updateArchiveFromReply(aiReply, archive);

            // 保存更新后的 Archive
            archiveRepository.save(archive);

            // 保存聊天记录
            ChatMessage message = new ChatMessage();
            message.setUserInput(userInput); // 保存原始输入
            message.setAiResponse(aiReply);
            message.setTimestamp(LocalDateTime.now());
            chatMessageRepository.save(message);

            // 返回成功响应
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("reply", aiReply);
            return ResponseEntity.ok(successResponse);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // 从模型回复中提取属性值修正并更新 Archive
    private void updateArchiveFromReply(String reply, Archive archive) {
        // 定义正则表达式，匹配“属性名 +数字”或“属性名 -数字”
        String regex = "(学习|健康|游戏|社交|金钱)\\s*([+-]\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(reply);

        while (matcher.find()) {
            String attribute = matcher.group(1); // 属性名（中文）
            int change = Integer.parseInt(matcher.group(2)); // 变化值（带+或-）

            // 根据属性名更新 Archive
            switch (attribute) {
                case "学习":
                    archive.setArchiveScience(archive.getArchiveScience() + change);
                    break;
                case "健康":
                    archive.setArchiveHealth(archive.getArchiveHealth() + change);
                    break;
                case "游戏":
                    archive.setArchiveGame(archive.getArchiveGame() + change);
                    break;
                case "社交":
                    archive.setArchiveSocial(archive.getArchiveSocial() + change);
                    break;
                case "金钱":
                    archive.setArchiveMoney(archive.getArchiveMoney() + change);
                    break;
            }
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory() {
        return ResponseEntity.ok(chatMessageRepository.findAllByOrderByIdAsc());
    }
}