package com.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


/**
 * Created by qws on 2017/9/15/015.
 */
@SpringBootApplication
@ServletComponentScan // 扫描使用注解方式的servlet
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}