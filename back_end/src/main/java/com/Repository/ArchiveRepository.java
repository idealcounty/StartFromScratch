package com.Repository;
import com.PO.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ArchiveRepository extends JpaRepository<Archive, Integer> {
    Archive findByArchiveId(Integer archiveId);
    Archive findByUserId(Integer userId);
}
