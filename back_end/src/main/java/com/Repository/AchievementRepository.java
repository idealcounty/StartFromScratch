package com.Repository;

import com.PO.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    Achievement findByAchievementId(Integer achievementId);
}
