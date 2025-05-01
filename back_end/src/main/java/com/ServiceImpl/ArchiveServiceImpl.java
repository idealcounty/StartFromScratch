package com.ServiceImpl;
import com.PO.FinalChapter;
import com.PO.User;
import com.Repository.FinalChapterRepository;
import com.Repository.UserRepository;
import com.VO.ArchiveVO;
import com.exception.RE0Exception;
import com.Service.ArchiveService;
import com.PO.Archive;
import com.Repository.ArchiveRepository;
import com.Service.ArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArchiveServiceImpl implements ArchiveService {
    @Autowired
    ArchiveRepository archiveRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FinalChapterRepository finalChapterRepository;
    @Override
    public Boolean createArchive(ArchiveVO archiveVO){
        Archive archive = archiveRepository.findByArchiveId(archiveVO.getArchiveId());
        if (archive != null) {
            throw RE0Exception.archiveNameAlreadyExists();
        }
        if(archiveVO.getArchiveHealth()+archiveVO.getArchiveScience()+archiveVO.getArchiveGame()+archiveVO.getArchiveSocial()>30){
            throw RE0Exception.attributeValueLimitExceeded();
        }
        Archive newArchive = archiveVO.toPO();
        archiveRepository.save(newArchive);
        FinalChapter finalChapter = new FinalChapter();
        finalChapter.setArchiveId(newArchive.getArchiveId());
        finalChapterRepository.save(finalChapter);
        return true;
    }
    @Override
    public Boolean deleteArchive(Integer archiveId){
        Archive archive = archiveRepository.findByArchiveId(archiveId);
        if (archive == null) {
            throw RE0Exception.archiveNotExists();
        }
        archiveRepository.delete(archive);
        return true;
    }
    @Override
    public List<ArchiveVO> getAllArchive(Integer userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw RE0Exception.userNotExists();
        }
        return user.getArchives().stream()
                .map(Archive::toVO)
                .collect(Collectors.toList());
    }
    @Override
    public Integer archiveComeToEnd(Integer archiveId){
        Archive archive = archiveRepository.findByArchiveId(archiveId);
        if (archive == null) {
            throw RE0Exception.archiveNotExists();
        }
        if(archive.getArchiveHealth()>=15||archive.getArchiveScience()>=15||archive.getArchiveGame()>=15||archive.getArchiveSocial()>=15||archive.getArchiveMoney()>=15){
            archive.setArchiveSuccessFinish(0);
            return archive.getArchiveId();
        }
        else
            return 0;
    }
    @Override
    public Integer archiveExit(Integer archiveId){
        Archive archive = archiveRepository.findByArchiveId(archiveId);
        if (archive == null) {
            throw RE0Exception.archiveNotExists();
        }
        archive.setArchiveSuccessFinish(1);
        return archive.getArchiveId();
    }
}
