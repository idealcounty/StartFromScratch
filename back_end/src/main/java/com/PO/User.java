package com.PO;

import com.VO.UserVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Basic
    @Column(name = "user_name")
    private String userName;

    @Basic
    @Column(name = "user_password")
    private String userPassword;

    @Basic
    @Column(name = "user_create_time")
    private Date userCreateTime;

    @Basic
    @Column(name="user_avatar")
    private String userAvatar;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<Archive> archives = new ArrayList<>();


    public UserVO toVO(){
        UserVO userVO=new UserVO();
        userVO.setUserId(this.userId);
        userVO.setUserName(this.userName);
        userVO.setUserPassword(this.userPassword);
        userVO.setUserCreateTime(this.userCreateTime);
        userVO.setUserAvatar(this.userAvatar);
        if (this.archives != null) {
            userVO.setArchiveIds(this.archives.stream()
                    .map(Archive::getArchiveId)
                    .collect(Collectors.toList()));
        }
        return userVO;
    }
}
