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
import java.util.concurrent.Callable;
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
            int health = 0;
            int science = 0;
            int social = 0;
            int game = 0;
            int money = 0;
    public ChatController(ChatMessageRepository chatMessageRepository, ArchiveRepository archiveRepository, UserService userService) {
        this.chatMessageRepository = chatMessageRepository;
        this.archiveRepository = archiveRepository;
        this.userService = userService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest request) {
        try {
            Integer userId = userService.getInformation().getUserId();
            Archive archive = archiveRepository.findByUserId(userId);
            if (archive == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "用户存档不存在");
                return ResponseEntity.status(404).body(errorResponse);
            }
            getProperties(archive);

            String userInput = request.getMessage();
            if (userInput == null || userInput.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "用户输入不能为空");
                return ResponseEntity.status(400).body(errorResponse);
            }

            String modifiedInput = String.format(
                "%s\n当前属性值：学习 %d, 健康 %d, 游戏 %d, 社交 %d, 金钱 %d",
                userInput, science, health, game, social, money
            );

            ApplicationParam param = ApplicationParam.builder()
                    .apiKey(apiKey)
                    .appId(appId)
                    .prompt(modifiedInput)
                    .build();

            Application application = new Application();
            ApplicationResult result = application.call(param);
            String aiReply = result.getOutput().getText();

            updateArchiveFromReply(aiReply, archive);
            if(judge(archive)){
                ChatRequest finalChapter=new ChatRequest();
                getProperties(archive);
                String finalChapterMessage = String.format("当前属性值：学习 %d, 健康 %d, 游戏 %d, 社交 %d, 金钱 %d,根据目前的属性值,为用户预测一个大学的结局!若某项属性值>50,可以安排一个比较好的结局;" +
                        "若某项属性值>90,可以安排一个非常好的结局!若某项属性值<10,则只能安排部署很好的结局.", science, health, game, social, money);
                finalChapter.setMessage(finalChapterMessage);

            }
            archiveRepository.save(archive);

            ChatMessage message = new ChatMessage();
            message.setUserInput(userInput);
            message.setAiResponse(aiReply);
            message.setTimestamp(LocalDateTime.now());
            chatMessageRepository.save(message);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("reply", aiReply);

            return ResponseEntity.ok(successResponse);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
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
    private boolean judge(Archive archive){
        if(archive.getArchiveHealth()>=100||archive.getArchiveScience()>=100
                ||archive.getArchiveSocial()>=100||archive.getArchiveGame()>=100||
                archive.getArchiveMoney()>=100||archive.getArchiveHealth()<=0||archive.getArchiveScience()<=0
                ||archive.getArchiveGame()<=0||archive.getArchiveMoney()<=0||archive.getArchiveSocial()<=0)
        {
            archive.setArchiveSuccessFinish(1);
            return true;
        }
            return false;
    }
    private void getProperties(Archive archive){
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