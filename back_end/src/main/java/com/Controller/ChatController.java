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
    private int health = 0;
    private int science = 0;
    private int social = 0;
    private int game = 0;
    private int money = 0;

    public ChatController(ChatMessageRepository chatMessageRepository, ArchiveRepository archiveRepository, UserService userService) {
        this.chatMessageRepository = chatMessageRepository;
        this.archiveRepository = archiveRepository;
        this.userService = userService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest request) {
        try {
            // Validate user and archive
            Integer userId = userService.getInformation().getUserId();
            Archive archive = archiveRepository.findByUserId(userId);
            if (archive == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "用户存档不存在");
                return ResponseEntity.status(404).body(errorResponse);
            }
            getProperties(archive);

            // Validate user input
            String userInput = request.getMessage();
            if (userInput == null || userInput.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "用户输入不能为空");
                return ResponseEntity.status(400).body(errorResponse);
            }

            // Construct prompt with current attributes
            String modifiedInput = String.format(
                "%s\n当前属性值：学习 %d, 健康 %d, 游戏 %d, 社交 %d, 金钱 %d",
                userInput, science, health, game, social, money
            );

            // Call DashScope API for regular response
            ApplicationParam param = ApplicationParam.builder()
                .apiKey(apiKey)
                .appId(appId)
                .prompt(modifiedInput)
                .ragOptions(RagOptions.builder().pipelineIds(List.of("file_b75bbb2b24244ab49424d7cca40e168f_11940386", "file_a291659172454c948a1f0aae6c591ad8_11940386", "file_5e20501991c2415494e7a454431f1204_11940386")).build())
                .build();

            Application application = new Application();
            ApplicationResult result = application.call(param);
            String aiReply = result.getOutput().getText();

            // Update archive based on regular reply
            updateArchiveFromReply(aiReply, archive);

            // Prepare response
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("reply", aiReply);

            // Check for final chapter trigger
            if (judge(archive)) {
                // Mark archive as complete
                archive.setArchiveSuccessFinish(1);

                // Construct final chapter prompt
                String finalChapterMessage = String.format(
                    "当前属性值：学习 %d, 健康 %d, 游戏 %d, 社交 %d, 金钱 %d\n" +
                    "根据目前的属性值，为用户预测一个大学的结局！\n" +
                    "规则：\n" +
                    "- 若某项属性值 > 90，安排一个非常好的结局（例如顶尖大学毕业、行业领袖）。\n" +
                    "- 若某项属性值 > 50，安排一个比较好的结局（例如顺利毕业、稳定工作）。\n" +
                    "- 若某项属性值 < 10，安排一个不太好的结局（例如辍学、经济困难）。\n" +
                    "- 综合考虑所有属性，生成一个详细的结局描述，长度不超过500字符。",
                    science, health, game, social, money
                );

                // Call DashScope API for final chapter
                ApplicationParam finalChapterParam = ApplicationParam.builder()
                    .apiKey(apiKey)
                    .appId(appId)
                    .prompt(finalChapterMessage)
                    .build();

                try {
                    Application finalChapterApplication = new Application();
                    ApplicationResult finalChapterResult = finalChapterApplication.call(finalChapterParam);
                    String finalChapterAiReply = finalChapterResult.getOutput().getText();

                    // Save final chapter outcome to archive
                    archive.setFinalOutcome(finalChapterAiReply);

                    // Save final chapter message and response to chat history
                    ChatMessage finalChapterMessageRecord = new ChatMessage();
                    finalChapterMessageRecord.setUserInput("触发大学结局预测");
                    finalChapterMessageRecord.setAiResponse(finalChapterAiReply);
                    finalChapterMessageRecord.setTimestamp(LocalDateTime.now());
                    chatMessageRepository.save(finalChapterMessageRecord);

                    // Include final chapter reply in response
                    successResponse.put("finalOutcome", finalChapterAiReply);
                } catch (ApiException | NoApiKeyException | InputRequiredException e) {
                    // Log error but don't fail the entire request
                    System.err.println("Final chapter API error: " + e.getMessage());
                    successResponse.put("finalOutcomeError", "无法生成大学结局：" + e.getMessage());
                }
            }

            // Save updated archive
            archiveRepository.save(archive);

            // Save regular message to chat history
            ChatMessage message = new ChatMessage();
            message.setUserInput(userInput);
            message.setAiResponse(aiReply);
            message.setTimestamp(LocalDateTime.now());
            chatMessageRepository.save(message);

            return ResponseEntity.ok(successResponse);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "DashScope API error: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/end")
    public ResponseEntity<?> endConversation() {
        try {
            // Validate user and archive
            Integer userId = userService.getInformation().getUserId();
            Archive archive = archiveRepository.findByUserId(userId);
            if (archive == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "用户存档不存在");
                return ResponseEntity.status(404).body(errorResponse);
            }

            // Check if archive is already completed
            Integer successFinish = archive.getArchiveSuccessFinish();
            if (successFinish != null && successFinish == 1) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "存档已完成，无需再次生成结局");
                return ResponseEntity.status(400).body(errorResponse);
            }

            // Get current properties
            getProperties(archive);

            // Mark archive as complete
            archive.setArchiveSuccessFinish(1);

            // Construct final chapter prompt
            String finalChapterMessage = String.format(
                "当前属性值：学习 %d, 健康 %d, 游戏 %d, 社交 %d, 金钱 %d\n" +
                "用户选择结束对话，请根据目前的属性值，为用户预测一个大学的结局！\n" +
                "规则：\n" +
                "- 若某项属性值 > 90，安排一个非常好的结局（例如顶尖大学毕业、行业领袖）。\n" +
                "- 若某项属性值 > 50，安排一个比较好的结局（例如顺利毕业、稳定工作）。\n" +
                "- 若某项属性值 < 10，安排一个不太好的结局（例如辍学、经济困难）。\n" +
                "- 综合考虑所有属性，生成一个详细的结局描述，长度不超过500字符。",
                science, health, game, social, money
            );

            // Call DashScope API for final chapter
            ApplicationParam finalChapterParam = ApplicationParam.builder()
                .apiKey(apiKey)
                .appId(appId)
                .prompt(finalChapterMessage)
                .build();

            Application finalChapterApplication = new Application();
            ApplicationResult finalChapterResult = finalChapterApplication.call(finalChapterParam);
            String finalChapterAiReply = finalChapterResult.getOutput().getText();

            // Save final chapter outcome to archive
            archive.setFinalOutcome(finalChapterAiReply);

            // Save final chapter message and response to chat history
            ChatMessage finalChapterMessageRecord = new ChatMessage();
            finalChapterMessageRecord.setUserInput("用户结束对话，触发大学结局预测");
            finalChapterMessageRecord.setAiResponse(finalChapterAiReply);
            finalChapterMessageRecord.setTimestamp(LocalDateTime.now());
            chatMessageRepository.save(finalChapterMessageRecord);

            // Save updated archive
            archiveRepository.save(archive);

            // Prepare response
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("finalOutcome", finalChapterAiReply);

            return ResponseEntity.ok(successResponse);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "DashScope API error: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    private void updateArchiveFromReply(String reply, Archive archive) {
        String regex = "(学习|健康|游戏|社交|金钱)\\s*([+-]\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(reply);

        while (matcher.find()) {
            String attribute = matcher.group(1);
            int change = Integer.parseInt(matcher.group(2));
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

    private boolean judge(Archive archive) {
        if (archive.getArchiveHealth() >= 90 || archive.getArchiveScience() >= 90 ||
            archive.getArchiveSocial() >= 90 || archive.getArchiveGame() >= 90 ||
            archive.getArchiveMoney() >= 90 ||
            archive.getArchiveHealth() <= 10 || archive.getArchiveScience() <= 10 ||
            archive.getArchiveSocial() <= 10 || archive.getArchiveGame() <= 10 ||
            archive.getArchiveMoney() <= 10) {
            archive.setArchiveSuccessFinish(1);
            return true;
        }
        return false;
    }

    private void getProperties(Archive archive) {
        health = archive.getArchiveHealth();
        science = archive.getArchiveScience();
        social = archive.getArchiveSocial();
        game = archive.getArchiveGame();
        money = archive.getArchiveMoney();
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory() {
        return ResponseEntity.ok(chatMessageRepository.findAllByOrderByIdAsc());
    }
}