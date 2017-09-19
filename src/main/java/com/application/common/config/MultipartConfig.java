package com.application.common.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.servlet.MultipartConfigElement;

/**
 * Created by qws on 2017/9/17/017.
 */
//springboot里默认使用tomcat的上传文件大小限制，即1MB，
//修改用下面的配置类：
@Configuration
public class MultipartConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("10MB");
        factory.setMaxRequestSize("10MB");
        return factory.createMultipartConfig();
    }

}
