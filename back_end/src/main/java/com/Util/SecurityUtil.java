package com.Util;

import com.PO.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
@Component
public class SecurityUtil {

    @Autowired
    HttpServletRequest httpServletRequest;

    public User getCurrentUser(){
        return (User)httpServletRequest.getSession().getAttribute("currentUser");
    }
}
