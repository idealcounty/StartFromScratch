package com.Service;

import com.VO.ArchiveVO;

import java.util.List;

public interface ArchiveService {
    Boolean createArchive(ArchiveVO archiveVO);
    Boolean deleteArchive(Integer archiveId);
    List<ArchiveVO> getAllArchive(Integer userId);
    Integer archiveComeToEnd(Integer archiveId);
    Integer archiveExit(Integer archiveId);
}
