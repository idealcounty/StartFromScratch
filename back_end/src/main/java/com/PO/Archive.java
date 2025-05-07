package com.PO;

import com.VO.ArchiveVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Archive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_id")
    private Integer archiveId;

    @Column(name = "archive_science")
    private Integer archiveScience;

    @Column(name = "archive_health")
    private Integer archiveHealth;

    @Column(name = "archive_game")
    private Integer archiveGame;

    @Column(name = "archive_social")
    private Integer archiveSocial;

    @Column(name = "archive_money")
    private Integer archiveMoney;

    @Column(name = "archive_success_finish")
    private Integer archiveSuccessFinish=0;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "final_outcome")
    private String finalOutcome;

    public ArchiveVO toVO(){
        ArchiveVO vo = new ArchiveVO();
        vo.setArchiveId(archiveId);
        vo.setArchiveScience(archiveScience);
        vo.setArchiveHealth(archiveHealth);
        vo.setArchiveGame(archiveGame);
        vo.setArchiveSocial(archiveSocial);
        vo.setArchiveMoney(archiveMoney);
        vo.setArchiveSuccessFinish(archiveSuccessFinish);
        vo.setUserId(userId);
        vo.setFinalOutcome(finalOutcome);
        return vo;
    }
}