package com.Repository;

import com.PO.FinalChapter;
import com.VO.FinalChapterVO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinalChapterRepository extends JpaRepository<FinalChapter, Integer> {
    FinalChapter findByArchiveId(Integer archiveId);
}
