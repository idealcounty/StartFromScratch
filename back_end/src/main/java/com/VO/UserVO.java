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

    // getter & setter 省略...

    public User toPO() {
        User user = new User();
        user.setUserId(this.userId);
        user.setUserName(this.userName);
        user.setUserPassword(this.userPassword);
        user.setUserCreateTime(this.userCreateTime);
        user.setUserAvatar(this.userAvatar);

        if (this.archiveId != null) {
            Archive archive = new Archive();
            archive.setArchiveId(this.archiveId);
            user.setArchive(archive);   // 对应你在 User 实体里新增的 one-to-one 字段
        }
        return user;
    }
}
