package com.Controller;
import com.Service.AchievementService;
import com.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AchievementController {
    @Autowired
    AchievementService achievementService;

}
