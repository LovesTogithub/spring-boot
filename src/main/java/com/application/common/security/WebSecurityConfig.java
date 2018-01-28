package com.application.common.security;

import com.application.filter.ReqCache;
import com.application.service.LightSwordUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by qws on 2017/9/25/025.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private ReqCache requestCache;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);
        http.csrf().disable().authorizeRequests()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login")
                .successHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    SavedRequest request = requestCache.getRequest(httpServletRequest, httpServletResponse);
                    System.out.println(request.getRedirectUrl());
                    httpServletResponse.sendRedirect(request.getRedirectUrl());
                })
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        return new LightSwordUserDetailService();

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    自定义service
        auth.userDetailsService(userDetailsService()); // （6）
    }


}


