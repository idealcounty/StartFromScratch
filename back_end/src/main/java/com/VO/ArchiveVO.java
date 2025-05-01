package com.VO;
import com.PO.Archive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class ArchiveVO {

    private Integer archiveId;
    private Integer archiveScience;
    private Integer archiveHealth;
    private Integer archiveGame;
    private Integer archiveSocial;
    public Archive toPO(){
        Archive archive = new Archive();
        archive.setArchiveId(archiveId);
        archive.setArchiveScience(archiveScience);
        archive.setArchiveHealth(archiveHealth);
        archive.setArchiveGame(archiveGame);
        archive.setArchiveSocial(archiveSocial);
        return archive;
    }
}
