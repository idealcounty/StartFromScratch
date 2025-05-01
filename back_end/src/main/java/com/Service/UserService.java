package com.Service;
import com.VO.UserVO;


public interface UserService {
    Boolean register(UserVO userVO);
    String login(String phone,String password);
    UserVO getInformation();
    Boolean updateInformation(int userId,UserVO userVO);
}