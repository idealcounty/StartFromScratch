package com.ServiceImpl;

import com.PO.FinalChapter;
import com.Repository.FinalChapterRepository;
import com.Service.FinalChapterService;
import com.VO.FinalChapterVO;
import com.exception.RE0Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinalChapterServiceImpl implements FinalChapterService {
    @Autowired
    FinalChapterRepository finalChapterRepository;

    @Override
    public FinalChapterVO createFinalChapter(Integer archiveId) {
        FinalChapter finalChapter = finalChapterRepository.findByArchiveId(archiveId);
        if(finalChapter == null) {
            throw  RE0Exception.archiveNotExists();
        }
        //workwork

        return finalChapter.toVO();
    }
}
