package com.VO;
import com.PO.Archive;
import com.PO.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UserVO {

    private Integer userId;
    private String userName;
    private String userPassword;
    private Date userCreateTime;
    private String userAvatar;
    private Integer archiveId;

    public User toPO() {
        User user = new User();
        user.setUserId(this.userId);
        user.setUserName(this.userName);
        user.setUserPassword(this.userPassword);
        user.setUserCreateTime(this.userCreateTime);
        user.setUserAvatar(this.userAvatar);
        user.setArchiveId(this.archiveId);
        return user;
    }
}
