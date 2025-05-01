package com.PO;
import com.VO.FinalChapterVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class FinalChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "final_chapter_id")
    private Integer finalChapterId;

    @Column(name = "archive_id")
    private Integer archiveId;

    @Column(name = "final_chapter_title")
    private String finalChapterTitle;

    @Column(name = "final_chapter_text")
    private String finalChapterText;

    public FinalChapterVO toVO(){
        FinalChapterVO finalChapterVO = new FinalChapterVO();
        finalChapterVO.setFinalChapterId(finalChapterId);
        finalChapterVO.setArchiveId(archiveId);
        finalChapterVO.setFinalChapterTitle(finalChapterTitle);
        finalChapterVO.setFinalChapterText(finalChapterText);
        return finalChapterVO;
    }
}
