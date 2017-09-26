package com.application.aspect;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Created by qws on 2017/9/19/019.
 * Web层日志切面
 */
@Aspect
@Component
//加载顺序
@Order(-5)
public class WebLogAspect {
    private Logger logger = Logger.getLogger(getClass());
    //实现线程同步
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(public * com.application.controller.*.*(..))")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        logger.info("URL : " + request.getRequestURL().toString());
        logger.info("HTTP_METHOD : " + request.getMethod());
        logger.info("IP : " + request.getRemoteAddr());
        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));

    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        logger.info("RESPONSE : " + ret);
        logger.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
    }

    @Around(value = "webLog()")
    public Object watchPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("前置");
        //proceed程序返回值
        Object proceed = joinPoint.proceed();
        String result;
        if (proceed == null) {
            result = "";
        } else if (proceed.getClass().isInstance(String.class)) {
            result = (String) proceed;
        } else {
            result = JSONObject.toJSONString(proceed);
        }
        logger.info("后置around");
        logger.info(result);
        return proceed;
    }


}
