package com.application.common.config;

import com.application.interceptor.DemoInterceptor;


/**
 * Created by qws on 2018/1/28/028.
 */

public class InterceptorConfig {


    public DemoInterceptor demoInterceptor(){
        return  new DemoInterceptor();
    }
}
