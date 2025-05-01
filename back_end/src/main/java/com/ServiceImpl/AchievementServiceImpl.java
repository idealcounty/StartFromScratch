package com.ServiceImpl;
import com.PO.Archive;
import com.Repository.AchievementRepository;
import com.Repository.ArchiveRepository;
import com.Service.AchievementService;
import com.VO.AchievementVO;
import com.exception.RE0Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AchievementServiceImpl implements AchievementService {
    @Autowired
    ArchiveRepository archiveRepository;
    @Autowired
    AchievementRepository achievementRepository;

    AchievementVO attributeValueComeToMax(Integer archiveId){
        Archive archive=archiveRepository.findByArchiveId(archiveId);
        AchievementVO achievementVO=new AchievementVO();
        return achievementVO;
    }
}
