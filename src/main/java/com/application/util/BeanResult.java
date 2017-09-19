package com.application.util;


import com.application.util.ListResult;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chengchao on 2016/10/26.
 */
public class BeanResult implements Serializable {

    public static final String ERROR = "ERROR";
    public static final String SUCCESS = "SUCCESS";

    private String status;
    private String cause;
    private Object data;


    public static final BeanResult error(String cause) {
        BeanResult bean = new BeanResult();
        bean.status = ERROR;
        bean.cause = cause;

        return bean;
    }


    public static final BeanResult success(Object data) {
        BeanResult bean = new BeanResult();
        bean.status = SUCCESS;
        bean.data = data;

        return bean;
    }


    public static final BeanResult success(Integer total, List<?> rows) {
        BeanResult bean = new BeanResult();
        bean.status = SUCCESS;
        bean.data = new ListResult(total, rows);

        return bean;
    }


    /**
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return
     */
    public String getCause() {
        return cause;
    }

    /**
     * @return
     */
    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this)
                .toString();
    }
}
