package com.PO;
import com.VO.AchievementVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    private Integer achievementId;

    @Column(name = "achievement_title")
    private String achievementTitle;

    @Column(name = "achievement_text")
    private String achievementText;

    public AchievementVO toVO(){
        AchievementVO achievementVO = new AchievementVO();
        achievementVO.setAchievementId(achievementId);
        achievementVO.setAchievementTitle(achievementTitle);
        achievementVO.setAchievementText(achievementText);
        return achievementVO;
    }
}
