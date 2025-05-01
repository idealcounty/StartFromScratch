package com.Controller;
import com.Service.FinalChapterService;
import com.VO.FinalChapterVO;
import com.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FinalChapterController {
    @Autowired
    FinalChapterService finalChapterService;
    @PostMapping("/archive/{archiveId}")
    public ResultVO<FinalChapterVO> createFinalChapter(@PathVariable int archiveId) {
        return ResultVO.buildSuccess(finalChapterService.createFinalChapter(archiveId));
    }
}
