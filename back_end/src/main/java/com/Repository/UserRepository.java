package com.Repository;

import com.PO.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserId(Integer userId);

    User findByUserName(String userName);

    User findByUserNameAndUserPassword(String userName, String userPassword);
}
