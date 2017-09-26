package com.application.service;

import com.application.dao.RoleDao;
import com.application.dao.TuserDao;
import com.application.dao.UserRoleDao;
import com.application.entity.Tuser;
import com.application.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qws on 2017/9/25/025.
 */
@Service
public class LightSwordUserDetailService implements UserDetailsService {

    @Autowired
    RoleDao roleDao;
    @Autowired
    TuserDao tuserDao;
    @Autowired
    UserRoleDao userRoleDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 判断登录的逻辑
        Tuser tuser = new Tuser();
        tuser.setUserName(username);
        Example<Tuser> example = Example.of(tuser);
        Tuser tuser1 = tuserDao.findOne(example);
        if (tuser1 == null) {
            throw new UsernameNotFoundException(username + " not found");
        }
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        UserRole userRole = new UserRole();
        userRole.setId(tuser1.getId());
        Example<UserRole> uexample = Example.of(userRole);
        List<UserRole> all = userRoleDao.findAll(uexample);
        for (UserRole userRole1 : all) {
            Integer roleId = userRole1.getRoleId();
            String roleName = roleDao.findOne((long) roleId.intValue()).getRole();
            if (!StringUtils.isEmpty(roleName)) {
                simpleGrantedAuthorities.add(new SimpleGrantedAuthority(roleName));
            }
            System.out.println("username is " + username + ", " + roleName);
        }
        return new User(username, tuser1.getPassWord(), simpleGrantedAuthorities);
    }


}
