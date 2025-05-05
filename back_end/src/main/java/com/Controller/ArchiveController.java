package com.Controller;
import com.PO.Archive;
import com.Service.ArchiveService;
import com.VO.ArchiveVO;
import com.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ArchiveController {
    @Autowired
    ArchiveService archiveService;
    @PostMapping("/archive/createArchive")
    public ResultVO<Boolean> createArchive(@RequestBody ArchiveVO archiveVO) {
        return ResultVO.buildSuccess(archiveService.createArchive(archiveVO));
    }
    @DeleteMapping("/archive/{archiveId}")
    public ResultVO<Boolean> deleteArchive(@PathVariable Integer archiveId) {
        return ResultVO.buildSuccess(archiveService.deleteArchive(archiveId));
    }
    @GetMapping("/user/{userId}")
    public ResultVO<ArchiveVO> getAllArchive(@PathVariable Integer userId) {
        return ResultVO.buildSuccess(archiveService.getAllArchive(userId));
    }
    @GetMapping("/archive/{archiveId}")
    public ResultVO<Integer> archiveComeToEnd(@PathVariable Integer archiveId) {
        return ResultVO.buildSuccess(archiveService.archiveComeToEnd(archiveId));
    }
    @GetMapping("/archive/{archiveId}/exit")
    public ResultVO<Integer> archiveExit(@PathVariable Integer archiveId) {
        return ResultVO.buildSuccess(archiveService.archiveExit(archiveId));
    }
}
