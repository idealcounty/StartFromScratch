package com.VO;
import com.PO.FinalChapter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FinalChapterVO {

    private Integer finalChapterId;
    private Integer archiveId;
    private String finalChapterTitle;
    private String finalChapterText;

    public FinalChapter toPO(){
        FinalChapter finalChapter = new FinalChapter();
        finalChapter.setFinalChapterId(finalChapterId);
        finalChapter.setArchiveId(archiveId);
        finalChapter.setFinalChapterTitle(finalChapterTitle);
        finalChapter.setFinalChapterText(finalChapterText);
        return finalChapter;

    }
}
