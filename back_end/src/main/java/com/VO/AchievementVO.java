package com.VO;
import com.PO.Achievement;
import com.VO.AchievementVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AchievementVO {
    private Integer achievementId;
    private String achievementTitle;
    private String achievementText;

    public Achievement toPO(){
        Achievement achievement = new Achievement();
        achievement.setAchievementId(achievementId);
        achievement.setAchievementTitle(achievementTitle);
        achievement.setAchievementText(achievementText);
        return achievement;
    }
}
