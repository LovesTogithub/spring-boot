package com.application.common.config;

import com.application.interceptor.DemoInterceptor;
import org.springframework.stereotype.Component;


/**
 * Created by qws on 2018/1/28/028.
 */
@Component
public class InterceptorConfig {


    public DemoInterceptor demoInterceptor(){
        return  new DemoInterceptor();
    }
}
