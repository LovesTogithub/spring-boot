package com.application.filter;


import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by qws on 2017/9/25/025.
 */
@Order(1) //@Order注解表示执行过滤顺序，值越小，越先执行
@WebFilter(filterName = "loginFilter", urlPatterns = ("/*"))
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("进入servlet容器");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String username;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpSession session = httpServletRequest.getSession();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.getClass().isInstance(String.class)) {
            username = principal.toString();
        } else {
            username = ((UserDetails) principal).getUsername();
        }

        session.setAttribute("username", username);
        System.out.println(session.getAttribute("username"));
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
