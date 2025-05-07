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
        if(archiveVO.getArchiveHealth()+archiveVO.getArchiveScience()+archiveVO.getArchiveGame()+archiveVO.getArchiveSocial()>350){
            throw RE0Exception.attributeValueLimitExceeded();
        }
        Archive archive = archiveRepository.findByUserId(archiveVO.getUserId());
        if (archive != null) {
            archive.setArchiveSuccessFinish(archiveVO.getArchiveSuccessFinish());
            archive.setArchiveGame(archiveVO.getArchiveGame());
            archive.setArchiveMoney(archiveVO.getArchiveMoney());
            archive.setArchiveHealth(archiveVO.getArchiveHealth());
            archive.setArchiveScience(archiveVO.getArchiveScience());
            archive.setArchiveSocial(archiveVO.getArchiveSocial());
            archiveRepository.save(archive);
            userRepository.findByUserId(archive.getUserId()).setArchiveId(archiveVO.getUserId());
            userRepository.save(userRepository.findByUserId(archive.getUserId()));
            FinalChapter finalChapter = new FinalChapter();
            finalChapter.setArchiveId(archive.getArchiveId());
            finalChapterRepository.save(finalChapter);
        }
        else{
            Archive newArchive = archiveVO.toPO();
            archiveRepository.save(newArchive);
            userRepository.findByUserId(newArchive.getUserId()).setArchiveId(newArchive.getArchiveId());
            userRepository.save(userRepository.findByUserId(newArchive.getUserId()));
            FinalChapter finalChapter = new FinalChapter();
            finalChapter.setArchiveId(newArchive.getArchiveId());
            finalChapterRepository.save(finalChapter);
        }
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
    public ArchiveVO getAllArchive(Integer userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw RE0Exception.userNotExists();
        }
        return archiveRepository.findByUserId(userId).toVO();
    }
    @Override
    public Integer archiveComeToEnd(Integer archiveId){
        Archive archive = archiveRepository.findByArchiveId(archiveId);
        if (archive == null) {
            throw RE0Exception.archiveNotExists();
        }
        if(archive.getArchiveHealth()>=100||archive.getArchiveScience()>=100||archive.getArchiveGame()>=100||archive.getArchiveSocial()>=100||archive.getArchiveMoney()>=100){
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
