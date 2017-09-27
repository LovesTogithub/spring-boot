package com.application.controller;

import com.application.util.BeanResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by qws on 2017/9/27/027.
 */
@RestController
@RequestMapping("/log-in")
public class AdminLogin {

    @RequestMapping("/")
    public BeanResult index() {
        BeanResult result;
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.getClass().isInstance(String.class)) {
            username = principal.toString();
        } else {
            username = ((UserDetails) principal).getUsername();
        }
        result = BeanResult.success(username);
        System.out.println(result);
        return result;
    }


}
