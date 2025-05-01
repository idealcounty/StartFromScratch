package com.ServiceImpl;

import com.exception.RE0Exception;
import com.PO.User;
import com.Repository.UserRepository;
import com.Service.UserService;
import com.Util.TokenUtil;
import com.VO.UserVO;
import com.Util.SecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenUtil tokenUtil;
    @Autowired
    SecurityUtil securityUtil;


    @Override
    public Boolean register(UserVO userVO) {
        User user = userRepository.findByUserName(userVO.getUserName());
        if (user != null) {
            throw RE0Exception.userNameAlreadyExists();
        }
        User newUser = userVO.toPO();
        newUser.setUserCreateTime(new Date());
        newUser = userRepository.save(newUser);
        return true;
    }

    @Override
    public String login(String userName, String userPassword) {
        User user = userRepository.findByUserNameAndUserPassword(userName, userPassword);
        if (user == null) {
            throw RE0Exception.userNameOrUserPasswordError();
        }
        return tokenUtil.getToken(user);
    }

    @Override
    public UserVO getInformation() {
        User user=securityUtil.getCurrentUser();
        return user.toVO();
    }

    @Override
    public Boolean updateInformation(int userId,UserVO userVO) {
        User user=userRepository.findById(userId).get();
        user.setUserPassword(userVO.getUserPassword());
        user.setUserName(userVO.getUserName());
        user.setUserAvatar(userVO.getUserAvatar());
        userRepository.save(user);
        return true;
    }
}

