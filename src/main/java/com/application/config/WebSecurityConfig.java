package com.application.config;

import com.application.service.LightSwordUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by qws on 2017/9/25/025.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
//        http.csrf().disable();
//
//        http.authorizeRequests()
//                .antMatchers("/").permitAll()
//                .antMatchers("/amchart/**",
//                        "/bootstrap/**",
//                        "/build/**",
//                        "/css/**",
//                        "/dist/**",
//                        "/documentation/**",
//                        "/fonts/**",
//                        "/js/**",
//                        "/pages/**",
//                        "/plugins/**"
//                ).permitAll() //默认不拦截静态资源的url pattern （2）
//                .anyRequest().authenticated().and()
//                .formLogin().loginPage("/login")// 登录url请求路径 (3)
//                .defaultSuccessUrl("/home").permitAll().and() // 登录成功跳转路径url(4)
//                .logout().permitAll();
//
//        http.logout().logoutSuccessUrl("/"); // 退出默认跳转页面 (5)

    }

//    @Override
//    @Bean
//    public UserDetailsService userDetailsService() { //覆盖写userDetailsService方法 (1)
//        return new LightSwordUserDetailService();
//
//    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("admin")
                .password("admin")
                .roles("USER", "ADMIN").and()
                .withUser("user").password("user")
                .roles("USER");
//        AuthenticationManager使用我们的 lightSwordUserDetailService 来获取用户信息
//        auth.userDetailsService(userDetailsService()); // （6）
    }

}


